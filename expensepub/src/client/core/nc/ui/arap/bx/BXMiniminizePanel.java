package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UIToolBar;
import nc.ui.uif2.NCAction;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.arap.bx.util.BXConstans;

/**
 * 借款报销单据查询方案最小化后的面板
 * @author chendya
 *
 */
@SuppressWarnings("serial")
public class BXMiniminizePanel extends JPanel{
	
	BXQueryAreaShell queryAreaShell;

	UISplitPane splitPane;
	
	UIToolBar toolBar;
	
	public BXMiniminizePanel(UISplitPane splitPane,BXQueryAreaShell queryAreaShell){
		this.queryAreaShell = queryAreaShell;
		this.splitPane = splitPane;
		initialize();
	}
	
	private UIToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = createToolBar();
			toolBar.setLayout(createLayout4Toolbar(FlowLayout.LEADING));
			toolBar.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
		}
		return toolBar;
	}
	
	private LayoutManager createLayout4Toolbar(int align) {
		FlowLayout flowLayout = new FlowLayout(align);
		flowLayout.setVgap(1);
		flowLayout.setHgap(2);
		return flowLayout;
	}
	
	private UIToolBar createToolBar() {
		UIToolBar toolbar = new UIToolBar();
		toolbar.setFloatable(false);// 不可移动
		toolbar.setOpaque(false);
		return toolbar;
	}
	
	private void initialize(){
		setLayout(new BorderLayout());
		getToolBar().add(new BXMiniminizeAction());
		add(getToolBar(),BorderLayout.CENTER);
	}
	
	final class BXMiniminizeAction extends NCAction{
		
		private BXMiniminizeAction(){
			init();
		}
		
		private void init() {
			putValue(Action.NAME, BXConstans.MINIMINIZE_ACTION_NAME);
			putValue(Action.SMALL_ICON, ThemeResourceCenter.getInstance().getImage("themeres/ui/toolbaricons/right_stretch_highlight.png"));
			putValue(Action.SHORT_DESCRIPTION, BXConstans.MINIMINIZE_ACTION_NAME);
			setCode("maxmiseAction");
		}      

		@Override
		public void doAction(ActionEvent e) throws Exception {
			splitPane.setLeftComponent(queryAreaShell);
			splitPane.setDividerLocation(BXConstans.MAXIMISED_POSITION);
		}
	}
}
