package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;

/**
 * @author luolch
 *　
 *　单据增加 Action　
 *
 */
@SuppressWarnings("serial")
public class ModifAction extends nc.ui.uif2.actions.EditAction{


	public void doAction(ActionEvent e) throws Exception{
		AggCostShareVO agcs = (AggCostShareVO) getModel().getSelectedData();
		CostShareVO csvo = (CostShareVO) agcs.getParentVO();
		if(csvo.getEffectstate() == IErmCostShareConst.CostShare_Bill_Effectstate_Y){
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0014")/*@res "错误"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0015")/*@res "已经生效，不能修改！"*/);
		}else {
			super.doAction(e);
		}
	}

	@Override
	protected boolean isActionEnable() {
		AggCostShareVO aggVo = (AggCostShareVO) getModel().getSelectedData();
		if (aggVo==null) {
			return false;
		}
		CostShareVO csvo = (CostShareVO)aggVo.getParentVO();
		return super.isActionEnable()&&csvo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL&&csvo.getEffectstate() == IErmCostShareConst.CostShare_Bill_Effectstate_N;
	}

}