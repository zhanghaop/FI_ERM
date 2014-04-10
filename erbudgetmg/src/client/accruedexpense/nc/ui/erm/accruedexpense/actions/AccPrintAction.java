package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;

/**
 * 卡片打印
 * 
 * @author chenshuaia
 * 
 */
public class AccPrintAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private IDataSource dataSource;

	protected AbstractUIAppModel model;

	public AccPrintAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.PRINT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		printInfo();
	}

	private void printInfo() {
		try {
			printByNodeKey(null);
		} catch (Exception e) {// 无打印模板的情况下，取默认模板
			printByNodeKey(ErmAccruedBillConst.AccruedBill_Tradetype_Travel);
		}
	}

	protected void printByNodeKey(String nodeKey) {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI(), getDataSource());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);
		entry.selectTemplate();
		entry.preview();
	}

	public String getNodeKey() {
		String nodeCode = getModel().getContext().getNodeCode();
		if (ErmAccruedBillConst.ACC_NODECODE_MN.equals(nodeCode)
				|| ErmAccruedBillConst.ACC_NODECODE_QRY.equals(nodeCode)) {
			return ((AccManageAppModel) getModel()).getCurrentTradeTypeCode();
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

	@Override
	protected boolean isActionEnable() {
		return model.getUiState() == UIState.NOT_EDIT && model.getSelectedData() != null;
	}

	@ModelMethod(modelType = ModelType.AbstractUIAppModel, methodType = MethodType.GETTER)
	public AbstractUIAppModel getModel() {
		return model;
	}

	@ModelMethod(modelType = ModelType.AbstractUIAppModel, methodType = MethodType.SETTER)
	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
}
