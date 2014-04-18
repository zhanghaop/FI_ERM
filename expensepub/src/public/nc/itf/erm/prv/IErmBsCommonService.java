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
	public Map<String, String> getPermissonOrgMapCall(String pk_user, String nodeCode, String pk_group)
			throws BusinessException;

	/**
	 * ��ѯ�û�������������������
	 * 
	 * @param pk_user
	 *            �û�pk��Ϊ��ʱ������ǰ��¼�û����в�ѯ
	 * @param tradeTypes
	 *            �����������飬Ϊ��ʱ�������
	 * @param isApproved
	 *            true ��ʾ�������� false ��ʾ��������
	 * @return ����pk����
	 * @throws BusinessException
	 */
	public String[] queryApprovedWFBillPksByCondition(String pk_user, String[] tradeTypes, boolean isApproved)
			throws BusinessException;
}
