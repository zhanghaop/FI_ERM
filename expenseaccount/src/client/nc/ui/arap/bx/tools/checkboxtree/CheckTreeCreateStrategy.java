package nc.ui.arap.bx.tools.checkboxtree;

import nc.vo.bd.meta.BDObjectTreeCreateStrategy;

/**
 * ��CheckBox���� �������
 * @author    sunhy
 * @version	   ����޸����� 2010-4-2
 * @see	   BDObjectTreeCreateStrategy
 * @since	�Ӳ�Ʒ��V60�汾�����౻��ӽ�����
 */ 
public class CheckTreeCreateStrategy extends BDObjectTreeCreateStrategy {

	/** ���ڵ����� */
	private String rootName = null;

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	@Override
	public CheckTreeNode getRootNode() {
		return new CheckTreeNode(getRootName());
	}

}
