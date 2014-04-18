package nc.ui.erm.report.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.ui.uif2.model.HierachicalDataAppModel;

public class ErmReportTypeHierachicalDataAppModel extends
        HierachicalDataAppModel {
    
    @Override
    public DefaultMutableTreeNode findNodeByBusinessObjectId(Object target_id) {

        return findNodeByBusinessObject(target_id);
    }

    @Override
    public DefaultMutableTreeNode findNodeByBusinessObject(Object businessObject) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getTree().getRoot();
        @SuppressWarnings("rawtypes")
        Enumeration e = root.preorderEnumeration();
        e.nextElement();
        while(e.hasMoreElements())
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject().equals(businessObject)) {
                return node;
            }
        } 
        return null;
    }

    public static enum ReportType {
        LOAN_ACC {
            @Override
            public String getTableName() {
                return "zb";
            }

            @Override
            public String getShowName() {
                return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("erm_report","report_type001");/* @res "借款类账表" */
            }

            @Override
            public String getBeanId() {
                return "e0499b58-c604-48a6-825b-9a7e4d6dacca";
            }
            
            private String[] subTypes = new String[]{"loandetail", "loanbalance", "loanaccount"};

            @Override
            public String[] getSubTypes() {
                return subTypes;
            }
        },
        EXPENSE_ACC {
            @Override
            public String getTableName() {
                return "cs";
            }

            @Override
            public String getShowName() {
                return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("erm_report","report_type002");/* @res "费用类账表" */
            }

            @Override
            public String getBeanId() {
                return "3fecbcfe-bca0-4fb1-b4ea-46d8b6616337";
            }

            private String[] subTypes = new String[]{"expensebalance", "expensedetail"};
            
            @Override
            public String[] getSubTypes() {
                return subTypes;
            }
        },
        MATTER_APP {
            @Override
            public String getTableName() {
                return "er_mtapp_detail";
            }

            @Override
            public String getShowName() {
                return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("erm_report","report_type003");/* @res "费用申请类账表" */
            }

            @Override
            public String getBeanId() {
                return "e3167d31-9694-4ea1-873f-2ffafd8fbed8";
            }

            private String[] subTypes = new String[]{"expensebalance", "expensedetail"};
            
            @Override
            public String[] getSubTypes() {
                return subTypes;
            }
        };
        
        public abstract String getTableName();
        
        public abstract String getShowName();
        
        public abstract String getBeanId();
        
        public abstract String[] getSubTypes();
    }
    
}
