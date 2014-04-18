package nc.ui.erm.mactrlschema.view;

import java.util.List;

import javax.swing.Action;

import nc.bs.erm.util.ErmBillTypeUtil;
import nc.bs.pf.pub.PfDataCache;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.ep.bx.BilltypeRuleVO;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.billtype.BilltypeVO;

public class MaCtrlBillTable extends BatchBillTable implements IComponentWithActions, ValueChangedListener {

	private static final long serialVersionUID = 1L;

	private List<Action> actions = null;

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	@Override
	public void initUI() {
		super.initUI();
		UIRefPane ctrlBillRef = (UIRefPane) billCardPanel.getBodyItem(MtappCtrlbillVO.PK_SRC_TRADETYPE).getComponent();
		BilltypeRuleVO rulevo = ErmBillTypeUtil.getBilltypeRuleVO(BilltypeRuleVO.MACTRLBILL);
		String insql = ErmBillTypeUtil.getBilltypeRuleWhereSql(rulevo,false);
		String wherePart = insql +" and istransaction = 'Y' and islock ='N' and  ( pk_group='"
				+ getModel().getContext().getPk_group() + "')";
		ctrlBillRef.getRefModel().setWherePart(wherePart);
		ctrlBillRef.addValueChangedListener(this);
		ctrlBillRef.setMultiSelectedEnabled(true);// 设置控制对象参照可以多选
	}


	
	@Override
	public void valueChanged(ValueChangedEvent e) {
		BillItem item = billCardPanel.getBodyItem(MtappCtrlbillVO.PK_SRC_TRADETYPE);
		if (item.getValueObject() != null) {
			Object[] billtypes = ((UIRefPane) item.getComponent()).getRefModel().getValues("pk_billtypecode");
				for (int i = 0; i < billtypes.length; i++) {
					String billtype = billtypes[i].toString();
					BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(billtype);
					if(billtypevo != null){
						String src_billtype = billtypevo.getNcbrcode();
						String src_system = billtypevo.getSystemcode();
						String pk_billtypeid = billtypevo.getPk_billtypeid();
						
						if (i == 0) {
							billCardPanel.setBodyValueAt(pk_billtypeid, getModel().getSelectedIndex(), MtappCtrlbillVO.PK_SRC_TRADETYPE);
							billCardPanel.setBodyValueAt(billtype, getModel().getSelectedIndex(), MtappCtrlbillVO.SRC_TRADETYPE);
							billCardPanel.setBodyValueAt(src_billtype, getModel().getSelectedIndex(), MtappCtrlbillVO.SRC_BILLTYPE);
							billCardPanel.setBodyValueAt(src_system, getModel().getSelectedIndex(), MtappCtrlbillVO.SRC_SYSTEM);
						}else{
							//先克隆选中行数据，再更改，最后增行
							MtappCtrlbillVO selectVO = (MtappCtrlbillVO)getModel().getSelectedData();
							MtappCtrlbillVO newobj = (MtappCtrlbillVO) selectVO.clone();
							newobj.setSrc_tradetype(billtype);
							newobj.setPk_src_tradetype(pk_billtypeid);
							newobj.setSrc_billtype(src_billtype);
							newobj.setSrc_system(src_system);
							newobj.setStatus(VOStatus.NEW);
							newobj.setPk_mtapp_cbill(null);
							getModel().addLines(new Object[]{newobj});// 增行
						}
					}
				}
			billCardPanel.getBillModel().loadLoadRelationItemValue();
		}else {
			billCardPanel.setBodyValueAt(null, getModel().getSelectedIndex(), MtappCtrlbillVO.PK_SRC_TRADETYPE);
			billCardPanel.setBodyValueAt(null, getModel().getSelectedIndex(), MtappCtrlbillVO.SRC_TRADETYPE);
			billCardPanel.setBodyValueAt(null, getModel().getSelectedIndex(), MtappCtrlbillVO.SRC_BILLTYPE);
			billCardPanel.setBodyValueAt(null, getModel().getSelectedIndex(), MtappCtrlbillVO.SRC_SYSTEM);
		}
	}

}