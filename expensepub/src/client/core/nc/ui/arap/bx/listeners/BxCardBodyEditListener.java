package nc.ui.arap.bx.listeners;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.arap.bx.actions.ContrastAction;
import nc.ui.arap.eventagent.EventTypeConst;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;

/**
 * nc.ui.arap.bx.listeners.BxCardBodyEditListener
 *
 * @author twei
 *
 */
public class BxCardBodyEditListener extends BXDefaultAction {

	public void afterEdit() throws BusinessException {
		EventObject event = (EventObject) getMainPanel().getAttribute(EventTypeConst.TEMPLATE_EDIT_EVENT);

		if (event instanceof BillEditEvent) {
			
			BillEditEvent eve=(BillEditEvent)event;
			
			BillItem bodyItem = getBxBillCardPanel().getBodyItem(eve.getTableCode(),eve.getKey());

			if(bodyItem==null)
				return;
			if(bodyItem.getKey().equals(BXBusItemVO.AMOUNT)||isAmoutField(bodyItem)){
				Object amount = getMainPanel().getBillCardPanel().getBodyValueAt(eve.getRow(), BXBusItemVO.AMOUNT);
				getMainPanel().getBillCardPanel().setBodyValueAt(amount, eve.getRow(), BXBusItemVO.YBJE);
				//��amount����ybje�¼�
				finBodyYbjeEdit();
				eve.setKey(BXBusItemVO.YBJE);
				modifyFinValues(eve.getKey(),eve.getRow());
				eve.setKey(BXBusItemVO.AMOUNT);
				getMainPanel().getBodyUIController().afterCardPanelEdit(event);
			}else if(bodyItem.getIDColName()!=null && bodyItem.getIDColName().equals(BXBusItemVO.SZXMID)){
				eve.setKey(bodyItem.getIDColName());
				getMainPanel().getBodyUIController().afterCardPanelEdit(event);
			}else if(eve.getKey().equals(BXBusItemVO.YBJE)||eve.getKey().equals(BXBusItemVO.CJKYBJE)||eve.getKey().equals(BXBusItemVO.ZFYBJE)||eve.getKey().equals(BXBusItemVO.HKYBJE)){
				if(eve.getKey().equals(BXBusItemVO.YBJE)){
					finBodyYbjeEdit();
				}
				modifyFinValues(eve.getKey(),eve.getRow());
			}
			if(getUserdefine(IBillItem.BODY, bodyItem.getKey(), 2)!=null){
				String formula=getUserdefine(IBillItem.BODY, bodyItem.getKey(), 2);
				String[] strings = formula.split(";");
				for(String form:strings){
					doFormulaAction(form,eve.getKey(),eve.getRow(),eve.getTableCode(),eve.getValue());
				}
			}
			
			//add by chenshuai , ����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
			doContract(bodyItem, eve);
			
			//��������
			doBodyReimAction();
			
		}
	}
	
	/**
	 * ����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
	 * @param bodyItem
	 * @throws ValidationException
	 * @throws BusinessException
	 */
	private void doContract(BillItem bodyItem, BillEditEvent eve) throws ValidationException, BusinessException {
		UFDouble ybje = (UFDouble)getBxBillCardPanel().getBodyValueAt(eve.getRow(), BXBusItemVO.YBJE);
		
		if(ybje != null && !ybje.equals(UFDouble.ZERO_DBL) //ԭ��Ϊ0ʱ������������
				&& BXConstans.BX_DJDL.equals(getBillValueVO().getParentVO().getDjdl())//����ʱ���г�������
				&& (bodyItem.getDataType() == IBillItem.DECIMAL //Ϊ��ֵ����ʱ���б仯
						|| bodyItem.getDataType() == IBillItem.INTEGER)){
			
			if(getBxBillCardPanel().getContrasts() == null && getBillValueVO().getContrastVO() != null){
				List<BxcontrastVO> contrastVosList = new ArrayList<BxcontrastVO>();
				for (int i = 0; i < getBillValueVO().getContrastVO().length; i++) {
					contrastVosList.add(getBillValueVO().getContrastVO()[i]);
				}
				getBxBillCardPanel().setContrasts(contrastVosList);//��Ҫ���ñ���cardpanel�� contrasts
			}
			
			if(getBxBillCardPanel().getContrasts() != null && getBxBillCardPanel().getContrasts().size() > 0){
				ContrastAction.doContrastToUI(getBxBillCardPanel(), getBillValueVO(),
						getBxBillCardPanel().getContrasts(), getBxBillCardPanel().isContrast());
			}
		}
	}

	private boolean isAmoutField(BillItem bodyItem) {
		String[] editFormulas = bodyItem.getEditFormulas();
		if(editFormulas==null){
			return false;
		}
		for(String formula:editFormulas){
			if(formula.indexOf(JKBXHeaderVO.AMOUNT)!=-1){
				return true;
			}
		}
		return false;
	}
}
