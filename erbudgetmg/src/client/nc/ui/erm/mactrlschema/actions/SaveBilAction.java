package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;

public class SaveBilAction extends BatchSaveAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		getEditor().getBillCardPanel().stopEditing();
		doNotNulValidate();// 必输项校验
//		MaCtrlSchemaChecker.checkCtrlBillOperation(getTreeModel(),getCheckCtrlBillList());
		super.doAction(e);
	}

	private List<String> getCheckCtrlBillList() {
		// 对比修改前后的控制对象，取出由于修改丢失的控制对象做校验
		BatchOperateVO vo = getModel().getCurrentSaveObject();
		List<String> beforedCtrlBillList = new ArrayList<String>();
		List<String> afterCtrlBillList = new ArrayList<String>();
		List<String> deltCtrlBillList = new ArrayList<String>();
		for (Object obj : vo.getUpdObjs()) {
			MtappCtrlbillVO beforeUpd = (MtappCtrlbillVO) getModel().getBeforeUpdateObject(obj);
			beforedCtrlBillList.add(beforeUpd.getSrc_tradetype());
			afterCtrlBillList.add(((MtappCtrlbillVO)obj).getSrc_tradetype());
		}
		for (int i = 0; i < beforedCtrlBillList.size(); i++) {
			if(!afterCtrlBillList.contains(beforedCtrlBillList.get(i))){
				deltCtrlBillList.add(beforedCtrlBillList.get(i));
			}
		}
		return deltCtrlBillList;
	}

	private void doNotNulValidate() {
		BillData data = getEditor().getBillCardPanel().getBillData();
		try {
			if (data != null)
				data.dataNotNullValidate();
		} catch (nc.vo.pub.ValidationException ex) {
			throw new BusinessExceptionAdapter(ex);
		}
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}

}
