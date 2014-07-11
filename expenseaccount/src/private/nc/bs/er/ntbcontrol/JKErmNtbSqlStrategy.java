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
 * ��Ԥ��ȡ������
 * @author chenshuaia
 *
 */
@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "��Ԥ��ȡ������" /*-=notranslate=-*/,type=BusinessType.NORMAL)
public class JKErmNtbSqlStrategy extends AbstractErmNtbSqlStrategy {
	
	public JKErmNtbSqlStrategy(){
		setBillType(BXConstans.JK_DJLXBM);
	}

	@Override
	public List<String> getSqls(NtbParamVO ntbParam) throws Exception {
		//����Ԥ������뵥������
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
		//����Ԥ������뵥������
		this.ntbParam = ntbParam;
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getSqlJk(true));
		
		String fromJkSql = getSqlFromJk(true);// �����ε���
		if (fromJkSql != null) {
			sqlList.add(fromJkSql);
		}
		return sqlList;
	}
	
	/**
	 * ��ȡ��������
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
		//ma���ղ���
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
		
		//���������ղ���
		DataRuleVO forwordFinalDataRule = ErBudgetUtil.getBillYsExeDataRule(bxBiltype);
		if(forwordFinalDataRule == null){//����û�п��Ʋ���
			return " 1=0 ";
		}
		
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// Ԥռ��
			if(srcFinalDataRule.getDataType().equals(IFormulaFuncName.PREFIND)){//���ղ���
				sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2,3) ");
			}else{
				DataRuleVO[] saveRuleVo = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(
						bxBiltype, BXConstans.ERM_NTB_SAVE_KEY, false);
				
				if(saveRuleVo != null){
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2) ");
				}
			}
		} else { // ִ��
			if(srcFinalDataRule.getDataType().equals(IFormulaFuncName.PREFIND)){//���ղ���
				return " 1=0 ";
			}else {
				if(forwordFinalDataRule.getActionCode().equals(BXConstans.ERM_NTB_APPROVE_KEY)){
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (3) ");//���ռִ����
				}else if(forwordFinalDataRule.getActionCode().equals(BXConstans.ERM_NTB_SAVE_KEY)){
					sql.append(" zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2,3) ");//���漴ռִ����
				}else{
					return " 1=0 ";//�޲���
				}
			}
		}
		return sql.toString();
	}
	
	/**
	 * ��ȡ��������������
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
			//�������ʱ�÷���ԭ�ҽ���Чʱȡ������
			sql.append("zb.djlxbm djlxbm, zb.bzbm bzbm, zb.djrq djrq, zb.shrq  shrq, zb.jsrq jsrq, zb.zy zy, zb.djbh djbh, zb.pk_group pk_group,");
			sql.append(" '" + BXConstans.BX_DJLXBM+ "' pk_billtype, zb.pk_jkbx pk_jkbx ,");
			if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// Ԥռ��
				sql.append(" -cst.GLOBALFYBBJE globalbbje,-cst.GROUPFYBBJE groupbbje, -cst.FYBBJE bbje,-cst.FYYBJE ybje ");
			} else {
				sql.append(" -cst.GLOBALCJKBBJE globalbbje,-cst.GROUPCJKBBJE groupbbje, -cst.CJKBBJE bbje,-cst.CJKYBJE ybje ");
			}
		} else {
			if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// Ԥռ��
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
		sql.append(" and cst." + BxcontrastVO.SXBZ + " != 2 ");//�ݴ�ʱ��Ч��־Ϊ2
		// TODO pk_item�ڽ��������е������Ҫ����Ĭ��ֵ�����ܶ���nullֵ���޷�ʹ������
		// 631�����Ľ�����дԤ�㣬���Բ�ѯ���Ԥ��ʱ����������������
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
		// TODO pk_item�ڽ��������е������Ҫ����Ĭ��ֵ�����ܶ���nullֵ���޷�ʹ������
		// 631�����Ľ�����дԤ�㣬���Բ�ѯ���Ԥ��ʱ����������������
		sql.append(" and (zb.pk_item is null or zb.pk_item = '~') ");
		// ����״̬
		sql.append(getBillStatus(BXConstans.JK_DJLXBM));
		return sql.toString();
	}

	private String getBillStatus(String billType) throws Exception {
		StringBuffer sql = new StringBuffer();
		Map<String, NtbObj> actionCodeMap = getActionCodeMap();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(getNtbParam().getMethodCode())) {// Ԥռ��
			if (actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = actionCodeMap.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2) ");
				} else {
					sql.append(" 1 =0 ");
				}
			}
		} else { // ִ��
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
