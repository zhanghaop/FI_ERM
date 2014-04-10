package nc.ui.arap.bx.btnstatus;

import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.arap.bx.util.BXConstans;

/**
 * @author twei
 *
 * nc.ui.arap.bx.btnstatus.BXContraBtnStatusListener
 * 
 * ³å½è¿î°´Å¥×´Ì¬¼àÌýÆ÷
 */
public class BXContraBtnStatusListener extends BXDefaultBtnStatusListener{
	
	public void updateButtonStatus(ExtButtonObject bo, IActionRuntime runtime) {
		
		setActionRunntimeV0(runtime);
		
		if(bo==null || runtime==null){
			return;
		}

		if(runtime.getCurrWorkPage()==Integer.parseInt(bo.getBtninfo().getPageid())){

			if(getVoCache().getCurrentDjdl().equals(BXConstans.BX_DJDL) ){
				bo.setVisible(true);
				if(runtime.getCurrentPageStatus()==BillWorkPageConst.WORKSTAT_NEW||runtime.getCurrentPageStatus()==BillWorkPageConst.WORKSTAT_EDIT){
					bo.setEnabled(true);
				}else{
					bo.setEnabled(false);
				}
			}else{
				bo.setVisible(false);
				bo.setEnabled(false);
			}
			
		}else {
			bo.setVisible(false);
			bo.setEnabled(false);
		}
		
		dealForLinkMode(bo,runtime);
	}

}
