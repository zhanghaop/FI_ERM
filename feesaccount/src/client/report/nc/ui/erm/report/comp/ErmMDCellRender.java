package nc.ui.erm.report.comp;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;

public class ErmMDCellRender extends BillTableCellRenderer {

    private static final long serialVersionUID = 3825117657637530366L;

    private BatchBillTable billTable;

    public ErmMDCellRender(BatchBillTable billTable) {
        super();
        this.billTable = billTable;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        if (value == null || "1".equals(value)) {
            if (billTable.getModel().getUiState() == UIState.NOT_EDIT) {
                String name = (String)billTable.getBillCardPanel().getBodyValueAt(row, "dsp_objname");
                ((JLabel)result).setText(name);
                billTable.getBillCardPanel().setBodyValueAt(name, row, "qry_objdatatype");
            }
        }
        return result;
    }
    
}
