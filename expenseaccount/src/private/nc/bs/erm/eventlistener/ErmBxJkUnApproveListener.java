package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.util.erm.closeacc.CloseAccUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.util.AuditInfoUtil;
/**
 * 借款报销取消生效时需要校验结账情况
 * @author wangled
 *
 */
public class ErmBxJkUnApproveListener  implements IBusinessListener{

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		if(ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)){
			StringBuffer msg = new StringBuffer();
			JKBXHeaderVO vo=vos[0].getParentVO();
			String pk_group=vo.getPk_group();
			String pk_org=vo.getPk_org();
			UFDate djrq=vo.getJsrq();//按签字日期控制
			if(djrq == null){
				djrq = AuditInfoUtil.getCurrentTime().getDate();
			}
			
			AccperiodmonthVO accperiodmonthVO=ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, djrq);
			String pk_accperiodmonth=accperiodmonthVO.getPk_accperiodmonth();
			String pk_accperiodscheme = accperiodmonthVO.getPk_accperiodscheme();
			BatchCloseAccBookVO[] ermCloseAccBook = CloseAccUtil.getEndAcc(pk_group, pk_org,
					pk_accperiodmonth, pk_accperiodscheme);
			if (ermCloseAccBook != null && ermCloseAccBook.length > 0) {
				BatchCloseAccBookVO accvo = ermCloseAccBook[0];
				if (accvo.getIsendacc().equals(UFBoolean.TRUE)) {
					msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0021")/*@res "这个会计期间费用管理已经结账，不可以反审批"*/);
				}
			}
			if (msg.length() != 0) {
				throw new BusinessException(msg.toString());
			}
		}
	}
}