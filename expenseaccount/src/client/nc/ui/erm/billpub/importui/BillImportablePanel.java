package nc.ui.erm.billpub.importui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.trade.excelimport.ExportDataInfo;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.excel.BankRefValueGetter;
import nc.ui.erm.excel.ErmBillItemValue;
import nc.ui.erm.excel.ErmImportablePanel;
import nc.ui.erm.excel.ReasonRefValueGetter;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trade.excelimport.InputItem;
import nc.ui.trade.excelimport.InputItemCreator;
import nc.ui.trade.excelimport.convertor.DefaultDataConvertor;
import nc.ui.trade.excelimport.convertor.IRefValueGetter;
import nc.ui.trade.excelimport.vo.CommonAggVO2;
import nc.ui.trade.excelimport.vo.DataRowVO;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
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
	private String[] childKeys = new String[] { JKBXHeaderVO.SZXMID,
			JKBXHeaderVO.JKBXR, JKBXHeaderVO.JOBID,
			JKBXHeaderVO.CASHPROJ, JKBXHeaderVO.PROJECTTASK,
			JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_PCORG_V,
			JKBXHeaderVO.PK_CHECKELE, JKBXHeaderVO.PK_RESACOSTCENTER,
			JKBXHeaderVO.PK_PROLINE, JKBXHeaderVO.PK_BRAND,
			JKBXHeaderVO.DWBM, JKBXHeaderVO.DEPTID, JKBXHeaderVO.JKBXR,
			JKBXHeaderVO.PAYTARGET, JKBXHeaderVO.RECEIVER,
			JKBXHeaderVO.SKYHZH, JKBXHeaderVO.HBBM,
			JKBXHeaderVO.CUSTOMER, JKBXHeaderVO.CUSTACCOUNT,
			JKBXHeaderVO.FREECUST, JKBXHeaderVO.PAYTARGET };
	
	
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
	 * 
	 * @param inputItemMap
	 * @return
	 */
	private Map<String, InputItem> getbxInputitems(Map<String, InputItem> inputItemMap) {
		Set<String> headItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(JKBXHeaderVO.TOTAL, JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.DJBH, JKBXHeaderVO.DJLXBM, JKBXHeaderVO.DJZT,
				JKBXHeaderVO.SPZT, JKBXHeaderVO.SXBZ, JKBXHeaderVO.ISCOSTSHARE, JKBXHeaderVO.ISEXPAMT)));
		// 表体业务页签
		Set<String> bodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(BXBusItemVO.AMOUNT)));

		// 表体分摊页签
		Set<String> csBodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(CShareDetailVO.ASSUME_ORG, CShareDetailVO.ASSUME_DEPT, CShareDetailVO.ASSUME_AMOUNT)));

		// 表体冲销页签
		Set<String> contrastBodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(BxcontrastVO.YBJE, BxcontrastVO.BBJE, BxcontrastVO.CJKYBJE, BxcontrastVO.JKBXR,
				BxcontrastVO.SZXMID, BxcontrastVO.JOBID, BxcontrastVO.HKYBJE)));

		dealBillHeadItem(inputItemMap, headItemKeys);
		dealBillConstPageItem(inputItemMap, contrastBodyItemKeys);

		// 多个业务页签
		BillTabVO[] billTabVOs = getEditorBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		for (BillTabVO billTabVO : billTabVOs) {
			String metadatapath = billTabVO.getMetadatapath();
			if (metadatapath != null && BXConstans.ER_BUSITEM.equals(metadatapath)) {
				for (String bodyKey : bodyItemKeys) {
					BillItem bodyItem = getEditorBillCardPanel().getBodyItem(billTabVO.getTabcode(), bodyKey);
					if (bodyItem != null) {
						InputItem item = new ErmBillItemValue(bodyItem, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
						inputItemMap.put(billTabVO.getTabcode() + "_" + bodyKey, item);
					}
				}
			}
		}

		for (String bodyKey : csBodyItemKeys) {
			BillItem bodyItem = getEditorBillCardPanel().getBodyItem(BXConstans.CSHARE_PAGE, bodyKey);
			if (bodyItem != null) {
				String mapKey = BXConstans.CSHARE_PAGE + "_" + bodyKey;
				if (inputItemMap.get(mapKey) == null) {
					InputItem item = new ErmBillItemValue(bodyItem, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
					inputItemMap.put(mapKey, item);
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
		Map<String, InputItem> inputItemMap = new LinkedHashMap<String, InputItem>();
		for (InputItem item : defaultInputItemList) {
			if (item.getPos() == IBillItem.BODY) {
				BillItem billItem = getEditorBillData().getBodyItem(item.getTabCode(), item.getItemKey());
				inputItemMap.put(item.getTabCode() + "_" + item.getItemKey(), new ErmBillItemValue(billItem));
			} else {
				BillItem billItem = getEditorBillData().getHeadTailItem(item.getItemKey());
				inputItemMap.put(item.getItemKey(), new ErmBillItemValue(billItem));
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
		
		if(inputItemMap.get(JKBXHeaderVO.FKYHZH) != null){//事由特殊处理，交易类型的pk设置为code
			refValueGetterMap.put(inputItemMap.get(JKBXHeaderVO.FKYHZH), new BankRefValueGetter());
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
		//给界面设置默认值
		setDefaultVlueToEditor(obj);
	}

	private void setDefaultVlueToEditor(Object obj) {
		if(obj instanceof CommonAggVO2){
			CommonAggVO2 excelVo = (CommonAggVO2)obj;
			
			String[] tableCodes = excelVo.getTableCodes();
			
			if(tableCodes == null){
				return;
			}
			
			//excel中存在的字段，不进行默认值设置
			Map<String ,List<String>> tableItemsMap = new HashMap<String ,List<String>>();
			for(String tableCode : tableCodes){
				CircularlyAccessibleValueObject[] children = excelVo.getTableVO(tableCode);
				if(children != null && children.length > 0){
					List<String> fieldNameList = new ArrayList<String>();
					
					DataRowVO dataRow = (DataRowVO)children[0];
					String[] fieldNames = dataRow.getAttributeNames();
					fieldNameList.addAll(Arrays.asList(fieldNames));
					tableItemsMap.put(tableCode, fieldNameList);
				}
			}
			
			JKBXVO value = (JKBXVO)getEditor().getValue();
			if(value != null){
				JKBXHeaderVO parentVo = value.getParentVO();
				BXBusItemVO[] bxBusitems = value.getChildrenVO();
				CShareDetailVO[] csDetails = value.getcShareDetailVo();
				
				if(bxBusitems != null){
					for(BXBusItemVO item : bxBusitems){
						for(String key : childKeys){//和新增一样代默认值
							if(tableItemsMap.get(item.getTablecode()) != null && !tableItemsMap.get(item.getTablecode()).contains(key)){
								if(parentVo.getAttributeValue(key) != null){
									if(item.getAttributeValue(key) == null){
										item.setAttributeValue(key, parentVo.getAttributeValue(key));
									}
								}
							}
						}
					}
				}
				
				if(csDetails != null){
					String pk_group = parentVo.getPk_group();
					String djlxbm = parentVo.getDjlxbm();
					
					//是否调整单
					boolean isAdjust = false;
					try {
						isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(pk_group, djlxbm, ErmDjlxConst.BXTYPE_ADJUST);
					} catch (BusinessException e1) {
						ExceptionHandler.consume(e1);
					}
					
					
					// 将数据从表头联动到表体,
					Map<String, String> map = getCsPageOppositeFieldByHead();
					List<String> attributeLst = tableItemsMap.get(BXConstans.CSHARE_PAGE);
					if (attributeLst == null) {
						attributeLst = tableItemsMap.get("costsharedetail");
					}
					
					for (CShareDetailVO csDetail : csDetails) {
						csDetail.setPk_group(pk_group);
						if (isAdjust) {
							csDetail.setYsdate(parentVo.getDjrq());
						}
						for (Map.Entry<String, String> entry : map.entrySet()) {
							if (attributeLst != null && !attributeLst.contains(entry.getValue())) {
								if (parentVo.getAttributeValue(entry.getKey()) != null) {
									if (csDetail.getAttributeValue(entry.getValue()) == null) {
										csDetail.setAttributeValue(entry.getValue(), parentVo.getAttributeValue(entry.getKey()));
									}
								}
							}
						}
					}
				}
				
				getEditor().setValue(value);
				
				//单据表体行状态设置
				BillCardPanel billCard = getEditor().getBillCardPanel();
				String[] bodyTableCodes = billCard.getBillData().getBodyTableCodes();
				for (String tableCode : bodyTableCodes) {
					BillModel billModel = billCard.getBillModel(tableCode);
					int rowCount = billModel.getRowCount();
					if(rowCount <= 0){
						continue;
					}
					
					int rowState = BillModel.ADD;
					for (int i = 0; i < rowCount; i++) {
						if (billModel.getRowState(i) != BillModel.UNSTATE) {
							billModel.setRowState(i, rowState);
						}
					}
				}
			}
		}
	}
	
	/**
	 * add CW 当模板上存在编辑公式时，可能会对结果有影响，所以在导入时，建议不再执行模板上的公式，按具体的excel中的值导入即可
	 */
	@Override
	protected void setProcessedVO(ExtendedAggregatedValueObject eavo) {
		if (getBillcardPanelEditor() != null) {
			((ErmBillBillForm) getEditor()).setEavo(eavo);
			BillData bd = getBillcardPanelEditor().getBillCardPanel().getBillData();
			bd.setImportBillValueVO(eavo, false, false);
			((ErmBillBillForm) getEditor()).setEavo(null);
		}
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
			public void processVO(ExtendedAggregatedValueObject importdata) {
				String pkBillTypeCode = getBillTypeCode();
				if (importdata != null && importdata.getParentVO() != null) {
					CircularlyAccessibleValueObject parentVo = importdata.getParentVO();
					if (parentVo.getAttributeValue(JKBXHeaderVO.DJBH) != null) {
						parentVo.setAttributeValue(JKBXHeaderVO.DJBH, null);// 单据编号清空
					}
					
					if (pkBillTypeCode != null && pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)
							&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(pkBillTypeCode)) {
						if (parentVo.getAttributeValue(JKBXHeaderVO.PAYTARGET) != null) {
							if (parentVo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("员工")) {
								parentVo.setAttributeValue(JKBXHeaderVO.PAYTARGET, BXStatusConst.PAY_TARGET_RECEIVER);
							} else if (parentVo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("供应商")) {
								parentVo.setAttributeValue(JKBXHeaderVO.PAYTARGET, BXStatusConst.PAY_TARGET_HBBM);
							} else if (parentVo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("客户")) {
								parentVo.setAttributeValue(JKBXHeaderVO.PAYTARGET, BXStatusConst.PAY_TARGET_CUSTOMER);
							} else {
								parentVo.setAttributeValue(JKBXHeaderVO.PAYTARGET, BXStatusConst.PAY_TARGET_OTHER);
							}
						}
					}
					
					if(parentVo.getAttributeValue(JKBXHeaderVO.RECEIVER) != null){//创维专项-收款人导入，按人员ID导入
						try {
							String wherePart = " code = '" + parentVo.getAttributeValue(JKBXHeaderVO.RECEIVER) + "'";
							PsndocVO[] persons = (PsndocVO[])CacheUtil.getValueFromCacheByWherePart(PsndocVO.class, wherePart);
							if(persons != null){
								BillItem receiverItem = getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
								
								if(receiverItem != null){
									UIRefPane refPane = (UIRefPane)receiverItem.getComponent();
									refPane.setPk_org(persons[0].getPk_org());
								}
							}
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
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
	
	private Map<String, String> getCsPageOppositeFieldByHead() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.SZXMID, CShareDetailVO.PK_IOBSCLASS);
		map.put(JKBXHeaderVO.PK_PCORG, CShareDetailVO.PK_PCORG);
		map.put(JKBXHeaderVO.PK_RESACOSTCENTER, CShareDetailVO.PK_RESACOSTCENTER);
		map.put(JKBXHeaderVO.JOBID, CShareDetailVO.JOBID);
		map.put(JKBXHeaderVO.PROJECTTASK, CShareDetailVO.PROJECTTASK);
		map.put(JKBXHeaderVO.PK_CHECKELE, CShareDetailVO.PK_CHECKELE);
		map.put(JKBXHeaderVO.CUSTOMER, CShareDetailVO.CUSTOMER);
		map.put(JKBXHeaderVO.HBBM, CShareDetailVO.HBBM);
		map.put(JKBXHeaderVO.FYDEPTID, CShareDetailVO.ASSUME_DEPT);
		map.put(JKBXHeaderVO.FYDWBM, CShareDetailVO.ASSUME_ORG);
		map.put(JKBXHeaderVO.PK_PROLINE, CShareDetailVO.PK_PROLINE);
		map.put(JKBXHeaderVO.PK_BRAND, CShareDetailVO.PK_BRAND);
		return map;
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
