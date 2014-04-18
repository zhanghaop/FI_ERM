package nc.bs.erm.matterappctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ErmMaCtrlException;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
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

public class MatterAppCtrlBO {

	// 申请单vomap
	Map<String, AggMatterAppVO> appPk2AppVoMap = new HashMap<String, AggMatterAppVO>();
	// 申请单明细行vomap
	Map<String, MtAppDetailVO> appDetailPk2VoMap = new HashMap<String, MtAppDetailVO>();
	
	// 根据费用申请单交易类型和组织分组查询 【费用申请控制规则设置】强制控制拉单后不允许删除控制对象后，可考虑不查询及校验
	Map<String, List<String>> key2CtrlBillVosMap = null;

	// 根据费用申请单交易类型和组织分组查询
	Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap = new HashMap<String, List<MtappCtrlfieldVO>>();

	// 刚性维度合计执行值:Map<fieldcode+fieldValue+……+appPk,sumValue>
	Map<String, UFDouble> busiFieldSum = new HashMap<String, UFDouble>();
	// 不允许超申请的业务单据总金额
	Map<String, UFDouble> noExceedBusiFieldSum = new HashMap<String, UFDouble>();

	// 根据刚性维度分组业务数据
	Map<String, List<MtappCtrlBusiVO>> unAdjustBusiVoMap = new HashMap<String, List<MtappCtrlBusiVO>>();

	// 刚性维度合计执行值:Map<fieldcode+fieldValue+……+appPk,sumValue>
	Map<String, UFDouble> appFieldSum = new HashMap<String, UFDouble>();
	// 申请单允许报销最大值合计
	Map<String, UFDouble> maxAppFieldSum = new HashMap<String, UFDouble>();

	// 根据刚性维度分组费用申请单数据
	Map<String, List<MtAppDetailVO>> unAdjustAppVoMap = new HashMap<String, List<MtAppDetailVO>>();

	// 根据全部维度分组费用申请单数据
	Map<String, List<MtAppDetailVO>> allAdjustAppVoMap = new HashMap<String, List<MtAppDetailVO>>();

	// 是否要回写费用申请单
	boolean isWriteBack = true;
	
	// 是否整单调剂回写
	boolean isAllAdjust = false;

	// 工具类
	MatterAppCtrlHelper helper = new MatterAppCtrlHelper();

	public MatterAppCtrlBO(boolean isWriteBack) {
		super();
		this.isWriteBack = isWriteBack;
	}
	public MatterAppCtrlBO(boolean isWriteBack,boolean isAllAdjust) {
		super();
		this.isWriteBack = isWriteBack;
		this.isAllAdjust = isAllAdjust;
	}
	
	/**
	 * 费用申请单控制及回写
	 *
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public MtappCtrlInfoVO matterappControl(IMtappCtrlBusiVO[] vos) throws BusinessException {
		MtappCtrlInfoVO errVo = new MtappCtrlInfoVO();
		if (ArrayUtils.isEmpty(vos)) {
			return errVo;
		}
		// ************ 1  转换业务数据为统一结构MtappCtrlBusiVO，且按费用申请单pk分组业务数据 ************
		Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap = new HashMap<String, List<MtappCtrlBusiVO>>();
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
			}
		}
		
		// ************ 二 查询数据：费用申请单，控制对象，控制维度 ************
		Set<String> appPKs = apppk2MtappCtrlBusiVOMap.keySet();
		AggMatterAppVO[] matterAppVOs = queryAppVOsAndCtrlData(appPKs.toArray(new String[appPKs.size()]));
		
		//TODO 初始化控制维度vo字段对照缓存，set到helper中供getAttribute使用
		helper.initCtrlFiledMap(apppk2MtappCtrlBusiVOMap, matterAppVOs, key2CtrlFieldVosMap);
		
		// ************ 三 分组数据：刚性维度分组统计业务数据;刚性维度+全部维度分组费用申请单 合计值 ************
		// 执行记录map<申请单pk+业务数据明细pk,List<MtapppfVO>>
		Map<String, List<MtapppfVO>> appPfMap = helper.constructMtappPfMap(VOUtils.getAttributeValues(matterAppVOs, null));
		// 按照维度，分组费用申请单，业务数据；同时将费用申请单交易类型，封装到业务数据中
		groupAppDataAndBusiData(ctrlvos,matterAppVOs,appPfMap);
		

		// ************ 四 明细控制校验 ************
		List<String> errorMsgList = validateBusiData(ctrlvos);
		
		// ************ 五 包装错误信息 ************
		if (errorMsgList.size() > 0) {
			errVo.setControlinfos(errorMsgList.toArray(new String[0]));
			errVo.setExceed(true);
			return errVo;
		}
		// ************ 六 查询、包装 反向回写申请单的业务数据执行记录************
		// 回写申请单
		try {
			writeBackMtappVO(preDataVOs,exeDataVOs,appPfMap);
		} catch (Exception e) {
			if(e instanceof ErmMaCtrlException){
				errVo.setExceed(((ErmMaCtrlException)e).isExceed());
				errVo.setControlinfos(new String[]{((ErmMaCtrlException)e).getMessage()});
				return errVo;
			}else{
				ExceptionHandler.handleException(e);
			}
		}

		// 设置返回结果是否超申请情况
		for (MtappCtrlBusiVO ctrlvo : ctrlvos) {
			if(ctrlvo.isExceed()){
				errVo.setExceed(true);
				break;
			}
		}
		
		// 由于存在中间表情况下，validate不做金额的校验（无法根据中间表，在不回写的情况下确定回写具体哪一行申请单行），在实际计算回写金额时才做处理，所以将writeBackMtappVO提到回写数据库前。
		if (isWriteBack) {
			
			// ************ 十  保存费用申请单执行记录 ************
			// 借款单单据类型在各个申请单明细行上的执行数占用情况，key为申请明细行pk
			Map<String, UFDouble> jkExeMap = new HashMap<String, UFDouble>();
			saveMtappPfVOs(appPfMap,jkExeMap);
			
			// 十二  包装费用申请单：子表币种间折算，主表合计值 及自动关闭，重启
			List<AggMatterAppVO>[] closeAndopenList= helper.setTotalAmount(matterAppVOs,jkExeMap);
			
			// 十三  保存费用申请单
			MDPersistenceService.lookupPersistenceService().saveBill(ErMdpersistUtil.getNCObject(matterAppVOs));
			
			// 自动关闭、重启
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
					UFDouble amount = helper.getDoubleValue(jkExeMap.get(mtapppfVO.getPk_mtapp_detail())).add(helper.getDoubleValue(mtapppfVO.getExe_amount()));
					jkExeMap.put(mtapppfVO.getPk_mtapp_detail(), amount);
				}
			}
		}
		MtapppfVO[] appPfVos = allPflist.toArray(new MtapppfVO[0]);
		new BaseDAO().execUpdateByVoState(appPfVos);
		//清理垃圾数据
		new BaseDAO().executeUpdate(" DELETE FROM  ER_MTAPP_PF WHERE EXE_AMOUNT=0 AND PRE_AMOUNT=0 ");

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
		
		// ************ 八  明细预占数回写 ************
		helper.writeBackAppVoPreData(preDataVOs,appPk2AppVoMap,appDetailPk2VoMap,appPfMap,allAdjustAppVoMap);
		
		// ************ 九  明细执行数回写 ************
		helper.writeBackAppVoExeData(exeDataVOs,appPk2AppVoMap,appDetailPk2VoMap,appPfMap,allAdjustAppVoMap,unAdjustBusiVoMap,unAdjustAppVoMap);
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
	 * @param appPKs
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private AggMatterAppVO[] queryAppVOsAndCtrlData(String[] appPKs) throws BusinessException {
		// 申请单加pk锁
		ErLockUtil.lockByPk("ERM_matterapp", Arrays.asList(appPKs));
		// 查询费用申请单:根据业务数据关联费用申请单pks
		// 查询费用申请单
		AggMatterAppVO[] matterAppVOs = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPKs(appPKs);
		if (matterAppVOs == null || matterAppVOs.length != appPKs.length) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0034")/*@res "费用申请单已经被删除，无法回写数据"*/);
		}

		//检查费用申请单，整单已关闭状态
		checkCloseStatus(matterAppVOs);

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

		// 查询控制维度。根据费用申请单交易类型和组织分组查询
		key2CtrlFieldVosMap = ctrlmap[1];
		if (this.isAllAdjust || this.key2CtrlFieldVosMap == null) {
			// 整单调剂情况，不按控制维度处理
			key2CtrlFieldVosMap = new HashMap<String, List<MtappCtrlfieldVO>>();
		}
		return matterAppVOs;
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
	 * 按照维度（刚性，全部） 分组业务数据 分组费用申请单detail
	 * 同时将费用申请单交易类型，封装到业务数据中
	 * @param vos
	 * @param matterAppVOs
	 * @param appPk2AppVoMap
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param appPfMap 
	 */
	private void groupAppDataAndBusiData(MtappCtrlBusiVO[] vos, AggMatterAppVO[] matterAppVOs, Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		
		// 分组费用申请单detail
		for (AggMatterAppVO appVo : matterAppVOs) {
			MatterAppVO parentVO = appVo.getParentVO();
			// matterAppVOs 哈希化
			appPk2AppVoMap.put(parentVO.getPrimaryKey(), appVo);

			MtAppDetailVO[] childrenVO = appVo.getChildrenVO();
			if (ArrayUtils.isEmpty(childrenVO)) {
				continue;
			}
			boolean is_adjust = parentVO.getIs_adjust() == null?true:parentVO.getIs_adjust().booleanValue();

			for (MtAppDetailVO mtAppDetailVO : childrenVO) {
				// 哈希化 明细行vo
				appDetailPk2VoMap.put(mtAppDetailVO.getPrimaryKey(),mtAppDetailVO);
				
				// 当费用申请单单据不可调剂情况，控制维度的可调剂不可用，则刚性维度 = 全部控制维度
				// fieldcode+fieldValue+……+appPk+pk_org
				String key = helper.getFieldKey(helper.getMtappCtrlFields(parentVO, key2CtrlFieldVosMap), mtAppDetailVO, !is_adjust,parentVO);
				
				// 合计余额
				helper.calculateRestData(appFieldSum, mtAppDetailVO, key);
				// 计算允许报销最大余额
				helper.calculateMaxRestData(maxAppFieldSum, mtAppDetailVO, key);

				// 根据刚性维度分组构建业务数据map
				helper.constructBusiVoMap(unAdjustAppVoMap, mtAppDetailVO, key);

				// fieldcode+fieldValue+……+appPk+pk_org
				String allFieldKey = helper.getFieldKey(helper.getMtappCtrlFields(parentVO, key2CtrlFieldVosMap), mtAppDetailVO, true,parentVO);

				// 根据全部维度分组构建业务数据map
				helper.constructBusiVoMap(allAdjustAppVoMap, mtAppDetailVO, allFieldKey);
				// 保留下两个维度，待用
				mtAppDetailVO.setUnAdjustKey(key);
				mtAppDetailVO.setAllFieldKey(allFieldKey);
			}
		}
		// 将刚性余额map，设置给helper，供费用金额计算使用
		helper.setAppFieldSum(appFieldSum);
		// 按刚性维度分组业务数据detail，获得各个刚性维度合计执行值，同时过滤不受规则控制的业务数据
		for (MtappCtrlBusiVO busiVo : vos) {
			AggMatterAppVO aggMatterAppVO = appPk2AppVoMap.get(busiVo.getMatterAppPK());

			MatterAppVO mtappvo = aggMatterAppVO.getParentVO();

			// 校验控制对象。校验当前业务数据交易类型是否在申请单的控制对象内
			List<String> ctrlBillList = key2CtrlBillVosMap.get(helper.getMtappCtrlRuleKey(mtappvo));
			if (ctrlBillList == null||!ctrlBillList.contains(busiVo.getTradeType())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0035")/*@res "【费用申请控制规则设置】控制对象已经被删除，无法回写数据"*/);
			}
			
			boolean is_adjust = mtappvo.getIs_adjust() == null?true:mtappvo.getIs_adjust().booleanValue();
			if(getDataDirection(busiVo) == 3){
				// 单据本身回写申请单情况，需要验证刚性控制维度是否对应存在
				String key = helper.getFieldKey(helper.getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap), busiVo, !is_adjust,mtappvo);
				if (appFieldSum.get(key) == null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
							getStrByID("upp2012v575_0","0upp2012V575-0122")/*@res ""业务单据与费用申请单刚性对照字段值不一致，请修改业务单据""*/);
				}
				String allFieldKey = null;
				if(is_adjust){
					allFieldKey= helper.getFieldKey(helper.getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap), busiVo, true,mtappvo);
				}else{
					allFieldKey = key;
				}
				// 保留busivo的刚性控制维度
				busiVo.setUnAdjustKey(key);
				busiVo.setAllFieldKey(allFieldKey);
			}

			// 回写执行数情况，需要进行刚性维度分组，进行金额校验、刚性维度内调剂回写
			if(IMtappCtrlBusiVO.DataType_exe.equals(busiVo.getDataType())){
				
				if(IMtappCtrlBusiVO.Direction_positive == busiVo.getDirection()){
					// 当费用申请单单据不可调剂情况，控制维度的可调剂不可用，则刚性维度 = 全部控制维度
					// fieldcode+fieldValue+……+appPk+pk_org
					String key = helper.getFieldKey(helper.getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap), busiVo, !is_adjust,mtappvo);
					// 合计业务数据执行数
					helper.calculateExeData(busiFieldSum, busiVo, key);
					if(!busiVo.isExceedEnable()){
						// 合计不可超出申请金额的业务数据执行数
						helper.calculateExeData(noExceedBusiFieldSum, busiVo, key);
					}
					
					// 根据刚性维度分组构建正向回写申请单的业务数据map(不包括中间表正向)
					if(StringUtil.isEmptyWithTrim(busiVo.getForwardBusidetailPK())){
						helper.constructBusiVoMap(unAdjustBusiVoMap, busiVo, key);
					}
				}else{
					// 反向回写执行数情况，需要根据执行记录计算执行数。将原占用
					String pfKey = busiVo.getMatterAppPK()+busiVo.getDetailBusiPK();
					List<MtapppfVO> pflist = appPfMap.get(pfKey);
					if(pflist != null){
						for (MtapppfVO mtapppfVO : pflist) {
							MtAppDetailVO detailvo = appDetailPk2VoMap.get(mtapppfVO.getPk_mtapp_detail());
							// fieldcode+fieldValue+……+appPk+pk_org
							String key = detailvo.getUnAdjustKey();
							
							// 合计余额
							UFDouble sumData = appFieldSum.get(key);
							if (sumData == null) {
								sumData = UFDouble.ZERO_DBL;
							}
							sumData = sumData.add(helper.getDoubleValue(mtapppfVO.getExe_amount()));
							appFieldSum.put(key, sumData);
							
							UFDouble maxsumData = maxAppFieldSum.get(key);
							if (maxsumData == null) {
								maxsumData = UFDouble.ZERO_DBL;
							}
							maxsumData = maxsumData.add(helper.getDoubleValue(mtapppfVO.getExe_amount()));
							maxAppFieldSum.put(key, maxsumData);
							
						}
					}
					
				}
				

			}
			
		}
	
	}

	/**
	 * 明细控制，回写
	 *
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param vos 
	 * @param appPfMap 
	 */
	private List<String> validateBusiData(MtappCtrlBusiVO[] vos) throws BusinessException {
		List<String> errorMsgList = new ArrayList<String>();
		
		for(MtappCtrlBusiVO busiVo : vos){//存在中间表的回写数据时，不进行校验，在回写中校验
			if(getDataDirection(busiVo) == 1 || getDataDirection(busiVo) == 2){
				return errorMsgList;
			}
		}
		
		// 遍历刚性控制维度的业务数据。因为刚性维度的key中包含了申请单的pk，所以进入遍历后可以唯一确定一个申请单
		for (Entry<String, UFDouble> entry : busiFieldSum.entrySet()) {
			String key = entry.getKey();

			// ************ 校验 ************
			if (appFieldSum.get(key) == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
						getStrByID("upp2012v575_0","0upp2012V575-0122")/*@res ""业务单据与费用申请单刚性对照字段值不一致，请修改业务单据""*/);
			}
			// 刚性控制校验：校验不可超申请的金额
			UFDouble noexceedAmount = noExceedBusiFieldSum.get(key);
			if(noexceedAmount != null && appFieldSum.get(key).compareTo(noexceedAmount) < 0){
				errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "此单据金额超过申请的费用余额，请修改金额或者重新申请"*/);
				break;
			}
			
			// 刚性控制校验：校验总金额不可超出申请单允许超申请最大余额
			if (maxAppFieldSum.get(key).compareTo(entry.getValue()) < 0) {
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
}