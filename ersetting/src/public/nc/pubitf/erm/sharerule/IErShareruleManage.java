package nc.pubitf.erm.sharerule;

import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.pub.BusinessException;

/**
 * ��̯����ά��
 * 
 * @author lvhj
 *
 */
public interface IErShareruleManage {
	
	/**
	 * ��̯��������
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO insertVO(AggshareruleVO vo) throws BusinessException;
	/**
	 * ��̯�����޸�
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO updateVO(AggshareruleVO vo) throws BusinessException;
	/**
	 * ��̯����ɾ��
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void deleteVO(AggshareruleVO vo) throws BusinessException;

}
