package nc.ui.er.djlx.listener;

import java.awt.Container;

import nc.bs.logging.Log;
import nc.impl.er.proxy.ProxyDjlx;
import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/** ���Ӱ�ť�Ĵ����� */
public class DelButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() throws BusinessException {
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common", "UCH051"));
		// TODO �Զ����ɷ������
		BillTypeVO vo = (BillTypeVO) getDataModel().getCurrData();
		int rs = MessageDialog.showOkCancelDlg((Container) getMainFrame(), NCLangRes.getInstance().getStrByID("_Beans", "UPP_Beans-000093")/* @res "ѯ��" */, NCLangRes.getInstance().getStrByID("common", "UCH002")/*
																																																					 * @res
																																																					 * "ɾ��ѡ���ĵ�������"
																																																					 */, MessageDialog.ID_CANCEL);
		if (rs == nc.ui.pub.beans.UIDialog.ID_CANCEL) {
			getMainFrame().showHintMessage("");
			return false;
		}
		ProxyDjlx.getIArapBillTypePublic().deleteBillType(vo);
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("20060101", "UPP20060101-000041")/* @res "ɾ���ɹ�" */);
		getDataModel().removeData(getDataModel().getCurrData());
		makeItemNull();
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common", "UCH006"));
		return true;
	}

	private void makeItemNull() {
		try {
			getBillCardPanel().getHeadItem("djlxjc").setValue(null);
			getBillCardPanel().getHeadItem("djlxmc").setValue(null);
			getBillCardPanel().getHeadItem("djlxjc_remark").setValue(null);
			getBillCardPanel().getHeadItem("djlxmc_remark").setValue(null);
			getBillCardPanel().getHeadItem("djlxbm").setValue(null);
			getBillCardPanel().getHeadItem("fcbz").setValue(UFBoolean.FALSE);
			getBillCardPanel().getHeadItem("isbankrecive").setValue(UFBoolean.FALSE);
			getBillCardPanel().getHeadItem("defcurrency").setValue(null);
			getBillCardPanel().getHeadItem("mjbz").setValue(UFBoolean.TRUE);
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			showErrorMessage(e.getMessage());
		}
	}
}
