package nc.ui.er.reimrule;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.GroupAction;
import nc.funcnode.ui.action.INCAction;
import nc.funcnode.ui.action.MenuAction;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.BillItemCloneTool;
import nc.ui.arap.bx.ButtonUtil;
import nc.ui.arap.bx.print.FIPrintEntry_BX;
import nc.ui.bill.tools.ColorConstants;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trade.pub.IVOTreeDataByCode;
import nc.ui.trade.pub.TableTreeNode;
import nc.ui.trade.pub.TreeCreateTool;
import nc.ui.trade.pub.VOTreeNode;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.MyDefusedVO;
import nc.vo.ep.bx.ReimRuleDef;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimtype.ReimTypeUtil;
import nc.vo.erm.util.VOUtils;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author twei
 * 
 *         nc.ui.er.reimrule.ReimRuleUI
 */
@SuppressWarnings("deprecation")
public class ReimRuleUI extends ToftPanel implements ValueChangedListener {

	private static final long serialVersionUID = 1L;
	
	private static final String ROOT = "root";
	private static final int STATUS_BROWSE = 0;
	private static final int STATUS_MOD = 1;
	private int pageStatus = STATUS_BROWSE;
	private static final String nodecode = "20110RSS";
	private ReimRuleDefVO ruleDef = null;
	private ExportExcelDialog expDialog = null;
	private UIRefPane ivjtOrg;
	private UIPanel jPanelOrg = null;
	private UILabel jLabel6 = null;

	private final ButtonObject btnMod = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000055")/* @res "修改" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000055")/* @res "修改" */, 5, "Edit"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnAdd = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000187")/* @res "增行" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000187")/* @res "增行" */, 5, "AddLine"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnDel = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000188")/* @res "删行" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000188")/* @res "删行" */, 5, "DelLine"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnSave = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000061")/* @res "保存" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000061")/* @res "保存" */, 5, "Save"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnCancel = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000064")/* @res "取消" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000064")/* @res "取消" */, 5, "Cancel"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnPrintGroup = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "打印" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "打印" */, 5, "PrintGroup"); /*
																		 * -=notranslate
																		 * = -
																		 */

	private final ButtonObject btnPrint = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "打印" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "打印" */, 5, "Print"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnPreview = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000112")/* @res "预览" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000112")/* @res "预览" */, 5, "Preview"); /*
																	 * -=notranslate
																	 * = -
																	 */
	//v63去掉输出功能
	private final ButtonObject btnOutput = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000148")/* @res "输出" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000148")/* @res "输出" */, 5, "Output");
	private final ButtonObject btnCopy = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPTcommon-000318")/* @res "复制" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPTcommon-000318")/* @res "复制" */, 5, "Copy"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnImp = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000483")/* @res "导入" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000483")/* @res "导入" */, 5, "Import"); /*
															 * -=notranslate= -
															 */
	private final ButtonObject btnExp = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000484")/* @res "导出" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000484")/* @res "导出" */, 5, "Export"); /*
															 * -=notranslate= -
															 */

	private final ButtonObject btnUp = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000485")/* @res "上移" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000485")/* @res "上移" */, 5, "Up"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnDown = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000486")/* @res "下移" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000486")/* @res "下移" */, 5, "Down"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnTop = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000487")/* @res "上移到最前" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000487")/* @res "上移到最前" */, 5, "Top"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnBottom = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000488")/* @res "下移到最后" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000488")/* @res "下移到最后" */, 5, "Bottom"); /*
																	 * -=notranslate
																	 * =-
																	 */

	private UISplitPane splitPane;

	private UISplitPane splitPane1;

	private BillCardPanel cardPanel;

	private UIScrollPane treePanel;

	private UITree tree;

	private Map<String, List<SuperVO>> dataMap = new HashMap<String, List<SuperVO>>();

	public Map<String, List<SuperVO>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, List<SuperVO>> dataMap) {
		this.dataMap = dataMap;
	}

	public ReimRuleUI() {
		super();
		initialize();
		btnMod.setEnabled(false);
		btnMod.setEnabled(false);
		btnAdd.setEnabled(false);
		btnAdd.setEnabled(false);
		btnDel.setEnabled(false);
		btnDel.setEnabled(false);
		btnSave.setEnabled(false);
		btnSave.setEnabled(false);
		initData();
		if (ErUiUtil.getBXDefaultOrgUnit() == null) {
			getBillCardPanel().setEnabled(false);
			getBillTypeTree().setEnabled(false);
			getTreePanel().setEnabled(false);
		}
	}

	@Override
	public ButtonObject[] getButtons() {
		btnPrintGroup.addChildButton(btnPrint);
		btnPrintGroup.addChildButton(btnPreview);
		btnPrintGroup.addChildButton(btnOutput);
		return new ButtonObject[] { btnMod, btnAdd, btnDel, btnSave, btnCancel,
				btnCopy, btnImp, btnExp, btnUp, btnDown, btnTop, btnBottom,
				btnPrintGroup };
	}

	@Override
	protected void postInit() {
		super.postInit();
		initActions();
		setPageStatus(3);
		getBillTypeTree().setSelectionInterval(0, 0);
	}

	// 初始化按钮快捷键等
	private void initActions() {
		List<Action> menuActions = getMenuActions();
		for (Action menu : menuActions) {
			if (menu instanceof AbstractNCAction) {
				AbstractNCAction action = (AbstractNCAction) menu;
				ActionInfo info = ActionRegistry
						.getActionInfo(action.getCode());
				if (info != null) {
					action.setCode(info.getCode());
					action
							.putValue(Action.ACCELERATOR_KEY, info
									.getKeyStroke());
					action.putValue(Action.SHORT_DESCRIPTION, info
							.getShort_description());
					action.putValue(Action.SMALL_ICON, info.getIcon());
				}
			}
		}
		// 分组
		createGroupActions(menuActions);
		menuActions = addSeparatorBetweenGroupBtn(menuActions);
		addSeparator4Print(menuActions);
		setMenuActions(menuActions);
	}

	// begin--added by chendya 添加分组

	/**
	 * 打印按钮修改为分割按钮
	 * 
	 * @param list
	 */
	private void addSeparator4Print(List<Action> list) {
		// 记录打印按钮位置
		int pos = 0;
		Action[] children = null;
		for (Iterator<Action> iterator = list.iterator(); iterator.hasNext();) {
			Action action = iterator.next();
			final String code = (String) action.getValue(INCAction.CODE);
			if (code != null && code.equals("PrintGroup")) {
				children = ((MenuAction) action).getAllChild();
				break;
			}
			pos++;
		}

		if (pos > 0 && pos < list.size()) {
			// 移除打印
			list.remove(pos);

			// 重构打印按钮为分割按钮
			GroupAction newPrintAction = new GroupAction();
			(newPrintAction).setActions(Arrays.asList(children));
			list.add(pos, newPrintAction);
		}
	}

	private ButtonObject[] getEditButtons() {
		return new ButtonObject[] { btnAdd, btnDel, btnSave, btnCancel, btnImp,
				btnUp, btnDown, btnTop, btnBottom };
	}

	private ButtonObject[] getBrowseButtons() {
		return new ButtonObject[] { btnMod, btnCopy, btnExp, btnPrintGroup };
	}

	public ButtonObject[] getUEButtons(Integer status) {
		switch (status.intValue()) {
		case 1:
			// 编辑态
			return getEditButtons();
		default:
			// 浏览态
			return getBrowseButtons();
		}
	}

	/**
	 * 按钮组与组之间添加分割Action
	 * 
	 * @author chendya
	 */
	protected List<Action> addSeparatorBetweenGroupBtn(List<Action> list) {
		List<Action> retList = new ArrayList<Action>();
		Action[] actions = list.toArray(new Action[0]);
		for (int i = 0, j = i + 1; i < actions.length; i++, j++) {
			retList.add(actions[i]);
			if (j <= actions.length - 1
					&& actions[i].getValue(BTN_GROUP_NAME) != null
					&& !actions[i].getValue(BTN_GROUP_NAME).equals(
							actions[j].getValue(BTN_GROUP_NAME))) {
				retList.add(new nc.funcnode.ui.action.SeparatorAction());
			}
		}
		return retList;
	}

	/**
	 * 添加分组
	 * 
	 * @author chendya
	 * @param actions
	 */
	private void createGroupActions(List<Action> actions) {
		for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
			Action action = iterator.next();
			if (action.getValue(BTN_GROUP_NAME) == null) {
				appendGroupName(action);
			}
		}
	}

	List<String> BTN_GROUP_ADD_MODIFIY_DEL_CODES = Arrays.asList(new String[] {
			"Edit", "AddLine", "DelLine", "Copy"

	});

	List<String> BTN_GROUP_SAVE_CODES = Arrays.asList(new String[] { "Save"

	});
	List<String> BTN_GROUP_CANCEL_CODES = Arrays.asList(new String[] { "Cancel"

	});

	List<String> BTN_GROUP_PRINT_CODES = Arrays.asList(new String[] { "Print"

	});

	List<String> BTN_GROUP_IMPEXP_CODES = Arrays.asList(new String[] { "Imp",
			"Exp"

	});

	List<String> BTN_GROUP_UP2DOWN_CODES = Arrays.asList(new String[] { "Up",
			"Down", "Top", "Bottom"

	});

	/**
	 * 增/改/删除按钮组
	 */
	static String BTN_GROUP_ADD_MODIFIY_DEL = "BTN_GROUP_ADD_MODIFIY_DEL";

	/**
	 * 刷新按钮组按钮组
	 */
	static String BTN_GROUP_REFRESH = "BTN_GROUP_REFRESH";

	/**
	 * 详细按钮组
	 */
	static String BTN_GROUP_DETAIL = "BTN_GROUP_DETAIL";

	/**
	 * 保存按钮组
	 */
	static String BTN_GROUP_SAVE = "BTN_GROUP_SAVE";

	/**
	 * 取消按钮组
	 */
	static String BTN_GROUP_CANCEL = "BTN_GROUP_CANCEL";

	/**
	 * 打印按钮组
	 */
	static String BTN_GROUP_PRINT = "BTN_GROUP_PRINT";

	/**
	 * 导出按钮组
	 */
	static String BTN_GROUP_IMPEXP = "BTN_GROUP_IMPEXP";

	/**
	 * 导出按钮组
	 */
	static String BTN_GROUP_UP2DOWN = "BTN_GROUP_UP2DOWN";

	static String BTN_GROUP_NAME = "BTN_GROUP_NAME";

	/**
	 * 按钮添加组名
	 * 
	 * @author chendya
	 */
	private void appendGroupName(Action action) {
		// 按钮编码
		final String code = (String) action.getValue(INCAction.CODE);

		// 按钮组名
		final String groupName = BTN_GROUP_NAME;

		// 增/改/删除按钮组
		if (BTN_GROUP_ADD_MODIFIY_DEL_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_ADD_MODIFIY_DEL);
		}

		// 保存按钮组
		else if (BTN_GROUP_SAVE_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_SAVE);
		}

		// 取消按钮组
		else if (BTN_GROUP_CANCEL_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_CANCEL);
		}

		// 打印按钮组
		else if (BTN_GROUP_PRINT_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_PRINT);
		}

		// 导出按钮组
		else if (BTN_GROUP_IMPEXP_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_IMPEXP);
		}

		// 导出按钮组
		else if (BTN_GROUP_UP2DOWN_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_UP2DOWN);
		}
	}

	// --end

	private void initData() {
		IReimTypeService remote = NCLocator.getInstance().lookup(IReimTypeService.class);
		List<ReimRuleVO> vos = new ArrayList<ReimRuleVO>();
		try {
			vos = remote.queryReimRule(null, getPkOrg());
		} catch (BusinessException e) {
			handleException(e);
		}
		setDataMap(VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
	}

	public UISplitPane getSplitPane() {
		if (splitPane == null)
			splitPane = new UISplitPane(JSplitPane.HORIZONTAL_SPLIT);
		return splitPane;
	}

	public UISplitPane getSplitPaneTop() {
		if (splitPane1 == null)
			splitPane1 = new UISplitPane(JSplitPane.VERTICAL_SPLIT);
		return splitPane1;
	}

	private void initialize() {

		try {
			// 添加树
			this.add(getSplitPaneTop());
			getSplitPaneTop().setTopComponent(getUIPanelOrg());
			getSplitPaneTop().setBottomComponent(getSplitPane());
			getSplitPane().setLeftComponent(getTreePanel());
			getSplitPane().setRightComponent(getBillCardPanel());
			getBillCardPanel().setPreferredSize(
					new java.awt.Dimension(298, 469));
			if (ErUiUtil.getBXDefaultOrgUnit() != null) {
				getRefOrg().setPK(ErUiUtil.getBXDefaultOrgUnit());
				getBillCardPanel().setEnabled(true);
				getBillTypeTree().setEnabled(true);
				getTreePanel().setEnabled(true);
				initData();

			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"uifactory", "UPPuifactory-000109")/*
														 * @res "发生异常，界面初始化错误"
														 */);
		}
	}
	
	// 动态显示对象名称，多语问题需处理(费用类型，币种)
	private void showObjName(BillCardPanel panel) {
		
		String namefield = getMultiFieldName("name");
		String[] currtypeFormula = new String[1];
		String[] expensetypeFormula = new String[1];
		
		currtypeFormula[0] = "currtype->getColValue(bd_currtype, " + namefield + ",pk_currtype , pk_currtype)";
		expensetypeFormula[0] = "expensetype->getColValue(er_expensetype, " + namefield + ",pk_expensetype, pk_expensetype)";
		panel.getBillModel().getItemByKey("expensetype").setLoadFormula(currtypeFormula);
		panel.getBillModel().getItemByKey("currtype").setLoadFormula(expensetypeFormula);
		panel.getBillModel().execLoadFormula();
	}
	
	/**
	 * 获取多语字段名称（name2,name,name3）
	 * @param namefield
	 * @return
	 */
	private String getMultiFieldName(String namefield) {
		int intValue = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		if(intValue>1){
			namefield=namefield+intValue;
		}
		return namefield;
	}
	
	/**
	 * 返回某个交易类型的初始数据结构
	 */
	private Map<String,BillData> templateBillDataMap;
	
	public Map<String, BillData> getTemplateBillDataMap() {
		if(templateBillDataMap==null){
			templateBillDataMap = new HashMap<String, BillData>();
		}
		return templateBillDataMap;
	}
	
	public BillData getTemplateInitBillData(final String tradetype) {
		if (getTemplateBillDataMap().get(tradetype) == null) {
			final BillCardPanel billCardPanel = new BillCardPanel();
			billCardPanel.loadTemplet(nodecode, null, ErUiUtil.getPk_user(),ErUiUtil.getBXDefaultOrgUnit());
			getTemplateBillDataMap().put(tradetype, billCardPanel.getBillData());
		}
		return getTemplateBillDataMap().get(tradetype);
	}

	public BillCardPanel getBillCardPanel() {
		if (cardPanel == null) {
			try {
				cardPanel = new BillCardPanel();
				cardPanel.loadTemplet(nodecode, null, ErUiUtil.getPk_user(),
						ErUiUtil.getBXDefaultOrgUnit());
				cardPanel.getBodyPanel().getTable().removeSortListener();
				cardPanel.addEditListener(new BillEditListener() {

					@Override
					public void bodyRowChange(BillEditEvent e) {

					}

					/**
					 * @author liansg
					 * @see 精度处理
					 */
					@Override
					public void afterEdit(BillEditEvent e) {

						String currentBodyTableCode = getBillCardPanel()
								.getCurrentBodyTableCode();
						CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
								.getBillData().getBodyValueVOs(
										currentBodyTableCode,
										ReimRuleVO.class.getName());
						ReimRuleVO[] reimRuleVOs = (ReimRuleVO[]) bodyValueVOs;

						int currencyPrecision = 0;// 币种精度

						for (ReimRuleVO vo : reimRuleVOs) {
							if (e.getKey().equals("currtype")) {
								try {
									currencyPrecision = Currency
											.getCurrDigit(vo.getPk_currtype());
								} catch (Exception e1) {
									ExceptionHandler.consume(e1);
								}

								String amount = "amount";
								getBillCardPanel().getBodyItem(amount).setDecimalDigits(currencyPrecision);
								
								getBillCardPanel().setBodyValueAt(vo.getAmount() == null ? UFDouble.ZERO_DBL: 
									vo.getAmount().setScale(currencyPrecision,UFDouble.ROUND_HALF_UP),
										e.getRow(), amount);
							}
						}
					}
				});

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}

		}

		return cardPanel;
	}

	public UIScrollPane getTreePanel() {
		if (treePanel == null) {
			treePanel = new UIScrollPane();
			treePanel.setAutoscrolls(true);
			treePanel
					.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			treePanel
					.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			treePanel.setMinimumSize(new java.awt.Dimension(3, 3));
			treePanel.setPreferredSize(new java.awt.Dimension(168, 469));
			treePanel.setViewportView(getBillTypeTree());
		}

		return treePanel;
	}

	protected UITree getBillTypeTree() {
		if (tree == null) {
			tree = new UITree();
			tree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setModel(getBillTypeTreeModel());
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					onBillTreeSelect((TableTreeNode) e.getPath()
							.getLastPathComponent());
				}
			});
		}
		return tree;
	}

	private DefaultTreeModel getBillTypeTreeModel() {

		TreeCreateTool treeCreateTool = new TreeCreateTool();

		IVOTreeDataByCode idtree = new IVOTreeDataByCode() {
			public String getCodeFieldName() {
				return "pk_billtypecode";
			}

			public String getCodeRule() {
				return "2";
			}

			public String getShowFieldName() {
				return getMultiFieldName("billtypename");
			}

			public SuperVO[] getTreeVO() {
				HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
				List<BilltypeVO> list = new ArrayList<BilltypeVO>();
				for (BilltypeVO vo : billtypes.values()) {
					if (vo.getSystemcode() != null && vo.getSystemcode().equalsIgnoreCase(BXConstans.ERM_PRODUCT_CODE)) {
						if (vo.getPk_billtypecode().equals(BXConstans.BX_DJLXBM)
								|| vo.getPk_billtypecode().equals(BXConstans.JK_DJLXBM)) {
							continue;
						}
						// 之所以改动这样的方式是 PfDataCache中的setBilltypeVOs
						// 中进行了改动去掉了"global0000..",此处通过当前集团进行过滤
						// if(vo.getPk_group()!=null &&
						// !vo.getPk_group().equalsIgnoreCase(BXConstans.GLOBAL_CODE)){
						if (vo.getPk_group() != null && !vo.getPk_group().equalsIgnoreCase(ErUiUtil.getPK_group())) {
							continue;
						}
						if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype())
								|| BXConstans.JK_DJLXBM.equals(vo.getParentbilltype())) {
							
							if(vo.getAttributeValue(getShowFieldName()) == null){
								//当billtypename多语字段值为空时，手动赋值为~的值（防止自定义交易类型未录制多语时，构建数报空指针）
								vo.setAttributeValue(getShowFieldName(), vo.getBilltypename());
							}
							list.add(vo);
						}
					}
				}
				BilltypeVO[] toArray = list.toArray(new BilltypeVO[] {});
				Arrays.sort(toArray, new Comparator<BilltypeVO>() {
					public int compare(BilltypeVO o1, BilltypeVO o2) {
						return o1.getPk_billtypecode().compareTo(o2.getPk_billtypecode());
					}
				});
				return toArray;
			};

		};
		DefaultTreeModel model = treeCreateTool.createTreeByCode(idtree
				.getTreeVO(), idtree.getCodeFieldName(), idtree.getCodeRule(),
				idtree.getShowFieldName());

		treeCreateTool.modifyRootNodeShowName(nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("2011", "UPP2011-000489")/*
																	 * @res
																	 * "报销管理交易类型"
																	 */);

		return model;

	}

	protected void onBillTreeSelect(TableTreeNode node) {
		refreshTemplate();
		refreshUI();
		setPageStatus(0);
		refreshButtonStatus();
		showObjName(getBillCardPanel());
	}

	protected String[] getDefCode(String djlxbm) {
		String[] codeDefs = new String[2];

		try {

			IArapCommonPrivate query = NCLocator.getInstance().lookup(
					IArapCommonPrivate.class);
			Collection<SuperVO> defusedVOs = query.getVOs(MyDefusedVO.class, "objcode='"
					+ djlxbm + "' or objcode='" + djlxbm + "B'", false);

			if (defusedVOs != null && defusedVOs.size() != 0) {
				for (Iterator<SuperVO> iter = defusedVOs.iterator(); iter.hasNext();) {
					MyDefusedVO vo = (MyDefusedVO) iter.next();
					if (vo.getObjcode().equals(djlxbm)) {
						codeDefs[0] = vo.getObjname();
					}
					if (vo.getObjcode().equals(djlxbm + "B")) {
						codeDefs[1] = vo.getObjname();
					}
				}

			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			showErrorMessage(e.getMessage());
		}
		return codeDefs;
	}

	/**
	 * 注掉原因：只有ImportExcelDialog类中使用到，但此方法并无实际意义。
	 * @param value
	 * @param defCode
	 * @param byName
	 * @return
	 */
//	public String getDefValueByKey(String value, String defCode, boolean byName) {
//		if (value == null || value.toString().length() == 0)
//			return value;
//
//		ReimRuleDefVO ruleDef2 = getRuleDef();
//		List<ReimRuleDef> reimRuleDefList = ruleDef2.getReimRuleDefList();
//
//		for (ReimRuleDef def : reimRuleDefList) {
//			if (def.getItemkey().equals(defCode)) {
//				int datatype = Integer.parseInt(def.getDatatype());
//				if (datatype == IBillItem.UFREF
//						|| datatype == IBillItem.USERDEF) {
//					String metaID = null;
//					if (isUserDef(def)) {
//						// ...
//					} else {
//						// preFix bug here 查询元数据ID
//						// metaID = ...;
//					}
//					if (metaID != null) {
//						IGeneralAccessor accessor = GeneralAccessorFactory
//								.getAccessor(metaID);
//						IBDData doc = null;
//						if (byName) {
//							if (accessor == null) {
//								continue;
//							}
//							try {
//								doc = accessor.getDocByNameWithMainLang(
//										ErUiUtil.getDefaultOrgUnit(ErUiUtil
//												.getPk_user(), ErUiUtil
//												.getPK_group()), def
//												.getDatatype());
//							} catch (Exception e) {
//								nc.bs.logging.Log.getInstance(this.getClass())
//										.error(e.getMessage());
//							}
//						} else
//							try {
//								doc = accessor.getDocByCode(ErUiUtil
//										.getDefaultOrgUnit(ErUiUtil
//												.getPk_user(), ErUiUtil
//												.getPK_group()), def
//										.getDatatype());
//							} catch (Exception e) {
//								nc.bs.logging.Log.getInstance(this.getClass())
//										.error(e.getMessage());
//							}
//						if (doc != null)
//							return doc.getPk();
//					}
//				} else {
//					return value;
//				}
//			}
//		}
//
//		return value;
//	}

	private void refreshTemplate() {
		final String djlxbm = getSelectedNodeCode();
		if (djlxbm.equals(ROOT)){
			return;
		}
		BillData billData = getTemplateInitBillData(djlxbm);
		BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
		//返回所选交易类型配置的自定义字段
		ReimRuleDefVO reimRuleDefvo = ReimTypeUtil.getReimRuleDefvo(djlxbm);
		if (reimRuleDefvo == null){
			//该交易类型没有自定义配置字段
			getBillCardPanel().setBillData(billData);
			setRuleDef(null);
			return;
		}
		if (getRuleDef() != null && reimRuleDefvo.getId().equals(getRuleDef().getId()))
			return;
		setRuleDef(reimRuleDefvo);

		List<BillItem> items = new ArrayList<BillItem>();
		if (bodyItems != null) {
			for (BillItem item : bodyItems) {
				if (item.getKey().startsWith("def")
						|| (item.getIDColName() != null && item.getIDColName()
								.startsWith("def"))) {
					continue;
				}
				items.add(item);
			}
		}
		List<ReimRuleDef> reimRuleDefList = reimRuleDefvo.getReimRuleDefList();
		if (reimRuleDefList != null) {
			for (ReimRuleDef def : reimRuleDefList) {
				int datatype = Integer.parseInt(def.getDatatype());
				if (datatype == IBillItem.UFREF
						|| datatype == IBillItem.USERDEF) {
					BillItem item1 = BillItemCloneTool.clone(bodyItems[1]);
					item1.setNull(true);
					item1.setDataType(datatype);
					item1.setRefType(def.getReftype());
					item1.setKey(def.getItemkey() + "_name");
					item1.setName(def.getShowname());
					item1.setIDColName(def.getItemkey());
					item1.setShowOrder(1);
					item1.setShow(true);
					item1.setM_bNotLeafSelectedEnabled(true);
					item1.setForeground(ColorConstants.COLOR_DEFAULT);
					items.add(item1);

					if (isUserDef(def)) {
						// FIXME
						// try{
						// IDef idef =
						// NCLocator.getInstance().lookup(IDef.class);
						// String docType =
						// def.getItemvalue().startsWith(ReimRuleVO.Reim_jkbxr_key)||def.getItemvalue().startsWith(ReimRuleVO.Reim_receiver_key)?"人员管理档案":"";
						// /*-=notranslate=-*/
						// docType =
						// def.getItemvalue().startsWith(ReimRuleVO.Reim_deptid_key)||def.getItemvalue().startsWith(ReimRuleVO.Reim_fydeptid_key)?"部门档案":docType;
						// /*-=notranslate=-*/
						//
						// if(def.getItemvalue().startsWith(ReimRuleVO.Reim_head_key)
						// ||
						// def.getItemvalue().startsWith(ReimRuleVO.Reim_body_key)){
						// String[] defCode=getDefCode(djlxbm);
						// docType =
						// def.getItemvalue().startsWith(ReimRuleVO.Reim_head_key)?defCode[0]:docType;
						// docType =
						// def.getItemvalue().startsWith(ReimRuleVO.Reim_body_key)?defCode[1]:docType;
						// }
						//
						// DefVO[] defs = idef.queryDefVO(docType,
						// BXUiUtil.getDefaultOrgUnit()); /*-=notranslate=-*/
						// if(defs!=null){
						// for(DefVO defVO:defs){
						// if(defVO==null ||
						// !defVO.getFieldName().equals(def.getItemvalue().substring(def.getItemvalue().indexOf(ReimRuleVO.REMRULE_SPLITER)+1)))
						// continue;
						// if (defVO.getDefdef().getPk_bdinfo() == null)
						// continue;
						//
						// if(datatype==IBillItem.USERDEF){
						// item1.setRefType(defVO.getDefdef().getPk_bdinfo());
						// item1.reCreateComponent();
						// item1.setIsDef(true);
						// }else{
						// BdinfoVO bdinfo =
						// (BdinfoVO)BdinfoManager.getBdInfoVO(defVO.getDefdef().getPk_bdinfo()).clone();
						// String refnodename = bdinfo.getRefnodename();
						// if(refnodename!=null){
						// item1.setRefType(refnodename);
						// }else{
						// item1.setRefType("<"+bdinfo.getSelfrefclass()+">");
						// }
						// }
						//
						// item1.setDataType(IBillItem.UFREF);
						// }
						// }
						// }catch (Exception e) {
						// handleException(e);
						// }
					}

					BillItem item2 = BillItemCloneTool.clone(bodyItems[1]);
					item2.setDataType(IBillItem.STRING);
					item2.setKey(def.getItemkey());
					item2.setName(def.getShowname());
					item2.setShow(false);
					item2.setNull(false);
					items.add(item2);
				} else {
					BillItem item1 = BillItemCloneTool.clone(bodyItems[1]);
					item1.setDataType(datatype);
					item1.setShow(true);
					item1.setNull(false);
					item1.setKey(def.getItemkey());
					item1.setName(def.getShowname());
					item1.setShowOrder(1);
					item1.setRefType(def.getReftype());
					items.add(item1);
				}
			}
		}

		// 设置对应参照的财务组织
		BillItem[] itemArray = items.toArray(new BillItem[] {});
		for (BillItem item : itemArray) {
			if (item.getComponent() instanceof UIRefPane) {
				UIRefPane ref = (UIRefPane) item.getComponent();
				if (ref.getRefModel() != null) {
					ref.getRefModel().setPk_org(getPkOrg());
				}
			}
		}

		billData.setBodyItems(itemArray);
		getBillCardPanel().setBillData(billData);
		
		//缓存起来
		getTemplateBillDataMap().put(djlxbm, billData);
	}

	private boolean isUserDef(ReimRuleDef def) {
		return def.getItemvalue().startsWith(ReimRuleVO.Reim_jkbxr_key)
				|| def.getItemvalue().startsWith(ReimRuleVO.Reim_receiver_key)
				|| def.getItemvalue().startsWith(ReimRuleVO.Reim_deptid_key)
				|| def.getItemvalue().startsWith(ReimRuleVO.Reim_fydeptid_key)
				|| (def.getItemvalue().startsWith(ReimRuleVO.Reim_head_key) && def
						.getItemvalue().indexOf("zyx") != -1)
				|| (def.getItemvalue().startsWith(ReimRuleVO.Reim_body_key) && def
						.getItemvalue().indexOf("defitem") != -1);
	}

	protected void handleException(java.lang.Throwable ex) {
		ExceptionHandler.consume(ex);
		ErUiUtil.showUif2DetailMessage(this, "", ex);
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				"UPP2011-000490")/* @res "报销标准设置" */;
	}

	@Override
	public void onButtonClicked(ButtonObject bo) {
		try {
			Object nodeId = ((TableTreeNode) getBillTypeTree().getSelectionPath().getLastPathComponent()).getNodeID();
			if (nodeId == null || nodeId.toString().trim().equals(ROOT)) {
				return;
			}
			if (bo.equals(btnMod)) {
				setPageStatus(STATUS_MOD);
				getRefOrg().setEnabled(false);
			} else if (bo.equals(btnAdd)) {
				getBillCardPanel().addLine();
					int rowCount = getBillCardPanel().getRowCount();
					getBillCardPanel().setBodyValueAt(Currency.getOrgLocalCurrPK(getPkOrg()),rowCount - 1, "pk_currtype");
					
					getBillCardPanel().execBodyFormulas(rowCount - 1,new String[] { "currtype->getColValue(bd_currtype,"+ 
							getMultiFieldName("name") + ",pk_currtype , pk_currtype)" });
			} else if (bo.equals(btnDel)) {
				getBillCardPanel().delLine();
			} else if (bo.equals(btnSave)) {
				getBillCardPanel().stopEditing();
				doSave();
				getRefOrg().setEnabled(true);
				setPageStatus(STATUS_BROWSE);
			} else if (bo.equals(btnCancel)) {
				setPageStatus(STATUS_BROWSE);
				getRefOrg().setEnabled(true);
			} else if (bo.equals(btnPrint)) {
				doPrint(1);
			} else if (bo.equals(btnCopy)) {
				doCopy();
			} else if (bo.equals(btnImp)) {
				doImport();
			} else if (bo.equals(btnExp)) {
				doExport();
			} else if (bo.equals(btnUp)) {
				doMove(-1);
			} else if (bo.equals(btnTop)) {
				doMove(-2);
			} else if (bo.equals(btnBottom)) {
				doMove(2);
			} else if (bo.equals(btnDown)) {
				doMove(1);
			}else if (bo.equals(btnPreview)) {
				doPrint(2);
			}else if (bo.equals(btnOutput)) {
				doPrint(3);
			}

			// 刷新数据
			if (bo.equals(btnMod) || bo.equals(btnSave) || bo.equals(btnCancel)) {
				refresh();
			}
			if(expDialog!=null && expDialog.getResult() != UIDialog.ID_CANCEL ){
				showHintMessage(ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_SUCCESS,bo));
			}
		} catch (Exception e) {
			ErUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(-1,bo), e);
		}
	}

	/**
	 * @param b
	 * @param i
	 * 
	 */
	private void doMove(int i) {
		String currentBodyTableCode = getBillCardPanel()
				.getCurrentBodyTableCode();
		int[] selectedRow = getBillCardPanel().getBillTable().getSelectedRows();
		if (selectedRow == null || selectedRow.length == 0)
			return;

		for (int j = 0; j < selectedRow.length - 1; j++) {
			if (selectedRow[j] + 1 != selectedRow[j + 1]) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000491")/*
															 * @res
															 * "请选中相邻的行进行移动操作！"
															 */);
				return;
			}
		}

		int row1 = 0, row2 = 0, row3 = 0, row4 = 0, row5 = 0, row6 = 0, row7 = 0, row8 = 0, selS = 0, selT = 0;
		CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRuleVO.class.getName());
		List<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();

		int fromRow = selectedRow[0];
		int toRow = selectedRow[selectedRow.length - 1];

		if (i == 1 || i == 2)
			if (toRow == bodyValueVOs.length - 1)
				return;

		if (i == -1 || i == -2)
			if (fromRow == 0)
				return;

		switch (i) {
		case 1:
			row1 = 0;
			row2 = fromRow;
			row3 = toRow + 1;
			row4 = toRow + 2;
			row5 = fromRow;
			row6 = toRow + 1;
			row7 = toRow + 2;
			row8 = bodyValueVOs.length;

			selS = fromRow + 1;
			selT = toRow + 1;
			break;
		case 2:
			row1 = 0;
			row2 = fromRow;
			row3 = toRow + 1;
			row4 = bodyValueVOs.length;
			row5 = fromRow;
			row6 = toRow + 1;

			selS = bodyValueVOs.length - 1 - (selectedRow.length - 1);
			selT = bodyValueVOs.length - 1;
			break;
		case -1:
			row1 = 0;
			row2 = fromRow - 1;
			row3 = fromRow;
			row4 = toRow + 1;
			row5 = fromRow - 1;
			row6 = fromRow;
			row7 = toRow + 1;
			row8 = bodyValueVOs.length;

			selS = fromRow - 1;
			selT = toRow - 1;
			break;
		case -2:
			row1 = fromRow;
			row2 = toRow + 1;
			row3 = 0;
			row4 = fromRow;
			row5 = toRow + 1;
			row6 = bodyValueVOs.length;

			selS = 0;
			selT = selectedRow.length - 1;
			break;

		default:
			break;
		}

		for (int p = row1; p < row2; p++) {
			list.add(bodyValueVOs[p]);
		}
		for (int p = row3; p < row4; p++) {
			list.add(bodyValueVOs[p]);
		}
		for (int p = row5; p < row6; p++) {
			list.add(bodyValueVOs[p]);
		}
		for (int p = row7; p < row8; p++) {
			list.add(bodyValueVOs[p]);
		}

		getBillCardPanel().getBillData().setBodyValueVO(
				list.toArray(new SuperVO[] {}));
		getBillCardPanel().getBillModel().execLoadFormula();
		getBillCardPanel().getBillTable().updateUI();

		getBillCardPanel().getBillTable().getSelectionModel()
				.setSelectionInterval(selS, selT);
	}

	private void doImport() {
		ReimRuleVO[] reimrules = null;
		// ReimRuleUI ruleUI = null;
		ReimRuleVO[] reimrulesAll = null;
		String currentBodyTableCode = this.getBillCardPanel()
				.getCurrentBodyTableCode();
		ReimRuleVO[] reimRuleVos = (ReimRuleVO[]) this.getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRuleVO.class.getName());
		ImportExcelDialog impDialog = new ImportExcelDialog(this, this);
		if (impDialog.showModal() == UIDialog.ID_OK) {

			reimrules = impDialog.importFromExcel();
			// impDialog.setVisible(true);
			// 如果为覆盖方式合并VO
			reimrulesAll = (ReimRuleVO[]) ArrayUtils.addAll(reimRuleVos,
					reimrules);
			getBillCardPanel().getBillData().clearViewData();
			if (reimrules != null) {
				if (impDialog.isRBIncrement()) {
					// getBillCardPanel().getBillData().setBodyValueVO(reimRuleVos);
					getBillCardPanel().getBillData().setBodyValueVO(
							reimrulesAll);
				} else
					getBillCardPanel().getBillData().setBodyValueVO(reimrules);

			} else
				getBillCardPanel().getBillData().setBodyValueVO(null);
		}
		getBillCardPanel().getBillModel().execLoadFormula();

	}

	private void doExport() {
		if (expDialog == null)
			expDialog = new ExportExcelDialog(this, this);
		expDialog.setVisible(true);
	}

	private void doCopy() {
		CopyDialog dialog = new CopyDialog(this, this);
		int result = dialog.showModal();
		if (result == UIDialog.ID_OK) {
			String corp = dialog.getCorpRef().getRefPK();
			String djlx = dialog.getDjlxRef().getRefCode();
			// FIXME 多语言改动
			// int ret =
			// showYesNoMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000492")/*@res
			// "您确定要把当前公司和交易类型设置的标准复制到指定公司和交易类型吗？这样会丢失目的公司和交易类型原有的报销标准数据！"*/);
			int ret = showYesNoMessage(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("ersetting_0", "02011001-0021")/*
																			 * @res
																			 * "您确定要把当前组织和交易类型设置的标准复制到指定组织和交易类型吗？这样会丢失目的组织和交易类型原有的报销标准数据！"
																			 */);
			if (ret != UIDialog.ID_YES) {
				return;
			}

			String currentBodyTableCode = getBillCardPanel()
					.getCurrentBodyTableCode();
			ReimRuleVO[] reimRuleVos = (ReimRuleVO[]) getBillCardPanel()
					.getBillData().getBodyValueVOs(currentBodyTableCode,
							ReimRuleVO.class.getName());
			if (reimRuleVos == null || reimRuleVos.length == 0) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000493")/*
															 * @res
															 * "选中的报销标准中没有具体的值,复制已取消!"
															 */);
				return;
			}
			String pk_billtype = reimRuleVos[0].getPk_billtype();
			// String pk_corp = reimRuleVos[0].getPk_corp();
			String pk_org = reimRuleVos[0].getPk_org();

			int count = 0;
			if (djlx.equals(pk_billtype) && pk_org.equals(corp)) // 同公司同交易类型不进行复制
				return;
			try {
				for (ReimRuleVO vo : reimRuleVos) {
					vo.setPk_billtype(djlx);
					vo.setPk_org(corp);
					count++;
				}
				List<ReimRuleVO> returnVos = NCLocator.getInstance()
						.lookup(IReimTypeService.class).saveReimRule(
								djlx, corp, reimRuleVos); // 直接进行保存的动作
				if (corp.equals(pk_org)) { // 如果是同公司的复制，需要同时更新datamap
					List<SuperVO> list = new ArrayList<SuperVO>();
					list.addAll(returnVos);
					getDataMap().put(djlx, list);
				}
			} catch (BusinessException e) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011",
								"UPP2011-000494")/* @res "复制失败：" */
						+ e.getMessage());
			}
		

			showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000495",null,new String[]{String.valueOf(count)})/* @res "复制成功,i条记录已复制!" */);
		}
	}

	@SuppressWarnings("restriction")
	private void doPrint(int flag) {
		BillCardPanel billCardPanel = this.getBillCardPanel();
		FIPrintEntry_BX printEntry = new FIPrintEntry_BX(billCardPanel);
		String pk_corp = getPkOrg();
		String pk_user = nc.ui.pub.ClientEnvironment.getInstance().getUser().getPrimaryKey();
		printEntry.getPrintEntry().setTemplateID(pk_corp, nodecode, pk_user, null, null);
		switch (flag) {
		case 1:
			printEntry.print(pk_corp, nodecode, null);
			break;
		case 2:
			printEntry.printView(pk_corp, nodecode, null);
			break;
		case 3:
			printEntry.output(pk_corp, nodecode, null);
		default:
			printEntry.printView(pk_corp, nodecode, null);
		}

	}

	private void doSave() throws BusinessException {
		String currentBodyTableCode = getBillCardPanel()
				.getCurrentBodyTableCode();
		CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRuleVO.class.getName());
		String pk_billtype = getSelectedNodeCode();
		String pk_org = getPkOrg();
		ReimRuleVO[] reimRuleVOs = (ReimRuleVO[]) bodyValueVOs;

		// 检查维度值
		checkReimRules(reimRuleVOs);

		for (ReimRuleVO vo : reimRuleVOs) {
			vo.setPk_billtype(pk_billtype);
			vo.setPk_corp(pk_org);
			vo.setPk_org(pk_org);
		}
		List<ReimRuleVO> returnVos = NCLocator.getInstance().lookup(
				IReimTypeService.class).saveReimRule(pk_billtype, pk_org,
				reimRuleVOs);
		List<SuperVO> list = new ArrayList<SuperVO>();
		list.addAll(returnVos);
		getDataMap().put(pk_billtype, list);
	}

	private void checkReimRules(ReimRuleVO[] reimRuleVOs)
			throws BusinessException {
		List<String> keys = new ArrayList<String>();
		int i = 0;
		for (ReimRuleVO rule : reimRuleVOs) {

			String[] attrs = new String[] { "pk_expensetype", "pk_deptid",
					"pk_psn", "pk_reimtype", "pk_currtype", "def1", "def2",
					"def3", "def4", "def5", "def6", "def7", "def8", "def9",
					"def10" };

			if (rule.getAttributeValue("pk_expensetype") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000499",null,new String[]{String.valueOf(i + 1)})/*
																			 * @res
																			 * "规则设置必须填写费用类型（第{i}行），保存失败!"
																	 */);
			}

			if (rule.getAttributeValue("pk_currtype") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000497",null,new String[]{String.valueOf(i + 1)})/*
																			 * @res
																			 * "规则设置必须填写币种（第i行），保存失败!"
																	 */);
			}

			if (rule.getAttributeValue("amount") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000500",null,new String[]{String.valueOf(i + 1)})/*
																			 * @res
																			 * "规则设置必须填写金额（第i行），保存失败!"
																	 */);
			}
			StringBuffer key = new StringBuffer();

			for (String attr : attrs) {
				if (rule.getAttributeValue(attr) != null)
					key.append(rule.getAttributeValue(attr));

				if (attr.equals("pk_currtype")) {
					int scale = 2;
					try {

						scale = Currency.getCurrDigit(rule.getAttributeValue(
								attr).toString());
					} catch (Exception e) {
						ExceptionHandler.consume(e);
					}
					rule.setAmount(rule.getAmount().setScale(scale,
							UFDouble.ROUND_HALF_UP));
				}
			}

			rule.setPriority(Integer.valueOf(i++));

			if (keys.contains(key.toString())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000501")/*
																			 * @res
																			 * "规则设置中包含各个维度全部相同的记录，保存失败!"
																			 */);
			} else {
				keys.add(key.toString());
			}
		}
	}

	private String getPkOrg() {
		return getRefOrg().getRefPK();
	}

	public int getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(int pageStatus) {
		this.pageStatus = pageStatus;
	}

	private void refresh() {
		refreshUI();
		refreshButtonStatus();
	}

	private void refreshButtonStatus() {

		// added by chendya
		updateButtonStatus(pageStatus);

		String selectedNodeCode = getSelectedNodeCode();
		for (ButtonObject button : getButtons()) {
			button.setEnabled(false);
		}
		switch (pageStatus) {
		case 0: {// browse
			if (!selectedNodeCode.equals(ROOT)) {
				btnMod.setEnabled(true);
				btnPrintGroup.setEnabled(true);
				btnCopy.setEnabled(true);
				btnExp.setEnabled(true);
			}
			break;
		}
		case 1: {// edit
			btnSave.setEnabled(true);
			btnAdd.setEnabled(true);
			btnDel.setEnabled(true);
			btnCancel.setEnabled(true);
			btnImp.setEnabled(true);

			btnDown.setEnabled(true);
			btnBottom.setEnabled(true);
			btnTop.setEnabled(true);
			btnUp.setEnabled(true);

			break;
		}
		case 3: {// 全部置灰
			btnSave.setEnabled(false);
			btnAdd.setEnabled(false);
			btnDel.setEnabled(false);
			btnCancel.setEnabled(false);
			btnImp.setEnabled(false);

			btnDown.setEnabled(false);
			btnBottom.setEnabled(false);
			btnTop.setEnabled(false);
			btnUp.setEnabled(false);

			break;
		}
		default:
			break;
		}
		updateButtons();
	}

	/**
	 * 设置按钮状态
	 * 
	 * @param status
	 */
	private void updateButtonStatus(Integer status) {
		// 重置按钮
		setButtons(getUEButtons(status));
		initActions();
	}

	private String getSelectedNodeCode() {
		TableTreeNode node = ((TableTreeNode) getBillTypeTree()
				.getSelectionPath().getLastPathComponent());
		if (node.getNodeID() == null
				|| node.getNodeID().toString().trim().equals(ROOT)) {
			return ROOT;
		} else {
			VOTreeNode node2 = (VOTreeNode) node;
			BilltypeVO data = (BilltypeVO) node2.getData();
			String pk_billtypecode = data.getPk_billtypecode();
			return pk_billtypecode;
		}
	}

	private void refreshUI() {
		switch (pageStatus) {
		case 0: { // browse
			// FIXME
			getBillCardPanel().setEnabled(false);
			getBillTypeTree().setEnabled(true);
			getTreePanel().setEnabled(true);
			break;
		}
		case 1: {// edit
			getBillCardPanel().setEnabled(true);
			getBillTypeTree().setEnabled(false);
			getTreePanel().setEnabled(false);
			break;
		}
		default:
			break;
		}

		List<SuperVO> vos = getDataMap().get(getSelectedNodeCode());
		if (vos != null) {
			getBillCardPanel().getBillData().setBodyValueVO(
					vos.toArray(new SuperVO[] {}));
		} else {
			getBillCardPanel().getBillData().setBodyValueVO(null);
		}
		getBillCardPanel().getBillModel().execLoadFormula();
	}

	public ReimRuleDefVO getRuleDef() {
		return ruleDef;
	}

	public void setRuleDef(ReimRuleDefVO ruleDef) {
		this.ruleDef = ruleDef;
	}

	public UIRefPane getRefOrg() {
		if (ivjtOrg == null) {

			ivjtOrg = new UIRefPane();
			ivjtOrg.setName("pk_org");
			ivjtOrg.setRefNodeName("财务组织"); /* -=notranslate=- */
			ivjtOrg.setRefModel(new FinanceOrgDefaultRefTreeModel());
			ivjtOrg.setPreferredSize(new Dimension(200, ivjtOrg.getHeight()));	
			ivjtOrg.addValueChangedListener(this);
			ivjtOrg.getRefModel().setFilterPks(ErUiUtil.getPermissionOrgs(null));
			ivjtOrg.getUITextField().setShowMustInputHint(true);
		}
		return ivjtOrg;
	}

	private UIPanel getUIPanelOrg() {
		if (jPanelOrg == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			// 注意多语
			// jLabel6.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000048")/*@res
			// "单据类型"*/);
			jLabel6 = new UILabel();
			jLabel6.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("common", "UCMD1-000006")/* @res "财务组织" */);
			jPanelOrg = new UIPanel();
			jPanelOrg.setLayout(flowLayout1);
			jPanelOrg.setName("jPanelOrg");
			jPanelOrg.add(jLabel6, null);
			jPanelOrg.add(getRefOrg(), null);
			jPanelOrg.setSize(639, 363);
		}
		return jPanelOrg;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if(event.getNewValue() != null){
			getBillCardPanel().setEnabled(true);
			getBillTypeTree().setEnabled(true);
			getTreePanel().setEnabled(true);
			initData();
			
			refreshTemplate();
			refreshUI();
			refreshButtonStatus();
		}else{
			getBillCardPanel().setEnabled(false);
			getBillTypeTree().setEnabled(false);
			getTreePanel().setEnabled(false);
			for(ButtonObject bo : getButtons()){
				bo.setEnabled(false);
			}
		}

	}
	
	@Override
	public boolean onClosing() {
		if (STATUS_BROWSE != getPageStatus()) {
			return doClosing();
		} else {
			return true;
		}
	}
	
	private boolean doClosing() {

		int i = CommonConfirmDialogUtils.showConfirmSaveDialog(getParent());
		switch (i) {
		case UIDialog.ID_YES: {
			try {
				getBillCardPanel().stopEditing();
				doSave();
			} catch (BusinessException e) {
				ErUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(-1,btnSave), e);
				return false;
			}
			return true;
		}
		case UIDialog.ID_CANCEL: {
			return false;
		}
		case UIDialog.ID_NO:
		default:
			return true;
		}

	}

}