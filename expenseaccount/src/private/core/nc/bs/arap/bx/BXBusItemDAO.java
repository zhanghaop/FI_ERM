package nc.bs.arap.bx;

import java.sql.SQLException;
import java.util.Collection;

import javax.naming.NamingException;

import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBusItemVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 *         nc.bs.arap.bx.BXBusItemDAO
 * 
 *         报销单据表体数据读取类
 * 
 */
public class BXBusItemDAO extends BXSuperDAO {
	public BXBusItemDAO() throws NamingException {
		super();
	}

	@SuppressWarnings("unchecked")
	public BXBusItemVO[] queryByBXVOPks(String[] pks,boolean isQueryJK) throws DAOException, SQLException, MetaDataException {
		String sql = SqlUtils.getInStr(BXBusItemVO.PK_JKBX, pks);
		sql += " and dr=0 order by rowno ";
		Collection<BXBusItemVO> result = null;
		if(isQueryJK){
			result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
					JKBusItemVO.class, sql, false);
		}else{
			result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
					BXBusItemVO.class, sql, false);
		}
		
		if (result != null) {
			return result.toArray(new BXBusItemVO[] {});
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	public BXBusItemVO[] queryByPks(String[] pks,boolean isQueryJK) throws BusinessException {
		Collection<BXBusItemVO> result = null;
		try {
			if(isQueryJK){
				result =  MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(JKBusItemVO.class, pks,
						false);
			}else{
				result =  MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(BXBusItemVO.class, pks,
						false);
			}
		} catch (MetaDataException e) {
			ExceptionHandler.handleException(e);
		}

		if (result != null) {
			return result.toArray(new BXBusItemVO[] {});
		}
		return null;
	}
}
