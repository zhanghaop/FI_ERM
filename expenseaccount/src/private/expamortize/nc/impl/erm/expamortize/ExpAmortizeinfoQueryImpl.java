package nc.impl.erm.expamortize;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.jdbc.framework.SQLParameter;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.util.erm.expamortize.ExpamtUtil;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class ExpAmortizeinfoQueryImpl implements IExpAmortizeinfoQuery {

	@Override
	public AggExpamtinfoVO[] queryByBxPks(String[] bxPks ,String currentAccMonth) throws BusinessException {
		String where = SqlUtils.getInStr(ExpamtinfoVO.PK_JKBX, bxPks, true);
		return queryByWhere(where, currentAccMonth);
	}

	@Override
	public AggExpamtinfoVO[] queryByPks(String[] pks, String currentAccMonth) throws BusinessException {
		String where = SqlUtils.getInStr(ExpamtinfoVO.PK_EXPAMTINFO, pks, true);
		return queryByWhere(where, currentAccMonth);
	}

	@Override
	public ExpamtinfoVO[] queryByOrg(String pk_org,String currentAccMonth)
			throws BusinessException {
		String whereCond = "pk_org='"+pk_org+"' and '"+currentAccMonth+"' between start_period and end_period and dr = 0 ";
		return queryInfoVOsByWhere(whereCond,null,currentAccMonth);
	}

	@Override
	public ExpamtinfoVO[] queryAllAmtingVOs() throws BusinessException {
		String where = ExpamtinfoVO.BILLSTATUS +" in (?,?) " ;
		SQLParameter para = new SQLParameter();
		para.addParam(ExpAmoritizeConst.Billstatus_Init);
		para.addParam(ExpAmoritizeConst.Billstatus_Amting);
		return queryInfoVOsByWhere(where,para, null);
	}
	
	/**
	 * 按查询条件查询vos
	 * 
	 * @param where
	 * @param currentAccMonth
	 * @return
	 * @throws BusinessException
	 */
	private AggExpamtinfoVO[] queryByWhere(String where, String currentAccMonth) throws BusinessException {
		IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<AggExpamtinfoVO> c = service.queryBillOfVOByCond(AggExpamtinfoVO.class, where, false);
		if(c == null || c.isEmpty()){
			return null;
		}
		//补充计算属性
		AggExpamtinfoVO[] result = c.toArray(new AggExpamtinfoVO[]{});
		ExpamtUtil.addComputePropertys(result, currentAccMonth);
		return result;
	}
	/**
	 * 按查询条件查询vos
	 * 
	 * @param where
	 * @return
	 * @throws BusinessException
	 */
	private ExpamtinfoVO[] queryInfoVOsByWhere(String where,SQLParameter para,  String currentAccMonth) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("unchecked")
		Collection<AggExpamtinfoVO> c = dao.retrieveByClause(ExpamtinfoVO.class, where,para);
		if(c == null || c.isEmpty()){
			return null;
		}
		
		ExpamtinfoVO[] result = c.toArray(new ExpamtinfoVO[c.size()]);
		ExpamtUtil.addComputePropertys(result, currentAccMonth);
		return result;
	}

	@Override
	public AggExpamtinfoVO queryByPk(String pk, String currentAccMonth) throws BusinessException {
		AggExpamtinfoVO[] result = queryByPks(new String[]{pk}, currentAccMonth);
		
		if(result != null && result.length > 0){
			return result[0];
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] queryPksByCond(String pk_org, String period)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String sqlWhere = "pk_org='"+pk_org+"' and '"+period+"' between start_period and end_period";
		
		Collection<ExpamtinfoVO> c= dao.retrieveByClause(ExpamtinfoVO.class, sqlWhere,new String[]{ExpamtinfoVO.PK_EXPAMTINFO});
		if(c != null && c.size() > 0){
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((ExpamtinfoVO)vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}

	@Override
	public ExpamtinfoVO[] queryExpamtinfoByPks(String[] pks, String currentAccMonth) throws BusinessException {
		String where = SqlUtils.getInStr(ExpamtinfoVO.PK_EXPAMTINFO, pks, true);
		return queryInfoVOsByWhere(where, null, currentAccMonth);
	}

	@Override
	public ExpamtinfoVO[] queryByOrgAndBillNo(String pk_org, String billno) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String sqlWhere = "pk_org='"+pk_org+"' and bx_billno='"+billno+"'";
		@SuppressWarnings("unchecked")
		Collection<ExpamtinfoVO> c= dao.retrieveByClause(ExpamtinfoVO.class, sqlWhere);
		if(c != null && c.size() > 0){
			ExpamtinfoVO[] result = c.toArray(new ExpamtinfoVO[c.size()]);
			ExpamtUtil.addComputePropertys(result, result[0].getStart_period());
			return result;
		}
		return null;
	}
	
	@Override
	public String[] queryByOrgBillNo(String pk_org, String billno) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String sqlWhere = "pk_org='"+pk_org+"' and bx_billno='"+billno+"'";
		@SuppressWarnings("unchecked")
		Collection<ExpamtinfoVO> c= dao.retrieveByClause(ExpamtinfoVO.class, sqlWhere,new String[]{ExpamtinfoVO.PK_EXPAMTINFO});
		if(c != null && c.size() > 0){
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((ExpamtinfoVO)vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] queryPksByWhereSql(String whereSql)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<ExpamtinfoVO> c = dao.retrieveByClause(ExpamtinfoVO.class,
				whereSql, new String[] { ExpamtinfoVO.PK_EXPAMTINFO });
		if (c != null && c.size() > 0) {
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((ExpamtinfoVO) vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExpamtDetailVO[] queryAllDetailVOs(String pkExpamtinfo)
			throws BusinessException {
		if (pkExpamtinfo == null) {
			return null;
		}

		ExpamtDetailVO[] detailVOs = null;
		try {
			IMDPersistenceQueryService queryService = MDPersistenceService.lookupPersistenceQueryService();
			StringBuffer whereSql = new StringBuffer();

			whereSql.append(SqlUtils.getInStr(ExpamtDetailVO.PK_EXPAMTINFO, new String[]{pkExpamtinfo}, false));

			Collection<ExpamtDetailVO> result = queryService.queryBillOfVOByCond(ExpamtDetailVO.class, whereSql.toString(), false);

			if (result == null) {
				return null;
			}

			detailVOs = (ExpamtDetailVO[]) result.toArray(new ExpamtDetailVO[] {});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return detailVOs;
	}
	
}
