package nc.ui.arap.bx.btnstatus;

import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.pub.BillWorkPageConst;

/**
 * @author twei
 *
 * nc.ui.arap.bx.btnstatus.BXPageBtnStatusListener
 * 
 * ·­Ò³°´Å¥×´Ì¬¼àÌýÆ÷
 */
public class BXPageBtnStatusListener extends BXDefaultBtnStatusListener{
	
	public void updateButtonStatus(ExtButtonObject bo, IActionRuntime runtime) {
		
		setActionRunntimeV0(runtime);
		
		if(bo==null || runtime==null){
			return;
		}
		
		dealForLinkMode(bo,runtime);
		
		if (bo.getBtninfo().getBtncode().equals("Pre")) { 	/*-=notranslate=-*/
			if(runtime.getCurrWorkPage() == BillWorkPageConst.CARDPAGE){
				bo.setEnabled(true);
			}else if(getVoCache().getPage()!=null && getVoCache().getPage().hasPreviousPage()){
				bo.setEnabled(true);
			}else{
				bo.setEnabled(false);
			}
		}
		
		if (bo.getBtninfo().getBtncode().equals("Next")) { 	/*-=notranslate=-*/
			if(runtime.getCurrWorkPage() == BillWorkPageConst.CARDPAGE){
				bo.setEnabled(true);
			}else if(getVoCache().getPage()!=null && getVoCache().getPage().hasNextPage()){
				bo.setEnabled(true);
			}else{
				bo.setEnabled(false);
			}
		}
	}

}
