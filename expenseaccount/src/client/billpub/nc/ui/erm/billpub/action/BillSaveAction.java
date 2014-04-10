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
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class BillSaveAction extends nc.ui.uif2.actions.SaveAction {
	
	private ContrastAction contrastaction;

    private static final long serialVersionUID = -3683160678696139800L;

    @Override
    public void doAction(ActionEvent e) throws Exception {
    	
    	BillCardPanel billCardPanel = ((ErmBillBillForm) getEditor()).getBillCardPanel();
		String tableCode = (billCardPanel.getBillData().getBodyTableCodes())[0];
//		boolean showing = billCardPanel.getBillTable(tableCode).isShowing();
//    	if(!showing){
//    		//单据的表体没有页签时，在修改保存时，将业务页签的值清空，在后面重新根据表头的值生成表体
//    		billCardPanel.getBillModel(tableCode).clearBodyData();
//    	}
		
		//ehp2报销单和还款单都不过滤金额为0的行
		DjLXVO djlxVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if(djlxVO!=null && BXConstans.JK_DJDL.equals(djlxVO.getDjdl())){
			delBlankLine(billCardPanel, tableCode);
		}
		
		JKBXVO value = (JKBXVO)getEditor().getValue();
		
		if (BXConstans.BX_DJDL.equals(value.getParentVO().getDjdl())) {
			boolean isContrast = checkContrast(value);// 是否冲借款控制
			if (isContrast) {
				value = (JKBXVO) getEditor().getValue();
			}
		}
		value.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);

		// 校验
		validate(value);

		// 执行单据模板验证公式，在表体存在校验公式时，表体过多，并且大多数行不符合校验规则的数据时
		//公式解析器会记录日志，这是个很耗时的操作 千行会耗时1~2s
		boolean execValidateFormulas = billCardPanel.getBillData().execValidateFormulas();
		if (!execValidateFormulas) {
			return;
		}

		if (((ErmBillBillForm) getEditor()).getResVO() != null) {
			JKBXVO vo = (JKBXVO) ((ErmBillBillForm) getEditor()).getResVO().getBusiobj();
			value.setMaheadvo(vo.getMaheadvo());
		}

		//表体中有超过标准值的行
		if(((ErmBillBillForm) getEditor()).getRows().size()>0){
			int result = MessageDialog.showYesNoDlg((ErmBillBillForm) getEditor(), null, "该单据所填金额超过标准允许的最大金额，是否确认保存？");
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
		// 报销单检查是否提示冲借款
		boolean flag = true;
		if (bxvo.getParentVO().getYbje() != null && bxvo.getParentVO().getYbje().doubleValue() >= 0) {
			if (bxvo.getParentVO().getCjkybje() != null
					&& bxvo.getParentVO().getCjkybje().compareTo(new UFDouble(0.00)) != 0) {
				// 有冲借款金额不再提示
				flag = false;
			}
		}
		if (flag) {
			// 交易类型是否提示冲借款
			UFBoolean isNoticeContrast = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getIscontrast();
			if (isNoticeContrast.booleanValue()) {
				try {
					// 本人是否有借款单
					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class)
							.getJKD(bxvo, bxvo.getParentVO().getDjrq(), null).size() > 0;
					if (hasJKD
							&& MessageDialog.ID_YES == MessageDialog
									.showYesNoDlg(
											(BillForm)getEditor(),
											nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
													"UPP2011-000049")/*
																	 * @res "提示"
																	 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
													.getStrByID("2011v61013_0", "02011v61013-0087")/*
																									 * @
																									 * res
																									 * "本人有未清的借款单，是否进行冲借款操作? "
																									 */)) {
						getContrastaction().doAction(null);
						// 重新取冲借款后的vo
					}
				} catch (Exception e) {
					ExceptionHandler.handleRuntimeException(e);
				}
			}
		}
		
		return flag;
	}
    
	public static void delBlankLine(BillCardPanel billCardPanel, String tableCode) {
		// 停止编辑
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
