package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.actions.AddLineAction;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.pub.lang.UFDouble;

public class AccAddLineAction extends AddLineAction {
	
	private static final long serialVersionUID = 1L;

	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getCardpanel().getBillCardPanel().stopEditing();
		int rownum = getCardpanel().getBillCardPanel().getRowCount();

		super.doAction(e);
		setDefaultValue(rownum);//设置默认值
		getCardpanel().getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
		
	}


	private void setDefaultValue(int rownum) {
		setBodyValue(getHeadItemStrValue(AccruedVO.OPERATOR_ORG),rownum, AccruedDetailVO.ASSUME_ORG);
		setBodyValue(getHeadItemStrValue(AccruedVO.OPERATOR_DEPT),rownum, AccruedDetailVO.ASSUME_DEPT);
		
//		setBodyValue(getHeadItemUFDoubleValue(AccruedVO.ORG_CURRINFO), rownum, AccruedDetailVO.ORG_CURRINFO);
//		setBodyValue(getHeadItemUFDoubleValue(AccruedVO.GROUP_CURRINFO), rownum, AccruedDetailVO.GROUP_CURRINFO);
//		setBodyValue(getHeadItemUFDoubleValue(AccruedVO.GLOBAL_CURRINFO), rownum, AccruedDetailVO.GLOBAL_CURRINFO);
		//增行时汇率，重新计算，不能从表头直接取
		AccUiUtil.resetBodyCurrInfo(getCardpanel().getBillCardPanel(),rownum);
		
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.ORG_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.GROUP_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.GLOBAL_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.REST_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.ORG_REST_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.GROUP_REST_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.GLOBAL_REST_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.VERIFY_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.ORG_VERIFY_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.GROUP_VERIFY_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.GLOBAL_VERIFY_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, AccruedDetailVO.PREDICT_REST_AMOUNT);
		
		AccUiUtil.setCostCenter(rownum, getCardpanel().getBillCardPanel());
		
	}
	
	private String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getCardpanel().getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
	
	@SuppressWarnings("unused")
	private UFDouble getHeadItemUFDoubleValue(String itemKey) {
		BillItem headItem = getCardpanel().getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? UFDouble.ZERO_DBL : (UFDouble) headItem.getValueObject();
	}

	private void setBodyValue(Object value , int rownum, String key) {
		getCardpanel().getBillCardPanel().setBodyValueAt(value, rownum, key);
	}
}
