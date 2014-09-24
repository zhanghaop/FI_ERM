package nc.ui.erm.billmanage.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billmanage.view.BatchContrastDetailDialog;
import nc.ui.erm.billmanage.view.BatchContrastDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class BatchContrastAction extends NCAction {

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;

	private BatchContrastDialog batchcontrastDialog;
	private BatchContrastDetailDialog batchContrastDetailDialog;

	public BatchContrastAction() {
		super();
		setCode(ErmActionConst.BATCHCONTRAST);
		setBtnName(ErmActionConst.getBatchContrastName());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

	    Object[] obj = (Object[]) getModel().getSelectedOperaDatas();
	    
	    JKBXVO[] selBxvos = Arrays.asList(obj).toArray(new JKBXVO[0]);

		checkBatchContrastValid(selBxvos,false);
		


		BatchContrastDialog dialog = getBatchContrastDialog(getModel()
				.getContext().getPk_org());
		
		dialog.showModal();
		if (dialog.getResult() == UIDialog.ID_OK) {

			List<String> mode_data = dialog.getCardPanel().getM_mode();
			BatchContratParam param = new BatchContratParam();
			param.setCxrq(BXUiUtil.getBusiDate());

			List<BxcontrastVO> results = NCLocator.getInstance().lookup(
					IBXBillPrivate.class).batchContrast(selBxvos, mode_data,
					param);

			if (results == null || results.size() == 0) {
				String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000003");
				MessageDialog.showWarningDlg(getEditor(),
						nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm",
								"UPP1005-000070")/* @res "警告" */, msg);
				return;
			}
			BatchContrastDetailDialog contrastDetailDialog = getContrastDetailDialog();

			Map<String, String> bxdBzMap = new HashMap<String, String>();
			for (JKBXVO vo : selBxvos) {
				bxdBzMap.put(vo.getParentVO().getPrimaryKey(), vo.getParentVO()
						.getBzbm());
			}

			for (BxcontrastVO result : results) {
				result.setSelected(UFBoolean.TRUE);
				result.setBzbm(bxdBzMap.get(result.getPk_bxd()));
			}

			contrastDetailDialog.initData(selBxvos[0], results
					.toArray(new BxcontrastVO[] {}));

			contrastDetailDialog.showModal();

			if (contrastDetailDialog.getResult() == UIDialog.ID_OK) {
				List<BxcontrastVO> selectedData = contrastDetailDialog
						.getSelectedData();

				if (selectedData.size() != 0) {
					NCLocator.getInstance().lookup(IBXBillPrivate.class)
							.saveBatchContrast(selectedData, false);
				}
			}
		}
	}

	public BatchContrastDialog getBatchContrastDialog(String pk_corp)
			throws BusinessException {
		if (batchcontrastDialog == null) {
			batchcontrastDialog = new BatchContrastDialog(getModel(),
					BXConstans.BXMNG_NODECODE, pk_corp);
		}
		return batchcontrastDialog;
	}

	public BatchContrastDetailDialog getContrastDetailDialog() {
		if (batchContrastDetailDialog == null) {
			batchContrastDetailDialog = new BatchContrastDetailDialog(
					getModel(), getModel().getContext().getNodeCode());
		}
		return batchContrastDetailDialog;
	}

    private void checkBatchContrastValid(JKBXVO[] selBxvos, boolean isCancel)
			throws DataValidateException {
	
		StringBuffer msgs =new StringBuffer();
		for (JKBXVO bxvo : selBxvos) {
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			if (parentVO.getDjdl().equals(BXConstans.JK_DJDL)) {
			    msgs.append(  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
		                .getStrByID("2011", "UPP2011-000006")/*
		                 * @res
		                 * "借款单据不能进行冲借款操作"
		                 */+ ":"
		                 + parentVO.getDjbh() + "\n");
				continue;
			}

			//费用调整类型的单据，不可以批量冲借款
			if(parentVO.isAdjustBxd()){
				msgs.append(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000950")/** @res* "报销类型为费用调整的报销单，不可以批量冲借款"*/
						+ ":"+ parentVO.getDjbh() + "\n");
				continue;
				
			}
			
			//如果存在拉单的单据，不可以批量冲借款
			if(!StringUtils.isEmpty(bxvo.getParentVO().getPk_item())){
				msgs.append(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000903")/** @res* "拉申请单的报销单，不可以批量冲借款"*/
						+ ":"+ parentVO.getDjbh() + "\n");
				continue;
				
			}
			// 报销单已核销预提，不可以批量冲借款
			AccruedVerifyVO[] accruedVerifyVO = bxvo.getAccruedVerifyVO();
			if(accruedVerifyVO != null && accruedVerifyVO.length > 0){
				msgs.append(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000956")/** @res* "报销单已经核销预提，不可以批量冲借款"*/
						+ ":"+ parentVO.getDjbh() + "\n");
				continue;
				
			}
			//单据状态校验
			String msg = ActionUtils.checkBillStatus(parentVO.getDjzt(),
					ActionUtils.CONTRAST, new int[] {}, new int[] {
				BXStatusConst.DJZT_TempSaved,
				BXStatusConst.DJZT_Sign });
			if (msg != null && msg.trim().length() != 0) {
			    msgs.append( msg + ":" + parentVO.getDjbh() + "\n");
			    continue;
			}
			
			if (!isCancel && parentVO.getDjrq().after(BXUiUtil.getBusiDate())) {
			    msgs.append( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
                        .getStrByID("2011", "UPP2011-000407")/*
                         * @res
                         * "报销单的单据日期不能晚于当前冲销日期"
                         */+ ":"+ parentVO.getDjbh() + "\n");
				continue;
			}
			
			//其他业务校验
			String otherCheckMsg = otherCheck(bxvo);
			if (otherCheckMsg != null && otherCheckMsg.trim().length() != 0) {
				msgs.append( otherCheckMsg + ":" + parentVO.getDjbh() + "\n");
				continue;
			}
			
			
		}

		if (!StringUtils.isNullWithTrim(msgs.toString())) {
			throw new DataValidateException(msgs.toString());
		}
	}
    /**
     * 其他业务校验
     * @param bxvo
     * @return
     */
	protected String otherCheck(JKBXVO bxvo) {
		return "";
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null) {
			return false;
		}
		return true;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
