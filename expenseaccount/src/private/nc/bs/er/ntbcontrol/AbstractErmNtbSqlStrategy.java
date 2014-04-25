package nc.bs.er.ntbcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.IFormulaFuncName;
import nc.itf.tb.control.OutEnum;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.control.TokenTools;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 
 * ����Ԥ��sql���ɲ��Գ����� ���ַ��õ������͵�sql�������������
 * 
 * @author chenshuaia
 * 
 */
public abstract class AbstractErmNtbSqlStrategy {
	private static final String ERM_CTRL_ST_KEY = "ERM0Z30001ERM0010001";

	/**
	 * Ԥ�����
	 */
	protected NtbParamVO ntbParam;

	/**
	 * ��������
	 */
	protected String billType;

	private Map<String, NtbObj> actionCodeMap = null;

	/**
	 * ��ȡ�ճ�ִ��sql
	 * 
	 * @param ntbParam
	 * @return
	 * @throws Exception
	 */
	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "��ѯ����������Ԥ��ȡ����sql�ӿ�" /*
																										 * -=
																										 * notranslate
																										 * =
																										 * -
																										 */, type = BusinessType.DOMAIN_INT)
	public abstract List<String> getSqls(NtbParamVO ntbParam) throws Exception;

	/**
	 * ��ȡ��ѯ������������ϸsql
	 * 
	 * @param ntbParam
	 * @return
	 * @throws Exception
	 */
	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "��ѯ��������Ԥ��ȡ����ϸ��sql�ӿ�" /*
																										 * -=
																										 * notranslate
																										 * =
																										 * -
																										 */, type = BusinessType.DOMAIN_INT)
	public abstract List<String> getDetailSqls(NtbParamVO ntbParam) throws Exception;

	protected String getWhereSql() throws Exception {
		StringBuffer whereSql = new StringBuffer();
		// �������ڡ��������ͻ�����Ϣ
		whereSql.append(getWhereFromVO(false));
		// ����pk����
		whereSql.append(getIncludeSubSql());
		return whereSql.toString();
	}

	/**
	 * ֧��Ԥ�����ڵ�����whereƴд��ʽ
	 * 
	 * @param isAdjust
	 * @return
	 * @throws Exception
	 */
	protected String getWhereSql(boolean isAdjust) throws Exception {
		StringBuffer whereSql = new StringBuffer();
		// �������ڡ��������ͻ�����Ϣ
		whereSql.append(getWhereFromVO(isAdjust));
		// ����pk����
		whereSql.append(getIncludeSubSql());
		return whereSql.toString();
	}

	/**
	 * ��ȡ��������pk��Ӧsql <br>
	 * <li>��ʽΪ��and pk in ('*','*')
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getIncludeSubSql() throws Exception {
		StringBuffer sql = new StringBuffer();

		NtbParamVO ntbvo = getNtbParam();
		String[] names = ntbvo.getBusiAttrs();

		for (int i = 0; i < names.length; i++) {
			String item_pk = ntbvo.getPkDim()[i];// ��Ԫ���Ӧ����pk
			boolean isIncludelower = ntbvo.getIncludelower()[i];// �Ƿ�����¼�

			List<String> bddata = new ArrayList<String>();
			if (!(item_pk.indexOf("|") >= 0) && !item_pk.equals(OutEnum.NOSUCHBASEPKATSUBCORP)) {
				if (isIncludelower) {
					HashMap<String, String[]> lowerArrays = ntbvo.getLowerArrays();
					if (lowerArrays != null && lowerArrays.get(names[i]) != null) {
						for (String data : lowerArrays.get(names[i])) {
							bddata.add(data);
						}
					}
				}
				bddata.add(item_pk);
			}
			
			String nameField = getSrcField(getBillType(), names[i]);
			if (nameField == null) {
				nameField = names[i];
			}

			// checkField(nameField);

			sql.append(" and " + SqlUtils.getInStr(nameField, bddata.toArray(new String[] {}), true));
		}

		return sql.toString();
	}

	// private void checkField(String field) throws BusinessException{
	// String billType = getBillType();
	// if(billType != null){
	// String headClassName = null;
	// String bodyClassName = null;
	// if(billType.startsWith("261")){
	// headClassName = MatterAppVO.class.getName();
	// bodyClassName = MtAppDetailVO.class.getName();
	// }else if(billType.startsWith("263")){
	// headClassName = JKHeaderVO.class.getName();
	// bodyClassName = BXBusItemVO.class.getName();
	// }else if(billType.startsWith("264")){
	// headClassName = BXHeaderVO.class.getName();
	// bodyClassName = BXBusItemVO.class.getName();
	// }else if(billType.startsWith("265")){
	// headClassName = CostShareVO.class.getName();
	// bodyClassName = CShareDetailVO.class.getName();
	// }else if(billType.startsWith("266")){
	// headClassName = MatterAppVO.class.getName();
	// bodyClassName = MtAppDetailVO.class.getName();
	// }
	// }
	// }

	/**
	 * where�����й̶�����<br>
	 * <li>���� <li>�������� <li>��������
	 * 
	 * @return
	 * @throws BusinessException
	 */
	protected String getWhereFromVO(boolean isAdjust) throws BusinessException {
		NtbParamVO ntbvo = getNtbParam();

		StringBuffer sql = new StringBuffer();

		String currtypeField = getSrcField(getBillType(), "pk_currtype");
		if (currtypeField == null) {
			currtypeField = "pk_currtype";
		}

		if (ntbvo.getCurr_type() == 3) {
			sql.append(" and " + currtypeField + " ='" + ntbvo.getPk_currency() + "' ");// ����
		}

		// ��������
		String dateTypeField = getDateTypeField(ntbvo, isAdjust);

		if (ntbvo.getBegDate() != null && ntbvo.getBegDate().toString().trim().length() > 0) {
			sql.append(" and " + dateTypeField + " >='" + ntbvo.getBegDate() + "' ");
		}
		if (ntbvo.getEndDate() != null && ntbvo.getEndDate().toString().trim().length() > 0) {
			sql.append(" and " + dateTypeField + " <='" + ntbvo.getEndDate() + "' ");
		}

		// ��������
		if (ntbvo.getBill_type() != null) {
			String billType = ntbvo.getBill_type();
			TokenTools token = null;
			if (billType.indexOf("#") != -1) {// ��Ԥ���������޸ģ���������¾ɵ������ͷָ���
				token = new TokenTools(billType, "#", false);
			} else {
				token = new TokenTools(billType, ",", false);
			}

			String[] billTypes = token.getStringArray();

			String billTypeField = getSrcField(getBillType(), "pk_billtype");
			if (billTypeField == null) {
				billTypeField = "pk_billtype";
			}

			sql.append(" and " + SqlUtils.getInStr(billTypeField, billTypes, true ));
		}

		return sql.toString();
	}

	/**
	 * ��ö�Ӧ���ݵ������ֶ�
	 * 
	 * @param ntbvo
	 * @param isAdjust
	 * @return
	 */
	protected String getDateTypeField(NtbParamVO ntbvo, boolean isAdjust) {
		String dateTypeField = ntbvo.getDateType();
		String dateStr = getSrcField(getBillType(), dateTypeField);
		if (dateStr != null) {
			dateTypeField = dateStr;
		} else {
			if (ErmBillConst.MatterApp_BILLTYPE.equals(getBillType())) {
				if (dateTypeField.equals(BXConstans.EFFECTDATE) || dateTypeField.equals(BXConstans.APPROVEDATE)) {
					dateTypeField = "ma.approvetime";
				} else {
					dateTypeField = "ma.billdate";
				}
			} else if (ErmBillConst.CostShare_BILLTYPE.equals(getBillType())) {
				if (dateTypeField.equals(BXConstans.EFFECTDATE) || dateTypeField.equals(BXConstans.APPROVEDATE)) {
					dateTypeField = "cs.approvedate";
				} else {
					dateTypeField = "cs.billdate";
				}
			} else if (ErmBillConst.Expamoritize_BILLTYPE.equals(getBillType())) {
				dateTypeField = "proc1.amortize_date";
			}
		}
		return dateTypeField;
	}

	/**
	 * ��ȡ������������(����������)
	 * 
	 * @return
	 * @throws BusinessException
	 */
	protected List<String> getSelfBillTypes() throws BusinessException {
		List<String> result = new ArrayList<String>();
		String billtypStr = getNtbParam().getBill_type();
		TokenTools token = null;
		if (billtypStr.indexOf("#") != -1) {
			token = new TokenTools(billtypStr, "#", false);
		} else {
			token = new TokenTools(billtypStr, ",", false);
		}

		String[] billtypes = token.getStringArray();// ����[2611,2641,2642,266X]

		if (billtypes.length > 0) {
			for (int i = 0; i < billtypes.length; i++) {
				String tradeBilltype = billtypes[i];
				BilltypeVO billtypeVO = getBilltypeVo(tradeBilltype);
				if (billtypeVO == null) {
					continue;
				}

				if (getBillType().equals(billtypeVO.getParentbilltype())) {
					result.add(billtypeVO.getPk_billtypecode());
				}
			}
		} else {
			BilltypeVO billtypeVO = getBilltypeVo(billtypStr);
			if (billtypeVO != null) {
				if (!getBillType().equals(billtypeVO.getPk_billtypecode())
						&& getBillType().equals(billtypeVO.getParentbilltype())) {
					result.add(billtypeVO.getPk_billtypecode());
				}
			}
		}
		return result;
	}

	private BilltypeVO getBilltypeVo(String tradeBilltype) throws BusinessException {
		BilltypeVO billtypeVO = PfDataCache.getBillType(tradeBilltype);
		if (billtypeVO == null) {
			StringBuffer sql = new StringBuffer();
			sql.append(" pk_billtypecode = '" + tradeBilltype + "' ");
			BilltypeVO[] billtypeVos = CacheUtil.getValueFromCacheByWherePart(BilltypeVO.class, sql.toString());
			if (billtypeVos != null && billtypeVos.length > 0) {
				billtypeVO = billtypeVos[0];
			}
		}
		// Log.getInstance(this.getClass()).error("�������Ͳ�ѯ��" + tradeBilltype +
		// billtypeVO);
		return billtypeVO;
	}

	/**
	 * ��ȡ
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Map<String, NtbObj> getActionCodeMap() throws Exception {
		actionCodeMap = new HashMap<String, NtbObj>();
		List<String> billtypesList = getSelfBillTypes();

		String tradeBilltype = null;
		if (billtypesList != null && billtypesList.size() > 0) {
			tradeBilltype = billtypesList.get(0);
		}
		String parentBilltype = getBillType();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTacticsBySysId(ERM_CTRL_ST_KEY);

		Map<String, DataRuleVO> tradeTypeMap = new HashMap<String, DataRuleVO>();
		Map<String, DataRuleVO> billTypeMap = new HashMap<String, DataRuleVO>();
		for (DataRuleVO ruleVo : ruleVos) {
			if (!ruleVo.isIsstart()) {
				continue;
			}

			if (tradeBilltype != null && tradeBilltype.equals(ruleVo.getBilltype_code())) {
				tradeTypeMap.put(ruleVo.getActionCode(), ruleVo);
			} else {
				if (parentBilltype.equals(ruleVo.getBilltype_code())) {
					billTypeMap.put(ruleVo.getActionCode(), ruleVo);
				}
			}
		}

		if (billTypeMap.size() > 0) {// ��������
			for (Map.Entry<String, DataRuleVO> entry : billTypeMap.entrySet()) {
				DataRuleVO ruleVo = entry.getValue();

				/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
				String methodFunc = ruleVo.getDataType();
				/** ��������ӣ�true������Ǽ��٣�false */
				boolean isAdd = ruleVo.isAdd();

				NtbObj ntbObj = new NtbObj(methodFunc.equals(IFormulaFuncName.UFIND) ? NtbType.EXE : NtbType.PREVIOUS,
						isAdd ? NtbDic.INC : NtbDic.DEC);
				actionCodeMap.put(ruleVo.getActionCode(), ntbObj);
			}
		}

		if (tradeTypeMap.size() > 0) {// �������͵����ȣ����Ǹ���������
			for (Map.Entry<String, DataRuleVO> entry : tradeTypeMap.entrySet()) {
				DataRuleVO ruleVo = entry.getValue();

				/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
				String methodFunc = ruleVo.getDataType();
				/** ��������ӣ�true������Ǽ��٣�false */
				boolean isAdd = ruleVo.isAdd();

				NtbObj ntbObj = new NtbObj(methodFunc.equals(IFormulaFuncName.UFIND) ? NtbType.EXE : NtbType.PREVIOUS,
						isAdd ? NtbDic.INC : NtbDic.DEC);
				actionCodeMap.put(ruleVo.getActionCode(), ntbObj);
			}
		}

		return actionCodeMap;
	}

	/**
	 * ��ȡԴ�ֶ�code<br>
	 * ���� Ԥ�㷽��ע��ı����ֶ�Ϊ pk_currtype ΪĿ���ֶ�,��Ӧ�������������е�
	 * bzbm���������Ǵ�billtype��pk_currtype�����ɻ�ȡbzbm
	 * 
	 * @param billtype
	 *            ��������
	 * @param descField
	 *            Ŀ�ĵ����ֶ�(Ԥ�㷽��ע����ֶ���Ϣcode)
	 * @return
	 */
	protected String getSrcField(String billtype, String descField) {
		String result = ErmBillFieldContrastCache.getSrcField(
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, billtype, descField);
		if (result == null) {
			// �����ֶβ�����,�������������Ԥ����ձ���δ���ҵ���Ӧ�ֶ�
			if (descField.startsWith(BXConstans.BUDGET_DEFITEM_BODY_PREFIX)) {
				String prefix = null;
				if (ErmMatterAppConst.MatterApp_BILLTYPE.equals(getBillType())) {
					prefix = "mad";
				} else if (IErmCostShareConst.COSTSHARE_BILLTYPE.equals(getBillType())) {
					prefix = "csd";
				} else if (ExpAmoritizeConst.Expamoritize_BILLTYPE.equals(getBillType())) {
					prefix = "atd";
				} else if (ErmBillConst.AccruedBill_Billtype.equals(getBillType())) {
					prefix = "acd";
				} else {
					prefix = "fb";
				}
				result = prefix + "." + BXConstans.BODY_USERDEF_PREFIX
						+ descField.substring(BXConstans.BUDGET_DEFITEM_BODY_PREFIX.length());
			} else if (descField.startsWith(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX)) {
				String prefix = null;
				if (ErmMatterAppConst.MatterApp_BILLTYPE.equals(getBillType())) {
					prefix = "ma";
				} else if (IErmCostShareConst.COSTSHARE_BILLTYPE.equals(getBillType())) {
					prefix = "cs";
				} else if (ExpAmoritizeConst.Expamoritize_BILLTYPE.equals(getBillType())) {
					prefix = "at1";
				} else if (ErmBillConst.AccruedBill_Billtype.equals(getBillType())) {
					prefix = "ac";
				} else {
					prefix = "zb";
				}

				if (BXConstans.BX_DJLXBM.equals(billtype) || BXConstans.JK_DJLXBM.equals(billtype)) {
					result = prefix + "." + BXConstans.HEAD_USERDEF_PREFIX
							+ descField.substring(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX.length());
				} else {
					result = prefix + "." + BXConstans.BODY_USERDEF_PREFIX
							+ descField.substring(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX.length());
				}
			}
		}
		return result;
	}

	public NtbParamVO getNtbParam() {
		return ntbParam;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	enum NtbDic {
		INC("INC"), DEC("DEC");
		final String value;

		NtbDic(String value) {
			this.value = value;
		}
	}

	enum NtbType {
		PREVIOUS("previous"), EXE("EXE");
		final String value;

		NtbType(String value) {
			this.value = value;
		}
	}

	static class NtbObj {
		NtbObj(NtbType type, NtbDic dic) {
			this.type = type;
			this.dic = dic;
		}

		public NtbType type;
		public NtbDic dic;
	}
}