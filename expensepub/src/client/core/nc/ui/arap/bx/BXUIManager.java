package nc.ui.arap.bx;

import nc.ui.glpub.IUiPanel;
import nc.ui.glpub.PanelStack;
import nc.ui.glpub.UiManager;
import nc.ui.pub.FramePanel;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.linkoperate.ILinkAdd;
import nc.ui.pub.linkoperate.ILinkAddData;
import nc.ui.pub.linkoperate.ILinkApprove;
import nc.ui.pub.linkoperate.ILinkApproveData;
import nc.ui.pub.linkoperate.ILinkMaintain;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.pub.linkoperate.ILinkQuery;
import nc.ui.pub.linkoperate.ILinkQueryData;

/**
 * @author twei
 * 
 * nc.ui.arap.bx.BXUIManager
 */
public class BXUIManager extends UiManager implements ILinkApprove, ILinkQuery, ILinkAdd, ILinkMaintain {

	private static final long serialVersionUID = -4994252781142308861L;

	public BXUIManager() {
		super(null);
	}

	@Override
	public boolean onClosing() {
		PanelStack stack = getStack();
		for (Object com : stack) {
			if(com instanceof ToftPanel){
				boolean b = ((ToftPanel)com).onClosing();
				if(!b)
					return false;
			}
		}
		return true;
	}
	
	public BXUIManager(FramePanel panel) {
		super(panel);
		
//		initApproveStatus();
		
	}

	private void initApproveStatus() {
		String isapprove = getParameter("isapprove");
		if(isapprove!=null){
		IUiPanel iPanel = this.getCurrentPanel();
			if (iPanel instanceof BXBillMainPanel) {
				BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
				BXBillMainPanel.getBxParam().setNodeOpenType(isapprove.equals("1")?BxParam.NodeOpenType_LR_PUB_Approve:BxParam.NodeOpenType_Default);
			}
		}
	}
		

	public void initData(nc.vo.pub.msg.MessageVO msgvo) {
		IUiPanel iPanel = this.getCurrentPanel();
		if (iPanel instanceof BXBillMainPanel) {
			BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
			BXBillMainPanel.setNodeCode(this.getModuleCode());
			BXBillMainPanel.postInit();
		}
	}
	public void doApproveAction(ILinkApproveData approvedata) {
		IUiPanel iPanel = this.getCurrentPanel();
		if (iPanel instanceof BXBillMainPanel) {
			BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
			BXBillMainPanel.doApproveAction(approvedata);
		}
	}
	public void doQueryAction(ILinkQueryData querydata) {
		IUiPanel iPanel = this.getCurrentPanel();
		if (iPanel instanceof BXBillMainPanel) {
			BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
			BXBillMainPanel.doQueryAction(querydata);
		}
	}
	public void doAddAction(ILinkAddData adddata) {
		IUiPanel iPanel = this.getCurrentPanel();
		if (iPanel instanceof BXBillMainPanel) {
			BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
			BXBillMainPanel.doAddAction(adddata);
		}
	}

	public void doMaintainAction(ILinkMaintainData maintaindata) {
		IUiPanel iPanel = this.getCurrentPanel();
		if (iPanel instanceof BXBillMainPanel) {
			BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
			BXBillMainPanel.doMaintainAction(maintaindata);
		}
	}
	public void postInit() {
		IUiPanel iPanel = this.getCurrentPanel();
		if (iPanel instanceof BXBillMainPanel) {
			BXBillMainPanel BXBillMainPanel = (BXBillMainPanel) iPanel;
			BXBillMainPanel.setNodeCode(this.getModuleCode());
			BXBillMainPanel.postInit();
		}
	}
}
