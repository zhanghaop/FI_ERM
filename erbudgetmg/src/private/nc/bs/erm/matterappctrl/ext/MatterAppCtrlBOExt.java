package nc.bs.erm.matterappctrl.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.matterapp.ErmMatterAppDAO;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.erm.util.ErMdpersistUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

import org.apache.commons.lang.ArrayUtils;

/**
 * 费用申请单控制回写业务实现类，扩展
 * 
 * @author lvhj
 *
 */
public class MatterAppCtrlBOExt {
	
	// 申请单vomap
	Map<String, AggMatterAppVO> appPk2AppVoMap = new HashMap<String, AggMatterAppVO>();
	// 申请单明细行vomap
	Map<String, MtAppDetailVO> appDetailPk2VoMap = new HashMap<String, MtAppDetailVO>();
	
	// 根据费用申请单交易类型和组织分组查询 【费用申请控制规则设置】强制控制拉单后不允许删除控制对象后，可考虑不查询及校验
	Map<String, List<String>> key2CtrlBillVosMap = null;
	
	// 是否要回写费用申请单
	private boolean isWriteBack = true;
	// 是否受控制规则控制
	private boolean isCtrlByRule = false;
	
	private MatterAppCtrlHelperExt helper = new MatterAppCtrlHelperExt();

	public MatterAppCtrlBOExt(boolean isWriteBack) {
		super();
		this.isWriteBack = isWriteBack;
	}
	public MatterAppCtrlBOExt(boolean isWriteBack,boolean isCtrlByRule) {
		super();
		this.isWriteBack = isWriteBack;
		this.isCtrlByRule = isCtrlByRule;
	}
	
	private UFDouble getDoubleValue(Object d) {
		return d == null ? UFDouble.ZERO_DBL : (UFDouble) d;
	}
	
	/**
	 * 费用申请单控制及回写
	 *
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControlByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException {
		MtappCtrlInfoVO errVo = new MtappCtrlInfoVO();
		if (ArrayUtils.isEmpty(vos)) {
			return errVo;
		}
		// ************ 1  转换业务数据为统一结构MtappCtrlBusiVO，且按费用申请单pk分组业务数据 ************
		Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap = new HashMap<String, List<MtappCtrlBusiVO>>();// 费用申请单pk分组的业务数据
		Map<String, UFDouble> apppk2BusiSumAmoutMap = new HashMap<String, UFDouble>();// 费用申请单pk，分组的业务单据合计回写执行数金额
		Map<String, UFDouble> apppk2JKSumAmoutMap = new HashMap<String, UFDouble>();// 费用申请单pk，分组的借款单合计回写执行数金额（不允许超申请）
		MtappCtrlBusiVO[] ctrlvos = new MtappCtrlBusiVO[vos.length];
		List<MtappCtrlBusiVO>[] preDataVOs = initDataListArray();//待回写预占数的业务数据
		List<MtappCtrlBusiVO>[] exeDataVOs = initDataListArray();//待回写执行数的业务数据
		for (int i = 0; i < ctrlvos.length; i++) {
			ctrlvos[i] = new MtappCtrlBusiVO(vos[i]);
			
			MtappCtrlBusiVO busivo = ctrlvos[i];
			String appPk = busivo.getMatterAppPK();
			List<MtappCtrlBusiVO> appCtrlBusiVOList = apppk2MtappCtrlBusiVOMap.get(appPk);
			if(appCtrlBusiVOList == null){
				appCtrlBusiVOList = new ArrayList<MtappCtrlBusiVO>();
				apppk2MtappCtrlBusiVOMap.put(appPk, appCtrlBusiVOList);
			}
			appCtrlBusiVOList.add(busivo);
			// 分组回写预占、执行的业务数据
			int direction = getDataDirection(busivo);
			if(IMtappCtrlBusiVO.DataType_pre.equals(busivo.getDataType())){
				preDataVOs[direction].add(busivo);
			}else{
				exeDataVOs[direction].add(busivo);
				apppk2BusiSumAmoutMap.put(appPk, getDoubleValue(apppk2BusiSumAmoutMap.get(appPk)).add(getDoubleValue(busivo.getAmount())));
				if(!busivo.isExceedEnable()){
					// 借款单单据维护时的金额，不包括报销单冲借款的情况
					apppk2JKSumAmoutMap.put(appPk, getDoubleValue(apppk2JKSumAmoutMap.get(appPk)).add(getDoubleValue(busivo.getAmount())));
				}
			}
		}
		// ************ 二 查询数据：费用申请单 ************
		AggMatterAppVO[] matterAppVOs = queryAppVOsAndCtrlData(apppk2MtappCtrlBusiVOMap.keySet());

		// ************ 四 明细控制校验 ************
		List<String> errorMsgList = validateBusiData(apppk2BusiSumAmoutMap,apppk2JKSumAmoutMap,apppk2MtappCtrlBusiVOMap);
		
		// ************ 五 包装错误信息 ************
		if (errorMsgList.size() > 0) {
			errVo.setControlinfos(errorMsgList.toArray(new String[0]));
			return errVo;
		}

		if (isWriteBack) {
			// ************ 六 查询、包装 反向回写申请单的业务数据执行记录************
			// 执行记录map<申请单pk+业务数据明细pk,List<MtapppfVO>>
			Map<String, List<MtapppfVO>> appPfMap = helper.constructMtappPfMap(appPk2AppVoMap.keySet().toArray(new String[0]));

			// 回写申请单
			writeBackMtappVO(preDataVOs,exeDataVOs,appPfMap);
			// 更新申请单，且保存执行记录
			updateMatterappVO(matterAppVOs, appPfMap);
		}
		return errVo;
	}

	/**
	 * 保存执行记录
	 * 
	 * @param appPfMap
	 * @param jkExeMap 
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private void saveMtappPfVOs(Map<String, List<MtapppfVO>> appPfMap, Map<String, UFDouble> jkExeMap)
			throws DAOException, BusinessException {

		List<MtapppfVO> allPflist = new ArrayList<MtapppfVO>();
		for (Entry<String, List<MtapppfVO>> pfvalue : appPfMap.entrySet()) {
			List<MtapppfVO> value = pfvalue.getValue();
			for (MtapppfVO mtapppfVO : value) {
				allPflist.add(mtapppfVO);
				if(BXConstans.JK_DJLXBM.equals(mtapppfVO.getPk_billtype())){
					//记录借款单类型的执行数占用情况
					UFDouble amount = getDoubleValue(jkExeMap.get(mtapppfVO.getPk_mtapp_detail())).add(getDoubleValue(mtapppfVO.getExe_amount()));
					jkExeMap.put(mtapppfVO.getPk_mtapp_detail(), amount);
				}
			}
		}
		MtapppfVO[] appPfVos = allPflist.toArray(new MtapppfVO[0]);
		new BaseDAO().execUpdateByVoState(appPfVos);
		//清理垃圾数据
		new BaseDAO().executeUpdate(" DELETE FROM  ER_MTAPP_PF WHERE FY_AMOUNT=0 AND EXE_AMOUNT=0 AND PRE_AMOUNT=0 ");

		// ************ 十一 更新费用申请单执行记录余额表 ************
		helper.synchronizBalance(appPfVos);
	}

	/**
	 * 回写申请单
	 * 
	 * @param preDataVOs
	 * @param exeDataVOs
	 * @param appPfMap
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, List<MtapppfVO>> writeBackMtappVO(
			List<MtappCtrlBusiVO>[] preDataVOs,
			List<MtappCtrlBusiVO>[] exeDataVOs,Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		// ************ 七  反向回写数据 ************
		writeBackAppVoNegative(preDataVOs, exeDataVOs, appPfMap);
		
		// ************ 八  明细预占数回写，不受可回写金额控制 ************
		// 回写中间表预占数
		helper.writeBackAppVoPreDataByRatio(preDataVOs[2],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		// 回写业务数据预占数
		helper.writeBackAppVoPreDataByRatio(preDataVOs[3],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		
		// ************ 九  明细执行数回写 ************
		// 回写中间表执行数
		helper.writeBackAppVoExeDataByRatio(exeDataVOs[2],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		// 回写业务数据执行数
		helper.writeBackAppVoExeDataByRatio(exeDataVOs[3],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		return appPfMap;
	}


	/**
	 * 反向回写明细数据
	 * 
	 * @param preDataVOs
	 * @param exeDataVOs
	 * @param appPfMap
	 * @throws BusinessException
	 */
	private void writeBackAppVoNegative(List<MtappCtrlBusiVO>[] preDataVOs,
			List<MtappCtrlBusiVO>[] exeDataVOs,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		// 1、反向回写数据--预占数
		helper.writeBackAppVoNegative(preDataVOs[0], appDetailPk2VoMap, appPfMap);
		// 2、中间表正向回写数据，相当于中间表本身的反向回写 --- 预占数
		helper.writeBackAppVoNegative(preDataVOs[1], appDetailPk2VoMap, appPfMap);
		
		// 3、反向回写数据 --执行数
		helper.writeBackAppVoNegative(exeDataVOs[0], appDetailPk2VoMap, appPfMap);
		// 4、中间表正向回写数据，相当于中间表本身的反向回写 --执行数
		helper.writeBackAppVoNegative(exeDataVOs[1], appDetailPk2VoMap, appPfMap);
	}
	
	/**
	 * 获得回写数据的方向
	 * 0：反向回写数据；1：中间表正向回写数据；2：中间表反向回写数据；3：正向回写数据
	 * @param busivo
	 * @return
	 */
	private int getDataDirection(MtappCtrlBusiVO busivo) {
		int direction = busivo.getDirection();
		String forwardBusidetailPK = busivo.getForwardBusidetailPK();
		
		int res = 0;
		
		if(IMtappCtrlBusiVO.Direction_negative == direction){
			res = StringUtil.isEmptyWithTrim(forwardBusidetailPK)?0:2;
		}else{
			res = StringUtil.isEmptyWithTrim(forwardBusidetailPK)?3:1;
		}
		
		return res;
	}

	private List<MtappCtrlBusiVO>[] initDataListArray() {
		@SuppressWarnings("unchecked")
		List<MtappCtrlBusiVO>[] array = new ArrayList[4];
		array[0] = new ArrayList<MtappCtrlBusiVO>();//反向回写数据
		array[1] = new ArrayList<MtappCtrlBusiVO>();//中间表正向回写数据
		array[2] = new ArrayList<MtappCtrlBusiVO>();//中间表反向回写数据
		array[3] = new ArrayList<MtappCtrlBusiVO>();//正向回写数据
		return array;
	}

	/**
	 * 查询费用申请单（加业务锁）,控制对象,控制维度
	 *
	 * @param apppk2MtappCtrlBusiVOMap
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @return 
	 */
	private AggMatterAppVO[] queryAppVOsAndCtrlData(Collection<String> appPks) throws BusinessException {
		// 申请单加pk锁
		ErLockUtil.lockByPk("ERM_matterapp", appPks);
		// 查询费用申请单:根据业务数据关联费用申请单pks
		// 查询费用申请单
		AggMatterAppVO[] matterAppVOs = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPKs(appPks.toArray(new String[0]));
		if (matterAppVOs == null || matterAppVOs.length != appPks.size()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0034")/*@res "费用申请单已经被删除，无法回写数据"*/);
		}

		//检查费用申请单，整单已关闭状态
		checkCloseStatus(matterAppVOs);
		
		// 分组费用申请单及申请单detail
		for (AggMatterAppVO appVo : matterAppVOs) {
			// hash化aggvo
			appPk2AppVoMap.put(appVo.getPrimaryKey(), appVo);
			 
			MtAppDetailVO[] childrenVO = appVo.getChildrenVO();
			if (ArrayUtils.isEmpty(childrenVO)) {
				continue;
			}

			for (MtAppDetailVO mtAppDetailVO : childrenVO) {
				// 哈希化 明细行vo
				appDetailPk2VoMap.put(mtAppDetailVO.getPrimaryKey(),mtAppDetailVO);
			}
		}
		
		return matterAppVOs;
	}

	@SuppressWarnings("unchecked")
	private void checkCtrlRule(AggMatterAppVO[] matterAppVOs, MtappCtrlBusiVO[] ctrlvos) throws BusinessException{
		// 受控制规则控制时，查询控制规则，只校验控制对象
		if(isCtrlByRule){

			// 根据交易类型和组织分组形成key
			Set<String[]> keySet = helper.getOrgTradeTypeKeyList(matterAppVOs);

			@SuppressWarnings("rawtypes")
			Map[] ctrlmap = NCLocator.getInstance().lookup(IErmMappCtrlBillQuery.class)
			.queryCtrlShema(Arrays.asList(keySet.toArray(new String[0][0])),InvocationInfoProxy.getInstance().getGroupId());
			
			// 查询控制对象。根据费用申请单交易类型和组织分组查询 【费用申请控制规则设置】强制控制拉单后不允许删除控制对象后，可考虑不查询及校验
			key2CtrlBillVosMap = ctrlmap[0];
			if (key2CtrlBillVosMap == null || key2CtrlBillVosMap.isEmpty()) {
				// 【费用申请控制规则设置】，设置不控制任何交易类型的业务单据
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0035")/*@res "【费用申请控制规则设置】控制对象已经被删除，无法回写数据"*/);
			}

			// 按刚性维度分组业务数据detail，获得各个刚性维度合计执行值，同时过滤不受规则控制的业务数据
			for (MtappCtrlBusiVO busiVo : ctrlvos) {
				AggMatterAppVO aggMatterAppVO = appPk2AppVoMap.get(busiVo.getMatterAppPK());

				MatterAppVO mtappvo = aggMatterAppVO.getParentVO();

				// 校验控制对象。校验当前业务数据交易类型是否在申请单的控制对象内
				List<String> ctrlBillList = key2CtrlBillVosMap.get(helper.getMtappCtrlRuleKey(mtappvo));
				if (ctrlBillList == null||!ctrlBillList.contains(busiVo.getTradeType())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0035")/*@res "【费用申请控制规则设置】控制对象已经被删除，无法回写数据"*/);
				}
			}
		}
	}
	
	/**
	 * 检查费用申请单，整单已关闭状态
	 *
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param matterAppVOs 
	 */
	private void checkCloseStatus(AggMatterAppVO[] matterAppVOs) throws BusinessException {
		StringBuffer billNoBuf = new StringBuffer();
		for (AggMatterAppVO vo : matterAppVOs) {
			if (vo.getParentVO().getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(vo.getParentVO().getCloseman()))) {
				//费用申请单整单关闭，不能回写
				billNoBuf.append("[" + vo.getParentVO().getBillno() + "] ");
			}
		}
		if (billNoBuf.length() > 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0036")/*@res "费用申请单"*/ + billNoBuf.toString() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0037")/*@res "已经关闭，无法回写数据"*/);
		}
	}

	/**
	 * 整单控制校验
	 *
	 * @param apppk2BusiSumAmoutMap 
	 * @param apppk2jkSumAmoutMap 
	 * @param apppk2MtappCtrlBusiVOMap 
	 */
	private List<String> validateBusiData(Map<String, UFDouble> apppk2BusiSumAmoutMap, Map<String, UFDouble> apppk2jkSumAmoutMap, Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap) throws BusinessException {
		List<String> errorMsgList = new ArrayList<String>();
		
		// 遍历刚性控制维度的业务数据。因为刚性维度的key中包含了申请单的pk，所以进入遍历后可以唯一确定一个申请单
		for (Entry<String, UFDouble> entry : apppk2BusiSumAmoutMap.entrySet()) {
			String key = entry.getKey();
			MatterAppVO vo = appPk2AppVoMap.get(key).getParentVO();
			
			// ************ 校验 ************
			// 刚性控制校验：费用申请单余额大于业务单据执行数，只校验原币
			// 不允许超申请的业务数据控制：本次允许回写最大金额 = 申请单总金额 - 总执行数；
			UFDouble max_amount = vo.getOrig_amount().sub(getDoubleValue(vo.getExe_amount()));
			UFDouble jk_amount = apppk2jkSumAmoutMap.get(key);
			if(jk_amount != null){
				if (max_amount.compareTo(jk_amount) < 0) {
					// 包装错误信息
					errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/);
					continue;
				}
			}
			
			UFDouble exe_amount_total = entry.getValue();
			if(max_amount.compareTo(exe_amount_total) >= 0){
				// 总执行数不超出申请总金额情况，不用按照超申请逻辑处理，在总金额范围内执行即可
				List<MtappCtrlBusiVO> busilist = apppk2MtappCtrlBusiVOMap.get(key);
				for (MtappCtrlBusiVO mtappCtrlBusiVO : busilist) {
					mtappCtrlBusiVO.setExceedEnable(false);
				}
				continue;
			}
			
			// 总执行金额的控制：本次允许回写最大金额 = 允许回写最大金额 - 总执行数；
			if(vo.getMax_amount() != null){
				max_amount = vo.getMax_amount().sub(getDoubleValue(vo.getExe_amount()));
			}
			if (max_amount.compareTo(exe_amount_total) < 0) {
				// 包装错误信息
				errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/);
				break;
			}

			if (errorMsgList.size() > 0) {
				// 存在错误信息，不包装回写数据
				break;
			}
		}
		return errorMsgList;
	}

	/**
	 * 按申请单明细行，回写控制
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException 
	 */
	public MtappCtrlInfoVO matterappControlByDetail(IMtappCtrlBusiVO[] vos) throws BusinessException {
		MtappCtrlInfoVO errVo = new MtappCtrlInfoVO();
		if (ArrayUtils.isEmpty(vos)) {
			return errVo;
		}
		// ************ 1  转换业务数据为统一结构MtappCtrlBusiVO，且按费用申请单pk分组业务数据 ************
		Map<String, UFDouble> appdetailpkpk2BusiSumAmoutMap = new HashMap<String, UFDouble>();// 费用申请单明细行pk，分组的业务单据合计回写执行数金额
		Map<String, UFDouble> appdetailpk2JKSumAmoutMap = new HashMap<String, UFDouble>();// 费用申请单pk，分组的借款单合计回写执行数金额（不允许超申请）
		MtappCtrlBusiVO[] ctrlvos = new MtappCtrlBusiVO[vos.length];
		Set<String> appPKs = new HashSet<String>();
		List<MtappCtrlBusiVO>[] preDataVOs = initDataListArray();//待回写预占数的业务数据
		List<MtappCtrlBusiVO>[] exeDataVOs = initDataListArray();//待回写执行数的业务数据
		for (int i = 0; i < ctrlvos.length; i++) {
			ctrlvos[i] = new MtappCtrlBusiVO(vos[i]);
			
			MtappCtrlBusiVO busivo = ctrlvos[i];
			String appPk = busivo.getMatterAppPK();
			appPKs.add(appPk);
			String appDetailPk = busivo.getMatterAppDetailPK();
			// 分组回写预占、执行的业务数据
			int direction = getDataDirection(busivo);
			if(IMtappCtrlBusiVO.DataType_pre.equals(busivo.getDataType())){
				preDataVOs[direction].add(busivo);
			}else{
				exeDataVOs[direction].add(busivo);
				appdetailpkpk2BusiSumAmoutMap.put(appDetailPk, getDoubleValue(appdetailpkpk2BusiSumAmoutMap.get(appDetailPk)).add(getDoubleValue(busivo.getAmount())));
				if(!busivo.isExceedEnable()){
					// 借款单单据维护时的金额，不包括报销单冲借款的情况
					appdetailpk2JKSumAmoutMap.put(appDetailPk, getDoubleValue(appdetailpk2JKSumAmoutMap.get(appDetailPk)).add(getDoubleValue(busivo.getAmount())));
				}
			}
		}
		// ************ 二 查询数据：费用申请单 ************
		AggMatterAppVO[] matterAppVOs = queryAppVOsAndCtrlData(appPKs); 
		// ************ 三 查询控制规则进行校验 ************
		if(isCtrlByRule){
			// 只校验控制对象
			checkCtrlRule(matterAppVOs,ctrlvos);
		}
		
		// ************ 四 明细控制校验 ************
		List<String> errorMsgList = validateBusiDataByDetail(appdetailpkpk2BusiSumAmoutMap,appdetailpk2JKSumAmoutMap);
		
		// ************ 五 包装错误信息 ************
		if (errorMsgList.size() > 0) {
			errVo.setControlinfos(errorMsgList.toArray(new String[0]));
			return errVo;
		}
		
		if (isWriteBack) {
			// ************ 六 查询、包装 反向回写申请单的业务数据执行记录************
			// 执行记录map<申请单pk+业务数据明细pk,List<MtapppfVO>>
			Map<String, List<MtapppfVO>> appPfMap = helper.constructMtappPfMap(appPk2AppVoMap.keySet().toArray(new String[0]));

			// 按申请单明细行，回写申请单
			writeBackMtappVOByDetail(preDataVOs,exeDataVOs,appPfMap);
			// 更新申请单，且保存执行记录
			updateMatterappVO(matterAppVOs, appPfMap);
		}
		return errVo;
		
	}

	/**
	 * 
	 * 
	 * 
	 * @param matterAppVOs
	 * @param appPfMap
	 * @throws DAOException
	 * @throws BusinessException
	 * @throws MetaDataException
	 */
	private void updateMatterappVO(AggMatterAppVO[] matterAppVOs,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		// ************ 十  保存费用申请单执行记录 ************
		// 借款单单据类型在各个申请单明细行上的执行数占用情况，key为申请明细行pk
		Map<String, UFDouble> jkExeMap = new HashMap<String, UFDouble>();
		saveMtappPfVOs(appPfMap,jkExeMap);
		
		// 十二  包装费用申请单：子表币种间折算，主表合计值 及自动关闭，重启
		List<AggMatterAppVO>[] closeAndopenList= helper.setTotalAmount(matterAppVOs,jkExeMap);
		
		// 十三  保存费用申请单
		MDPersistenceService.lookupPersistenceService().saveBill(ErMdpersistUtil.getNCObject(matterAppVOs));
		
		// 处理关闭重启申请单
		ErmMatterAppDAO dao = new ErmMatterAppDAO();
		IErmMatterAppBillClose closeService = NCLocator.getInstance().lookup(IErmMatterAppBillClose.class);
		if (!closeAndopenList[0].isEmpty()) {
			AggMatterAppVO[] aggvos = closeAndopenList[0].toArray(new AggMatterAppVO[0]);
			// 补充ts
			dao.addTsToVOs(VOUtils.getHeadVOs(aggvos));
			// 自动重启申请单
			closeService.autoOpenVOs(aggvos);
		}
		if (!closeAndopenList[1].isEmpty()) {
			AggMatterAppVO[] aggvos = closeAndopenList[1].toArray(new AggMatterAppVO[0]);
			// 补充ts
			dao.addTsToVOs(VOUtils.getHeadVOs(aggvos));
			// 自动关闭申请单，暂时不考虑对统一个申请单即关闭也重启的情况导致的版本校验失败情况
			closeService.autoCloseVOs(aggvos);
		}

	}
	
	/**
	 * 按申请单明细行控制校验
	 *
	 * @param appDetailpk2BusiSumAmoutMap 
	 * @param appdetailpk2JKSumAmoutMap 
	 */
	private List<String> validateBusiDataByDetail(Map<String, UFDouble> appDetailpk2BusiSumAmoutMap, Map<String, UFDouble> appdetailpk2JKSumAmoutMap) throws BusinessException {
		List<String> errorMsgList = new ArrayList<String>();
		
		// 遍历刚性控制维度的业务数据。因为刚性维度的key中包含了申请单的pk，所以进入遍历后可以唯一确定一个申请单
		for (Entry<String, UFDouble> entry : appDetailpk2BusiSumAmoutMap.entrySet()) {
			String key = entry.getKey();
			MtAppDetailVO vo = appDetailPk2VoMap.get(key);
			if(vo == null){
				throw new BusinessException("费用申请单待回写明细行不存在，请检查数据");
			}
			if (vo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(vo.getCloseman()))) {
				//费用申请单明细行关闭，不能回写
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0036")
						/*@res "费用申请单"*/ + "[" + vo.getBillno() + "] " +"明细行"+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0037")/*@res "已经关闭，无法回写数据"*/);
			}
			
			
			// ************ 校验 ************
			// 刚性控制校验：费用申请单余额大于业务单据执行数，只校验原币
			// 不允许超申请的业务数据控制：本次允许回写最大金额 = 申请单总金额 - 总执行数；
			UFDouble max_amount = vo.getOrig_amount().sub(getDoubleValue(vo.getExe_amount()));
			UFDouble jk_amount = appdetailpk2JKSumAmoutMap.get(key);
			if(jk_amount != null){
				if (max_amount.compareTo(jk_amount) < 0) {
					// 包装错误信息
					errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/);
					break;
				}
			}
			
			// 总执行金额的控制：本次允许回写最大金额 = 允许回写最大金额 - 总执行数；
			if(vo.getMax_amount() != null){
				max_amount = vo.getMax_amount().sub(getDoubleValue(vo.getExe_amount()));
			}
			if (max_amount.compareTo(entry.getValue()) < 0) {
				// 包装错误信息
				errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/);
				break;
			}

			if (errorMsgList.size() > 0) {
				// 存在错误信息，不包装回写数据
				continue;
			}
		}
		return errorMsgList;
	}
	/**
	 * 按申请单明细行，回写申请单
	 * 
	 * @param preDataVOs
	 * @param exeDataVOs
	 * @param appPfMap
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, List<MtapppfVO>> writeBackMtappVOByDetail(
			List<MtappCtrlBusiVO>[] preDataVOs,
			List<MtappCtrlBusiVO>[] exeDataVOs,Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		// ************ 七  反向回写数据 ************
		writeBackAppVoNegative(preDataVOs, exeDataVOs, appPfMap);
		
		// ************ 八  明细预占数回写，不受可回写金额控制 ************
		// 回写中间表预占数
		helper.writeBackAppVoPreDataByDetail(preDataVOs[2],appDetailPk2VoMap,appPfMap);
		// 回写业务数据预占数
		helper.writeBackAppVoPreDataByDetail(preDataVOs[3],appDetailPk2VoMap,appPfMap);
		
		// ************ 九  明细执行数回写 ************
		// 回写中间表执行数
		helper.writeBackAppVoExeDataByDetail(exeDataVOs[2],appDetailPk2VoMap,appPfMap);
		// 回写业务数据执行数
		helper.writeBackAppVoExeDataByDetail(exeDataVOs[3],appDetailPk2VoMap,appPfMap);
		return appPfMap;
	}
}