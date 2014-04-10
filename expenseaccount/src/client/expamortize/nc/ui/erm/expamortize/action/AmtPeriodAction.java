package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoManage;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.erm.expamortize.view.AmtPeriodDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UITextField;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IBillListPanelView;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
/**
 *
 * @author wangled
 *
 */
public class AmtPeriodAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private IQueryAndRefreshManager dataManager = null;
	private IBillListPanelView view = null;

	public IQueryAndRefreshManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IQueryAndRefreshManager dataManager) {
		this.dataManager = dataManager;
	}

	public AmtPeriodAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0069")/*@res "修改摊销期间"*/);
		setCode("AmtPeriod");
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
	
	@Override
    protected boolean isActionEnable()
    {
	    //当没有数据，或者选择的数据剩余摊销期等于0时，是不可修改摊销期的
        ExpamtinfoVO expamtinfoVo= (ExpamtinfoVO) getModel().getSelectedData();
        if(expamtinfoVo==null){
            return false;
        }
	    return !expamtinfoVo.getRes_period().equals(Integer.valueOf(0));
    }

    @Override
	public void doAction(ActionEvent e) throws Exception {
		UIDialog amtPeriodDialog = getAmtPeriodDialog();
		if (amtPeriodDialog.showModal() == UIDialog.ID_OK) {
			// 修改摊销的信息后，做下面处理
			try{
				UITextField field2 = ((AmtPeriodDialog) amtPeriodDialog)
						.getField2();
				if(field2.getValue()==null){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0139")/*@res "请输入修改后的摊销期"*/);
				}
				int period2 = Integer.parseInt(field2.getValue().toString());
				if(period2<=0){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0129")/*@res "摊销期间应大于0"*/);
				}
				ExpamtinfoVO vo = (ExpamtinfoVO) getModel().getSelectedData();
				if(vo==null){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0068")/*@res "没有选择摊销信息"*/);
				}
				String currAccPeriod=((ExpamorizeManageModel)getModel()).getPeriod();
				ExpamtinfoVO expamtinfoVO = NCLocator.getInstance().lookup(
						IExpAmortizeinfoManage.class).updatePeriod(period2, vo,currAccPeriod);
				getModel().directlyUpdate(expamtinfoVO);
			}catch(NumberFormatException ex){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0128")/*@res "摊销期间不正确"*/);
			}
		}
	}

	private UIDialog getAmtPeriodDialog() {
		return new AmtPeriodDialog(model.getContext(), dataManager, getModel());
	}

	public IBillListPanelView getView() {
		return view;
	}

	public void setView(IBillListPanelView view) {
		this.view = view;
	}

}