package nc.itf.arap.pub;

import java.util.Map;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
/**
 *	�����൥�ݶ����ṩ��ҵ����ӿ�. 
 */
public interface IBXBillPublic {
	/**
	 * 	 
	* �ֽ���ƽ̨ͨ���˽ӿڻ�д����״̬.
	*  �ڵ��ݸ����ʱά����������˺����״̬��¼Ϊ��δ���ˡ�.
	* ���ݱ�ȫ���������¼Ϊ�������ˡ������ݵ���������Դ��ֶβ�������.	
	 */
	public void updateQzzt(JKBXVO[] vos)throws BusinessException;

	/**
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * 
	 * ��ʽ���浥�ݣ�������ҵ��У�飬����������ģ��ӿ�
	 */
	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException;
	/**
	 * @param vos Ҫ�ݴ浥�ݵľۺ�VO����
	 * @return
	 * @throws BusinessException
	 * 
	 * �ݴ浥�ݣ��������κ�ҵ��У�飬����������ģ��ӿ�
	 */
	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException;
	
	/**
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * 
	 * ���µ���
	 */
	public JKBXVO[] update(JKBXVO[] vos)throws BusinessException;
	
	public JKBXHeaderVO updateHeader(JKBXHeaderVO header,String[] fields)throws BusinessException;
	
	/**
	 * @param bills
	 * @return
	 * @throws BusinessException
	 * 
	 * ɾ������
	 */
	public MessageVO[] deleteBills(JKBXVO[] bills)throws BusinessException;
	

	/**
	 * cmp�������ݺ�������ݵĴ���
	 */
	public void updateDataAfterImport(String[] pk_tradetype)throws BusinessException;
	
//	/**
//	 * Ϊ��ĿԤ���ṩ�Ĳ�ѯ����ӿ�
//	 * ��ѯ��Ч״̬�ı�����(ҵ�񳡾����ȱ��������ֶ�ִ����ĿԤ��)
//	 * @param pk_group ��������
//	 * @param pk_project ��Ŀ����
//	 * @param pk_billtypes ����������������
//	 * @return ��ĿԤ��ִ��VO[]
//	 * @throws BusinessException
//	 */
//	public IBudgetExecVO[] queryBxBill4ProjBudget(String pk_group, String pk_project, String[] pk_billtypes) throws BusinessException;
//	
//	/**
//	 * Ϊ��ĿԤ���ṩ�Ĳ�ѯ����ӿ�
//	 * <p>���������ͱ����ѯ��Ч״̬�ı�����
//	 * <p>(ҵ�񳡾����ȱ��������ֶ�ִ����ĿԤ��)
//	 * @param pk_group ��������
//	 * @param pk_project ��Ŀ����
//	 * @param djlxbms �������ͱ���s
////	 * @return ��ĿԤ��ִ��VO[]
////	 * @throws BusinessException
////	 */
////	public IBudgetExecVO[] queryBxBill4ProjBudget2(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
//	
	/**
	 * ��ȡָ����Ŀ��������ͨ���ı������ı����ܽ��֮��<br>
	 * ��������Ŀ���㵥��ȡ�ɱ�ʱ��Ҫ��ȡ����Ŀ��������ͨ���ı������ı����ܽ��֮��<br>
	 * ������������ȡ�ɱ� ��ť
	 * @param pk_group ����
	 * @param pk_project ��Ŀ
	 * @return Map<���֣����>
	 * @throws BusinessException
	 */
	public Map<String,UFDouble> queryAmount4ProjFinal(String pk_group, String pk_project) throws BusinessException;
	
	/**
	 * Ϊ��ĿԤ���ṩ�Ĳ�ѯ����ӿ�
	 * <p>��ĿԤ�㷢��ʱ��ȡ���ͨ����������ִ����
	 * @param pk_group ����
	 * @param pk_project ��Ŀpk
	 * @param djlxbms �������ͼ���
	 * @return �����ۺ�VO[]
	 */
	public JKBXVO[] queryBxApproveBill4ProjBudget(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
	
	/**
	 * Ϊ��ĿԤ���ṩ�Ĳ�ѯ����ӿ�
	 * <p>��ĿԤ�㷢��ʱ��ȡ��������ִ�������������ݴ棬ɾ�����ݵ����б�����
	 * @param pk_group ����
	 * @param pk_project ��Ŀpk
	 * @param djlxbms �������ͼ���
	 * @return �����ۺ�VO[]
	 */
	public JKBXVO[] queryBxBill4ProjBudget(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
}
