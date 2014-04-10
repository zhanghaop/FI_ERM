package nc.ui.arap.bx;

import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.Lists;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.impl.er.proxy.ProxyDjlx;
import nc.itf.cmp.pub.ITabExComponent4BX;
import nc.itf.uap.pf.IPFMetaModel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.er.util.ButtonActionConvert;
import nc.ui.pub.ButtonObject;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.cmp.BusiInfo;
import nc.vo.cmp.settlement.NodeType;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.querytemplate.querytype.IQueryType;
import nc.vo.uif2.LoginContext;

/**
 * @author twei
 * @author liansg
 * 
 *         nc.ui.arap.bx.BXBillMainPanelDJ
 * 
 *         借款报销单据入口
 */
public class BXBillMainPanelDJ extends BXBillMainPanel implements
		AppEventListener, ITabExComponent4BX {

	private static final long serialVersionUID = 4549878138508760576L;

	private LoginContext loginContext = null;

	public BXBillMainPanelDJ() {
		initialize();
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.ui.arap.bx.BXBillMainPanel#initUi()
	 */
	@Override
	protected void initUi() {
		add(getCardContentPanel(), "CARD");
		try {
			loadCardTemplet();
		} catch (Exception e) {
			handleException(e);
		}
		getBillCardPanel().setEnabled(false);
		setCurrentpage(BillWorkPageConst.CARDPAGE);
	}

	/**
	 * @see 注意：方法loadBillListTemplate为重写方法，djlxbm
	 *      必须先赋为null（String）,并且加载模板处参数采用djlxbm， 区别于
	 *      单据管理以及单据录入，单据管理，单据录入处加载模板需要采用strDjlxbm这个传入的参数。此种情况
	 *      nodekey需要传入空，pub_systemplate 表中nodekey也为空。注意第2，3个参数不要写反
	 * @author liansg(add by liansg 2010/01/25)
	 * 
	 * */
	@Override
	@SuppressWarnings("restriction")
	protected void loadBillListTemplate() {
		// 加载单据列表模板
		String djlxbm = null;
		getBillListPanel().loadTemplet(getNodeCode(), null,
				getBxParam().getPk_user(), BXUiUtil.getPK_group(), djlxbm); // 加载单据列表模板

		// 处理列表界面自定义项
		dealListUserDefItem();
		getTempletEventAgent().initBillListPane(getBillListPanel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.ui.arap.bx.BXBillMainPanel#getDjlxvosByNodeCode()
	 */
	@Override
	protected DjLXVO[] getDjlxvosByNodeCode() {
		String group = WorkbenchEnvironment.getInstance().getGroupVO()
				.getPrimaryKey();
		DjLXVO[] djLXVOs = new DjLXVO[] {};
		try {
			djLXVOs = new DjLXVO[] { ProxyDjlx.getIArapBillTypePublic()
					.getDjlxvoByDjlxbm(getBillType(), group) };
		} catch (Exception e) {
			handleException(e);
		}
		return djLXVOs;
	}

	/**
	 * @return 单据类型编码
	 */
	protected String getBillType() {
		if (getModuleCode().equals(BXConstans.BXCLFJK_CODE))
			return BXConstans.BILLTYPECODE_CLFJK;
		else if (getModuleCode().equals(BXConstans.BXMELB_CODE))
			return "2632";
		else if (getModuleCode().equals(BXConstans.BXCLFBX_CODE))
			return BXConstans.BILLTYPECODE_CLFBX;
		IPFMetaModel ipf = NCLocator.getInstance().lookup(IPFMetaModel.class);
		String tempBilltype;
		try {
			tempBilltype = ipf.getBilltypeByNodecode(getModuleCode());
			return tempBilltype;
		} catch (BusinessException e) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0038")/*
																				 * @res
																				 * "根据节点不能找到对应的单据类型！"
																				 */);
		}

	}

	@Override
	public QueryConditionDLG getQryDlg() {
		if (queryDialog == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			tempinfo.setPk_Org(BXUiUtil.getPK_group());
			tempinfo.setCurrentCorpPk(BXUiUtil.getBXDefaultOrgUnit());
			tempinfo.setUserid(BXUiUtil.getPk_user());
			tempinfo.setQueryType(IQueryType.NEITHER);

			String oldnodeCode = getNodeCode();
			String nodeCode = "";
			String nodeKey = getCache().getCurrentDjlxbm();
			if (getCache().getCurrentDjlxbm().contains("264X-")) {
				nodeCode = BXConstans.BXCLFBX_CODE;
				nodeKey = "2641";
			} else if (getCache().getCurrentDjlxbm().contains("263X-")) {
				nodeCode = BXConstans.BXCLFJK_CODE;
				nodeKey = "2631";
			} else {
				nodeCode = oldnodeCode;
			}
			tempinfo.setFunNode(nodeCode);
			queryDialog = new BxQueryDLG(this, null, tempinfo,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
							"UC000-0002782")/*
											 * @res "查询条件"
											 */, nodeCode, oldnodeCode, nodeKey);

		}
		return queryDialog;
	}

	/**
	 * @see 注意：方法loadCardTemplet为重写方法，djlxbm
	 *      必须先赋为null（String）,并且加载模板处参数采用djlxbm， 区别于
	 *      单据管理以及单据录入，单据管理，单据录入处加载模板需要采用strDjlxbm这个传入的参数。此种情况
	 *      nodekey需要传入空，pub_systemplate 表中nodekey也为空。
	 * @author liansg(add by liansg)
	 * 
	 * */
	@Override
	public void loadCardTemplet(String strDjlxbm) {
		String djlxbm = null;
		getBillCardPanel().loadTemplet(getNodeCode(), null,
				getBxParam().getPk_user(), BXUiUtil.getPK_group(), djlxbm);// 加载单据卡片模板
		setCardTemplateLoaded(true);
		// 处理卡片界面自定义项显示
		dealCardUserDefItem();
	}

	/**
	 * @see 将Buttons转换成对应的对应的Actions
	 * @return 返回Actions的列表
	 */
	public List<Action> getActions() {
		List<Action> actionslist = Lists.newArrayList();
		ButtonObject[] ret = getDjButtons();
		// 将对应的按钮转换成actions
		Action[] actions = ButtonActionConvert.buttonToAction(this, ret);
		for (int i = 0; i < actions.length; i++) {
			actionslist.add(actions[i]);
		}
		return actionslist;
	}

	private List<Action> getActions4Cmp() {
		List<Action> actionslist = Lists.newArrayList();
		ButtonObject[] ret = getDjButtons4Cmp();
		// 将对应的按钮转换成actions
		Action[] actions = ButtonActionConvert.buttonToAction(this, ret);
		for (int i = 0; i < actions.length; i++) {
			actionslist.add(actions[i]);
		}
		return actionslist;
	}

	public IFunNodeClosingListener getCloseListener() {
		return null;
	}

	public List<Action> getEditActions() {
		return null;
	}

	public JComponent getExComponent() {
		return this;
	}

	/**
	 * @return 此处涉及UIFactory 2 的对应的Context 主要作为载体
	 * 
	 */
	public void setLoginContext(LoginContext context) {
		this.loginContext = context;
	}

	public void setNodeType(NodeType nodeType) {

	}

	@Override
	public List<Action> getActions(String[] actionnames) {
		return getActions4Cmp();
	}

	/**
	 * @author chendya
	 * @see 获得对应的BusiInfo信息，传入业务页签
	 * 
	 */
	public void handleEvent(AppEvent event) {
		if (event.getType().equals(BXConstans.BROWBUSIBILL)) {
			try {
				BusiInfo busiinfo = (BusiInfo) event.getContextObject();
				if (busiinfo != null && busiinfo.getBill_type() != null
						&& busiinfo.getBill_type().startsWith("26")) {
					// 仅报销类单据才结算联查
					doLinkedFromCmp(busiinfo);
				}
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	/**
	 * @see 设置当前被加载页签的结点号，用于加载按钮，设置权限
	 * 
	 * */
	@Override
	public void setLoadedNodecode(String nodecode) throws BusinessException {
		setNodeCode(nodecode);

	}

	@Override
	public void setFuncMenuActions(List<Action> actionList, String funcCode) {
		this.setMenuActions(actionList, funcCode);
	}

	@Override
	public List<Action> getFuncMenuActions() {
		return this.getMenuActions();
	}

	@Override
	public List<Action> getActions(String nodetype) {
		return getActions4Cmp();
	}

	@Override
	public List<Action> getEditActions(String nodetype) {
		return null;
	}
}