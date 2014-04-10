package nc.ui.arap.bx.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.ui.arap.bx.BatchContrastDetailDialog;
import nc.ui.arap.bx.BatchContrastDialog;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.msg.MessageDetailDlg;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author twei
 *
 * 批量冲借款Action
 *
 * nc.ui.arap.bx.actions.BatchContrastAction
 */
public class BatchContrastAction extends BXDefaultAction {


	/**
	 * @throws BusinessException
	 *
	 * 1. 初始化冲借款对话框
	 * 2. 取得选择信息,在界面上记录借款单信息，刷新界面
	 */
	public void contrast() throws BusinessException {

		JKBXVO[] selBxvos = getSelBxvosClone();

		if(selBxvos==null || selBxvos.length==0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000002")/*@res "请先选择报销单据之后再进行操作!"*/);

		checkBatchContrastValid(selBxvos,false);

		BatchContrastDialog dialog = getMainPanel().getBatchContrastDialog(getPk_org());

		dialog.showModal();

		if (dialog.getResult() == UIDialog.ID_OK) {

			List<String> mode_data = dialog.getCardPanel().getM_mode();
			BatchContratParam param=new BatchContratParam();
			param.setCxrq(BXUiUtil.getBusiDate());

			List<BxcontrastVO> results = getIBXBillPrivate().batchContrast(selBxvos,mode_data,param);

			if(results==null || results.size()==0){
				getMainPanel().showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000003")/*@res "没有冲借款数据"*/);

				return ;
			}
			BatchContrastDetailDialog contrastDetailDialog = getMainPanel().getContrastDetailDialog();

			Map<String,String> bxdBzMap=new HashMap<String,String>();
			for(JKBXVO vo:selBxvos){
				bxdBzMap.put(vo.getParentVO().getPrimaryKey(), vo.getParentVO().getBzbm());
			}
			
			for(BxcontrastVO result:results){
				result.setSelected(new UFBoolean(true));
				result.setBzbm(bxdBzMap.get(result.getPk_bxd()));
			}

			contrastDetailDialog.initData(selBxvos[0], results.toArray(new BxcontrastVO[]{}));

			contrastDetailDialog.showModal();

			if (contrastDetailDialog.getResult() == UIDialog.ID_OK) {
				List<BxcontrastVO> selectedData = contrastDetailDialog.getSelectedData();

				if(selectedData.size()!=0){
					getIBXBillPrivate().saveBatchContrast(selectedData,false);
				}
			}
		}
	}

	/**
	 * 取消冲借款
	 * @throws BusinessException
	 */
	public void cancelContrast() throws BusinessException{

		JKBXVO[] selBxvos = getSelBxvosClone();

		if(selBxvos==null || selBxvos.length==0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000004")/*@res "请先选择报销单据之后再进行取消冲借款操作!"*/);

		checkBatchContrastValid(selBxvos,true);

		if (getParent().showOkCancelMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000005")/*@res "确定要取消冲借款么,同一批次的冲借款都将被取消!"*/) != MessageDetailDlg.ID_OK)
			return ;

		BatchContrastDetailDialog contrastDetailDialog = getMainPanel().getContrastDetailDialog();

		Collection<BxcontrastVO> results = getIBXBillPrivate().queryJkContrast(selBxvos,true);

		Map<String,String> bxdBzMap=new HashMap<String,String>();
		for(JKBXVO vo:selBxvos){
			bxdBzMap.put(vo.getParentVO().getPrimaryKey(), vo.getParentVO().getBzbm());
		}
		for(BxcontrastVO result:results){
			result.setSelected(new UFBoolean(true));
			result.setBzbm(bxdBzMap.get(result.getPk_bxd()));
		}
		
		contrastDetailDialog.initData(selBxvos[0], results.toArray(new BxcontrastVO[]{}));

		contrastDetailDialog.showModal();

		if (contrastDetailDialog.getResult() == UIDialog.ID_OK) {
			
			List<BxcontrastVO> batchContrast = contrastDetailDialog.getSelectedData();
			
			if(batchContrast==null || batchContrast.size()==0){
				getMainPanel().showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000003")/*@res "没有冲借款数据"*/);

				return ;
			}

			getIBXBillPrivate().saveBatchContrast(batchContrast,true);

		}
	}

	private void checkBatchContrastValid(JKBXVO[] selBxvos,boolean isCancel) throws DataValidateException {

		String msgs = "";
		for (JKBXVO bxvo:selBxvos) {
			JKBXHeaderVO parentVO=bxvo.getParentVO();
			String msg=ActionUtils.checkBillStatus(parentVO.getDjzt(), MessageVO.CONTRAST, new int[]{},new int[] { BXStatusConst.DJZT_TempSaved,BXStatusConst.DJZT_Sign });

			if(parentVO.getDjdl().equals(BXConstans.JK_DJDL)){
				msgs = msgs+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000006")/*@res "借款单据不能进行冲借款操作"*/ + ":"+parentVO.getDjbh()+"\n";
				continue;
			}

			if(!isCancel && parentVO.getDjrq().after(BXUiUtil.getBusiDate())){
				msgs = msgs+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000407")/*@res "报销单的单据日期不能晚于当前冲销日期"*/ + ":"+parentVO.getDjbh()+"\n";
				continue;
			}

			if(msg!=null && msg.trim().length()!=0){
				msgs = msgs+ msg + ":"+parentVO.getDjbh()+"\n";
			}


		}

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

	}



}