package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.tb.obj.NtbParamVO;

@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "结转单预算取数策略" /*-=notranslate=-*/,type=BusinessType.NORMAL)
public class CSErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	
	public CSErmNtbSqlStrategy(){
		setBillType(IErmCostShareConst.COSTSHARE_BILLTYPE);
	}

	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		//设置预算参数
		this.ntbParam = ntbParam;
		
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlCs(false));
		return sqlList;
	}

	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception {
		//设置预算参数
		this.ntbParam = ntbParam;
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlCs(true));
		return sqlList;
	}
	
	private String getSqlCs(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getFromSql());
		sql.append(" where 1=1 " + getWhereSql());
		sql.append(" and cs.isexpamt ='N' " + getWhereSql());
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
					sql.append("cs.src_type = 1 and ");//预占时仅事前分摊占预算
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" = ").append(BXStatusConst.DJZT_Saved);
				} 
			}
		} else { // 执行
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" >= 1 ");
					return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
				}
			}
			
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" = ").append(BXStatusConst.DJZT_Sign);
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
			if(src_org != null){
				sql.append(src_org + " pk_org,");
			}
			
			if(sql.length() == 0){
				sql.append("csd.assume_org pk_org,");
			}
			
			sql.append("cs.pk_tradetype djlxbm, cs.bzbm bzbm, cs.billdate djrq, cs.approvedate  shrq, ");
			sql.append("cs.approvedate jsrq, cs.zy zy, cs.billno djbh, cs.pk_group pk_group, ");
			sql.append(" csd.globalbbje globalbbje,csd.groupbbje groupbbje, csd.bbje bbje,csd.assume_amount ybje ");
		} else {
			sql.append(" sum(csd.globalbbje) globalbbje,sum(csd.groupbbje) groupbbje, sum(csd.bbje) bbje,sum(csd.assume_amount) ybje ");
		}
		return sql.toString();
	}

	private String getFromSql() {
		String from = " er_cshare_detail csd inner join er_costshare cs on cs.pk_costshare=csd.pk_costshare ";
		return from;
	}

}
