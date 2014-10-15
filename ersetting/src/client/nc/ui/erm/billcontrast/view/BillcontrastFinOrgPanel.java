package nc.ui.erm.billcontrast.view;

import nc.ui.bd.pub.BDOrgPanel;

public class BillcontrastFinOrgPanel extends BDOrgPanel{
	private static final long serialVersionUID = 7956520045068494804L;
//	private UIRefPane refPane = null;

	
	public void initUI() {
		super.initUI();
//		getRefPane().setPreferredSize(new Dimension(122,22));
		
	}
//	@Override
//	public UIRefPane getRefPane() {
//		if (refPane == null) {
//			refPane = new UIRefPane();
//			refPane.setRefModel(new FinanceOrgDefaultRefTreeModel());
//			refPane.addValueChangedListener(this);
//		}
//		return refPane;
//	}
//	@Override
//	protected void initDefaultOrg() {
//		String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();
//		if(defaultOrg!=null){
//			refPane.setPK(defaultOrg);
//			refPane.getUITextField().setValue(defaultOrg);
//		}
//	}
}
