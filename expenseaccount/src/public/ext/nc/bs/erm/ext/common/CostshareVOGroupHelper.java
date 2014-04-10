package nc.bs.erm.ext.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;

/**
 * 经销商垫付结转单vo分组工具类
 * 
 * @author lvhj
 *
 */
public class CostshareVOGroupHelper {

	/**
	 * 分期分摊生成责任凭证使用。按利润中心+分期日期，分组包装分摊记录
	 * 
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggCostShareVO>> groupPcorgVOs(AggCostShareVO... vos) {
		return groupPcorgVOs(null, null, vos);
	}
	
	/**
	 * 分期分摊生成责任凭证使用。按利润中心+分期日期，分组包装分摊记录
	 * 
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggCostShareVO>> groupPcorgVOs(String pk_pcorg,UFDate busidate,AggCostShareVO... vos) {
		Map<String, List<AggCostShareVO>> res_map = new HashMap<String, List<AggCostShareVO>>();
		for (AggCostShareVO aggvo : vos) {
			CShareMonthVO[] monthvos = (CShareMonthVO[]) aggvo.getTableVO(CShareMonthVO.getDefaultTableName());
			if(monthvos == null || monthvos.length ==0){
				continue;
			}
			CircularlyAccessibleValueObject[] childrenVO = aggvo.getChildrenVO();
			Map<String, CircularlyAccessibleValueObject> detailvoMap = VOUtils.changeCollection2Map(Arrays.asList(childrenVO));
			// 利润中心-分期日期-明细行列表
			Map<String, Map<UFDate, List<CShareDetailVO>>> pcorg_date_detialmap = new HashMap<String, Map<UFDate,List<CShareDetailVO>>>();
			for (int i = 0; i < monthvos.length; i++) {
				CShareMonthVO monthvo = monthvos[i];
				String pcorg = monthvo.getPk_pcorg();
				if(!(StringUtil.isEmpty(pk_pcorg)||StringUtil.compare(pk_pcorg, pcorg) == 0)){
					// 无指定利润中心，或者指定利润中心是匹配当前行利润中心，才进行包装
					continue;
				}
				UFDate billdate = monthvo.getBilldate();
				if(!(busidate==null||busidate.equals(billdate))){
					// 无指定业务日期，或者指定业务日期是匹配当前行业务日期，才进行包装
					continue;
				}
				
				// pcorg_date_detialmap维护
				Map<UFDate, List<CShareDetailVO>> date_detailmap = pcorg_date_detialmap.get(pcorg);
				if(date_detailmap == null){
					date_detailmap = new HashMap<UFDate, List<CShareDetailVO>>();
					pcorg_date_detialmap.put(pcorg, date_detailmap);
				}
				List<CShareDetailVO> detaillist = date_detailmap.get(billdate);
				if(detaillist == null){
					detaillist = new ArrayList<CShareDetailVO>();
					date_detailmap.put(billdate, detaillist);
				}
				CShareDetailVO detailvo = (CShareDetailVO) detailvoMap.get(monthvo.getPk_cshare_detail());
				CShareDetailVO v_detailvo = (CShareDetailVO) detailvo.clone();
				detaillist.add(v_detailvo);
				// 明细行信息,相关字段变更
				v_detailvo.setPk_org(pcorg);//主组织变更为利润中心
				v_detailvo.setAssume_amount(monthvo.getOrig_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setBbje(monthvo.getOrg_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setGroupbbje(monthvo.getGroup_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setGlobalbbje(monthvo.getGlobal_amount());// 凭证金额设置为分期均摊金额
			}
			for (Entry<String, Map<UFDate, List<CShareDetailVO>>> pc_date_entry : pcorg_date_detialmap.entrySet()) {
				String pcorg = pc_date_entry.getKey();// 利润中心
				Map<UFDate, List<CShareDetailVO>> date_map = pc_date_entry.getValue();
				
				List<AggCostShareVO> list = new ArrayList<AggCostShareVO>();
				res_map.put(pcorg, list);
				
				for (Entry<UFDate, List<CShareDetailVO>> date_entry : date_map.entrySet()) {
					UFDate billdate = date_entry.getKey();// 分期日期
					List<CShareDetailVO> detaillist = date_entry.getValue();// 分期分摊明细行
					// 包装生成凭证的vo
					AggCostShareVO v_aggvo = genarateNewAggVO(pcorg, billdate,detaillist,aggvo);
					list.add(v_aggvo);
				}
			}
		
		}
		return res_map;
	}



	/**
	 * 包装生成凭证的vo
	 * 
	 * @param pcorg
	 * @param billdate
	 * @param detaillist
	 * @param aggvo
	 * @param close_status 关闭状态
	 * @return
	 */
	private static AggCostShareVO genarateNewAggVO(String pcorg,
			UFDate billdate, List<CShareDetailVO> detaillist,AggCostShareVO aggvo) {
		// clone聚合vo
		AggCostShareVO v_aggvo = new AggCostShareVO();
		CostShareVO v_parentVO = (CostShareVO) aggvo.getParentVO().clone();
		v_aggvo.setParentVO(v_parentVO);
		// 重新设置明细行信息
		v_aggvo.setChildrenVO(detaillist.toArray(new CShareDetailVO[0]));
		// 重新设置表头相关字段信息
//		v_parentVO.setPk_org(pcorg);//主组织设置为利润中心
		v_parentVO.setBilldate(billdate);//单据日期重新设置
		// FIXME 使用计算属性来进行替换这里自定义项的应用
		v_parentVO.setDefitem3("RA00");//设置目标单据为责任凭证
//		UFDouble[] amounts = computeTotolAmount(detaillist);// 设置表头金额
//		v_parentVO.setTotal(amounts[0]);
//		v_parentVO.setYbje(amounts[0]);
//		v_parentVO.setBbje(amounts[1]);
//		v_parentVO.setGroupbbje(amounts[2]);
//		v_parentVO.setGlobalbbje(amounts[3]);
		v_parentVO.setPrimaryKey(v_aggvo.getVoucherKey());// 设置生成凭证的主键
		return v_aggvo;
	}

//	/**
//	 * 计算明细行合计金额
//	 * 
//	 * @param detaillist
//	 * @return
//	 */
//	private static UFDouble[] computeTotolAmount(List<CShareDetailVO> detaillist){
//		UFDouble[] amounts = new UFDouble[4];
//		for (CShareDetailVO v_detailvo : detaillist) {
//			// 计算利润中心分组的合计值
//			amounts[0] = UFDoubleTool.sum(amounts[0], v_detailvo.getAssume_amount());
//			amounts[1] = UFDoubleTool.sum(amounts[1], v_detailvo.getBbje());
//			amounts[2] = UFDoubleTool.sum(amounts[2], v_detailvo.getGroupbbje());
//			amounts[3] = UFDoubleTool.sum(amounts[3], v_detailvo.getGlobalbbje());
//		}
//		return amounts;
//	}

	
}
