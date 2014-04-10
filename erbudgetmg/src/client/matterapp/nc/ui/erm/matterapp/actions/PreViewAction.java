package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;

/**
 * @author chenshuaia
 * 
 *         ¥Ú”°ªÓ∂Ø
 */
@SuppressWarnings( { "serial" })
public class PreViewAction extends NCAction {
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	public PreViewAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.PREVIEW);
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && getModel().getUiState() == UIState.NOT_EDIT;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		printInfo();
	}

	private IDataSource dataSource;

	private void printInfo() {
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.getModel().getContext().getEntranceUI());
		PrintEntry entry = new PrintEntry(frame);
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(MatterAppUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, getNodeKey());
		entry.selectTemplate();
		entry.setDataSource(getDataSource());
		entry.preview();
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

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}
}