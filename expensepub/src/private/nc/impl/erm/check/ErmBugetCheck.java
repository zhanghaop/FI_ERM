package nc.impl.erm.check;

import nc.bs.erm.accruedexpense.common.AccFlowCheckInfo;
import nc.bs.erm.matterapp.common.ErmMtAppFlowCheckInfo;
//import nc.bs.pub.pf.IBusinessCheck;
import nc.vo.ep.bx.ErmFlowCheckInfo;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.NtbCtlInfoVO;

public class ErmBugetCheck {
//	public class ErmBugetCheck implements IBusinessCheck {

//	@Override
	public String check(Object billvo) {
		try {
			if (billvo instanceof JKBXVO) {
				return getYsControlAlarmInfo(ErmFlowCheckInfo
						.doNtbCheck((JKBXVO) billvo));
			}
			if (billvo instanceof AggMatterAppVO) {
				return getYsControlAlarmInfo(ErmMtAppFlowCheckInfo
						.doNtbCheck((AggMatterAppVO) billvo));
			}
			if (billvo instanceof AggAccruedBillVO) {
				return getYsControlAlarmInfo(AccFlowCheckInfo
						.doNtbCheck((AggAccruedBillVO) billvo));
			}
			return null;
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}

	private String getYsControlAlarmInfo(NtbCtlInfoVO tpcontrolvo) {
		if (tpcontrolvo == null) {
			return null;
		}
		StringBuffer controlMsg = new StringBuffer();
		String[] seminfos;
		if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // Ô¤¾¯
			seminfos = tpcontrolvo.getAlarmInfos();
			for (int j = 0; j < seminfos.length; j++) {
				controlMsg.append("\n" + seminfos[j]); // Ô¤¾¯ÐÅÏ¢
			}
		}
		return controlMsg.toString();
	}

//	@Override
	public Object checkCallBack(Object billvo) {
		if (billvo instanceof JKBXVO) {
			((JKBXVO) billvo).setHasNtbCheck(true);
		}
		if (billvo instanceof AggMatterAppVO) {
			((AggMatterAppVO) billvo).getParentVO().setHasntbcheck(UFBoolean.TRUE);
		}
		if (billvo instanceof AggAccruedBillVO) {
			((AggAccruedBillVO) billvo).getParentVO().setHasntbcheck(UFBoolean.TRUE);
		}
		
		return billvo;
	}

}

