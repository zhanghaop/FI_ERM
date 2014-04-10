package nc.itf.erm.prv;

import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * ���ù���ҵ�����
 * 
 * @author lvhj
 * 
 */
public interface IErmBsCommonService {

	/**
	 * ������Ȩ�޵���֯
	 * 
	 * @param pk_user
	 * @param nodeCode
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> getPermissonOrgMapCall(String pk_user,
			String nodeCode, String pk_group) throws BusinessException;
}
