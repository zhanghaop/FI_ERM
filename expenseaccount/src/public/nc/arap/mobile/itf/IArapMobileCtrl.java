package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IArapMobileCtrl extends IErmMobileCtrl{
	/**
	 * Ĭ��ͨѶ�ѷѱ�����
	 */
	public static final String defaultDjlxbm = "2643";
	
	/**
	 * ��pk��ѯ������
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Object> getJkbxCard(String headpk) throws BusinessException;
	
	/**
	 * ��ѯ��ǰ�����µ�ȫ���������͵���ֵ
	 *  
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Map<String, String>> queryReimType(String userid) throws BusinessException;
}
   