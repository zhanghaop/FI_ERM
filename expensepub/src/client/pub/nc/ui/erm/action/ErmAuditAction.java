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
 * ��������������
 * 
 * @author chenshuaia
 * 
 */
public abstract class ErmAuditAction extends ApproveAction {
	private static final long serialVersionUID = 1L;

	// ����ʵ������Ȩ����Ҫ
	private String mdOperateCode = null; // Ԫ���ݲ�������
	private String operateCode = null; // ��Դ����������룬��������ע����һ������ע�룬�򲻽�������Ȩ�޿��ơ�
	private String resourceCode = null; // ҵ��ʵ����Դ����

	private BillManageModel model;
	
	/**
	 * ���������Ϣ
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
							"UPP2011-000339")/* @res "�û�ȡ������" */);
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
																														 * "��������...."
																														 */);
			int doneCount = mon.getSumWorks() + chunks.size();
			int allCount = aggs.length;
			int failCount = this.failCount;

			mes.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", "�Ѵ���{0}������", "UPP2011-000920", null,
					new String[] { doneCount + "/" + allCount }));/*
																 * @res
																 * �Ѵ���{0}������"
																 */

			if (failCount > 0) {
				mes.append(","
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", "{0}������ʧ��", "UPP2011-000921", null,
								new String[] { "" + failCount }));/* @res {0}������ʧ�� */
			}

			mon.setProcessInfo2(mes.toString());
			mon.worked(chunks.size());
		}

		// �¼������߳�, ��ҵ���������ִ�д˷������Ը��½���Swing�ؼ�
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
	 * ������������
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
