package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nc.ui.arap.bx.actions.QueryAction;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.queryarea.QueryArea;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.actions.ComponentMiniAction;
import nc.ui.uif2.components.IMiniminizedEventListener;
import nc.ui.uif2.components.IMinimizableComponent;
import nc.ui.uif2.components.MinimizedEvent;
import nc.ui.uif2.components.miniext.IMinimizableComponentExt;
import nc.ui.uif2.components.miniext.MiniIconAction;
import nc.vo.arap.bx.util.BXConstans;

/**
 * 借款/报销单据 包装了查询方案的组件
 *
 * @author chendya
 *
 */
@SuppressWarnings("serial")
public class BXQueryAreaShell extends JPanel implements
		IMinimizableComponentExt {

	private static final String ZB_SZXMID = "zb.szxmid";

	private static final String SZXMID = "szxmid";

	BXBillMainPanel mainPanel = null;

	private QueryArea queryArea = null;

	private IMinimizableComponent delegator = null;

	private List<MiniIconAction> miniIconActions = null;

	public BXQueryAreaShell(BXBillMainPanel mainPanel ,QueryArea queryArea) {
		this.queryArea = queryArea;
		this.mainPanel = mainPanel;
		initialize();
		initListener();
	}

	private void initialize() {
		if (getQueryArea() == null)
			throw new IllegalStateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0040")/*@res "查询方案属性必须设置！"*/);
		setLayout(new BorderLayout());
		add(getQueryArea(), BorderLayout.CENTER);
		setMiniAction4QueryArea();
	}

	private void initListener() {
		delegator = new MiniminizedEventSource(this);
		delegator.setMiniminizedEventListener(new BXMiniminizedEventAdapter());

		queryArea.setQueryExecutor(new IQueryExecutor() {

			@Override
			public void doQuery(IQueryScheme queryScheme) {
				QueryAction action = new QueryAction();
				action.setActionRunntimeV0(mainPanel);
				try {
					String whereSQLOnly = queryScheme.getWhereSQLOnly();
					if(whereSQLOnly!=null){
						whereSQLOnly=whereSQLOnly.replaceAll(SZXMID, ZB_SZXMID);
					}
					action.doQuickQuery(whereSQLOnly);
				} catch (Exception e) {
					mainPanel.handleException(e);
				}
			}
		});
	}

	public QueryArea getQueryArea() {
		return queryArea;
	}

	public void setQueryArea(QueryArea queryArea) {
		this.queryArea = queryArea;
	}

	private void setMiniAction4QueryArea() {
		ComponentMiniAction miniAction = new ComponentMiniAction();
		miniAction.setComponent(this);
		getQueryArea().setMiniAction(miniAction);
	}

	public boolean isMiniminized() {
		return delegator.isMiniminized();
	}

	public void miniminized() {
		delegator.miniminized();
	}

	public void setMiniminized(boolean isMini) {
		delegator.setMiniminized(isMini);
	}

	public void setMiniminizedEventListener(IMiniminizedEventListener listener) {
		delegator.setMiniminizedEventListener(listener);
	}

	@Override
	public List<MiniIconAction> getMiniIconActions() {
		return this.miniIconActions;
	}

	public void setMiniIconActions(List<MiniIconAction> actions) {
		this.miniIconActions = actions;
	}

	final class MiniminizedEventSource implements IMinimizableComponent {

		private JComponent hostComponent = null;

		private IMiniminizedEventListener l = null;

		private boolean isMini = false;

		public static final String ISMINI_PROPERTY = "isMini";

		public MiniminizedEventSource(JComponent hostComponent) {
			this.hostComponent = hostComponent;
		}

		@Override
		public void miniminized() {
			if (l != null)
				l.miniminized(new MinimizedEvent(this.hostComponent));
		}

		@Override
		public void setMiniminizedEventListener(
				IMiniminizedEventListener listener) {
			if (listener != null)
				l = listener;
		}

		@Override
		public void setMiniminized(boolean isMini) {

			boolean oldValue = isMiniminized();
			this.isMini = isMini;

			if (oldValue != isMiniminized())
				this.hostComponent.firePropertyChange(ISMINI_PROPERTY,
						oldValue, isMini);
		}

		public boolean isMiniminized() {
			return this.isMini;
		}
	}

	final class BXMiniminizedEventAdapter implements IMiniminizedEventListener {

		@Override
		public void miniminized(MinimizedEvent event) {
			final BXQueryAreaShell src = (BXQueryAreaShell)event.getSourceComponent();
			if (!(src instanceof IMinimizableComponent))
				return;
			UISplitPane splitPane = ((UISplitPane)src.getParent());
			splitPane.setDividerLocation(BXConstans.MINIMINIZED_POSITION);
		}
	}
}