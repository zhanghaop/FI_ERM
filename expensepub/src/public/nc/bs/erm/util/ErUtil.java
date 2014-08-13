package nc.bs.erm.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.BillTypeCacheKey;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.org.IOrgConst;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.itf.uap.sf.IProductVersionQueryService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.ICloseAccQryPubServicer;
import nc.pubitf.uapbd.CurrencyRateUtilHelper;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.ErmDataFinder;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.rs.MemoryResultSet;
import nc.vo.sm.install.ProductVersionVO;
import nc.vo.trade.billsource.LightBillVO;

public class ErUtil {
	/**
	 * 调用模块是否安装
	 * 
	 * @param pro
	 * @return
	 */
	public static boolean isProductTbbInstalled(String pro) {
		boolean value = false;
		
		try {
			ProductVersionVO[] ProductVersionVOs  = NCLocator.getInstance().lookup(IProductVersionQueryService.class).queryByProductCode(pro);
			if(ProductVersionVOs == null || ProductVersionVOs.length==0){
				value = false;
			} else {
				value = true;
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return value ;
	}
	
	/**
	 * 调用启用模块API(非预算调用)
	 * @param pk_group 集团
	 * @param funcode 功能节点 数据来源于dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isProductInstalled(String strCorpPK,String pro) {
	
		boolean value = false;
		
		try {
			value = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(strCorpPK, pro);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return value ;
	}
	
	/**
	 * 根据pk_group和pk_org解析节点类型
	 * @param pk_group 集团
	 * @param pk_org 主组织
	 * @return
	 */
	public static NODE_TYPE getNodeTypeByPk_groupAndPk_org(String pk_group, String pk_org) {
        if (!StringUtil.isEmpty(pk_org) && !StringUtil.isEmpty(pk_group) && pk_org.equals(pk_group)) {
            //所属组织、所属集团均不为空且两者相等的为集团级数据
            return NODE_TYPE.GROUP_NODE;
        } else if (!StringUtil.isEmpty(pk_org) && !StringUtil.isEmpty(pk_group) && !pk_org.equals(pk_group) && !pk_org.equals(IOrgConst.GLOBEORG)){
            //所属组织、所属集团均不为空且所属组织不等于所属集团且所属组织不等于全局组织的为组织级数据
            return NODE_TYPE.ORG_NODE;
        }
        //其它为全局级数据（包括无所属组织、所属集团字段或无该字段值的数据）
        return NODE_TYPE.GLOBE_NODE;
    }
	
	/**
	 * 批量根据模块号，业务单元oid,会计期间(年-月)判断是否关账。若已关帐返回true
	 * 
	 * @param moduleCode
	 * @param pk_org
	 * @param date
	 * @throws BusinessException
	 */
	public static boolean isOrgCloseAcc(String moduleCode, String pk_org, UFDate date) throws BusinessException {
		if (moduleCode == null || pk_org == null || date == null) {
			return false;
		}
		moduleCode = moduleCode.substring(0, 4);
		AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
		// 设置日期
		calendar.setDate(date);
		String period = calendar.getMonthVO().getYearmth();
		Map<String, Boolean> res = NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class).isCloseByModuleIdAndPk_org(
				moduleCode, pk_org, new String[] { period });
		if (res.get(period) != null && res.get(period).booleanValue()) {
			return true;
		}
		
		return false;
	}
	
	public static void convertCurrtype(MemoryResultSet result, ReportQueryCondVO queryVO) throws SQLException {
        boolean beForeignCurrency = IPubReportConstants.ACCOUNT_FORMAT_FOREIGN
                .equals(((ReportInitializeVO) queryVO.getRepInitContext()
                        .getParentVO()).getReportformat());
        if (beForeignCurrency) {
            return;
        }
	    @SuppressWarnings("unchecked")
        List<List<Object>> dataRowList = result.getResultArrayList();
        if (dataRowList == null || dataRowList.size() == 0) {
            return;
        }
        CurrencyRateUtilHelper currIns = CurrencyRateUtilHelper.getInstance();
        String pkCurrGroup = currIns.getLocalCurrtypeByOrgID(queryVO.getPk_group());
        String pkCurrGloble = currIns.getLocalCurrtypeByOrgID(IOrgConst.GLOBEORG);
        int pkCurrtypeIndex = result.getColumnIndex("pk_currtype");
        int orgIndex = result.getColumnIndex("pk_org");
        List<Object> dataRow = null;
        for (int i = 0; i < dataRowList.size(); i++) {
            dataRow = dataRowList.get(i);
            String pkOrg = (String)dataRow.get(orgIndex -1);
            String pkCurrtype = (String)dataRow.get(pkCurrtypeIndex - 1);
            if (pkOrg == null || pkCurrtype == null) {
                continue;
            }
            if (IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
                pkCurrtype = currIns.getLocalCurrtypeByOrgID(pkOrg);
                dataRow.set(pkCurrtypeIndex - 1, pkCurrtype);
            } else if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
                // 集团本币
                dataRow.set(pkCurrtypeIndex - 1, pkCurrGroup);
            } else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
                // 全局本币
                dataRow.set(pkCurrtypeIndex - 1, pkCurrGloble);
            }
        }
	}
	
	/**
	 * 查询下游单据
	 * @param djlxbm 源单据类型
	 * @param forwordBillTypes 下游单据类型
	 * @param strBillIds 源单据id集合
	 * @return 下游单据信息
	 * @throws Exception
	 */
	public static LightBillVO[] queryForwardBills(String djlxbm, String[] forwordBillTypes, String[] strBillIds)
			throws Exception {
		String strBilltype = "";
		if (djlxbm.startsWith("261")) {
			strBilltype = ErmBillConst.MatterApp_BILLTYPE;
		}else if(djlxbm.startsWith("263")){
			strBilltype = BXConstans.JK_DJLXBM;
		}else if(djlxbm.startsWith("264")){
			strBilltype = BXConstans.BX_DJLXBM;
		}

		if (forwordBillTypes != null && forwordBillTypes.length > 0) {
			List<LightBillVO> result = new ArrayList<LightBillVO>();
			
//			BillTypeSetBillFinder billTypeBillFinder = new BillTypeSetBillFinder();
			for (String forwordBillType : forwordBillTypes) {
				if (isExistsBilltype(forwordBillType)) {
					LightBillVO[] lightVos = new ErmDataFinder().getForwardBills(strBilltype, forwordBillType,
							strBillIds);
					if (lightVos != null) {
						result.addAll(Arrays.asList(lightVos));
					}
				}
			}
			return result.toArray(new LightBillVO[0]);
		}
		return null;
	}
	
	/**
	 * 
	 * 判断单据类型是否存在
	 * 
	 * @param strTypeCodestr
	 * @return
	 */
	private static boolean isExistsBilltype(String strTypeCodestr) {
		BillTypeCacheKey strTypeCode = new BillTypeCacheKey().buildBilltype(strTypeCodestr).buildPkGroup(
				InvocationInfoProxy.getInstance().getGroupId());
		if (strTypeCode == null || StringUtil.isEmptyWithTrim(strTypeCode.getBilltype()))
			return false;
		String billtype = strTypeCode.getBilltype().trim();
		strTypeCode.buildBilltype(billtype);
		// XXX::根据缓存获取单据类型
		BilltypeVO btVO = PfDataCache.getBillTypeInfo(strTypeCode);
		if (btVO == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取人员设置授权代理条件sql
	 * 
	 * @param pk_psn
	 * @param rolersql
	 * @param billtype
	 * @param pk_user
	 * @param billDate
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public static String getAgentWhereString(String pk_psn, String rolersql, String billtype, String pk_user,
			String billDate, String pk_org) throws BusinessException {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class).getAgentWhereString(pk_psn, rolersql, billtype,
				pk_user, billDate, pk_org);
	}

    public static String[] insertHeadOneOrg(String pkOrg, Object[] pkOrgs) {
        String[] pksOrg = null;
        if (pkOrgs != null) {
            pksOrg = new String[pkOrgs.length + 1];
            pksOrg[0] = pkOrg;
            for (int nPos = 0; nPos < pkOrgs.length; nPos++) {
                pksOrg[nPos + 1] = (String)pkOrgs[nPos];
            }
        } else {
            pksOrg = new String[] { pkOrg };
        }
        return pksOrg;
    }
    
    
    /**
	 * 获取数据权限sql
	 * 
	 * @param queryScheme
	 * @return
	 */
	public static String getQueryPowerSql(String whereCondition) {
		if (whereCondition == null) {
			return "";
		}

		int indexOf = whereCondition.indexOf("ZDP_");// 取第一个数据权限索引的下标
		if (indexOf != -1) {
			String tempsql = (String) whereCondition.subSequence(0, indexOf);// 不包含数据权限的s

			String lowerCase = tempsql.toLowerCase();
			int lastIndexOf = lowerCase.lastIndexOf("and");// 找到最后一个and的下标
			String powerSql = whereCondition.substring(lastIndexOf + 3);
			return powerSql;
		}

		return "";
	}

	/**
	 * 获取无数据权限的sql
	 * 
	 * @param queryScheme
	 * @return
	 */
	public static String getQueryNomalSql(String whereCondition) {
		if (whereCondition == null) {
			return "";
		}

		String powerSql = getQueryPowerSql(whereCondition);
		if (powerSql != null && powerSql.trim().length() > 0) {
			String tempSql = whereCondition.replace(powerSql, "");

			return tempSql.substring(0, tempSql.toLowerCase().lastIndexOf("and"));
		}

		return whereCondition;
	}
}
