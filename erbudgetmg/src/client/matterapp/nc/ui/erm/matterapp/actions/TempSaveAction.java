package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;

/**
 * @author chenshuaia ÔÝ´æ
 *
 */
public class TempSaveAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private AbstractAppModel model;
	private IEditor editor;

	private IErmMatterAppBillManage appService;

	public TempSaveAction() {
		super();
		this.setBtnName(ErmActionConst.getTempSaveName());
		this.setCode(ErmActionConst.TEMPSAVE);
	}

	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO aggVo = getAggVo();
		aggVo.getParentVO().setAttributeValue(MatterAppVO.BILLSTATUS, ErmMatterAppConst.BILLSTATUS_TEMPSAVED);
		AggMatterAppVO result = getAppService().tempSave(aggVo);
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
			AggMatterAppVO selectedData = (AggMatterAppVO)getModel().getSelectedData();
			int billstatus = selectedData.getParentVO().getBillstatus();
			if(billstatus == ErmMatterAppConst.BILLSTATUS_TEMPSAVED){
				return true;
			}
		}
		return  false;
	}

	private IErmMatterAppBillManage getAppService(){
		if(appService == null){
			appService = NCLocator.getInstance().lookup(IErmMatterAppBillManage.class);
		}
		return appService;
	}

	private AggMatterAppVO getAggVo(){
		return (AggMatterAppVO)getEditor().getValue();
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