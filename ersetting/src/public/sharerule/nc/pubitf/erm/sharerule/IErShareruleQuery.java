package nc.pubitf.erm.sharerule;

import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;

/**
 * ��̯�����ѯ
 * 
 * @author lvhj
 *
 */
public interface IErShareruleQuery {

	/**
	 * ������֯��ѯ��vo
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public ShareruleVO[] queryByOrg(String pk_group,String pk_org) throws BusinessException;
	
	
	/**
	 * ����PK��ѯ
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO queryByPK(String pk) throws BusinessException;

	/**
	 * ����pkGroup,pkOrg��ѯ�ۺ�vo
	 * 
	 * @param pkGroup
	 * @param pkOrg
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO[] queryByOrgAndGroup(String pkGroup, String pkOrg) throws BusinessException; 
}
