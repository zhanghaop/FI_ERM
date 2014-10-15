package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
/**
 * @author wangled
 * ¥Ú”°
 */
@SuppressWarnings("serial")
public class PrintListAction extends NCAction {
	private BillManageModel model;
	private IDataSource metidataSource;

	public PrintListAction() {
		super();
		this.setBtnName(ErmActionConst.getPrintListName());
		setCode(ErmActionConst.PRINTLIST);
	}
	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null&&model.getUiState()== UIState.NOT_EDIT;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		printInfo();
	}

	private void printInfo() {
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
				this.model.getContext().getEntranceUI());
		PrintEntry entry = new PrintEntry(frame);
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null,"list");
		entry.selectTemplate();
		entry.setDataSource(getMetidataSource());
		entry.preview();

	}

	public BillManageModel getModel() {
		return model;
	}
	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	public IDataSource getMetidataSource() {
		return metidataSource;
	}
	public void setMetidataSource(IDataSource metidataSource) {
		this.metidataSource = metidataSource;
	}

}