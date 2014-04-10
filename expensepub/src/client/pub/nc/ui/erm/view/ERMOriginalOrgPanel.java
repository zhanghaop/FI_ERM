package nc.ui.erm.view;

import java.awt.Dimension;

import nc.ui.pub.beans.UIRefPane;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;

/**
 * 财务组织面板（区分财务组织版本面板ERMOrgPane） 
 *1 支持UIState变化，联动编辑状态：新增态，可编辑，其他状态不可编辑
 * 2支持选中数据变化，自动联动切换主组织值，默认联动pk_org的值
 * 
 * @author shengqy
 * 
 */
public class ERMOriginalOrgPanel extends ERMOrgPane {

	private static final long serialVersionUID = 1L;
	
	private UIRefPane m_refPane = null;
	
	protected void onSelectionChanged() {
		Object selectedData = getModel().getSelectedData();
		Object pk_org = null;
		if (selectedData instanceof SuperVO) {
			pk_org = ((SuperVO) selectedData).getAttributeValue("pk_org");
		} else if (selectedData instanceof AggregatedValueObject) {
			pk_org = ((AggregatedValueObject) selectedData).getParentVO().getAttributeValue("pk_org");
		}

		setPkOrg(pk_org);
	}
	
	public UIRefPane getRefPane() {
		if (m_refPane == null) {
			m_refPane = new UIRefPane();
			m_refPane.setPreferredSize(new Dimension(200, 20));
            m_refPane.setRefNodeName("财务组织"/* -=notranslate=- */);
            m_refPane.getRefModel().setDisabledDataShow(false);
			m_refPane.setButtonFireEvent(true);
		}
		return m_refPane;
	}
}
