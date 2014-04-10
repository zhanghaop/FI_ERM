package nc.ui.erm.matterapp.actions;

import nc.ui.erm.matterapp.listener.MatterQueryActionListener;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.uif2.actions.QueryAction;

/**
 * 事项审批单查询按钮
 * @author chenshuaia
 *
 */
public class MaQueryAction extends QueryAction {
	private static final long serialVersionUID = 1L;

	private boolean isFirstCall = true;

	private MatterQueryActionListener queryEventListener;

	@Override
	protected IQueryConditionDLG getQueryCoinditionDLG() {
		IQueryConditionDLG queryConditionDLG = super.getQueryCoinditionDLG();
		if (isFirstCall) {
			queryConditionDLG
					.registerCriteriaEditorListener(getQueryEventListener());
			isFirstCall = false;
		}

		return queryConditionDLG;
	}

	public MatterQueryActionListener getQueryEventListener() {
		return queryEventListener;
	}

	public void setQueryEventListener(
			MatterQueryActionListener queryEventListener) {
		this.queryEventListener = queryEventListener;
	}
}
