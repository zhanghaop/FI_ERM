package nc.ui.erm.report.action;

import nc.ui.erm.report.model.ErmBatchBillTableModel;
import nc.ui.uif2.actions.batch.BatchAddLineAction;

public class ErmBatchAddLineAction extends BatchAddLineAction {

    private static final long serialVersionUID = -8305898948001183072L;

    @Override
    protected boolean isActionEnable() {
        ErmBatchBillTableModel model = (ErmBatchBillTableModel)getModel();
        return super.isActionEnable() && model.getReportType() != null;
    }

    
}
