package nc.ui.arap.bx;

import nc.ui.pub.ButtonObject;

public class ButtonUtil {

	/**
	 * 状态栏按钮提示消息
	 * @author chendya
	 * @author chendya
	 * @param msgType
	 * @param button
	 * @return
	 */
	public static String getButtonHintMsg(Integer msgType, ButtonObject button) {
		
		final String btncode = button.getName();
		final String suffixSuccess = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0008",null,new String[]{btncode})/*@res "成功"*/;
		final String suffixFail = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0016",null,new String[]{btncode})/*@res "失败"*/;
		
		switch (msgType.intValue()) {
		case 0:
			// 操作成功
			return  suffixSuccess;

		case -1:
			// 操作失败
			return suffixFail;
		default:
			// 正在操作
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0007",null,new String[]{btncode})/*@res "正在"*/;
		}
	}

	/**
	 * 正在点击操作消息类型
	 */
	public static int MSG_TYPE_ING = 99;

	/**
	 * 操作成功消息类型
	 */
	public static int MSG_TYPE_SUCCESS = 0;

	/**
	 * 操作失败消息类型
	 */
	public static int MSG_TYPE_FAIL = -1;

}