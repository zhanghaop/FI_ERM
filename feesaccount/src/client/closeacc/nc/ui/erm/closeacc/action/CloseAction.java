package nc.ui.erm.closeacc.action;

import java.awt.event.ActionEvent;

import nc.bs.bd.service.ValueObjWithErrLog;
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
public class CloseAction extends NCAction {

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillListView listView;
	private IQueryAndRefreshManager dataManager;
    private IValidationService validationService;

	public CloseAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0000")/*@res "结账"*/);
		setCode(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0000")/*@res "结账"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

        if (validationService != null)
            validationService.validate(getModel());

        CloseAccBookVO closeAccBookVO = (CloseAccBookVO) getModel()
                .getSelectedData();

		ValueObjWithErrLog valueObj = getService().closeAcc(closeAccBookVO);
		getDataManager().refresh();

		//检查但不控制时的消息提示
		if (valueObj.getErrLogList() != null) {
		    String errorMsg =
		        (String)valueObj.getErrLogList().get(0).getErrReason();
		    ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0002")/*"结账成功！"*/, errorMsg, getModel().getContext());
		}
		else
		    ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0002")/*@res "结账成功！"*/,
                    getModel().getContext());
	}

    @Override
    protected boolean isActionEnable() {
        CloseAccBookVO closeAccVos = (CloseAccBookVO)getModel().getSelectedData();
        return closeAccVos!=null && !closeAccVos.getIsendacc().booleanValue();
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

    public void setValidationService(IValidationService validationService) {
        this.validationService = validationService;
    }

}