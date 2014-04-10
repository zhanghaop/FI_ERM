package nc.bs.er.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.er.util.TempTableDAO;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.sql.SqlCreatorTools;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.IFormulaFuncName;
import nc.itf.tb.control.OutEnum;
import nc.itf.tb.sysmaintain.BdContrastCache;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.accessor.IBDData;
import nc.vo.cmp.pub.EfficientPubMethod;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.control.QryObjVO;
import nc.vo.erm.control.TokenTools;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.IdBdcontrastVO;
import nc.vo.tb.obj.NtbParamVO;

/**
 * <p>
 * TODO 预算工具类
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @version V6.0
 * @since V6.0 创建时间：2011-3-10 下午02:44:51
 */
public class ErmNtbSqlTools extends SqlCreatorTools {

	private static final String ERM_CTRL_ST_KEY = "ERM0Z30001ERM0010001";
	/**
	 * 临时表是否已经创建
	 */
	private boolean isDone = true;
	private static String TEMP_TABLE_BHXJ = "TEMP_TABLE_BHXJ";
	private final String[] colNames = new String[] { "pk", "sub_pk" };
	private final String[] dataTypes = new String[] { "char(20)", "char(20)" };
	private String[] m_bieMing = null;
	private boolean isdetail = false; // 是否明细联查

	public boolean isIsdetail() {
		return isdetail;
	}

	public void setIsdetail(boolean isdetail) {
		this.isdetail = isdetail;
	}

	public ErmNtbSqlTools() {
		super();
	}

	private String getDjfbFromSql(boolean isjk, boolean iscjk) {
		CreatJoinSQLTool aAnlyDir = new CreatJoinSQLTool();
		Vector<QryObjVO> vQryObj = getSqlVO().getQryObj();
		String tmp = null;
		try {
			tmp = aAnlyDir.getJoinSQL(vQryObj, null, getSqlVO().getVoConditions(), null);
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}

		String from = "";
		if (iscjk) {
			from = " er_bxzb zb inner join er_bxcontrast cxb on cxb.pk_bxd=zb.pk_jkbx inner join er_jkzb jkb on jkb.pk_jkbx=cxb.pk_jkd  and jkb.qcbz='N' " +
					" inner join er_busitem fb on jkb.pk_jkbx=fb.pk_jkbx ";
		} else if (isjk) {
			from = " er_busitem fb inner join er_jkzb zb on zb.pk_jkbx=fb.pk_jkbx ";
		} else {
			from = " er_bxzb zb inner join er_busitem fb on zb.pk_jkbx = fb.pk_jkbx left join er_costshare cs on cs.src_id = zb.pk_jkbx ";
		}
		if (tmp != null) {
			from += tmp;
		}
		return from;
	}

	private void insertIntoTempTable() throws Exception {
		ArrayList<Object> array = getSqlVO().getSourceArr();
		if (array == null) {
			return;
		}
		Hashtable<String, String[]> hash = new Hashtable<String, String[]>();

		for (int i = 0; i < array.size(); i++) {
			NtbParamVO paramvo = (NtbParamVO) array.get(i);
			String[] pks = paramvo.getPkDim();
			boolean[] bhxj = paramvo.getIncludelower();

			if (pks == null) {
				return;
			}
			for (int j = 0; j < pks.length; j++) {
				boolean isbhxj = bhxj[j];
				if (!isbhxj) {
					continue;
				}
				TokenTools token = new TokenTools(pks[j], ",", false);
				String[] pks_row = token.getStringArray();
				for (int u = 0; u < pks_row.length; u++) {
					if (getSqlVO().isFromTB()) {
						String classTypeId = null;
						String pk = paramvo.getPkDim()[j];
						String pk_org = paramvo.getPk_Org();
						boolean isIncludeSelf = true;
						IdBdcontrastVO vo = BdContrastCache.getNewInstance().getVOByField(paramvo.getSys_id(), paramvo.getBusiAttrs()[j]);
						classTypeId = vo.getPk_bdinfo();
						IGeneralAccessor bdinfo = GeneralAccessorFactory.getAccessor(classTypeId);
						java.util.List<String> bddata = new ArrayList<String>();
						if (!(pk.indexOf("|") >= 0) && !pk.equals(OutEnum.NOSUCHBASEPKATSUBCORP)) {
							List<IBDData> childDocs = bdinfo.getChildDocs(pk_org, pk, false);
							if (childDocs != null) {
								for (IBDData data : childDocs) {
									bddata.add(data.getPk());
								}
							}
							if (isIncludeSelf) {
								/** 增加包含本级PK */
								bddata.add(pk);
							}
							for (int k = 0; k < bddata.size(); k++) {
								String[] data = new String[2];
								data[0] = pks_row[u];
								data[1] = bddata.get(k);
								hash.put(data[0] + data[1], data);
							}
						}
					} else {
						String[] data = new String[2];
						data[0] = pks_row[u];
						data[1] = pks_row[u];
						hash.put(data[0] + data[1], data);
					}
				}
			}
		}
		try {
			if (hash.size() > 0) {
				String[][] datas = new String[hash.size()][2];
				datas = hash.values().toArray(datas);// ？？ hash
				// 的value是一个一维数组，可是怎么去转换一个二维数组
				TempTableDAO dmo = new TempTableDAO();
				dmo.insertIntoTable(TEMP_TABLE_BHXJ, colNames, dataTypes, datas);
				isDone = true;
			}
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw e;
		}
	}

	private String createTempTable() {
		if (isDone) {
			return TEMP_TABLE_BHXJ;
		}
		boolean b = shouldCreateTable();
		if (!b) {
			return null;
		}
		try {
			TempTableDAO dmo = new TempTableDAO();
			String tempTableName = "TEMP_TABLE_BHXJ";
			TEMP_TABLE_BHXJ = dmo.createTable(tempTableName, colNames, dataTypes);
			insertIntoTempTable();
			isDone = true;

		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			return null;
		}
		return TEMP_TABLE_BHXJ;
	}

	private boolean shouldCreateTable() {
		ArrayList<Object> array = getSqlVO().getSourceArr();
		boolean[] bhxj = null;
		for (int i = 0; i < array.size(); i++) {
			Object obj = array.get(i);
			if (obj == null) {
				continue;
			}
			NtbParamVO ntbvo = (NtbParamVO) obj;
			bhxj = ntbvo.getIncludelower();
			if (bhxj != null) {
				for (int j = 0; j < bhxj.length; j++) {
					if (j < bhxj.length && bhxj[j]) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * inner join TEMP_TABLE_BHXJ t1 on t1.sub_pk = fb.deptid inner join
	 * TEMP_TABLE_BHXJ t2 on t2.sub_pk = fb.szxmid
	 * 
	 * */
	private String getIncludeSubDocSql() {

		String sql = "";
		ArrayList<Object> array = getSqlVO().getSourceArr();
		NtbParamVO firstVO = getFirstNtbVO();
		boolean[] bhxj = null;
		bhxj = firstVO.getIncludelower();
		String[] busiAttrs = firstVO.getBusiAttrs();
		m_bieMing = new String[busiAttrs.length];
		int idx = 0;
		String tmpTable = createTempTable();
		if (tmpTable == null) {
			return "";
		}
		for (int i = 0; i < array.size(); i++) {
			Object obj = array.get(i);
			if (obj == null) {
				continue;
			}
			NtbParamVO ntbParamVO = (NtbParamVO) obj;
			bhxj = ntbParamVO.getIncludelower();
			if (idx >= bhxj.length) {
				break;
			}
			for (int j = idx; j < bhxj.length; j++) {
				if (bhxj[j]) {
					String bieming = " t" + j;
					m_bieMing[j] = bieming + ".pk";
					sql += " inner join " + TEMP_TABLE_BHXJ + bieming + " on " + bieming + ".sub_pk = " + busiAttrs[j];
					idx = j + 1;
				}
			}
		}
		return sql;
	}

	private String getDataType(String billtype, boolean iscjk) throws Exception {
		NtbParamVO vo = getFirstNtbVO();
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTacticsBySysId(ERM_CTRL_ST_KEY);

		Map<String, NtbObj> map = new HashMap<String, NtbObj>();
		for (DataRuleVO ruleVo : ruleVos) {
			/** 单据类型/交易类型 */
			if (!billtype.equals(ruleVo.getBilltype_code()))
				continue;
			if (iscjk && (!ruleVo.getActionCode().equals(BXConstans.ERM_NTB_CONTRASTAPPROVE_KEY))
					&& (!ruleVo.getActionCode().equals(BXConstans.ERM_NTB_CONTRASTUNAPPROVE_KEY)))
				continue;

			/** 预占的：PREFIND,执行：UFIND */
			String methodFunc = ruleVo.getDataType();
			/** 如果是增加：true，如果是减少，false */
			boolean isAdd = ruleVo.isAdd();

			NtbObj ntbObj = new NtbObj(methodFunc.equals(IFormulaFuncName.UFIND) ? NtbType.EXE : NtbType.PREVIOUS, isAdd ? NtbDic.INC : NtbDic.DEC);
			map.put(ruleVo.getActionCode(), ntbObj);
		}

		if (IErmCostShareConst.COSTSHARE_BILLTYPE.equals(billtype)) {
			// 费用结转单 查询
			return getBillStatusSqlCs(vo.getMethodCode(), map);

		} else if (ErmMatterAppConst.MatterApp_BILLTYPE.equals(billtype)) {
			//事项审批单查询
			return getBillStatusSqlMa(vo.getMethodCode(), map);
		}else if (ExpAmoritizeConst.Expamoritize_BILLTYPE.equals(billtype)) {
			//摊销信息查询
			return getBillStatusSqlAt(vo.getMethodCode(), map);
		}
		
		return getDataTypeSqlBx(iscjk, vo.getMethodCode(), map);
	}
	
	/**
	 * 摊销状态sql
	 * @param methodCode
	 * @param map
	 * @return
	 */
	private String getBillStatusSqlAt(String methodCode, Map<String, NtbObj> map) {
		String sql = null;
		NtbObj obj = map.get(BXConstans.ERM_NTB_AMORTIZE_KEY);
		
		if (obj != null) {
			if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
				sql = " 1=1 ";
			}
			
			if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
				sql = " 1=1 ";
			}
		}
		
		return sql == null ? " and 1=0 " : " and " + sql;
	}

	/**
	 * 事项审批单datatypesql获得
	 * 
	 * @param ntbMethodCode
	 * @param map
	 * @return
	 */
	private String getBillStatusSqlMa(String ntbMethodCode, Map<String, NtbObj> map) {
		String sql = null;
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(ntbMethodCode)) {// 预占用
			if (map.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql = "ma." + MatterAppVO.BILLSTATUS + " in (" + ErmMatterAppConst.BILLSTATUS_SAVED + ","
							+ ErmMatterAppConst.BILLSTATUS_COMMITED + ") ";
				}
			}
			if (map.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql = "ma." + MatterAppVO.BILLSTATUS + " = " + ErmMatterAppConst.BILLSTATUS_APPROVED;
				}
			}

		} else { // 执行
			if (map.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql = "ma." + MatterAppVO.BILLSTATUS + " >= " + ErmMatterAppConst.BILLSTATUS_SAVED;
				}
			}
			if (map.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql = "ma." + MatterAppVO.BILLSTATUS + " = " + ErmMatterAppConst.BILLSTATUS_APPROVED;
				}
			}
		}
		return sql == null ? " and 1=0 " : " and " + sql;
	}

	/**
	 * 费用结转单datatypesql获得
	 * 
	 * @param ntbMethodCode
	 * @param map <ActionCode, NtbObj>
	 * @return
	 */
	private String getBillStatusSqlCs(String ntbMethodCode, Map<String, NtbObj> map) {
		StringBuffer sql = new StringBuffer();
		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(ntbMethodCode)) {// 预占用
			if (map.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" = ").append(BXStatusConst.DJZT_Saved);
				} 
			}
		} else { // 执行
			if (map.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" >= 1 ");
					return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
				} else {
				}
			}
			
			if (map.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("cs.").append(CostShareVO.BILLSTATUS).append(" = ").append(BXStatusConst.DJZT_Sign);
				}
			}
		}
		return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
	}

	/**
	 * 报销业务datatypesql获得
	 * 
	 * @param iscjk
	 * @param ntbMethodCode
	 * @param map
	 * @return
	 */
	private String getDataTypeSqlBx(boolean iscjk, String ntbMethodCode, Map<String, NtbObj> map) {
		StringBuffer sql = new StringBuffer();

		NtbObj obj = null;
		if (IFormulaFuncName.PREFIND.equals(ntbMethodCode)) {// 预占用
			if (iscjk)
				sql.append(" 1 =0 ");
			else if (map.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.PREVIOUS && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" in (1,2) ");
				} else {
					sql.append(" 1 =0 ");
				}
			}
		} else { // 执行
			if (iscjk) {
				if (map.get(BXConstans.ERM_NTB_CONTRASTAPPROVE_KEY) != null) {
					obj = map.get(BXConstans.ERM_NTB_CONTRASTAPPROVE_KEY);
					if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.DEC) {
						sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" >= 1 ");
					}
				}
				return sql.length() == 0 ? sql.toString() : " and " + sql.toString();

			}
			if (map.get(BXConstans.ERM_NTB_SAVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_SAVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" >= 1 ");
					return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
				}
			}
			if (map.get(BXConstans.ERM_NTB_APPROVE_KEY) != null) {
				obj = map.get(BXConstans.ERM_NTB_APPROVE_KEY);
				if (obj != null && obj.type == NtbType.EXE && obj.dic == NtbDic.INC) {
					sql.append("zb.").append(JKBXHeaderVO.DJZT).append(" = 3 ");
				} else {
				}
			}
		}
		return sql.length() == 0 ? sql.toString() : " and " + sql.toString();
	}

	private String getDataType(String billtype) throws Exception {
		return getDataType(billtype, false);
	}

	private String getSqlBx() throws Exception {
		StringBuffer sql = new StringBuffer();
		String groupcolumns = getGroupBySql();
		sql.append(" select " + groupcolumns + "," + getSumSql_Dai());
		sql.append(" from " + getDjfbFromSql(false, false) + getIncludeSubDocSql());
		sql.append(" where 1=1 " + getPartWhereSql(true));
		String sGroupinsql = getGroupBySql_In();
		if (sGroupinsql != null && sGroupinsql.trim().length() > 0) {
			sql.append(" and " + sGroupinsql);
		}
		sql.append(getDataType(BXConstans.BX_DJLXBM));
		
		//过滤掉摊销与分摊的报销单
		sql.append(" and zb.iscostshare = 'N' ");
		sql.append(" and (cs.src_id is null or (cs.src_type = 0 and cs.billstatus < 2))");
		String orderby = getOrderbySql(groupcolumns);
		if (orderby != null && orderby.trim().length() > 0) {
			if (isdetail) {
				sql.append(" order by " + orderby);
			} else {
				sql.append(" group by " + orderby + " order by " + orderby);
			}
		}
		
		// 替换报销产品与报销单的对照字段
		String bxSql = ErmBillFieldContrastCache.getConvertStr(sql.toString(), ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField,
				BXConstans.BX_DJLXBM);

		return bxSql;
	}

	/**
	 * @return
	 * @throws Exception
	 * 
	 *             说明： 此方法提供预算联查时查询冲销明细的记录
	 * 
	 *             select：单据号,单据主键,生效标志,单据类型编码（报销单），其他字段（借款单），金额（冲销明细） group：借款单
	 *             where：借款单
	 * 
	 */
	private String getSqlCJk() throws Exception {
		String sql = "";
		String bhxjSql = ErmBillFieldContrastCache.getConvertStr(getIncludeSubDocSql(),
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, BXConstans.JK_DJLXBM);
		String sqlSelect = ErmBillFieldContrastCache.getConvertStr(getGroupBySql(),
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, BXConstans.JK_DJLXBM);

		sqlSelect = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sqlSelect, "zb.", "jkb.");
		sqlSelect = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sqlSelect, "jkb.djbh", "zb.djbh");
		sqlSelect = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sqlSelect, "jkb.pk_jkbx", "zb.pk_jkbx");
		sqlSelect = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sqlSelect, "jkb.djlxbm", "zb.djlxbm");

		bhxjSql = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(bhxjSql, "zb.", "jkb.");

		sql += " select " + sqlSelect + "," + getSumSql_Cjk();
		sql += " from " + getDjfbFromSql(true, true) + bhxjSql;
		sql += " where ";

		String whereSql = "1=1" + getPartWhereSql(true);
		String sGroupinsql = getGroupBySql_In();
		if (sGroupinsql != null && sGroupinsql.trim().length() > 0) {
			whereSql += " and " + sGroupinsql;
		}
		
		whereSql = ErmBillFieldContrastCache.getConvertStr(whereSql,
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, BXConstans.JK_DJLXBM);
		whereSql = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(whereSql, "zb.", "jkb.");
		whereSql += " and zb.dr=0 and cxb.dr=0 ";

		whereSql = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(whereSql, "jkb.sxbz", "zb.sxbz");

		sql += whereSql;

		String sGroup = getOrderbySql(sqlSelect);

		sGroup = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sGroup, "zb.", "jkb.");
		sGroup = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sGroup, "jkb.djbh", "zb.djbh");
		sGroup = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sGroup, "jkb.pk_jkbx", "zb.pk_jkbx");
		sGroup = nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sGroup, "jkb.djlxbm", "zb.djlxbm");
		sql += getDataType(BXConstans.JK_DJLXBM, true);

		if (sGroup != null && sGroup.trim().length() > 0) {
			if (isdetail) {
				sql += " order by " + sGroup;
			} else {
				sql += " group by " + sGroup + " order by " + sGroup;
			}
		}
		return sql;
	}

	private String getSqlJk() throws Exception {
		String sql = "";

		String groupColumns = getGroupBySql();
		sql += " select " + groupColumns + "," + getSumSql_Dai();
		sql += " from " + getDjfbFromSql(true, false) + getIncludeSubDocSql();
		sql += " where zb.qcbz='N' and " + "1=1" + getPartWhereSql(true);
		String sGroupinsql = getGroupBySql_In();
		if (sGroupinsql != null && sGroupinsql.trim().length() > 0) {
			sql += " and " + sGroupinsql;
		}
		sql += getDataType(BXConstans.JK_DJLXBM);
		String sGroup = getOrderbySql(groupColumns);
		if (sGroup != null && sGroup.trim().length() > 0) {
			if (isdetail) {
				sql += " order by " + sGroup;
			} else {
				sql += " group by " + sGroup + " order by " + sGroup;
			}
		}
		
		sql = ErmBillFieldContrastCache.getConvertStr(sql, ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField,
				BXConstans.JK_DJLXBM);
		
		return sql;
	}

	protected NtbParamVO getFirstNtbVO() {
		if (getSqlVO().getSourceArr() == null || getSqlVO().getSourceArr().size() <= 0) {
			return null;
		}
		return (NtbParamVO) getSqlVO().getSourceArr().get(0);
	}

	private boolean isBhxj(int idx) {
		if (m_bieMing != null && idx >= 0 && idx < m_bieMing.length) {
			return m_bieMing[idx] != null;
		}
		return false;
	}

	protected String getcolName(String[] names, int idx) {
		String colName = "";
		if (getSqlVO().isDetail()) {
			return names[idx];
		}
		boolean isbhxj = isBhxj(idx);
		if (isbhxj) {
			colName = m_bieMing[idx];
		} else {
			colName = names[idx];
		}
		return colName;
	}

	public String getGroupBySql() throws Exception {
		String sql = " ";
		NtbParamVO ntbvo = getFirstNtbVO();
		if (ntbvo == null) {
			return "";
		}
		String[] names = ntbvo.getBusiAttrs();
		getSqlVO().setGroupCount(names.length + 1);// 设置group by 的列数
		for (int i = 0; i < names.length; i++) {
			if (names[i] == null || names[i].trim().equals("")) {
				throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504", "UPP20060504-000077")/*
																														 * @res
																														 * "错误！GroupBy中的字段不可为空！"
																														 */);
			}
			String colName = getcolName(names, i);

			if (ntbvo.getIncludelower()[i]) {
				colName = "t" + i + ".pk";
			}

			if (i == 0) {
				sql += colName;
			} else {
				sql += " ," + colName;
			}

		}
		String s_bhxj = getBhxjStr(ntbvo);
		sql += s_bhxj;
		return sql;
	}

	public static final String BHXJ_SIGN = "BHXJ";

	protected String getBhxjStr(NtbParamVO ntbvo) {
		if (ntbvo == null) {
			return "";
		}
		boolean[] bhxj = ntbvo.getIncludelower();
		if (bhxj == null || bhxj.length == 0) {
			return "";
		}
		String s_bhxj = "";
		for (int i = 0; i < bhxj.length; i++) {
			if (bhxj[i]) {
				s_bhxj += "Y";
			} else {
				s_bhxj += "N";
			}
		}
		return ",'N" + s_bhxj + "'" + " " + BHXJ_SIGN;
	}

	private String getOrderbySql(String str) {
		String ret = "";
		if (str == null || str.length() == 0) {
			return "";
		}
		str = str.trim();
		int idx = str.length();
		if (str.charAt(str.length() - 1) == ',') {
			idx = str.length() - 1;
		}
		if (idx > -1) {
			ret = str.substring(0, idx);
		}
		ret = EfficientPubMethod.getInstance().getOrderByString(str);
		return ret;

	}

	public String getGroupByFields() {
		String sqlSelect = null;
		try {
			sqlSelect = getGroupBySql();
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}
		if (sqlSelect == null) {
			Log.getInstance(this.getClass()).debug("####ERROE:NtbSqlTools.getSelectedFields(),sqlSelect == null");
			return null;
		}
		String groupBy = getOrderbySql(sqlSelect);

		return groupBy;
	}

	private String getGroupBySql_In() throws Exception {
		ArrayList<Object> array = getSqlVO().getSourceArr();
		String[] names = null;
		String sql = "";
		if (array != null && array.size() > 0) {
			names = getBusiAttrs();
			String tempstr = null;
			for (int i = 0; i < names.length; i++) {
				if (m_bieMing == null || m_bieMing[i] == null) {
					tempstr = getExcludeSubSql(array, i);
					if (tempstr == null || tempstr.length() <= 0) {
						continue;
					}
					if (sql.trim().length() > 0) {
						sql += " and ";
					}
					sql += " " + tempstr;
				} else {
					sql += bhxj(array, i);
				}
			}
		}
		return sql;
	}

	/**
	 * @return
	 */
	private String[] getBusiAttrs() {
		String[] names;
		NtbParamVO ntbvo = getFirstNtbVO();
		names = ntbvo.getBusiAttrs();
		return names;
	}

	private String bhxj(ArrayList<Object> array, int i) {
		String sql = "";
		return sql;
	}

	/**
	 * 不包含下级的SQL
	 * 
	 * @param array
	 * @param names
	 * @param sql
	 * @param i
	 * @return
	 * @throws Exception
	 */
	private String getExcludeSubSql(java.util.ArrayList<Object> array, int i) throws Exception {
		String[] names = getBusiAttrs();

		String partSql = " ";
		partSql += names[i];
		String inSql = "  ";
		Hashtable<String, String> hash = new Hashtable<String, String>();
		for (int j = 0; j < array.size(); j++) {
			NtbParamVO ntbvotemp = (NtbParamVO) array.get(j);
			String pks = ntbvotemp.getPkDim()[i];
			boolean isIncludelower = ntbvotemp.getIncludelower()[i];

			// **
			String classTypeId = null;
			String pk = ntbvotemp.getPkDim()[i];
			String pk_org = ntbvotemp.getPk_Org();
			IdBdcontrastVO vo = BdContrastCache.getNewInstance().getVOByField(ntbvotemp.getSys_id(), ntbvotemp.getBusiAttrs()[i]);
			classTypeId = vo.getPk_bdinfo();
			IGeneralAccessor bdinfo = GeneralAccessorFactory.getAccessor(classTypeId);
			java.util.List<String> bddata = new ArrayList<String>();
			if (!(pk.indexOf("|") >= 0) && !pk.equals(OutEnum.NOSUCHBASEPKATSUBCORP)) {
				List<IBDData> childDocs = bdinfo.getChildDocs(pk_org,pk, false);
				if (childDocs != null && isIncludelower) {
					for (IBDData data : childDocs) {
						bddata.add(data.getPk());
					}
				}
				String[] busiAttrs = ntbvotemp.getBusiAttrs();
				m_bieMing = new String[busiAttrs.length];
				for (int k = 0; k < bddata.size(); k++) {
					hash.put(bddata.toArray(new String[0])[k], bddata.toArray(new String[0])[k]);

				}
				if (pks != null && pks.trim().length() > 0) {
					hash.put(ntbvotemp.getPkDim()[i], ntbvotemp.getPkDim()[i]);
				}
			}
		}
		String[] arry = hash.values().toArray(new String[0]);
		for (int sql = 0; sql < arry.length; sql++) {
			inSql += " '" + StringUtil.replaceAllString(arry[sql], ",", "','") + "',";
		}
		if (inSql.trim().length() == 0) {
			return "";
		}
		inSql = inSql.substring(0, inSql.length() - 1);

		String sql = "";
		inSql = " in (" + inSql + ") ";
		sql = partSql + inSql;
		return sql;

	}

	/**
	 * getPartWhereSql 方法注解。
	 */
	// @Override
	private String getPartWhereSql(boolean bArapbill) throws Exception {
		String sql = " and fb.dr=0 and zb.dr=0 and zb.qcbz='N' ";

		sql += getWhereFromVO();
		// 加自定义查询条件
		String zdyWhere = getSqlVO().getNormalCond();
		if (zdyWhere != null && zdyWhere.trim().length() > 0) {
			if (zdyWhere.trim().substring(0, 3).equalsIgnoreCase("AND")) {
				sql += zdyWhere;
			} else {
				sql += " and " + zdyWhere;
			}
		}
		return sql;
	}

	private String getPartWhereSql() {

		String sql = getWhereFromVO();

		// 加自定义查询条件
		String zdyWhere = getSqlVO().getNormalCond();
		if (zdyWhere != null && zdyWhere.trim().length() > 0) {
			if (zdyWhere.trim().substring(0, 3).equalsIgnoreCase("AND")) {
				sql += zdyWhere;
			} else {
				sql += " and " + zdyWhere;
			}
		}
		return sql;
	}

	/**
	 * 返回一个String数组 存放Dai方，Jie方和事项审批单的sql 总的查询结果应该是三者之和
	 */
	@Override
	public String[] getSql() throws Exception {
		isDone = false;
		List<String> sqlList = new ArrayList<String>();
		Set<String> parentBillTypes = getAllParentBillTyps();
		if(parentBillTypes.contains(BXConstans.BX_DJLXBM) || parentBillTypes.contains(BXConstans.JK_DJLXBM)){
			sqlList.add(getSqlBx());
			sqlList.add(getSqlJk());
			sqlList.add(getSqlCJk());
		}

		if (parentBillTypes.contains(IErmCostShareConst.COSTSHARE_BILLTYPE)) {
			sqlList.add(getSqlCs());
		}
		if (parentBillTypes.contains(ErmMatterAppConst.MatterApp_BILLTYPE)) {
			sqlList.add(getSqlMa());
		}
		
		if(parentBillTypes.contains(ExpAmoritizeConst.Expamoritize_BILLTYPE)){
			sqlList.add(getSqlAt());
		}

		String[] sqls = sqlList.toArray(new String[sqlList.size()]);

		Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0033")/*
																																		 * @res
																																		 * "-------打印查询语句开始-----"
																																		 */);
		for (int i = 0; i < sqls.length; i++) {
			Log.getInstance(this.getClass()).debug(sqls[i]);
		}
		Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0034")/*
																																		 * @res
																																		 * "-------打印查询语句完成-----"
																																		 */);
		return sqls;
	}

	/**
	 * 获得查询的全部单据类型
	 * 
	 * @return
	 */
	private Set<String> getAllParentBillTyps() {
		Set<String> set = new HashSet<String>();
		
		String billType = getFirstNtbVO().getBill_type();
		TokenTools token = null;
		if(billType.indexOf("#")!=-1){
			token = new TokenTools(billType, "#" , false);
		}else{
			token = new TokenTools(billType, "," , false);
		}
		
		String[] billTypes = token.getStringArray();
		
		if (billTypes != null && billTypes.length > 0) {
			String parentBilltype = null;
			for (int i = 0; i < billTypes.length; i++) {
				BilltypeVO vo = PfDataCache.getBillType(billTypes[i]);
				if (vo.getIstransaction() != null || vo.getIstransaction().booleanValue()) {
					// 获得交易类型的
					parentBilltype = vo.getParentbilltype();
				} else {
					parentBilltype = vo.getPk_billtypecode();
				}
				
				if(parentBilltype == null){//如果无父类单据，就用交易类型
					parentBilltype = billTypes[i];
				}
				set.add(parentBilltype);
			}
		}
		return set;
	}

	private String getSumSql_Dai() {
		String sql = "";
		if (isdetail) {
			String sql2 = "zb.djlxbm djlxbm, zb.bzbm bzbm, zb.djrq djrq, zb.shrq  shrq, zb.jsrq jsrq, zb.zy zy, zb.djbh djbh, zb.pk_group pk_group,";
			if (getDirection() == -1) {
				sql = sql2 + " fb.globalbbje globalbbje,fb.groupbbje groupbbje, fb.bbje bbje,fb.ybje ybje ";
			} else {
				sql = sql2 + " -fb.globalbbje globalbbje,-fb.groupbbje groupbbje, -fb.bbje  bbje,-fb.ybje  ybje ";
			}
		} else {
			if (getDirection() == -1) {
				sql = " sum(fb.globalbbje) globalbbje,sum(fb.groupbbje) groupbbje, sum(fb.bbje) bbje,sum(fb.ybje) ybje ";
			} else {
				sql = " sum(-fb.globalbbje) globalbbje,sum(-fb.groupbbje) groupbbje, sum(-fb.bbje) bbje,sum(-fb.ybje) ybje ";
			}
		}
		return sql;

	}

	/**
	 * sk,sj,hj
	 */
	private String getSumSql_Cjk() {
		String sql = "";
		if (isdetail) {
			String contrastMemo = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0161");// "冲借款";
			String sql2 = "zb.djlxbm djlxbm, zb.bzbm bzbm, zb.djrq djrq, zb.shrq  shrq, zb.jsrq jsrq,'" + contrastMemo
					+ "' zy, zb.djbh djbh, zb.pk_group pk_group,";
			if (getDirection() == -1) {
				sql = sql2 + " -cxb.globalbbje globalbbje,-cxb.groupbbje groupbbje,-cxb.bbje bbje,-cxb.ybje ybje ";
			} else {
				sql = sql2 + " cxb.globalbbje globalbbje,cxb.groupbbje groupbbje,cxb.bbje bbje,cxb.ybje ybje ";
			}
		} else {
			if (getDirection() == -1) {
				sql = " sum(-cxb.globalbbje) globalbbje,sum(-cxb.groupbbje) groupbbje,sum(-cxb.bbje) bbje,sum(-cxb.ybje) ybje ";
			} else {
				sql = " sum(cxb.globalbbje) globalbbje,sum(cxb.groupbbje) groupbbje,sum(cxb.bbje) bbje,sum(cxb.ybje) ybje ";
			}
		}
		return sql;
	}

	/**
	 * getPartWhereSql 方法注解。
	 */
	private String getWhereFromVO() {
		ArrayList<Object> array = getSqlVO().getSourceArr();
		NtbParamVO ntbvo = (NtbParamVO) array.get(0);

		String sql = " and ";
		sql += getDwbmPartWhere();
		if (getSqlVO().isDetail()) {
			if (ntbvo.getPk_currency() != null && !ntbvo.getPk_currency().trim().equals("")) {
				sql += " and zb.bzbm ='" + ntbvo.getPk_currency() + "' ";
			}
		}
		// 默认单据日期
		String strDateType = "djrq";
		// 日期类型
		final String dateType = ntbvo.getDateType();
		if (dateType != null) {
			if ("sxrq".equals(dateType)) {
				strDateType = "jsrq";
			} else {
				strDateType = dateType.trim();
			}
		}

		if (ntbvo.getBegDate() != null && ntbvo.getBegDate().toString().trim().length() > 0) {
			sql += " and " + strDateType + " >='" + ntbvo.getBegDate() + "' ";
		}
		if (ntbvo.getEndDate() != null && ntbvo.getEndDate().toString().trim().length() > 0) {
			sql += " and " + strDateType + " <='" + ntbvo.getEndDate() + "' ";
		}
		String strBillType = getDjlxbmPartWhere();
		if (strBillType.trim().length() > 0) {
			sql += " and   " + strBillType;
		}
		return sql;
	}

	/**
	 * @param array
	 * @return
	 */
	protected String getDjlxbmPartWhere() {
		ArrayList<Object> array = getSqlVO().getSourceArr();
		String strBillType = "";
		Hashtable<String, String> hash = new Hashtable<String, String>();
		for (int j = 0; j < array.size(); j++) {
			NtbParamVO ntbvo2 = (NtbParamVO) array.get(j);
			if (ntbvo2.getBill_type() != null) {
				String billType = ntbvo2.getBill_type();
				TokenTools token = null;
				if(billType.indexOf("#")!=-1){
					token = new TokenTools(billType, "#" , false);
				}else{
					token = new TokenTools(billType, "," , false);
				}
				
				String[] billTypes = token.getStringArray();
				if (billTypes != null && billTypes.length > 0) {
					for (int i = 0; i < billTypes.length; i++) {
						hash.put(billTypes[i], billTypes[i]);
					}
				}
			}
		}
		String[] arry = hash.values().toArray(new String[0]);
		for (int i = 0; i < arry.length; i++) {
			if (i != 0) {
				strBillType += ",";
			}
			strBillType += "'" + arry[i] + "' ";
		}
		if (strBillType.trim().length() > 0) {
			strBillType = " pk_billtype  in (" + strBillType + ") ";
		} else {
			strBillType = "  1=1  ";
		}
		return strBillType;
	}

	protected String getDwbmPartWhere() {
		ArrayList<Object> array = getSqlVO().getSourceArr();
		Hashtable<String, String> hash = new Hashtable<String, String>();
		String dwbm = "";
		for (int j = 0; j < array.size(); j++) {
			NtbParamVO ntbvo2 = (NtbParamVO) array.get(j);
			if (ntbvo2 != null && ntbvo2.getPk_Org() != null) {
				hash.put(ntbvo2.getPk_Org(), ntbvo2.getPk_Org());
			}
		}
		String[] arry = hash.values().toArray(new String[0]);
		for (int i = 0; i < arry.length; i++) {
			if (i != 0) {
				dwbm += ",";
			}
			dwbm += "'" + arry[i] + "' ";
		}

		dwbm = "  1=1  ";

		return dwbm;
	}

	/**
	 * "收"------ 1; "付"------ -1;
	 */
	protected int getDirection() {
		NtbParamVO ntbvo = getFirstNtbVO();
		String direction = ntbvo.getDirection();
		if (direction.trim().equals(BXConstans.RECEIVABLE)) {
			return 1;
		} else if (direction.trim().equals(BXConstans.PAYABLE)) {
			return -1;
		}
		return 0;
	}

	public String[] getSumMoneyFields() {
		int direction = getDirection();
		if (direction == 1) {
			return new String[] { "sf_ybje", "sf_fbje", "sf_bbje", };
		} else {
			return new String[] { "ff_ybje", "ff_fbje", "ff_bbje", };
		}
	}

	/**
	 * 费用结转单查询sql
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getSqlCs() throws Exception {
		String sql = "";
		String groupcolumns = getGroupBySql();
		sql += " select " + groupcolumns + "," + getSumSql_CS();
		sql += " from " + getCsFromSql() + getIncludeSubDocSql();
		sql += " where 1=1 " + getPartWhereSql();
		String sGroupinsql = getGroupBySql_In();
		if (sGroupinsql != null && sGroupinsql.trim().length() > 0) {
			sql += " and " + sGroupinsql;
		}
		sql += getDataType(IErmCostShareConst.COSTSHARE_BILLTYPE);
		String orderby = getOrderbySql(groupcolumns);
		if (orderby != null && orderby.trim().length() > 0) {
			if (isdetail) {
				sql += " order by " + orderby;
			} else {
				sql += " group by " + orderby + " order by " + orderby;
			}
		}
		// 替换报销产品与费用结转单的对照字段
		sql = ErmBillFieldContrastCache.getConvertStr(sql, ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField,
				IErmCostShareConst.COSTSHARE_BILLTYPE);
		// 返回
		return sql;
	}
	
	/**
	 * 摊销过程查询sql
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getSqlAt() throws Exception {
		String sql = "";
		String groupcolumns = getGroupBySql();
		sql += " select " + groupcolumns + "," + getSumSql_AT();
		sql += " from " + getATFromSql() + getIncludeSubDocSql();
		sql += " where 1=1 " + getPartWhereSql();
		String sGroupinsql = getGroupBySql_In();
		if (sGroupinsql != null && sGroupinsql.trim().length() > 0) {
			sql += " and " + sGroupinsql;
		}
		
		String orderby = getOrderbySql(groupcolumns);
		if (orderby != null && orderby.trim().length() > 0) {
			if (isdetail) {
				sql += " order by " + orderby;
			} else {
				sql += " group by " + orderby + " order by " + orderby;
			}
		}
		
		// 替换报销产品与费用结转单的对照字段
		sql = ErmBillFieldContrastCache.getConvertStr(sql, ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField,
				ExpAmoritizeConst.Expamoritize_BILLTYPE);
		// 返回
		return sql;
	}

	private String getCsFromSql() {
		CreatJoinSQLTool aAnlyDir = new CreatJoinSQLTool();
		Vector<QryObjVO> vQryObj = getSqlVO().getQryObj();
		String tmp = null;
		try {
			tmp = aAnlyDir.getJoinSQL(vQryObj, null, getSqlVO().getVoConditions(), null);
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}

		String from = " er_cshare_detail csd inner join er_costshare cs on cs.pk_costshare=csd.pk_costshare ";

		if (tmp != null) {
			from += tmp;
		}
		return from;
	}
	
	private String getATFromSql() {
		CreatJoinSQLTool aAnlyDir = new CreatJoinSQLTool();
		Vector<QryObjVO> vQryObj = getSqlVO().getQryObj();
		String tmp = null;
		try {
			tmp = aAnlyDir.getJoinSQL(vQryObj, null, getSqlVO().getVoConditions(), null);
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}

		String from = " er_expamtinfo at right join er_expamtproc proc on at.pk_expamtinfo = proc.pk_expamtinfo " +
				"inner join er_expamtdetail atd on at.pk_expamtinfo = atd.pk_expamtinfo ";

		if (tmp != null) {
			from += tmp;
		}
		return from;
	}

	private String getSumSql_CS() {
		String sql = "";
		if (isdetail) {
			String sql2 = "cs.pk_tradetype djlxbm, cs.bzbm bzbm, billdate djrq, approvedate  shrq, "
					+ "approvedate jsrq, cs.zy zy, cs.billno djbh, cs.pk_group pk_group, assume_org org, ";
			if (getDirection() == -1) {
				sql = sql2 + " csd.globalbbje globalbbje,csd.groupbbje groupbbje, csd.bbje bbje,csd.assume_amount ybje ";
			} else {
				sql = sql2 + " -csd.globalbbje globalbbje,-csd.groupbbje groupbbje,-csd.bbje bbje,-csd.assume_amount ybje ";
			}
		} else {
			if (getDirection() == -1) {
				sql = " sum(csd.globalbbje) globalbbje,sum(csd.groupbbje) groupbbje, sum(csd.bbje) bbje,sum(csd.assume_amount) ybje ";
			} else {
				sql = " sum(-csd.globalbbje) globalbbje,sum(-csd.groupbbje) groupbbje, sum(-csd.bbje) bbje,sum(-csd.assume_amount) ybje ";
			}
		}
		return sql;

	}
	
	private String getSumSql_AT() {
		StringBuffer sql = new StringBuffer();
		if (isdetail) {
			sql.append(" pk_billtype djlxbm, at.bzbm, proc.amortize_date djrq, proc.amortize_date shrq, ")
			   .append(" proc.amortize_date jsrq, '' zy, at.bx_billno djbh, at.pk_group pk_group, assume_org org,");
			if (getDirection() == -1) {
				sql.append(" proc.globalbbje globalbbje, proc.groupbbje groupbbje, proc.bbje bbje, proc.curr_amount ybje ");
			} else {
				sql.append(" -proc.globalbbje globalbbje, -proc.groupbbje groupbbje, -proc.bbje bbje, -proc.curr_amount ybje ");
			}
		} else {
			if (getDirection() == -1) {
				sql.append(" sum(proc.globalbbje) globalbbje,sum(proc.groupbbje) groupbbje, sum(proc.bbje) bbje,sum(proc.curr_amount) ybje ");
			} else {
				sql.append(" sum(-proc.globalbbje) globalbbje,sum(-proc.groupbbje) groupbbje, sum(-proc.bbje) bbje,sum(-proc.curr_amount) ybje ");
			}
		}
		return sql.toString();

	}

	/**
	 * 事项审批单查询sql
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getSqlMa() throws Exception {
		String sql = "";
		String groupcolumns = getGroupBySql();
		sql += " select " + groupcolumns + "," + getSumSql_MA();
		sql += " from " + getMaFromSql() + getIncludeSubDocSql();
		sql += " where ma.dr = 0 " + getPartWhereSql();
		String sGroupinsql = getGroupBySql_In();
		if (sGroupinsql != null && sGroupinsql.trim().length() > 0) {
			sql += " and " + sGroupinsql;
		}
		sql += getDataType(ErmMatterAppConst.MatterApp_BILLTYPE);
		String orderby = getOrderbySql(groupcolumns);
		if (orderby != null && orderby.trim().length() > 0) {
			if (isdetail) {
				sql += " order by " + orderby;
			} else {
				sql += " group by " + orderby + " order by " + orderby;
			}
		}
		// 替换报销产品与费用结转单的对照字段
		sql = ErmBillFieldContrastCache.getConvertStr(sql, ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField,
				ErmMatterAppConst.MatterApp_BILLTYPE);
		// 返回
		return sql;
	}

	private String getMaFromSql() {
		CreatJoinSQLTool aAnlyDir = new CreatJoinSQLTool();
		Vector<QryObjVO> vQryObj = getSqlVO().getQryObj();
		String tmp = null;
		try {
			tmp = aAnlyDir.getJoinSQL(vQryObj, null, getSqlVO().getVoConditions(), null);
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}

		String from = " er_mtapp_detail mad inner join er_mtapp_bill ma on ma.pk_mtapp_bill=mad.pk_mtapp_bill ";

		if (tmp != null) {
			from += tmp;
		}
		return from;
	}
	
	private String getSumSql_MA() {
		String sql = "";
		if (isdetail) {
			String sql2 = "ma.pk_tradetype djlxbm, pk_currtype bzbm, billdate djrq, ma.approvetime shrq, "
					+ "ma.approvetime jsrq, ma.reason zy, ma.billno djbh, ma.pk_group pk_group, apply_org org,";
			if (getDirection() == -1) {
				sql = sql2 + " mad.global_rest_amount globalbbje,mad.group_rest_amount groupbbje, mad.org_rest_amount bbje,mad.rest_amount ybje ";
			} else {
				sql = sql2 + " -mad.global_rest_amount globalbbje,-mad.group_rest_amount groupbbje, -mad.org_rest_amount bbje,-mad.rest_amount ybje ";
			}
		} else {
			if (getDirection() == -1) {
				sql = " sum(mad.global_rest_amount) globalbbje,sum(mad.group_rest_amount) groupbbje, sum(mad.org_rest_amount) bbje,sum(mad.rest_amount) ybje ";
			} else {
				sql = " sum(-mad.global_rest_amount) globalbbje,sum(-mad.group_rest_amount) groupbbje, sum(-mad.org_rest_amount) bbje,sum(-mad.rest_amount) ybje ";
			}
		}
		return sql;

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

	class NtbObj {
		NtbObj(NtbType type, NtbDic dic) {
			this.type = type;
			this.dic = dic;
		}

		NtbType type;
		NtbDic dic;
	}
}