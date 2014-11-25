package nc.impl.erm.check;

import nc.bs.pub.pf.IBusinessCheck;
import nc.vo.ep.bx.ErmFlowCheckInfo;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.tb.control.NtbCtlInfoVO;

public class ErmBugetCheck implements IBusinessCheck {

	@Override
	public String check(Object billvo) {
		try {
			return getYsControlAlarmInfo(ErmFlowCheckInfo.doNtbCheck((JKBXVO) billvo));
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}
	
	private String getYsControlAlarmInfo(NtbCtlInfoVO tpcontrolvo) {
		if(tpcontrolvo == null){
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

	@Override
	public Object checkCallBack(Object billvo) {
		((JKBXVO) billvo).setHasNtbCheck(true);
		return billvo;
	}

}
