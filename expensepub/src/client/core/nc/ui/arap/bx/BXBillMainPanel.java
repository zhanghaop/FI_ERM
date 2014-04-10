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
 *         �������������
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

	private Map<String, List<SuperVO>> reimRuleDataMap = new HashMap<String, List<SuperVO>>(); // �������򻺴�����
	private Map<String, SuperVO> expenseMap = new HashMap<String, SuperVO>(); // �������ͻ�������
	private Map<String, SuperVO> reimtypeMap = new HashMap<String, SuperVO>(); // �������ͻ�������

	private BxParam bxParam;// ��������

	private boolean isCardTemplateLoaded;// ��Ƭģ���Ƿ��Ѿ�����

	private int temppkIndex = 0;// ��ʱ����pk����

	protected int panelStatus = 0; // ��嵱ǰ״̬ //0��ͨ��1������2��ѯ,3����,4�޸�

	protected String tempBillPk; // ��ʱ����pk

	protected String tempBilltype; // ��ʱ����pk

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
	 * ���潻�����͵ĵ������ݽṹ
	 */
	protected Map<String, BillData> djlxmBillDataCache = new HashMap<String, BillData>();

	/**
	 * �б��µ������ݽṹ
	 */
	protected BillListData listData;
	
	/**
	 * ��Ƭ�µ������ݽṹ
	 */
	protected BillData billData;

	/**
	 * �������֯�����ֶ�
	 */
	private Map<String,List<String>> orgRefFieldsMap = new HashMap<String, List<String>>();
	
	public BXBillMainPanel() {
		super();
	}

	/**
	 * ����֯�������ֶμ���
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
	 * ������֯�������ֶ�
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
	 * �б�����Զ��������
	 */
	private BXCompositeBillListDataPrepare bxCompositeBillListDataPrepare;
	private BXUserdefitemContainerListPreparator bxUserdefitemContainerListPreparator;

	/**
	 * ��Ƭ�����Զ��������
	 */
	private BXCompositeBillDataPrepare bxCompositeBillDataPrepare;
	private BXUserdefitemContainerPreparator bxUserdefitemContainerPreparator;

	/**
	 * �������Զ������ѯ�����б�
	 */
	List<UserdefQueryParam> bxParams;

	/**
	 * ���Զ������ѯ�����б�
	 */
	List<UserdefQueryParam> jkParams;

	/**
	 * ���Զ�����������ѯ�����б�
	 */
	List<QueryParam> jkQueryParamList;

	/**
	 * �������Զ�����������ѯ�����б�
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
			// ��ͷ
			QueryParam param = new QueryParam();
			param.setRulecode(ERM_263X_H);
			//param.setMdfullname(BXConstans.JK_HEAD_MDFULLNAME);
			jkQueryParamList.add(param);

			// ����

			// ҵ��ҳǩ
			param = new QueryParam();
			param.setRulecode(ERM_263X_B);
			//param.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			jkQueryParamList.add(param);

			// ����ҳǩ
			param = new QueryParam();
			param.setRulecode(ERM_263X_B);
			//param.setMdfullname(BXConstans.JK_BODY_FINITEM_MDFULLNAME);
			jkQueryParamList.add(param);

			// ��������ҳǩ
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
			// ��ͷ
			QueryParam param = new QueryParam();
			
			param.setRulecode(ERM_264X_H);
//			param.setMdfullname(BXConstans.BX_HEAD_MDFULLNAME);
			bxQueryParamList.add(param);

			// ����
			// ҵ��ҳǩ
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
																		 * "���"
																		 */;

	private  final String STR_BX = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("expensepub_0", "02011002-0042")/*
																		 * @res
																		 * "����"
																		 */;

	/**
	 * �޸Ĳ�ѯ����ǰ׺����
	 *
	 * @author chendya
	 * @param items
	 * @param src
	 *            ԭ����
	 * @param dest
	 *            Ŀ������
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
	 * @author chendya ����Ƭ�����Զ�������ʾ
	 */
	protected void dealCardUserDefItem() {
		BillData billData = getBillCardPanel().getBillData();
		getBxCompositeBillDataPrepare().prepareBillData(billData);
		getBillCardPanel().setBillData(billData);

	}

	/**
	 * @author chendya �����б�����Զ�������ʾ
	 */
	protected void dealListUserDefItem() {
		BillListData billListData = getBillListPanel().getBillListData();
		getBillListPanel().setListData(billListData,
				getBxCompositeBillListDataPrepare());
	}

	/**
	 * @author chendya �Ƿ����
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
	 * ���ر������Զ������ѯ�����б�
	 *
	 * @return
	 */
	protected List<UserdefQueryParam> getBXUserdefQueryParamList() {
		if (bxParams == null) {
			bxParams = new ArrayList<UserdefQueryParam>();
			// ��ͷ(β)
			UserdefQueryParam paramHead = new UserdefQueryParam();
			paramHead.setRulecode(ERM_264X_H);
			paramHead.setPos(IBillItem.HEAD);
			paramHead.setPrefix(BXConstans.HEAD_USERDEF_PREFIX);
			bxParams.add(paramHead);

			// ����
			// ҵ��ҳǩ
			UserdefQueryParam paramBody = new UserdefQueryParam();
			paramBody.setRulecode(ERM_264X_B);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			bxParams.add(paramBody);
		}

		return bxParams;
	}

	/**
	 * @author chendya ���ؽ��Զ������ѯ�����б�
	 * @return
	 */
	protected List<UserdefQueryParam> getJKUserdefQueryParamList() {
		if (jkParams == null) {
			jkParams = new ArrayList<UserdefQueryParam>();
			// ��ͷ(β)
			UserdefQueryParam paramHead = new UserdefQueryParam();
			//paramHead.setMdfullname(BXConstans.JK_HEAD_MDFULLNAME);
			paramHead.setRulecode(ERM_263X_H);
			paramHead.setPos(IBillItem.HEAD);
			paramHead.setPrefix(BXConstans.HEAD_USERDEF_PREFIX);
			jkParams.add(paramHead);

			// ����
			// ҵ��ҳǩ
			UserdefQueryParam paramBody = new UserdefQueryParam();
			//paramBody.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			paramBody.setRulecode(ERM_263X_B);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			jkParams.add(paramBody);

			// ����ҳǩ
			paramBody = new UserdefQueryParam();
			//paramBody.setMdfullname(BXConstans.JK_BODY_BUSITEM_MDFULLNAME);
			paramBody.setRulecode(ERM_263X_B);
			paramBody.setPos(IBillItem.BODY);
			paramBody.setPrefix(BXConstans.BODY_USERDEF_PREFIX);
			jkParams.add(paramBody);

			// ��������ҳǩ
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

	

	// begin--added by chendya �б������Ӳ�ѯ����

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
	 * ��ѯ������С��������
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
				// begin--added by chendya ��Ӳ�ѯ����

				// getListContentPanel().add(getBillListPanel(), "Center");
				getListContentPanel().add(getSplitPane(), "Center");
				// --end
				listContentPanel
						.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2011", "UPP2011-000168")/*
																	 * @res "�б�"
																	 */);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return listContentPanel;
	}

	// begin--added by chendya ��Ƭ������ӡ����ء���ť���

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

				// begin--added by chendya ��Ƭ������ӷ��ذ�ť�Լ���ҳ��ť�����
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
		 * @res "��������"
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

// begin--added by chendya �������ã�����Զ�̵��ô��������Ч��
		//�ڵ�Ȩ��
		callitems.add(new PermissionOrgVoCall(this));

		//���Ž�ɫ
		callitems.add(new RoleVoCall());

		if(!getBxParam().isInit()){
			if(BXConstans.BXMNG_NODECODE.equals(getNodeCode())||BXConstans.BXBILL_QUERY.equals(getNodeCode())||BXConstans.BXLR_QCCODE.equals(getNodeCode())){
				//���浥��ģ�棬�������Զ�����
				callitems.add(new BXBillListTemplateCall(this));
			}
		}

		//����ڼ�
		callitems.add(new QcDateCall());

		//��ǰ��¼��������ҵ��Ա�ĸ��������ʺ�Ĭ�ϱ������ӻ���Ϣ
		callitems.add(new UserBankAccVoCall());

		//���沿�Ź����ĳɱ�����//��Ч������
		//callitems.add(new BXDeptRelCostCenterCall());

		//��ǰҵ��ʱ�����е���֯��汾
		//callitems.add(new OrgMultiVersionVORemoteCall(this));

		//���浱ǰ��¼��������֯�²��Ŷ�汾
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
	 * �������µĵ�������VO
	 * @author chendya
	 * @param vos
	 * @return
	 */
	private DjLXVO getLatestDjlxVO(DjLXVO[] vos){
		if(vos==null||vos.length==0){
			throw new IllegalArgumentException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0004")/*@res "����Ĳ�������Ϊ��"*/);
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
	 * �����ʼ������
	 */
	@Override
	@SuppressWarnings( { "deprecation" })
	protected void postInit() {

		DjLXVO[] djlxvosByNodeCode = null;
		this.setNodeCode(getModuleCode());
		List<DjLXVO> djlxs = new ArrayList<DjLXVO>();

		// ��ʼ������������Ϣ
		if (getCache().getCurrentDjdl() == null) {
			djlxvosByNodeCode = getDjlxvosByNodeCode();

			// У����ĵ�������
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
				 * @res "�ýڵ㵥�������ѱ���棬���ɲ����ڵ㣡"
				 */
				showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000171"));
			}
			getCache().setDjlxVOS(validDjlx);

			// �˴�Ĭ�϶�ȡ��һ����������
			getCache().setCurrentDjlx(getLatestDjlxVO(validDjlx));

		}

		// �������ã���cmp���غ�������ع������߼��ŵ�����
		try {
			initPublic();
		} catch (BusinessException e) {
			showErrorMessage(e.getMessage());
			return;
		}

		// ����ģ���¼�����
		setTempletEventAgent(new UIEventagent(this));

		// �����ʼ������
		initUi();

		setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);

		// ��ʼ���������û���
		initActionRuntime();

		// ��ʼ����ť
		ButtonObject[] btArray = getDjButtons();
		setButtons(btArray);

		// ��ʼ����ť��ݼ���
		initActions();

		// ˢ�°�ť״̬
		refreshBtnStatus();
	}

	private void initPublic() throws BusinessException {
		// ����Զ�̵���
		callRemoteService();
	}

	/**
	 * �����ʼ������
	 *
	 * @param billtype
	 * @throws Exception
	 */
	protected void postInitForCmp(String billtype) throws Exception {
		DjLXVO djlx = null;
		/*
		 * @res "��ǰ��˾δ����ý�������"
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
		// ��ʼ������������Ϣ
		getCache().setDjlxVOS(djlxvosByNodeCode);

		getCache().setCurrentDjlx(djlxvosByNodeCode[0]);

		String strDjlxbm = getCache().getCurrentDjlxbm();

		BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(strDjlxbm);

		String nodecode2 = billtypevo.getNodecode();

		this.setNodeCode(nodecode2);

		// �������ã���cmp���غ�������ع������߼��ŵ�����
		try {
			initPublic();
		} catch (BusinessException e) {
			handleException(e);
			return;
		}
		// ����ģ���¼�����
		setTempletEventAgent(new UIEventagent(this));

		// ��ʼ���������û���
		initActionRuntime();

		// �����ʼ������
		initUIFromCmp();

		// ���㡰ҵ����Ϣ��ҳǩ��ʼ����ť
		initAction4Cmp();

		refreshBtnStatus();
	}

	/**
	 * Ϊ����ڵ��л�����ҵ����Ϣ��ҳǩ��ʼ����ť
	 *
	 * @author chendya
	 */
	@SuppressWarnings("deprecation")
	private void initAction4Cmp() {
		setButtons(getDjButtons4Cmp());
	}

	/**
	 * Ϊ����ڵ��л�����ҵ����Ϣ��ҳǩ��ʼ����ť
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
			if (btn.getCode().equals("����") || btn.getCode().equals("��ӡ����")) {/*-=notranslate=-*/
				buttonList.add(btn);
			}
		}
		return buttonList.toArray(new ButtonObject[0]);
	}

	/**
	 * �����л�ҵ����Ϣʱ����
	 * @throws Exception
	 */
	private void initUIFromCmp() throws Exception {
		add(getCardContentPanel(), "CARD");
		loadCardTemplate(true);
		getBillCardPanel().setEnabled(false);
		setCurrentpage(BillWorkPageConst.CARDPAGE);
	}

	/**
	 * �����ʼ������
	 */
	protected abstract void initUi();

	/**
	 * ��ʼ���������û���
	 */
	private void initActionRuntime() {
		this.setAttribute(BXConstans.KEY_BILLTYPE, getCache()
				.getCurrentDjlxbm());
		this.setAttribute(BXConstans.KEY_PARENTBILLTYPE, getParentBillType());
	}

	protected abstract DjLXVO[] getDjlxvosByNodeCode();

//added by chendya ��Ӻ�uif2��ͬ�ķ�ҳ��
	BXPaginationBar pageBarPanel;

	public BXPaginationBar getPageBarPanel() {
		if(pageBarPanel==null){
			pageBarPanel = new BXPaginationBar(this);
		}
		return pageBarPanel;
	}

//--end

	/**
	 * �б����
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
	 * �б������ӳ����Ӽ���
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
	 * �����Ӽ���
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
		/** ��Ӿ��ȼ��� **/
		BXUiUtil.addDecimalListenerToListpanel(listPanel,getCache());

		BillRendererVO voCell = new BillRendererVO();
		voCell.setShowThMark(true);
		voCell.setShowZeroLikeNull(true);

		listPanel.getParentListPanel().setTotalRowShow(true);
		listPanel.getChildListPanel().setShowFlags(voCell);
		listPanel.getParentListPanel().setShowFlags(voCell);
		// //���Ƚ�panel��Ϊ���ã�֮��ѳ���selected��Ϊ������
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

		//added by chendya ֧�ַ�ҳ��
		listPanel.addHeadNavigatePanel(getPageBarPanel());
	}

	/**
	 * @param ybField
	 * @param fbField
	 * @param bbField
	 *
	 *            ���ý���ֶε����ݾ���
	 */
	protected void setJeFieldDigits(String ybField, String bbField) {
		// FIXME ���������Ϣ��ȷ������ʱȥ��
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
	 *             ����Ԫ��VO�б�׷������ǰ�б�
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
	 *             ����Ԫ��VO�б�, ����յ�ǰ�б�
	 */
	public void setListVO(List<JKBXHeaderVO> vos) throws BusinessException {

		getBillListPanel().getHeadBillModel().clearBodyData();
		getBillListPanel().getBodyBillModel().clearBodyData();

		JKBXHeaderVO[] headvos = new JKBXHeaderVO[vos.size()];
		vos.toArray(headvos);

		if ((headvos == null || headvos.length < 1)) {
			showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"2006030102", "UPP2006030102-000424")/*
														 * @res "û����ص���"
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
	 * ˢ�½���
	 */
	public void updateView() {

		if (getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {
			updateListView();
		} else {
			updateCardView();
		}
	}

	/**
	 * ˢ�¿�Ƭ����
	 */
	private void updateCardView() {

		getBillCardPanel().setBillValueVO(getCache().getCurrentVO());

		setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);
	}

	/**
	 * ˢ���б����
	 */
	public void updateListView() {

		JKBXHeaderVO[] headvos = getCache().getCurrentPageVOs();
		/*
		 * UI����2����Ԫ���ݣ�����ֵӦ����Ԫ���ݷ�ʽ ��Ԫ���ݷ���
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
			//added by chendya setBodyDataVO��������Զ�setChangeTable
			getBillListPanel().getHeadBillModel().setBodyDataVO(clonevos);

			//������ʾpkֵ
			getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(clonevos);
			//--end

			// begin--added by chendya
			// ����Ԫ���ݷ�ʽgetBillListPanel().getBillListData().setHeaderValueObjectByMetaData(clonevos)
			// �޷��������Ƿ�ѡ��������ֶ�����
			for (int j = 0; j < clonevos.length; j++) {
				UFBoolean selected = clonevos[j].getSelected();
				// �Ƿ�ѡ��
				getBillListPanel().getHeadBillModel().setValueAt(selected, j,
						JKBXHeaderVO.SELECTED);
			}
			// --end
			// ���辫��
			BXUiUtil.resetBBHLDecimal(getBillListPanel(), clonevos);

		}

		try {
			// �޸ĵ����������Ƽ��ع�ʽ
			BillItem djlxmcBillItem = getBillListPanel().getBillListData().getHeadItem(JKBXHeaderVO.DJLXMC);
			BXUiUtil.modifyLoadFormula(djlxmcBillItem, "billtypename");
			getBillListPanel().getHeadBillModel().execLoadFormula();
		} catch (Exception e) {
			Logger.debug(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"2006030102", "UPP2006030102-000037")/*
														 * @res "���ع�ʽ����:"
														 */
					+ e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.arap.bx.BaseUIPanel#getBillCardPanel()
	 *
	 * ��Ƭ����
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
	 * @return �����͵������ͱ���
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
	 *      static int WORKSTAT_EDIT = 3; //�޸ı༭״̬ public static int
	 *      WORKSTAT_NEW = 1; //�����༭״̬ public static int WORKSTAT_BROWSE =0;
	 *      //���״̬
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
																			 * "�ýڵ㵥�������Ѿ���棬���ܽ��в���!"
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

		((BXBillCardPanel) getBillCardPanel()).initData(); // ��Ƭ�����ʼ��һЩ����
		refreshBodyActionStatus();// ������尴ť״̬

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
	 * @return ���������ѯ
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

						//�����в�����ť״̬
						refreshBtnStatus(new String[] { "�в���", "����", "������","������", "ճ��������β", "ɾ��", "ճ����" }); /*-=notranslate=-*/

						//��������
						final String currTabcode = e.getBtvo().getTabcode();
						cardPanel.getBillModel(currTabcode).loadLoadRelationItemValue();

						//����ҳǩ���ɱ༭
						setContrastTabEnable(false);

						showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0001",null,new String[]{e.getBtvo().getTabname()})/*@res "���л���"*/);
					}

				}, IBillItem.BODY);

	}

	/**
	 * ���ó���Ҳǩ�Ƿ�ɱ༭
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
			// ���ص���ģ��
			getBillCardPanel().loadTemplet(getNodeCode(), null,getBxParam().getPk_user(), BXUiUtil.getPK_group(),strDjlxbm); 
			setCardTemplateLoaded(true);
			
			// ����Ƭ�����Զ�������ʾ
			dealCardUserDefItem();
			djlxmBillDataCache.put(strDjlxbm, getBillCardPanel().getBillData());
		}
	}

	public void handleException(ButtonObject bo,java.lang.Throwable ex){
		BXUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(ButtonUtil.MSG_TYPE_FAIL,bo),ex);
	}

	/**
	 * ͳһ�����쳣��Ϣ
	 * @author chendya
	 * @param e
	 */
	public void handleException(java.lang.Throwable ex) {
		BXUiUtil.showUif2DetailMessage(this, "", ex);
	}

	@Override
	public void showErrorMessage(String e) {
//begin-- Remarked by chendya V6�쳣��Ϣ�����򣬸���uif2���쳣��ʾ��ʽ(״̬���Ϸ���ʾ�쳣��Ϣ)
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
	 * @return ���ݵ�ǰ�ĵ������ͱ���ȡҵ������VO (busitype.xml��������)
	 * @see BusiTypeVO
	 */
	public BusiTypeVO getBusTypeVO() {
		return BXUtil.getBusTypeVO(getCache().getCurrentDjlxbm(), getCache().getCurrentDjdl());
	}

	/**
	 * @return �Ƿ�ģ��չʾ
	 */
	@Override
	public boolean isTempletView(String tableCode) {
		Boolean isTempletView = getBusTypeVO().getIsTableTemplet().get(tableCode);
		if (isTempletView == null)
			isTempletView = true;
		return isTempletView;
	}

	/**
	 * ��ʼ���������Ͳ���
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
	 *            ��ʾ��־��Ϣ
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
	 *             ��ʼ������Ի���
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
	 *             ��ʼ��������Ի���
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
	 *             ��ʼ�����ݽ���Ի���
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
	 * CAУ��
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
			 * @res "�����֤δͨ��������ʧ��!"
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
				//����ƾ֤������gbh������ӿ����Ѿ�������
				return;
			}
			if("arap_jkbx#mergePfAct".equals(((ExtButtonObject) bo).getBtninfo().getFlowid())){
				//�Ƶ�������
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
			// ��ť�Ƿ���ca��֤
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
			 * @res "������������ʧ��!�޷���λ�ü�¼"
			 */
			handleException(new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000172")));
		}
	}

	/*
	 * (non-Javadoc) ��ϵͳ���鱨�����ݣ�ʵ�����µ������ӿڣ�ͬʱ���ݾɵĽӿ�
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
				// �µ������ӿ�
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
			 * @res "�����������ʧ��!�޷���λ�ü�¼"
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
			 * @res "�����������ʧ��!�޷���λ�ü�¼"
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
			// * "������������ʧ��!�޷���λ�ü�¼"
			// */);
		}
	}

	/**
	 * @param vos
	 * @throws BusinessException
	 */
	private void doLinkAction(List<JKBXVO> vos) throws BusinessException {
		
		for(JKBXVO vo : vos){
			//added by chendya ���龫�ȴ���
			new CurrencyControlBO().dealBXVOdigit(vo);
		}

		getCache().putVOArray(vos.toArray(new JKBXVO[] {}));

		getCache().setPage(new PageUtil(vos.size(), Page.STARTPAGE, getCache().getMaxRecords()));
		
		if(getCurrWorkPage() == BillWorkPageConst.CARDPAGE && vos.size()>1){
			CardAction action = new CardAction();
			action.setActionRunntimeV0(this);
			action.changeTab(BillWorkPageConst.LISTPAGE, true, false,null);
		}

//added by chendya �����ݹ���ڵ����飬�򿪵Ľڵ㻹�ǵ��ݹ���ڵ�ʱ�������ǰģ�治�����鵥�ݵ�ģ�棬����Ҫ�������鵥�ݶ�Ӧ�ĵ���ģ��
		if (getCache().getCurrentDjlxbm() != null
				&& getCache().getCurrentDjlxbm().equals(
						vos.get(0).getParentVO().getDjlxbm())) {
			try {
				loadCardTemplet(vos.get(0).getParentVO().getDjlxbm());

				//added by chendya v6.1���������򿪵��ǵ��ݹ����б����(����һ��ģ��)�����޸��б������ʾ����(������鱨��������ʾ��������������������ʾ���)
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
			// * "������������ʧ��!�޷���λ�ü�¼"
			// */);
		}
	}

	/**
	 * @return
	 * @throws ComponentException
	 *
	 *             Look up �ӿ�
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
								 * @res "������Ϣ"
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
	 * �ӽ���ڵ�򿪱�������
	 * @param info ����ҵ����Ϣ
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
	 * �ж��Ƿ���Ҫ����Ȩ�޵��ڰ�ť
	 *
	 * @return
	 */
	public boolean isAdjustButtonByPower() {
		return true;
	}

	/***************************************************************************
	 * �����Ҫ���ݰ�ťȨ�޵��ڰ�ť������뷵�ظù���ԭ���Ĺ��ܽڵ�
	 *
	 * @param pk_tradetype
	 *            ��Ҫչʾ�Ľ�������
	 * @return
	 */
	public String getRawFuncode() {
		return PfDataCache.getBillType(this.getCache().getCurrentDjlxbm())
				.getNodecode();
	}

	/**
	 * ������Ҫ����ĵ�������
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
			// �����Ƿ��õ���/�ڳ�����
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