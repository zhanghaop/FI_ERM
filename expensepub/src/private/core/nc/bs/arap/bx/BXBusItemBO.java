package nc.bs.arap.bx;

import java.sql.SQLException;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtils;
import nc.bs.logging.Log;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * nc.bs.ep.bx.BXBusItemBO
 * 
 * @author twei
 * 
 * 报销单据表体业务类
 */
public class BXBusItemBO implements IBXBusItemBO {

	private BXBusItemDAO bxBusItemDAO;

	public BXBusItemDAO getBxBusItemDAO() throws SQLException {
		try {
			if (null == bxBusItemDAO) {
				bxBusItemDAO = new BXBusItemDAO();
			}
		} catch (NamingException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new SQLException(e.getMessage());
		}
		return bxBusItemDAO;
	}

	public void setBxBusItemDMO(BXBusItemDAO bxBusItemDAO) {
		this.bxBusItemDAO = bxBusItemDAO;
	}

	public BXBusItemVO[] save(BXBusItemVO[] items) throws BusinessException {
		try {
			return (BXBusItemVO[]) getBxBusItemDAO().save(items);
		} catch (SQLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	public BXBusItemVO[] update(BXBusItemVO[] items) throws BusinessException {
		try {
			return (BXBusItemVO[]) getBxBusItemDAO().update(items);
		} catch (SQLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	public void deleteByBXVOs(JKBXVO[] headers) throws BusinessException {
		//BXBusItemVO[] itemVOs = queryByBXVOs(headers);
		//deleteVOs(itemVOs);
		String[] pks = getVOPks(headers);
		
		try {
			String whereSql = SqlUtils.getInStr(BXBusItemVO.PK_JKBX,pks);
			new BaseDAO().executeUpdate(" update er_busitem set dr=1 where " + whereSql);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
	}

	public void deleteVOs(BXBusItemVO[] itemVOs) throws BusinessException {
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i].setDr(1);
		}
		try {
			getBxBusItemDAO().update(itemVOs, new String[] { "dr" });
		} catch (SQLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	public BXBusItemVO[] queryByBXVOs(JKBXVO[] vos) throws BusinessException {

		String[] pks = getVOPks(vos);
		try {
			return getBxBusItemDAO().queryByBXVOPks(pks);
		} catch (SQLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

	}

	public BXBusItemVO[] queryByHeaders(JKBXHeaderVO[] headerVOs) throws BusinessException {

		String[] pks = getVOPks(headerVOs);
		try {
			return getBxBusItemDAO().queryByBXVOPks(pks);
		} catch (SQLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	private String[] getVOPks(JKBXVO[] vos) {
		String[] pks = new String[vos.length];
		for (int i = 0; i < vos.length; i++) {
			pks[i] = vos[i].getParentVO().getPrimaryKey();
		}
		return pks;
	}

	private String[] getVOPks(SuperVO[] vos) {
		String[] pks = new String[vos.length];
		for (int i = 0; i < vos.length; i++) {
			pks[i] = vos[i].getPrimaryKey();
		}
		return pks;
	}

}
