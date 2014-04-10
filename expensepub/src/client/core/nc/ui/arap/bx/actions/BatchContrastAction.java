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
 * ��������Action
 *
 * nc.ui.arap.bx.actions.BatchContrastAction
 */
public class BatchContrastAction extends BXDefaultAction {


	/**
	 * @throws BusinessException
	 *
	 * 1. ��ʼ������Ի���
	 * 2. ȡ��ѡ����Ϣ,�ڽ����ϼ�¼����Ϣ��ˢ�½���
	 */
	public void contrast() throws BusinessException {

		JKBXVO[] selBxvos = getSelBxvosClone();

		if(selBxvos==null || selBxvos.length==0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000002")/*@res "����ѡ��������֮���ٽ��в���!"*/);

		checkBatchContrastValid(selBxvos,false);

		BatchContrastDialog dialog = getMainPanel().getBatchContrastDialog(getPk_org());

		dialog.showModal();

		if (dialog.getResult() == UIDialog.ID_OK) {

			List<String> mode_data = dialog.getCardPanel().getM_mode();
			BatchContratParam param=new BatchContratParam();
			param.setCxrq(BXUiUtil.getBusiDate());

			List<BxcontrastVO> results = getIBXBillPrivate().batchContrast(selBxvos,mode_data,param);

			if(results==null || results.size()==0){
				getMainPanel().showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000003")/*@res "û�г�������"*/);

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
	 * ȡ������
	 * @throws BusinessException
	 */
	public void cancelContrast() throws BusinessException{

		JKBXVO[] selBxvos = getSelBxvosClone();

		if(selBxvos==null || selBxvos.length==0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000004")/*@res "����ѡ��������֮���ٽ���ȡ���������!"*/);

		checkBatchContrastValid(selBxvos,true);

		if (getParent().showOkCancelMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000005")/*@res "ȷ��Ҫȡ������ô,ͬһ���εĳ������ȡ��!"*/) != MessageDetailDlg.ID_OK)
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
				getMainPanel().showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000003")/*@res "û�г�������"*/);

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
				msgs = msgs+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000006")/*@res "���ݲ��ܽ��г������"*/ + ":"+parentVO.getDjbh()+"\n";
				continue;
			}

			if(!isCancel && parentVO.getDjrq().after(BXUiUtil.getBusiDate())){
				msgs = msgs+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000407")/*@res "�������ĵ������ڲ������ڵ�ǰ��������"*/ + ":"+parentVO.getDjbh()+"\n";
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