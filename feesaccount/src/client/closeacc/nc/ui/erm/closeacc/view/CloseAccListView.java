package nc.ui.erm.closeacc.view;

import nc.ui.uif2.editor.BillListView;
/**
 * 
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class CloseAccListView extends BillListView{
    private CloseaccDesInfoPanel rightView;

    
	public CloseAccListView(){
		super();
	}
	
	public void initUI() {
		super.initUI();
		//getBillListPanel().setMultiSelect(true);
		//getBillListPanel().getHeadBillModel().addRowStateChangeEventListener(new HeadRowStateListener());
		
	}
	
	/**
	 * 支持全选全消的操作：全选和全消时，要将信息清空
	 * @author wangled
	 *
	 */
//	private class HeadRowStateListener implements IBillModelRowStateChangeEventListener {
//		@Override
//		public void valueChanged(RowStateChangeEvent event) {
//			if (isAllRowSelected()) {//全选和全消时
//			}
//		}
//	}
	
	@Override
	protected void handleSelectionChanged() {
		super.handleSelectionChanged();
		//可结账提示信息在选中表中某一行时显示
		Object selectedOperaDatas = getModel().getSelectedData();
		if(selectedOperaDatas==null){
			return;
		}
	}
	
    public CloseaccDesInfoPanel getRightView() {
        return rightView;
    }

    public void setRightView(CloseaccDesInfoPanel rightView) {
        this.rightView = rightView;
    }
    
//	private boolean isAllRowSelected(){
//		int rowCount = getBillListPanel().getHeadBillModel().getRowCount();
//		boolean isAllSelected= true;
//		for(int row = 0 ; row < rowCount ; row++){
//			if(getBillListPanel().getHeadBillModel().getRowAttribute(row).getRowState() == BillModel.UNSTATE){
//				isAllSelected = false;
//				break;
//			}
//		}
//		return isAllSelected;
//		
//	}

}
