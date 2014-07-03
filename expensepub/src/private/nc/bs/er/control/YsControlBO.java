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
 *         预算控制BO nc.bs.arap.control.YsControlBO
 */
@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "费用预算控制核心代码" /*-=notranslate=-*/,type=BusinessType.CORE)
public class YsControlBO {
	
	/**
	 * 预算控制方式
	 */
	private Integer YsControlType = null;

	/**
	 * 预算控制
	 * @param items
	 *            实现预算控制的单据
	 * @param iscontrary
	 *            是否反向控制(删除和反生效为反向,保存和生效为正向)
	 * @param hascheck
	 *            是否再校验
	 * @param ruleVOs
	 *            控制规则
	 * @return
	 * @throws BusinessException
	 */
	public String budgetCtrl(IFYControl[] items, boolean iscontrary, Boolean hascheck, DataRuleVO[] ruleVOs) throws BusinessException {

		YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(items, iscontrary, ruleVOs);

		return ysControl(controlVos, hascheck);
	}
	
	/**
	 * 预算控制<br>
	 * 外部调用可以将多个预算VO统一放到一个数组里面进行控制<br>
	 * 处理问题：对于预算一般处理为先释放后占用，这里统一放在一起交到预算来处理
	 * @param ps 预算控制VO
	 * @param hascheck 是否检查（预警控制，前台天际确定后显示true，超预算不控制）
	 * @return
	 * @throws BusinessException
	 */
	public String budgetCtrl(YsControlVO[] ps, Boolean hascheck) throws BusinessException {
		return ysControl(ps, hascheck);
	}

	/**
	 * @param items
	 *            要控制的元素数组
	 * @param items_old
	 *            要控制的元素原始数组
	 * @throws BusinessException
	 */
	public String edit(IFYControl[] items, IFYControl[] items_old, Boolean hascheck, DataRuleVO[] ruleVOs) throws BusinessException {
		YsControlVO[] ps = ErBudgetUtil.getEditControlVOs(items, items_old, ruleVOs);
		
		return ysControl(ps, hascheck);
	}

	

	/**
	 * 调用预算控制接口
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

		// 用于判断抛出何种异常，如果为true:预警，false：刚性控制，null：柔性控制
		Boolean isAlarm = null;
		StringBuffer resultStr = new StringBuffer("");

//		for (String corp : pkcorps) {// 这里将多个公司的预算控制一起抛出
//			NtbCtlInfoVO ctrlInfoVO = ysControlCore(map.get(corp).toArray(new YsControlVO[] {}));
			NtbCtlInfoVO ctrlInfoVO = ysControlCore(controlVos);
			if (ctrlInfoVO != null) {
				StringBuffer controlMsg = new StringBuffer();

				if (ctrlInfoVO.isControl()) {// 刚性控制
					isAlarm = Boolean.FALSE;
					controlMsg.append(getStringFromArrayStr(ctrlInfoVO.getControlInfos()));
				} else if (ctrlInfoVO.isAlarm()) {// 预警控制
					if (hascheck == null || Boolean.FALSE.equals(hascheck)) {
						isAlarm = Boolean.TRUE;
					}
					controlMsg.append(getStringFromArrayStr(ctrlInfoVO.getAlarmInfos()));
				} else if (ctrlInfoVO.isMayBeControl()) {// 柔性控制, 无审批流时刚性控制
					boolean isStartWorkFlow = false;
					String bill_pk = controlVos[0].getItems()[0].getWorkFlowBillPk();
					String djlxbm = controlVos[0].getItems()[0].getWorkFolwBillType();
					
//					if(controlVos[0].getItems()[0] instanceof CostShareYsControlVO){//事前结转，按报销单是否有审批流为准
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

					if (!isStartWorkFlow) {//借款报销单存不存在审批流则刚性控制
						isAlarm = Boolean.FALSE;
					}

					// 柔性控制弹出警告提示信息
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
			}else{// 预警控制直接抛出异常
				throw new BugetAlarmBusinessException(resultStr.toString());
			}
		} else if (isAlarm.equals(Boolean.FALSE)) {
			// 刚性控制直接抛出异常
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
	 * 报销管理预算控制核心代码
	 * 
	 * @param controlVos 预算控制VO
	 * @return
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "预算控制核心" /*-=notranslate=-*/,type=BusinessType.CORE)
	private NtbCtlInfoVO ysControlCore(YsControlVO[] controlVos) throws BusinessException {

		// 是否安装预算
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return null;
		}
		Vector<IAccessableBusiVO> busiVoVector = new Vector<IAccessableBusiVO>();
		for (YsControlVO vo : controlVos) {
			busiVoVector.addElement(vo);
		}
		if (busiVoVector == null || busiVoVector.size() == 0) {
			return null;
		}
		FiBillAccessableBusiVO[] fiBillAccessableBusiVOs = busiVoVector.toArray(new FiBillAccessableBusiVO[0]);

		// 调用预算接口查询控制信息
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
	 * 获取控制方式
	 * <li>检查且回写
	 * <li>只检查不回写
	 * <li>不检查直接回写
	 * @return
	 */
	public int getYsControlType() {
		return YsControlType == null?ErmConst.YsControlType_CONTROL:YsControlType;
	}

	public void setYsControlType(Integer ysControlType) {
		YsControlType = ysControlType;
	}
}
