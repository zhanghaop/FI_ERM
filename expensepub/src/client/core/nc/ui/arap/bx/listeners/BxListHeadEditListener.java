package nc.ui.arap.bx.listeners;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.logging.Log;
import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.arap.eventagent.EventTypeConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author twei
 * 
 *         nc.ui.arap.bx.listeners.BxListHeadEditListener
 */
public class BxListHeadEditListener extends BXDefaultAction {

	public void afterEdit() {

		EventObject event = (EventObject) getMainPanel().getAttribute(EventTypeConst.TEMPLATE_EDIT_EVENT);

		if (event instanceof BillEditEvent) {

			BillEditEvent e = (BillEditEvent) event;
			String key = e.getKey();

			if (key.equals(JKBXHeaderVO.SELECTED)) {

				int row = e.getRow();
				String djoid = getBillListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.PK_JKBX).toString();
				JKBXVO vo = getVoCache().getVOByPk(djoid);
				if (vo != null) {
					if (((Boolean) e.getValue()).booleanValue())
						vo.getParentVO().setSelected(UFBoolean.TRUE);
					else
						vo.getParentVO().setSelected(UFBoolean.FALSE);
				}
				getActionRunntimeV0().updateButtonStatus();
			}
		}
	}

	public void bodyRowChange(){
		
		EventObject event = (EventObject) getMainPanel().getAttribute(EventTypeConst.TEMPLATE_EDIT_EVENT);
		BillEditEvent  e=null;
		
		if (event instanceof BillEditEvent){
			e=(BillEditEvent)event;
		}else{
			return ;
		}

		if(e.getPos()==BillItem.BODY){
			return;
		}
		
		if(getMainPanel().getBillListPanel().getBodyBillModel().getBodyItems()==null || getMainPanel().getBillListPanel().getBodyBillModel().getBodyItems().length==0){
			return ;
		}
		
		String[] bodyTableCodes = getMainPanel().getBillListPanel().getBillListData().getBodyTableCodes();
		if(bodyTableCodes!=null){
			for(String table:bodyTableCodes){
				getMainPanel().getBillListPanel().setBodyValueVO(table,new BXBusItemVO[]{});
			}
		}
		
		int row = e.getRow();
		
		if(row<0)
			return ;
		
		Object pkjkbx = getMainPanel().getBillListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.PK_JKBX);
	
		if(pkjkbx==null)
			return;
		
		JKBXVO zbvo = getMainPanel().getCache().getVOByPk(pkjkbx.toString());
		//���ݱ�ͷVO����Ϣ����ѯ����������ҳǩ����Ϣ����������ľۺ�VO
		if( (zbvo.getChildrenVO()==null || zbvo.getChildrenVO().length==0 ) && !zbvo.isChildrenFetched()){  //��ʼ������vo
			try {
				zbvo=retrieveChidren(zbvo);
			} catch (BusinessException e1) {
				throw new BusinessRuntimeException(e1.getMessage(),e1);
			}
		}
		
//		//��ϣ��map<ҳǩ���룬����VO�б�>������ҳǩ�������������ݵ���ϵ
//		Map<String,List> map=new HashMap<String, List>();
//		
//		//����ҵ��ҳǩ��Ϣ
//		BXBusItemVO[] bxBusItems=zbvo.getBxBusItemVOS();
//		if(bxBusItems!=null){
//			for (int i = 0; i < bxBusItems.length; i++) {
//				if(map.containsKey(bxBusItems[i].getTablecode())){
//					List<BXBusItemVO> list = map.get(bxBusItems[i].getTablecode());
//					list.add(bxBusItems[i]);
//					map.put(bxBusItems[i].getTablecode(),list);
//				}else{
//					List<BXBusItemVO> list=new ArrayList<BXBusItemVO>();
//					list.add(bxBusItems[i]);
//					map.put(bxBusItems[i].getTablecode(), list);
//				}
//			}
//		}
//		
//		BxcontrastVO[] contrastItems=zbvo.getContrastVO();
//		
//		//��ʼ����������Ϣ ()
//		if(contrastItems != null){
//			for (int i = 0; i < contrastItems.length; i++) {
//				if(map.containsKey(contrastItems[i].getTableName())){
//					List<BxcontrastVO> list = map.get(contrastItems[i].getTableName());
//					list.add(contrastItems[i]);
//				}else {
//					List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();
//					list.add(contrastItems[i]);
//					map.put(contrastItems[i].getTableName(), list);
//				}
//			}
//		}
		
		//Set<String> tables = map.keySet();
		
		String[] tables = getMainPanel().getBillListPanel().getBillListData().getBodyTableCodes();
		
		for (String table:tables) {
			if(getMainPanel().getBillListPanel().getBodyBillModel(table).getBodyItems()!=null){
				BillItem[] bodyItems = getMainPanel().getBillListPanel().getBodyBillModel(table).getBodyItems();
				for(BillItem item:bodyItems){
					BXUiUtil.resetDecimalDigits(item,zbvo);
				}

				//CircularlyAccessibleValueObject[] voArray = (CircularlyAccessibleValueObject[]) map.get(table).toArray(new CircularlyAccessibleValueObject[]{});
				
				CircularlyAccessibleValueObject[] voArray =  zbvo.getTableVO(table);
				
				if(voArray!=null && voArray.length!=0){
					getMainPanel().getBillListPanel().setBodyValueVO(table,voArray);
					getMainPanel().getBillListPanel().getBodyBillModel(table).loadLoadRelationItemValue();
				}
				  
				getMainPanel().getBillListPanel().getBodyBillModel(table).execLoadFormula();
				//���ɱ༭
				getMainPanel().getBillListPanel().getBodyBillModel(table).setEnabled(false);
				
			}
		}


		
	}

	/**
	 * ���б��幫ʽ �������кţ��ֶα��룬ҳǩ����
	 * 
	 * @see nc.ui.arap.bx.actions.RowAction
	 * @return void
	 */
	private void execBodyFormula(int rownum, String key, String tableCode) {
		BillItem item = getBillListPanel().getBillListData().getBodyItem(tableCode, key);
		if (item != null) {
			String[] formulas = item.getEditFormulas();
			BillModel bm = getBillListPanel().getBodyBillModel(tableCode);
			if (bm != null)
				bm.execFormulas(rownum, formulas);
		}
	}
}
