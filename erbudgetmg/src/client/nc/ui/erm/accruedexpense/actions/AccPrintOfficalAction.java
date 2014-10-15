package nc.ui.erm.accruedexpense.actions;


import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class AccPrintOfficalAction extends AccPreViewAction {

	private static final long serialVersionUID = 1L;

	public AccPrintOfficalAction() {
		super();
		setBtnName(ErmActionConst.getOfficalprintName());
		setCode(ErmActionConst.OFFICALPRINT);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception{
		try {
			AggAccruedBillVO aggVO = (AggAccruedBillVO) getModel().getSelectedData();

			// 检查VO
			checkOfficalPrint(aggVO);
			// 打印
			super.doAction(e);
			// 更新打印信息
			aggVO.getParentVO().setPrinter(ErUiUtil.getPk_user());
			aggVO.getParentVO().setPrintdate(ErUiUtil.getBusiDate());

			AccruedVO parent = NCLocator.getInstance().lookup(IErmAccruedBillManage.class).updatePrintInfo(aggVO.getParentVO());
			aggVO.setParentVO(parent);
			((BillManageModel) getModel()).directlyUpdate(aggVO);
		} catch (BusinessException ex) {
			exceptionHandler.handlerExeption(ex);
		}
	}

	private boolean checkOfficalPrint(AggAccruedBillVO aggVO) throws BusinessException{
		if(aggVO == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0016")/*@res "请选择单据"*/);
		}

		AccruedVO head = (AccruedVO) aggVO.getParentVO();

		if (head.getBillstatus() == null || !(head.getBillstatus().intValue() == ErmAccruedBillConst.BILLSTATUS_APPROVED)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0017")/*@res "只有已审核的单据才能做正式打印"*/);
		}

		String user = head.getPrinter();
		if (!StringUtils.isNullWithTrim(user)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0018")/*@res "不能重复正式打印"*/);
		}

		return true;
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
	}
}
