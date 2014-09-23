package nc.ui.er.reimrule.action;

import java.awt.Container;
import java.awt.event.ActionEvent;

import nc.ui.er.reimrule.dialog.BatchEditDialog;
import nc.ui.er.reimrule.view.ControlTable;
import nc.ui.er.reimrule.view.DimensTable;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.actions.batch.BatchCancelAction;


/**
 * 按钮编辑的动作
 * 
 * @author shiwla
 *
 */
@SuppressWarnings("serial")
public class CancelAction extends BatchCancelAction {

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		try{
			if(getEditor() instanceof DimensTable){
				Container container = getEditor().getParent();
				while(container!=null && !(container instanceof BatchEditDialog)){
					container = container.getParent();
				}
				if(container != null){
					((UIDialog)container).closeCancel();
				}
			}
			else if(getEditor() instanceof ControlTable){
				Container container = getEditor().getParent();
				while(container!=null && !(container instanceof BatchEditDialog)){
					container = container.getParent();
				}
				if(container != null){
					((UIDialog)container).closeCancel();
				}
			}
		}catch (Exception e1) {
			MessageDialog.showHintDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000039")/**
					 * @*
					 * res* "错误"
					 */
					, e1.getMessage());
		}
	} 
	
}
