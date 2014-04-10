package nc.ui.erm.costshare.common;

import java.awt.Container;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;

/**
 * ��Ƭ����༭����ʱͳһ��ѯ�ʿ�
 *
 * @author zhulin
 *
 */
public class CommonConfirmDialogUtils {

	public static int showConfirmUnReportDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0030")/*@res "ȷ��ȡ���ϱ�"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0031")/*@res "��ȷ��Ҫȡ���ϱ���ѡ������"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmReportDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0032")/*@res "ȷ���ϱ�"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0033")/*@res "��ȷ��Ҫ�ϱ���ѡ������"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmSaveDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0034")/*@res "ȷ�ϱ���"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0035")/*@res "��ȷ���Ƿ�Ҫ�������޸ĵ����ݣ�"*/;
		return MessageDialog.showYesNoCancelDlg(parent, TITLE, QUESTION,
				UIDialog.ID_CANCEL);
	}

	public static int showConfirmDeleteDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0036")/*@res "ȷ��ɾ��"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0037")/*@res "��ȷ��Ҫɾ����ѡ������?"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	/**
	 * ѯ���û��Ƿ�Ҫȡ���� ����ֵΪUIDialog.ID_YES ���� UIDialog.ID_NO
	 *
	 * @param parent
	 * @return
	 */
	public static int showConfirmCancelDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0038")/*@res "ȷ��ȡ��"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0039")/*@res "�Ƿ�ȷ��Ҫȡ����"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmDisableDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0040")/*@res "ȷ��ͣ��"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0041")/*@res "�Ƿ�ȷ��ͣ�ã�"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmEnableDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0042")/*@res "ȷ������"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0043")/*@res "�Ƿ�ȷ�����ã�"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmCancelDialog2(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0038")/*@res "ȷ��ȡ��"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0044")/*@res "ȡ���·���ɾ����˾��д���ʾ���Ϣ���Ƿ������"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmCancelDialog3(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0038")/*@res "ȷ��ȡ��"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0045")/*@res "ȡ���ش���ս�ɾ�����е�Ӧ�Է�����Ϣ���Ƿ������"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

	public static int showConfirmCommitDialog(Container parent) {
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0046")/*@res "ȷ���ύ"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0047")/*@res "�Ƿ�ȷ��Ҫ�ύ��ѡ���ݣ�"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION,
				UIDialog.ID_NO);
	}

}