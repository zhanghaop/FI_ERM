package nc.ui.arap.bx.btnstatus;

import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.pub.BillWorkPageConst;

/**
 * @author twei
 *
 * nc.ui.arap.bx.btnstatus.BxOperationBtnStatusListener
 * 
 * 单据操作按钮状态监听器
 */
public class BxOperationBtnStatusListener extends BXDefaultBtnStatusListener {

	public void updateButtonStatus(ExtButtonObject bo, IActionRuntime runtime) {

		super.updateButtonStatus(bo, runtime);
		
		if(runtime.getCurrWorkPage()== BillWorkPageConst.LISTPAGE){
			if(bo.getBtninfo().getBtncode().equals("打印操作")){/*-=notranslate=-*/
				bo.setEnabled(true);
				return;
			}
		}
		//打印按钮可见控制
		if(getSelBxvos().length>0 && getSelBxvos()[0].getParentVO().getPk_jkbx()!=null && !getSelBxvos()[0].getParentVO().getPk_jkbx().equals("")){
			if(status)
				bo.setEnabled(true);
		}else{
			bo.setEnabled(false);
		}
	}

}
