package nc.ui.er.reimrule.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;

import nc.ui.er.reimrule.dialog.BatchEditDialog;
import nc.ui.er.reimrule.view.ControlTable;
import nc.ui.er.reimrule.view.DimensTable;
import nc.ui.er.reimrule.view.RuleTable;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.pub.SuperVO;

@SuppressWarnings("serial")
public class SaveAction extends BatchSaveAction{

	@Override
	public void doAction(ActionEvent e){
		try{
			List<SuperVO> vos = null;
			if(getEditor() instanceof DimensTable){
				vos = ((DimensTable) getEditor()).Save();
				Container container = getEditor().getParent();
				while(container!=null && !(container instanceof BatchEditDialog)){
					container = container.getParent();
				}
				if(container != null){
					((BatchEditDialog)container).setReturnvo(vos);
					((BatchEditDialog)container).closeOK();
				}
			}
			else if(getEditor() instanceof ControlTable){
				vos = ((ControlTable) getEditor()).Save();
				Container container = getEditor().getParent();
				while(container!=null && !(container instanceof BatchEditDialog)){
					container = container.getParent();
				}
				if(container != null){
					((BatchEditDialog)container).setReturnvo(vos);
					((BatchEditDialog)container).closeOK();
				}
			}
			else if(getEditor() instanceof RuleTable){
				((RuleTable) getEditor()).Save();
				getModel().setUiState(UIState.NOT_EDIT);
			}
		}catch (Exception e1) {
			MessageDialog.showHintDlg(getEditor(),"´íÎó",e1.getMessage());
		}
	} 
}
