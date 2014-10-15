package nc.ui.erm.report.model;

import nc.ui.erm.report.model.ErmReportTypeHierachicalDataAppModel.ReportType;
import nc.ui.uif2.model.BatchBillTableModel;

public class ErmBatchBillTableModel extends BatchBillTableModel {

    private ReportType reportType;

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    
}
