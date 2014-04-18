package nc.ui.er.pub;

/**
 * 在此处插入类型说明。查看日志
 * 创建日期：(01-9-30 9:37:33)
 * @author：陈飞
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
	 * Aaaaaaa 构造子注解。
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
	 * ShenheLog 构造子注解。
	 * 
	 * @param owner
	 *            java.awt.Frame
	 */
	public MessageLog(java.awt.Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * ShenheLog 构造子注解。
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
	 * 返回 JDialogContentPane 特性值。
	 * 
	 * @return com.sun.java.swing.JPanel
	 */
	/* 警告：此方法将重新生成。 */
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
	 * 返回 JScrollPane1 特性值。
	 * 
	 * @return com.sun.java.swing.JScrollPane
	 */
	/* 警告：此方法将重新生成。 */
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
	 * 返回 JTextArea1 特性值。
	 * 
	 * @return com.sun.java.swing.JTextArea
	 */
	/* 警告：此方法将重新生成。 */
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
	 * 每当部件抛出异常时被调用
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable e) {

		/* 除去下列各行的注释，以将未捕捉到的异常打印至 stdout。 */
		ExceptionHandler.debug(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000281")/* @res "--------- 未捕捉到的异常 ---------" */);
		ExceptionHandler.consume(e);
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			// user code begin {1}
			this.setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000950")/* @res "查看日志" */);
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
	 * 主入口点 - 当部件作为应用程序运行时，启动这个部件。
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
		// System.err.println("nc.ui.pub.beans.UIDialog 的 main() 中发生异常");
		// }
	}
}