package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;

/**
 * 
 *	‘§Ã·µ•‘›¥Ê
 */
public class AccTempSaveAction extends NCAction {

	private static final long serialVersionUID = 1L;

	public AccTempSaveAction() {
		super();
		setBtnName(ErmActionConst.getTempSaveName());
		setCode(ErmActionConst.TEMPSAVE);
	}
	
	private AbstractAppModel model;
	private IEditor editor;

	private IErmAccruedBillManage appService;

	public void doAction(ActionEvent e) throws Exception {
		AggAccruedBillVO aggVo = getAggVo();
		aggVo.getParentVO().setAttributeValue(AccruedVO.BILLSTATUS, ErmAccruedBillConst.BILLSTATUS_TEMPSAVED);
		
		AggAccruedBillVO result = getAppService().tempSave(aggVo);
		
		if(getModel().getUiState()==UIState.ADD){
			getModel().directlyAdd(result);
		}else if(getModel().getUiState()==UIState.EDIT){
			getModel().directlyUpdate(result);
		}

		getModel().setUiState(UIState.NOT_EDIT);
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getUiState()==UIState.ADD){
			return true;
		}else if(getModel().getUiState()==UIState.EDIT){
			AggAccruedBillVO selectedData = (AggAccruedBillVO)getModel().getSelectedData();
			int billstatus = selectedData.getParentVO().getBillstatus();
			if(billstatus == ErmAccruedBillConst.BILLSTATUS_TEMPSAVED){
				return true;
			}
		}
		return  false;
	}

	private IErmAccruedBillManage getAppService(){
		if(appService == null){
			appService = NCLocator.getInstance().lookup(IErmAccruedBillManage.class);
		}
		return appService;
	}

	private AggAccruedBillVO getAggVo(){
		return (AggAccruedBillVO)getEditor().getValue();
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}
}
