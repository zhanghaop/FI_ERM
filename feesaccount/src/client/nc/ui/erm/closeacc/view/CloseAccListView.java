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
	 * ֧��ȫѡȫ���Ĳ�����ȫѡ��ȫ��ʱ��Ҫ����Ϣ���
	 * @author wangled
	 *
	 */
//	private class HeadRowStateListener implements IBillModelRowStateChangeEventListener {
//		@Override
//		public void valueChanged(RowStateChangeEvent event) {
//			if (isAllRowSelected()) {//ȫѡ��ȫ��ʱ
//			}
//		}
//	}
	
	@Override
	protected void handleSelectionChanged() {
		super.handleSelectionChanged();
		//�ɽ�����ʾ��Ϣ��ѡ�б���ĳһ��ʱ��ʾ
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
