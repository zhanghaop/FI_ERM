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
 * 单据是否具有删除权限处理类
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-11-09 下午06:19:35
 */
public class IsCheckOperatePermission {
	public void process(JKBXVO vo, String resourceCode, String operationCode) {
		try {
			// 注意这里资源实体编码，资源实体id，这里暂时有改动，请确认以后改动

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
				error.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0046")/*@res "单据数据操作权限不允许执行("*/ + optionName + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0047")/*@res "操作)，请确认！"*/);
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
						error.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0048")/*@res "该集团启用了审核者权限，反审核人必须是审核人"*/);
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
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000006")/*@res "查询"*/;
		if(action.equals(BXConstans.EXPDELOPTCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000039")/*@res "删除"*/;
		if(action.equals(BXConstans.EXPEDITCODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000045")/*@res "修改"*/;
		if(action.equals(BXConstans.EXPAPPROVECODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000027")/*@res "审核"*/;
		if(action.equals(BXConstans.EXPUNAPPROVECODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0049")/*@res "反审核"*/;
		return "";

	}
}