package nc.ui.erm.accruedexpense.importable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.common.ErmBillConst;
import nc.itf.trade.excelimport.ExportDataInfo;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.erm.excel.ErmBillItemValue;
import nc.ui.erm.excel.ErmImportablePanel;
import nc.ui.erm.excel.ReasonRefValueGetter;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trade.excelimport.InputItem;
import nc.ui.trade.excelimport.InputItemCreator;
import nc.ui.trade.excelimport.convertor.DefaultDataConvertor;
import nc.ui.trade.excelimport.convertor.IRefValueGetter;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.trade.excelimport.processor.IVOProcessor;

public class AccImportablePanel extends ErmImportablePanel {

	public AccImportablePanel(String title, AbstractUIAppModel appModel, String configPath) {
		super(title, appModel, configPath);
	}

	@Override
	protected String getBillType() {
		return ErmBillConst.AccruedBill_Billtype;
	}

	@Override
	public List<InputItem> getInputItems() {
		//模板切换后，重新设置模板
		if(getUiBillCardPanel()!=null && !getUiBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getPrimaryKey().
				equals(getEditor().getBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getPrimaryKey())){
			((AccMNBillForm)getEditor()).setBillData(getUiBillCardPanel().getBillData().getBillTempletVO());
		}
		
		List<InputItem> resultInputItemList = new ArrayList<InputItem>();
		List<InputItem> defaultInputItemList = InputItemCreator.getInputItems(getEditorBillData(), false);
		Map<String, InputItem> inputItemMap = new HashMap<String, InputItem>();
		for (InputItem item : defaultInputItemList) {
			if (item.getPos() == IBillItem.BODY) {
				inputItemMap.put(item.getTabCode() + "_" + item.getItemKey(), item);
			} else {
				inputItemMap.put(item.getItemKey(), item);
			}
		}
		
		Set<String> headItemKeys = ErmAccruedBillConst.excelInputHeadItems;
		Set<String> bodyItemKeys = ErmAccruedBillConst.excelInputBodyItems;

		for (String headKey : headItemKeys) {//处理可导出的字段
			BillItem headItem = getEditorBillCardPanel().getHeadItem(headKey);
			if (headItem != null) {
				ErmBillItemValue item = null;
				if(headKey.equals(AccruedVO.BILLNO) || headKey.equals(AccruedVO.PK_TRADETYPEID) || headKey.equals(AccruedVO.PK_TRADETYPE)){
					item = new ErmBillItemValue(headItem, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
				}else{
					item = new ErmBillItemValue(headItem, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
				}
				inputItemMap.put(headKey, item);
			}
		}

		for (String bodyKey : bodyItemKeys) {
			for (String tableCode : getTableCodes()) {
				BillItem bodyItem = getEditorBillCardPanel().getBodyItem(tableCode, bodyKey);
				if (bodyItem != null) {
					InputItem item = new ErmBillItemValue(bodyItem, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
					inputItemMap.put(tableCode + "_" + bodyKey, item);
				}
			}
		}
		
		resultInputItemList.addAll(inputItemMap.values());
		processSpecialItems(inputItemMap);
		return resultInputItemList;
	}
	
	private void processSpecialItems(Map<String, InputItem> inputItemMap) {
		Map<InputItem, IRefValueGetter> refValueGetterMap = new HashMap<InputItem, IRefValueGetter>();
		
//		if(inputItemMap.get(AccruedVO.PK_TRADETYPE) != null){//交易类型特殊处理，交易类型的pk设置为code
//			refValueGetterMap.put(inputItemMap.get(AccruedVO.PK_TRADETYPE), new TradeTypeRefValueGetter());
//		}
		
		if(inputItemMap.get(AccruedVO.REASON) != null){//事由特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(AccruedVO.REASON), new ReasonRefValueGetter());
		}
		
		((DefaultDataConvertor) getDataConvertor()).setRefValueGetterMap(refValueGetterMap);
	}
	
	@Override
	public ExportDataInfo getValue(List<InputItem> exportItems) {
		return super.getValue(exportItems);
	}

	protected String[] getTableCodes(){
		return new String[]{ErmAccruedBillConst.Accrued_MDCODE_DETAIL};
	}
	
	@Override
	public void setValue(Object obj) {
		super.setValue(obj);
	}

	@Override
	public void save() throws Exception {
		super.save();
		//更新界面新增数据
		((BillManageModel)this.getAppModel()).directlyAdd(getEditor().getValue());
	}
	
	@Override
	protected IVOProcessor createVOProcessor() {//VO处理器
		return new IVOProcessor(){
			@Override
			public void processVO(ExtendedAggregatedValueObject eavo) {
				if (eavo != null && eavo.getParentVO() != null) {
					CircularlyAccessibleValueObject pvo = eavo.getParentVO();
					if(pvo.getAttributeValue(AccruedVO.BILLNO) != null){
						pvo.setAttributeValue(AccruedVO.BILLNO, null);//单据编号清空
					}
				}
			}
		};
	}
}
