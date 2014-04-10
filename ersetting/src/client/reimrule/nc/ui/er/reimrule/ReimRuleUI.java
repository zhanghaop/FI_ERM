package nc.ui.er.reimrule;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.GroupAction;
import nc.funcnode.ui.action.INCAction;
import nc.funcnode.ui.action.MenuAction;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.ButtonUtil;
import nc.ui.arap.bx.print.FIPrintEntry_BX;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.util.ErUiUtil;
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
import nc.ui.trade.excelimport.util.StatusBarMsgCleaner;
import nc.ui.trade.pub.IVOTreeDataByCode;
import nc.ui.trade.pub.TableTreeNode;
import nc.ui.trade.pub.TreeCreateTool;
import nc.ui.trade.pub.VOTreeNode;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.vo.ep.bx.MyDefusedVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.ml.MultiLangContext;
import nc.vo.pf.pub.util.ArrayUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;

/**
 * @author shiwla
 * nc.ui.er.reimrule.ReimRuleUI
 * time 2014-01-29
 */
@SuppressWarnings("deprecation")
public class ReimRuleUI extends ToftPanel implements ValueChangedListener {

	private static final long serialVersionUID = 1L;
	
	private static final String ROOT = "root";
	private static final int STATUS_BROWSE = 0;
	private static final int STATUS_MODIFY = 1;
	private static final int STATUS_CONFIG = 2;
	private static final int STATUS_CONTROL = 3;
	private int pageStatus = STATUS_BROWSE;
	private static final String nodecode = "20110RSS";
	private static final String nodecodeRule = "20111RSS";//������׼����ģ��
	private static final String nodecodeDim = "20112RSS";//������׼ά��ģ��
	private static final String pk_group=WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
	private String centControlItem = null;//���Ŀ�����
	private ExportExcelDialog expDialog = null;
	private UIRefPane ivjtOrg;
	private UIPanel jPanelOrg = null;
	private UILabel jLabel6 = null;
	
	private final ButtonObject btnMod = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000055")/* @res "�޸�" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000055")/* @res "�޸�" */, 5, "Edit"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnAdd = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000187")/* @res "����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000187")/* @res "����" */, 5, "AddLine"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnDel = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000188")/* @res "ɾ��" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000188")/* @res "ɾ��" */, 5, "DelLine"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnSave = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000061")/* @res "����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000061")/* @res "����" */, 5, "Save"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnCancel = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000064")/* @res "ȡ��" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000064")/* @res "ȡ��" */, 5, "Cancel"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnPrintGroup = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "��ӡ" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "��ӡ" */, 5, "PrintGroup"); /*
																		 * -=notranslate
																		 * = -
																		 */

	private final ButtonObject btnPrint = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "��ӡ" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000207")/* @res "��ӡ" */, 5, "Print"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnPreview = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000112")/* @res "Ԥ��" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000112")/* @res "Ԥ��" */, 5, "Preview"); /*
																	 * -=notranslate
																	 * = -
																	 */
	//v63ȥ���������
	private final ButtonObject btnOutput = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000148")/* @res "���" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC001-0000148")/* @res "���" */, 5, "Output");
	private final ButtonObject btnCopy = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPTcommon-000318")/* @res "����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPTcommon-000318")/* @res "����" */, 5, "Copy"); /*
																	 * -=notranslate
																	 * =-
	
																	 */
	//v631����ǰ̨���úͿ�������
	private final ButtonObject btnConfig = new ButtonObject("����" ,"����", 5, "Config");
	private final ButtonObject btnControl = new ButtonObject("��������" ,"��������", 5, "Control");
	private final ButtonObject btnRefresh = new ButtonObject("ˢ��" ,"ˢ��", 5, "Refresh");
	
	private final ButtonObject btnImp = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000483")/* @res "����" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000483")/* @res "����" */, 5, "Import"); /*
															 * -=notranslate= -
															 */
	private final ButtonObject btnExp = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000484")/* @res "����" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000484")/* @res "����" */, 5, "Export"); /*
															 * -=notranslate= -
															 */

	private final ButtonObject btnUp = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000485")/* @res "����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000485")/* @res "����" */, 5, "Up"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnDown = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000486")/* @res "����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000486")/* @res "����" */, 5, "Down"); /*
																 * -=notranslate=
																 * -
																 */
	private final ButtonObject btnTop = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000487")/* @res "���Ƶ���ǰ" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000487")/* @res "���Ƶ���ǰ" */, 5, "Top"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private final ButtonObject btnBottom = new ButtonObject(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000488")/* @res "���Ƶ����" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000488")/* @res "���Ƶ����" */, 5, "Bottom"); /*
																	 * -=notranslate
																	 * =-
																	 */
	private UISplitPane splitPane;

	private UISplitPane splitPane1;

	private BillCardPanel cardPanel;

	private UIScrollPane treePanel;
	
	private ReimConfigPanel dimPanel;

	private UITree tree;

	public ReimRuleUI() {
		super();
		initialize();
		btnMod.setEnabled(false);
		btnAdd.setEnabled(false);
		btnDel.setEnabled(false);
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
				btnCopy, btnImp, btnExp, btnConfig, btnControl, btnUp, btnDown, btnTop, btnBottom,
				btnPrintGroup };
	}

	@Override
	protected void postInit() {
		super.postInit();
		initActions();
		setPageStatus(4);
		getBillTypeTree().setSelectionInterval(0, 0);
	}

	// �������ж���İ�ť����Ϊ��ť��ʼ����ݼ���(����ť�����˳�����η����ݼ�)
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
		// ����
		createGroupActions(menuActions);
		menuActions = addSeparatorBetweenGroupBtn(menuActions);
		addSeparator4Print(menuActions);
		setMenuActions(menuActions);
	}

	// begin--added by chendya ��ӷ���

	/**
	 * ��ӡ��ť�޸�Ϊ�ָť
	 * 
	 * @param list
	 */
	private void addSeparator4Print(List<Action> list) {
		// ��¼��ӡ��ťλ��
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
			// �Ƴ���ӡ
			list.remove(pos);

			// �ع���ӡ��ťΪ�ָť
			GroupAction newPrintAction = new GroupAction();
			(newPrintAction).setActions(Arrays.asList(children));
			list.add(pos, newPrintAction);
		}
	}

	private ButtonObject[] getEditButtons() {
		return new ButtonObject[] { btnAdd, btnDel, btnSave, btnCancel, btnImp,
				btnUp, btnDown, btnTop, btnBottom };
	}

	private ButtonObject[] getConfigButtons() {
		return new ButtonObject[] { btnAdd, btnDel, btnSave, btnCancel, btnCopy, btnUp, btnDown, btnTop, btnBottom};
	}
	
	private ButtonObject[] getBrowseButtons() {
		return new ButtonObject[] { btnMod, btnCopy, btnRefresh,btnConfig, btnControl, btnExp, btnPrintGroup};
	}

	private ButtonObject[] getControlButtons() {
		return new ButtonObject[] {btnSave, btnCancel};
	}
	public ButtonObject[] getUEButtons(Integer status) {
		switch (status.intValue()) {
		case 1:
			// �༭̬
			return getEditButtons();
		case 2:
			// ����̬
			return getConfigButtons();
		case 3:
			//����̬
			return getControlButtons();
		default:
			// ���̬
			return getBrowseButtons();
		}
	}

	/**
	 * ��ť������֮����ӷָ�Action
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
	 * ��ӷ���
	 * 
	 * @author chendya
	 * @param actions
	 */
	private void createGroupActions(List<Action> actions) {
		for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
			Action action = iterator.next();
			//�����ť��δ���飬����Ҫ����ť�����
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

	List<String> BTN_GROUP_CONFIG_CODES = Arrays.asList(new String[] { 
			"Config","Control"
	});
	
	List<String> BTN_GROUP_REFRESH_CODES = Arrays.asList(new String[] { 
			"Refresh"
	});
	
	List<String> BTN_GROUP_IMPEXP_CODES = Arrays.asList(new String[] { "Imp",
			"Exp"

	});

	List<String> BTN_GROUP_UP2DOWN_CODES = Arrays.asList(new String[] { "Up",
			"Down", "Top", "Bottom"

	});

	/**
	 * ��/��/ɾ����ť��
	 */
	static String BTN_GROUP_ADD_MODIFIY_DEL = "BTN_GROUP_ADD_MODIFIY_DEL";

	/**
	 * ˢ�°�ť�鰴ť��
	 */
	static String BTN_GROUP_REFRESH = "BTN_GROUP_REFRESH";

	/**
	 * ��ϸ��ť��
	 */
	static String BTN_GROUP_DETAIL = "BTN_GROUP_DETAIL";

	/**
	 * ���水ť��
	 */
	static String BTN_GROUP_SAVE = "BTN_GROUP_SAVE";

	/**
	 * ȡ����ť��
	 */
	static String BTN_GROUP_CANCEL = "BTN_GROUP_CANCEL";

	/**
	 * ��ӡ��ť��
	 */
	static String BTN_GROUP_PRINT = "BTN_GROUP_PRINT";

	/**
	 * ������ť��
	 */
	static String BTN_GROUP_IMPEXP = "BTN_GROUP_IMPEXP";
	/**
	 * ���ð�ť��
	 */
	static String BTN_GROUP_CONFIG = "BTN_GROUP_CONFIG";

	/**
	 * �������ư�ť��
	 */
	static String BTN_GROUP_UP2DOWN = "BTN_GROUP_UP2DOWN";

	static String BTN_GROUP_NAME = "BTN_GROUP_NAME";

	/**
	 * Ϊ������action��ť�������
	 * 
	 * @author chendya
	 */
	private void appendGroupName(Action action) {
		// ��ť����
		final String code = (String) action.getValue(INCAction.CODE);

		// ��ť����
		final String groupName = BTN_GROUP_NAME;

		// ��/��/ɾ����ť��
		if (BTN_GROUP_ADD_MODIFIY_DEL_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_ADD_MODIFIY_DEL);
		}

		// ���水ť��
		else if (BTN_GROUP_SAVE_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_SAVE);
		}

		// ȡ����ť��
		else if (BTN_GROUP_CANCEL_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_CANCEL);
		}

		// ��ӡ��ť��
		else if (BTN_GROUP_PRINT_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_PRINT);
		}

		// ������ť��
		else if (BTN_GROUP_IMPEXP_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_IMPEXP);
		}

		//ˢ�°�ť��
		else if(BTN_GROUP_REFRESH_CODES.contains(code)){
			action.putValue(groupName, BTN_GROUP_REFRESH);
		}
		
		//���ð�ť��
		else if(BTN_GROUP_CONFIG_CODES.contains(code)){
			action.putValue(groupName, BTN_GROUP_CONFIG);
		}
		// ���ư�ť��
		else if (BTN_GROUP_UP2DOWN_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_UP2DOWN);
		}
	}

	// --end

	//�����ݿ��л�ñ���ά�Ⱥͱ�����׼�����浽����
	private void initData() {
		IReimTypeService remote = NCLocator.getInstance().lookup(IReimTypeService.class);
		List<ReimRulerVO> vorules;
		List<ReimRuleDimVO> vodims;
		try {
			vorules = remote.queryReimRuler(null, pk_group, getPkOrg());
			ReimRuleUtil.setDataMapRule(VOUtils.changeCollectionToMapList(vorules, "pk_billtype"));
			vodims = remote.queryReimDim(null, pk_group, getPkOrg());
			ReimRuleUtil.setDataMapDim(VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
			ReimRuleUtil.getTemplateBillDataMap().clear();
		} catch (BusinessException e) {
			handleException(e);
		}
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
			// �����
			this.add(getSplitPaneTop());
			getSplitPaneTop().setTopComponent(getUIPanelOrg());
			getSplitPaneTop().setBottomComponent(getSplitPane());
			getSplitPaneTop().setDividerLocation(28);
			getSplitPaneTop().setEnabled(false);
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
			dimPanel = new ReimConfigPanel(nodecodeDim);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"uifactory", "UPPuifactory-000109")/*
														 * @res "�����쳣�������ʼ������"
														 */);
		}
	}
	
	public BillCardPanel getBillCardPanel() {
		if(pageStatus==STATUS_CONFIG)
		{
			return dimPanel.getBillCardPanel();
		}
		else
		{
			if (cardPanel == null) {
				try {
					cardPanel = new BillCardPanel();
					cardPanel.loadTemplet(nodecodeRule, null, ErUiUtil.getPk_user(),
							ErUiUtil.getBXDefaultOrgUnit());
					cardPanel.getBodyPanel().getTable().removeSortListener();
					cardPanel.addEditListener(new BillEditListener() {

						@Override
						public void bodyRowChange(BillEditEvent e) {

						}

						@Override
						public void afterEdit(BillEditEvent e) {
							ReimRuleUtil.afterEdit(getBillCardPanel(),getSelectedNodeCode(),e);
						}
					});

				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}

			}

			return cardPanel;
		}
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
				return "djlxbm";
//				return "pk_billtypecode";
			}

			public String getCodeRule() {
				return "2";
			}

			public String getShowFieldName() {
				return getMultiFieldName("djlxmc");
//				return getMultiFieldName("billtypename");
			}

			public SuperVO[] getTreeVO() {
				try {
					List<DjLXVO> list = new ArrayList<DjLXVO>();
					DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
							"pk_group = '"+pk_group+"' and djdl in('jk','bx')");
					for(DjLXVO vo:vos){
						if(vo.getAttributeValue(getShowFieldName()) == null){
							//��billtypename�����ֶ�ֵΪ��ʱ���ֶ���ֵΪ~��ֵ����ֹ�Զ��彻������δ¼�ƶ���ʱ������������ָ�룩
							vo.setAttributeValue(getShowFieldName(), vo.getDjlxmc());
						}
						if(vo.getBxtype()==null || vo.getBxtype() != ErmDjlxConst.BXTYPE_ADJUST)
							list.add(vo);
					}
					DjLXVO[] toArray = list.toArray(new DjLXVO[] {});
					Arrays.sort(toArray, new Comparator<DjLXVO>() {
						public int compare(DjLXVO o1, DjLXVO o2) {
							return o1.getDjlxbm().compareTo(o2.getDjlxbm());
						}
					});
					return toArray;
				}catch (BusinessException e) {
					return null;
				}
				
//				HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
//				List<BilltypeVO> list = new ArrayList<BilltypeVO>();
//				for (BilltypeVO vo : billtypes.values()) {
//					if (vo.getSystemcode() != null && vo.getSystemcode().equalsIgnoreCase(BXConstans.ERM_PRODUCT_CODE)) {
//						if (vo.getPk_billtypecode().equals(BXConstans.BX_DJLXBM)
//								|| vo.getPk_billtypecode().equals(BXConstans.JK_DJLXBM)) {
//							continue;
//						}
//						// ͨ����ǰ���Ž��й���
//						if (vo.getPk_group() != null && !vo.getPk_group().equalsIgnoreCase(ErUiUtil.getPK_group())) {
//							continue;
//						}
//						if (BXConstans.BX_DJLXBM.equals(vo.getParentbilltype())
//								|| BXConstans.JK_DJLXBM.equals(vo.getParentbilltype())) {
//							
//							if(vo.getAttributeValue(getShowFieldName()) == null){
//								//��billtypename�����ֶ�ֵΪ��ʱ���ֶ���ֵΪ~��ֵ����ֹ�Զ��彻������δ¼�ƶ���ʱ������������ָ�룩
//								vo.setAttributeValue(getShowFieldName(), vo.getBilltypename());
//							}
//							list.add(vo);
//						}
//					}
//				}
//				BilltypeVO[] toArray = list.toArray(new BilltypeVO[] {});
//				Arrays.sort(toArray, new Comparator<BilltypeVO>() {
//					public int compare(BilltypeVO o1, BilltypeVO o2) {
//						return o1.getPk_billtypecode().compareTo(o2.getPk_billtypecode());
//					}
//				});
//				return toArray;
			};

		};
		DefaultTreeModel model = treeCreateTool.createTreeByCode(idtree
				.getTreeVO(), idtree.getCodeFieldName(), idtree.getCodeRule(),
				idtree.getShowFieldName());

		treeCreateTool.modifyRootNodeShowName(nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("2011", "UPP2011-000489")/*
																	 * @res
																	 * "��������������"
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
	 * ������������ѡ�����޸�ʱ����
	 */
	private void refreshTemplate() {
		final String djlxbm = getSelectedNodeCode();
		if (djlxbm.equals(ROOT)){
			return;
		}
		BillData billData = ReimRuleUtil.getTemplateInitBillData(djlxbm,nodecodeRule);
		List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(djlxbm);
		if (reimruledim!=null && reimruledim.size()>0) {
			getBillCardPanel().setVisible(true);
			for(SuperVO vo:reimruledim)
			{
				if(((ReimRuleDimVO)vo).getControlflag().booleanValue())
				{
					centControlItem=((ReimRuleDimVO)vo).getCorrespondingitem();
				}
			}
			if(pageStatus==STATUS_CONTROL){
				if(centControlItem==null){
					billData.setBodyItems(null);
					getBillCardPanel().setBillData(billData);
				}
				else
					ReimRuleUtil.showControlPage(getBillCardPanel(),billData,reimruledim,centControlItem,getPkOrg(),getSelectedNodeCode());
			}
			else{
				ReimRuleUtil.showModifyPage(getBillCardPanel(),billData,reimruledim,getPkOrg());
//				���浽�õ������Ͷ�Ӧ��billdata����
				ReimRuleUtil.getTemplateBillDataMap().put(djlxbm, billData);
			}
		}
		else{
			getBillCardPanel().setVisible(false);
		}
	}
	
	protected void handleException(java.lang.Throwable ex) {
		ExceptionHandler.consume(ex);
//		ErUiUtil.showUif2DetailMessage(this, "", ex);
		showErrorMessage("����",ex.getMessage());
		StatusBarMsgCleaner.getInstance().messageAdded(this);
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				"UPP2011-000490")/* @res "������׼����" */;
	}

	@Override
	public void onButtonClicked(ButtonObject bo) {
		try {
			Object nodeId = ((TableTreeNode) getBillTypeTree().getSelectionPath().getLastPathComponent()).getNodeID();
			if (nodeId == null || nodeId.toString().trim().equals(ROOT)) {
				return;
			}
			if (bo.equals(btnMod)) {
				setPageStatus(STATUS_MODIFY);
				getRefOrg().setEnabled(false);
			}
			else if(bo.equals(btnConfig)){
				if(doProcess(true))
					refresh();
//				setPageStatus(STATUS_CONFIG);
//				getRefOrg().setEnabled(false);
			}else if(bo.equals(btnControl)){
				doProcess(false);
				refresh();
//				setPageStatus(STATUS_CONTROL);
//				getRefOrg().setEnabled(false);
			}else if (bo.equals(btnAdd)) {
				doAdd();
			} else if (bo.equals(btnDel)) {
				doDelete();
			} else if (bo.equals(btnSave)) {
				doSave();
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

			// ˢ������
			if (bo.equals(btnMod) || bo.equals(btnRefresh) || bo.equals(btnSave) 
					|| bo.equals(btnCancel)) {
				refresh();
			}
			if(bo.equals(btnRefresh)){
				initData();
				refresh();
				showHintMessage("ˢ�³ɹ�");
				StatusBarMsgCleaner.getInstance().messageAdded(this);
			}
			if(expDialog!=null && expDialog.getResult() != UIDialog.ID_CANCEL ){
				showHintMessage(ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_SUCCESS,bo));
			}
		} catch (Exception e) {
			ErUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(-1,bo), e);
		}
	}
	
	public boolean doProcess(boolean isConfig) {
//        LoginContext context = new LoginContext();
//        ToftPanelAdaptor adpter = new ToftPanelAdaptor();
//        try{
//        	adpter.init(getFuncletContext());
//        }catch(Exception e){
//        	//�����null�����ùܣ���Ӱ��
//        }
//        context.setEntranceUI(adpter);
//        context.setPk_group(pk_group);
//        context.setPk_org(getPkOrg());
//        context.setPk_loginUser(ErUiUtil.getPk_user());
//        // �Ի����ʼ��
//        BatchEditDialog dialog = new BatchEditDialog(adpter);
//
//        // �Ի������
//        dialog.setTitle("������׼����");
//        FuncletInitData data = new FuncletInitData(-1, dialog);
//        Dimension dimension = new Dimension(1000, 800);
//        dialog.initUI(isConfig,getSelectedNodeCode(),context, getFilePath(isConfig), data, dimension);
//        dialog.setResizable(true);
//        dialog.showModal();
//        if(dialog.getResult() == UIDialog.ID_OK)
//    		return true;
        return false;
    }
	
	private String getFilePath(boolean isConfig){
		if(isConfig)
			return "nc/ui/er/reimrule/config/reimconfig_config.xml";
		else
			return "nc/ui/er/reimrule/config/reimcontrol_config.xml";
		
	}
	
	/**
	 * ��ȡ�����ֶ����ƣ�name2,name,name3��
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
	// ��̬��ʾ�������ƣ��������͵Ķ������⴦��
	private void showObjName(BillCardPanel panel) {
		if(panel.getBillModel() == null)
			return;
		String namefield = getMultiFieldName("name");
		String[] expensetypeFormula = new String[1];
		expensetypeFormula[0] = "pk_expensetype->getColValue(er_expensetype, " + namefield + ",pk_expensetype, pk_expensetype)";
		String[] a = panel.getBillModel().getItemByKey("pk_expensetype").getLoadFormula();
		if(ArrayUtil.arrayEquals(a, expensetypeFormula))
			return;
		panel.getBillModel().getItemByKey("pk_expensetype").setLoadFormula(expensetypeFormula);
		panel.getBillModel().execLoadFormula();
	}
	private void doAdd() throws BusinessException {
		if(pageStatus==STATUS_MODIFY)
		{
			//�޸ı�׼
			getBillCardPanel().addLine();
			int rowCount = getBillCardPanel().getRowCount();
			String pk;
			if(getPkOrg().equals("~"))
				pk = Currency.getGroupLocalCurrPK(pk_group);
			else
				pk = Currency.getOrgLocalCurrPK(getPkOrg());
			if(pk==null)
				pk="";
			getBillCardPanel().setBodyValueAt(pk,rowCount - 1, ReimRulerVO.PK_CURRTYPE);
			getBillCardPanel().setBodyValueAt(Currency.getCurrInfo(pk).getAttributeValue(getMultiFieldName("name")),rowCount - 1, ReimRulerVO.PK_CURRTYPE_NAME);
			if(getBillCardPanel().getBillModel()!=null)
				getBillCardPanel().getBillModel().loadLoadRelationItemValue(rowCount - 1,rowCount - 1);
		}
		else if(pageStatus==STATUS_CONFIG)
		{
			//�޸ı�׼ά��
			dimPanel.addLine();
		}
	}
	
	private void doDelete() throws BusinessException {
		if(pageStatus==STATUS_MODIFY)
		{
			getBillCardPanel().delLine();
		}
		else if(pageStatus==STATUS_CONFIG)
		{
			dimPanel.delLine(ReimRuleUtil.getDataMapRule().get(getSelectedNodeCode()));
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
															 * "��ѡ�����ڵ��н����ƶ�������"
															 */);
				return;
			}
		}

		int row1 = 0, row2 = 0, row3 = 0, row4 = 0, row5 = 0, row6 = 0, row7 = 0, row8 = 0, selS = 0, selT = 0;
		CircularlyAccessibleValueObject[] bodyValueVOs = null;
		if(pageStatus==STATUS_MODIFY)
		{
			bodyValueVOs = getBillCardPanel()
			.getBillData().getBodyValueVOs(currentBodyTableCode,
					ReimRulerVO.class.getName());
		}
		else if(pageStatus==STATUS_CONFIG)
		{
			bodyValueVOs = getBillCardPanel()
			.getBillData().getBodyValueVOs(currentBodyTableCode,
					ReimRuleDimVO.class.getName());
		}
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
			if(pageStatus==STATUS_MODIFY)
			{
				ReimRulerVO rule = (ReimRulerVO)bodyValueVOs[p];
	             if(rule.getPk_expensetype()!=null && ReimRuleUtil.getExpenseMap().get(rule.getPk_expensetype())==null)
	 				rule.setPk_expensetype(ReimRuleUtil.getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
			}
			list.add(bodyValueVOs[p]);
		}
		for (int p = row3; p < row4; p++) {
			if(pageStatus==STATUS_MODIFY)
			{
				ReimRulerVO rule = (ReimRulerVO)bodyValueVOs[p];
	             if(rule.getPk_expensetype()!=null && ReimRuleUtil.getExpenseMap().get(rule.getPk_expensetype())==null)
	 				rule.setPk_expensetype(ReimRuleUtil.getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
			}
			list.add(bodyValueVOs[p]);
		}
		for (int p = row5; p < row6; p++) {
			if(pageStatus==STATUS_MODIFY)
			{
				ReimRulerVO rule = (ReimRulerVO)bodyValueVOs[p];
	             if(rule.getPk_expensetype()!=null && ReimRuleUtil.getExpenseMap().get(rule.getPk_expensetype())==null)
	 				rule.setPk_expensetype(ReimRuleUtil.getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
			}
			list.add(bodyValueVOs[p]);
		}
		for (int p = row7; p < row8; p++) {
			if(pageStatus==STATUS_MODIFY)
			{
				ReimRulerVO rule = (ReimRulerVO)bodyValueVOs[p];
	             if(rule.getPk_expensetype()!=null && ReimRuleUtil.getExpenseMap().get(rule.getPk_expensetype())==null)
	 				rule.setPk_expensetype(ReimRuleUtil.getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
			}
			list.add(bodyValueVOs[p]);
		}

		getBillCardPanel().getBillData().setBodyValueVO(
				list.toArray(new SuperVO[] {}));
		if(getBillCardPanel().getBillModel()!=null)
			getBillCardPanel().getBillModel().loadLoadRelationItemValue();
		getBillCardPanel().getBillModel().execLoadFormula();
		getBillCardPanel().getBillTable().updateUI();

		getBillCardPanel().getBillTable().getSelectionModel()
				.setSelectionInterval(selS, selT);
	}

	private void doImport() {
//		ReimRuleVO[] reimrules = null;
//		// ReimRuleUI ruleUI = null;
//		ReimRuleVO[] reimrulesAll = null;
//		String currentBodyTableCode = this.getBillCardPanel()
//				.getCurrentBodyTableCode();
//		ReimRuleVO[] reimRuleVos = (ReimRuleVO[]) this.getBillCardPanel()
//				.getBillData().getBodyValueVOs(currentBodyTableCode,
//						ReimRuleVO.class.getName());
//		ImportExcelDialog impDialog = new ImportExcelDialog(this, this);
//		if (impDialog.showModal() == UIDialog.ID_OK) {
//
//			reimrules = impDialog.importFromExcel();
//			// impDialog.setVisible(true);
//			// ���Ϊ���Ƿ�ʽ�ϲ�VO
//			reimrulesAll = (ReimRuleVO[]) ArrayUtils.addAll(reimRuleVos,
//					reimrules);
//			getBillCardPanel().getBillData().clearViewData();
//			if (reimrules != null) {
//				if (impDialog.isRBIncrement()) {
//					// getBillCardPanel().getBillData().setBodyValueVO(reimRuleVos);
//					getBillCardPanel().getBillData().setBodyValueVO(
//							reimrulesAll);
//				} else
//					getBillCardPanel().getBillData().setBodyValueVO(reimrules);
//
//			} else
//				getBillCardPanel().getBillData().setBodyValueVO(null);
//		}
//		getBillCardPanel().getBillModel().execLoadFormula();

	}

	private void doExport() {
		if (expDialog == null)
			expDialog = new ExportExcelDialog(this, getBillCardPanel());
		expDialog.setVisible(true);
	}

	private void doCopy() {
		CopyDialog dialog = new CopyDialog(this);
		int result = dialog.showModal();
		if (result == UIDialog.ID_OK) {
			String corp = dialog.getCorpRef().getRefPK();
			String djlx = dialog.getDjlxRef().getRefCode();
			int ret = showYesNoMessage(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("ersetting_0", "02011001-0021")/*
																			 * @res
																			 * "��ȷ��Ҫ�ѵ�ǰ��֯�ͽ����������õı�׼���Ƶ�ָ����֯�ͽ��������������ᶪʧĿ����֯�ͽ�������ԭ�еı�����׼���ݣ�"
																			 */);
			if (ret != UIDialog.ID_YES) {
				return;
			}
			//��ָ����˾Ϊ�գ���Ĭ��Ϊ���ż���׼
			if(corp == null)
				corp="~";
			String pk_billtype = getSelectedNodeCode();
			String pk_org = getPkOrg();
			if(pageStatus==STATUS_CONFIG)
			{
				dimPanel.doCopy(pk_group,pk_org,pk_billtype,corp,djlx);
			}
			else
			{
				String currentBodyTableCode = getBillCardPanel()
						.getCurrentBodyTableCode();
				ReimRulerVO[] reimRuleVos = (ReimRulerVO[]) getBillCardPanel()
						.getBillData().getBodyValueVOs(currentBodyTableCode,
								ReimRulerVO.class.getName());
				if (reimRuleVos == null || reimRuleVos.length == 0) {
					showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("2011", "UPP2011-000493")/*
																 * @res
																 * "ѡ�еı�����׼��û�о����ֵ,������ȡ��!"
																 */);
					return;
				}
				int count = 0;
				if (djlx.equals(pk_billtype) && pk_org.equals(corp)) // ͬ��˾ͬ�������Ͳ����и���
					return;
				try {
					for (ReimRulerVO vo : reimRuleVos) {
						//��ֵ������������Ҫ���⴦��
						if(vo.getPk_expensetype()!=null && ReimRuleUtil.getExpenseMap().get(vo.getPk_expensetype())==null)
							vo.setPk_expensetype(ReimRuleUtil.getExpenseNameMap().get(vo.getPk_expensetype()).getPrimaryKey());
						vo.setPk_billtype(djlx);
						vo.setPk_group(pk_group);
						vo.setPk_org(corp);
						vo.setPriority(count);
						count++;
					}
					List<ReimRulerVO> returnVos = NCLocator.getInstance()
							.lookup(IReimTypeService.class).saveReimRule(
									djlx,pk_group,corp,reimRuleVos); // ֱ�ӽ��б���Ķ���
					if (corp.equals(pk_org)) { // �����ͬ��˾�ĸ��ƣ���Ҫͬʱ����datamaprule
						List<SuperVO> list = new ArrayList<SuperVO>();
						list.addAll(returnVos);
						ReimRuleUtil.putRule(djlx, list);
					}
				} catch (BusinessException e) {
					showErrorMessage(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("2011",
									"UPP2011-000494")/* @res "����ʧ�ܣ�" */
							+ e.getMessage());
				}
				showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000495",null,new String[]{String.valueOf(count)})/* @res "���Ƴɹ�,i����¼�Ѹ���!" */);
			}
		}
	}

	@SuppressWarnings("restriction")
	private void doPrint(int flag) {
		BillCardPanel billCardPanel = this.getBillCardPanel();
		FIPrintEntry_BX printEntry = new FIPrintEntry_BX(billCardPanel);
		String pk_corp = getPkOrg();
		String pk_user = WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey();
		//String pk_user = nc.ui.pub.ClientEnvironment.getInstance().getUser().getPrimaryKey();
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
		if(pageStatus==STATUS_MODIFY)
		{
			//������׼�޸Ľ���ı���
			getBillCardPanel().stopEditing();
			String currentBodyTableCode = getBillCardPanel()
					.getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
					.getBillData().getBodyValueVOs(currentBodyTableCode,
							ReimRulerVO.class.getName());
			String pk_billtype = getSelectedNodeCode();
			String pk_org = getPkOrg();
			ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;
	
			// ��鱨����׼��ֵ�Ƿ���ȷ�����к��Ŀ�������֡�����Ϊ��
			ReimRuleUtil.checkReimRules(reimRuleVOs,pk_billtype,pk_group,pk_org,centControlItem);
			//����ʱ,�ȴ�ԭ��׼����ȡ�������ֵ��Ȼ�󱣴��׼��Ȼ��д�����ֵ
			List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(getSelectedNodeCode());
			List<ReimRulerVO> returnVos;
			if(vos==null){
				//��ԭ��׼Ϊ�գ���ֱ�ӱ���
				returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(pk_billtype, 
						pk_group,pk_org,reimRuleVOs);
			}
			else{
				List<SuperVO> vos1 = new ArrayList<SuperVO>();
				StringBuilder cents = new StringBuilder();
				for(SuperVO vo:vos){
					if(!cents.toString().contains((String) vo.getAttributeValue(centControlItem))){
						vos1.add(vo);
						cents.append((String) vo.getAttributeValue(centControlItem));
					}
				}
				NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(pk_billtype, 
						pk_group,pk_org,reimRuleVOs);
				returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).
						saveControlItem(centControlItem,pk_billtype, pk_group,
						pk_org,vos1.toArray(new ReimRulerVO[0]));
			}
			List<SuperVO> list = new ArrayList<SuperVO>();
			list.addAll(returnVos);
			ReimRuleUtil.putRule(pk_billtype, list);
		}
		else if(pageStatus==STATUS_CONFIG)
		{
			getBillCardPanel().stopEditing();
			String currentBodyTableCode = getBillCardPanel()
					.getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
					.getBillData().getBodyValueVOs(currentBodyTableCode,
							ReimRuleDimVO.class.getName());
			String pk_billtype = getSelectedNodeCode();
			String pk_org = getPkOrg();
			ReimRuleDimVO[] reimDimVOs = (ReimRuleDimVO[]) bodyValueVOs;
	
			// ���ά��ֵ�������뵥�����͡���֯���Ӧ��
			ReimRuleUtil.checkReimDims(reimDimVOs,pk_billtype,pk_group,pk_org);
			//����
			List<ReimRuleDimVO> returnVos = NCLocator.getInstance().lookup(
					IReimTypeService.class).saveReimDim(pk_billtype, pk_group,pk_org,
							reimDimVOs);
			List<SuperVO> list = new ArrayList<SuperVO>();
			list.addAll(returnVos);
			ReimRuleUtil.putDim(pk_billtype, list);
//			ReimRuleUtil.getTemplateBillDataMap().put(pk_billtype, null);
		}
		else if(pageStatus==STATUS_CONTROL)
		{
			getBillCardPanel().stopEditing();
			String currentBodyTableCode = getBillCardPanel()
					.getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
					.getBillData().getBodyValueVOs(currentBodyTableCode,
							ReimRulerVO.class.getName());
			String pk_billtype = getSelectedNodeCode();
			String pk_org = getPkOrg();
			ReimRulerVO[] controlVOs = (ReimRulerVO[]) bodyValueVOs;
			if(centControlItem.equalsIgnoreCase(ReimRulerVO.PK_EXPENSETYPE))
				ReimRuleUtil.addReimRules(controlVOs);
			List<ReimRulerVO> returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).
					saveControlItem(centControlItem,pk_billtype, pk_group,
					pk_org,controlVOs);
			List<SuperVO> list = new ArrayList<SuperVO>();
			list.addAll(returnVos);
			ReimRuleUtil.putRule(pk_billtype, list);
		}
		getRefOrg().setEnabled(true);
		setPageStatus(STATUS_BROWSE);
		showHintMessage("����ɹ�");
		StatusBarMsgCleaner.getInstance().messageAdded(this);
	}

	private String getPkOrg() {
		if(getRefOrg().getRefPK()!=null)
			return getRefOrg().getRefPK();
		else
			return "~";
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
				btnConfig.setEnabled(true);
				btnControl.setEnabled(true);
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
		case 2: {// config
			btnSave.setEnabled(true);
			btnAdd.setEnabled(true);
			btnDel.setEnabled(true);
			btnCancel.setEnabled(true);
			
			btnCopy.setEnabled(true);

			btnDown.setEnabled(true);
			btnBottom.setEnabled(true);
			btnTop.setEnabled(true);
			btnUp.setEnabled(true);

			break;
		}
		case 3: {//control
			btnSave.setEnabled(true);
			btnCancel.setEnabled(true);

			break;
		}
		case 4: {// ȫ���û�
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
	 * ���ð�ť״̬
	 * 
	 * @param status
	 */
	private void updateButtonStatus(Integer status) {
		// ���ð�ť
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
			DjLXVO data = (DjLXVO) node2.getData();
			String djlxbm = data.getDjlxbm();
			return djlxbm;
//			BilltypeVO data = (BilltypeVO) node2.getData();
//			String pk_billtypecode = data.getPk_billtypecode();
//			return pk_billtypecode;
		}
	}

	private void refreshUI() {
		switch (pageStatus) {
		//���״̬���Ҳ���ʾ������棬װ�ر�����׼����
		case 0: { 
			getSplitPane().setRightComponent(getBillCardPanel());
			refreshTemplate();
			getBillCardPanel().setEnabled(false);
			getBillTypeTree().setEnabled(true);
			getTreePanel().setEnabled(true);
			List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(getSelectedNodeCode());
			//����չʾҳ��
			if (vos != null && getBillCardPanel().getBodyItems()!= null) {
//				CombineTableUI tableUI=new CombineTableUI();
//				tableUI.process(vos,getBillCardPanel().getBodyItems());
//				getBillCardPanel().getBodyPanel().getTable().setUI(tableUI);
				getBillTypeTree().setEnabled(true);
				getTreePanel().setEnabled(true);
				try{
				getBillCardPanel().getBillData().setBodyValueVO(
						vos.toArray(new SuperVO[] {}));
				}catch(Exception e){
					handleException(e);
				}
				if(getBillCardPanel().getBillModel()!=null){
					getBillCardPanel().getBillModel().execLoadFormula();
					getBillCardPanel().getBillModel().loadLoadRelationItemValue();
				}
			} else {
				getBillCardPanel().getBillData().setBodyValueVO(null);
			}
			break;
		}
		//�޸�״̬����Ϊֻ�������̬�޸İ�ť�ſ��ã�����ֻ��Ҫ������
		case 1: {
			getBillCardPanel().setEnabled(true);
			getBillTypeTree().setEnabled(false);
			getTreePanel().setEnabled(false);
			break;
		}
		//����״̬���Ҳ���ʾ���ý��棬���ر�����׼ά������
		case 2: {
			getSplitPane().setRightComponent(dimPanel.getBillCardPanel());
			dimPanel.setCellEditor(getSelectedNodeCode());
			dimPanel.getBillCardPanel().setPreferredSize(
					new java.awt.Dimension(298, 469));
			dimPanel.getBillCardPanel().setEnabled(true);
			getBillTypeTree().setEnabled(false);
			getTreePanel().setEnabled(false);
			List<SuperVO> vos = ReimRuleUtil.getDataMapDim().get(getSelectedNodeCode());
			//���δ����ά�ȣ�����г�ʼ��
			if(vos==null || vos.size()==0){
				if(vos==null)
					vos = new ArrayList<SuperVO>();
				try {
					List<ReimRuleDimVO> vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimDim("2631", "GLOBLE00000000000000", "~");
					for(ReimRuleDimVO dimvo:vodims){
						dimvo.setPk_billtype(getSelectedNodeCode());
						dimvo.setPk_group(pk_group);
						dimvo.setPk_org(getPkOrg());
						vos.add(dimvo);
					}
				}catch (BusinessException ex) {
					ExceptionHandler.consume(ex);
				}
			}
			dimPanel.setData(vos);
			break;
		}
		case 3: {
			//����״̬����Ϊֻ�������̬���ư�ť�ſ��ã����Բ���Ҫ�޸��Ҳ���棬ֻ��Ҫ������
			refreshTemplate();
			getBillCardPanel().setEnabled(true);
			getBillTypeTree().setEnabled(false);
			getTreePanel().setEnabled(false);
			break;
		}
		default:
			break;
		}
	}

	public UIRefPane getRefOrg() {
		if (ivjtOrg == null) {

			ivjtOrg = new UIRefPane();
			ivjtOrg.setName("pk_org");
			ivjtOrg.setRefNodeName("ҵ��Ԫ"); 
			ivjtOrg.setPreferredSize(new Dimension(200, ivjtOrg.getHeight()));	
			ivjtOrg.addValueChangedListener(this);
			ivjtOrg.getRefModel().setFilterPks(BXUiUtil.getPermissionOrgs(null));
			ivjtOrg.getUITextField().setShowMustInputHint(true);
		}
		return ivjtOrg;
	}

	private UIPanel getUIPanelOrg() {
		if (jPanelOrg == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			jLabel6 = new UILabel();
			jLabel6.setText("ҵ��Ԫ");//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UCMD1-000006")/* @res "������֯" */);
			jPanelOrg = new UIPanel();
			jPanelOrg.setLayout(flowLayout1);
			jPanelOrg.setName("jPanelOrg");
			jPanelOrg.add(jLabel6, null);
			jPanelOrg.add(getRefOrg(), null);
			jPanelOrg.setSize(639, 363);
		}
		return jPanelOrg;
	}

	//��֯���ı�ʱ������ֻ�����̬����
	@Override
	public void valueChanged(ValueChangedEvent event) {
		getBillCardPanel().setEnabled(true);		
		getBillTypeTree().setEnabled(true);
		getTreePanel().setEnabled(true);
		initData();
		refreshUI();
		refreshButtonStatus();
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