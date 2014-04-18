package nc.bs.erm.util;

import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;

/**
 * 预算工具类
 * 
 * @author chenshuaia
 */
public class ErBudgetUtil {

	/**
	 * 查询单据类型/交易类型，最终执行预算的控制策略
	 * 
	 * @param pk_billtype
	 * @return
	 * @throws BusinessException 
	 */
	public static DataRuleVO getBillYsExeDataRule(String pk_billtype) throws BusinessException{
		// 查询审批生效
		DataRuleVO[] bxruleVos = NCLocator
				.getInstance()
				.lookup(IBudgetControl.class)
				.queryControlTactics(pk_billtype,
						BXConstans.ERM_NTB_APPROVE_KEY, false);
		
		if (bxruleVos == null || bxruleVos.length == 0) {
			// 防止只在保存环节配置了控制策略
			bxruleVos = NCLocator
					.getInstance()
					.lookup(IBudgetControl.class)
					.queryControlTactics(pk_billtype,
							BXConstans.ERM_NTB_SAVE_KEY, false);
		}
		return bxruleVos == null || bxruleVos.length == 0? null:bxruleVos[0];
	}
	
	/**
	 * 根据上游单据类型/交易类型，当前单据预算动作控制策略，获得上游单据的预算控制策略
	 * 注：当前预算控制策略配置时，一个动作只能配置一个上游控制策略，所以billrules最大长度是2
	 * 
	 * @param src_billtype
	 * @param billrules
	 * @return
	 * @throws BusinessException 
	 */
	public static DataRuleVO[] getSrcBillDataRule(String src_billtype,DataRuleVO[] billrules) throws BusinessException{
		
		DataRuleVO srcrule = getBillYsExeDataRule(src_billtype);
		
		if(srcrule == null || billrules == null || billrules.length == 0){
			// 上游单据没有控制策略或者当前单据没有预算控制策略情况，不走上游单据预算
			return null;
		}
		boolean isBillExerule = false;
		DataRuleVO billrule = billrules[0];
		if(IFormulaFuncName.UFIND.equals(billrule.getDataType())){
			// 执行数是最终动作
			isBillExerule = true;
		}else{
			DataRuleVO[] ruleVos = NCLocator
			.getInstance()
			.lookup(IBudgetControl.class)
			.queryTakeDataTactics(billrule.getBilltype_code(),IFormulaFuncName.UFIND);
			
			isBillExerule = ruleVos == null || ruleVos.length ==0;
		}
		// clone 下游单据的控制策略，供上游单据使用
		DataRuleVO[] newbillrules = new DataRuleVO[billrules.length];
		for (int i = 0; i < newbillrules.length; i++) {
			newbillrules[i] = (DataRuleVO) billrules[i].clone();
			
			DataRuleVO newdataRuleVO = newbillrules[i];
			newdataRuleVO.setPk_billtype(srcrule.getPk_billtype());
			newdataRuleVO.setBilltype_code(srcrule.getBilltype_code());
			newdataRuleVO.setBilltype_name(srcrule.getBilltype_name());
		}
		// 返回最终的控制策略结果
		if(isBillExerule){
			// 是当前单据最终动作情况，查询上游单据最终预算执行策略
			if(newbillrules.length > 1){
				return new DataRuleVO[]{srcrule,newbillrules[1]};
			}else{
				return new DataRuleVO[]{srcrule};
			}
		}else{
			return newbillrules;
		}
		
	}
	
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
	
//	public static IFYControl[] getCostControlVOByCSVO(AggCostShareVO[] csVo,String pk_user) throws BusinessException {
//		IFYControl[] items;
//		// 查询费用结转主vo
//		List<CostShareYsControlVO> itemList = new ArrayList<CostShareYsControlVO>();
//		List<String> depts = new ArrayList<String>();
//		List<String> corps = new ArrayList<String>();
//		List<String> centers = new ArrayList<String>();
//		for (int i = 0; i < csVo.length; i++) {
//			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
//			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
//			for (int j = 0; j < dtailvos.length; j++) {
//				if (dtailvos[j].getAssume_dept() != null) {
//					depts.add(dtailvos[j].getAssume_dept());
//				}
//				if (dtailvos[j].getPk_resacostcenter() != null) {
//					centers.add(dtailvos[j].getPk_resacostcenter());
//				}
//				corps.add(dtailvos[j].getAssume_org());
//			}
//		}
//		List<String> listPerson = getCorpPerson(corps,pk_user);
//		List<String> listDept = getPassDeptPerson(depts.toArray(new String[0]),pk_user);
//		List<String> listCenter = getPassCenterPerson(centers.toArray(new String[0]),pk_user);
//		
//		for (int i = 0; i < csVo.length; i++) {
//			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
//			CostShareVO headvo = (CostShareVO) aggvo.getParentVO();
//			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
//			for (int j = 0; j < dtailvos.length; j++) {
//				// 转换生成controlvo
//				CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(headvo, dtailvos[j]);
//				if (csVo != null && csVo.length == 1) {
//					//报销管理节点需要处理
//					if (listPerson.contains(dtailvos[j].getAssume_org())) {
//						// 联查负责单位的预算执行情况
//						itemList.add(cscontrolvo);
//						continue;
//					}
//					if (listDept.contains(dtailvos[j].getAssume_dept())) {
//						// 联查负责部门的预算情况
//						itemList.add(cscontrolvo);
//						continue;
//					} 
//					
//					if (listCenter !=null && listCenter.contains(dtailvos[j].getPk_resacostcenter())) {
//						// 联查负责成本中心的预算执行情况
//						itemList.add(cscontrolvo);
//						continue;
//					}
//				}
//			}
//		}
//		items = itemList.toArray(new IFYControl[0]);
//		return items;
//	}
}
