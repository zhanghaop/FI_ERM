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
 * ȡ����ӡ
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
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0043"), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0042")/*@res "�������ظ�ȡ����ӡ��"*/, getModel().getContext());
			return;
		}

		AggCostShareVO[] aggvos = null;
		try {
			aggvos =  ((CostShareModelService)((BillManageModel)getModel()).getService()).printNormal(new String[]{aggVO.getParentVO().getPrimaryKey()},
					null,null);
		} catch (Exception e1) {
			nc.bs.logging.Logger.error(e1.getMessage(), e1);
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0043"), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0043")/*@res "ȡ����ӡʧ�ܣ�"*/, getModel().getContext());
		}
		((BillManageModel)getModel()).directlyUpdate(aggvos);
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0044")/*@res "ȡ����ӡ�ɹ�"*/, getModel().getContext());
	}
	/**
	 * û����ʽ��ӡ��ȡ����ʽ��ӡ��ť��������
	 */
	@Override
	protected boolean isActionEnable() {
		AggCostShareVO aggVO = (AggCostShareVO) getModel().getSelectedData();
		return aggVO != null && ((CostShareVO)aggVO.getParentVO()).getBillstatus()==BXStatusConst.DJZT_Sign && ((CostShareVO)aggVO.getParentVO()).getPrinter()!=null;
	}

}