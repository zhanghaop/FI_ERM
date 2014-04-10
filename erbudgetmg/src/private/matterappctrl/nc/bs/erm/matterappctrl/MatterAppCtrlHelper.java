package nc.bs.erm.matterappctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.itf.uap.pf.IPFConfig;
import nc.vo.er.exception.ErmMaCtrlException;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappbillpfVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.change.ExchangeRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

import org.apache.commons.lang.ArrayUtils;

public class MatterAppCtrlHelper {
	
	/**
	 * 控制维度中在VO对照中的对照
	 * Map<ma_tradetype+busi_tradetype+pk_org, Map<ctrlfield, List<busifield>>>
	 */
	private Map<String, Map<String, List<String>>> ctrlFiledMap;

	/**
	 * 申请单刚性控制维度可用余额map，供预占数的执行记录费用金额计算使用
	 */
	private Map<String, UFDouble> appFieldSum;

	
	/**
	 * 根据交易类型和组织分组形成key
	 *
	 * @param matterAppVOs
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public Set<String[]> getOrgTradeTypeKeyList(AggMatterAppVO[] matterAppVOs) {
		Set<String[]> keySet = new HashSet<String[]>();
		for (AggMatterAppVO aggMatterAppVO : matterAppVOs) {
			keySet.add(new String[] { aggMatterAppVO.getParentVO().getPk_org(), aggMatterAppVO.getParentVO().getPk_tradetype() });
		}
		return keySet;
	}

	/**
	 * 根据维度分组构建业务数据map
	 *
	 * @param unAdjustBusiVoMap
	 * @param busiVo
	 * @param key
	 * @author: wangyhh@ufida.com.cn
	 */
	public <T> void constructBusiVoMap(Map<String, List<T>> unAdjustBusiVoMap, T busiVo, String key) {
		if (unAdjustBusiVoMap.containsKey(key)) {
			unAdjustBusiVoMap.get(key).add(busiVo);
		} else {
			List<T> list = new ArrayList<T>();
			list.add(busiVo);
			unAdjustBusiVoMap.put(key, list);
		}
	}

	/**
	 * 合计业务数据执行数
	 *
	 * @param fieldSum
	 * @param busiVo
	 * @param key
	 * @author: wangyhh@ufida.com.cn
	 */
	public void calculateExeData(Map<String, UFDouble> fieldSum,MtappCtrlBusiVO busiVo, String key) {
		UFDouble exeData = getDoubleValue(busiVo.getExeData());

		if (fieldSum.containsKey(key)) {
			UFDouble sum = fieldSum.get(key);
			sum = sum.add(exeData);
			fieldSum.put(key, sum);
		} else {
			fieldSum.put(key, exeData);
		}
	}

	/**
	 * 合计费用申请单余额
	 *
	 * @param appFieldSum
	 * @param mtAppDetailVO
	 * @param key
	 * @author: wangyhh@ufida.com.cn
	 */
	public void calculateRestData(Map<String, UFDouble> appFieldSum, MtAppDetailVO mtAppDetailVO, String key) {
		UFDouble sumData = appFieldSum.get(key);
		if (sumData == null) {
			sumData = UFDouble.ZERO_DBL;
		}

		sumData = sumData.add(getDoubleValue(mtAppDetailVO.getRest_amount()));
		appFieldSum.put(key, sumData);
	}
	public void calculateMaxRestData(Map<String, UFDouble> appFieldSum, MtAppDetailVO mtAppDetailVO, String key) {
		UFDouble sumData = appFieldSum.get(key);
		if (sumData == null) {
			sumData = UFDouble.ZERO_DBL;
		}
		UFDouble rest_amount = mtAppDetailVO.getMax_amount() == null?getDoubleValue(mtAppDetailVO.getRest_amount())
				:mtAppDetailVO.getMax_amount().sub(getDoubleValue(mtAppDetailVO.getExe_amount()));
		sumData = sumData.add(rest_amount);
		appFieldSum.put(key, sumData);
	}

	public UFDouble getDoubleValue(Object d) {
		return d == null ? UFDouble.ZERO_DBL : (UFDouble) d;
	}

	/**
	 * 根据刚性字段拼接key fieldcode+fieldValue+……+appPk+pk_org 未设置控制维度，整单控制，取appPk为key
	 *
	 * @param key2CtrlFieldVosMap
	 * @param vo
	 * @param orgTradeTypekey
	 * @param appPk
	 * @param isAllFiled
	 * @param mattParanrVo兼容费用申请单表头字段维度控制;业务数据传null即可
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 * @throws BusinessException 
	 */
	public String getFieldKey(List<MtappCtrlfieldVO> ctrlFieldList, Object vo, boolean isAllFiled,MatterAppVO mattParanrVo) throws BusinessException {
		StringBuffer keybuf = new StringBuffer();
		if (ctrlFieldList != null) {
			for (MtappCtrlfieldVO mtappCtrlfieldVO : ctrlFieldList) {
				// 刚性维度字段拼接
				if (isAllFiled || (!mtappCtrlfieldVO.getAdjust_enable().booleanValue())) {
					keybuf.append(mtappCtrlfieldVO.getFieldcode());
					keybuf.append(getAttributeValue(vo, mtappCtrlfieldVO.getFieldcode(),mattParanrVo));
				}
			}
		}

		keybuf.append(mattParanrVo.getPrimaryKey());
//		keybuf.append(getAttributeValue(vo, "pk_org"));暂不区分组织

		return keybuf.toString();
	}

	/**
	 * 区分类型获取字段值
	 *
	 * @param vo
	 * @param keybuf
	 * @param attr
	 * @param mattParanrVo
	 * @author: wangyhh@ufida.com.cn
	 * @throws BusinessException 
	 */
	private Object getAttributeValue(Object vo, String attr,MatterAppVO mattParanrVo) throws BusinessException {
		if (vo instanceof IMtappCtrlBusiVO) {
			IMtappCtrlBusiVO busivo = (IMtappCtrlBusiVO) vo;
			
			List<String> srcattr = null;
			String ctrlFiledMapKey = getCtrlFiledMapKey(mattParanrVo, ((IMtappCtrlBusiVO) vo).getTradeType());
			if (ctrlFiledMapKey != null) {
				Map<String, List<String>> voKeyMap = ctrlFiledMap.get(ctrlFiledMapKey);
				srcattr = voKeyMap.get(attr);
			}
			
			if(srcattr == null || srcattr.isEmpty()){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0118")/*@res ""控制维度设置的字段必须设置维度对照""*/);
			}
			return busivo.getAttributeValue(srcattr.toArray(new String[0]));
		} else if (vo instanceof MtAppDetailVO) {
			String[] split = StringUtil.split(attr, ".");
			if(split.length == 1 && mattParanrVo != null){
				return mattParanrVo.getAttributeValue(attr);
			}
			return ((MtAppDetailVO) vo).getAttributeValue(split[1]);
		} else {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0039")/*@res "不支持的类型"*/);
		}
	}

	/**
	 * 一次回写，先考虑反向回写，再全部维度回写，最后调剂
	 * 
	 * @param key2CtrlFieldVosMap
	 * @param appPk2AppVoMap
	 * @param allAdjustAppVoMap
	 * @param appPfMap
	 * @param unAdjustAppList
	 * @param busiList
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private void writeBackDetail_new(AggMatterAppVO aggMatterAppVO, Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, List<MtapppfVO>> appPfMap,List<MtAppDetailVO> unAdjustAppList, List<MtappCtrlBusiVO> busiList) throws BusinessException {
		//需要调剂的业务数据
		List<MtappCtrlBusiVO> adjBusiList = new ArrayList<MtappCtrlBusiVO>(); 
//		MatterAppVO mtappvo = aggMatterAppVO.getParentVO();
		
		for (MtappCtrlBusiVO busivo : busiList) {
			// 业务金额      
			UFDouble exe_amount = getDoubleValue(busivo.getExeData());
			UFDouble pre_amount = getDoubleValue(busivo.getPreData());
			// 未处理业务金额
			UFDouble[] remaindAmount = new UFDouble[]{exe_amount,pre_amount,UFDouble.ZERO_DBL};
			if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0) {
				// 全部维度匹配
				String allFieldKey = busivo.getAllFieldKey();
				List<MtAppDetailVO> allFieldAppVoList = allAdjustAppVoMap.get(allFieldKey);
				if (allFieldAppVoList != null) {
					for (MtAppDetailVO appDetailVo : allFieldAppVoList) {
						if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
							continue;
						}

//						MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo,appPfMap,mtappvo.getPk_tradetype());
//						// 设置上游为本单据释放的金额
//						remaindAmount[2] = extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getDetailBusiPK());
						// 回写明细行
						remaindAmount = writeBackDetailAppVo(remaindAmount, appDetailVo, null,appPfMap, busivo, UFBoolean.FALSE);
						if (UFDouble.ZERO_DBL.equals(remaindAmount[0]) && UFDouble.ZERO_DBL.equals(remaindAmount[1])) {
							break;
						}
					}
				}
			}

			// 准备调剂数据
			if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0) {
				busivo.setExe_data(remaindAmount[0]);
				busivo.setPre_data(remaindAmount[1]);
				adjBusiList.add(busivo);
			}
		}
		
		//调剂
		if(adjBusiList.size() > 0){
			// 业务金额      
			for (MtappCtrlBusiVO busivo : adjBusiList) {
				UFDouble exe_amount = getDoubleValue(busivo.getExeData());
				UFDouble pre_amount = getDoubleValue(busivo.getPreData());
				// 未处理业务金额
				UFDouble[] remaindAmount = new UFDouble[]{exe_amount,pre_amount,UFDouble.ZERO_DBL};
				// 顺序调剂刚性控制维度
				for (MtAppDetailVO appDetailVo : unAdjustAppList) {
					if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
						continue;
					}

//					MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
					
//					// 设置上游为本单据释放的金额
//					remaindAmount[2] = extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getDetailBusiPK());
					// 回写明细行金额
					remaindAmount = writeBackDetailAppVo(remaindAmount, appDetailVo,null,appPfMap,busivo,UFBoolean.TRUE);
					if (UFDouble.ZERO_DBL.equals(remaindAmount[0]) && UFDouble.ZERO_DBL.equals(remaindAmount[1])) {
						break;
					}
				}
				if (busivo.isExceedEnable()&&(remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0 )) {
					// 超申请情况
					busivo.setExceed(true);
					busivo.setExe_data(remaindAmount[0]);
					busivo.setPre_data(remaindAmount[1]);
					
					String allFieldKey = busivo.getAllFieldKey();
					List<MtAppDetailVO> allFieldAppVoList = allAdjustAppVoMap.get(allFieldKey);
					
					remaindAmount = writeBackDetail_exceed(busivo, aggMatterAppVO, allFieldAppVoList, unAdjustAppList, appPfMap);
				}
				if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0 ) {
					throw new ErmMaCtrlException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/
							,true);
				}
			}
		}
	}
	
	/**
	 * 超申请情况回写，按照允许报销最大金额回写，全部维度回写，最后调剂
	 * 
	 * @param busivo 业务数据
	 * @param aggMatterAppVO 回写的申请单
	 * @param allFieldAppVoList 完全匹配申请单行
	 * @param unAdjustAppList 可调剂申请单行
	 * @param appPfMap 执行记录
	 * @throws BusinessException
	 */
	private UFDouble[] writeBackDetail_exceed(MtappCtrlBusiVO busivo,AggMatterAppVO aggMatterAppVO,
			List<MtAppDetailVO> allFieldAppVoList,List<MtAppDetailVO> unAdjustAppList, 
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		//需要调剂的业务数据
//		MatterAppVO mtappvo = aggMatterAppVO.getParentVO();
		// 超申请业务金额      
		UFDouble exe_amount = getDoubleValue(busivo.getExeData());
		UFDouble pre_amount = getDoubleValue(busivo.getPreData());
		// 未处理业务金额
		UFDouble[] remaindAmount = new UFDouble[]{exe_amount,pre_amount,UFDouble.ZERO_DBL};
		if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0) {
			if (allFieldAppVoList != null) {
				for (MtAppDetailVO appDetailVo : allFieldAppVoList) {
					if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
						continue;
					}

//					MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo,appPfMap,mtappvo.getPk_tradetype());
					// 按允许报销最大金额回写明细行,不处理调剂标识位、费用金额
					remaindAmount = writeBackDetailAppVo(remaindAmount, appDetailVo,null, appPfMap, busivo, UFBoolean.FALSE,UFBoolean.TRUE);
				}
			}
		}

		// 准备调剂数据
		if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0) {
			// 顺序调剂刚性控制维度
			if(unAdjustAppList != null){
				for (MtAppDetailVO appDetailVo : unAdjustAppList) {
					if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
						continue;
					}
					
//					MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
					// 按允许报销最大金额回写明细行金额,不处理调剂标识位、费用金额
					remaindAmount = writeBackDetailAppVo(remaindAmount, appDetailVo,null, appPfMap,busivo,UFBoolean.TRUE,UFBoolean.TRUE);
				}
			}
		}
		
		return remaindAmount;
	}
	
	/**
	 * 非超申请回写费用审批单(预占；执行；余额=金额-执行) 包装费用审批单执行记录
	 * 
	 * 正向回写：业务执行数和费用申请单余额比较，余额不能为负
	 * 反向回写：业务执行数和费用申请单执行数比较，执行数不能为负
	 * 
	 * @param exe_amount
	 *            业务数据{执行金额,预占金额}
	 * @param appDetailVo
	 * @param appPfVo
	 * @param iMtappCtrlBusiVO
	 * @param isAdjust 调剂标志位
	 * @return 业务数据剩余调剂金额
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	protected UFDouble[] writeBackDetailAppVo(UFDouble[] exe_amount, MtAppDetailVO appDetailVo,MtapppfVO appPfVo, Map<String, List<MtapppfVO>> appPfMap,
			MtappCtrlBusiVO iMtappCtrlBusiVO, UFBoolean isAdjust) throws BusinessException {
		return writeBackDetailAppVo(exe_amount, appDetailVo,appPfVo,appPfMap, iMtappCtrlBusiVO, isAdjust, UFBoolean.FALSE);
	}
	
	/**
	 * 回写费用审批单(预占；执行；余额=金额-执行) 包装费用审批单执行记录
	 * 
	 * 正向回写：业务执行数和费用申请单余额比较，余额不能为负
	 * 反向回写：业务执行数和费用申请单执行数比较，执行数不能为负
	 * 
	 * @param exe_amount
	 *            业务数据{执行金额,预占金额}
	 * @param appDetailVo
	 * @param appPfVo
	 * @param iMtappCtrlBusiVO
	 * @param isAdjust 调剂标志位
	 * @param is_exceed 是否超申请回写
	 * @return 业务数据剩余调剂金额
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	protected UFDouble[] writeBackDetailAppVo(UFDouble[] exe_amount, MtAppDetailVO appDetailVo,MtapppfVO appPfVo, Map<String, List<MtapppfVO>> appPfMap,
			MtappCtrlBusiVO iMtappCtrlBusiVO, UFBoolean isAdjust,UFBoolean is_exceed) throws BusinessException {// 本次回写剩余金额
		if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
	    			getStrByID("201212_0","0201212-0095")/*@res ""待回写的申请单明细行已经关闭，无法回写，请手工取消关闭""*/);
		}
	
		//		// 待调剂金额{执行数，预占数}
//		UFDouble[] remaindAmount = new UFDouble[] { UFDouble.ZERO_DBL, UFDouble.ZERO_DBL };
//		// 业务数据可回写金额
//		UFDouble[] enableAmount = exe_amount;
		if (exe_amount[0].equals(UFDouble.ZERO_DBL) && exe_amount[1].equals(UFDouble.ZERO_DBL)) {
			return exe_amount;
		}
		// 业务数据可回写金额
		UFDouble[] enableAmount = new UFDouble[] { UFDouble.ZERO_DBL, UFDouble.ZERO_DBL,UFDouble.ZERO_DBL };
		// 预占数直接全部回写到当前明细行
		enableAmount[0] = exe_amount[0];
		enableAmount[1] = exe_amount[1];
		enableAmount[2] = exe_amount.length ==3 ? exe_amount[2]:UFDouble.ZERO_DBL;
		
		exe_amount[1] = UFDouble.ZERO_DBL;
		
//		if(isAdjust.booleanValue()){
//			//调剂不回写预占数
//			enableAmount[1] = UFDouble.ZERO_DBL;
//			remaindAmount[1] = exe_amount[1];
//		}
		
		// 费用申请单余额
		UFDouble rest_amount = appDetailVo.getRest_amount();
		UFDouble appRest_value = getDoubleValue(rest_amount);
		if(is_exceed.booleanValue()&&appDetailVo.getMax_amount()!=null ){
			// 超申请情况下，按照允许报销最大金额回写
			appRest_value = appRest_value.add(appDetailVo.getMax_amount().sub(appDetailVo.getOrig_amount()));
//			Logger.debug(appDetailVo.getOrig_amount()+"允许超出金额："+appRest_value);
		}
		// 余额小于0情况，按0处理
		appRest_value = appRest_value.getDouble() < 0 ? UFDouble.ZERO_DBL : appRest_value;
		// 费用申请单执行数
		UFDouble appExe_value = getDoubleValue(appDetailVo.getExe_amount());
		
		// 业务执行数的金额
		UFDouble busiAmount = exe_amount[0];
		if(busiAmount.getDouble() >= 0){
			//正向回写,余额不能为负
			UFDouble detailexe_amount = busiAmount.compareTo(appRest_value) >0?appRest_value:busiAmount;
			enableAmount[0] = detailexe_amount;
			exe_amount[0] = busiAmount.sub(detailexe_amount);
			
		}else{
			//FIXME 反向回写,执行数不能为负
			if(busiAmount.add(appExe_value).doubleValue() < 0){
				enableAmount[0] = appExe_value.multiply(-1);
				exe_amount[0] = busiAmount.sub(appExe_value.multiply(-1));
			}else{
				exe_amount[0] = UFDouble.ZERO_DBL;
			}
		}
		if (enableAmount[0].equals(UFDouble.ZERO_DBL) && enableAmount[1].equals(UFDouble.ZERO_DBL)) {
			return exe_amount;
		}

		appDetailVo.setStatus(VOStatus.UPDATED);
		appDetailVo.setExe_amount(getDoubleValue(appDetailVo.getExe_amount()).add(enableAmount[0]));
		appDetailVo.setPre_amount(getDoubleValue(appDetailVo.getPre_amount()).add(enableAmount[1]));

		// 多币种折算及包装执行记录
		if(appPfVo == null){
			appPfVo = getMtapppfVO_new(iMtappCtrlBusiVO, appDetailVo,appPfMap,appDetailVo.getPk_tradetype());
		}
		
		convertMutiAmount(appDetailVo, iMtappCtrlBusiVO,appPfVo, isAdjust, enableAmount);
		// 处理刚性维度可用余额的数值
		if(appFieldSum != null){
			
			if(IMtappCtrlBusiVO.DataType_pre.equals(iMtappCtrlBusiVO.getDataType())
					&&IMtappCtrlBusiVO.Direction_negative == iMtappCtrlBusiVO.getDirection()
					&&StringUtil.isEmpty(iMtappCtrlBusiVO.getSrcBusidetailPK())){
				// 业务数据反向回写预占数情况，不影响可使用余额
				// do nothing
			}else if(IMtappCtrlBusiVO.DataType_pre.equals(iMtappCtrlBusiVO.getDataType())
					&&IMtappCtrlBusiVO.Direction_positive == iMtappCtrlBusiVO.getDirection()
					&&!StringUtil.isEmpty(iMtappCtrlBusiVO.getSrcBusidetailPK())){
				// 中间表数据正向回写预占数情况（比如取消冲借款），不影响可使用余额
				// do nothing
			}else{
				// 预占数、执行数都会直接影响业务数据费用金额的占用情况
				UFDouble temp_amount = appFieldSum.get(appDetailVo.getUnAdjustKey());
				temp_amount = temp_amount.sub(enableAmount[0]).sub(enableAmount[1]);
				appFieldSum.put(appDetailVo.getUnAdjustKey(), temp_amount);
			}
		}
		return exe_amount;
	}

	/**
	 * 多币种折算及包装执行记录
	 * 
	 * @param appDetailVo
	 * @param appPfVo
	 * @param iMtappCtrlBusiVO
	 * @param isAdjust
	 * @param enableAmount
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	protected void convertMutiAmount(MtAppDetailVO appDetailVo, MtappCtrlBusiVO iMtappCtrlBusiVO,MtapppfVO appPfVo,
			UFBoolean isAdjust, UFDouble[] enableAmount) throws BusinessException {
		
		// 折算汇率计算，按照申请单本身处理
		String pk_group = appDetailVo.getPk_group();
		String pk_org = appDetailVo.getAssume_org();
		UFDate billDate = appDetailVo.getBilldate();
 		String currency = appDetailVo.getPk_currtype();
		UFDouble[] currInfo = new UFDouble[]{appDetailVo.getOrg_currinfo(),appDetailVo.getGroup_currinfo(),appDetailVo.getGlobal_currinfo()};
		
		// 计算执行记录总费用金额，且根据当前业务数据最新汇率进行折算本币
		UFDouble totalExeamount = getDoubleValue(appPfVo.getExe_amount()).add(getDoubleValue(enableAmount[0]));
		UFDouble totalPreamount = getDoubleValue(appPfVo.getPre_amount()).add(getDoubleValue(enableAmount[1]));
		UFDouble fy_amount = totalExeamount.add(totalPreamount);
		// 费用原币 = 单据总执行数  与 （单据当前余额+已经被执行的执行数+上游释放的金额） 的小值
		UFDouble last_rest_amount = null;
		if(appFieldSum != null && IMtappCtrlBusiVO.DataType_pre.equals(iMtappCtrlBusiVO.getDataType())
				&&IMtappCtrlBusiVO.Direction_positive == iMtappCtrlBusiVO.getDirection()
				&&StringUtil.isEmpty(iMtappCtrlBusiVO.getSrcBusidetailPK())){
			//业务单据根据规则回写、正向占用预占数情况，在刚性维度可用全部余额的范围内进行计算费用金额
			UFDouble rest_amount = appFieldSum.get(appDetailVo.getUnAdjustKey());
			last_rest_amount = rest_amount.compareTo(UFDouble.ZERO_DBL)<0?UFDouble.ZERO_DBL:rest_amount;
		}else{
			UFDouble rest_amount = getDoubleValue(appDetailVo.getRest_amount()).compareTo(UFDouble.ZERO_DBL)<0?UFDouble.ZERO_DBL:
				getDoubleValue(appDetailVo.getRest_amount());
			last_rest_amount = rest_amount.add(getDoubleValue(appPfVo.getFy_amount())).add(getDoubleValue(enableAmount[2]));
		}
		fy_amount = fy_amount.compareTo(last_rest_amount)>0?last_rest_amount:fy_amount;
		appPfVo.setFy_amount(fy_amount);
		UFDouble[] fyAmounts;
		try {
			fyAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency, appPfVo.getFy_amount(), currInfo);
			appPfVo.setOrg_fy_amount(getDoubleValue(fyAmounts[1]));
			appPfVo.setGroup_fy_amount(getDoubleValue(fyAmounts[2]));
			appPfVo.setGlobal_fy_amount(getDoubleValue(fyAmounts[3]));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// 计算执行记录总执行数，且根据当前业务数据最新汇率进行折算本币
		appPfVo.setExe_amount(totalExeamount);
		UFDouble[] pfexeAmounts = null;
		try {
			pfexeAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency,appPfVo.getExe_amount(), currInfo);
			appPfVo.setOrg_exe_amount(getDoubleValue(pfexeAmounts[1]));
			appPfVo.setGroup_exe_amount(getDoubleValue(pfexeAmounts[2]));
			appPfVo.setGlobal_exe_amount(getDoubleValue(pfexeAmounts[3]));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		// 计算执行记录总预占数，且根据当前业务数据最新汇率进行折算本币
		appPfVo.setPre_amount(totalPreamount);
		UFDouble[] pfpreAmounts;
		try {
			pfpreAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency, appPfVo.getPre_amount(), currInfo);
			appPfVo.setOrg_pre_amount(getDoubleValue(pfpreAmounts[1]));
			appPfVo.setGroup_pre_amount(getDoubleValue(pfpreAmounts[2]));
			appPfVo.setGlobal_pre_amount(getDoubleValue(pfpreAmounts[3]));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// 计算申请单明细行执行数、预占数、余额，折算对应本币
		try {
			UFDouble[] exeAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency, enableAmount[0], currInfo);
			appDetailVo.setOrg_exe_amount(getDoubleValue(appDetailVo.getOrg_exe_amount()).add(exeAmounts[1]));
			appDetailVo.setGroup_exe_amount(getDoubleValue(appDetailVo.getGroup_exe_amount()).add(exeAmounts[2]));
			appDetailVo.setGlobal_exe_amount(getDoubleValue(appDetailVo.getGlobal_exe_amount()).add(exeAmounts[3]));
			
			UFDouble[] preAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency, enableAmount[1], currInfo);
			appDetailVo.setOrg_pre_amount(getDoubleValue(appDetailVo.getOrg_pre_amount()).add(preAmounts[1]));
			appDetailVo.setGroup_pre_amount(getDoubleValue(appDetailVo.getGroup_pre_amount()).add(preAmounts[2]));
			appDetailVo.setGlobal_pre_amount(getDoubleValue(appDetailVo.getGlobal_pre_amount()).add(preAmounts[3]));
			
			// 计算余额=总金额-总执行
			UFDouble amount = getDoubleValue(appDetailVo.getOrig_amount()).sub(getDoubleValue(appDetailVo.getExe_amount()));
			appDetailVo.setRest_amount(amount);
			appDetailVo.setOrg_rest_amount(getDoubleValue(appDetailVo.getOrg_amount()).sub(getDoubleValue(appDetailVo.getOrg_exe_amount())));
			appDetailVo.setGroup_rest_amount(getDoubleValue(appDetailVo.getGroup_amount()).sub(getDoubleValue(appDetailVo.getGroup_exe_amount())));
			appDetailVo.setGlobal_rest_amount(getDoubleValue(appDetailVo.getGlobal_amount()).sub(getDoubleValue(appDetailVo.getGlobal_exe_amount())));
			
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		if(isAdjust != null){
			// 超申请情况不更改可调剂标识位
			appPfVo.setIs_adjust(isAdjust);
		}
		
		if (appPfVo.getPrimaryKey() == null) {
			appPfVo.setStatus(VOStatus.NEW);
		}else {
			appPfVo.setStatus(VOStatus.UPDATED);
		}
	}
	

	/**
	 * 构建执行记录map<申请单pk+业务数据明细pk,MtapppfVO>
	 *
	 * @param mtappPks
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public Map<String, List<MtapppfVO>> constructMtappPfMap(String[] mtappPks) throws BusinessException {
		Map<String, List<MtapppfVO>> appPfMap = new HashMap<String, List<MtapppfVO>>();
		if(mtappPks == null || mtappPks.length == 0){
			return appPfMap;
		}
		List<MtapppfVO> mtapppfVOList = queryMtAppPfVos(mtappPks);
		for (MtapppfVO pfVO : mtapppfVOList) {
			String key = pfVO.getPk_matterapp() + pfVO.getBusi_detail_pk();
			List<MtapppfVO> list = appPfMap.get(key);
			if(list == null){
				list = new ArrayList<MtapppfVO>();
				appPfMap.put(key, list);
			}
			list.add(pfVO);
		}
		return appPfMap;
	}
	
	/**
	 * 创建新的执行记录
	 * 
	 * @param iMtappCtrlBusiVO
	 * @param appDetailVo
	 * @param ma_tradetype 
	 * @return
	 */
	private MtapppfVO getNewMtappPfVO(IMtappCtrlBusiVO iMtappCtrlBusiVO,
			MtAppDetailVO appDetailVo, String ma_tradetype) {
		MtapppfVO mtapppfVO = new MtapppfVO();
		String userId = InvocationInfoProxy.getInstance().getUserId();
		mtapppfVO.setPk_matterapp(appDetailVo.getPk_mtapp_bill());
		mtapppfVO.setPk_mtapp_detail(appDetailVo.getPk_mtapp_detail());
		mtapppfVO.setBusisys(iMtappCtrlBusiVO.getBusiSys());
		mtapppfVO.setPk_djdl(iMtappCtrlBusiVO.getpk_djdl());
		mtapppfVO.setPk_billtype(iMtappCtrlBusiVO.getBillType());
		mtapppfVO.setPk_tradetype(iMtappCtrlBusiVO.getTradeType());
		mtapppfVO.setBusi_pk(iMtappCtrlBusiVO.getBusiPK());
		mtapppfVO.setPk_group(iMtappCtrlBusiVO.getPk_group());
		mtapppfVO.setPk_org(iMtappCtrlBusiVO.getPk_org());
		mtapppfVO.setExe_user(userId);
		mtapppfVO.setExe_time(new UFDateTime());
		mtapppfVO.setCreator(userId);
		mtapppfVO.setCreationtime(new UFDateTime());
		mtapppfVO.setBusi_detail_pk(iMtappCtrlBusiVO.getDetailBusiPK());
		mtapppfVO.setMa_tradetype(ma_tradetype);
		return mtapppfVO;
	}

	/**
	 * 查询费用申请单执行记录 根据费用申请单pk
	 *
	 * @param mtappPK
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	@SuppressWarnings("unchecked")
	private List<MtapppfVO> queryMtAppPfVos(String[] mtappPK) throws BusinessException{
		String sql = SqlUtils.getInStr(MtapppfVO.PK_MATTERAPP, mtappPK, true);
		return (List<MtapppfVO>) new BaseDAO().retrieveByClause(MtapppfVO.class, sql);
	}

	/**
	 * 子表币种间折算，主表合计值，自动关闭、自动重启
	 *
	 * @param matterAppVOs
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param jkExeMap 
	 * @param closeMap 
	 * 
	 * @return 返回待重启和关闭的费用申请单
	 */
	public List<AggMatterAppVO>[] setTotalAmount(AggMatterAppVO[] matterAppVOs, Map<String, UFDouble> jkExeMap) throws BusinessException {
		@SuppressWarnings("unchecked")
		List<AggMatterAppVO>[] lists = new ArrayList[2];
		lists[0] = new ArrayList<AggMatterAppVO>();
		lists[1] = new ArrayList<AggMatterAppVO>();
		for (AggMatterAppVO vo : matterAppVOs) {
			MtAppDetailVO[] childrenVOs = vo.getChildrenVO();
			if (ArrayUtils.isEmpty(childrenVOs)) {
				continue;
			}

			// 合计值组 顺序：金额-余额-执行数-预占数（原币，组织，集团，全局）
			UFDouble[] amounts = new UFDouble[12];
			for (int i = 0; i < amounts.length; i++) {
				amounts[i] = UFDouble.ZERO_DBL;
			}

			String[] detailItemName = new String[] { /*MtAppDetailVO.ORIG_AMOUNT, MtAppDetailVO.ORG_AMOUNT, MtAppDetailVO.GROUP_AMOUNT, MtAppDetailVO.GLOBAL_AMOUNT, */
					MtAppDetailVO.REST_AMOUNT, MtAppDetailVO.ORG_REST_AMOUNT, MtAppDetailVO.GROUP_REST_AMOUNT, MtAppDetailVO.GLOBAL_REST_AMOUNT,
					MtAppDetailVO.EXE_AMOUNT, MtAppDetailVO.ORG_EXE_AMOUNT, MtAppDetailVO.GROUP_EXE_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT,
					MtAppDetailVO.PRE_AMOUNT, MtAppDetailVO.ORG_PRE_AMOUNT, MtAppDetailVO.GROUP_PRE_AMOUNT, MtAppDetailVO.GLOBAL_PRE_AMOUNT };

			//关闭状态数
			int closeNum = 0;
			List<MtAppDetailVO> closeList = new ArrayList<MtAppDetailVO>();
			List<MtAppDetailVO> openList = new ArrayList<MtAppDetailVO>();
			for (MtAppDetailVO mtAppDetailVO : childrenVOs) {
				// 折算
//				resetAmountByOriAmount(detailItemName, mtAppDetailVO);

				// 合计
				for (int i = 0; i < amounts.length; i++) {
					UFDouble doubleValue = getDoubleValue(mtAppDetailVO.getAttributeValue(detailItemName[i]));
//					if(MtAppDetailVO.REST_AMOUNT.equals(detailItemName[i]) && doubleValue.compareTo(UFDouble.ZERO_DBL) < 0){
//						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/);
//					}
					amounts[i] = amounts[i].add(doubleValue);
				}

				// 行自动关闭、重启
				autoClose(jkExeMap, closeList,openList, mtAppDetailVO);
				
				//关闭行数
				if(ErmMatterAppConst.CLOSESTATUS_Y == mtAppDetailVO.getClose_status().intValue()){
					closeNum++;
				}
			}

			// 设置合计值
			String[] headItemName = new String[] { /*MatterAppVO.ORIG_AMOUNT, MatterAppVO.ORG_AMOUNT, MatterAppVO.GROUP_AMOUNT, MatterAppVO.GLOBAL_AMOUNT,*/
					MatterAppVO.REST_AMOUNT, MatterAppVO.ORG_REST_AMOUNT, MatterAppVO.GROUP_REST_AMOUNT, MatterAppVO.GLOBAL_REST_AMOUNT,
					MatterAppVO.EXE_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT, MatterAppVO.GROUP_EXE_AMOUNT, MatterAppVO.GLOBAL_EXE_AMOUNT,
					MatterAppVO.PRE_AMOUNT, MatterAppVO.ORG_PRE_AMOUNT, MatterAppVO.GROUP_PRE_AMOUNT, MatterAppVO.GLOBAL_PRE_AMOUNT };
			MatterAppVO parentVO = vo.getParentVO();
			for (int i = 0; i < headItemName.length; i++) {
				parentVO.setAttributeValue(headItemName[i], amounts[i]);
				parentVO.setStatus(VOStatus.UPDATED);
			}

			// 包装重启、关闭的费用申请单数据 
			MatterAppVO parentVOclone = (MatterAppVO) parentVO.clone();
			if(!openList.isEmpty()){
				AggMatterAppVO openvo = new AggMatterAppVO();
				openvo.setParentVO(parentVOclone);
				openvo.setChildrenVO(openList.toArray(new MtAppDetailVO[0]));
				lists[0].add(openvo);
			}
			if(!closeList.isEmpty()){
				AggMatterAppVO closevo = new AggMatterAppVO();
				closevo.setParentVO(parentVOclone);
				closevo.setChildrenVO(closeList.toArray(new MtAppDetailVO[0]));
				lists[1].add(closevo);
			}
//			if(closeNum == childrenVOs.length){
//				parentVO.setClose_status(ErmMatterAppConst.CLOSESTATUS_Y);
//				parentVO.setCloseman(INCSystemUserConst.NC_USER_PK);
//				parentVO.setClosedate(AuditInfoUtil.getCurrentTime().getDate());
//			}else{
//				parentVO.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);
//				parentVO.setCloseman(null);
//				parentVO.setClosedate(null);
//			}
		}
		return lists;
	}

	private void autoClose(Map<String, UFDouble> jkExeMap,
			List<MtAppDetailVO> closeList,List<MtAppDetailVO> openList,
			MtAppDetailVO mtAppDetailVO) {
		if(mtAppDetailVO.getRest_amount().compareTo(UFDouble.ZERO_DBL) <= 0 && getDoubleValue(jkExeMap.get(mtAppDetailVO.getPrimaryKey())).compareTo(UFDouble.ZERO_DBL)==0){
//			mtAppDetailVO.setClose_status(ErmMatterAppConst.CLOSESTATUS_Y);
//			mtAppDetailVO.setCloseman(INCSystemUserConst.NC_USER_PK);
//			mtAppDetailVO.setClosedate(AuditInfoUtil.getCurrentTime().getDate());
			// 申请单明细行被除了借款单外的单据全部占用后自动关闭
			closeList.add((MtAppDetailVO) mtAppDetailVO.clone());
			
			
		}else{
			if(ErmMatterAppConst.CLOSESTATUS_Y == mtAppDetailVO.getClose_status().intValue()&&INCSystemUserConst.NC_USER_PK.equals(mtAppDetailVO.getCloseman())){
//				mtAppDetailVO.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);
//				mtAppDetailVO.setCloseman(null);
//				mtAppDetailVO.setClosedate(null);
				// 自动重启
				openList.add((MtAppDetailVO) mtAppDetailVO.clone());
				
			}
		}
	}
	

	/**
	 * 根据原币折算本币
	 *
	 * @param pk_group 集团
	 * @param pk_org 组织
	 * @param busiDate日期
	 * @param pk_currenType原币币种
	 * @param oriAmount原币金额
	 * @param orgRate 本币汇率
	 * @param groupRate集团汇率
	 * @param globalRate全局汇率
	 * @return [0] 原币金额，[1] 本币金额， [2] 集团本币金额 [3] 全局本币金额
	 * @throws Exception
	 * @author: wangyhh@ufida.com.cn
	 */
	private UFDouble[] getAmountsByOriAmount(String pk_group, String pk_org, UFDate busiDate, String pk_currenType, UFDouble oriAmount, UFDouble[] rates) throws Exception {
		UFDouble orgRate = rates[0];
		UFDouble groupRate = rates[1];
		UFDouble globalRate = rates[2];
		
		UFDouble[] result = new UFDouble[4];

		UFDouble[] jes = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currenType, oriAmount, null, null, null, orgRate, busiDate);
		UFDouble[] groupGlobalMoney = Currency.computeGroupGlobalAmount(jes[0], jes[2], pk_currenType, busiDate, pk_org, pk_group, globalRate, groupRate);

		result[0] = oriAmount;
		result[1] = jes[2];
		result[2] = groupGlobalMoney[0];
		result[3] = groupGlobalMoney[1];

		return result;
	}

	public void synchronizBalance(MtapppfVO[] appPfVos) throws BusinessException{
		if(ArrayUtils.isEmpty(appPfVos)){
			return;
//			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0041")/*@res "同步执行记录异常，请联系管理员"*/);
		}

//		Set<String[]> keySet = new HashSet<String[]>();
//		for (MtapppfVO vo : appPfVos) {
//			keySet.add(new String[]{vo.getPk_mtapp_detail(),vo.getPk_djdl()});
//		}
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (MtapppfVO vo : appPfVos) {
			if(map.get(vo.getPk_djdl()) == null){
				List<String> detailList = new ArrayList<String>();
				detailList.add(vo.getPk_mtapp_detail());
				map.put(vo.getPk_djdl(), detailList);
			}else{
				map.get(vo.getPk_djdl()).add(vo.getPk_mtapp_detail());
			}
		}
		
		StringBuffer sqlBuf = new StringBuffer();
//		for (String[] param : keySet) {
//			if(param == null || param.length != 2 || param[0] == null || param[1] == null){
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0028")/*@res "参数不完整，请联系管理员"*/);
//			}
//			if (sqlBuf.length() != 0) {
//				sqlBuf.append(" or ");
//			}
//			sqlBuf.append(" (" + MtappbillpfVO.PK_MTAPP_DETAIL + " = '" + param[0] + "' AND " + MtappbillpfVO.PK_DJDL  + " = '" + param[1] + "') ");
//		}
		
		
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			if (sqlBuf.length() != 0) {
				sqlBuf.append(" or ");
			}

			sqlBuf.append(" (" + SqlUtils.getInStr(MtappbillpfVO.PK_MTAPP_DETAIL, entry.getValue(),true) + " AND "
					+ MtappbillpfVO.PK_DJDL + " = '" + entry.getKey() + "') ");
		}

		String deleteSql = " DELETE FROM ER_MTAPP_BILLPF WHERE " + sqlBuf.toString();
		String insertSql = " INSERT INTO ER_MTAPP_BILLPF(PK_MATTERAPP,PK_MTAPP_DETAIL,PK_DJDL,BUSISYS,PK_GROUP,PK_ORG,EXE_AMOUNT,PRE_AMOUNT) " +
				" SELECT PK_MATTERAPP,PK_MTAPP_DETAIL,PK_DJDL,BUSISYS,PK_GROUP,PK_ORG,SUM(coalesce(EXE_AMOUNT,0)) AS EXE_AMOUNT,SUM(coalesce(PRE_AMOUNT,0)) AS PRE_AMOUNT FROM ER_MTAPP_PF " +
				" WHERE " + sqlBuf.toString() + " GROUP BY PK_MATTERAPP,PK_MTAPP_DETAIL,PK_DJDL,BUSISYS,PK_GROUP,PK_ORG ";

		new BaseDAO().executeUpdate(deleteSql);
		new BaseDAO().executeUpdate(insertSql);

	}
	
	/**
	 * 回写费用申请单执行数
	 * 
	 * @param exeDataVOs 回写执行数的业务数据
	 * @param appPk2AppVoMap 费用申请单vomap
	 * @param appDetailPk2VoMap 申请单明细行vomap
	 * @param key2CtrlFieldVosMap 费用申请单控制维度
	 * @param appPfMap 业务数据执行记录信息
	 * @param allAdjustAppVoMap 全部匹配维度的申请单明细map
	 * @param unAdjustBusiVoMap 
	 * @param unAdjustAppVoMap 
	 * @throws BusinessException 
	 */
	public void writeBackAppVoExeData(List<MtappCtrlBusiVO>[] exeDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, List<MtappCtrlBusiVO>> unAdjustBusiVoMap, Map<String, List<MtAppDetailVO>> unAdjustAppVoMap)
			throws BusinessException {
	
//		/**
//		 * 下游单据额外可用的申请单明细行金额
//		 * Map<申请单明细行pk+下游单据明细pk，上游释放出的金额(负数)>
//		 */
//		Map<String, UFDouble> extraAmountMap = new HashMap<String, UFDouble>();
		
		// 3、中间表反向回写数据,按照与下游完全匹配行、上游回写顺序，释放上游金额
		writeBackAppVoExeContrastPositive(exeDataVOs, appPk2AppVoMap,
				appPfMap, allAdjustAppVoMap,appDetailPk2VoMap);
		// 4、正向回写数据，需要处理调剂情况
		writeBackAppVoExePositive(unAdjustBusiVoMap,exeDataVOs[3], appPk2AppVoMap,
				 appPfMap, allAdjustAppVoMap,unAdjustAppVoMap);

	}
	
	/**
	 * 反向回写中间表预占数
	 * 
	 * @param preDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param appDetailPk2VoMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoPreContrastPositive(List<MtappCtrlBusiVO>[] preDataVOs, Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, MtAppDetailVO> appDetailPk2VoMap)
			throws BusinessException {
		
		// 按照 申请单pk+业务数据明细pk，分组待正向回写的业务单据
		Map<String, MtappCtrlBusiVO> positiveBusiDataMap = new HashMap<String, MtappCtrlBusiVO>();
		for (MtappCtrlBusiVO busivo : preDataVOs[3]) {
			positiveBusiDataMap.put(busivo.getMatterAppPK()+busivo.getDetailBusiPK(), busivo);
		}
		for (MtappCtrlBusiVO busivo : preDataVOs[2]) {
			// 获得中间表下游单据的完全匹配行记录
//			MatterAppVO mtappvo = appPk2AppVoMap.get(busivo.getMatterAppPK()).getParentVO();
			MtappCtrlBusiVO forwardBusiVO = positiveBusiDataMap.get(busivo.getMatterAppPK()+ busivo.getForwardBusidetailPK());
			List<MtAppDetailVO> mtAppDetailList = null;
			UFDouble[] exe_amount = new UFDouble[]{UFDouble.ZERO_DBL,busivo.getPreData()};
			if(forwardBusiVO == null){
				// 中间表不存在下游情况，回写预占数直接回写到上游回写记录对应第一行
				List<MtapppfVO> srcpflist= appPfMap.get(busivo.getMatterAppPK()+ busivo.getSrcBusidetailPK());
				// 释放上游单据原占用的申请。优先释放下游单据需要占用的行，再按照上游占用申请的顺序进行释放；中间表本身金额是负数
				mtAppDetailList = new ArrayList<MtAppDetailVO>();
				for (MtapppfVO srcmtapppfVO : srcpflist) {
					MtAppDetailVO appDetailVo = appDetailPk2VoMap.get(srcmtapppfVO.getPk_mtapp_detail());
					mtAppDetailList.add(appDetailVo);
				}
			}else{
				// 获取下游单据 完全匹配维度对应的申请单明细行第一行
				// fieldcode+fieldValue+……+appPk+pk_org
				String allFieldKey = forwardBusiVO.getAllFieldKey();
			    mtAppDetailList = allAdjustAppVoMap.get(allFieldKey);
			    if(mtAppDetailList == null || mtAppDetailList.isEmpty()){
			    	throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
			    			getStrByID("201212_0","0201212-0090")/*@res ""业务单据与费用申请单控制维度对照字段值不一致，请修改业务单据""*/);
			    }
			}
			
			for (MtAppDetailVO appDetailVo : mtAppDetailList) {
				if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
					continue;
				}
				// 获得本身的执行记录
//				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				// 回写费用申请单明细行金额
				exe_amount = writeBackDetailAppVo(exe_amount, appDetailVo,null, appPfMap, busivo, UFBoolean.FALSE);
//				// 记录为下游单据释放的金额
//				extraAmountMap.put(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK(),getDoubleValue(busivo.getPreData()).multiply(-1));
				// 预占数直接写到完全匹配行的第一行，写到就结束遍历
				break;
			}
			
			if(exe_amount[1].compareTo(UFDouble.ZERO_DBL) != 0){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
		    			getStrByID("201212_0","0201212-0095")/*@res ""待回写的申请单明细行已经关闭，无法回写，请手工取消关闭""*/);
			}
		}
	}
	
	/**
	 * 反向回写中间表执行数
	 * 
	 * @param exeDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param appDetailPk2VoMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoExeContrastPositive(List<MtappCtrlBusiVO>[] exeDataVOs, Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, MtAppDetailVO> appDetailPk2VoMap)
			throws BusinessException {
		
		// 按照 申请单pk+业务数据明细pk，分组待正向回写的业务单据
		Map<String, MtappCtrlBusiVO> positiveBusiDataMap = new HashMap<String, MtappCtrlBusiVO>();
		for (MtappCtrlBusiVO busivo : exeDataVOs[3]) {
			positiveBusiDataMap.put(busivo.getMatterAppPK()+busivo.getDetailBusiPK(), busivo);
		}
		// 包装中间表待使用的上游单据执行记录,为防止影响上游单据执行记录clone后使用
//		List<MtapppfVO> srcpflist = new ArrayList<MtapppfVO>();
		Map<String, List<MtapppfVO>> srcpfListMap = new HashMap<String, List<MtapppfVO>>();
		// 按费用申请单明细行分组上游单据执行记录
		Map<String, MtapppfVO> srcpfMap = new HashMap<String, MtapppfVO>();
		for (MtappCtrlBusiVO busivo : exeDataVOs[2]) {
			// 获得中间表上游单据的执行记录，需要按照上游单据回写申请单的顺序进行排序获得
			List<MtapppfVO> temp_srcpflist= appPfMap.get(busivo.getMatterAppPK()+ busivo.getSrcBusidetailPK());
			// 按费用申请单明细行分组上游单据执行记录
			for (MtapppfVO mtapppfVO : temp_srcpflist) {
				String busi_detail_pk = mtapppfVO.getBusi_detail_pk();
				String key = mtapppfVO.getPk_mtapp_detail()+busi_detail_pk;
				if(srcpfMap.containsKey(key)){
					continue;
				}
				MtapppfVO mtapppfVO_c = (MtapppfVO) mtapppfVO.clone();
				srcpfMap.put(key, mtapppfVO_c);
				List<MtapppfVO> srcpflist = srcpfListMap.get(busi_detail_pk);
				if(srcpflist == null){
					srcpflist = new ArrayList<MtapppfVO>();
					srcpfListMap.put(busi_detail_pk, srcpflist);
				}
				srcpflist.add(mtapppfVO_c);
			}
		}
		for (MtappCtrlBusiVO busivo : exeDataVOs[2]) {

			UFDouble remaindAmount = busivo.getExeData(); 
//			MatterAppVO mtappvo = appPk2AppVoMap.get(busivo.getMatterAppPK()).getParentVO();
			// 获得中间表下游单据的完全匹配行记录
			MtappCtrlBusiVO forwardBusiVO = positiveBusiDataMap.get(busivo.getMatterAppPK()+ busivo.getForwardBusidetailPK());
			if(forwardBusiVO != null){
//				List<MtappCtrlfieldVO> ctrlFieldList =getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap);
				// fieldcode+fieldValue+……+appPk+pk_org
				String allFieldKey = forwardBusiVO.getAllFieldKey();
				List<MtAppDetailVO> mtAppDetailList = allAdjustAppVoMap.get(allFieldKey);
//				List<String> matchDetailPks = new ArrayList<String>();
				if(mtAppDetailList != null){
					for (MtAppDetailVO appDetailVo : mtAppDetailList) {
						if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
							continue;
						}
						MtapppfVO srcmtapppfVO = srcpfMap.get(appDetailVo.getPrimaryKey()+busivo.getSrcBusidetailPK());
						if(srcmtapppfVO != null){
//							matchDetailPks.add(appDetailVo.getPrimaryKey());
							// 中间表的执行记录是独立的，不与上游或者下游混在一起
//							MtapppfVO mtapppfVO = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
							// 计算回写金额。最大只允许回写上游占用的金额
							UFDouble exe_amount = getDoubleValue(srcmtapppfVO.getExe_amount());
							if(exe_amount.add(remaindAmount).compareTo(UFDouble.ZERO_DBL)>0){
								exe_amount = remaindAmount;
								remaindAmount = UFDouble.ZERO_DBL;
							}else{
								remaindAmount = exe_amount.add(remaindAmount);
								exe_amount = exe_amount.multiply(new UFDouble(-1));
							}
							UFDouble[] exeAmounts = new UFDouble[]{exe_amount,UFDouble.ZERO_DBL}; 
							// 释放上游单据占用的申请
							exeAmounts = writeBackDetailAppVo(exeAmounts, appDetailVo,null, appPfMap, busivo, UFBoolean.FALSE);
//							// 记录释放的金额
//							extraAmountMap.put(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK(), (exe_amount.multiply(new UFDouble(-1))).add(
//									getDoubleValue(extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK()))));
							// 释放上游单据执行记录占用的执行数
							srcmtapppfVO.setExe_amount(srcmtapppfVO.getExe_amount().add(exe_amount));
							if(exeAmounts[0].compareTo(UFDouble.ZERO_DBL) != 0){
								throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
										getStrByID("201212_0","0201212-0095")/*@res ""待回写的申请单明细行已经关闭，无法回写，请手工取消关闭""*/);
							}
							if(remaindAmount.compareTo(UFDouble.ZERO_DBL)==0){
								// 执行完成
								break;
							}
						}
					}
				}
			}
			
			// 释放上游单据原占用的申请。优先释放下游单据需要占用的行，再按照上游占用申请的顺序进行释放；中间表本身金额是负数
			if(remaindAmount.compareTo(UFDouble.ZERO_DBL) !=0 ){
				// 按上游单据回写申请单顺序进行释放申请
				for (MtapppfVO srcmtapppfVO : srcpfListMap.get(busivo.getSrcBusidetailPK())) {
					MtAppDetailVO appDetailVo = appDetailPk2VoMap.get(srcmtapppfVO.getPk_mtapp_detail());
					if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
						continue;
					}
					// 中间表的执行记录是独立的，不与上游或者下游混在一起
//					MtapppfVO mtapppfVO = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
					// 计算回写金额。最大只允许回写上游占用的金额
					UFDouble exe_amount = getDoubleValue(srcmtapppfVO.getExe_amount());
					if(exe_amount.add(remaindAmount).compareTo(UFDouble.ZERO_DBL)>0){
						exe_amount = remaindAmount;
						remaindAmount = UFDouble.ZERO_DBL;
					}else{
						remaindAmount = exe_amount.add(remaindAmount);
						exe_amount = exe_amount.multiply(new UFDouble(-1));
					}
					UFDouble[] exeAmounts = new UFDouble[]{exe_amount,UFDouble.ZERO_DBL}; 
					// 释放上游单据占用的申请
					exeAmounts = writeBackDetailAppVo(exeAmounts, appDetailVo, null,appPfMap, busivo, UFBoolean.FALSE);
//					// 记录释放的金额
//					extraAmountMap.put(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK(), (exe_amount.multiply(new UFDouble(-1))).add(
//							getDoubleValue(extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK()))));
					// 释放上游单据执行记录占用的执行数
					srcmtapppfVO.setExe_amount(srcmtapppfVO.getExe_amount().add(exe_amount));					
					if(exeAmounts[0].compareTo(UFDouble.ZERO_DBL) != 0){
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
								getStrByID("201212_0","0201212-0095")/*@res ""待回写的申请单明细行已经关闭，无法回写，请手工取消关闭""*/);
					}
					if(remaindAmount.compareTo(UFDouble.ZERO_DBL)==0){
						// 执行完成
						break;
					}
				}
			}
			
		}
	}
	
	/**
	 * 正向回写执行数
	 * @param unAdjustBusiVoMap 
	 * 
	 * @param exeDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param unAdjustAppVoMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoExePositive(Map<String, List<MtappCtrlBusiVO>> unAdjustBusiVoMap, List<MtappCtrlBusiVO> exeDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, List<MtAppDetailVO>> unAdjustAppVoMap)
			throws BusinessException {
		
		for (Entry<String, List<MtappCtrlBusiVO>> unAdjustEntry : unAdjustBusiVoMap.entrySet()) {
			List<MtAppDetailVO> unAdjustAppList = unAdjustAppVoMap.get(unAdjustEntry.getKey());
			AggMatterAppVO aggMatterAppVO = appPk2AppVoMap.get(unAdjustAppList.get(0).getPk_mtapp_bill());
//			MatterAppVO mtappvo = aggMatterAppVO.getParentVO();
			
			writeBackDetail_new(aggMatterAppVO, allAdjustAppVoMap, appPfMap, unAdjustAppList, unAdjustEntry.getValue());
		}
	}

	/**
	 * 回写费用申请单预占数
	 * 
	 * @param preDataVOs 回写预占数的业务数据
	 * @param appPk2AppVoMap 费用申请单vomap
	 * @param appDetailPk2VoMap 申请单明细行vomap
	 * @param appPfMap 业务数据执行记录信息
	 * @param allAdjustAppVoMap 全部匹配维度的申请单明细map
	 * @throws BusinessException 
	 */
	public void writeBackAppVoPreData(List<MtappCtrlBusiVO>[] preDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,Map<String, List<MtapppfVO>> appPfMap, Map<String, List<MtAppDetailVO>> allAdjustAppVoMap) throws BusinessException {
//		/**
//		 * 下游单据额外可用的申请单明细行金额
//		 * Map<申请单明细行pk+下游单据明细pk，上游释放出的金额(负数)>
//		 */
//		Map<String, UFDouble> extraAmountMap = new HashMap<String, UFDouble>();
		
		// 3、中间表反向回写数据
		writeBackAppVoPreContrastPositive(preDataVOs, appPk2AppVoMap,
				appPfMap, allAdjustAppVoMap,appDetailPk2VoMap);
		// 4、正向回写数据
		writeBackAppVoPrePositive(preDataVOs[3], appPk2AppVoMap,
				 appPfMap, allAdjustAppVoMap);
	}

	/**
	 * 正向回写预占数
	 * 
	 * @param preDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @throws BusinessException
	 */
	private void writeBackAppVoPrePositive(List<MtappCtrlBusiVO> preDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap)
			throws BusinessException {
		for (MtappCtrlBusiVO busivo : preDataVOs) {
			// 获得业务数据全部控制维度key
//			MatterAppVO mtappvo = appPk2AppVoMap.get(busivo.getMatterAppPK()).getParentVO();
			// fieldcode+fieldValue+……+appPk+pk_org
			String allFieldKey = busivo.getAllFieldKey();
			// 获取完全匹配维度对应的申请单明细行第一行
			List<MtAppDetailVO> mtAppDetailList = allAdjustAppVoMap.get(allFieldKey);
			if(mtAppDetailList == null || mtAppDetailList.isEmpty()){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
						getStrByID("201212_0","0201212-0090")/*@res ""业务单据与费用申请单控制维度对照字段值不一致，请修改业务单据""*/);
			}
			
			for (MtAppDetailVO appDetailVo : mtAppDetailList) {
				if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
					continue;
				}
				// 获得本身的执行记录
//				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				// 回写费用申请单明细行金额
				writeBackDetailAppVo(new UFDouble[]{UFDouble.ZERO_DBL,busivo.getPreData()}, appDetailVo, null,appPfMap, busivo, UFBoolean.FALSE);
				// 预占数直接写到完全匹配行的第一行，写到就结束遍历
				break;
			}
		}
	}

	
	/**
	 * 获取业务行+申请单明细行的执行记录
	 * 
	 * @param busivo
	 * @param appDetailVo
	 * @param appPfMap
	 * @param ma_tradetype 
	 * @return
	 */
	protected MtapppfVO getMtapppfVO_new(MtappCtrlBusiVO busivo,MtAppDetailVO appDetailVo,Map<String, List<MtapppfVO>> appPfMap, String ma_tradetype){
		String pk_mtapp_detail = appDetailVo.getPk_mtapp_detail();
		MtapppfVO appPfVo = null;
		String pfKey = busivo.getMatterAppPK()+busivo.getDetailBusiPK();
		List<MtapppfVO> pflist = appPfMap.get(pfKey);
		if(pflist == null){
			pflist = new ArrayList<MtapppfVO>();
			appPfMap.put(pfKey, pflist);
		}
		for (MtapppfVO mtapppfVO : pflist) {
			if(mtapppfVO.getPk_mtapp_detail().equals(pk_mtapp_detail)){
				appPfVo = mtapppfVO;
				break;
			}
		}
		if(appPfVo == null){
			appPfVo = getNewMtappPfVO(busivo, appDetailVo,ma_tradetype);
			pflist.add(appPfVo);
		}
		return appPfVo;
	}
			
	/**
	 * 反向回写申请单
	 * 
	 * @param datavos
	 * @param appDetailPk2VoMap
	 * @param appPfMap
	 * @throws BusinessException
	 */
	public void writeBackAppVoNegative(List<MtappCtrlBusiVO> datavos,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		for (MtappCtrlBusiVO busivo : datavos) {
			// 获得本身的执行记录
			String pfKey = busivo.getMatterAppPK()+busivo.getDetailBusiPK();
			List<MtapppfVO> pflist = appPfMap.get(pfKey);
			if(pflist != null){
				for (MtapppfVO mtapppfVO : pflist) {
					MtAppDetailVO detailvo = appDetailPk2VoMap.get(mtapppfVO.getPk_mtapp_detail());
					UFDouble exe_amount = UFDouble.ZERO_DBL;
					UFDouble pre_data = UFDouble.ZERO_DBL;
					// 按照回写数据类型，释放执行数据
					if(IMtappCtrlBusiVO.DataType_exe.equals(busivo.getDataType())){
						exe_amount = getDoubleValue(mtapppfVO.getExe_amount()).multiply(-1);
					}else{
						pre_data = getDoubleValue(mtapppfVO.getPre_amount()).multiply(-1);
					}
					
					writeBackDetailAppVo(new UFDouble[]{exe_amount,pre_data}, 
							detailvo, mtapppfVO,appPfMap, busivo, UFBoolean.FALSE);
				}
			}
		}
	}
	
	/**
	 * 获得费用申请单控制规则map的key
	 * 
	 * @param mtappvo
	 * @return
	 */
	public String getMtappCtrlRuleKey(MatterAppVO mtappvo) {
		return mtappvo.getPk_org() + mtappvo.getPk_tradetype();
	}
	/**
	 * 获得费用申请单控制字段
	 * 
	 * @param mtappvo
	 * @return
	 */
	public List<MtappCtrlfieldVO> getMtappCtrlFields(MatterAppVO mtappvo,Map<String, List<MtappCtrlfieldVO>> fieldmap) {
		return fieldmap.get(getMtappCtrlRuleKey(mtappvo));
	}
	
	/**
	 * 获得费用申请单控制对象
	 * 
	 * @param mtappvo
	 * @return
	 */
	public List<String> getMtappCtrlBills(MatterAppVO mtappvo,Map<String, List<String>> billmap) {
		return billmap.get(getMtappCtrlRuleKey(mtappvo));
	}
	
	
	/**
	 * 初始化控制维度中在VO对照中的对照
	 * @param apppk2MtappCtrlBusiVOMap 申请单PK和 busiVo的对照
	 * @param matterAppVOs
	 * @param key2CtrlFieldVosMap 控制维度Map
	 * @throws BusinessException
	 */
	void initCtrlFiledMap(Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap, AggMatterAppVO[] matterAppVOs,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap) throws BusinessException {
		if (apppk2MtappCtrlBusiVOMap == null || matterAppVOs == null) {
			return;
		}

		ctrlFiledMap = new HashMap<String, Map<String, List<String>>>();

		for (Map.Entry<String, List<MtappCtrlBusiVO>> entry : apppk2MtappCtrlBusiVOMap.entrySet()) {
			AggMatterAppVO appVo = getMatterAppVos(entry.getKey(), matterAppVOs);
			if (appVo == null) {
				continue;
			}
			String pk_ma_tradetype = appVo.getParentVO().getPk_tradetype();

			for (MtappCtrlBusiVO busiVo : entry.getValue()) {
				String key = getCtrlFiledMapKey(appVo.getParentVO(), busiVo.getTradeType());
				if (key == null) {
					continue;
				}

				if (ctrlFiledMap.get(key) == null) {
					ArrayList<ExchangeRuleVO> ruleVoList = findExchangeRule(pk_ma_tradetype,
							busiVo.getTradeType());

					if (ruleVoList == null || ruleVoList.isEmpty()) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"upp2012v575_0", "0upp2012V575-0129")/*
																	 * @res
																	 * "费用申请单控制维度未配置vo对照，请检查！"
																	 */);
					}

//					String orgTradeTypekey = getMtappCtrlRuleKey(appVo.getParentVO());
					// 校验控制对象。校验当前业务数据交易类型是否在申请单的控制对象内
					List<MtappCtrlfieldVO> ctrlBillList = getMtappCtrlFields(appVo.getParentVO(), key2CtrlFieldVosMap);
					List<String> ctrlFieldCodeList = new ArrayList<String>();
					
					if(ctrlBillList != null){
						for(MtappCtrlfieldVO ctrlField : ctrlBillList){
							ctrlFieldCodeList.add(ctrlField.getFieldcode());
						}
					}
					
					Map<String, List<String>> voKeyMap = new HashMap<String, List<String>>();
					for (ExchangeRuleVO ruleVo : ruleVoList) {
						String ruleData = ruleVo.getRuleData();
						if (ctrlFieldCodeList.contains(ruleData)) {
							List<String> list = voKeyMap.get(ruleData);
							if(list == null){
								list = new ArrayList<String>();
								voKeyMap.put(ruleData, list);
							}
							list.add(ruleVo.getDest_attr());
						}
					}
					ctrlFiledMap.put(key, voKeyMap);
				}
			}
		}
	}

	private AggMatterAppVO getMatterAppVos(String matterAppPK, AggMatterAppVO[] matterAppVOs) {
		for (AggMatterAppVO aggVo : matterAppVOs) {
			if (matterAppPK.equals(aggVo.getParentVO().getPk_mtapp_bill())) {
				return aggVo;
			}
		}

		return null;
	}

	/**
	 * ma_tradetype+busi_tradetype+pk_org
	 * 
	 * @param mtappvo
	 * @param tradeTpye
	 * @return
	 */
	private String getCtrlFiledMapKey(MatterAppVO mtappvo, String tradeTpye) {
		if (mtappvo == null) {
			return null;
		}
		return mtappvo.getPk_tradetype() + "_" + tradeTpye + "_" + mtappvo.getPk_org();
	}

	/**
	 * 查询获得vo对照规则
	 * 
	 * @param context
	 * @return
	 */
	private ArrayList<ExchangeRuleVO> findExchangeRule(String srcBilltypeOrTrantype, String destBilltypeOrTrantype) {
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		@SuppressWarnings("unchecked")
		ArrayList<ExchangeRuleVO> exchangeRuleVO = (ArrayList<ExchangeRuleVO>) NCLocator.getInstance()
				.lookup(IPFConfig.class)
				.getMappingRelation(srcBilltypeOrTrantype, destBilltypeOrTrantype, null, pk_group);
		return exchangeRuleVO;
	}

	public void setAppFieldSum(Map<String, UFDouble> appFieldSum) {
		this.appFieldSum = appFieldSum;
	}
}