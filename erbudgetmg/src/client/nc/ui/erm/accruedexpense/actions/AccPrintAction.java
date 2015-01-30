package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * 卡片打印
 * 
 * @author chenshuaia
 * 
 */
public class AccPrintAction extends NCAction {
	private static final long serialVersionUID = 1L;

	protected AbstractUIAppModel model;

	public AccPrintAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.PRINT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		printInfo();
	}

	private void printInfo() throws BusinessException {
		try {
			printByNodeKey(null);
		} catch (Exception e) {// 无打印模板的情况下，取默认模板
			printByNodeKey(ErmAccruedBillConst.AccruedBill_Tradetype_Travel);
		}
	}

	protected void printByNodeKey(String nodeKey) throws BusinessException {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);
		
		setDatasource(entry);
		if(entry.selectTemplate() == 1){
			entry.print();
		}
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
	
	public void setDatasource(PrintEntry entry) throws BusinessException {
		BillManageModel model = (BillManageModel) getModel();
		AggAccruedBillVO[] vos = null;

		if (getModel() instanceof BillManageModel) {
			Object[] objs = model.getSelectedOperaDatas();
			if (objs != null) {
				vos = new AggAccruedBillVO[objs.length];
				for (int i = 0; i < vos.length; i++) {
					vos[i] = (AggAccruedBillVO) objs[i];
				}
			}
		}

		if ((((vos == null) || (vos.length == 0)))) {
			vos = new AggAccruedBillVO[] { (AggAccruedBillVO) model.getSelectedData() };
		}

		if (vos != null && vos.length > 0) {
			for (AggAccruedBillVO vo : vos) {
				if ((vo.getChildrenVO() == null || vo.getChildrenVO().length == 0)) {
					// 补齐表体信息
					vo = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPk(vo.getParentVO().getPrimaryKey());
				}

				AccruedPrintMetaDataSingleDataSource dataSource = new AccruedPrintMetaDataSingleDataSource(vo);
				dataSource.setModel(getModel());
				entry.setDataSource(dataSource);
			}
		}
	}
}
