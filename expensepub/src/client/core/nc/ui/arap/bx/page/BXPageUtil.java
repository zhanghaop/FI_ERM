package nc.ui.arap.bx.page;

import nc.ui.arap.bx.BXBillMainPanel;

/**
 * 报销新分页工具
 * @author chendya
 *
 */
public class BXPageUtil {

	private final BXBillMainPanel panel;

	/**
	 * 每页大小
	 */
	private Integer perPageSize;

	/**
	 * 总行数
	 */
	private Integer totalRowCount;

	/**
	 * 当前页的起始查询位置
	 */
	public Integer getCurrPageStartPos() {
		return (getCurrPage()-1)*getPerPageSize();
	}

	public BXPageUtil(BXBillMainPanel panel) {
		this.panel = panel;
		initialize();
	}

	private void initialize(){
		setPerPageSize(panel.getPageBarPanel().getPerPageSize());
	}

	/**
	 * 当前页码
	 *
	 * @return
	 */
	public Integer getCurrPage() {
		Integer currPage = panel.getPageBarPanel().getCurrPage();
		if(currPage==null||currPage<=1){
			currPage = 1;
		}
		return currPage;
	}

	private void setPerPageSize(Integer perPageSize) {
		this.perPageSize = perPageSize;
	}

	/**
	 * 每页显示数量
	 *
	 * @return
	 */
	public Integer getPerPageSize() {
		return perPageSize;
	}

	/**
	 * 总行数
	 */
	public Integer getTotalRowCount() {
		return totalRowCount;
	}

	public void setTotalRowCount(Integer totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	/**
	 * 总页数
	 */
	public Integer getTotalPageSize() {
		if (getTotalRowCount() == 0) {
			return 0;
		}
		if (getTotalRowCount() % getPerPageSize() == 0) {
			return getTotalRowCount() / getPerPageSize();
		}
		return getTotalRowCount() / getPerPageSize() + 1;
	}

	@Override
	public String toString() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0011",null,new String[]{
				String.valueOf(getPerPageSize()),
				String.valueOf(getTotalPageSize()),
				String.valueOf(getTotalRowCount()),
		})/*@res "每页"*/
		/*@res"张单据,共计"*/
		/*@res "页"*/
		/*@res ",共计"*/
		/*@res "行"*/;
	}
}