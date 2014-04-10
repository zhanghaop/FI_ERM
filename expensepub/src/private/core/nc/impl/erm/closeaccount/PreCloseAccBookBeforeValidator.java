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
			throw new BusinessExceptionAdapter(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0088")/*@res "�޴˻���ڼ䣡"*/));
		}
		String yearmth = accperiodmonthVO.getYearmth();
		String syear = yearmth.substring(0, 4);
		String period = yearmth.substring(5);
		String pk_org = closeAccBookVO.getPk_org();

		try {
			// 1.У��ϵͳ�������Ƿ�֧����ǰ���ˡ�
			validateCanPreClose(pk_org);

			// 2.������к���ǰ����Ҫ�ص�ƾ֤�Ƿ����ȷ��
			validateAllowcloseFactor(pk_liabook, syear, period);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);;
			throw new BusinessExceptionAdapter(e);
		}

		return null;
	}

	/**
	 * У�������˲����������Ƿ�֧����ǰ���ˡ�
	 */
	private void validateCanPreClose(String pk_org) throws BusinessException {
		UFBoolean isCanPreCloseAcc = SysInitQuery.getParaBoolean(pk_org,
				canPreCloseParamCode);
		if(isCanPreCloseAcc == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0089")/*@res "ϵͳ�������Ƿ�֧����ǰ���ˡ�Ϊ�գ�������ǰ���ˣ�"*/);
		}
		if (!isCanPreCloseAcc.booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0090")/*@res "ϵͳ�������Ƿ�֧����ǰ���ˡ�Ϊ�񣬲�����ǰ���ˣ�"*/);
		}
	}

	/**
	 * ������к���ǰ����Ҫ�ص�ƾ֤�Ƿ����ȷ��
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
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0091")/*@res "����δȷ�ϵĺ���ǰ����Ҫ�ص�ƾ֤��������ǰ���ˣ�"*/);
		}
	}

}