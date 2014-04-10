package nc.bs.erm.matterappctrl;

import java.util.Collection;

import nc.bs.arap.util.SqlUtils;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class MtapppfVOQryServiceImp implements IMtapppfVOQryService {
	@Override
	public MtapppfVO[] queryMtapppfVoByBusiPk(String[] busiPks) throws BusinessException {
		if (ArrayUtils.isEmpty(busiPks)) {
			return null;
		}

		String inStr = SqlUtils.getInStr(MtapppfVO.BUSI_PK, busiPks);
		@SuppressWarnings("unchecked")
		Collection<MtapppfVO> ret = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				MtapppfVO.class, inStr, false);
		return ret == null ? null : ret.toArray(new MtapppfVO[] {});
	}

	@Override
	public MtapppfVO[] queryMtapppfVoByBusiDetailPk(String[] busiDetailPks) throws BusinessException {
		if (ArrayUtils.isEmpty(busiDetailPks)) {
			return null;
		}
		
		String inStr = SqlUtils.getInStr(MtapppfVO.BUSI_DETAIL_PK, busiDetailPks);
		@SuppressWarnings("unchecked")
		Collection<MtapppfVO> ret = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				MtapppfVO.class, inStr, false);
		return ret == null ? null : ret.toArray(new MtapppfVO[] {});
	}
}
