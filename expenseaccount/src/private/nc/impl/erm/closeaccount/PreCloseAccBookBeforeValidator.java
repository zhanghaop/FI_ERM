package nc.impl.erm.closeaccount;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.check.VoucherStatus;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class PreCloseAccBookBeforeValidator implements Validator {
	private final String canPreCloseParamCode = "RA112";

	@Override
	public ValidationFailure validate(Object obj) {
		if (obj == null)
			return null;
		CloseAccBookVO closeAccBookVO = (CloseAccBookVO) obj;
		String pk_liabook = closeAccBookVO.getCloseorgpks();
		String pk_accperiodmonth = closeAccBookVO.getPk_accperiodmonth();
		AccperiodmonthVO accperiodmonthVO = null;
		try {
			accperiodmonthVO = (AccperiodmonthVO) new BaseDAO().retrieveByPK(
					AccperiodmonthVO.class, pk_accperiodmonth);
		} catch (DAOException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);;
			throw new BusinessExceptionAdapter(e);
		}
		if (accperiodmonthVO == null) {
			throw new BusinessExceptionAdapter(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0088")/*@res "无此会计期间！"*/));
		}
		String yearmth = accperiodmonthVO.getYearmth();
		String syear = yearmth.substring(0, 4);
		String period = yearmth.substring(5);
		String pk_org = closeAccBookVO.getPk_org();

		try {
			// 1.校验系统参数‘是否支持提前关账’
			validateCanPreClose(pk_org);

			// 2.检查所有含提前关账要素的凭证是否均已确认
			validateAllowcloseFactor(pk_liabook, syear, period);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);;
			throw new BusinessExceptionAdapter(e);
		}

		return null;
	}

	/**
	 * 校验责任账簿级参数“是否支持提前关账”
	 */
	private void validateCanPreClose(String pk_org) throws BusinessException {
		UFBoolean isCanPreCloseAcc = SysInitQuery.getParaBoolean(pk_org,
				canPreCloseParamCode);
		if(isCanPreCloseAcc == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0089")/*@res "系统参数‘是否支持提前关账’为空，不能提前关账！"*/);
		}
		if (!isCanPreCloseAcc.booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0090")/*@res "系统参数‘是否支持提前关账’为否，不能提前关账！"*/);
		}
	}

	/**
	 * 检查所有含提前关账要素的凭证是否均已确认
	 */
	private void validateAllowcloseFactor(String pk_liabook, String syear,
			String period) throws BusinessException {
		String sql = "select count(1) num from resa_respdetail t, resa_factorasoa r where t.pk_factorasoa = r.pk_factorasoa and t.pk_org = ? and t.syear = ? and t.period = ? and r.allowclose = 'Y' and t.voucherstatus != ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_liabook);
		parameter.addParam(syear);
		parameter.addParam(period);
		parameter.addParam(VoucherStatus.APPROVED.value());
		Object obj = new BaseDAO().executeQuery(sql, parameter,
				new ColumnProcessor());
		int num = 0;
		if (obj instanceof Integer) {
			num = ((Integer) obj).intValue();
		}
		if (num > 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0091")/*@res "存在未确认的含提前关账要素的凭证，不能提前关账！"*/);
		}
	}

}