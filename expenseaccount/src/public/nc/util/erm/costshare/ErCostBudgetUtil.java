package nc.util.erm.costshare;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.erm.costshare.ext.CostShareYsControlVOExt;
import nc.vo.pub.BusinessException;

/**
 * 结转单预算工具
 * 
 * @author chenshuaia
 * 
 */
public class ErCostBudgetUtil {
	public static IFYControl[] getCostControlVOByCSVO(AggCostShareVO[] csVo, String pk_user) throws BusinessException {
		IFYControl[] items;
		// 查询费用结转主vo
		List<CostShareYsControlVO> itemList = new ArrayList<CostShareYsControlVO>();
		List<String> depts = new ArrayList<String>();
		List<String> corps = new ArrayList<String>();
		List<String> centers = new ArrayList<String>();
		for (int i = 0; i < csVo.length; i++) {
			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				if (dtailvos[j].getAssume_dept() != null) {
					depts.add(dtailvos[j].getAssume_dept());
				}
				if (dtailvos[j].getPk_resacostcenter() != null) {
					centers.add(dtailvos[j].getPk_resacostcenter());
				}
				corps.add(dtailvos[j].getAssume_org());
			}
		}
		List<String> listPerson = getCorpPerson(corps, pk_user);
		List<String> listDept = getPassDeptPerson(depts.toArray(new String[0]), pk_user);
		List<String> listCenter = getPassCenterPerson(centers.toArray(new String[0]), pk_user);

		for (int i = 0; i < csVo.length; i++) {
			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
			CostShareVO headvo = (CostShareVO) aggvo.getParentVO();
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
			
			boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(headvo.getPk_group(), headvo.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);

			for (int j = 0; j < dtailvos.length; j++) {
				// 转换生成controlvo
				CShareDetailVO detailvo = dtailvos[j];
				CostShareYsControlVOExt cscontrolvo = new CostShareYsControlVOExt(headvo, detailvo);
				if(isAdjust){
					// 调整单情况，需要根据分摊明细行的预算占用日期进行预算控制
					cscontrolvo.setYsDate(detailvo.getYsdate());
					// 不受负责人限制
					itemList.add(cscontrolvo);
					continue;
				}
				if (csVo != null && csVo.length == 1) {
					// 报销管理节点需要处理
					if (listPerson.contains(detailvo.getAssume_org())) {
						// 联查负责单位的预算执行情况
						itemList.add(cscontrolvo);
						continue;
					}
					if (listDept.contains(detailvo.getAssume_dept())) {
						// 联查负责部门的预算情况
						itemList.add(cscontrolvo);
						continue;
					}

					if (listCenter != null && listCenter.contains(detailvo.getPk_resacostcenter())) {
						// 联查负责成本中心的预算执行情况
						itemList.add(cscontrolvo);
						continue;
					}
				}
			}
		}
		items = itemList.toArray(new IFYControl[0]);
		return items;
	}

	// 登陆用户是否为当前公司的费用单位负责人
	private static List<String> getCorpPerson(List<String> corps, String pk_loginUser) {
		List<String> corpList = null;
		try {
			corpList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedCorpOfPerson(
					corps.toArray(new String[0]), pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return corpList;
	}

	// 登陆用户是否为当前登陆公司的部门负责人
	private static List<String> getPassDeptPerson(String[] depts, String pk_loginUser) {
		List<String> deptList = null;
		try {
			deptList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedDeptOfPerson(depts, pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		// 费用承担部门过滤
		return deptList;
	}

	// 登陆用户是否为当前登陆公司的部门负责人
	private static List<String> getPassCenterPerson(String[] centers, String pk_loginUser) {
		List<String> centerList = null;
		try {
			centerList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedCenterOfPerson(centers,
					pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		// 费用承担部门过滤
		return centerList;
	}
}
