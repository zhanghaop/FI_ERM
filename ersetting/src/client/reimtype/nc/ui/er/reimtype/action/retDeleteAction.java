package nc.ui.er.reimtype.action;


import java.awt.event.ActionEvent;

import nc.bs.uif2.validation.ValidationFailure;
import nc.ui.er.reimtype.view.ReimTypeEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.util.BDReferenceChecker;


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

		//查询报销标准以及单据是否应用报销类型
		// 引用校验
		ValidationFailure res = BDReferenceChecker.getInstance().validate(expVO);
		if(res != null){
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0000")/*@res "错误"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0022")/*@res "该报销类型已被引用,不能删除！"*/);
			return ;
		}
		
//		String pk_reimtype = expVO.getPk_reimtype();
//		if(pk_reimtype!=null){
//			boolean isreimtype = NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class).getIsReimtypeUsed(pk_reimtype);
//			if(isreimtype){
//				MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0000")/*@res "错误"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0022")/*@res "该报销类型已被引用,不能删除！"*/);
//				return ;
//			}
//		}
		//调用真正删除
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