package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IClfMobileCtrl extends IErmMobileCtrl{
	
	/**
	 * Ĭ�Ͻ�ͨ�ѱ�����
	 */
	public static final String defaultDjlxbm = "2641";
	
	/**
	 * ��pk��ѯ������
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Object> getJkbxCard(String headpk) throws BusinessException;
	
}
   