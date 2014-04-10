package nc.ui.erm.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.INCAction;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.ui.arap.bx.ButtonUtil;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;
import nc.vo.erm.common.CommonSuperVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 * @author liansg 6.0 nc.ui.arap.common.CommonUI
 *
 *         实现简单的管理界面 1. 定义VO ,继承CommonSuperVO @see LoanControlVO 2. 实现卡片界面,
 *         主要实现方法 setVo, getVo @see LoanControlCard 3. 实现列表界面, 主要实现方法 getHeader,
 *         getHeaderColumns @see LoanControlList 4. 实现管理界面， 引用卡片列表界面 @see
 *         LoanControlMailPanel
 *
 * @see CommonSuperVO
 * @see CommonCard
 * @see CommonList
 * @see CommonModel
 * @see CommonModelListener
 * @see CommonUI
 */
public abstract class CommonUI extends ToftPanel implements CommonModelListener {

	private static final long serialVersionUID = 1L;

	private final ButtonObject btnAdd = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
					"UC001-0000108")/* @res "新增" */, nc.ui.ml.NCLangRes
					.getInstance().getStrByID("common", "UC001-0000108")/*
																		 * @res
																		 * "新增"
																		 */, 5,
			"Add");

	private final ButtonObject btnDelete = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
					"UC001-0000039")/* @res "删除" */, nc.ui.ml.NCLangRes
					.getInstance().getStrByID("common", "UC001-0000039")/*
																		 * @res
																		 * "删除"
																		 */, 5,
			"Delete");

	private final ButtonObject btnMod = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
					"UC001-0000045")/* @res "修改" */, nc.ui.ml.NCLangRes
					.getInstance().getStrByID("common", "UC001-0000045")/*
																		 * @res
																		 * "修改"
																		 */, 5,
			"Edit");

	private final ButtonObject btnRef = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
					"UC001-0000009")/* @res "刷新" */, nc.ui.ml.NCLangRes
					.getInstance().getStrByID("common", "UC001-0000009")/*
																		 * @res
																		 * "刷新"
																		 */, 5,
			"Refresh");

	protected final ButtonObject btnSave = new ButtonObject(
			nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
					"UC001-0000001")/* @res "保存" */, nc.ui.ml.NCLangRes
					.getInstance().getStrByID("common", "UC001-0000001")/*
																		 * @res
																		 * "保存"
																		 */, 5,
			"Save");

	private final ButtonObject btnCancel = new ButtonObject(nc.ui.ml.NCLangRes
			.getInstance().getStrByID("2006", "UC001-0000008")/* @res "取消" */,
			nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006", "UC001-0000008")/*
														 * @res "取消"
														 */, 5, "Cancel"); /*
																			 * -=notranslate
																			 * =
																			 * -
																			 */

	private final ButtonObject btnCard = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0023")/*@res "详细"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0023")/*@res "详细"*/, 5, "Card");

	private final ButtonObject btnList = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000038")/*@res "返回"*/,	nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000038")/*@res "返回"*/, 5, "Return");

	private final ButtonObject[] btnArrListStatus = new ButtonObject[] {
			btnAdd,btnMod,btnDelete, btnRef, btnCard };

	private final ButtonObject[] btnArrSaveEnable = new ButtonObject[] {
			btnSave, btnCancel, btnList };

	private CommonModel model;

	public CommonModel getModel() {
		if (model == null) {
			model = new CommonModel();
			model.setModeListener(this);
		}
		return model;
	}

	public void setModel(CommonModel model) {
		this.model = model;
	}

	public CommonUI() {

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void postInit() {
		setButtons(new ButtonObject[] { btnAdd,btnMod,btnDelete, btnRef,
				btnSave, btnCancel, btnCard, btnList });
		initActions();
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
		setMenuActions(menuActions);
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

	static List<String> BTN_GROUP_ADD_MODIFIY_DEL_CODES = Arrays
			.asList(new String[] { "Add", "Edit", "Delete"

			});

	static List<String> BTN_GROUP_REFRESH_CODES = Arrays
			.asList(new String[] { "Refresh"

			});

	static List<String> BTN_GROUP_DETAIL_CODES = Arrays
			.asList(new String[] { "Card",

			});

	static List<String> BTN_GROUP_SAVE_CODES = Arrays
			.asList(new String[] { "Save"

			});
	static List<String> BTN_GROUP_CANCEL_CODES = Arrays
			.asList(new String[] { "Cancel"

			});
	static List<String> BTN_GROUP_RETURN_CODES = Arrays
			.asList(new String[] { "Return" });

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
	 * 返回按钮组
	 */
	static String BTN_GROUP_RETURN = "BTN_GROUP_RETURN";

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

		// 刷新按钮组
		else if (BTN_GROUP_REFRESH_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_REFRESH);
		}

		// 详细按钮组
		else if (BTN_GROUP_DETAIL_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_DETAIL);
		}

		// 保存按钮组
		else if (BTN_GROUP_SAVE_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_SAVE);
		}

		// 取消按钮组
		else if (BTN_GROUP_CANCEL_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_CANCEL);
		}

		// 返回按钮组
		else if (BTN_GROUP_RETURN_CODES.contains(code)) {
			action.putValue(groupName, BTN_GROUP_RETURN);
		}
	}

	private void initData() throws BusinessException {
		IArapCommonPrivate impl = (IArapCommonPrivate) NCLocator.getInstance()
				.lookup(IArapCommonPrivate.class.getName());
		Collection<SuperVO> vos = impl
				.getVOs(getVoClass(), getWhereStr(), true);
		getModel().init();
		ArrayList<SuperVO> arrayList = new ArrayList<SuperVO>();
		arrayList.addAll(vos);
		getModel().setVos(arrayList);
	}

	public void initDataOrg() throws BusinessException {
		IArapCommonPrivate impl = (IArapCommonPrivate) NCLocator.getInstance()
				.lookup(IArapCommonPrivate.class.getName());
		Collection<SuperVO> vos = impl
				.getVOs(getVoClass(), getWhereStr(), true);
		getModel().init();
		ArrayList<SuperVO> arrayList = new ArrayList<SuperVO>();
		arrayList.addAll(vos);
		getModel().setVos(arrayList);
	}

	@Override
	public void init() {

		super.init();

		initialize();

		try {
			initData();
		} catch (BusinessException e) {
			BXUiUtil.showUif2DetailMessage(this, "", e);
		}

	}

	protected void saveData() throws BusinessException {

		IArapCommonPrivate impl = NCLocator.getInstance().lookup(IArapCommonPrivate.class);
		SuperVO vo = getCardPanel().getVO();

		vo.validate();
		if (getModel().getStatus() == CommonModel.STATUS_ADD) {
			vo = impl.save(vo);
			getModel().getVos().add(vo);
		} else {
			vo.setPrimaryKey(getModel().getSelectedvo().getPrimaryKey());
			vo.setAttributeValue("ts", getModel().getSelectedvo()
					.getAttributeValue("ts"));

			vo = impl.update(vo);
			getModel().updateVO(vo);
		}

		getModel().setSelectedvo(vo);
		getModel().setStatus(CommonModel.STATUS_CARD);
	}

	private void delData() throws BusinessException {

		if (!setSelectedVO()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000192")/*
																		 * @res
																		 * "请先选择一条记录!"
																		 */);
		}

		if (!showDeleteConfirmMessage())
			return;

		doBeforeDel();

		IArapCommonPrivate impl = (IArapCommonPrivate) NCLocator.getInstance()
				.lookup(IArapCommonPrivate.class.getName());
		SuperVO vo = getModel().getSelectedvo();
		impl.delete(vo);
		getModel().getVos().remove(vo);
		getModel().setSelectedvo(null);
		getModel().setStatus(CommonModel.STATUS_LIST);

		doAfterDel();
	}

	protected void doBeforeDel() {
	}

	protected void doAfterDel() {
	}

	protected boolean showDeleteConfirmMessage() {
		int i = showYesNoMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011", "UPP2011-000193")/* @res "是否确认删除该记录?" */);
		return i == UIDialog.ID_YES;
	}

	@Override
	public void onButtonClicked(ButtonObject bo) {
		try {
			if (bo == btnAdd) {
				doAdd();
			} else if (bo == btnDelete) {
				delData();
			} else if (bo == btnMod) {
				doMod();
			} else if (bo == btnCard) {
				setSelectedVO();
				getModel().setStatus(CommonModel.STATUS_CARD);
			} else if (bo == btnList) {
				getModel().setStatus(CommonModel.STATUS_LIST);
			} else if (bo == btnSave) {
				saveData();
			} else if (bo == btnCancel) {
				getModel().setStatus(CommonModel.STATUS_CARD);
			} else if (bo == btnRef) {
				initData();
			}
			showHintMessage(ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_SUCCESS,bo));
		} catch (Exception e) {
			BXUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(-1,bo), e);
		}
	}

	private void doAdd() {

		getModel().setSelectedvo(null);
		getModel().setStatus(CommonModel.STATUS_ADD);

	}

	private void doMod() throws BusinessException {

		if (getModel().getStatus() == CommonModel.STATUS_LIST) {
			if (!setSelectedVO()) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000192")/*
																			 * @res
																			 * "请先选择一条记录!"
																			 */);
			}
		} else {
			if (getModel().getSelectedvo() == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000192")/*
																			 * @res
																			 * "请先选择一条记录!"
																			 */);
			}
		}

		getModel().setStatus(CommonModel.STATUS_MOD);
	}

	private boolean setSelectedVO() throws BusinessException {

		String selectVoPk = getListPanel().getSelectVoPk();

		getModel().setSelectedvopk(selectVoPk);

		return selectVoPk != null;

	}

	private void initialize() {
		setName("CommonUI");
		setLayout(new java.awt.CardLayout());
		setSize(565, 337);

		CommonCard cardPanel = getCardPanel();
		cardPanel.setParentUI(this);
		CommonList listPanel = getListPanel();
		listPanel.setParentUI(this);

		cardPanel.initUI();
		listPanel.initUI();

		add(cardPanel, cardPanel.getName());
		add(listPanel, listPanel.getName());
	}

	public abstract CommonCard getCardPanel();

	public abstract CommonList getListPanel();

	public abstract Class getVoClass();

	public abstract String getWhereStr();

	public void updateStatus() {

		switch (getModel().getStatus()) {
		case CommonModel.STATUS_ADD:
		case CommonModel.STATUS_MOD:
			((java.awt.CardLayout) getLayout()).show(this, getCardPanel()
					.getName());
			getCardPanel().setVO(getModel().getSelectedvo());
			getCardPanel().setEditStatus(true);
			break;
		case CommonModel.STATUS_LIST:
			getListPanel().setData(getModel().getVos());
			((java.awt.CardLayout) getLayout()).show(this, getListPanel()
					.getName());
			break;
		case CommonModel.STATUS_CARD:
			((java.awt.CardLayout) getLayout()).show(this, getCardPanel()
					.getName());
			getCardPanel().setVO(getModel().getSelectedvo());
			getCardPanel().setEditStatus(false);
			break;
		default:
			break;
		}

		updateButtonStatus();
	}

	private void updateButtonStatus() {

		switch (getModel().getStatus()) {
		case CommonModel.STATUS_ADD:
		case CommonModel.STATUS_MOD:
			setSaveAble(false);
			break;
		case CommonModel.STATUS_LIST:
			setListStatus();
			break;
		case CommonModel.STATUS_CARD:
			setSaveAble(true);
			break;
		default:
			break;
		}

		updateButtons();
	}

	private void setListStatus() {
		btnAdd.setEnabled(true);
		btnRef.setEnabled(true);
		btnCard.setEnabled(true);
		btnList.setEnabled(false);
		// 由于acction不能设置visible，所以此处需要重置按钮
		setButtons(btnArrListStatus);
		initActions();
	}

	private void setSaveAble(boolean status) {

		setButtons(btnArrSaveEnable);
		initActions();
		btnAdd.setEnabled(status);
		btnMod.setEnabled(status);
		btnList.setEnabled(status);
		btnSave.setEnabled(status == true ? false : true);
		btnCancel.setEnabled(status == true ? false : true);

	}

	public void updateVos() {
		getListPanel().setData(getModel().getVos());
	}

}