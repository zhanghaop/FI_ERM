package nc.pubitf.erm.erminit;

import java.util.List;

import nc.vo.erm.erminit.ErminitVO;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * ���ù����ڳ���ѯ
 * 
 * @author lvhj
 *
 */
public interface IErminitQueryService {
	
	/**
	 * ����֯��ѯ�����ڳ�
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public ErminitVO queryByOrg(String pk_org) throws BusinessException;
	
	/**
	 * ����֯��ѯ�����ڳ��Ĺر�״̬
	 */
	
	public boolean queryStatusByOrg(String pk_org) throws BusinessException;
	/**
	 * ��ѯ��֯�Ľ�����Ϣ
	 */
	public List<CloseAccBookVO> queryAccStatusByOrg(String pk_org) throws BusinessException;
	
	/**
	 * ������֯�رյķ����ڳ�
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryStatusByOrgs(String[] pk_org)throws BusinessException;
	
}
