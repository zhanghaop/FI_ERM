package nc.ui.erm.report.comp;

import java.awt.Component;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillTableCellRenderer;

public class ErmDataTypeCellRenderer extends BillTableCellRenderer {

    private static final long serialVersionUID = 2362693718285841159L;
    private Map<String, String> cache = new TreeMap<String, String>();
    private UIRefPane refPane;
    
    public ErmDataTypeCellRenderer(UIRefPane refPane) {
        super();
        this.refPane = refPane;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // TODO Auto-generated method stub
        Component result =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        if (cache.isEmpty()) {
            AbstractRefModel refModel = refPane.getRefModel();
            Vector data = refModel.getData();
            int pkIndex = refModel.getFieldIndex(refModel.getPkFieldCode());
            int nameIndex = refModel.getFieldIndex(refModel.getRefNameField());
            for (Object obj : data) {
                Vector rowData = (Vector)obj;
                cache.put((String)rowData.get(pkIndex), (String)rowData.get(nameIndex));
            }
            
        }
        if (value != null) {
            ((JLabel)result).setText(cache.get(value));
        }
        return result;
    }

    
    
}
