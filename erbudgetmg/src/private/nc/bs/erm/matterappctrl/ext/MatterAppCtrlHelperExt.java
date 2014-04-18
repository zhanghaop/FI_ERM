package nc.bs.erm.matterappctrl.ext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterappctrl.MatterAppCtrlHelper;
import nc.itf.fi.pub.Currency;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

/**
 * 费用申请单控制回写实现辅助类，扩展
 * 
 * @author lvhj
 *
 */
public class MatterAppCtrlHelperExt extends MatterAppCtrlHelper{
	
	/**
	 * 根据比例计算申请单明细行，各行可回写金额
	 * 
	 * @param childrenVO 申请单明细行
	 * @param total_amount 回写总金额
	 * @param busivo 业务数据vo 
	 * @param appPfList 申请单执行记录map 
	 * @return detailExeAmountMap <明细行pk，回写金额>
	 */
	private Map<String, UFDouble> computeMaDetailWrAmount(MtAppDetailVO[] childrenVO,MtappCtrlBusiVO busivo,Map<String, List<MtapppfVO>> appPfMap){
		Map<String, UFDouble> detailExeAmountMap = new HashMap<String, UFDouble>();
		// hash化中间表来源单据的申请单总执行金额情况
		Map<String, UFDouble> srcDetailPfMap = initSrcDetailPfMap(busivo,
				appPfMap);
		
		UFDouble total_amount = busivo.getAmount();
		int ybDecimalDigit = Currency.getCurrDigit(busivo.getCurrency());// 原币精度
		//比例计算总金额
		UFDouble sumAmount = UFDouble.ZERO_DBL;

		// 可回写尾差的申请单明细行,使用有序的map来保证按顺序回写尾差
		Map<String,UFDouble> differDetailAmount = new LinkedHashMap<String, UFDouble>();
		// 可回写尾差的申请单明细行pk，回写预占数时记录最后一行
		String differDetailPk = null;
		
		for (int i = 0; i < childrenVO.length; i++) {
			MtAppDetailVO appDetailVo = childrenVO[i];
			// 按比例回写每行金额
			UFDouble amount = total_amount.multiply(appDetailVo.getShare_ratio()).div(100);
			// 根据业务单据原币精度处理回写的原币精度
			amount = amount.setScale(ybDecimalDigit,BigDecimal.ROUND_UP);
			// 回写执行数时，需控制本行可回写的最大金额
			if(IMtappCtrlBusiVO.DataType_exe.equals(busivo.getDataType())){
				// 中间表可回写的最大值 = 上游单据总回写金额 - 已经释放的执行数金额;业务单据可回写最大值 = 当前申请单明细行最大允许回写金额 - 总执行
				UFDouble usable_amout = srcDetailPfMap.get(appDetailVo.getPrimaryKey());
				if(usable_amout == null){
					// 单据本身回写：可超出申请单的最大值 = 最大可回写值 - 总执行；不可超出申请的最大值 = 申请总金额 - 总执行
					usable_amout = busivo.isExceedEnable()?appDetailVo.getMax_amount().sub(appDetailVo.getExe_amount()):
						appDetailVo.getOrig_amount().sub(appDetailVo.getExe_amount());
				}
				if(amount.compareTo(usable_amout)>0){
					amount = usable_amout;
				}else{
					if(amount.compareTo(UFDouble.ZERO_DBL) <0){
						// 当执行数小于0时，使用相加的方式处理金额比较
						UFDouble usable_rest = amount.add(usable_amout);
						if(usable_rest.compareTo(UFDouble.ZERO_DBL)<0){
							amount = usable_amout.multiply(-1);
						}else{
							// 当前行还剩有余额，可回写尾差
							differDetailAmount.put(appDetailVo.getPrimaryKey(), usable_rest);
						}
					}else{
						// 当前行还剩有余额，可回写尾差
						differDetailAmount.put(appDetailVo.getPrimaryKey(), usable_amout.sub(amount));
					}
				}
			}else{
				// 预占不进行金额的控制，则尾差直接放在最后一行
				differDetailPk = appDetailVo.getPrimaryKey();
			}
			// 回写明细行金额记录
			detailExeAmountMap.put(appDetailVo.getPrimaryKey(), amount);
			// 计算合计金额
			sumAmount = sumAmount.add(amount);
		}
		// 尾差值计算处理
		UFDouble differAmount = total_amount.sub(sumAmount);
		dealDifferAmount(detailExeAmountMap, differDetailAmount,
				differDetailPk, differAmount);
		return detailExeAmountMap;
	}

	/**
	 * 尾差值计算处理
	 * 
	 * 优先按照指定行回写尾差，无指定行情况按各个行的可用余额进行回写尾差
	 * 
	 * @param detailExeAmountMap
	 * @param differDetailAmount
	 * @param differDetailPk
	 * @param differAmount
	 */
	private void dealDifferAmount(Map<String, UFDouble> detailExeAmountMap,
			Map<String, UFDouble> differDetailAmount, String differDetailPk,
			UFDouble differAmount) {
		if(differAmount != null){
			// 优先按照指定行回写尾差，无指定行情况按各个行的可用余额进行回写尾差
			if(differDetailPk != null){
				detailExeAmountMap.put(differDetailPk, detailExeAmountMap.get(differDetailPk).add(differAmount));
			}else{
				// 按顺序逐行回写尾差
				for (Entry<String, UFDouble> differ : differDetailAmount.entrySet()) {
					String key = differ.getKey();
					UFDouble differ_rest_amount = differ.getValue();
					if(differ_rest_amount.compareTo(differAmount)>0){
						if(differAmount.compareTo(UFDouble.ZERO_DBL) <0){
							// 当执行数小于0时，使用相加的方式处理金额比较
							if(differAmount.add(differ_rest_amount).compareTo(UFDouble.ZERO_DBL)<0){
								UFDouble amount = differ_rest_amount.multiply(-1);
								detailExeAmountMap.put(key, detailExeAmountMap.get(key).add(amount));
								differAmount = differAmount.sub(amount);
							}else{
								detailExeAmountMap.put(key, detailExeAmountMap.get(key).add(differAmount));
								break;
							}
						}else{
							detailExeAmountMap.put(key, detailExeAmountMap.get(key).add(differAmount));
							break;
						}
					}else{
						detailExeAmountMap.put(key, detailExeAmountMap.get(key).add(differ_rest_amount));
						differAmount = differAmount.sub(differ_rest_amount);
					}
				}
			}
		}
	}

	/**
	 * 哈希化中间表来源单据
	 * 
	 * @param busivo
	 * @param appPfMap
	 * @return
	 */
	private Map<String, UFDouble> initSrcDetailPfMap(MtappCtrlBusiVO busivo,
			Map<String, List<MtapppfVO>> appPfMap) {
		Map<String, UFDouble> srcDetailPfMap = new HashMap<String, UFDouble>();
		if(appPfMap == null){
			return srcDetailPfMap;
		}
		if(IMtappCtrlBusiVO.DataType_exe.equals(busivo.getDataType())&&!StringUtil.isEmptyWithTrim(busivo.getSrcBusidetailPK())){
			String srcdetailKey = busivo.getMatterAppPK()+busivo.getSrcBusidetailPK();
			for (Entry<String, List<MtapppfVO>> entry : appPfMap.entrySet()) {
				if(entry.getKey().startsWith(srcdetailKey)){
					// 包含中间表上游，及释放上游的全部执行记录。进行计算上游单据当前占用情况
					List<MtapppfVO> list = entry.getValue();
					for (MtapppfVO mtapppfVO : list) {
						String pk_mtapp_detail = mtapppfVO.getPk_mtapp_detail();
						UFDouble temp_amount = srcDetailPfMap.get(pk_mtapp_detail);
						srcDetailPfMap.put(pk_mtapp_detail, mtapppfVO.getExe_amount().add(getDoubleValue(temp_amount)));
					}
				}
			}
		}
		return srcDetailPfMap;
	}
	
	/**
	 * 整单按比例正向回写预占数
	 * 
	 * @param preDataVOs
	 * @param appPk2AppVoMap
	 * @param appDetailPk2VoMap
	 * @param appPfMap
	 * @throws BusinessException 
	 */
	public void writeBackAppVoPreDataByRatio(List<MtappCtrlBusiVO> preDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		for (MtappCtrlBusiVO busivo : preDataVOs) {
			AggMatterAppVO maaggvo = appPk2AppVoMap.get(busivo.getMatterAppPK());
//			MatterAppVO mtappvo = maaggvo.getParentVO();
			MtAppDetailVO[] childrenVO = maaggvo.getChildrenVO();
			if(childrenVO == null || childrenVO.length == 0){
				throw new BusinessException("待回写申请单没有明细行，请检查！");
			}
			
			// 计算各个明细行待回写的金额
			Map<String, UFDouble> detailAmountMap = computeMaDetailWrAmount(childrenVO, busivo,null);
			
			for (MtAppDetailVO appDetailVo : childrenVO) {
				// 获得本身的执行记录
//				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				
				// 按比例回写每行金额
				UFDouble amount = detailAmountMap.get(appDetailVo.getPrimaryKey());
				// 回写明细行预占数
				writeBackDetailAppVo(new UFDouble[]{UFDouble.ZERO_DBL,amount}, appDetailVo, null,appPfMap, busivo, UFBoolean.FALSE);
			}
			
		}
		
	}

	/**
	 * 整单按比例正向回写执行数
	 * 
	 * @param exeDataVOs
	 * @param appPk2AppVoMap
	 * @param appDetailPk2VoMap
	 * @param appPfMap
	 * @throws BusinessException 
	 */
	public void writeBackAppVoExeDataByRatio(List<MtappCtrlBusiVO> exeDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		for (MtappCtrlBusiVO busivo : exeDataVOs) {
			AggMatterAppVO maaggvo = appPk2AppVoMap.get(busivo.getMatterAppPK());
//			MatterAppVO mtappvo = maaggvo.getParentVO();
			MtAppDetailVO[] childrenVO = maaggvo.getChildrenVO();
			if(childrenVO == null || childrenVO.length == 0){
				throw new BusinessException("待回写申请单没有明细行，请检查！");
			}
			// 计算各个明细行待回写的金额
			Map<String, UFDouble> detailAmountMap = computeMaDetailWrAmount(childrenVO, busivo,appPfMap);
			
			for (MtAppDetailVO appDetailVo : childrenVO) {
				// 获得本身的执行记录
//				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				
				// 按比例回写每行金额
				UFDouble amount = detailAmountMap.get(appDetailVo.getPrimaryKey());
				// 回写明细行预占数
				writeBackDetailAppVo(new UFDouble[]{amount,UFDouble.ZERO_DBL}, appDetailVo,null, appPfMap, busivo, UFBoolean.FALSE);
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see nc.bs.erm.matterappctrl.MatterAppCtrlHelper#writeBackDetailAppVo(nc.vo.pub.lang.UFDouble[], nc.vo.erm.matterapp.MtAppDetailVO, nc.vo.erm.matterappctrl.MtapppfVO, nc.vo.erm.matterappctrl.IMtappCtrlBusiVO, nc.vo.pub.lang.UFBoolean)
	 */
	protected UFDouble[] writeBackDetailAppVo(UFDouble[] exe_amount, MtAppDetailVO appDetailVo, MtapppfVO appPfVo,
			Map<String, List<MtapppfVO>> appPfMap, MtappCtrlBusiVO iMtappCtrlBusiVO, UFBoolean isAdjust) throws BusinessException {// 本次回写剩余金额
		// 回写申请单明细行
		if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
	    			getStrByID("201212_0","0201212-0095")/*@res ""待回写的申请单明细行已经关闭，无法回写，请手工取消关闭""*/);
		}
	
		if (exe_amount[0].equals(UFDouble.ZERO_DBL) && exe_amount[1].equals(UFDouble.ZERO_DBL)) {
			return exe_amount;
		}
		
		// 费用申请单余额
		UFDouble rest_amount = getDoubleValue(appDetailVo.getRest_amount());
		if(iMtappCtrlBusiVO.isExceedEnable()){
			rest_amount = appDetailVo.getMax_amount().sub(appDetailVo.getOrig_amount()).add(rest_amount);
		}
		// 业务执行数的金额
		UFDouble busiAmount = getDoubleValue(exe_amount[0]);
		if(busiAmount.compareTo(rest_amount) > 0){
			throw new BusinessException("待回写的申请单明细行余额不足，请确认");
		}
		appDetailVo.setStatus(VOStatus.UPDATED);
		appDetailVo.setExe_amount(getDoubleValue(appDetailVo.getExe_amount()).add(exe_amount[0]));
		appDetailVo.setPre_amount(getDoubleValue(appDetailVo.getPre_amount()).add(exe_amount[1]));

		// 多币种折算及包装执行记录
		if(appPfVo == null){
			appPfVo = getMtapppfVO_new(iMtappCtrlBusiVO, appDetailVo,appPfMap,appDetailVo.getPk_tradetype());
		}
		convertMutiAmount(appDetailVo,iMtappCtrlBusiVO,appPfVo, isAdjust, new UFDouble[]{exe_amount[0],exe_amount[1],UFDouble.ZERO_DBL});
		
		return exe_amount;
	}
	
	/**
	 * 按申请单明细行，正向回写预占数
	 * 
	 * @param preDataVOs
	 * @param appDetailPk2VoMap
	 * @param appPfMap
	 * @throws BusinessException 
	 */
	public void writeBackAppVoPreDataByDetail(List<MtappCtrlBusiVO> preDataVOs,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		
		for (MtappCtrlBusiVO busivo : preDataVOs) {
			
			MtAppDetailVO mtAppDetailVO = appDetailPk2VoMap.get(busivo.getMatterAppDetailPK());
			// 获得本身的执行记录
//			MtapppfVO appPfVo = getMtapppfVO_new(busivo, mtAppDetailVO, appPfMap,mtAppDetailVO.getPk_tradetype());
			
			// 按比例回写每行金额
			UFDouble amount = busivo.getAmount();
			// 回写明细行预占数
			writeBackDetailAppVo(new UFDouble[]{UFDouble.ZERO_DBL,amount}, mtAppDetailVO,null,appPfMap, busivo, UFBoolean.FALSE);
			
		}
		
	}

	/**
	 * 按申请单明细行，正向回写执行数
	 * 
	 * @param exeDataVOs
	 * @param appDetailPk2VoMap
	 * @param appPfMap
	 * @throws BusinessException 
	 */
	public void writeBackAppVoExeDataByDetail(List<MtappCtrlBusiVO> exeDataVOs,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		
		for (MtappCtrlBusiVO busivo : exeDataVOs) {

			MtAppDetailVO mtAppDetailVO = appDetailPk2VoMap.get(busivo
					.getMatterAppDetailPK());
			// 获得本身的执行记录
//			MtapppfVO appPfVo = getMtapppfVO_new(busivo, mtAppDetailVO,
//					appPfMap, mtAppDetailVO.getPk_tradetype());

			// 按比例回写每行金额
			UFDouble amount = busivo.getAmount();
			// 回写明细行预占数
			writeBackDetailAppVo(new UFDouble[] {amount, UFDouble.ZERO_DBL},
					mtAppDetailVO, null,appPfMap, busivo, UFBoolean.FALSE);

		}
		
	}

}