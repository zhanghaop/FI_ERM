
package nc.vo.arap.bx.util;

/**
 * 分页信息接口
 */
public interface Page {
	
	public static int STARTPAGE = 1;

	/**
	 * 是否是首页（第一页），第一页页码为1
	 *
	 * @return 首页标识
	 */
	public boolean isFirstPage();

	/**
	 * 是否是最后一页
	 *
	 * @return 末页标识
	 */
	public boolean isLastPage();

	/**
	 * 是否有下一页
	 *
	 * @return 下一页标识
	 */
	public boolean hasNextPage();

	/**
	 * 是否有上一页
	 *
	 * @return 上一页标识
	 */
	public boolean hasPreviousPage();

	/**
	 * 获取最后一页页码，也就是总页数
	 *
	 * @return 最后一页页码
	 */
	public int getLastPageNumber();

	/**
		* 获取当前页的首条数据的行编码
		*
		* @return 当前页的首条数据的行编码
		*/
	public int getThisPageFirstElementNumber();

	/**
		* 获取当前页的末条数据的行编码
		*
		* @return 当前页的末条数据的行编码
		*/
	public int getThisPageLastElementNumber();

	/**
	 * 总的数据条目数量，0表示没有数据
	 *
	 * @return 总数量
	 */
	public int getTotalNumberOfElements();

	/**
	 * 获取下一页编码
	 *
	 * @return 下一页编码
	 */
	public int getNextPageNumber();

	/**
	 * 获取上一页编码
	 *
	 * @return 上一页编码
	 */
	public int getPreviousPageNumber();

	/**
	 * 每一页显示的条目数
	 *
	 * @return 每一页显示的条目数
	 */
	public int getPageSize();

	/**
	 * 当前页的页码
	 *
	 * @return 当前页的页码
	 */
	public int getThisPageNumber();
	
	/**
	 * 移动到下一页 
	 */
	public void next();
	
	/**
	 * 移动到上一页
	 */
	public void previous();
	
	/**
	 * 设置总的元素数量
	 */
	public void setTotalNumberOfElements(int count);
	

	/**
	 * 设置当前页码
	 * @param num
	 */
	public void setThisPageNumber(int num);
	

	/**
	 * 设置分页大小
	 * @param size
	 */
	public void setPageSize(int size);
	

}
