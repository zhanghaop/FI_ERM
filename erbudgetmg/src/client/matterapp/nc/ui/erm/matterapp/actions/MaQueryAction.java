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
			queryConditionDLG.registerCriteriaEditorListener(getQueryEventListener());
//			if(getModel().getContext().getNodeCode().equals(ErmMatterAppConst.MAPP_NODECODE_QY)){//平台提供的参数、方法均没有用
//				queryConditionDLG.registerDataPowerEnableJudger(new IDataPowerEnableJudger() {
//					@Override
//					public boolean isDataPowerEnabled(FilterMeta fm, boolean deafaultValue) {
//						return false;
//					}
//				});
//			}
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
