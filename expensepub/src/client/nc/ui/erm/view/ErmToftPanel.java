package nc.ui.erm.view;

import nc.ui.glpub.IParent;
import nc.ui.glpub.IUiPanel;
import nc.ui.pub.ButtonObject;
import nc.ui.uif2.ToftPanelAdaptor;

/**
 * ���ù���ڵ������
 *
 * @author lvhj
 *
 */
public class ErmToftPanel extends ToftPanelAdaptor implements IUiPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	boolean initialized;

	public void addListener(Object arg0, Object arg1) {
	}

	public void removeListener(Object arg0, Object arg1) {
	}

	public Object invoke(Object arg0, Object arg1) {
		// ����ƾ֤��������¼���������Ҫ��������
		return null;
	}

	public void nextClosed() {
		// do nothing
	}
	private  nc.ui.glpub.IParent m_parent = null;

	/**
	 * �����ʼ����ʶ
	 */
	private boolean isinit = false;

	public void showMe(IParent parent) {
		parent.getUiManager().add(this, this.getName());
		m_parent = parent;
//		setFrame(parent.getFrame());
		if(!this.isinit){
			isinit = true;
			this.initUI();
		}
	}

	public IParent getUiManager(){
		return m_parent;
	}

	@Override
	public ButtonObject[] getButtons() {
		return null;
	}

	@Override
	public void onButtonClicked(ButtonObject bo) {
	}
}