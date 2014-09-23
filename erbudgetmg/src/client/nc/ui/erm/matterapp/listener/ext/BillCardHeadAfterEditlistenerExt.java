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
 * 卡片表头编辑后listener，扩展事件处理
 * 
 * 合生元专用
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
		if(ErmMatterAppConstExt.STARTPERIOD_FIELD.equals(evt.getKey())||ErmMatterAppConstExt.ENDPERIOD_FIELD.equals(evt.getKey())){//开始结束期间  合生元
			checkStartEndAccperiod(evt.getKey());
		} 
	}
	
	/**
	 * 合生元
	 * 校验开始期间是否合法,不合法则置null
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
        
		if (startdate.compareTo(enddate) > 0) {

			if (ErmMatterAppConstExt.STARTPERIOD_FIELD.equals(key)) {
				billForm.getBillCardPanel().getHeadItem(ErmMatterAppConstExt.ENDPERIOD_FIELD).setValue(null);
				exceptionHandler.handlerExeption(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("201212_0", "0201212-0105")/*
																 * @res
																 * "开始期间不能晚于结束期间"
																 */));
			} else if (ErmMatterAppConstExt.ENDPERIOD_FIELD.equals(key)) {
				exceptionHandler.handlerExeption(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("201212_0", "0201212-0106")/*
																 * @res
																 * "结束期间不能早于开始期间"
																 */));
			}
			billForm.setHeadValue(key, "");
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