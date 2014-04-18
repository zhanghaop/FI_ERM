package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.util.erm.closeacc.CloseAccUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

/**
 * 费用结账审批前和反审批前需要校验结账情况
 *
 * @author wangled
 *
 */
public class ErmCSApproveListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggCostShareVO[] vos = (AggCostShareVO[]) obj.getNewObjects();
		if (ErmEventType.TYPE_UNAPPROVE_BEFORE.equalsIgnoreCase(eventType)||ErmEventType.TYPE_APPROVE_BEFORE.equalsIgnoreCase(eventType)) {
			StringBuffer msg = new StringBuffer();
			CostShareVO vo = (CostShareVO) vos[0].getParentVO();
			String pk_group = vo.getPk_group();
			String pk_org = vo.getPk_org();
			UFDate djrq = vo.getBilldate();
			AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil
					.getAccperiodmonthByUFDate(pk_org, djrq);
			String pk_accperiodmonth = accperiodmonthVO.getPk_accperiodmonth();
			String pk_accperiodscheme = accperiodmonthVO
					.getPk_accperiodscheme();
			BatchCloseAccBookVO[] ermCloseAccBook = CloseAccUtil.getEndAcc(
					pk_group, pk_org, pk_accperiodmonth, pk_accperiodscheme);
			if (ermCloseAccBook != null && ermCloseAccBook.length > 0) {
				BatchCloseAccBookVO accvo = ermCloseAccBook[0];
				if (accvo.getIsendacc().equals(UFBoolean.TRUE)) {
					if(ErmEventType.TYPE_UNAPPROVE_BEFORE.equalsIgnoreCase(eventType)){
						msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0140")/*@res "这个会计期间费用管理已经结账，不可以取消审批"*/);
					}else if(ErmEventType.TYPE_APPROVE_BEFORE.equalsIgnoreCase(eventType)){
						msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0138")/*@res "这个会计期间费用管理已经结账，不可以审批"*/);

					}
				}
			}
			if (msg.length() != 0) {
				throw new BusinessException(msg.toString());
			}

		}

	}

}