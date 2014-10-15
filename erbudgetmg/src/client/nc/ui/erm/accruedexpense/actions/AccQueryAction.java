package nc.ui.erm.accruedexpense.actions;

import nc.ui.erm.accruedexpense.listener.AccQueryCriteriaChangedListener;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.uif2.actions.QueryAction;


public class AccQueryAction extends QueryAction {

	private static final long serialVersionUID = 1L;
	
	private AccQueryCriteriaChangedListener queryEventListener;
	
	private boolean isFirstCall = true;

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

	public AccQueryCriteriaChangedListener getQueryEventListener() {
		return queryEventListener;
	}

	public void setQueryEventListener(AccQueryCriteriaChangedListener queryEventListener) {
		this.queryEventListener = queryEventListener;
	}

	

}
