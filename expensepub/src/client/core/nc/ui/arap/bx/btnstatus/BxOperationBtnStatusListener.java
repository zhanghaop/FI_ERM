package nc.ui.arap.bx.btnstatus;

import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.pub.BillWorkPageConst;

/**
 * @author twei
 *
 * nc.ui.arap.bx.btnstatus.BxOperationBtnStatusListener
 * 
 * ���ݲ�����ť״̬������
 */
public class BxOperationBtnStatusListener extends BXDefaultBtnStatusListener {

	public void updateButtonStatus(ExtButtonObject bo, IActionRuntime runtime) {

		super.updateButtonStatus(bo, runtime);
		
		if(runtime.getCurrWorkPage()== BillWorkPageConst.LISTPAGE){
			if(bo.getBtninfo().getBtncode().equals("��ӡ����")){/*-=notranslate=-*/
				bo.setEnabled(true);
				return;
			}
		}
		//��ӡ��ť�ɼ�����
		if(getSelBxvos().length>0 && getSelBxvos()[0].getParentVO().getPk_jkbx()!=null && !getSelBxvos()[0].getParentVO().getPk_jkbx().equals("")){
			if(status)
				bo.setEnabled(true);
		}else{
			bo.setEnabled(false);
		}
	}

}
