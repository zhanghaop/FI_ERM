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
	
	public ImportAction()
	{
		ActionInitializer.initializeAction(this, IActionCode.IMPORT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ReimRulerVO[] reimrules = null;
		ReimRulerVO[] reimrulesAll = null;
		String currentBodyTableCode = this.getBillCardPanel()
				.getCurrentBodyTableCode();
		ReimRulerVO[] reimRuleVos = (ReimRulerVO[]) this.getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRulerVO.class.getName());
		ImportExcelDialog impDialog = new ImportExcelDialog(getEditor(),getBillCardPanel());
		if (impDialog.showModal() == UIDialog.ID_OK) {

			reimrules = impDialog.importFromExcel();
			// impDialog.setVisible(true);
			// 如果为覆盖方式合并VO
			reimrulesAll = (ReimRulerVO[]) ArrayUtils.addAll(reimRuleVos,
					reimrules);
			getBillCardPanel().getBillData().clearViewData();
			if (reimrules != null) {
				if (impDialog.isRBIncrement()) {
					// getBillCardPanel().getBillData().setBodyValueVO(reimRuleVos);
					getBillCardPanel().getBillData().setBodyValueVO(
							reimrulesAll);
				} else
					getBillCardPanel().getBillData().setBodyValueVO(reimrules);

			} 
			else
				getBillCardPanel().getBillData().setBodyValueVO(null);
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
	
	public BillCardPanel getBillCardPanel(){
		return getEditor().getBillCardPanel();
	}
	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

}
