package nc.ui.arap.bx.authorize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.INCAction;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.rbac.IRoleManageQuery;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.ui.arap.bx.ButtonUtil;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.model.PsndocDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.ml.NCLangRes;
import nc.ui.org.ref.DeptDefaultRefModel;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.MultiLangContext;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.uap.rbac.constant.IRoleConst;
import nc.vo.uap.rbac.role.RoleVO;

/**
 * ��д�����Ȩ��������
 *
 * @author chendya
 *
 */
@SuppressWarnings( { "deprecation", "serial" })
public class AuthorizeUI extends ToftPanel {

	/**
	 * ��֯���
	 */
	OrgPanel orgPanel;

	UISplitPane hSplitPane;
	UISplitPane vSplitPane;

	/**
	 * ҵ��Ա�б����
	 */
	BillListPanel billListPanel;

	/**
	 * ���ſ�Ƭ����
	 */
	BillCardPanel billCardPanel;

	/**
	 * ��ɫ��
	 */
	CheckBoxTree billTree;
	UIScrollPane treeScrollPane;

	/**
	 * �޸İ�ť
	 */
	ButtonObject btnEdit;

	/**
	 * ���а�ť
	 */
	ButtonObject btnAddLine;

	/**
	 * ɾ�а�ť
	 */
	ButtonObject btnDelLine;

	/**
	 * ���水ť
	 */
	ButtonObject btnSave;
	/**
	 * ȡ����ť
	 */
	ButtonObject btnCancel;

	public AuthorizeUI() {
		super();
		initalize();
	}
	
	@Override
	public void init() {
		super.init();
		//���ݳ�ʼ̬
		updateBillStatus(IAuthorizeUIConst.STATUS_INIT);
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0005")/*@res "��Ȩ��������"*/;
	}

	private void initalize() {

		// ��֯���
		add(getOrgPanel(), BorderLayout.NORTH);

		add(getHSplitPane(), BorderLayout.CENTER);

		// ����Ĭ��ֵ
		setDefaultValue();
		
		// ע�����
		initListener();

		// ������ɫ��
		createBillTree();

		// ��ʼ������ģ��
		initRefPane();

	}

	@Override
	public ButtonObject[] getButtons() {
		return getInitButtons();
	}

	@Override
	protected void postInit() {
		super.postInit();
		initActions();
	}

	/**
	 * ����ģ�ͳ�ʼ��
	 */
	private void initRefPane() {

		// ��Ա֧�ֿ���֯�������ѡ
		((UIRefPane) getBillListPanel().getHeadBillModel().getItemByKey(
				"pk_user").getComponent()).setMultiCorpRef(true);
		((UIRefPane) getBillListPanel().getHeadBillModel().getItemByKey(
				"pk_user").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane) getBillListPanel().getHeadBillModel().getItemByKey(
				"pk_user").getComponent()).setTreeGridNodeMultiSelected(true);

		// ����֧�ֶ�ѡ
		((UIRefPane) getBillCardPanel().getHeadItem("pk_deptdoc")
				.getComponent()).setMultiCorpRef(true);
		((UIRefPane) getBillCardPanel().getHeadItem("pk_deptdoc")
				.getComponent()).setMultiOrgSelected(true);
		((UIRefPane) getBillCardPanel().getHeadItem("pk_deptdoc")
				.getComponent()).setMultiSelectedEnabled(true);

	}

	/**
	 * ע�����
	 */
	private void initListener() {
		// ҵ��Ա�༭ǰ�¼�
		((UIRefPane) getBillListPanel().getHeadBillModel().getItemByKey("pk_user").getComponent())
				.addRefEditListener(new UserEditBeforeListener());
		
		// ҵ��Ա�༭���¼�
		((UIRefPane) getBillListPanel().getHeadBillModel().getItemByKey("pk_user").getComponent())
				.addValueChangedListener(new ValueChangedAdapter());
	}

	/**
	 * ҵ��Ա���ձ༭ǰ�¼�������
	 * 
	 * @author shengqy
	 * 
	 */
	final class UserEditBeforeListener implements RefEditListener {

		@Override
		public boolean beforeEdit(RefEditEvent event) {
			// ����Ա����ҵ��Ԫ��ֵ
			String pk_org = getOrgPanel().getRefpaneOrg().getRefPK();
			if (pk_org == null) {
				pk_org = ErUiUtil.getBXDefaultOrgUnit();
			}
			((UIRefPane) event.getSource()).setPk_org(pk_org);
			return true;
		}
		
	}
	
	/**
	 * ����ģ�ͱ༭���¼�������
	 *
	 * @author chendya
	 *
	 */
	final class ValueChangedAdapter implements ValueChangedListener {
		@Override
		public void valueChanged(ValueChangedEvent event) {
			afterEdit((UIRefPane) event.getSource());
		}
	}

	/**
	 *
	 * ���ձ༭���¼�
	 *
	 * @author chendya
	 *
	 * @param refPane
	 */
	private void afterEdit(UIRefPane refPane) {

		if (refPane.getRefModel() instanceof FinanceOrgDefaultRefTreeModel) {
			// ������֯�༭���¼�
			afterEditOrg();

		} else if (refPane.getRefModel() instanceof PsndocDefaultRefModel) {
			// ҵ��Ա�༭���¼�
			afterEditPsndoc(refPane);

		} else if (refPane.getRefModel() instanceof DeptDefaultRefModel) {
			// ���Ų��ձ༭���¼�
		}
	}

	/**
	 * ������֯�༭���¼�
	 *
	 * @author chendya
	 */
	private void afterEditOrg() {
		createBillTree();
	}

	/**
	 * ҵ��Ա�༭���¼�
	 *
	 * @author chendya
	 *
	 * @param refPane
	 */
	private void afterEditPsndoc(UIRefPane refPane) {
		// ��Ա���ձ༭���¼�
		String[] pk_users = refPane.getRefPKs();

		// ������ʼ��
		final int startRow = getBillListPanel().getHeadBillModel().getEditRow();
		if (pk_users != null && pk_users.length > 0) {
			// �������ѡ���VOs
			SqdlrVO[] vos = createSelectedUserVOs(pk_users);

			// �����Ѿ����ڵ�VOs
			SqdlrVO[] bodyVOs = (SqdlrVO[]) getBillListPanel()
					.getHeadBillModel()
					.getBodyValueVOs(SqdlrVO.class.getName());
			Map<String, SqdlrVO> map = new HashMap<String, SqdlrVO>();
			if (bodyVOs != null && bodyVOs.length > 0) {
				for (int i = 0; i < bodyVOs.length; i++) {
					final String pk_user = bodyVOs[i].getPk_user();
					if (!map.containsKey(pk_user)) {
						map.put(pk_user, bodyVOs[i]);
					}
				}
			}
			// ���˺�ʣ���VO
			SqdlrVO[] leftVOs = filterExistsVOs(map, vos);
			if (vos != null && vos.length > 0
					&& (leftVOs == null || leftVOs.length == 0)) {
				getBillListPanel().getHeadBillModel().delLine(
						new int[] { getBillListPanel().getHeadBillModel()
								.getEditRow() });
				getBillListPanel().getHeadBillModel().fireTableDataChanged();
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0006")/*@res "�Ѿ�������ѡ���ҵ��Ա"*/);
				return;
			}
			if (leftVOs != null && leftVOs.length > 0) {
				for (int i = 0; i < leftVOs.length; i++) {
					int row = startRow + i;
					getBillListPanel().getHeadBillModel().insertRow(row);
					getBillListPanel().getHeadBillModel().setRowState(row,
							BillModel.SELECTED);
				}
				getBillListPanel().getHeadBillModel()
						.setBodyRowObjectByMetaData(leftVOs, startRow);
			}
		}
	}

	/**
	 * ���˵��Ѿ�ѡ�����Ա
	 *
	 * @param map
	 * @param vos
	 */
	private SqdlrVO[] filterExistsVOs(Map<String, SqdlrVO> map, SqdlrVO[] vos) {
		if (map == null || map.size() == 0) {
			return vos;
		}
		if (vos == null || vos.length == 0) {
			return null;
		}
		List<SqdlrVO> voList = new ArrayList<SqdlrVO>();
		for (int i = 0; i < vos.length; i++) {
			final String pk_user = vos[i].getPk_user();
			if (map.containsKey(pk_user)) {
				continue;
			}
			voList.add(vos[i]);
		}
		return voList.toArray(new SqdlrVO[0]);
	}

	/**
	 * �����û�PKs����VOs
	 *
	 * @param pk_users
	 * @return
	 */
	private SqdlrVO[] createSelectedUserVOs(String[] pk_users) {
		if (pk_users == null || pk_users.length == 0) {
			return null;
		}
		List<SqdlrVO> voList = new ArrayList<SqdlrVO>();
		for (int i = 0; i < pk_users.length; i++) {
			SqdlrVO vo = new SqdlrVO();
			vo.setPk_user(pk_users[i]);
			vo.setPk_org(getPsnPk_org(pk_users[i]));
			voList.add(vo);
		}
		return voList.toArray(new SqdlrVO[0]);
	}
	
	/**
	 * ����BXUiUtil.getPsnPk_org();ԭ�򣺻�������е����⣬ʼ���õ�ǰ��¼�û�pk�����档�޷�������Ա���档
	 * @param pk_psn����Աpk
	 * @return:��Ա������֯
	 */
	private String getPsnPk_org(String pk_psndoc){
		if (StringUtil.isEmpty(pk_psndoc)) {
			return null;
		}
		
		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		String pk_org = (String) instance.getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + ErUiUtil.getPK_group());
		if (pk_org == null) {
			try {
				PsndocVO[] persons = NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(new String[] { pk_psndoc },
						new String[] { PsndocVO.PK_ORG ,PsndocVO.PK_GROUP});
				// ��Ա������֯
				pk_org = persons[0].getPk_org();
				instance.putClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + ErUiUtil.getPK_group(), pk_org);
				instance.putClientCache(PsnVoCall.GROUP_PK_ + pk_psndoc + ErUiUtil.getPK_group(), persons[0].getPk_group());
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
		}
		return pk_org;
	}
	
	/**
	 * ɾ���б�����ͷ����
	 */
	public void delNullLines(int editRow) {
		BillItem[] bodyItems = getBillListPanel().getHeadBillModel()
				.getBodyItems();
		int rows = getBillListPanel().getHeadBillModel().getRowCount();
		List<Integer> delLineList = new ArrayList<Integer>();
		for (int i = 0; i < rows; i++) {
			boolean isNull = true;
			for (int j = 0; j < bodyItems.length; j++) {
				Object value = getBillListPanel().getHeadBillModel()
						.getValueAt(i, bodyItems[j].getKey());
				if (value != null && value.toString().trim().length() > 0) {
					isNull = false;
				}
			}
			if (isNull && (editRow != i)) {
				delLineList.add(i);
			}
		}
		if (delLineList.size() == 0) {
			return;
		}
		int[] delLines = new int[delLineList.size()];
		for (Iterator<Integer> iterator = delLineList.iterator(); iterator
				.hasNext();) {
			Integer i = iterator.next();
			delLines[i] = i.intValue();
		}
		getBillListPanel().getHeadBillModel().delLine(delLines);
	}

	/**
	 * ���õ���״̬
	 *
	 * @param status
	 */
	protected void updateBillStatus(Integer status) {
		switch (status.intValue()) {
		case -1:
		case 0:
			// ��ʼ̬�����̬�������ɱ༭
			setBillEditable(false);
			break;
		case 1:
			// �༭̬
			setBillEditable(true);
			break;
		}
		// ���ư�ť״̬
		updateButtonStatus(status);
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

		// ����״̬
		switch (status.intValue()) {
		case -1:
			// ��ʼ̬
			setButtonEnable(getButtons(), false);
			break;
		case 1:
			// �༭̬
			setButtonEnable(getButtons(), true);
			setButtonEnable(getBtnEdit(), false);
			break;
		default:
			// ���̬
			setButtonEnable(getButtons(), false);
			setButtonEnable(getBtnEdit(), true);
			break;
		}
	}

	/**
	 * ���ݽ���(��Ƭ���б�)�Ƿ�����༭
	 *
	 * @param flag
	 */
	protected void setBillEditable(boolean flag) {
		getBillListPanel().setEnabled(flag);
		getBillListPanel().updateUI();
		getBillCardPanel().setEnabled(flag);
		getBillCardPanel().updateUI();
	}

	private void setButtonEnable(ButtonObject button, boolean flag) {
		setButtonEnable(new ButtonObject[] { button }, flag);
	}

	private void setButtonEnable(ButtonObject[] buttons, boolean flag) {
		if (buttons == null || buttons.length == 0) {
			return;
		}
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(flag);
		}
		updateButtons();
	}

	/**
	 * ����Ĭ��ֵ
	 */
	protected void setDefaultValue() {
		// Ĭ��ȡֵΪ���Ի��������õ�ҵ��Ԫ
		getOrgPanel().setPkValue(BXUiUtil.getDefaultOrgUnit());
	}

	// ��ʼ����ť��ݼ���
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
		setMenuActions(menuActions);
	}

	private ButtonObject[] getInitButtons() {
		return new ButtonObject[] { getBtnEdit() };
	}

	private ButtonObject[] getEditButtons() {
		return new ButtonObject[] { getBtnAddLine(), getBtnDelLine(),
				getBtnSave(), getBtnCancel() };
	}

	private ButtonObject[] getBrowseButtons() {
		return new ButtonObject[] { getBtnEdit() };
	}

	public ButtonObject[] getUEButtons(Integer status) {
		switch (status.intValue()) {
		case -1:
			// ��ʼ̬
			return getInitButtons();
		case 1:
			// �༭̬
			return getEditButtons();
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
			if (action.getValue(BTN_GROUP_NAME) == null) {
				appendGroupName(action);
			}
		}
	}

	List<String> BTN_GROUP_ADD_MODIFIY_DEL_CODES = Arrays
			.asList(new String[] { "AddLine", "DelLine"

			});

	List<String> BTN_GROUP_SAVE_CODES = Arrays
			.asList(new String[] { "Save"

			});
	List<String> BTN_GROUP_CANCEL_CODES = Arrays
			.asList(new String[] { "Cancel"

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
	 * ���ذ�ť��
	 */
	static String BTN_GROUP_RETURN = "BTN_GROUP_RETURN";

	static String BTN_GROUP_NAME = "BTN_GROUP_NAME";

	/**
	 * ��ť�������
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
	}

	public ButtonObject getBtnEdit() {
		if (btnEdit == null) {
			btnEdit = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000055")/*
																		 * @res
																		 * "�޸�"
																		 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000055")/*
													 * @res "�޸�"
													 */, 5, "Edit");
		}
		return btnEdit;
	}

	private ButtonObject getBtnAddLine() {
		if (btnAddLine == null) {
			btnAddLine = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000187")/*
																		 * @res
																		 * "����"
																		 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000187")/*
													 * @res "����"
													 */, 5, "AddLine");
		}
		return btnAddLine;
	}

	private ButtonObject getBtnDelLine() {
		if (btnDelLine == null) {
			btnDelLine = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000188")/*
																		 * @res
																		 * "ɾ��"
																		 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000188")/*
													 * @res "ɾ��"
													 */, 5, "DelLine");
		}
		return btnDelLine;
	}

	private ButtonObject getBtnSave() {
		if (btnSave == null) {
			btnSave = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000061")/*
																		 * @res
																		 * "����"
																		 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000061")/*
													 * @res "����"
													 */, 5, "Save");
		}
		return btnSave;
	}

	private ButtonObject getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000064")/*
																		 * @res
																		 * "ȡ��"
																		 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000064")/*
													 * @res "ȡ��"
													 */, 5, "Cancel");
		}
		return btnCancel;
	}

	public UISplitPane getHSplitPane() {
		if (hSplitPane == null) {
			hSplitPane = new UISplitPane(UISplitPane.HORIZONTAL_SPLIT);
			hSplitPane.setLeftComponent(getTreeScrollPane());
			hSplitPane.setRightComponent(getVSplitPane());
			hSplitPane
					.setDividerLocation(IAuthorizeUIConst.H_MINIMINIZED_POSITION);
			hSplitPane.setBorder(null);
		}
		return hSplitPane;
	}

	public UISplitPane getVSplitPane() {
		if (vSplitPane == null) {
			vSplitPane = new UISplitPane(UISplitPane.VERTICAL_SPLIT);
			vSplitPane.setTopComponent(getBillListPanel());
			vSplitPane.setBottomComponent(getBillCardPanel());
			vSplitPane
					.setDividerLocation(IAuthorizeUIConst.V_MINIMINIZED_POSITION);
			vSplitPane.setBorder(null);
		}
		return vSplitPane;
	}

	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel();
			billListPanel.loadTemplet(IAuthorizeUIConst.LIST_TEMPLATE_ID);
			// �����ѡ
			billListPanel.setMultiSelect(true);
		}
		return billListPanel;
	}

	public BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel();
			billCardPanel.loadTemplet(IAuthorizeUIConst.CARD_TEMPLATE_ID);
		}
		return billCardPanel;
	}

	private CheckTreeNode createNode(Object obj) {
		return new CheckTreeNode(obj);
	}

	/**
	 * ���ع�����ɫ����VO����
	 *
	 * @author chendya
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected RoleVO[] getRoleTreeVO() {
		RoleVO[] vos = null;
		try {
			// ����ȡҵ��Ԫ����Ľ�ɫ,���ȡ���ż�
			String pk_org = getOrgPanel().getPkValue();
			StringBuffer condtion = new StringBuffer();
			if (pk_org != null && pk_org.trim().length() > 0) {
				condtion.append("isnull(dr,0)=0 and (pk_org='" + pk_org
						+ "') and role_type=" + IRoleConst.BUSINESS_TYPE);
			} else {
				//���󶨵�ֻ�鼯�ż���ɫ(�˴��޶��鼯�ż���ɫ�󽫲��߱�ͬʱ��������֯����ɫ���ܣ���������Ժ�������������˴�����������)
				condtion.append("dr=0 and pk_org=pk_group and pk_group='"
						+ BXUiUtil.getPK_group() + "' and role_type="
						+ IRoleConst.BUSINESS_TYPE);
			}
			vos = NCLocator.getInstance().lookup(IRoleManageQuery.class)
					.queryRoleByWhereClause(condtion.toString());
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			MessageDialog
					.showErrorDlg(this, "no success", NCLangRes.getInstance()
							.getStrByID("101614", "UPP101614-000019")/*
																	 * @res
																	 * "û�гɹ��õ�������������"
																	 */);
		}
		if (vos != null && vos.length > 0) {
			Arrays.sort(vos, new Comparator() {
				public int compare(Object o1, Object o2) {
					String str1 = ((RoleVO) o1).getRole_code();
					String str2 = ((RoleVO) o2).getRole_code();
					if(str1!=null){
						return  str1.compareToIgnoreCase(str2);
					}
					return 0;
				}
			});
		}
		return vos;
	}

	/**
	 * ���ݽ�ɫVO��������
	 *
	 * @author chendya
	 *
	 * @param vos
	 */
	private void createBillTree(RoleVO[] vos) {
		if (vos == null || vos.length == 0) {
			getBillTree().setModel(null);
			return;
		}
		final String pk_org = getOrgPanel().getRefpaneOrg().getRefModel().getPkValue();
		
		// ���ڵ���ʾ����
		String rootName = "";
		if (pk_org != null && pk_org.trim().length() > 0) {
			rootName = BXUiUtil.getColValue(OrgVO.getDefaultTableName(),OrgVO.NAME, OrgVO.PK_ORG, pk_org);
		} else {
			rootName = BXUiUtil.getGroupName();
		}
		//���ڵ�
		CheckTreeNode root = createNode(rootName);
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				root.add(createNode(vos[i]));
			}
		}
		DefaultTreeModel model = new DefaultTreeModel(root);
		getBillTree().setModel(model);
		getBillTree().setCellEditor(new DefaultCellEditor(new UITextField()));
		getBillTree().setCellRenderer(new CheckTreeRenderer());
	}

	/**
	 * ������ɫ��
	 */
	private void createBillTree() {
		createBillTree(getRoleTreeVO());
	}

	public CheckBoxTree getBillTree() {
		if (billTree == null) {
			billTree = new CheckBoxTree();
			billTree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
		return billTree;
	}

	/**
	 * ������ѡ�еĽڵ�
	 *
	 * @return
	 */
	private List<CheckTreeNode> getCheckedTreeNode() {
		List<CheckTreeNode> list = new ArrayList<CheckTreeNode>();
		CheckTreeNode root = (CheckTreeNode) getBillTree().getModel().getRoot();
		list.addAll(getCheckedTreeNode(root));
		return list;
	}

	/**
	 * ����ѡ�еĽڵ㣬������ѡ�еĽڵ�
	 *
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<CheckTreeNode> getCheckedTreeNode(CheckTreeNode node) {
		List<CheckTreeNode> list = new ArrayList<CheckTreeNode>();
		if (node.getChildCount() > 0) {
			java.util.Enumeration<CheckTreeNode> children = node
					.preorderEnumeration();
			children.nextElement();
			while (children.hasMoreElements()) {
				list.addAll(getCheckedTreeNode((children.nextElement())));
			}
		}
		if (node.isChecked()&& !node.isRoot()) {
			list.add(node);
		}
		return list;
	}

	private void setBillData(List<SqdlrVO> vos) {

		if (vos == null || vos.size() == 0) {
			return;
		}
		// 1.ҵ��Ա
		List<SqdlrVO> operatorVOList = new ArrayList<SqdlrVO>();

		// 2.����
		List<String> pk_depts = new ArrayList<String>();

		// 3.������
		boolean isAgentSelfDept = false;

		// 4.���в���
		boolean isAgentAllDept = false;

		for (Iterator<SqdlrVO> iterator = vos.iterator(); iterator.hasNext();) {
			SqdlrVO vo = iterator.next();
			final String keyword = vo.getKeyword().trim();
			if (ISqdlrKeyword.KEYWORD_BUSIUSER.equals(keyword)) {
				operatorVOList.add(vo);
			} else if (ISqdlrKeyword.KEYWORD_PK_DEPTDOC.equals(keyword)) {
				pk_depts.add(vo.getPk_user());
			} else if (ISqdlrKeyword.KEYWORD_ISSAMEDEPT.equals(keyword)) {
				isAgentSelfDept = true;
			} else if (ISqdlrKeyword.KEYWORD_ISALL.equals(keyword)) {
				isAgentAllDept = true;
			}
		}
		// �����б�
		setBillListData(operatorVOList.toArray(new SqdlrVO[0]));
		getBillListPanel().getHeadBillModel().loadLoadRelationItemValue();

		// ���ÿ�Ƭ
		setBillCardData(pk_depts.toArray(new String[0]), isAgentSelfDept,
				isAgentAllDept);
	}

	private void setBillListData(SqdlrVO[] vos) {
		getBillListPanel().getHeadBillModel().setBodyDataVO(vos);
	}

	private void setBillCardData(String[] pk_depts, boolean isAgentSelfDept,
			boolean isAgentAllDept) {
		((UIRefPane) getBillCardPanel().getHeadItem("pk_deptdoc")
				.getComponent()).setPKs(pk_depts);
		((UICheckBox) getBillCardPanel().getHeadItem("issamedept")
				.getComponent()).setSelected(isAgentSelfDept);
		((UICheckBox) getBillCardPanel().getHeadItem("isall").getComponent())
				.setSelected(isAgentAllDept);
	}

	/**
	 * key=��ɫID��value= List<SqdlrVO>
	 */
	Map<String, List<SqdlrVO>> bufferData;

	public Map<String, List<SqdlrVO>> getVOCache() {
		if (bufferData == null) {
			bufferData = new java.util.HashMap<String, List<SqdlrVO>>();
		}
		return bufferData;
	}

	public void setVOCache(Map<String, List<SqdlrVO>> map) {
		getVOCache().clear();
		bufferData = map;
	}

	/**
	 * ���ؽ�ɫ��ص�VO
	 *
	 * @param node
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private List<SqdlrVO> getRoleRelationVOs(CheckTreeNode node)
			throws BusinessException {
		RoleVO value = (RoleVO) node.getUserObject();
		final String pk_role = value.getPk_role();
		if (!getVOCache().containsKey(pk_role)) {
			List<SqdlrVO> values = (List<SqdlrVO>) NCLocator.getInstance()
					.lookup(IUAPQueryBS.class).retrieveByClause(SqdlrVO.class,
							"pk_roler='" + pk_role + "'");
			if (values != null && values.size() > 0) {
				getVOCache().put(pk_role, values);
			}
		}
		return getVOCache().get(pk_role);

	}

	public UIScrollPane getTreeScrollPane() {
		if (treeScrollPane == null) {
			treeScrollPane = new UIScrollPane();
			treeScrollPane.setViewportView(getBillTree());
		}
		return treeScrollPane;
	}

	public OrgPanel getOrgPanel() {
		if (orgPanel == null) {
			orgPanel = new OrgPanel();
		}
		return orgPanel;
	}

	@Override
	public void onButtonClicked(ButtonObject bo) {
		showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0007",null,new String[]{bo.getName()})/*@res "����"*/ + "...");
		try {
			if (bo == getBtnEdit()) {
				onEdit();
			} else if (bo == getBtnAddLine()) {
				onLineAdd();
			} else if (bo == getBtnDelLine()) {
				onLineDel();
			} else if (bo == getBtnSave()) {
				onSave();
			} else if (bo == getBtnCancel()) {
				onCancl();
			}
		} catch (Exception e) {
			handleException(bo, e);
		}
		
		showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0008" ,null,new String[]{bo.getName()})/*@res "�ɹ�"*/);
	}

	/**
	 * ͳһ�����쳣
	 *
	 * @author chendya
	 * @param e
	 */
	protected void handleException(ButtonObject bo, Exception ex) {
		ExceptionHandler.consume(ex);
		BXUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(
				ButtonUtil.MSG_TYPE_FAIL, bo), ex);
	}

	/**
	 * ͳһ�����쳣
	 *
	 * @author chendya
	 * @param e
	 */
	protected void handleException(Exception e) {
		ExceptionHandler.consume(e);
		BXUiUtil.showUif2DetailMessage(this, "", e);
	}

	protected void onEdit() throws BusinessException {
		updateBillStatus(IAuthorizeUIConst.STATUS_EDIT);
	}

	protected void onLineAdd() throws BusinessException {
		getBillListPanel().getHeadBillModel().addLine();
		getBillListPanel().getHeadBillModel().setEditRow(
				getBillListPanel().getHeadBillModel().getRowCount() - 1);
	}

	protected void onLineDel() throws BusinessException {
		int rowcount = getBillListPanel().getHeadBillModel().getRowCount();
		if (rowcount <= 0) {
			return;
		}
		List<Integer> selectedRowList = new ArrayList<Integer>();
		for (int i = 0; i < rowcount; i++) {
			if (BillModel.SELECTED == getBillListPanel().getHeadBillModel()
					.getRowState(i)) {
				selectedRowList.add(new Integer(i));
			}
		}
		Integer[] rows = selectedRowList.toArray(new Integer[0]);
		if (rows == null || rows.length == 0) {
			// û��ѡ��ɾ�����У���Ĭ�ϴ����һ�п�ʼɾ��
			int tableSelectedRow = getBillListPanel().getHeadTable()
					.getSelectedRow();
			if (tableSelectedRow > -1) {
				getBillListPanel().getHeadBillModel().delLine(
						new int[] { tableSelectedRow });
			} else {
				getBillListPanel().getHeadBillModel().delLine(
						new int[] { rowcount - 1 });
			}
			return;
		}
		int[] delRows = new int[rows.length];
		for (int i = 0; i < rows.length; i++) {
			delRows[i] = rows[i];
		}
		getBillListPanel().getHeadBillModel().delLine(delRows);
	}

	/**
	 * �����Ƭ�б��������
	 */
	private void clearBillData() {
		clearBillListData();
		clearBillCardData();
	}

	/**
	 * ����б��������
	 */
	private void clearBillListData() {
		getBillListPanel().getHeadBillModel().clearBodyData();
	}

	/**
	 * �����Ƭ��������
	 */
	private void clearBillCardData() {
		getBillCardPanel().getBillData().clearViewData();
	}

	/**
	 * ���ع��˵��б����Ŀ��к��VO
	 *
	 * @author chendya
	 * @param vos
	 * @return
	 */
	private SqdlrVO[] filterNullPkUserLine() {
		List<SqdlrVO> voList = new ArrayList<SqdlrVO>();
		SqdlrVO[] vos = (SqdlrVO[]) getBillListPanel().getHeadBillModel()
				.getBodyValueVOs(SqdlrVO.class.getName());
		List<Integer> delRowList = new ArrayList<Integer>();
		for (int i = vos.length - 1; i >= 0; i--) {
			if (vos[i].getPk_user() == null
					|| vos[i].getPk_user().trim().length() == 0
					|| vos[i].getPk_org() == null
					|| vos[i].getPk_org().trim().length() == 0) {
				// ��¼Ҫɾ�����У�����ͳһ����
				delRowList.add(i);
			} else {
				voList.add(vos[i]);
			}
		}
		// ͳһ����Ҫɾ������
		if (delRowList.size() > 0) {
			int[] delRows = new int[delRowList.size()];
			int i = 0;
			for (Iterator<Integer> iterator = delRowList.iterator(); iterator
					.hasNext();) {
				Integer value = iterator.next();
				delRows[i++] = value.intValue();
			}
			getBillListPanel().getHeadBillModel().delLine(delRows);
		}
		return voList.toArray(new SqdlrVO[0]);
	}

	/**
	 * @author chendya ����ֹͣ�༭
	 */
	protected void stopBillEditing() {
		//�б�ֹͣ�༭
		getBillListPanel().setEnabled(false);
		if(getBillListPanel().getHeadTable().getCellEditor() != null){
			getBillListPanel().getHeadTable().getCellEditor().stopCellEditing();
		}
		
		// ��Ƭֹͣ�༭
		getBillCardPanel().stopEditing();
	}

	/**
	 * ����
	 *
	 * @throws BusinessException
	 */
	protected void onSave() throws BusinessException {

		stopBillEditing();

		List<SqdlrVO> preSaveVOList = new ArrayList<SqdlrVO>();

		List<CheckTreeNode> selectedNodeList = getCheckedTreeNode();

		if (selectedNodeList == null || selectedNodeList.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0009")/*@res "������ѡ��һ����ɫ"*/);
		}

		// ��ɫ����
		List<String> pk_roleList = new ArrayList<String>();
		for (Iterator<CheckTreeNode> iterator = selectedNodeList.iterator(); iterator.hasNext();) {
			CheckTreeNode node = iterator.next();
			final String pk_role = ((RoleVO) node.getUserObject()).getPk_role();
			pk_roleList.add(pk_role);

			// ���˿���
			// 1.ҵ��Ա
			SqdlrVO[] userVOs = filterNullPkUserLine();
			if (userVOs != null && userVOs.length > 0) {
				for (int i = 0; i < userVOs.length; i++) {
					userVOs[i].setType(ISqdlrKeyword.TYPE_AUTH_AGENT_NODE_TYPE);
					userVOs[i].setPk_roler(pk_role);
					userVOs[i].setKeyword(ISqdlrKeyword.KEYWORD_BUSIUSER);
					userVOs[i].setPk_group(ErUiUtil.getPK_group());
				}
				preSaveVOList.addAll(Arrays.asList(userVOs));
			}

			// 2.����
			String[] pk_depts = ((UIRefPane) getBillCardPanel().getHeadItem(
					"pk_deptdoc").getComponent()).getRefModel().getPkValues();
			if (pk_depts != null && pk_depts.length > 0) {
				SqdlrVO[] deptVOs = new SqdlrVO[pk_depts.length];
				for (int i = 0; i < pk_depts.length; i++) {
					deptVOs[i] = new SqdlrVO();
					deptVOs[i].setType(ISqdlrKeyword.TYPE_AUTH_AGENT_NODE_TYPE);
					deptVOs[i].setKeyword(ISqdlrKeyword.KEYWORD_PK_DEPTDOC);
					deptVOs[i].setPk_roler(pk_role);
					deptVOs[i].setPk_user(pk_depts[i]);
					deptVOs[i].setPk_group(ErUiUtil.getPK_group());
				}
				preSaveVOList.addAll(Arrays.asList(deptVOs));
			}
			// 3.��ͬ����(��������Ա���ڲ���)
			Boolean isAgentSameDept = (Boolean) getBillCardPanel().getHeadItem(
					"issamedept").getValueObject();
			if (isAgentSameDept != null && isAgentSameDept.booleanValue()) {
				SqdlrVO sameDeptVO = new SqdlrVO();
				sameDeptVO.setType(ISqdlrKeyword.TYPE_AUTH_AGENT_NODE_TYPE);
				sameDeptVO.setPk_roler(pk_role);
				sameDeptVO.setPk_user("true");
				sameDeptVO.setKeyword(ISqdlrKeyword.KEYWORD_ISSAMEDEPT);
				sameDeptVO.setPk_group(ErUiUtil.getPK_group());
				preSaveVOList.add(sameDeptVO);
			}

			// 4.���еĲ���
			Boolean isAgentAllDept = (Boolean) getBillCardPanel().getHeadItem(
					"isall").getValueObject();
			if (isAgentAllDept != null && isAgentAllDept.booleanValue()) {
				SqdlrVO allDeptVO = new SqdlrVO();
				allDeptVO.setType(ISqdlrKeyword.TYPE_AUTH_AGENT_NODE_TYPE);
				allDeptVO.setPk_roler(pk_role);
				allDeptVO.setKeyword(ISqdlrKeyword.KEYWORD_ISALL);
				allDeptVO.setPk_user("true");
				allDeptVO.setPk_group(ErUiUtil.getPK_group());
				preSaveVOList.add(allDeptVO);
			}
		}
		// ��ɾ����Ȼ������
		StringBuffer whereSql = new StringBuffer(" dr=0 ");
		StringBuilder inSQL = new StringBuilder();
		for (Iterator<String> iterator = pk_roleList.iterator(); iterator
				.hasNext();) {
			String pk_role = iterator.next();
			if (inSQL.length() > 0) {
				inSQL.append(",").append("'" + pk_role + "'");
			} else {
				inSQL.append("'" + pk_role + "'");
			}
		}
		if (inSQL.length() > 0) {
			whereSql.append(" and pk_roler in ").append("(").append(inSQL)
					.append(")");
		}

		// ����
		IBXBillPrivate service = NCLocator.getInstance().lookup(
				IBXBillPrivate.class);
		service.saveSqdlVO(preSaveVOList, whereSql.toString());

		// ������ѯ���ݣ����û���
		StringBuffer sql = new StringBuffer();
		String[] selectFileds = new String[] { SqdlrVO.PK_ROLER,
				SqdlrVO.PK_AUTHORIZE, SqdlrVO.PK_USER, SqdlrVO.PK_ORG,
				SqdlrVO.KEYWORD };
		sql.append(" select ");
		StringBuilder fields = new StringBuilder();
		for (int i = 0; i < selectFileds.length; i++) {
			if (fields.length() > 0) {
				fields.append(",").append(selectFileds[i]);
			} else {
				fields.append(selectFileds[i]);
			}
		}
		sql.append(fields.toString());
		sql.append(" FROM ").append(new SqdlrVO().getTableName());
		sql.append(" WHERE ").append(whereSql);
		setVOCache(service.querySqdlrVO(sql.toString()));

		// ������ͼ
		updateView();

		updateBillStatus(IAuthorizeUIConst.STATUS_BROWSE);

	}

	/**
	 * ��������
	 */
	protected void updateView() {
		Object valueObject = ((CheckTreeNode) getBillTree().getSelectionPath()
				.getLastPathComponent()).getUserObject();
		if (valueObject instanceof RoleVO) {
			RoleVO value = (RoleVO) valueObject;
			final String pk_role = value.getPk_role();
			setBillData(getVOCache().get(pk_role));
		}
	}

	/**
	 * ȡ��
	 *
	 * @throws BusinessException
	 */
	protected void onCancl() throws BusinessException {
		// ��յ�������
		clearBillData();
		updateBillStatus(IAuthorizeUIConst.STATUS_BROWSE);
	}

	@Override
	public boolean onClosing() {
		boolean result = true;
		if (getBillCardPanel().getBillData().getEnabled()|| getBillListPanel().getBillListData().isEnabled()) {
			/*
			 * @res "ȷ��Ҫ�˳���"
			 */
			if (nc.ui.pub.beans.MessageDialog.showYesNoDlg(this,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"expensepub_0", "02011002-0019")/* @res "��ʾ" */,
					(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000371"))) == UIDialog.ID_NO) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * ��֯���
	 *
	 * @author chendya
	 *
	 */
	final class OrgPanel extends UIPanel {

		/**
		 * ������֯��ǩ
		 */
		UILabel refTitle;

		/**
		 * ������֯����
		 */
		UIRefPane refpaneOrg;

		public void setPkValue(String pkValue) {
			getRefpaneOrg().setPK(pkValue);
		}

		public void setPkValues(String[] pkValues) {
			getRefpaneOrg().setPKs(pkValues);
		}

		public String[] getPkValues() {
			return getRefpaneOrg().getRefModel().getPkValues();
		}

		public String getPkValue() {
			return getRefpaneOrg().getRefModel().getPkValue();
		}

		public OrgPanel() {
			super();
			initialize();
		}

		private void initialize() {
			FlowLayout flowMgr = new FlowLayout();
			flowMgr.setAlignment(FlowLayout.LEFT);
			setLayout(flowMgr);
			add(getRefTitle());
			add(getRefpaneOrg());
		}

		public UILabel getRefTitle() {
			if (refTitle == null) {
				refTitle = new UILabel(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("common", "UCMD1-000006")/*
																			 * @res
																			 * "������֯"
																			 */);
			}
			return refTitle;
		}

		/**
		 * ���ز�����֯����
		 *
		 * @return
		 */
		public UIRefPane getRefpaneOrg() {
			if (refpaneOrg == null) {
				refpaneOrg = new UIRefPane();
				refpaneOrg.setRefNodeName("������֯");
				refpaneOrg.setMultiSelectedEnabled(true);
				refpaneOrg.setPreferredSize(new Dimension(200, refpaneOrg.getHeight()));
				// ��֧�ֶ༯�Ų���
				refpaneOrg.setMultiCorpRef(false);
				refpaneOrg.addValueChangedListener(new ValueChangedAdapter());
				refpaneOrg.setMultiSelectedEnabled(false);
				refpaneOrg.getRefModel().setFilterPks(BXUiUtil.getPermissionOrgs(null));
			}
			return refpaneOrg;
		}
	}

	/**
	 * ��ѡ������
	 *
	 * @author chendya
	 *
	 */
	private class CheckBoxTree extends UITree {

		public CheckBoxTree() {
			super();
			initialize();
		}

		private void initialize() {
			// ��ѡ�����
			addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					onBillTreeValueChanged((CheckTreeNode) e.getPath()
							.getLastPathComponent());
				}
			});
			// ���������
			addMouseListener(new MouseClickOnTreeAdapter(this));
		}
	}

	/**
	 * ���ڵ���ѡ���¼�
	 *
	 * @throws
	 */
	protected void onBillTreeValueChanged(CheckTreeNode node) {
		if (!node.isRoot()) {
			try {
				List<SqdlrVO> vos = getRoleRelationVOs(node);
				if (vos != null && vos.size() > 0) {
					// ���õ���(��Ƭ���б�)��������
					setBillData(vos);
				} else {
					clearBillData();
				}
			} catch (BusinessException e) {
				handleException(e);
			}
		}
		// ���õ���״̬
		updateBillStatus(IAuthorizeUIConst.STATUS_BROWSE);
	}

	/**
	 * ���ڵ�
	 *
	 * @author chendya
	 *
	 */
	final class CheckTreeNode extends DefaultMutableTreeNode {

		boolean isChecked;

		CheckTreeNode() {
			this(null);
		}

		CheckTreeNode(Object value) {
			this(value, true, false);
		}

		public CheckTreeNode(Object userObject, boolean allowsChildren,
				boolean isChecked) {
			super(userObject, allowsChildren);
			this.isChecked = isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public boolean isChecked() {
			return isChecked;
		}

		@Override
		public String toString() {
			if (getUserObject() instanceof RoleVO) {
				int intValue = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
				switch (intValue){
					case 1:return ((RoleVO) getUserObject()).getRole_name();
					case 2:return ((RoleVO) getUserObject()).getRole_name2()!=null?((RoleVO) getUserObject()).getRole_name2():((RoleVO) getUserObject()).getRole_name();
					case 3:return ((RoleVO) getUserObject()).getRole_name3()!=null?((RoleVO) getUserObject()).getRole_name3():((RoleVO) getUserObject()).getRole_name();
				}
			}
			return getUserObject().toString();
		}
	}

	/**
	 * ���ڵ�����¼������������⹫��
	 *
	 * @author chendya
	 *
	 */
	private class MouseClickOnTreeAdapter extends MouseAdapter {

		CheckBoxTree tree;

		MouseClickOnTreeAdapter(CheckBoxTree tree) {
			this.tree = tree;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = tree.getRowForLocation(e.getX(), e.getY());
			TreePath treePath = tree.getPathForRow(row);
			if (treePath == null)
				return;
			CheckTreeNode node = (CheckTreeNode) treePath
					.getLastPathComponent();
			if (node.isChecked()) {
				onNodeUnChecked(node);
			} else {
				onNodeChecked(node);
			}
			tree.repaint();
			if (getCheckedTreeNode() == null
					|| getCheckedTreeNode().size() == 0) {
				updateBillStatus(IAuthorizeUIConst.STATUS_INIT);
			} else {
				updateBillStatus(IAuthorizeUIConst.STATUS_BROWSE);
			}
		}

		/**
		 * ����ڵ�ѡ�� ����Ǹ��ڵ�ѡ�У�������Ҷ�ӽڵ��Զ�ѡ��; �����Ҷ�ӽڵ㣬���Ҷ�ӽڵ㼰�������ӽڵ�ѡ�У�
		 *
		 * @author chendya
		 * @param node
		 */
		@SuppressWarnings("unchecked")
		private void onNodeChecked(CheckTreeNode node) {
			node.setChecked(true);
			if (node.isRoot()) {
				java.util.Enumeration<CheckTreeNode> children = node
						.preorderEnumeration();
				children.nextElement();
				while (children.hasMoreElements()) {
					children.nextElement().setChecked(true);
				}
			} else {
				// ѡ�и��ڵ�
				if (!((CheckTreeNode) node.getParent()).isChecked()) {
					((CheckTreeNode) node.getParent()).setChecked(true);
				}
				if (node.getChildCount() > 0) {
					Enumeration e = node.preorderEnumeration();
					e.nextElement();
					while (e.hasMoreElements()) {
						CheckTreeNode childNode = (CheckTreeNode) e
								.nextElement();
						onNodeChecked(childNode);
					}
				}
			}
		}

		/**
		 * ����ڵ�ȡ��ѡ�� ����Ǹ��ڵ㣬��ȡ������Ҷ�ӽڵ��ѡ��;
		 * �����Ҷ�ӽڵ㣬��ȡ����Ҷ�ӽڵ㼰�������ӽڵ�ѡ��,�������ӽڵ��Լ���Ҷ�ӽڵ㶼ȡ��ѡ�к󣬸��ڵ�Ҳ�Զ�ȡ��ѡ��
		 *
		 * @author chendya
		 * @param node
		 */
		@SuppressWarnings("unchecked")
		private void onNodeUnChecked(CheckTreeNode node) {
			node.setChecked(false);
			if (node.isRoot()) {
				java.util.Enumeration<CheckTreeNode> children = node
						.preorderEnumeration();
				children.nextElement();
				while (children.hasMoreElements()) {
					children.nextElement().setChecked(false);
				}
			} else {
				if (node.getChildCount() > 0) {
					Enumeration e = node.preorderEnumeration();
					e.nextElement();
					while (e.hasMoreElements()) {
						CheckTreeNode childNode = (CheckTreeNode) e
								.nextElement();
						onNodeUnChecked(childNode);
					}
				}
				if (getCheckedTreeNode().size() == 0) {
					((CheckTreeNode) node.getRoot()).setChecked(false);
				}
			}
		}
	}

	/**
	 * ���ڵ���Ⱦ��
	 *
	 * @author chendya
	 *
	 */
	private class CheckTreeRenderer extends JPanel implements TreeCellRenderer {

		private static final long serialVersionUID = 1L;
		private final Color selColor = Color.YELLOW;
		protected JCheckBox box = new JCheckBox();

		public CheckTreeRenderer() {
			this.setOpaque(true);
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			CheckTreeNode node = (CheckTreeNode) value;

			if (selected) {
				this.setBackground(selColor);
				box.setBackground(selColor);
			} else {
				this.setBackground(tree.getBackground());
				box.setBackground(tree.getBackground());
			}
			box.setSelected(node.isChecked());
			box.setText(node.toString());
			return box;
		}
	}

}