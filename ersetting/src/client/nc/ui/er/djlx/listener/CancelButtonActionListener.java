package nc.ui.er.djlx.listener;


import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.pub.BillWorkPageConst;

/**���Ӱ�ť�Ĵ�����*/
public class CancelButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() {
		// TODO �Զ����ɷ������
		
		getMainFrame().setWorkstat(BillWorkPageConst.WORKSTAT_BROWSE);
		getBillCardPanel().setEnabled(false);	
		getUITree().setEnabled(true);
		refresh();
		return true;
	}
}
