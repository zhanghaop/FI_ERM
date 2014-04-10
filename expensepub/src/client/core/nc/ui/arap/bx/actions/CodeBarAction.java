package nc.ui.arap.bx.actions;

import nc.ui.arap.bx.BarCodeDialog;


public class CodeBarAction extends BXDefaultAction {
	
	public CodeBarAction() {
		
	}
	public void codeBar() throws Exception{
		new BarCodeDialog(getMainPanel()).showModal();
	}
}
