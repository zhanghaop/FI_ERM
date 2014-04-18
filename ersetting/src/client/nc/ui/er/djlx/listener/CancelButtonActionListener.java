package nc.ui.er.djlx.listener;


import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.pub.BillWorkPageConst;

/**增加按钮的处理方法*/
public class CancelButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() {
		// TODO 自动生成方法存根
		
		getMainFrame().setWorkstat(BillWorkPageConst.WORKSTAT_BROWSE);
		getBillCardPanel().setEnabled(false);	
		getUITree().setEnabled(true);
		refresh();
		return true;
	}
}
