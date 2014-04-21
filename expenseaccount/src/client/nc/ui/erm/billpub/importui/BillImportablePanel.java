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
 * ���������뵼��
 * @author wangled
 *
 */
public class BillImportablePanel extends ErmImportablePanel {
	public BillImportablePanel(String title, AbstractUIAppModel appModel,
			String configPath) {
		super(title, appModel, configPath);
	}
	
	/**
	 * �����ֶεĴ������ֽ��������������
	 */
	@Override
	public List<InputItem> getInputItems() {
		//ģ���л�����������ģ��
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
	 * ���Ĵ���
	 * @param inputItemMap
	 */
	private void getjkInputitems(Map<String, InputItem> inputItemMap) {
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ,JKBXHeaderVO.PAYFLAG,JKBXHeaderVO.PK_ITEM_BILLNO)));
		
		
		//�������ҳǩ
		Set<String> bodyItemKeys3 = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BxcontrastVO.YBJE,BxcontrastVO.BBJE,BxcontrastVO.CJKYBJE,BxcontrastVO.JKBXR,BxcontrastVO.SZXMID,
				BxcontrastVO.JOBID,BxcontrastVO.HKYBJE)));
		
		dealBillHeadItem(inputItemMap, headItemKeys);
		
		dealBillConstPageItem(inputItemMap, bodyItemKeys3);
	}
	/**
	 * �������Ĵ���
	 * @param inputItemMap
	 * @return
	 */
	private Map<String, InputItem> getbxInputitems(Map<String, InputItem> inputItemMap) {
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ,JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT)));
		//�����̯ҳǩ
		Set<String> bodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				CShareDetailVO.ASSUME_ORG,CShareDetailVO.ASSUME_AMOUNT,CShareDetailVO.ASSUME_DEPT,CShareDetailVO.PK_GROUP)));
		//����ҵ��ҳǩ
		Set<String> bodyItemKeys2 = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BXBusItemVO.RECEIVER,BXBusItemVO.PAYTARGET,BXBusItemVO.HBBM,BXBusItemVO.CUSTOMER,BXBusItemVO.AMOUNT)));
		
		//�������ҳǩ
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
		
		//���ҵ��ҳǩ
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
	 * ����Ĵ���
	 * @param inputItemMap
	 * @return
	 */
	private Map<String, InputItem> gethkInputItems(Map<String, InputItem> inputItemMap) {
		Set<String>  headItemKeys =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				JKBXHeaderVO.TOTAL,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.DJBH,JKBXHeaderVO.DJLXBM,JKBXHeaderVO.DJZT,JKBXHeaderVO.SPZT,
				JKBXHeaderVO.SXBZ)));
		
		//�������ҳǩ
		Set<String> bodyItemKeys = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
				BxcontrastVO.YBJE,BxcontrastVO.BBJE,BxcontrastVO.CJKYBJE,BxcontrastVO.JKBXR,BxcontrastVO.SZXMID,
				BxcontrastVO.JOBID,BxcontrastVO.HKYBJE)));
		
		dealBillHeadItem(inputItemMap, headItemKeys);
		
		dealBillConstPageItem(inputItemMap, bodyItemKeys);
		return inputItemMap;
	}
	
	
	/**
	 * �����ͷ�ֶ�
	 * @param inputItemMap
	 * @param headItemKeys
	 */
	private void dealBillHeadItem(Map<String, InputItem> inputItemMap,
			Set<String> headItemKeys) {
		//����ɱ�ͷ�������ֶ�
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
	 * �������ҳǩ�ֶ�
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
	 * �ȵõ�����ģ����Ĭ�ϵĵ��뵼���ֶ�
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
	 * �����ֶεĴ���
	 * @param inputItemMap
	 */
	private void processSpecialItems(Map<String, InputItem> inputItemMap) {
		Map<InputItem, IRefValueGetter> refValueGetterMap = new HashMap<InputItem, IRefValueGetter>();
		
		if(inputItemMap.get(JKBXHeaderVO.ZY) != null){//�������⴦���������͵�pk����Ϊcode
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
		//���½�����������
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
	protected IVOProcessor createVOProcessor() {//VO������
		return new IVOProcessor(){
			public void processVO(ExtendedAggregatedValueObject eavo) {
				String pkBillTypeCode = getBillTypeCode();
				if (eavo != null && eavo.getParentVO() != null) {
					CircularlyAccessibleValueObject pvo = eavo.getParentVO();
					if (pvo.getAttributeValue(JKBXHeaderVO.DJBH) != null) {
						pvo.setAttributeValue(JKBXHeaderVO.DJBH, null);// ���ݱ�����
					}
					if (pkBillTypeCode != null && pkBillTypeCode.startsWith(BXConstans.BX_PREFIX)
							&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(pkBillTypeCode)) {
						if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET) != null) {
							if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("Ա��")) {
								pvo.setAttributeValue(JKBXHeaderVO.PAYTARGET, 0);
							} else if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("��Ӧ��")) {
								pvo.setAttributeValue(JKBXHeaderVO.PAYTARGET, 1);
							} else if (pvo.getAttributeValue(JKBXHeaderVO.PAYTARGET).equals("�ͻ�")) {
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
	protected  String getBillType() {//����Ҫ����
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
