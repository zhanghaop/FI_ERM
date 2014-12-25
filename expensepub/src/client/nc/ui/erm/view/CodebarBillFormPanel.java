package nc.ui.erm.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;

/**
 * 
 * @author wangled
 * 
 */
@SuppressWarnings("serial")
public class CodebarBillFormPanel extends UIPanel implements AppEventListener {
	/*
	 * �ؼ�����
	 */
	private UITextField field = null;

	private BillManageModel model;
	private String value = ""; // �洢����ϵ��

	public CodebarBillFormPanel() {
		super();
		initUI();
	}

	private void initUI() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(getUILabel());
		add(getUITextField());
		this.setBorder(null);
		getUITextField().requestFocusInWindow();
	}

	/**
	 * ʵ��Label����ʾ���֣�
	 * 
	 * @author liangjy1 2008-12-15 ����08:55:38
	 * @return
	 */
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
	private UITextField getUITextField() {
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
							
							AggregatedValueObject result = CodeBarQueryUtil.doBarCodeQuery(value, (BillManageModel) getModel());
							if (result == null) {
								ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000967")/*
																																					 * @
																																					 * res
																																					 * "δ�ҵ����������ĵ���"
																																					 */, getModel().getContext());
							}
							// ����Ѿ����������ѯ�������ݣ��Ͳ���׷��
							if (result != null) {
								if (oldPkSet.contains(((SuperVO)result.getParentVO()).getPrimaryKey())) {
									((BillManageModel) getModel()).directlyAdd(result);
									((BillManageModel) getModel()).directlyUpdate(result);//�����µ������������ʾ��������
								} else {
									// ����Ѿ����������ѯ�������ݣ��Ͳ���׷��
									((BillManageModel) getModel()).directlyUpdate(result);
								}
							}
						} catch (Exception ex) {
							ExceptionHandler.handleRuntimeException(ex);
						} finally {
							field.setText("");
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

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void handleEvent(AppEvent event) {
		// TODO Auto-generated method stub

	}
}
