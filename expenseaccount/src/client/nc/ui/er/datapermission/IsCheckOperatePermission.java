package nc.ui.er.datapermission;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ErmBusinessException;
import nc.vo.er.exception.Log;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.uap.busibean.exception.BusiBeanRuntimeException;

/**
 * <p>
 * �����Ƿ����ɾ��Ȩ�޴�����
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-09 ����06:19:35
 */
public class IsCheckOperatePermission {
	public void process(JKBXVO vo, String resourceCode, String operationCode) {
		try {
			// ע��������Դʵ����룬��Դʵ��id��������ʱ�иĶ�����ȷ���Ժ�Ķ�

			boolean value = false;
			JKBXHeaderVO headvo = vo.getParentVO();
			if (headvo == null || headvo.getPk() == null)
				value = false;
			List<String> pks = new ArrayList<String>();

			if (headvo != null && headvo.getPk() != null)
				pks.add(headvo.getPk());

			if (pks.size() == 0)
				value = false;
			

            String pk_group = null;
            String cuserid = BXUiUtil.getPk_user();
            String approver = null;
			if (headvo != null) {
	            pk_group = headvo.getPk_group();
	            approver = headvo.getApprover();
	            value = false;
			} else {
		         value = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(cuserid,resourceCode, operationCode, pk_group, vo);
			}

			StringBuilder error = new StringBuilder();
			String optionName = getOptionName(operationCode);
			if (!value) {
				error.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0046")/*@res "�������ݲ���Ȩ�޲�����ִ��("*/ + optionName + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0047")/*@res "����)����ȷ�ϣ�"*/);
				ErmBusinessException ex = new ErmBusinessException(error
						.toString());
				Log.getInstance().error(ex);
				throw new BusiBeanRuntimeException(ex.getMessage(), ex);
			}
			if (operationCode.equals(BXConstans.EXPUNAPPROVECODE)) {
				Boolean flag = false;
				try {
					flag = NCLocator.getInstance().lookup(IDataPermissionPubService.class).isEnableApproverPerm(cuserid, resourceCode,pk_group);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
				if (flag) {
					if (!cuserid.equals(approver)) {
						error.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0048")/*@res "�ü��������������Ȩ�ޣ�������˱����������"*/);
						ErmBusinessException ex = new ErmBusinessException(error
								.toString());
						Log.getInstance().error(ex);
						throw new BusiBeanRuntimeException(ex.getMessage(), ex);
					}
				}
			}

		} catch (Exception e) {
			Log.getInstance().error(e);
			throw new BusiBeanRuntimeException(e.getMessage(), e);
		}

	}
	private static String getOptionName(String action) {
		if(action.equals(BXConstans.EXPQUERYOPTCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000006")/*@res "��ѯ"*/;
		if(action.equals(BXConstans.EXPDELOPTCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000039")/*@res "ɾ��"*/;
		if(action.equals(BXConstans.EXPEDITCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000045")/*@res "�޸�"*/;
		if(action.equals(BXConstans.EXPAPPROVECODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000027")/*@res "���"*/;
		if(action.equals(BXConstans.EXPUNAPPROVECODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0049")/*@res "�����"*/;
		return "";

	}
}