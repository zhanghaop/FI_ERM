package nc.bs.erm.matterapp.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 申请单vo分组工具类
 * 
 * @author lvhj
 *
 */
public class MtappVOGroupHelper {

	/**
	 * 分期分摊生成责任凭证使用。按利润中心+分期日期，分组包装申请单
	 * 
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggMatterAppVO>> groupPcorgVOs(AggMatterAppVO... vos) {
		return groupPcorgVOs(null,null,vos);
	}
	
	/**
	 * 分期分摊生成责任凭证使用。按利润中心+分期日期，分组包装申请单
	 * 
	 * @param pk_pcorg 指定重算利润中心
	 * @param busidate 指定重算业务日期
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggMatterAppVO>> groupPcorgVOs(String pk_pcorg,UFDate busidate,AggMatterAppVO... vos) {
		Map<String, List<AggMatterAppVO>> res_map = new HashMap<String, List<AggMatterAppVO>>();
		for (AggMatterAppVO aggvo : vos) {
			MtappMonthExtVO[] monthvos = (MtappMonthExtVO[]) aggvo.getTableVO(MtappMonthExtVO.getDefaultTableName());
			if(monthvos == null || monthvos.length ==0){
				continue;
			}
			MtAppDetailVO[] childrenVO = aggvo.getChildrenVO();
			Map<String, MtAppDetailVO> detailvoMap = VOUtils.changeCollection2Map(Arrays.asList(childrenVO));
			// 利润中心-分期日期-明细行列表
			Map<String, Map<UFDate, List<MtAppDetailVO>>> pcorg_date_detialmap = new HashMap<String, Map<UFDate,List<MtAppDetailVO>>>();
			for (int i = 0; i < monthvos.length; i++) {
				MtappMonthExtVO monthvo = monthvos[i];
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
				Map<UFDate, List<MtAppDetailVO>> date_detailmap = pcorg_date_detialmap.get(pcorg);
				if(date_detailmap == null){
					date_detailmap = new HashMap<UFDate, List<MtAppDetailVO>>();
					pcorg_date_detialmap.put(pcorg, date_detailmap);
				}
				List<MtAppDetailVO> detaillist = date_detailmap.get(billdate);
				if(detaillist == null){
					detaillist = new ArrayList<MtAppDetailVO>();
					date_detailmap.put(billdate, detaillist);
				}
				MtAppDetailVO detailvo = detailvoMap.get(monthvo.getPk_mtapp_detail());
				MtAppDetailVO v_detailvo = (MtAppDetailVO) detailvo.clone();
				detaillist.add(v_detailvo);
				// 明细行信息,相关字段变更
				v_detailvo.setPk_org(pcorg);//主组织变更为利润中心
				v_detailvo.setBilldate(billdate);// 制单日期变更为分期日期
				v_detailvo.setOrig_amount(monthvo.getOrig_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setOrg_amount(monthvo.getOrg_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setGroup_amount(monthvo.getGroup_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setGlobal_amount(monthvo.getGlobal_amount());// 凭证金额设置为分期均摊金额
				v_detailvo.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);//设置关闭状态为未关闭
			}
			for (Entry<String, Map<UFDate, List<MtAppDetailVO>>> pc_date_entry : pcorg_date_detialmap.entrySet()) {
				String pcorg = pc_date_entry.getKey();// 利润中心
				Map<UFDate, List<MtAppDetailVO>> date_map = pc_date_entry.getValue();
				
				List<AggMatterAppVO> list = new ArrayList<AggMatterAppVO>();
				res_map.put(pcorg, list);
				
				for (Entry<UFDate, List<MtAppDetailVO>> date_entry : date_map.entrySet()) {
					UFDate billdate = date_entry.getKey();// 分期日期
					List<MtAppDetailVO> detaillist = date_entry.getValue();// 分期分摊明细行
					// 包装生成凭证的vo
					AggMatterAppVO v_aggvo = genarateNewAggVO(pcorg, billdate,detaillist,aggvo,ErmMatterAppConst.CLOSESTATUS_N);
					list.add(v_aggvo);
				}
			}
		
		}
		return res_map;
	}


	/**
	 * 分期分摊生成责任凭证使用。按余额负数包装关闭日期当期申请单
	 * 
	 * @param vos
	 * @return
	 */
	public static List<AggMatterAppVO> getCloseVOs(AggMatterAppVO... vos) {
		return getCloseVOs(null,vos);
	}
	
	/**
	 * 分期分摊生成责任凭证使用。按余额负数包装关闭日期当期申请单
	 * 
	 * @param pk_pcorg 利润中心pk，为空值时按照利润中心分组包装
	 * @param vos
	 * @return
	 */
	public static List<AggMatterAppVO> getCloseVOs(String pk_pcorg,AggMatterAppVO... vos) {
		List<AggMatterAppVO> res_list = new ArrayList<AggMatterAppVO>();
		for (AggMatterAppVO aggvo : vos) {
			MatterAppVO parentVO = aggvo.getParentVO();
			if(!isManualClose(parentVO)){
				// 非手工关闭情况，不处理
				continue;
			}
			UFDate closedate = parentVO.getClosedate();
			// 按利润中心分组的申请单明细
			Map<String, List<MtAppDetailVO>> detailmap = new HashMap<String, List<MtAppDetailVO>>();

			MtAppDetailVO[] childrenVO = aggvo.getChildrenVO();
			if(childrenVO != null && childrenVO.length >0){
				for (MtAppDetailVO detailvo : childrenVO) {
					String pcorg = detailvo.getPk_pcorg();// 利润中心
					if(StringUtil.isEmpty(pk_pcorg)||StringUtil.compare(pk_pcorg, pcorg) == 0){
						// 不按照指定利润中心查询，或者当前行利润中心是指定利润中心，可进行包装
						if(detailvo.getRest_amount().compareTo(UFDouble.ZERO_DBL) <= 0){
							// 余额小于等于0情况，不处理
							continue ;
						}
						List<MtAppDetailVO> list = detailmap.get(pcorg);
						if(list == null){
							list = new ArrayList<MtAppDetailVO>();
							detailmap.put(pcorg, list);
						}
						MtAppDetailVO v_detailvo = (MtAppDetailVO) detailvo.clone();
						list.add(v_detailvo);
						// 变更明细行，相关字段信息
						v_detailvo.setPk_org(pcorg);//主组织设置为利润中心
						v_detailvo.setBilldate(closedate);// 单据日期设置为关闭日期
						v_detailvo.setOrig_amount(v_detailvo.getRest_amount().multiply(-1));// 生成凭证金额设置为余额的负数
						v_detailvo.setOrg_amount(v_detailvo.getOrg_rest_amount().multiply(-1));
						v_detailvo.setGroup_amount(v_detailvo.getGroup_rest_amount().multiply(-1));
						v_detailvo.setGlobal_amount(v_detailvo.getGlobal_rest_amount().multiply(-1));
					}
				}
			}
			
			for (Entry<String, List<MtAppDetailVO>> pc_detail : detailmap.entrySet()) {
				String pcorg = pc_detail.getKey();
				List<MtAppDetailVO> detaillist = pc_detail.getValue();

				// 包装生成凭证的vo
				AggMatterAppVO v_aggvo = genarateNewAggVO(pcorg, closedate,detaillist,aggvo,ErmMatterAppConst.CLOSESTATUS_Y);
				
				res_list.add(v_aggvo);
			}
		}
		return res_list;
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
	private static AggMatterAppVO genarateNewAggVO(String pcorg,
			UFDate billdate, List<MtAppDetailVO> detaillist,AggMatterAppVO aggvo,Integer close_status) {
		// clone聚合vo
		AggMatterAppVO v_aggvo = new AggMatterAppVO();
		MatterAppVO v_parentVO = (MatterAppVO) aggvo.getParentVO().clone();
		v_aggvo.setParentVO(v_parentVO);
		// 重新设置明细行信息
		v_aggvo.setChildrenVO(detaillist.toArray(new MtAppDetailVO[0]));
		// 重新设置表头相关字段信息
//		v_parentVO.setPk_org(pcorg);//主组织设置为利润中心
		v_parentVO.setBilldate(billdate);//单据日期设置为关闭日期
		v_parentVO.setClose_status(close_status);
//		UFDouble[] amounts = computeTotolAmount(detaillist);// 设置表头金额
//		v_parentVO.setOrig_amount(amounts[0]);
//		v_parentVO.setOrg_amount(amounts[1]);
//		v_parentVO.setGroup_amount(amounts[2]);
//		v_parentVO.setGlobal_amount(amounts[3]);
		v_parentVO.setPrimaryKey(v_aggvo.getVoucherKey());// 设置生成凭证的主键
		return v_aggvo;
	}

//	/**
//	 * 计算明细行合计金额
//	 * 
//	 * @param detaillist
//	 * @return
//	 */
//	private static UFDouble[] computeTotolAmount(List<MtAppDetailVO> detaillist){
//		UFDouble[] amounts = new UFDouble[4];
//		for (MtAppDetailVO v_detailvo : detaillist) {
//			// 计算利润中心分组的合计值
//			amounts[0] = UFDoubleTool.sum(amounts[0], v_detailvo.getOrig_amount());
//			amounts[1] = UFDoubleTool.sum(amounts[1], v_detailvo.getOrg_amount());
//			amounts[2] = UFDoubleTool.sum(amounts[2], v_detailvo.getGroup_amount());
//			amounts[3] = UFDoubleTool.sum(amounts[3], v_detailvo.getGlobal_amount());
//		}
//		return amounts;
//	}
	/**
	 * 是否手工关闭
	 * 
	 * @param parentVO
	 * @return
	 */
	private static boolean isManualClose(MatterAppVO parentVO){
		return parentVO.getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y &&
		parentVO.getRest_amount().compareTo(UFDouble.ZERO_DBL)>0;
	}
	
}
