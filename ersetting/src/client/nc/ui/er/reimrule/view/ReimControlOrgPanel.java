package nc.ui.er.reimrule.view;

import java.awt.Dimension;

import javax.swing.event.EventListenerList;

import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.uif2.LoginContext;

public class ReimControlOrgPanel extends BDOrgPanel {

	private static final long serialVersionUID = 1L;
	private EventListenerList listeners = new EventListenerList();
	private UIRefPane refPane = null;
	
	@Override
	public void initUI() {
		super.initUI();
		getModel().removeAppEventListener(this);
		if (ErUiUtil.getBXDefaultOrgUnit() != null) {
			getRefPane().setPK(ErUiUtil.getBXDefaultOrgUnit());
			getModel().getContext().setPk_org(ErUiUtil.getBXDefaultOrgUnit());
		}
		else{
			getRefPane().setPK(ReimRulerVO.PKORG);
			getModel().getContext().setPk_org(ReimRulerVO.PKORG);
		}
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		try {
			String pk_org = getRefPane().getRefPK();
			if(pk_org==null)
				getModel().getContext().setPk_org(ReimRulerVO.PKORG);
			else
				getModel().getContext().setPk_org(pk_org);
			getDataManager().initModel();
		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
//		fireEvent(new AppEvent(AppEventConst.UISTATE_CHANGED, this, null));
	}

	public void addAppEventListener(AppEventListener l) {
		listeners.remove(AppEventListener.class, l);
		listeners.add(AppEventListener.class, l);
	}

	public void fireEvent(AppEvent event) {
		AppEventListener[] ls = listeners.getListeners(AppEventListener.class);
		for (AppEventListener listener : ls) {
			listener.handleEvent(event);
		}
	}

    @Override
    public UIRefPane getRefPane() {
        if (refPane == null) {
            refPane = new UIRefPane();
            refPane.setPreferredSize(new Dimension(200, 20));
            LoginContext context = getModel().getContext();
            AbstractRefModel refModel = new FinanceOrgDefaultRefTreeModel(); 
            refModel.setPk_group(context.getPk_group());
            refModel.setFilterPks(context.getPkorgs());
            refPane.setRefModel(refModel);
            refPane.addValueChangedListener(this);
        }
        return refPane;
    }

    @Override
    protected void initDefaultOrg() {
        if (getModel().getContext().getPk_org() != null) {
            getRefPane().setPK(getModel().getContext().getPk_org());
        }
    }
}
