package nc.ui.erm.matterapp.listener.ext;

import java.util.Vector;

import nc.bs.erm.matterapp.ext.ErmMatterAppConstExt;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * ��Ƭ��ͷ�༭��listener����չ�¼�����
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 * 
 */
@SuppressWarnings("restriction")
public class BillCardHeadAfterEditlistenerExt implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	private static final long serialVersionUID = 1L;

	private MatterAppMNBillForm billForm;

	protected IExceptionHandler exceptionHandler;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent evt) {
		if(ErmMatterAppConstExt.STARTPERIOD_FIELD.equals(evt.getKey())||ErmMatterAppConstExt.ENDPERIOD_FIELD.equals(evt.getKey())){//��ʼ�����ڼ�  ����Ԫ
			checkStartEndAccperiod(evt.getKey());
		} 
	}
	
	/**
	 * ����Ԫ
	 * У�鿪ʼ�ڼ��Ƿ�Ϸ�,���Ϸ�����null
	 * @param evt
	 */
	
	@SuppressWarnings("rawtypes")
	private void checkStartEndAccperiod(String key){
		if(null==billForm.getHeadItemStrValue(ErmMatterAppConstExt.ENDPERIOD_FIELD)||null==billForm.getHeadItemStrValue(ErmMatterAppConstExt.STARTPERIOD_FIELD))
			return;
		
        UIRefPane startRefPane = (UIRefPane) billForm.getBillCardPanel().getHeadItem(ErmMatterAppConstExt.STARTPERIOD_FIELD).getComponent();
        Vector startv = startRefPane.getRefModel().getSelectedData();
        
        UFDate startdate = new UFDate(((Vector)startv.get(0)).get(1).toString());
		
        UIRefPane endRefPane = (UIRefPane) billForm.getBillCardPanel().getHeadItem(ErmMatterAppConstExt.ENDPERIOD_FIELD).getComponent();
        Vector endv = endRefPane.getRefModel().getSelectedData();
        
        UFDate enddate = new UFDate(((Vector)endv.get(0)).get(1).toString());
        
        if(startdate.compareTo(enddate)>0){
        	
        	if(ErmMatterAppConstExt.STARTPERIOD_FIELD.equals(key)){
        		billForm.getBillCardPanel().getHeadItem(ErmMatterAppConstExt.ENDPERIOD_FIELD).setValue(null);
        		exceptionHandler.handlerExeption(new BusinessException("��ʼ�ڼ䲻�����ڽ����ڼ�"));
        	} else if(ErmMatterAppConstExt.ENDPERIOD_FIELD.equals(key)) {
        		exceptionHandler.handlerExeption(new BusinessException("�����ڼ䲻�����ڿ�ʼ�ڼ�"));
        	}
        	billForm.setHeadValue(key,"");
       }else{
    	   ShowStatusBarMsgUtil.showStatusBarMsg("", ((DefaultExceptionHanler)exceptionHandler).getContext());
       }
	}


	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	

}