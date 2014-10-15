package nc.ui.arap.bx.tools.checkboxtree;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import nc.vo.logging.Debug;

/**
 * PK��ģ�ʹ�����
 * @author sunhy
 *
 */
public class PkTreeModelCreator {
	HashMap id_obj_map = new HashMap();

	HashMap obj_pid_map = new HashMap();

	public DefaultTreeModel createTree(Object[] objs, ITreeNodeProvider provider) {
		return createTree(objs, provider, true);
	}

	/**
	 * ���ݶ������鹹����
	 * @author    sunhy
	 * @param objs �������������ݵ�objs
	 * @param provider �ڵ㴦����
	 * @param bIgnoreLostParent �Ƿ���Ե�û�и��ڵ�Ľڵ�
	 * @return DefaultTreeModel
	 * @since	   �����V60�汾���˷�������ӽ�����
	 */
	public DefaultTreeModel createTree(Object[] objs,
			ITreeNodeProvider provider, boolean bIgnoreLostParent) {
		if (objs != null && objs.length != 0) {
			int size = objs.length;
			DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[size];
			for (int i = 0; i < size; i++) {
				Object obj = objs[i];
				DefaultMutableTreeNode treeNode = provider
						.createTreeNode(obj);
				nodes[i] = treeNode;
				Object nodeId = provider.getNodeId(obj);
				Object parentId = provider.getParentNodeId(obj);
				id_obj_map.put(nodeId, nodes[i]);
				obj_pid_map.put(nodes[i], parentId);
			}
			return createPKTree(nodes, provider.getRootNode(),
					bIgnoreLostParent);
		}

		return null;
	}

	private DefaultTreeModel createPKTree(DefaultMutableTreeNode[] treenods,
			DefaultMutableTreeNode root, boolean bIgnoreLostParent) {
		DefaultMutableTreeNode rootnode = root;
		id_obj_map.put("", rootnode);

		for (int i = 0; i < treenods.length; i++) {
			Object parentID = obj_pid_map.get(treenods[i]);
			DefaultMutableTreeNode parentNode = (parentID == null) ? rootnode
					: (DefaultMutableTreeNode) id_obj_map.get(parentID);
			if (parentNode != null) {
				parentNode.add(treenods[i]);
			} else {
				if (bIgnoreLostParent) {
					Debug.debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0036")/*@res "���ڵ�δ�ҵ�,�����д�:"*/+treenods[i]);
				} else {
					root.add(treenods[i]);
				}

			}
		}
		DefaultTreeModel treemodel = new DefaultTreeModel(rootnode);

		return treemodel;
	}
}