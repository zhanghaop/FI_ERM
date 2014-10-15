package nc.impl.erm.closeaccount;

import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.itf.org.ICloseAccBookManageService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.check.VoucherStatus;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;

public class CloseAccBookBeforeValidator implements Validator {
	private final String canPreCloseParamCode = "RA112";

	@Override
	public ValidationFailure validate(Object obj) {
		if (obj == null)
			return null;
		CloseAccBookVO closeAccBookVO = (CloseAccBookVO) obj;
		String module_id = closeAccBookVO.getModuleid();
		String pk_liabook = closeAccBookVO.getCloseorgpks();
		String pk_accperiodmonth = closeAccBookVO.getPk_accperiodmonth();

		// 1.检查所有非来源于损益结转的凭证是否均已确认
		AccperiodmonthVO accperiodmonthVO;
		try {
			accperiodmonthVO = (AccperiodmonthVO) new BaseDAO().retrieveByPK(
					AccperiodmonthVO.class, pk_accperiodmonth);
		} catch (DAOException e) {
			throw new BusinessExceptionAdapter(e);
		}
		String yearmth = accperiodmonthVO.getYearmth();
		String syear = yearmth.substring(0, 4);
		String period = yearmth.substring(5);

		validateVoucherApproved(pk_liabook, syear, period);

		// 2.支持提前关账且未提前关账的自动提前关账
		boolean isCanPreClose = false;// 取责任账簿级参数:是否支持提前关账
		try {
			UFBoolean isNeedPreCloseAcc = SysInitQuery.getParaBoolean(
					pk_liabook, canPreCloseParamCode);
			if (isNeedPreCloseAcc != null)
				isCanPreClose = isNeedPreCloseAcc.booleanValue();
		} catch (BusinessException e) {
			ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0005")/*@res "查询参数“是否支持提前关账”时异常，不做自动提前关账操作！"*/,e));
			return null;
		}
		if(isCanPreClose == false){
			ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0006")/*@res "参数“是否支持提前关账”为否，不做自动提前关账操作！"*/));
			return null;
		}
		boolean preClose = isPreClose(pk_liabook, pk_accperiodmonth, module_id);
		if (!preClose) {// 未提前关账的自动执行提前关账
			closeAccBookVO.setIspreclose(UFBoolean.TRUE);
			closeAccBookVO.setPreclosetime(new UFDateTime());
			closeAccBookVO.setPrecloseuser("NC_USER0000000000000");// NC系统用户


			ICloseAccBookManageService closeManageService = NCLocator
					.getInstance().lookup(ICloseAccBookManageService.class);
			try {
				/*LiabilityBookVO liabookVO = (LiabilityBookVO) new BaseDAO()
						.retrieveByPK(LiabilityBookVO.class, pk_liabook);
				closeManageService.preCloseAccBook(
						liabookVO.getPk_liabilityperiod(), closeAccBookVO);*/

				new BaseDAO().updateVO(closeAccBookVO);
			} catch (BusinessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
				throw new BusinessExceptionAdapter(e);
			}
		}
		return null;
	}

	/**
	 * 检查所有非来源于损益结转的凭证是否均已确认
	 */
	private void validateVoucherApproved(String pk_liabook, String syear,
			String period){
		String sql = "select count(1) num from resa_respvoucher t where t.pk_org = ? and t.syear = ? and t.period = ? and t.srcsystem != ? and t.voucherstatus != ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_liabook);
		parameter.addParam(syear);
		parameter.addParam(period);
		parameter.addParam("RATSF");
		parameter.addParam(VoucherStatus.APPROVED.value());
		Object obj = null;
		try {
			obj = new BaseDAO().executeQuery(sql, parameter, new ColumnProcessor());
		} catch (DAOException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			throw new BusinessExceptionAdapter(e);
		}
		int num = 0;
		if (obj instanceof Integer) {
			num = ((Integer) obj).intValue();
		}
		if (num > 0) {
			//throw new BusinessException("存在未确认的非来源于损益结转的凭证，不能关账！");
			throw new BusinessExceptionAdapter(new BusinessException(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0087")/*@res "存在未确认的非来源于损益结转的凭证，不能关账！"*/));
		}
	}

	/**
	 * 是否已经提前关账
	 */
	@SuppressWarnings({ "unchecked" })
	private boolean isPreClose(String pk_liabook, String pk_accperiodmonth,
			String module_id) {
		BaseDAO dao = new BaseDAO();
		String condition = CloseAccBookVO.CLOSEORGPKS + " = ? and "
				+ CloseAccBookVO.PK_ACCPERIODMONTH + " = ? and "
				+ CloseAccBookVO.MODULEID + " = ? and "
				+ CloseAccBookVO.ISPRECLOSE + " = 'Y'";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_liabook);
		parameter.addParam(pk_accperiodmonth);
		parameter.addParam(module_id);
		List<CloseAccBookVO> closeAccBookVOList;
		try {
			closeAccBookVOList = (List<CloseAccBookVO>) dao.retrieveByClause(
					CloseAccBookVO.class, condition, parameter);
		} catch (DAOException e) {
			throw new BusinessExceptionAdapter(e);
		}
		if (closeAccBookVOList != null && closeAccBookVOList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}