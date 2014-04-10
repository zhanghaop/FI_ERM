package nc.ui.arap.bx.tools.checkboxtree;

import nc.vo.bd.meta.BDObjectTreeCreateStrategy;

/**
 * 带CheckBox树的 建造策略
 * @author    sunhy
 * @version	   最后修改日期 2010-4-2
 * @see	   BDObjectTreeCreateStrategy
 * @since	从产品的V60版本，此类被添加进来。
 */ 
public class CheckTreeCreateStrategy extends BDObjectTreeCreateStrategy {

	/** 根节点名称 */
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
