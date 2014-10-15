package nc.bs.arap.bx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import nc.bs.logging.Log;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

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

	public void deleteByBXVOs(JKBXVO[] jkbxVos) throws BusinessException {
		try {
			if(jkbxVos[0].getChildrenVO() != null && jkbxVos[0].getChildrenVO().length != 0){
				
				MDPersistenceService.lookupPersistenceService().deleteBillFromDB(jkbxVos[0].getChildrenVO());
			}
		} catch (Exception e) {
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

	public BXBusItemVO[] queryByHeaders(JKBXHeaderVO[] headerVOs) throws BusinessException {
		try {
			List<BXBusItemVO> busitemVoList = new ArrayList<BXBusItemVO>();
			List<String> jkList = new ArrayList<String>();
			List<String> bxList = new ArrayList<String>();
			for(JKBXHeaderVO vo: headerVOs){
				if(BXConstans.JK_DJDL.equals(headerVOs[0].getDjdl())){
					jkList.add(vo.getPk_jkbx());
				}else{
					bxList.add(vo.getPk_jkbx());
				}
			}
			if(bxList.size()!=0){
				busitemVoList = Arrays.asList(getBxBusItemDAO().queryByBXVOPks(bxList.toArray(new String[0]),false));
			}
			if(jkList.size()!=0){
				busitemVoList.addAll(Arrays.asList(getBxBusItemDAO().queryByBXVOPks(jkList.toArray(new String[0]),true)));
			}

			return busitemVoList.toArray(new BXBusItemVO[0]);
		} catch (SQLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
}
