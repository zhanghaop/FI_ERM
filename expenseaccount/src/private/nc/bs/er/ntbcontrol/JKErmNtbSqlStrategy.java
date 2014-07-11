package nc.bs.er.ntbcontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.IFormulaFuncName;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.control.TokenTools;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 借款单预算取数策略
 * @author chenshuaia
 *
 */
@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "借款单预算取数策略" /*-=notranslate=-*/,type=BusinessType.NORMAL)
public class JKErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	
	public JKErmNtbSqlStrategy(){
		setBillType(BXConstans.JK_DJLXBM);
	}

	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		//设置预算参数与单据类型
		this.ntbParam = ntbParam;
		
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlJk(false));
		
		String fromJkSql = getSqlFromJk(false);
		if (fromJkSql != null) {
			sqlList.add(fromJkSql);
		}
		return sqlList;
	}

	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception  {
		//设置预算参数与单据类型
		this.ntbParam = ntbParam;
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlJk(true));
		
		String fromJkSql = getSqlFromJk(true);// 借款单下游单据
		if (fromJkSql != null) {
			sqlList.add(fromJkSql);
		}
		return sqlList;
	}
	
	/**
	 * 获取借款的下游
	 * @return
	 * @throws Exception 
	 */
	private String getSqlFromJk(boolean isDetail) throws Exception{
		String[] effectPks = getEffectJkDetailPks();
		
		if (ArrayUtils.isEmpty(effectPks)) {
			return null;
		}
		
		return getFromJkSql(isDetail, effectPks);
	}
	
	private String getFromJkSql(boolean isDetail, String[] effectPks) throws SQLException, BusinessException {
		StringBuffer sql = new StringBuffer();
		sql.append("select " + getFromJKSelectFields(isDetail));
		sql.append(" from  er_bxcontrast cst inner join er_busitem fb on cst.pk_finitem = fb.pk_busitem ");
		sql.append(" left join er_bxzb zb on fb.pk_jkbx = zb.pk_jkbx ");
		sql.append(" where 1=1 and " + SqlUtils.getInStr("cst.pk_busitem", effectPks));
		sql.append(" and " + getFromJkBillStatus());

		return sql.toString();
	}

	private String getFromJkBillStatus() throws BusinessException {
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

	private String getFromJKSelectFields(boolean isDetail) {
		StringBuffer sql = new StringBuffer();
		if (isDetail) {
			String orgAttr = getNtbParam().getOrg_Attr();
			String src_org = getSrcField(getBillType(),orgAttr);
			if(src_org == null){
				sql.append(src_org + " pk_org,");
			}
			
			if(sql.length() == 0){
				sql.append("zb.pk_org pk_org,");
			}
			//冲借款，保存时用费用原币金额；生效时取冲借款金额
			sql.append("zb.djlxbm djlxbm, zb.bzbm bzbm, zb.djrq djrq, zb.shrq  shrq, zb.jsrq jsrq, zb.zy zy, zb.djbh djbh, zb.pk_group pk_group,");
			sql.append(" '" + BXConstans.BX_DJLXBM+ "' pk_billtype, zb.pk_jkbx pk_jkbx ,");
			if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
				sql.append(" -cst.GLOBALFYBBJE globalbbje,-cst.GROUPFYBBJE groupbbje, -cst.FYBBJE bbje,-cst.FYYBJE ybje ");
			} else {
				sql.append(" -cst.GLOBALCJKBBJE globalbbje,-cst.GROUPCJKBBJE groupbbje, -cst.CJKBBJE bbje,-cst.CJKYBJE ybje ");
			}
		} else {
			if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
				sql.append(" sum(-cst.GLOBALFYBBJE) globalbbje,sum(-cst.GROUPFYBBJE) groupbbje, sum(-cst.FYBBJE) bbje,sum(-cst.FYYBJE) ybje ");
			} else {
				sql.append(" sum(-cst.GLOBALCJKBBJE) globalbbje,sum(-cst.GROUPCJKBBJE) groupbbje, sum(-cst.CJKBBJE) bbje,sum(-cst.CJKYBJE) ybje ");
			}
		}

		return sql.toString();
	}

	private String[] getEffectJkDetailPks() throws Exception {
		String[] detailPks = null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct fb.pk_busitem from ");
		sql.append(" er_busitem fb inner join er_jkzb zb on fb.pk_jkbx = zb.pk_jkbx ");
		sql.append(" right join  er_bxcontrast cst on cst.pk_busitem = fb.pk_busitem ");
		sql.append(" where 1=1 " + getWhereSql());
		sql.append(" and zb." + JKBXHeaderVO.DJZT + " = 3 ");
		sql.append(" and cst." + BxcontrastVO.SXBZ + " != 2 ");//暂存时生效标志为2
		// TODO pk_item在借款报销单据中的外键需要设置默认值，不能都是null值，无法使用索引
		// 631拉单的借款单不回写预算，所以查询借款预算时不包括拉单的数据
		sql.append(" and (zb.pk_item is null or zb.pk_item = '~') ");

		@SuppressWarnings("rawtypes")
		List result = (List)new BaseDAO().executeQuery(sql.toString(), new ResultSetProcessor(){
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> temp = new ArrayList<String>();
				while(rs.next()){
					temp.add(rs.getString(1));
				}
				return temp;
			}
			
		});
		
		if(result != null && result.size() > 0){
			detailPks = new String[result.size()];
			for(int i = 0; i < result.size(); i ++){
				detailPks[i] = (String)result.get(i);
			}
		}

		return detailPks;
	}
	private String getSqlJk(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getJkFromSql());
		sql.append(" where 1=1 " + getWhereSql());
		sql.append(" and fb.dr=0 and zb.dr=0 and zb.qcbz='N' ");
		// TODO pk_item在借款报销单据中的外键需要设置默认值，不能都是null值，无法使用索引
		// 631拉单的借款单不回写预算，所以查询借款预算时不包括拉单的数据
		sql.append(" and (zb.pk_item is null or zb.pk_item = '~') ");
		// 单据状态
		sql.append(getBillStatus(BXConstans.JK_DJLXBM));
		return sql.toString();
	}

	private String getBillStatus(String billType) throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2) ");
				} else {
					sql.append(" 1 =0 ");
				}
			}
		} else { // 执行
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" >= 1 ");
					return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" = 3 ");
				}
			}
		}
		return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
	}

	private String getSelectFields(boolean isDetail) {
		StringBuffer sql = new StringBuffer();
		if (isDetail) {
			String orgAttr = getNtbParam().getOrg_Attr();
			String src_org = getSrcField(getBillType(),orgAttr);
			if(src_org == null){
				sql.append(src_org + " pk_org,");
			}
			
			if(sql.length() == 0){
				sql.append("zb.pk_org pk_org,");
			}
			
			sql.append("zb.djlxbm djlxbm, zb.bzbm bzbm, zb.djrq djrq, zb.shrq  shrq, zb.jsrq jsrq, zb.zy zy, zb.djbh djbh, zb.pk_group pk_group,");
			sql.append(" '" + BXConstans.JK_DJLXBM + "' pk_billtype, zb.pk_jkbx pk_jkbx, ");
			sql.append(" fb.globalbbje globalbbje,fb.groupbbje groupbbje, fb.bbje bbje,fb.ybje ybje ");
		} else {
			sql.append(" sum(fb.globalbbje) globalbbje,sum(fb.groupbbje) groupbbje, sum(fb.bbje) bbje,sum(fb.ybje) ybje ");
		}
		return sql.toString();
	}

	private String getJkFromSql() {
		String from = " er_busitem fb inner join er_jkzb zb on zb.pk_jkbx=fb.pk_jkbx ";
		return from;
	}

}
