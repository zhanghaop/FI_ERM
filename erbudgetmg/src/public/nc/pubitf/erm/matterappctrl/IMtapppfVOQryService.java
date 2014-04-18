package nc.pubitf.erm.matterappctrl;

import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.BusinessException;

/**
 * 费用申请单执行情况查询接口
 * @author chenshuaia
 *
 */
public interface IMtapppfVOQryService {
	/**
	 * 按业务单据pks查询出申请执行情况
	 * @param busiPks 业务pks
	 * @return
	 * @throws BusinessException
	 */
	public MtapppfVO[] queryMtapppfVoByBusiPk(String[] busiPks) throws BusinessException;
	
	public MtapppfVO[] queryMtapppfVoByBusiDetailPk(String[] busiDetailPks) throws BusinessException;
}
