package nc.bs.er.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ErmBusinessException;
import nc.vo.er.exception.Log;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * ���/��������Ȩ��У�鹤��(ǰ��̨���ɵ���)
 * 
 * @author chendya@ufida.com.cn
 * 
 */
public class BXDataPermissionChkUtil {

	/**
	 * �Ƿ��ڳ����õ���
	 * 
	 * @param vo
	 */
	private static boolean isQcOrInitBill(JKBXVO vo) {
		return vo != null && vo.getParentVO() != null && (vo.getParentVO().getQcbz().booleanValue() || vo.getParentVO().isInit());
	}

	/**
	 * ����У������ά��Ȩ��
	 * 
	 * @author chendya
	 * @param vos
	 * @param resourceCode
	 * @param operationCode
	 * @param loginUser
	 * @throws BusinessException
	 */
	public static void process(JKBXVO[] vos, String resourceCode, String operationCode, String loginUser)
			throws BusinessException {

		// ��������
		final String option = getOptionName(operationCode);
		if (vos == null || vos.length == 0) {
			// û��ҪУ�������
			return;
		}
		// final String res_data_id[] = new String[vos.length];
		List<String> res_data_ids = new ArrayList<String>();
		// <����id�����ݺ�>
		Map<String, String> billCodeMap = new HashMap<String, String>();
		for (int i = 0; i < vos.length; i++) {
			// �ݴ棬�ڳ������õ��ݶ�����Ȩ��
			if (vos[i].getParentVO() == null || isQcOrInitBill(vos[i])
					|| vos[i].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				continue;
			}
			// res_data_id[i] = vos[i].getParentVO().getPrimaryKey();
			// if(res_data_id[i]==null||res_data_id[i].length()==0){
			// continue;
			// }
			// billCodeMap.put(res_data_id[i], vos[i].getParentVO().getDjbh());
			if (vos[i].getParentVO().getPrimaryKey() != null) {
				res_data_ids.add(vos[i].getParentVO().getPrimaryKey());
				billCodeMap.put(vos[i].getParentVO().getPrimaryKey(), vos[i].getParentVO().getDjbh());
			}
		}
		if (billCodeMap.size() == 0) {
			// û����ҪУ���
			return;
		}
		String pk_group = vos[0].getParentVO().getPk_group();
		String approver = vos[0].getParentVO().getApprover();
		StringBuilder error = new StringBuilder();

		Map<String, UFBoolean> rs = NCLocator.getInstance().lookup(IDataPermissionPubService.class)
				.isUserhasPermissionByMetaDataOperation(resourceCode, res_data_ids.toArray(new String[res_data_ids.size()]), operationCode, pk_group, loginUser);
		if (rs != null && rs.values() != null) {
			Iterator<Map.Entry<String, UFBoolean>> it = rs.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, UFBoolean> entry = it.next();
				if (!entry.getValue().booleanValue()) {
					// ��һ����ͨ���������쳣
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0151", null, new String[] { option, billCodeMap.get(entry.getKey()) })/*
																											 * @res
																											 * "��ǰ�û�û��{0}"
																											 * ����
																											 * [
																											 * {
																											 * 1
																											 * }
																											 * ]
																											 * ������Ȩ��
																											 * !
																											 * "
																											 */);
				}
			}
		}

		// ����˲�����������Ĵ���
		if (operationCode.equals(BXConstans.EXPUNAPPROVECODE) || operationCode.equals(BXConstans.LOANUNAPPROVECODE)) {
			boolean flag = NCLocator.getInstance().lookup(IDataPermissionPubService.class).isEnableApproverPerm(loginUser,
					resourceCode, pk_group);
			if (flag) {
				if (!loginUser.equals(approver)) {
					error.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0153")/*
																														 * @res
																														 * "�ü��������������Ȩ�ޣ�������˱����������!"
																														 */);
					ErmBusinessException ex = new ErmBusinessException(error.toString());
					Log.getInstance().error(ex);
					throw new BusinessException(ex.getMessage());
				}
			}
		}
	}

	/**
	 * У������ά��Ȩ��
	 * 
	 * @author chendya
	 * @param vo
	 * @param resourceCode
	 * @param operationCode
	 * @param loginUser
	 * @throws BusinessException
	 */
	public static void process(JKBXVO vo, String resourceCode, String operationCode, String loginUser) throws BusinessException {

		// ��������
		final String option = getOptionName(operationCode);
		if (vo == null || vo.getParentVO() == null || vo.getParentVO().getPrimaryKey() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0154")/*
																															 * @res
																															 * "��������Ϊ�գ��޷�У������Ȩ�� "
																															 */
					+ option + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0155")/*
																													 * @res
																													 * " ����ʧ��!"
																													 */);
		}
		JKBXHeaderVO headvo = vo.getParentVO();
		String pk_group = headvo.getPk_group();
		String approver = headvo.getApprover();
		StringBuilder error = new StringBuilder();

		boolean hasPermission = NCLocator.getInstance().lookup(IDataPermissionPubService.class)
				.isUserhasPermissionByMetaDataOperation(resourceCode, vo.getParentVO().getPrimaryKey(), operationCode, pk_group,
						loginUser);
		if (!hasPermission) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0151")/*
																															 * @res
																															 * "��ǰ�û�û��"
																															 */
					+ option + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0156")/*
																													 * @res
																													 * "����������Ȩ��!"
																													 */);
		}

		// ����˲�����������Ĵ���
		if (operationCode.equals(BXConstans.EXPUNAPPROVECODE) || operationCode.equals(BXConstans.LOANUNAPPROVECODE)) {
			boolean flag = NCLocator.getInstance().lookup(IDataPermissionPubService.class).isEnableApproverPerm(loginUser,
					resourceCode, pk_group);
			if (flag) {
				if (!loginUser.equals(approver)) {
					error.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0153")/*
																														 * @res
																														 * "�ü��������������Ȩ�ޣ�������˱����������!"
																														 */);
					ErmBusinessException ex = new ErmBusinessException(error.toString());
					Log.getInstance().error(ex);
					throw new BusinessException(ex.getMessage());
				}
			}
		}
	}

	/**
	 * ���ز�����������
	 * 
	 * @param action
	 * @return
	 * @throws BusinessException
	 */
	private static String getOptionName(String action) {
		if (action.equals(BXConstans.EXPQUERYOPTCODE) || action.equals(BXConstans.LOANQUERYOPTCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000006")/*
																									 * @res
																									 * "��ѯ"
																									 */;
		if (action.equals(BXConstans.EXPDELOPTCODE) || action.equals(BXConstans.LOANDELOPTCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000039")/*
																									 * @res
																									 * "ɾ��"
																									 */;
		if (action.equals(BXConstans.EXPEDITCODE) || action.equals(BXConstans.LOANEDITCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000045")/*
																									 * @res
																									 * "�޸�"
																									 */;
		if (action.equals(BXConstans.EXPAPPROVECODE) || action.equals(BXConstans.LOANAPPROVECODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000027")/*
																									 * @res
																									 * "���"
																									 */;
		if (action.equals(BXConstans.EXPUNAPPROVECODE) || action.equals(BXConstans.LOANUNAPPROVECODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0049")/*
																										 * @res
																										 * "�����"
																										 */;
		return "";
	}
}