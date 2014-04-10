package nc.bs.erm.erminit;

import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.ErMdpersistUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.erm.erminit.ErminitVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.util.AuditInfoUtil;

public class ErminitCloseBO {

	public boolean close(String pkOrg) throws BusinessException {
		ErminitVO erinitvo = getErminitVO(pkOrg);
		if (erinitvo != null) {
			if(erinitvo.getClose_status().equals(UFBoolean.TRUE)){
				//已经是关闭状态了，就不可以重复关闭了
				return false;
			}
			erinitvo.setClose_status(UFBoolean.TRUE);
			erinitvo.setCloseman(getBSLoginUser());
			erinitvo.setClosedate(new UFDate(getBizDateTime()));
			AuditInfoUtil.updateData(erinitvo);
			String[] filtAttrNames = { "close_status", "modifier",
					"modifiedtime", "closeman", "closedate" };
			updateErminiVO(erinitvo, filtAttrNames);

		} else {
			ErminitVO erminitVO = new ErminitVO();
			erminitVO.setStatus(VOStatus.NEW);
			erminitVO.setPk_org(pkOrg);
			erminitVO.setClose_status(UFBoolean.TRUE);
			erminitVO.setPk_group(getGroupId());
			erminitVO.setCloseman(getBSLoginUser());
			erminitVO.setClosedate(new UFDate(getBizDateTime()));
			AuditInfoUtil.addData(erminitVO); // 审计信息
			MDPersistenceService.lookupPersistenceService().saveBill(erminitVO);
		}
		return true;
	}

	public boolean unclose(String pkOrg) throws BusinessException {
		ErminitVO erinitvo = getErminitVO(pkOrg);
		if (erinitvo == null)
			return true;
		else{
			if(erinitvo.getClose_status().equals(UFBoolean.FALSE)){
				return false;
			}
			erinitvo.setClose_status(UFBoolean.FALSE);
			erinitvo.setUncloseman(getBSLoginUser());
			erinitvo.setUnclosedate(new UFDate(getBizDateTime()));
			AuditInfoUtil.updateData(erinitvo);
			String[] filtAttrNames = { "close_status", "modifier", "modifiedtime",
					"uncloseman", "unclosedate" };
			updateErminiVO(erinitvo, filtAttrNames);
		}
		return true;
	}

	public void updateErminiVO(ErminitVO vo, String[] filtAttrNames)
			throws BusinessException {
		NCObject ncobjs = ErMdpersistUtil.getNCObject(vo);
		MDPersistenceService.lookupPersistenceService().updateBillWithAttrs(
				new NCObject[] { ncobjs }, filtAttrNames);
	}

	@SuppressWarnings("unchecked")
	public static ErminitVO getErminitVO(String pkOrg) throws DAOException {
		String whereContion = "pk_org='" + pkOrg + "'";
        List<ErminitVO> erinitvoList = (List<ErminitVO>) new BaseDAO().retrieveByClause(
				ErminitVO.class, whereContion);
		if (erinitvoList.size() == 0) {
			return null;
		}
		return (ErminitVO) erinitvoList.get(0);
	}

	private static String getBSLoginUser() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	private static String getGroupId() {
		return InvocationInfoProxy.getInstance().getGroupId();
	}

	private static long getBizDateTime() {
		return InvocationInfoProxy.getInstance().getBizDateTime();
	}
}
