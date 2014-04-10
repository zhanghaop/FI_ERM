package nc.impl.arap.bx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BXZbBO;
import nc.bs.arap.bx.ContrastBO;
import nc.bs.arap.bx.IBXBusItemBO;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.BXBsUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.org.IOrgVersionQueryService;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.uap.rbac.constant.IRoleConst;
import nc.vo.vorg.OrgVersionVO;

/**
 * nc.impl.arap.bx.ArapBXBillPrivateImp
 * 
 * @author twei
 * 
 *         借款报销类单据私有业务处理接口实现
 */
public class ArapBXBillPrivateImp implements IBXBillPrivate {

	public BXZbBO bxZbBO = new BXZbBO();

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count, DjCondVO condVO)
			throws BusinessException {
		return bxZbBO.queryHeaders(start, count, condVO);
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl) throws BusinessException {
		return bxZbBO.queryHeadersByWhereSql(sql, djdl);
	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO header) throws BusinessException {
		return bxZbBO.queryContrasts(header);
	}

	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header) throws BusinessException {
		return bxZbBO.queryJsContrasts(header);
	}

	public MessageVO[] audit(JKBXVO[] bxvos) throws BusinessException {
		return bxZbBO.audit(bxvos);
	}

	public BXBusItemVO[] queryItems(JKBXHeaderVO header) throws BusinessException {
		return getBxBusitemBO(header.getDjlxbm(), header.getDjdl()).queryByHeaders(
				new JKBXHeaderVO[] { header });
	}

	private IBXBusItemBO getBxBusitemBO(String djlxbm, String djdl) throws BusinessException {
		return bxZbBO.getBxBusitemBO(djlxbm, djdl);
	}

	public MessageVO[] unAudit(JKBXVO[] bxvos) throws BusinessException {
		return bxZbBO.unAudit(bxvos);
	}

	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys, String djdl) throws BusinessException {
		return bxZbBO.queryHeadersByPrimaryKeys(keys, djdl);
	}

	public List<JKBXVO> queryVOs(Integer start, Integer count, DjCondVO condVO) throws BusinessException {

		List<JKBXHeaderVO> headers = queryHeaders(start, count, condVO);

		List<JKBXVO> vos = retriveItems(headers);

		return vos;
	}

	public int querySize(DjCondVO condVO) throws BusinessException {
		return bxZbBO.querySize(condVO);
	}

	public List<JKBXVO> queryVOsByPrimaryKeys(String[] keys, String djdl) throws BusinessException {
		List<JKBXHeaderVO> name = queryHeadersByPrimaryKeys(keys, djdl);
		List<JKBXVO> vos = retriveItems(name);
		return vos;
	}

	public List<JKBXVO> retriveItems(List<JKBXHeaderVO> headers) throws BusinessException {

		if (headers == null || headers.size() == 0)
			return null;

		List<JKBXVO> vos = new ArrayList<JKBXVO>();

		JKBXHeaderVO[] headArray = headers.toArray(new JKBXHeaderVO[] {});
		BXBusItemVO[] bxItems = queryItems(headArray);

		Collection<BxcontrastVO> contrasts = bxZbBO.queryContrasts(headArray, headers.get(0).getDjdl());

		Map<String, List<CircularlyAccessibleValueObject>> bxItemMap = VOUtils.changeArrayToMapList(bxItems,
				new String[] { JKBXHeaderVO.PK_JKBX });
		Map<String, List<SuperVO>> contrastMap = VOUtils.changeCollectionToMapList(contrasts, headers.get(0)
				.getDjdl().equals(BXConstans.BX_DJDL) ? BxcontrastVO.PK_BXD : BxcontrastVO.PK_JKD);

		// 根据币种处理VO精度
		CurrencyControlBO currencyControlBO = new CurrencyControlBO();

		for (Iterator<JKBXHeaderVO> iter = headers.iterator(); iter.hasNext();) {
			JKBXHeaderVO header = iter.next();
			String pk = header.getPrimaryKey();
			List<CircularlyAccessibleValueObject> list2 = bxItemMap.get(pk);
			List<SuperVO> list3 = contrastMap.get(pk);
			JKBXVO bxvo = VOFactory.createVO(header, list2 == null ? new BXBusItemVO[] {} : list2
					.toArray(new BXBusItemVO[] {}));
			bxvo.setContrastVO(list3 == null ? null : list3.toArray(new BxcontrastVO[] {}));
			bxvo.setChildrenFetched(true);

			currencyControlBO.dealBXVOdigit(bxvo);// 精度处理

			vos.add(bxvo);
		}
		return vos;
	}

	public List<JKBXVO> queryVOsByWhereSql(String sql, String djdl) throws BusinessException {
		List<JKBXHeaderVO> name = queryHeadersByWhereSql(sql, djdl);
		List<JKBXVO> vos = retriveItems(name);
		return vos;
	}

	public BXBusItemVO[] queryItems(JKBXHeaderVO[] header) throws BusinessException {
		if (header == null || header.length == 0)
			return null;

		JKBXHeaderVO headerVO = header[0];
		return getBxBusitemBO(headerVO.getDjlxbm(), headerVO.getDjdl()).queryByHeaders(header);
	}

	public List<BxcontrastVO> batchContrast(JKBXVO[] selBxvos, List<String> mode_data, BatchContratParam param)
			throws BusinessException {
		return new ContrastBO().batchContrast(selBxvos, mode_data, param);
	}

	public void saveBatchContrast(List<BxcontrastVO> selectedData, boolean delete) throws BusinessException {
		new ContrastBO().saveBatchContrast(selectedData, delete);
	}

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos, boolean isBatch)
			throws BusinessException {
		return bxZbBO.queryJkContrast(selBxvos, isBatch);
	}

	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs, Map<String, String[]> defMap)
			throws BusinessException {
		bxZbBO.saveSqdlrs(roles, sqdlrVOs);
		bxZbBO.savedefSqdlrs(roles, defMap);

	}

	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles) throws BusinessException {
		return bxZbBO.querySqdlr(pk_roles);
	}

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs) throws BusinessException {
		bxZbBO.delSqdlrs(roles, sqdlrVOs);
	}

	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp, String ywy_corp)
			throws BusinessException {
		return bxZbBO.querySqdlr(pk_user, user_corp, ywy_corp);
	}

	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap) throws BusinessException {
		bxZbBO.savedefSqdlrs(roles, defMap);
	}

	public void copyDefused(BilltypeVO source, BilltypeVO target) throws DAOException {
		// BaseDAO baseDAO = new BaseDAO();
		// //取出insert两条语句
		// Collection<DefusedSuperVO> defs =
		// baseDAO.retrieveByClause(DefusedSuperVO.class,
		// " objcode in ('"+source.getPk_billtypecode()+"','"+source.getPk_billtypecode()+"B')");
		// //替换objcode和objname
		// String pk_head=null;
		// String pk_body=null;
		// String pk_head_source=null;
		// String pk_body_source=null;
		//
		// for(DefusedSuperVO def:defs){
		// if(def.getObjcode().equals(source.getPk_billtypecode())){
		// def.setObjcode(target.getPk_billtypecode());
		// def.setObjname(target.getBilltypename()+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-000530")/*@res
		// "单据头"*/);
		//
		//
		// pk_head_source=def.getPrimaryKey();//查找依据的主键
		// pk_head = baseDAO.insertVO(def);//变化后的主键
		// }else{
		// def.setObjcode(target.getPk_billtypecode()+"B");
		// def.setObjname(target.getBilltypename()+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-000531")/*@res
		// "单据体"*/);
		//
		// pk_body_source=def.getPrimaryKey();
		// pk_body = baseDAO.insertVO(def);
		// }
		// }
		//
		// Collection<DefcsttblnmeVO> def_head =
		// baseDAO.retrieveByClause(DefcsttblnmeVO.class,
		// " pk_defused='"+pk_head_source+"' ");
		// //替换pk_defcsttblnme
		// for(DefcsttblnmeVO def:def_head){
		// def.setPk_defused(pk_head);
		// baseDAO.insertVO(def);
		// }
		// Collection<DefcsttblnmeVO> def_body =
		// baseDAO.retrieveByClause(DefcsttblnmeVO.class,
		// " pk_defused='"+pk_body_source+"' ");
		// //替换pk_defcsttblnme
		// for(DefcsttblnmeVO def:def_body){
		// def.setPk_defused(pk_body);
		// baseDAO.insertVO(def);
		// }
		//
		// Collection<DefquoteSuperVO> defcsqu_head =
		// baseDAO.retrieveByClause(DefquoteSuperVO.class,
		// " pk_defused='"+pk_head_source+"' ");
		// //替换pk_defcsttblnme
		// for(DefquoteSuperVO def:defcsqu_head){
		// def.setPk_defused(pk_head);
		// baseDAO.insertVO(def);
		// }
		// Collection<DefquoteSuperVO> defcsqu_body =
		// baseDAO.retrieveByClause(DefquoteSuperVO.class,
		// " pk_defused='"+pk_body_source+"' ");
		// //替换pk_defcsttblnme
		// for(DefquoteSuperVO def:defcsqu_body){
		// def.setPk_defused(pk_body);
		// baseDAO.insertVO(def);
		// }

	}

	public JKBXVO retriveItems(JKBXHeaderVO header) throws BusinessException {
		JKBXVO bxvo = VOFactory.createVO(header, queryItems(header));
		Collection<BxcontrastVO> contrasts = queryContrasts(bxvo.getParentVO());
		BxcontrastVO[] contrast = contrasts.toArray(new BxcontrastVO[] {});
		bxvo.setContrastVO(contrast);
		bxvo.setChildrenFetched(true);

		return bxvo;
	}

	public Map<String, String> getTsByPrimaryKey(String[] key, String tableName, String pkfield)
			throws BusinessException {
		return bxZbBO.getTsByPrimaryKey(key, tableName, pkfield);
	}

	@SuppressWarnings("unchecked")
	public List<ReimRuleVO> queryReimRule(String billtype, String pk_org) throws BusinessException {
		try {
			StringBuffer buf = new StringBuffer();
			SQLParameter params = new SQLParameter();
			if (!StringUtil.isEmpty(billtype)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_billtype = ? ");
				params.addParam(billtype);
			}

			if (buf.length() > 0) {
				buf.append(" and ");
			}

			if (!StringUtil.isEmpty(pk_org)) {
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			} else {
				buf.append(" pk_org is null ");
			}
			return (List<ReimRuleVO>) new BaseDAO()
					.retrieveByClause(ReimRuleVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean getIsExpensetypeUsed(String pk_expensetype) throws BusinessException {
		try {
			// 通过取得页面财务组织查询对应的报销标准是否引用返回真假
			List<ReimRuleVO> reimrulevo = (List<ReimRuleVO>) new BaseDAO().retrieveByClause(ReimRuleVO.class,
					"1=1" + " and pk_expensetype='" + pk_expensetype + "' ");
			if (reimrulevo.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean getIsReimtypeUsed(String pk_reimtype) throws BusinessException {
		try {
			// 通过取得页面财务组织查询对应的报销标准是否引用返回真假
			List<ReimRuleVO> reimrulevo = (List<ReimRuleVO>) new BaseDAO().retrieveByClause(ReimRuleVO.class,
					"1=1" + " and pk_reimtype='" + pk_reimtype + "' ");
			if (reimrulevo.size() > 0) {
				return true;
			} else {
				// 判断是否在单据中引用
				List<BXBusItemVO> list = (List<BXBusItemVO>) new BaseDAO().retrieveByClause(
						BXBusItemVO.class, "1=1" + " and pk_reimtype='" + pk_reimtype + "' ");
				if (list.size() > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<ReimRuleVO> saveReimRule(String billtype, String pk_org, ReimRuleVO[] reimRuleVOs)
			throws BusinessException {
		try {
			if (billtype == null || billtype.trim().length() == 0) {
				return new ArrayList<ReimRuleVO>();
			}
			BaseDAO baseDAO = new BaseDAO();
			if (pk_org == null) {
				baseDAO.deleteByClause(ReimRuleVO.class, "pk_billtype='" + billtype + "' and pk_org is null");
			} else {
				baseDAO.deleteByClause(ReimRuleVO.class, "pk_billtype='" + billtype + "' and pk_org='"+ pk_org + "'");
			}
			baseDAO.insertVOArray(reimRuleVOs);
			return queryReimRule(billtype, pk_org);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @author chendya
	 * @return 返回登录用户所关联的人员（返回值1）、人员所在的部门（返回值2）、所属组织(返回值3),所属集团(返回值4)
	 * @param userid
	 *            用户id
	 * @param pk_group
	 *            当前登录集团
	 */
	public String[] queryPsnidAndDeptid(String cuserid, String pk_group) throws BusinessException {
		String[] result = new String[4];
		String jkbxr = NCLocator.getInstance().lookup(IUserPubService.class).queryPsndocByUserid(cuserid);
		result[0] = jkbxr;
		if (jkbxr != null) {
			IPsndocPubService pd = NCLocator.getInstance().lookup(IPsndocPubService.class);
			PsndocVO[] pm = pd.queryPsndocByPks(new String[] { jkbxr }, new String[] { PsndocVO.PK_ORG,
					PsndocVO.PK_GROUP });
			result[1] = pd.queryMainDeptByPandocIDs(jkbxr).get("pk_dept");
			result[2] = pm[0].getPk_org();
			result[3] = pm[0].getPk_group();
		}
		return result;
	}

	@Override
	public String getAgentWhereString(String jkbxr, String rolersql, String billtype, String cuserid,
			String date, String pkOrg) throws BusinessException {
		String wherePart = "";
		try {
			if (jkbxr == null || jkbxr.trim().length() == 0) {
				String pk_psndoc = BXBsUtil.getPk_psndoc(cuserid);
				if (pk_psndoc != null) {
					jkbxr = pk_psndoc;
				}
			}
			if (rolersql == null || rolersql.length() == 0) {
				// 业务类角色
				rolersql = BXBsUtil.getRoleInStr(IRoleConst.BUSINESS_TYPE);
			}
			String dept = null;
			if (jkbxr != null && jkbxr.length() != 0) {
				dept = BXBsUtil.getPsnPk_dept(jkbxr);
			}
			wherePart += " and (bd_psndoc.pk_psndoc='"
					+ jkbxr
					+ "' "
					+ " or ( bd_psndoc.pk_psndoc in(select pk_user from er_indauthorize where type=0 and keyword = 'busiuser' and "
					+ rolersql
					+ ") ) "
					+ " or ( bd_psnjob.pk_dept in(select pk_user from er_indauthorize where type=0 and keyword = 'pk_deptdoc' and "
					+ rolersql
					+ ") )"
					+ " or ((select count(pk_user) from er_indauthorize where type=0 and keyword = 'isall' and pk_user like 'true%' and "
					+ rolersql
					+ ") > 0) "
					+ " or ((select count(pk_user) from er_indauthorize where type=0 and keyword = 'issamedept' and pk_user like 'true%' and "
					+ rolersql + ") > 0 and bd_psnjob.pk_dept ='" + dept + "' ) ";

			String billtypeSql = "";
			if (!StringUtils.isNullWithTrim(billtype)) {
				billtypeSql = " and billtype='" + billtype + "' ";
			}

			wherePart += " or (bd_psndoc.pk_psndoc in(select pk_user from er_indauthorize where pk_operator='"
					+ cuserid
					+ "'"
					+ billtypeSql
					+ " and '"
					+ date
					+ "'<=enddate and '"
					+ date
					+ "'>=startdate)))";

		} catch (Exception e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000392")/*
									 * @res "设置借款报销人过滤错误，录入人没有关联到业务员!"
									 */);
		}
		return wherePart;
	}

	@Override
	public void saveSqdlVO(List<SqdlrVO> preSaveVOList, String condition) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		dao.deleteByClause(SqdlrVO.class, condition);
		dao.insertVOList(preSaveVOList);
	}

	@SuppressWarnings( { "unchecked", "serial" })
	@Override
	public Map<String, List<SqdlrVO>> querySqdlrVO(String sql) throws BusinessException {
		return (Map<String, List<SqdlrVO>>) new BaseDAO().executeQuery(sql.toString(),
				new ResultSetProcessor() {
					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						Collection<SqdlrVO> list = new ArrayList<SqdlrVO>();
						while (rs.next()) {
							SqdlrVO vo = new SqdlrVO();
							vo.setPk_roler((String) rs.getObject(1));
							vo.setPk_authorize((String) rs.getObject(2));
							vo.setPk_user((String) rs.getObject(3));
							vo.setPk_org((String) rs.getObject(4));
							vo.setKeyword((String) rs.getObject(5));
							list.add(vo);
						}
						SqdlrVO[] vos = list.toArray(new SqdlrVO[0]);
						Map<String, List<SqdlrVO>> map = new HashMap<String, List<SqdlrVO>>();
						for (int i = 0; i < vos.length; i++) {
							final String pk_role = vos[i].getPk_roler();
							if (map.containsKey(pk_role)) {
								List<SqdlrVO> valueList = map.get(pk_role);
								valueList.add(vos[i]);
								map.put(pk_role, valueList);
							} else {
								List<SqdlrVO> valueList = new ArrayList<SqdlrVO>();
								valueList.add(vos[i]);
								map.put(pk_role, valueList);
							}
						}
						return map;
					}
				});
	}

	@Override
	public Map<String, String> queryDefaultOrgAndQcrq(String pk_psndoc) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		UFDate startDate = null;
		if (pk_psndoc != null && pk_psndoc.length() > 0) {
			PsndocVO[] persons = NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(
					new String[] { pk_psndoc }, new String[] { PsndocVO.PK_ORG });
			// 人员所属组织
			String pk_org = persons[0].getPk_org();
			try {
				String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
						.getOrgModulePeriodByOrgIDAndModuleID(pk_org, BXConstans.ERM_MODULEID);
				if (yearMonth != null && yearMonth.length() != 0) {
					String year = yearMonth.substring(0, 4);
					String month = yearMonth.substring(5, 7);
					if (year != null && month != null) {
						// 返回组织的会计日历
						AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
						if (calendar == null) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("2011v61013_0", "02011v61013-0021")/*
																					 * @res
																					 * "组织的会计期间为空"
																					 */);
						}
						calendar.set(year, month);
						if (calendar.getMonthVO() == null) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("2011v61013_0", "02011v61013-0022")/*
																					 * @res
																					 * "组织起始期间为空"
																					 */);
						}
						startDate = calendar.getMonthVO().getBegindate();
					}
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			if (startDate == null) {
				ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("expensepub_0", "02011002-0001")/*
																	 * @res
																	 * "该组织模块启用日期为空"
																	 */));
				return null;
			}
			result.put(pk_org, startDate.toString());
		}
		return result;
	}

	@Override
	public BillTempletVO[] getBillListTplData(BillOperaterEnvVO[] envos) throws BusinessException {
		nc.itf.uap.billtemplate.IBillTemplateQry service = NCLocator.getInstance().lookup(
				nc.itf.uap.billtemplate.IBillTemplateQry.class);
		BillTempletVO[] billTempletVOs = service.findBillTempletDatas(envos);
		return billTempletVOs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, SuperVO> getDeptRelCostCenterMap(String pk_group) throws BusinessException {
		boolean isResInstalled = BXUtil.isProductInstalled(pk_group, BXConstans.FI_RES_FUNCODE);
		if (!isResInstalled) {
			return null;
		}
		SQLParameter param = new SQLParameter();
		param.addParam(pk_group);
		Collection<DeptVO> voList = new BaseDAO().retrieveByClause(DeptVO.class, "pk_group=?",
				new String[] { DeptVO.PK_DEPT }, param);
		// 成本中心查询接口
		ICostCenterQueryOpt service = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class);
		Map<String, SuperVO> map = new HashMap<String, SuperVO>();
		for (DeptVO vo : voList) {
			String key = vo.getPk_dept();
			SuperVO[] costCenterVOs = service.queryCostCenterVOByDept(new String[] { key });
			if (costCenterVOs != null && costCenterVOs.length > 0) {
				map.put(key, costCenterVOs[0]);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SuperVO> getVORemoteCall(Class<?> voClassName, String condition, String[] fields)
			throws BusinessException {
		return (List<SuperVO>) new BaseDAO().retrieveByClause(voClassName, condition, fields);
	}

	@Override
	public Map<String, String> getPermissonOrgMapCall(String pkUser, String nodeCode, String pkGroup,
			UFDate date) throws BusinessException {
		String[] userPermissionOrgPKs = NCLocator.getInstance().lookup(IFunctionPermissionPubService.class)
				.getUserPermissionPkOrgs(pkUser, nodeCode, pkGroup);

		Map<String, String> map = new HashMap<String, String>();

		Map<String, OrgVersionVO> orgVersionVOsByOrgs = NCLocator.getInstance().lookup(
				IOrgVersionQueryService.class).getOrgVersionVOsByOrgsAndDate(userPermissionOrgPKs, date);

		for (String key : orgVersionVOsByOrgs.keySet()) {
			map.put(orgVersionVOsByOrgs.get(key).getPk_vid(), key);
		}

		return map;
	}

}
