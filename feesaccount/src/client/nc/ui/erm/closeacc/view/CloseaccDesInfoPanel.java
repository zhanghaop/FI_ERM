package nc.ui.erm.closeacc.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;

import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.beans.toolbar.ToolBarButton;
import nc.ui.pub.beans.util.ColumnLayout;
import nc.ui.uif2.actions.ComponentMiniAction;
import nc.ui.uif2.components.IMiniminizedEventListener;
import nc.ui.uif2.components.IMinimizableComponent;
import nc.ui.uif2.components.MiniminizedEventSource;
import nc.ui.uif2.components.miniext.IMinimizableComponentExt;
import nc.ui.uif2.components.miniext.MiniIconAction;
import nc.uitheme.ui.ThemeResourceCenter;
/**
 *支持最小化的面板
 * wangled
 */
public class CloseaccDesInfoPanel extends UIPanel 
				implements IMinimizableComponentExt{
	private static final long serialVersionUID = 1L;
	
	private IMinimizableComponent miniDelegator = null;
	
	private List<MiniIconAction> miniIconActions = null;
	
	private ComponentMiniAction miniAction = null ;
	
	public CloseaccDesInfoPanel(){
		miniDelegator = new MiniminizedEventSource(this);
	}
	
	public void initUI(){
		//侧面面板支持最小化设置
		ToolBarButton btn = new ToolBarButton(getMiniAction());
		UIPanel panel =new UIPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(btn);
		panel.add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0013")/*@res "备注"*/));
		//设置面板中的内容
		this.setLayout(new ColumnLayout(ColumnLayout.TOP, 1, 1, false, false));
		this.add(panel);
		UITextArea expComp = new UITextArea();
		expComp.setPreferredSize((new Dimension(500,150)));
		this.add(expComp);
		expComp.setText(getExp());
		expComp.setEnabled(false);
	}
	
	//最小化的Action和图标
	private static final  Icon MinImageIcon = ThemeResourceCenter.getInstance().getImage("themeres/ui/right_stretch.png");
	private static final Icon MinImageHeighlightIcon = ThemeResourceCenter.getInstance().getImage("themeres/ui/toolbaricons/right_stretch_highlight.png");
	private ComponentMiniAction getMiniAction() {
		if(miniAction == null)
		{
			miniAction = new ComponentMiniAction(this);
			miniAction.putValue(Action.SMALL_ICON, MinImageIcon);
			miniAction.putValue(ToolBarButton.HIGHLIGHT_ICON, MinImageHeighlightIcon);
		}
		return miniAction;
	}

	private String getExp() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0014")/*@res "(1)期初关闭后才能结账\n(2)截止本月待摊费用全部摊销后才能结账\n(3)已经结账的期间不能再进行任何业务处理"*/;
	}

	@Override
	public List<MiniIconAction> getMiniIconActions() {
		return this.miniIconActions;
	}
	
	public void setMiniIconActions(List<MiniIconAction> actions)
	{
		this.miniIconActions = actions;
	}

	@Override
	public boolean isMiniminized() {
		return miniDelegator.isMiniminized();
	}

	@Override
	public void miniminized() {
		miniDelegator.miniminized();
	}

	@Override
	public void setMiniminized(boolean isMini) {
		miniDelegator.setMiniminized(isMini);
		
	}

	@Override
	public void setMiniminizedEventListener(IMiniminizedEventListener listener) {
		miniDelegator.setMiniminizedEventListener(listener);
	}

}