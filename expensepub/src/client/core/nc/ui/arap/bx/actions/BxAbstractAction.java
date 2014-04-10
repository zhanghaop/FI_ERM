package nc.ui.arap.bx.actions;

import java.awt.CardLayout;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.engine.IAction;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.ToftPanel;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;

@SuppressWarnings({ "restriction", "deprecation" })
public class BxAbstractAction implements IAction {

	public String KEY_DELERRO_INFO = "KEY_DELERRO_INFO";

	
	public String getPk_org(){
//modified by chendya@ufida.com.cn		
		BXBillMainPanel panel=(BXBillMainPanel) getParent();
		if(panel!=null&&panel.getCurrWorkPage()==BillWorkPageConst.LISTPAGE){
			//列表界面取选中行的组织
			int rowIndex = panel.getBillListPanel().getHeadTable().getSelectedRow();
			return (String)panel.getBillListPanel().getHeadBillModel().getValueAt(rowIndex, JKBXHeaderVO.PK_ORG);
		}else if(panel!=null&&panel.getCurrWorkPage()==BillWorkPageConst.CARDPAGE){
			//卡片界面取表头组织
			return (String)panel.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		}else{
			//还取不到，取默认的业务单元
			return BXUiUtil.getBXDefaultOrgUnit();
		}
//--end		
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

	
}
