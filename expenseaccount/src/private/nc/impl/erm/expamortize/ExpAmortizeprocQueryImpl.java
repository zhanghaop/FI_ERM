package nc.impl.erm.expamortize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.er.util.SqlUtils;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expamortize.IExpAmortizeprocQuery;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class ExpAmortizeprocQueryImpl implements IExpAmortizeprocQuery {
	
	@Override
	public ExpamtprocVO[] linkProcByInfoVo(ExpamtinfoVO infovo) throws BusinessException {
		if (infovo == null) {
			return null;
		}
		ExpamtprocVO[] exprocVOs = null;
		try {
			
			String whereSql = " pk_expamtinfo in (select detail.pk_expamtdetail from er_expamtdetail detail " +
					"where detail.pk_expamtinfo = '"+ infovo.getPk_expamtinfo() +"' ) order by accperiod desc";

			IMDPersistenceQueryService queryService = MDPersistenceService.lookupPersistenceQueryService();
			
			@SuppressWarnings("unchecked")
			Collection<ExpamtprocVO> result = queryService.queryBillOfVOByCond(ExpamtprocVO.class, whereSql,
					false);
			if (result == null) {
				return null;
			}
			
			
			exprocVOs = dealprocGroupByAccperiod(result.toArray(new ExpamtprocVO[]{}));;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return exprocVOs;
	}
	
	/**
	 * 此方法仅是将同期摊销记录的金额相加
	 * 
	 * @param procs
	 * @return
	 */
	private ExpamtprocVO[] dealprocGroupByAccperiod(ExpamtprocVO[] procs) {
		if (procs != null) {
			Map<String, ExpamtprocVO> procMap = new HashMap<String, ExpamtprocVO>();
			
			for (ExpamtprocVO proc : procs) {
				String key = proc.getCreationtime().toString();
				if (procMap.get(key) == null) {
					procMap.put(key, proc);
				} else {
					ExpamtprocVO tempProc = procMap.get(key);

					tempProc.setCurr_amount(tempProc.getCurr_amount().add(proc.getCurr_amount()));
					tempProc.setCurr_orgamount(tempProc.getCurr_orgamount().add(proc.getCurr_orgamount()));
					tempProc.setCurr_groupamount(tempProc.getCurr_groupamount().add(proc.getCurr_groupamount()));
					tempProc.setCurr_globalamount(tempProc.getCurr_globalamount().add(proc.getCurr_globalamount()));
				}
			}
			
			List<ExpamtprocVO> result = new ArrayList<ExpamtprocVO>();
			
			for(ExpamtprocVO proc :procMap.values()){
				result.add(proc);
			}
			
			Collections.sort(result, new Comparator<ExpamtprocVO>() {
				@Override
				public int compare(ExpamtprocVO o1, ExpamtprocVO o2) {
					return o1.getCreationtime().compareTo(o2.getCreationtime());
				}
			});
			
			return result.toArray(new ExpamtprocVO[] {});
		}

		return null;
	}

	@Override
	public ExpamtprocVO[] queryByDjbhAndPKOrg(String djbh,String pk_org)throws BusinessException{
		if (djbh == null || pk_org == null) {
			return null;
		}

		ExpamtprocVO[] exprocVOs = null;

		try {
			IMDPersistenceQueryService queryService = MDPersistenceService.lookupPersistenceQueryService();
			StringBuffer whereSql = new StringBuffer();

			whereSql.append(
					"pk_expamtinfo in (select pk_expamtdetail From er_expamtdetail where bx_billno='" + djbh
							+ "' and pk_org='" + pk_org + "')").append(" order by accperiod ");
			@SuppressWarnings("unchecked")
			Collection<ExpamtprocVO> result = queryService.queryBillOfVOByCond(ExpamtprocVO.class, whereSql.toString(),
					false);
			if (result == null) {
				return null;
			}
			exprocVOs = dealprocGroupByAccperiod(result.toArray(new ExpamtprocVO[] {}));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return exprocVOs;
	}

	@Override
	public ExpamtprocVO[] queryByInfoPksAndAccperiod(String[] infoPks, String accperiod) throws BusinessException {
		ExpamtprocVO[] result = null;

		IMDPersistenceQueryService queryService = MDPersistenceService.lookupPersistenceQueryService();
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(SqlUtils.getInStr1(ExpamtprocVO.PK_EXPAMTINFO, infoPks)).append(" and ")
				.append(ExpamtprocVO.ACCPERIOD + "= '" + accperiod + "'");

		@SuppressWarnings("unchecked")
		Collection<ExpamtprocVO> resultCollection = queryService.queryBillOfVOByCond(ExpamtprocVO.class,
				whereSql.toString(), false);

		if (resultCollection == null) {
			return null;
		}

		result = (ExpamtprocVO[]) resultCollection.toArray(new ExpamtprocVO[] {});

		return result;
	}

	@Override
	public ExpamtprocVO[] queryByProcPks(String[] pks) throws BusinessException {
		if (pks == null) {
			return null;
		}

		ExpamtprocVO[] exprocVOs = null;

		try {
			IMDPersistenceQueryService queryService = MDPersistenceService.lookupPersistenceQueryService();

			@SuppressWarnings("unchecked")
			Collection<ExpamtprocVO> result = queryService.queryBillOfVOByPKs(ExpamtprocVO.class, pks, false);
			if (result == null) {
				return null;
			}
			exprocVOs = (ExpamtprocVO[]) result.toArray(new ExpamtprocVO[] {});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return exprocVOs;
	}
	
	@Override
	public ExpamtprocVO[] queryEffectProcByPksAndAccperiod(String[] infoPks, String accperiod) throws BusinessException {
		ExpamtprocVO[] result = null;

		IMDPersistenceQueryService queryService = MDPersistenceService.lookupPersistenceQueryService();
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(SqlUtils.getInStr1(ExpamtprocVO.PK_EXPAMTINFO, infoPks)).append(" and ")
				.append(ExpamtprocVO.ACCPERIOD + "= '" + accperiod + "'");
		
		whereSql.append(" order by creationtime desc ");
		
		//按摊销日期进行排序
		@SuppressWarnings("unchecked")
		Collection<ExpamtprocVO> resultCollection = queryService.queryBillOfVOByCond(ExpamtprocVO.class, whereSql.toString(), false);
		
		// 如果摊销记录为空或摊销记录/表体行%2 = 0 ，则返回null； 因为当摊销记录/表体行%2 = 0 时，说明已反摊销
		if (resultCollection == null || resultCollection.size() == 0 || resultCollection.size() / infoPks.length % 2 == 0) {
			return null;
		}
		ExpamtprocVO[] totalProcVos = resultCollection.toArray(new ExpamtprocVO[]{});

		result = new ExpamtprocVO[infoPks.length];
		for(int i = 0; i < result.length; i ++){
			//取结果集的最新infoPks.length行数据
			result[i] = totalProcVos[i];
		}

		return result;
	}
}
