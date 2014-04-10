package nc.pubitf.erm.matterappctrl;

import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.BusinessException;

/**
 * �������뵥ִ�������ѯ�ӿ�
 * @author chenshuaia
 *
 */
public interface IMtapppfVOQryService {
	/**
	 * ��ҵ�񵥾�pks��ѯ������ִ�����
	 * @param busiPks ҵ��pks
	 * @return
	 * @throws BusinessException
	 */
	public MtapppfVO[] queryMtapppfVoByBusiPk(String[] busiPks) throws BusinessException;
	
	public MtapppfVO[] queryMtapppfVoByBusiDetailPk(String[] busiDetailPks) throws BusinessException;
}
