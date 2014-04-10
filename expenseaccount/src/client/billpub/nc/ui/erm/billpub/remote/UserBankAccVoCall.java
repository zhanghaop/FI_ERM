package nc.ui.erm.billpub.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.pub.BusinessException;

/**
 * 缓存当前登录人所关联业务员的个人银行帐号默认报销卡子户信息
 * 
 * @author chendya
 * 
 */
public class UserBankAccVoCall extends AbstractCall implements IRemoteCallItem {

	/**
	 * 当前登录人所关联业务员的个人银行帐号子户信息标识
	 */
	public static String USERBANKACC_VOCALL = "USERBANKACC_VOCALL";

	public UserBankAccVoCall() {
		super();
	}

	@Override
	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.bd.psnbankacc.IPsnBankaccPubService");
		callvo.setMethodname("queryDefaultBankAccByPsnDoc");
		callvo.setParamtype(new Class[] { String.class});
		callvo.setParam(new Object[] { BXUiUtil.getPk_psndoc() });
		return callvo;
	}

	public void handleResult(Map<String, Object> datas)
			throws BusinessException {
		BankAccbasVO bankAccbasVO = (BankAccbasVO) datas.get(callvo.getCode());
		if (bankAccbasVO != null && bankAccbasVO.getBankaccsub()!=null) {
			WorkbenchEnvironment.getInstance().putClientCache(
					USERBANKACC_VOCALL + BXUiUtil.getPk_psndoc(), bankAccbasVO.getBankaccsub());
		}
	}
}