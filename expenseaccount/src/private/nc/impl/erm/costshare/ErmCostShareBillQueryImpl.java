package nc.impl.erm.costshare;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.erm.extendconfig.ErmExtendconfigInterfaceCenter;
import nc.itf.uif.pub.IUifService;
import nc.jdbc.framework.SQLParameter;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class ErmCostShareBillQueryImpl implements IErmCostShareBillQuery {

	private IUifService getService(){
		return NCLocator.getInstance().lookup(IUifService.class);
	}
	
	public AggCostShareVO queryBillByPK(String pk) throws BusinessException {
		IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();
		AggCostShareVO aggvo = service.queryBillOfVOByPK(AggCostShareVO.class, pk, false);
		if(aggvo != null){
			// 补充扩展子表信息
			String pk_group = ((CostShareVO)aggvo.getParentVO()).getPk_group();
			ErmExtendconfigInterfaceCenter.fillExtendTabVOs(pk_group,
					CostShareVO.PK_TRADETYPE,aggvo);
		}
		return aggvo;
	}
	
	@SuppressWarnings("unchecked")
	public AggCostShareVO[] queryBillByWhere(String sqlWhere)
			throws BusinessException {
		List<AggCostShareVO> vos = (List<AggCostShareVO>) MDPersistenceService.lookupPersistenceQueryService().
		queryBillOfVOByCond(AggCostShareVO.class, sqlWhere, false);
		if (vos != null) {
			return (AggCostShareVO[]) vos.toArray(new AggCostShareVO[0]);
		}
		return new AggCostShareVO[0];
	}
	
	public CostShareVO queryCShareVOByBxVoHead(BXHeaderVO header, UFBoolean from) throws BusinessException {
		String key = CostShareVO.SRC_ID;
		try {
			StringBuffer sql = new StringBuffer(SqlUtils.getInStr(key, new BXHeaderVO[]{header}, BXHeaderVO.PK_JKBX)+ " and dr=0 ");
			
			if(from != null){
				if(from.equals(UFBoolean.TRUE)){
					sql.append(" and src_type = " + IErmCostShareConst.CostShare_Bill_SCRTYPE_BX + " ");
				}else{
					sql.append(" and src_type = " + IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL + " ");
				}
			}
			CostShareVO[] result = (CostShareVO[])getService().queryByCondition(CostShareVO.class, sql.toString());
			
			if(result == null || result.length == 0){
				return null;
			}else{
				return result[0];
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	
	public CShareDetailVO[] queryCShareDetailVOSByBxVoHead(BXHeaderVO header) throws BusinessException {
		String key = CostShareVO.SRC_ID;
		try {
			return (CShareDetailVO[])getService().queryByCondition(CShareDetailVO.class,SqlUtils.getInStr(key, new BXHeaderVO[]{header}, BXHeaderVO.PK_JKBX) + 
					" and " + CShareDetailVO.SRC_TYPE  + "=" + IErmCostShareConst.CostShare_Bill_SCRTYPE_BX + 
					" and dr=0 ");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public AggCostShareVO[] queryBillByPKs(String[] pks)
			throws BusinessException {
		String condition = null;
		AggCostShareVO[] aggvo = null;
		try {
			condition = SqlUtils.getInStr(CostShareVO.PK_COSTSHARE, pks, false);
			aggvo = queryBillByWhere(condition);
			sortAggCostShare(aggvo);
		} catch (SQLException e) {
			Logger.error("根据pk批量查询费用结转单失败",e);
			throw new BusinessException(e);
		}
		if(aggvo != null && aggvo.length > 0){
			// 补充扩展子表信息
			String pk_group = ((CostShareVO)aggvo[0].getParentVO()).getPk_group();
			ErmExtendconfigInterfaceCenter.fillExtendTabVOs(pk_group,
					CostShareVO.PK_TRADETYPE,aggvo);
		}
		
		return aggvo;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sortAggCostShare(AggCostShareVO[] aggvo) {
		Arrays.sort(aggvo, new Comparator(){
			public int compare(Object o1, Object o2) {
				String billno1 = ((CostShareVO) ((AggCostShareVO) o1).getParentVO()).getBillno();
				String billno2 = ((CostShareVO) ((AggCostShareVO) o2).getParentVO()).getBillno();
				return billno2.compareTo(billno1);
			}
			
		});
	}

	public CostShareVO queryCShareVOByBxVoHead(JKBXHeaderVO header, UFBoolean from) throws BusinessException {
		String key = CostShareVO.SRC_ID;
		try {
			StringBuffer sql = new StringBuffer(SqlUtils.getInStr(key, new JKBXHeaderVO[]{header}, BXHeaderVO.PK_JKBX)+ " and dr=0 ");
			
			if(from != null){
				if(from.equals(UFBoolean.TRUE)){
					sql.append(" and src_type = " + IErmCostShareConst.CostShare_Bill_SCRTYPE_BX + " ");
				}else{
					sql.append(" and src_type = " + IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL + " ");
				}
			}
			CostShareVO[] result = (CostShareVO[])getService().queryByCondition(CostShareVO.class, sql.toString());
			
			if(result == null || result.length == 0){
				return null;
			}else{
				return result[0];
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CostShareVO[] getShareByMthPk(String pkOrg, String begindate,
			String enddate) throws BusinessException {
		String sql = "pk_org=? and billdate>=? and billdate<=? and dr = 0 ";
		SQLParameter param = new SQLParameter();
		param.addParam(pkOrg);
		param.addParam(begindate);
		param.addParam(enddate);
		Collection<CostShareVO> c = new BaseDAO().retrieveByClause(
				CostShareVO.class, sql, param);
		if (c == null || c.isEmpty()) {
			return null;
		}
		return c.toArray(new CostShareVO[] {});
	}
}
