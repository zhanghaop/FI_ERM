package nc.bs.erm.matterapp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.SqlUtils;
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
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;

/**
 * �����ת�����־û�
 * 
 * @author lvhj
 * 
 */
public class ErmMatterAppDAO {

	private IMDPersistenceService service;

	private IMDPersistenceService getService() {
		if (service == null) {
			service = MDPersistenceService.lookupPersistenceService();
		}
		return service;
	}

	private IMDPersistenceQueryService qryservice;

	private IMDPersistenceQueryService getQryService() {
		if (qryservice == null) {
			qryservice = MDPersistenceService.lookupPersistenceQueryService();
		}
		return qryservice;
	}

	private BaseDAO basedao;

	private BaseDAO getBaseDAO() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}

	public AggMatterAppVO insertVO(AggMatterAppVO vo) throws BusinessException {

		setRowNo(vo);
		try {
			String pk = getService().saveBill(vo);
			AggMatterAppVO savedVo = getQryService().queryBillOfVOByPK(AggMatterAppVO.class, pk, false);
			savedVo.getParentVO().setHasntbcheck(vo.getParentVO().getHasntbcheck());//����Ԥ�����
			return savedVo;
		} catch (MetaDataException e) {
			throw new BusinessException(e.getCause().getMessage(),e.getCause());
		}
	}
	
	/**
	 * �����к�
	 * 
	 * @param vo
	 */
	private void setRowNo(AggMatterAppVO vo) {
		MtAppDetailVO[] details = vo.getChildrenVO();

		if (details != null) {// �����к�
			int row = 0;
			for (int i = 0; i < details.length; i++) {
				if (details[i].getStatus() != VOStatus.DELETED) {
					row++;
					details[i].setRowno(row);
				}
			}
		}
	}

	public void deleteVOs(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
		try {
			getService().deleteBillFromDB(ncobjs);
		} catch (MetaDataException e) {
			throw new BusinessException(e.getCause().getMessage(), e.getCause());
		}
	}

	/**
	 * ��vo����
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException {
		setRowNo(vo);
		try {
			String pk = getService().saveBillWithRealDelete(vo);
			AggMatterAppVO savedVo = getQryService().queryBillOfVOByPK(AggMatterAppVO.class, pk, false);
			savedVo.getParentVO().setHasntbcheck(vo.getParentVO().getHasntbcheck());
			return savedVo;
		} catch (MetaDataException e) {
			throw new BusinessException(e.getCause().getMessage(), e.getCause());
		}
	}

	/**
	 * ���ֶθ���vo
	 * 
	 * @param headvos
	 * @param fields
	 * @throws BusinessException
	 */
	public void updateAggVOsByFields(AggMatterAppVO[] aggvos, String[] headfields, String[] detailfields) throws BusinessException {
		List<MatterAppVO> headlist = new ArrayList<MatterAppVO>();
		List<MtAppDetailVO> detaillist = new ArrayList<MtAppDetailVO>();
		boolean onlyHead = true;
		if (detailfields != null && detailfields.length > 0) {
			onlyHead = false;
		}
		
		for (AggMatterAppVO aggvo : aggvos) {
			headlist.add((MatterAppVO) aggvo.getParentVO());
			if (onlyHead) {
				break;
			}
			CircularlyAccessibleValueObject[] detailvos = aggvo.getChildrenVO();
			if (detailvos == null || detailvos.length == 0) {
				continue;
			}
			for (int i = 0; i < detailvos.length; i++) {
				detaillist.add((MtAppDetailVO) detailvos[i]);
			}
		}
		
		updateVOsByFields(headlist.toArray(new MatterAppVO[]{}), headfields);
		// ���ݿ��ֶθ����ӱ�
		if (!detaillist.isEmpty()) {
			updateVOsByFields(detaillist.toArray(new MtAppDetailVO[]{}), detailfields);
		}
	}
	
	/**
	 * ���ֶθ���vo
	 * 
	 * @param headvos
	 * @param fields
	 * @throws BusinessException
	 */
	public void updateVOsByFields(SuperVO[] vos, String[] headfields) throws BusinessException {
		// ���ݿ��ֶθ�������
		if(vos != null && vos.length > 0){
			getBaseDAO().updateVOArray(vos, headfields);
			addTsToVOs(vos);
		}
	}
	
	/**
	 * ���ֶθ���vo
	 * 
	 * @param headvos
	 * @param fields
	 * @throws BusinessException
	 */
	public MtAppDetailVO[] queryMtDetailsByPk(String mAppPk) throws BusinessException {
		// ���ݿ��ֶθ�������
		
		@SuppressWarnings("unchecked")
		Collection<MtAppDetailVO> result = getBaseDAO().retrieveByClause(MtAppDetailVO.class,
				MtAppDetailVO.PK_MTAPP_BILL + " = '" + mAppPk + "' ");
		
		if(result != null){
			return result.toArray(new MtAppDetailVO[]{});
		}
		
		return null;
	}
	
	public Map<String, List<MtAppDetailVO>> queryMtDetailsByPks(String[] mAppPk) throws BusinessException {
	    String sWhere;
	    try {
	        sWhere = SqlUtils.getInStr(MtAppDetailVO.PK_MTAPP_BILL, mAppPk, true);
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage(), e);
        }

        Map<String, List<MtAppDetailVO>> map = new HashMap<String, List<MtAppDetailVO>>();
        @SuppressWarnings("unchecked")
        Collection<MtAppDetailVO> result = getBaseDAO().retrieveByClause(MtAppDetailVO.class, sWhere);
        Iterator<MtAppDetailVO> itera = result.iterator();
        while (itera.hasNext()) {
            MtAppDetailVO vo = itera.next();
            List<MtAppDetailVO> list = map.get(vo.getPk_mtapp_bill());
            if (list == null) {
                list = new LinkedList<MtAppDetailVO>();
                map.put(vo.getPk_mtapp_bill(), list);
            }
            list.add(vo);
        }
	    return map;
	}

	/**
	 * ΪVO����TS
	 * 
	 * @param array
	 * @throws DAOException
	 */
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
		/** -------------------�����˲�ֹ���----------------- */
		int PACKAGESIZE = 400;
		int fixedPKLength = pkAry.size();
		int numofPackage = fixedPKLength / PACKAGESIZE + (fixedPKLength % PACKAGESIZE > 0 ? 1 : 0);
		/** ------------------------------------------------- */

		/** ���ݰ��ĸ������SQL���� */
		String[] strTemp = new String[numofPackage];

		/** ���ݰ��ĸ���ѭ������numofPackage��SQL���� */
		for (int s = 0; s < numofPackage; s++) {
			/** ȷ����ʼ�����ֹ�� */
			int beginIndex = s * PACKAGESIZE;
			int endindex = beginIndex + PACKAGESIZE;
			/** ���ȳ��������΢�� */
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
}
