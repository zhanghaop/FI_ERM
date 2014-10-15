package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;

/**
 * @author chenshuaia
 * 
 *         打印活动
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
		try {
			printByNodeKey(null);
		} catch (Exception e) {// 无打印模板的情况下，取默认模板
			printByNodeKey(ErmMatterAppConst.MatterApp_TRADETYPE_Travel);
		}
	}
	
	private void printByNodeKey(String nodeKey) {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI(), getDataSource());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(MatterAppUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);
		entry.selectTemplate();
		entry.preview();
	}

	public String getNodeKey() {
		String nodeCode = getModel().getContext().getNodeCode();
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(nodeCode) ||
				ErmMatterAppConst.MAPP_NODECODE_QY.equals(nodeCode)) {
			return ((MAppModel) getModel()).getDjlxbm();
		} else {
			return null;
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