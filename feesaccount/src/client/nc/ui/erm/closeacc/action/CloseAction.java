package nc.ui.erm.closeacc.action;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.closeacc.IErmCloseAccService;
import nc.ui.erm.closeacc.view.CloseAccPanel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.org.CloseAccBookVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;

/**
 * 批量处理结账的信息
 * @author wangled
 *
 */
public class CloseAction extends NCAction {

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillListView listView;
	private IQueryAndRefreshManager dataManager;
	private CloseAccPanel toppane;


	public CloseAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0000")/*@res "结账"*/);
		//setCode(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0000")/*@res "结账"*/);
		setCode("EndAcc");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		List<CloseAccBookVO> closeAccBookVO =new ArrayList<CloseAccBookVO>();
		List<CloseAccBookVO> data = getModel().getData();
		Map<String,Integer> orgByRow = new  LinkedHashMap<String,Integer>();
		if(data != null && data.size() != 0){
			for(int i=0; i<data.size(); i++){
				if(listView.getBillListPanel().getHeadBillModel().getRowAttribute(i).getRowState() == BillModel.SELECTED){
					CloseAccBookVO SelectedVOs = (CloseAccBookVO) data.get(i);
					closeAccBookVO.add(SelectedVOs);
					orgByRow.put(SelectedVOs.getPk_org(), i);
				}
			}
		}
		
		if(closeAccBookVO.size()==0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0", "0201109-0105")/**
					 * @res"请选择需要结账的记录！"*/);
		}
		
		List<ValueObjWithErrLog> batchErrorLogList=new ArrayList<ValueObjWithErrLog>();
		//将校验通过的关帐信息出入后台
		ValueObjWithErrLog[] batchcloseAcc = getService().batchcloseAcc(closeAccBookVO.toArray(new CloseAccBookVO[0]));
		
		List<CloseAccBookVO> resultCloseAcc= new ArrayList<CloseAccBookVO>();
		
		
		if(batchcloseAcc !=null && batchcloseAcc.length!=0){
			batchErrorLogList.addAll(Arrays.asList(batchcloseAcc));
		}
		
		//将错误的行的组织，字体加粗
		List<String> errorg= new ArrayList<String>();
		for(ValueObjWithErrLog errLog :batchcloseAcc){
			if(batchcloseAcc!= null){				
				if(errLog.getVos().length!=0){
					CloseAccBookVO[] vos = Arrays.asList(errLog.getVos()).toArray(new CloseAccBookVO[0]);
					resultCloseAcc.add(vos[0]);//正确的结果
				}
			}
			if(errLog.getErrLogList()!=null && errLog.getErrLogList().size()!=0){
				errorg.add(errLog.getErrLogList().get(0).getErrOrg());
			}
		}
		
		//处理最后的展现数据：避免使用directUpdate方法，因为没有datapks
		List<CloseAccBookVO> showCloseAcc= new ArrayList<CloseAccBookVO>();
		List<String> reOrgs= new ArrayList<String>();
		for(CloseAccBookVO revo : resultCloseAcc){
			reOrgs.add(revo.getCloseorgpks());
		}
		for(CloseAccBookVO showVO : data){
			if( reOrgs.size()==0 ||( reOrgs.size()!=0 && !reOrgs.contains(showVO.getCloseorgpks()))){
				showCloseAcc.add(showVO);
			}
		}
		if(reOrgs.size()!=0){
			showCloseAcc.addAll(resultCloseAcc);
		}
		
		Collections.sort(showCloseAcc, new AccBookVOComparator());
		
		getModel().initModel(showCloseAcc.toArray( new CloseAccBookVO [0]));
		
		
		//显示错误行的字体变化
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
				if(integer!=null){
					listView.getBillListPanel().getHeadBillModel().setFont(new Font("",Font.BOLD,12), integer, i);
					

				}
			}
		}
		//设置select状态
		for (Entry<String, Integer> orgRow:orgByRow.entrySet()){
			Integer i = orgRow.getValue();
			listView.getBillListPanel().getHeadBillModel().getRowAttribute(i).setRowState(BillModel.SELECTED);
		}
		
		//处理消息提示
		ErUiUtil.showBatchBookAccResults(getModel().getContext(), batchErrorLogList.toArray(new ValueObjWithErrLog[0]),"EndAcc");
	}
	
	private class AccBookVOComparator implements Comparator<CloseAccBookVO>{
		@Override
		public int compare(CloseAccBookVO o1, CloseAccBookVO o2) {
			//同一会计期间各财务组织按编码排序
			try {
				OrgVO code1 = CacheUtil.getVOByPk(OrgVO.class, o1.getPk_org());
				OrgVO code2 = CacheUtil.getVOByPk(OrgVO.class, o2.getPk_org());
				return code1.getCode().compareTo(code2.getCode());
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			return 0;
		}
    }

    @Override
    protected boolean isActionEnable() {
        String selectdItemValue = (String) ((CloseAccPanel)getToppane()).getUIComboBox().getSelectdItemValue();
        return  "wjz".equals(selectdItemValue);
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

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
        this.model.addAppEventListener(this);
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