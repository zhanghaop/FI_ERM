package nc.ui.erm.report.model;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.vo.bd.access.tree.AbastractTreeCreateStrategy;

public class ErmQryObjTreeCreateStrategy extends AbastractTreeCreateStrategy {

    @Override
    public boolean isCodeTree() {
        return false;
    }

    @Override
    public DefaultMutableTreeNode getRootNode() {
        return new DefaultMutableTreeNode(getRootName());
    }

    @Override
    public DefaultMutableTreeNode createTreeNode(Object obj) {
        
        return super.createTreeNode(obj);
    }

    @Override
    public Object getNodeId(Object obj) {
        return obj;
    }
    
    private String getRootName() {
        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("erm_report","report_type004");/* @res "’À±Ì¿‡–Õ" */
    }

    @Override
    public Object getParentNodeId(Object obj) {
        return getRootName();
    }
    
    

}
