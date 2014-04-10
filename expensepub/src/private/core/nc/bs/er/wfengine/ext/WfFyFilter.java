package nc.bs.er.wfengine.ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pub.pf.IParticipantFilter;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.itf.uap.pf.IPFOrgUnit;
import nc.itf.uap.rbac.IRoleManageQuery;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.org.DeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.OrganizeUnit;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.uap.pf.PFRuntimeException;
import nc.vo.uap.rbac.UserRoleVO;

/**
 * �����������ߡ������������óе���
 * nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilter implements IParticipantFilter{

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		HashSet<String> hsId = new HashSet<String>();
		AggregatedValueObject billvo = (AggregatedValueObject) pfc.getBillEntity();

		//::��õ��������ϵķ�����֧��˾��
		String strLiezhiCorp = (String) billvo.getParentVO().getAttributeValue("fydwbm");
		//::��õ����ӱ��ϵķ�����֧���ţ�
		String strLieZhiDept = (String) billvo.getParentVO().getAttributeValue("fydeptid");
		//::��õ��������ϵı������ţ�
		String strBaoxiaoDept = (String) billvo.getParentVO().getAttributeValue("deptid");

		if(strLieZhiDept==null || strLiezhiCorp==null)
			return null;

		//�����֧��˾Ϊ�գ�����֧��˾=��ǰ��˾
		//������֧���Ÿ����˶�Ӧ���û�
		List<String> units = findDeptMgrOfDept(strLieZhiDept);

		//�������ڸ���֯�µ������û������ɵ�¼��ǰ��˾��
		HashSet<String> hsTemp = new HashSet<String>();
		findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsTemp, strLiezhiCorp);

		//�ж���֧���Ÿ������Ƿ����ڸ���֯
		boolean bIncluded = false;
		for (Iterator iter = hsTemp.iterator(); iter.hasNext();) {
			String id = (String) iter.next();
			if (units.contains(id)) {
				bIncluded = true;
				hsId.add(id);
			}
		}
		if (!bIncluded && !pfc.isForDispatch())
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000233")/*@res "��̬�޶�������֧���Ÿ����˲����ڵ�ǰ��ɫ"*/);

		return hsId;
	}

	/**
	 * �������ڸ���֯�µ������û������ɵ�¼ĳ��˾
	 *
	 * @param participantId
	 * @param participantType
	 * @param hsId
	 * @param strCorp
	 * @throws BusinessException
	 */
	private void findUserOfOrgunitByCorp(String participantId, String participantType,
			HashSet<String> hsId, String strCorp) throws BusinessException {
		if(participantType.equals("ROLE")){
			IRoleManageQuery ir=NCLocator.getInstance().lookup(IRoleManageQuery.class);
			UserRoleVO[] userroles = ir.queryUserRoleVOByRoleID(new String[]{participantId});
			for (int i = 0; i < userroles.length; i++) {
				if(!hsId.contains(userroles[i].getCuserid())){
					hsId.add(userroles[i].getCuserid());
				}
			}
		}else{
			hsId.add(participantId);
		}
	}

	public String getCode() {
		return "[U]Stakeholder";
	}

	/**
	 * ���Ҳ��Ÿ�������Ϣ
	 * @param deptdocPK ����PK
	 * @return {���Ÿ������û�PK;����VO}
	 * @throws DAOException
	 * @throws BusinessException
	 */
	protected List<String> findDeptMgrOfDept(String deptdocPK) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		//����PK�ҵ�����VO
		DeptVO deptdocVO = (DeptVO) dao.retrieveByPK(DeptVO.class, deptdocPK);
		if (deptdocVO == null)
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000237")/*@res "����PK�Ҳ�������VO"*/);

		String deptMgr = deptdocVO.getPrincipal();

		List<String> list=new ArrayList<String>();
		retrUser(deptMgr,list);

		return list;
	}

	private void retrUser(String deptMgr, List<String> list) throws PFBusinessException, DAOException, BusinessException {
		if (deptMgr == null || deptMgr.length() == 0)
			return;

		//�ҵ����Ÿ�����Ա��Ӧ���û�
		IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);

		UserVO clerkVOs = ucQry.queryUserVOByPsnDocID(deptMgr);
		
		if (clerkVOs != null){
			list.add(clerkVOs.getPrimaryKey());
		}
	}

}