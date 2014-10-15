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
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0069")/*@res "�޸�̯���ڼ�"*/);
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
	    //��û�����ݣ�����ѡ�������ʣ��̯���ڵ���0ʱ���ǲ����޸�̯���ڵ�
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
			// �޸�̯������Ϣ�������洦��
			try{
				UITextField field2 = ((AmtPeriodDialog) amtPeriodDialog)
						.getField2();
				if(field2.getValue()==null){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0139")/*@res "�������޸ĺ��̯����"*/);
				}
				int period2 = Integer.parseInt(field2.getValue().toString());
				if(period2<=0){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0129")/*@res "̯���ڼ�Ӧ����0"*/);
				}
				ExpamtinfoVO vo = (ExpamtinfoVO) getModel().getSelectedData();
				if(vo==null){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0068")/*@res "û��ѡ��̯����Ϣ"*/);
				}
				String currAccPeriod=((ExpamorizeManageModel)getModel()).getPeriod();
				ExpamtinfoVO expamtinfoVO = NCLocator.getInstance().lookup(
						IExpAmortizeinfoManage.class).updatePeriod(period2, vo,currAccPeriod);
				getModel().directlyUpdate(expamtinfoVO);
			}catch(NumberFormatException ex){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0128")/*@res "̯���ڼ䲻��ȷ"*/);
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