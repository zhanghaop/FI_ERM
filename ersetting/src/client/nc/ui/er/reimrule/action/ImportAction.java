package nc.ui.er.reimrule.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.er.reimrule.ImportExcelDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.er.reimrule.ReimRulerVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 数据导入动作
 * 
 * @author lkp
 * 
 */
@SuppressWarnings("serial")
public class ImportAction extends NCAction {

	private AbstractUIAppModel model = null;
	private BatchBillTable editor;

	public ImportAction() {
		ActionInitializer.initializeAction(this, IActionCode.IMPORT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ReimRulerVO[] reimrules = null;
		ReimRulerVO[] reimrulesAll = null;
		String currentBodyTableCode = this.getBillCardPanel().getCurrentBodyTableCode();
		ReimRulerVO[] reimRuleVos = (ReimRulerVO[]) this.getBillCardPanel().getBillData().getBodyValueVOs(currentBodyTableCode, ReimRulerVO.class.getName());
		ImportExcelDialog impDialog = new ImportExcelDialog(getEditor(), getBillCardPanel());
		if (impDialog.showModal() == UIDialog.ID_OK) {

			reimrules = impDialog.importFromExcel();
			reimrulesAll = (ReimRulerVO[]) ArrayUtils.addAll(reimRuleVos, reimrules);

			int rowCount = editor.getModel().getRowCount();
			if (rowCount > 0) {
				for (int i = rowCount - 1; i >= 0; i--) {
					editor.getModel().delLine(i);
				}
			}

			if (reimrules != null) {
				if (impDialog.isRBIncrement()) {
					editor.getModel().addLines(reimrulesAll);
				} else {
					editor.getModel().addLines(reimrules);
				}
			}
			getBillCardPanel().getBillModel().loadLoadRelationItemValue();
		}
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		return model.getUiState() == UIState.EDIT;
	}

	public BillCardPanel getBillCardPanel() {
		return getEditor().getBillCardPanel();
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

}
