package nc.bs.erm.expenseaccount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.businessevent.EventDispatcher;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.logging.Logger;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDReferenceChecker;

/**
 * �����ʺ�̨����ҵ����
 * 
 * @author lvhj
 * 
 */
public class ExpenseAccountBO {

	private ExpenseAccountDAO dao;

	private ExpenseAccountDAO getDAO() {
		if (dao == null) {
			dao = new ExpenseAccountDAO();
		}
		return dao;
	}

	public void insertVOs(ExpenseAccountVO[] vos) throws BusinessException {
		// ���˽��Ϊ0����
		vos = removeNullData(vos);
		if (vos == null || vos.length == 0) {
			return;
		}
		// ���������Ϣ
		AuditInfoUtil.addData(vos);
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSave(vos);

		fireBeforeInsertEvent(vos);
		if (vos[0].getBillstatus() == 3) {
			fireBeforeSignEvent(vos);
		}
		// ��������
		vos = getDAO().insertAccountVO(vos);

		fireAfterInsertEvent(vos);

		// ��д����
		synchExpenseBal(vos, null);
	}

	private ExpenseAccountVO[] removeNullData(ExpenseAccountVO[] vos) {
		if (vos == null || vos.length == 0) {
			return null;
		}
		List<ExpenseAccountVO> list = new ArrayList<ExpenseAccountVO>();
		for (int i = 0; i < vos.length; i++) {
			 if(vos[i].getAssume_amount().compareTo(UFDouble.ZERO_DBL) != 0){
             	// ���Ϊ0���в�����
             	list.add(vos[i]);
             }
		}
		return list.toArray(new ExpenseAccountVO[list.size()]);
	}

	private void synchExpenseBal(ExpenseAccountVO[] vos,
			ExpenseAccountVO[] deletevos) throws BusinessException {
		new ExpenseAccountBalanceBO().synchExpenseBal(
				vos,deletevos);
	}

	public void updateVOs(ExpenseAccountVO[] vos, ExpenseAccountVO[] oldvos)
			throws BusinessException {
		// ���˽��Ϊ0��������
		vos = removeNullData(vos);
		if ((vos == null || vos.length == 0) && (oldvos == null || oldvos.length == 0)){
			return;
		}
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSave(vos);
		// ���������Ϣ
		AuditInfoUtil.updateData(vos);
		
		// ɾ��ԭ��
		fireBeforeDeleteEvent(oldvos);
		getDAO().deleteAccountVO(oldvos);
		fireAfterDeleteEvent(oldvos);
		
		// ��������
		fireBeforeInsertEvent(vos);
		getDAO().insertAccountVO(vos);
		fireAfterInsertEvent(vos);
		
		// ��д����
		synchExpenseBal(vos,oldvos);
	}

	public void deleteVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		
		// ɾ������У��
		BDReferenceChecker.getInstance().validate(vos);
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkDelete(vos);
		// ɾ������
		fireBeforeDeleteEvent(vos);
		getDAO().deleteAccountVO(vos);
		fireAfterDeleteEvent(vos);
		// ��д����
		synchExpenseBal(null,vos);
	}
	
	public void approveVOs(ExpenseAccountVO[] vos) throws BusinessException {
		final long start = System.currentTimeMillis();
		if (vos == null || vos.length == 0) {
			return;
		}

		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkApprove(vos);
		// ��ѯoLdvo����������ɾ��
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// ������Ч״̬
		for (int i = 0; i < vos.length; i++) {
			vos[i].setBillstatus(BXStatusConst.DJZT_Verified);
		}
		
		// ���±���
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		
		// ��д����
		synchExpenseBal(vos,oldvos);
		final long end = System.currentTimeMillis();
		Logger.debug("ͬ�������ʻ��ܱ��ʱ22222222222222222��" + String.valueOf(end - start)+" ���� ");
	}

	public void signVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
//		long start = System.currentTimeMillis();
		// ����
//		updatelockOperate(vos);
//		// �汾У��
//		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSign(vos);
//		long end = System.currentTimeMillis();
//		Logger.debug("ͬ�������ʻ��ܱ��ʱ-��Ч-У�飺" + String.valueOf(end - start)+" ���� ");
//		start = System.currentTimeMillis();
		// ��ѯoLdvo����������ɾ��
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		
//		end = System.currentTimeMillis();
//		Logger.debug("ͬ�������ʻ��ܱ��ʱ-��Ч-��ѯoldvos��" + String.valueOf(end - start)+" ���� ");
//		start = System.currentTimeMillis();
		// ���ǰ�¼�����
//		fireBeforeSignEvent(vos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("ͬ�������ʻ��ܱ��ʱ-��Ч-��Чǰ�¼���" + String.valueOf(end - start)+" ���� ");
//		start = System.currentTimeMillis();
		
		// ������Ч״̬
		for (int i = 0; i < vos.length; i++) {
			vos[i].setBillstatus(BXStatusConst.DJZT_Sign);
		}
		
		fireBeforeSignEvent(vos);
		// ���±���
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		
		fireAfterSignEvent(vos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("ͬ�������ʻ��ܱ��ʱ-��Ч-��Ч���±��棺" + String.valueOf(end - start)+" ���� ");
//		start = System.currentTimeMillis();
		// ��˺��¼�����
//		fireAfterSignEvent(vos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("ͬ�������ʻ��ܱ��ʱ-��Ч-��Ч���¼���" + String.valueOf(end - start)+" ���� ");
//		start = System.currentTimeMillis();
		
		// ��д����
		synchExpenseBal(vos,oldvos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("ͬ�������ʻ��ܱ��ʱ-��Ч-ͬ������" + String.valueOf(end - start)+" ���� ");
//		start = System.currentTimeMillis();
		
		// // ��¼ҵ����־
		// ErmBusiLogUtils.insertSmartBusiLogs(vos,
		// (AggCostShareVO[]) oldvos.toArray(new AggCostShareVO[0]),
		// IErmCostShareConst.CS_MD_APPROVE_OPER);
	}
	
	/**
	 * ���supervo�����pks
	 * 
	 * @param vos
	 * @return
	 */
	private String[] getVOsPk(SuperVO[] vos) {
		Set<String> pks = new HashSet<String>();
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				pks.add(vos[i].getPrimaryKey());
			}
		}
		return pks.toArray(new String[0]);
	}
	
	public void unApproveVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkunApprove(vos);
		// ��ѯoLdvo����������ɾ��
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// �ⲿ��ֵ�����״̬ҵ����Ϣ
		// ���±���
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		// ��д����
		synchExpenseBal(vos,oldvos);
	}

	public void unSignVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
//		// ����
//		updatelockOperate(vos);
//		// �汾У��
//		BDVersionValidationUtil.validateVersion(vos);
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSign(vos);
		// ��ѯoLdvo����������ɾ��
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// �ⲿ��ֵ�����״̬ҵ����Ϣ
		for (int i = 0; i < oldvos.length; i++) {
			if(oldvos[i].getBillstatus().equals(BXStatusConst.DJZT_Verified)){
				oldvos[i].setBillstatus(BXStatusConst.DJZT_Saved);
			}
		}
		// ���±���
		fireBeforeUnSignEvent(vos);
		
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		fireAfterUnSignEvent(vos);
		// ��д����
		synchExpenseBal(vos,oldvos);
	}

	public void writeoffVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkWriteoff(vos);
		// ��ѯoLdvo����������ɾ��
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// ���ó���״̬
		for (int i = 0; i < vos.length; i++) {
			vos[i].setIswriteoff(UFBoolean.TRUE);
		}
		
		fireBeforeWriteOffEvent(vos);
		// ���±���
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.ISWRITEOFF });
		
		fireAfterWriteOffEvent(vos);
		// ��д����
		synchExpenseBal(vos,oldvos);
	}

	public void unWriteoffVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		
		// voУ��
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkunWriteoff(vos);
		// ��ѯoLdvo����������ɾ��
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// ���ó���״̬
		for (int i = 0; i < vos.length; i++) {
			vos[i].setIswriteoff(UFBoolean.FALSE);
		}
		
		fireBeforeUnWriteOffEvent(vos);
		// ���±���
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.ISWRITEOFF });
		
		fireAfterUnWriteOffEvent(vos);
		// ��д����
		synchExpenseBal(vos,oldvos);
	}

	public static final String lockmessage = "ERM_expenseaccount";

	public static final String[] accountLockFields = new String[] {
			ExpenseAccountVO.SRC_BILLTYPE, ExpenseAccountVO.SRC_TRADETYPE,
			ExpenseAccountVO.SRC_ID, ExpenseAccountVO.SRC_SUBID };

	
	private void fireBeforeSignEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_SIGN_BEFORE, vos));
		}
	}

	private void fireAfterSignEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_SIGN_AFTER, vos));
		}
	}

	private void fireBeforeUnSignEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_UNSIGN_BEFORE, vos));
		}
	}

	private void fireAfterUnSignEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_UNSIGN_AFTER, vos));
		}
	}

	private void fireBeforeInsertEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_INSERT_BEFORE, vos));
		}
	}

	private void fireAfterInsertEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_INSERT_AFTER, vos));
		}
	}

	private void fireBeforeDeleteEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_DELETE_BEFORE, vos));
		}
	}

	private void fireAfterDeleteEvent(ExpenseAccountVO... vos) throws BusinessException {
		if(vos != null && vos.length > 0 && !vos[0].getIswriteoff().booleanValue()){
			EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
					ErmEventType.TYPE_DELETE_AFTER, vos));
		}
	}

	private void fireBeforeWriteOffEvent(ExpenseAccountVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
				ErmEventType.TYPE_WRITEOFF_BEFORE, vos));
	}

	private void fireAfterWriteOffEvent(ExpenseAccountVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
				ErmEventType.TYPE_WRITEOFF_AFTER, vos));
	}

	private void fireBeforeUnWriteOffEvent(ExpenseAccountVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
				ErmEventType.TYPE_UNWRITEOFF_BEFORE, vos));
	}

	private void fireAfterUnWriteOffEvent(ExpenseAccountVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(ExpenseAccountConst.ExpenseAccount_MDID,
				ErmEventType.TYPE_UNWRITEOFF_AFTER, vos));
	}
}
