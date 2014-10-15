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
 * �������뵥���ƻ�дʵ�ָ����࣬��չ
 * 
 * @author lvhj
 *
 */
public class MatterAppCtrlHelperExt extends MatterAppCtrlHelper{
	
	/**
	 * ���ݱ����������뵥��ϸ�У����пɻ�д���
	 * 
	 * @param childrenVO ���뵥��ϸ��
	 * @param total_amount ��д�ܽ��
	 * @param busivo ҵ������vo 
	 * @param appPfList ���뵥ִ�м�¼map 
	 * @return detailExeAmountMap <��ϸ��pk����д���>
	 */
	private Map<String, UFDouble> computeMaDetailWrAmount(MtAppDetailVO[] childrenVO,MtappCtrlBusiVO busivo,Map<String, List<MtapppfVO>> appPfMap){
		Map<String, UFDouble> detailExeAmountMap = new HashMap<String, UFDouble>();
		// hash���м����Դ���ݵ����뵥��ִ�н�����
		Map<String, UFDouble> srcDetailPfMap = initSrcDetailPfMap(busivo,
				appPfMap);
		
		UFDouble total_amount = busivo.getAmount();
		int ybDecimalDigit = Currency.getCurrDigit(busivo.getCurrency());// ԭ�Ҿ���
		//���������ܽ��
		UFDouble sumAmount = UFDouble.ZERO_DBL;

		// �ɻ�дβ������뵥��ϸ��,ʹ�������map����֤��˳���дβ��
		Map<String,UFDouble> differDetailAmount = new LinkedHashMap<String, UFDouble>();
		// �ɻ�дβ������뵥��ϸ��pk����дԤռ��ʱ��¼���һ��
		String differDetailPk = null;
		
		for (int i = 0; i < childrenVO.length; i++) {
			MtAppDetailVO appDetailVo = childrenVO[i];
			// ��������дÿ�н��
			UFDouble amount = total_amount.multiply(appDetailVo.getShare_ratio()).div(100);
			// ����ҵ�񵥾�ԭ�Ҿ��ȴ����д��ԭ�Ҿ���
			amount = amount.setScale(ybDecimalDigit,BigDecimal.ROUND_UP);
			// ��дִ����ʱ������Ʊ��пɻ�д�������
			if(IMtappCtrlBusiVO.DataType_exe.equals(busivo.getDataType())){
				// �м��ɻ�д�����ֵ = ���ε����ܻ�д��� - �Ѿ��ͷŵ�ִ�������;ҵ�񵥾ݿɻ�д���ֵ = ��ǰ���뵥��ϸ����������д��� - ��ִ��
				UFDouble usable_amout = srcDetailPfMap.get(appDetailVo.getPrimaryKey());
				if(usable_amout == null){
					// ���ݱ����д���ɳ������뵥�����ֵ = ���ɻ�дֵ - ��ִ�У����ɳ�����������ֵ = �����ܽ�� - ��ִ��
					usable_amout = busivo.isExceedEnable()?appDetailVo.getMax_amount().sub(appDetailVo.getExe_amount()):
						appDetailVo.getOrig_amount().sub(appDetailVo.getExe_amount());
				}
				if(amount.compareTo(usable_amout)>0){
					amount = usable_amout;
				}else{
					if(amount.compareTo(UFDouble.ZERO_DBL) <0){
						// ��ִ����С��0ʱ��ʹ����ӵķ�ʽ������Ƚ�
						UFDouble usable_rest = amount.add(usable_amout);
						if(usable_rest.compareTo(UFDouble.ZERO_DBL)<0){
							amount = usable_amout.multiply(-1);
						}else{
							// ��ǰ�л�ʣ�����ɻ�дβ��
							differDetailAmount.put(appDetailVo.getPrimaryKey(), usable_rest);
						}
					}else{
						// ��ǰ�л�ʣ�����ɻ�дβ��
						differDetailAmount.put(appDetailVo.getPrimaryKey(), usable_amout.sub(amount));
					}
				}
			}else{
				// Ԥռ�����н��Ŀ��ƣ���β��ֱ�ӷ������һ��
				differDetailPk = appDetailVo.getPrimaryKey();
			}
			// ��д��ϸ�н���¼
			detailExeAmountMap.put(appDetailVo.getPrimaryKey(), amount);
			// ����ϼƽ��
			sumAmount = sumAmount.add(amount);
		}
		// β��ֵ���㴦��
		UFDouble differAmount = total_amount.sub(sumAmount);
		dealDifferAmount(detailExeAmountMap, differDetailAmount,
				differDetailPk, differAmount);
		return detailExeAmountMap;
	}

	/**
	 * β��ֵ���㴦��
	 * 
	 * ���Ȱ���ָ���л�дβ���ָ��������������еĿ��������л�дβ��
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
			// ���Ȱ���ָ���л�дβ���ָ��������������еĿ��������л�дβ��
			if(differDetailPk != null){
				detailExeAmountMap.put(differDetailPk, detailExeAmountMap.get(differDetailPk).add(differAmount));
			}else{
				// ��˳�����л�дβ��
				for (Entry<String, UFDouble> differ : differDetailAmount.entrySet()) {
					String key = differ.getKey();
					UFDouble differ_rest_amount = differ.getValue();
					if(differ_rest_amount.compareTo(differAmount)>0){
						if(differAmount.compareTo(UFDouble.ZERO_DBL) <0){
							// ��ִ����С��0ʱ��ʹ����ӵķ�ʽ������Ƚ�
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
	 * ��ϣ���м����Դ����
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
					// �����м�����Σ����ͷ����ε�ȫ��ִ�м�¼�����м������ε��ݵ�ǰռ�����
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
	 * ���������������дԤռ��
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
				throw new BusinessException("����д���뵥û����ϸ�У����飡");
			}
			
			// ���������ϸ�д���д�Ľ��
			Map<String, UFDouble> detailAmountMap = computeMaDetailWrAmount(childrenVO, busivo,null);
			
			for (MtAppDetailVO appDetailVo : childrenVO) {
				// ��ñ����ִ�м�¼
//				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				
				// ��������дÿ�н��
				UFDouble amount = detailAmountMap.get(appDetailVo.getPrimaryKey());
				// ��д��ϸ��Ԥռ��
				writeBackDetailAppVo(new UFDouble[]{UFDouble.ZERO_DBL,amount}, appDetailVo, null,appPfMap, busivo, UFBoolean.FALSE);
			}
			
		}
		
	}

	/**
	 * ���������������дִ����
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
				throw new BusinessException("����д���뵥û����ϸ�У����飡");
			}
			// ���������ϸ�д���д�Ľ��
			Map<String, UFDouble> detailAmountMap = computeMaDetailWrAmount(childrenVO, busivo,appPfMap);
			
			for (MtAppDetailVO appDetailVo : childrenVO) {
				// ��ñ����ִ�м�¼
//				MtapppfVO appPfVo = getMtapppfVO_new(busivo, appDetailVo, appPfMap,mtappvo.getPk_tradetype());
				
				// ��������дÿ�н��
				UFDouble amount = detailAmountMap.get(appDetailVo.getPrimaryKey());
				// ��д��ϸ��Ԥռ��
				writeBackDetailAppVo(new UFDouble[]{amount,UFDouble.ZERO_DBL}, appDetailVo,null, appPfMap, busivo, UFBoolean.FALSE);
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see nc.bs.erm.matterappctrl.MatterAppCtrlHelper#writeBackDetailAppVo(nc.vo.pub.lang.UFDouble[], nc.vo.erm.matterapp.MtAppDetailVO, nc.vo.erm.matterappctrl.MtapppfVO, nc.vo.erm.matterappctrl.IMtappCtrlBusiVO, nc.vo.pub.lang.UFBoolean)
	 */
	protected UFDouble[] writeBackDetailAppVo(UFDouble[] exe_amount, MtAppDetailVO appDetailVo, MtapppfVO appPfVo,
			Map<String, List<MtapppfVO>> appPfMap, MtappCtrlBusiVO iMtappCtrlBusiVO, UFBoolean isAdjust) throws BusinessException {// ���λ�дʣ����
		// ��д���뵥��ϸ��
		if (appDetailVo.getClose_status().intValue() == ErmMatterAppConst.CLOSESTATUS_Y && (!INCSystemUserConst.NC_USER_PK.equals(appDetailVo.getCloseman()))) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
	    			getStrByID("201212_0","0201212-0095")/*@res ""����д�����뵥��ϸ���Ѿ��رգ��޷���д�����ֹ�ȡ���ر�""*/);
		}
	
		if (exe_amount[0].equals(UFDouble.ZERO_DBL) && exe_amount[1].equals(UFDouble.ZERO_DBL)) {
			return exe_amount;
		}
		
		// �������뵥���
		UFDouble rest_amount = getDoubleValue(appDetailVo.getRest_amount());
		if(iMtappCtrlBusiVO.isExceedEnable()){
			rest_amount = appDetailVo.getMax_amount().sub(appDetailVo.getOrig_amount()).add(rest_amount);
		}
		// ҵ��ִ�����Ľ��
		UFDouble busiAmount = getDoubleValue(exe_amount[0]);
		if(busiAmount.compareTo(rest_amount) > 0){
			throw new BusinessException("����д�����뵥��ϸ�����㣬��ȷ��");
		}
		appDetailVo.setStatus(VOStatus.UPDATED);
		appDetailVo.setExe_amount(getDoubleValue(appDetailVo.getExe_amount()).add(exe_amount[0]));
		appDetailVo.setPre_amount(getDoubleValue(appDetailVo.getPre_amount()).add(exe_amount[1]));

		// ��������㼰��װִ�м�¼
		if(appPfVo == null){
			appPfVo = getMtapppfVO_new(iMtappCtrlBusiVO, appDetailVo,appPfMap,appDetailVo.getPk_tradetype());
		}
		convertMutiAmount(appDetailVo,iMtappCtrlBusiVO,appPfVo, isAdjust, new UFDouble[]{exe_amount[0],exe_amount[1],UFDouble.ZERO_DBL});
		
		return exe_amount;
	}
	
	/**
	 * �����뵥��ϸ�У������дԤռ��
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
			// ��ñ����ִ�м�¼
//			MtapppfVO appPfVo = getMtapppfVO_new(busivo, mtAppDetailVO, appPfMap,mtAppDetailVO.getPk_tradetype());
			
			// ��������дÿ�н��
			UFDouble amount = busivo.getAmount();
			// ��д��ϸ��Ԥռ��
			writeBackDetailAppVo(new UFDouble[]{UFDouble.ZERO_DBL,amount}, mtAppDetailVO,null,appPfMap, busivo, UFBoolean.FALSE);
			
		}
		
	}

	/**
	 * �����뵥��ϸ�У������дִ����
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
			// ��ñ����ִ�м�¼
//			MtapppfVO appPfVo = getMtapppfVO_new(busivo, mtAppDetailVO,
//					appPfMap, mtAppDetailVO.getPk_tradetype());

			// ��������дÿ�н��
			UFDouble amount = busivo.getAmount();
			// ��д��ϸ��Ԥռ��
			writeBackDetailAppVo(new UFDouble[] {amount, UFDouble.ZERO_DBL},
					mtAppDetailVO, null,appPfMap, busivo, UFBoolean.FALSE);

		}
		
	}

}