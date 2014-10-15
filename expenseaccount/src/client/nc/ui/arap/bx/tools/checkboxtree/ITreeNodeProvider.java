package nc.ui.arap.bx.tools.checkboxtree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * �˽ӿ������Ĺ��������ṩ�ӿڣ�֧��Pk�������ͱ��������������������ޣ�������Object
 * @author sunhy
 */
public interface ITreeNodeProvider {
	/**
	 * ����treeNode,ʵ��������ڴ�����ʵ������ڵ��������Ϣ������CheckTreeNode��showText����
	 * @param obj  ���ڵ��userObject
	 * @return
	 */
	public DefaultMutableTreeNode createTreeNode(Object obj);
	/**
	 * �����Pk��������ʵ�ִ˷��������ش˽ڵ�IDֵ
	 * @param obj �˽ڵ�ֵ
	 * @return
	 */
	public Object getNodeId(Object obj);
	/**
	 * �����PK��������ʵ�ִ˷���,���ش˽ڵ�ĸ��ڵ�PKֵ
	 * @param obj �˽ڵ�ֵ
	 * @return
	 */
	public Object getParentNodeId(Object obj);
	/**
	 * ����Ǳ��������˷������ص�ǰ�ڵ����ֵ
	 * @param obj ��ǰ�ڵ�ֵ
	 * @return
	 */
	public Object getCodeValue(Object obj);
	/**
	 * ����Ǳ�����,�˷������ص�ǰ�ڵ�������
	 * @return
	 */
	public String getCodeRule();
	/**
	 * �����Ƿ��ձ�����������true����������false��pk��
	 * @return
	 */
	public boolean isCodeTree();
	/**
	 * @return  ���ڵ����
	 */
	public DefaultMutableTreeNode getRootNode();
}
