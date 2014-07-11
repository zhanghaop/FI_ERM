package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.tb.obj.NtbParamVO;
/**
 * 报销sql预算查询策略
 * @author chenshuaia
 *
 */
@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "报销单预算取数策略" /*-=notranslate=-*/,type=BusinessType.NORMAL)
public class BXErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	
	public BXErmNtbSqlStrategy(){
		setBillType(BXConstans.BX_DJLXBM);
	}
	
	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		//设置预算参数与单据类型
		this.ntbParam = ntbParam;
		
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlBx(false));
		return sqlList;
	}
	
	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception {
		this.ntbParam = ntbParam;
		// 设置预算参数与单据类型
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlBx(true));
		return sqlList;
	}

	private String getSqlBx(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getBxFromSql());
		sql.append(" where 1=1 " + getWhereSql());
		
		//过滤掉摊销与分摊的报销单
		sql.append(" and fb.dr=0 and zb.dr=0 and zb.qcbz='N' ");
		sql.append(" and zb.isexpamt = 'N' ");
		sql.append(" and (cs.src_id is null or (cs.src_type = 0 and cs.billstatus < 2))");
		// 过滤掉核销预提的报销单
		sql.append(" and (acv.pk_bxd is null)");
		//单据状态 
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
	
	private String getBxFromSql() {
		String from = " er_bxzb zb inner join er_busitem fb on zb.pk_jkbx = fb.pk_jkbx left join er_costshare cs on cs.src_id = zb.pk_jkbx" +
				" left join er_accrued_verify acv on acv.pk_bxd = zb.pk_jkbx ";
		return from;
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
			sql.append(" '" + BXConstans.BX_DJLXBM + "' pk_billtype, zb.pk_jkbx pk_jkbx ,");
			sql.append(" fb.globalbbje globalbbje,fb.groupbbje groupbbje, fb.bbje bbje,fb.ybje ybje ");
		} else {
			sql.append(" sum(fb.globalbbje) globalbbje,sum(fb.groupbbje) groupbbje, sum(fb.bbje) bbje,sum(fb.ybje) ybje ");
		}
		return sql.toString();
	}
}
