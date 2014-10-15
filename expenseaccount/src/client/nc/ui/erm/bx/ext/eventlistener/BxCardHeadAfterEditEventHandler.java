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
 * 报销单表头编辑后事件监听处理
 * 
 * 合生元专用：处理开始期间、结束期间
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
			// 经销商垫付报销单，开始期间或者结束期间编辑后数据校验
			if (key.equals(JKBXHeaderVO.ZYX1)||key.equals(JKBXHeaderVO.ZYX2)){
				checkStartEndAccperiod(key);
			}
		}
		
	}
	
	/**
	 * 合生元
	 * 校验开始期间是否合法,不合法则置null
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
        		getBillCardPanel().getHeadItem(JKBXHeaderVO.ZYX2).setValue(null);// 清空结束期间
        		exceptionHandler.handlerExeption(new BusinessException("开始期间不能晚于结束期间"));
        	} else if(JKBXHeaderVO.ZYX2.equals(key)) {
        		exceptionHandler.handlerExeption(new BusinessException("结束期间不能早于开始期间"));
        	}
        	setHeadValue(key,"");
       }else{
    	   ShowStatusBarMsgUtil.showStatusBarMsg("", ((DefaultExceptionHanler)exceptionHandler).getContext());
       }
	}

	/**
	 * 设置表头值
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
	 * 获取表头指定字段字符串Value
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
