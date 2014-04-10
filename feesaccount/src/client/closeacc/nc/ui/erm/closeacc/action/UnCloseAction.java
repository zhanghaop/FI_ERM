package nc.ui.erm.closeacc.action;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.closeacc.IErmCloseAccService;
import nc.ui.erm.closeacc.view.CloseAccPanel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 *
 * @author wangled
 *
 */
public class UnCloseAction extends NCAction {

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillListView listView;
	private IQueryAndRefreshManager dataManager;
	private CloseAccPanel toppane;


	public UnCloseAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0003")/*@res "取消结账"*/);
		//setCode(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0003")/*@res "取消结账"*/);
		setCode("UnEndAcc");
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
        this.model.addAppEventListener(this);
	}


	/**
	 * 批量处理结账的信息
	 * wangle
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		
		List<CloseAccBookVO> uncloseAccBookVO = new ArrayList<CloseAccBookVO>();
		List<CloseAccBookVO> data = getModel().getData();
		Map<String,Integer> orgByRow = new  LinkedHashMap<String,Integer>();
		if (data != null && data.size() != 0) {
			for (int i = 0; i < data.size(); i++) {
				if (listView.getBillListPanel().getHeadBillModel()
						.getRowAttribute(i).getRowState() == BillModel.SELECTED) {
					CloseAccBookVO SelectedVOs = (CloseAccBookVO) data.get(i);
					uncloseAccBookVO.add(SelectedVOs);
					orgByRow.put(SelectedVOs.getPk_org(), i);
				}
			}
		}
		if(uncloseAccBookVO.size()==0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0", "0201109-0106")/**
			 * @res"请选择需要取消结账的记录！"*/);
		}
		
		List<ValueObjWithErrLog> batchErrorLogList=new ArrayList<ValueObjWithErrLog>();
		

		ValueObjWithErrLog[] batchuncloseAcc = getService().batchuncloseAcc(uncloseAccBookVO.toArray(new CloseAccBookVO[0]));

		if(batchuncloseAcc !=null && batchuncloseAcc.length!=0){
			batchErrorLogList.addAll(Arrays.asList(batchuncloseAcc));
		}
		
		//将错误的行的组织，字体加粗
		List<String> errorg= new ArrayList<String>();
		List<CloseAccBookVO> resultUnCloseAcc= new ArrayList<CloseAccBookVO>();
		for(ValueObjWithErrLog errLog :batchuncloseAcc){
			if(errLog!= null){				
				if(errLog.getVos().length!=0){
					CloseAccBookVO[] vos = Arrays.asList(errLog.getVos()).toArray(new CloseAccBookVO[0]);
					resultUnCloseAcc.add(vos[0]);
				}
			}
			if(errLog.getErrLogList()!=null && errLog.getErrLogList().size()!=0){
				errorg.add(errLog.getErrLogList().get(0).getErrOrg());
			}
		}
		
		int columnCount = listView.getBillListPanel().getHeadBillModel().getColumnCount();
		int rowCount =  listView.getBillListPanel().getHeadBillModel().getRowCount();
		for(int i= 0 ;i<rowCount ; i++){
			for (int j= 0 ;j<columnCount ; j++){
				listView.getBillListPanel().getHeadBillModel().setForeground(null,i, j);
				listView.getBillListPanel().getHeadBillModel().setFont(null,i, j);
			}
		}

		for(int i=0 ; i<columnCount ;i++){
			for (String org :errorg){
				Integer integer = orgByRow.get(org);
				//listView.getBillListPanel().getHeadBillModel().setForeground(Color.RED, integer, i);
				listView.getBillListPanel().getHeadBillModel().setFont(new Font("",Font.BOLD,12), integer, i);
			}
		}
		
		getModel().directlyUpdate(resultUnCloseAcc.toArray(new SuperVO[resultUnCloseAcc.size()]));
		


		//消息提示
		ErUiUtil.showBatchBookAccResults(getModel().getContext(), batchErrorLogList.toArray(new ValueObjWithErrLog[0]),"UnEndAcc");
	}


    @Override
    protected boolean isActionEnable() {
        String selectdItemValue = (String) ((CloseAccPanel)getToppane()).getUIComboBox().getSelectdItemValue();
        return  !"wjz".equals(selectdItemValue);
    }
    
	public IErmCloseAccService getService() {
		return NCLocator.getInstance().lookup(IErmCloseAccService.class);
	}

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	public IQueryAndRefreshManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IQueryAndRefreshManager dataManager) {
		this.dataManager = dataManager;
	}
	
	public CloseAccPanel getToppane() {
		return toppane;
	}

	public void setToppane(CloseAccPanel toppane) {
		this.toppane = toppane;
	}


}