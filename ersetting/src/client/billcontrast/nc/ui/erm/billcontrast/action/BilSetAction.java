package nc.ui.erm.billcontrast.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.billcontrast.IErmBillcontrastManage;
import nc.itf.erm.billcontrast.IErmBillcontrastQuery;
import nc.ui.erm.billcontrast.model.BillcontrastModelDataManager;
import nc.ui.erm.billcontrast.view.BillContrastBatchBillTable;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;

@SuppressWarnings("serial")
public class BilSetAction extends NCAction {
	private BatchBillTableModel model;
	private BillContrastBatchBillTable bilTable;
	private BillcontrastModelDataManager dataManager;

	public BilSetAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0002")/*@res "生成预置数据"*/);
		setCode(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0002")/*@res "生成预置数据"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 先要选择一个财务组织
		String pk_org = getModel().getContext().getPk_org();
		if (pk_org == null) {
			((DefaultExceptionHanler) getExceptionHandler())
					.setErrormsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0003")/*@res "生成预置数据失败"*/);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0004")/*@res "请选择一个财务组织"*/);
		}
		String pk_group = ErUiUtil.getPK_group();
		BillcontrastVO[] vos = getSevice().queryAllByGloble().clone();
		for (BillcontrastVO vo : vos) {
			vo.setPk_group(pk_group);
			vo.setPk_org(pk_org);
			vo.setPk_billcontrast(null);
		}
		BatchOperateVO batchVO = new BatchOperateVO();
		batchVO.setAddObjs(vos);
		getManage().batchSave(batchVO);
		getDataManager().initModel();
	}

	// 如果有数据的话，预置数据不可以
	protected boolean isActionEnable() {
		int row = getBilTable().getBillCardPanel().getRowCount();
		if (row != 0) {
			return false;
		} else {
			return true;
		}
	}

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillContrastBatchBillTable getBilTable() {
		return bilTable;
	}

	public void setBilTable(BillContrastBatchBillTable bilTable) {
		this.bilTable = bilTable;

	}

	public BillcontrastModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(BillcontrastModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public IErmBillcontrastQuery getSevice() {
		return NCLocator.getInstance().lookup(IErmBillcontrastQuery.class);
	}

	public IErmBillcontrastManage getManage() {
		return NCLocator.getInstance().lookup(IErmBillcontrastManage.class);
	}
}