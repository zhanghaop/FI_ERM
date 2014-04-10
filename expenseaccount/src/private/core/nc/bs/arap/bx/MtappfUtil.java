package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.BusinessException;

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
}
