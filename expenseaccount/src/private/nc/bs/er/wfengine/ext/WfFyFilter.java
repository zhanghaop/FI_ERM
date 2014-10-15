package nc.bs.er.wfengine.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.rbac.IUserPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * �����������ߡ������������óе����Ÿ����� nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilter extends ErmBaseParticipantFilter {
	
	public WfFyFilter() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000906")/*
																												 * @
																												 * res
																												 * "���ò��Ÿ�����"
																												 */;
	}
	
	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;

		HashSet<String> hsId = new HashSet<String>();

		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();
		Set<String> depts = new HashSet<String>();// ������֧����

		if (!ErmForCShareUtil.isHasCShare(billvo)) {// �޷��÷�̯ʱ,���汨��
			// ��õ����ӱ��ϵķ�����֧���ţ�
			depts.add((String) billvo.getParentVO().getAttributeValue(JKBXHeaderVO.FYDEPTID));
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();// ���÷�̯ҳǩ
			for (CShareDetailVO detailvo : detailvos) {
				if (detailvo.getAssume_dept() != null) {
					depts.add(detailvo.getAssume_dept());
				}
			}
		}

		if (depts.size() == 0 )
			return null;

		// ������֧���Ÿ����˶�Ӧ���û�
		List<String> deptUserList = findDeptMgrOfDept(depts.toArray(new String[depts.size()]));

		//�û�����
		HashSet<String> allUserList = getPfc().getUserList();
		if(allUserList == null){
			return null;
		}
		
		for (String deptUserId : deptUserList) {
			if (allUserList.contains(deptUserId)) {
				hsId.add(deptUserId);
			} else {
				//���쳣
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000910", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}�����ڵ�ǰ{1}��"
																												 */);
			}
		}

		return hsId;
	}

	public String getCode() {
		return "[U]Stakeholder";
	}

	/**
	 * ���Ҹ����Ÿ�������Ϣ
	 *
	 * @param deptdocPks
	 *            ������PKs
	 * @return {�����Ÿ������û�PK;����VO}
	 * @throws DAOException
	 * @throws BusinessException
	 */

	protected List<String> findDeptMgrOfDept(String[] deptPks)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> list = new ArrayList<String>();
		String where = SqlUtil.buildInSql(DeptVO.PK_DEPT, deptPks);
		@SuppressWarnings("rawtypes")
        Collection result = dao.retrieveByClause(DeptVO.class, where);
		if (result != null && result.size() > 0) {
		    for (Object obj : result.toArray()) {
		        DeptVO vo = (DeptVO)obj;
		        String deptMgrpsn = vo.getPrincipal();
	            retrUser(deptMgrpsn, list);
		    }
		} else if (result == null || deptPks.length != result.size()) {
		    throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl
                    .getNCLangRes().getStrByID("2011", "UPP2011-000237")/*
                                                                         * @res
                                                                         * "����PK�Ҳ�������VO"
                                                                         */);
		}
		return list;
	}

	private void retrUser(String deptMgr, List<String> list)
			throws PFBusinessException, DAOException, BusinessException {
		if (StringUtil.isEmpty(deptMgr)){
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0082")/*@res "δ�ҵ����óе����Ÿ�����"*/);
		}

		// �ҵ����Ÿ�����Ա��Ӧ���û�
		IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);

		UserVO clerkVOs = ucQry.queryUserVOByPsnDocID(deptMgr);

		if (clerkVOs != null) {
			list.add(clerkVOs.getPrimaryKey());
		}else{
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0082")/*@res "δ�ҵ����óе����Ÿ�����"*/);
		}
	}

	public ParticipantFilterContext getPfc() {
		return pfc;
	}

	public void setPfc(ParticipantFilterContext pfc) {
		this.pfc = pfc;
	}
}