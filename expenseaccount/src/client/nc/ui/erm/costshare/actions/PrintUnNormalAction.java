package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.costshare.ui.CostShareModelService;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;

/**
 * @author luolch
 *
 * 取消打印
 *
 *
 */
@SuppressWarnings({ "serial" })
public class PrintUnNormalAction extends CsPrintAction {

	public PrintUnNormalAction() {
		super();
		setBtnName(ErmActionConst.getCancelprintName());
		setCode(ErmActionConst.CANCELPRINT);
		putValue(Action.ACCELERATOR_KEY, null);
		putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getOfficalprintName());
	}
	@Override
	public void doAction(ActionEvent e) {
		AggCostShareVO aggVO = (AggCostShareVO) getModel().getSelectedData();
		if (((CostShareVO)aggVO.getParentVO()).getPrinter()==null) {
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0043"), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0042")/*@res "不允许重复取消打印！"*/, getModel().getContext());
			return;
		}

		AggCostShareVO[] aggvos = null;
		try {
			aggvos =  ((CostShareModelService)((BillManageModel)getModel()).getService()).printNormal(new String[]{aggVO.getParentVO().getPrimaryKey()},
					null,null);
		} catch (Exception e1) {
			nc.bs.logging.Logger.error(e1.getMessage(), e1);
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0043"), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0043")/*@res "取消打印失败！"*/, getModel().getContext());
		}
		((BillManageModel)getModel()).directlyUpdate(aggvos);
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0044")/*@res "取消打印成功"*/, getModel().getContext());
	}
	/**
	 * 没有正式打印，取消正式打印按钮不可以用
	 */
	@Override
	protected boolean isActionEnable() {
		AggCostShareVO aggVO = (AggCostShareVO) getModel().getSelectedData();
		return aggVO != null && ((CostShareVO)aggVO.getParentVO()).getBillstatus()==BXStatusConst.DJZT_Sign && ((CostShareVO)aggVO.getParentVO()).getPrinter()!=null;
	}

}