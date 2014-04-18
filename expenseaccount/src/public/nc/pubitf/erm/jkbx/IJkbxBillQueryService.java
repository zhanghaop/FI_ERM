package nc.pubitf.erm.jkbx;

import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * ������-��ѯ���񣬶���ӿ�
 * 
 * @author lvhj
 *
 */
public interface IJkbxBillQueryService {

	/**
	 * ��ѯ������ؼ��ֶ���Ϣ
	 * 
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryJKBills() throws BusinessException;
	/**
	 * ��ѯ����ҵ���йؼ��ֶ���Ϣ
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryJKBusItems(String headpk) throws BusinessException;
	/**
	 * ��ѯ����������ؼ��ֶ���Ϣ
	 * 
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryBXBills() throws BusinessException;
	/**
	 * ��ѯ��������ҵ���йؼ��ֶ���Ϣ
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryBXBusItems(String headpk) throws BusinessException;
	
	
	
}
