package nc.ui.er.basepub;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;

public class AbstractOKCancelDlg extends UIDialog  implements ActionListener  {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UIButton cancelButton = null;

	private UIButton okButton = null;

	private UIPanel northPanel = null;

	private UIPanel southPanel = null;

	private JPanel contentPane = null;

	/**
	 * @param parent
	 */
	public AbstractOKCancelDlg(Container parent) {
		super(parent);
		initialize();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getOkButton()) {
			onOk();
		} else if (e.getSource() == getCancelButton()) {
			onCancel();
		}
	}

	private JPanel getUIDialogContentPane() {
		if (contentPane == null) {
			try {
				contentPane = new JPanel();
				contentPane.setName("UIDialogContentPane");
				contentPane.setLayout(new java.awt.BorderLayout());
				contentPane.add(getNorthPanel(), "Center");
				contentPane.add(getSouthPanel(), "South");
			} catch (Throwable ex) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ex);;
			}
		}
		return contentPane;
	}

	/**
	 * <p>
	 * 获得OK按钮
	 * <p>
	 * 
	 * @return
	 */
	private UIButton getOkButton() {
		if (okButton == null) {
			try {
				okButton = new UIButton();
				okButton.setName("btnOk");
				okButton.setPreferredSize(new java.awt.Dimension(80, 22));
				okButton.setText(NCLangRes.getInstance().getStrByID("common",
						"UC001-0000044")/* "@res确定" */);
			} catch (Throwable ex) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ex);;
			}
		}
		return okButton;
	}

	private UIButton getCancelButton() {
		if (cancelButton == null) {
			try {
				cancelButton = new UIButton();
				cancelButton.setName("btnCancel");
				cancelButton.setText(NCLangRes.getInstance().getStrByID(
						"common", "UC001-0000008")/* "@res取消" */);
			} catch (Throwable ex) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ex);;
			}
		}
		return cancelButton;
	}

	/**
	 * <p>
	 * 获得按钮栏
	 * 
	 * @return
	 */
	private UIPanel getSouthPanel() {
		if (southPanel == null) {
			try {
				southPanel = new UIPanel();
				southPanel.setName("pnlSouth");
				southPanel.setPreferredSize(new java.awt.Dimension(10, 36));
				southPanel.setBorder(new EtchedBorder());
				southPanel.add(getOkButton(), getOkButton().getName());
				southPanel.add(getCancelButton(), getCancelButton().getName());
			} catch (Throwable ex) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ex);
			}
		}

		return southPanel;
	}

	/**
	 * <p>
	 * 获得内容栏
	 * 
	 * @return
	 */
	protected UIPanel getNorthPanel() {
		if (northPanel == null) {
			try {
				northPanel = new UIPanel();
				northPanel.setName("pnlNorth");
				northPanel.setPreferredSize(new java.awt.Dimension(10, 36));
				northPanel.setBorder(new EtchedBorder());
			} catch (Throwable ex) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ex);;
			}
		}

		return northPanel;
	}

	private final void initialize() {
		try {
			setName("okCancleDlg");
			setDefaultCloseOperation(initWindowConstants());
			setSize(initDlgWidth(), initDlgHigh());
			setContentPane(getUIDialogContentPane());
			initConnections();

		} catch (Throwable ex) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(ex);;
		}
	}

	protected int initDlgWidth() {
		return 230;
	}

	protected int initDlgHigh() {
		return 150;
	}

	protected int initWindowConstants() {
		return WindowConstants.DISPOSE_ON_CLOSE;
	}

	private void onOk() {
		if (onBoOK()) {
			closeOK();
		}
	}

	private void onCancel() {
		closeCancel();
	}

	/**
	 * <p>
	 * 执行OK按钮
	 * <p>
	 * 
	 * @return
	 */
	protected boolean onBoOK() {
		return true;
	}

	protected void initConnections() {
		getOkButton().addActionListener(this);
		getCancelButton().addActionListener(this);
	}



}
