package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.erm.util.action.ErmActionInitializer;
import nc.ui.erm.costshare.ui.CostShareModelService;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;

/**
 * @author luolch
 *
 *         暂存活动
 *
 */
public class TempSaveAction extends SaveAction {
	private static final long serialVersionUID = 1L;

	public TempSaveAction() {
		super();
		ErmActionInitializer.initializeAction(this, ErmActionConst.TEMPSAVE);
	}

	public void doAction(ActionEvent e) throws Exception {

		AggCostShareVO value = (AggCostShareVO) getEditor().getValue();
		validate(value);
		((CostShareVO) value.getParentVO())
				.setEffectstate(IErmCostShareConst.CostShare_Bill_Effectstate_N);
		BillManageModel bm = (BillManageModel) getModel();
		CostShareModelService ser = (CostShareModelService) bm.getService();
		if (getModel().getUiState() == UIState.ADD) {
			((CostShareVO) value.getParentVO()).setStatus(VOStatus.NEW);
			value = ser.getIBillmagePrivateService().tempSaveVO(value);
			bm.directlyAdd(value);
		}
		AggCostShareVO aggcs = ((AggCostShareVO)value);
		if (getModel().getUiState() == UIState.EDIT) {
			CircularlyAccessibleValueObject[] bodyvo = ((BillForm)getEditor()).getBillCardPanel().getBillModel().getBodyValueVOs(CShareDetailVO.class.getName());
			CircularlyAccessibleValueObject[] cs = aggcs.getChildrenVO();
			if(cs!=null && cs.length !=0 ) {
				//重新包装vo，保存vo = 现表体vo+表体已改变vo
				List<CShareDetailVO> csList = new ArrayList<CShareDetailVO>();
				for (int i = 0; i < bodyvo.length; i++) {
					csList.add((CShareDetailVO)bodyvo[i]);
				}
				CircularlyAccessibleValueObject[] changeBodyVo = ((BillForm)getEditor()).getBillCardPanel().getBillModel().getBodyValueChangeVOs(CShareDetailVO.class.getName());
				for (int i = 0; i < changeBodyVo.length; i++) {
				    if(changeBodyVo[i].getStatus()== VOStatus.DELETED){
				    	csList.add((CShareDetailVO)changeBodyVo[i]);
				    }
				}
				aggcs.setChildrenVO(csList.toArray(new CShareDetailVO[0]));
			}else {
				aggcs.setChildrenVO(bodyvo);
			}


			((CostShareVO) value.getParentVO()).setStatus(VOStatus.UPDATED);
			value = ser.getIBillmagePrivateService().tempSaveVO(value);
			bm.directlyUpdate(value);
		}

		getModel().setUiState(UIState.NOT_EDIT);
		
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0061")/* @res "暂存成功" */, getModel().getContext());
	}
	@Override
	protected boolean isActionEnable() {
		 if(getModel().getUiState()==UIState.EDIT){
				AggCostShareVO selectedvo = (AggCostShareVO) getModel().getSelectedData();
				CostShareVO csvo = (CostShareVO) selectedvo.getParentVO();
				return csvo.getBillstatus() == BXStatusConst.DJZT_TempSaved;
		}
		return getModel().getUiState()==UIState.ADD;
	}

}