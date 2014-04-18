package nc.impl.er.indauthorize;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.indauthorize.IIndAuthorizeQueryService;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.pub.BusinessException;


public class IndAuthorizeQueryServiceImpl implements
		IIndAuthorizeQueryService {
	/**
	 * @author liansg
	 */
	@Override
	public IndAuthorizeVO[] queryIndAuthorizes(String whereCond)
			throws BusinessException {
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				IndAuthorizeVO.class, whereCond, false);
		if (ncobjects == null) {
			return new IndAuthorizeVO[0];
		}
		IndAuthorizeVO[] rvtVOs = new IndAuthorizeVO[ncobjects.length];
		for (int i = 0; i < rvtVOs.length; i++) {
			rvtVOs[i] = (IndAuthorizeVO) ncobjects[i].getContainmentObject();
		}
		return rvtVOs;
	}

	/**
	 * @author chendya
	 * @return ���ص�¼�û�����������Ա������ֵ1������Ա���ڵĲ��ţ�����ֵ2����������֯(����ֵ3),��������(����ֵ4)
	 * @param userid
	 *            �û�id
	 * @param pk_group
	 *            ��ǰ��¼����
	 */
	public String[] queryPsnidAndDeptid(String cuserid, String pk_group) throws BusinessException {
		String[] result = new String[4];
		String jkbxr = NCLocator.getInstance().lookup(IUserPubService.class).queryPsndocByUserid(cuserid);
		result[0] = jkbxr;
		if (jkbxr != null) {
			IPsndocPubService pd = NCLocator.getInstance().lookup(IPsndocPubService.class);
			PsndocVO[] pm = pd.queryPsndocByPks(new String[] { jkbxr }, new String[] { PsndocVO.PK_ORG, PsndocVO.PK_GROUP });
			result[1] = pd.queryMainDeptByPandocIDs(jkbxr).get("pk_dept");
			result[2] = pm[0].getPk_org();
			result[3] = pm[0].getPk_group();
		}
		return result;
	}
}
