package nc.bs.er.control;

import java.util.Vector;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.verifynew.BusinessShowException;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;
import nc.vo.util.AuditInfoUtil;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

/**
 * @author twei
 * 
 *         Ԥ�����BO nc.bs.arap.control.YsControlBO
 */
@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "����Ԥ����ƺ��Ĵ���" /*-=notranslate=-*/,type=BusinessType.CORE)
public class YsControlBO {
	
	/**
	 * Ԥ����Ʒ�ʽ
	 */
	private Integer YsControlType = null;

	/**
	 * Ԥ�����
	 * @param items
	 *            ʵ��Ԥ����Ƶĵ���
	 * @param iscontrary
	 *            �Ƿ������(ɾ���ͷ���ЧΪ����,�������ЧΪ����)
	 * @param hascheck
	 *            �Ƿ���У��
	 * @param ruleVOs
	 *            ���ƹ���
	 * @return
	 * @throws BusinessException
	 */
	public String budgetCtrl(IFYControl[] items, boolean iscontrary, Boolean hascheck, DataRuleVO[] ruleVOs) throws BusinessException {

		YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(items, iscontrary, ruleVOs);

		return ysControl(controlVos, hascheck);
	}
	
	/**
	 * Ԥ�����<br>
	 * �ⲿ���ÿ��Խ����Ԥ��VOͳһ�ŵ�һ������������п���<br>
	 * �������⣺����Ԥ��һ�㴦��Ϊ���ͷź�ռ�ã�����ͳһ����һ�𽻵�Ԥ��������
	 * @param ps Ԥ�����VO
	 * @param hascheck �Ƿ��飨Ԥ�����ƣ�ǰ̨���ȷ������ʾtrue����Ԥ�㲻���ƣ�
	 * @return
	 * @throws BusinessException
	 */
	public String budgetCtrl(YsControlVO[] ps, Boolean hascheck) throws BusinessException {
		return ysControl(ps, hascheck);
	}

	/**
	 * @param items
	 *            Ҫ���Ƶ�Ԫ������
	 * @param items_old
	 *            Ҫ���Ƶ�Ԫ��ԭʼ����
	 * @throws BusinessException
	 */
	public String edit(IFYControl[] items, IFYControl[] items_old, Boolean hascheck, DataRuleVO[] ruleVOs) throws BusinessException {
		YsControlVO[] ps = ErBudgetUtil.getEditControlVOs(items, items_old, ruleVOs);
		
		return ysControl(ps, hascheck);
	}

	

	/**
	 * ����Ԥ����ƽӿ�
	 * 
	 * @param controlVos
	 * @param hascheck
	 * @param iscontrary
	 * @return
	 * @throws BusinessShowException
	 */
	public String ysControl(YsControlVO[] controlVos, Boolean hascheck) throws BusinessException {

		if (controlVos == null || controlVos.length == 0) {
			return null;
		}

//		Map<String, List<YsControlVO>> map = new HashMap<String, List<YsControlVO>>();
//		for (YsControlVO vo : controlVos) {
//			String key = vo.getPKOrg();
//			if (map.containsKey(key)) {
//				List<YsControlVO> list = map.get(key);
//				list.add(vo);
//			} else {
//				ArrayList<YsControlVO> list = new ArrayList<YsControlVO>();
//				list.add(vo);
//				map.put(key, list);
//			}
//		}
//		Set<String> pkcorps = map.keySet();

		// �����ж��׳������쳣�����Ϊtrue:Ԥ����false�����Կ��ƣ�null�����Կ���
		Boolean isAlarm = null;
		StringBuffer resultStr = new StringBuffer("");

//		for (String corp : pkcorps) {// ���ｫ�����˾��Ԥ�����һ���׳�
//			NtbCtlInfoVO ctrlInfoVO = ysControlCore(map.get(corp).toArray(new YsControlVO[] {}));
			NtbCtlInfoVO ctrlInfoVO = ysControlCore(controlVos);
			if (ctrlInfoVO != null) {
				StringBuffer controlMsg = new StringBuffer();

				if (ctrlInfoVO.isControl()) {// ���Կ���
					isAlarm = Boolean.FALSE;
					controlMsg.append(getStringFromArrayStr(ctrlInfoVO.getControlInfos()));
				} else if (ctrlInfoVO.isAlarm()) {// Ԥ������
					if (hascheck == null || Boolean.FALSE.equals(hascheck)) {
						isAlarm = Boolean.TRUE;
					}
					controlMsg.append(getStringFromArrayStr(ctrlInfoVO.getAlarmInfos()));
				} else if (ctrlInfoVO.isMayBeControl()) {// ���Կ���, ��������ʱ���Կ���
					boolean isStartWorkFlow = false;
					String bill_pk = controlVos[0].getItems()[0].getWorkFlowBillPk();
					String djlxbm = controlVos[0].getItems()[0].getWorkFolwBillType();
					
//					if(controlVos[0].getItems()[0] instanceof CostShareYsControlVO){//��ǰ��ת�����������Ƿ���������Ϊ׼
//						Integer src_type = (Integer)((CostShareYsControlVO)controlVos[0].getItems()[0]).getItemValue(CostShareVO.SRC_TYPE);
//						if(src_type == IErmCostShareConst.CostShare_Bill_SCRTYPE_BX){
//							bill_pk = (String)((CostShareYsControlVO)controlVos[0].getItems()[0]).getItemValue(CostShareVO.SRC_ID);
//							djlxbm = (String)((CostShareYsControlVO)controlVos[0].getItems()[0]).getItemValue(CostShareVO.DJLXBM);
//						}
//					}
					
					isStartWorkFlow = NCLocator.getInstance().lookup(IPFWorkflowQry.class)
							.isApproveFlowStartup(bill_pk, djlxbm);

					if (!isStartWorkFlow) {
						if (controlVos[0].getItems()[0].getDjzt() <= BXStatusConst.DJZT_Saved) {
							isStartWorkFlow = NCLocator
									.getInstance()
									.lookup(IPFWorkflowQry.class)
									.isExistWorkflowDefinitionWithEmend(djlxbm,
											controlVos[0].getItems()[0].getPk_org(), AuditInfoUtil.getCurrentUser(), -1,
											WorkflowTypeEnum.Approveflow.getIntValue());
						}
					}

					if (!isStartWorkFlow) {//�������治��������������Կ���
						isAlarm = Boolean.FALSE;
					}

					// ���Կ��Ƶ���������ʾ��Ϣ
					if(isAlarm != null){
						controlMsg.append(getStringFromArrayStr(ctrlInfoVO.getFlexibleControlInfos()));
					}
				}

				if (controlMsg.length() != 0) {
					resultStr.append(controlMsg).append("\n");
				}
			}
			
//		}

		if (isAlarm == null) {
			if (resultStr.toString().length() == 0) {
				return null;
			}
			return resultStr.toString();
		} else if (isAlarm.equals(Boolean.TRUE)) {
			
			if(getYsControlType() == ErmConst.YsControlType_AlarmNOCHECK_CONTROL){
				return null;
			}else{// Ԥ������ֱ���׳��쳣
				throw new BugetAlarmBusinessException(resultStr.toString());
			}
		} else if (isAlarm.equals(Boolean.FALSE)) {
			// ���Կ���ֱ���׳��쳣
			throw new BusinessException(resultStr.toString());
		}

		return null;
	}
	
	private String getStringFromArrayStr(String[] infos){
		if(infos != null){
			StringBuffer controlMsg = new StringBuffer("");
			for (String info: infos) {
				controlMsg.append("\n" + info);
			}
			
			return controlMsg.toString();
		}
		
		return null;
	}

	/**
	 * ��������Ԥ����ƺ��Ĵ���
	 * 
	 * @param controlVos Ԥ�����VO
	 * @return
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "Ԥ����ƺ���" /*-=notranslate=-*/,type=BusinessType.CORE)
	private NtbCtlInfoVO ysControlCore(YsControlVO[] controlVos) throws BusinessException {

		// �Ƿ�װԤ��
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return null;
		}
		Vector<IAccessableBusiVO> busiVoVector = new Vector<IAccessableBusiVO>();
		for (YsControlVO vo : controlVos) {
			FiBillAccessableBusiVOProxy busiVOProxy = new FiBillAccessableBusiVOProxy(vo);
			busiVoVector.addElement(busiVOProxy);
		}
		if (busiVoVector == null || busiVoVector.size() == 0) {
			return null;
		}
		FiBillAccessableBusiVO[] fiBillAccessableBusiVOs = busiVoVector.toArray(new FiBillAccessableBusiVO[0]);

		// ����Ԥ��ӿڲ�ѯ������Ϣ
		IBudgetControl budgetservice = NCLocator.getInstance().lookup(IBudgetControl.class);
		NtbCtlInfoVO ctrlInfoVO = null;
		switch (getYsControlType()) {
			case ErmConst.YsControlType_CHECK:{
				ctrlInfoVO = budgetservice.getCheckInfo(fiBillAccessableBusiVOs);
				break;
			}
			case ErmConst.YsControlType_NOCHECK_CONTROL:{
				budgetservice.noCheckUpdateExe(fiBillAccessableBusiVOs);
				break;
			}
			default:{
				ctrlInfoVO = budgetservice.getControlInfo(fiBillAccessableBusiVOs);
				break;
			}
		}

		return ctrlInfoVO;
	}
	
	/**
	 * ��ȡ���Ʒ�ʽ
	 * <li>����һ�д
	 * <li>ֻ��鲻��д
	 * <li>�����ֱ�ӻ�д
	 * @return
	 */
	public int getYsControlType() {
		return YsControlType == null?ErmConst.YsControlType_CONTROL:YsControlType;
	}

	public void setYsControlType(Integer ysControlType) {
		YsControlType = ysControlType;
	}
}
