package nc.ui.erm.report.validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.IBatchValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.itf.fipub.report.IReportInitializeQuery;
import nc.ui.erm.report.comp.QryObjRegBatchBillTable;
import nc.ui.erm.report.model.ErmBatchBillTableModel;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.arap.util.SqlUtils_Pub;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.fipub.report.AggReportInitializeVO;
import nc.vo.fipub.report.QueryObjVO;
import nc.vo.fipub.report.ReportInitializeItemVO;
import nc.vo.pub.BusinessException;

public class ErmQryObjBatchValidationService implements IBatchValidationService {

    private QryObjRegBatchBillTable billTable;
    
    public void setBillTable(QryObjRegBatchBillTable billTable) {
        this.billTable = billTable;
    }
    
    private IReportInitializeQuery reportInitializeQuery;

    public IReportInitializeQuery getReportInitializeQuery() {
        if (reportInitializeQuery == null) {
            reportInitializeQuery = NCLocator.getInstance().lookup(
                    IReportInitializeQuery.class);
        }
        return reportInitializeQuery;
    }

    @Override
    public void validate(Object obj) throws ValidationException {
        try {
            BillCardPanel cardPanel = billTable.getBillCardPanel(); 
            cardPanel.getBillData().dataNotNullValidate();

            ValidationException excep = new ValidationException();
            int rowCount = cardPanel.getBillModel().getRowCount();
            Map<String, String> rowMap = new HashMap<String, String>();
            for (int row = 0; row < rowCount; row++) {
                String val = (String)cardPanel.getBodyValueAt(row, "tallyfieldname");
                String qryName = rowMap.get(val);
                if (qryName == null) {
                    String resid = (String)cardPanel.getBodyValueAt(row, "resid");
                    String name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap", resid);
                    rowMap.put(val, name);
                } else {
                    String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                            "feesaccount_0", "02011001-0127", null, 
                            new String[]{ qryName });/* @res "查询对象{0}不能重复！" */
                    ValidationFailure failure = new ValidationFailure(message);
                    excep.addValidationFailure(failure);
                    throw excep;
                }
            }
            
            ErmBatchBillTableModel model = (ErmBatchBillTableModel)billTable.getModel();
            String where = " ownmodule = 'erm' and " + SqlUtils_Pub.getInStr("reporttype", model.getReportType().getSubTypes());
            Collection<AggReportInitializeVO> releasedReport = null;
            try {
                releasedReport = getReportInitializeQuery().getInitializedReportByCond(where);
            } catch (BusinessException e) {
                Logger.error(e.getMessage(), e);
            }
            
            BatchOperateVO batchOperateVO = (BatchOperateVO)obj;
            Object[] rows = batchOperateVO.getUpdObjs();
            if (rows != null && rows.length > 0) {
                for (Object vo : rows) {
                    QueryObjVO qryObj = (QueryObjVO) vo;
                    QueryObjVO oldObj = (QueryObjVO)billTable.getModel().getBeforeUpdateObject(qryObj);
//                    Logger.error("old register : tallyfieldname-" + oldObj.getTallyfieldname());
                    String oldName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap", oldObj.getResid());
                    if (oldName == null) {
                        oldName = oldObj.getDsp_objname();
                    }
                    String newName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap", qryObj.getResid());
//                    Logger.error("updated : tallyfieldname-" + qryObj.getTallyfieldname());
                    
                    if (releasedReport == null || releasedReport.isEmpty()) {
                        break;
                    }
                    release : for (AggReportInitializeVO agg : releasedReport) {
                        if (agg.getChildrenVO() == null || agg.getChildrenVO().length == 0) {
                            continue;
                        }
                        for (ReportInitializeItemVO body : (ReportInitializeItemVO[])agg.getChildrenVO()) {
//                            Logger.error("released : tallyfieldname-" + body.getTallyfieldname() + 
//                                    "   qry_field-" + body.getQry_objfieldname());
                            if (oldObj.getTallyfieldname().equalsIgnoreCase((body.getTallyfieldname()))) {
                                String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                        "feesaccount_0", "02011001-0126", null, 
                                        new String[]{ oldName, newName });/* @res "查询对象{0}已经被账表使用，不能修改为{1}。" */
                                ValidationFailure failure = new ValidationFailure(message);
                                excep.addValidationFailure(failure);
                                break release;
                            }
                        }
                    }
                }
                if (!excep.getFailures().isEmpty()) {
                    throw excep;
                }
            }
        } catch (nc.vo.pub.ValidationException e) {
            ValidationFailure failure = new ValidationFailure(e.getMessage());
            ValidationException excep = new ValidationException();
            excep.addValidationFailure(failure);
            throw excep;
        }
    }

    @Override
    public int[] unNecessaryData(List<Object> rows) {
        return null;
    }

}
