package nc.ui.erm.billcontrast.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;
import nc.vo.util.ManageModeUtil;
/**
 * 批量保存
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class BilSaveAction extends BatchSaveAction{
	public void doAction(ActionEvent e) throws Exception {
		if (isDoBeforeAction(this, e)) {
			beforeSave();
			super.doAction(e);

		}
	}
	/**
	 * 校验交易类型不可以是空
	 */
	private void beforeSave() throws BusinessException{
		getEditor().getBillCardPanel().stopEditing();
		BillcontrastVO[] vos=this.getModel().getRows().toArray(new BillcontrastVO[]{});
		for(BillcontrastVO vo:vos){
			String src_tradetype=vo.getSrc_tradetypeid();
			String des_tradetype=vo.getDes_tradetypeid();
			if(src_tradetype == null || "".equals(src_tradetype)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0000")/*@res "需要输入来源交易类型"*/);
			}
			if(des_tradetype == null || "".equals(des_tradetype)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0001")/*@res "需要输入目标交易类型"*/);
			}
		}

	}
	
	public boolean isDoBeforeAction(Action action, ActionEvent e) {
		BillScrollPane bsp = getEditor().getBillCardPanel().getBodyPanel();
		int rownum = bsp.getTable().getSelectedRow();
		int rowState = getEditor().getBillCardPanel().getBillModel().getRowState(rownum);
		if(BillModel.NORMAL!=rowState){
			if (!ManageModeUtil.manageable(getModel().getSelectedData(), getModel()
					.getContext())) {
				MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
						null, getErrorMsg());
				return false;
			}
		}
		return true;

	}
	
	public String getErrorMsg() {
		return ManageModeUtil.getDisManageableMsg(getModel().getContext()
				.getNodeType());
	}

}