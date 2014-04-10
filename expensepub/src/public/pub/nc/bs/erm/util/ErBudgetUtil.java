package nc.bs.erm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;

/**
 * 预算工具类
 * 
 * @author chenshuaia
 */
public class ErBudgetUtil {

	/**
	 * 获取修改后的预算控制VO集合
	 * 
	 * @param items
	 *            修改后VO集合
	 * @param items_old
	 *            修改前VO集合
	 * @param ruleVOs
	 *            控制策略
	 * @return
	 */
	public static YsControlVO[] getEditControlVOs(IFYControl[] items, IFYControl[] items_old, DataRuleVO[] ruleVOs) {
		YsControlVO[] ps = null;
		Vector<YsControlVO> v = new Vector<YsControlVO>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** 单据类型/交易类型 */
			/** 预占的：PREFIND,执行：UFIND */
			String methodFunc = ruleVo.getDataType();
			/** 如果是增加：true，如果是减少，false */
			boolean isAdd = ruleVo.isAdd();
			for (int i = 0; i < items.length; i++) {
				if (items[i].isYSControlAble()) {
					YsControlVO psTemp = new YsControlVO();
					psTemp.setIscontrary(false);
					psTemp.setItems(new IFYControl[] { items[i] });
					psTemp.setAdd(isAdd);
					psTemp.setMethodCode(methodFunc);
					v.addElement(psTemp);
				}
			}

			for (int i = 0; i < items_old.length; i++) {
				if (items_old[i].isYSControlAble()) {
					YsControlVO psTemp = new YsControlVO();
					psTemp.setIscontrary(true);
					psTemp.setItems(new IFYControl[] { items_old[i] });
					psTemp.setAdd(!isAdd);
					psTemp.setMethodCode(methodFunc);
					v.addElement(psTemp);
				}
			}
		}
		ps = new YsControlVO[v.size()];
		v.copyInto(ps);
		return ps;
	}
	
	/**
	 * 获取预算控制VO
	 * 有场景，写执行和回写预占的数据不一致，提供此方法
	 * @param items
	 * @param contrayItems 
	 * @param iscontrary
	 * @param ruleVOs
	 * @return
	 */
	public static YsControlVO[] getCtrlVOs(IFYControl[] items, IFYControl[] contrayItems, boolean iscontrary, DataRuleVO[] ruleVOs) {
		YsControlVO[] result = null;
		Vector<YsControlVO> resultVector = new Vector<YsControlVO>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** 单据类型/交易类型 */
			String billType = ruleVo.getBilltype_code();
			/** 预占的：PREFIND,执行：UFIND */
			String methodFunc = ruleVo.getDataType();
			/** 如果是增加：true，如果是减少，false */
			boolean isAdd = true; 
			
			if (n == 1) {//存在上游的情况
				isAdd = iscontrary ? ruleVo.isAdd() : !ruleVo.isAdd();
			} else {
				isAdd = iscontrary ? !ruleVo.isAdd() : ruleVo.isAdd();
			}
			
			IFYControl[] fyItems = null;
			if (contrayItems == null || contrayItems.length == 0) {
				fyItems = items;
			} else if (n == 0) {
				fyItems = items;
			} else {
				fyItems = contrayItems;
			}
			
			for (int i = 0; i < fyItems.length; i++) {
				IFYControl item = fyItems[i];
				if (billType.equals(item.getParentBillType()) || billType.equals(item.getDjlxbm())) {
					if(item.isYSControlAble()){
						YsControlVO controlVo = new YsControlVO();
						controlVo.setIscontrary(iscontrary);
						controlVo.setItems(new IFYControl[] { item });
						controlVo.setAdd(isAdd);
						controlVo.setMethodCode(methodFunc);
						resultVector.addElement(controlVo);
					}
				}
			}
			
		}
		result = new YsControlVO[resultVector.size()];
		resultVector.copyInto(result);
		return result;
	}
	
	/**
	 * 获取预算控制VO
	 * 
	 * @param items
	 * @param iscontrary
	 * @param ruleVOs
	 * @return
	 */
	public static YsControlVO[] getCtrlVOs(IFYControl[] items, boolean iscontrary, DataRuleVO[] ruleVOs) {
		return getCtrlVOs(items, null, iscontrary, ruleVOs);
	}
	
	public static IFYControl[] getCostControlVOByCSVO(AggCostShareVO[] csVo,String pk_user) throws BusinessException {
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
		List<String> listPerson = getCorpPerson(corps,pk_user);
		List<String> listDept = getPassDeptPerson(depts.toArray(new String[0]),pk_user);
		List<String> listCenter = getPassCenterPerson(centers.toArray(new String[0]),pk_user);
		
		for (int i = 0; i < csVo.length; i++) {
			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
			CostShareVO headvo = (CostShareVO) aggvo.getParentVO();
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				// 转换生成controlvo
				CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(headvo, dtailvos[j]);
				if (csVo != null && csVo.length == 1) {
					//报销管理节点需要处理
					if (listPerson.contains(dtailvos[j].getAssume_org())) {
						// 联查负责单位的预算执行情况
						itemList.add(cscontrolvo);
						continue;
					}
					if (listDept.contains(dtailvos[j].getAssume_dept())) {
						// 联查负责部门的预算情况
						itemList.add(cscontrolvo);
						continue;
					} 
					
					if (listCenter !=null && listCenter.contains(dtailvos[j].getPk_resacostcenter())) {
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
	
	/*
	 * 是否是跨部门分摊
	 * (分摊公司和费用承担公司相同，分摊信息中分摊公司一致，部门不为null)
	 */
	public static Boolean isSameAssumeDept(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();
		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return false;
		}

		Set<String> deptSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// 部门为空时,返回false;
			if (detailvos[i].getAssume_dept() == null) {
				return false;
			}
			deptSet.add(detailvos[i].getAssume_dept());
		}

		if (deptSet.size() <= 1) {// 部门仅有一个情况下返回false
			return false;
		}
		return true;
	}
	
	// 登陆用户是否为当前公司的费用单位负责人
	public static List<String> getCorpPerson(List<String> corps,String pk_loginUser) {
		List<String> corpList = null;
		try {
			corpList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedCorpOfPerson(corps.toArray(new String[0]), pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return corpList;
	}
	
	// 登陆用户是否为当前登陆公司的部门负责人
	public static List<String> getPassDeptPerson(String[] depts,String pk_loginUser) {
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
	public static List<String> getPassCenterPerson(String[] centers,String pk_loginUser) {
		List<String> centerList = null;
		try {
			centerList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedCenterOfPerson(centers, pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		
		// 费用承担部门过滤
		return centerList;
	}
	
	/*
	 * 是否跨组织分摊
	 * (费用承担公司于分摊公司部同)
	 */
	public static Boolean isSameAssumeOrg(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();
		String fydwbm = ((CostShareVO)vo.getParentVO()).getFydwbm();
		
		if (detailvos == null || detailvos.length == 0) {
			return Boolean.FALSE;
		}
		
		for (int i = 0; i < detailvos.length; i++) {
			if (!fydwbm.equals(detailvos[i].getAssume_org())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	/*
	 * 是否跨成本中心分摊 (成本中心于分摊成本中心部同)
	 */
	public static Boolean isSameCenter(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();

		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return false;
		}

		Set<String> centerSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// 成本中心为空时,返回false;
			if (detailvos[i].getPk_resacostcenter() == null) {
				return false;
			}
			centerSet.add(detailvos[i].getPk_resacostcenter());
		}

		if (centerSet.size() <= 1) {// 部门仅有一个情况下返回false
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否是同公司分摊，如果是同公司分摊，则返回true
	 * @param vo
	 * @return
	 */
	private static UFBoolean isShareAssumeDept(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();
		String fydwbm = ((CostShareVO)vo.getParentVO()).getFydwbm();
		
		if (detailvos == null || detailvos.length == 0) {
			return UFBoolean.FALSE;
		}
		
		for (int i = 0; i < detailvos.length; i++) {
			if (!fydwbm.equals(detailvos[i].getAssume_org())) {
				return UFBoolean.FALSE;
			}
		}
		return UFBoolean.TRUE;
	}
}
