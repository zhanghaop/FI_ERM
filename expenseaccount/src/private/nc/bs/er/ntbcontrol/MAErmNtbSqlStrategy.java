package nc.bs.er.ntbcontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.core.util.ObjectCreator;
import nc.itf.arap.pub.IErmMaSubBudgetSql;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.IFormulaFuncName;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.control.TokenTools;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 申请单执行取数策略
 * 
 * @author chenshuaia
 * 
 */
@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "申请单预算取数策略" /*-=notranslate=-*/, type = BusinessType.NORMAL)
public class MAErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	public MAErmNtbSqlStrategy() {
		setBillType(ErmMatterAppConst.MatterApp_BILLTYPE);
	}

	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		// 设置预算参数与单据类型
		this.ntbParam = ntbParam;

		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlMa(false));
		String[] fromMaSqls = getSqlFromMa(false,getSqlMaForSP(false));
		if (!ArrayUtils.isEmpty(fromMaSqls)) {
			for (String sql : fromMaSqls) {
				sqlList.add(sql);
			}
		}
		return sqlList;
	}

	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		// 设置预算参数与单据类型
		this.ntbParam = ntbParam;
		sqlList.add(getSqlMa(true));

		String[] fromMaSqls = getSqlFromMa(true,getSqlMaForSP(true));
		if (!ArrayUtils.isEmpty(fromMaSqls)) {
			for (String sql : fromMaSqls) {
				if(sql != null){
					sqlList.add(sql);
				}
			}
		}
		return sqlList;
	}

	/**
	 * 查询是下游单据对申请单的执行数或预算数的占用
	 * 
	 * @param isDetail
	 * @param string 
	 * @return
	 * @throws Exception
	 */
	private String[] getSqlFromMa(boolean isDetail, String masql) throws Exception {
		Map<String, List<String>> effectPkMap = getEffectPfPksMap();
		List<String> sqlList = new ArrayList<String>();

		masql=" and pf.pk_mtapp_detail in (select distinct mad.pk_mtapp_detail "+masql.substring(masql.indexOf(" from "))+")";
		List<String> bxDetailList = effectPkMap.get("bx");
		if(bxDetailList != null && bxDetailList.size() > 0){
			String bxSql = getFromSal(isDetail, bxDetailList.toArray(new String[0]), "er_bxzb");
			if(bxSql != null){
				bxSql+=masql;
				sqlList.add(bxSql);
				
			}
		}
		
		List<String> meDetaillist = effectPkMap.get("35");
		if (meDetaillist != null && meDetaillist.size() > 0) {// 营销费用单
			boolean isInstallMe = BXUtil.isProductTbbInstalled(BXConstans.ME_FUNCODE);
			if (isInstallMe) {
				// TODO根据接口查询
				IErmMaSubBudgetSql subService = (IErmMaSubBudgetSql) ObjectCreator.newInstance("me", "nc.vo.so.m35meext.pub.ErmMaSubBudgetSqlImpl");
				String[] effectPks = subService.getMaBudgetSubBillEffectDetailPks(meDetaillist.toArray(new String[0]), ntbParam);
				String meSql = getFromSubSql(isDetail, effectPks);

				if (meSql != null) {
					meSql+=masql;
					sqlList.add(meSql);
				}
			}
		}

		return sqlList.toArray(new String[]{});
	}
	
	/**
	 * 外系统获取sql
	 * @param isDetail
	 * @param effectPks 有效业务行pk集合
	 * @return
	 * @throws Exception
	 */
	private String getFromSubSql(boolean isDetail, String[] effectPks) throws Exception {
		if (effectPks == null || effectPks.length == 0) {
			return null;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select " + getFromMaSelectFields(isDetail));
		sql.append(" from  er_mtapp_pf pf ");
		sql.append("  inner join er_mtapp_bill ma on pf.pk_matterapp = ma.pk_mtapp_bill  ");
		sql.append(" where 1=1 and " + SqlUtils.getInStr("pf.busi_detail_pk", effectPks, true));
		return sql.toString();
	}

	private String getFromSal(boolean isDetail, String[] effectPks, String tableName) throws Exception {
		if(effectPks == null || effectPks.length == 0 ){
			return null;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select " + getFromMaSelectFields(isDetail));
		sql.append(" from  er_mtapp_pf pf inner join " + tableName + " zb on pf.busi_pk = zb.pk_jkbx  ");
		sql.append("  inner join er_mtapp_bill ma on pf.pk_matterapp = ma.pk_mtapp_bill  ");
		sql.append(" where 1=1 and " + nc.vo.fi.pub.SqlUtils.getInStr("pf.busi_detail_pk", effectPks, true));
		sql.append(" and " + getFromMaBillStatus());
		return sql.toString();
	}
	
	private Object getFromMaBillStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		//ma最终策略
		DataRuleVO srcFinalDataRule = ErBudgetUtil.getBillYsExeDataRule(getSelfBillTypes().get(0));
		if(srcFinalDataRule == null){
			return " 1=0 ";
		}
		
		String[] forwordBilltypes = getBXBillTypes();
		
		String bxBiltype = null;
		if(forwordBilltypes == null || forwordBilltypes.length == 0){
			bxBiltype = BXConstans.BX_DJLXBM;
		}else{
			bxBiltype = forwordBilltypes[0];
		}
		//报销单最终策略
		DataRuleVO forwordFinalDataRule = ErBudgetUtil.getBillYsExeDataRule(bxBiltype);
		if(forwordFinalDataRule == null){//下游没有控制策略
			return " 1=0 ";
		}
		
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if(srcFinalDataRule.getDataType().equals(IFormulaFuncName.PREFIND)){//最终策略
				sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2,3) ");
			}else{
				DataRuleVO[] saveRuleVo = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(
						bxBiltype, BXConstans.ERM_NTB_SAVE_KEY, false);
				
				if(saveRuleVo != null){
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2) ");
				}
			}
		} else { // 执行
			if(srcFinalDataRule.getDataType().equals(IFormulaFuncName.PREFIND)){//最终策略
				return " 1=0 ";
			}else {
				if(forwordFinalDataRule.getActionCode().equals(BXConstans.ERM_NTB_APPROVE_KEY)){
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (3) ");//审核占执行数
				}else if(forwordFinalDataRule.getActionCode().equals(BXConstans.ERM_NTB_SAVE_KEY)){
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2,3) ");//保存即占执行数
				}else{
					return " 1=0 ";//无策略
				}
			}
		}
		
		return sql.toString();
	}
	
	/**
	 * 获取报销单交易类型
	 * @return
	 */
	private String[] getBXBillTypes(){
		List<String> result = new ArrayList<String>();
		
		String billtypStr = getNtbParam().getBill_type();
		TokenTools token = null;
		if (billtypStr.indexOf("#") != -1) {
			token = new TokenTools(billtypStr, "#", false);
		} else {
			token = new TokenTools(billtypStr, ",", false);
		}

		String[] billtypes = token.getStringArray();
		
		for(String billType : billtypes){
			if(billType.startsWith("264")){
				result.add(billType);
			}
		}
		
		return result.toArray(new String[0]);
	}

	private String getFromMaSelectFields(boolean isDetail) {
		StringBuffer sql = new StringBuffer();
		if (isDetail) {
			sql.append("ma.pk_tradetype djlxbm, ma.pk_currtype bzbm,ma.billdate djrq,ma.approvetime shrq," +
						"ma.approvetime jsrq, ma.reason zy, ma.billno djbh,ma.pk_group pk_group,ma.pk_org pk_org,");
			sql.append(" '" + ErmBillConst.MatterApp_BILLTYPE+ "' pk_billtype, ma.pk_mtapp_bill pk_jkbx ,");
			sql.append(" -pf.global_fy_amount globalbbje,-pf.group_fy_amount groupbbje, -pf.org_fy_amount bbje,-pf.fy_amount ybje ");
		} else {
			// 631不处理借款单、冲借款占用申请单的预算，且费用金额定位为下游单据执行数与费用申请单余额的小值，所以不管预占还是执行都应该按照费用金额查询
			sql.append(" sum(-pf.global_fy_amount) globalbbje,sum(-pf.group_fy_amount) groupbbje, sum(-pf.org_fy_amount) bbje,sum(-pf.fy_amount) ybje ");
		}
		return sql.toString();
	}
	
	/**
	 * 获取申请记录Map<单据大类,业务行pk集合>
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<String>> getEffectPfPksMap() throws Exception {
		// TODO 需要处理其他下游的预算取数情况
		final Map<String, List<String>> result = new HashMap<String, List<String>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct pf.pk_mtapp_detail, pf.pk_mtapp_pf,pf.pk_djdl, pf.busi_detail_pk from ");
		sql.append(" er_mtapp_detail mad inner join er_mtapp_bill ma on ma.pk_mtapp_bill=mad.pk_mtapp_bill");
		sql.append(" right join  ER_MTAPP_PF pf on pf.PK_MTAPP_DETAIL = mad.PK_MTAPP_DETAIL ");
		sql.append(" where 1=1 " + getWhereSql());
		sql.append(" and ma." + MatterAppVO.BILLSTATUS + " = " + ErmMatterAppConst.BILLSTATUS_APPROVED);
		
		new BaseDAO().executeQuery(sql.toString(), new BaseProcessor(){
			private static final long serialVersionUID = 1L;
			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					String djdl = rs.getString(MtapppfVO.PK_DJDL);
					if(result.get(djdl) == null){
						List<String> pkList = new ArrayList<String>();
						pkList.add(rs.getString(MtapppfVO.BUSI_DETAIL_PK ));
						result.put(djdl, pkList);
					}else{
						result.get(djdl).add(rs.getString(MtapppfVO.BUSI_DETAIL_PK ));
					}
				}
				return null;
			}
			
		});
		
		return result;
	}

	private String getSqlMa(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getFromSql());
		sql.append(" where 1=1 " + getWhereSql());

		// sql.append(" and mad.close_status = " +
		// ErmMatterAppConst.CLOSESTATUS_N + " ");
		// 单据状态
		sql.append(getBillStatus());
		return sql.toString();
	}
	
	private String getSqlMaForSP(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getFromSql());
		sql.append(" where 1=1 " + getWhereSql());

		// sql.append(" and mad.close_status = " +
		// ErmMatterAppConst.CLOSESTATUS_N + " ");
		// 单据状态
		return sql.toString();
	}

	private String getBillStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("ma." + MatterAppVO.BILLSTATUS + " in (" + ErmMatterAppConst.BILLSTATUS_SAVED  + ")");
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("ma." + MatterAppVO.BILLSTATUS + " = " + ErmMatterAppConst.BILLSTATUS_APPROVED);
				}
			}

		} else { // 执行
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("ma." + MatterAppVO.BILLSTATUS + " >= " + ErmMatterAppConst.BILLSTATUS_SAVED);
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("ma." + MatterAppVO.BILLSTATUS + " = " + ErmMatterAppConst.BILLSTATUS_APPROVED);
				}
			}
		}
		return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
	}

	private String getSelectFields(boolean isDetail) {
		StringBuffer sql = new StringBuffer();
		if (isDetail) {
			String orgAttr = getNtbParam().getOrg_Attr();
			if(orgAttr == null){
				sql.append("ma.pk_org pk_org,");
			}else{
				String src_org = getSrcField(getBillType(),orgAttr);
				sql.append(src_org + " pk_org,");
			}
			
			sql.append(" ma.pk_tradetype djlxbm, ma.pk_currtype bzbm, ma.billdate djrq, ma.approvetime shrq,");
			sql.append(" ma.approvetime jsrq, ma.reason zy, ma.billno djbh, ma.pk_group pk_group,");
			sql.append(" '" + ErmBillConst.MatterApp_BILLTYPE + "' pk_billtype, ma.pk_mtapp_bill pk_jkbx ,");
			
			sql.append(" (case when ma.close_status = 1 and mad.global_rest_amount > 0 then (mad.global_amount - mad.global_rest_amount) ");
			sql.append(" else mad.global_amount end) globalbbje,");
			
			sql.append(" (case when ma.close_status = 1 and mad.group_rest_amount > 0 then (mad.group_amount - mad.group_rest_amount) " );
			sql.append(" else mad.group_amount end) groupbbje, ");
			
			sql.append(" (case when ma.close_status = 1 and mad.org_rest_amount > 0 then (mad.org_amount - mad.org_rest_amount) " );
			sql.append(" else mad.org_amount end) bbje,");
			
			sql.append(" (case when ma.close_status = 1 and mad.rest_amount > 0 then (mad.orig_amount - mad.rest_amount)");
			sql.append(" else mad.orig_amount end) ybje ");
		} else {
			sql.append(" sum(case when ma.close_status = 1 and mad.global_rest_amount > 0 then (mad.global_amount - mad.global_rest_amount) ");
			sql.append(" else mad.global_amount end )globalbbje,");
			
			sql.append(" sum(case when ma.close_status = 1 and mad.group_rest_amount > 0 then (mad.group_amount - mad.group_rest_amount) ");
			sql.append(" else mad.group_amount end) groupbbje, ");
			
			
			sql.append(" sum(case when ma.close_status = 1 and mad.org_rest_amount > 0 then (mad.org_amount - mad.org_rest_amount)" );
			sql.append(" else mad.org_amount end) bbje,");
			
			sql.append(" sum(case when ma.close_status = 1 and mad.rest_amount > 0 then (mad.orig_amount - mad.rest_amount)");
			sql.append(" else mad.orig_amount end) ybje ");
		}
		return sql.toString();
	}

	private String getFromSql() {
		String from = " er_mtapp_detail mad inner join er_mtapp_bill ma on ma.pk_mtapp_bill=mad.pk_mtapp_bill ";
		return from;
	}
}
