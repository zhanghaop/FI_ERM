package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.pub.link.ILinkQueryDataPlural;
import nc.security.NCAuthenticatorFactory;
import nc.ui.arap.bx.actions.CardAction;
import nc.ui.arap.bx.actions.SaveAction;
import nc.ui.arap.bx.remote.BXBillListTemplateCall;
import nc.ui.arap.bx.remote.BXDeptRelCostCenterCall;
import nc.ui.arap.bx.remote.BusiTypeCall;
import nc.ui.arap.bx.remote.DeptMultiVersionVoRemoteCall;
import nc.ui.arap.bx.remote.ExpenseTypeCall;
import nc.ui.arap.bx.remote.InitNodeCall;
import nc.ui.arap.bx.remote.OrgMultiVersionVORemoteCall;
import nc.ui.arap.bx.remote.PermissionOrgVoCall;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.arap.bx.remote.QcDateCall;
import nc.ui.arap.bx.remote.ReimRuleDefCall;
import nc.ui.arap.bx.remote.ReimTypeCall;
import nc.ui.arap.bx.remote.RoleVoCall;
import nc.ui.arap.bx.remote.UserBankAccVoCall;
import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.eventagent.UIEventagent;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.pub.MessageLog;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.fipub.service.RometCallProxy;
import nc.ui.glpub.IUiPanel;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemHyperlinkEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillTabbedPaneTabChangeEvent;
import nc.ui.pub.bill.BillTabbedPaneTabChangeListener;
import nc.ui.pub.bill.IBillData;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IBillListData;
import nc.ui.pub.linkoperate.ILinkAdd;
import nc.ui.pub.linkoperate.ILinkAddData;
import nc.ui.pub.linkoperate.ILinkApprove;
import nc.ui.pub.linkoperate.ILinkApproveData;
import nc.ui.pub.linkoperate.ILinkMaintain;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.pub.linkoperate.ILinkQuery;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;
import nc.ui.queryarea.QueryArea;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.editor.UserdefQueryParam;
import nc.ui.uif2.userdefitem.QueryParam;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXQueryUtil;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.arap.bx.util.Page;
import nc.vo.arap.bx.util.PageUtil;
import nc.vo.bx.pub.ref.BXBilltypeRefModel;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxAggregatedVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.check.VOChecker;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.global.IRuntimeConstans;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillRendererVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;

/**
 * @author twei
 *
 *         报销单据主面板
 *
 *         nc.ui.arap.bx.BXBillMainPanel
 */
public abstract class BXBillMainPanel extends BaseUIPanel implements IUiPanel,
		ILinkQuery, ILinkAdd, ILinkApprove, ILinkMaintain, IUINodecodeSearcher,
		nc.itf.cmp.pub.ITabExComponent{
	
	private static final String ERM_264X_H = "erm_264X_H";
	private static final String ERM_264X_B = "erm_264X_B";
	
	private static final String ERM_263X_H = "erm_263X_H";
	private static final String ERM_263X_B = "erm_263X_B";
	
	private static final long serialVersionUID = 6874062120711320268L;
	
	protected abstract void loadBillListTemplate();
	
	public abstract QueryConditionDLG getQryDlg();

	private Map<String, List<SuperVO>> reimRuleDataMap = new HashMap<String, List<SuperVO>>(); // 报销规则缓存数据
	private Map<String, SuperVO> expenseMap = new HashMap<String, SuperVO>(); // 费用类型缓存数据
	private Map<String, SuperVO> reimtypeMap = new HashMap<String, SuperVO>(); // 费用类型缓存数据

	private BxParam bxParam;// 参数缓存

	private boolean isCardTemplateLoaded;// 卡片模板是否已经加载

	private int temppkIndex = 0;// 临时单据pk索引

	protected int panelStatus = 0; // 面板当前状态 //0普通，1审批，2查询,3增加,4修改

	protected String tempBillPk; // 临时单据pk

	protected String tempBilltype; // 临时单据pk

	private int headTabIndex;

	protected BillListPanel listPanel;

	private BillCardPanel cardPanel;

	public QueryConditionDLG queryDialog;

	private UIRefPane djlxRef;

	private ContrastDialog contrastDialog;

	private SettleDialog settleDialog;

	private BatchContrastDialog batchcontrastDialog;

	private BatchContrastDetailDialog batchContrastDetailDialog;

	private UIPanel cardContentPanel;

	private UIPanel listContentPanel;
	
	private List<String> panelEditableKeyList;

	/**
	 * 缓存交易类型的单据数据结构
	 */
	protected Map<String, BillData> djlxmBillDataCache = new HashMap<String, BillData>();

	/**
	 * 列表下单据数据结构
	 */
	protected BillListData listData;
	
	/**
	 * 卡片下单据数据结构
	 */
	protected BillData billData;

	/**
	 * 缓存各组织关联字段
	 */
	private Map<String,List<String>> orgRefFieldsMap = new HashMap<String, List<String>>();
	
	public BXBillMainPanel() {
		super();
	}

	/**
	 * 各组织关联的字段集合
	 * @return
	 */
	public List<String> getAllOrgRefFields(){
		List<String> list = new ArrayList<String>();
		list.addAll(getBusTypeVO().getPayentity_billitems());
		list.addAll(getBusTypeVO().getCostentity_billitems());
		list.addAll(getBusTypeVO().getUseentity_billitems());
		return list;
	}

	/**
	 * 返回组织关联的字段
	 * @param orgField
	 * @return
	 */
	public List<String> getOrgRefFields(String orgField){
		if(!orgRefFieldsMap.containsKey(orgField)){
			if(JKBXHeaderVO.PK_ORG.equals(orgField)){
				orgRefFieldsMap.put(orgField, getBusTypeVO().getPayentity_billitems());
			}else if(JKBXHeaderVO.FYDWBM.equals(orgField)){
				orgRefFieldsMap.put(orgField, getBusTypeVO().getCostentity_billitems());
			}else if(JKBXHeaderVO.DWBM.equals(orgField)){
				orgRefFieldsMap.put(orgField, getBusTypeVO().getUseentity_billitems());
			}
		}
		return orgRefFieldsMap.get(orgField);
	}

	public BillListData getListData() {
		return listData;
	}

	public void setListData(BillListData listData) {
		this.listData = listData;
	}

	/**
	 * 列表界面自定义项处理器
	 */
	private BXCompositeBillListDataPrepare bxCompositeBillListDataPrepare;
	private BXUserdefitemContainerListPreparator bxUserdefitemContainerListPreparator;

	/**
	 * 卡片界面自定义项处理器
	 */
	private BXCompositeBillDataPrepare bxCompositeBillDataPrepare;
	private BXUserdefitemContainerPreparator bxUserdefitemContainerPreparator;

	/**
	 * 报销单自定义项查询参数列表
	 */
	List<UserdefQueryParam> bxParams;

	/**
	 * 借款单自定义项查询参数列表
	 */
	List<UserdefQueryParam> jkParams;

	/**
	 * 借款单自定义项容器查询参数列表
	 */
	List<QueryParam> jkQueryParamList;

	/**
	 * 报销单自定义项容器查询参数列表
	 */
	List<QueryParam> bxQueryParamList;

	/**
	 * @author chendya
	 * @return
	 */
	private BXCompositeBillDataPrepare getBxCompositeBillDataPrepare() {
		if (bxCompositeBillDataPrepare == null) {
			bxCompositeBillDataPrepare = new BXCompositeBillDataPrepare();
			bxCompositeBillDataPrepare
					.setBillDataPrepares(Arrays
							.asList(new IBillData[] { getBxUserdefitemContainerPreparator() }));
		}
		return bxCompositeBillDataPrepare;
	}

	/**
	 * @author chendya
	 * @return
	 */
	private BXUserdefitemContainerPreparator getBxUserdefitemContainerPreparator() {
		if (bxUserdefitemContainerPreparator == null) {
			bxUserdefitemContainerPreparator = new BXUserdefitemContainerPreparator();
			bxUserdefitemContainerPreparator
					.setContainer(getBxUserDefItemContainer());
			bxUserdefitemContainerPreparator
					.setParams(getUserdefQueryParamList());

		}
		return bxUserdefitemContainerPreparator;
	}

	/**
	 * @author chendya
	 * @return
	 */
	public BXCompositeBillListDataPrepare getBxCompositeBillListDataPrepare() {
		if (bxCompositeBillListDataPrepare == null) {
			bxCompositeBillListDataPrepare = new BXCompositeBillListDataPrepare();
			bxCompositeBillListDataPrepare
					.setBillListDataPrepares(Arrays
							.asList(new IBillListData[] { getBxUserdefitemContainerListPreparator() }));
		}
		return bxCompositeBillListDataPrepare;
	}

	/**
	 * @author chendya
	 * @return
	 */
	private BXUserdefitemContainerListPreparator getBxUserdefitemContainerListPreparator() {
		if (bxUserdefitemContainerListPreparator == null) {
			bxUserdefitemContainerListPreparator = new BXUserdefitemContainerListPreparator();
			bxUserdefitemContainerListPreparator
					.setContainer(getBxUserDefItemContainer());
			bxUserdefitemContainerListPreparator
					.setParams(getUserdefQueryParamList());
		}
		return bxUserdefitemContainerListPreparator;
	}

	/**
	 * @author chendya
	 * @return
	 */
	public BXUserDefItemContainer getBxUserDefItemContainer() {
		BXUserDefItemContainer bxUserDefItemContainer = new BXUserDefItemContainer();
		bxUserDefItemContainer.setParams(getQueryParamList());
		return bxUserDefItemContainer;
	}

	/**
	 * @author chendya
	 * @return
	 */
	private List<QueryParam> getQueryParamList() {
		if (isJKBill()) {
			return getJkQueryParamList();
		}
		return getBxQueryParamList();
	}

	/**
	 * @author chendya
	 * @return
	 */
	private List<QueryParam> getJkQueryParamList() {
		if (jkQueryParamList == null) {
			jkQueryParamList = new ArrayList<QueryParam>();
			// 表头
			QueryParam param = new QueryParam();
			param.setRulecode(ERM_263X_H);
			//param.setMdfullname(BXConstans.JK_HEAD_MDFULLNAME);
			jkQueryParamList.add(param);

			// 表体

			// 业务页签
			param = new QueryParam();
			param.setRulecode(ERM_263X_B);
			//param.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			jkQueryParamList.add(param);

			// 财务页签
			param = new QueryParam();
			param.setRulecode(ERM_263X_B);
			//param.setMdfullname(BXConstans.JK_BODY_FINITEM_MDFULLNAME);
			jkQueryParamList.add(param);

			// 冲销对照页签
			param = new QueryParam();
			//param.setMdfullname(BXConstans.JK_BODY_CONTRAST_MDFULLNAME);
			param.setRulecode(ERM_263X_B);
			jkQueryParamList.add(param);
		}
		return jkQueryParamList;
	}

	/**
	 * @author chendya
	 * @return
	 */
	private List<QueryParam> getBxQueryParamList() {
		if (bxQueryParamList == null) {
			bxQueryParamList = new ArrayList<QueryParam>();
			// 表头
			QueryParam param = new QueryParam();
			
			param.setRulecode(ERM_264X_H);
//			param.setMdfullname(BXConstans.BX_HEAD_MDFULLNAME);
			bxQueryParamList.add(param);

			// 表体
			// 业务页签
			param = new QueryParam();
			param.setRulecode(ERM_264X_B);
//			param.setMdfullname(BXConstans.BX_BODY_BUSITEM_MDFULLNAME);
			bxQueryParamList.add(param);

		}
		return bxQueryParamList;
	}

	private  final String STR_JK = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("expensepub_0", "02011002-0041")/*
																		 * @res
																		 * "借款"
																		 */;

	private  final String STR_BX = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("expensepub_0", "02011002-0042")/*
																		 * @res
																		 * "报销"
																		 */;

	/**
	 * 修改查询条件前缀名称
	 *
	 * @author chendya
	 * @param items
	 * @param src
	 *            原名称
	 * @param dest
	 *            目标名称
	 */
	private void modifyPrefixShowName(BillItem[] items, final String src,final String dest) {
		for (int i = 0; i < items.length; i++) {
			final String fieldName = items[i].getName();
			if(StringUtil.isEmpty(fieldName)||fieldName.startsWith(src)){
				continue;
			}
			if(fieldName.startsWith(dest)){
				items[i].setName(fieldName.replace(dest, src));
			}
		}
	}

	/**
	 * @author chendya 处理卡片界面自定义项显示
	 */
	protected void dealCardUserDefItem() {
		BillData billData = getBillCardPanel().getBillData();
		getBxCompositeBillDataPrepare().prepareBillData(billData);
		getBillCardPanel().setBillData(billData);

	}

	/**
	 * @author chendya 处理列表界面自定义项显示
	 */
	protected void dealListUserDefItem() {
		BillListData billListData = getBillListPanel().getBillListData();
		getBillListPanel().setListData(billListData,
				getBxCompositeBillListDataPrepare());
	}

	/**
	 * @author chendya 是否借款单据
	 * @return
	 */
	protected boolean isJKBill() {
		return getCache().getCurrentDjlx().getDjdl().equals(BXConstans.JK_DJDL);
	}

	/**
	 * @author chendya
	 * @return
	 */
	public List<UserdefQueryParam> getUserdefQueryParamList() {
		if (isJKBill()) {
			return getJKUserdefQueryParamList();
		}
		return getBXUserdefQueryParamList();
	}

	/**
	 * 返回报销单自定义项查询参数列表
	 *
	 * @return
	 */
	protected List<UserdefQueryParam> getBXUserdefQueryParamList() {
		if (bxParams == null) {
			bxParams = new ArrayList<UserdefQueryParam>();
			// 表头(尾)
			UserdefQueryParam paramHead = new UserdefQueryParam();
			paramHead.setRulecode(ERM_264X_H);
			paramHead.setPos(IBillItem.HEAD);
			paramHead.setPrefix(BXConstans.HEAD_USERDEF_PREFIX);
			bxParams.add(paramHead);

			// 表体
			// 业务页签
			UserdefQueryParam paramBody = new UserdefQueryParam();
			paramBody.setRulecode(ERM_264X_B);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			bxParams.add(paramBody);
		}

		return bxParams;
	}

	/**
	 * @author chendya 返回借款单自定义项查询参数列表
	 * @return
	 */
	protected List<UserdefQueryParam> getJKUserdefQueryParamList() {
		if (jkParams == null) {
			jkParams = new ArrayList<UserdefQueryParam>();
			// 表头(尾)
			UserdefQueryParam paramHead = new UserdefQueryParam();
			//paramHead.setMdfullname(BXConstans.JK_HEAD_MDFULLNAME);
			paramHead.setRulecode(ERM_263X_H);
			paramHead.setPos(IBillItem.HEAD);
			paramHead.setPrefix(BXConstans.HEAD_USERDEF_PREFIX);
			jkParams.add(paramHead);

			// 表体
			// 业务页签
			UserdefQueryParam paramBody = new UserdefQueryParam();
			//paramBody.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			paramBody.setRulecode(ERM_263X_B);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			jkParams.add(paramBody);

			// 财务页签
			paramBody = new UserdefQueryParam();
			//paramBody.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			paramBody.setRulecode(ERM_263X_B);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			jkParams.add(paramBody);

			// 冲销对照页签
			paramBody = new UserdefQueryParam();
			paramBody.setRulecode(ERM_263X_B);
			//paramBody.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			jkParams.add(paramBody);
		}

		return jkParams;
	}

	// --end

	

	// begin--added by chendya 列表界面添加查询方案

	BXQueryAreaShell queryAreaShell;

	QueryArea queryArea;

	public BXQueryAreaShell getQueryAreaShell() {
		if (queryAreaShell == null) {
			queryAreaShell = new BXQueryAreaShell(this, getQueryArea());
			queryAreaShell.setName("QueryArea");
		}
		return queryAreaShell;
	}

	public QueryArea getQueryArea() {
		if (queryArea == null) {
			queryArea = new QueryArea(getQryDlg());
		}
		return queryArea;
	}

	UISplitPane splitPane;

	/**
	 * 查询方案最小化后的面板
	 */
	BXMiniminizePanel miniminizePanel;

	public BXMiniminizePanel getMiniminizePanel() {
		if (miniminizePanel == null) {
			miniminizePanel = new BXMiniminizePanel(getSplitPane(),
					getQueryAreaShell());
		}
		return miniminizePanel;
	}

	public UISplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new UISplitPane(UISplitPane.HORIZONTAL_SPLIT);
			splitPane.setDividerLocation(BXConstans.MAXIMISED_POSITION);
			splitPane.setLeftComponent(getQueryAreaShell());
			splitPane.setRightComponent(getBillListPanel());
			splitPane.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(
							UISplitPane.DIVIDER_LOCATION_PROPERTY)) {
						final Integer currVal = (Integer) evt.getNewValue();
						if (currVal.intValue() == BXConstans.MINIMINIZED_POSITION) {
							splitPane.remove(getQueryAreaShell());
							splitPane.setLeftComponent(getMiniminizePanel());
						}
					}
				}
			});
		}
		return splitPane;
	}

	// --end
	public nc.ui.pub.beans.UIPanel getListContentPanel() {
		if (listContentPanel == null) {
			try {
				listContentPanel = new nc.ui.pub.beans.UIPanel();
				listContentPanel.setName("listContentPanel");
				listContentPanel.setAutoscrolls(true);
				listContentPanel.setLayout(new java.awt.BorderLayout());
				// begin--added by chendya 添加查询方案

				// getListContentPanel().add(getBillListPanel(), "Center");
				getListContentPanel().add(getSplitPane(), "Center");
				// --end
				listContentPanel
						.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2011", "UPP2011-000168")/*
																	 * @res "列表"
																	 */);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return listContentPanel;
	}

	// begin--added by chendya 卡片界面添加“返回”按钮面板

	BXSimilarUECardLayoutToolbarPanel cardToolbarPanel;

	public BXSimilarUECardLayoutToolbarPanel getCardToolbarPanel() {
		if (cardToolbarPanel == null) {
			cardToolbarPanel = new BXSimilarUECardLayoutToolbarPanel(
					BXBillMainPanel.this);
		}
		return cardToolbarPanel;
	}

	// --end

	public UIPanel getCardContentPanel() {
		if (cardContentPanel == null) {
			try {
				cardContentPanel = new UIPanel();
				cardContentPanel.setName("cardContentPanel");
				cardContentPanel.setLayout(new java.awt.BorderLayout());

				// begin--added by chendya 卡片界面添加返回按钮以及分页按钮的面板
				cardContentPanel.add(getCardToolbarPanel(), BorderLayout.NORTH);

				// --end
				cardContentPanel.add(getBillCardPanel(), "Center");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return cardContentPanel;
	}

	@Override
	public String getTitle() {
		/*
		 * @res "报销管理"
		 */
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				"UPP2011-000369");
	}

	protected void callRemoteService() throws BusinessException {
		List<IRemoteCallItem> callitems = new ArrayList<IRemoteCallItem>();
		callitems.add(new ReimTypeCall(this));
		callitems.add(new ExpenseTypeCall(this));
		callitems.add(new BusiTypeCall(this));
		callitems.add(new InitNodeCall(this));
		callitems.add(new ReimRuleDefCall(this));
		callitems.add(new PsnVoCall());

// begin--added by chendya 批量调用，减少远程调用次数，提高效率
		//节点权限
		callitems.add(new PermissionOrgVoCall(this));

		//集团角色
		callitems.add(new RoleVoCall());

		if(!getBxParam().isInit()){
			if(BXConstans.BXMNG_NODECODE.equals(getNodeCode())||BXConstans.BXBILL_QUERY.equals(getNodeCode())||BXConstans.BXLR_QCCODE.equals(getNodeCode())){
				//缓存单据模版，并处理自定义项
				callitems.add(new BXBillListTemplateCall(this));
			}
		}

		//会计期间
		callitems.add(new QcDateCall());

		//当前登录人所关联业务员的个人银行帐号默认报销卡子户信息
		callitems.add(new UserBankAccVoCall());

		//缓存部门关联的成本中心//有效率问题
		//callitems.add(new BXDeptRelCostCenterCall());

		//当前业务时间所有的组织多版本
		//callitems.add(new OrgMultiVersionVORemoteCall(this));

		//缓存当前登录人所属组织下部门多版本
		String bxDefaultOrgUnit = BXUiUtil.getBXDefaultOrgUnit();
		if(!StringUtil.isEmpty(bxDefaultOrgUnit)){
			//callitems.add(new DeptMultiVersionVoRemoteCall(this,bxDefaultOrgUnit));
		}
// --end
		try {
			RometCallProxy.callRemoteService(callitems);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	@Override
	protected String checkPrerequisite() {
		return null;
	}

	@Override
	protected void initialize() {

		setName("MainPanel");
		setAutoscrolls(true);
		setLayout(new java.awt.CardLayout());
		setSize(774, 419);

	}

	public BxParam getBxParam() {
		if (this.bxParam == null) {
			this.bxParam = new BxParam();
		}
		return bxParam;
	}

	/**
	 * 返回最新的单据类型VO
	 * @author chendya
	 * @param vos
	 * @return
	 */
	private DjLXVO getLatestDjlxVO(DjLXVO[] vos){
		if(vos==null||vos.length==0){
			throw new IllegalArgumentException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0004")/*@res "传入的参数不能为空"*/);
		}
		DjLXVO retVo = vos[0];
		for (int i = 1; i < vos.length; i++) {
			if(vos[i].getTs()!=null && (vos[i].getTs().compareTo(retVo.getTs())>0)){
				retVo = vos[i];
			}
		}
		return retVo;
	}

	/**
	 * 界面初始化调用
	 */
	@Override
	@SuppressWarnings( { "deprecation" })
	protected void postInit() {

		DjLXVO[] djlxvosByNodeCode = null;
		this.setNodeCode(getModuleCode());
		List<DjLXVO> djlxs = new ArrayList<DjLXVO>();

		// 初始化单据类型信息
		if (getCache().getCurrentDjdl() == null) {
			djlxvosByNodeCode = getDjlxvosByNodeCode();

			// 校验封存的单据类型
			for (DjLXVO djlx : djlxvosByNodeCode) {

				if (getBxParam().getIsQc()
						&& djlx.getDjdl().equals(BXConstans.BX_DJDL)) {
					continue;
				}
				djlxs.add(djlx);
			}
			DjLXVO[] validDjlx = djlxs.toArray(new DjLXVO[] {});
			if (validDjlx == null || validDjlx.length == 0) {
				/*
				 * @res "该节点单据类型已被封存，不可操作节点！"
				 */
				showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000171"));
			}
			getCache().setDjlxVOS(validDjlx);

			// 此处默认读取第一个单据类型
			getCache().setCurrentDjlx(getLatestDjlxVO(validDjlx));

		}

		// 公共调用，从cmp加载和自身加载公共的逻辑放到这里
		try {
			initPublic();
		} catch (BusinessException e) {
			showErrorMessage(e.getMessage());
			return;
		}

		// 设置模板事件代理
		setTempletEventAgent(new UIEventagent(this));

		// 界面初始化方法
		initUi();

		setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);

		// 初始化流程配置环境
		initActionRuntime();

		// 初始化按钮
		ButtonObject[] btArray = getDjButtons();
		setButtons(btArray);

		// 初始化按钮快捷键等
		initActions();

		// 刷新按钮状态
		refreshBtnStatus();
	}

	private void initPublic() throws BusinessException {
		// 进行远程调用
		callRemoteService();
	}

	/**
	 * 界面初始化调用
	 *
	 * @param billtype
	 * @throws Exception
	 */
	protected void postInitForCmp(String billtype) throws Exception {
		DjLXVO djlx = null;
		/*
		 * @res "当前公司未分配该交易类型"
		 */
		String djlxMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"2011", "UPP2011-000370");
		try {
			djlx = NCLocator.getInstance().lookup(IArapBillTypePublic.class)
					.getDjlxvoByDjlxbm(billtype, BXUiUtil.getPK_group());
		} catch (Exception e) {
			throw new BusinessRuntimeException(djlxMsg);
		}
		if (djlx == null) {
			throw new BusinessRuntimeException(djlxMsg);
		}
		DjLXVO[] djlxvosByNodeCode = new DjLXVO[] { djlx };
		// 初始化单据类型信息
		getCache().setDjlxVOS(djlxvosByNodeCode);

		getCache().setCurrentDjlx(djlxvosByNodeCode[0]);

		String strDjlxbm = getCache().getCurrentDjlxbm();

		BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(strDjlxbm);

		String nodecode2 = billtypevo.getNodecode();

		this.setNodeCode(nodecode2);

		// 公共调用，从cmp加载和自身加载公共的逻辑放到这里
		try {
			initPublic();
		} catch (BusinessException e) {
			handleException(e);
			return;
		}
		// 设置模板事件代理
		setTempletEventAgent(new UIEventagent(this));

		// 初始化流程配置环境
		initActionRuntime();

		// 界面初始化方法
		initUIFromCmp();

		// 结算“业务信息”页签初始化按钮
		initAction4Cmp();

		refreshBtnStatus();
	}

	/**
	 * 为结算节点切换到“业务信息”页签初始化按钮
	 *
	 * @author chendya
	 */
	@SuppressWarnings("deprecation")
	private void initAction4Cmp() {
		setButtons(getDjButtons4Cmp());
	}

	/**
	 * 为结算节点切换到“业务信息”页签初始化按钮
	 *
	 * @author chendya
	 * @return
	 */
	protected ButtonObject[] getDjButtons4Cmp() {
		this.setCurrentpage(BillWorkPageConst.CARDPAGE);
		this.setLastWorkPage(BillWorkPageConst.CARDPAGE);
		final ButtonObject[] btArray = getUEDjButtons(getCurrentPageStatus());
		List<ButtonObject> buttonList = new ArrayList<ButtonObject>();
		for (ButtonObject btn : btArray) {
			if (btn.getCode().equals("联查") || btn.getCode().equals("打印操作")) {/*-=notranslate=-*/
				buttonList.add(btn);
			}
		}
		return buttonList.toArray(new ButtonObject[0]);
	}

	/**
	 * 结算切换业务信息时调用
	 * @throws Exception
	 */
	private void initUIFromCmp() throws Exception {
		add(getCardContentPanel(), "CARD");
		loadCardTemplate(true);
		getBillCardPanel().setEnabled(false);
		setCurrentpage(BillWorkPageConst.CARDPAGE);
	}

	/**
	 * 界面初始化方法
	 */
	protected abstract void initUi();

	/**
	 * 初始化流程配置环境
	 */
	private void initActionRuntime() {
		this.setAttribute(BXConstans.KEY_BILLTYPE, getCache()
				.getCurrentDjlxbm());
		this.setAttribute(BXConstans.KEY_PARENTBILLTYPE, getParentBillType());
	}

	protected abstract DjLXVO[] getDjlxvosByNodeCode();

//added by chendya 添加和uif2相同的分页栏
	BXPaginationBar pageBarPanel;

	public BXPaginationBar getPageBarPanel() {
		if(pageBarPanel==null){
			pageBarPanel = new BXPaginationBar(this);
		}
		return pageBarPanel;
	}

//--end

	/**
	 * 列表界面
	 */
	@Override
	public nc.ui.pub.bill.BillListPanel getBillListPanel() {
		if (listPanel == null) {
			try {
				listPanel = new BXBillListPanel();
				listPanel.setName("LIST");

				loadBillListTemplate();

				resetBilllistpanel();

				addHyperlinkListener();

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}

		return listPanel;
	}

	private void addBillItemsHyperlinkListener(BillItem[] items,
			ListBillItemHyperlinkListener ll) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].isListHyperlink()) {
					items[i].addBillItemHyperlinkListener(ll);
				}
			}
		}
	}

	/**
	 * @author chendya
	 * 列表界面添加超链接监听
	 */
	private void addHyperlinkListener() {
		BillItem[] headBillItems = this.listPanel.getBillListData()
				.getHeadItems();
		ListBillItemHyperlinkListener ll = new ListBillItemHyperlinkListener();
		this.addBillItemsHyperlinkListener(headBillItems, ll);
		String[] tabcodes = this.listPanel.getBillListData()
				.getBodyTableCodes();
		if (tabcodes == null) {
			return;
		}
		for (int i = 0; i < tabcodes.length; i++) {
			BillItem[] bodyItems = this.listPanel.getBillListData()
					.getBodyItemsForTable(tabcodes[i]);
			this.addBillItemsHyperlinkListener(bodyItems, ll);
		}
	}

	/**
	 * 超链接监听
	 * @author chendya
	 *
	 */
	private class ListBillItemHyperlinkListener implements
			BillItemHyperlinkListener {

		@Override
		public void hyperlink(BillItemHyperlinkEvent event) {
			if(BillWorkPageConst.LISTPAGE==getCurrWorkPage()){
				CardAction action = new CardAction();
				action.setActionRunntimeV0(BXBillMainPanel.this);
				try {
					action.changeTab();
					refreshBtnStatus();
				} catch (BusinessException e) {
					handleException(e);
				}
			}
		}

	}

	private void resetBilllistpanel() {
		/** 添加精度监听 **/
		BXUiUtil.addDecimalListenerToListpanel(listPanel,getCache());

		BillRendererVO voCell = new BillRendererVO();
		voCell.setShowThMark(true);
		voCell.setShowZeroLikeNull(true);

		listPanel.getParentListPanel().setTotalRowShow(true);
		listPanel.getChildListPanel().setShowFlags(voCell);
		listPanel.getParentListPanel().setShowFlags(voCell);
		// //首先将panel置为可用，之后把除了selected置为不可用
		listPanel.setEnabled(true);
		// listPanel.getHeadItem(BXHeaderVO.SELECTED).setEnabled(false);
		BillItem[] listHeadItems = listPanel.getHeadBillModel()
				.getBodyItems();
		BillItem[] listBodyItems = listPanel.getBodyBillModel()
				.getBodyItems();
		for (BillItem item : listHeadItems) {
			if (!item.getKey().equals(JKBXHeaderVO.SELECTED)) {
				item.setEnabled(false);
			}
		}
		if (listBodyItems != null) {
			for (BillItem item : listBodyItems) {
				item.setEnabled(false);
			}
		}
		setJeFieldDigits(JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);

		initLoginEntityItems(BXUiUtil.getBXDefaultOrgUnit(), listPanel
				.getBodyBillModel().getBodyItems(), listPanel
				.getHeadBillModel().getBodyItems());

		//added by chendya 支持分页栏
		listPanel.addHeadNavigatePanel(getPageBarPanel());
	}

	/**
	 * @param ybField
	 * @param fbField
	 * @param bbField
	 *
	 *            设置金额字段的数据精度
	 */
	protected void setJeFieldDigits(String ybField, String bbField) {
		// FIXME 币种相关信息不确定，暂时去掉
		// Integer intBBDigit = getBxParam().getDigit_b();
		Integer intBBDigit = 2;
		BillItem bbje = listPanel.getHeadBillModel().getItemByKey(bbField);

		if (bbje != null) {
			bbje.setDecimalDigits(intBBDigit);
		}
	}

	/**
	 * @param vos
	 * @throws BusinessException
	 *
	 *             设置元素VO列表，追加至当前列表
	 */
	public void appendListVO(List<JKBXHeaderVO> vos) throws BusinessException {

		JKBXVO tempvo = null;
		List<JKBXVO> tempvos = new ArrayList<JKBXVO>();

		for (int i = 0; i < vos.size(); i++) {
			tempvo = VOFactory.createVO(vos.get(i).getDjdl());
			tempvo.setParentVO(vos.get(i));
			tempvo.setChildrenVO(null);
			tempvos.add(tempvo);
		}

		getCache().putVOArray(tempvos.toArray(new JKBXVO[] {}));

	}

	/**
	 * @param vos
	 * @throws BusinessException
	 *
	 *             设置元素VO列表, 会清空当前列表
	 */
	public void setListVO(List<JKBXHeaderVO> vos) throws BusinessException {

		getBillListPanel().getHeadBillModel().clearBodyData();
		getBillListPanel().getBodyBillModel().clearBodyData();

		JKBXHeaderVO[] headvos = new JKBXHeaderVO[vos.size()];
		vos.toArray(headvos);

		if ((headvos == null || headvos.length < 1)) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"2006030102", "UPP2006030102-000424")/*
														 * @res "没有相关单据"
														 */);
			return;
		}

		getCache().clearVOData();

		try {
			JKBXVO tempvo = null;
			List<JKBXVO> tempvos = new ArrayList<JKBXVO>();

			for (int i = 0; i < headvos.length; i++) {
				tempvo = VOFactory.createVO(headvos[i].getDjbh());
				tempvo.setParentVO(headvos[i]);
				tempvo.setChildrenVO(null);
				tempvos.add(tempvo);
			}

			getCache().putVOArray(tempvos.toArray(new JKBXVO[] {}));

		} catch (Throwable e) {
			ExceptionHandler.consume(e);
		}
		updateView();
	}

	/**
	 * 刷新界面
	 */
	public void updateView() {

		if (getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {
			updateListView();
		} else {
			updateCardView();
		}
	}

	/**
	 * 刷新卡片界面
	 */
	private void updateCardView() {

		getBillCardPanel().setBillValueVO(getCache().getCurrentVO());

		setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);
	}

	/**
	 * 刷新列表界面
	 */
	public void updateListView() {

		JKBXHeaderVO[] headvos = getCache().getCurrentPageVOs();
		/*
		 * UI工厂2采用元数据，设置值应该走元数据方式 非元数据方法
		 * getBillListPanel().setHeaderValueVO(headvos); add by liansg
		 */
		if (getBxParam().isInit()) {
			for (JKBXHeaderVO vo : headvos) {
				if (!vo.getIsinitgroup().booleanValue()) {
					vo.setSetorg(vo.getPk_org());
				}
			}
			getBillListPanel().setHeaderValueVO(headvos);
		} else {
			JKBXHeaderVO[] clonevos = new JKBXHeaderVO[headvos.length];
			int i = 0;
			for (JKBXHeaderVO b : headvos) {
				JKBXHeaderVO clone = (JKBXHeaderVO) b.clone();
				if (clone.getYbje().equals(UFDouble.ZERO_DBL)) {
					clone.setYbje(clone.getCjkybje());
					clone.setBbje(clone.getCjkbbje());
					clone.setGroupbbje(clone.getGroupcjkbbje());
					clone.setGlobalbbje(clone.getGlobalcjkbbje());
				}
				clonevos[i++] = clone;
			}
			//added by chendya setBodyDataVO方法会出自动setChangeTable
			getBillListPanel().getHeadBillModel().setBodyDataVO(clonevos);

			//避免显示pk值
			getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(clonevos);
			//--end

			// begin--added by chendya
			// 由于元数据方式getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(clonevos)
			// 无法处理处理是否选中项，这里手动处理
			for (int j = 0; j < clonevos.length; j++) {
				UFBoolean selected = clonevos[j].getSelected();
				// 是否选中
				getBillListPanel().getHeadBillModel().setValueAt(selected, j,
						JKBXHeaderVO.SELECTED);
			}
			// --end
			// 重设精度
			BXUiUtil.resetBBHLDecimal(getBillListPanel(), clonevos);

		}

		try {
			// 修改单据类型名称加载公式
			BillItem djlxmcBillItem = getBillListPanel().getBillListData().getHeadItem(JKBXHeaderVO.DJLXMC);
			BXUiUtil.modifyLoadFormula(djlxmcBillItem, "billtypename");
			getBillListPanel().getHeadBillModel().execLoadFormula();
		} catch (Exception e) {
			Logger.debug(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"2006030102", "UPP2006030102-000037")/*
														 * @res "加载公式出错:"
														 */
					+ e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.arap.bx.BaseUIPanel#getBillCardPanel()
	 *
	 * 卡片界面
	 */
	@Override
	public BillCardPanel getBillCardPanel() {
		if (cardPanel == null) {
			try {
				String strDjlxbm = getCache().getCurrentDjlxbm();
				cardPanel = new BXBillCardPanel(this, "CARD", strDjlxbm,
						BXUiUtil.getBXDefaultOrgUnit(), BXUiUtil.getPk_user());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return cardPanel;
	}

	/**
	 * @return 父类型单据类型编码
	 */
	private String getParentBillType() {
		boolean isBxdjdl = getCache().getCurrentDjlx().getDjdl().equals(
				BXConstans.BX_DJDL);
		String parentBillType = isBxdjdl ? BXConstans.BX_DJLXBM
				: BXConstans.JK_DJLXBM;
		return parentBillType;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see nc.ui.arap.engine.AbstractRuntime#setCurrentPageStatus(int) public
	 *      static int WORKSTAT_EDIT = 3; //修改编辑状态 public static int
	 *      WORKSTAT_NEW = 1; //新增编辑状态 public static int WORKSTAT_BROWSE =0;
	 *      //浏览状态
	 */
	@Override
	public void setCurrentPageStatus(int newstat) {

		if (newstat == BillWorkPageConst.WORKSTAT_LIMIT) {

		} else if (getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_LIMIT) {
			if (newstat == BillWorkPageConst.WORKSTAT_EDIT
					|| newstat == BillWorkPageConst.WORKSTAT_NEW) {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000429")/*
																			 * @res
																			 * "该节点单据类型已经封存，不能进行操作!"
																			 */);
			} else {
				UFBoolean fcbz = getCache().getCurrentDjlx().getFcbz();
				if (fcbz != null && fcbz.booleanValue()) {
					setCurrentPageStatus(BillWorkPageConst.WORKSTAT_LIMIT);
					return;
				}
			}
		} else if (newstat == BillWorkPageConst.WORKSTAT_BROWSE) {
			UFBoolean fcbz = getCache().getCurrentDjlx().getFcbz();
			if (fcbz != null && fcbz.booleanValue()) {
				setCurrentPageStatus(BillWorkPageConst.WORKSTAT_LIMIT);
				return;
			}
		}

		super.setCurrentPageStatus(newstat);

		switch (newstat) {
		case 0:
			getBillCardPanel().setEnabled(false);
			break;
		case 1:
			getBillCardPanel().setEnabled(true);
			break;
		case 3:
			getBillCardPanel().setEnabled(true);
			break;
		case 4:
			getBillCardPanel().setEnabled(false);
			break;
		default:
			break;
		}

		((BXBillCardPanel) getBillCardPanel()).initData(); // 卡片界面初始化一些数据
		refreshBodyActionStatus();// 重设表体按钮状态

	}

	private void refreshBodyActionStatus() {
		List<Action> bodyActions = ((BXBillCardPanel) getBillCardPanel())
				.getBodyActions();
		if (bodyActions != null) {
			for (Action act : bodyActions) {
				boolean isenable = getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_NEW
						|| getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT;
				act.setEnabled(isenable);
			}
		}
	}

	public void loadCardTemplet() throws Exception {
		loadCardTemplate(false);
	}

	/**
	 * @return 条码输入查询
	 */
	public void doBarCodeQuery(String pk) {

		if (pk == null || pk.trim().length() == 0)
			return;

		List<JKBXVO> values = null;
		try {
			// values =
			// getIBXBillPrivate().queryVOsByWhereSql(" where zb.djbh='"+pk.trim()+"' and zb.pk_org='"+BXUiUtil.getDefaultOrgUnit()+"'",
			// "");
			values = getIBXBillPrivate().queryVOsByWhereSql(
					" where zb.djbh='" + pk.trim() + "' and zb.pk_group='"
							+ BXUiUtil.getPK_group() + "'", "");

		} catch (Exception e) {
		}
		if (values != null && values.size() != 0) {
			for (JKBXVO vo : values) {
				getCache().addVO(vo);
				updateView();
			}
		}
	}

	private void loadCardTemplate(boolean isFromCmp) throws Exception {

		if (!isFromCmp)
			initActionRuntime();

		loadCardTemplet(getCache().getCurrentDjlxbm());

		((BXBillCardPanel) getBillCardPanel()).initProp();

		if (!isFromCmp) {
			getTempletEventAgent().initBillCardPane(getBillCardPanel());
		}

		cardPanel.setAutoExecHeadEditFormula(true);
		cardPanel.addTabbedPaneTabChangeListener(
				new BillTabbedPaneTabChangeListener() {

					public void afterTabChanged(BillTabbedPaneTabChangeEvent e) {

						//处理行操作按钮状态
						refreshBtnStatus(new String[] { "行操作", "增行", "复制行","插入行", "粘贴行至表尾", "删行", "粘贴行" }); /*-=notranslate=-*/

						//加载数据
						final String currTabcode = e.getBtvo().getTabcode();
						cardPanel.getBillModel(currTabcode).loadLoadRelationItemValue();

						//冲借款页签不可编辑
						setContrastTabEnable(false);

						showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0001",null,new String[]{e.getBtvo().getTabname()})/*@res "已切换到"*/);
					}

				}, IBillItem.BODY);

	}

	/**
	 * 设置冲销也签是否可编辑
	 *
	 * @author chendya
	 * @param flag
	 */
	protected void setContrastTabEnable(boolean flag) {
		cardPanel.getBillModel(BXConstans.CONST_PAGE).setEnabled(flag);
	}

	public void loadCardTemplet(String strDjlxbm) throws Exception {
		if(strDjlxbm == null){
			
		}
		if (djlxmBillDataCache.containsKey(strDjlxbm)) {
			getBillCardPanel().setBillData(djlxmBillDataCache.get(strDjlxbm));
		} else {
			// 加载单据模板
			getBillCardPanel().loadTemplet(getNodeCode(), null,getBxParam().getPk_user(), BXUiUtil.getPK_group(),strDjlxbm); 
			setCardTemplateLoaded(true);
			
			// 处理卡片界面自定义项显示
			dealCardUserDefItem();
			djlxmBillDataCache.put(strDjlxbm, getBillCardPanel().getBillData());
		}
	}

	public void handleException(ButtonObject bo,java.lang.Throwable ex){
		BXUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_FAIL,bo),ex);
	}

	/**
	 * 统一处理异常信息
	 * @author chendya
	 * @param e
	 */
	public void handleException(java.lang.Throwable ex) {
		BXUiUtil.showUif2DetailMessage(this, "", ex);
	}

	@Override
	public void showErrorMessage(String e) {
//begin-- Remarked by chendya V6异常信息不弹框，改用uif2的异常显示方式(状态栏上方显示异常信息)
//		super.showErrorMessage(e);
//--end
	}

	@Override
	public IBodyUIController getBodyUIController() {
		if (null == bodyUIController) {
			try {
				BusiTypeVO busiTypeVO = getBusTypeVO();
				bodyUIController = (IBodyUIController) Class.forName(busiTypeVO.getInterfaces().get(BusiTypeVO.IBodyUIController)).newInstance();
				bodyUIController.setContainer(this);
			} catch (Exception e) {
				ExceptionHandler.consume(e);;
			}
		}
		return bodyUIController;
	}

	/**
	 * @return 根据当前的单据类型编码取业务类型VO (busitype.xml定义内容)
	 * @see BusiTypeVO
	 */
	public BusiTypeVO getBusTypeVO() {
		return BXUtil.getBusTypeVO(getCache().getCurrentDjlxbm(), getCache().getCurrentDjdl());
	}

	/**
	 * @return 是否模板展示
	 */
	@Override
	public boolean isTempletView(String tableCode) {
		Boolean isTempletView = getBusTypeVO().getIsTableTemplet().get(tableCode);
		if (isTempletView == null)
			isTempletView = true;
		return isTempletView;
	}

	/**
	 * 初始化单据类型参照
	 * 
	 * @param isqc
	 */
	public UIRefPane getDjlxRef(boolean isqc) {
		if (this.djlxRef == null) {
			this.djlxRef = new UIRefPane();
			this.djlxRef.setName("trantsyperef");
			this.djlxRef.setLocation(578, 458);
			this.djlxRef.setIsCustomDefined(true);
			this.djlxRef.setVisible(false);
			this.djlxRef.setRefModel(new BXBilltypeRefModel());
			String strWherePart = "";
			if (isqc) {
				strWherePart = " parentbilltype in ('263X') ";
			} else {
				strWherePart = " parentbilltype in ('263X','264X') ";
			}
			djlxRef.getRefModel().setWherePart(strWherePart+ " and istransaction = 'Y' and isnull(islock,'N') ='N' and  ( pk_group='"
									+ WorkbenchEnvironment.getInstance().getGroupVO().getPk_group() + "')");
		}
		return djlxRef;

	}

	public int getHeadRowIndexByPK(String strHeadPK) {
		int intRowCount = getBillListPanel().getHeadTable().getRowCount();
		for (int index = 0; index < intRowCount; index++) {
			String strRowPK = getBillListPanel().getHeadBillModel().getValueAt(
					index, JKBXHeaderVO.PK_JKBX).toString();
			if (strRowPK.trim().equals(strHeadPK.trim()))
				return index;
		}
		return -1;
	}

	/**
	 * @param msgVOs
	 *
	 *            显示日志信息
	 */
	public void viewLog(MessageVO[] msgVOs) {

		Vector<String> v = new Vector<String>();
		for (int i = 0; msgVOs != null && i < msgVOs.length; i++) {
			v.addElement(msgVOs[i].toString());
		}

		if (v.size() > 0) {

			MessageLog f = new MessageLog(this);
			Double w = new Double((getToolkit().getScreenSize().getWidth() - f
					.getWidth()) / 2);
			Double h = new Double((getToolkit().getScreenSize().getHeight() - f
					.getHeight()) / 2);
			f.setLocation(w.intValue(), h.intValue());
			f.f_setText(v);
			f.showModal();
		}

	}

	/**
	 * @param vo
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 *
	 *             初始化冲借款对话框
	 */
	public ContrastDialog getContrastDialog(JKBXVO vo, String pk_corp)
			throws BusinessException {
		if (contrastDialog == null) {
			contrastDialog = new ContrastDialog(this,
					BXConstans.BXMNG_NODECODE, pk_corp, vo);
		}

		contrastDialog.initData(vo);

		return contrastDialog;
	}

	/**
	 * @param vo
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 *
	 *             初始化批冲借款对话框
	 */
	public BatchContrastDialog getBatchContrastDialog(String pk_corp)
			throws BusinessException {
		if (batchcontrastDialog == null) {
			batchcontrastDialog = new BatchContrastDialog(this,
					BXConstans.BXMNG_NODECODE, pk_corp);
		}
		return batchcontrastDialog;
	}

	/**
	 * @param selBxvos
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 *
	 *             初始化单据结算对话框
	 */
	public SettleDialog getSettleDialog(JKBXVO[] selBxvos, String pk_corp)
			throws BusinessException {
		if (settleDialog == null) {
			settleDialog = new SettleDialog(this, getNodeCode(), pk_corp);
		}

		settleDialog.initData(selBxvos);

		return settleDialog;
	}

	public boolean isCardTemplateLoaded() {
		return isCardTemplateLoaded;
	}

	public void setCardTemplateLoaded(boolean isCardTemplateLoaded) {
		this.isCardTemplateLoaded = isCardTemplateLoaded;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seenc.ui.pub.pf.IUINodecodeSearcher#findNodecode(nc.ui.pub.linkoperate.
	 * ILinkQueryData)
	 */
	public String findNodecode(ILinkQueryData lqd) {
		return BXConstans.BXMNG_NODECODE;
	}

	/**
	 * CA校验
	 *
	 * @throws BusinessException
	 */
	public void checkID() throws BusinessException {

		UserVO loginUser = WorkbenchEnvironment.getInstance().getLoginUser();

		if (loginUser.getIsca() == null) {
			return;
		} else {
			boolean isCaUser = loginUser.getIsca().booleanValue();
			if (!isCaUser)
				return;
		}

		DjLXVO djlx = getCache().getCurrentDjlx();

		boolean checked = true;

		if (null != djlx.getIsidvalidated()
				&& djlx.getIsidvalidated().booleanValue()) {
			try {
				checked = !(NCAuthenticatorFactory.getBusiAuthenticator(
						this.getBxParam().getPk_user()).sign("ERM_SIGN") == null);
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		}

		if (!checked) {
			/*
			 * @res "身份认证未通过，操作失败!"
			 */
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2008", "UPP2008-000102"));
		}
	}

	@Override
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		boolean success = true;
		try {
			beforeOnButtonClicked(bo);
			if (bo instanceof ExtButtonObject) {
				getExtBtnProxy().doAction((ExtButtonObject) bo);
			}
		} catch (BusinessException e) {
			success = false;
			Log.getInstance(this.getClass()).error(e);
			handleException(bo,e);
		}
		afterOnButtonClicked(bo, success);
		this.refreshBtnStatus();
	}

	@Override
	protected void afterOnButtonClicked(nc.ui.pub.ButtonObject bo,
			boolean success) {
		if (bo instanceof ExtButtonObject) {
			if("arap_jkbx#linkVoucher".equals(((ExtButtonObject) bo).getBtninfo().getFlowid())){
				//联查凭证不处理，gbh的联查接口里已经处理了
				return;
			}
			if("arap_jkbx#mergePfAct".equals(((ExtButtonObject) bo).getBtninfo().getFlowid())){
				//制单不处理
				return;
			}
			if (success) {
				showHintMessage(ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_SUCCESS,bo));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void showHintMessage(String hint) {
		super.showHintMessage(hint);
	}

	@Override
	protected void beforeOnButtonClicked(nc.ui.pub.ButtonObject bo)
			throws BusinessException {
		final String[] caSignButtons = new String[] { "Add", "Edit", "Copy" };
		for (String code : caSignButtons) {
			// 按钮是否走ca认证
			if (bo.getCode().equals(code)) {
				checkID();
			}
		}
		if (bo instanceof ExtButtonObject) {
			showHintMessage(ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_ING,bo));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nc.ui.pub.linkoperate.ILinkAdd#doAddAction(nc.ui.pub.linkoperate.ILinkAddData
	 * )
	 */
	public void doAddAction(ILinkAddData adddata) {
		try {
			String vouchids = adddata.getSourceBillID();
			String billtype = adddata.getSourceBillType();

			List<JKBXVO> vos = getIBXBillPrivate().queryVOsByPrimaryKeys(
					new String[] { vouchids },
					BXQueryUtil
							.getDjdlFromBm(billtype, getCache().getDjlxVOS()));

			add(getBillCardPanel(), getBillCardPanel().getName());

			setCurrentpage(BillWorkPageConst.CARDPAGE);

			doLinkAction(vos);

		} catch (Exception e) {
			/*
			 * @res "新增借款报销单据失败!无法定位该记录"
			 */
			handleException(new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000172")));
		}
	}

	/*
	 * (non-Javadoc) 外系统联查报销单据，实现最新的批量接口，同时兼容旧的接口
	 *
	 * @see
	 * nc.ui.pub.linkoperate.ILinkQuery#doQueryAction(nc.ui.pub.linkoperate.
	 * ILinkQueryData)
	 */
	public void doQueryAction(ILinkQueryData querydata) {
		try {
			if (querydata == null)
				return;
			String[] billIDs = null;
			if (querydata instanceof ILinkQueryDataPlural) {
				// 新的批量接口
				billIDs = ((ILinkQueryDataPlural) querydata).getBillIDs();
			} else {
				billIDs = new String[]{querydata.getBillID()};
			}
			List<JKBXVO> voList = getIBXBillPrivate().queryVOsByPrimaryKeys(billIDs,
					BXQueryUtil.getDjdlFromBm(querydata.getBillType(), getCache().getDjlxVOS()));
			getBxParam().setNodeOpenType(BxParam.NodeOpenType_Link);
			doLinkAction(voList);
		} catch (Exception e) {
			/*
			 * @res "联查借款报销单据失败!无法定位该记录"
			 */
			handleException(new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000173")));
		}
	}

	public void doQueryActionFromCmp(ILinkQueryData querydata) {
		try {
			Object[] userObject = (Object[]) querydata.getUserObject();
			String[] vouchids = null;

			if (userObject != null && userObject.length != 0)
				vouchids = (String[]) (userObject)[0];
			else if (querydata.getBillID() != null)
				vouchids = new String[] { querydata.getBillID() };

			String billtype = querydata.getBillType();

			List<JKBXVO> vos = getIBXBillPrivate().queryVOsByPrimaryKeys(
					vouchids,
					BXQueryUtil
							.getDjdlFromBm(billtype, getCache().getDjlxVOS()));

			postInitForCmp(billtype);

			setCurrentpage(BillWorkPageConst.CARDPAGE);

			doLinkAction(vos);

		} catch (Exception e) {
			/*
			 * @res "联查借款报销单据失败!无法定位该记录"
			 */
			handleException((new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000173"))));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nc.ui.pub.linkoperate.ILinkApprove#doApproveAction(nc.ui.pub.linkoperate
	 * .ILinkApproveData)
	 */
	public void doApproveAction(ILinkApproveData approvedata) {
		try {
			String vouchids = approvedata.getBillID();
			String billtype = approvedata.getBillType();

			List<JKBXVO> vos = getIBXBillPrivate().queryVOsByPrimaryKeys(
					new String[] { vouchids },
					BXQueryUtil
							.getDjdlFromBm(billtype, getCache().getDjlxVOS()));

			getCache().putVOArray(vos.toArray(new JKBXVO[] {}));

			CardAction action = new CardAction();
			action.setActionRunntimeV0(this);
			action.changeTab(BillWorkPageConst.CARDPAGE, true, false, vos
					.get(0));

			getBxParam().setNodeOpenType(BxParam.NodeOpenType_Approve);

			doLinkAction(vos);

		} catch (Exception e) {
			// showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			// "UPP2011-000174")/*
			// * @res
			// * "审批借款报销单据失败!无法定位该记录"
			// */);
		}
	}

	/**
	 * @param vos
	 * @throws BusinessException
	 */
	private void doLinkAction(List<JKBXVO> vos) throws BusinessException {
		
		for(JKBXVO vo : vos){
			//added by chendya 联查精度处理
			new CurrencyControlBO().dealBXVOdigit(vo);
		}

		getCache().putVOArray(vos.toArray(new JKBXVO[] {}));

		getCache().setPage(new PageUtil(vos.size(), Page.STARTPAGE, getCache().getMaxRecords()));
		
		if(getCurrWorkPage() == BillWorkPageConst.CARDPAGE && vos.size()>1){
			CardAction action = new CardAction();
			action.setActionRunntimeV0(this);
			action.changeTab(BillWorkPageConst.LISTPAGE, true, false,null);
		}

//added by chendya 处理单据管理节点联查，打开的节点还是单据管理节点时，如果当前模版不是联查单据的模版，则需要加载联查单据对应的单据模版
		if (getCache().getCurrentDjlxbm() != null
				&& getCache().getCurrentDjlxbm().equals(
						vos.get(0).getParentVO().getDjlxbm())) {
			try {
				loadCardTemplet(vos.get(0).getParentVO().getDjlxbm());

				//added by chendya v6.1因联查界面打开的是单据管理列表界面(共用一个模版)，固修改列表界面显示名称(借款联查报销，则显示报销，报销联查借款则显示借款)
				if(getCurrWorkPage() == BillWorkPageConst.LISTPAGE){
					BillListData billListData = getBillListPanel().getBillListData();
					modifyHeadItemShowName(billListData,new String[]{JKBXHeaderVO.YBJE});
					modifyBodyItemShowName(billListData,new String[]{});
					getBillListPanel().setListData(billListData);
				}
			} catch (Exception e) {
				throw new BusinessException(e.getMessage());
			}
		}
//--end

		updateView();

		refreshBtnStatus();
	}

	private void modifyHeadItemShowName(BillListData billListData , String[] fields){
		List<BillItem> itemList = new ArrayList<BillItem>();
		for (int i = 0; i < fields.length; i++) {
			BillItem item = billListData.getHeadItem(fields[i]);
			itemList.add(item);
		}
		modifyItemShowName((BillItem[])itemList.toArray(new BillItem[0]));
	}

	private void modifyBodyItemShowName(BillListData billListData , String[] fields){
		List<BillItem> itemList = new ArrayList<BillItem>();
		for (int i = 0; i < fields.length; i++) {
			BillItem item = billListData.getBodyItem(fields[i]);
			itemList.add(item);
		}
		modifyItemShowName((BillItem[])itemList.toArray(new BillItem[0]));
	}

	private void modifyItemShowName(BillItem[] items){
		if (isJKBill()) {
			modifyPrefixShowName(items, STR_JK, STR_BX);
		} else {
			modifyPrefixShowName(items, STR_BX, STR_JK);
		}
	}

	public void doMaintainAction(ILinkMaintainData maintaindata) {
		try {
			String vouchids = maintaindata.getBillID();

			List<JKBXVO> vos = getIBXBillPrivate().queryVOsByPrimaryKeys(
					new String[] { vouchids }, "");

			getCache().putVOArray(vos.toArray(new JKBXVO[] {}));

			CardAction action = new CardAction();
			action.setActionRunntimeV0(this);
			action.changeTab(BillWorkPageConst.CARDPAGE, true, false, vos
					.get(0));

			getBxParam().setNodeOpenType(BxParam.NodeOpenType_Approve);

			doLinkAction(vos);

		} catch (Exception e) {
			// showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			// "UPP2011-000174")/*
			// * @res
			// * "审批借款报销单据失败!无法定位该记录"
			// */);
		}
	}

	/**
	 * @return
	 * @throws ComponentException
	 *
	 *             Look up 接口
	 */
	private IBXBillPrivate getIBXBillPrivate() throws ComponentException {
		return ((IBXBillPrivate) NCLocator.getInstance().lookup(
				IBXBillPrivate.class.getName()));
	}

	private void initLoginEntityItems(String pkcorp, BillItem[] headitems,
			BillItem[] bodyitems) {
		if (null == pkcorp || "".equals(pkcorp))
			return;
		BusiTypeVO busTypeVO = getBusTypeVO();

		List<String> list = new ArrayList<String>();
		list.addAll(busTypeVO.getCostentity_billitems());
		list.addAll(busTypeVO.getPayentity_billitems());
		list.addAll(busTypeVO.getUseentity_billitems());

		if (headitems != null) {
			for (int i = 0; i < headitems.length; i++) {
				if (headitems[i].getDataType() == 5) {
					if (headitems[i].getComponent() != null
							&& headitems[i].getComponent() instanceof UIRefPane) {
						UIRefPane ref = (UIRefPane) headitems[i].getComponent();
						AbstractRefModel refModel = ref.getRefModel();
						if (refModel != null) {

							refModel.setPk_org(pkcorp);

							if (list.contains(headitems[i].getKey())) {
								ref.setPk_org(pkcorp);
								ref.setValue(null);
							}
						}
						// ref.setPK(itemvos[i].getAttributeValue(bodyitems[i].getKey()));
					}
				}
			}
		}
		if (bodyitems != null) {
			for (int i = 0; i < bodyitems.length; i++) {
				if (bodyitems[i].getDataType() == 5) {
					if (bodyitems[i].getComponent() != null
							&& bodyitems[i].getComponent() instanceof UIRefPane) {
						UIRefPane ref = (UIRefPane) bodyitems[i].getComponent();
						AbstractRefModel refModel = ref.getRefModel();
						if (refModel != null) {

							refModel.setPk_org(pkcorp);
						}
						// ref.setPK(itemvos[i].getAttributeValue(bodyitems[i].getKey()));
					}
				}
			}
		}
	}

	public BatchContrastDetailDialog getContrastDetailDialog() {
		if (batchContrastDetailDialog == null) {
			batchContrastDetailDialog = new BatchContrastDetailDialog(this,
					getNodeCode());
		}
		return batchContrastDetailDialog;
	}

	public String getTabTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				"UPP2011-000175")/*
								 * @res "报销信息"
								 */;
	}

	public void notifyBusiRefresh(String id) throws BusinessException {
		List<JKBXVO> vos = getIBXBillPrivate().queryVOsByPrimaryKeys(
				new String[] { id }, null);
		JKBXVO vo = (vos != null && vos.size() != 0) ? vos.get(0) : null;
		if (vo != null) {
			this.getCache().addVO(vo);
			this.updateView();
		}
	}

	/**
	 * 从结算节点打开报销单据
	 * @param info 结算业务信息
	 */
	@SuppressWarnings("deprecation")
	public void doLinkedFromCmp(nc.vo.cmp.BusiInfo info) {
		if (this.getParent() instanceof ToftPanel) {
			this.setFrame(((ToftPanel) this.getParent()).getFrame());
		}
		if (null != info && null != info.getPk_bill()) {
			setPanelStatus(2);
			this.setTempBillPk(info.getPk_bill());
			this.setTempBilltype(info.getBill_type());
			doQueryActionFromCmp(new ILinkQueryData() {
				public String getBillID() {
					return getTempBillPk();
				}

				public String getBillType() {
					return getTempBilltype();
				}

				public String getPkOrg() {
					return null;
				}
				public Object getUserObject() {
					return null;
				}
			});
		}
	}

	public int getPanelStatus() {
		return panelStatus;
	}

	public void setPanelStatus(int panelStatus) {
		this.panelStatus = panelStatus;
	}

	public String getTempBillPk() {
		return tempBillPk;
	}

	public void setTempBillPk(String tempBillPk) {
		this.tempBillPk = tempBillPk;
	}

	public int getHeadTabIndex() {
		return headTabIndex;
	}

	public void setHeadTabIndex(int headTabIndex) {
		this.headTabIndex = headTabIndex;
	}

	public int getTemppkIndex() {
		return temppkIndex;
	}

	public void setTemppkIndex(int temppkIndex) {
		this.temppkIndex = temppkIndex;
	}

	public String getTempBilltype() {
		return tempBilltype;
	}

	public void setTempBilltype(String tempBilltype) {
		this.tempBilltype = tempBilltype;
	}

	/**
	 * 判断是否需要根据权限调节按钮
	 *
	 * @return
	 */
	public boolean isAdjustButtonByPower() {
		return true;
	}

	/***************************************************************************
	 * 如果需要根据按钮权限调节按钮，则必须返回该功能原生的功能节点
	 *
	 * @param pk_tradetype
	 *            需要展示的交易类型
	 * @return
	 */
	public String getRawFuncode() {
		return PfDataCache.getBillType(this.getCache().getCurrentDjlxbm())
				.getNodecode();
	}

	/**
	 * 设置需要处理的单据类型
	 *
	 * @param tradetype
	 */
	public void setTradeType(String tradetype) {
		DjLXVO djlxVO = getCache().getDjlxVO(tradetype);
		this.getCache().setCurrentDjlx(djlxVO);
	}

	public Object invoke(Object objData, Object objUserData) {
		if (objData instanceof String
				&& "VoucherSaved".equalsIgnoreCase((String) objData)) {
			String seqnum = (String) getAttribute(IRuntimeConstans.settleNo);
			JKBXVO[] selectedvos = getSelBxvos();
			if (selectedvos != null && selectedvos.length != 0) {
				List<String> selectedPK = new ArrayList<String>();
				List<SuperVO> headers = new ArrayList<SuperVO>();
				for (JKBXVO vo : selectedvos) {
					selectedPK.add(vo.getParentVO().getPk_jkbx());
					vo.getParentVO().setJsh(seqnum);
					headers.add(vo.getParentVO());
				}
				try {
					NCLocator.getInstance().lookup(
							nc.itf.cmp.settlement.ICMP4BusiMakeBill.class)
							.notifyCMPAfterMakeBill(BXUiUtil.getPK_group(),
									BXUiUtil.getBXDefaultOrgUnit(), selectedPK,
									seqnum);
					NCLocator.getInstance().lookup(IArapCommonPrivate.class)
							.update(
									headers
											.toArray(new SuperVO[] {}),
									new String[] { JKBXHeaderVO.JSH });
					KeyLock.freeKeyArray(selectedPK.toArray(new String[] {}),
							getBxParam().getPk_user(), null);
					updateView();
				} catch (Exception e) {
					ExceptionHandler.consume(e);
				}
			}
		}
		return null;
	}

	public void showMe(nc.ui.glpub.IParent parent) {
		freeLock();
		parent.getUiManager().add(this, this.getName());
		m_parent = parent;
		setFrame(parent.getFrame());
	}


	public JKBXVO getBillValueVO() throws ValidationException {

		if (getCurrentPageStatus() != BillWorkPageConst.WORKSTAT_NEW
				&& getCurrentPageStatus() != BillWorkPageConst.WORKSTAT_EDIT) {
			JKBXVO currentVO = getCache().getCurrentVO();
			if (currentVO != null){
				return currentVO;
			}
			else{
					return VOFactory.createVO(VOFactory.createHeadVO(getCache().getCurrentDjdl()));
				}
		}

		BxAggregatedVO vo = new BxAggregatedVO(getCache().getCurrentDjdl());
		getBillCardPanel().getBillValueVOExtended(vo);

		JKBXVO bxvo = VOFactory.createVO(vo.getParentVO(), vo.getChildrenVO());
		
		bxvo.setNCClient(true);
		
		if (bxvo.getParentVO().getPk_jkbx() == null
				&& getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE) {
			// 设置是否常用单据/期初单据
			bxvo.getParentVO().setInit(getBxParam().isInit());
			bxvo.getParentVO().setQcbz(new UFBoolean(getBxParam().getIsQc()));
		}

		try {
			new SaveAction(this).prepareVO(bxvo);
			new VOChecker().prepare(bxvo);
		} catch (ValidationException e) {
		} catch (BusinessException e1) {
			throw new ValidationException(e1.getMessage());
		}
		bxvo.setSettlementInfo((nc.vo.cmp.settlement.SettlementAggVO) getAttribute(IRuntimeConstans.SettleVO));
		return bxvo;
	}

	public JKBXVO[] getSelBxvos() {
		List<JKBXVO> list = new ArrayList<JKBXVO>();
		if (getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {
			list = getCache().getSelectedVOs();
		} else if (getCurrWorkPage() == BillWorkPageConst.CARDPAGE) {

			JKBXVO vo = getCache().getCurrentVO();
			if (vo != null) {
				list.add(vo);
			}
		}
		for (JKBXVO vo : list) {
			vo.getParentVO().setInit(getBxParam().isInit());
		}
		return list.toArray(new JKBXVO[list.size()]);
	}

	public void nextClosed() {
		try {
			JKBXVO[] selectedvos = getSelBxvos();

			if (selectedvos != null && selectedvos.length != 0) {
				List<String> selectedPK = new ArrayList<String>();
				for (JKBXVO vo : selectedvos) {
					selectedPK.add(vo.getParentVO().getPk_jkbx());
				}
				if (selectedPK.size() > 0) {
					KeyLock.freeKeyArray(selectedPK.toArray(new String[] {}),
							getBxParam().getPk_user(), null);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	@Override
	public boolean onClosing() {
		boolean result = true;
		if (getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT
				|| getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_NEW) {
			/*
			 * @res "确认要退出吗？"
			 */
			if (nc.ui.pub.beans.MessageDialog.showYesNoDlg(this,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"expensepub_0", "02011002-0019")/* @res "提示" */,
					(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000371"))) == UIDialog.ID_NO) {
				result = false;
			}
		}
		freeLock();
		return result;
	}

	private void freeLock() {
		try {
			JKBXVO[] selectedvos = getSelBxvos();
			String user = BXUiUtil.getPk_user();
			if (selectedvos != null && selectedvos.length != 0) {
				for (JKBXVO bxvo : selectedvos) {
					KeyLock.freeKeyArray(new String[] { bxvo.getParentVO()
							.getPk_jkbx() }, user, bxvo.getParentVO()
							.getTableName());
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	public Map<String, List<SuperVO>> getReimRuleDataMap() {
		return reimRuleDataMap;
	}

	public Map<String, SuperVO> getExpenseMap() {
		return expenseMap;
	}

	public Map<String, SuperVO> getReimtypeMap() {
		return reimtypeMap;
	}

	public void setExpenseMap(Map<String, SuperVO> expenseMap) {
		this.expenseMap = expenseMap;
	}

	public void setReimRuleDataMap(Map<String, List<SuperVO>> reimRuleDataMap) {
		this.reimRuleDataMap = reimRuleDataMap;
	}

	public void setReimtypeMap(Map<String, SuperVO> reimtypeMap) {
		this.reimtypeMap = reimtypeMap;
	}

	public void setFuncMenuActions(List<Action> actionList, String funcCode) {
		this.setMenuActions(actionList, funcCode);
	}

	public List<Action> getFuncMenuActions() {
		return this.getMenuActions();
	}

	public List<String> getPanelEditableKeyList() {
		return panelEditableKeyList;
	}

	public void setPanelEditableKeyList(List<String> panelEditableKeyList) {
		this.panelEditableKeyList = panelEditableKeyList;
	}
}