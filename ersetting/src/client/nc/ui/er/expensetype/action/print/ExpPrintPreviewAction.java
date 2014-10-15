package nc.ui.er.expensetype.action.print;

/**
 * <p>
 *  档案模板打印界面。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-11-16 下午04:14:47
 */
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import nc.ui.arap.bx.print.ErmMultiPanelDirectPrintAction;
import nc.ui.uif2.editor.IBillCardPanelEditor;


public class ExpPrintPreviewAction extends ErmMultiPanelDirectPrintAction {
	
	private static final long serialVersionUID = 1L;
	private IBillCardPanelEditor cardPanel;

	private void initPrintPanel() {
		List<IBillCardPanelEditor> editors = new ArrayList<IBillCardPanelEditor>(1);
		editors.add(getCardPanel());
		setHeadEditors(editors);
		setTailEditor(getCardPanel());
		List<JPanel> bodyEditors = new ArrayList<JPanel>(1);
		bodyEditors.add((JPanel) getCardPanel());
		setBodyEditors(bodyEditors);
	}

	public IBillCardPanelEditor getCardPanel() {
		return cardPanel;
	}

	public void setCardPanel(IBillCardPanelEditor cardPanel) {
		this.cardPanel = cardPanel;
		initPrintPanel();
	}

}
