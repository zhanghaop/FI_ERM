package nc.ui.er.djlx.listener;


import nc.bs.logging.Log;
import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.ml.NCLangRes;
import nc.vo.pub.BusinessException;

/**修改按钮的处理方法*/
public class EditButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() throws BusinessException {
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common","UCH027"));
		
		getMainFrame().getBillCardPanel().setEnabled(true);
		getMainFrame().getBillCardPanel().updateValue();
		
		getUITree().setEnabled(false);
		setCardEnable(true);
		getMainFrame().setWorkstat(BillWorkPageConst.WORKSTAT_EDIT);
		
		boolean s=getMainFrame().getBillCardPanel().requestFocusInWindow();
		
		Log.getInstance(this.getClass()).debug("request focus "+s);
        return true;
	}


}
