package nc.ui.arap.bx;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.GroupAction;
import nc.funcnode.ui.action.INCAction;
import nc.funcnode.ui.action.MenuAction;
import nc.itf.uap.pf.IPFMetaModel;
import nc.ui.arap.engine.ExtBtnProxy;
import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.arap.eventagent.UIEventagent;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.util.ErmBtnRes;
import nc.ui.glpub.IUiPanel;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.FramePanel;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;

@SuppressWarnings({ "serial", "deprecation" })
public abstract class BxActionRuntime extends ToftPanel implements IActionRuntime, IUiPanel {

	/**
	 * 按钮代理类
	 */
	protected ExtBtnProxy btnProxy = null;

	private UIEventagent templetEventAgent = null;

	private String nodeCode = null;
	
	private static final String CONTRAST = "Contrast";

	/**
	 * 环境变量
	 */
	Map<String, Object> attrs = new HashMap<String, Object>();

	/**
	 * 当前页面状态（见ArapBillWorkPageConst）
	 */

	public nc.ui.glpub.IParent m_parent = null;

	public nc.ui.glpub.IUiPanel m_cardView = null;
	
	//FIXME
	private final FramePanel m_frame = null;

	private boolean actionStatus = true; // 动作执行状态

	public boolean isActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(boolean actionStatus) {
		this.actionStatus = actionStatus;
	}

	/**
	 * 客户端身份认证
	 * 
	 * @return
	 * @throws Exception
	 */
	protected void beginPressBtn(ButtonObject bo) {
		this.showHintMessage("");
		if (null != bo && ErmBtnRes.getBtnRes(bo.getCode()) != null && null != ErmBtnRes.getBtnRes(bo.getCode())[0]) {
			try {
				this.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("2008", ErmBtnRes.getBtnRes(bo.getCode())[0]));
			} catch (Exception e) {

			}
		}
	}

	public void endPressBtn(ButtonObject bo) {

		this.showHintMessage("");
		if (null != bo && ErmBtnRes.getBtnRes(bo.getCode()) != null && null != ErmBtnRes.getBtnRes(bo.getCode())[1]) {
			try {
				this.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("2008", ErmBtnRes.getBtnRes(bo.getCode())[1]));
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 实现类重写
	 */
	public Object getAttribute(String key) {
		return attrs.get(key);
	}

	/**
	 * 当前页标志 public static int LISTPAGE = 0;//列表界面 public static int CARDPAGE
	 * =1;//卡片界面 public static int ZYXPAGE = 2;//自由项界面 public static int VITPAGE =
	 * 3;//即时核销界面 public static int MAKEUPPAGE = 4;//补差界面 public static int
	 * VITINIT = 0;//即时核销初始状态
	 * 
	 * public static int WORKSTAT_EDIT = 3;//修改编辑状态 public static int
	 * WORKSTAT_NEW = 1;//新增编辑状态 public static int WORKSTAT_BROWSE =0;//浏览状态
	 */

	private int lastWorkPage = BillWorkPageConst.LISTPAGE;

	private int currentpage = BillWorkPageConst.LISTPAGE;

	/**
	 * 当前页面状态（见ArapBillWorkPageConst） public static int WORKSTAT_EDIT =
	 * 3;//修改编辑状态 public static int WORKSTAT_NEW = 1;//新增编辑状态 public static int
	 * WORKSTAT_BROWSE =0;//浏览状态
	 */
	private int lastPageState = BillWorkPageConst.WORKSTAT_BROWSE;

	private int pageState = BillWorkPageConst.WORKSTAT_BROWSE;

	public int getLastPageState() {
		return lastPageState;
	}

	public void setLastPageState(int lastPageState) {
		this.lastPageState = lastPageState;
	}

	public void setCurrentpage(int currentpage) {
		this.setLastWorkPage(this.getCurrWorkPage());
		this.currentpage = currentpage;
	}

	public void setCurrentPageStatus(int newstat) {
		
		this.setLastPageState(this.getCurrentPageStatus());
		pageState = newstat;
	}

	public int getCurrentPageStatus() {
		return pageState;
	}

	public int getCurrWorkPage() {
		return currentpage;
	}

	protected int getLastWorkPage() {
		return lastWorkPage;
	}

	protected void setLastWorkPage(int workPage) {
		lastWorkPage = workPage;
	}

	private String pageStatus = "";

	public String getPageStatus() {
		return pageStatus;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	/**
	 * 实现类重写
	 */
	public void setAttribute(String key, Object value) {
		attrs.put(key, value);
	}

	public ButtonObject[] getDjButtons() {
		ButtonObject[] ret = null;
		try {
			ret = getExtBtnProxy().getButtons() == null ? new ButtonObject[] {}
					: getExtBtnProxy().getButtons();

			// 期初单据删除与联查不相关按钮
			if (BXConstans.BXLR_QCCODE.equals(getNodeCode())) {
				final String[] removeBtnNames 
					= new String[] { "审批情况","预算执行情况","联查结算信息","联查报销标准","联查报销制度"/*-=notranslate=-*/
									,"联查资金计划","联查凭证", "联查借款单","联查往来单", "资金申请" };/*-=notranslate=-*/
				ret = removeSpecialButtons(ret, removeBtnNames);
			}
			//单据查询节点删除交易类型按钮
			if (BXConstans.BXBILL_QUERY.equals(getNodeCode())) {
				final String removeBtnName="交易类型";
				ret = removeSpecialButtons(ret, removeBtnName);
			}
			

			this.updateButtons();
			
		} catch (Exception e) {
			showErrorMessage(e);
		}
		return ret;
	}
	
	/**
	 * 删除按钮
	 * @author chendya
	 * @param ret
	 * @param removeBtnNames
	 */
	private ButtonObject[] removeSpecialButtons(ButtonObject[] ret,
			String[] removeBtnNames) {
		if (removeBtnNames != null && removeBtnNames.length > 0) {
			for (int i = 0; i < removeBtnNames.length; i++) {
				ret = removeSpecialButtons(ret, removeBtnNames[i]);
			}
		}
		return ret;
	}
	
	/**
	 * 从按钮中移除指定的按钮，比如借款单不需要冲销按钮
	 * 
	 * @author chendya
	 * @param buttons
	 *            传入的按钮
	 * @param btnCodes
	 *            指定按钮的编码
	 * @return 移除指定按钮后剩下的按钮
	 */
	public ButtonObject[] removeSpecialButtons(ButtonObject[] buttons,
			String btnCode) {
		ArrayList<ButtonObject> list = new ArrayList<ButtonObject>();
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].getChildren() != null) {
				// 递归从子按钮中移除指定的按钮
				buttons[i].setChildButtonGroup(removeSpecialButtons(buttons[i]
						.getChildButtonGroup(), btnCode));
			}
			final String buttonCode = buttons[i].getCode();
			if (!buttonCode.equals(btnCode) && !list.contains(buttons[i])) {
				list.add(buttons[i]);
			}
		}
		return list.toArray(new ButtonObject[0]);
	}

	@Override
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {

		boolean success = true;

		try {

			beforeOnButtonClicked(bo);

			if (bo instanceof ExtButtonObject)
				getExtBtnProxy().doAction((ExtButtonObject) bo);

		} catch (BusinessException e) {
			success = false;
			Log.getInstance(this.getClass()).error(e);
			this.showErrorMessage(e.getMessage());
			this.showHintMessage(e.getMessage());
		}

		afterOnButtonClicked(bo, success);

		this.refreshBtnStatus();
	}
	
//begin-- added by chendya 按钮菜单分组处理
	/**
	 * 缓存浏览态和编辑态按钮
	 */
	Map<String, ButtonObject[]> stateBtnMap = new HashMap<String, ButtonObject[]>();
	public static final String BTN_IN_NEW_STATE = "BTN_IN_NEW_STATE";
	public static final String BTN_IN_BROWSER_STATE = "BTN_IN_BROWSER_STATE";
	public static final String BTN_IN_EDIT_STATE = "BTN_IN_EDIT_STATE";
	
	//编辑态按钮
	protected final String[] EDIT_BUTTONS_NAMES = new String[]{CONTRAST,"Tempsave"/*暂存*/,"Save"/*保存*/,"Cancel"/*取消*/,"Ass","Document"};
	
	//新增态按钮
	protected final String[] NEW_BUTTONS_NAMES = new String[]{CONTRAST,"Tempsave"/*暂存*/,"Save"/*保存*/,"Cancel"/*取消*/};
	
	private Map<String, ButtonObject[]> getStateBtnMap() {
		return stateBtnMap;
	}
	
	/**
	 * 返回UE规范分组(新增，浏览、编辑态)按钮
	 * 
	 * @param status
	 * @return
	 */
	protected ButtonObject[] getUEDjButtons(int status) {
		switch (status) {
		// 新增
		case 1:
			return getNewBtns();
		// 编辑态
		case 3:
			return getEditableBtns();
		default:
			return getBrowseBtns();
		}
	}
	
	public static List<String> remove(List<String> list,String item){
		List<String> retList = new LinkedList<String>();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			if(item==null||item.equals(string)){
				continue;
			}
			retList.add(string);
		}
		return retList;
	}
	
	public static List<String> remove(String[] arrays,String item){
		List<String> retList = new LinkedList<String>();
		for (int i = 0; i < arrays.length; i++) {
			if(item!=null&&item.equals(arrays[i])){
				continue;
			}
			retList.add(arrays[i]);
		}
		return retList;
	}

	/**
	 * 返回浏览态按钮(卡片浏览，列表浏览)
	 * 
	 * @return
	 */
	private ButtonObject[] getBrowseBtns() {
		String key = BTN_IN_BROWSER_STATE+getCurrWorkPage();
		String djdl = ((BXBillMainPanel)this).getCache().getCurrentDjdl();
		//单据管理节点，浏览界面是可以冲借款的
		if(BXConstans.BXMNG_NODECODE.equals(getNodeCode())){
			//如果有选定行VO，以选定行VO的单据大类为准
			if(((BXBillMainPanel)this).getCache().getCurrentVO()!=null&&((BXBillMainPanel)this).getCache().getCurrentVO().getParentVO()!=null
					&&((BXBillMainPanel)this).getCache().getCurrentVO().getParentVO().getDjdl()!=null){
				djdl = ((BXBillMainPanel)this).getCache().getCurrentVO().getParentVO().getDjdl();
			}
			key = key+djdl;
		}
		if (getStateBtnMap().get(key) == null) {
			List<ButtonObject> list = new ArrayList<ButtonObject>();
			ButtonObject[] btns = getDjButtons();
			List<String> names = Arrays.asList(NEW_BUTTONS_NAMES);
//			if(BXConstans.BXMNG_NODECODE.equals(getNodeCode())&&BXConstans.BX_DJDL.equals(djdl)){
				//1.单据管理节点的借款单据移除冲借款按钮
				names = remove(names, CONTRAST);
//			}
			for (ButtonObject btn : btns) {
				if (!names.contains(btn.getCode())) {
					list.add(btn);
				}
			}
			getStateBtnMap().put(key,
					list.toArray(new ButtonObject[] {}));
		}
		return getStateBtnMap().get(key);
	}

	/**
	 * 返回新增态按钮
	 * 
	 * @return
	 */
	private ButtonObject[] getNewBtns() {
		final String djdl = ((BXBillMainPanel)this).getCache().getCurrentDjdl();
		if (getStateBtnMap().get(BTN_IN_NEW_STATE+djdl) == null) {
			List<ButtonObject> list = new ArrayList<ButtonObject>();
			ButtonObject[] btns = getDjButtons();
			List<String> names = Arrays.asList(NEW_BUTTONS_NAMES);
			//借款单据录入节点不可冲借款
			if(BXConstans.JK_DJDL.equals(djdl)){
				names = remove(names, CONTRAST);
			}
			for (ButtonObject btn : btns) {
				if (names.contains(btn.getCode())) {
					list.add(btn);
				}
			}
			getStateBtnMap().put(BTN_IN_NEW_STATE+djdl,list.toArray(new ButtonObject[] {}));
		}
		return getStateBtnMap().get(BTN_IN_NEW_STATE+djdl);
	}
	
	/**
	 * 返回编辑态按钮
	 * 
	 * @return
	 */
	private ButtonObject[] getEditableBtns() {
		BXBillMainPanel panel = ((BXBillMainPanel)this);
		String djdl = panel.getCache().getCurrentDjdl();
		String key = BTN_IN_EDIT_STATE + djdl;
		
		if (getStateBtnMap().get(key) == null) {
			List<ButtonObject> list = new ArrayList<ButtonObject>();
			ButtonObject[] btns = getDjButtons();
			List<String> names = Arrays.asList(EDIT_BUTTONS_NAMES);
			for (ButtonObject btn : btns) {
				if(BXConstans.JK_DJDL.equals(djdl)&&CONTRAST.equals(btn.getCode())){
					continue;
				}
				if (names.contains(btn.getCode())) {
					list.add(btn);
				}
			}
			getStateBtnMap().put(key,list.toArray(new ButtonObject[] {}));
		}
		return getStateBtnMap().get(key);
	}
//--end added by chendya按钮菜单分组处理

	public void refreshBtnStatus() {

		//卡片列表切换后重置按钮状态
		if (getCurrWorkPage() != getLastWorkPage()) {
			
		}
		
		setButtons(getUEDjButtons(getCurrentPageStatus()));

		//初始化按钮快捷键等
		initActions();
		
		try {
			getExtBtnProxy().updateStatus();
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e);
		}

//		updateButtons();
		
		setLastPageState(getCurrentPageStatus());
		setLastWorkPage(getCurrWorkPage());
		
		if(((BXBillMainPanel)this).getBxParam().getNodeOpenType()==BxParam.NodeOpenType_Link
				||((BXBillMainPanel)this).getBxParam().getNodeOpenType()==BxParam.NodeOpenType_Approve){
			try {
				hideUnVisibleButtons(getExtBtnProxy().getButtons());
			} catch (Exception e) {
				Log.getInstance(this.getClass()).error(e);
			}
		}
		//added by chendya 控制翻页按钮状态
		setPageButtonStatus();
		
		//控制返回按钮状态
		setReturnButtonStatus();
		
	}
	
	protected void setReturnButtonStatus(){
		BXBillMainPanel panel = ((BXBillMainPanel)this);
		if(getLastPageState()==BillWorkPageConst.WORKSTAT_BROWSE){
			panel.getCardToolbarPanel().setReturnActionStatus(true);
		}
		else{
			panel.getCardToolbarPanel().setReturnActionStatus(false);
		}
	}
	
	protected void setPageButtonStatus(){
		BXBillMainPanel panel = ((BXBillMainPanel)this);
		if(getLastPageState()==BillWorkPageConst.WORKSTAT_BROWSE){
			final int size = panel.getCache().getVoCache().size();
			if (size <= 1) {
				panel.getCardToolbarPanel().setAllActionStatus(false);
			} else {
				panel.getCardToolbarPanel().setAllActionStatus(true);
			}
		}else{
			panel.getCardToolbarPanel().setAllActionStatus(false);
		}
	}
	
	public void hideUnVisibleButtons(ButtonObject[] buttons){
		if(buttons==null||buttons.length==0){
			return;
		}
		ArrayList<ButtonObject> btnList = new ArrayList<ButtonObject>();
		for (int i = 0; i < buttons.length; i++) {
			if(buttons[i].isVisible()){
				btnList.add(buttons[i]);
			}
		}
		setButtons(btnList.toArray(new ButtonObject[0]));
	}

	public void refreshBtnStatus(String[] btncodes) {

		getExtBtnProxy().updateStatus(btncodes);

		this.updateButtons();
	}
	
	//初始化按钮快捷键等
	protected void initActions() {
		if(getFuncletContext()==null){
			return;
		}
		List<Action> menuActions = getMenuActions();
		for(Action menu:menuActions){
			if(menu instanceof MenuAction){
				MenuAction mAction = (MenuAction)menu;
				if(mAction.getChildCount()>0){
					Action[] subActions = mAction.getAllChild();
					for (int i = 0; i < subActions.length; i++) {
						initActions((AbstractNCAction)subActions[i]);
					}
				}
			}else if(menu instanceof AbstractNCAction){
				initActions((AbstractNCAction)menu);
			}
		}
		//分组
		createGroupActions(menuActions);
		menuActions = addSeparatorBetweenGroupBtn(menuActions);
		addSeparator4Print(menuActions);
		//添加分割栏
		setMenuActions(menuActions);
	}
	/**
	 * 添加分组
	 * @author chendya
	 * @param actions
	 */
	private void createGroupActions(List<Action> actions){
		for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
			Action action = iterator.next();
			if(action.getValue(BXConstans.BTN_GROUP_NAME)==null){
				appendGroupName(action);
			}
		}
	}
	
	/**
	 * 按钮添加组名
	 *@author chendya 
	 */
	private void appendGroupName(Action action){
		//按钮编码
		final String code = (String)action.getValue(INCAction.CODE);
		
		//按钮组名
		final String groupName = BXConstans.BTN_GROUP_NAME;
		
		//交易类型
		if(BXConstans.BTN_GROUP_DJLX_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_DJLX);
		}
		//增加按钮组
		else if(BXConstans.BTN_GROUP_ADD_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_ADD);
		}
		
		//冲借款按钮组
		else if(BXConstans.BTN_GROUP_CONTRAST_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_CONTRAST);
		}
		
		//保存按钮组
		else if(BXConstans.BTN_GROUP_SAVE_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_SAVE);
		}
		
		//取消按钮组
		else if(BXConstans.BTN_GROUP_CANCEL_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_CANCEL);
		}
		//查询按钮组
		else if(BXConstans.BTN_GROUP_QUERY_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_QUERY);
		}
		//审核按钮组
		else if(BXConstans.BTN_GROUP_APPROVE_ASS_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_APPROVE_ASS);
		}
		//全选，全消按钮组
		else if(BXConstans.BTN_GROUP_SELECT_CANCEL_ALL_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_SELECT_CANCEL_ALL);
		}
		//联查按钮组
		else if(BXConstans.BTN_GROUP_LINKQUERY_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_LINKQUERY);
		}
		//制单按钮组
		else if(BXConstans.BTN_GROUP_MAKEVOUCHER_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_MAKEVOUCHER);
		}
		//打印按钮组
		else if(BXConstans.BTN_GROUP_PRINT_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_PRINT);
		}
	}
	
	/**
	 * 按钮组与组之间添加分割Action
	 * @author chendya
	 */
	protected List<Action> addSeparatorBetweenGroupBtn(List<Action> list){
		List<Action> retList = new ArrayList<Action>();
		Action[] actions  = list.toArray(new Action[0]);
		for (int i = 0, j = i + 1; i < actions.length; i++, j++) {
			retList.add(actions[i]);
			if(j<=actions.length-1&&actions[i].getValue(BXConstans.BTN_GROUP_NAME)!=null
					&&!actions[i].getValue(BXConstans.BTN_GROUP_NAME).equals(actions[j].getValue(BXConstans.BTN_GROUP_NAME))){
				retList.add(new nc.funcnode.ui.action.SeparatorAction());
			}
		}
		return retList;
	}
	
	/**
	 * 正式打印按钮前添加分割线
	 * @param list
	 */
	private void addSeparator4Print(List<Action> list){
		//记录打印按钮位置
		int pos = 0;
		Action[] children = null;
		MenuAction printAction = null;
		for (Iterator<Action> iterator = list.iterator(); iterator.hasNext();) {
			Action action = iterator.next();
			final String code = (String)action.getValue(INCAction.CODE);
			if(code!=null&&code.equals("打印操作")){/*-=notranslate=-*/
				printAction = (MenuAction)action;
				children = printAction.getAllChild();
				//记录分割线加的位置
				int idx = 0;
				for (int i = 0; i < children.length; i++) {
					if("Officalprint".equals(children[i].getValue(INCAction.CODE))){
						idx = i;
						break;
					}
				}
				if(idx>0){
				//添加分割线
					printAction.addChildAction(idx, new nc.funcnode.ui.action.SeparatorAction());
				}
				children = printAction.getAllChild();
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
	
	private void initActions(AbstractNCAction action){
		ActionInfo info = ActionRegistry.getActionInfo(action.getCode());
		if(info!=null){
			action.setCode(info.getCode());
			action.putValue(Action.ACCELERATOR_KEY, info.getKeyStroke());
			action.putValue(Action.SHORT_DESCRIPTION, info.getShort_description());
			action.putValue(Action.SMALL_ICON, info.getIcon());
		}
	}

	/**
	 * 实现类重写
	 * 
	 * @throws BusinessException
	 */
	protected void beforeOnButtonClicked(nc.ui.pub.ButtonObject bo) throws BusinessException {

	}

	/**
	 * 实现类重写
	 */
	protected void afterOnButtonClicked(nc.ui.pub.ButtonObject bo, boolean success) {

	}

	protected ExtBtnProxy getExtBtnProxy() {
		if (null == btnProxy) {

			btnProxy = new ExtBtnProxy(this);
		}
		return btnProxy;
	}

	public void initBillCardPane(BillCardPanel card) {

	}

	public void initBillListPane(BillListPanel list) {

	}

	public UIEventagent getTempletEventAgent() {
		if (templetEventAgent == null) {
			templetEventAgent = new UIEventagent(this);
		}
		return templetEventAgent;
	}

	public void setTempletEventAgent(UIEventagent templetEventAgent) {
		this.templetEventAgent = templetEventAgent;
	}

	/**
	 * 执行入口节点方法
	 */
	public final Object invokeMethod(String methodName, Object... objs) throws BusinessException {

		Class[] cls = new Class[objs.length];

		for (int i = 0, size = objs.length; i < size; i++) {
			cls[i] = objs[i].getClass();
		}
		try {

			Method mtd = this.getClass().getMethod(methodName, cls);

			if (!Modifier.isPublic(mtd.getModifiers())) {

				throw new BusinessException("the method is not public");
			}
			return mtd.invoke(this, objs);

		} catch (Exception e) {

			Log.getInstance(this.getClass()).error(e);

			throw new BusinessException(e.getMessage());
		}

	}

	public void updateButtonStatus() {
		refreshBtnStatus();
	}

	public void addListener(Object objListener, Object objUserdata) {
	}

	public void removeListener(Object objListener, Object objUserdata) {
	}


	
	public String getParentNodeCode() {
	  if (getNodeCode()!= null) {
		try {			
			IPFMetaModel ipf = NCLocator.getInstance().getInstance().lookup(IPFMetaModel.class);
			String tempBilltype = ipf.getBilltypeByNodecode(getNodeCode());
			
			String parentBilltype = PfDataCache.getBillType(tempBilltype).getParentbilltype();
			return PfDataCache.getBillType(parentBilltype).getNodecode();
		} catch (Exception e) {
		}
	  }
	  return getNodeCode();
	} 


	public String getCurrentCorp() {
		return BXUiUtil.getPK_group();
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

}
