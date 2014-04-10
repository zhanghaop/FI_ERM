package nc.vo.arap.bx.util;

import nc.vo.ep.bx.MessageVO;

public class ActionUtils {

	/**
	 * @param djzt 单据状态
	 * @param operation 执行动作 , @see {@link MessageVO}
	 * @param statusAllowed 允许动作执行的状态值
	 * @return 空表示验证通过, 否则返回错误提示信息
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed) {

		String strMessage = null;

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		String operationName = MessageVO.getOperationName(operation);

		switch (djzt) {
			case 0: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "单据未审核"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "单据已经审核"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "单据已经签字"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "单据状态不明"*/;
				break;
			}
		}

		return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",不能"*/+operationName;
	}


	/**
	 * @param djzt 单据状态
	 * @param operation 执行动作 , @see {@link MessageVO}
	 * @param statusAllowed 允许动作执行的状态值
	 * @param statusNotAllowed 不允许动作执行的状态值
	 * @return 空表示验证通过, 否则返回错误提示信息
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed,int[] statusNotAllowed) {

		String strMessage = null;

		String operationName = MessageVO.getOperationName(operation);

		switch (djzt) {
			case 0: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "单据未审核"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "单据已经审核"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "单据已经签字"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "单据状态不明"*/;
				break;
			}
		}

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		for (int i = 0; i < statusNotAllowed.length; i++) {
			if(statusNotAllowed[i]==djzt)
				return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",不能"*/+operationName;
		}

		return "";
	}

}