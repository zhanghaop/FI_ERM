package nc.impl.erm.billcontrast;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.erm.billcontrast.IErmBillcontrastQuery;
import nc.itf.org.IOrgConst;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.billcontrast.IErmBillcontrastQueryService;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class ErmBillcontrastQueryImpl implements IErmBillcontrastQuery,
		IErmBillcontrastQueryService {
	/**
	 * 组织如果有对应关系优先取组织的，如果没有对应取集团预置的数据
	 */
	@Override
	public String queryDesTradetypeBySrc(String pk_org, String src_tradetype)
			throws BusinessException {
		List<BillcontrastVO> list = new ArrayList<BillcontrastVO>();
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		
		String pk_group =InvocationInfoProxy.getInstance().getGroupId();
		String whereCond = "pk_org in('" + pk_org + "','" + pk_group
				+ "')  and src_tradetype='" + src_tradetype + "'";
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				BillcontrastVO.class, whereCond, false);
		if (ncobjects == null) {
			return null;
		} else {
			if (ncobjects.length != 1) {
				for (NCObject ob : ncobjects) {
					BillcontrastVO billcontrastVO = (BillcontrastVO) ob
							.getContainmentObject();
					if (!billcontrastVO.getAttributeValue("pk_org").equals(
							billcontrastVO.getAttributeValue("pk_group"))) {
						list.add(billcontrastVO);
					}
				}
			} else {
				list.add((BillcontrastVO) ncobjects[0].getContainmentObject());
			}
		}
		return list.get(0).getDes_tradetype();
	}

	@Override
	public BillcontrastVO[] queryAllByOrg(String pk_org,String pk_group,LoginContext context)
			throws BusinessException {
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		// 将集团和组织都查询出来
//		String MDID="f495d9be-96af-41b6-827f-e10a45e58512";
//		String whereCond = VisibleUtil.getVisibleCondition(context,MDID);
		String whereCond = "pk_org = '" + pk_org + "'or pk_org='"
				+ pk_group + "' order by pk_org";
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				BillcontrastVO.class, whereCond, false);
		if (ncobjects == null) {
			return new BillcontrastVO[0];
		}
		BillcontrastVO[] rvtVOs = new BillcontrastVO[ncobjects.length];
		for (int i = 0; i < rvtVOs.length; i++) {
			rvtVOs[i] = (BillcontrastVO) ncobjects[i].getContainmentObject();
		}
		return rvtVOs;

	}

	@Override
	public BillcontrastVO[] queryVOsByWhere(String pk_org, String where)
			throws BusinessException {
		return null;
	}

	@Override
	public BillcontrastVO[] queryAllByGloble() throws BusinessException {
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		String whereCond = "pk_org = '" + IOrgConst.GLOBEORG + "'";
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				BillcontrastVO.class, whereCond, false);
		if (ncobjects == null) {
			return new BillcontrastVO[0];
		}
		BillcontrastVO[] rvtVOs = new BillcontrastVO[ncobjects.length];
		for (int i = 0; i < rvtVOs.length; i++) {
			rvtVOs[i] = (BillcontrastVO) ncobjects[i].getContainmentObject();
		}
		return rvtVOs;
	}

}
