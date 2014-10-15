package nc.ui.erm.report.model;

import nc.ui.erm.report.model.ErmReportTypeHierachicalDataAppModel.ReportType;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;

public class ErmQryObjRegDataManager implements IAppModelDataManager {

    private ErmReportTypeHierachicalDataAppModel treeModel;
    
    private BatchBillTableModel model;

    public void initModel() {
        if (treeModel.getAllDatas() == null || treeModel.getAllDatas().length == 0) {
            treeModel.initModel(ReportType.values());
            treeModel.setSelectedData(treeModel.getAllDatas()[0]);
        }
        if (treeModel.getSelectedData() instanceof ReportType) {
            treeModel.setSelectedData(treeModel.getSelectedData());
        } else {
            model.initModel(null);
        }
    }

    public BatchBillTableModel getModel() {
        return model;
    }

    public void setModel(BatchBillTableModel model) {
        this.model = model;
    }

    public ErmReportTypeHierachicalDataAppModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(ErmReportTypeHierachicalDataAppModel treeModel) {
        this.treeModel = treeModel;
    }
    
    
}
