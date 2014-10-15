package nc.pubitf.erm.closeacc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bs.dao.BaseDAO;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.itf.org.IBatchCloseAccQryService;
import nc.itf.org.ICloseAccBookQryService;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.org.ICloseAccPubServicer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.org.CloseAccBookVO;
import nc.vo.org.CloseAccModuleVO;
import nc.vo.org.OrgModulePeriodVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trade.sqlutil.IInSqlBatchCallBack;
import nc.vo.trade.sqlutil.InSqlBatchCaller;

import org.apache.commons.lang.StringUtils;

public class ErmCloseAccBookQryServiceImpl implements IErmCloseAccBookQryService {

    @Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "通过会计期间和主组织查询结账VO"/* -=notranslate=- */, type = BusinessType.CORE)
    public CloseAccBookVO[] queryCloseAccBookVoByPk(String pk_accperiod,
            String pk_org) throws BusinessException {
        if (StringUtils.isEmpty(pk_org)) {
            return new CloseAccBookVO[0];
        }

        ICloseAccBookQryService qryServicer = NCLocator.getInstance().lookup(ICloseAccBookQryService.class);
        
        CloseAccModuleVO[] vos = qryServicer.queryCloseAccMdlVosByPK_org(pk_org,
                BXConstans.ERM_MODULEID);
        
        // 启用期间
        String enableYearMth = null;
        
        // 将组织的启用期间查出
        if (vos != null) {
            AccperiodmonthVO enableMonthVo = AccperiodmonthAccessor
                    .getInstance().queryAccperiodmonthVOByPk(
                            vos[0].getPk_accperiodmonth());
            enableYearMth = enableMonthVo.getYearmth();
        }
        // 查询
        ICloseAccBookQryService closeAccBookQry = 
            NCLocator.getInstance().lookup(ICloseAccBookQryService.class);
        CloseAccBookVO[] closeAccBookVos = closeAccBookQry.queryCloseAccBookVoByPk(
                pk_accperiod,
                pk_org,
                pk_org,
                BXConstans.ERM_MODULEID,
                enableYearMth);
        return closeAccBookVos;
    }

    @Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "通过会计期间和主组织批量查询结账VO"/* -=notranslate=- */, type = BusinessType.CORE)
    public CloseAccBookVO[] batchqueryCloseAccBookVoByPk(
    			String pk_group,
    	      String moduleid, String pk_accperiodscheme, String accPeriodMonth,
    	      String pkOrg[]) throws BusinessException {
    	if (pkOrg==null || pkOrg.length==0) {
            return new CloseAccBookVO[0];
        }
    	// 批量查询
		BatchCloseAccBookVO[] queryCloseAccBookVOs = NCLocator.getInstance()
				.lookup(IBatchCloseAccQryService.class).queryCloseAccBookVOs(
						pk_group, BXConstans.ERM_MODULEID, pk_accperiodscheme,
						accPeriodMonth, pkOrg);

    	return queryCloseAccBookVOs;
    }
    

    
    @Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.MaxMinPeriod, description = "根据给定财务组织查询最大结账期间和最小未结账期间"/* -=notranslate=- */, type = BusinessType.CORE)
    public Map<String, List<String>> getMaxEndedAndMinNotEndedAccByOrg(
            String[] pk_org) throws BusinessException {
        ICloseAccPubServicer closeAccBookPubServ = 
            NCLocator.getInstance().lookup(ICloseAccPubServicer.class);
        Map<String,String> maxMap = closeAccBookPubServ.getMaxEndedAccByOrg(BXConstans.ERM_MODULEID, pk_org);
        Map<String,String> minMap = getMinNotEndedAccByOrg(BXConstans.ERM_MODULEID, pk_org);
        Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
        for (String pkOrg : pk_org) {
            List<String> list = new ArrayList<String>(2);
            returnMap.put(pkOrg, list);
        }
        fillData(maxMap, returnMap);
        fillData(minMap, returnMap);
        return returnMap;
    }
    
    /**
     * 要将 调整期过滤掉
     * @param moduleid
     * @param pkOrg
     * @return
     * @throws BusinessException
     */
	public Map<String, String> getMinNotEndedAccByOrg(final String moduleid,
			String[] pkOrg) throws BusinessException {
		final Map<String, String> result = new HashMap<String, String>();
		InSqlBatchCaller caller = new InSqlBatchCaller(pkOrg);
		try {
			caller.execute(new IInSqlBatchCallBack() {
				@Override
				@SuppressWarnings("unchecked")
				public Object doWithInSql(String inSql)
						throws BusinessException, SQLException {
					try {
						List<Map<String, String>> values = new ArrayList<Map<String, String>>();
						List<Map<String, String>> selectedValue = (List<Map<String, String>>) new BaseDAO()
								.executeQuery("select c."
										+ OrgVO.PK_ORG
										+ ",min(d."
										+ AccperiodmonthVO.YEARMTH
										+ ") "
										+ AccperiodmonthVO.YEARMTH
										+ " from (select max("
										+ AccperiodmonthVO.YEARMTH
										+ ") "
										+ AccperiodmonthVO.YEARMTH
										+ ",x."
										+ OrgVO.PK_ORG
										+ ",x."
										+ AccperiodmonthVO.PK_ACCPERIODSCHEME
										+ " from "
										+ OrgVO.getDefaultTableName()
										+ " x left join "
										+ CloseAccBookVO.getDefaultTableName()
										+ " a on x."
										+ OrgVO.PK_ORG
										+ "=a."
										+ CloseAccBookVO.PK_ORG
										+ " and a."
										+ CloseAccBookVO.ISENDACC
										+ "='"
										+ UFBoolean.TRUE
										+ "' and a."
										+ CloseAccBookVO.MODULEID
										+ "='"
										+ moduleid
										+ "' left join "
										+ AccperiodmonthVO
												.getDefaultTableName()
										+ " b on a."
										+ CloseAccBookVO.PK_ACCPERIODMONTH
										+ "=b."
										+ AccperiodmonthVO.PK_ACCPERIODMONTH
										+ " where x."
										+ OrgVO.PK_ORG
										+ " in "
										+ inSql
										+ " group by x.pk_org,x."
										+ AccperiodmonthVO.PK_ACCPERIODSCHEME
										+ ") c left join "
										+ AccperiodmonthVO
												.getDefaultTableName()
										+ " d on c." + AccperiodmonthVO.YEARMTH
										+ "<d." + AccperiodmonthVO.YEARMTH
										+ " and c."
										+ AccperiodmonthVO.PK_ACCPERIODSCHEME
										+ "=d."
										+ AccperiodmonthVO.PK_ACCPERIODSCHEME
										+ " and d.isadj= 'N' "
										+ " group by c." + OrgVO.PK_ORG + "",
										new MapListProcessor());
						List<Map<String, String>> defaultValue = (List<Map<String, String>>) new BaseDAO()
								.executeQuery("select "
										+ OrgModulePeriodVO.PK_ORG
										+ " "
										+ OrgModulePeriodVO.PK_ORG
										+ ","
										+ OrgModulePeriodVO.PK_ACCPERIOD
										+ " "
										+ AccperiodmonthVO.YEARMTH
										+ " from "
										+ OrgModulePeriodVO
												.getDefaultTableName()
										+ " where " + OrgModulePeriodVO.PK_ORG
										+ " in " + inSql + " and "
										+ OrgModulePeriodVO.MODULEID + "='"
										+ moduleid + "' and "
										+ OrgModulePeriodVO.PK_ORG
										+ " not in (select c."
										+ CloseAccBookVO.PK_ORG + " from "
										+ CloseAccBookVO.getDefaultTableName()
										+ " c where c."
										+ CloseAccBookVO.ISENDACC + " = '"
										+ UFBoolean.TRUE + "' and c."
										+ CloseAccBookVO.MODULEID + " = '"
										+ moduleid + "')",
										new MapListProcessor());
						if (selectedValue != null && selectedValue.size() > 0)
							values.addAll(selectedValue);
						if (defaultValue != null && defaultValue.size() > 0)
							values.addAll(defaultValue);
						for (Map<String, String> m : values)
							result.put(m.get(OrgVO.PK_ORG), m
									.get(AccperiodmonthVO.YEARMTH));
					} catch (BusinessException e) {
						throw e;
					} catch (Exception e) {
						throw new BusinessExceptionAdapter(
								new BusinessException(e));
					}
					return null;
				}
			});
			return result;
		} catch (BusinessException e) {
			throw e;
		} catch (SQLException e) {
			throw new BusinessExceptionAdapter(new BusinessException(e));
		}
	}
    
    private void fillData(Map<String,String> maxMinMap, Map<String,List<String>> returnMap) {
        Iterator<Entry<String, List<String>>> iter = returnMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, List<String>> entry = iter.next();
            if (maxMinMap == null || maxMinMap.get(entry.getKey()) == null) {
                entry.getValue().add(StringUtils.EMPTY);
            } else {
                entry.getValue().add(maxMinMap.get(entry.getKey()));
            }
        }
    }

	@Override
	public Map<String, List<String>> queryPeriodAndOrg(
			Map<String, List<String>> maxMinDesinfo, String closeStatus) throws BusinessException {
		Map<String,List<String>> periodOrgMap=new HashMap<String,List<String>>();//会计月作为key
		for(Map.Entry<String, List<String>> entrySet: maxMinDesinfo.entrySet()){
			//将统一会计期间的组织结账信息为一组，分批分组来查询
			String minMaxPeriod = "";
			if("wjz".equals(closeStatus)){
				minMaxPeriod = entrySet.getValue().get(1);//未结账
			}else{
				minMaxPeriod = entrySet.getValue().get(0);
			}
			
			String pk_org = entrySet.getKey();
			
			//没有对应的会计期间和调整期都不处理结账
			if(StringUtils.isEmpty(minMaxPeriod) || minMaxPeriod.length()!=7 ){
				continue;
			}
			try {
				AccperiodmonthVO accperiodmonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org,minMaxPeriod);
				List<String> orgList = periodOrgMap.get(accperiodmonth.getPk_accperiodmonth());
				if (orgList == null) {
					orgList = new ArrayList<String>();
					periodOrgMap.put(accperiodmonth.getPk_accperiodmonth(), orgList);
				}
				orgList.add(pk_org);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}

		return periodOrgMap;
	}

	@Override
	public List<CloseAccBookVO> queryCloseAccBook(
			Map<String, List<String>> periodOrgMap) throws BusinessException {
		List<CloseAccBookVO> batchvos = new ArrayList<CloseAccBookVO>();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		
		for (Map.Entry<String, List<String>> entrySet : periodOrgMap.entrySet()) {
			// 得到的是每个组织的会计月
			String pkAccperiod = entrySet.getKey();
			List<String> pk_orgs = entrySet.getValue();
			CloseAccBookVO[] vos;
			// 得到所有组织的不同会计期间pk
			try {
				AccperiodmonthVO monthVO = ErAccperiodUtil.getAccperiodmonthByPk(pkAccperiod);
				vos = batchqueryCloseAccBookVoByPk(pk_group,BXConstans.ERM_MODULEID,
						monthVO.getPk_accperiodscheme(), pkAccperiod,
						pk_orgs.toArray(new String[0]));
				batchvos.addAll(Arrays.asList(vos));
				
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
			Collections.sort(batchvos, new AccBookVOComparator());
		
		return batchvos;
	}
	
    private class AccBookVOComparator implements Comparator<CloseAccBookVO>{
		@Override
		public int compare(CloseAccBookVO o1, CloseAccBookVO o2) {
			//财务组织按编码排序
			try {
				OrgVO code1 = CacheUtil.getVOByPk(OrgVO.class, o1.getPk_org());
				OrgVO code2 = CacheUtil.getVOByPk(OrgVO.class, o2.getPk_org());
				return code1.getCode().compareTo(code2.getCode());
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			return 0;
		}
    }


}
