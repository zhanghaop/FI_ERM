package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.ErmDjlxConst;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.tb.obj.NtbParamVO;

@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "��ת��Ԥ��ȡ������" /*-=notranslate=-*/,type=BusinessType.NORMAL)
public class CSErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	
	public CSErmNtbSqlStrategy(){
		setBillType(IErmCostShareConst.COSTSHARE_BILLTYPE);
	}

	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		//����Ԥ�����
		this.ntbParam = ntbParam;
		
		List<String> sqlList = new ArrayList<String>();
		// �ǵ��������
		sqlList.add(getSqlCs(false,false));
		// ���������
		sqlList.add(getSqlCs(false,true));
		return sqlList;
	}

	@Override
	public List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception {
		//����Ԥ�����
		this.ntbParam = ntbParam;
		List<String> sqlList = new ArrayList<String>();
		// �ǵ��������
		sqlList.add(getSqlCs(true,false));
		// ���������
		sqlList.add(getSqlCs(true,true));
		return sqlList;
	}
	
	private String getSqlCs(boolean isDetail,boolean isAdjust) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select " + getSelectFields(isDetail));
		sql.append(" from " + getFromSql());
		sql.append(" where 1=1 " + getWhereSql(isAdjust));
		sql.append(" and cs.isexpamt ='N' ");
		// ����״̬
		sql.append(getBillStatus());
		// ��������������
		sql.append(" and ");
		if(!isAdjust){
			sql.append(" not ");
		}
		sql.append("exists (select 1 from er_djlx djlx where cs.djlxbm=djlx.djlxbm and cs.pk_group = djlx.pk_group" +
				" and  djlx.bxtype = "+ErmDjlxConst.BXTYPE_ADJUST+ ")");
		
		return sql.toString();
	}

	private String getBillStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// Ԥռ��
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("cs.src_type = 1 and ");//Ԥռʱ����ǰ��̯ռԤ��
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" = ").append(BXStatusConst.DJZT_Saved);
				} 
			}
		} else { // ִ��
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
			sql.append(" '" + ErmBillConst.CostShare_BILLTYPE + "' pk_billtype, cs.pk_costshare pk_jkbx ,");
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

	@Override
	protected String getDateTypeField(NtbParamVO ntbvo, boolean isAdjust) {
		if(isAdjust){
			// ��������������շ�̯��ϸ�е�Ԥ��ռ�����ڽ�������ȡ��
			return "csd.ysdate";
		}else{
			return super.getDateTypeField(ntbvo, isAdjust);
		}
	}
}
