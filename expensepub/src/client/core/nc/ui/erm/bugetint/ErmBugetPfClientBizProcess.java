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
 * TODO 预算柔性控制。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br>
 * 说明：一、是否控制预算：为Y，则超预算，在审批时会在审批框给予提示***； 为N或者不设置，超预算在审批时不给提示.
   		 二、是否柔性预算控制批准：为Y，则超预算审批流也能通过，并且结束；为N或者不设置，审批不能通过；并且此参数要配置在末尾审批人处才生效
		 这个是v5系列的实现，
		其中第二个参数的处理主要是为了处理审批流转过程中的预算金额占用问题，
		v6因为预算提供了预占数的控制， 并且是收付和报销都提供了预占用的控制策略,
		所以v6中将第二个参数删除，只需要处理第一个参数和是否超预算的函数。
 * <br>
 *
 * @see
 * @author
 * @version V6.0
 * @since V6.0 创建时间：2011-3-28 上午09:48:57
 */
public class ErmBugetPfClientBizProcess implements IPFClientBizProcess {

	private String budgetControl_id = "isBudgetControl";

//	private String budgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011V57","UPP2011V57-000003")/*@res "是否控制预算"*/;
	private String budgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0054")/*@res "是否控制预算"*/;
//	private String hasbudgetControl_id = "hasBudgetControl";
//	private String hasbudgetControl_name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011V57","UPP2011V57-000004")/*@res "是否柔性预算控制批准"*/;
//	private String hasbudgetControl_name = "是否柔性预算控制批准";
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
					if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // 控制
						result.setShowPass(false);
						seminfos = tpcontrolvo.getControlInfos();
						for (int j = 0; j < seminfos.length; j++) {
							controlMsg.append("\n" + seminfos[j]); // 预警信息
						}
					} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // 预警
						seminfos = tpcontrolvo.getAlarmInfos();
						for (int j = 0; j < seminfos.length; j++) {
							controlMsg.append("\n" + seminfos[j]); // 预警信息
						}
					} else if ((tpcontrolvo != null)
							&& tpcontrolvo.isMayBeControl()) { // 柔性
						seminfos = tpcontrolvo.getFlexibleControlInfos();
						for (int j = 0; j < seminfos.length; j++) {
							controlMsg.append("\n" + seminfos[j]); // 预警信息
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

		//判断预算产品是否启用
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