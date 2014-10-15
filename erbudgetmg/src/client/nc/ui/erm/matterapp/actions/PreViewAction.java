package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

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
import nc.vo.er.exception.ExceptionHandler;

/**
 * @author chenshuaia
 * 
 *         打印活动
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
		try{
			printByNodeKey(null);
		}catch (Exception e) {//无打印模板的情况下，取默认模板
			ExceptionHandler.consume(e);
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
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(nodeCode) || ErmMatterAppConst.MAPP_NODECODE_QY.equals(nodeCode)) {
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