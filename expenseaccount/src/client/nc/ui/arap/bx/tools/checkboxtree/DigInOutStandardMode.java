package nc.ui.arap.bx.tools.checkboxtree;

/** 
 * 选中（取消选中）上级节点，则所有下级节点全部选中（取消选中）；
 * 选中下级节点，则该节点的所有祖先节点自动选中；
 * 取消选中下级节点，如果该节点的兄弟节点都没有选中，则该节点的父节点也取消选中，依此类推该节点的祖先节点。  
 * 
 * @author sunhy
 */
public class DigInOutStandardMode extends DigInAndToRootMode {
	public void onNodeUnChecked(CheckTreeNode node) {
		super.onNodeUnChecked(node);
		setParentNodeSelected(node);		
	}
	protected void setParentNodeSelected(CheckTreeNode node){
	    //取消选中节点，如果该节点的兄弟节点都没有选中，则该节点的父节点也取消选中，
	    //依此类推该节点的祖先节点。 
	    CheckTreeNode nodeParent = (CheckTreeNode)node.getParent();
	    if (nodeParent == null){
	        return;
	    }
	    boolean bChecked = false;
	    if (nodeParent.getChildCount()>1){//如果只有一个子节点，则可以直接设置取消选中
	        for(int i=0;i<nodeParent.getChildCount();i++){
	            CheckTreeNode nodeChild = (CheckTreeNode)nodeParent.getChildAt(i);
	            if (nodeChild.isChecked()){//如果有
	                bChecked = true;
	                break;//直接退出
	            }
	        }
	    }
	    if (!bChecked){
	        setNodeUnChecked(nodeParent);
	        setParentNodeSelected(nodeParent);	
	    }
	    
	}
}
