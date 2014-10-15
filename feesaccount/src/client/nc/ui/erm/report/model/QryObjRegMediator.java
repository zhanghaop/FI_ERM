package nc.ui.erm.report.model;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.fipub.queryobjreg.IReportQueryObjRegQuery;
import nc.ui.erm.report.model.ErmReportTypeHierachicalDataAppModel.ReportType;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.UIStateChangeEvent;
import nc.ui.uif2.components.TreePanel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.fipub.report.QueryObjVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class QryObjRegMediator  implements AppEventListener {
    
    private HierachicalDataAppModel hierachicalDataAppModel;
    private ErmBatchBillTableModel batchBillTableModel;
    private TreePanel treePanel;
    
    @Override
    public void handleEvent(AppEvent event) {
        if (event.getSource() == hierachicalDataAppModel) {
            Object obj = hierachicalDataAppModel.getSelectedData();
            if (obj instanceof ReportType && 
                    AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
                ReportType reportType = (ReportType)obj;
                batchBillTableModel.setReportType(reportType);
                filterQryObj(reportType.getTableName());
            } else {
                batchBillTableModel.setReportType(null);
                batchBillTableModel.initModel(null);
            }
        } else if (event.getSource() == batchBillTableModel && event instanceof UIStateChangeEvent) {
            UIStateChangeEvent stateEve = (UIStateChangeEvent)event;
            boolean isEnable = (stateEve.getNewState() == UIState.NOT_EDIT);
            treePanel.getTree().setEnabled(isEnable);
        }
    }
    
    private void filterQryObj(String tableName) {
        String where = " ownmodule = 'erm' and dr = 0 and dsp_objtablename = '" + tableName + "' ";
        List<QueryObjVO> queryObjVOs;
        try {
            queryObjVOs = NCLocator.getInstance().lookup(
                    IReportQueryObjRegQuery.class).getRegisteredQueryObjByClause(where);
            // 处理多语显示问题
            String dspName = null;
            for (QueryObjVO queryObjVO : queryObjVOs) {
                if (StringUtils.isNotBlank(queryObjVO.getResid())) {
                    dspName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap", queryObjVO.getResid());
                } else {
                    dspName = null;
                }
                if (!StringUtils.isEmpty(dspName)) {
                    queryObjVO.setDsp_objname(dspName);
                }
            }

            batchBillTableModel.initModel(queryObjVOs.toArray());
        } catch (BusinessException e) {
            Logger.error(e.getMessage(), e);
        }
    }

    public void setHierachicalDataAppModel(
            HierachicalDataAppModel hierachicalDataAppModel) {
        hierachicalDataAppModel.addAppEventListener(this);
        this.hierachicalDataAppModel = hierachicalDataAppModel;
    }

    public void setBatchBillTableModel(ErmBatchBillTableModel batchBillTableModel) {
        this.batchBillTableModel = batchBillTableModel;
        batchBillTableModel.addAppEventListener(this);
    }

    public void setTreePanel(TreePanel treePanel) {
        this.treePanel = treePanel;
    }
    

}
