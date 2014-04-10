package nc.ui.erm.billpub.view.eventhandler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.jcom.lang.StringUtil;

/**
 * ����������
 */
public class AddFromMtAppEditorUtil {
	
	private ErmBillBillForm billform;
	private static final String HEAD = "head"; // ���ݱ�ͷ
	private static final String BUS_BODY = "bus_body"; // ����ҵ�����
	
	public AddFromMtAppEditorUtil(ErmBillBillForm billform) {
		this.billform = billform;
	}

	/**
	 * ����ʱ��������������ؽ������
	 * @throws BusinessException
	 */
	public  void resetBillItemOnAdd() throws BusinessException{
		
		JKBXVO jkbxvo = (JKBXVO) ((ErmBillBillForm) getEditor()).getResVO().getBusiobj();
		// ������ά���Ƿ���ģ������ʾ
		List<String> ctrlFieldList = ((ErmBillBillForm) getEditor()).getResVO().getMtCtrlBusiFieldMap().get(jkbxvo.getParentVO().getPk_item());
		boolean ismashare = jkbxvo.getParentVO().getIsmashare() != null && jkbxvo.getParentVO().getIsmashare().booleanValue(); // ���뵥�Ƿ��̯
		// ����У�����ά�ȵ���ʾ
		if (isBX()) {
			checkCtrlField(ctrlFieldList, ismashare);
		}
		// ���ñ�ͷ�ֶβ��ɱ༭
		setHeadFieldNotEdit();
		
		if (jkbxvo.getChildrenVO() != null && jkbxvo.getChildrenVO().length > 0) {
			// ������ŵ�ҵ��ҳǩ
			int index = getEditor().getBillCardPanel().getBillData().getBodyTableCodeIndex(getBusPageCode());
			getEditor().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(index);
			// ����������������ֶ�
			modifyOtherJeOfBody();
			
			//���ڱ�����������ʱ����ͷ���տ�����������
			if(BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
				Integer paytarget = (Integer) getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PAYTARGET).getValueObject();
				for(int i = 0 ; i< jkbxvo.getChildrenVO().length ;i++){
					getEditor().getBillCardPanel().setBodyValueAt(paytarget, i, JKBXHeaderVO.PAYTARGET);
				}
			}
		}
	}
	
	/**
	 * �޸�ʱ������������������ֶβ��ɱ༭
	 * @throws BusinessException
	 */
	public void resetBillItemOnEdit() throws BusinessException{
		setHeadFieldNotEdit();
	}
	


	/**
	 * ���ñ�ͷ�ֶβ��ɱ༭
	 */
	private void setHeadFieldNotEdit() {
		((ErmBillBillForm)getEditor()).getBillOrgPanel().getRefPane().setEnabled(false); //������ϵĲ�����֯���ɱ༭
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).setEnabled(false);
		// ���뵥��̯������������������󣬲�����ȡ����̯����̯��־λ�û�
		BillItem ismashare = getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE);
		if(ismashare != null && (Boolean)ismashare.getValueObject()){
			BillItem iscostshareItem = getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.ISCOSTSHARE);
			if(iscostshareItem != null){
				iscostshareItem.setEnabled(false);
			}
		}
	}
	
	private void modifyOtherJeOfBody() {
		JKBXVO aggvo = (JKBXVO)  billform.getResVO().getBusiobj();
		BXBusItemVO[] busitemVOs = aggvo.getBxBusItemVOS();
		
		String[] bodyTableCodes = getEditor().getBillCardPanel().getBillData().getBodyTableCodes();
		if(bodyTableCodes != null){
			getEditor().getBillCardPanel().getBillModel(bodyTableCodes[0]).setNeedCalculate(false);//�ϼƽ����ʱ�ر�
		}
		
		for (int i = 0; i < busitemVOs.length; i++) {
			Object amount =getEditor().getBillCardPanel().getBodyValueAt(i, BXBusItemVO.AMOUNT);
			getEditor().getBillCardPanel().setBodyValueAt(amount, i, BXBusItemVO.YBJE);
			new BodyEventHandleUtil(((ErmBillBillForm)getEditor())).modifyFinValues(BXBusItemVO.YBJE, i,null);
		}
		
		if(bodyTableCodes != null){
			getEditor().getBillCardPanel().getBillModel(bodyTableCodes[0]).setNeedCalculate(true);//�ϼƽ������
		}
		
		// ��ͷ���ϼƣ��������ͷ�������ֵ
		((ErmBillBillForm)getEditor()).getbodyEventHandle().finBodyYbjeEdit();
	}
	
	/**
	 * ������ά���Ƿ���ʾ
	 * 
	 * @param item
	 * @throws BusinessException
	 */
	private void checkCtrlField(List<String> ctrlFieldList, boolean ismashare) throws BusinessException {
		if(ctrlFieldList == null || ctrlFieldList.size() < 0){
			return;
		}
		Map<String,List<String>> map = groupCtrlFieldCodeMap(ctrlFieldList);
		
		// У���ͷ����ά���ֶ��Ƿ���ʾ
		checkHeadCtrlFieldIsShow(map.get(HEAD));
		
		if(ismashare){
			// ��̯ʱ��У���̯ҳǩ�Ŀ���ά���ֶ��Ƿ���ʾ
			checkCSCtrlFieldIsShow(map.get(BXConstans.COSTSHAREDETAIL));
		}else{
			checkBusCtrlFieldIsShow(map.get(BUS_BODY));
		}
	}
	
	private void checkBusCtrlFieldIsShow(List<String> ctrlfields) throws BusinessException {
		if (ctrlfields == null || ctrlfields.size() < 0) {
			return;
		}
		boolean flag = false;
		for(String ctrlfield : ctrlfields){
				String[] keys = StringUtil.split(ctrlfield, ".");
				BillItem item = getEditor().getBillCardPanel().getBodyItem(getBusPageCode(),keys[1]);
				if(item == null || !item.isShow()){
					flag = true;
					break;
				}
		}
		if (flag) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0155")/* @res "���ֿ���ά��û����ʾ�����޸ĵ���ģ�壡" */);
		}
	}

	private void checkCSCtrlFieldIsShow(List<String> ctrlfields) throws BusinessException {
		if (ctrlfields == null || ctrlfields.size() < 0) {
			return;
		}
		boolean flag = false;
		for(String ctrlfield : ctrlfields){
				String[] keys = StringUtil.split(ctrlfield, ".");
				BillItem item = getEditor().getBillCardPanel().getBodyItem(BXConstans.CSHARE_PAGE,keys[1]);
				if(item == null || !item.isShow()){
					flag = true;
					break;
				}
		}
		if (flag) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0155")/* @res "���ֿ���ά��û����ʾ�����޸ĵ���ģ�壡" */);
		}
	}

	/**
	 * ����ͷ����ά���ֶ��Ƿ���ʾ
	 * @param ctrlfields
	 * @throws BusinessException 
	 */
	private void checkHeadCtrlFieldIsShow(List<String> ctrlfields) throws BusinessException{
		if (ctrlfields == null || ctrlfields.size() < 0) {
			return;
		}
		boolean flag = false;// �Ƿ���ڿ����ֶ�δ��ʾ
		for (String ctrlfield : ctrlfields) {
				// ��ͷ�ֶ�ע���汾
				String fieldcode_v = null;
				if (JKBXHeaderVO.getOrgMultiVersionFieldMap().containsKey(ctrlfield)) {
					fieldcode_v = JKBXHeaderVO.getOrgVFieldByField(ctrlfield);
				}
				BillItem item = getEditor().getBillCardPanel().getHeadItem(ctrlfield);
				BillItem item_v = getEditor().getBillCardPanel().getHeadItem(fieldcode_v);
				if ((item == null || !item.isShow()) && (item_v == null || !item_v.isShow())) {
					flag = true;
					break;
				}
		}
		
		if (flag) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0155")/* @res "���ֿ���ά��û����ʾ�����޸ĵ���ģ�壡" */);
		}
	}
	
	
	/**
	 * ��Ҫ���Ƶ��ֶΰ��ձ�ͷ��ҵ����壬��̯�������
	 * @param fieldcodes
	 * @return
	 */
	private Map<String,List<String>> groupCtrlFieldCodeMap(List<String> fieldcodes){
		Map<String,List<String>> map =  new HashMap<String, List<String>>();
		List<String> cShareCodes = new ArrayList<String>();
		List<String> busCodes = new ArrayList<String>();
		List<String> headCodes = new ArrayList<String>();	
		for (String fieldcode : fieldcodes) {
			if (fieldcode.indexOf((int) '.') != -1) {
				String[] keys = StringUtil.split(fieldcode, ".");
				if (BXConstans.COSTSHAREDETAIL.equals(keys[0])) {
					cShareCodes.add(fieldcode);
				}else{
					busCodes.add(fieldcode);
				}
			}else{
				headCodes.add(fieldcode);
			}
		}
		map.put(HEAD, headCodes);
		map.put(BUS_BODY, busCodes);
		map.put(BXConstans.COSTSHAREDETAIL, cShareCodes);
		return map;
	}
	
	
	/**
	 * �õ���ǰ���ݵ�ҵ��ҳǩ
	 * @return
	 */
	private String getBusPageCode()
	{
		if(isBX()){
			return BXConstans.BUS_PAGE;
		}
		return BXConstans.BUS_PAGE_JK;
	}
	
	private boolean isBX(){
		String currentBillTypeCode = ((ErmBillBillManageModel)getEditor().getModel()).getCurrentBillTypeCode();
		DjLXVO currentDjlx = ((ErmBillBillManageModel)getEditor().getModel()).getCurrentDjlx(currentBillTypeCode);
		return BXConstans.BX_DJDL.equals(currentDjlx.getDjdl());
	}

	private BillForm getEditor() {
		return billform;
	}
	
}
