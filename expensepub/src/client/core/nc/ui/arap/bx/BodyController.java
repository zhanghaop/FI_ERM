package nc.ui.arap.bx;

import java.util.EventObject;

import nc.bs.logging.Logger;
import nc.ui.arap.eventagent.EventTypeConst;
import nc.ui.arap.eventagent.TemplateConst;
import nc.ui.arap.eventagent.UIEventagent;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * nc.ui.arap.bx.BodyController
 * 
 * 借款报销表体界面控制器默认实现.
 * 
 * @see IBodyUIController
 * 
 * @author twei
 * 
 */
public class BodyController implements IBodyUIController {

	private BaseUIPanel parent;

	public void afterCardPanelEdit(EventObject e) {
		
		String key = "";
		if(e instanceof BillEditEvent){
			key = ((BillEditEvent)e).getKey();
			
			if(key.equals(BXBusItemVO.AMOUNT) || key.equals(BXBusItemVO.SZXMID) || key.equals(BXBusItemVO.SZXMMC)){
				
				UIEventagent templetEventAgent = parent.getTempletEventAgent();
				try {
					templetEventAgent.execute(e, EventTypeConst.AFTER_EDIT, TemplateConst.TEMPLATE_TYPE_CARD, 
							EventTypeConst.LISTENER_BillEditListener, TemplateConst.TEMPLATE_HEAD);
				} catch (BusinessException e1) {
					ExceptionHandler.consume(e1);
				}
				
			}
		}
	}

	public void beferCardPanelEdit(EventObject e) {

	}

	public void initBodyUIPanel(BXBusItemVO[] vo) {

	}

	public BXBusItemVO[] save() {
		return null;
	}

	public void saveCheck() throws BusinessException {

	}

	public void setContainer(BaseUIPanel container) {
		this.parent = container;
	}

	public BillScrollPane getBodyUIPanel(String tableCode) {
		BillScrollPane panel=new BillScrollPane();
		return panel;
	}

}
