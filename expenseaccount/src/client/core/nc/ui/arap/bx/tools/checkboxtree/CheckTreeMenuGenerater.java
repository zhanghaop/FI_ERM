package nc.ui.arap.bx.tools.checkboxtree;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.ButtonGroup;

import nc.ui.pub.beans.UIPopupMenu;
import nc.ui.pub.beans.UIRadioButtonMenuItem;

/**
 * 带选择框的树的右键菜单生成器 
 * @author sunhy
 */
public class CheckTreeMenuGenerater implements ItemListener{
	
	CheckBoxTree tree = null;
	UIPopupMenu popupMenu = null;
	ButtonGroup buttonGroup = null;
	HashMap<String,INodeCheckMode> mapCheckMode = new HashMap<String,INodeCheckMode>();
	
    public void itemStateChanged(ItemEvent e){
    	UIRadioButtonMenuItem item = (UIRadioButtonMenuItem)e.getSource();
    	INodeCheckMode checkMode = mapCheckMode.get(item.getName());
    	tree.setCheckMode(checkMode);
    }

	public CheckTreeMenuGenerater(CheckBoxTree tree) {
		this.tree = tree;
		this.popupMenu = new UIPopupMenu();
		this.buttonGroup = new ButtonGroup();
	}
	
	public void addMenuCheckItem(String sName,String sText,INodeCheckMode checkMode){
		mapCheckMode.put(sName, checkMode);
		UIRadioButtonMenuItem menuItem = new UIRadioButtonMenuItem(sText);
		menuItem.setName(sName);
		menuItem.addItemListener(this);
		this.buttonGroup.add(menuItem);
		this.popupMenu.add(menuItem);
	}
	
	public UIPopupMenu getPopupMenu() {
		return popupMenu;
	}
}
