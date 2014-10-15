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
 * 费用帐后台管理业务类
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
		// 过滤金额为0的行
		vos = removeNullData(vos);
		if (vos == null || vos.length == 0) {
			return;
		}
		// 设置审计信息
		AuditInfoUtil.addData(vos);
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSave(vos);

		fireBeforeInsertEvent(vos);
		if (vos[0].getBillstatus() == 3) {
			fireBeforeSignEvent(vos);
		}
		// 新增保存
		vos = getDAO().insertAccountVO(vos);

		fireAfterInsertEvent(vos);

		// 回写余额表
		synchExpenseBal(vos, null);
	}

	private ExpenseAccountVO[] removeNullData(ExpenseAccountVO[] vos) {
		if (vos == null || vos.length == 0) {
			return null;
		}
		List<ExpenseAccountVO> list = new ArrayList<ExpenseAccountVO>();
		for (int i = 0; i < vos.length; i++) {
			 if(vos[i].getAssume_amount().compareTo(UFDouble.ZERO_DBL) != 0){
             	// 金额为0的行不处理
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
		// 过滤金额为0的新增行
		vos = removeNullData(vos);
		if ((vos == null || vos.length == 0) && (oldvos == null || oldvos.length == 0)){
			return;
		}
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSave(vos);
		// 设置审计信息
		AuditInfoUtil.updateData(vos);
		
		// 删除原帐
		fireBeforeDeleteEvent(oldvos);
		getDAO().deleteAccountVO(oldvos);
		fireAfterDeleteEvent(oldvos);
		
		// 新增新帐
		fireBeforeInsertEvent(vos);
		getDAO().insertAccountVO(vos);
		fireAfterInsertEvent(vos);
		
		// 回写余额表
		synchExpenseBal(vos,oldvos);
	}

	public void deleteVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		
		// 删除引用校验
		BDReferenceChecker.getInstance().validate(vos);
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkDelete(vos);
		// 删除单据
		fireBeforeDeleteEvent(vos);
		getDAO().deleteAccountVO(vos);
		fireAfterDeleteEvent(vos);
		// 回写余额表
		synchExpenseBal(null,vos);
	}
	
	public void approveVOs(ExpenseAccountVO[] vos) throws BusinessException {
		final long start = System.currentTimeMillis();
		if (vos == null || vos.length == 0) {
			return;
		}

		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkApprove(vos);
		// 查询oLdvo，带余额表中删除
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// 设置生效状态
		for (int i = 0; i < vos.length; i++) {
			vos[i].setBillstatus(BXStatusConst.DJZT_Verified);
		}
		
		// 更新保存
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		
		// 回写余额表
		synchExpenseBal(vos,oldvos);
		final long end = System.currentTimeMillis();
		Logger.debug("同步费用帐汇总表耗时22222222222222222：" + String.valueOf(end - start)+" 毫秒 ");
	}

	public void signVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
//		long start = System.currentTimeMillis();
		// 加锁
//		updatelockOperate(vos);
//		// 版本校验
//		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSign(vos);
//		long end = System.currentTimeMillis();
//		Logger.debug("同步费用帐汇总表耗时-生效-校验：" + String.valueOf(end - start)+" 毫秒 ");
//		start = System.currentTimeMillis();
		// 查询oLdvo，带余额表中删除
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		
//		end = System.currentTimeMillis();
//		Logger.debug("同步费用帐汇总表耗时-生效-查询oldvos：" + String.valueOf(end - start)+" 毫秒 ");
//		start = System.currentTimeMillis();
		// 审核前事件处理
//		fireBeforeSignEvent(vos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("同步费用帐汇总表耗时-生效-生效前事件：" + String.valueOf(end - start)+" 毫秒 ");
//		start = System.currentTimeMillis();
		
		// 设置生效状态
		for (int i = 0; i < vos.length; i++) {
			vos[i].setBillstatus(BXStatusConst.DJZT_Sign);
		}
		
		fireBeforeSignEvent(vos);
		// 更新保存
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		
		fireAfterSignEvent(vos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("同步费用帐汇总表耗时-生效-生效更新保存：" + String.valueOf(end - start)+" 毫秒 ");
//		start = System.currentTimeMillis();
		// 审核后事件处理
//		fireAfterSignEvent(vos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("同步费用帐汇总表耗时-生效-生效后事件：" + String.valueOf(end - start)+" 毫秒 ");
//		start = System.currentTimeMillis();
		
		// 回写余额表
		synchExpenseBal(vos,oldvos);
		
//		end = System.currentTimeMillis();
//		Logger.debug("同步费用帐汇总表耗时-生效-同步余额表：" + String.valueOf(end - start)+" 毫秒 ");
//		start = System.currentTimeMillis();
		
		// // 记录业务日志
		// ErmBusiLogUtils.insertSmartBusiLogs(vos,
		// (AggCostShareVO[]) oldvos.toArray(new AggCostShareVO[0]),
		// IErmCostShareConst.CS_MD_APPROVE_OPER);
	}
	
	/**
	 * 获得supervo数组的pks
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
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkunApprove(vos);
		// 查询oLdvo，带余额表中删除
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// 外部赋值，审核状态业务信息
		// 更新保存
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		// 回写余额表
		synchExpenseBal(vos,oldvos);
	}

	public void unSignVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
//		// 加锁
//		updatelockOperate(vos);
//		// 版本校验
//		BDVersionValidationUtil.validateVersion(vos);
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkSign(vos);
		// 查询oLdvo，带余额表中删除
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// 外部赋值，审核状态业务信息
		for (int i = 0; i < oldvos.length; i++) {
			if(oldvos[i].getBillstatus().equals(BXStatusConst.DJZT_Verified)){
				oldvos[i].setBillstatus(BXStatusConst.DJZT_Saved);
			}
		}
		// 更新保存
		fireBeforeUnSignEvent(vos);
		
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.BILLSTATUS });
		fireAfterUnSignEvent(vos);
		// 回写余额表
		synchExpenseBal(vos,oldvos);
	}

	public void writeoffVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkWriteoff(vos);
		// 查询oLdvo，带余额表中删除
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// 设置冲销状态
		for (int i = 0; i < vos.length; i++) {
			vos[i].setIswriteoff(UFBoolean.TRUE);
		}
		
		fireBeforeWriteOffEvent(vos);
		// 更新保存
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.ISWRITEOFF });
		
		fireAfterWriteOffEvent(vos);
		// 回写余额表
		synchExpenseBal(vos,oldvos);
	}

	public void unWriteoffVOs(ExpenseAccountVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		
		// vo校验
		ExpenseAccountChecker vochecker = new ExpenseAccountChecker();
		vochecker.checkunWriteoff(vos);
		// 查询oLdvo，带余额表中删除
		IMDPersistenceQueryService qryService = MDPersistenceService.lookupPersistenceQueryService();
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = qryService.queryBillOfVOByPKs(ExpenseAccountVO.class, getVOsPk(vos), true);
		ExpenseAccountVO[] oldvos = c.toArray(new ExpenseAccountVO[c.size()]);
		// 设置冲销状态
		for (int i = 0; i < vos.length; i++) {
			vos[i].setIswriteoff(UFBoolean.FALSE);
		}
		
		fireBeforeUnWriteOffEvent(vos);
		// 更新保存
		getDAO().updateAccountVO(
				vos,
				new String[] { ExpenseAccountVO.PK_EXPENSEACCOUNT,
						ExpenseAccountVO.ISWRITEOFF });
		
		fireAfterUnWriteOffEvent(vos);
		// 回写余额表
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
