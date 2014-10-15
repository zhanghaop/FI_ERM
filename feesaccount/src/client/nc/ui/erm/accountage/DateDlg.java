package nc.ui.erm.accountage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;

import nc.ui.bd.manage.UIRefCellEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITablePane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillCellEditor;
import nc.vo.pub.lang.UFDate;

/**
 * 日期期间设置对话框<br>
 *
 * @since V60<br>
 */
public class DateDlg extends UIDialog {
	/** 序列化版本ID */
	private static final long serialVersionUID = 1L;

	/** 界面按钮名称 */
	private static final String BTN_ADD = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000002")/*@res "增加"*/;
	private static final String BTN_OK = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "确定"*/;
	private static final String BTN_CANCEL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000008")/*@res "取消"*/;
	private static final String BTN_INSERT = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0000")/*@res "插入"*/;
	private static final String BTN_DELETE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000039")/*@res "删除"*/;

	/** 表头名称 */
	private static final String[] TABLE_HEADER_NAME = { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001821")/*@res "序号"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003900")/*@res "起始日期"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001971")/*@res "截止日期"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002448")/*@res "显示名称"*/ };

	/** 对话框标题 */
	private String m_sTitle = null;

	/** 内容面板 */
	private UIPanel uiDialogContentPane = null;
	private UIPanel uiPanelNorth = null;
	private UIPanel uiPanelSouth = null;
	private UITablePane dateTablePanelCenter = null;

	// 按钮
	private JButton btnAdd = null;
	private JButton btnOk = null;
	private JButton btnCancel = null;
	private JButton btnInsert = null;
	private JButton btnDel = null;

	// 表格
	private UITable m_table;
	// 表格模型
	private DefaultTableModel m_tableModel = null;

	private JLabel m_jLabelNorth = null;

	/** 日期参照 */
	private UIRefPane dateRefPane = null;

	private DateEventHandler dateEventHandler = new DateEventHandler();

	public DateDlg(Container parent, String title) {
		super(parent, title);
		this.m_sTitle = title;
		initialize();
	}

	/**
	 * 界面初始化方法
	 */
	private void initialize() {
		setName("DateDlg");
		setTitle(m_sTitle);
		setSize(500, 350);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getUIDialogContentPane());
		// 初始化按钮事件处理器
		initButtonEventHandler();
		// 初始化表格
		initTable();
	}

	/**
	 * 总内容面板
	 *
	 * @return UIPanel
	 */
	private UIPanel getUIDialogContentPane() {
		if (uiDialogContentPane == null) {
			uiDialogContentPane = new UIPanel();

			uiDialogContentPane.setName("UIDialogContentPane");
			uiDialogContentPane.setLayout(new BorderLayout());

			uiDialogContentPane.add(getUIPanelNorth(), "North");
			uiDialogContentPane.add(getUIPanelSouth(), "South");
			uiDialogContentPane.add(getDateTablePanelCenter(), "Center");
		}
		return uiDialogContentPane;
	}

	private void initButtonEventHandler() {
		getAddBtn().addMouseListener(dateEventHandler);
		getInsertBtn().addMouseListener(dateEventHandler);
		getDelBtn().addMouseListener(dateEventHandler);
		getOKBtn().addMouseListener(dateEventHandler);
		getCancelBtn().addMouseListener(dateEventHandler);
	}

	private UIPanel getUIPanelNorth() {
		if (uiPanelNorth == null) {
			uiPanelNorth = new UIPanel();
			uiPanelNorth.setName("UIPanelNorth");
			uiPanelNorth.setPreferredSize(new Dimension(10, 30));
			uiPanelNorth.setLayout(new BorderLayout());
			uiPanelNorth.add(getJLabelNorth(), "Center");
		}
		return uiPanelNorth;
	}

	private UIPanel getUIPanelSouth() {
		if (uiPanelSouth == null) {
			uiPanelSouth = new UIPanel();
			uiPanelSouth.setName("UIPanelSouth");
			uiPanelSouth.setPreferredSize(new Dimension(10, 50));
			uiPanelSouth.setLayout(null);
			uiPanelSouth.add(getAddBtn(), getAddBtn().getName());
			uiPanelSouth.add(getOKBtn(), getOKBtn().getName());
			uiPanelSouth.add(getCancelBtn(), getCancelBtn().getName());
			uiPanelSouth.add(getInsertBtn(), getInsertBtn().getName());
			uiPanelSouth.add(getDelBtn(), getDelBtn().getName());
		}
		return uiPanelSouth;
	}

	private UITablePane getDateTablePanelCenter() {
		if (dateTablePanelCenter == null) {
			dateTablePanelCenter = new UITablePane();
			dateTablePanelCenter.setName("DateTablePanelCenter");
		}
		return dateTablePanelCenter;
	}

	private JLabel getJLabelNorth() {
		if (m_jLabelNorth == null) {
			m_jLabelNorth = new JLabel();
			m_jLabelNorth.setName("JLabel1");
			m_jLabelNorth.setText(m_sTitle);
			m_jLabelNorth.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return m_jLabelNorth;
	}

	private JButton getAddBtn() {
		if (btnAdd == null) {
			btnAdd = new JButton();
			btnAdd.setName("AddBtn");
			btnAdd.setText(BTN_ADD);
			btnAdd.setBounds(12, 12, 85, 27);
			btnAdd.setMargin(new Insets(4, 0, 0, 0));
			btnAdd.setHorizontalTextPosition(SwingConstants.CENTER);
			btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		return btnAdd;
	}

	private JButton getInsertBtn() {
		if (btnInsert == null) {
			btnInsert = new JButton();
			btnInsert.setName("InsertBtn");
			btnInsert.setText(BTN_INSERT);
			btnInsert.setBounds(109, 12, 85, 27);
			btnInsert.setMargin(new Insets(4, 0, 0, 0));
			btnInsert.setHorizontalTextPosition(SwingConstants.CENTER);
			btnInsert.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		return btnInsert;
	}

	private JButton getDelBtn() {
		if (btnDel == null) {
			btnDel = new JButton();
			btnDel.setName("DelBtn");
			btnDel.setText(BTN_DELETE);
			btnDel.setBounds(206, 12, 85, 27);
			btnDel.setMargin(new Insets(4, 0, 0, 0));
			btnDel.setHorizontalTextPosition(SwingConstants.CENTER);
			btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		return btnDel;
	}

	private JButton getOKBtn() {
		if (btnOk == null) {
			btnOk = new JButton();
			btnOk.setName("OKBtn");
			btnOk.setText(BTN_OK);
			btnOk.setBounds(303, 12, 85, 27);
			btnOk.setMargin(new Insets(4, 0, 0, 0));
			btnOk.setHorizontalTextPosition(SwingConstants.CENTER);
			btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		return btnOk;
	}

	private JButton getCancelBtn() {
		if (btnCancel == null) {
			btnCancel = new JButton();
			btnCancel.setName("CancelBtn");
			btnCancel.setText(BTN_CANCEL);
			btnCancel.setBounds(400, 12, 85, 27);
			btnCancel.setMargin(new Insets(4, 0, 0, 0));
			btnCancel.setHorizontalTextPosition(SwingConstants.CENTER);
			btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		return btnCancel;
	}

	public void initTable() {
		// 设定Table
		m_table = getDateTablePanelCenter().getTable();

		// 设定数据模型
		Object[][] obj = new Object[0][4];
		m_tableModel = new DefaultTableModel();
		m_tableModel.setDataVector(obj, TABLE_HEADER_NAME);
		m_table.setModel(m_tableModel);

		TableColumn column = null;
		// 布局，为每一列设置编辑器
		column = m_table.getColumn(TABLE_HEADER_NAME[0]);
		column.setCellRenderer(new SequenceRender());
		column.setMaxWidth(50);
		column.setMinWidth(25);

		column = m_table.getColumn(TABLE_HEADER_NAME[1]);
		column.setCellEditor(new UIRefCellEditor(getDateRefPanel()));

		column = m_table.getColumn(TABLE_HEADER_NAME[2]);
		column.setCellEditor(new UIRefCellEditor(getDateRefPanel()));

		column = m_table.getColumn(TABLE_HEADER_NAME[3]);
		column.setMaxWidth(200);
		column.setMinWidth(160);
		column.setCellEditor(new BillCellEditor(new UITextField()));

		// 处理插入行以后，编辑截止日期的事件
		// 使待定日期为上一行截止日期+1
		m_tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					int editColumn = e.getColumn();
					if (editColumn == 2) {
						UFDate editDate = getAimDate(e.getFirstRow(), editColumn);
						if (editDate == null) {
							return;
						}
						if (e.getFirstRow() < m_table.getRowCount() - 1) {
							// 不是最后一行
							UIRefPane refPane = (UIRefPane) ((UIRefCellEditor) m_table
									.getCellEditor(e.getFirstRow() + 1, editColumn - 1)).getComponent();
							refPane.setValue(editDate.getDateAfter(1).toStdString());
						}
					}

				}
			}
		});

		m_table.doLayout();
	}

	private UIRefPane getDateRefPanel() {
		if (dateRefPane == null) {
			dateRefPane = new UIRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0001")/*@res "日历"*/);
			dateRefPane.setName("dateRefPanel");
			dateRefPane.setValue(null);
			dateRefPane.setLocation(654, 350);
		}
		return dateRefPane;
	}

	public Object[][] getData() {
		Object[][] datas = convertVector2DToObject2D(m_tableModel.getDataVector(), 0);
		return convert2AccountAgeInterval(datas);
	}

	/**
	 * 将表格的数据模型中的数据转化成二维数组输出， 并且行数比必需的多add行<br>
	 *
	 * @param dataVector
	 * @param add
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object[][] convertVector2DToObject2D(Vector dataVector, int add) {
		Vector rowVector = null;
		Vector midVector = new Vector();
		for (int i = 0; i < dataVector.size(); i++) {
			rowVector = (Vector<Object>) dataVector.get(i);
			midVector.add(rowVector);
		}

		int len = midVector.size();
		Object[][] rs = new Object[len + add][rowVector.size()];
		Object data = null;
		for (int i = 0; i < len; i++) {
			rowVector = (Vector) midVector.get(i);
			for (int j = 0; j < rowVector.size(); j++) {
				data = rowVector.elementAt(j);
				rs[i][j] = data == null ? "" : data.toString();
			}
		}

		// TEST1:
		// rs = new Object[3][4];
		// rs[0][0] = 1;
		// rs[0][1] = "2010-11-05";
		// rs[0][2] = "2010-12-05";
		// rs[0][3] = "";
		// rs[1][0] = 2;
		// rs[1][1] = "2010-12-06";
		// rs[1][2] = "2011-01-05";
		// rs[1][3] = "";
		// rs[2][0] = 3;
		// rs[2][1] = "2011-01-06";
		// rs[2][2] = "";
		// rs[2][3] = "";

		// TEST2:
		// rs = new Object[1][4];
		// rs[0][0] = 1;
		// rs[0][1] = "2010-11-05";
		// rs[0][2] = "";
		// rs[0][3] = "";

		// TEST3:
		// rs = new Object[1][4];
		// rs[0][0] = 1;
		// rs[0][1] = "";
		// rs[0][2] = "2010-11-05";
		// rs[0][3] = "";

		return rs;
	}

	/**
	 * 将二维数组转化为账龄分析必须的期间格式、账龄描述<br>
	 *
	 * @param datas<br>
	 * @return Object[][]<br>
	 */
	private Object[][] convert2AccountAgeInterval(Object[][] datas) {
		for (int i = 0; i < datas.length; i++) {
			if (StringUtils.isEmpty(datas[i][3].toString())) {
				if (StringUtils.isEmpty(datas[i][1].toString())) {
					datas[i][1] = new UFDate("1900-01-01").toString();
					datas[i][3] = datas[i][2] + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0002")/*@res "以前"*/;
					datas[i][2] = new UFDate(datas[i][2].toString()).toString();
				} else if (StringUtils.isEmpty(datas[i][2].toString())) {
					datas[i][2] = new UFDate("7099-12-31").toString();
					datas[i][3] = datas[i][1] + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0003")/*@res "以后"*/;
					datas[i][1] = new UFDate(datas[i][1].toString()).toString();
				} else {
					datas[i][3] = datas[i][1] + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000626")/*@res "到"*/ + datas[i][2];
					datas[i][1] = new UFDate(datas[i][1].toString()).toString();
					datas[i][2] = new UFDate(datas[i][2].toString()).toString();
				}
			}
		}

		return datas;
	}

	class DateEventHandler implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		};

		public void mouseEntered(MouseEvent e) {
		};

		public void mouseExited(MouseEvent e) {
		};

		public void mousePressed(MouseEvent e) {
		};

		public void mouseReleased(MouseEvent e) {
			if (e.getSource() == DateDlg.this.getAddBtn()) {
				connE2MAdd(e);
			} else if (e.getSource() == DateDlg.this.getInsertBtn()) {
				connE2MInsert(e);
			} else if (e.getSource() == DateDlg.this.getDelBtn()) {
				connE2MDel(e);
			} else if (e.getSource() == DateDlg.this.getOKBtn()) {
				connE2MOk(e);
			} else if (e.getSource() == DateDlg.this.getCancelBtn()) {
				connE2MCancel(e);
			}
		};

	};

	private void connE2MAdd(MouseEvent e) {
		this.onAdd();
	}

	private void connE2MInsert(MouseEvent e) {
		// 停止编辑状态
		stopEditor();

		int rowCount = m_tableModel.getRowCount(); // 获取总行数
		int rowSelected = m_table.getSelectedRow(); // 获取选中行


		if (rowCount == 1) { // 仅有一行
			this.onAdd();
			return;
		} else if (rowSelected == rowCount - 1) { // 选中的是最后一行
			this.onAdd();
			return;
		}

		// 中间行
		UFDate upDate = getAimDate(rowSelected, 2);
		UFDate downDate = getAimDate(rowSelected + 1, 2);
		if (upDate == null || downDate == null) {
			return;
		}

		int distance = UFDate.getDaysBetween(upDate, downDate);
		// 没有空间用来插入新行
		if (distance == 1) {
			return;
		}

		Object[] obj = new Object[4];
		obj[0] = rowSelected + 1;
		UFDate newRowDate = getAimDate(rowSelected + 1, 1);
		obj[1] = newRowDate == null ? "" : newRowDate.toStdString();

		m_tableModel.setValueAt(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0004")/*@res "待定"*/, rowSelected + 1, 1);
		m_tableModel.insertRow(rowSelected + 1, obj);

		// 更新行号
		for (int i = rowSelected; i < rowCount + 1; i++) {
			m_tableModel.setValueAt(i + 1, i, 0);
		}

		m_table.editCellAt(rowSelected + 1, 2);
		return;
	}

	private void connE2MDel(MouseEvent e) {
		// 停止编辑状态
		stopEditor();

		int rowSelected = m_table.getSelectedRow();
		int rowCount = m_tableModel.getRowCount();
		if (rowCount == 1) {
			// 只有一行，不允许删除
			return;
		}

		// 第一行
		if (rowSelected == 0) {
			m_tableModel.setValueAt(null, rowSelected + 1, 1);
			m_tableModel.removeRow(rowSelected);

			// 更正行号
			for (int i = 0; i < rowCount - 1; i++) {
				m_tableModel.setValueAt(i + 1, i, 0);
			}
			return;
		}

		// 最后一行
		if (rowSelected == rowCount - 1) {
			m_tableModel.removeRow(rowSelected);
			return;
		}

		// 中间行
		UFDate delDate = getAimDate(rowSelected, 1);
		m_tableModel.setValueAt(delDate == null ? "" : delDate.toStdString(), rowSelected + 1, 1);
		m_tableModel.removeRow(rowSelected);

		// 更正行号
		for (int i = rowSelected; i < rowCount - 1; i++) {
			m_tableModel.setValueAt(i + 1, i, 0);
		}

	}

	private void connE2MOk(MouseEvent e) {
		// 停止编辑状态
		stopEditor();

		if (m_tableModel.getRowCount() == 0) {
			return;
		}

		String errMsg = doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0005")/*@res "错误"*/, errMsg);
			return;
		}

		super.closeOK();
	}

	private void connE2MCancel(MouseEvent e) {
		// 停止编辑状态
		stopEditor();
		super.closeCancel();
	}

	/**
	 * 增加一新行，起始日期为上一行的截止日期+1天
	 */
	public void onAdd() {
		// 停止编辑状态
		stopEditor();

		// 检查是否可以增加
		int maxRowCount = m_tableModel.getRowCount();
		UFDate lastDate = getAimDate(maxRowCount - 1, 2);
		boolean forbit = (lastDate == null) ? true : false;
		if (maxRowCount == 0) {
			forbit = false;
		}

		if (forbit) {
			return;
		}

		// 新行
		Object[] obj = new Object[4];
		obj[0] = "" + (maxRowCount + 1);
		if (lastDate != null) {
			lastDate = lastDate.getDateAfter(1);
		}
		obj[1] = lastDate == null ? "" : lastDate.toStdString();
		obj[3] = "";

		m_tableModel.addRow(obj);
	}

	/**
	 * 获取指定行和列的日期值
	 *
	 * @param row 行号
	 * @param col 列号
	 * @return UFDate
	 */
	public UFDate getAimDate(int row, int col) {
		UFDate date = null;
		if (row >= 0 && row < m_tableModel.getRowCount() && col >= 0
				&& col < m_tableModel.getColumnCount()) {
			Object val = m_tableModel.getValueAt(row, col);
			if (val != null && !"".equals(val)) {
				date = new UFDate(val.toString());
			}
		}
		return date;
	}

	/**
	 * 关闭正在编辑的单元
	 */
	private void stopEditor() {
		if (m_tableModel.getRowCount() == 0) {
			return;
		}

		TableColumnModel columnModel = m_table.getColumnModel();
		TableCellEditor editor = null;
		for (int i = 0; i < m_tableModel.getColumnCount(); i++) {
			editor = columnModel.getColumn(i).getCellEditor();
			if (editor != null) {
				editor.stopCellEditing();
			}
		}

		m_table.validate();
	}

	private String doBusiCheck() {
		String errMsg = null;
		Object[][] datas = convertVector2DToObject2D(m_tableModel.getDataVector(), 0);
		if (datas == null || datas.length == 0) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0006")/*@res "至少输入一个账龄区间！"*/;
		}

		String beforeDate = datas[0][1].toString();
		String afterDate = datas[0][2].toString();
		if (StringUtils.isEmpty(beforeDate) && StringUtils.isEmpty(afterDate)) {
			errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0006")/*@res "至少输入一个账龄区间！"*/;
		} else {
			if (!StringUtils.isEmpty(beforeDate) && !StringUtils.isEmpty(afterDate) && beforeDate.compareTo(afterDate) > 0) {
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0008",null,new String[]{String.valueOf(1)})/*@res "第"i行开始日期不能大于截止日期！"*/;
			} else {
				boolean noErr = true;
				for (int i = 1; i < datas.length && noErr; i++) {
					for (int j = 1; j < 3 && noErr; j++) {
						beforeDate = afterDate;
						afterDate = datas[i][j].toString();

						boolean afterDateIsNull = StringUtils.isEmpty(afterDate);
						if (afterDateIsNull && i < datas.length - 1) {
							errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0009",null,new String[]{String.valueOf(i + 1)})/*@res "第i行日期不允许为空！"*/;
							noErr = false;
							break;
						}

						if (!afterDateIsNull && beforeDate.compareTo(afterDate) > 0 && j == 2) {
							errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0008",null,new String[]{String.valueOf(i + 1)})/*@res "第i行开始日期不能大于截止日期！"*/;
							noErr = false;
							break;
						}

						if (!afterDateIsNull && beforeDate.compareTo(afterDate) >= 0 && j == 1) {
							errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0011",null,new String[]{String.valueOf(i + 1),String.valueOf(i)})/*@res "第i+1行开始日期必须大于第i行截止日期"*/;
							noErr = false;
							break;
						}
					}
				}
			}
		}

		return errMsg;
	}

}

