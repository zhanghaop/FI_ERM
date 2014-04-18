package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;

import org.apache.commons.lang.ArrayUtils;

public class MtappfUtil {
	/**
	 * ���ݳ����ж�Ӧ�����뵥��¼<br>
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public static MtapppfVO[] getContrastMaPfVos(JKBXVO[] vos) throws BusinessException {
		List<String> contrastPkList = new ArrayList<String>();
		// ���ҵ���������vo����
		for (JKBXVO bxVo : vos) {
			if (ArrayUtils.isEmpty(bxVo.getContrastVO())) {
				continue;
			}
			for (BxcontrastVO contrastVo : bxVo.getContrastVO()) {
				if(contrastVo.getPk_bxcontrast() != null){
					contrastPkList.add(contrastVo.getPk_bxcontrast());
				}
			}
		}

		if (contrastPkList.size() == 0) {
			return null;
		}

		MtapppfVO[] pfVos = NCLocator.getInstance().lookup(IMtapppfVOQryService.class).queryMtapppfVoByBusiDetailPk(
				contrastPkList.toArray(new String[] {}));

		if (ArrayUtils.isEmpty(pfVos)) {// �޷���ִ�м�¼�򷵻�
			return null;
		}

		return pfVos;
	}

	/**
	 * ���ݽ���Vo��ѯ���������������뵥�����¼
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public static MtapppfVO[] getMaPfVosByJKBXVo(JKBXVO[] vos) throws BusinessException {
		if (!ArrayUtils.isEmpty(vos)) {

			List<String> detailPkList = new ArrayList<String>();
			// ���ҵ���������vo����
			for (JKBXVO bxVo : vos) {
				if (ArrayUtils.isEmpty(bxVo.getChildrenVO())) {
					continue;
				}
				for (BXBusItemVO busItem : bxVo.getChildrenVO()) {
					if (busItem.getPk_busitem() != null && busItem.getPk_item() != null) {// �������������ʱ
						detailPkList.add(busItem.getPk_busitem());
					}
				}
			}

			if (detailPkList.size() == 0) {
				return null;
			}

			return NCLocator.getInstance().lookup(IMtapppfVOQryService.class).queryMtapppfVoByBusiDetailPk(
					detailPkList.toArray(new String[] {}));
		}
		return null;
	}
	
	/**
	 * ���ݷ��ý�ת��Vo��ѯ���������������뵥�����¼
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public static MtapppfVO[] getMaPfVosByCsVo(AggCostShareVO... vos) throws BusinessException {
		if (!ArrayUtils.isEmpty(vos)) {

			List<String> detailPkList = new ArrayList<String>();
			// ���ҵ���������vo����
			for (AggCostShareVO aggvo : vos) {
				if (ArrayUtils.isEmpty(aggvo.getChildrenVO())) {
					continue;
				}
				JKBXHeaderVO bxheadvo = aggvo.getBxvo() == null ? null:aggvo.getBxvo().getParentVO();
				if(bxheadvo != null && !StringUtil.isEmpty(bxheadvo.getPk_item())&&bxheadvo.getIsmashare() != null && bxheadvo.getIsmashare().booleanValue()){
					// ֻ����ǰ��̯������������������ִ�м�¼
					for (CircularlyAccessibleValueObject detailvo : aggvo.getChildrenVO()) {
						detailPkList.add(detailvo.getPrimaryKey());
					}
				}
			}

			if (!detailPkList.isEmpty()) {
				return NCLocator.getInstance().lookup(IMtapppfVOQryService.class).queryMtapppfVoByBusiDetailPk(
						detailPkList.toArray(new String[] {}));
			}
		}
		return null;
	}
	
	/**
	 * ���������¼��ȡ���뵥Ԥ���дVO����
	 * 
	 * @param pfVos
	 * @param isContray
	 * @param actionCode
	 * @param flowbillruleVos 
	 * @return
	 * @throws BusinessException
	 */
	public static List<YsControlVO> getMaControlVos(MtapppfVO[] pfVos, MtapppfVO[] contrayPfVos, boolean isContray,
			String actionCode, DataRuleVO[] flowbillruleVos) throws BusinessException {
		List<YsControlVO> result = new ArrayList<YsControlVO>();

		if (ArrayUtils.isEmpty(pfVos)) {
			return result;
		}

		// ���뵥���Ʋ���
//		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
//				.queryControlTactics(pfVos[0].getMa_tradetype(), actionCode, true);
		
		DataRuleVO[] ruleVos =  ErBudgetUtil.getSrcBillDataRule(pfVos[0].getMa_tradetype(), flowbillruleVos);

		if (ArrayUtils.isEmpty(ruleVos)) {
			return result;
		}

		List<IFYControl> fyControlList = new ArrayList<IFYControl>();
		List<IFYControl> contrayFyControlList = new ArrayList<IFYControl>();

		if (pfVos != null) {
			fyControlList = getMaFyControlVos(pfVos);
		}

		if (contrayPfVos != null && contrayPfVos.length > 0) {
			contrayFyControlList = getMaFyControlVos(contrayPfVos);
		}

		if (fyControlList.size() > 0 || contrayFyControlList.size() > 0) {
			YsControlVO[] ysVOs = ErBudgetUtil.getCtrlVOs(fyControlList.toArray(new IFYControl[] {}),
					contrayFyControlList.toArray(new IFYControl[] {}), isContray, ruleVos);

			for (YsControlVO ysVo : ysVOs) {
				result.add(ysVo);
			}
		}

		return result;
	}

	private static List<IFYControl> getMaFyControlVos(MtapppfVO[] pfVos) throws BusinessException {
		if (pfVos == null) {
			return null;
		}

		Set<String> parentPkSet = new HashSet<String>();// ���뵥PK
		List<String> childPkList = new ArrayList<String>();// ���뵥ҵ����PK����
		Map<String, MatterAppVO> pk2MappVoMap = new HashMap<String, MatterAppVO>();
		Map<String, MtAppDetailVO> pk2MappDetailVoMap = new HashMap<String, MtAppDetailVO>();

		for (MtapppfVO pf : pfVos) {
			parentPkSet.add(pf.getPk_matterapp());
			childPkList.add(pf.getPk_mtapp_detail());
		}

		// ��ѯ���뵥��Ϊ�˲���ҵ����Ϣ��
		IErmMatterAppBillQuery maQueryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		MatterAppVO[] parentVos = maQueryService.queryMatterAppVoByPks(parentPkSet.toArray(new String[] {}));// ��ѯ�����¼���������뵥��ͷ
		MtAppDetailVO[] childrenVos = maQueryService.queryMtAppDetailVOVoByPks(childPkList.toArray(new String[] {}));

		if (parentVos == null || childrenVos == null) {
			return null;
		}

		for (MatterAppVO appVo : parentVos) {
			pk2MappVoMap.put(appVo.getPk_mtapp_bill(), appVo);
		}

		for (MtAppDetailVO detail : childrenVos) {
			pk2MappDetailVoMap.put(detail.getPk_mtapp_detail(), detail);
		}

		// ���뵥ҵ���м���
		List<IFYControl> fyControlList = new ArrayList<IFYControl>();

		for (MtapppfVO pf : pfVos) {
			MatterAppVO headvo = pk2MappVoMap.get(pf.getPk_matterapp());
			MtAppDetailVO detail = pk2MappDetailVoMap.get(pf.getPk_mtapp_detail());

			if (headvo == null || detail == null) {
				continue;
			}

			detail = (MtAppDetailVO) detail.clone();
			headvo = (MatterAppVO) headvo.clone();

			// �����ý������Ԥ��
			detail.setOrig_amount(pf.getFy_amount() == null ? UFDouble.ZERO_DBL : pf.getFy_amount().abs());
			detail.setOrg_amount(pf.getOrg_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getOrg_fy_amount().abs());
			detail.setGroup_amount(pf.getGroup_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getGroup_fy_amount()
					.abs());
			detail.setGlobal_amount(pf.getGlobal_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getGlobal_fy_amount()
					.abs());
			MatterAppYsControlVO controlvo = new MatterAppYsControlVO(headvo, detail);
			// 631��Ҫ�ͷ����뵥��Ԥռ����ִ������ͬ������Ҫ��������Ԥռ����
//			// ����Ԥռ���
//			controlvo.setPreItemJe(new UFDouble[] {
//					pf.getGlobal_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getGlobal_fy_amount().abs(),
//					pf.getGroup_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getGroup_fy_amount().abs(),
//					pf.getOrg_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getOrg_fy_amount().abs(),
//					pf.getFy_amount() == null ? UFDouble.ZERO_DBL : pf.getFy_amount().abs() });
			fyControlList.add(controlvo);
		}
		
		return fyControlList;
	}
}
