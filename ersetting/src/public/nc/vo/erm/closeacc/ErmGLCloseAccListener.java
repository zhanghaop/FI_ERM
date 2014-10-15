package nc.vo.erm.closeacc;

import java.util.Map;

import javax.resource.NotSupportedException;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.gl.businessevent.CloseAccBookEvent;
import nc.bs.logging.Log;
import nc.itf.org.IAccountingBookQryService;
import nc.pubitf.initgroup.InitGroupQuery;
import nc.pubitf.org.IAccountingBookPubService;
import nc.pubitf.org.ICloseAccQryPubServicer;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.para.SysInitQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

@SuppressWarnings("restriction")
public class ErmGLCloseAccListener implements IBusinessListener {

	private static final String ER_SYSCODE = "2011";

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		CloseAccBookEvent eve = (CloseAccBookEvent) event;
		CloseAccBookVO closeAccBookVO = (CloseAccBookVO) eve.getInputVOs()[0];
		String pk_org = closeAccBookVO.getPk_org();
		AccperiodmonthVO queryAccperiodmonthVOByPK = NCLocator.getInstance().lookup(
				IAccountingBookQryService.class).queryAccperiodmonthVOByPK(
				closeAccBookVO.getPk_accperiodmonth());
		String glCloseDate = queryAccperiodmonthVOByPK.getYearmth(); // 正在关账的年度

		boolean canBeClose = checkSystem(getCreateDate(ER_SYSCODE, pk_org), pk_org, glCloseDate);

		try {
			if (!canBeClose) {
				String sysName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("funcode","D2011")/*@res "费用管理"*/;
				eve.addUnSettleSystem(closeAccBookVO, sysName);
			}
		} catch (NotSupportedException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public void checkUnCloseAcc(String year, String cope, String pk_org) throws BusinessException {
	    checkUnCloseAcc(year, cope, pk_org, 
	            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0",
                    "02011001-0025") /* @res "总账已经关帐，不能进行报销管理反关账! */);
	}

    public void checkUnCloseAcc(String year, String cope, String pk_org,
            String errorMsg) throws BusinessException {
        String period = year + "-" + cope;
        Map<String, String> book = NCLocator
                .getInstance()
                .lookup(IAccountingBookPubService.class)
                .queryAccountingBookIDByFinanceOrgIDWithMainAccountBook(
                        new String[] { pk_org });
        String pkbook = null;
        if (book != null && book.size() != 0) {
            pkbook = book.values().iterator().next();
        }
        if (StringUtil.isEmpty(pkbook)) {
            Log.getInstance(getClass()).debug("this org has no bookaccount");
            return;
        }
        // 安装总账产品，需要总账关账检查业务系统的参数是否启用，是单产品，就不需要检查业务系统的参数
        boolean isInstallGL = InitGroupQuery.isEnabled(InvocationInfoProxy
                .getInstance().getGroupId(), BXConstans.GL_FUNCODE);
        boolean isValidate = false;
        if (isInstallGL) {
            String glPara = SysInitQuery.getParaString(pk_org, "GL034");
            String erPara = SysInitQuery.getParaString(pk_org, "ER12");
            if (glPara != null && "Y".equals(erPara))
                isValidate = true;
        }
        if (isValidate) {
            // 期间帐簿是否关帐
            boolean isClosed = NCLocator.getInstance()
                    .lookup(ICloseAccQryPubServicer.class)
                    .isCloseByAccountBookId(pkbook, period);
            if (isClosed) {
                throw new BusinessException(errorMsg);
            }
        }

    }

	private boolean checkSystem(String ermCreateDate, String pk_org, String period)
			throws BusinessException {
		Map<String, Boolean> res = NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class)
				.isCloseByModuleIdAndPk_org(ER_SYSCODE, pk_org, new String[] { period });

		if (res.get(period) == null || !res.get(period).booleanValue()) {
			if (ermCreateDate == null)
				return true;
			if (ermCreateDate.compareTo(period) <= 0) {
				return false;
			}
		}
		return true;
	}

	public static String getCreateDate(String moduleCode, String pk_org) {

		IOrgUnitPubService orgUnitPubService = NCLocator.getInstance().lookup(
				IOrgUnitPubService.class);
		try {
			return orgUnitPubService.getOrgModulePeriodByOrgIDAndModuleID(pk_org, moduleCode);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return null;
	}

}