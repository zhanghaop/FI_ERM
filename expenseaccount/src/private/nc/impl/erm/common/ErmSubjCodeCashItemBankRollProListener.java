package nc.impl.erm.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.businessevent.CheckEvent;
import nc.bs.businessevent.CheckEvent.CheckUserObject;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.bd.cashflow.CashflowUseVO;
import nc.vo.bd.fundplan.FundPlanUseVO;
import nc.vo.bd.inoutbusiclass.InoutUseVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;

/**
 * 收支项目、资金计划、现金流量取消分配校验
 * @author chenshuaia
 *
 */
public class ErmSubjCodeCashItemBankRollProListener implements IBusinessListener {
	private Map<String, Object> dataMap = new HashMap<String, Object>();//pk,对象对照
	
	List<String> pklist = new ArrayList<String>();//pk集合
	
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		String eventType = event.getEventType();

		if (IEventType.TYPE_CANCELASSIGN_CHECK.equals(eventType)) {
			Object userObject = event.getUserObject();
			Object[] userObjects = ((CheckUserObject) userObject).getUserObjects();
			StringBuffer checkSql = new StringBuffer("SELECT DISTINCT ");

			// 收支项目
			CheckEvent checkEvent = (CheckEvent) event;
			if (userObjects[0] instanceof InoutUseVO) {
				// 第一步查费用帐（报销、结转走费用帐（摊销与报销一致，查报销即可））
				
				for(Object obj : userObjects){
					InoutUseVO inoutVo = (InoutUseVO)obj;
					dataMap.put(inoutVo.getPk_inoutbusiclass(), inoutVo);
					
					// 第一步 查借款报销单
					StringBuffer checkJkSql = new StringBuffer("SELECT DISTINCT ");
					checkJkSql.append("er_busitem.szxmid from er_jkzb zb left join er_busitem ");
					checkJkSql.append(" on zb.pk_jkbx = er_busitem.pk_jkbx where ");
					checkJkSql.append(" er_busitem.szxmid = '" + inoutVo.getPk_inoutbusiclass() + "'");
					checkJkSql.append(" and zb.fydwbm = '" + inoutVo.getPk_org() + "' ");
					boolean isExist  = checkBillRef(checkJkSql, checkEvent);
					if(isExist){
						continue;
					}
					
					// 第二步 查报销单
					StringBuffer checkBxSql = new StringBuffer("SELECT DISTINCT ");
					checkBxSql.append("er_busitem.szxmid from er_bxzb zb left join er_busitem ");
					checkBxSql.append(" on zb.pk_jkbx = er_busitem.pk_jkbx where ");
					checkBxSql.append(" er_busitem.szxmid = '" + inoutVo.getPk_inoutbusiclass() + "'");
					checkBxSql.append(" and zb.fydwbm = '" + inoutVo.getPk_org() + "' ");
					isExist = checkBillRef(checkBxSql, checkEvent);
					
					if(isExist){
						continue;
					}
					
					// 第三步查申请单
					StringBuffer checkMaSql = new StringBuffer("SELECT DISTINCT ");
					checkMaSql.append(MtAppDetailVO.PK_IOBSCLASS).append(" from er_mtapp_detail where ");
					checkMaSql.append(" pk_iobsclass = '" + inoutVo.getPk_inoutbusiclass() + "' ");
					checkMaSql.append(" and pk_org = '" + inoutVo.getPk_org() + "' ");
					isExist = checkBillRef(checkMaSql, checkEvent);
					if(isExist){
						continue;
					}
					
					//第四步查结转单
					StringBuffer checkCsSql = new StringBuffer("SELECT DISTINCT ");
					checkCsSql.append(CShareDetailVO.PK_IOBSCLASS).append(" from ER_CSHARE_DETAIL cs where ");
					checkCsSql.append(" cs.PK_IOBSCLASS = '" + inoutVo.getPk_inoutbusiclass() + "' ");
					checkCsSql.append(" and cs.ASSUME_ORG = '" + inoutVo.getPk_org() + "' ");
					checkBillRef(checkCsSql, checkEvent);
				}
				

			} else if (userObjects[0] instanceof CashflowUseVO) {// 现金流量项目
				checkSql.append(ExpenseAccountVO.BX_CASHITEM).append(" FROM er_expenseaccount WHERE ");
				for (Object useVO : userObjects) {
					CashflowUseVO cashFlow = (CashflowUseVO) useVO;
					dataMap.put(cashFlow.getPk_cashflow(), useVO);
					
					
					// 第一步 查报销单
					StringBuffer checkJkSql = new StringBuffer("SELECT DISTINCT ");
					checkJkSql.append("zb.cashitem from er_jkzb zb where ");
					checkJkSql.append(" zb.pk_org = '" + cashFlow.getPk_org() + "'");
					checkJkSql.append(" and zb.cashitem = '" + cashFlow.getPk_cashflow() + "' ");
					boolean isExist = checkBillRef(checkJkSql, checkEvent);
					
					if(isExist){
						continue;
					}
					
					// 第二步 查报销单
					StringBuffer checkBxSql = new StringBuffer("SELECT DISTINCT ");
					checkBxSql.append("zb.cashitem from er_bxzb zb where ");
					checkBxSql.append(" zb.pk_org = '" + cashFlow.getPk_org() + "'");
					checkBxSql.append(" and zb.cashitem = '" + cashFlow.getPk_cashflow() + "' ");
					checkBillRef(checkBxSql, checkEvent);
				}

			} else if (userObjects[0] instanceof FundPlanUseVO) {// 资金计划项目
				checkSql.append(ExpenseAccountVO.BX_CASHPROJ).append(" FROM er_expenseaccount WHERE ");
				for (Object useVO : userObjects) {
					FundPlanUseVO fundplan = ((FundPlanUseVO) useVO);
					dataMap.put(fundplan.getPk_fundplan(), useVO);
					
					// 第一步 查报销单
					StringBuffer checkJkSql = new StringBuffer("SELECT DISTINCT ");
					checkJkSql.append("zb.cashproj from er_jkzb zb where ");
					checkJkSql.append(" zb.pk_org = '" + fundplan.getPk_org() + "'");
					checkJkSql.append(" and zb.cashproj = '" + fundplan.getPk_fundplan() + "' ");
					boolean isExist = checkBillRef(checkJkSql, checkEvent);
					if(isExist){
						continue;
					}
					
					// 第二步 查报销单
					StringBuffer checkBxSql = new StringBuffer("SELECT DISTINCT ");
					checkBxSql.append("zb.cashproj from er_bxzb zb where ");
					checkBxSql.append(" zb.pk_org = '" + fundplan.getPk_org() + "'");
					checkBxSql.append(" and zb.cashproj = '" + fundplan.getPk_fundplan() + "' ");
					checkBillRef(checkBxSql, checkEvent);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean checkBillRef(StringBuffer checkSql, CheckEvent event) throws DAOException, BusinessException {
		List<String> executeQuery = (List<String>) new BaseDAO().executeQuery(checkSql.toString(), new BaseProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				List<String> lstResult = new ArrayList<String>();
				while (rs.next()) {
					lstResult.add(rs.getString(1));
				}
				return lstResult;
			}

		});
		
		if (!executeQuery.isEmpty()) {
			try {
				for (String pk : executeQuery) {
					event.addErrorMsg(dataMap.get(pk),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0169")/*
																												 * @
																												 * res
																												 * "数据已经被费用模块引用，不能取消分配!"
																												 */);
				}
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
			
			return true;
		}
		
		return false;
	}

}
