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
	 * ����ά������VO�����еĶ���
	 * Map<ma_tradetype+busi_tradetype+pk_org, Map<ctrlfield, busifield>>
	 */
	private Map<String, Map<String, String>> ctrlFiledMap;

	/**
	 * ���ݽ������ͺ���֯�����γ�key
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
	 * ����ά�ȷ��鹹��ҵ������map
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
	 * �ϼ�ҵ������ִ����
	 *
	 * @param fieldSum
	 * @param busiVo
	 * @param key
	 * @author: wangyhh@ufida.com.cn
	 */
	public void calculateExeData(Map<String, UFDouble> fieldSum, MtappCtrlBusiVO busiVo, String key) {
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
	 * �ϼƷ������뵥���
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

	public UFDouble getDoubleValue(Object d) {
		return d == null ? UFDouble.ZERO_DBL : (UFDouble) d;
	}

	/**
	 * ���ݸ����ֶ�ƴ��key fieldcode+fieldValue+����+appPk+pk_org δ���ÿ���ά�ȣ��������ƣ�ȡappPkΪkey
	 *
	 * @param key2CtrlFieldVosMap
	 * @param vo
	 * @param orgTradeTypekey
	 * @param appPk
	 * @param isAllFiled
	 * @param mattParanrVo���ݷ������뵥��ͷ�ֶ�ά�ȿ���;ҵ�����ݴ�null����
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public String getFieldKey(List<MtappCtrlfieldVO> ctrlFieldList, Object vo, boolean isAllFiled,MatterAppVO mattParanrVo) {
		StringBuffer keybuf = new StringBuffer();
		if (ctrlFieldList != null) {
			for (MtappCtrlfieldVO mtappCtrlfieldVO : ctrlFieldList) {
				// ����ά���ֶ�ƴ��
				if (isAllFiled || (!mtappCtrlfieldVO.getAdjust_enable().booleanValue())) {
					keybuf.append(mtappCtrlfieldVO.getFieldcode());
					keybuf.append(getAttributeValue(vo, mtappCtrlfieldVO.getFieldcode(),mattParanrVo));
				}
			}
		}

		keybuf.append(mattParanrVo.getPrimaryKey());
//		keybuf.append(getAttributeValue(vo, "pk_org"));�ݲ�������֯

		return keybuf.toString();
	}

	/**
	 * �������ͻ�ȡ�ֶ�ֵ
	 *
	 * @param vo
	 * @param keybuf
	 * @param attr
	 * @param mattParanrVo
	 * @author: wangyhh@ufida.com.cn
	 */
	private Object getAttributeValue(Object vo, String attr,MatterAppVO mattParanrVo) {
		if (vo instanceof IMtappCtrlBusiVO) {
			IMtappCtrlBusiVO busivo = (IMtappCtrlBusiVO) vo;
			
			String srcattr = null;
			String ctrlFiledMapKey = getCtrlFiledMapKey(mattParanrVo, ((IMtappCtrlBusiVO) vo).getTradeType());
			if (ctrlFiledMapKey != null) {
				Map<String, String> voKeyMap = ctrlFiledMap.get(ctrlFiledMapKey);
				srcattr = voKeyMap.get(attr);
			}
			
			if(srcattr == null){
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0118")/*@res ""����ά�����õ��ֶα�������ά�ȶ���""*/);
			}
			return busivo.getAttributeValue(srcattr);
		} else if (vo instanceof MtAppDetailVO) {
			String[] split = StringUtil.split(attr, ".");
			if(split.length == 1 && mattParanrVo != null){
				return mattParanrVo.getAttributeValue(attr);
			}
			return ((MtAppDetailVO) vo).getAttributeValue(split[1]);
		} else {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0039")/*@res "��֧�ֵ�����"*/);
		}
	}

	/**
	 * һ�λ�д���ȿ��Ƿ����д����ȫ��ά�Ȼ�д��������
	 * 
	 * @param key2CtrlFieldVosMap
	 * @param appPk2AppVoMap
	 * @param allAdjustAppVoMap
	 * @param appPfMap
	 * @param unAdjustAppList
	 * @param busiList
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param extraAmountMap 
	 */
	private void writeBackDetail_new(List<MtappCtrlfieldVO> ctrlFieldList,AggMatterAppVO aggMatterAppVO, Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, List<MtapppfVO>> appPfMap,List<MtAppDetailVO> unAdjustAppList, List<MtappCtrlBusiVO> busiList, Map<String, UFDouble> extraAmountMap) throws BusinessException {
		//��Ҫ������ҵ������
		List<MtappCtrlBusiVO> adjBusiList = new ArrayList<MtappCtrlBusiVO>(); 
		MatterAppVO mtappvo = aggMatterAppVO.getParentVO();
		
		for (MtappCtrlBusiVO busivo : busiList) {
			// ҵ����      
			UFDouble exe_amount = getDoubleValue(busivo.getExeData());
			UFDouble pre_amount = getDoubleValue(busivo.getPreData());
			// δ����ҵ����
			UFDouble[] remaindAmount = new UFDouble[]{exe_amount,pre_amount,UFDouble.ZERO_DBL};
			if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0) {
				// ȫ��ά��ƥ��
				String allFieldKey = getFieldKey(ctrlFieldList, busivo, true, mtappvo);
				List<MtAppDetailVO> allFieldAppVoList = allAdjustAppVoMap.get(allFieldKey);
				if (allFieldAppVoList != null) {
					for (MtAppDetailVO appDetailVo : allFieldAppVoList) {
						if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
							continue;
						}

						MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo,appPfMap,mtappvo.getPk_tradetype());
						// ��������Ϊ�������ͷŵĽ��
						remaindAmount[2] = extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getDetailBusiPK());
						// ��д��ϸ��
						remaindAmount = writeBackDetailAppVo(remaindAmount, appDetailVo, appPfVo, busivo, UFBoolean.FALSE);
						if (UFDouble.ZERO_DBL.equals(remaindAmount[0]) && UFDouble.ZERO_DBL.equals(remaindAmount[1])) {
							break;
						}
					}
				}
			}

			// ׼����������
			if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0) {
				busivo.setExe_data(remaindAmount[0]);
				busivo.setPre_data(remaindAmount[1]);
				adjBusiList.add(busivo);
			}
		}
		
		//����
		if(adjBusiList.size() > 0){
			// ҵ����      
			for (MtappCtrlBusiVO busivo : adjBusiList) {
				UFDouble exe_amount = getDoubleValue(busivo.getExeData());
				UFDouble pre_amount = getDoubleValue(busivo.getPreData());
				// δ����ҵ����
				UFDouble[] remaindAmount = new UFDouble[]{exe_amount,pre_amount,UFDouble.ZERO_DBL};
				// ˳��������Կ���ά��
				for (MtAppDetailVO appDetailVo : unAdjustAppList) {
					if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
						continue;
					}

					MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
					
					// ��������Ϊ�������ͷŵĽ��
					remaindAmount[2] = extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getDetailBusiPK());
					// ��д��ϸ�н��
					remaindAmount = writeBackDetailAppVo(remaindAmount, appDetailVo, appPfVo,busivo,UFBoolean.TRUE);
					if (UFDouble.ZERO_DBL.equals(remaindAmount[0]) && UFDouble.ZERO_DBL.equals(remaindAmount[1])) {
						break;
					}
				}
				//Ԥռ�������Ƶ��ݱ���
				if (remaindAmount[0].compareTo(UFDouble.ZERO_DBL) != 0 || remaindAmount[1].compareTo(UFDouble.ZERO_DBL) != 0 ) {
//					throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0036")/*@res "�������뵥"*/ + aggMatterAppVO.getParentVO().getBillno() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0040")/*@res ""�˵��ݽ�������ķ��������޸Ľ�������������""*/);
					throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
				}
			}
		}
	}
	
	/**
	 * ��д����������(Ԥռ��ִ�У����=���-ִ��) ��װ����������ִ�м�¼
	 * 
	 * �����д��ҵ��ִ�����ͷ������뵥���Ƚϣ�����Ϊ��
	 * �����д��ҵ��ִ�����ͷ������뵥ִ�����Ƚϣ�ִ��������Ϊ��
	 * 
	 * @param exe_amount
	 *            ҵ������{ִ�н��,Ԥռ���}
	 * @param appDetailVo
	 * @param appPfVo
	 * @param iMtappCtrlBusiVO
	 * @return ҵ������ʣ��������
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	protected UFDouble[] writeBackDetailAppVo(UFDouble[] exe_amount, MtAppDetailVO appDetailVo, MtapppfVO appPfVo, IMtappCtrlBusiVO iMtappCtrlBusiVO, UFBoolean isAdjust) throws BusinessException {// ���λ�дʣ����
		if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
	    			getStrByID("201212_0","0201212-0095")/*@res ""����д�����뵥��ϸ���Ѿ��رգ��޷���д�����ֹ�ȡ���ر�""*/);
		}
	
		//		// ���������{ִ������Ԥռ��}
//		UFDouble[] remaindAmount = new UFDouble[] { UFDouble.ZERO_DBL, UFDouble.ZERO_DBL };
//		// ҵ�����ݿɻ�д���
//		UFDouble[] enableAmount = exe_amount;
		if (exe_amount[0].equals(UFDouble.ZERO_DBL) && exe_amount[1].equals(UFDouble.ZERO_DBL)) {
			return exe_amount;
		}
		// ҵ�����ݿɻ�д���
		UFDouble[] enableAmount = new UFDouble[] { UFDouble.ZERO_DBL, UFDouble.ZERO_DBL,UFDouble.ZERO_DBL };
		// Ԥռ��ֱ��ȫ����д����ǰ��ϸ��
		enableAmount[0] = exe_amount[0];
		enableAmount[1] = exe_amount[1];
		enableAmount[2] = exe_amount.length ==3 ? exe_amount[2]:UFDouble.ZERO_DBL;
		
		exe_amount[1] = UFDouble.ZERO_DBL;
		
//		if(isAdjust.booleanValue()){
//			//��������дԤռ��
//			enableAmount[1] = UFDouble.ZERO_DBL;
//			remaindAmount[1] = exe_amount[1];
//		}
		
		// �������뵥���
		UFDouble rest_amount = appDetailVo.getRest_amount();
		UFDouble appRest_value = getDoubleValue(rest_amount);
		// �������뵥ִ����
		UFDouble appExe_value = getDoubleValue(appDetailVo.getExe_amount());
		// ҵ��ִ�����Ľ��
		UFDouble busiAmount = exe_amount[0];
		if(busiAmount.compareTo(UFDouble.ZERO_DBL) >= 0){
			//�����д,����Ϊ��
			UFDouble detailexe_amount = busiAmount.compareTo(appRest_value) >0?appRest_value:busiAmount;
			enableAmount[0] = detailexe_amount;
			exe_amount[0] = busiAmount.sub(detailexe_amount);
			
		}else{
			//FIXME �����д,ִ��������Ϊ��
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

		// ��������㼰��װִ�м�¼
		convertMutiAmount(appDetailVo, appPfVo, iMtappCtrlBusiVO, isAdjust, enableAmount);
		
		return exe_amount;
	}

	/**
	 * ��������㼰��װִ�м�¼
	 * 
	 * @param appDetailVo
	 * @param appPfVo
	 * @param iMtappCtrlBusiVO
	 * @param isAdjust
	 * @param enableAmount
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	protected void convertMutiAmount(MtAppDetailVO appDetailVo, MtapppfVO appPfVo, IMtappCtrlBusiVO iMtappCtrlBusiVO, UFBoolean isAdjust, UFDouble[] enableAmount) throws BusinessException {
		String pk_group = iMtappCtrlBusiVO.getPk_group();
		String pk_org = iMtappCtrlBusiVO.getPk_org();
		UFDate billDate = iMtappCtrlBusiVO.getBillDate();
		String currency = iMtappCtrlBusiVO.getCurrency();
		UFDouble[] currInfo = iMtappCtrlBusiVO.getCurrInfo();
		
		// ����ִ�м�¼�ܷ��ý��Ҹ��ݵ�ǰҵ���������»��ʽ������㱾��
		UFDouble totalExeamount = getDoubleValue(appPfVo.getExe_amount()).add(getDoubleValue(enableAmount[0]));
		UFDouble totalPreamount = getDoubleValue(appPfVo.getPre_amount()).add(getDoubleValue(enableAmount[1]));
		// ����ԭ�� = ������ִ����  �� �����ݵ�ǰ���+�Ѿ���ִ�е�ִ����+�����ͷŵĽ� ��Сֵ
		UFDouble fy_amount = totalExeamount.add(totalPreamount);
		UFDouble last_rest_amount = getDoubleValue(appDetailVo.getRest_amount()).add(getDoubleValue(appPfVo.getExe_amount())).add(getDoubleValue(enableAmount[2]));
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
		
		// ����ִ�м�¼��ִ�������Ҹ��ݵ�ǰҵ���������»��ʽ������㱾��
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
		// ����ִ�м�¼��Ԥռ�����Ҹ��ݵ�ǰҵ���������»��ʽ������㱾��
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
		
		// �������뵥��ϸ��ִ������Ԥռ�����������Ӧ����
		try {
			UFDouble[] exeAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency, enableAmount[0], currInfo);
			appDetailVo.setOrg_exe_amount(getDoubleValue(appDetailVo.getOrg_exe_amount()).add(exeAmounts[1]));
			appDetailVo.setGroup_exe_amount(getDoubleValue(appDetailVo.getGroup_exe_amount()).add(exeAmounts[2]));
			appDetailVo.setGlobal_exe_amount(getDoubleValue(appDetailVo.getGlobal_exe_amount()).add(exeAmounts[3]));
			
			UFDouble[] preAmounts = getAmountsByOriAmount(pk_group, pk_org, billDate, currency, enableAmount[1], currInfo);
			appDetailVo.setOrg_pre_amount(getDoubleValue(appDetailVo.getOrg_pre_amount()).add(preAmounts[1]));
			appDetailVo.setGroup_pre_amount(getDoubleValue(appDetailVo.getGroup_pre_amount()).add(preAmounts[2]));
			appDetailVo.setGlobal_pre_amount(getDoubleValue(appDetailVo.getGlobal_pre_amount()).add(preAmounts[3]));
			
			// �������=�ܽ��-��ִ��
			UFDouble amount = getDoubleValue(appDetailVo.getOrig_amount()).sub(getDoubleValue(appDetailVo.getExe_amount()));
			appDetailVo.setRest_amount(amount);
			appDetailVo.setOrg_rest_amount(getDoubleValue(appDetailVo.getOrg_amount()).sub(getDoubleValue(appDetailVo.getOrg_exe_amount())));
			appDetailVo.setGroup_rest_amount(getDoubleValue(appDetailVo.getGroup_amount()).sub(getDoubleValue(appDetailVo.getGroup_exe_amount())));
			appDetailVo.setGlobal_rest_amount(getDoubleValue(appDetailVo.getGlobal_amount()).sub(getDoubleValue(appDetailVo.getGlobal_exe_amount())));
			
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		if(isAdjust != null){
			appPfVo.setIs_adjust(isAdjust);
		}
		
		if (appPfVo.getPrimaryKey() == null) {
			appPfVo.setStatus(VOStatus.NEW);
		}else {
			appPfVo.setStatus(VOStatus.UPDATED);
		}
	}

	/**
	 * ����ִ�м�¼map<���뵥pk+ҵ��������ϸpk,MtapppfVO>
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
	 * �����µ�ִ�м�¼
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
	 * ��ѯ�������뵥ִ�м�¼ ���ݷ������뵥pk
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
	 * �ӱ���ּ����㣬����ϼ�ֵ���Զ��رա��Զ�����
	 *
	 * @param matterAppVOs
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 * @param jkExeMap 
	 * @param closeMap 
	 * 
	 * @return ���ش������͹رյķ������뵥
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

			// �ϼ�ֵ�� ˳�򣺽��-���-ִ����-Ԥռ����ԭ�ң���֯�����ţ�ȫ�֣�
			UFDouble[] amounts = new UFDouble[12];
			for (int i = 0; i < amounts.length; i++) {
				amounts[i] = UFDouble.ZERO_DBL;
			}

			String[] detailItemName = new String[] { /*MtAppDetailVO.ORIG_AMOUNT, MtAppDetailVO.ORG_AMOUNT, MtAppDetailVO.GROUP_AMOUNT, MtAppDetailVO.GLOBAL_AMOUNT, */
					MtAppDetailVO.REST_AMOUNT, MtAppDetailVO.ORG_REST_AMOUNT, MtAppDetailVO.GROUP_REST_AMOUNT, MtAppDetailVO.GLOBAL_REST_AMOUNT,
					MtAppDetailVO.EXE_AMOUNT, MtAppDetailVO.ORG_EXE_AMOUNT, MtAppDetailVO.GROUP_EXE_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT,
					MtAppDetailVO.PRE_AMOUNT, MtAppDetailVO.ORG_PRE_AMOUNT, MtAppDetailVO.GROUP_PRE_AMOUNT, MtAppDetailVO.GLOBAL_PRE_AMOUNT };

			//�ر�״̬��
			int closeNum = 0;
			List<MtAppDetailVO> closeList = new ArrayList<MtAppDetailVO>();
			List<MtAppDetailVO> openList = new ArrayList<MtAppDetailVO>();
			for (MtAppDetailVO mtAppDetailVO : childrenVOs) {
				// ����
//				resetAmountByOriAmount(detailItemName, mtAppDetailVO);

				// �ϼ�
				for (int i = 0; i < amounts.length; i++) {
					UFDouble doubleValue = getDoubleValue(mtAppDetailVO.getAttributeValue(detailItemName[i]));
//					if(MtAppDetailVO.REST_AMOUNT.equals(detailItemName[i]) && doubleValue.compareTo(UFDouble.ZERO_DBL) < 0){
//						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0038")/*@res "�˵��ݽ�������ķ��������޸Ľ�������������"*/);
//					}
					amounts[i] = amounts[i].add(doubleValue);
				}

				// ���Զ��رա�����
				autoClose(jkExeMap, closeList,openList, mtAppDetailVO);
				
				//�ر�����
				if(ErmMatterAppConst.CLOSESTATUS_Y == mtAppDetailVO.getClose_status().intValue()){
					closeNum++;
				}
			}

			// ���úϼ�ֵ
			String[] headItemName = new String[] { /*MatterAppVO.ORIG_AMOUNT, MatterAppVO.ORG_AMOUNT, MatterAppVO.GROUP_AMOUNT, MatterAppVO.GLOBAL_AMOUNT,*/
					MatterAppVO.REST_AMOUNT, MatterAppVO.ORG_REST_AMOUNT, MatterAppVO.GROUP_REST_AMOUNT, MatterAppVO.GLOBAL_REST_AMOUNT,
					MatterAppVO.EXE_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT, MatterAppVO.GROUP_EXE_AMOUNT, MatterAppVO.GLOBAL_EXE_AMOUNT,
					MatterAppVO.PRE_AMOUNT, MatterAppVO.ORG_PRE_AMOUNT, MatterAppVO.GROUP_PRE_AMOUNT, MatterAppVO.GLOBAL_PRE_AMOUNT };
			MatterAppVO parentVO = vo.getParentVO();
			for (int i = 0; i < headItemName.length; i++) {
				parentVO.setAttributeValue(headItemName[i], amounts[i]);
				parentVO.setStatus(VOStatus.UPDATED);
			}

			// ��װ�������رյķ������뵥���� 
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
			// ���뵥��ϸ�б����˽���ĵ���ȫ��ռ�ú��Զ��ر�
			closeList.add((MtAppDetailVO) mtAppDetailVO.clone());
			
			
		}else{
			if(ErmMatterAppConst.CLOSESTATUS_Y == mtAppDetailVO.getClose_status().intValue()&&INCSystemUserConst.NC_USER_PK.equals(mtAppDetailVO.getCloseman())){
//				mtAppDetailVO.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);
//				mtAppDetailVO.setCloseman(null);
//				mtAppDetailVO.setClosedate(null);
				// �Զ�����
				openList.add((MtAppDetailVO) mtAppDetailVO.clone());
				
			}
		}
	}
	

	/**
	 * ����ԭ�����㱾��
	 *
	 * @param pk_group ����
	 * @param pk_org ��֯
	 * @param busiDate����
	 * @param pk_currenTypeԭ�ұ���
	 * @param oriAmountԭ�ҽ��
	 * @param orgRate ���һ���
	 * @param groupRate���Ż���
	 * @param globalRateȫ�ֻ���
	 * @return [0] ԭ�ҽ�[1] ���ҽ� [2] ���ű��ҽ�� [3] ȫ�ֱ��ҽ��
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
//			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0041")/*@res "ͬ��ִ�м�¼�쳣������ϵ����Ա"*/);
		}

		Set<String[]> keySet = new HashSet<String[]>();
		for (MtapppfVO vo : appPfVos) {
			keySet.add(new String[]{vo.getPk_mtapp_detail(),vo.getPk_djdl()});
		}

		StringBuffer sqlBuf = new StringBuffer();
		for (String[] param : keySet) {
			if(param == null || param.length != 2 || param[0] == null || param[1] == null){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0028")/*@res "����������������ϵ����Ա"*/);
			}
			if (sqlBuf.length() != 0) {
				sqlBuf.append(" or ");
			}
			sqlBuf.append(" (" + MtappbillpfVO.PK_MTAPP_DETAIL + " = '" + param[0] + "' AND " + MtappbillpfVO.PK_DJDL  + " = '" + param[1] + "') ");
		}


		String deleteSql = " DELETE FROM ER_MTAPP_BILLPF WHERE " + sqlBuf.toString();
		String insertSql = " INSERT INTO ER_MTAPP_BILLPF(PK_MATTERAPP,PK_MTAPP_DETAIL,PK_DJDL,BUSISYS,PK_GROUP,PK_ORG,EXE_AMOUNT,PRE_AMOUNT) " +
				" SELECT PK_MATTERAPP,PK_MTAPP_DETAIL,PK_DJDL,BUSISYS,PK_GROUP,PK_ORG,SUM(coalesce(EXE_AMOUNT,0)) AS EXE_AMOUNT,SUM(coalesce(PRE_AMOUNT,0)) AS PRE_AMOUNT FROM ER_MTAPP_PF " +
				" WHERE " + sqlBuf.toString() + " GROUP BY PK_MATTERAPP,PK_MTAPP_DETAIL,PK_DJDL,BUSISYS,PK_GROUP,PK_ORG ";

		new BaseDAO().executeUpdate(deleteSql);
		new BaseDAO().executeUpdate(insertSql);

	}
	
	/**
	 * ��д�������뵥ִ����
	 * 
	 * @param exeDataVOs ��дִ������ҵ������
	 * @param appPk2AppVoMap �������뵥vomap
	 * @param appDetailPk2VoMap ���뵥��ϸ��vomap
	 * @param key2CtrlFieldVosMap �������뵥����ά��
	 * @param appPfMap ҵ������ִ�м�¼��Ϣ
	 * @param allAdjustAppVoMap ȫ��ƥ��ά�ȵ����뵥��ϸmap
	 * @param unAdjustBusiVoMap 
	 * @param unAdjustAppVoMap 
	 * @throws BusinessException 
	 */
	public void writeBackAppVoExeData(List<MtappCtrlBusiVO>[] exeDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, MtAppDetailVO> appDetailPk2VoMap,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, List<MtappCtrlBusiVO>> unAdjustBusiVoMap, Map<String, List<MtAppDetailVO>> unAdjustAppVoMap)
			throws BusinessException {
	
		/**
		 * ���ε��ݶ�����õ����뵥��ϸ�н��
		 * Map<���뵥��ϸ��pk+���ε�����ϸpk�������ͷų��Ľ��(����)>
		 */
		Map<String, UFDouble> extraAmountMap = new HashMap<String, UFDouble>();
		
		// 3���м�����д����,������������ȫƥ���С����λ�д˳���ͷ����ν��
		writeBackAppVoExeContrastPositive(exeDataVOs, appPk2AppVoMap,
				key2CtrlFieldVosMap, appPfMap, allAdjustAppVoMap,appDetailPk2VoMap,extraAmountMap);
		// 4�������д���ݣ���Ҫ����������
		writeBackAppVoExePositive(unAdjustBusiVoMap,exeDataVOs[3], appPk2AppVoMap,
				key2CtrlFieldVosMap, appPfMap, allAdjustAppVoMap,unAdjustAppVoMap,extraAmountMap);

	}
	
	/**
	 * �����д�м��Ԥռ��
	 * 
	 * @param preDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param extraAmountMap 
	 * @param appDetailPk2VoMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoPreContrastPositive(List<MtappCtrlBusiVO>[] preDataVOs, Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap,Map<String, UFDouble> extraAmountMap, Map<String, MtAppDetailVO> appDetailPk2VoMap)
			throws BusinessException {
		
		// ���� ���뵥pk+ҵ��������ϸpk������������д��ҵ�񵥾�
		Map<String, MtappCtrlBusiVO> positiveBusiDataMap = new HashMap<String, MtappCtrlBusiVO>();
		for (MtappCtrlBusiVO busivo : preDataVOs[3]) {
			positiveBusiDataMap.put(busivo.getMatterAppPK()+busivo.getDetailBusiPK(), busivo);
		}
		for (MtappCtrlBusiVO busivo : preDataVOs[2]) {
			// ����м�����ε��ݵ���ȫƥ���м�¼
			MatterAppVO mtappvo = appPk2AppVoMap.get(busivo.getMatterAppPK()).getParentVO();
			MtappCtrlBusiVO forwardBusiVO = positiveBusiDataMap.get(busivo.getMatterAppPK()+ busivo.getForwardBusidetailPK());
			List<MtAppDetailVO> mtAppDetailList = null;
			UFDouble[] exe_amount = new UFDouble[]{UFDouble.ZERO_DBL,busivo.getPreData()};
			if(forwardBusiVO == null){
				// �м�����������������дԤռ��ֱ�ӻ�д�����λ�д��¼��Ӧ��һ��
				List<MtapppfVO> srcpflist= appPfMap.get(busivo.getMatterAppPK()+ busivo.getSrcBusidetailPK());
				// �ͷ����ε���ԭռ�õ����롣�����ͷ����ε�����Ҫռ�õ��У��ٰ�������ռ�������˳������ͷţ��м�������Ǹ���
				mtAppDetailList = new ArrayList<MtAppDetailVO>();
				for (MtapppfVO srcmtapppfVO : srcpflist) {
					MtAppDetailVO appDetailVo = appDetailPk2VoMap.get(srcmtapppfVO.getPk_mtapp_detail());
					mtAppDetailList.add(appDetailVo);
				}
			}else{
				// ��ȡ���ε��� ��ȫƥ��ά�ȶ�Ӧ�����뵥��ϸ�е�һ��
				List<MtappCtrlfieldVO> ctrlFieldList = key2CtrlFieldVosMap.get(getMtappCtrlRuleKey(mtappvo));
				// fieldcode+fieldValue+����+appPk+pk_org
				String allFieldKey = getFieldKey(ctrlFieldList, forwardBusiVO,true,mtappvo);
			    mtAppDetailList = allAdjustAppVoMap.get(allFieldKey);
			    if(mtAppDetailList == null || mtAppDetailList.isEmpty()){
			    	throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
			    			getStrByID("201212_0","0201212-0090")/*@res ""ҵ�񵥾���������뵥����ά�ȶ����ֶ�ֵ��һ�£����޸�ҵ�񵥾�""*/);
			    }
			}
			
			for (MtAppDetailVO appDetailVo : mtAppDetailList) {
				if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
					continue;
				}
				// ��ñ����ִ�м�¼
				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				// ��д�������뵥��ϸ�н��
				exe_amount = writeBackDetailAppVo(exe_amount, appDetailVo, appPfVo, busivo, UFBoolean.FALSE);
				// ��¼Ϊ���ε����ͷŵĽ��
				extraAmountMap.put(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK(),getDoubleValue(busivo.getPreData()).multiply(-1));
				// Ԥռ��ֱ��д����ȫƥ���еĵ�һ�У�д���ͽ�������
				break;
			}
			
			if(exe_amount[1].compareTo(UFDouble.ZERO_DBL) != 0){
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
		    			getStrByID("201212_0","0201212-0095")/*@res ""����д�����뵥��ϸ���Ѿ��رգ��޷���д�����ֹ�ȡ���ر�""*/);
			}
		}
	}
	
	/**
	 * �����д�м��ִ����
	 * 
	 * @param exeDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param appDetailPk2VoMap 
	 * @param extraAmountMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoExeContrastPositive(List<MtappCtrlBusiVO>[] exeDataVOs, Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, MtAppDetailVO> appDetailPk2VoMap, Map<String, UFDouble> extraAmountMap)
			throws BusinessException {
		
		// ���� ���뵥pk+ҵ��������ϸpk������������д��ҵ�񵥾�
		Map<String, MtappCtrlBusiVO> positiveBusiDataMap = new HashMap<String, MtappCtrlBusiVO>();
		for (MtappCtrlBusiVO busivo : exeDataVOs[3]) {
			positiveBusiDataMap.put(busivo.getMatterAppPK()+busivo.getDetailBusiPK(), busivo);
		}
		// ��װ�м���ʹ�õ����ε���ִ�м�¼,Ϊ��ֹӰ�����ε���ִ�м�¼clone��ʹ��
		List<MtapppfVO> srcpflist = new ArrayList<MtapppfVO>();
		// ���������뵥��ϸ�з������ε���ִ�м�¼
		Map<String, MtapppfVO> srcpfMap = new HashMap<String, MtapppfVO>();
		for (MtappCtrlBusiVO busivo : exeDataVOs[2]) {
			// ����м�����ε��ݵ�ִ�м�¼����Ҫ�������ε��ݻ�д���뵥��˳�����������
			List<MtapppfVO> temp_srcpflist= appPfMap.get(busivo.getMatterAppPK()+ busivo.getSrcBusidetailPK());
			// ���������뵥��ϸ�з������ε���ִ�м�¼
			for (MtapppfVO mtapppfVO : temp_srcpflist) {
				String key = mtapppfVO.getPk_mtapp_detail()+mtapppfVO.getBusi_detail_pk();
				if(srcpfMap.containsKey(key)){
					continue;
				}
				MtapppfVO mtapppfVO_c = (MtapppfVO) mtapppfVO.clone();
				srcpflist.add(mtapppfVO_c);
				srcpfMap.put(key, mtapppfVO_c);
			}
		}
		for (MtappCtrlBusiVO busivo : exeDataVOs[2]) {

			UFDouble remaindAmount = busivo.getExeData(); 
			MatterAppVO mtappvo = appPk2AppVoMap.get(busivo.getMatterAppPK()).getParentVO();
			// ����м�����ε��ݵ���ȫƥ���м�¼
			MtappCtrlBusiVO forwardBusiVO = positiveBusiDataMap.get(busivo.getMatterAppPK()+ busivo.getForwardBusidetailPK());
			if(forwardBusiVO != null){
				List<MtappCtrlfieldVO> ctrlFieldList = key2CtrlFieldVosMap.get(getMtappCtrlRuleKey(mtappvo));
				// fieldcode+fieldValue+����+appPk+pk_org
				String allFieldKey = getFieldKey(ctrlFieldList, forwardBusiVO,true,mtappvo);
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
							// �м���ִ�м�¼�Ƕ����ģ��������λ������λ���һ��
							MtapppfVO mtapppfVO = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
							// �����д�����ֻ�����д����ռ�õĽ��
							UFDouble exe_amount = getDoubleValue(srcmtapppfVO.getExe_amount());
							if(exe_amount.add(remaindAmount).compareTo(UFDouble.ZERO_DBL)>0){
								exe_amount = remaindAmount;
								remaindAmount = UFDouble.ZERO_DBL;
							}else{
								remaindAmount = exe_amount.add(remaindAmount);
								exe_amount = exe_amount.multiply(new UFDouble(-1));
							}
							UFDouble[] exeAmounts = new UFDouble[]{exe_amount,UFDouble.ZERO_DBL}; 
							// �ͷ����ε���ռ�õ�����
							exeAmounts = writeBackDetailAppVo(exeAmounts, appDetailVo, mtapppfVO, busivo, UFBoolean.FALSE);
							// ��¼�ͷŵĽ��
							extraAmountMap.put(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK(), (exe_amount.multiply(new UFDouble(-1))).add(
									getDoubleValue(extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK()))));
							// �ͷ����ε���ִ�м�¼ռ�õ�ִ����
							srcmtapppfVO.setExe_amount(srcmtapppfVO.getExe_amount().add(exe_amount));
							if(exeAmounts[0].compareTo(UFDouble.ZERO_DBL) != 0){
								throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
										getStrByID("201212_0","0201212-0095")/*@res ""����д�����뵥��ϸ���Ѿ��رգ��޷���д�����ֹ�ȡ���ر�""*/);
							}
							if(remaindAmount.compareTo(UFDouble.ZERO_DBL)==0){
								// ִ�����
								break;
							}
						}
					}
				}
			}
			
			// �ͷ����ε���ԭռ�õ����롣�����ͷ����ε�����Ҫռ�õ��У��ٰ�������ռ�������˳������ͷţ��м�������Ǹ���
			if(remaindAmount.compareTo(UFDouble.ZERO_DBL) !=0 ){
				// �����ε��ݻ�д���뵥˳������ͷ�����
				for (MtapppfVO srcmtapppfVO : srcpflist) {
					MtAppDetailVO appDetailVo = appDetailPk2VoMap.get(srcmtapppfVO.getPk_mtapp_detail());
					if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
						continue;
					}
					// �м���ִ�м�¼�Ƕ����ģ��������λ������λ���һ��
					MtapppfVO mtapppfVO = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
					// �����д�����ֻ�����д����ռ�õĽ��
					UFDouble exe_amount = getDoubleValue(srcmtapppfVO.getExe_amount());
					if(exe_amount.add(remaindAmount).compareTo(UFDouble.ZERO_DBL)>0){
						exe_amount = remaindAmount;
						remaindAmount = UFDouble.ZERO_DBL;
					}else{
						remaindAmount = exe_amount.add(remaindAmount);
						exe_amount = exe_amount.multiply(new UFDouble(-1));
					}
					UFDouble[] exeAmounts = new UFDouble[]{exe_amount,UFDouble.ZERO_DBL}; 
					// �ͷ����ε���ռ�õ�����
					exeAmounts = writeBackDetailAppVo(exeAmounts, appDetailVo, mtapppfVO, busivo, UFBoolean.FALSE);
					// ��¼�ͷŵĽ��
					extraAmountMap.put(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK(), (exe_amount.multiply(new UFDouble(-1))).add(
							getDoubleValue(extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getForwardBusidetailPK()))));
					// �ͷ����ε���ִ�м�¼ռ�õ�ִ����
					srcmtapppfVO.setExe_amount(srcmtapppfVO.getExe_amount().add(exe_amount));					
					if(exeAmounts[0].compareTo(UFDouble.ZERO_DBL) != 0){
						throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
								getStrByID("201212_0","0201212-0095")/*@res ""����д�����뵥��ϸ���Ѿ��رգ��޷���д�����ֹ�ȡ���ر�""*/);
					}
					if(remaindAmount.compareTo(UFDouble.ZERO_DBL)==0){
						// ִ�����
						break;
					}
				}
			}
			
		}
	}
	
	/**
	 * �����дִ����
	 * @param unAdjustBusiVoMap 
	 * 
	 * @param exeDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param unAdjustAppVoMap 
	 * @param extraAmountMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoExePositive(Map<String, List<MtappCtrlBusiVO>> unAdjustBusiVoMap, List<MtappCtrlBusiVO> exeDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, List<MtAppDetailVO>> unAdjustAppVoMap, Map<String, UFDouble> extraAmountMap)
			throws BusinessException {
		
		for (Entry<String, List<MtappCtrlBusiVO>> unAdjustEntry : unAdjustBusiVoMap.entrySet()) {
			List<MtAppDetailVO> unAdjustAppList = unAdjustAppVoMap.get(unAdjustEntry.getKey());
			AggMatterAppVO aggMatterAppVO = appPk2AppVoMap.get(unAdjustAppList.get(0).getPk_mtapp_bill());
			MatterAppVO mtappvo = aggMatterAppVO.getParentVO();
			
			writeBackDetail_new(key2CtrlFieldVosMap.get(getMtappCtrlRuleKey(mtappvo)), aggMatterAppVO, allAdjustAppVoMap, appPfMap, unAdjustAppList, unAdjustEntry.getValue(),extraAmountMap);
		}
	}

	/**
	 * ��д�������뵥Ԥռ��
	 * 
	 * @param preDataVOs ��дԤռ����ҵ������
	 * @param appPk2AppVoMap �������뵥vomap
	 * @param appDetailPk2VoMap ���뵥��ϸ��vomap
	 * @param key2CtrlFieldVosMap �������뵥����ά��
	 * @param appPfMap ҵ������ִ�м�¼��Ϣ
	 * @param allAdjustAppVoMap ȫ��ƥ��ά�ȵ����뵥��ϸmap
	 * @throws BusinessException 
	 */
	public void writeBackAppVoPreData(List<MtappCtrlBusiVO>[] preDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, MtAppDetailVO> appDetailPk2VoMap, Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap, Map<String, List<MtapppfVO>> appPfMap, Map<String, List<MtAppDetailVO>> allAdjustAppVoMap) throws BusinessException {
		/**
		 * ���ε��ݶ�����õ����뵥��ϸ�н��
		 * Map<���뵥��ϸ��pk+���ε�����ϸpk�������ͷų��Ľ��(����)>
		 */
		Map<String, UFDouble> extraAmountMap = new HashMap<String, UFDouble>();
		
		// 3���м�����д����
		writeBackAppVoPreContrastPositive(preDataVOs, appPk2AppVoMap,
				key2CtrlFieldVosMap, appPfMap, allAdjustAppVoMap,extraAmountMap,appDetailPk2VoMap);
		// 4�������д����
		writeBackAppVoPrePositive(preDataVOs[3], appPk2AppVoMap,
				key2CtrlFieldVosMap, appPfMap, allAdjustAppVoMap,extraAmountMap);
	}

	/**
	 * �����дԤռ��
	 * 
	 * @param preDataVOs
	 * @param appPk2AppVoMap
	 * @param key2CtrlFieldVosMap
	 * @param appPfMap
	 * @param allAdjustAppVoMap
	 * @param extraAmountMap 
	 * @throws BusinessException
	 */
	private void writeBackAppVoPrePositive(List<MtappCtrlBusiVO> preDataVOs,
			Map<String, AggMatterAppVO> appPk2AppVoMap,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap,
			Map<String, List<MtapppfVO>> appPfMap,
			Map<String, List<MtAppDetailVO>> allAdjustAppVoMap, Map<String, UFDouble> extraAmountMap)
			throws BusinessException {
		for (MtappCtrlBusiVO busivo : preDataVOs) {
			// ���ҵ������ȫ������ά��key
			MatterAppVO mtappvo = appPk2AppVoMap.get(busivo.getMatterAppPK()).getParentVO();
			// fieldcode+fieldValue+����+appPk+pk_org
			List<MtappCtrlfieldVO> ctrlFieldList = key2CtrlFieldVosMap.get(mtappvo.getPk_org() + mtappvo.getPk_tradetype());
			String allFieldKey = getFieldKey(ctrlFieldList, busivo,true,mtappvo);
			// ��ȡ��ȫƥ��ά�ȶ�Ӧ�����뵥��ϸ�е�һ��
			List<MtAppDetailVO> mtAppDetailList = allAdjustAppVoMap.get(allFieldKey);
			if(mtAppDetailList == null || mtAppDetailList.isEmpty()){
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
						getStrByID("201212_0","0201212-0090")/*@res ""ҵ�񵥾���������뵥����ά�ȶ����ֶ�ֵ��һ�£����޸�ҵ�񵥾�""*/);
			}
			
			for (MtAppDetailVO appDetailVo : mtAppDetailList) {
				if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
					continue;
				}
				// ��ñ����ִ�м�¼
				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				// ��д�������뵥��ϸ�н��
				UFDouble extraAmount = extraAmountMap.get(appDetailVo.getPrimaryKey()+busivo.getDetailBusiPK());
				writeBackDetailAppVo(new UFDouble[]{UFDouble.ZERO_DBL,busivo.getPreData(),getDoubleValue(extraAmount)}, appDetailVo, appPfVo, busivo, UFBoolean.FALSE);
				// Ԥռ��ֱ��д����ȫƥ���еĵ�һ�У�д���ͽ�������
				break;
			}
		}
	}

	
	/**
	 * ��ȡҵ����+���뵥��ϸ�е�ִ�м�¼
	 * 
	 * @param busivo
	 * @param appDetailVo
	 * @param appPfMap
	 * @param ma_tradetype 
	 * @return
	 */
	protected MtapppfVO getMtapppfVO_new(MtappCtrlBusiVO busivo,MtAppDetailVO appDetailVo,Map<String, List<MtapppfVO>> appPfMap, String ma_tradetype){
		MtapppfVO appPfVo = null;
		String pfKey = busivo.getMatterAppPK()+busivo.getDetailBusiPK();
		List<MtapppfVO> pflist = appPfMap.get(pfKey);
		if(pflist == null){
			pflist = new ArrayList<MtapppfVO>();
			appPfMap.put(pfKey, pflist);
		}
		for (MtapppfVO mtapppfVO : pflist) {
			if(mtapppfVO.getPk_mtapp_detail().equals(appDetailVo.getPrimaryKey())){
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
	 * �����д���뵥
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
			// ��ñ����ִ�м�¼
			String pfKey = busivo.getMatterAppPK()+busivo.getDetailBusiPK();
			List<MtapppfVO> pflist = appPfMap.get(pfKey);
			if(pflist != null){
				for (MtapppfVO mtapppfVO : pflist) {
					MtAppDetailVO detailvo = appDetailPk2VoMap.get(mtapppfVO.getPk_mtapp_detail());
					UFDouble exe_amount = UFDouble.ZERO_DBL;
					UFDouble pre_data = UFDouble.ZERO_DBL;
					// ���ջ�д�������ͣ��ͷ�ִ������
					if(IMtappCtrlBusiVO.DataType_exe.equals(busivo.getDataType())){
						exe_amount = getDoubleValue(mtapppfVO.getExe_amount()).multiply(-1);
					}else{
						pre_data = getDoubleValue(mtapppfVO.getPre_amount()).multiply(-1);
					}
					writeBackDetailAppVo(new UFDouble[]{exe_amount,pre_data}, detailvo, mtapppfVO, busivo, UFBoolean.FALSE);
				}
			}
		}
	}
	
	/**
	 * ��÷������뵥���ƹ���map��key
	 * 
	 * @param mtappvo
	 * @return
	 */
	public String getMtappCtrlRuleKey(MatterAppVO mtappvo) {
		return mtappvo.getPk_org() + mtappvo.getPk_tradetype();
	}
	
	/**
	 * ��ʼ������ά������VO�����еĶ���
	 * @param apppk2MtappCtrlBusiVOMap ���뵥PK�� busiVo�Ķ���
	 * @param matterAppVOs
	 * @param key2CtrlFieldVosMap ����ά��Map
	 * @throws BusinessException
	 */
	void initCtrlFiledMap(Map<String, List<MtappCtrlBusiVO>> apppk2MtappCtrlBusiVOMap, AggMatterAppVO[] matterAppVOs,
			Map<String, List<MtappCtrlfieldVO>> key2CtrlFieldVosMap) throws BusinessException {
		if (apppk2MtappCtrlBusiVOMap == null || matterAppVOs == null) {
			return;
		}

		ctrlFiledMap = new HashMap<String, Map<String, String>>();

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
						throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"upp2012v575_0", "0upp2012V575-0129")/*
																	 * @res
																	 * "�������뵥����ά��δ����vo���գ����飡"
																	 */);
					}

					String orgTradeTypekey = getMtappCtrlRuleKey(appVo.getParentVO());
					// У����ƶ���У�鵱ǰҵ�����ݽ��������Ƿ������뵥�Ŀ��ƶ�����
					List<MtappCtrlfieldVO> ctrlBillList = key2CtrlFieldVosMap.get(orgTradeTypekey);
					List<String> ctrlFieldCodeList = new ArrayList<String>();
					
					if(ctrlBillList != null){
						for(MtappCtrlfieldVO ctrlField : ctrlBillList){
							ctrlFieldCodeList.add(ctrlField.getFieldcode());
						}
					}
					
					Map<String, String> voKeyMap = new HashMap<String, String>();
					for (ExchangeRuleVO ruleVo : ruleVoList) {
						String ruleData = ruleVo.getRuleData();
						if (ctrlFieldCodeList.contains(ruleData)) {
							voKeyMap.put(ruleData, ruleVo.getDest_attr());
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
	 * ��ѯ���vo���չ���
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
}