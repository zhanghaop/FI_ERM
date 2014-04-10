package nc.ui.erm.bx.ext.eventlistener;

import java.util.Arrays;
import java.util.List;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

/**
 * ��������ͷ�༭ǰ�¼���������
 * 
 * ����Ԫר�ã������ı����Ĳ��ɽ��з�̯��̯��
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class BxCardHeadBeforeEditEventHandler implements IAppEventHandler<CardHeadTailBeforeEditEvent> {

	private IExceptionHandler exceptionHandler;
	
	/**
	 * ��̯��̯����������ֶ�
	 */
	private List<String> fy_fields = Arrays.asList(new String[]{JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT,
			JKBXHeaderVO.START_PERIOD,JKBXHeaderVO.TOTAL_PERIOD});
	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		String key = e.getKey();
		e.setReturnValue(Boolean.TRUE);
		if (fy_fields.contains(key)){
			// ���շ������뵥���ɵı����������ɽ��з�̯��̯��
			BillItem pk_item = e.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
			String pk_matterapp = pk_item == null ? null : (String) pk_item.getValueObject();
			if(!StringUtil.isEmpty(pk_matterapp)){
				exceptionHandler.handlerExeption(new BusinessException("���շ������뵥���ɵı���������֧�ַ�̯��̯��"));
				e.setReturnValue(Boolean.FALSE);
			}
			
			BillItem djlxbm = e.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM);
			String pk_djlxbm = djlxbm == null ? null : (String) djlxbm.getValueObject();

			if(ErmConstExt.Distributor_BX_Tradetype.equals(pk_djlxbm)){
				exceptionHandler.handlerExeption(new BusinessException("�����̵渶����������֧��̯��"));
				e.setReturnValue(Boolean.FALSE);
			}
		}
	}
	
	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
