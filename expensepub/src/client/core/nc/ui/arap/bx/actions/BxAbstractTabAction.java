package nc.ui.arap.bx.actions;

import java.awt.CardLayout;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.engine.IAction;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.actions.AbstractBodyTableExtendAction;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;


public class BxAbstractTabAction extends AbstractBodyTableExtendAction implements IAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public BxAbstractTabAction() {
 		super();
	}

	public String KEY_DELERRO_INFO = "KEY_DELERRO_INFO";

	
	public String getPk_org(){
		try{
			BXBillMainPanel panel=(BXBillMainPanel) getParent();
			BillCardPanel billCardPanel = panel.getBillCardPanel();
			Object valueObject = billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
			return valueObject==null?null:valueObject.toString();
		}catch (Exception e) {
		}
		
		return BXUiUtil.getBXDefaultOrgUnit();
	}

	private IActionRuntime actionRunntimeV0 = null;

	public void setActionRunntimeV0(IActionRuntime runtime) {
		actionRunntimeV0 = runtime;
	}

	protected IActionRuntime getActionRunntimeV0() {
		return actionRunntimeV0;
	}

	protected CardLayout getLayout() throws BusinessException {
		return (CardLayout) getActionRunntimeV0().invokeMethod("getLayout");
	}

	protected ToftPanel getParent() {
		return (ToftPanel) getActionRunntimeV0();
	}

	@Override
	public void doAction() {

	}

}
