package nc.ui.erm.report.comp;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import nc.ui.erm.report.model.ErmReportTypeHierachicalDataAppModel.ReportType;

public class ErmQryObjTypeTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 6220721286819549203L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        
        Component c =  super.getTreeCellRendererComponent(tree, value, sel, expanded,
                leaf, row, hasFocus);
        if(value instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof String) {
                setText((String)node.getUserObject());
            } else if (node.getUserObject() instanceof ReportType) {
                String text = ((ReportType)node.getUserObject()).getShowName();
                setText(text);
            }
        }
        return c;
    }
}
