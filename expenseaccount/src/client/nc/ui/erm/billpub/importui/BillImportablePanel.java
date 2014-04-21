package nc.ui.erm.billpub.importui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.trade.excelimport.ExportDataInfo;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.excel.ErmBillItemValue;
import nc.ui.erm.excel.ErmImportablePanel;
import nc.ui.erm.excel.ReasonRefValueGetter;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trade.excelimport.InputItem;
import nc.ui.trade.excelimport.InputItemCreator;
import nc.ui.trade.excelimport.convertor.DefaultDataConvertor;
import nc.ui.trade.excelimport.convertor.IRefValueGetter;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.trade.excelimport.processor.IVOProcessor;
/**
 * 借款报销单导入导出
 * @author wangled
 *
 */
public class BillImportablePanel extends ErmImportablePanel {
	public BillImportablePanel(String title, AbstractUIAppModel appModel,
			String configPath) {
		super(title, appModel, configPath);
	}
	
	/**
	 * 导出字段的处理：区分借款单、还款单、报销单
	 */
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
		String pkBillTypeCode = getBillTypeCode();
		
		((ErmBillBillManageModel)((ErmBillBillForm)getEditor()).getModel()).setCurrentBillTypeCode(pkBillTypeCode);
		((ErmBillBillManageModel)((ErmBillBillForm)getEditor()).getModel()).setSelectBillTypeCode(pkBillTypeCode);
		
		List<InputItem> resultInputItemList = new ArrayList<InputItem>();
		Map<String, InputItem> inputItemMap = getDefaultInputItems();
		
		if(pkBillTypeCode!=null && pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)
				&& BXConstans.BILLTYPECODE_RETURNBILL.equals(pkBillTypeCode)){
			inputItemMap = gethkInputItems(inputItemMap);
		}else if(pkBillTypeCode!=null && pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)
			    && !BXConstans.BILLTYPECODE_RETURNBILL.equals(pkBillTypeCode)){
			inputItemMap =getbxInputitems(inputItemMap);
		}else if(pkBillTypeCode!=null && !pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)){
			getjkInputitems(inputItemMap);
		}
		
		resultInputItemList.addAll(inputItemMap.values());
		processSpecialItems(inputItemMap);
		return resultInputItemList;
	}

	/**
	 * 借款单的处理
	 * @param inputItemMap
	 */
	private void getjkInputitems(Map<String, InputItem> inputItemMap) {
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ,JKBXHeaderVO.PAYFLAG,JKBXHeaderVO.PK_ITEM_BILLNO)));
		
		
		//表体冲销页签
		Set<String> bodyItemKeys3 = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BxcontrastVO.YBJE,BxcontrastVO.BBJE,BxcontrastVO.CJKYBJE,BxcontrastVO.JKBXR,BxcontrastVO.SZXMID,
				BxcontrastVO.JOBID,BxcontrastVO.HKYBJE)));
		
		dealBillHeadItem(inputItemMap, headItemKeys);
		
		dealBillConstPageItem(inputItemMap, bodyItemKeys3);
	}
	/**
	 * 报销单的处理
	 * @param inputItemMap
	 * @return
	 */
	private Map<String, InputItem> getbxInputitems(Map<String, InputItem> inputItemMap) {
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ,JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT)));
		//表体分摊页签
		Set<String> bodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				CShareDetailVO.ASSUME_ORG,CShareDetailVO.ASSUME_AMOUNT,CShareDetailVO.ASSUME_DEPT,CShareDetailVO.PK_GROUP)));
		//表体业务页签
		Set<String> bodyItemKeys2 = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BXBusItemVO.RECEIVER,BXBusItemVO.PAYTARGET,BXBusItemVO.HBBM,BXBusItemVO.CUSTOMER,BXBusItemVO.AMOUNT)));
		
		//表体冲销页签
		Set<String> bodyItemKeys3 = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BxcontrastVO.YBJE,BxcontrastVO.BBJE,BxcontrastVO.CJKYBJE,BxcontrastVO.JKBXR,BxcontrastVO.SZXMID,
				BxcontrastVO.JOBID,BxcontrastVO.HKYBJE)));
		
		dealBillHeadItem(inputItemMap, headItemKeys);
		
		dealBillConstPageItem(inputItemMap, bodyItemKeys3);

		
		for(String bodyKey : bodyItemKeys){
			BillItem bodyItem = getEditorBillCardPanel().getBodyItem(BXConstans.CSHARE_PAGE, bodyKey);
			if (bodyItem != null) {
				InputItem item = new ErmBillItemValue(bodyItem, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
				inputItemMap.put(BXConstans.CSHARE_PAGE + "_" + bodyKey, item);
			}
		}
		
		//多个业务页签
		BillTabVO[] billTabVOs = getEditorBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		for (BillTabVO billTabVO : billTabVOs) {
			 String metadatapath = billTabVO.getMetadatapath();
			 if(metadatapath!=null && BXConstans.ER_BUSITEM.equals(metadatapath)){
				 for(String bodyKey : bodyItemKeys2){
					 BillItem bodyItem = getEditorBillCardPanel().getBodyItem(billTabVO.getTabcode(), bodyKey);
					 if (bodyItem != null) {
						 InputItem item = new ErmBillItemValue(bodyItem, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
						 inputItemMap.put(billTabVO.getTabcode() + "_" + bodyKey, item);
					 }
				 }
			 }
		}
		
		return inputItemMap;
	}

	/**
	 * 还款单的处理
	 * @param inputItemMap
	 * @return
	 */
	private Map<String, InputItem> gethkInputItems(Map<String, InputItem> inputItemMap) {
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ)));
		
		//表体冲销页签
		Set<String> bodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BxcontrastVO.YBJE,BxcontrastVO.BBJE,BxcontrastVO.CJKYBJE,BxcontrastVO.JKBXR,BxcontrastVO.SZXMID,
				BxcontrastVO.JOBID,BxcontrastVO.HKYBJE)));
		
		dealBillHeadItem(inputItemMap, headItemKeys);
		
		dealBillConstPageItem(inputItemMap, bodyItemKeys);
		return inputItemMap;
	}
	
	
	/**
	 * 处理表头字段
	 * @param inputItemMap
	 * @param headItemKeys
	 */
	private void dealBillHeadItem(Map<String, InputItem> inputItemMap,
			Set<String> headItemKeys) {
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
	}

	/**
	 * 处理冲销页签字段
	 * @param inputItemMap
	 * @param bodyItemKeys
	 */
	private void dealBillConstPageItem(Map<String, InputItem> inputItemMap,
			Set<String> bodyItemKeys) {
		for(String bodyKey : bodyItemKeys){
			BillItem bodyItem = getEditorBillCardPanel().getBodyItem(BXConstans.CONST_PAGE, bodyKey);
			if (bodyItem != null) {
				InputItem item = new ErmBillItemValue(bodyItem, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
				inputItemMap.put(BXConstans.CONST_PAGE + "_" + bodyKey, item);
			}
		}
	}
	
	/**
	 * 先得到单据模板上默认的导入导出字段
	 */
	private Map<String, InputItem> getDefaultInputItems() {
		List<InputItem> defaultInputItemList = InputItemCreator.getInputItems(getEditorBillData(), false);
		Map<String, InputItem> inputItemMap = new HashMap<String, InputItem>();
		for (InputItem item : defaultInputItemList) {
			if (item.getPos() == IBillItem.BODY) {
				inputItemMap.put(item.getTabCode() + "_" + item.getItemKey(), item);
				
			} else {
				inputItemMap.put(item.getItemKey(), item);
			}
		}
		return inputItemMap;
	}
	
	/**
	 * 特殊字段的处理
	 * @param inputItemMap
	 */
	private void processSpecialItems(Map<String, InputItem> inputItemMap) {
		Map<InputItem, IRefValueGetter> refValueGetterMap = new HashMap<InputItem, IRefValueGetter>();
		
		if(inputItemMap.get(JKBXHeaderVO.ZY) != null){//事由特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(JKBXHeaderVO.ZY), new ReasonRefValueGetter());
		}
		((DefaultDataConvertor) getDataConvertor()).setRefValueGetterMap(refValueGetterMap);
	}
	
	

	private String getBillTypeCode() {
		Object selectedData = ((BillManageModel)getUiEditor().getModel()).getSelectedData();
		return selectedData!=null ? ((JKBXVO)selectedData).getParentVO().getDjlxbm() : ((ErmBillBillManageModel)getUiEditor().getModel()).getCurrentBillTypeCode();
	}
	
	@Override
	public void setValue(Object obj) {
		super.setValue(obj);
	}

	@Override
	public void save() throws Exception {
		super.save();
		//更新界面新增数据
		String pkBillTypeCode = getBillTypeCode();
		JKBXVO value = (JKBXVO)getEditor().getValue();
		if(pkBillTypeCode!=null && pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)
			    && !BXConstans.BILLTYPECODE_RETURNBILL.equals(pkBillTypeCode)){
			BillModel billModel = getEditor().getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE);
			if(billModel!=null){
				CShareDetailVO[] details = (CShareDetailVO[]) billModel.getBodyValueVOs(CShareDetailVO.class.getName());
				value.setcShareDetailVo(details);
			}else{
				value = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryVOsByPrimaryKeysForNewNode(new String[]{value.getParentVO().getPk_jkbx()},BXConstans.BX_DJDL,false,null).get(0);
			}
		}
		((BillManageModel)this.getAppModel()).directlyAdd(value);
	}
	
	@Override
	protected IVOProcessor createVOProcessor() {//VO处理器
		return new IVOProcessor(){
			public void processVO(ExtendedAggregatedValueObject eavo) {
				String pkBillTypeCode = getBillTypeCode();
				if (eavo != null && eavo.getParentVO() != null) {
					CircularlyAccessibleValueObject pvo = eavo.getParentVO();
					if (pvo.getAttributeValue(JKBXHeaderVO.DJBH) != null) {
						pvo.setAttributeValue(JKBXHeaderVO.DJBH, null);// 单据编号清空
					}
					if (pkBillTypeCode != null && pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)
							&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(pkBillTypeCode)) {
						if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET) != null) {
							if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("员工")) {
								pvo.setAttributeValue(JKBXHeaderVO.PAYTARGET, 0);
							} else if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("供应商")) {
								pvo.setAttributeValue(JKBXHeaderVO.PAYTARGET, 1);
							} else if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("客户")) {
								pvo.setAttributeValue(JKBXHeaderVO.PAYTARGET, 2);
							} else {
								pvo.setAttributeValue(JKBXHeaderVO.PAYTARGET, 3);
							}
						}
					} 
				}	
			}
		};
	}
	
	@Override
	public ExportDataInfo getValue(List<InputItem> exportItems) {
		ExportDataInfo exportData = super.getValue(exportItems);
		return exportData;
	}
	
	@Override
	protected  String getBillType() {//不需要处理
		return null;
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
