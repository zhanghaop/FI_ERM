package nc.ui.arap.bx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import nc.ui.arap.bx.actions.QueryAction;
import nc.ui.arap.bx.page.BXPageUtil;
import nc.ui.arap.bx.page.IBXPageConst;
import nc.ui.ml.NCLangRes;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.pub.BusinessException;

/**
 * 借款报销分页工具栏(和uif2一样的分页工具栏)
 *
 * @author chendya
 *
 */
@SuppressWarnings("serial")
public class BXPaginationBar extends JPanel implements ActionListener,
		IPageObserver {

	private static final Color splitColor;

	static {
		splitColor = ThemeResourceCenter.getInstance().getColor(
				"themeres/ui/toolbaricons/uif2Control.theme.xml", "splitColor");
	}

	public static final String FIRST = "first";

	public static final String LAST = "last";

	public static final String NEXT = "next";

	public static final String PREV = "prev";

	/**
	 * 全部不可用状态
	 */
	public static Integer STATUS_ALL_UNENABLE = -1;

	/**
	 * 仅上一页不可用状态
	 */
	public static Integer STATUS_PREV_UNENABLE = 1;

	/**
	 * 上一页、下一页均不可用状态
	 */
	public static Integer STATUS_PREVNEXT_UNENABLE = 2;

	/**
	 * 仅下一页不可用状态
	 */
	public static Integer STATUS_NEXT_UNENABLE = 3;

	/**
	 * 全部可用状态
	 */
	public static Integer STATUS_ALL_ENABLE = 99;

	/**
	 * 组件高度
	 */
	private static final int HEIGHT = 16;

	/**
	 * 首页按钮
	 */
	JButton firstBtn = null;
	/**
	 * 上一页按钮
	 */
	JButton prevBtn = null;

	/**
	 * 下一页按钮
	 */
	JButton nextBtn = null;

	/**
	 * 末页按钮
	 */
	JButton lastBtn = null;

	/**
	 * 字符串儿“第”的文本标签
	 */
	private JLabel whichPageLabel;

	/**
	 * 字符串儿”每页行数“标签
	 */
	private JLabel pageSizeLabel;

	/**
	 * 字符串儿“共{0}行...共{0}页”标签
	 */
	private JLabel pageTotalInfoLabel;

	/**
	 * 当前第几页文本框
	 */
	private NumberTextField currPageIndex = null;

	/**
	 * 每页显示数量文本框
	 */
	private NumberTextField pageSizeField = null;

	private boolean showLeftBorder = true;

	BXBillMainPanel panel;

	/**
	 * 共计页数
	 */
	private Integer totalPageSize;

	/**
	 * 共计行数
	 */
	private Integer totalRowCount;

	public BXPaginationBar(BXBillMainPanel panel) {
		super();
		this.panel = panel;
		initialize();
	}

	private void initialize() {
		initUI();
		if (this.isShowLeftBorder())
			setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, splitColor));
		valueChanged();
	}

	private void initUI() {
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(getFirstBtn());
		add(getPrevBtn());
		add(getNextBtn());
		add(getLastBtn());
		add(getWhichPageLabel());
		add(getCurrPageIndex());
		add(getPageTotalInfoLabel());
		add(getPageSizeLabel());
		add(getPageSizeField());

		setButtonFeature();
	}

	public void setTotalPageSize(Integer totalPageSize) {
		this.totalPageSize = totalPageSize;
	}

	public void setTotalRowCount(Integer totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	private Integer getTotalPageSize() {
		if (totalPageSize == null) {
			totalPageSize = 0;
		}
		return totalPageSize;
	}

	private Integer getTotalRowCount() {
		if (totalRowCount == null) {
			totalRowCount = 0;
		}
		return totalRowCount;
	}

	private void setPerPageSize(Integer perPageSize) {
		if (perPageSize < 0) {
			perPageSize = IBXPageConst.DEFAULT_PER_PAGE_SIZE;
		} else if (perPageSize > IBXPageConst.MAX_PER_PAGE_SIZE) {
			perPageSize = IBXPageConst.MAX_PER_PAGE_SIZE;
		}
		getPageSizeField().setText("" + perPageSize);
	}

	/**
	 * 每页大小
	 *
	 * @return
	 */
	public Integer getPerPageSize() {
		String text = getPageSizeField().getText();
		if (text != null && text.trim().length() > 0) {
			return Integer.parseInt(text);
		}
		return IBXPageConst.DEFAULT_PER_PAGE_SIZE;
	}

	private void setCurrPage(Integer currPage) {
		if (currPage==null||currPage < 1) {
			currPage = 1;
		} else if (currPage > getTotalPageSize()) {
			currPage = getTotalPageSize();
			if(currPage==0){
				currPage = 1;
			}
		}
		getCurrPageIndex().setText("" + currPage);
	}

	/**
	 * 当前页码
	 *
	 * @return
	 */
	public Integer getCurrPage() {
		String text = getCurrPageIndex().getText();
		if (text != null && text.trim().length() > 0) {
			return Integer.parseInt(text);
		}
		return 1;
	}

	/**
	 * 只允许输入数字的文本输入框
	 *
	 * @author chendya
	 *
	 */
	final class NumberTextField extends JTextField {

		NumberTextField() {
			super();
		}

		@Override
		protected Document createDefaultModel() {
			return new NumberDocument();
		}

		class NumberDocument extends PlainDocument {
			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {
				if (str == null || str.length() == 0) {
					return;
				}
				char[] ch = str.toCharArray();
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < ch.length; i++) {
					if (ch[i] >= '0' && ch[i] <= '9') {
						buffer.append(ch[i] + "");
					}
				}
				super.insertString(offs, buffer.toString(), a);
			}
		}
	}

	private JButton getFirstBtn() {
		if (firstBtn == null) {
			firstBtn = new JButton("|<");
			firstBtn.setToolTipText(NCLangRes.getInstance().getStrByID("uif2",
					"PaginationBar-000000")/* 首页 */);
		}
		return firstBtn;
	}

	private JButton getPrevBtn() {
		if (prevBtn == null) {
			prevBtn = new JButton("<");
			prevBtn.setToolTipText(NCLangRes.getInstance().getStrByID("uif2",
					"PaginationBar-000001")/* 上一页 */);
		}
		return prevBtn;
	}

	public JButton getNextBtn() {
		if (nextBtn == null) {
			nextBtn = new JButton(">");
			nextBtn.setToolTipText(NCLangRes.getInstance().getStrByID("uif2",
					"PaginationBar-000002")/* 下一页 */);
		}
		return nextBtn;
	}

	public JButton getLastBtn() {
		if (lastBtn == null) {
			lastBtn = new JButton(">|");
			lastBtn.setToolTipText(NCLangRes.getInstance().getStrByID("uif2",
					"PaginationBar-000003")/* 末页 */);
		}
		return lastBtn;
	}

	public JLabel getWhichPageLabel() {
		if (whichPageLabel == null) {
			whichPageLabel = new JLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0017")/*@res "第"*/);
			whichPageLabel.setPreferredSize(new Dimension(20, HEIGHT));
			whichPageLabel.setOpaque(false);
		}
		return whichPageLabel;
	}

	private NumberTextField getCurrPageIndex() {
		if (currPageIndex == null) {
			currPageIndex = new NumberTextField();
			currPageIndex.setHorizontalAlignment(NumberTextField.CENTER);
			currPageIndex.setPreferredSize(new Dimension(26, HEIGHT));
			currPageIndex.setToolTipText(NCLangRes.getInstance().getStrByID(
					"uif2", "PaginationBar-000004")/* 当前页码 */);
			currPageIndex.addActionListener(this);
			currPageIndex.setOpaque(false);
			currPageIndex.setText("1");
		}
		return currPageIndex;
	}

	private JLabel getPageTotalInfoLabel() {
		if (pageTotalInfoLabel == null) {
			pageTotalInfoLabel = new JLabel();
			pageTotalInfoLabel.setPreferredSize(new Dimension(250, HEIGHT));
			pageTotalInfoLabel.setOpaque(false);
		}
		return pageTotalInfoLabel;
	}

	private JLabel getPageSizeLabel() {
		if (pageSizeLabel == null) {
			pageSizeLabel = new JLabel();
			pageSizeLabel.setPreferredSize(new Dimension(80, HEIGHT));
			pageSizeLabel.setText(NCLangRes.getInstance().getStrByID("uif2",
					"PaginationBar-000005")/* 每页行数 */);
			pageSizeLabel.setOpaque(false);
		}
		return pageSizeLabel;
	}

	private NumberTextField getPageSizeField() {
		if (pageSizeField == null) {
			pageSizeField = new NumberTextField();
			pageSizeField.setPreferredSize(new Dimension(30, HEIGHT));
			pageSizeField.setHorizontalAlignment(NumberTextField.CENTER);
			pageSizeField.addActionListener(this);
			pageSizeField.setOpaque(false);
			pageSizeField.setText(""+IBXPageConst.DEFAULT_PER_PAGE_SIZE);
		}
		return pageSizeField;
	}

	public void setButtonStatus(Integer status) {
		if (status == null) {
			return;
		}
		switch (status.intValue()) {
		case -1:
			// 全部不可用
			setButtonEnable(getButtons(), false);
			break;
		case 1:
			// 上一页不可用
			setButtonEnable(getButtons(), false);
			setButtonEnable(getPrevBtn(), true);
			break;
		case 2:
			// 下一页不可用
			setButtonEnable(getButtons(), false);
			setButtonEnable(getPrevBtn(), true);
			setButtonEnable(getNextBtn(), true);
			break;
		case 3:
			// 下一页不可用
			setButtonEnable(getButtons(), false);
			setButtonEnable(getNextBtn(), true);
			break;
		default:
			// 全部可用
			setButtonEnable(getButtons(), true);
		}
	}

	private void setButtonEnable(JButton[] buttons, boolean flag) {
		if (buttons == null || buttons.length == 0) {
			return;
		}
		for (int i = 0; i < buttons.length; i++) {
			setButtonEnable(buttons[i], flag);
		}
	}

	private void setButtonEnable(JButton btn, boolean flag) {
		btn.setEnabled(flag);
	}

	private JButton[] getButtons() {
		return new JButton[] { getFirstBtn(), getPrevBtn(), getNextBtn(),
				getLastBtn() };
	}

	private void setButtonFeature() {
		JButton[] buttons = getButtons();
		for (int i = 0; i < buttons.length; i++) {
			setButtonFeature(buttons[i]);
		}
	}

	private void setButtonFeature(JButton button) {
		button.setPreferredSize(new Dimension(30, 16));
		button.addActionListener(this);
		button.setFocusable(false);
		button.setOpaque(false);
	}

	public boolean isShowLeftBorder() {
		return showLeftBorder;
	}

	public void setShowLeftBorder(boolean showLeftBorder) {
		this.showLeftBorder = showLeftBorder;
	}

	public void valueChanged() {
		setPageTotalInfoString();
	}

	private void setPageTotalInfoString() {
		pageTotalInfoLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0018",null,new String[]{String.valueOf(getTotalPageSize()),String.valueOf(getTotalRowCount())})/*@res "页，共{0}页，{1} 条记录。"*/);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		try {
			doAction(source);
		} catch (BusinessException e1) {
			panel.handleException(e1);
		}
	}

	public void doAction(Object evtSource) throws BusinessException {

		if (evtSource == this.currPageIndex || this.pageSizeField == evtSource) {
			BXPageUtil util = new BXPageUtil(panel);
			util.setTotalRowCount(getTotalRowCount());
			setCurrPage(util.getCurrPage());
			setTotalPageSize(util.getTotalPageSize());
			setPerPageSize(getPerPageSize());
			goToPage();
			return;
		}

		if (evtSource == this.firstBtn) {
			setCurrPage(1);
		} else if (evtSource == this.prevBtn) {
			setCurrPage(getCurrPage() - 1);
		} else if (evtSource == this.nextBtn) {
			setCurrPage(getCurrPage() + 1);
		} else if (evtSource == this.lastBtn) {
			setCurrPage(getPerPageSize());
		}
		goToPage();
	}

	QueryAction action;

	public QueryAction getQueryAction() {
		if (action == null) {
			action = new QueryAction();
			action.setActionRunntimeV0(panel);
		}
		return action;
	}

	private void goToPage() throws BusinessException {
		getQueryAction().goToPageQuery();
		valueChanged();
	}

	@Override
	public void update(IPageSubject o, Object arg) {
		if (arg != null && arg instanceof BXPageUtil) {
			BXPageUtil page = (BXPageUtil) arg;
			setCurrPage(page.getCurrPage());
			setPerPageSize(page.getPerPageSize());
			setTotalRowCount(page.getTotalRowCount());
			setTotalPageSize(page.getTotalPageSize());
			valueChanged();
		}
	}
}