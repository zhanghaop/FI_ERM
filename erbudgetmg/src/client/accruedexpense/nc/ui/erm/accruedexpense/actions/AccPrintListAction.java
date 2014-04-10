package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;

public class AccPrintListAction extends NCAction {

	private BillManageModel model;
	private IDataSource dataSource;

	private static final long serialVersionUID = 1L;

	public AccPrintListAction() {
		super();
		setCode(ErmActionConst.PRINTLIST);
		setBtnName(ErmActionConst.getPrintListName());
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		printInfo();
	}

	private void printInfo() {
		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI(), getDataSource());

		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, "list");
		entry.selectTemplate();
		entry.preview();
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}
}
