package nc.ui.erm.billcontrast.action;

import java.awt.event.ActionEvent;

import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.actions.batch.BatchAddLineWithDefValueAction;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;
/**
 * ʵ�ַ�̯��ת���ݶ�Ӧ�������ӵĹ���
 * @author wangle
 *
 */
public class BilAddAction extends BatchAddLineWithDefValueAction{
	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultValue(Object obj) {
		super.setDefaultValue(obj);
		if (!(obj instanceof BillcontrastVO)) {
			return;
		}
		BillcontrastVO bil = (BillcontrastVO) obj;
		String pk_org=getModel().getContext().getPk_org();
		String pk_group = getModel().getContext().getPk_group();
		bil.setPk_group(pk_group);
		bil.setPk_org(pk_org);
		
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		beforeAction();
		super.doAction(e);
	}
	
	public void beforeAction()throws BusinessException{
		//�õ�������֯
		String pk_org=getModel().getContext().getPk_org();
		String errormsg = null;
		String msg = null;
		if(pk_org==null){
			errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0028")/*@res "����ѡ�������֯!"*/;
			msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0029")/*@res "����ʧ��!"*/;
			((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(msg);
			throw new BusinessException(errormsg);
		}
	}
}
