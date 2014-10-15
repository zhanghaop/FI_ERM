package nc.ui.erm.billpub.interceptor;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import nc.bs.uif2.IActionCode;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.action.INCAction;
import nc.security.NCAuthenticatorFactory;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
/**
 *	CA��֤��ť������ 
 *
 */
public class CaActionInterceptor  implements ActionInterceptor{

	private AbstractAppModel model;
	
	private static final String ADDFROMMTAPP_CODE="AddFromMtapp";
	
	private List<String> cardodes = Arrays.asList(new String[]{IActionCode.EDIT,IActionCode.COPY, IActionCode.ADD, ADDFROMMTAPP_CODE,IActionCode.APPROVE});

	@Override
	public boolean afterDoActionFailed(Action action, ActionEvent e, Throwable ex) {
		return true;
	}

	@Override
	public void afterDoActionSuccessed(Action action, ActionEvent e) {
	}

	@Override
	public boolean beforeDoAction(Action action, ActionEvent e) {
		if(cardodes.contains(((NCAction)action).getValue(INCAction.CODE))){
			try {
				checkID();
			} catch (Exception e1) {
				if(((NCAction)action).getExceptionHandler() != null){
					((NCAction)action).getExceptionHandler().handlerExeption(e1);
				}
				return false;
			}
		}
		return true;
	}
	
	/** �ӽ������ͻ�ȡCA��֤��Ϣ
	 * @throws Exception */
	private void checkID() throws Exception {

		UserVO loginUser = WorkbenchEnvironment.getInstance().getLoginUser();

		if (loginUser.getIsca() == null) {
			return;
		} else {
			boolean isCaUser = loginUser.getIsca().booleanValue();
			if (!isCaUser)
				return;
		}

		DjLXVO djlx = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();

		boolean checked = true;

		if (null != djlx.getIsidvalidated()
				&& djlx.getIsidvalidated().booleanValue()) {
			checked = !(NCAuthenticatorFactory.getBusiAuthenticator(
					ErUiUtil.getPk_user()).sign("ERM_SIGN") == null);
		}

		if (!checked) {
			/*
			 * @res "�����֤δͨ��������ʧ��!"
			 */
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("2008", "UPP2008-000102"));
		}
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}
}
