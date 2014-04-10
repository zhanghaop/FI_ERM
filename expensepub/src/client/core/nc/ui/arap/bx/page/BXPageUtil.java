package nc.ui.arap.bx.page;

import nc.ui.arap.bx.BXBillMainPanel;

/**
 * �����·�ҳ����
 * @author chendya
 *
 */
public class BXPageUtil {

	private final BXBillMainPanel panel;

	/**
	 * ÿҳ��С
	 */
	private Integer perPageSize;

	/**
	 * ������
	 */
	private Integer totalRowCount;

	/**
	 * ��ǰҳ����ʼ��ѯλ��
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
	 * ��ǰҳ��
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
	 * ÿҳ��ʾ����
	 *
	 * @return
	 */
	public Integer getPerPageSize() {
		return perPageSize;
	}

	/**
	 * ������
	 */
	public Integer getTotalRowCount() {
		return totalRowCount;
	}

	public void setTotalRowCount(Integer totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	/**
	 * ��ҳ��
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
		})/*@res "ÿҳ"*/
		/*@res"�ŵ���,����"*/
		/*@res "ҳ"*/
		/*@res ",����"*/
		/*@res "��"*/;
	}
}