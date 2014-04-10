package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.gl.businessevent.CloseAccBookEvent;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.util.erm.closeacc.CloseAccUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * 总账关帐校验费用管理系统是否结账
 *
 * @author wangled
 *
 */
public class ErmGLCloseAccListener implements IBusinessListener {

    private static final String PARAM_ER_CLOSEACC_CHECK_SETTLEACC = "ER12";

    private static final String STR_Y = "Y";

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		CloseAccBookEvent eve = (CloseAccBookEvent) event;
		CloseAccBookVO vo = (CloseAccBookVO) eve.getInputVOs()[0];
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String pk_org = vo.getPk_org();
		String pk_accperiodmonth = vo.getPk_accperiodmonth();
		AccperiodmonthVO accperiodVO = ErAccperiodUtil
				.getAccperiodmonthByPk(pk_accperiodmonth);
		String pk_accperiodscheme = accperiodVO.getPk_accperiodscheme();
		// 查询费用系统是否结账
        if (!BXConstans.GL_FUNCODE.equals(vo.getModuleid())) {
			return;
		}
		String paraString = SysinitAccessor.getInstance().getParaString(pk_org,
		        PARAM_ER_CLOSEACC_CHECK_SETTLEACC);
		if (!STR_Y.equals(paraString))
		    return;
		BatchCloseAccBookVO[] ermCloseAccBook = CloseAccUtil.getEndAcc(
				pk_group, pk_org, pk_accperiodmonth, pk_accperiodscheme);
		if (ermCloseAccBook != null && ermCloseAccBook.length > 0) {
			BatchCloseAccBookVO accvo = ermCloseAccBook[0];
			if (!accvo.getIsendacc().equals(UFBoolean.TRUE)) {
                String sysName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
                        .getStrByID("201109_0", "0201109-0092"); /* @res "费用管理" */
                try {
                    eve.addUnSettleSystem(accvo, sysName);
                } catch (Exception e) {
                    throw ExceptionHandler.handleException(e);
                }
			}
		}
	}

}