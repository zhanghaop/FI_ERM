package nc.pubitf.erm.sharerule;

import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;

/**
 * 分摊规则查询
 * 
 * @author lvhj
 *
 */
public interface IErShareruleQuery {

	/**
	 * 根据组织查询主vo
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public ShareruleVO[] queryByOrg(String pk_group,String pk_org) throws BusinessException;
	
	
	/**
	 * 根据PK查询
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO queryByPK(String pk) throws BusinessException;

	/**
	 * 根据pkGroup,pkOrg查询聚合vo
	 * 
	 * @param pkGroup
	 * @param pkOrg
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO[] queryByOrgAndGroup(String pkGroup, String pkOrg) throws BusinessException; 
}
