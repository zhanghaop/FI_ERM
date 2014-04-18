package nc.impl.erm.closeaccount;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtil;
import nc.itf.erm.closeaccbook.IBatchCloseAccBookQryService;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.org.LiabilityBookVO;
import nc.vo.org.SetOfBookVO;
import nc.vo.pub.BusinessException;

public class BatchCloseAccBookQryServiceImpl implements
		IBatchCloseAccBookQryService {

	@SuppressWarnings("unchecked")
	@Override
	public List<AccperiodmonthVO> getPeriodmonthsByScheme(
			String pk_accperiodscheme) throws BusinessException {
		BaseDAO dao = new BaseDAO();

		String condition = AccperiodVO.PK_ACCPERIODSCHEME + " = '"
				+ pk_accperiodscheme + "'";
		List<AccperiodVO> periodVOList = (List<AccperiodVO>) dao
				.retrieveByClause(AccperiodVO.class, condition);
		if (periodVOList == null || periodVOList.isEmpty()) {
			return null;
		}

		List<AccperiodmonthVO> periodmonthVOList = null;
		List<String> periodpkList = new ArrayList<String>(periodVOList.size());
		for (AccperiodVO periodVO : periodVOList) {
			periodpkList.add(periodVO.getPk_accperiod());
		}
		condition = SqlUtil.buildInSql(AccperiodmonthVO.PK_ACCPERIOD,
				periodpkList);
		String orderBy = AccperiodmonthVO.YEARMTH;
		periodmonthVOList = (List<AccperiodmonthVO>) dao.retrieveByClause(
				AccperiodmonthVO.class, condition, orderBy);
		return periodmonthVOList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LiabilityBookVO> getLiabooksByScheme(String pk_accperiodscheme)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();

		String condition = SetOfBookVO.PK_ACCPERIODSCHEME + "='"
				+ pk_accperiodscheme + "'";
		// 先查出使用此期间方案的所有账簿
		List<SetOfBookVO> bookList = (List<SetOfBookVO>) dao.retrieveByClause(
				SetOfBookVO.class, condition);
		if (bookList == null || bookList.isEmpty()) {
			return null;
		}
		// 然后查出关联这些账簿的所有责任账簿
		List<LiabilityBookVO> liabilityBookVOList = null;
		List<String> bookpkList = new ArrayList<String>(bookList.size());
		for (SetOfBookVO bookVO : bookList) {
			bookpkList.add(bookVO.getPk_setofbook());
		}
		condition = SqlUtil
				.buildInSql(LiabilityBookVO.PK_SETOFBOOK, bookpkList);
		liabilityBookVOList = (List<LiabilityBookVO>) dao.retrieveByClause(
				LiabilityBookVO.class, condition);

		return liabilityBookVOList;
	}

}
