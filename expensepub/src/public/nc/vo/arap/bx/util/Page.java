
package nc.vo.arap.bx.util;

/**
 * ��ҳ��Ϣ�ӿ�
 */
public interface Page {
	
	public static int STARTPAGE = 1;

	/**
	 * �Ƿ�����ҳ����һҳ������һҳҳ��Ϊ1
	 *
	 * @return ��ҳ��ʶ
	 */
	public boolean isFirstPage();

	/**
	 * �Ƿ������һҳ
	 *
	 * @return ĩҳ��ʶ
	 */
	public boolean isLastPage();

	/**
	 * �Ƿ�����һҳ
	 *
	 * @return ��һҳ��ʶ
	 */
	public boolean hasNextPage();

	/**
	 * �Ƿ�����һҳ
	 *
	 * @return ��һҳ��ʶ
	 */
	public boolean hasPreviousPage();

	/**
	 * ��ȡ���һҳҳ�룬Ҳ������ҳ��
	 *
	 * @return ���һҳҳ��
	 */
	public int getLastPageNumber();

	/**
		* ��ȡ��ǰҳ���������ݵ��б���
		*
		* @return ��ǰҳ���������ݵ��б���
		*/
	public int getThisPageFirstElementNumber();

	/**
		* ��ȡ��ǰҳ��ĩ�����ݵ��б���
		*
		* @return ��ǰҳ��ĩ�����ݵ��б���
		*/
	public int getThisPageLastElementNumber();

	/**
	 * �ܵ�������Ŀ������0��ʾû������
	 *
	 * @return ������
	 */
	public int getTotalNumberOfElements();

	/**
	 * ��ȡ��һҳ����
	 *
	 * @return ��һҳ����
	 */
	public int getNextPageNumber();

	/**
	 * ��ȡ��һҳ����
	 *
	 * @return ��һҳ����
	 */
	public int getPreviousPageNumber();

	/**
	 * ÿһҳ��ʾ����Ŀ��
	 *
	 * @return ÿһҳ��ʾ����Ŀ��
	 */
	public int getPageSize();

	/**
	 * ��ǰҳ��ҳ��
	 *
	 * @return ��ǰҳ��ҳ��
	 */
	public int getThisPageNumber();
	
	/**
	 * �ƶ�����һҳ 
	 */
	public void next();
	
	/**
	 * �ƶ�����һҳ
	 */
	public void previous();
	
	/**
	 * �����ܵ�Ԫ������
	 */
	public void setTotalNumberOfElements(int count);
	

	/**
	 * ���õ�ǰҳ��
	 * @param num
	 */
	public void setThisPageNumber(int num);
	

	/**
	 * ���÷�ҳ��С
	 * @param size
	 */
	public void setPageSize(int size);
	

}
