package nc.ui.erm.pub;

import java.awt.Container;

import nc.itf.fipub.report.IFipubReportQryDlg;
import nc.itf.fipub.report.IReportQueryCond;
import nc.vo.fipub.report.FipubBaseQueryCondition;

import com.ufida.dataset.IContext;

public class ErmReportExpQueryAction extends ErmReportDefaultQueryAction {
	@Override
	public int getSysCode() {
		return ErmReportQryDlg.ERM_EXPDETAIL;
	}
	
	@Override
	protected FipubBaseQueryCondition createQueryCondition(boolean isContinue, IReportQueryCond qryCondVO) {
		return new FipubBaseQueryCondition(true, qryCondVO);
	}
	
    @SuppressWarnings("restriction")
    @Override
    protected IFipubReportQryDlg getQryDlg(Container parent,
            final IContext context,
            String nodeCode, int iSysCode, String title) {
        if (dlg == null) {
            dlg = super.getQryDlg(parent, context, nodeCode, iSysCode, title);
            ((ErmAbstractReportBaseDlg) dlg).setPkOrgSameAssumeOrg(true);
        }
        
        return dlg;
    }
}
