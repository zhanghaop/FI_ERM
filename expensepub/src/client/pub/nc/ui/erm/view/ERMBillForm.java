package nc.ui.erm.view;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.bs.uif2.IActionCode;
import nc.funcnode.ui.action.INCAction;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.UITabbedPaneUI;
import nc.ui.pub.bill.action.BillTableLineAction;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.bill.BillTabVO;

import org.apache.commons.lang.ArrayUtils;

public class ERMBillForm extends BillForm implements ITabbedPaneAwareComponent, IAutoShowUpComponent {

	private static final long serialVersionUID = 1L;

	// ��ҳǩʱ���Ƿ���ʾҳǩ����Ĭ��Ϊtrue��
	private boolean tabSingleShow = true;

	private TabbedPaneAwareCompnonetDelegate tabbedPaneAwareComponent = null;
	private AutoShowUpEventSource autoShowComponent = null;
	private IFunNodeClosingListener closingListener = null;

	private boolean showOrgPanel = true;
	private ERMOrgPane billOrgPanel;
	
	/**
	 * ���尴ť�������Ӧλ�õ�ӳ�� <br>
	 * 
	 * @see nc.ui.pub.bill.BillScrollPane,
	 *      nc.ui.pub.bill.action.BillTableLineAction,
	 *      nc.ui.pub.bill.tableaction.BillTableActionManager
	 */
	public static final Map<String, Integer> BODY_CODE_ACTION_MAP;
	static {
		BODY_CODE_ACTION_MAP = new HashMap<String, Integer>();
		// �����а�ť
		BODY_CODE_ACTION_MAP.put(IActionCode.ADDLINE, BillTableLineAction.ADDLINE);
		// �����а�ť
		BODY_CODE_ACTION_MAP.put(IActionCode.INSLINE, BillTableLineAction.INSERTLINE);
		// ɾ���а�ť
		BODY_CODE_ACTION_MAP.put(IActionCode.DELLINE, BillTableLineAction.DELLINE);

		// ###### ��ʱû�еı��尴ť #######
		// �����а�ť
		BODY_CODE_ACTION_MAP.put(IActionCode.COPYLINE, BillTableLineAction.COPYLINE);
		// ճ���а�ť
		BODY_CODE_ACTION_MAP.put(IActionCode.PASTELINE, BillTableLineAction.PASTELINE);
		// ճ���е�β����ť
		BODY_CODE_ACTION_MAP.put(IActionCode.PASTELINETOTAIL, BillTableLineAction.PASTELINETOTAIL);
		// #############
	}

	public ERMBillForm() {
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
		autoShowComponent = new AutoShowUpEventSource(this);
	}

	public void initUI() {
		super.initUI();
		// ��ҳǩʱ���Ƿ���ʾҳǩ����Ĭ��Ϊtrue��
		billCardPanel.getBodyTabbedPane().setUI(new UITabbedPaneUI(tabSingleShow));

		if (isShowOrgPanel()) {
			add(getBillOrgPanel(), BorderLayout.NORTH);
			// ����֯��壬��billform����Ҫ�ٻ�ȡ����
			setRequestFocus(false);
		}
		
		processPopupMenu();
	}

	public boolean isShowOrgPanel() {
		return showOrgPanel;
	}

	public ERMOrgPane getBillOrgPanel() {
		if (null == billOrgPanel && isShowOrgPanel()) {
			billOrgPanel = new ERMOrgPane();
			billOrgPanel.setModel(getModel());
			billOrgPanel.initUI();
		}
		return billOrgPanel;
	}

	@Override
	protected void onEdit() {
		showMeUp();
		super.onEdit();
	}

	@Override
	protected void onAdd() {
		showMeUp();
		super.onAdd();
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SHOW_EDITOR.equals(event.getType())) {
			showMeUp();
		}
	}

	/**
	 * �����Ҽ��˵�
	 * 
	 */
	protected void processPopupMenu() {
		// ��ñ�������ҳǩ
		BillTabVO[] tabVos = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		// ���ע��ı���action
		Map<String, List<Action>> m_btnMap = getBodyActionMap();

		if (!ArrayUtils.isEmpty(tabVos)) {
			for (BillTabVO tabVo : tabVos) {
				String tabCode = tabVo.getTabcode();
				BillScrollPane bodyScrollPane = getBillCardPanel().getBodyPanel(tabCode);

				/**
				 * ���ԭ��action
				 */
				bodyScrollPane.clearDefalutEditAction();
				bodyScrollPane.clearEditAction();
				bodyScrollPane.clearNotEditAction();

				/**
				 * ��ע���action��ʾ�ڽ�����
				 */
				if (m_btnMap != null) {
					List<Action> actions = m_btnMap.get(tabCode);
					if (!(actions == null || actions.isEmpty())) {
						// �����޸���Ҫ��Ϊ�������Ҽ�������ť�Լ��б༭�Ի����ж�Ӧ�İ�ť���������У�ɾ���еȣ�
						for (Action action : actions) {
							// ��ť����
							String actionCode = (String) action.getValue(INCAction.CODE);

							// Ĭ�ϱ༭��ť����
							if (BODY_CODE_ACTION_MAP.containsKey(actionCode)) {
								if (BODY_CODE_ACTION_MAP.get(actionCode) != null) {
									bodyScrollPane.replaceDefaultAction(BODY_CODE_ACTION_MAP.get(actionCode), action);
								} else {
									bodyScrollPane.addEditAction(action);
								}
							}
							// �̶���ť����������ʱû�����ð�ť�ǹ̶���ť�����Ǳ༭��ť�������ã���˲���Ĭ�ϱ༭��ť���İ�ť������ʱ�����̶���ť������
							else {
								bodyScrollPane.addFixAction(action);
							}
						}
					}
				}
			}
		}
	}
	
	public boolean isTabSingleShow() {
		return tabSingleShow;
	}

	public void setTabSingleShow(boolean tabSingleShow) {
		this.tabSingleShow = tabSingleShow;
	}

	@Override
	public void addTabbedPaneAwareComponentListener(ITabbedPaneAwareComponentListener l) {
		this.tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
	}

	@Override
	public boolean canBeHidden() {
		boolean isHidden = true;
		if (closingListener != null) {
			isHidden = closingListener.canBeClosed();
		}
		return isHidden;
	}

	@Override
	public boolean isComponentVisible() {
		return this.tabbedPaneAwareComponent.isComponentVisible();
	}

	@Override
	public void setComponentVisible(boolean visible) {
		this.tabbedPaneAwareComponent.setComponentVisible(visible);

	}

	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		// ע�������
		this.autoShowComponent.setAutoShowUpEventListener(l);
	}

	@Override
	public void showMeUp() {
		this.autoShowComponent.showMeUp();
	}

	public AutoShowUpEventSource getAutoShowComponent() {
		return this.autoShowComponent;
	}

	public void setAutoShowComponent(AutoShowUpEventSource autoShowComponent) {
		this.autoShowComponent = autoShowComponent;
	}

	public IFunNodeClosingListener getClosingListener() {
		return closingListener;
	}

	public void setClosingListener(IFunNodeClosingListener closingListener) {
		this.closingListener = closingListener;
	}

}
