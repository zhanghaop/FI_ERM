package nc.impl.erm.expamortize;

import java.util.Collection;

import nc.bs.arap.bx.BusiLogUtil;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expamortize.IExpAmortizeprocManage;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.util.AuditInfoUtil;

public class ExpAmortizeprocManageImpl implements IExpAmortizeprocManage {
	/**
	 * ̯��ҵ�����
	 */
	private static final String EXPAMTPROC_MD_ADD_OPER = "8be8aaa0-53c2-4313-ad09-cf32e07a034a";
	
	@Override
	public ExpamtprocVO[] insertVOs(ExpamtprocVO[] vos) throws BusinessException {
		if (vos != null) {
			ExpamtprocVO[] result = insertVo(vos);
			// ��¼ҵ����־
			BusiLogUtil.insertSmartBusiLog(EXPAMTPROC_MD_ADD_OPER, new ExpamtprocVO[] { result[0] }, null);
			return result;
		}
		return null;
	}
	
	private ExpamtprocVO[] insertVo(ExpamtprocVO[] vos) throws MetaDataException {
		// ���������Ϣ
		for (ExpamtprocVO proc : vos) {
			proc.setStatus(VOStatus.NEW);
			AuditInfoUtil.addData(proc);
		}
		// ��������
		String[] pks = getService().saveBill(vos);
		@SuppressWarnings("unchecked")
		Collection<ExpamtprocVO> result = getQryService().queryBillOfVOByPKs(ExpamtprocVO.class, pks, false);
		return result.toArray(new ExpamtprocVO[] {});
	}

	private IMDPersistenceQueryService getQryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}
	
	private IMDPersistenceService getService() {
		return MDPersistenceService.lookupPersistenceService();
	}
}
