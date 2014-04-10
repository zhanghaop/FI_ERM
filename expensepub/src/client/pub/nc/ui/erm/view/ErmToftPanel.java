package nc.ui.erm.view;

import nc.ui.glpub.IParent;
import nc.ui.glpub.IUiPanel;
import nc.ui.pub.ButtonObject;
import nc.ui.uif2.ToftPanelAdaptor;

/**
 * 费用管理节点入口类
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
		// 处理凭证动作后的事件，暂无需要处理内容
		return null;
	}

	public void nextClosed() {
		// do nothing
	}
	private  nc.ui.glpub.IParent m_parent = null;

	/**
	 * 界面初始化标识
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