package nc.ui.arap.bx.tools.checkboxtree;

/** 
 * ѡ�У�ȡ��ѡ�У��ϼ��ڵ㣬�������¼��ڵ�ȫ��ѡ�У�ȡ��ѡ�У���
 * ѡ���¼��ڵ㣬��ýڵ���������Ƚڵ��Զ�ѡ�У�
 * ȡ��ѡ���¼��ڵ㣬����ýڵ���ֵܽڵ㶼û��ѡ�У���ýڵ�ĸ��ڵ�Ҳȡ��ѡ�У��������Ƹýڵ�����Ƚڵ㡣  
 * 
 * @author sunhy
 */
public class DigInOutStandardMode extends DigInAndToRootMode {
	public void onNodeUnChecked(CheckTreeNode node) {
		super.onNodeUnChecked(node);
		setParentNodeSelected(node);		
	}
	protected void setParentNodeSelected(CheckTreeNode node){
	    //ȡ��ѡ�нڵ㣬����ýڵ���ֵܽڵ㶼û��ѡ�У���ýڵ�ĸ��ڵ�Ҳȡ��ѡ�У�
	    //�������Ƹýڵ�����Ƚڵ㡣 
	    CheckTreeNode nodeParent = (CheckTreeNode)node.getParent();
	    if (nodeParent == null){
	        return;
	    }
	    boolean bChecked = false;
	    if (nodeParent.getChildCount()>1){//���ֻ��һ���ӽڵ㣬�����ֱ������ȡ��ѡ��
	        for(int i=0;i<nodeParent.getChildCount();i++){
	            CheckTreeNode nodeChild = (CheckTreeNode)nodeParent.getChildAt(i);
	            if (nodeChild.isChecked()){//�����
	                bChecked = true;
	                break;//ֱ���˳�
	            }
	        }
	    }
	    if (!bChecked){
	        setNodeUnChecked(nodeParent);
	        setParentNodeSelected(nodeParent);	
	    }
	    
	}
}
