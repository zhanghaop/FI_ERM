package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.matterapp.common.ErmForMatterAppUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.actions.AddLineAction;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 单据增行 Action　
 * @author chenshuaia
 */
@SuppressWarnings("serial")
public class AddRowAction extends AddLineAction {
	
	private MatterAppMNBillForm billForm;
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getBillCardPanel().stopEditing();
		int rownum = getBillCardPanel().getRowCount();
//		boolean isNeedAvg = ErmForMatterAppUtil.isNeedBalanceJe(getBillCardPanel());

		super.doAction(e);
		setDefaultValue(rownum);//设置默认值
		getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
		
//		if(isNeedAvg){
//			ErmForMatterAppUtil.reComputeBodyJeByAvg(getBillCardPanel());
//			MatterAppUiUtil.setBodyShareRatio(getBillCardPanel());
//			for (int i = 0; i < getBillCardPanel().getRowCount(); i++) {
//				billForm.resetCardBodyAmount(i);
//			}
//		}
	}
	
	private void setDefaultValue(int rownum) {
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_GROUP),rownum, MtAppDetailVO.PK_GROUP);
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_ORG),rownum, MtAppDetailVO.PK_ORG);
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_ORG),rownum, MtAppDetailVO.ASSUME_ORG);
		setBodyValue(getHeadItemStrValue(MatterAppVO.ASSUME_DEPT),rownum, MtAppDetailVO.ASSUME_DEPT);
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_CURRTYPE),rownum, MtAppDetailVO.PK_CURRTYPE);
		
		//汇率
		setBodyValue(getHeadItemUFDoubleValue(MatterAppVO.ORG_CURRINFO), rownum, MtAppDetailVO.ORG_CURRINFO);
		setBodyValue(getHeadItemUFDoubleValue(MatterAppVO.GROUP_CURRINFO), rownum, MtAppDetailVO.GROUP_CURRINFO);
		setBodyValue(getHeadItemUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO), rownum, MtAppDetailVO.GLOBAL_CURRINFO);
		
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORIG_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORG_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GROUP_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GLOBAL_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORG_EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GROUP_EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GLOBAL_EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.PRE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORG_PRE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GROUP_PRE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GLOBAL_PRE_AMOUNT);

		ErmForMatterAppUtil.setCostCenter(rownum, getBillCardPanel());
	}


	private void setBodyValue(Object value , int rownum, String key) {
		getBillCardPanel().setBodyValueAt(value, rownum, key);
	}
	
	private BillCardPanel getBillCardPanel(){
		return getCardpanel().getBillCardPanel();
	}
	
	/**
	 * 获取表头指定字段字符串Value
	 * 
	 * @param itemKey
	 * @return
	 */
	protected String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
	
	/**
	 * 获取表头指定字段字符串Value
	 * 
	 * @param itemKey
	 * @return
	 */
	protected UFDouble getHeadItemUFDoubleValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? UFDouble.ZERO_DBL : (UFDouble) headItem.getValueObject();
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}
	
	
}
