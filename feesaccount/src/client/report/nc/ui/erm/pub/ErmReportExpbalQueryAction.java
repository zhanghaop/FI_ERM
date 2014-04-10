package nc.ui.erm.pub;

import java.awt.Container;

import nc.itf.fipub.report.IFipubReportQryDlg;

import com.ufida.dataset.IContext;

public class ErmReportExpbalQueryAction extends ErmReportDefaultQueryAction {
	@Override
	public int getSysCode() {
		return ErmReportQryDlg.ERM_EXPBAL;
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
