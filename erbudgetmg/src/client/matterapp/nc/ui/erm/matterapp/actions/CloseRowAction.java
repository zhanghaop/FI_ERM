package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.ui.erm.matterapp.view.AbstractMappBillForm;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

/**
 *
 * 关闭行Action Action　
 *
 * @author chenshuaia
 *
 */
@SuppressWarnings("serial")
public class CloseRowAction extends NCAction{
	private AbstractAppModel model;
	private BillForm cardpanel;

	private IErmMatterAppBillClose appBillCloseService;

	public CloseRowAction(){
		super();
		this.setBtnName(ErmActionConst.getCloseLineName());
		this.setCode(ErmActionConst.CLOSELINE);
		this.putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getCloseLineName());
		this.putValue(Action.SMALL_ICON, ThemeResourceCenter.getInstance().getImage(
				ErmMatterAppConst.TOOLBARICONS_CLOSE_PNG));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO seldate = (AggMatterAppVO) getModel().getSelectedData();
		// 取得表体选择数据
		int[] rows = getCardpanel().getBillCardPanel().getBodyPanel().getTable()
				.getSelectedRows();
		if (rows == null || rows.length <= 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0010")/*@res "未选择行"*/);
		}
		
		if (rows.length > 1) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0096")/** @res* "请选择一条申请单明细进行操作！" */
			);
		}
		MtAppDetailVO children = (MtAppDetailVO)getBillModel().getBodyValueRowVO(rows[0], MtAppDetailVO.class.getName());

		AggMatterAppVO closevo = new AggMatterAppVO();
		closevo.setParentVO(seldate.getParentVO());
		closevo.setChildrenVO(new MtAppDetailVO[]{children});

		// 仅支持单行关闭与重启
		Object retObj = null;
		if(children.getClose_status() == null || children.getClose_status() == ErmMatterAppConst.CLOSESTATUS_N){
			retObj = getAppBillService().closeVOs(new AggMatterAppVO[]{closevo});
		}else{
			retObj = openRow(closevo);
		}
		getModel().directlyUpdate(retObj);
	}

	private Object openRow(AggMatterAppVO closevo) throws BusinessException {
		Object retObj = null;
		try {
			retObj = getAppBillService().openVOs(new AggMatterAppVO[]{closevo});
		} catch (BugetAlarmBusinessException ex) {
			if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000049")/*
														 * @ res "提示"
														 */, ex.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
					getStrByID("upp2012v575_0","0upp2012V575-0125")/*@res ""是否继续重启？""*/) == MessageDialog.ID_YES) {
				closevo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // 不检查
				return openRow(closevo);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")/*
										 * @res "预算申请失败"
										 */);
			}
		}
		return retObj;
	}

	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		if(((AbstractMappBillForm)cardpanel).getLink_type() != -1){
			return false;
		}
		int[] rows = getCardpanel().getBillCardPanel().getBodyPanel().getTable().getSelectedRows();
		if (rows == null || rows.length <= 0) {
			return false;
		}else{
			int row = getCardpanel().getBillCardPanel().getBodyPanel().getTable().getSelectedRow();
			MtAppDetailVO children = (MtAppDetailVO)getCardpanel().getBillCardPanel().getBillModel().getBodyValueRowVO(
					row, MtAppDetailVO.class.getName());
			
			if(children != null && INCSystemUserConst.NC_USER_PK.equals(children.getCloseman())){
				return false;
			}
		}

		return true;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillForm getCardpanel() {
		return cardpanel;
	}

	public void setCardpanel(BillForm cardpanel) {
		this.cardpanel = cardpanel;
	}

	public IErmMatterAppBillClose getAppBillService() {
		if (appBillCloseService == null) {
			appBillCloseService = NCLocator.getInstance().lookup(
					IErmMatterAppBillClose.class);
		}
		return appBillCloseService;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = getBtnName() + ErmActionConst.FAIL_MSG;
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(null);
	}

	private BillModel getBillModel() {
		return getCardpanel().getBillCardPanel().getBillModel();
	}
}