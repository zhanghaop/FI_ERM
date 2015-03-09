package nc.arap.mobile.itf;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IErmMobileCtrl {
	public String addJkbx(Map<String, Object> valuemap,String djlxbm) throws BusinessException;
	
	
	/**
	 * ����PKɾ��������
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public String deleteJkbx(String headpk,String userid) throws BusinessException;
	
	/**
	 * ��ҳ��ѯ��ǰ�û�δ���\����ɵĵ���
	 * 
	 * @param userid
	 * @param flag ����״̬��־0-δ���  1-����� 
	 * @return �����������Ʒ���ı���������ֵ
	 * @throws BusinessException
	 */
	public Map<String, List<Map<String, String>>> getBXHeadsByUser(String userid,String flag,String billtype,String startline,String pagesize,String pks)
	throws BusinessException ;
	/**
	 * ��ѯ��ǰ�û�����������
	 * 
	 * @param userid
	 * @return ������
	 * @throws BusinessException
	 */
	public Map<String,List<Map<String, String>>> getBXApproveBillByUser(String userid,String flag,String billtype,String startline,String pagesize,String pks)
	throws BusinessException ;
	
	
	/**
	 * ��������
	 * 
	 * @param pks
	 * @return ������
	 * @throws BusinessException
	 */
	String auditBXBillByPKs(String[] pks,String userid,String checknote,String ischeck,String flag,String[] pk_users) throws Exception;
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
			
}
