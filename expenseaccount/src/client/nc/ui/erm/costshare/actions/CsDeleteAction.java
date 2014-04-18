package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.ui.erm.costshare.ui.CostShareModelService;
import nc.ui.erm.costshare.ui.CsBillManageModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.DeleteAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * @author luolch
 *
 * 单据删除动作，支持批量删除
 *
 */
@SuppressWarnings("serial")
public class CsDeleteAction extends DeleteAction {
	private boolean isCard;
	@Override
	public void doAction(ActionEvent e) throws Exception {
			CostShareModelService cs =  (CostShareModelService) ((CsBillManageModel)getModel()).getService();
			Object[] aggCs = ((BillManageModel)getModel()).getSelectedOperaDatas();
			//卡片处理方式：不弹出TextArea大文本框，显示UE规范错误
			if(isCard){
				AggCostShareVO aggVo = (AggCostShareVO)getModel().getSelectedData();
				CostShareVO csvo = (CostShareVO)aggVo.getParentVO();
				if (csvo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_BX) {
					ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0035")/*@res "错误"*/,
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012V575_0","0upp2012V575-0097"/*"删除单据失败:单据编号({0}) ,报销单生成不允许删除!"*/,null,
									new String[]{csvo.getBillno()}), 
							getModel().getContext());
				}else {
					aggCs = new AggCostShareVO[]{aggVo};
					MessageVO[] loginfo = null;
					loginfo = cs.newDelete(aggCs);
					//删除成功
					if (loginfo!=null) {
						((CsBillManageModel)getModel()).directlyDelete(aggCs);
					//单据状态显示成功
						ShowStatusBarMsgUtil.showStatusBarMsg(loginfo[0].toString(), getModel().getContext());
					}
				}
				
			}
			else {
				//没有选择复选框，默认认为光标选中框
				MessageVO[] mglog = new MessageVO[aggCs.length];
				for (int i = 0; i < aggCs.length; i++) {
					AggCostShareVO aggVo = (AggCostShareVO)aggCs[i];
					CostShareVO csvo = (CostShareVO)aggVo.getParentVO();
					if (csvo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_BX) {
						mglog[i] = new MessageVO(aggVo,ActionUtils.DELETE);
						mglog[i].setSuccess(false);
						mglog[i].setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012V575_0","0upp2012V575-0117")
						/*"报销单生成不允许删除!"*/);
					}else {
						try {
							mglog[i] = cs.newDelete(new AggCostShareVO[]{aggVo})[0];
						} catch (Exception e1) {
							mglog[i]  = new MessageVO(aggVo,ActionUtils.DELETE);
							mglog[i].setSuccess(false);
							mglog[i].setErrorMessage(e1.getMessage());
							nc.bs.logging.Logger.error(e1.getMessage(), e1);
						}
					}
				}
				
				List<String> successVosKey=new ArrayList<String>() ;
				for (int i = 0; i < mglog.length; i++) {
					if(mglog[i].isSuccess() ){
						successVosKey.add(mglog[i].getSuccessVO().getParentVO().getPrimaryKey());
						//((CsBillManageModel)getModel()).directlyDelete(new AggCostShareVO[] {(AggCostShareVO) mglog[i].getSuccessVO()});
					}
				}
				List<AggregatedValueObject> successVos=new ArrayList<AggregatedValueObject>() ;
				for(int j=0; j<aggCs.length;j++ ){
					if(successVosKey.contains(((AggCostShareVO)aggCs[j]).getParentVO().getPrimaryKey())){
						successVos.add(((AggCostShareVO)aggCs[j]));
					}
				}
				//更新表格数据
				((CsBillManageModel)getModel()).directlyDelete(successVos.toArray(new AggregatedValueObject[] {}));
				nc.ui.erm.util.ErUiUtil.showBatchResults(getModel().getContext(), mglog);
			}
		}
	
	@Override
	protected boolean isActionEnable() {
		Object[] datas = ((CsBillManageModel)getModel()).getSelectedOperaDatas();
		boolean issel = false;
		if (datas!=null) {
			for (int i = 0; i < datas.length; i++) {
				 CostShareVO csvo = ((CostShareVO)((AggCostShareVO)datas[i]).getParentVO());
				if (csvo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL&&csvo.getEffectstate() == IErmCostShareConst.CostShare_Bill_Effectstate_N) {
					// 自制单据，且未生效
					issel=true;
					break;
				}
			}
		}

		return model.getUiState()==UIState.NOT_EDIT&& issel;
	}
	public void setCard(boolean isCard) {
		this.isCard = isCard;
	}
	public boolean isCard() {
		return isCard;
	}

}