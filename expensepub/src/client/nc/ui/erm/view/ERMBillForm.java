package nc.ui.erm.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;

import nc.bs.uif2.IActionCode;
import nc.funcnode.ui.action.INCAction;
import nc.ui.erm.model.ERMBillManageModel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.UITabbedPaneUI;
import nc.ui.pub.bill.action.BillTableLineAction;
import nc.ui.pubapp.uif2app.event.card.CardPanelLoadEvent;
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
import nc.vo.pub.bill.BillTempletVO;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("restriction")
public class ERMBillForm extends BillForm implements ITabbedPaneAwareComponent, IAutoShowUpComponent {

	private static final long serialVersionUID = 1L;

	// 单页签时，是否显示页签名。默认为true。
	private boolean tabSingleShow = true;

	private TabbedPaneAwareCompnonetDelegate tabbedPaneAwareComponent = null;
	private AutoShowUpEventSource autoShowComponent = null;
	private IFunNodeClosingListener closingListener = null;

	private boolean showOrgPanel = true;
	private ERMOrgPane billOrgPanel;
	private CodebarBillFormPanel codebarPanel;
	/**
	 * 表体按钮编码与对应位置的映射 <br>
	 * 
	 * @see nc.ui.pub.bill.BillScrollPane,
	 *      nc.ui.pub.bill.action.BillTableLineAction,
	 *      nc.ui.pub.bill.tableaction.BillTableActionManager
	 */
	public static final Map<String, Integer> BODY_CODE_ACTION_MAP;
	static {
		BODY_CODE_ACTION_MAP = new HashMap<String, Integer>();
		// 新增行按钮
		BODY_CODE_ACTION_MAP.put(IActionCode.ADDLINE, BillTableLineAction.ADDLINE);
		// 插入行按钮
		BODY_CODE_ACTION_MAP.put(IActionCode.INSLINE, BillTableLineAction.INSERTLINE);
		// 删除行按钮
		BODY_CODE_ACTION_MAP.put(IActionCode.DELLINE, BillTableLineAction.DELLINE);

		// ###### 暂时没有的表体按钮 #######
		// 复制行按钮
		BODY_CODE_ACTION_MAP.put(IActionCode.COPYLINE, BillTableLineAction.COPYLINE);
		// 粘贴行按钮
		BODY_CODE_ACTION_MAP.put(IActionCode.PASTELINE, BillTableLineAction.PASTELINE);
		// 粘贴行到尾部按钮
		BODY_CODE_ACTION_MAP.put(IActionCode.PASTELINETOTAIL, BillTableLineAction.PASTELINETOTAIL);
		// #############
	}

	
	/**
	 * 卡片事件处理扩展处理类
	 */
	private ErmCardPanelEventTransformer eventTransformer;
	
	public ERMBillForm() {
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
		autoShowComponent = new AutoShowUpEventSource(this);
	}

	public void initUI() {
		super.initUI();
		// 单页签时，是否显示页签名。默认为true。
		billCardPanel.getBodyTabbedPane().setUI(new UITabbedPaneUI(tabSingleShow));

		if (isShowOrgPanel()) {
			add(getBillOrgPanel(), BorderLayout.NORTH);
			// 有组织面板，则billform不需要再获取焦点
			setRequestFocus(false);
		}
		if(getCodebarPanel()!=null)//如果有快捷码  则显示为快捷码区域
			add(getCodebarBillPanel(), BorderLayout.NORTH);
		processPopupMenu();
		
		// 参考pubapp处理扩展事件，为了不影响原产品实现，处理扩展事件的model使用ERMBillManageModel的getAppModelExDelegate
		// 派发扩展事件使用model的方法fireExtEvent
		ERMBillManageModel ermmodel = (ERMBillManageModel) this.getModel();
		eventTransformer = new ErmCardPanelEventTransformer(this.getBillCardPanel(), ermmodel);
//		eventTransformer.setOrgBillEditListener(this);
//		eventTransformer.setBillTabbedPaneTabChangeListener(tabChangeHandler);

		// 派发界面初始化事件
		CardPanelLoadEvent e = new CardPanelLoadEvent(this.getBillCardPanel());
		ermmodel.fireExtEvent(e);
	}
	
	public UIPanel getCodebarBillPanel() {
		UIPanel billPanel = new UIPanel();
		billPanel.setPreferredSize(new Dimension(500, 30));
		billPanel.setLayout(new BorderLayout());
		if (isShowOrgPanel()) {
			getBillOrgPanel().setBorder(null);
			billPanel.add(getBillOrgPanel(),BorderLayout.WEST);
			// 有组织面板，则billform不需要再获取焦点
			setRequestFocus(false);
		}
		billPanel.add(getCodebarPanel(),BorderLayout.EAST);
		billPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
		return billPanel;
	}
	
	public void setCodebarPanel(CodebarBillFormPanel codebarPanel) {
		this.codebarPanel = codebarPanel;
	}

	public CodebarBillFormPanel getCodebarPanel(){
		return codebarPanel;
	}

	public void setBillData(BillTempletVO template) {
		
		processTemplateVO(template);
		BillData billdata = new BillData(template, getBillStatus());
		processErmBillData(billdata);
		if(getUserdefitemPreparator() != null)
			getUserdefitemPreparator().prepareBillData(billdata);
		processBillData(billdata);
		billCardPanel.setBillData(billdata);
	}
	
	/**
	 * 扩展BillData
	 * 
	 * @param data
	 */
	protected void processErmBillData(BillData data) {}
	
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
		
		 if (AppEventConst.UISTATE_CHANGED == event.getType()) {
		    eventTransformer.getOldValueMap().clear();
		}
	}

	/**
	 * 处理右键菜单
	 * 
	 */
	protected void processPopupMenu() {
		// 获得表体所有页签
		BillTabVO[] tabVos = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		// 获得注册的表体action
		Map<String, List<Action>> m_btnMap = getBodyActionMap();

		if (!ArrayUtils.isEmpty(tabVos)) {
			for (BillTabVO tabVo : tabVos) {
				String tabCode = tabVo.getTabcode();
				BillScrollPane bodyScrollPane = getBillCardPanel().getBodyPanel(tabCode);

				/**
				 * 清空原有action
				 */
				bodyScrollPane.clearDefalutEditAction();
				bodyScrollPane.clearEditAction();
				bodyScrollPane.clearNotEditAction();

				/**
				 * 将注册的action显示在界面上
				 */
				if (m_btnMap != null) {
					List<Action> actions = m_btnMap.get(tabCode);
					if (!(actions == null || actions.isEmpty())) {
						// 这样修改主要是为了适配右键弹出按钮以及行编辑对话框中对应的按钮（如新增行，删除行等）
						for (Action action : actions) {
							// 按钮编码
							String actionCode = (String) action.getValue(INCAction.CODE);

							// 默认编辑按钮区域
							if (BODY_CODE_ACTION_MAP.containsKey(actionCode)) {
								if (BODY_CODE_ACTION_MAP.get(actionCode) != null) {
									bodyScrollPane.replaceDefaultAction(BODY_CODE_ACTION_MAP.get(actionCode), action);
								} else {
									bodyScrollPane.addEditAction(action);
								}
							}
							// 固定按钮区（由于暂时没有设置按钮是固定按钮区还是编辑按钮区的设置，因此不是默认编辑按钮区的按钮现在暂时都按固定按钮来处理）
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
	
	/**
	 * 获取功能权限组织
	 * @return
	 */
	protected String[] getFuncPermissionPkorgs() {
		return this.getModel().getContext().getFuncInfo().getFuncPermissionPkorgs();
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
		// 注册监听器
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

	public ErmCardPanelEventTransformer getEventTransformer() {
		return eventTransformer;
	}

}

