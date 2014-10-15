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
 * 报销单表头编辑前事件监听处理
 * 
 * 合生元专用：拉单的报销的不可进行分摊、摊销
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class BxCardHeadBeforeEditEventHandler implements IAppEventHandler<CardHeadTailBeforeEditEvent> {

	private IExceptionHandler exceptionHandler;
	
	/**
	 * 分摊、摊销设置相关字段
	 */
	private List<String> fy_fields = Arrays.asList(new String[]{JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT,
			JKBXHeaderVO.START_PERIOD,JKBXHeaderVO.TOTAL_PERIOD});
	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		String key = e.getKey();
		e.setReturnValue(Boolean.TRUE);
		if (fy_fields.contains(key)){
			// 参照费用申请单生成的报销单，不可进行分摊、摊销
			BillItem pk_item = e.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
			String pk_matterapp = pk_item == null ? null : (String) pk_item.getValueObject();
			if(!StringUtil.isEmpty(pk_matterapp)){
				exceptionHandler.handlerExeption(new BusinessException("参照费用申请单生成的报销单，不支持分摊、摊销"));
				e.setReturnValue(Boolean.FALSE);
			}
			
			BillItem djlxbm = e.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM);
			String pk_djlxbm = djlxbm == null ? null : (String) djlxbm.getValueObject();

			if(ErmConstExt.Distributor_BX_Tradetype.equals(pk_djlxbm)){
				exceptionHandler.handlerExeption(new BusinessException("经销商垫付报销单，不支持摊销"));
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
