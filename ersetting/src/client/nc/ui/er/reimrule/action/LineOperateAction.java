package nc.ui.er.reimrule.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.AbstractLineOperateAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.pub.BusinessException;

@SuppressWarnings("serial")
public class LineOperateAction  extends AbstractLineOperateAction{
	
	private String move = null;
	private int i = 0;
	private BatchBillTable editor = null;
	public LineOperateAction()
	{
		super();
	} 
	
	@Override 
	protected boolean isActionEnable() {

		if(move.equals("up")){
			ActionInitializer.initializeAction(this, IActionCode.PRE);
			setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000043")/**
			 * @*
			 * res*"上移一行"
			 */
			);
			putValue(Action.SHORT_DESCRIPTION,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000043")/**
					 * @*
					 * res*"上移一行"
					 */
					+ "(Ctrl+U)");
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));
			putValue(Action.SMALL_ICON, ThemeResourceCenter.getInstance()
					.getImage("themeres/ui/toolbaricons/move_up.png"));
			i=-1;
		}
		else if(move.equals("top")){
			ActionInitializer.initializeAction(this, IActionCode.FIRST);
			setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000044")/**
					 * @*
					 * res*"移到顶部"
					 */
					);
			putValue(Action.SHORT_DESCRIPTION, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000044")/**
					 * @*
					 * res*"移到顶部"
					 */
					 + "(Ctrl+T)");
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
			putValue(Action.SMALL_ICON, ThemeResourceCenter.getInstance()
					.getImage("themeres/ui/toolbaricons/top.png"));
			i=-2;
		}
		else if(move.equals("down")){
			ActionInitializer.initializeAction(this, IActionCode.NEXT);
			setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000045")/**
					 * @*
					 * res*"下移一行"
					 */
					);
			putValue(Action.SHORT_DESCRIPTION, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000045")/**
					 * @*
					 * res*"下移一行"
					 */
					 + "(Ctrl+D)");
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
			putValue(Action.SMALL_ICON, ThemeResourceCenter.getInstance()
					.getImage("themeres/ui/toolbaricons/move_down.png"));
			i=1;
		}
		else if(move.equals("bottom")){
			ActionInitializer.initializeAction(this, IActionCode.LAST);
			setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000046")/**
					 * @*
					 * res*"移到底部"
					 */
					);
			putValue(Action.SHORT_DESCRIPTION, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000046")/**
					 * @*
					 * res*"移到底部"
					 */
					 + "(Ctrl+B)");
			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
			putValue(Action.SMALL_ICON, ThemeResourceCenter.getInstance()
					.getImage("themeres/ui/toolbaricons/bottom.png"));
			i=2;
		}
		return getModel().getUiState() == UIState.EDIT && getModel().getRows().size() > 0 && getModel().getSelectedIndex() != -1;
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		
		int index = getModel().getSelectedIndex();
		if(index == -1)
			return ;
	
		Integer[] selectedRow = getModel().getSelectedOperaRows();
		if (selectedRow == null || selectedRow.length == 0)
			return;

		for (int j = 0; j < selectedRow.length - 1; j++) {
			if (selectedRow[j] + 1 != selectedRow[j + 1]) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000491")/*
															 * @res
															 * "请选中相邻的行进行移动操作！"
															 */);
			}
		}

		if(i == 0)
			return;
		int row1 = 0, row2 = 0, row3 = 0, row4 = 0, row5 = 0, row6 = 0, row7 = 0, row8 = 0, selS = 0, selT = 0;
		List<Object> bodyValueVOs = getModel().getRows();
		List<Object> list = new ArrayList<Object>();

		int fromRow = selectedRow[0];
		int toRow = selectedRow[selectedRow.length - 1];

		if (i == 1 || i == 2)
			if (toRow == bodyValueVOs.size() - 1)
				return;

		if (i == -1 || i == -2)
			if (fromRow == 0)
				return;

		switch (i) {
		case 1:
			row1 = 0;
			row2 = fromRow;
			row3 = toRow + 1;
			row4 = toRow + 2;
			row5 = fromRow;
			row6 = toRow + 1;
			row7 = toRow + 2;
			row8 = bodyValueVOs.size();
		
			selS = fromRow + 1;
			selT = toRow + 1;
			break;
		case 2:
			row1 = 0;
			row2 = fromRow;
			row3 = toRow + 1;
			row4 = bodyValueVOs.size();
			row5 = fromRow;
			row6 = toRow + 1;
		
			selS = bodyValueVOs.size() - 1 - (selectedRow.length - 1);
			selT = bodyValueVOs.size() - 1;
			break;
		case -1:
			row1 = 0;
			row2 = fromRow - 1;
			row3 = fromRow;
			row4 = toRow + 1;
			row5 = fromRow - 1;
			row6 = fromRow;
			row7 = toRow + 1;
			row8 = bodyValueVOs.size();
		
			selS = fromRow - 1;
			selT = toRow - 1;
			break;
		case -2:
			row1 = fromRow;
			row2 = toRow + 1;
			row3 = 0;
			row4 = fromRow;
			row5 = toRow + 1;
			row6 = bodyValueVOs.size();
		
			selS = 0;
			selT = selectedRow.length - 1;
			break;
		
		default:
			break;
		}
		
		for (int p = row1; p < row2; p++) {
			list.add(bodyValueVOs.get(p));
		}
		for (int p = row3; p < row4; p++) {
			list.add(bodyValueVOs.get(p));
		}
		for (int p = row5; p < row6; p++) {
			list.add(bodyValueVOs.get(p));
		}
		for (int p = row7; p < row8; p++) {
			list.add(bodyValueVOs.get(p));
		}
		for(int i=0;i<list.size();i++){
			bodyValueVOs.set(i, list.get(i));
		}
		int[] selectedOperaRows = new int[selectedRow.length];
		for(int j = 0;j<=selT-selS;j++)
			selectedOperaRows[j] = selS+j;
		getModel().setSelectedOperaRows(selectedOperaRows);
		getEditor().setValue(getModel().getRows().toArray());
		getBillCardPanel().getBillTable().getSelectionModel()
				.setSelectionInterval(selS, selT);
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

	public BillCardPanel getBillCardPanel(){
		return getEditor().getBillCardPanel();
	}
}
