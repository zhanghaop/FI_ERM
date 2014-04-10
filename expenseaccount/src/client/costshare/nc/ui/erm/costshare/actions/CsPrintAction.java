package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.UIState;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
/**
 * @author luolch
 *
 * ¥Ú”°
 * 
 */
@SuppressWarnings({ "serial" })
public class CsPrintAction extends nc.ui.uif2.actions.PrintAction {

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null&&model.getUiState()== UIState.NOT_EDIT;
	}

	@Override
	public void doAction(ActionEvent e) {
		super.doAction(e);
		printInfo();
	}
	
	private IDataSource dataSource;

	private void printInfo() {
		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI(), getDataSource());
		AggCostShareVO vo = (AggCostShareVO) getModel().getSelectedData();
		String pk_tradetypecode = (String) vo.getParentVO().getAttributeValue(CostShareVO.PK_TRADETYPE);
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(getModel().getContext().getPk_group(), getModel().getContext().getNodeCode(), pkUser, null,
				pk_tradetypecode);
		entry.selectTemplate();
		entry.preview();

	}
	
	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}
}