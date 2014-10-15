package nc.ui.erm.expamortize.view;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import nc.ui.pub.beans.UIPanel;
@SuppressWarnings("serial")
public class ExpamortizePanel extends UIPanel{
	
	public void  initUI(){
		this.add(getToporgpane());
		this.add(getTopperiodpane());
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	private JPanel toporgpane;
	private JPanel topperiodpane;
	public JPanel getToporgpane() {
		return toporgpane;
	}
	public void setToporgpane(JPanel toporgpane) {
		this.toporgpane = toporgpane;
	}
	public JPanel getTopperiodpane() {
		return topperiodpane;
	}
	public void setTopperiodpane(JPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}
}
