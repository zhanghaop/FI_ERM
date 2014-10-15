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
 *֧����С�������
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
		//�������֧����С������
		ToolBarButton btn = new ToolBarButton(getMiniAction());
		UIPanel panel =new UIPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(btn);
		panel.add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0013")/*@res "��ע"*/));
		//��������е�����
		this.setLayout(new ColumnLayout(ColumnLayout.TOP, 1, 1, false, false));
		this.add(panel);
		UITextArea expComp = new UITextArea();
		expComp.setPreferredSize((new Dimension(500,150)));
		this.add(expComp);
		expComp.setText(getExp());
		expComp.setEnabled(false);
	}
	
	//��С����Action��ͼ��
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
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0014")/*@res "(1)�ڳ��رպ���ܽ���\n(2)��ֹ���´�̯����ȫ��̯������ܽ���\n(3)�Ѿ����˵��ڼ䲻���ٽ����κ�ҵ����"*/;
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