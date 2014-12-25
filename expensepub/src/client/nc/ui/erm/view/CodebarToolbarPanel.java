package nc.ui.erm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

@SuppressWarnings("serial")
public class CodebarToolbarPanel extends CardLayoutToolbarPanel {

	private UIPanel rightPanel = null;
	private UITextField field = null;
	private UICheckBox checkBox = null;
	private String value = ""; // �洢����ϵ��

	public CodebarToolbarPanel() {
		super();
		initUI();
	}

	private void initUI() {
		setPreferredSize(new Dimension(100, 30));
		getRightPanel();
	}

	private UIPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new UIPanel();
			rightPanel.setName("baseFrame");
			rightPanel.setOpaque(false);
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
			rightPanel.add(getUICheckBox());
			rightPanel.add(getUILabel());
			rightPanel.add(getUITextField());
			add(rightPanel, BorderLayout.EAST);
		}
		return rightPanel;
	}

	private UILabel getUILabel() {
		UILabel label = new UILabel();
		label.setName("barCode");
		label.setOpaque(false);
		label.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000966")/*
																									 * @
																									 * res
																									 * "�����"
																									 */);
		return label;
	}

	/**
	 * ��ʼ���ı���
	 * 
	 * @author liangjy1 2008-12-15 ����08:56:36
	 * @return
	 */
	public UITextField getUITextField() {
		if (field == null) {
			field = new UITextField();
			field.setName("inputField");
			field.setPreferredSize(new Dimension(200, 20));
			field.requestFocusInWindow();
			field.setFont(new Font("Dialog", Font.PLAIN, 12));
			field.addKeyListener(new KeyListener() {
				@SuppressWarnings("unchecked")
				public void keyPressed(KeyEvent e) {
					char press = e.getKeyChar();
					if ('\n' == press) {
						value = field.getText().trim();
						try {
							List<AggregatedValueObject> oldData = ((BillManageModel) getModel()).getData();
							Set<String> oldPkSet = new HashSet<String>();
							for (AggregatedValueObject vo : oldData) {
								String pk = ((SuperVO) vo.getParentVO()).getPrimaryKey();
								if (pk != null) {
									oldPkSet.add(pk);
								}
							}
							
							AggregatedValueObject result = null;
							try {
								result = CodeBarQueryUtil.doBarCodeQuery(value, (BillManageModel) getModel());
							} catch (BusinessException e1) {
								ExceptionHandler.handleRuntimeException(e1);
							}
							if (result == null) {
								ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000967")/*
																																					 * @
																																					 * res
																																					 * "δ�ҵ����������ĵ���"
																																					 */,// NCLangRes.getInstance().getStrByID("common",
																																						// "UCH007")
																																						// */,
										getModel().getContext());
								Thread.sleep(100);
							} else if (result != null ) {
								if (!oldPkSet.contains(((SuperVO)result.getParentVO()).getPrimaryKey())) {
									((BillManageModel) getModel()).directlyAdd(result);
									((BillManageModel) getModel()).directlyUpdate(result);//�����µ������������ʾ��������
								} else {
									// ����Ѿ����������ѯ�������ݣ��Ͳ���׷��
									((BillManageModel) getModel()).directlyUpdate(result);
								}
							}
							field.setText("");
							if (isSelected()) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										field.requestFocusInWindow();
									}
								});
							} else {
								// ����ɨ��δѡ�У����뿨Ƭ����
								if (result != null){
									getModel().fireEvent(new AppEvent(AppEventConst.SHOW_EDITOR));
								}
							}
						} catch (InterruptedException et) {
							ExceptionHandler.handleRuntimeException(et);
						}
					}
				}

				public void keyReleased(KeyEvent e) {

				}

				public void keyTyped(KeyEvent e) {

				}
			});
		}
		return field;
	}

	/**
	 * ��ѡ���ʼ��
	 * 
	 * @author liangjy1 2008-12-18 ����02:04:46
	 * @return
	 */
	public UICheckBox getUICheckBox() {
		if (checkBox == null) {
			String CHECKSHOW = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000968")/*
																											 * @
																											 * res
																											 * "����ɨ��"
																											 */;
			checkBox = new UICheckBox(CHECKSHOW, false);
			checkBox.setOpaque(false);
		}
		return checkBox;
	}

	/**
	 * ��ѡ���Ƿ���ѡ��״̬
	 * 
	 * @author liangjy1 2008-12-15 ����04:10:58
	 * @return
	 */
	public boolean isSelected() {
		boolean flag = false;
		if (getUICheckBox().isSelected()) {
			flag = true;
		}
		return flag;
	}
}
