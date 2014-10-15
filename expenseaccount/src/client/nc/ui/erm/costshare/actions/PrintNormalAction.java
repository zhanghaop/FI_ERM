package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.erm.costshare.ui.CostShareModelService;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.lang.UFDateTime;

/**
 * @author luolch
 *
 * 正式打印
 *
 * 
 */
@SuppressWarnings({ "serial" })
public class PrintNormalAction extends CsPrintAction {

	public PrintNormalAction() {
		super();
		setBtnName(ErmActionConst.getOfficalprintName());
		setCode(ErmActionConst.OFFICALPRINT);
		putValue(Action.ACCELERATOR_KEY, null);
		putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getOfficalprintName());
	}

	@Override
	public void doAction(ActionEvent e) {
		AggCostShareVO aggVO = (AggCostShareVO) getModel().getSelectedData();
		CostShareVO csvo = (CostShareVO)aggVO.getParentVO();
		if (csvo.getPrinter()!=null) {
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0041"), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0040")/*@res "不允许重复正式打印！"*/, getModel().getContext());
			return;
		}
		super.doAction(e);
		AggCostShareVO[] aggvos = null;
		try {
			aggvos =  ((CostShareModelService)((BillManageModel)getModel()).getService()).printNormal(new String[]{aggVO.getParentVO().getPrimaryKey()},
					new UFDateTime(WorkbenchEnvironment.getInstance().getBusiDate().toString()).toString(),getModel().getContext().getPk_loginUser());
		} catch (Exception e1) {
			nc.bs.logging.Logger.error(e1.getMessage(), e1);
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0041"), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0041")/*@res "正式打印失败！"*/, getModel().getContext());
		}
		((BillManageModel)getModel()).directlyUpdate(aggvos);
	}
	@Override
	protected boolean isActionEnable() {
		AggCostShareVO aggVO = (AggCostShareVO) getModel().getSelectedData();
		return aggVO != null && ((CostShareVO)aggVO.getParentVO()).getBillstatus()==BXStatusConst.DJZT_Sign;
	}
}