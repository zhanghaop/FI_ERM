package nc.itf.erm.pub;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * ���ñ��������ù鼯�ӿ�
 * 
 * @since 6.31
 * @version 2013
 * @author chenshuaia
 */
public interface ICombineResultForME {

	/**
	 * Ӫ�����ù鼯���� ����������ʱ���ѯ�鼯����
	 * 
	 * @param conditionTable
	 *            ��ѯ������ʱ��
	 * @param elementTable
	 *            ���ݱ����ж�Ӧ����Ҫ��
	 * @return ExpenseAccountVO �����ʣ����ܺ�Ľ����
	 * 
	 * 
	 * @throws BusinessException
	 */
	public ExpenseAccountVO[] combineProcess(String conditionTable, String elementTable ) throws BusinessException;

	/**
	 * ȡ����Դ���ݿɹ鼯�������ͷ�Χ
	 * 
	 * @return String[] ��������pk����
	 * @throws BusinessException
	 */
	public String[] getAllTranstypes() throws BusinessException;
	
	/**
	 * �������ڷ�Χ ��ѯ������Ч�ķ�����id
	 * 
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @return String[]
	 * @throws BusinessException
	 */
	String[] getEarlyDataIDs(UFDate startDate, UFDate endDate) throws BusinessException;

	/**
	 * ���ݷ�����id�����ѯ������vo
	 * 
	 * @param ids
	 *            ������id����
	 * @return ExpenseAccountVO[]
	 * @throws BusinessException
	 */
	ExpenseAccountVO[] getEarlyDataVOs(String[] ids) throws BusinessException;
}
