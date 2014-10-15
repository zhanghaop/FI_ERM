package nc.ui.erm.billpub.importui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.ui.erm.billpub.view.ErmBillBillForm;
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
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;

public class HKImportablePanel extends ErmImportablePanel {

	public HKImportablePanel(String title, AbstractUIAppModel appModel,
			String configPath) {
		super(title, appModel, configPath);
	}

	@Override
	protected String getBillType() {
		return BXConstans.BX_DJLXBM;
	}
	
	@Override
	public List<InputItem> getInputItems() {
		//模板切换后，重新设置模板
		if (getUiBillCardPanel() != null
				&& !getUiBillCardPanel().getBillData().getBillTempletVO()
						.getHeadVO().getPrimaryKey().equals(
								getEditor().getBillCardPanel().getBillData()
										.getBillTempletVO().getHeadVO()
										.getPrimaryKey())) {
			((ErmBillBillForm) getEditor()).setBillData(getUiBillCardPanel()
					.getBillData().getBillTempletVO());
		}
		
		List<InputItem> resultInputItemList = new ArrayList<InputItem>();
		List<InputItem> defaultInputItemList = InputItemCreator.getInputItems(getEditorBillData(), false);
		Map<String, InputItem> inputItemMap = new HashMap<String, InputItem>();
		for (InputItem item : defaultInputItemList) {
			if (item.getPos() == IBillItem.BODY) {
				if(item.getItemKey().equals("assume_org")){
					continue;
				}
				inputItemMap.put(item.getTabCode() + "_" + item.getItemKey(), item);
				
			} else {
				inputItemMap.put(item.getItemKey(), item);
			}
		}
		
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ,JKBXHeaderVO.PK_ITEM_BILLNO)));
		
		//表体冲销页签
		Set<String> bodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BxcontrastVO.YBJE,BxcontrastVO.BBJE,BxcontrastVO.CJKYBJE,BxcontrastVO.JKBXR,BxcontrastVO.SZXMID,
				BxcontrastVO.JOBID,BxcontrastVO.HKYBJE)));
		
		//处理可表头导出的字段	
		for (String headKey : headItemKeys) {
			BillItem headItem = getEditorBillCardPanel().getHeadItem(headKey);
			if (headItem != null) {
				ErmBillItemValue item = null;
				if(headKey.equals(JKBXHeaderVO.TOTAL) || headKey.equals(JKBXHeaderVO.PK_ORG_V)){
					item = new ErmBillItemValue(headItem, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
				}else{
					item = new ErmBillItemValue(headItem, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);

				}
				inputItemMap.put(headKey, item);
			}
		}
		
		for(String bodyKey : bodyItemKeys){
			BillItem bodyItem = getEditorBillCardPanel().getBodyItem(BXConstans.CONST_PAGE, bodyKey);
			if (bodyItem != null) {
				InputItem item = new ErmBillItemValue(bodyItem, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
				inputItemMap.put(BXConstans.CONST_PAGE + "_" + bodyKey, item);
			}
		}
	
		resultInputItemList.addAll(inputItemMap.values());
		processSpecialItems(inputItemMap);
		return resultInputItemList;
	}

	private void processSpecialItems(Map<String, InputItem> inputItemMap) {
		Map<InputItem, IRefValueGetter> refValueGetterMap = new HashMap<InputItem, IRefValueGetter>();
		
		if(inputItemMap.get(JKBXHeaderVO.ZY) != null){//事由特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(JKBXHeaderVO.ZY), new ReasonRefValueGetter());
		}
		((DefaultDataConvertor) getDataConvertor()).setRefValueGetterMap(refValueGetterMap);
	}

	@Override
	protected String getAddActionBeanName() {
		return "addaction";
	}

	@Override
	protected String getSaveActionBeanName() {
		return "saveaction";
	}

	@Override
	protected String getCancelActionBeanName() {
		return "cancelaction";
	}

	@Override
	protected String getBillCardEditorBeanName() {
		return "editor";
	}
	
}
