package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.common.ErmBillConst;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 预提单取数策略
 * 
 * @author chenshuaia
 * 
 */
public class ACErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	
	public ACErmNtbSqlStrategy(){
		setBillType(ErmBillConst.AccruedBill_Billtype);
	}
	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		// 设置预算参数与单据类型
		this.ntbParam = ntbParam;

		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSql(false));
		return sqlList;
	}

	private String getSql(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getFromSql());
		sql.append(" where 1=1 " + getWhereSql());
		// 单据状态
		sql.append(getBillStatus());
		return sql.toString();
	}

	private Object getBillStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// 预占用
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("ac." + AccruedVO.BILLSTATUS + " in (" + ErmAccruedBillConst.BILLSTATUS_SAVED + ")");
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("ac." + AccruedVO.BILLSTATUS + " = " + ErmAccruedBillConst.BILLSTATUS_APPROVED);
				}
			}

		} else { // 执行
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("ac." + AccruedVO.BILLSTATUS + " >= " + ErmAccruedBillConst.BILLSTATUS_SAVED);
				}
			}
			if (actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("ac." + AccruedVO.BILLSTATUS + " = " + ErmAccruedBillConst.BILLSTATUS_APPROVED);
				}
			}
		}
		return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
	}

	private String getSelectFields(boolean isDetail) {
		StringBuffer sql = new StringBuffer();
		if (isDetail) {
			String orgAttr = getNtbParam().getOrg_Attr();
			String src_org = getSrcField(getBillType(), orgAttr);
			if (src_org != null) {
				sql.append(src_org + " pk_org,");
			}

			if (sql.length() == 0) {
				sql.append("acd.assume_org pk_org,");
			}

			sql.append("ac.pk_tradetype djlxbm, ac.pk_currtype bzbm, ac.billdate djrq, ac.approvetime  shrq, ");
			sql.append("ac.approvetime jsrq, ac.reason zy, ac.billno djbh, ac.pk_group pk_group, ");
			sql.append(" acd.global_amount globalbbje,acd.group_amount groupbbje, acd.org_amount bbje,acd.amount ybje ");
		} else {
			sql.append(" sum(acd.global_amount) globalbbje,sum(acd.group_amount) groupbbje, sum(acd.org_amount) bbje,sum(acd.amount) ybje ");
		}
		return sql.toString();
	}

	private String getFromSql() {
		return " er_accrued ac inner join  er_accrued_detail acd on ac.pk_accrued_bill = acd.pk_accrued_bill ";
	}

	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception {
		// 设置预算参数与单据类型
		this.ntbParam = ntbParam;
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSql(true));
		return sqlList;
	}
}
