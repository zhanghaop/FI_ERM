package nc.ui.erm.closeacc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.validation.IValidationService;
import nc.pubitf.erm.closeacc.IErmCloseAccService;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.org.CloseAccBookVO;

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
    private IValidationService validationService;

	public UnCloseAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0003")/*@res "取消结账"*/);
		setCode(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0003")/*@res "取消结账"*/);
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
        this.model.addAppEventListener(this);
	}

	public void setValidationService(IValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
	public void doAction(ActionEvent e) throws Exception {

        if (validationService != null)
            validationService.validate(this.model);

		CloseAccBookVO closeAccBookVO = (CloseAccBookVO) getModel()
				.getSelectedData();
		getService().uncloseAcc(closeAccBookVO);
		getDataManager().refresh();
		
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0093")/*@res "取消结账成功！"*/,
                getModel().getContext());
	}

    @Override
    protected boolean isActionEnable() {
        CloseAccBookVO vo = (CloseAccBookVO) getModel().getSelectedData();
        return  vo != null && vo.getIsendacc().booleanValue();
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

}