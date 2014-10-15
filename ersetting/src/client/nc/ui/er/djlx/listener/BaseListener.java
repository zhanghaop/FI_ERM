package nc.ui.er.djlx.listener;

import nc.ui.er.component.ExTreeNode;
import nc.ui.er.plugin.MainFrameInfoGetter;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

public class BaseListener extends MainFrameInfoGetter{
	protected boolean isSelectedDetail(BillTypeVO vo){
		if(vo==null || vo.getParentVO()==null || ((DjLXVO)vo.getParentVO()).getDjlxbm()==null ){
			return false;
		}
		return true;
	}
	protected boolean isSysButton(BillTypeVO vo){
		if(isSysCorp()){//集团
			return isDefaultBillType(vo);
		}
		if(vo==null || vo.getParentVO()==null 
				||((DjLXVO)vo.getParentVO()).getDjlxbm()==null ){
			if(! isDefaultBtn(((DjLXVO)vo.getParentVO()).getDjlxbm())){
				return false;
			}
			
		}
		return true;
	}
	private boolean isDefaultBillType(BillTypeVO vo) {
		if(vo==null || vo.getParentVO()==null 
				||((DjLXVO)vo.getParentVO()).getDjlxbm()==null 
				|| ((DjLXVO)vo.getParentVO()).getDjlxbm().startsWith("D") ){
			return true;
		}else{
			return false;
		}
	}
	private boolean isDefaultBtn(String sCode){
		
		return false;

	}
	/**设置卡片编辑状态控件使用状况*/
	public void setCardEnable(boolean enable) throws BusinessException {
//		如果用户选定的系统初始的单据类型，则不可以封存
		BillTypeVO billtype = (BillTypeVO)getCurrData();
	    if (isSysCorp()) {//集团
	    	if(isSysButton(billtype)){
	    		getBillCardPanel().getHeadItem("fcbz").setEnabled(false);
	    	}else{
	    		getBillCardPanel().getHeadItem("fcbz").setEnabled(true);
	    	}
	    } else {
	    	
	    	if(billtype==null || isDefaultBillType(billtype) || isDefaultBtn(billtype.getDjlxbm())){
	    		getBillCardPanel().getHeadItem("fcbz").setEnabled(false);
	    	}else{
	    		getBillCardPanel().getHeadItem("fcbz").setEnabled(true);
	    	}
	    }
	    
	    //首先从选定的单据类型中找出其单据编码
	    ExTreeNode node = (ExTreeNode)getUITree().getLastSelectedPathComponent();

	    if (node != null) {}
	  
	}
}
