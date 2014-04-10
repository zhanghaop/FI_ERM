package nc.ui.erm.closeacc.view;

import nc.ui.uif2.editor.BillListView;
/**
 * 
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class CloseAccListView extends BillListView{
	public CloseAccListView(){
		super();
	}
	
	public void initUI() {
        setMultiSelectionEnable(false);
		super.initUI();
	}
}
