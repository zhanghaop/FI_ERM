package nc.ui.er.reimtype.action;


import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.ui.er.reimtype.view.ReimTypeEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.reimtype.ReimTypeVO;


public class retDeleteAction extends BatchDelLineAction {

	/**
	 * @author liansg
	 */
	private static final long serialVersionUID = 1L;

	private BatchBillTable editor = null;

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}
	public void doAction(ActionEvent e) throws Exception {
		getEditor().getBillCardPanel().stopEditing();
		ReimTypeVO expVO = (ReimTypeVO) this.getModel().getSelectedData();
		String pk_reimtype = expVO.getPk_reimtype();

		//��ѯ������׼�Լ������Ƿ�Ӧ�ñ�������
		if(pk_reimtype!=null){
			boolean isreimtype = NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class).getIsReimtypeUsed(pk_reimtype);
			if(isreimtype){
				MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0000")/*@res "����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0022")/*@res "�ñ��������ѱ�����,����ɾ����"*/);
				return ;
			}
		}
		//��������ɾ��
		super.doAction(e);

	}
	@Override
	protected boolean isActionEnable() {
		if(this.getModel().getUiState() == UIState.EDIT){
			return true;
		}
		ReimTypeVO rvtVO = (ReimTypeVO) getModel()
				.getSelectedData();
		boolean flag = true;
		if (rvtVO != null) {
			flag = ((ReimTypeEditor) getEditor())
					.isShareReimType(rvtVO);
		}
		return flag;
	}

}