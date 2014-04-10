package nc.pubitf.erm.erminit;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.erminit.ErminitCloseBO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.erm.erminit.ErminitVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class ErminitQueryServiceImpl implements IErminitQueryService {
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BxcontrastVO> getBxcontrastVO(String keys)throws BusinessException {
			String wheresql="pk_jkd='" +keys+ "'";
			Collection c = new BaseDAO().retrieveByClause(BxcontrastVO.class, wheresql);
			
			if (c==null||c.isEmpty()) {
				return null;
			}
			return (List<BxcontrastVO>)c;
	}

	@Override
	public ErminitVO queryByOrg(String pkOrg) throws BusinessException {
		return null;
	}

	@Override
	public boolean queryStatusByOrg(String pkOrg) throws BusinessException {
		ErminitVO erminiVO = ErminitCloseBO.getErminitVO(pkOrg);
		if (erminiVO != null
				&& erminiVO.getClose_status().equals(UFBoolean.TRUE)) {
			return true;
		}
		return false;
	}

	@Override
	public List<CloseAccBookVO> queryAccStatusByOrg(String pkOrg) throws BusinessException {
		String whereContion = "pk_org='" + pkOrg + "'and moduleid='2011'";
		@SuppressWarnings("unchecked")
        Collection<CloseAccBookVO> c = new BaseDAO().retrieveByClause(
				CloseAccBookVO.class, whereContion);
		if (c==null||c.isEmpty()) {
			return null;
		}
		return (List<CloseAccBookVO>)c;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String[] queryStatusByOrgs(String[] pkOrg)
			throws BusinessException {
		try {
			String inStr = SqlUtils.getInStr(ErminitVO.PK_ORG, pkOrg);
			inStr += " and close_status='Y' ";
			List<ErminitVO> erinitvoList = (List<ErminitVO>) new BaseDAO()
					.retrieveByClause(ErminitVO.class, inStr,
							new String[] { ErminitVO.PK_ORG });
			if (erinitvoList.size() != 0) {
				return VOUtils.getAttributeValues(erinitvoList.toArray(new ErminitVO[0]), ErminitVO.PK_ORG);
			}
		} catch (SQLException e) {
			 
		}

		return null;
	}

}
