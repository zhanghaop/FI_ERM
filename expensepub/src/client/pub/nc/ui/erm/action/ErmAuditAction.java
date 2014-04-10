package nc.ui.erm.action;

import java.util.List;

import javax.swing.SwingWorker;

import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.progress.DefaultProgressMonitor;
import nc.ui.uif2.actions.ApproveAction;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.uif2.LoginContext;

/**
 * 加入审批进度条
 * 
 * @author chenshuaia
 * 
 */
public abstract class ErmAuditAction extends ApproveAction {
	private static final long serialVersionUID = 1L;

	// 以下实现数据权限需要
	private String mdOperateCode = null; // 元数据操作编码
	private String operateCode = null; // 资源对象操作编码，以上两者注入其一，都不注入，则不进行数据权限控制。
	private String resourceCode = null; // 业务实体资源编码

	private BillManageModel model;
	
	/**
	 * 审批结果信息
	 */
	protected MessageVO[] msgs = null;

	private TPAProgressUtil tpaProgressUtil;

	public class ListApproveSwingWork extends SwingWorker<MessageVO[], Integer> {
		AggregatedValueObject[] aggs;
		DefaultProgressMonitor mon;
		int successCount = 0;
		int failCount = 0;
		boolean first = true;

		public ListApproveSwingWork(AggregatedValueObject[] aggs, DefaultProgressMonitor mon) {
			this.aggs = aggs;
			this.mon = mon;
		}

		@Override
		protected MessageVO[] doInBackground() throws Exception {
			mon.setProcessInfo2(null);
			MessageVO[] result = new MessageVO[aggs.length];
			for (int i = 0; i < aggs.length; i++) {
				if (mon.isCanceled()) {
					break;
				}
				AggregatedValueObject aggvo = aggs[i];
				result[i] = approveSingle(aggvo);

				if (result[i].isSuccess()) {
					successCount++;
				} else {
					failCount++;
				}

				publish(1);
			}

			if ((failCount + successCount) != aggs.length) {
				for (int i = (failCount + successCount); i < aggs.length; i++) {
					result[i] = new MessageVO(aggs[i], ActionUtils.AUDIT);
					result[i].setSuccess(false);
					result[i].setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000339")/* @res "用户取消操作" */);
				}
			}

			return result;
		}

		@Override
		protected void process(List<Integer> chunks) {
			StringBuffer mes = new StringBuffer();
			if (first) {
				mon.setProcessInfo2(mes.toString());
				first = false;
			}

			mon.setProcessInfo(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub0322_0", "UPP2011-000922")/*
																														 * @
																														 * res
																														 * "正在审批...."
																														 */);
			int doneCount = mon.getSumWorks() + chunks.size();
			int allCount = aggs.length;
			int failCount = this.failCount;

			mes.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", "已处理{0}条数据", "UPP2011-000920", null,
					new String[] { doneCount + "/" + allCount }));/*
																 * @res
																 * 已处理{0}条数据"
																 */

			if (failCount > 0) {
				mes.append(","
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", "{0}条处理失败", "UPP2011-000921", null,
								new String[] { "" + failCount }));/* @res {0}条处理失败 */
			}

			mon.setProcessInfo2(mes.toString());
			mon.worked(chunks.size());
		}

		// 事件分配线程, 当业务处理结束后执行此方法可以更新界面Swing控件
		@Override
		protected void done() {
			mon.done();
			try {
				List<AggregatedValueObject> auditedVos = ErUiUtil.combineMsgs(msgs, get());
				getModel().directlyUpdate(auditedVos.toArray(new AggregatedValueObject[auditedVos.size()]));
				ErUiUtil.showBatchResults(getModel().getContext(), msgs);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	/**
	 * 单个单据审批
	 * 
	 * @param aggvo
	 * @return
	 * @throws Exception
	 */
	protected abstract MessageVO approveSingle(AggregatedValueObject aggvo) throws Exception;

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public TPAProgressUtil getTpaProgressUtil() {
		if (this.tpaProgressUtil == null) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}
		return tpaProgressUtil;

	}

	protected boolean checkDataPermission() {
		if (StringUtil.isEmptyWithTrim(getOperateCode()) && StringUtil.isEmptyWithTrim(getMdOperateCode())
				|| StringUtil.isEmptyWithTrim(getResourceCode()))
			return true;

		LoginContext context = getModel().getContext();
		String userId = context.getPk_loginUser();
		String pkgroup = context.getPk_group();
		Object data = getModel().getSelectedData();
		boolean hasp = true;
		if (!StringUtil.isEmptyWithTrim(getMdOperateCode()))
			hasp = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(userId, getResourceCode(),
					getMdOperateCode(), pkgroup, data);
		else
			hasp = DataPermissionFacade.isUserHasPermission(userId, getResourceCode(), getOperateCode(), pkgroup, data);
		return hasp;
	}

	public String getMdOperateCode() {
		return mdOperateCode;
	}

	public void setMdOperateCode(String mdOperateCode) {
		this.mdOperateCode = mdOperateCode;
	}

	public String getOperateCode() {
		return operateCode;
	}

	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}
}
