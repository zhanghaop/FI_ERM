package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.PrintAction;
/**
 * @author wangled
 * ¥Ú”°
 */
@SuppressWarnings("serial")
public class PrintCardAction extends PrintAction {
	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null&&model.getUiState()== UIState.NOT_EDIT;
	}
	@Override
	public void doAction(ActionEvent e) {
		super.doAction(e);
		printInfo();
	}
	private IDataSource dataSource;
	private void printInfo() {
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,
				this.model.getContext().getEntranceUI());
		PrintEntry entry = new PrintEntry(frame);
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null,"card");
		entry.selectTemplate();
		entry.setDataSource(getDataSource());
		entry.preview();

	}
	
	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}
}
