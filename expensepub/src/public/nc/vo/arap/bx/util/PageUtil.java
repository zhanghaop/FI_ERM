package nc.vo.arap.bx.util;

/**
 * @author twei
 *
 * nc.vo.arap.bx.util.PageUtil
 * 
 * 分页组件实现
 */
public class PageUtil implements Page {

	private int curPage;
	private int pageSize;
	private int totalCount = 0;

	public PageUtil(int totalCount, int curPage, int pageSize) {
		this.curPage = curPage;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
	}

	public boolean isFirstPage() {
		return getThisPageNumber() == STARTPAGE;
	}
	public int getThisPageFirstElementNumber() {
		return (getThisPageNumber() - 1) * getPageSize();
	}

	public int getThisPageLastElementNumber() {
		int fullPage = getThisPageFirstElementNumber() + getPageSize() - 1;
		return getTotalNumberOfElements() < fullPage
			? getTotalNumberOfElements()-1
			: fullPage;
	}
	public boolean isLastPage() {
		return getThisPageNumber() >= getLastPageNumber();
	}

	public boolean hasNextPage() {
		return getLastPageNumber() > getThisPageNumber();
	}

	public boolean hasPreviousPage() {
		return getThisPageNumber() > 1;
	}

	public int getLastPageNumber() {
		return totalCount % this.pageSize == 0
			? totalCount / this.pageSize
			: totalCount / this.pageSize + 1;
	}

	public int getTotalNumberOfElements() {
		return totalCount;
	}

	public int getNextPageNumber() {
		return getThisPageNumber() + 1;
	}

	public int getPreviousPageNumber() {
		return getThisPageNumber() - 1;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getThisPageNumber() {
		return curPage;
	}

	public void setThisPageNumber(int i) {
		curPage = i;
	}
	
	public void setTotalNumberOfElements(int count) {
		totalCount = count;	
	}

	public void setPageSize(int i) {
		pageSize = i;
	}

	public void next() {
		curPage++;
	}

	public void previous() {
		curPage--;
	}

	
}
