package nc.bs.er.ntbcontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.itf.tb.control.IFormulaFuncName;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
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
		String[] fromMaSqls = getSqlFromMa(false);
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

		String[] fromMaSqls = getSqlFromMa(true);
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
	 * @return
	 * @throws Exception
	 */
	private String[] getSqlFromMa(boolean isDetail) throws Exception {
		Map<String, List<String>> effectPkMap = getEffectPfPks();

		if (effectPkMap.get("bx").size() == 0) {
			return null;
		}

		String[] result = new String[3];
		
		result[0] = getFromSal(isDetail, effectPkMap.get("bx").toArray(new String[0]), "er_jkzb");
		result[1] = getFromSal(isDetail, effectPkMap.get("bx").toArray(new String[0]), "er_bxzb");
		
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					result[2] = getFromContrastSal(isDetail, effectPkMap.get("cjk1").toArray(new String[0]));
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					result[2] = getFromContrastSal(isDetail, effectPkMap.get("cjk2").toArray(new String[0]));
				}
			}

		} else { // 执行
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					result[2] = getFromContrastSal(isDetail, effectPkMap.get("cjk1").toArray(new String[0]));
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					result[2] = getFromContrastSal(isDetail, effectPkMap.get("cjk2").toArray(new String[0]));
				}
			}
		}

		return result;
	}
	
	private String getFromContrastSal(boolean isDetail, String[] effectPks) throws SQLException {
		if(effectPks == null || effectPks.length == 0 ){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select " + getFromMaSelectFields(isDetail));
		sql.append(" from  er_mtapp_pf pf inner join er_jkzb zb on pf.busi_pk = zb.pk_jkbx  ");
		sql.append(" where 1=1 and " + SqlUtils.getInStr("pf.pk_mtapp_pf", effectPks));
		return sql.toString();
	}

	private String getFromSal(boolean isDetail, String[] effectPks, String tableName) throws Exception {
		if(effectPks == null || effectPks.length == 0 ){
			return null;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select " + getFromMaSelectFields(isDetail));
		sql.append(" from  er_mtapp_pf pf inner join " + tableName + " zb on pf.busi_pk = zb.pk_jkbx  ");
		sql.append(" where 1=1 and " + SqlUtils.getInStr("pf.pk_mtapp_pf", effectPks));
		sql.append(" and " + getFromMaBillStatus());
		return sql.toString();
	}

	private Object getFromMaBillStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2) ");
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (3) ");
				}
			}

		} else { // 执行
			sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (3) ");
		}
		return sql.toString();
	}

	private String getFromMaSelectFields(boolean isDetail) {
		StringBuffer sql = new StringBuffer();
		if (isDetail) {
			sql.append("zb.djlxbm djlxbm, zb.bzbm bzbm, zb.djrq djrq, zb.shrq  shrq, zb.jsrq jsrq, zb.zy zy, zb.djbh djbh, zb.pk_group pk_group,zb.pk_org pk_org,");
			if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
				sql.append(" -pf.global_fy_amount globalbbje,-pf.group_fy_amount groupbbje, -pf.org_fy_amount bbje,-pf.fy_amount ybje ");
			} else {
				sql.append(" -pf.global_exe_amount globalbbje,-pf.group_exe_amount groupbbje, -pf.org_exe_amount bbje,-pf.exe_amount ybje ");
			}
		} else {
			if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
				sql.append(" sum(-pf.global_fy_amount) globalbbje,sum(-pf.group_fy_amount) groupbbje, sum(-pf.org_fy_amount) bbje,sum(-pf.fy_amount) ybje ");
			} else {
				sql.append(" sum(-pf.global_exe_amount) globalbbje,sum(-pf.group_exe_amount) groupbbje, sum(-pf.org_exe_amount) bbje,sum(-pf.exe_amount) ybje ");
			}
		}
		return sql.toString();
	}
	
	/**
	 * bx,cjk1,cjk2
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<String>> getEffectPfPks() throws Exception {
		
		final Map<String, List<String>> result = new HashMap<String, List<String>>();
		result.put("bx", new ArrayList<String>());
		result.put("cjk1", new ArrayList<String>());//未生效冲借款对应的申请记录行
		result.put("cjk2", new ArrayList<String>());//生效冲借款对应的申请记录行
		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct pf.pk_mtapp_detail, pf.pk_mtapp_pf,pf.pk_djdl, pf.busi_detail_pk from ");
		sql.append(" er_mtapp_detail mad inner join er_mtapp_bill ma on ma.pk_mtapp_bill=mad.pk_mtapp_bill");
		sql.append(" right join  ER_MTAPP_PF pf on pf.PK_MTAPP_DETAIL = mad.PK_MTAPP_DETAIL ");
		sql.append(" where 1=1 " + getWhereSql());
		sql.append(" and ma." + MatterAppVO.BILLSTATUS + " = " + ErmMatterAppConst.BILLSTATUS_APPROVED);
		
		final List<String> jkDetailList = new ArrayList<String>();
		final List<String> jkBxPfPkList = new ArrayList<String>();
		final Map<String, String> map = new HashMap<String, String>();
		new BaseDAO().executeQuery(sql.toString(), new BaseProcessor(){
			private static final long serialVersionUID = 1L;

			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					if(!rs.getString(MtapppfVO.PK_DJDL).equals("bx")){
						jkDetailList.add(rs.getString(MtapppfVO.BUSI_DETAIL_PK));
						map.put(rs.getString(MtapppfVO.BUSI_DETAIL_PK), rs.getString(MtapppfVO.PK_MTAPP_PF));
					}
					
					jkBxPfPkList.add(rs.getString(MtapppfVO.PK_MTAPP_PF));
				}
				return null;
			}
			
		});
		
		if (jkDetailList.size() > 0) {
			@SuppressWarnings("unchecked")
			Collection<BxcontrastVO> contrastVos = new BaseDAO().retrieveByClause(BxcontrastVO.class,
					SqlUtil.buildInSql(BxcontrastVO.PK_BXCONTRAST, jkDetailList));
			
			if(contrastVos != null && contrastVos.size() > 0){
				for (BxcontrastVO contrast : contrastVos) {
					if (BXStatusConst.SXBZ_VALID == contrast.getSxbz()) {
						result.get("cjk2").add(map.get(contrast.getPk_bxcontrast()));
					} else {
						result.get("cjk1").add(map.get(contrast.getPk_bxcontrast()));
					}
					jkBxPfPkList.remove(map.get(contrast.getPk_bxcontrast()));
				}
			}
		}

		result.get("bx").addAll(jkBxPfPkList);
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

	private String getBillStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("ma." + MatterAppVO.BILLSTATUS + " in (" + ErmMatterAppConst.BILLSTATUS_SAVED + ","
							+ ErmMatterAppConst.BILLSTATUS_COMMITED + ")");
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
			sql.append(" (case when ma.close_status = 1 then (mad.global_amount - mad.global_rest_amount) else mad.global_amount end) globalbbje,");
			sql.append(" (case when ma.close_status = 1 then (mad.group_amount - mad.group_rest_amount) else mad.group_amount end) groupbbje, ");
			sql.append(" (case when ma.close_status = 1 then (mad.org_amount - mad.org_rest_amount) else mad.org_amount end) bbje,");
			sql.append(" (case when ma.close_status = 1 then (mad.orig_amount - mad.rest_amount) else mad.orig_amount end) ybje ");
		} else {
			sql.append(" sum(case when ma.close_status = 1 then (mad.global_amount - mad.global_rest_amount) else mad.global_amount end) globalbbje,");
			sql.append(" sum(case when ma.close_status = 1 then (mad.group_amount - mad.group_rest_amount) else mad.group_amount end) groupbbje, ");
			sql.append(" sum(case when ma.close_status = 1 then (mad.org_amount - mad.org_rest_amount) else mad.org_amount end) bbje,");
			sql.append(" sum(case when ma.close_status = 1 then (mad.orig_amount - mad.rest_amount) else mad.orig_amount end) ybje ");
		}
		return sql.toString();
	}

	private String getFromSql() {
		String from = " er_mtapp_detail mad inner join er_mtapp_bill ma on ma.pk_mtapp_bill=mad.pk_mtapp_bill ";
		return from;
	}
}
