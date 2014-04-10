package nc.vo.erm.pub;

import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IBusiFormat;
import nc.itf.iufo.freereport.extend.IReportAdjustor;
import nc.pub.smart.model.SmartModel;
import nc.ui.erm.pub.ErmReportDataAdjustor;
import nc.vo.fipub.query.ReportQueryVO;
import nc.vo.fipub.report.FipubBaseQueryCondition;
import nc.vo.fipub.report.ReportQueryCondVO;

public class ErmBaseQueryCondition extends FipubBaseQueryCondition {

    private static final long serialVersionUID = 8238019160223292184L;

    public ErmBaseQueryCondition(boolean isContinue, IReportQueryCond qryCondVO) {
        super(isContinue, qryCondVO);
    }
    
    public Object clone(){
        IReportQueryCond qryCondVO = getQryCondVO();

        if(qryCondVO instanceof ReportQueryVO){
            qryCondVO =  (IReportQueryCond) ((ReportQueryVO)qryCondVO).clone();
        } else if (qryCondVO instanceof ReportQueryCondVO) {
            qryCondVO =  (IReportQueryCond) ((ReportQueryCondVO)qryCondVO).clone();
        }
        Object newCondition = super.clone();
        if (!(newCondition instanceof ErmBaseQueryCondition)) {
            newCondition = new ErmBaseQueryCondition(true,qryCondVO );
        }
        return newCondition;
    }

    public IReportAdjustor getReportAdjustor() {
        return new ErmReportDataAdjustor(getQryCondVO());
    }

    @Override
    public IBusiFormat getBusiFormat(String areaName, SmartModel smartModel) {
        // TODO Auto-generated method stub
        return super.getBusiFormat(areaName, smartModel);
    }
    
}
