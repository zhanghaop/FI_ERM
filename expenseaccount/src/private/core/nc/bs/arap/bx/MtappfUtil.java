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
	 * 根据冲销行对应的申请单记录<br>
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public static MtapppfVO[] getContrastMaPfVos(JKBXVO[] vos) throws BusinessException {
		List<String> contrastPkList = new ArrayList<String>();
		// 借款业务行与冲销vo对照
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

		if (ArrayUtils.isEmpty(pfVos)) {// 无费用执行记录则返回
			return null;
		}

		return pfVos;
	}

	/**
	 * 根据借款报销Vo查询他们所关联的申请单申请记录
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public static MtapppfVO[] getMaPfVosByJKBXVo(JKBXVO[] vos) throws BusinessException {
		if (!ArrayUtils.isEmpty(vos)) {

			List<String> detailPkList = new ArrayList<String>();
			// 借款业务行与冲销vo对照
			for (JKBXVO bxVo : vos) {
				if (ArrayUtils.isEmpty(bxVo.getChildrenVO())) {
					continue;
				}
				for (BXBusItemVO busItem : bxVo.getChildrenVO()) {
					if (busItem.getPk_busitem() != null && busItem.getPk_item() != null) {// 存在拉单的情况时
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
