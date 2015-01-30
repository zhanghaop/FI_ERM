package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.erm.matterapp.actions.print.MatterAppMetaDataSingleDataSource;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * @author chenshuaia
 * 
 *         打印活动
 */
@SuppressWarnings( { "serial" })
public class PrintAction extends NCAction {
	private MatterAppMNBillForm billForm;
	
	private MAppModel model;
	
	public PrintAction(){
		ActionInitializer.initializeAction(this, IActionCode.PRINT);
	}
	
	@Override
	public void doAction(ActionEvent e) throws BusinessException{
		printInfo();
	}

	protected void printInfo() throws BusinessException {
		try {
			printByNodeKey(null);
		} catch (Exception e) {// 无打印模板的情况下，取默认模板
			printByNodeKey(ErmMatterAppConst.MatterApp_TRADETYPE_Travel);
		}
	}
	
	protected void printByNodeKey(String nodeKey) throws BusinessException {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(MatterAppUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);
		
		setDatasource(entry);
		if(entry.selectTemplate() == 1){
			entry.print();
		}
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
	
	public void setDatasource(PrintEntry entry) throws BusinessException {
		BillManageModel model = (BillManageModel) getModel();
		AggMatterAppVO[] vos = null;

		if (getModel() instanceof BillManageModel) {
			Object[] objs = model.getSelectedOperaDatas();
			if (objs != null) {
				vos = new AggMatterAppVO[objs.length];
				for (int i = 0; i < vos.length; i++) {
					vos[i] = (AggMatterAppVO) objs[i];
				}
			}
		}

		if ((((vos == null) || (vos.length == 0)))) {
			vos = new AggMatterAppVO[] { (AggMatterAppVO) model.getSelectedData() };
		}

		if (vos != null && vos.length > 0) {
			for (AggMatterAppVO vo : vos) {
				if ((vo.getChildrenVO() == null || vo.getChildrenVO().length == 0)) {
					// 补齐表体信息
					vo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPK(vo.getParentVO().getPrimaryKey());
				}

				MatterAppMetaDataSingleDataSource dataSource = new MatterAppMetaDataSingleDataSource(vo);
				dataSource.setModel(getModel());
				entry.setDataSource(dataSource);
			}
		}
	}
	
	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && getModel().getUiState() == UIState.NOT_EDIT;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		model.addAppEventListener(this);
		this.model = model;
	}
}