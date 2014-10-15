package nc.ui.erm.bx.ext.eventlistener;

import java.util.Vector;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * ��������ͷ�༭���¼���������
 * 
 * ����Ԫר�ã�����ʼ�ڼ䡢�����ڼ�
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class BxCardHeadAfterEditEventHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	
	private BillCardPanel cardPanel;
	private IExceptionHandler exceptionHandler;
	
	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		cardPanel = e.getBillCardPanel();
		
		String key = e.getKey();
		String pk_djlxbm = getHeadItemStrValue(JKBXHeaderVO.DJLXBM);
		
		if(ErmConstExt.Distributor_BX_Tradetype.equals(pk_djlxbm)){
			// �����̵渶����������ʼ�ڼ���߽����ڼ�༭������У��
			if (key.equals(JKBXHeaderVO.ZYX1)||key.equals(JKBXHeaderVO.ZYX2)){
				checkStartEndAccperiod(key);
			}
		}
		
	}
	
	/**
	 * ����Ԫ
	 * У�鿪ʼ�ڼ��Ƿ�Ϸ�,���Ϸ�����null
	 * @param evt
	 */
	
	@SuppressWarnings("rawtypes")
	private void checkStartEndAccperiod(String key){
		if(null==getHeadItemStrValue(JKBXHeaderVO.ZYX2)||null==getHeadItemStrValue(JKBXHeaderVO.ZYX1))
			return;
		
        UIRefPane startRefPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZYX1).getComponent();
        Vector startv = startRefPane.getRefModel().getSelectedData();
        
        UFDate startdate = new UFDate(((Vector)startv.get(0)).get(1).toString());
		
        UIRefPane endRefPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZYX2).getComponent();
        Vector endv = endRefPane.getRefModel().getSelectedData();
        
        UFDate enddate = new UFDate(((Vector)endv.get(0)).get(1).toString());
        
        if(startdate.compareTo(enddate)>0){
        	
        	if(JKBXHeaderVO.ZYX1.equals(key)){
        		getBillCardPanel().getHeadItem(JKBXHeaderVO.ZYX2).setValue(null);// ��ս����ڼ�
        		exceptionHandler.handlerExeption(new BusinessException("��ʼ�ڼ䲻�����ڽ����ڼ�"));
        	} else if(JKBXHeaderVO.ZYX2.equals(key)) {
        		exceptionHandler.handlerExeption(new BusinessException("�����ڼ䲻�����ڿ�ʼ�ڼ�"));
        	}
        	setHeadValue(key,"");
       }else{
    	   ShowStatusBarMsgUtil.showStatusBarMsg("", ((DefaultExceptionHanler)exceptionHandler).getContext());
       }
	}

	/**
	 * ���ñ�ͷֵ
	 * @param key
	 * @param value
	 * @return
	 */
	public void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}
	
	/**
	 * ��ȡ��ͷָ���ֶ��ַ���Value
	 *
	 * @param itemKey
	 * @return
	 */
	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	private BillCardPanel getBillCardPanel(){
		return cardPanel;
	}
	

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
