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
	 * ��ť������
	 */
	protected ExtBtnProxy btnProxy = null;

	private UIEventagent templetEventAgent = null;

	private String nodeCode = null;
	
	private static final String CONTRAST = "Contrast";

	/**
	 * ��������
	 */
	Map<String, Object> attrs = new HashMap<String, Object>();

	/**
	 * ��ǰҳ��״̬����ArapBillWorkPageConst��
	 */

	public nc.ui.glpub.IParent m_parent = null;

	public nc.ui.glpub.IUiPanel m_cardView = null;
	
	//FIXME
	private final FramePanel m_frame = null;

	private boolean actionStatus = true; // ����ִ��״̬

	public boolean isActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(boolean actionStatus) {
		this.actionStatus = actionStatus;
	}

	/**
	 * �ͻ��������֤
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
	 * ʵ������д
	 */
	public Object getAttribute(String key) {
		return attrs.get(key);
	}

	/**
	 * ��ǰҳ��־ public static int LISTPAGE = 0;//�б���� public static int CARDPAGE
	 * =1;//��Ƭ���� public static int ZYXPAGE = 2;//��������� public static int VITPAGE =
	 * 3;//��ʱ�������� public static int MAKEUPPAGE = 4;//������� public static int
	 * VITINIT = 0;//��ʱ������ʼ״̬
	 * 
	 * public static int WORKSTAT_EDIT = 3;//�޸ı༭״̬ public static int
	 * WORKSTAT_NEW = 1;//�����༭״̬ public static int WORKSTAT_BROWSE =0;//���״̬
	 */

	private int lastWorkPage = BillWorkPageConst.LISTPAGE;

	private int currentpage = BillWorkPageConst.LISTPAGE;

	/**
	 * ��ǰҳ��״̬����ArapBillWorkPageConst�� public static int WORKSTAT_EDIT =
	 * 3;//�޸ı༭״̬ public static int WORKSTAT_NEW = 1;//�����༭״̬ public static int
	 * WORKSTAT_BROWSE =0;//���״̬
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
	 * ʵ������д
	 */
	public void setAttribute(String key, Object value) {
		attrs.put(key, value);
	}

	public ButtonObject[] getDjButtons() {
		ButtonObject[] ret = null;
		try {
			ret = getExtBtnProxy().getButtons() == null ? new ButtonObject[] {}
					: getExtBtnProxy().getButtons();

			// �ڳ�����ɾ�������鲻��ذ�ť
			if (BXConstans.BXLR_QCCODE.equals(getNodeCode())) {
				final String[] removeBtnNames 
					= new String[] { "�������","Ԥ��ִ�����","���������Ϣ","���鱨����׼","���鱨���ƶ�"/*-=notranslate=-*/
									,"�����ʽ�ƻ�","����ƾ֤", "�����","����������", "�ʽ�����" };/*-=notranslate=-*/
				ret = removeSpecialButtons(ret, removeBtnNames);
			}
			//���ݲ�ѯ�ڵ�ɾ���������Ͱ�ť
			if (BXConstans.BXBILL_QUERY.equals(getNodeCode())) {
				final String removeBtnName="��������";
				ret = removeSpecialButtons(ret, removeBtnName);
			}
			

			this.updateButtons();
			
		} catch (Exception e) {
			showErrorMessage(e);
		}
		return ret;
	}
	
	/**
	 * ɾ����ť
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
	 * �Ӱ�ť���Ƴ�ָ���İ�ť�����������Ҫ������ť
	 * 
	 * @author chendya
	 * @param buttons
	 *            ����İ�ť
	 * @param btnCodes
	 *            ָ����ť�ı���
	 * @return �Ƴ�ָ����ť��ʣ�µİ�ť
	 */
	public ButtonObject[] removeSpecialButtons(ButtonObject[] buttons,
			String btnCode) {
		ArrayList<ButtonObject> list = new ArrayList<ButtonObject>();
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].getChildren() != null) {
				// �ݹ���Ӱ�ť���Ƴ�ָ���İ�ť
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
	
//begin-- added by chendya ��ť�˵����鴦��
	/**
	 * �������̬�ͱ༭̬��ť
	 */
	Map<String, ButtonObject[]> stateBtnMap = new HashMap<String, ButtonObject[]>();
	public static final String BTN_IN_NEW_STATE = "BTN_IN_NEW_STATE";
	public static final String BTN_IN_BROWSER_STATE = "BTN_IN_BROWSER_STATE";
	public static final String BTN_IN_EDIT_STATE = "BTN_IN_EDIT_STATE";
	
	//�༭̬��ť
	protected final String[] EDIT_BUTTONS_NAMES = new String[]{CONTRAST,"Tempsave"/*�ݴ�*/,"Save"/*����*/,"Cancel"/*ȡ��*/,"Ass","Document"};
	
	//����̬��ť
	protected final String[] NEW_BUTTONS_NAMES = new String[]{CONTRAST,"Tempsave"/*�ݴ�*/,"Save"/*����*/,"Cancel"/*ȡ��*/};
	
	private Map<String, ButtonObject[]> getStateBtnMap() {
		return stateBtnMap;
	}
	
	/**
	 * ����UE�淶����(������������༭̬)��ť
	 * 
	 * @param status
	 * @return
	 */
	protected ButtonObject[] getUEDjButtons(int status) {
		switch (status) {
		// ����
		case 1:
			return getNewBtns();
		// �༭̬
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
	 * �������̬��ť(��Ƭ������б����)
	 * 
	 * @return
	 */
	private ButtonObject[] getBrowseBtns() {
		String key = BTN_IN_BROWSER_STATE+getCurrWorkPage();
		String djdl = ((BXBillMainPanel)this).getCache().getCurrentDjdl();
		//���ݹ���ڵ㣬��������ǿ��Գ����
		if(BXConstans.BXMNG_NODECODE.equals(getNodeCode())){
			//�����ѡ����VO����ѡ����VO�ĵ��ݴ���Ϊ׼
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
				//1.���ݹ���ڵ�Ľ����Ƴ����ť
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
	 * ��������̬��ť
	 * 
	 * @return
	 */
	private ButtonObject[] getNewBtns() {
		final String djdl = ((BXBillMainPanel)this).getCache().getCurrentDjdl();
		if (getStateBtnMap().get(BTN_IN_NEW_STATE+djdl) == null) {
			List<ButtonObject> list = new ArrayList<ButtonObject>();
			ButtonObject[] btns = getDjButtons();
			List<String> names = Arrays.asList(NEW_BUTTONS_NAMES);
			//����¼��ڵ㲻�ɳ���
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
	 * ���ر༭̬��ť
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
//--end added by chendya��ť�˵����鴦��

	public void refreshBtnStatus() {

		//��Ƭ�б��л������ð�ť״̬
		if (getCurrWorkPage() != getLastWorkPage()) {
			
		}
		
		setButtons(getUEDjButtons(getCurrentPageStatus()));

		//��ʼ����ť��ݼ���
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
		//added by chendya ���Ʒ�ҳ��ť״̬
		setPageButtonStatus();
		
		//���Ʒ��ذ�ť״̬
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
	
	//��ʼ����ť��ݼ���
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
		//����
		createGroupActions(menuActions);
		menuActions = addSeparatorBetweenGroupBtn(menuActions);
		addSeparator4Print(menuActions);
		//��ӷָ���
		setMenuActions(menuActions);
	}
	/**
	 * ��ӷ���
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
	 * ��ť�������
	 *@author chendya 
	 */
	private void appendGroupName(Action action){
		//��ť����
		final String code = (String)action.getValue(INCAction.CODE);
		
		//��ť����
		final String groupName = BXConstans.BTN_GROUP_NAME;
		
		//��������
		if(BXConstans.BTN_GROUP_DJLX_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_DJLX);
		}
		//���Ӱ�ť��
		else if(BXConstans.BTN_GROUP_ADD_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_ADD);
		}
		
		//���ť��
		else if(BXConstans.BTN_GROUP_CONTRAST_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_CONTRAST);
		}
		
		//���水ť��
		else if(BXConstans.BTN_GROUP_SAVE_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_SAVE);
		}
		
		//ȡ����ť��
		else if(BXConstans.BTN_GROUP_CANCEL_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_CANCEL);
		}
		//��ѯ��ť��
		else if(BXConstans.BTN_GROUP_QUERY_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_QUERY);
		}
		//��˰�ť��
		else if(BXConstans.BTN_GROUP_APPROVE_ASS_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_APPROVE_ASS);
		}
		//ȫѡ��ȫ����ť��
		else if(BXConstans.BTN_GROUP_SELECT_CANCEL_ALL_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_SELECT_CANCEL_ALL);
		}
		//���鰴ť��
		else if(BXConstans.BTN_GROUP_LINKQUERY_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_LINKQUERY);
		}
		//�Ƶ���ť��
		else if(BXConstans.BTN_GROUP_MAKEVOUCHER_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_MAKEVOUCHER);
		}
		//��ӡ��ť��
		else if(BXConstans.BTN_GROUP_PRINT_CODES.contains(code)){
			action.putValue(groupName, BXConstans.BTN_GROUP_PRINT);
		}
	}
	
	/**
	 * ��ť������֮����ӷָ�Action
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
	 * ��ʽ��ӡ��ťǰ��ӷָ���
	 * @param list
	 */
	private void addSeparator4Print(List<Action> list){
		//��¼��ӡ��ťλ��
		int pos = 0;
		Action[] children = null;
		MenuAction printAction = null;
		for (Iterator<Action> iterator = list.iterator(); iterator.hasNext();) {
			Action action = iterator.next();
			final String code = (String)action.getValue(INCAction.CODE);
			if(code!=null&&code.equals("��ӡ����")){/*-=notranslate=-*/
				printAction = (MenuAction)action;
				children = printAction.getAllChild();
				//��¼�ָ��߼ӵ�λ��
				int idx = 0;
				for (int i = 0; i < children.length; i++) {
					if("Officalprint".equals(children[i].getValue(INCAction.CODE))){
						idx = i;
						break;
					}
				}
				if(idx>0){
				//��ӷָ���
					printAction.addChildAction(idx, new nc.funcnode.ui.action.SeparatorAction());
				}
				children = printAction.getAllChild();
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
	 * ʵ������д
	 * 
	 * @throws BusinessException
	 */
	protected void beforeOnButtonClicked(nc.ui.pub.ButtonObject bo) throws BusinessException {

	}

	/**
	 * ʵ������д
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
	 * ִ����ڽڵ㷽��
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
