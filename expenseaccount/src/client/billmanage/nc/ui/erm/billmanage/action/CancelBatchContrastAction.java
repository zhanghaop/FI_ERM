package nc.ui.erm.billmanage.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
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
import nc.ui.pub.msg.MessageDetailDlg;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.lang.ArrayUtils;

public class CancelBatchContrastAction extends NCAction {
	private static final long serialVersionUID = 1L;
	
	private BillManageModel model;
	private BillForm editor;

	private BatchContrastDialog batchcontrastDialog;
	private BatchContrastDetailDialog batchContrastDetailDialog;

	public CancelBatchContrastAction() {
		super();
		setCode(ErmActionConst.CANCELBATCHCONTRAST);
		setBtnName(ErmActionConst.getCancelBatConName());
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
	    Object[] obj = (Object[]) getModel().getSelectedOperaDatas();
		JKBXVO[] selBxvos = Arrays.asList(obj).toArray(new JKBXVO[0]);

		if(selBxvos==null || selBxvos.length==0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000004")/*@res "����ѡ��������֮���ٽ���ȡ���������!"*/);
		
		checkBatchContrastValid(selBxvos,true);
		

		
		/*@res "ȷ��Ҫȡ������ô,ͬһ���εĳ������ȡ��!"*/
		String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000005");
		if (MessageDialog.showOkCancelDlg(getEditor(), null,msg) != MessageDetailDlg.ID_OK)
			return ;

		BatchContrastDetailDialog contrastDetailDialog = getContrastDetailDialog();

		Collection<BxcontrastVO> results = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryJkContrast(selBxvos,true);

		Map<String,String> bxdBzMap=new HashMap<String,String>();
		for(JKBXVO vo:selBxvos){
			bxdBzMap.put(vo.getParentVO().getPrimaryKey(), vo.getParentVO().getBzbm());
		}
		for(BxcontrastVO result:results){
			result.setSelected(UFBoolean.TRUE);
			result.setBzbm(bxdBzMap.get(result.getPk_bxd()));
		}
		
		contrastDetailDialog.initData(selBxvos[0], results.toArray(new BxcontrastVO[]{}));

		contrastDetailDialog.showModal();

		if (contrastDetailDialog.getResult() == UIDialog.ID_OK) {
			
			List<BxcontrastVO> batchContrast = contrastDetailDialog.getSelectedData();
			
			if(batchContrast==null || batchContrast.size()==0){
				String msg2 = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000003");
				MessageDialog.showWarningDlg(getEditor(),nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm","UPP1005-000070")/* @res "����" */, msg2);
				return ;
			}
			NCLocator.getInstance().lookup(IBXBillPrivate.class).saveBatchContrast(batchContrast,true);
		}
	}
	
	public BatchContrastDialog getBatchContrastDialog(String pk_corp)
	throws BusinessException {
		if (batchcontrastDialog == null) {
			batchcontrastDialog = new BatchContrastDialog(getModel(),BXConstans.BXMNG_NODECODE, pk_corp);
		}
		return batchcontrastDialog;
	}
	
	public BatchContrastDetailDialog getContrastDetailDialog() {
		if (batchContrastDetailDialog == null) {
			batchContrastDetailDialog = new BatchContrastDetailDialog(getModel(), getModel().getContext().getNodeCode());
		}
		return batchContrastDetailDialog;
	}
	
    private void checkBatchContrastValid(JKBXVO[] selBxvos, boolean isCancel)
	throws DataValidateException {
		StringBuffer msgs=new StringBuffer();
		for (JKBXVO bxvo : selBxvos) {
			JKBXHeaderVO parentVO = bxvo.getParentVO();

            if (parentVO.getDjdl().equals(BXConstans.JK_DJDL))
            {
                msgs.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
                        getStrByID("2011", "UPP2011-000006")/*���ݲ��ܽ��г������
                  */+ ":" + parentVO.getDjbh() + "\n");
                continue;
            }

            //������������ĵ��ݣ���������������
            if(!StringUtils.isEmpty(bxvo.getParentVO().getPk_item())){
            	msgs.append(nc.vo.ml.NCLangRes4VoTransl
            			.getNCLangRes().getStrByID("2011", "UPP2011-000904")/** @res* "�����뵥�ı�������������ȡ����������"*/
            			+ ":"+ parentVO.getDjbh() + "\n");
            	continue;
            }
            //���õ������͵ĵ��ݣ���������������
            if(parentVO.isAdjustBxd()){
            	msgs.append(nc.vo.ml.NCLangRes4VoTransl
            			.getNCLangRes().getStrByID("2011", "UPP2011-000951")/** @res* "��������Ϊ���õ����ı�������������ȡ����������"*/
            			+ ":"+ parentVO.getDjbh() + "\n");
            	continue;
            	
            }
            
            // �������Ѻ���Ԥ�ᣬ��������������
            AccruedVerifyVO[] accruedVerifyVO = bxvo.getAccruedVerifyVO();
            if(accruedVerifyVO != null && accruedVerifyVO.length > 0){
            	msgs.append("�������Ѿ�����Ԥ�ᣬ������ȡ����������" + ":"+ parentVO.getDjbh() + "\n");
            	continue;
            	
            }
          //����״̬У��
            String msg = ActionUtils.checkBillStatus(parentVO.getDjzt(),
              	ActionUtils.CONTRAST, new int[] {}, new int[] {
              	BXStatusConst.DJZT_TempSaved,
              	BXStatusConst.DJZT_Sign });
              if (msg != null && msg.trim().length() != 0) {
                  msgs.append(msg + ":" + parentVO.getDjbh() + "\n");
                  continue;
              }
            
            if (!isCancel && parentVO.getDjrq().after(BXUiUtil.getBusiDate())) {
                msgs.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
                        .getStrByID("2011", "UPP2011-000407")/*
                         * @res
                         * "�������ĵ������ڲ������ڵ�ǰ��������"
                         */+ ":"+ parentVO.getDjbh() + "\n");
                continue;
            }
    	   
    	   //����ҵ��У��
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
     * ����ҵ��У��
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
		if (selectedData == null || ArrayUtils.isEmpty(selectedData.getContrastVO())) {
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
