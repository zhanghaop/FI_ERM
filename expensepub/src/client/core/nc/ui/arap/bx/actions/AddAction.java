package nc.ui.arap.bx.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 * @modified liansg 　 　单据增加 Action　
 * 
 *           nc.ui.arap.bx.actions.AddAction
 */
public class AddAction extends BXDefaultAction {
	
	/**
	 * @author chendya 新增时，如果是列表，则切换到卡片
	 */
	public void chg2Card() throws BusinessException {
		CardAction action = new CardAction();
		action.setActionRunntimeV0(this.getActionRunntimeV0());
		action.changeTab(BillWorkPageConst.CARDPAGE, true, false, null);
		getMainPanel().refreshBtnStatus();
	}

	/**
	 * 点击新增按钮之前的操作
	 * 
	 * @throws BusinessException
	 */
	private void beforeAdd() throws BusinessException {
		// 如果是列表界面,新增时切换到卡片
		if (!isCard()) {
			chg2Card();
		}
		// 设置焦点
		getBillCardPanel().transferFocusTo(0);
		
		// 设置新增状态
		getMainPanel().setCurrentPageStatus(BillWorkPageConst.WORKSTAT_NEW);

		getBillCardPanel().getBillData().clearViewData();
	}
	
	public boolean add() throws Exception {
		
		getMainPanel().getBillCardPanel().setBodyAutoAddLine(true);
				
		// 新增之前的处理
		beforeAdd();

		// 设置默认的借款报销单位
		setDefaultOrg();

		// 取借款报销单位
		final String pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();

		// 设置和组织相关的默认值（包括相关人员<2> 常用单据<3> 默认币种以及相关默认值<4> true表示增加）
		setDefaultWithOrg(getVoCache().getCurrentDjdl(), getVoCache().getCurrentDjlxbm(), pk_org ,false);
		
		// 设置模板默认值
		setHeadTailItemDefaultValue(getBillCardPanel().getBillData().getHeadTailItems());

		//交易类型不可编辑
		getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM).setEnabled(false);
		
		//交易类型名称
		getBillCardPanel().getHeadItem("djlxmc").setValue(BXUiUtil.getDjlxNameMultiLang(getVoCache().getCurrentDjlx().getDjlxbm()));
			
		//新增还款单特殊处理
		if (getVoCache().getCurrentDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL)) {
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, new UFDouble(0));
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, new UFDouble(0));
		}
		// 报销标准处理<6>
		doReimRuleAction();
		
		//在表体中无行时，自动添加一行,还款单不允许增行,冲销页签不允许增行
		if(!getVoCache().getCurrentDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL) 
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE)
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE_JK) 
				&& (getMainPanel().getBillValueVO().getChildrenVO() == null || getMainPanel().getBillValueVO().getChildrenVO().length == 0)){
			for (Action action : this.getBxBillCardPanel().getBodyActions()){
				if(action instanceof AddRowAction){
					((AddRowAction) action).addRow();
				}
			}
		}
		//在报销借款单位为空时，需要先设置报销借款单位后，才能编辑其他字段
		doBxOrgField();
		
		return true;
	}
	
	private void doBxOrgField() {
		//在报销借款单位为空时，需要先设置报销借款单位后，才能编辑其他字段
		if(this.getHeadValue(JKBXHeaderVO.PK_ORG) == null || this.getHeadValue(JKBXHeaderVO.PK_ORG_V) == null){

			BillItem[] items = getBillCardPanel().getHeadItems();
			
			if(items != null && items.length > 0){
				BillItem itemTemp = null;
				List<String> keyList = new ArrayList<String>();
				
				for(int i = 0; i < items.length; i ++){
					itemTemp = items[i];
					if(itemTemp.isEnabled() && !JKBXHeaderVO.PK_ORG_V.equals(itemTemp.getKey())){
						itemTemp.setEnabled(false);
						keyList.add(itemTemp.getKey());
					}
				}
				
				//在设置完借款报销单位后，应设置这些item设置为可编辑
				this.getMainPanel().setPanelEditableKeyList(keyList);
			}
		}
	}

	/**
	 * 设置单据模版默认值
	 * @param items
	 */
	private void setHeadTailItemDefaultValue(BillItem[] items) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				Object value = item.getDefaultValueObject();
				if (value != null)
					item.setValue(value);
			}
		}
	}
}
