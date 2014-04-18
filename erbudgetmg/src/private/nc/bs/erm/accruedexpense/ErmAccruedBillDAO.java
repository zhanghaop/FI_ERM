package nc.bs.erm.accruedexpense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.ErMdpersistUtil;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;

public class ErmAccruedBillDAO {
	private IMDPersistenceService service;
	private IMDPersistenceQueryService qryservice;
	private BaseDAO basedao;

	public IMDPersistenceService getService() {
		if (service == null) {
			service = MDPersistenceService.lookupPersistenceService();
		}
		return service;
	}

	public IMDPersistenceQueryService getQryservice() {
		if (qryservice == null) {
			qryservice = MDPersistenceService.lookupPersistenceQueryService();
		}
		return qryservice;
	}

	public BaseDAO getBaseDAO() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}

	public AggAccruedBillVO insertVO(AggAccruedBillVO vo) throws MetaDataException {
		if(vo == null){
			return null;
		}
		setBodyRowNo(vo);
		getService().saveBill(vo);
		return vo;
	}
	
	public void deleteVOs(AggAccruedBillVO[] vos) throws MetaDataException  {
		if(vos == null || vos.length == 0){
			return;
		}
		NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
		getService().deleteBillFromDB(ncobjs);
	}


	public AggAccruedBillVO updateVO(AggAccruedBillVO vo) throws MetaDataException{
		setBodyRowNo(vo);
		String pk = getService().saveBillWithRealDelete(vo);
		AggAccruedBillVO newvo = getQryservice().queryBillOfVOByPK(AggAccruedBillVO.class, pk, false);
		if(newvo != null){//补齐计算属性
			newvo.getParentVO().setHasntbcheck(vo.getParentVO().getHasntbcheck());
		}
		return vo;
	}

	public AccruedDetailVO[] queryAccruedDetailsByHeadPk(String headpk) throws BusinessException {
		// 数据库字段更新主表
		
		@SuppressWarnings("unchecked")
		Collection<AccruedDetailVO> result = getBaseDAO().retrieveByClause(AccruedDetailVO.class,
				AccruedDetailVO.PK_ACCRUED_BILL + " = '" + headpk + "' ");
		
		if(result != null){
			return result.toArray(new AccruedDetailVO[]{});
		}
		
		return null;
	}
	
	/**
	 * 按字段更新vo
	 * 
	 * @param headvos
	 * @param fields
	 * @throws BusinessException
	 */
	public void updateAggVOsByHeadFields(AggAccruedBillVO[] aggvos, String[] headfields) throws BusinessException {
		List<AccruedVO> headlist = new ArrayList<AccruedVO>();
		for (AggAccruedBillVO aggvo : aggvos) {
			headlist.add((AccruedVO) aggvo.getParentVO());
		}
		updateVOsByFields(headlist.toArray(new AccruedVO[headlist.size()]), headfields);
	}
	
	/**
	 * 按字段更新vo
	 * 
	 * @param headvos
	 * @param fields
	 * @throws BusinessException
	 */
	public void updateVOsByFields(SuperVO[] vos, String[] headfields) throws BusinessException {
		// 数据库字段更新主表
		if(vos != null && vos.length > 0){
			getBaseDAO().updateVOArray(vos, headfields);
			addTsToVOs(vos);
		}
	}
	
	/**
	 * 为VO补齐TS
	 * 
	 * @param array
	 * @throws DAOException
	 */
	@SuppressWarnings("deprecation")
	public void addTsToVOs(SuperVO[] vos) throws DAOException {
		if (vos == null || vos.length == 0)
			return;
		List<String> pks = new ArrayList<String>();
		for (int i = 0; i < vos.length; i++) {
			pks.add(vos[i].getPrimaryKey());
		}
		String tablename = vos[0].getTableName();
		String pkfield = vos[0].getPKFieldName();
		try {
			Map<String, UFDateTime> tsMap = getTsMap(pks, tablename, pkfield);

			for (int i = 0; i < vos.length; i++) {
				vos[i].setAttributeValue("ts", tsMap.get(vos[i].getPrimaryKey()));
			}

		} catch (DbException e) {
			ExceptionHandler.consume(e);
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, UFDateTime> getTsMap(List<String> pkAry, String tablename, String pkfield) throws DbException {
		Map<String, UFDateTime> newTsMap = new HashMap<String, UFDateTime>();
		String[] strTemp = getTsSqlStr(pkAry, tablename, pkfield);
		for (int i = 0; i < strTemp.length; i++) {
			List lResult = getTsChanged(strTemp[i]);
			for (Iterator iter = lResult.iterator(); iter.hasNext();) {
				Object[] objs = (Object[]) iter.next();
				newTsMap.put(objs[0].toString(), new UFDateTime(objs[1].toString()));
			}
		}
		return newTsMap;
	}
	
	private String[] getTsSqlStr(List<String> pkAry, String tableName, String fieldName) {

		String[] strTemp = getSplitSqlIn(pkAry);

		for (int i = 0; i < strTemp.length; i++) {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select ");
			sqlBuffer.append(fieldName);
			sqlBuffer.append(",ts from ");
			sqlBuffer.append(tableName);
			sqlBuffer.append(" where ");
			sqlBuffer.append(fieldName);
			sqlBuffer.append(" in ");
			sqlBuffer.append(strTemp[i]);
			strTemp[i] = sqlBuffer.toString();
		}
		return strTemp;
	}
	
	private static String[] getSplitSqlIn(List<String> pkAry) {
		/** -------------------增加了拆分功能----------------- */
		int PACKAGESIZE = 400;
		int fixedPKLength = pkAry.size();
		int numofPackage = fixedPKLength / PACKAGESIZE + (fixedPKLength % PACKAGESIZE > 0 ? 1 : 0);
		/** ------------------------------------------------- */

		/** 根据包的个数组成SQL数组 */
		String[] strTemp = new String[numofPackage];

		/** 根据包的个数循环构建numofPackage个SQL数组 */
		for (int s = 0; s < numofPackage; s++) {
			/** 确定起始点和中止点 */
			int beginIndex = s * PACKAGESIZE;
			int endindex = beginIndex + PACKAGESIZE;
			/** 长度超出后进行微调 */
			if (endindex > fixedPKLength)
				endindex = fixedPKLength;

            StringBuilder sbArys = new StringBuilder("(");
			for (int i = beginIndex; i < endindex; i++) {
			    sbArys.append("'").append(pkAry.get(i)).append("',");
			}
			sbArys.deleteCharAt(sbArys.length() - 1);
            sbArys.append(")");
			strTemp[s] = sbArys.toString();
		}
		return strTemp;
	}
	
	@SuppressWarnings("unchecked")
	private List getTsChanged(String strSql) throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			List lRet = (List) jdbc.executeQuery(strSql, new ArrayListProcessor());
			return lRet;
		} finally {
			if (persist != null)
				persist.release();
		}
	}
	
	public Map<String, List<AccruedDetailVO>> queryAccruedDetailsByHeadPks(String[] headpks) throws BusinessException {
	    String sWhere = SqlUtils.getInStr(AccruedDetailVO.PK_ACCRUED_BILL, headpks, true);
        Map<String, List<AccruedDetailVO>> map = new HashMap<String, List<AccruedDetailVO>>();
        @SuppressWarnings("unchecked")
        Collection<AccruedDetailVO> result = getBaseDAO().retrieveByClause(AccruedDetailVO.class, sWhere);
        Iterator<AccruedDetailVO> itera = result.iterator();
        while (itera.hasNext()) {
        	AccruedDetailVO vo = itera.next();
            List<AccruedDetailVO> list = map.get(vo.getPk_accrued_bill());
            if (list == null) {
                list = new LinkedList<AccruedDetailVO>();
                map.put(vo.getPk_accrued_bill(), list);
            }
            list.add(vo);
        }
	    return map;
	}
	
	public Map<String, List<AccruedVerifyVO>> queryAccruedVerifiesByHeadPks(String[] headpks) throws BusinessException {
	    String sWhere = SqlUtils.getInStr(AccruedVerifyVO.PK_ACCRUED_BILL, headpks, true);
        Map<String, List<AccruedVerifyVO>> map = new HashMap<String, List<AccruedVerifyVO>>();
        @SuppressWarnings("unchecked")
        Collection<AccruedVerifyVO> result = getBaseDAO().retrieveByClause(AccruedVerifyVO.class, sWhere);
        Iterator<AccruedVerifyVO> itera = result.iterator();
        while (itera.hasNext()) {
        	AccruedVerifyVO vo = itera.next();
            List<AccruedVerifyVO> list = map.get(vo.getPk_accrued_bill());
            if (list == null) {
                list = new LinkedList<AccruedVerifyVO>();
                map.put(vo.getPk_accrued_bill(), list);
            }
            list.add(vo);
        }
	    return map;
	}
	
	private void setBodyRowNo(AggAccruedBillVO vo) {
		if (vo.getChildrenVO() != null) {
			AccruedDetailVO[] bodyvos = (AccruedDetailVO[]) vo.getChildrenVO();
			int rowno = 0;
			for (AccruedDetailVO bodyvo : bodyvos) {
				if (bodyvo.getStatus() != VOStatus.DELETED) {
					bodyvo.setRowno(++rowno);
				}
			}
		}
	}

}
