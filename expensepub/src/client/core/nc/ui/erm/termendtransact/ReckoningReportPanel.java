package nc.ui.erm.termendtransact;

import java.util.Vector;

import nc.vo.er.exception.ExceptionHandler;

public class ReckoningReportPanel extends nc.ui.pub.beans.UIPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6849402931006397761L;
	private nc.ui.pub.beans.UIPanel ivjResultPanel = null;
	private nc.ui.pub.beans.UITablePane ivjResultTable = null;
	private String[] m_sTitle = {nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0001821")/*@res "���"*/,nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000069")/*@res "��ĩ��鲻�ϸ񵥾�"*/,""};
	private Vector m_vData = null;
/**
 * ReckoingReportPanel ������ע�⡣
 */
public ReckoningReportPanel() {
	super();
	initialize();
}
/**
 * ReckoingReportPanel ������ע�⡣
 * @param p0 java.awt.LayoutManager
 */
public ReckoningReportPanel(java.awt.LayoutManager p0) {
	super(p0);
}
/**
 * ReckoingReportPanel ������ע�⡣
 * @param p0 java.awt.LayoutManager
 * @param p1 boolean
 */
public ReckoningReportPanel(java.awt.LayoutManager p0, boolean p1) {
	super(p0, p1);
}
/**
 * ReckoingReportPanel ������ע�⡣
 * @param p0 boolean
 */
public ReckoningReportPanel(boolean p0) {
	super(p0);
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-9-21 11:22:19)
 * ����޸����ڣ�(2001-9-21 11:22:19)
 * @author��wyan
 * @return nc.vo.pub.ValueObject[]
 */
public Vector getData() {
	return m_vData;
}
/**
 * ���� ResultPanel ����ֵ��
 * @return nc.ui.pub.beans.UIPanel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UIPanel getResultPanel() {
	if (ivjResultPanel == null) {
		try {
			ivjResultPanel = new nc.ui.pub.beans.UIPanel();
			ivjResultPanel.setName("ResultPanel");
			ivjResultPanel.setLayout(new java.awt.BorderLayout());
			getResultPanel().add(getResultTable(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResultPanel;
}
/**
 * ���� ResultTable ����ֵ��
 * @return nc.ui.pub.beans.UITablePane
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UITablePane getResultTable() {
	if (ivjResultTable == null) {
		try {
			ivjResultTable = new nc.ui.pub.beans.UITablePane();
			ivjResultTable.setName("ResultTable");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResultTable;
}
/**
 * ÿ�������׳��쳣ʱ������
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable e) {

	ExceptionHandler.consume(e);
}
/**
 * ��ʼ���ࡣ
 */
/* ���棺�˷������������ɡ� */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ReckoingReportPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(521, 319);
		add(getResultPanel(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
//	initTable();
	// user code end
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-17 10:20:08)
 * ����޸����ڣ�(2001-8-17 10:20:08)
 * @author��wyan
 */
public void initTable() {

	try {
		nc.ui.pub.beans.table.NCTableModel model = new nc.ui.pub.beans.table.NCTableModel();
		Vector title = TermEndTransactUI.converToVector(m_sTitle);
		model.setDataVector(getData(), title);
		getResultTable().getTable().setModel(model);
		getResultTable().getTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		javax.swing.table.TableColumn col1 = getResultTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0001821")/*@res "���"*/);
		javax.swing.table.TableColumn col2 = getResultTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000069")/*@res "��ĩ��鲻�ϸ񵥾�"*/);
		javax.swing.table.TableColumn col3 = getResultTable().getTable().getColumn("");
		col1.setMinWidth(60);
		col1.setMaxWidth(60);
		col2.setMinWidth(390);
		col2.setMaxWidth(390);
		col3.setMinWidth(70);
		col3.setMaxWidth(70);
//		col1.setCellRenderer(new nc.vo.arap.transaction.MyTableCellRenderer());
//		col3.setCellRenderer(new nc.vo.arap.transaction.MyTableCellRenderer());
	}
	catch (Exception ex) {
		ExceptionHandler.consume(ex);
	}
}

/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-9-21 11:21:51)
 * ����޸����ڣ�(2001-9-21 11:21:51)
 * @author��wyan
 * @param voArr nc.vo.pub.ValueObject[]
 */
public void setData(Vector vData) {
    m_vData = vData;
}
}