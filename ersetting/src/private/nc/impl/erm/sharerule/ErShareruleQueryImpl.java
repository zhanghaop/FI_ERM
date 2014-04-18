package nc.impl.erm.sharerule;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.sharerule.IErShareruleQuery;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;

/**
 * 分摊规则查询服务实现类
 * 
 * @author shengqy
 * 
 */
public class ErShareruleQueryImpl implements IErShareruleQuery {

	/**
	 * 根据pkGroup,pkOrg查询主表信息
	 */
	@Override
	public ShareruleVO[] queryByOrg(String pkGroup, String pkOrg) throws BusinessException {
		if (pkGroup == null || pkOrg == null) {
			return null;
		}
		BaseDAO dao = new BaseDAO();
		String sql = "select * from er_sharerule where pk_org='" + pkOrg + "' and pk_group='" + pkGroup + "'";
		@SuppressWarnings("unchecked")
		Collection<ShareruleVO> result = (Collection<ShareruleVO>) dao.executeQuery(sql,
				new BeanListProcessor(ShareruleVO.class));
		return result.toArray(new ShareruleVO[result.size()]);
	}

	/**
	 * 根据pk查询
	 */
	@Override
	public AggshareruleVO queryByPK(String pk) throws BusinessException {
		IMDPersistenceQueryService qryservice = MDPersistenceService.lookupPersistenceQueryService();
		return qryservice.queryBillOfVOByPK(AggshareruleVO.class, pk, false);
	}

	/**
	 * 根据pkGroup,pkOrg查询聚合vo
	 * 
	 * @param pkGroup
	 * @param pkOrg
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public AggshareruleVO[] queryByOrgAndGroup(String pkGroup, String pkOrg) throws BusinessException {
		if (pkGroup == null || pkOrg == null) {
			return null;
		}
		String whereCondStr = "( pk_group='" + pkGroup + "' and pk_org='" + pkOrg + "' )";
		IMDPersistenceQueryService qryservice = MDPersistenceService.lookupPersistenceQueryService();
		Collection col = qryservice.queryBillOfVOByCond(AggshareruleVO.class, whereCondStr, false);
		return (AggshareruleVO[]) col.toArray(new AggshareruleVO[]{});
	}
}
