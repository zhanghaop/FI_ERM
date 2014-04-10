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
 *         �����������
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
	 * @see ע�⣺����loadBillListTemplateΪ��д������djlxbm
	 *      �����ȸ�Ϊnull��String��,���Ҽ���ģ�崦��������djlxbm�� ������
	 *      ���ݹ����Լ�����¼�룬���ݹ�������¼�봦����ģ����Ҫ����strDjlxbm�������Ĳ������������
	 *      nodekey��Ҫ����գ�pub_systemplate ����nodekeyҲΪ�ա�ע���2��3��������Ҫд��
	 * @author liansg(add by liansg 2010/01/25)
	 * 
	 * */
	@Override
	@SuppressWarnings("restriction")
	protected void loadBillListTemplate() {
		// ���ص����б�ģ��
		String djlxbm = null;
		getBillListPanel().loadTemplet(getNodeCode(), null,
				getBxParam().getPk_user(), BXUiUtil.getPK_group(), djlxbm); // ���ص����б�ģ��

		// �����б�����Զ�����
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
	 * @return �������ͱ���
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
																				 * "���ݽڵ㲻���ҵ���Ӧ�ĵ������ͣ�"
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
											 * @res "��ѯ����"
											 */, nodeCode, oldnodeCode, nodeKey);

		}
		return queryDialog;
	}

	/**
	 * @see ע�⣺����loadCardTempletΪ��д������djlxbm
	 *      �����ȸ�Ϊnull��String��,���Ҽ���ģ�崦��������djlxbm�� ������
	 *      ���ݹ����Լ�����¼�룬���ݹ�������¼�봦����ģ����Ҫ����strDjlxbm�������Ĳ������������
	 *      nodekey��Ҫ����գ�pub_systemplate ����nodekeyҲΪ�ա�
	 * @author liansg(add by liansg)
	 * 
	 * */
	@Override
	public void loadCardTemplet(String strDjlxbm) {
		String djlxbm = null;
		getBillCardPanel().loadTemplet(getNodeCode(), null,
				getBxParam().getPk_user(), BXUiUtil.getPK_group(), djlxbm);// ���ص��ݿ�Ƭģ��
		setCardTemplateLoaded(true);
		// ����Ƭ�����Զ�������ʾ
		dealCardUserDefItem();
	}

	/**
	 * @see ��Buttonsת���ɶ�Ӧ�Ķ�Ӧ��Actions
	 * @return ����Actions���б�
	 */
	public List<Action> getActions() {
		List<Action> actionslist = Lists.newArrayList();
		ButtonObject[] ret = getDjButtons();
		// ����Ӧ�İ�ťת����actions
		Action[] actions = ButtonActionConvert.buttonToAction(this, ret);
		for (int i = 0; i < actions.length; i++) {
			actionslist.add(actions[i]);
		}
		return actionslist;
	}

	private List<Action> getActions4Cmp() {
		List<Action> actionslist = Lists.newArrayList();
		ButtonObject[] ret = getDjButtons4Cmp();
		// ����Ӧ�İ�ťת����actions
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
	 * @return �˴��漰UIFactory 2 �Ķ�Ӧ��Context ��Ҫ��Ϊ����
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
	 * @see ��ö�Ӧ��BusiInfo��Ϣ������ҵ��ҳǩ
	 * 
	 */
	public void handleEvent(AppEvent event) {
		if (event.getType().equals(BXConstans.BROWBUSIBILL)) {
			try {
				BusiInfo busiinfo = (BusiInfo) event.getContextObject();
				if (busiinfo != null && busiinfo.getBill_type() != null
						&& busiinfo.getBill_type().startsWith("26")) {
					// �������൥�ݲŽ�������
					doLinkedFromCmp(busiinfo);
				}
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	/**
	 * @see ���õ�ǰ������ҳǩ�Ľ��ţ����ڼ��ذ�ť������Ȩ��
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