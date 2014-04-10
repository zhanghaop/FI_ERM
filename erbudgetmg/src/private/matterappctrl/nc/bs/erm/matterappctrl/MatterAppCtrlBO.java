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

	// ���뵥vomap
	Map<String, AggMatterAppVO> appPk2AppVoMap = new HashMap<String, AggMatterAppVO>();
	// ���뵥��ϸ��vomap
	Map<String, MtAppDetailVO> appDetailPk2VoMap = new HashMap<String, MtAppDetailVO>();
	
	// ���ݷ������뵥�������ͺ���֯�����ѯ ������������ƹ������á�ǿ�ƿ�������������ɾ�����ƶ���󣬿ɿ��ǲ���ѯ��У��
	Map<String, List<String>> key2CtrlBillVosMap = null;

	// ���ݷ������뵥�������ͺ���֯�����ѯ
	Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap = new HashMap<String, List<MtappCtrlfieldVO>>();

	// ����ά�Ⱥϼ�ִ��ֵ:Map<fieldcode+fieldValue+����+appPk,sumValue>
	Map<String, UFDouble> busiFieldSum = new HashMap<String, UFDouble>();
	// �����������ҵ�񵥾��ܽ��
	Map<String, UFDouble> noExceedBusiFieldSum = new HashMap<String, UFDouble>();

	// ���ݸ���ά�ȷ���ҵ������
	Map<String, List<MtappCtrlBusiVO>> unAdjustBusiVoMap = new HashMap<String, List<MtappCtrlBusiVO>>();

	// ����ά�Ⱥϼ�ִ��ֵ:Map<fieldcode+fieldValue+����+appPk,sumValue>
	Map<String, UFDouble> appFieldSum = new HashMap<String, UFDouble>();
	// ���뵥���������ֵ�ϼ�
	Map<String, UFDouble> maxAppFieldSum = new HashMap<String, UFDouble>();

	// ���ݸ���ά�ȷ���������뵥����
	Map<String, List<MtAppDetailVO>> unAdjustAppVoMap = new HashMap<String, List<MtAppDetailVO>>();

	// ����ȫ��ά�ȷ���������뵥����
	Map<String, List<MtAppDetailVO>> allAdjustAppVoMap = new HashMap<String, List<MtAppDetailVO>>();

	// �Ƿ�Ҫ��д�������뵥
	boolean isWriteBack = true;
	
	// �Ƿ�����������д
	boolean isAllAdjust = false;

	// ������
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
	 * �������뵥���Ƽ���д
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
		// ************ 1  ת��ҵ������Ϊͳһ�ṹMtappCtrlBusiVO���Ұ��������뵥pk����ҵ������ ************
		Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap = new HashMap<String, List<MtappCtrlBusiVO>>();
		MtappCtrlBusiVO[] ctrlvos = new MtappCtrlBusiVO[vos.length];
		List<MtappCtrlBusiVO>[] preDataVOs = initDataListArray();//����дԤռ����ҵ������
		List<MtappCtrlBusiVO>[] exeDataVOs = initDataListArray();//����дִ������ҵ������
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
			// �����дԤռ��ִ�е�ҵ������
			int direction = getDataDirection(busivo);
			if(IMtappCtrlBusiVO.DataType_pre.equals(busivo.getDataType())){
				preDataVOs[direction].add(busivo);
			}else{
				exeDataVOs[direction].add(busivo);
			}
		}
		
		// ************ �� ��ѯ���ݣ��������뵥�����ƶ��󣬿���ά�� ************
		Set<String> appPKs = apppk2MtappCtrlBusiVOMap.keySet();
		AggMatterAppVO[] matterAppVOs = queryAppVOsAndCtrlData(appPKs.toArray(new String[appPKs.size()]));
		
		//TODO ��ʼ������ά��vo�ֶζ��ջ��棬set��helper�й�getAttributeʹ��
		helper.initCtrlFiledMap(apppk2MtappCtrlBusiVOMap, matterAppVOs, key2CtrlFieldVosMap);
		
		// ************ �� �������ݣ�����ά�ȷ���ͳ��ҵ������;����ά��+ȫ��ά�ȷ���������뵥 �ϼ�ֵ ************
		// ִ�м�¼map<���뵥pk+ҵ��������ϸpk,List<MtapppfVO>>
		Map<String, List<MtapppfVO>> appPfMap = helper.constructMtappPfMap(VOUtils.getAttributeValues(matterAppVOs, null));
		// ����ά�ȣ�����������뵥��ҵ�����ݣ�ͬʱ���������뵥�������ͣ���װ��ҵ��������
		groupAppDataAndBusiData(ctrlvos,matterAppVOs,appPfMap);
		

		// ************ �� ��ϸ����У�� ************
		List<String> errorMsgList = validateBusiData(ctrlvos);
		
		// ************ �� ��װ������Ϣ ************
		if (errorMsgList.size() > 0) {
			errVo.setControlinfos(errorMsgList.toArray(new String[0]));
			errVo.setExceed(true);
			return errVo;
		}
		// ************ �� ��ѯ����װ �����д���뵥��ҵ������ִ�м�¼************
		// ��д���뵥
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

		// ���÷��ؽ���Ƿ��������
		for (MtappCtrlBusiVO ctrlvo : ctrlvos) {
			if(ctrlvo.isExceed()){
				errVo.setExceed(true);
				break;
			}
		}
		
		// ���ڴ����м������£�validate��������У�飨�޷������м���ڲ���д�������ȷ����д������һ�����뵥�У�����ʵ�ʼ����д���ʱ�����������Խ�writeBackMtappVO�ᵽ��д���ݿ�ǰ��
		if (isWriteBack) {
			
			// ************ ʮ  ����������뵥ִ�м�¼ ************
			// �����������ڸ������뵥��ϸ���ϵ�ִ����ռ�������keyΪ������ϸ��pk
			Map<String, UFDouble> jkExeMap = new HashMap<String, UFDouble>();
			saveMtappPfVOs(appPfMap,jkExeMap);
			
			// ʮ��  ��װ�������뵥���ӱ���ּ����㣬����ϼ�ֵ ���Զ��رգ�����
			List<AggMatterAppVO>[] closeAndopenList= helper.setTotalAmount(matterAppVOs,jkExeMap);
			
			// ʮ��  ����������뵥
			MDPersistenceService.lookupPersistenceService().saveBill(ErMdpersistUtil.getNCObject(matterAppVOs));
			
			// �Զ��رա�����
			ErmMatterAppDAO dao = new ErmMatterAppDAO();
			IErmMatterAppBillClose closeService = NCLocator.getInstance().lookup(IErmMatterAppBillClose.class);
			if (!closeAndopenList[0].isEmpty()) {
				AggMatterAppVO[] aggvos = closeAndopenList[0].toArray(new AggMatterAppVO[0]);
				// ����ts
				dao.addTsToVOs(VOUtils.getHeadVOs(aggvos));
				// �Զ��������뵥
				closeService.autoOpenVOs(aggvos);
			}
			if (!closeAndopenList[1].isEmpty()) {
				AggMatterAppVO[] aggvos = closeAndopenList[1].toArray(new AggMatterAppVO[0]);
				// ����ts
				dao.addTsToVOs(VOUtils.getHeadVOs(aggvos));
				// �Զ��ر����뵥����ʱ�����Ƕ�ͳһ�����뵥���ر�Ҳ������������µİ汾У��ʧ�����
				closeService.autoCloseVOs(aggvos);
			}

		}
		return errVo;
	}

	/**
	 * ����ִ�м�¼
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
					//��¼�����͵�ִ����ռ�����
					UFDouble amount = helper.getDoubleValue(jkExeMap.get(mtapppfVO.getPk_mtapp_detail())).add(helper.getDoubleValue(mtapppfVO.getExe_amount()));
					jkExeMap.put(mtapppfVO.getPk_mtapp_detail(), amount);
				}
			}
		}
		MtapppfVO[] appPfVos = allPflist.toArray(new MtapppfVO[0]);
		new BaseDAO().execUpdateByVoState(appPfVos);
		//������������
		new BaseDAO().executeUpdate(" DELETE FROM  ER_MTAPP_PF WHERE EXE_AMOUNT=0 AND PRE_AMOUNT=0 ");

		// ************ ʮһ ���·������뵥ִ�м�¼���� ************
		helper.synchronizBalance(appPfVos);
	}

	/**
	 * ��д���뵥
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
		// ************ ��  �����д���� ************
		writeBackAppVoNegative(preDataVOs, exeDataVOs, appPfMap);
		
		// ************ ��  ��ϸԤռ����д ************
		helper.writeBackAppVoPreData(preDataVOs,appPk2AppVoMap,appDetailPk2VoMap,appPfMap,allAdjustAppVoMap);
		
		// ************ ��  ��ϸִ������д ************
		helper.writeBackAppVoExeData(exeDataVOs,appPk2AppVoMap,appDetailPk2VoMap,appPfMap,allAdjustAppVoMap,unAdjustBusiVoMap,unAdjustAppVoMap);
		return appPfMap;
	}

	/**
	 * �����д��ϸ����
	 * 
	 * @param preDataVOs
	 * @param exeDataVOs
	 * @param appPfMap
	 * @throws BusinessException
	 */
	private void writeBackAppVoNegative(List<MtappCtrlBusiVO>[] preDataVOs,
			List<MtappCtrlBusiVO>[] exeDataVOs,
			Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		// 1�������д����--Ԥռ��
		helper.writeBackAppVoNegative(preDataVOs[0], appDetailPk2VoMap, appPfMap);
		// 2���м�������д���ݣ��൱���м����ķ����д --- Ԥռ��
		helper.writeBackAppVoNegative(preDataVOs[1], appDetailPk2VoMap, appPfMap);
		
		// 3�������д���� --ִ����
		helper.writeBackAppVoNegative(exeDataVOs[0], appDetailPk2VoMap, appPfMap);
		// 4���м�������д���ݣ��൱���м����ķ����д --ִ����
		helper.writeBackAppVoNegative(exeDataVOs[1], appDetailPk2VoMap, appPfMap);
	}

	/**
	 * ��û�д���ݵķ���
	 * 0�������д���ݣ�1���м�������д���ݣ�2���м�����д���ݣ�3�������д����
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
		array[0] = new ArrayList<MtappCtrlBusiVO>();//�����д����
		array[1] = new ArrayList<MtappCtrlBusiVO>();//�м�������д����
		array[2] = new ArrayList<MtappCtrlBusiVO>();//�м�����д����
		array[3] = new ArrayList<MtappCtrlBusiVO>();//�����д����
		return array;
	}

	/**
	 * ��ѯ�������뵥����ҵ������,���ƶ���,����ά��
	 *
	 * @param appPKs
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private AggMatterAppVO[] queryAppVOsAndCtrlData(String[] appPKs) throws BusinessException {
		// ���뵥��pk��
		ErLockUtil.lockByPk("ERM_matterapp", Arrays.asList(appPKs));
		// ��ѯ�������뵥:����ҵ�����ݹ����������뵥pks
		// ��ѯ�������뵥
		AggMatterAppVO[] matterAppVOs = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPKs(appPKs);
		if (matterAppVOs == null || matterAppVOs.length != appPKs.length) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0034")/*@res "�������뵥�Ѿ���ɾ�����޷���д����"*/);
		}

		//���������뵥�������ѹر�״̬
		checkCloseStatus(matterAppVOs);

		// ���ݽ������ͺ���֯�����γ�key
		Set<String[]> keySet = helper.getOrgTradeTypeKeyList(matterAppVOs);

		@SuppressWarnings("rawtypes")
		Map[] ctrlmap = NCLocator.getInstance().lookup(IErmMappCtrlBillQuery.class)
		.queryCtrlShema(Arrays.asList(keySet.toArray(new String[0][0])),InvocationInfoProxy.getInstance().getGroupId());
		
		// ��ѯ���ƶ��󡣸��ݷ������뵥�������ͺ���֯�����ѯ ������������ƹ������á�ǿ�ƿ�������������ɾ�����ƶ���󣬿ɿ��ǲ���ѯ��У��
		key2CtrlBillVosMap = ctrlmap[0];
		if (key2CtrlBillVosMap == null || key2CtrlBillVosMap.isEmpty()) {
			// ������������ƹ������á������ò������κν������͵�ҵ�񵥾�
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0035")/*@res "������������ƹ������á����ƶ����Ѿ���ɾ�����޷���д����"*/);
		}

		// ��ѯ����ά�ȡ����ݷ������뵥�������ͺ���֯�����ѯ
		key2CtrlFieldVosMap = ctrlmap[1];
		if (this.isAllAdjust || this.key2CtrlFieldVosMap == null) {
			// ���������������������ά�ȴ���
			key2CtrlFieldVosMap = new HashMap<String, List<MtappCtrlfieldVO>>();
		}
		return matterAppVOs;
	}

	/**
	 * ���������뵥�������ѹر�״̬
	 *
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param matterAppVOs 
	 */
	private void checkCloseStatus(AggMatterAppVO[] matterAppVOs) throws BusinessException {
		StringBuffer billNoBuf = new StringBuffer();
		for (AggMatterAppVO vo : matterAppVOs) {
			if (vo.getParentVO().getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(vo.getParentVO().getCloseman()))) {
				//�������뵥�����رգ����ܻ�д
				billNoBuf.append("[" + vo.getParentVO().getBillno() + "] ");
			}
		}
		if (billNoBuf.length() > 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0036")/*@res "�������뵥"*/ + billNoBuf.toString() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0037")/*@res "�Ѿ��رգ��޷���д����"*/);
		}
	}

	/**
	 * ����ά�ȣ����ԣ�ȫ���� ����ҵ������ ����������뵥detail
	 * ͬʱ���������뵥�������ͣ���װ��ҵ��������
	 * @param vos
	 * @param matterAppVOs
	 * @param appPk2AppVoMap
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param appPfMap 
	 */
	private void groupAppDataAndBusiData(MtappCtrlBusiVO[] vos, AggMatterAppVO[] matterAppVOs, Map<String, List<MtapppfVO>> appPfMap) throws BusinessException {
		
		// ����������뵥detail
		for (AggMatterAppVO appVo : matterAppVOs) {
			MatterAppVO parentVO = appVo.getParentVO();
			// matterAppVOs ��ϣ��
			appPk2AppVoMap.put(parentVO.getPrimaryKey(), appVo);

			MtAppDetailVO[] childrenVO = appVo.getChildrenVO();
			if (ArrayUtils.isEmpty(childrenVO)) {
				continue;
			}
			boolean is_adjust = parentVO.getIs_adjust() == null?true:parentVO.getIs_adjust().booleanValue();

			for (MtAppDetailVO mtAppDetailVO : childrenVO) {
				// ��ϣ�� ��ϸ��vo
				appDetailPk2VoMap.put(mtAppDetailVO.getPrimaryKey(),mtAppDetailVO);
				
				// ���������뵥���ݲ��ɵ������������ά�ȵĿɵ��������ã������ά�� = ȫ������ά��
				// fieldcode+fieldValue+����+appPk+pk_org
				String key = helper.getFieldKey(helper.getMtappCtrlFields(parentVO, key2CtrlFieldVosMap), mtAppDetailVO, !is_adjust,parentVO);
				
				// �ϼ����
				helper.calculateRestData(appFieldSum, mtAppDetailVO, key);
				// ����������������
				helper.calculateMaxRestData(maxAppFieldSum, mtAppDetailVO, key);

				// ���ݸ���ά�ȷ��鹹��ҵ������map
				helper.constructBusiVoMap(unAdjustAppVoMap, mtAppDetailVO, key);

				// fieldcode+fieldValue+����+appPk+pk_org
				String allFieldKey = helper.getFieldKey(helper.getMtappCtrlFields(parentVO, key2CtrlFieldVosMap), mtAppDetailVO, true,parentVO);

				// ����ȫ��ά�ȷ��鹹��ҵ������map
				helper.constructBusiVoMap(allAdjustAppVoMap, mtAppDetailVO, allFieldKey);
				// ����������ά�ȣ�����
				mtAppDetailVO.setUnAdjustKey(key);
				mtAppDetailVO.setAllFieldKey(allFieldKey);
			}
		}
		// ���������map�����ø�helper�������ý�����ʹ��
		helper.setAppFieldSum(appFieldSum);
		// ������ά�ȷ���ҵ������detail����ø�������ά�Ⱥϼ�ִ��ֵ��ͬʱ���˲��ܹ�����Ƶ�ҵ������
		for (MtappCtrlBusiVO busiVo : vos) {
			AggMatterAppVO aggMatterAppVO = appPk2AppVoMap.get(busiVo.getMatterAppPK());

			MatterAppVO mtappvo = aggMatterAppVO.getParentVO();

			// У����ƶ���У�鵱ǰҵ�����ݽ��������Ƿ������뵥�Ŀ��ƶ�����
			List<String> ctrlBillList = key2CtrlBillVosMap.get(helper.getMtappCtrlRuleKey(mtappvo));
			if (ctrlBillList == null||!ctrlBillList.contains(busiVo.getTradeType())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0035")/*@res "������������ƹ������á����ƶ����Ѿ���ɾ�����޷���д����"*/);
			}
			
			boolean is_adjust = mtappvo.getIs_adjust() == null?true:mtappvo.getIs_adjust().booleanValue();
			if(getDataDirection(busiVo) == 3){
				// ���ݱ����д���뵥�������Ҫ��֤���Կ���ά���Ƿ��Ӧ����
				String key = helper.getFieldKey(helper.getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap), busiVo, !is_adjust,mtappvo);
				if (appFieldSum.get(key) == null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
							getStrByID("upp2012v575_0","0upp2012V575-0122")/*@res ""ҵ�񵥾���������뵥���Զ����ֶ�ֵ��һ�£����޸�ҵ�񵥾�""*/);
				}
				String allFieldKey = null;
				if(is_adjust){
					allFieldKey= helper.getFieldKey(helper.getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap), busiVo, true,mtappvo);
				}else{
					allFieldKey = key;
				}
				// ����busivo�ĸ��Կ���ά��
				busiVo.setUnAdjustKey(key);
				busiVo.setAllFieldKey(allFieldKey);
			}

			// ��дִ�����������Ҫ���и���ά�ȷ��飬���н��У�顢����ά���ڵ�����д
			if(IMtappCtrlBusiVO.DataType_exe.equals(busiVo.getDataType())){
				
				if(IMtappCtrlBusiVO.Direction_positive == busiVo.getDirection()){
					// ���������뵥���ݲ��ɵ������������ά�ȵĿɵ��������ã������ά�� = ȫ������ά��
					// fieldcode+fieldValue+����+appPk+pk_org
					String key = helper.getFieldKey(helper.getMtappCtrlFields(mtappvo, key2CtrlFieldVosMap), busiVo, !is_adjust,mtappvo);
					// �ϼ�ҵ������ִ����
					helper.calculateExeData(busiFieldSum, busiVo, key);
					if(!busiVo.isExceedEnable()){
						// �ϼƲ��ɳ����������ҵ������ִ����
						helper.calculateExeData(noExceedBusiFieldSum, busiVo, key);
					}
					
					// ���ݸ���ά�ȷ��鹹�������д���뵥��ҵ������map(�������м������)
					if(StringUtil.isEmptyWithTrim(busiVo.getForwardBusidetailPK())){
						helper.constructBusiVoMap(unAdjustBusiVoMap, busiVo, key);
					}
				}else{
					// �����дִ�����������Ҫ����ִ�м�¼����ִ��������ԭռ��
					String pfKey = busiVo.getMatterAppPK()+busiVo.getDetailBusiPK();
					List<MtapppfVO> pflist = appPfMap.get(pfKey);
					if(pflist != null){
						for (MtapppfVO mtapppfVO : pflist) {
							MtAppDetailVO detailvo = appDetailPk2VoMap.get(mtapppfVO.getPk_mtapp_detail());
							// fieldcode+fieldValue+����+appPk+pk_org
							String key = detailvo.getUnAdjustKey();
							
							// �ϼ����
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
	 * ��ϸ���ƣ���д
	 *
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param vos 
	 * @param appPfMap 
	 */
	private List<String> validateBusiData(MtappCtrlBusiVO[] vos) throws BusinessException {
		List<String> errorMsgList = new ArrayList<String>();
		
		for(MtappCtrlBusiVO busiVo : vos){//�����м��Ļ�д����ʱ��������У�飬�ڻ�д��У��
			if(getDataDirection(busiVo) == 1 || getDataDirection(busiVo) == 2){
				return errorMsgList;
			}
		}
		
		// �������Կ���ά�ȵ�ҵ�����ݡ���Ϊ����ά�ȵ�key�а��������뵥��pk�����Խ�����������Ψһȷ��һ�����뵥
		for (Entry<String, UFDouble> entry : busiFieldSum.entrySet()) {
			String key = entry.getKey();

			// ************ У�� ************
			if (appFieldSum.get(key) == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
						getStrByID("upp2012v575_0","0upp2012V575-0122")/*@res ""ҵ�񵥾���������뵥���Զ����ֶ�ֵ��һ�£����޸�ҵ�񵥾�""*/);
			}
			// ���Կ���У�飺У�鲻�ɳ�����Ľ��
			UFDouble noexceedAmount = noExceedBusiFieldSum.get(key);
			if(noexceedAmount != null && appFieldSum.get(key).compareTo(noexceedAmount) < 0){
				errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
				break;
			}
			
			// ���Կ���У�飺У���ܽ��ɳ������뵥��������������
			if (maxAppFieldSum.get(key).compareTo(entry.getValue()) < 0) {
				// ��װ������Ϣ
				errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
				break;
			}

			if (errorMsgList.size() > 0) {
				// ���ڴ�����Ϣ������װ��д����
				continue;
			}
		}
		return errorMsgList;
	}
}