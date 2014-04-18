package nc.ui.erm.costshare.common;

import java.awt.Container;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;

/**
 * 卡片界面编辑数据时统一的询问框
 *
 * @author zhulin
 *
 */
public class CommonConfirmDialogUtils {

	public static int showConfirmUnReportDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0030")/*@res "确认取消上报"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0031")/*@res "您确定要取消上报所选数据吗？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmReportDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0032")/*@res "确认上报"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0033")/*@res "您确定要上报所选数据吗？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmSaveDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0034")/*@res "确认保存"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0035")/*@res "请确认是否要保存已修改的数据？"*/;
		return MessageDialog.showYesNoCancelDlg(parent, TITLE, QUESTION,
				UIDialog.ID_CANCEL);
	}

	public static int showConfirmDeleteDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0036")/*@res "确认删除"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0037")/*@res "您确定要删除所选数据吗?"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	/**
	 * 询问用户是否要取消。 返回值为UIDialog.ID_YES 或者 UIDialog.ID_NO
	 *
	 * @param parent
	 * @return
	 */
	public static int showConfirmCancelDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0038")/*@res "确认取消"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0039")/*@res "是否确认要取消？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmDisableDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0040")/*@res "确认停用"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0041")/*@res "是否确认停用？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmEnableDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0042")/*@res "确认启用"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0043")/*@res "是否确认启用？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmCancelDialog2(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0038")/*@res "确认取消"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0044")/*@res "取消下发将删除公司填写的问卷信息，是否继续？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmCancelDialog3(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0038")/*@res "确认取消"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0045")/*@res "取消重大风险将删除已有的应对方案信息，是否继续？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmCommitDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0046")/*@res "确认提交"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0047")/*@res "是否确定要提交所选数据？"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

}