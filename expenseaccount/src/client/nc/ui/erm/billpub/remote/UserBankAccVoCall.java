package nc.ui.erm.billpub.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.pub.BusinessException;

/**
 * ���浱ǰ��¼��������ҵ��Ա�ĸ��������ʺ�Ĭ�ϱ������ӻ���Ϣ
 * 
 * @author chendya
 * 
 */
public class UserBankAccVoCall extends AbstractCall implements IRemoteCallItem {

	/**
	 * ��ǰ��¼��������ҵ��Ա�ĸ��������ʺ��ӻ���Ϣ��ʶ
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