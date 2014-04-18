package nc.bs.erm.util;

import java.awt.Dimension;

import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.uif2.LoginContext;

public class ErmFinOrgPanel extends BDOrgPanel{
	private static final long serialVersionUID = 7956520045068494804L;
	private UIRefPane refPane = null;
	
    @Override
	public UIRefPane getRefPane() {
		if (refPane == null) {
			refPane = new UIRefPane();
            refPane.setPreferredSize(new Dimension(200, 20));
            LoginContext context = getModel().getContext();
			AbstractRefModel refModel = new nc.ui.org.ref.BusinessUnitDefaultRefModel(); 
			refModel.setPk_group(context.getPk_group());
			refModel.setFilterPks(context.getPkorgs());
            refPane.setRefModel(refModel);
			refPane.addValueChangedListener(this);
		}
		return refPane;
	}

    @Override
    protected void initDefaultOrg() {
        if (getModel().getContext().getPk_org() != null) {
            getRefPane().setPK(getModel().getContext().getPk_org());
        }
    }
}
