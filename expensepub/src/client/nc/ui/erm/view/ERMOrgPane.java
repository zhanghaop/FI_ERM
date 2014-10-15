package nc.ui.erm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;

import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;

/**
 * 财务组织面板
 * 1	支持UIState变化，联动编辑状态：新增态，可编辑，其他状态不可编辑
 * 2	支持选中数据变化，自动联动切换主组织值，默认联动pk_org_v的值
 * <b>Date:</b>2012-12-17<br>
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ERMOrgPane extends UIPanel implements AppEventListener  {
	private static final long serialVersionUID = 2261791010382680748L;

	private UIRefPane m_refPane = null;

	private BillManageModel model = null;
	/**
	 * 初始化界面
	 */
	public void initUI() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0018")/*@res "财务组织"*/));
		add(getRefPane());
		getRefPane().setEnabled(false);

		// 添加分隔线
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));

//		//主组织权限过滤
//		filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext().getNodeCode()), getRefPane());
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.SELECTION_CHANGED == event.getType() || AppEventConst.SELECTED_DATE_CHANGED == event.getType()) {
			onSelectionChanged();
		} else if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			if (model.getUiState() == UIState.ADD) {
				getRefPane().setEnabled(true);
			} else {
				getRefPane().setEnabled(false);
			}

			if (model.getUiState() == UIState.ADD || model.getUiState() == UIState.EDIT) {
				//显示必填项红*
				getRefPane().getUITextField().setShowMustInputHint(true);
			} else if(model.getUiState() == UIState.NOT_EDIT){
				//不显示必填项红*
				getRefPane().getUITextField().setShowMustInputHint(false);
			}
		}
	}

	protected void onSelectionChanged() {
		Object selectedData = getModel().getSelectedData();
		Object pk_org = null;
		if (selectedData instanceof SuperVO) {
			pk_org = ((SuperVO)selectedData).getAttributeValue("pk_org_v");
		}else if (selectedData instanceof AggregatedValueObject) {
			pk_org = ((AggregatedValueObject)selectedData).getParentVO().getAttributeValue("pk_org_v");
		}

		setPkOrg(pk_org);
	}

	public void setPkOrg(Object pk_org){
		String pk = getRefPane().getRefModel().getPkValue();
		if(pk != null && pk.equals(pk_org)){
			return;
		}
		getRefPane().setPK(pk_org);
		getRefPane().setRefValue();
	}

	/**
	 * 过滤主组织权限
	 *
	 * @param pkOrgs
	 * @param refPane
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void filtOrgs(String[] pkOrgs, UIRefPane refPane) {
		if (pkOrgs == null) {
			// 没有分配主组织权限情况
			pkOrgs = new String[0];
		}
        // refPane.getRefModel().setFilterPks(pkOrgs);
        ErUiUtil.setRefFilterPks(refPane.getRefModel(), pkOrgs);
		refPane.getRefModel().setAddEnableStateWherePart(true);
	}

	public UIRefPane getRefPane() {
		if (m_refPane == null) {
			m_refPane = new UIRefPane();
			m_refPane.setPreferredSize(new Dimension(200, 20));
            m_refPane.setRefNodeName("财务组织版本"/* -=notranslate=- */);
            m_refPane.getRefModel().setDisabledDataShow(false);
			m_refPane.setButtonFireEvent(true);
		}
		return m_refPane;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = (BillManageModel) model;
		this.model.addAppEventListener(this);
	}

}