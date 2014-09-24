package nc.ui.erm.bugetint;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nc.bs.er.util.WriteBackUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.ui.pf.pub.PFClientBizRetObj;
import nc.ui.pub.pf.IPFClientBizProcess;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.ErmFlowCheckInfo;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXMtappCtrlBusiVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.pm.budget.pub.BudgetEnum;
import nc.vo.pm.budget.pub.BudgetReturnMSG;
import nc.vo.pm.budget.pub.BudgetReturnVO;
import nc.vo.pm.util.ListUtil;
import nc.vo.pmbd.budgetctrl.BudgetCtrlTypeConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.PfClientBizProcessContext;
import nc.vo.tb.control.NtbCtlInfoVO;
import nc.vo.wfengine.core.data.BasicType;
import nc.vo.wfengine.core.data.DataField;

import org.apache.commons.lang.StringUtils;
/**
 * <p>
 * Ԥ�����Կ��ơ�
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br>
 * ˵����һ���Ƿ����Ԥ�㣺ΪY����Ԥ�㣬������ʱ���������������ʾ***�� ΪN���߲����ã���Ԥ��������ʱ������ʾ.
   		 �����Ƿ�����Ԥ�������׼��ΪY����Ԥ��������Ҳ��ͨ�������ҽ�����ΪN���߲����ã���������ͨ�������Ҵ˲���Ҫ������ĩβ�����˴�����Ч
		 �����v5ϵ�е�ʵ�֣�
		���еڶ��������Ĵ�����Ҫ��Ϊ�˴���������ת�����е�Ԥ����ռ�����⣬
		v6��ΪԤ���ṩ��Ԥռ���Ŀ��ƣ� �������ո��ͱ������ṩ��Ԥռ�õĿ��Ʋ���,
		����v6�н��ڶ�������ɾ����ֻ��Ҫ�����һ���������Ƿ�Ԥ��ĺ�����
 * <br>
 *
 * @see
 * @author
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2011-3-28 ����09:48:57
 */
public class ErmBugetPfClientBizProcess implements IPFClientBizProcess {

	private String budgetControl_id = "isBudgetControl";
	private String budgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0054")/*@res "�Ƿ����Ԥ��"*/;

	private String projBudgetControl_id = "isProjBudgetControl";
	private String projBudgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0032")/*@res "�Ƿ���ĿԤ�����"*/;

	private String matterAppBudgetControl_id = "isMaBudgetControl";
	private String matterAppBudgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0033")/*@res "�Ƿ���з������뵥����"*/;
	
	private String isWorkFlowFinalNode_id = "isWorkFlowFinalNode";
	private String isWorkFlowFinalNode_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0185")/*@res "�Ƿ�������󻷽�(y/n)"*/;;

	public PFClientBizRetObj execute(Container parent, PfClientBizProcessContext context) {
		PFClientBizRetObj result = new PFClientBizRetObj();

		@SuppressWarnings("rawtypes")
		List argsList = context.getArgsList();
		if (argsList == null || argsList.size() == 0)
			return result;

		Object billvo = context.getBillvo();

		for (@SuppressWarnings("rawtypes")
		Iterator iterator = argsList.iterator(); iterator.hasNext();) {
			DataField df = (DataField) iterator.next();
			Object value = df.getInitialValue();
			if (value == null)
				continue;

			StringBuffer controlMsg = new StringBuffer();
			if (budgetControl_id.equals(df.getName())) {//Ԥ�����
				if (UFBoolean.valueOf(value.toString()).booleanValue()) {
					NtbCtlInfoVO tpcontrolvo = null;
					try {
						tpcontrolvo = ErmFlowCheckInfo
								.doNtbCheck((JKBXVO) billvo);
					} catch (BusinessException e) {
						throw new BusinessRuntimeException(e.getMessage(), e);
					}


					if(tpcontrolvo != null){
						if(tpcontrolvo.isControl()){
							result.setShowPass(false);//��׼��ť�û�
						}
						String controlInfo = getYsControlInfo(tpcontrolvo);
						if(!StringUtils.isEmpty(controlInfo)){
							controlMsg.append("\n" + controlInfo);
						}
					}
				}
			}else if (projBudgetControl_id.equals(df.getName())) {//��ĿԤ�����
				if (UFBoolean.valueOf(value.toString()).booleanValue()) {
					BudgetReturnMSG control = null;
					try {
						control = ErmFlowCheckInfo
								.doProjBudgetCheck(new JKBXVO[]{(JKBXVO) billvo});
						if (control != null) {
							List<BudgetReturnVO> checkResult = control.getDetailList();
							UFBoolean isFlex = isOverBudget(checkResult);
							((JKBXVO) billvo).getParentVO().setFlexible_flag(isFlex);
							if (isFlex != null && isFlex.booleanValue()) {// ���Կ���
								controlMsg.append("\n" + control.getErrorMSG());
							}
						}
					} catch (BusinessException e) {//���Կ���
						result.setShowPass(false);
						controlMsg.append("\n" + e.getMessage());
					}
				}
			}else if(matterAppBudgetControl_id.equals(df.getName())){//��������Ԥ�����
				if(UFBoolean.valueOf(value.toString()).booleanValue()){
					checkMatterBudget(result, billvo, controlMsg);
				}
			}

			result.setHintMessage(controlMsg.toString());
		}
		
		if (StringUtils.isEmpty(result.getHintMessage() == null ? null : result.getHintMessage().trim())) {
			return null;
		}

		return result;
	}

	private void checkMatterBudget(PFClientBizRetObj result, Object billvo, StringBuffer controlMsg) {
		try {
			String param = SysinitAccessor.getInstance().getParaString(((JKBXVO)billvo).getParentVO().getPk_org(), BXParamConstant.PARAM_MTAPP_CTRL);
			if (BXParamConstant.getMTAPP_CTRL_SAVE().equals(param)) {
				return ;
			}
		
			IMatterAppCtrlService service = NCLocator.getInstance().lookup(IMatterAppCtrlService.class);

			JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone((JKBXVO) billvo);

			List<IMtappCtrlBusiVO> mtBusiVoList = new ArrayList<IMtappCtrlBusiVO>();
			
			WriteBackUtil.fillBusiVo(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, true, false, headers);


			MtappCtrlInfoVO ctrlInfo = service.matterappValidate(mtBusiVoList.toArray(new JKBXMtappCtrlBusiVO[] {}));

			if(ctrlInfo != null && ctrlInfo.getControlinfos() != null){
				result.setShowPass(false);

				for (String errorMsg : ctrlInfo.getControlinfos()) {
					if(errorMsg != null){
						controlMsg.append("\n" + errorMsg);
					}
				}
			}
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}

	private String getYsControlInfo(NtbCtlInfoVO tpcontrolvo) {
		if(tpcontrolvo == null){
			return null;
		}

		StringBuffer controlMsg = new StringBuffer();
		String[] seminfos;
		if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // ����
			seminfos = tpcontrolvo.getControlInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // Ԥ����Ϣ
			}
		} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // Ԥ��
			seminfos = tpcontrolvo.getAlarmInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // Ԥ����Ϣ
			}
		} else if ((tpcontrolvo != null)
				&& tpcontrolvo.isMayBeControl()) { // ����
			seminfos = tpcontrolvo.getFlexibleControlInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // Ԥ����Ϣ
			}
		}

		return controlMsg.toString();
	}

	public DataField[] getApplicationArgs() {
		List<DataField> dataFieldList = new ArrayList<DataField>();
		
		dataFieldList.add(new DataField(isWorkFlowFinalNode_id, isWorkFlowFinalNode_name, BasicType.BOOLEAN));
		//�ж�Ԥ���Ʒ�Ƿ�����
		boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if(istbbused){
			dataFieldList.add(new DataField(budgetControl_id, budgetControl_name, BasicType.BOOLEAN));
		}

		//�ж���ĿԤ���Ʒ�Ƿ�����
		boolean isPimused = BXUtil.isProductTbbInstalled(BXConstans.PIM_FUNCODE);
		if(isPimused){
			dataFieldList.add(new DataField(projBudgetControl_id, projBudgetControl_name, BasicType.BOOLEAN));
		}
		
		dataFieldList.add(new DataField(matterAppBudgetControl_id, matterAppBudgetControl_name, BasicType.BOOLEAN));
		return dataFieldList.toArray(new DataField[]{});
	}
	
	/**
	 * �Ƿ񳬳���ĿԤ��
	 * 
	 * @param checkResult
	 * @return
	 * @Author:ligangm
	 * @Date:2012-3-28
	 */
	private UFBoolean isOverBudget(List<BudgetReturnVO> checkResult) {
		if (!ListUtil.isEmpty(checkResult)) {
			for (Iterator<BudgetReturnVO> iterator = checkResult.iterator(); iterator.hasNext();) {
				BudgetReturnVO ctlInfoVO = iterator.next();
				if (BudgetEnum.OVERBUDGET.equals(ctlInfoVO.getWbsBudget())
						&& BudgetCtrlTypeConst.flexible_control == ctlInfoVO.getControlStyle()) {
					return UFBoolean.TRUE;
				}
			}
		}
		return UFBoolean.FALSE;
	}
}