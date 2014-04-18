package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.actions.AddLineAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

public class AccAddAction extends AddAction {

	private static final long serialVersionUID = 1L;

	private AddLineAction addLineAction;

	private BillForm editor;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 新增切换交易类型
		if (ErmAccruedBillConst.ACC_NODECODE_MN.equals(getModel().getContext().getNodeCode())) {
			String oldDjlxbm = ((AccManageAppModel) getModel()).getCurrentTradeTypeCode();
			try {
				String selectDjlxbm = ((AccManageAppModel) getModel()).getSelectBillTypeCode();
				((AccManageAppModel) getModel()).setCurrentTradeTypeCode(selectDjlxbm);
				if (selectDjlxbm != null && !selectDjlxbm.equals(editor.getNodekey())) {
					((AccMNBillForm) editor).loadCardTemplet(selectDjlxbm);
				}
			} catch (Exception e2) {
				((AccManageAppModel) getModel()).setCurrentTradeTypeCode(oldDjlxbm);
				editor.setNodekey(oldDjlxbm);
				throw e2;
			}
		}
		
		// 检查交易类型是否被封存
		checkTradetype();

		super.doAction(e);

		BillTabbedPane tabPane = editor.getBillCardPanel().getBodyTabbedPane();
		int index = tabPane.indexOfComponent(editor.getBillCardPanel().getBodyPanel(
				ErmAccruedBillConst.Accrued_MDCODE_DETAIL));
		if (index >= 0) {
			editor.getBillCardPanel().getBodyTabbedPane().setSelectedIndex(index);
			getAddLineAction().doAction(e);
		}
	}

	private void checkTradetype() throws BusinessException {
		String currentTradeType = ((AccManageAppModel) getModel()).getCurrentTradeTypeCode();
		DjLXVO tradeTypeVo = ((AccManageAppModel) getModel()).getTradeTypeVo(currentTradeType);
		if (tradeTypeVo == null || tradeTypeVo.getFcbz().booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000171")/**
			 * @res
			 *      * "该节点单据类型已被封存，不可操作节点！"
			 */
			);
		}

	}

	public AddLineAction getAddLineAction() {
		return addLineAction;
	}

	public void setAddLineAction(AddLineAction addLineAction) {
		this.addLineAction = addLineAction;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
