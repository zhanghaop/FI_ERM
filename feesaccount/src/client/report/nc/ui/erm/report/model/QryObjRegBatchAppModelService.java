package nc.ui.erm.report.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.fipub.queryobjreg.IReportQueryObjReg;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.uif2.LoginContext;

public class QryObjRegBatchAppModelService implements IBatchAppModelService {

    @Override
    public Object[] queryByDataVisibilitySetting(LoginContext context)
            throws Exception {
        return null;
    }

    @Override
    public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
        return NCLocator.getInstance().lookup(IReportQueryObjReg.class).batchSave(batchVO);
    }

}
