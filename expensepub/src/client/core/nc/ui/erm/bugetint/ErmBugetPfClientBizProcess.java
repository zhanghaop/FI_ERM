package nc.ui.erm.bugetint;

import java.awt.Container;
import java.util.Iterator;
import java.util.List;
import nc.ui.pf.pub.PFClientBizRetObj;
import nc.ui.pub.pf.IPFClientBizProcess;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.ErmFlowCheckInfo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.PfClientBizProcessContext;
import nc.vo.tb.control.NtbCtlInfoVO;
import nc.vo.wfengine.core.data.BasicType;
import nc.vo.wfengine.core.data.DataField;
/**
 * <p>
 * TODO Ԥ�����Կ��ơ�
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

//	private String budgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011V57","UPP2011V57-000003")/*@res "�Ƿ����Ԥ��"*/;
	private String budgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0054")/*@res "�Ƿ����Ԥ��"*/;
//	private String hasbudgetControl_id = "hasBudgetControl";
//	private String hasbudgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011V57","UPP2011V57-000004")/*@res "�Ƿ�����Ԥ�������׼"*/;
//	private String hasbudgetControl_name = "�Ƿ�����Ԥ�������׼";
	public PFClientBizRetObj execute(Container parent,
			PfClientBizProcessContext context) {

		PFClientBizRetObj result = new PFClientBizRetObj();

		List argsList = context.getArgsList();
		if (argsList == null || argsList.size() == 0)
			return result;

		Object billvo = context.getBillvo();

		for (Iterator iterator = argsList.iterator(); iterator.hasNext();) {
			DataField df = (DataField) iterator.next();
			if (budgetControl_id.equals(df.getName())) {
				Object value = df.getInitialValue();
				if (value == null)
					break;
				if (UFBoolean.valueOf(value.toString()).booleanValue()) {
					NtbCtlInfoVO tpcontrolvo = null;
					try {
						tpcontrolvo = ErmFlowCheckInfo
								.doNtbCheck((JKBXVO) billvo);
					} catch (BusinessException e) {
						throw new BusinessRuntimeException(e.getMessage(), e);
					}
					StringBuffer controlMsg = new StringBuffer();
					String[] seminfos = null;
					if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // ����
						result.setShowPass(false);
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
					result.setHintMessage(controlMsg.toString());
				}
				break;
			}
		}

//		for (Iterator iterator = argsList.iterator(); iterator.hasNext();) {
//			DataField df = (DataField) iterator.next();
//			if (hasbudgetControl_id.equals(df.getName())) {
//				Object value = df.getInitialValue();
//				if (value == null)
//					break;
//				if (UFBoolean.valueOf(value.toString()).booleanValue()) {
//					BXVO vo = (BXVO) billvo;
//					vo.setHasNtbCheck(true);
//				}
//				break;
//			}
//		}
		return result;
	}

	public DataField[] getApplicationArgs() {

		//�ж�Ԥ���Ʒ�Ƿ�����
//		boolean istbbused = BXUtil.isProductInstalled(BXUiUtil.getPK_group(),BXConstans.TBB_FUNCODE);
		boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		DataField arg = null;
		DataField arg1 = null;
		if(istbbused){
			arg = new DataField(budgetControl_id, budgetControl_name, BasicType.BOOLEAN);
//			arg1 = new DataField(hasbudgetControl_id, hasbudgetControl_name, BasicType.BOOLEAN);
		}
//		return new DataField[] { arg, arg1 };
		return new DataField[] { arg };


	}



}