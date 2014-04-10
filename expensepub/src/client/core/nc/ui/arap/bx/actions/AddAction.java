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
 * @modified liansg �� ���������� Action��
 * 
 *           nc.ui.arap.bx.actions.AddAction
 */
public class AddAction extends BXDefaultAction {
	
	/**
	 * @author chendya ����ʱ��������б����л�����Ƭ
	 */
	public void chg2Card() throws BusinessException {
		CardAction action = new CardAction();
		action.setActionRunntimeV0(this.getActionRunntimeV0());
		action.changeTab(BillWorkPageConst.CARDPAGE, true, false, null);
		getMainPanel().refreshBtnStatus();
	}

	/**
	 * ���������ť֮ǰ�Ĳ���
	 * 
	 * @throws BusinessException
	 */
	private void beforeAdd() throws BusinessException {
		// ������б����,����ʱ�л�����Ƭ
		if (!isCard()) {
			chg2Card();
		}
		// ���ý���
		getBillCardPanel().transferFocusTo(0);
		
		// ��������״̬
		getMainPanel().setCurrentPageStatus(BillWorkPageConst.WORKSTAT_NEW);

		getBillCardPanel().getBillData().clearViewData();
	}
	
	public boolean add() throws Exception {
		
		getMainPanel().getBillCardPanel().setBodyAutoAddLine(true);
				
		// ����֮ǰ�Ĵ���
		beforeAdd();

		// ����Ĭ�ϵĽ�����λ
		setDefaultOrg();

		// ȡ������λ
		final String pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();

		// ���ú���֯��ص�Ĭ��ֵ�����������Ա<2> ���õ���<3> Ĭ�ϱ����Լ����Ĭ��ֵ<4> true��ʾ���ӣ�
		setDefaultWithOrg(getVoCache().getCurrentDjdl(), getVoCache().getCurrentDjlxbm(), pk_org ,false);
		
		// ����ģ��Ĭ��ֵ
		setHeadTailItemDefaultValue(getBillCardPanel().getBillData().getHeadTailItems());

		//�������Ͳ��ɱ༭
		getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM).setEnabled(false);
		
		//������������
		getBillCardPanel().getHeadItem("djlxmc").setValue(BXUiUtil.getDjlxNameMultiLang(getVoCache().getCurrentDjlx().getDjlxbm()));
			
		//����������⴦��
		if (getVoCache().getCurrentDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL)) {
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, new UFDouble(0));
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, new UFDouble(0));
		}
		// ������׼����<6>
		doReimRuleAction();
		
		//�ڱ���������ʱ���Զ����һ��,�������������,����ҳǩ����������
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
		//�ڱ�����λΪ��ʱ����Ҫ�����ñ�����λ�󣬲��ܱ༭�����ֶ�
		doBxOrgField();
		
		return true;
	}
	
	private void doBxOrgField() {
		//�ڱ�����λΪ��ʱ����Ҫ�����ñ�����λ�󣬲��ܱ༭�����ֶ�
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
				
				//�������������λ��Ӧ������Щitem����Ϊ�ɱ༭
				this.getMainPanel().setPanelEditableKeyList(keyList);
			}
		}
	}

	/**
	 * ���õ���ģ��Ĭ��ֵ
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
