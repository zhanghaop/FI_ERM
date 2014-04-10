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
 * 拉单工具类
 */
public class AddFromMtAppEditorUtil {
	
	private ErmBillBillForm billform;
	private static final String HEAD = "head"; // 单据表头
	private static final String BUS_BODY = "bus_body"; // 单据业务表体
	
	public AddFromMtAppEditorUtil(ErmBillBillForm billform) {
		this.billform = billform;
	}

	/**
	 * 新增时，设置拉单的相关界面控制
	 * @throws BusinessException
	 */
	public  void resetBillItemOnAdd() throws BusinessException{
		
		JKBXVO jkbxvo = (JKBXVO) ((ErmBillBillForm) getEditor()).getResVO().getBusiobj();
		// 检查控制维度是否在模板中显示
		List<String> ctrlFieldList = ((ErmBillBillForm) getEditor()).getResVO().getMtCtrlBusiFieldMap().get(jkbxvo.getParentVO().getPk_item());
		boolean ismashare = jkbxvo.getParentVO().getIsmashare() != null && jkbxvo.getParentVO().getIsmashare().booleanValue(); // 申请单是否分摊
		// 借款单不校验控制维度的显示
		if (isBX()) {
			checkCtrlField(ctrlFieldList, ismashare);
		}
		// 设置表头字段不可编辑
		setHeadFieldNotEdit();
		
		if (jkbxvo.getChildrenVO() != null && jkbxvo.getChildrenVO().length > 0) {
			// 将焦点放到业务页签
			int index = getEditor().getBillCardPanel().getBillData().getBodyTableCodeIndex(getBusPageCode());
			getEditor().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(index);
			// 联动表体其他金额字段
			modifyOtherJeOfBody();
		}
	}
	
	/**
	 * 修改时，重新设置拉单相关字段不可编辑
	 * @throws BusinessException
	 */
	public void resetBillItemOnEdit() throws BusinessException{
		setHeadFieldNotEdit();
	}
	


	/**
	 * 设置表头字段不可编辑
	 */
	private void setHeadFieldNotEdit() {
		((ErmBillBillForm)getEditor()).getBillOrgPanel().getRefPane().setEnabled(false); //设置最顶上的财务组织不可编辑
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).setEnabled(false);
		// 申请单分摊的情况，报销单拉单后，不允许取消分摊，分摊标志位置灰
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
			getEditor().getBillCardPanel().getBillModel(bodyTableCodes[0]).setNeedCalculate(false);//合计金额暂时关闭
		}
		
		for (int i = 0; i < busitemVOs.length; i++) {
			Object amount =getEditor().getBillCardPanel().getBodyValueAt(i, BXBusItemVO.AMOUNT);
			getEditor().getBillCardPanel().setBodyValueAt(amount, i, BXBusItemVO.YBJE);
			new BodyEventHandleUtil(((ErmBillBillForm)getEditor())).modifyFinValues(BXBusItemVO.YBJE, i);
		}
		
		if(bodyTableCodes != null){
			getEditor().getBillCardPanel().getBillModel(bodyTableCodes[0]).setNeedCalculate(true);//合计金额重启
		}
		
		// 表头金额合计，并计算表头其他金额值
		((ErmBillBillForm)getEditor()).getbodyEventHandle().finBodyYbjeEdit();
	}
	
	/**
	 * 检查控制维度是否显示
	 * 
	 * @param item
	 * @throws BusinessException
	 */
	private void checkCtrlField(List<String> ctrlFieldList, boolean ismashare) throws BusinessException {
		if(ctrlFieldList == null || ctrlFieldList.size() < 0){
			return;
		}
		Map<String,List<String>> map = groupCtrlFieldCodeMap(ctrlFieldList);
		
		// 校验表头控制维度字段是否显示
		checkHeadCtrlFieldIsShow(map.get(HEAD));
		
		if(ismashare){
			// 分摊时，校验分摊页签的控制维度字段是否显示
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
					"0201107-0155")/* @res "部分控制维度没有显示，请修改单据模板！" */);
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
					"0201107-0155")/* @res "部分控制维度没有显示，请修改单据模板！" */);
		}
	}

	/**
	 * 检查表头控制维度字段是否显示
	 * @param ctrlfields
	 * @throws BusinessException 
	 */
	private void checkHeadCtrlFieldIsShow(List<String> ctrlfields) throws BusinessException{
		if (ctrlfields == null || ctrlfields.size() < 0) {
			return;
		}
		boolean flag = false;// 是否存在控制字段未显示
		for (String ctrlfield : ctrlfields) {
				// 表头字段注意多版本
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
					"0201107-0155")/* @res "部分控制维度没有显示，请修改单据模板！" */);
		}
	}
	
	
	/**
	 * 将要控制的字段按照表头，业务表体，分摊表体分组
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
	 * 得到当前单据的业务页签
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
