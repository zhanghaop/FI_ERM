package nc.ui.erm.matterapp.importable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.itf.trade.excelimport.ExportDataInfo;
import nc.ui.erm.excel.ErmBillItemValue;
import nc.ui.erm.excel.ErmImportablePanel;
import nc.ui.erm.excel.ReasonRefValueGetter;
import nc.ui.erm.excel.TradeTypeRefValueGetter;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trade.excelimport.InputItem;
import nc.ui.trade.excelimport.InputItemCreator;
import nc.ui.trade.excelimport.convertor.DefaultDataConvertor;
import nc.ui.trade.excelimport.convertor.IRefValueGetter;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.trade.excelimport.processor.IVOProcessor;

/**
 * 申请单excel导入导入
 * 
 * @author chenshuaia
 * 
 */
public class MaImportablePanel extends ErmImportablePanel {
		
	/**
	 * 通过构造方法来进行注入
	 * @param title
	 * @param appModel
	 * @param configPath
	 */
	public MaImportablePanel(String title, AbstractUIAppModel appModel, String configPath) {
		super(title, appModel, configPath);
	}
	
	protected String getBillType() {
		return ErmBillConst.MatterApp_BILLTYPE;
	}
	
	
	@Override
	public List<InputItem> getInputItems() {
		//模板切换后，重新设置模板
		if(getUiBillCardPanel()!=null && !getUiBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getPrimaryKey().
				equals(getEditor().getBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getPrimaryKey())){
			((MatterAppMNBillForm)getEditor()).setBillDataTemplate(getUiBillCardPanel().getBillData().getBillTempletVO());
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
		
		String[] headItemKeys = AggMatterAppVO.excelInputHeadItems;
		String[] bodyItemKeys = AggMatterAppVO.excelInputBodyItems;

		for (String headKey : headItemKeys) {//处理可导出的字段
			BillItem headItem = getEditorBillCardPanel().getHeadItem(headKey);
			if (headItem != null) {
				ErmBillItemValue item = null;
				if(headKey.equals(MatterAppVO.BILLNO) || headKey.equals(MatterAppVO.PK_TRADETYPE)){
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
		
		if(inputItemMap.get(MatterAppVO.PK_TRADETYPE) != null){//交易类型特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(MatterAppVO.PK_TRADETYPE), new TradeTypeRefValueGetter());
		}
		
		if(inputItemMap.get(MatterAppVO.REASON) != null){//事由特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(MatterAppVO.REASON), new ReasonRefValueGetter());
		}
		
		if(inputItemMap.get(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "_" + MatterAppVO.REASON) != null){//事由特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "_" + MatterAppVO.REASON), new ReasonRefValueGetter());
		}
		
		((DefaultDataConvertor) getDataConvertor()).setRefValueGetterMap(refValueGetterMap);
	}
	
	@Override
	public ExportDataInfo getValue(List<InputItem> exportItems) {
		return super.getValue(exportItems);
	}

	protected String[] getTableCodes(){
		return new String[]{ErmMatterAppConst.MatterApp_MDCODE_DETAIL};
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
					if(pvo.getAttributeValue(MatterAppVO.BILLNO) != null){
						pvo.setAttributeValue(MatterAppVO.BILLNO, null);//单据编号清空
					}
				}
			}
		};
	}
}
