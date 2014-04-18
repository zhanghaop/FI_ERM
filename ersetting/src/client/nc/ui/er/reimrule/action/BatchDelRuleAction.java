package nc.ui.er.reimrule.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.er.reimrule.view.DimensTable;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.pub.SuperVO;

@SuppressWarnings("serial")
public class BatchDelRuleAction extends BatchDelLineAction{
	BatchBillTable editor;
	@Override
	public void doAction(ActionEvent e) throws Exception {
		String djlxorg = ((DimensTable)getEditor()).getOrgPanel().getRefPane().getUITextField().getValue().toString();
		//djlx  org  isconfig
		String[] str = djlxorg.split(";");
		List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(str[0]);
		if(vos!=null && vos.size()>0){
			for(SuperVO vo:vos){
				ReimRuleDimVO dimvo = (ReimRuleDimVO)getModel().getSelectedData();
				if(vo.getAttributeValue(dimvo.getCorrespondingitem()) != null){
					MessageDialog.showHintDlg(getEditor(),"错误","该列对应的报销标准上有值，请删除！");
					return;
				}
			}
		}
		getModel().delLine(-1);
	}
	public BatchBillTable getEditor() {
		return editor;
	}
	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}
}
