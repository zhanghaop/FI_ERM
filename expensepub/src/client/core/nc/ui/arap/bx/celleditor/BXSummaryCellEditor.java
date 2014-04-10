package nc.ui.arap.bx.celleditor;

import java.awt.Dimension;
import java.util.EventObject;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillCellEditor;

/**
 * 摘要(即事由字段特殊处理)
 * 
 * @author chendya
 * 
 */
public class BXSummaryCellEditor extends BillCellEditor {

	private static final long serialVersionUID = 8683190344977055977L;

	public BXSummaryCellEditor(UIRefPane refPane) {
		super(refPane);
		initialize(refPane);
	}

	private void initialize(final UIRefPane refpane) {
		editorComponent = refpane;
		UITextField textField = refpane.getUITextField();
		this.clickCountToStart = 1;
		refpane.setSize(new Dimension(new Double(refpane.getSize().getWidth()).intValue(), 23));
		refpane.setPreferredSize(new Dimension(new Double(refpane.getPreferredSize().getWidth()).intValue(), 23));
		refpane.getUIButton().requestFocus();
		delegate = new EditorDelegate() {
			
			private static final long serialVersionUID = -6528545265180076887L;

			public void setValue(Object value) {
				String str = (value == null) ? "" : value.toString();
				((UIRefPane) editorComponent).setValue(str);
				((UIRefPane) editorComponent).getRefModel().setSelectedData(null);
				((UIRefPane) editorComponent).getUITextField().setText(str);
				if (!((UIRefPane) editorComponent).getUITextField().hasFocus()){
					((UIRefPane) editorComponent).getUITextField().selectAll();
				}
			}

			public Object getCellEditorValue() {
				if (((UIRefPane) editorComponent).getRefName() != null)
					return ((UIRefPane) editorComponent).getRefName();
				UIRefPane pan = (UIRefPane) editorComponent;
				if (pan.getUITextField().getText() == null || "".equals(pan.getUITextField().getText())) {
					Object obj = pan.getValueObj();
					if (obj == null || ((Object[]) obj)[0] == null) {
						pan.setValueObj(null);
					} else {
						return obj;
					}
				}
				return pan.getUITextField().getText();
			}

			public boolean startCellEditing(EventObject anEvent) {
				editorComponent.repaint();
				return true;
			}

			public boolean stopCellEditing() {
				if (!refpane.stopEditing()){
					return false;
				}
				boolean flag = super.stopCellEditing();
				return flag;
			}
		};
		//加监听
		textField.addActionListener(delegate);
	}

}
