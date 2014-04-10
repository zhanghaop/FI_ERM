package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
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
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

/**
 * 拉单工具类
 */
public class AddFromMtAppEditorUtil {
	private ErmBillBillForm billform;
	
	public AddFromMtAppEditorUtil(ErmBillBillForm billform) {
		this.billform = billform;
	}

	public  void resetBillItem() throws BusinessException{
		
		setHeadFieldNotEdit();
		// 调整显示表体第一个业务页签
//		getEditor().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(0);
		
//		String tableCode = getEditor().getBillCardPanel().getCurrentBodyTableCode();
//		if(tableCode == null){
//			return;
//		}
//		// 当第一个页签不是业务页签时，直接返回
//		BillTabVO[] billTabVOs = getEditor().getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
//		if (billTabVOs != null) {
//			for (BillTabVO billTab : billTabVOs) {
//				if (tableCode.equals(billTab.getTabcode())) {
//					String metaDataPath = billTab.getMetadatapath();
//					if (metaDataPath != null && !BXConstans.JK_BUSITEM.equals(metaDataPath)
//							&& !BXConstans.ER_BUSITEM.equals(metaDataPath)) {
//						return;
//					}
//				}
//			}
//		}
		
		// 如果控制维度是（收支项目、成本中心、利润中心、核算要素、项目、项目任务）其中的，无论控制表头或者表体，表头表体都不可编辑。
		// 设置拉单字段不可编辑
		setCtrlFieldNotEdit();
		if (((JKBXVO) getResVO().getBusiobj()).getChildrenVO() != null
				&& ((JKBXVO) getResVO().getBusiobj()).getChildrenVO().length > 0) {
			getEditor().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(0);
			modifyOtherJeOfBody();
		}
	}

	private void setCtrlFieldNotEdit() throws BusinessException {
		String buspage = getBusCode();
		Map<String, List<String>> map = getResVO().getMtCtrlBusiFieldMap();
		JKBXVO aggvo = (JKBXVO) getResVO().getBusiobj();
		BXBusItemVO[] busItemVOs = aggvo.getBxBusItemVOS();
		// 拉单过来的字段都不可编辑,并且特殊字段，表头表体都不可编辑
		List<String> specialField = getSpecialField();
		if (map.containsKey(aggvo.getParentVO().getPk_item())) {
			for (String fieldcode : map.get(aggvo.getParentVO().getPk_item())) {
				if (fieldcode.indexOf((int) '.') != -1 && busItemVOs.length > 0) {
					for (int i = 0; i < busItemVOs.length; i++) {
						String[] keys = StringUtil.split(fieldcode, ".");
						BillItem item = getEditor().getBillCardPanel().getBodyItem(buspage, keys[1]);
						// 控制维度字段检查
						checkCtrlField(item);
						if (item != null) {
							getEditor().getBillCardPanel().getBillModel(buspage).setCellEditable(i, keys[1], false);
							// 特殊：控制的表体的利润中心字段，则多版本字段也不可编辑
							if (keys[1].equals(BXBusItemVO.PK_PCORG) || keys[1].equals(BXBusItemVO.PK_PCORG_V)) {
								getEditor().getBillCardPanel().getBillModel(buspage).setCellEditable(i, BXBusItemVO.PK_PCORG,
										false);
								getEditor().getBillCardPanel().getBillModel(buspage).setCellEditable(i, BXBusItemVO.PK_PCORG_V,
										false);
							}
						}
						// 如果是特殊字段，则表头也要设置为不可编辑，多版本字段也不可编辑
						if (specialField.contains(keys[1])) {
							BillItem specialItem = getEditor().getBillCardPanel().getHeadItem(keys[1]);
							if (specialItem != null) {
								specialItem.setEnabled(false);
							}
							BillItem specialItem_v = null;
							if (JKBXHeaderVO.getOrgMultiVersionFieldMap().containsKey(keys[1])) {
								specialItem_v = getEditor().getBillCardPanel().getHeadItem(
										JKBXHeaderVO.getOrgVFieldByField(keys[1]));
							}
							if (specialItem_v != null) {
								specialItem_v.setEnabled(false);
							}
						}
					}
				} else {
					// 表头字段注意多版本
					String fieldcode_v = null;
					if (JKBXHeaderVO.getOrgMultiVersionFieldMap().containsKey(fieldcode)) {
						fieldcode_v = JKBXHeaderVO.getOrgVFieldByField(fieldcode);
					}
					BillItem item = getEditor().getBillCardPanel().getHeadItem(fieldcode);

					BillItem item_v = getEditor().getBillCardPanel().getHeadItem(fieldcode_v);
					// 控制维度字段检查
					checkCtrlField(item, item_v);

					if (item != null) {
						item.setEnabled(false);
					}
					if (item_v != null) {
						item_v.setEnabled(false);
					}
					// 如果是特殊字段，则表体也要设置为不可编辑
					if (specialField.contains(fieldcode)) {
						BillItem bodyitem = getEditor().getBillCardPanel().getBodyItem(buspage, fieldcode);
						if (bodyitem != null) {
							bodyitem.setEnabled(false);
						}
						if (fieldcode_v != null) {
							BillItem bodyitem_v = getEditor().getBillCardPanel().getBodyItem(buspage, fieldcode_v);
							if (bodyitem_v != null) {
								bodyitem_v.setEnabled(false);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 得到当前单据的业务页签
	 * @return
	 */
	private String getBusCode()
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
	
	/**
	 * 检查控制维度是否显示
	 * 
	 * @param item
	 * @throws BusinessException
	 */
	private void checkCtrlField(BillItem... items) throws BusinessException{
		boolean flag = false;// 是否存在显示内容
		for (BillItem item : items) {
			if(item != null && item.isShow()){
				flag = true;
				break;
			}
		}
		if(!flag){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0155")/*@res "部分控制维度没有显示，请修改单据模板！"*/);
		}
	}
	
	/**
	 * 拉单的一些特殊字段：
	 * 收支项目、成本中心、利润中心、项目、项目任务、核算要素
	 * @return
	 */
	public static List<String> getSpecialField() {
		List<String> specialField = new ArrayList<String>();
		specialField.add(JKBXHeaderVO.SZXMID);
		specialField.add(JKBXHeaderVO.PK_RESACOSTCENTER);
		specialField.add(JKBXHeaderVO.PK_PCORG);
		specialField.add(JKBXHeaderVO.PK_PCORG_V);
		specialField.add(JKBXHeaderVO.JOBID);
		specialField.add(JKBXHeaderVO.PROJECTTASK);
		specialField.add(JKBXHeaderVO.PK_CHECKELE);
		return specialField;
	}

	private void setHeadFieldNotEdit() {
		((ErmBillBillForm)getEditor()).getBillOrgPanel().getRefPane().setEnabled(false); //设置最顶上的财务组织不可编辑
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).setEnabled(false);
		getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).setEnabled(false);
	}
	
	private void modifyOtherJeOfBody() {
		JKBXVO aggvo = (JKBXVO) getResVO().getBusiobj();
		BXBusItemVO[] busitemVOs = aggvo.getBxBusItemVOS();
		if(busitemVOs == null || busitemVOs.length <0){
			return;
		}
		for (int i = 0; i < busitemVOs.length; i++) {
			Object amount =getEditor().getBillCardPanel().getBodyValueAt(i, BXBusItemVO.AMOUNT);
			getEditor().getBillCardPanel().setBodyValueAt(amount, i, BXBusItemVO.YBJE);
			// 改amount触发ybje事件
			((ErmBillBillForm)getEditor()).getbodyEventHandle().finBodyYbjeEdit();
			new BodyEventHandleUtil(((ErmBillBillForm)getEditor())).modifyFinValues(BXBusItemVO.YBJE, i);
		}
	}
	
	private BillForm getEditor() {
		return billform;
	}
	
	private MatterAppConvResVO getResVO() {
		return billform.getResVO();
	}
}
