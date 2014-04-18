package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.businessevent.bd.BDCommonEvent.BDCommonUserObj;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class ErmBxWtxValidatelListener implements IBusinessListener {

    @Override
    @Business(business=ErmBusinessDef.CloseAcc,subBusiness=CloseAccBiz.JKBXControlCloseAccBefore, description = "结账前校验是否有未摊销的摊销信息"/*-=notranslate=-*/ ,type=BusinessType.CORE)
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
        StringBuffer msg = new StringBuffer();
        String pk_accperiodmonth = vo.getPk_accperiodmonth();
        AccperiodmonthVO accperiodVO = ErAccperiodUtil
                .getAccperiodmonthByPk(pk_accperiodmonth);
        String yearAndMonth = accperiodVO.getYearmth();

        // 未摊销的摊销信息校验
        String expmsg = checkExpAmortize(pk_org, yearAndMonth);
        if (expmsg != null) {
            msg.append(expmsg);
        }

        if (msg.length() > 0)
            msg.deleteCharAt(msg.length() - 1);
        if (msg.length() != 0) {
            throw new BusinessException(msg.toString());
        }
    }

    /**
     * 摊销生效
     *
     * @param expvo
     * @throws BusinessException
     */
    private String checkExpAmortize(String pk_org, String yearAndMonth)
            throws BusinessException {
        StringBuffer msg = new StringBuffer();
        ExpamtinfoVO[] expvos = getExpAmortizeSerivce().queryByOrg(pk_org,
                yearAndMonth);
        if (expvos == null) {
            return null;
        }
        for (ExpamtinfoVO vo : expvos) {
            if (!vo.getAmt_status().equals(UFBoolean.TRUE)) {
                msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0089")/*@res "有未摊销的摊销信息"*/ + vo.getBx_billno() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0090")/*@res "不可以结帐 !\n"*/);
            }
        }
        if (msg.length() != 0) {
            return msg.toString();
        } else {
            return null;
        }
    }

    private IExpAmortizeinfoQuery getExpAmortizeSerivce() {
        return NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
    }

}
