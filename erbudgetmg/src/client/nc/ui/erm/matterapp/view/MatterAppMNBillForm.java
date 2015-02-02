package nc.ui.erm.matterapp.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.TableCellEditor;

import nc.bs.erm.common.ErmConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterapp.common.MatterAppUtils;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.erm.extendconfig.ErmExtendconfigCache;
import nc.itf.fi.pub.Currency;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.remote.ErmRometCallProxy;
import nc.ui.erm.billpub.remote.RoleVoCall;
import nc.ui.erm.extendtab.AbstractErmExtendCard;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.common.MultiVersionUtils;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IGetBillRelationItemValue;
import nc.ui.pub.bill.itemconverters.BodyUFRefPKConvert;
import nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.pubapp.uif2app.model.AppEventHandlerMediator;
import nc.ui.pubapp.uif2app.model.IAppModelEx;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.extendconfig.ErmExtendConfigVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

/**
 * 事项审批card
 * 
 * @author chenshuaia
 * 
 */
public class MatterAppMNBillForm extends AbstractMappBillForm {

	private static final long serialVersionUID = 1L;

	private Map<String, BillTabVO> tabInfo = new HashMap<String, BillTabVO>();

	private List<AbstractErmExtendCard> scrollPaneList = new ArrayList<AbstractErmExtendCard>();

	private boolean isMatterAppChanged = false;
	
	private NCAction rapidShareAction;

	@Override
	public void initUI() {
		String initDjlxbm = null;
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(getContext().getNodeCode()) 
				|| ErmMatterAppConst.MAPP_NODECODE_QY.equals(getContext().getNodeCode())) {
			DjLXVO[] djlxvos = ((MAppModel) this.getModel()).getAllDJLXVOs();
			if (djlxvos != null && djlxvos.length > 0) {
				for (DjLXVO vo : djlxvos) {
					if (UFBoolean.FALSE.equals(vo.getFcbz())) {
						this.setNodekey(vo.getDjlxbm());
						initDjlxbm = vo.getDjlxbm();
						break;
					}
				}

				if (((MAppModel) this.getModel()).getDjlxbm() == null) {
					this.setNodekey(djlxvos[0].getDjlxbm());
					initDjlxbm = djlxvos[0].getDjlxbm();
				}
			}
		} else {
			initDjlxbm = getTradeTypeByNodeCode();
		}
		((MAppModel) this.getModel()).setDjlxbm(initDjlxbm);
		((MAppModel) this.getModel()).setSelectBillTypeCode(initDjlxbm);
		
		super.initUI();
		
		// 主组织
		this.getBillOrgPanel().getRefPane().addValueChangedListener((ValueChangedListener) this.getBillCardHeadAfterEditlistener());

		initBillCardItem();
	}
	
	/**
	 * 切换模板/初始化界面时，监听/字段特殊处理
	 */
	private void initBillCardItem() {
		this.initBodyMulSelected();
		
		//精度处理
		MatterAppUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel(), this.getModel());

		// 对交易类型参照设置查询条件
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setWherePart(" istransaction = 'Y' and islock ='N' and pk_group='" + MatterAppUiUtil.getPK_group() + "' ");

		// 表体事由的特殊处理，处理现实问题
		BillItem bodyItem = this.getBillCardPanel().getBodyItem(MtAppDetailVO.REASON);
		if (bodyItem != null) {
			bodyItem.setConverter(new BodyUFRefPKConvert() {
				@Override
				public Object convertToBillItem(int type, Object value) {
					IConstEnum o = null;

					if (value instanceof IConstEnum) {
						o = (IConstEnum) value;
					} else {
						if (value != null && !value.equals(IBillItem.EMPTYSTRING)) {
							String v = value.toString();
							o = new DefaultConstEnum(v, v);
						}
					}
					return o;
				}
			});
			
			bodyItem.setGetBillRelationItemValue(new IGetBillRelationItemValue() {
				@Override
				public IConstEnum[] getRelationItemValue(ArrayList<IConstEnum> ies, String[] id) {
					DefaultConstEnum[] ss = new DefaultConstEnum[1];
					Object[] s = new Object[id.length];
					for (int i = 0; i < s.length; i++) {
						s[i] = id[i];
					}
					ss[0] = new DefaultConstEnum(s, MtAppDetailVO.REASON);
					return ss;
				}

			});
		}
	}
	
	@Override
	protected void processPopupMenu() {
		super.processPopupMenu();
		// 加入快速分摊按钮
		BillScrollPane bodyBillScroll = getBillCardPanel().getBodyPanel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		if(bodyBillScroll !=null){
			bodyBillScroll.addEditAction(getRapidShareAction());
		}
	}

	@Override
	public void setBillData(BillTempletVO template) {
		// 查询整理扩展页签信息
		dealExtendTab();
		
		// 处理billdata
		super.setBillData(template);

		// 处理扩展页签展现
		if (!this.tabInfo.isEmpty()) {
			for (AbstractErmExtendCard extendtab : this.scrollPaneList) {
				extendtab.setTableModel(this.getBillCardPanel().getBillData().getBillModel(extendtab.getTableCode()));
			}
			Collection<BillTabVO> collec = this.tabInfo.values();
			this.getBillCardPanel().getBodyTabbedPane().addScrollPane(collec.toArray(new BillTabVO[collec.size()]),
					this.scrollPaneList.toArray(new UIScrollPane[scrollPaneList.size()]));
		}
	}

	private void dealExtendTab() {
		// 查询扩展页签信息
		this.tabInfo.clear();
		this.scrollPaneList.clear();

		String pk_group = this.getModel().getContext().getPk_group();
		String ma_tradetype = ((MAppModel) this.getModel()).getDjlxbm();
		ErmExtendConfigVO[] extendConfigVO = null;
		try {
			extendConfigVO = ErmExtendconfigCache.getInstance().getErmExtendConfigVOs(pk_group, ma_tradetype);
		} catch (BusinessException e) {
			this.getExceptionHandler().handlerExeption(e);
		}
		if (extendConfigVO == null || extendConfigVO.length == 0) {
			return;
		}
		
		String suffix = MultiLangUtil.getCurrentLangSeqSuffix();
		
		for (ErmExtendConfigVO tabvo : extendConfigVO) {
			if (StringUtil.isEmpty(tabvo.getCardclass())) {
				continue;
			}
			String busi_tabname = (String) tabvo.getAttributeValue("busi_tabname" + suffix);
			if(StringUtil.isEmpty(busi_tabname) ){
				busi_tabname = (String) tabvo.getAttributeValue("busi_tabname");
			}
			BillTabVO btvo = new BillTabVO();
			btvo.setPos(Integer.valueOf(1));
			btvo.setTabcode(tabvo.getBusi_tabcode());
			btvo.setTabname(busi_tabname);
			btvo.setMetadataclass(tabvo.getMetadataclass());
			this.tabInfo.put(tabvo.getBusi_tabcode(), btvo);
			// 通过反射获得扩展页签
			AbstractErmExtendCard scrollPanes = MatterAppMNBillForm.createInstance(tabvo.getCardclass(), tabvo
					.getBusi_sys());
			scrollPanes.setModel(this.getModel());
			scrollPanes.setParentCard(this);
			scrollPanes.setBillParent(this.getBillCardPanel());
			scrollPanes.setTableCode(tabvo.getBusi_tabcode());
			scrollPanes.setTableName(tabvo.getBusi_tabname());
			scrollPanes.initUI();

			// 通过反射获得扩展页签的监听类
			AppEventHandlerMediator appEventHandlerMediator = MatterAppMNBillForm.createaAppEventInstance(tabvo
					.getCardlistenerclass(), tabvo.getBusi_sys());
			appEventHandlerMediator.setModel((IAppModelEx) this.getModel());

			this.scrollPaneList.add(scrollPanes);

		}
	}

	@Override
	protected void processErmBillData(BillData data) {
		super.processErmBillData(data);
		
		for (AbstractErmExtendCard extendtab : this.scrollPaneList) {
	      // 扩展页签加入到billdata
	      // data.setBillModel(extendtab.getTableCode(), extendtab.getTableModel());
	      data.setBodyItems(extendtab.getTableCode(), extendtab.getTableModel().getBodyItems());
		}

	}
   @Override
   protected void processBillData(BillData data) {
	   super.processBillData(data);
	   // 清除自动创建的扩展页签，避免与扩展页签的pane冲突
	   for (AbstractErmExtendCard extendtab : this.scrollPaneList) {
	      data.setBodyItems(extendtab.getTableCode(), null);
	      data.setBillModel(extendtab.getTableCode(), extendtab.getTableModel());
	   }
  }

	/**
	 * 初始化表体页签中的字段
	 * 
	 * @throws ValidationException
	 */
	private void initBodyMulSelected() {
		try {
			String[] names = AggMatterAppVO.getBodyMultiSelectedItems();
			for (String name : names) {
				BillItem item = this.getBillCardPanel().getBodyItem(ErmMatterAppConst.MatterApp_MDCODE_DETAIL, name);
				if (item != null && item.getComponent() instanceof UIRefPane) {
					((UIRefPane) item.getComponent()).setMultiSelectedEnabled(true);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	@Override
	protected void setBodyTabActive(String tabcode) {
		Set<String> extendtabcodes = this.tabInfo.keySet();
		if (!extendtabcodes.contains(this.billCardPanel.getCurrentBodyTableCode())) {
			super.setBodyTabActive(tabcode);
		} else {
			for (UIScrollPane scrollPane : this.scrollPaneList) {
				if (!(scrollPane instanceof AbstractErmExtendCard)
						|| !extendtabcodes.contains(((AbstractErmExtendCard) scrollPane).getTableCode())) {
					continue;
				}
				List<Action> actions = ((AbstractErmExtendCard) scrollPane).getActions();
				for (Object object : actions) {
					if (this.getModel().getUiState() != UIState.ADD && this.getModel().getUiState() != UIState.EDIT) {
						if (!(object instanceof DefaultHeadZoomAction)) {
							((AbstractAction) object).setEnabled(false);
						}
					}
				}

				this.billCardPanel.addTabAction(1, actions);
			}
		}
	}

	/**
	 * 通过反射取到扩展页签的类
	 * 
	 * @param className
	 * @param busiSysCode
	 * @return
	 */
	private static AbstractErmExtendCard createInstance(String className, String busiSysCode) {

		AbstractErmExtendCard instance = null;
		try {
			if (StringUtil.isEmptyWithTrim(busiSysCode)) {
				instance = (AbstractErmExtendCard) ObjectCreator.newInstance(className);
			} else {
				instance = (AbstractErmExtendCard) ObjectCreator.newInstance(busiSysCode, className);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException("cann't create instance. ClassName: " + className + ",devModuleCode:"
					+ busiSysCode + ". Please check register info in table er_extendconfig");
		}
		return instance;
	}

	/**
	 * 通过反射取到扩展页签的监听类
	 * 
	 * @param className
	 * @param busiSysCode
	 * @return
	 */
	private static AppEventHandlerMediator createaAppEventInstance(String className, String busiSysCode) {

		AppEventHandlerMediator instance = null;
		try {
			if (StringUtil.isEmptyWithTrim(busiSysCode)) {
				instance = (AppEventHandlerMediator) ObjectCreator.newInstance(className);
			} else {
				instance = (AppEventHandlerMediator) ObjectCreator.newInstance(busiSysCode, className);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException("cann't create instance. ClassName: " + className + ",devModuleCode:"
					+ busiSysCode + ". Please check register info in table er_extendconfig");
		}
		return instance;
	}

	public Map<String, BillTabVO> getTabInfo() {
		return this.tabInfo;
	}

	@Override
	public Object getValue() {
		AggMatterAppVO value = (AggMatterAppVO) super.getValue();
		for (Entry<String, BillTabVO> tabvo : this.tabInfo.entrySet()) {
			AbstractErmExtendCard tabpane = (AbstractErmExtendCard) this.getBillCardPanel().getBodyTabbedPane()
					.getScrollPane(tabvo.getValue());
			
			TableCellEditor cellEditor = tabpane.getTable().getCellEditor();
			if(cellEditor != null){
				cellEditor.stopCellEditing();
			}
			value.setTableVO(tabpane.getTableCode(), (CircularlyAccessibleValueObject[]) tabpane.getValue());
		}
		return value;
	}

	/**
	 * 加载人员相关信息缓存
	 * 
	 * 如果需要多次远程调用，换用PsnVoCall处理
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void loadInitData() {
		try {
			List<IRemoteCallItem> callitems = new ArrayList<IRemoteCallItem>();
			//人员信息
			callitems.add(new PsnVoCall());
			// 集团角色
			callitems.add(new RoleVoCall());
			try {
				ErmRometCallProxy.callRemoteService(callitems);
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		} catch (BusinessException e) {
			this.exceptionHandler.handlerExeption(e);
		}
	}

	/**
	 * 加载模板
	 * 
	 * @param strDjlxbm
	 * @throws Exception
	 */
	public void loadCardTemplet(String strDjlxbm) throws Exception {
		// 加载单据模板
		this.setNodekey(strDjlxbm);
		BillTempletVO template = this.createBillTempletVO();

		if (template == null) {// 在找不到时，可能自定义了节点，再查找一次
			template = this.getTemplate(strDjlxbm);
		}

		if (template == null) {
			Logger.error(NCLangRes.getInstance().getStrByID("uif2", "BillCardPanelForm-000000", null,
					new String[] { strDjlxbm })/* 没有找到nodekey：{0}对应的卡片模板 */);
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTable-000000")/* 没有找到设置的单据模板信息 */);
		}
		this.setBillData(template);
//		// 切换模板后表体添加精度监听
//		MatterAppUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel(), this.getModel());
//
//		// 对交易类型参照设置查询条件
//		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setMatchPkWithWherePart(true);
//		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setWherePart(
//				" istransaction = 'Y' and islock ='N' and pk_group='" + MatterAppUiUtil.getPK_group() + "' ");
//		
//		//切换模板后，重新加载表体的多选项
//		initMatterAppPage();
		
		initBillCardItem();
	}
	
	public void setBillDataTemplate(BillTempletVO template){
		this.setBillData(template);
	}

	private BillTempletVO getTemplate(String strDjlxbm) {
		BillTempletVO template;
		this.billCardPanel.setBillType(this.getModel().getContext().getNodeCode());
		this.billCardPanel.setBusiType(null);
		this.billCardPanel.setOperator(this.getModel().getContext().getPk_loginUser());
		this.billCardPanel.setCorp(this.getModel().getContext().getPk_group());
		template = this.billCardPanel.getDefaultTemplet(this.billCardPanel.getBillType(), null, this.billCardPanel
				.getOperator(), this.billCardPanel.getCorp(), strDjlxbm, null);
		return template;
	}

	/**
	 * 打开节点时调用
	 */
	@Override
	protected BillTempletVO createBillTempletVO() {
		BillTempletVO template = super.createBillTempletVO();
		if (template == null) {
			template = this.getTemplate(this.getNodekey());
		}
		return template;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			// 管理节点设置交易类型,
			AggMatterAppVO aggVo = (AggMatterAppVO) this.getModel().getSelectedData();

			if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(this.getContext().getNodeCode())
					|| ErmMatterAppConst.MAPP_NODECODE_QY.equals(this.getContext().getNodeCode())) {
				if (this.getModel().getUiState() != UIState.ADD) {
//					return;
					String voDjlxbm = null;
					
					if (aggVo != null) {
						voDjlxbm = aggVo.getParentVO().getPk_tradetype();
						((MAppModel) this.getModel()).setDjlxbm(voDjlxbm);
					}else{
						((MAppModel) this.getModel()).setDjlxbm(((MAppModel) this.getModel()).getSelectBillTypeCode());
					}
					
					if (aggVo != null && !getNodekey().equals(voDjlxbm)) {
						try {
							((MAppModel) this.getModel()).setDjlxbm(voDjlxbm);
							this.loadCardTemplet(voDjlxbm);
							this.setEditable(false);
						} catch (Exception e) {
							ExceptionHandler.consume(e);
						}
					}
				}

			}
			if (aggVo != null) {// 设置精度(金额、汇率) 精度的设置应在设置value之前处理
				String pk_org = aggVo.getParentVO().getPk_org();
				String currency = aggVo.getParentVO().getPk_currtype();
				try {
					MatterAppUiUtil.resetHeadDigit(this.billCardPanel, pk_org, currency);
				} catch (Exception e) {
				}
			}
		}
		// 父类处理中，当发生SELECTION_CHANGED事件时，设置了card的vo，并执行了公式
		super.handleEvent(event);
	}

	@Override
	public void showMeUp() {
		super.showMeUp();
		// 加载人员相关信息缓存
		this.loadInitData();
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		filtBillCardItem();
		this.setHeadRateBillFormEnable();
		this.getBillCardPanel().getHeadItem(MatterAppVO.PK_TRADETYPE).setEnabled(false);

		//申请单新增没有调用setValue方法，没有执行显示公式，这里调用
		if(isAutoExecLoadFormula()){
			execLoadFormula();
		}
		if(isAutoExecLoadRelationItem()){
			this.getBillCardPanel().getBillData().loadLoadHeadRelation();
		}
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		filtBillCardItem();
		this.setHeadRateBillFormEnable();
		this.getBillCardPanel().getHeadItem(MatterAppVO.PK_TRADETYPE).setEnabled(false);
	}

	public void filtBillCardItem() {
		
		Object appstatus = getBillCardPanel().getHeadItem(MatterAppVO.APPRSTATUS).getValueObject();
		if (appstatus != null && (IBillStatus.COMMIT == (Integer) appstatus || IBillStatus.CHECKGOING == (Integer) appstatus)) {
			// 审批状态是提交态时，不做处理
			return;
		}
		
		//过滤申请人
		filtApplyer();
	}
	
	/**
	 * 过滤申请人，报销类型时，受授权代理限制
	 */
	private void filtApplyer() {
		// 若申请人不是登陆用户，并且不在授权代理中，将字段清空
		String pk_org = (String) getHeadItemStrValue(MatterAppVO.PK_ORG);
		MAppModel maModel = (MAppModel) getModel();
		UFDate billDate = (UFDate) getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
		
		//申请单类型
		Integer matype = maModel.getTradeTypeVo(maModel.getDjlxbm()).getMatype();
		if (matype == null || matype == ErmConst.MATTERAPP_BILLTYPE_BX) {

			try {
				ErUiUtil.initSqdlr(this, getBillCardPanel().getHeadItem(MatterAppVO.BILLMAKER), maModel.getDjlxbm(),
						pk_org, billDate);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}

			String loginPsn = BXUiUtil.getPk_psndoc();
			if (loginPsn != null && !loginPsn.equals(getHeadItemStrValue(MatterAppVO.BILLMAKER))) {
				// 处理授权代理:先对借款报人过滤
				BillItem headItem = this.getBillCardPanel().getHeadItem(MatterAppVO.BILLMAKER);
				UIRefPane refPane = (UIRefPane) headItem.getComponent();
				AbstractRefGridTreeModel model = (AbstractRefGridTreeModel) refPane.getRefModel();
				model.setPk_org(pk_org);
				model.setMatchPkWithWherePart(true);
				if ((String) headItem.getValueObject() != null) {
					@SuppressWarnings("rawtypes")
					Vector vec = model.matchPkData((String) headItem.getValueObject());
					if (vec == null || vec.isEmpty()) {
						refPane.setPK(null);
					}
				}
				model.setMatchPkWithWherePart(false);
			}
		}
	}

	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		
		getBillOrgPanel().setPkOrg(null);
		
		// 表头
		this.setHeadValue(MatterAppVO.PK_BILLTYPE, ErmMatterAppConst.MatterApp_BILLTYPE);
		this.setHeadValue(MatterAppVO.BILLDATE, MatterAppUiUtil.getBusiDate());
		this.setHeadValue(MatterAppVO.PK_GROUP, MatterAppUiUtil.getPK_group());
		this.setHeadValue(MatterAppVO.PK_TRADETYPE, ((MAppModel) this.getModel()).getDjlxbm());

		// 表头金额

		String[] headAmounts = AggMatterAppVO.getHeadAmounts();
		for (String field : headAmounts) {
			this.setHeadValue(field, UFDouble.ZERO_DBL);
		}

		// 表尾
		this.setTailValue(MatterAppVO.APPROVER, null);
		this.setTailValue(MatterAppVO.APPROVETIME, null);
		this.setTailValue(MatterAppVO.CLOSEMAN, null);
		this.setTailValue(MatterAppVO.CLOSEDATE, null);
		this.setTailValue(MatterAppVO.PRINTER, null);
		this.setTailValue(MatterAppVO.PRINTDATE, null);
		this.setTailValue(MatterAppVO.CREATOR, MatterAppUiUtil.getPk_user());
		this.setTailValue(MatterAppVO.CREATIONTIME, null);

		// 状态设置
		this.setHeadValue(MatterAppVO.BILLSTATUS, BXStatusConst.DJZT_Saved);
		this.setHeadValue(MatterAppVO.APPRSTATUS, IBillStatus.FREE);
		this.setHeadValue(MatterAppVO.EFFECTSTATUS, ErmMatterAppConst.EFFECTSTATUS_NO);
		this.setHeadValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);

		try {
			this.setPsnInfoByUserId();// 设置组织，人员等信息
			this.resetCurrency();// 设置币种
			this.resetHeadDigit();// 设置精度
			this.setCurrencyRate();// 设置汇率
			this.resetOrgAmount();// 设置金额
			
			// 过滤组织
			ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(this.getModel().getContext(), (UFDate) this.getBillCardPanel()
					.getHeadItem(MatterAppVO.BILLDATE).getValueObject()), this.getBillOrgPanel().getRefPane());
			
			String pk_org_v = this.getHeadItemStrValue(MatterAppVO.PK_ORG_V);
			if (pk_org_v != null) {
				this.getBillOrgPanel().setPkOrg(pk_org_v);
			} else {
				this.setEditable(false);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 适用于设置费用申请界面设置币种默认值
	 * 
	 * @throws BusinessException
	 */
	public void resetCurrency() throws BusinessException {
		String pk_currency = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_currency == null) {
			MAppModel model = (MAppModel) this.getModel();
			DjLXVO djlxvo = model.getTradeTypeVo(model.getDjlxbm());
			if (djlxvo == null || djlxvo.getDefcurrency() == null) {
				// 组织本币币种
				String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
				pk_currency = Currency.getOrgLocalCurrPK(pk_org);
				// 默认组织本币作为原币
			} else {
				pk_currency = djlxvo.getDefcurrency();
			}

			this.setHeadValue(MatterAppVO.PK_CURRTYPE, pk_currency);
		}
	}

	/**
	 * 根据人员信息设置单位与部门等信息 如果未关联人员，则按个性化中心中设置的默认组织
	 * 
	 * @throws BusinessException
	 */
	public void setPsnInfoByUserId() throws BusinessException {
		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		String pk_group = instance.getGroupVO().getPk_group();
		String pk_psndoc = ErUiUtil.getPk_psndoc();
		String pk_org = null;
		if (!StringUtil.isEmpty(pk_psndoc)) {
			String pk_dept = (String) instance.getClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + pk_group);
			pk_org = (String) instance.getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + pk_group);
			this.setHeadValue(MatterAppVO.APPLY_ORG, pk_org);//申请单位设置值
			this.setHeadValue(MatterAppVO.APPLY_DEPT, pk_dept);
			this.setHeadValue(MatterAppVO.BILLMAKER, pk_psndoc);

			List<String> list = Arrays.asList(getFuncPermissionPkorgs());
			if (list.contains(pk_org)) {//财务组织设置默认值
				getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG).setValue(pk_org);
				this.setHeadOrgMultiVersion(MatterAppVO.PK_ORG_V, pk_org);
				this.setHeadValue(MatterAppVO.ASSUME_DEPT, pk_dept);
			} else {
				pk_org = ErUiUtil.getDefaultOrgUnit();
				if (pk_org != null) {
					getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG).setValue(pk_org);
					this.setHeadOrgMultiVersion(MatterAppVO.PK_ORG_V, pk_org);
				}
			}
		} else {
			pk_org = ErUiUtil.getDefaultOrgUnit();
			if (pk_org != null) {
				getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG).setValue(pk_org);
				this.setHeadOrgMultiVersion(MatterAppVO.PK_ORG_V, pk_org);
			}
		}
		// 发出组织变更事件
		if(!StringUtil.isEmpty(pk_org)){
			OrgChangedEvent orgevent = new OrgChangedEvent(getModel().getContext().getPk_org(),pk_org);
			getModel().getContext().setPk_org(pk_org);
			getModel().fireEvent(orgevent);
		}
		
	}

	/**
	 * 设置多版本值
	 * 
	 * @param vField
	 *            多版本字段
	 * @param pk_org
	 *            组织值
	 * @throws BusinessException
	 */
	public void setHeadOrgMultiVersion(String vField, String pk_org) throws BusinessException {
		if (pk_org != null) {
			UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
			if (date == null || StringUtil.isEmpty(date.toString())) {
				date = ErUiUtil.getBusiDate();
			}

			UIRefPane refPane = this.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V);
			String pk_vid = MultiVersionUtils.getHeadOrgMultiVersion(pk_org, date, refPane.getRefModel());

			this.getBillCardPanel().getHeadItem(vField).setValue(pk_vid);
		}
	}

	/**
	 * 重新设置表头与表体中本币的金额<br>
	 * 适用场景：编辑完币种或主组织后
	 */
	public void resetOrgAmount() throws BusinessException {
		// 表头金额重算
		this.resetHeadAmounts();

		// 表体金额重算
		this.resetCardBodyAmount();
	}

	/**
	 * 重置表头精度
	 */
	public void resetHeadDigit() {
		String pk_currency = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);

		try {// 设置表头精度
			MatterAppUiUtil.resetHeadDigit(this.getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}

	/**
	 * 重置表头精度
	 * 
	 * @param pk_currency
	 *            币种
	 * @param pk_org
	 *            组织
	 */
	public void resetHeadDigit(String pk_currency, String pk_org) {
		try {// 设置表头精度
			MatterAppUiUtil.resetHeadDigit(this.getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}

	/**
	 * 设置表头金额 重新计算表头本币金额
	 * 
	 * @throws BusinessException
	 */
	public void resetHeadAmounts() throws BusinessException {
		UFDouble total = this.getHeadUFDoubleValue(MatterAppVO.ORIG_AMOUNT);// 原币进行一次取数与设值，因为精度有可能变化，需要重新设置值
		this.setHeadValue(MatterAppVO.ORIG_AMOUNT, total);
		total = this.getHeadUFDoubleValue(MatterAppVO.ORIG_AMOUNT);

		this.setHeadValue(MatterAppVO.REST_AMOUNT, total);// 余额与总金额相同

		// 获取到集团和组织
		String pk_group = this.getHeadItemStrValue(MatterAppVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
		// 原币币种pk
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_org == null || pk_currtype == null) {
			return;
		}

		// 获取到汇率
		UFDouble orgRate = this.getHeadUFDoubleValue(MatterAppVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(MatterAppVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO);

		// 组织本币金额
		UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org), total,
				orgRate, MatterAppUiUtil.getSysdate());
		this.setHeadValue(MatterAppVO.ORG_AMOUNT, orgAmount);
		this.setHeadValue(MatterAppVO.ORG_REST_AMOUNT, orgAmount);

		// 集团、全局金额
		UFDouble[] money = Currency.computeGroupGlobalAmount(total, orgAmount, pk_currtype, MatterAppUiUtil
				.getSysdate(), pk_org, pk_group, globalRate, groupRate);

		this.setHeadValue(MatterAppVO.GROUP_AMOUNT, money[0]);
		this.setHeadValue(MatterAppVO.GROUP_REST_AMOUNT, money[0]);
		this.setHeadValue(MatterAppVO.GLOBAL_AMOUNT, money[1]);
		this.setHeadValue(MatterAppVO.GLOBAL_REST_AMOUNT, money[1]);

		// 计算最大允许报销金额
		this.computeMaxAmount(pk_group, pk_currtype, total);
	}

	/**
	 * 计算最大允许报销金额
	 * 
	 * @param pk_group
	 * @param pk_currtype
	 * @param total
	 * @throws BusinessException
	 */
	private void computeMaxAmount(String pk_group, String pk_currtype, UFDouble orig_amount) throws BusinessException {
		// 交易类型

		MAppModel model = (MAppModel) getModel();
		DjLXVO djlxVo = model.getTradeTypeVo(model.getDjlxbm());

		this.setHeadValue(MatterAppVO.MAX_AMOUNT, MatterAppUtils.computeMaxAmount(orig_amount, djlxVo));
	}
	
	/**
	 * 重置表体汇率
	 * 
	 * @param key
	 */
	public void resetCardBodyRate() {
		// 清空表体中的值
		int rowCount = this.getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getRowCount();
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				this.resetCardBodyRate(row);
			}
		}
	}

	public void resetCardBodyRate(int row) {
		String headPk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
		
		String assume_org = this.getBodyItemStrValue(row, MtAppDetailVO.ASSUME_ORG);
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);// 币种
		UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();// 单据日期
		if (assume_org == null || pk_currtype == null || date == null) {
			return;
		}
		

		try {
			String headOrgCurrPk = Currency.getOrgLocalCurrPK(headPk_org);
			String assume_orgCurrPk = Currency.getOrgLocalCurrPK(assume_org);//本币相同时，取表头汇率
			if (headPk_org.equals(assume_org)
					|| (headOrgCurrPk != null && assume_orgCurrPk != null && assume_orgCurrPk.equals(headOrgCurrPk))) {
				setBodyValue(getHeadUFDoubleValue(MatterAppVO.ORG_CURRINFO), row, MtAppDetailVO.ORG_CURRINFO);
				setBodyValue(getHeadUFDoubleValue(MatterAppVO.GROUP_CURRINFO), row, MtAppDetailVO.GROUP_CURRINFO);
				setBodyValue(getHeadUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO), row, MtAppDetailVO.GLOBAL_CURRINFO);
			}else{
				// 汇率(本币，集团本币，全局本币汇率)
				UFDouble orgRate = Currency.getRate(assume_org, pk_currtype, date);
				UFDouble groupRate = Currency.getGroupRate(assume_org, ErUiUtil.getPK_group(), pk_currtype, date);
				UFDouble globalRate = Currency.getGlobalRate(assume_org, pk_currtype, date);
				
				setBodyValue(orgRate, row, MtAppDetailVO.ORG_CURRINFO);
				setBodyValue(groupRate, row, MtAppDetailVO.GROUP_CURRINFO);
				setBodyValue(globalRate, row, MtAppDetailVO.GLOBAL_CURRINFO);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 重新设置表体中本币金额
	 */
	public void resetCardBodyAmount() {
		// 清空表体中的值
		BillModel billModel = this.getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		int rowCount = billModel.getRowCount();
		
		billModel.setNeedCalculate(false);//合计金额暂时关闭
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				this.resetCardBodyAmount(row);
			}
		}
		billModel.setNeedCalculate(true);//合计金额重启，计算合计效率较低，统一一次处理
	}

	public void resetCardBodyAmount(int rowNum) {
		// 获取到集团和组织
		UFDouble ori_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.ORIG_AMOUNT);// 重置精度
		this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.ORIG_AMOUNT);
		this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.REST_AMOUNT);
		UFDouble exe_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.EXE_AMOUNT);
		this.setBodyValue(exe_amount, rowNum, MtAppDetailVO.EXE_AMOUNT);
		UFDouble max_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.MAX_AMOUNT);
		this.setBodyValue(max_amount, rowNum, MtAppDetailVO.MAX_AMOUNT);
		UFDouble apply_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.APPLY_AMOUNT);
		this.setBodyValue(apply_amount, rowNum, MtAppDetailVO.APPLY_AMOUNT);

		String pk_org = this.getBodyItemStrValue(rowNum,MtAppDetailVO.ASSUME_ORG);
		if (pk_org == null) {
			return;
		}
		
		//集团
		String pk_group = this.getHeadItemStrValue(MatterAppVO.PK_GROUP);
		// 原币币种pk
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);


		// 获取到汇率(能根据表体费用单位)计算表体本币金额
		UFDouble hl = (UFDouble)getBodyValue(rowNum, MtAppDetailVO.ORG_CURRINFO);
		UFDouble grouphl = (UFDouble)getBodyValue(rowNum, MtAppDetailVO.GROUP_CURRINFO);
		UFDouble globalhl = (UFDouble)getBodyValue(rowNum, MtAppDetailVO.GLOBAL_CURRINFO);
		
		try {
			// 组织本币金额
			UFDouble[] bbje = Currency.computeYFB(pk_org,
					Currency.Change_YBCurr, pk_currtype, ori_amount, null, null, null, hl,
					ErUiUtil.getSysdate());
			this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.ORIG_AMOUNT);
			this.setBodyValue(bbje[2], rowNum, MtAppDetailVO.ORG_AMOUNT);
			this.setBodyValue(bbje[2], rowNum, MtAppDetailVO.ORG_REST_AMOUNT);

			// 集团、全局金额
			UFDouble[] money = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
					pk_currtype, ErUiUtil.getSysdate(), pk_org, pk_group,globalhl, grouphl);
			this.setBodyValue(money[0], rowNum, MtAppDetailVO.GROUP_AMOUNT);
			this.setBodyValue(money[0], rowNum, MtAppDetailVO.GROUP_REST_AMOUNT);
			this.setBodyValue(money[1], rowNum, MtAppDetailVO.GLOBAL_AMOUNT);
			this.setBodyValue(money[1], rowNum, MtAppDetailVO.GLOBAL_REST_AMOUNT);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 设置汇率
	 * 
	 * @param pk_org
	 *            组织pk
	 * @param pk_currtype
	 *            原币币种
	 * @param date
	 *            制单时间
	 */
	public void setCurrencyRate() {
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);// 组织
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);// 币种
		UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();// 单据日期

		if (pk_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			// 汇率(本币，集团本币，全局本币汇率)
			UFDouble orgRate = Currency.getRate(pk_org, pk_currtype, date);
			UFDouble groupRate = Currency.getGroupRate(pk_org, ErUiUtil.getPK_group(), pk_currtype, date);
			UFDouble globalRate = Currency.getGlobalRate(pk_org, pk_currtype, date);

			this.getBillCardPanel().getHeadItem(MatterAppVO.ORG_CURRINFO).setValue(orgRate);
			this.getBillCardPanel().getHeadItem(MatterAppVO.GROUP_CURRINFO).setValue(groupRate);
			this.getBillCardPanel().getHeadItem(MatterAppVO.GLOBAL_CURRINFO).setValue(globalRate);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 设置卡片编辑状态汇率的可编辑状态
	 */
	public void setHeadRateBillFormEnable() {
		boolean[] rateStatus = MatterAppUiUtil.getCurrRateEnableStatus(this.getHeadItemStrValue(MatterAppVO.PK_ORG),
				this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE));

		this.getBillCardPanel().getHeadItem(MatterAppVO.ORG_CURRINFO).setEnabled(rateStatus[0]);
		this.getBillCardPanel().getHeadItem(MatterAppVO.GROUP_CURRINFO).setEnabled(rateStatus[1]);
		this.getBillCardPanel().getHeadItem(MatterAppVO.GLOBAL_CURRINFO).setEnabled(rateStatus[2]);
	}

	// 根据表头将表体数据查出
	@Override
	protected void synchronizeDataFromModel() {
		AggMatterAppVO selectedData = (AggMatterAppVO) this.getModel().getSelectedData();
		if (selectedData != null && selectedData.getChildrenVO() == null) {
			try {
				AggMatterAppVO vo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPK(
						selectedData.getParentVO().getPrimaryKey());
				if (vo != null) {
					selectedData = vo;
					if (selectedData.getChildrenVO() == null) {
						selectedData.setChildrenVO(new MtAppDetailVO[0]);
					}

					// 更新model数据
					((MAppModel) this.getModel()).directlyUpdateWithoutFireEvent(selectedData);
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"201212_0", "0201212-0027")/*
														 * @ res
														 * "数据已经被其他用户删除，请刷新界面"
														 */, this.getModel().getContext());
					((BillManageModel) this.getModel()).directlyDelete(selectedData);
				}

			} catch (Exception e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}

		if (selectedData != null) {
			this.setValue(selectedData);
		} else {
			this.getBillCardPanel().getBillData().clearViewData();
		}

	}

	public boolean isMatterAppChanged() {
		return this.isMatterAppChanged;
	}

	public void setMatterAppChanged(boolean isMatterAppChanged) {
		this.isMatterAppChanged = isMatterAppChanged;
	}

	@Override
	public void setValue(Object object) {
		super.setValue(object);
		// 设置扩展页签中的数据，设置到界面上
		Map<String, BillTabVO> tabInfo = this.getTabInfo();
		for (Entry<String, BillTabVO> tabvo : tabInfo.entrySet()) {
			AbstractErmExtendCard tabpane = (AbstractErmExtendCard) this.getBillCardPanel().getBodyTabbedPane()
					.getScrollPane(tabInfo.get(tabvo.getKey()));
			tabpane.setValue(object);
		}
	}
	
	
	public void resetBodyMaxAmount(){
		BillModel billModel = this.getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		int rowCount = billModel.getRowCount();
		
		billModel.setNeedCalculate(false);//合计金额暂时关闭
		if (rowCount > 0) {
			MAppModel model = (MAppModel)getModel();
			DjLXVO djlxVo = model.getTradeTypeVo(model.getDjlxbm());
			
			for (int row = 0; row < rowCount; row++) {
				this.resetBodyMaxAmount(row, djlxVo);
			}
		}
		
		MatterAppUiUtil.fillLastRowAmount(this.getBillCardPanel());

		billModel.setNeedCalculate(true);//合计金额重启，计算合计效率较低，统一一次处理
	}
	
	/**
	 * 计算最大金额
	 * 
	 * @param row
	 */
	public void resetBodyMaxAmount(int row, DjLXVO djlxVo) {
		if (row >= 0) {
			UFDouble oriAmount = (UFDouble) getBodyValue(row, MtAppDetailVO.ORIG_AMOUNT);
			if (oriAmount != null && oriAmount.compareTo(UFDouble.ZERO_DBL) != 0) {
				try {
					this.setBodyValue(MatterAppUtils.computeMaxAmount(oriAmount, djlxVo), row,
							MtAppDetailVO.MAX_AMOUNT);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			} else {
				this.setBodyValue(UFDouble.ZERO_DBL, row, MtAppDetailVO.MAX_AMOUNT);
			}
		}
	}

	public NCAction getRapidShareAction() {
		return rapidShareAction;
	}

	public void setRapidShareAction(NCAction rapidShareAction) {
		this.rapidShareAction = rapidShareAction;
	}
	
	
}
