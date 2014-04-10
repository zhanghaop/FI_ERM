package nc.vo.arap.bx.util;

import nc.vo.ep.bx.MessageVO;

public class ActionUtils {

	/**
	 * @param djzt ����״̬
	 * @param operation ִ�ж��� , @see {@link MessageVO}
	 * @param statusAllowed ������ִ�е�״ֵ̬
	 * @return �ձ�ʾ��֤ͨ��, ���򷵻ش�����ʾ��Ϣ
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
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "����δ���"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "�����Ѿ����"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "�����Ѿ�ǩ��"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "����״̬����"*/;
				break;
			}
		}

		return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",����"*/+operationName;
	}


	/**
	 * @param djzt ����״̬
	 * @param operation ִ�ж��� , @see {@link MessageVO}
	 * @param statusAllowed ������ִ�е�״ֵ̬
	 * @param statusNotAllowed ��������ִ�е�״ֵ̬
	 * @return �ձ�ʾ��֤ͨ��, ���򷵻ش�����ʾ��Ϣ
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed,int[] statusNotAllowed) {

		String strMessage = null;

		String operationName = MessageVO.getOperationName(operation);

		switch (djzt) {
			case 0: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "����δ���"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "�����Ѿ����"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "�����Ѿ�ǩ��"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "����״̬����"*/;
				break;
			}
		}

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		for (int i = 0; i < statusNotAllowed.length; i++) {
			if(statusNotAllowed[i]==djzt)
				return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",����"*/+operationName;
		}

		return "";
	}

}