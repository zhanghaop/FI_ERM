package nc.ui.arap.bx.tools.checkboxtree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;

/**
 * �� ��Ⱦ��
 * @author    sunhy
 * @version	   ����޸����� 2010-4-6
 * @see	   TreeCellRenderer
 * @since	�Ӳ�Ʒ��V60�汾�����౻��ӽ�����
 */ 
public class CheckTreeRenderer extends JPanel implements TreeCellRenderer {
	
	private static final long serialVersionUID = 1L;
	private Color selColor = UIManager.getColor("List.selectionBackground");
	protected JCheckBox box = new JCheckBox();

	public CheckTreeRenderer() {
		this.setOpaque(true);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		CheckTreeNode node = (CheckTreeNode) value;

		if (selected) {
			this.setBackground(selColor);
			box.setBackground(selColor);
		} else {
			this.setBackground(tree.getBackground());
			box.setBackground(tree.getBackground());
		}
		box.setSelected(node.isChecked());
		box.setText(node.toString());
		return box;
	}
	
	private String null2Empty(Object o)
	{
		return o==null?"":o+"";
	}
}