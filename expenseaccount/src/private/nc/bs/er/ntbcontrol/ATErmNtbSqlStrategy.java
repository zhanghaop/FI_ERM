package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.tb.obj.NtbParamVO;

@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "摊销信息预算取数策略" /*-=notranslate=-*/,type=BusinessType.NORMAL)
public class ATErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	public ATErmNtbSqlStrategy(){
		setBillType(ExpAmoritizeConst.Expamoritize_BILLTYPE);
	}

	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		//设置预算参数与单据类型
		this.ntbParam = ntbParam;
		
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlAt(false));
		return sqlList;
	}

	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception {
		// 设置预算参数与单据类型
		this.ntbParam = ntbParam;
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlAt(true));
		return sqlList;
	}
	
	private String getSqlAt(boolean isDetail) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getFromSql());
		sql.append(" where 1=1 " + getWhereSql());
		// 单据状态
		sql.append(getBillStatus());
		return sql.toString();
	}

	private String getBillStatus() throws Exception {
		String sql = null;
		NtbObj obj = getActionCodeMap().get(BXConstans.ERM_NTB_AMORTIZE_KEY);
		
		if (IFormulaFuncName.UFIND.equals(getNtbParam().getMethodCode())){
			if (obj != null) {
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql = " 1=1 ";
				}
				
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql = " 1=1 ";
				}
			}
		}
		
		return sql == null ? " and 1=0 " : " and " + sql;
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
				sql.append("assume_org pk_org,");
			}
			
			sql.append(" at1.pk_billtype djlxbm, at1.bzbm, proc1.amortize_date djrq, proc1.amortize_date shrq, ");
			sql.append(" '" + ErmBillConst.Expamoritize_BILLTYPE + "' pk_billtype, at1.pk_expamtinfo pk_jkbx ,");
			sql.append(" proc1.amortize_date jsrq, '' zy, at1.bx_billno djbh, at1.pk_group pk_group, assume_org pk_org,");
			sql.append(" proc1.curr_globalamount globalbbje, proc1.curr_groupamount groupbbje, proc1.curr_orgamount bbje, proc1.curr_amount ybje ");

		} else {
			sql.append(" sum(proc1.curr_globalamount) globalbbje,sum(proc1.curr_groupamount) groupbbje, sum(proc1.curr_orgamount) bbje,sum(proc1.curr_amount) ybje ");
		}
		return sql.toString();
	}

	private String getFromSql() {
		String from = " er_expamtdetail atd right join er_expamtproc proc1 on atd.pk_expamtdetail = proc1.pk_expamtinfo " +
				"inner join er_expamtinfo at1 on at1.pk_expamtinfo = atd.pk_expamtinfo ";
		return from;
	}
}
