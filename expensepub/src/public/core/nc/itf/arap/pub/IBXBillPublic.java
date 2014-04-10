package nc.itf.arap.pub;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.pm.budget.pub.IBudgetExecVO;
import nc.vo.pub.BusinessException;
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
	
	/**
	 * Ϊ��ĿԤ���ṩ�Ĳ�ѯ����ӿ�
	 * ��ѯ��Ч״̬�ı�����(ҵ�񳡾����ȱ��������ֶ�ִ����ĿԤ��)
	 * @param pk_group ��������
	 * @param pk_project ��Ŀ����
	 * @param pk_billtypes ����������������
	 * @return ��ĿԤ��ִ��VO[]
	 * @throws BusinessException
	 */
	public IBudgetExecVO[] queryBxBill4ProjBudget(String pk_group, String pk_project, String[] pk_billtypes) throws BusinessException;
	
	/**
	 * Ϊ��ĿԤ���ṩ�Ĳ�ѯ����ӿ�
	 * ���������ͱ����ѯ��Ч״̬�ı�����(ҵ�񳡾����ȱ��������ֶ�ִ����ĿԤ��)
	 * @param pk_group ��������
	 * @param pk_project ��Ŀ����
	 * @param djlxbms �������ͱ���s
	 * @return ��ĿԤ��ִ��VO[]
	 * @throws BusinessException
	 */
	public IBudgetExecVO[] queryBxBill4ProjBudget2(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
}
