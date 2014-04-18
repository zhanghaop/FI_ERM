package nc.ui.arap.bx;

import nc.ui.pub.ButtonObject;

public class ButtonUtil {

	/**
	 * ״̬����ť��ʾ��Ϣ
	 * @author chendya
	 * @author chendya
	 * @param msgType
	 * @param button
	 * @return
	 */
	public static String getButtonHintMsg(Integer msgType, ButtonObject button) {
		
		final String btncode = button.getName();
		final String suffixSuccess = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0008",null,new String[]{btncode})/*@res "�ɹ�"*/;
		final String suffixFail = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0016",null,new String[]{btncode})/*@res "ʧ��"*/;
		
		switch (msgType.intValue()) {
		case 0:
			// �����ɹ�
			return  suffixSuccess;

		case -1:
			// ����ʧ��
			return suffixFail;
		default:
			// ���ڲ���
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0007",null,new String[]{btncode})/*@res "����"*/;
		}
	}

	/**
	 * ���ڵ��������Ϣ����
	 */
	public static int MSG_TYPE_ING = 99;

	/**
	 * �����ɹ���Ϣ����
	 */
	public static int MSG_TYPE_SUCCESS = 0;

	/**
	 * ����ʧ����Ϣ����
	 */
	public static int MSG_TYPE_FAIL = -1;

}