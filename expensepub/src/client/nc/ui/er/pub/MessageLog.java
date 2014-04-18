package nc.ui.er.pub;

/**
 * �ڴ˴���������˵�����鿴��־
 * �������ڣ�(01-9-30 9:37:33)
 * @author���·�
 */
import nc.vo.er.exception.ExceptionHandler;

public class MessageLog extends nc.ui.pub.beans.UIDialog {

	private static final long serialVersionUID = -219440886130217464L;

	private nc.ui.pub.beans.UIPanel ivjJDialogContentPane = null;

	private nc.ui.pub.beans.UITextArea ivjJTextArea1 = null;

	private nc.ui.pub.beans.UIScrollPane ivjJScrollPane1 = null;


	public MessageLog(java.awt.Container parent) {
		super(parent);
		initialize();
	}

	/**
	 * Aaaaaaa ������ע�⡣
	 * 
	 * @param parent
	 *            java.awt.Container
	 * @param title
	 *            java.lang.String
	 */
	public MessageLog(java.awt.Container parent, String title) {
		super(parent, title);
		initialize();
	}

	/**
	 * ShenheLog ������ע�⡣
	 * 
	 * @param owner
	 *            java.awt.Frame
	 */
	public MessageLog(java.awt.Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * ShenheLog ������ע�⡣
	 * 
	 * @param owner
	 *            java.awt.Frame
	 * @param title
	 *            java.lang.String
	 */
	public MessageLog(java.awt.Frame owner, String title) {
		super(owner, title);
		initialize();
	}

	@SuppressWarnings("rawtypes")
    public void f_setText(java.util.Vector textV) {
		StringBuilder sbValue = new StringBuilder();
		for (int i = 0; i < textV.size(); i++) {
			sbValue.append(textV.elementAt(i).toString()).append("\r\n");
		}
		getJTextArea1().setText(sbValue.toString());
	}

	/**
	 * ���� JDialogContentPane ����ֵ��
	 * 
	 * @return com.sun.java.swing.JPanel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIPanel getJDialogContentPane() {
		if (ivjJDialogContentPane == null) {
			try {
				ivjJDialogContentPane = new nc.ui.pub.beans.UIPanel();
				ivjJDialogContentPane.setName("JDialogContentPane");
				ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
				getJDialogContentPane().add(getJScrollPane1(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJDialogContentPane;
	}

	/**
	 * ���� JScrollPane1 ����ֵ��
	 * 
	 * @return com.sun.java.swing.JScrollPane
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new nc.ui.pub.beans.UIScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				getJScrollPane1().setViewportView(getJTextArea1());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane1;
	}

	/**
	 * ���� JTextArea1 ����ֵ��
	 * 
	 * @return com.sun.java.swing.JTextArea
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UITextArea getJTextArea1() {
		if (ivjJTextArea1 == null) {
			try {
				ivjJTextArea1 = new nc.ui.pub.beans.UITextArea();
				ivjJTextArea1.setName("JTextArea1");
				ivjJTextArea1.setBounds(0, 0, 600, 410);
				// user code begin {1}

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextArea1;
	}

	/**
	 * ÿ�������׳��쳣ʱ������
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable e) {

		/* ��ȥ���и��е�ע�ͣ��Խ�δ��׽�����쳣��ӡ�� stdout�� */
		ExceptionHandler.debug(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000281")/* @res "--------- δ��׽�����쳣 ---------" */);
		ExceptionHandler.consume(e);
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			// user code begin {1}
			this.setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000950")/* @res "�鿴��־" */);
			// user code end
			setName("ShenheLog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(600, 410);
			setContentPane(getJDialogContentPane());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	/**
	 * ����ڵ� - ��������ΪӦ�ó�������ʱ���������������
	 * 
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		// try {
		// ShenheLog s;
		// s = new ShenheLog();
		// s.setModal(true);
		// s.addWindowListener(new java.awt.event.WindowAdapter() {
		// public void windowClosing(java.awt.event.WindowEvent e) {
		// System.exit(0);
		// };
		// });
		// s.show();
		// java.awt.Insets insets = s.getInsets();
		// s.setSize(s.getWidth() + insets.left + insets.right, s.getHeight() + insets.top + insets.bottom);
		// s.setVisible(true);
		// } catch (Throwable exception) {
		// System.err.println("nc.ui.pub.beans.UIDialog �� main() �з����쳣");
		// }
	}
}