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
 * �������뵥���ƻ�дҵ��ʵ���࣬��չ
 * 
 * @author lvhj
 *
 */
public class MatterAppCtrlBOExt {
	
	// ���뵥vomap
	Map<String, AggMatterAppVO> appPk2AppVoMap = new HashMap<String, AggMatterAppVO>();
	// ���뵥��ϸ��vomap
	Map<String, MtAppDetailVO> appDetailPk2VoMap = new HashMap<String, MtAppDetailVO>();
	
	// ���ݷ������뵥�������ͺ���֯�����ѯ ������������ƹ������á�ǿ�ƿ�������������ɾ�����ƶ���󣬿ɿ��ǲ���ѯ��У��
	Map<String, List<String>> key2CtrlBillVosMap = null;
	
	// �Ƿ�Ҫ��д�������뵥
	private boolean isWriteBack = true;
	// �Ƿ��ܿ��ƹ������
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
	 * �������뵥���Ƽ���д
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
		// ************ 1  ת��ҵ������Ϊͳһ�ṹMtappCtrlBusiVO���Ұ��������뵥pk����ҵ������ ************
		Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap = new HashMap<String, List<MtappCtrlBusiVO>>();// �������뵥pk�����ҵ������
		Map<String, UFDouble> apppk2BusiSumAmoutMap = new HashMap<String, UFDouble>();// �������뵥pk�������ҵ�񵥾ݺϼƻ�дִ�������
		Map<String, UFDouble> apppk2JKSumAmoutMap = new HashMap<String, UFDouble>();// �������뵥pk������Ľ��ϼƻ�дִ���������������룩
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
				apppk2BusiSumAmoutMap.put(appPk, getDoubleValue(apppk2BusiSumAmoutMap.get(appPk)).add(getDoubleValue(busivo.getAmount())));
				if(!busivo.isExceedEnable()){
					// ������ά��ʱ�Ľ���������������������
					apppk2JKSumAmoutMap.put(appPk, getDoubleValue(apppk2JKSumAmoutMap.get(appPk)).add(getDoubleValue(busivo.getAmount())));
				}
			}
		}
		// ************ �� ��ѯ���ݣ��������뵥 ************
		AggMatterAppVO[] matterAppVOs = queryAppVOsAndCtrlData(apppk2MtappCtrlBusiVOMap.keySet());

		// ************ �� ��ϸ����У�� ************
		List<String> errorMsgList = validateBusiData(apppk2BusiSumAmoutMap,apppk2JKSumAmoutMap,apppk2MtappCtrlBusiVOMap);
		
		// ************ �� ��װ������Ϣ ************
		if (errorMsgList.size() > 0) {
			errVo.setControlinfos(errorMsgList.toArray(new String[0]));
			return errVo;
		}

		if (isWriteBack) {
			// ************ �� ��ѯ����װ �����д���뵥��ҵ������ִ�м�¼************
			// ִ�м�¼map<���뵥pk+ҵ��������ϸpk,List<MtapppfVO>>
			Map<String, List<MtapppfVO>> appPfMap = helper.constructMtappPfMap(appPk2AppVoMap.keySet().toArray(new String[0]));

			// ��д���뵥
			writeBackMtappVO(preDataVOs,exeDataVOs,appPfMap);
			// �������뵥���ұ���ִ�м�¼
			updateMatterappVO(matterAppVOs, appPfMap);
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
					UFDouble amount = getDoubleValue(jkExeMap.get(mtapppfVO.getPk_mtapp_detail())).add(getDoubleValue(mtapppfVO.getExe_amount()));
					jkExeMap.put(mtapppfVO.getPk_mtapp_detail(), amount);
				}
			}
		}
		MtapppfVO[] appPfVos = allPflist.toArray(new MtapppfVO[0]);
		new BaseDAO().execUpdateByVoState(appPfVos);
		//������������
		new BaseDAO().executeUpdate(" DELETE FROM  ER_MTAPP_PF WHERE FY_AMOUNT=0 AND EXE_AMOUNT=0 AND PRE_AMOUNT=0 ");

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
		
		// ************ ��  ��ϸԤռ����д�����ܿɻ�д������ ************
		// ��д�м��Ԥռ��
		helper.writeBackAppVoPreDataByRatio(preDataVOs[2],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		// ��дҵ������Ԥռ��
		helper.writeBackAppVoPreDataByRatio(preDataVOs[3],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		
		// ************ ��  ��ϸִ������д ************
		// ��д�м��ִ����
		helper.writeBackAppVoExeDataByRatio(exeDataVOs[2],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
		// ��дҵ������ִ����
		helper.writeBackAppVoExeDataByRatio(exeDataVOs[3],appPk2AppVoMap,appDetailPk2VoMap,appPfMap);
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
	 * @param apppk2MtappCtrlBusiVOMap
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @return 
	 */
	private AggMatterAppVO[] queryAppVOsAndCtrlData(Collection<String> appPks) throws BusinessException {
		// ���뵥��pk��
		ErLockUtil.lockByPk("ERM_matterapp", appPks);
		// ��ѯ�������뵥:����ҵ�����ݹ����������뵥pks
		// ��ѯ�������뵥
		AggMatterAppVO[] matterAppVOs = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPKs(appPks.toArray(new String[0]));
		if (matterAppVOs == null || matterAppVOs.length != appPks.size()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0034")/*@res "�������뵥�Ѿ���ɾ�����޷���д����"*/);
		}

		//���������뵥�������ѹر�״̬
		checkCloseStatus(matterAppVOs);
		
		// ����������뵥�����뵥detail
		for (AggMatterAppVO appVo : matterAppVOs) {
			// hash��aggvo
			appPk2AppVoMap.put(appVo.getPrimaryKey(), appVo);
			 
			MtAppDetailVO[] childrenVO = appVo.getChildrenVO();
			if (ArrayUtils.isEmpty(childrenVO)) {
				continue;
			}

			for (MtAppDetailVO mtAppDetailVO : childrenVO) {
				// ��ϣ�� ��ϸ��vo
				appDetailPk2VoMap.put(mtAppDetailVO.getPrimaryKey(),mtAppDetailVO);
			}
		}
		
		return matterAppVOs;
	}

	@SuppressWarnings("unchecked")
	private void checkCtrlRule(AggMatterAppVO[] matterAppVOs, MtappCtrlBusiVO[] ctrlvos) throws BusinessException{
		// �ܿ��ƹ������ʱ����ѯ���ƹ���ֻУ����ƶ���
		if(isCtrlByRule){

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

			// ������ά�ȷ���ҵ������detail����ø�������ά�Ⱥϼ�ִ��ֵ��ͬʱ���˲��ܹ�����Ƶ�ҵ������
			for (MtappCtrlBusiVO busiVo : ctrlvos) {
				AggMatterAppVO aggMatterAppVO = appPk2AppVoMap.get(busiVo.getMatterAppPK());

				MatterAppVO mtappvo = aggMatterAppVO.getParentVO();

				// У����ƶ���У�鵱ǰҵ�����ݽ��������Ƿ������뵥�Ŀ��ƶ�����
				List<String> ctrlBillList = key2CtrlBillVosMap.get(helper.getMtappCtrlRuleKey(mtappvo));
				if (ctrlBillList == null||!ctrlBillList.contains(busiVo.getTradeType())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0035")/*@res "������������ƹ������á����ƶ����Ѿ���ɾ�����޷���д����"*/);
				}
			}
		}
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
	 * ��������У��
	 *
	 * @param apppk2BusiSumAmoutMap 
	 * @param apppk2jkSumAmoutMap 
	 * @param apppk2MtappCtrlBusiVOMap 
	 */
	private List<String> validateBusiData(Map<String, UFDouble> apppk2BusiSumAmoutMap, Map<String, UFDouble> apppk2jkSumAmoutMap, Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap) throws BusinessException {
		List<String> errorMsgList = new ArrayList<String>();
		
		// �������Կ���ά�ȵ�ҵ�����ݡ���Ϊ����ά�ȵ�key�а��������뵥��pk�����Խ�����������Ψһȷ��һ�����뵥
		for (Entry<String, UFDouble> entry : apppk2BusiSumAmoutMap.entrySet()) {
			String key = entry.getKey();
			MatterAppVO vo = appPk2AppVoMap.get(key).getParentVO();
			
			// ************ У�� ************
			// ���Կ���У�飺�������뵥������ҵ�񵥾�ִ������ֻУ��ԭ��
			// �����������ҵ�����ݿ��ƣ����������д����� = ���뵥�ܽ�� - ��ִ������
			UFDouble max_amount = vo.getOrig_amount().sub(getDoubleValue(vo.getExe_amount()));
			UFDouble jk_amount = apppk2jkSumAmoutMap.get(key);
			if(jk_amount != null){
				if (max_amount.compareTo(jk_amount) < 0) {
					// ��װ������Ϣ
					errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
					continue;
				}
			}
			
			UFDouble exe_amount_total = entry.getValue();
			if(max_amount.compareTo(exe_amount_total) >= 0){
				// ��ִ���������������ܽ����������ð��ճ������߼��������ܽ�Χ��ִ�м���
				List<MtappCtrlBusiVO> busilist = apppk2MtappCtrlBusiVOMap.get(key);
				for (MtappCtrlBusiVO mtappCtrlBusiVO : busilist) {
					mtappCtrlBusiVO.setExceedEnable(false);
				}
				continue;
			}
			
			// ��ִ�н��Ŀ��ƣ����������д����� = �����д����� - ��ִ������
			if(vo.getMax_amount() != null){
				max_amount = vo.getMax_amount().sub(getDoubleValue(vo.getExe_amount()));
			}
			if (max_amount.compareTo(exe_amount_total) < 0) {
				// ��װ������Ϣ
				errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
				break;
			}

			if (errorMsgList.size() > 0) {
				// ���ڴ�����Ϣ������װ��д����
				break;
			}
		}
		return errorMsgList;
	}

	/**
	 * �����뵥��ϸ�У���д����
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
		// ************ 1  ת��ҵ������Ϊͳһ�ṹMtappCtrlBusiVO���Ұ��������뵥pk����ҵ������ ************
		Map<String, UFDouble> appdetailpkpk2BusiSumAmoutMap = new HashMap<String, UFDouble>();// �������뵥��ϸ��pk�������ҵ�񵥾ݺϼƻ�дִ�������
		Map<String, UFDouble> appdetailpk2JKSumAmoutMap = new HashMap<String, UFDouble>();// �������뵥pk������Ľ��ϼƻ�дִ���������������룩
		MtappCtrlBusiVO[] ctrlvos = new MtappCtrlBusiVO[vos.length];
		Set<String> appPKs = new HashSet<String>();
		List<MtappCtrlBusiVO>[] preDataVOs = initDataListArray();//����дԤռ����ҵ������
		List<MtappCtrlBusiVO>[] exeDataVOs = initDataListArray();//����дִ������ҵ������
		for (int i = 0; i < ctrlvos.length; i++) {
			ctrlvos[i] = new MtappCtrlBusiVO(vos[i]);
			
			MtappCtrlBusiVO busivo = ctrlvos[i];
			String appPk = busivo.getMatterAppPK();
			appPKs.add(appPk);
			String appDetailPk = busivo.getMatterAppDetailPK();
			// �����дԤռ��ִ�е�ҵ������
			int direction = getDataDirection(busivo);
			if(IMtappCtrlBusiVO.DataType_pre.equals(busivo.getDataType())){
				preDataVOs[direction].add(busivo);
			}else{
				exeDataVOs[direction].add(busivo);
				appdetailpkpk2BusiSumAmoutMap.put(appDetailPk, getDoubleValue(appdetailpkpk2BusiSumAmoutMap.get(appDetailPk)).add(getDoubleValue(busivo.getAmount())));
				if(!busivo.isExceedEnable()){
					// ������ά��ʱ�Ľ���������������������
					appdetailpk2JKSumAmoutMap.put(appDetailPk, getDoubleValue(appdetailpk2JKSumAmoutMap.get(appDetailPk)).add(getDoubleValue(busivo.getAmount())));
				}
			}
		}
		// ************ �� ��ѯ���ݣ��������뵥 ************
		AggMatterAppVO[] matterAppVOs = queryAppVOsAndCtrlData(appPKs); 
		// ************ �� ��ѯ���ƹ������У�� ************
		if(isCtrlByRule){
			// ֻУ����ƶ���
			checkCtrlRule(matterAppVOs,ctrlvos);
		}
		
		// ************ �� ��ϸ����У�� ************
		List<String> errorMsgList = validateBusiDataByDetail(appdetailpkpk2BusiSumAmoutMap,appdetailpk2JKSumAmoutMap);
		
		// ************ �� ��װ������Ϣ ************
		if (errorMsgList.size() > 0) {
			errVo.setControlinfos(errorMsgList.toArray(new String[0]));
			return errVo;
		}
		
		if (isWriteBack) {
			// ************ �� ��ѯ����װ �����д���뵥��ҵ������ִ�м�¼************
			// ִ�м�¼map<���뵥pk+ҵ��������ϸpk,List<MtapppfVO>>
			Map<String, List<MtapppfVO>> appPfMap = helper.constructMtappPfMap(appPk2AppVoMap.keySet().toArray(new String[0]));

			// �����뵥��ϸ�У���д���뵥
			writeBackMtappVOByDetail(preDataVOs,exeDataVOs,appPfMap);
			// �������뵥���ұ���ִ�м�¼
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
		// ************ ʮ  ����������뵥ִ�м�¼ ************
		// �����������ڸ������뵥��ϸ���ϵ�ִ����ռ�������keyΪ������ϸ��pk
		Map<String, UFDouble> jkExeMap = new HashMap<String, UFDouble>();
		saveMtappPfVOs(appPfMap,jkExeMap);
		
		// ʮ��  ��װ�������뵥���ӱ���ּ����㣬����ϼ�ֵ ���Զ��رգ�����
		List<AggMatterAppVO>[] closeAndopenList= helper.setTotalAmount(matterAppVOs,jkExeMap);
		
		// ʮ��  ����������뵥
		MDPersistenceService.lookupPersistenceService().saveBill(ErMdpersistUtil.getNCObject(matterAppVOs));
		
		// ����ر��������뵥
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
	
	/**
	 * �����뵥��ϸ�п���У��
	 *
	 * @param appDetailpk2BusiSumAmoutMap 
	 * @param appdetailpk2JKSumAmoutMap 
	 */
	private List<String> validateBusiDataByDetail(Map<String, UFDouble> appDetailpk2BusiSumAmoutMap, Map<String, UFDouble> appdetailpk2JKSumAmoutMap) throws BusinessException {
		List<String> errorMsgList = new ArrayList<String>();
		
		// �������Կ���ά�ȵ�ҵ�����ݡ���Ϊ����ά�ȵ�key�а��������뵥��pk�����Խ�����������Ψһȷ��һ�����뵥
		for (Entry<String, UFDouble> entry : appDetailpk2BusiSumAmoutMap.entrySet()) {
			String key = entry.getKey();
			MtAppDetailVO vo = appDetailPk2VoMap.get(key);
			if(vo == null){
				throw new BusinessException("�������뵥����д��ϸ�в����ڣ���������");
			}
			if (vo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(vo.getCloseman()))) {
				//�������뵥��ϸ�йرգ����ܻ�д
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0036")
						/*@res "�������뵥"*/ + "[" + vo.getBillno() + "] " +"��ϸ��"+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0037")/*@res "�Ѿ��رգ��޷���д����"*/);
			}
			
			
			// ************ У�� ************
			// ���Կ���У�飺�������뵥������ҵ�񵥾�ִ������ֻУ��ԭ��
			// �����������ҵ�����ݿ��ƣ����������д����� = ���뵥�ܽ�� - ��ִ������
			UFDouble max_amount = vo.getOrig_amount().sub(getDoubleValue(vo.getExe_amount()));
			UFDouble jk_amount = appdetailpk2JKSumAmoutMap.get(key);
			if(jk_amount != null){
				if (max_amount.compareTo(jk_amount) < 0) {
					// ��װ������Ϣ
					errorMsgList.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
					break;
				}
			}
			
			// ��ִ�н��Ŀ��ƣ����������д����� = �����д����� - ��ִ������
			if(vo.getMax_amount() != null){
				max_amount = vo.getMax_amount().sub(getDoubleValue(vo.getExe_amount()));
			}
			if (max_amount.compareTo(entry.getValue()) < 0) {
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
	/**
	 * �����뵥��ϸ�У���д���뵥
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
		// ************ ��  �����д���� ************
		writeBackAppVoNegative(preDataVOs, exeDataVOs, appPfMap);
		
		// ************ ��  ��ϸԤռ����д�����ܿɻ�д������ ************
		// ��д�м��Ԥռ��
		helper.writeBackAppVoPreDataByDetail(preDataVOs[2],appDetailPk2VoMap,appPfMap);
		// ��дҵ������Ԥռ��
		helper.writeBackAppVoPreDataByDetail(preDataVOs[3],appDetailPk2VoMap,appPfMap);
		
		// ************ ��  ��ϸִ������д ************
		// ��д�м��ִ����
		helper.writeBackAppVoExeDataByDetail(exeDataVOs[2],appDetailPk2VoMap,appPfMap);
		// ��дҵ������ִ����
		helper.writeBackAppVoExeDataByDetail(exeDataVOs[3],appDetailPk2VoMap,appPfMap);
		return appPfMap;
	}
}