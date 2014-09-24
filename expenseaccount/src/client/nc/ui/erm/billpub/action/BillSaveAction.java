package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBxUIControl;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class BillSaveAction extends nc.ui.uif2.actions.SaveAction {
	
	private ContrastAction contrastaction;

    private static final long serialVersionUID = -3683160678696139800L;

    @Override
    public void doAction(ActionEvent e) throws Exception {
    	
    	BillCardPanel billCardPanel = ((ErmBillBillForm) getEditor()).getBillCardPanel();
		//String tableCode = (billCardPanel.getBillData().getBodyTableCodes())[0];
    	BillTabVO[] billTabVOs = billCardPanel.getBillData().getBillTabVOs(IBillItem.BODY);

		//		boolean showing = billCardPanel.getBillTable(tableCode).isShowing();
//    	if(!showing){
//    		//���ݵı���û��ҳǩʱ�����޸ı���ʱ����ҵ��ҳǩ��ֵ��գ��ں������¸��ݱ�ͷ��ֵ���ɱ���
//    		billCardPanel.getBillModel(tableCode).clearBodyData();
//    	}
		
		//ehp2�������ͻ���������˽��Ϊ0����
		DjLXVO djlxVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if(djlxVO!=null && BXConstans.JK_DJDL.equals(djlxVO.getDjdl())){
			for (BillTabVO billTabVO : billTabVOs) {
				String metaDataPath = billTabVO.getMetadatapath();
				if (metaDataPath != null&& BXConstans.JK_BUSITEM.equals(metaDataPath)) {
					delBlankLine(billCardPanel, billTabVO.getTabcode());
				}
			}
		}
		
		JKBXVO value = (JKBXVO)getEditor().getValue();
		
		if (BXConstans.BX_DJDL.equals(value.getParentVO().getDjdl())) {
			boolean isContrast = checkContrast(value);// �Ƿ�������
			if (isContrast) {
				value = (JKBXVO) getEditor().getValue();
			}
		}
		value.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);

		// У��
		validate(value);

		// ִ�е���ģ����֤��ʽ���ڱ������У�鹫ʽʱ��������࣬���Ҵ�����в�����У����������ʱ
		//��ʽ���������¼��־�����Ǹ��ܺ�ʱ�Ĳ��� ǧ�л��ʱ1~2s
		boolean execValidateFormulas = billCardPanel.getBillData().execValidateFormulas();
		if (!execValidateFormulas) {
			return;
		}

		if (((ErmBillBillForm) getEditor()).getResVO() != null) {
			JKBXVO vo = (JKBXVO) ((ErmBillBillForm) getEditor()).getResVO().getBusiobj();
			value.setMaheadvo(vo.getMaheadvo());
		}

		//�������г�����׼ֵ����
		if(((ErmBillBillForm) getEditor()).getRows().size()>0){
			int result = MessageDialog.showYesNoDlg((ErmBillBillForm) getEditor(), null, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0115")/*
					 * @
					 * res
					 * "�õ������������׼����������Ƿ�ȷ�ϱ��棿"
					 */);
			if (result != UIDialog.ID_YES) 
				return;
		}
		
		if (getModel().getUiState() == UIState.ADD) {
			doAddSave(value);
		} else if (getModel().getUiState() == UIState.EDIT) {
			doEditSave(value);
		}

		showSuccessInfo();
    }
    
    private boolean checkContrast(JKBXVO bxvo) {
		// ����������Ƿ���ʾ����
		boolean flag = true;
		if (bxvo.getParentVO().getYbje() != null && bxvo.getParentVO().getYbje().doubleValue() >= 0) {
			if (bxvo.getParentVO().getCjkybje() != null
					&& bxvo.getParentVO().getCjkybje().compareTo(new UFDouble(0.00)) != 0) {
				// �г��������ʾ
				flag = false;
			}
		}
		if (flag) {
			// ���������Ƿ���ʾ����
			UFBoolean isNoticeContrast = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getIscontrast();
			if (isNoticeContrast.booleanValue()) {
				try {
					// �����Ƿ��н�
					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class)
							.getJKD(bxvo, bxvo.getParentVO().getDjrq(), null).size() > 0;
					if (hasJKD
							&& MessageDialog.ID_YES == MessageDialog
									.showYesNoDlg(
											(BillForm)getEditor(),
											nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
													"UPP2011-000049")/*
																	 * @res "��ʾ"
																	 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
													.getStrByID("2011v61013_0", "02011v61013-0087")/*
																									 * @
																									 * res
																									 * "������δ��Ľ����Ƿ���г������? "
																									 */)) {
						getContrastaction().doAction(null);
						// ����ȡ������vo
					}
				} catch (Exception e) {
					ExceptionHandler.handleRuntimeException(e);
				}
			}
		}
		
		return flag;
	}
    
	public static void delBlankLine(BillCardPanel billCardPanel, String tableCode) {
		// ֹͣ�༭
		billCardPanel.stopEditing();
		int rowCount = billCardPanel.getRowCount(tableCode);
		List<Integer> dellist=new ArrayList<Integer>();
		for (int currow =0;currow<rowCount;currow++){
			
			UFDouble amount = (UFDouble) billCardPanel.getBillModel(tableCode).getValueAt(currow, JKBXHeaderVO.AMOUNT);
			UFDouble ybje = (UFDouble) billCardPanel.getBillModel(tableCode).getValueAt(currow, JKBXHeaderVO.YBJE);
			UFDouble hkybje = (UFDouble) billCardPanel.getBillModel(tableCode).getValueAt(currow, JKBXHeaderVO.HKYBJE);
			UFDouble zfybje = (UFDouble) billCardPanel.getBillModel(tableCode).getValueAt(currow, JKBXHeaderVO.ZFYBJE);
			
			if ((amount == null || amount.compareTo(UFDouble.ZERO_DBL) == 0)
					&& (ybje == null || ybje.compareTo(UFDouble.ZERO_DBL) == 0)
					&& (hkybje == null || hkybje.compareTo(UFDouble.ZERO_DBL) == 0)
					&& (zfybje == null || zfybje.compareTo(UFDouble.ZERO_DBL) == 0)) {
				dellist.add(currow);
			}
		}
		int del[]=new int[dellist.size()];
		for (int i = 0; i < dellist.size(); i++) {
			del[i]=dellist.get(i);
		}
		billCardPanel.getBillModel(tableCode).delLine(del);
	}

	public ContrastAction getContrastaction() {
		return contrastaction;
	}

	public void setContrastaction(ContrastAction contrastaction) {
		this.contrastaction = contrastaction;
	}
}
