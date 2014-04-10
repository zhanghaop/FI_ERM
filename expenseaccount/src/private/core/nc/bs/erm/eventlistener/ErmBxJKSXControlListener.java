package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.businessevent.bd.BDCommonEvent.BDCommonUserObj;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.er.exception.ErmBusinessRuntimeException;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 结账前校验是否生效
 *
 * @author wangled
 *
 */
public class ErmBxJKSXControlListener implements IBusinessListener {

    private static final String PARAM_ER9 = "ER9";
    private static final String PARAM_BJC = "1"; // nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0085")/*@res
                                                 // "不检查"*/;
    private static final String PARAM_JCDBKZ = "2"; // nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0086")/*@res
                                                    // "检查但不控制"*/;
    private static final String PARAM_JCBQKZ = "3"; // nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0087")/*@res
                                                    // "检查并且控制"*/;

	@Override
    @Business(business=ErmBusinessDef.CloseAcc,subBusiness=CloseAccBiz.JKBXControlCloseAccBefore, description = "结账前校验单据是否生效"/*-=notranslate=-*/ ,type=BusinessType.CORE)
	public void doAction(IBusinessEvent event) throws BusinessException {
		BDCommonEvent erEvent = (BDCommonEvent) event;
		BDCommonUserObj obj = (BDCommonUserObj) erEvent.getUserObject();
		Object vos[] = (Object[]) obj.getNewObjects();
		CloseAccBookVO vo = ((CloseAccBookVO) vos[0]);
		// 只校验费用管理模块
		if (!BXConstans.ERM_MODULEID.equals(vo.getModuleid())) {
			return;
		}

        String pk_org = vo.getPk_org();
		String param_er =
		    SysinitAccessor.getInstance().getParaString(pk_org, PARAM_ER9);
		//不检查
		if (PARAM_BJC.equals(param_er))
		    return;

        StringBuffer msg = new StringBuffer();
        String pk_accperiodmonth = vo.getPk_accperiodmonth();
        AccperiodmonthVO accperiodVO = ErAccperiodUtil
                .getAccperiodmonthByPk(pk_accperiodmonth);
        String begindate = accperiodVO.getBegindate().toString();
        String enddate = accperiodVO.getEnddate().toString();
//        String yearAndMonth = accperiodVO.getYearmth();

        // 1.报销单
        String bxmsg = checkbxzt(pk_org, begindate, enddate);
        if (bxmsg != null) {
            msg.append(bxmsg);
        }
        // 借款单
        String jxmsg = checkjkzt(pk_org, begindate, enddate);
        if (jxmsg != null) {
            msg.append(jxmsg);
        }

        // 2.费用结转单
        String sharemsg = checksharezt(pk_org, begindate, enddate);
        if (sharemsg != null) {
            msg.append(sharemsg);
        }

        // 3.摊销信息
//        String expmsg = checkExpAmortize(pk_org, yearAndMonth);
//        if (expmsg != null) {
//            msg.append(expmsg);
//        }

        // 4. 预提单
        String accmsg = checkaccruedbill(pk_org, begindate, enddate);
        if(accmsg != null){
        	msg.append(accmsg);
        }
        if (msg.length() > 0)
            msg.deleteCharAt(msg.length() - 1);
		//检查并且控制
		if (msg.length() != 0 && PARAM_JCBQKZ.equals(param_er)) {
			throw new BusinessException(msg.toString());
		}
		else if (msg.length() != 0 && PARAM_JCDBKZ.equals(param_er)) {
		    //检查但不控制
            throw new ErmBusinessRuntimeException(msg.toString());
		}
	}

	private String checkjkzt(String pk_org, String begindate, String enddate)
			throws BusinessException {
		StringBuffer msg = new StringBuffer();
		JKHeaderVO[] jkvos = getIBXBillPrivateSerivce()
				.getJKdjByMthPk(pk_org, begindate, enddate);
		if (jkvos == null) {
			return null;
		}

		for (JKHeaderVO vo : jkvos) {
			if (BXStatusConst.DJZT_Sign != vo.getDjzt()) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0088")/*@res "有未生效的借款单据"*/ + vo.getDjbh() + "\n");
			}
		}
		if (msg.length() != 0) {
			return msg.toString();
		} else {
			return null;
		}
	}

	private String checkaccruedbill(String pk_org, String begindate, String enddate) throws BusinessException {
		StringBuffer msg = new StringBuffer();
		String condition = AccruedVO.PK_ORG + "='" + pk_org + "' and " + AccruedVO.BILLDATE + ">='" + begindate
				+ "' and " + AccruedVO.BILLDATE + "<='" + enddate + "'";
		AggAccruedBillVO[] aggvos = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByWhere(
				condition);
		if (aggvos == null || aggvos.length == 0) {
			return null;
		}

		for (AggAccruedBillVO vo : aggvos) {
			if (BXStatusConst.DJZT_Sign != vo.getParentVO().getBillstatus()) {
				msg.append("有未生效的预提单" + vo.getParentVO().getBillno());
			}
		}
		if (msg.length() != 0) {
			return msg.toString();
		} else {
			return null;
		}
	}

	/**
	 * 摊销生效
	 *
	 * @param expvo
	 * @throws BusinessException
	 */
//	private String checkExpAmortize(String pk_org, String yearAndMonth)
//			throws BusinessException {
//		StringBuffer msg = new StringBuffer();
//		ExpamtinfoVO[] expvos = getExpAmortizeSerivce().queryByOrg(pk_org,
//				yearAndMonth);
//		if (expvos == null) {
//			return null;
//		}
//		for (ExpamtinfoVO vo : expvos) {
//			if (!vo.getAmt_status().equals(UFBoolean.TRUE)) {
//				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0089")/*@res "有未摊销的摊销信息"*/ + vo.getBx_billno() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0090")/*@res "不可以结帐 !\n"*/);
//			}
//		}
//		if (msg.length() != 0) {
//			return msg.toString();
//		} else {
//			return null;
//		}
//	}

	/**
	 * 费用结转生效
	 *
	 * @param costvo
	 * @throws BusinessException
	 */
	private String checksharezt(String pk_org, String begindate, String enddate)
			throws BusinessException {
		StringBuffer msg = new StringBuffer();
		CostShareVO[] costvos = getCostShareSerivce()
				.getShareByMthPk(pk_org, begindate, enddate);
		if(costvos==null){
			return null;
		}
		for (CostShareVO vo : costvos) {
			if (BXStatusConst.DJZT_Sign != vo.getBillstatus()) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0091")/*@res "有未生效的费用结转单据"*/ + vo.getBillno() + "\n");
			}
		}
		if (msg.length() != 0) {
			return msg.toString();
		} else {
			return null;
		}
	}

	/**
	 * 报销生效
	 *
	 * @param bxvo
	 * @throws BusinessException
	 */
	private String checkbxzt(String pk_org, String begindate, String enddate)
			throws BusinessException {
		StringBuffer msg = new StringBuffer();
		BXHeaderVO[] bxvos = getIBXBillPrivateSerivce()
				.getBXdjByMthPk(pk_org, begindate, enddate);
		if(bxvos==null){
			return null;
		}
		for (JKBXHeaderVO vo : bxvos) {
			if (BXStatusConst.DJZT_Sign != vo.getDjzt()) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0092")/*@res "有未生效的报销单据"*/ + vo.getDjbh() + "\n");
			}
		}
		if (msg.length() != 0) {
			return msg.toString();
		} else {
			return null;
		}
	}

//	private IExpAmortizeinfoQuery getExpAmortizeSerivce() {
//		return NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
//	}

	private IErmCostShareBillQuery getCostShareSerivce() {
		return NCLocator.getInstance().lookup(IErmCostShareBillQuery.class);
	}

	private IBXBillPrivate getIBXBillPrivateSerivce() {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

}