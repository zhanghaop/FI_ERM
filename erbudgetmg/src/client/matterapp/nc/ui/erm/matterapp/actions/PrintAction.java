package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;

/**
 * @author chenshuaia
 * 
 *         ¥Ú”°ªÓ∂Ø
 */
@SuppressWarnings( { "serial" })
public class PrintAction extends nc.ui.uif2.actions.PrintAction {
	private MatterAppMNBillForm billForm;
	
	private IDataSource dataSource;

	@Override
	public void doAction(ActionEvent e){
		printInfo();
	}

	private void printInfo() {
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.getModel().getContext().getEntranceUI());
		PrintEntry entry = new PrintEntry(frame);
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(MatterAppUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, getNodeKey());
		entry.selectTemplate();
		entry.setDataSource(getDataSource());

		entry.print();
	}

	public String getNodeKey() {
		String nodeCode = getModel().getContext().getNodeCode();
		if (!ErmMatterAppConst.MAPP_NODECODE_MN.equals(nodeCode)) {
			return null;
		} else {
			return ((MAppModel) getModel()).getDjlxbm();
		}
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}
}