package nc.bs.erm.expamortize.eventlistener;
/**
 * 报销单取消生效前要校验是否摊销生效
 */
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
/**
 * 报销单反审核前待摊费用插件
 * @author wangled
 *
 */
public class ErmExpamUnApproveListener implements IBusinessListener{

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		if(ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)){
			StringBuffer msg = new StringBuffer();
			CShareDetailVO[] cShareDetailVo = vos[0].getcShareDetailVo();
			JKBXHeaderVO vo=vos[0].getParentVO();
			String billNo=vo.getDjbh();
			if(cShareDetailVo!=null && cShareDetailVo.length!=0){
				//需要按报销单表体分摊业务的承担单位
				for(CShareDetailVO csdetailvo:cShareDetailVo){
					String assumeOrg = csdetailvo.getAssume_org();
					checkExpamtinfo(msg, billNo, assumeOrg);
				}
			}else{
				//要根据表头费用承担单位来判断
				String fwdwbm=vo.getFydwbm();
				checkExpamtinfo(msg, billNo, fwdwbm);
			}
		}
	}

	private void checkExpamtinfo(StringBuffer msg, String billNo,
			String org) throws BusinessException {
		ExpamtinfoVO[] expamt=getSerive().queryByOrgAndBillNo(org,billNo);
		if (expamt != null && expamt.length > 0) {
			ExpamtinfoVO infoVo = expamt[0];
			if (infoVo.getAmt_status().equals(UFBoolean.TRUE)) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0102")/*@res "报销单已经摊销过，不可以反审批"*/);
			}
		}
		if (msg.length() != 0) {
			throw new BusinessException(msg.toString());
		}
	}

	private IExpAmortizeinfoQuery getSerive(){
		return NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
	}

}