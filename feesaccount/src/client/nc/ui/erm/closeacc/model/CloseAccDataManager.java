package nc.ui.erm.closeacc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pubitf.erm.closeacc.IErmCloseAccBookQryService;
import nc.ui.erm.closeacc.view.CloseAccFinOrgPanel;
import nc.ui.erm.closeacc.view.CloseAccListView;
import nc.ui.erm.closeacc.view.CloseAccPanel;
import nc.ui.erm.closeacc.view.CloseaccDesInfoPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 *
 * @author wangled
 *
 */
public class CloseAccDataManager implements IQueryAndRefreshManager,
        IAppModelDataManager {
    private IExceptionHandler exceptionHandler;
    private CloseAccManageModel model;
    private CloseaccDesInfoPanel rightView;
	private JPanel toporgpane;
	private JPanel topperiodpane;
	private JPanel toppane;


	private CloseAccListView listView;


    @Override
    public void initModelBySqlWhere(String sqlWhere) {

    }

    @Override
    public void refresh() {
    	initModel();
    }

    /**
     * 修改查询方式：查询出各财务组织对应会计期间的结账信息，可以多选组织
     * wangled
     */
	@Override
    public void initModel() {
    	String[] orgPks = ((CloseAccFinOrgPanel)getToporgpane()).getRefPane().getRefPKs();
    	if (orgPks!=null && orgPks.length!=0) {
    		String selectdItemValue = (String) ((CloseAccPanel)getToppane()).getUIComboBox().getSelectdItemValue();
    		Map<String, List<String>> maxMinDesinfo = getMaxMinDesinfo(orgPks);
    		//得到所有组织的可结账信息
    		Map<String,List<String>> periodOrgMap=new HashMap<String,List<String>>();//会计月作为key
			try {
				periodOrgMap = NCLocator.getInstance().lookup(
						IErmCloseAccBookQryService.class).queryPeriodAndOrg(
						maxMinDesinfo, selectdItemValue);
				
				List<CloseAccBookVO> batchvos = new ArrayList<CloseAccBookVO>();
				
				batchvos = NCLocator.getInstance().lookup(
						IErmCloseAccBookQryService.class).queryCloseAccBook(periodOrgMap);
				
				getModel().initModel(batchvos.toArray(new CloseAccBookVO[0]));

				// 清除选择的状态
				int rowCount = getListView().getBillListPanel().getHeadBillModel().getRowCount();
				for (int i = 0; i < rowCount; i++) {
					getListView().getBillListPanel().getHeadBillModel().getRowAttribute(i).setRowState(BillModel.UNSTATE);
				}
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);	
			}
    	}
    }
	
    
    private Map<String, List<String>> getMaxMinDesinfo(String[] pk_org) {
        Map<String, List<String>> maxMinMap;
        try {
            maxMinMap = getErmCloseAccBookQryService().getMaxEndedAndMinNotEndedAccByOrg( pk_org );
        } catch (BusinessException e) {
            Logger.error(e.getMessage(), e);
            return null;
        }
        return maxMinMap;
    }
    
    private IErmCloseAccBookQryService getErmCloseAccBookQryService() {
        return NCLocator.getInstance().lookup(IErmCloseAccBookQryService.class);
    }
    
    public IErmCloseAccBookQryService getService() {
        return NCLocator.getInstance().lookup(IErmCloseAccBookQryService.class);
    }

	public JPanel getToporgpane() {
		return toporgpane;
	}
	public void setToporgpane(JPanel toporgpane) {
		this.toporgpane = toporgpane;
	}

    public CloseaccDesInfoPanel getRightView() {
        return rightView;
    }

    public void setRightView(CloseaccDesInfoPanel rightView) {
        this.rightView = rightView;
    }

    public IExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(IExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public CloseAccManageModel getModel() {
        return model;
    }

    public void setModel(CloseAccManageModel model) {
        this.model = model;
    }

	public JPanel getTopperiodpane() {
		return topperiodpane;
	}

	public void setTopperiodpane(JPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}

	public JPanel getToppane() {
		return toppane;
	}

	public void setToppane(JPanel toppane) {
		this.toppane = toppane;
	}
	
	public CloseAccListView getListView() {
		return listView;
	}

	public void setListView(CloseAccListView listView) {
		this.listView = listView;
	}
	
	
    
    

}