package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;

public class CsSaveAction extends SaveAction {
	private static final long serialVersionUID = 1L;
	

	public void doAction(ActionEvent e) throws Exception {
		
		Object value = getEditor().getValue();
		
		doValidate(value);
		
		if(getModel().getUiState()==UIState.ADD){
			getModel().add(value);
		}
		AggCostShareVO aggcs = ((AggCostShareVO)value);
		if(getModel().getUiState()==UIState.EDIT){
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
			
			getModel().update(aggcs);
		}
		showSuccessInfo();
		getModel().setUiState(UIState.NOT_EDIT);
	}


	private void doValidate(Object value) throws BusinessException{
		//新增需要校验费用结转单的日期和报销单的日期：结转单的日期不可以早于报销单的日期
		AggCostShareVO aggcs = ((AggCostShareVO)value);
		if(aggcs!=null){
			UFDate bxdjrq = ((CostShareVO)aggcs.getParentVO()).getBx_djrq();
			UFDate csbilldate = ((CostShareVO)aggcs.getParentVO()).getBilldate();
			if(bxdjrq!=null && bxdjrq.compareTo(csbilldate)>0){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102","UPP2006030102-001120"))/*结转单的日期不可以早于报销单的日期**/;
			}
		}
		
		BillData data = ((BillForm)getEditor()).getBillCardPanel().getBillData();
		try {
			if(data != null)
				data.dataNotNullValidate();
		} catch (nc.vo.pub.ValidationException ex) {
			throw new BusinessExceptionAdapter(ex);
		}
	}

}
