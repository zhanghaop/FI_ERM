package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;
/**
 * �û��Զ������ͱ�����
 */
public interface IDefMobileCtrl {
	/**
	 * ��������
	 * 
	 */
	public Map<String,Map<String,String>> getBXbilltype(String userid) throws BusinessException;
	/**
	 * ���浥��
	 * 
	 */
	public String saveJkbx(Map<String,Object> map, String djlxbm,String userid) throws BusinessException;
	public String commitDefJkbx(String userid,String pk_jkbx) throws BusinessException;
		/**
		 * ���ݵ������ͱ����ȡ��ӦDSL�ļ�
		 */
	public String getDslFile(String userid,String djlxbm,String nodecode, String flag) throws BusinessException;
	/**
	 * �õ�����
	 * 
	 */
	public String getRefList(String userid, String reftype)
				throws BusinessException;

	/**
	 * ���ݵ������ͱ����ȡ��ӦDSL�ļ�
	 */
	public String getItemDslFile(String userid,String djlxbm,String nodecode,String tablecode, String flag) throws BusinessException;
	/**
	 * �����Զ���ģ��ĵ���
	 */
	public String addDefJkbx(Map<String,Object> jkbxInfo,String djlxbm,String userid) throws BusinessException;
	/**
	 * tsУ��
	 */
	public String validateTs(String userid,String djlxbm,String nodecode,String tsflag) throws BusinessException;
	
	/**
	 * ����pk��ѯ����
	 */
	public Map<String,Object> getJkbxCard(String pk_jkbx,String userid,String djlxbm) throws BusinessException;
	
}