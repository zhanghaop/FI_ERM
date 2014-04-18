package nc.ui.erm.pub;

import nc.itf.fipub.report.IReportQueryCond;
import nc.pubimpl.fipub.subscribe.ReportSubscribeConditionConvertor;
import nc.vo.erm.pub.ErmBaseQueryCondition;
import nc.vo.fipub.report.FipubBaseQueryCondition;

public class ErmReportSubscribeConditionConvertor extends
        ReportSubscribeConditionConvertor {

    protected FipubBaseQueryCondition createQueryCondition(boolean isContinue,
            IReportQueryCond qryCondVO) {
        return new ErmBaseQueryCondition(true, qryCondVO);
    }

}
