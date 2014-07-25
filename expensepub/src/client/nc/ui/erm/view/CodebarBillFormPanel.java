package nc.ui.erm.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
/**
 * 
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class CodebarBillFormPanel extends UIPanel implements AppEventListener{
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
	private void initUI() 
	{
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
		label.setText("�����");
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
						try{
							List<JKBXVO> oldData = ((BillManageModel) getModel()).getData();
							Map<String,JKBXVO> oldDataMap= new HashMap<String,JKBXVO>();
							for (JKBXVO vo:oldData){
								if(oldDataMap.get(vo.getParentVO().getPrimaryKey())==null){
									oldDataMap.put(vo.getParentVO().getPrimaryKey(), vo);
								}
							}
							List<JKBXVO> values=CodeBarQueryUtil.doBarCodeQuery(value,(BillManageModel) getModel());
							if(values == null){
								ShowStatusBarMsgUtil.showStatusBarMsg("δ�ҵ����������ĵ���",getModel().getContext());
							}
					         // ����Ѿ����������ѯ�������ݣ��Ͳ���׷��
							if (values != null && values.size() != 0) {
								if (!oldDataMap.keySet().contains(
										values.get(0).getParentVO().getPrimaryKey())) {
									((BillManageModel) getModel()).directlyAdd(values.get(0));
									((BillManageModel) getModel()).nextRow();
								} else {
									((BillManageModel) getModel()).directlyUpdate(values.get(0));
								}
							}
						}catch(Exception ex){
							ExceptionHandler.handleRuntimeException(ex);
						}finally{
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
