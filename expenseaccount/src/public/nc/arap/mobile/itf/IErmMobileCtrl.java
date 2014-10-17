package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;
/**
 * clf��jtf��txf���ֻ������ͱ����������ӿ�
 */

public interface IErmMobileCtrl {
	public String addJkbx(Map<String, Object> valuemap,String djlxbm) throws BusinessException;
	
	
	/**
	 * ��pk��ѯ������
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Object> getJkbxCard(String headpk) throws BusinessException;
	
	
	/**
	 * ����PKɾ��������
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public String deleteJkbx(String headpk,String userid) throws BusinessException;
	
	public String commitJkbx(String userid,String headpk) throws BusinessException;

	
	/**
	 * ��ѯ��ǰ�û�ȫ��δ����ͨ��\����ͨ���ı�����
	 * 
	 * @param userid
	 * @param flag ����״̬��־ 1-����� 0-Ϊ���
	 * @return �����������Ʒ���ı���������ֵ
	 * @throws BusinessException
	 */
	public Map<String, Map<String, String>> getBXHeadsByUser(String userid,String flag)
	throws BusinessException ;
	/**
	 * ��ѯ��ǰ�û�����������
	 * 
	 * @param userid
	 * @return ������
	 * @throws BusinessException
	 */
	public Map<String,Map<String, Map<String, String>>> getBXApproveBillByUser(String userid,String flag)
	throws BusinessException ;
	
	/**
	 * ��������
	 * 
	 * @param pks
	 * @return ������
	 * @throws BusinessException
	 */
	String auditBXBillByPKs(String[] pks,String userid) throws Exception;
	/**

	    * ��pk��ѯ������

	    * 

	    * @param headpk

	    * @throws BusinessException

	    */

	   public Map<String,Map<String,String>> loadBxdWorkflownote(String pk_jkbx,String djlxbm) throws BusinessException;
	   
	   /**
		 * ����������ѯ����
		 * 
		 * @param pks
		 * @return �������͵���
		 * @throws BusinessException
		 */
	   public Map<String,Map<String,String>> getBxdByCond(Map condMap,String userid) throws BusinessException;
	   
	   /**
		 * ��ѯ������Ŀ
		 * 
		 */
	   public Map<String, Map<String, String>> loadExpenseTypeInfoString(
			String userid) throws BusinessException;

	   /**
		 * ȡ������
		 * 
		 */
		public String unAuditbxd(String pk_jkbx,String userid) throws BusinessException;
		/**
		 * ��ǰ�û�������������
		 * 
		 */
		public String get_unapproved_bxdcount(String userid) throws BusinessException;
		
//	/**
//	 * ��ѯ��ǰ�û�ȫ��δ����ͨ���ı�����,��djlxbm����
//	 * 
//	 * @param userid
//	 * @return �����������Ʒ���ı���������ֵ
//	 * @throws BusinessException
//	 */
//	public Map<String, Map<String, String>> getUnApprovedBXHeadsByUser(String userid)
//	throws BusinessException ;
//	
//	/**
//	 * ��ѯ������������Ϣ�б�,���ܡ����¡���������
//	 * 
//	 * @return
//	 * @throws BusinessException
//	 */
//	public Map<String, Map<String, String>> queryBXHeads(String userid,String querydate) throws BusinessException ;
//	
	
}
