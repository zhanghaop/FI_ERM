package nc.ui.erm.view;

import java.awt.Dimension;

import nc.ui.pub.beans.UIRefPane;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;

/**
 * ������֯��壨���ֲ�����֯�汾���ERMOrgPane�� 
 *1 ֧��UIState�仯�������༭״̬������̬���ɱ༭������״̬���ɱ༭
 * 2֧��ѡ�����ݱ仯���Զ������л�����ֵ֯��Ĭ������pk_org��ֵ
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
            m_refPane.setRefNodeName("������֯"/* -=notranslate=- */);
            m_refPane.getRefModel().setDisabledDataShow(false);
			m_refPane.setButtonFireEvent(true);
		}
		return m_refPane;
	}
}
