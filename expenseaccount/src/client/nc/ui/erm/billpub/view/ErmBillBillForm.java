package nc.ui.erm.billpub.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.funcnode.ui.FuncletInitData;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.md.data.access.DASFacade;
import nc.mddb.constant.ElementConstant;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.model.ErmBillBillAppModelService;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.eventhandler.AddFromMtAppEditorUtil;
import nc.ui.erm.billpub.view.eventhandler.ERMCardAmontDecimalListener;
import nc.ui.erm.billpub.view.eventhandler.ERMCardCShareRateListener;
import nc.ui.erm.billpub.view.eventhandler.InitBillCardBeforeEditListener;
import nc.ui.erm.billpub.view.eventhandler.InitBodyEventHandle;
import nc.ui.erm.billpub.view.eventhandler.InitEventHandle;
import nc.ui.erm.billpub.view.eventhandler.MultiVersionUtil;
import nc.ui.erm.costshare.ui.CSDetailCardAmontDecimalListener;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.erm.view.ERMUserdefitemContainerPreparator;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemHyperlinkEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IGetBillRelationItemValue;
import nc.ui.pub.bill.MetaDataGetBillRelationItemValue;
import nc.ui.pub.bill.itemeditors.StringBillItemEditor;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.uap.sf.SFClientUtil2;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.AddLineAction;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IRowSelectModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.BodyEditVO;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.arap.bx.util.ControlBodyEditVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.cmp.BusiInfo;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.link.LinkQuery;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
/**
 * ���ÿ�Ƭ
 */
public class ErmBillBillForm extends ERMBillForm {

	private static final long serialVersionUID = 3483698188504542111L;
	private InitEventHandle eventHandle = null;
	private InitBodyEventHandle bodyEventHandle =null;
	private AddFromMtAppEditorUtil addFromMtAppUtil = null;
	/**
	 * ���ݻ���
	 */
	// �������򻺴����ݣ�<�������ͣ�������׼>
//	private Map<String, List<SuperVO>> reimRuleDataMap = new HashMap<String, List<SuperVO>>();
	// ����ά�Ȼ������ݣ�<�������ͣ�������׼ά��>
//	private Map<String, List<SuperVO>> reimDimMap = new HashMap<String, List<SuperVO>>(); 
	// ����������֯�����ݻ��棬<��֯����׼>
	private Map<String, Map<String, List<SuperVO>>> reimRuleDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>();
	private Map<String, Map<String, List<SuperVO>>> reimDimDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>(); 
	/**
	 * ������׼��Ҫ���Ƶ���,�棨item��ֵ����aftereditor����
	 */
	private List<ControlBodyEditVO> controlRule = new ArrayList<ControlBodyEditVO>();
	private Set<Integer> rows = new HashSet<Integer>();
	
	private List<String> panelEditableKeyList;
	private Map<String,List<String>> orgRefFieldsMap = new HashMap<String, List<String>>();
	private ErmBillBillFormHelper helper = new ErmBillBillFormHelper(this);
	private boolean isContrast = false; // �Ƿ�����˳������
	private boolean isVerifyAccrued = false; // �Ƿ�����˺���Ԥ�����
	
	//ģ�建��
	private Map<String,BillData> containerCacheMap = new HashMap<String, BillData>();
	
	//������ť
	private ContrastAction contrastaction;
	
	private DefaultExceptionHanler execeptionHandler;

	/**
	 * �������ɵ�vo����
	 */
	private MatterAppConvResVO resVO = null;

	private NCAction rapidShareAction;
	
	//Excel����ʱVO
	private ExtendedAggregatedValueObject eavo;
	
	/**
	 * �Ƿ��һ�ο�Ƭ��ʾ
	 */
	private boolean isInit = true;
	
	@Override
	public void initUI() {
		super.initUI();
		
		//��ʾ�ϼ���
		showTatalLine();
		
		//�ڳ����ǳ��õ���,���ر���������뵥�ֶ�
		BillItem maBillnoItem = getBillCardPanel().getHeadItem("pk_item.billno");
		if(maBillnoItem != null){
			if(isInit()||((ErmBillBillManageModel)getModel()).iscydj()){
				getBillCardPanel().hideHeadItem(new String[]{"pk_item.billno"});
			}
		}
		
		// �������뵥pk_item�ֶΣ����������ȡֵ�������������������
		dealPkitemGetRelationValue();

		containerCacheMap.put(getNodekey(), getBillCardPanel().getBillData());
		
		((ErmBillBillAppModelService) ((BillManageModel) getModel()).getService()).setEditor(this);
		
		//���ӿ�Ƭ���б������
		addEventListener();
		
		// �������뵥�ֶμӳ�����
		addHyperlinkListenerForPK_ITEM_NO();
	}
	
	/**
	 * �������뵥pk_item�ֶΣ����������ȡֵ�������������������
	 */
	private void dealPkitemGetRelationValue() {
		final BillItem maitem = this.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
		if (maitem != null) {
			maitem.setGetBillRelationItemValue(new IGetBillRelationItemValue() {

				private MetaDataGetBillRelationItemValue metaValue = new MetaDataGetBillRelationItemValue(
						maitem.getMetaDataProperty().getRefBusinessEntity());;
				
				@Override
				public IConstEnum[] getRelationItemValue(ArrayList<IConstEnum> ies, String[] id) {
					
					ErmBillBillManageModel model = (ErmBillBillManageModel)getModel();
					// ��model�л�û���ֵ
					String maids = StringUtil.toString(id);
					IConstEnum[] maReationValues = model.getMaReationValues(maids);
					
					if(maReationValues == null){
						// �޻���ֵʱ��ͨ��Ԫ���ݷ�ʽ���ֵ
						maReationValues = metaValue.getRelationItemValue(ies, id);
						
						model.addMaRelationValues(maids, maReationValues);
					}
					
					return maReationValues;
				}

			});
		}
	}

	/**
	 *���ӽ����ı����Զ���ҳǩ �ϵİ�ť
	 */
	@Override
	protected void setBodyTabActive(String tabcode) {
		Map<String, List<Action>> bodyActionMap = getBodyActionMap();
		List<Action> actions = bodyActionMap.get(tabcode);
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if (actions == null) {
			if(BXConstans.JK_DJDL.equals(currentDjLXVO.getDjdl()))
			{
				actions = bodyActionMap.get(BXConstans.BUS_PAGE_JK);
			}else if(BXConstans.BX_DJDL.equals(currentDjLXVO.getDjdl())){
				actions = bodyActionMap.get(BXConstans.BUS_PAGE);
			}
		}
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		if(isAdjust&&!BXConstans.CSHARE_PAGE.equals(tabcode)){
			// �����������ֻ�з�̯ҳǩ���в���
			actions = bodyActionMap.get(BXConstans.CONST_PAGE);
		}
		
		billCardPanel.addTabAction(IBillItem.BODY, actions);
		
		//����ҳǩ�����Ա༭
		helper.setCostPageEnabled(getBillCardPanel(),false);
	}

	protected void addHyperlinkListenerForPK_ITEM_NO() {
		CardBillItemHyperlinkListener ll = new CardBillItemHyperlinkListener();
		BillItem item = this.billCardPanel.getHeadItem(JKBXHeaderVO.PK_ITEM_BILLNO);
		// ע�⻹�û��pk_item_billno�ֶ�
		if (item != null) {
			item.addBillItemHyperlinkListener(ll);
		}
	}
	
	@Override
	protected void processBillData(BillData data) {
		super.processBillData(data);
		BillItem pk_item = data.getHeadItem(JKBXHeaderVO.PK_ITEM);
		if(pk_item!=null){
			pk_item.setDataType(IBillItem.STRING);
			pk_item.setItemEditor(new StringBillItemEditor(pk_item));
		}
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO,ErmDjlxConst.BXTYPE_ADJUST);

		if(isAdjust){
			// EHP2���õ�������������÷�̯���빴ѡ����̯���ɹ�ѡ���Ҳ��ɱ༭
			BillItem ybje = data.getHeadItem(JKBXHeaderVO.YBJE);
			if(ybje !=null){
				// �������������ͷ����˱����ɱ༭
				ybje.setEdit(false);
			}
			BillItem total = data.getHeadItem(JKBXHeaderVO.TOTAL);
			if(total !=null){
				// �������������ͷ�ϼƽ��ɱ༭
				total.setEdit(false);
			}
			BillItem iscostshare = data.getHeadItem(JKBXHeaderVO.ISCOSTSHARE);
			if(iscostshare !=null){
				iscostshare.setEdit(false);
			}
			BillItem isexpamt = data.getHeadItem(JKBXHeaderVO.ISEXPAMT);
			if(isexpamt !=null){
				isexpamt.setEdit(false);
			}
			BillItem start_period = data.getHeadItem(JKBXHeaderVO.START_PERIOD);
			if(start_period != null){
				start_period.setEdit(false);
			}
			BillItem totol_period = data.getHeadItem(JKBXHeaderVO.TOTAL_PERIOD);
			if(totol_period != null){
				totol_period.setEdit(false);
			}
			BillItem share_ratio = data.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.SHARE_RATIO);
			if(share_ratio != null){
				share_ratio.setEdit(false);
			}
		}
	}
	
	/**
	 * ��Ƭ���泬����
	 */
	private class CardBillItemHyperlinkListener implements BillItemHyperlinkListener {

		@Override
		public void hyperlink(BillItemHyperlinkEvent event) {
			String pk =getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM).getValueObject().toString();
			LinkQuery linkQuery = new LinkQuery(ErmBillConst.MatterApp_DJDL, new String[] { pk });
//			SFClientUtil.openLinkedQueryDialog(, , linkQuery);
			FuncletInitData initData = new FuncletInitData();
			initData.setInitData(linkQuery);
			initData.setInitType(ILinkType.LINK_TYPE_QUERY);
			SFClientUtil2.openFuncNodeDialog(getBillCardPanel(), BXConstans.MTAMN_NODE, initData, null, false,
					false, null, new String[] { ErmConst.BUSIACTIVE_LINKQUERY });
		}
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			Object selectedData = getModel().getSelectedData();
			if (selectedData != null) {
				//�л�ģ��
				String nodekey = getNodekey();
				String newBillType = ((JKBXVO)selectedData).getParentVO().getDjlxbm();
				String nodeCode =getModel().getContext().getNodeCode();
                if (nodeCode.equals(BXConstans.BXLR_QCCODE) || nodeCode.equals(BXConstans.BXMNG_NODECODE) || nodeCode.equals(BXConstans.BXBILL_QUERY) || nodeCode.equals(BXConstans.BXINIT_NODECODE_G)
                        || nodeCode.equals(BXConstans.BXINIT_NODECODE_U)  || nodeCode.equals(BXConstans.MONTHEND_DEAL))
                {
                    changeTemplate(newBillType, nodekey);
                }
			}
		}else if(BXConstans.BROWBUSIBILL.equals(event.getType())){
			BusiInfo info = (BusiInfo) event.getContextObject();
			BilltypeVO billType = PfDataCache.getBillType(info.getBill_type());
			changeTemplate(info.getBill_type(), null);
			String djdl = billType == null ? "" : billType.getNcbrcode();
			if (info.getBill_type()!= null && info.getBill_type().trim().startsWith("26")) {
				try {
					List<JKBXVO> jkbxs = getIBXBillPrivate().queryVOsByPrimaryKeys(new String[] {info.getPk_bill()},djdl);
					if (jkbxs == null || jkbxs.size() == 0) {
						throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0", "02006v61008-0250")/* @res "û��ҵ�񵥾�, ҵ�񵥾�id" */+ info.getPk_bill());
					}
					setValue(jkbxs.get(0));
					getBillOrgPanel().setPkOrg(jkbxs.get(0).getParentVO().getPk_org_v());
				} catch (BusinessException e) {
					getExeceptionHandler().handlerExeption(e);
					//ExceptionHandler.consume(e);
				}
			}
		
		}

		super.handleEvent(event);
	}

	/**
	 * @return
	 * @throws ComponentException
	 * 
	 *             Look up �ӿ�
	 */
	private IBXBillPrivate getIBXBillPrivate() throws ComponentException {
		return ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName()));
	}
	
	/**
	 * �л�ģ��
	 *
	 * @param selectedData
	 * @author: wangyhh@ufida.com.cn
	 */
	public void changeTemplate(String newBillType,String oldBillType) {
		if (!newBillType.equals(oldBillType)) {
			//�л�ģ��
			BillData billData = containerCacheMap.get(newBillType);
			if(billData == null){
				if(oldBillType == null){
					oldBillType = getNodekey();
				}
				containerCacheMap.get(oldBillType).clearViewData();

				getBillCardPanel().loadTemplet(getModel().getContext().getNodeCode(), null, WorkbenchEnvironment.getInstance().getLoginUser().getCuserid(), BXUiUtil.getPK_group(),newBillType);
				
				//��ʾ�ϼ���
				showTatalLine();
				
				//�ڳ����ǳ��õ���,���ر���������뵥�ֶ�
				if(isInit()||((ErmBillBillManageModel)getModel()).iscydj()){
					if(getBillCardPanel().getHeadItem("pk_item.billno") != null){
						getBillCardPanel().hideHeadItem(new String[]{"pk_item.billno"});;
					}
				}
				//���Ӿ��ȼ���				
				addEventListener();
				
				// �������뵥�ֶμӳ�����
				addHyperlinkListenerForPK_ITEM_NO();
				
				new ERMUserdefitemContainerPreparator(getModel().getContext(),this,"zyx").resetBillData();
				
				getBillCardPanel().setEnabled(false);

				
				containerCacheMap.put(newBillType, getBillCardPanel().getBillData());
			}else{
				getBillCardPanel().setBillData(billData);
			}


		}
		setNodekey(newBillType);
		((ErmBillBillManageModel)getModel()).setCurrentBillTypeCode(newBillType);
		
		//�л�ģ���,�������ñ���İ�ť
		String tabCode = billCardPanel.getBodyTabbedPane().getSelectedTableCode();
		setBodyTabActive(tabCode);
		
		BillData newBillData = containerCacheMap.get(newBillType);
		processBillData(newBillData);
		
		//�������õ����ϱ��������
		String nodeCode = getModel().getContext().getNodeCode();
		if(nodeCode.equals(BXConstans.BXINIT_NODECODE_G) || nodeCode.equals(BXConstans.BXINIT_NODECODE_U)){
			BillItem[] headItems = getBillCardPanel().getBillData().getHeadItems();
			for(BillItem headItem : headItems){
				headItem.setNull(false);
			}
			BillItem[] bodyItems = getBillCardPanel().getBillData().getBodyItems();
			for(BillItem bodyItem : bodyItems){
				bodyItem.setNull(false);
			}
			//���õ���-���Žڵ㣬��ͷ������֯Ҳ���Ǳ�����
			if(nodeCode.equals(BXConstans.BXINIT_NODECODE_G)){
				getBillOrgPanel().getRefPane().getUITextField().setShowMustInputHint(false);
			}
		}
			
	}
	@Override
	protected void onNotEdit() {
		super.onNotEdit();
		resVO = null;
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		setBillItemEnable();
		
		helper.getAfterEditUtil().initPayentityItems(false);
		helper.getAfterEditUtil().initCostentityItems(false);
		helper.getAfterEditUtil().initUseEntityItems(false);
		helper.getAfterEditUtil().initPayorgentityItems(false);
		
		filtOrgField();
		filtDeptField();

		filterHeadItem();
		
		// ��ʼ�����ݷ�̯��־��ʾ�����ط�̯ҳǩ
		helper.initCostPageShow(getModel().getUiState());
		
		setExpamtEnable();
		
	
	}
	
	// ��Ƭ����༭״̬Ĭ��Ϊ���ɸ���
	private void setBillItemEnable() {
		JKBXVO jkbxvo = (JKBXVO) getModel().getSelectedData();
		
		UFBoolean isGroup = BXUiUtil.isGroup(getModel().getContext().getNodeCode());
		if (!isGroup.booleanValue()) {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setEnabled(false);
			//v6.1����
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setEnabled(false);
		}
		getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).setEnabled(false);
		getBillCardPanel().getHeadItem(JKBXHeaderVO.DJZT).setEnabled(false);
		getBillCardPanel().getHeadItem(JKBXHeaderVO.OPERATOR).setEnabled(false);

		getBillCardPanel().getHeadTailItem(JKBXHeaderVO.APPROVER).setEnabled(false);
		getBillCardPanel().getHeadTailItem(JKBXHeaderVO.SHRQ).setEnabled(false);

		// �����Ϣ
		getBillCardPanel().getTailItem(JKBXHeaderVO.CREATOR).setEnabled(false);
		getBillCardPanel().getTailItem(JKBXHeaderVO.CREATIONTIME).setEnabled(false);

		getBillCardPanel().getTailItem(JKBXHeaderVO.MODIFIER).setEnabled(false);
		getBillCardPanel().setTailItem(JKBXHeaderVO.MODIFIER,
				WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());

		getBillCardPanel().getTailItem(JKBXHeaderVO.MODIFIEDTIME).setEnabled(
				false);
		
		// ���ñ��һ����Ƿ��ܱ༭
		if (jkbxvo.getParentVO().getPk_org() != null) {
			try {
				if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null
						&& !getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject()
								.equals(Currency.getOrgLocalCurrPK(jkbxvo.getParentVO().getPk_org()))) {
					getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(true);
				}
				else{
					getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(false);
				}
				// �����ĵ����޸�ʱ�����óе���λ�����޸�
				if(!jkbxvo.getParentVO().getDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL) && isFromMtapp(jkbxvo)){
					getAddFromMtAppEditorUtil().resetBillItemOnEdit();
					
				}
			} catch (BusinessException e) {
				getExeceptionHandler().handlerExeption(e);
			}
		}
		
		// ������ػ����ܷ�༭,����Ҫ�������û��ʣ��ͻ��ʵľ���
		String pk_currtype =(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject();//ԭ�ұ���
		getHelper().setCurrRateEnable(jkbxvo.getParentVO().getPk_org(), pk_currtype);
		
	}
	

	/**
	 * �ж��Ƿ������������ĵ���
	 * @param jkbxvo
	 * @return
	 */
	private Boolean isFromMtapp(JKBXVO jkbxvo){
		if(jkbxvo.getParentVO().getPk_item() != null){
				return Boolean.TRUE;
			
		}
		return Boolean.FALSE;
	}
	
	@Override
	protected void onAdd() {
		String selectBillTypeCode = ((ErmBillBillManageModel) getModel())
				.getSelectBillTypeCode();

		//����ǵ��ݹ������ݲ�ѯ,�ڳ����ݣ����õ��ݣ����ݵ������Ͱ�ť�л�ģ��
		String nodeCode = getModel().getContext().getNodeCode();
		if (nodeCode.equals(BXConstans.BXLR_QCCODE) || nodeCode.equals(BXConstans.BXMNG_NODECODE)
				|| nodeCode.equals(BXConstans.BXBILL_QUERY) || nodeCode.equals(BXConstans.BXINIT_NODECODE_G) || nodeCode.equals(BXConstans.BXINIT_NODECODE_U)) {
			changeTemplate(selectBillTypeCode, getNodekey());
		}
		super.onAdd();
		
		// �����ֶ���������
		try {
		
			//����ģ�� �������Ͳ��ɱ༭
			getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM).setEnabled(false);
			
			//���˵��ݿ�Ƭ�ϵ���֯
			filtOrgField();
			
			UFDate date=(UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			if (isInit()) {
				//�ڳ�����
				date=new UFDate("3000-01-01");
			} else{
				//��������Ϊ�գ�ȥҵ������
				if(date == null || StringUtil.isEmpty(date.toString())){
					date = BXUiUtil.getBusiDate();
				}
			}
			//������������֯,��Ҫ�������ں�������
			ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(),date), getBillOrgPanel().getRefPane());
						
			// ��������ʱ���������˵������Ϣ
			//helper.setPsnInfoByUserId();----->�ں�̨�Ѿ�����
			
			String pkOrg = getModel().getContext().getPk_org();
			//������֯���õ���Ĭ��ֵ
			String currentBillTypeCode = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
			DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(currentBillTypeCode);
			helper.setDefaultWithOrg(currentDjlx.getDjdl(), currentBillTypeCode, pkOrg, false);
			
			if (resVO != null) {
				// ����ҳ���ֶδ���
				getAddFromMtAppEditorUtil().resetBillItemOnAdd();
			}
			
			pkOrg = getModel().getContext().getPk_org();
			// ��ʼ�����ݷ�̯��־��ʾ�����ط�̯ҳǩ
			helper.initCostPageShow(getModel().getUiState());
			
			
			// ���س��õ��ݣ����ݻ������¼����ͷ����ҳǩ���ֵ��ע���̯Ӧ�ڳ�ʼ����̯ҳǩ��
			// �Ժ���Կ��ǣ���Ϊ���ʴ����Ľ��仯���߼����ں�̨����
			String billTypeCode = ((ErmBillBillManageModel) (getModel())).getCurrentBillTypeCode();
			DjLXVO djdl = ((ErmBillBillManageModel) (getModel())).getCurrentDjlx(billTypeCode);
			if (djdl != null && djdl.getIsloadtemplate() != null && djdl.getIsloadtemplate().booleanValue()
					&& getHeadValue(JKBXHeaderVO.PK_ORG) != null && getHeadValue(JKBXHeaderVO.PK_ITEM) == null) {
				getEventHandle().resetBodyFinYFB();
				getEventHandle().getEventHandleUtil().setHeadYFB();
			}
			
			
			//���˵��ݿ�Ƭ�ϵĲ���
			filtDeptField();

			helper.getAfterEditUtil().initPayentityItems(false);
			helper.getAfterEditUtil().initCostentityItems(false);
			helper.getAfterEditUtil().initUseEntityItems(false);
			helper.getAfterEditUtil().initPayorgentityItems(false);

			// ������׼����<6>
			doReimRuleAction();

			// �ڱ���������ʱ���Զ����һ��,�������������,����ҳǩ����������
			addLine();

			// �ڱ�����λΪ��ʱ����Ҫ�����ñ�����λ�󣬲��ܱ༭�����ֶ�;���˽�����
			doPKOrgField();
			
			//������Ӧ�ֶ�
			filterHeadItem();
			
			//���ý����汾
			helper.setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
					JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V, JKBXHeaderVO.PK_PAYORG_V }, new String[] {
					JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG,
					JKBXHeaderVO.PK_PAYORG });

			helper.setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, (String) getHeadValue(JKBXHeaderVO.DWBM),
					JKBXHeaderVO.DEPTID);
			helper.setHeadDeptMultiVersion(JKBXHeaderVO.FYDEPTID_V, (String) getHeadValue(JKBXHeaderVO.FYDWBM),
					JKBXHeaderVO.FYDEPTID);
			
			MultiVersionUtil.setBodyOrgMultiVersion(BXBusItemVO.PK_PCORG_V, BXBusItemVO.PK_PCORG, this);
			
			// ����ǻ�������������ɾ��
			if (BXConstans.BXRB_CODE.equals(getModel().getContext().getNodeCode())) {

				getBillCardPanel().getBillModel(BXConstans.BUS_PAGE).clearBodyData();
			}
		} catch (BusinessException e) {
			// �ָ�Ϊ�Ǳ༭̬
			getModel().setUiState(UIState.NOT_EDIT);
			IRowSelectModel rowModel = (IRowSelectModel) getModel();
			int index = rowModel.getSelectedRow();
			rowModel.setSelectedRow(index);
			// �׳��쳣��Ϣ
			getExeceptionHandler().handlerExeption(e);
		}
	}
	
	public void filterHeadItem() {
		//��������
		filtZy();
		
		//���˽�����
		filtJkbx();
		
		// ��λ�����˻���Ҫ���ֺ�֧����λ��������������������ʱ���¹��ˡ�
		filtFkyhzh();
		
		// �տ������ʺţ����������˻��������տ��˺ͱ��ֱ������
//		filtSkyhzh();
		
		//�����ֽ��˻�
		filtAccount();
		
		//�����������Ĺ��˺���Ҫ��
		filtPk_Checkele();
		
		//�����ʽ�ƻ���Ŀ
		filtCashProj();
		
		//���˳ɱ�����
		filtResaCostCenter();
		
//		//���ݹ�Ӧ�̹��˿��������ʻ�
//		filtHbbm();
		
		//������Ŀ����
		filtProjTask();
		
		//��ڹ����Ų��շ�Χ������ȫ����������֯�Ĳ��ŵ�����
		fileCenterDept();
		
		// �Թ�֧�������տ��˺͸��������˻����ɱ༭
		filtIscusupplier();
		
		// ���ݿ��̣�����ɢ���Ƿ�ɱ༭
		filtFreeCust();
	}


	private void filtFreeCust() {
		Object hbbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.HBBM).getValueObject();
		Object customer = getBillCardPanel().getHeadItem(JKBXHeaderVO.CUSTOMER).getValueObject();
		if(hbbm == null && customer == null){
			getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEdit(false);
		}else {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEdit(true);
		}
	}
	
	public void filtJkbx() {
		Object appstatus = getBillCardPanel().getHeadItem(JKBXHeaderVO.SPZT).getValueObject();
		if ((appstatus != null && (IBillStatus.COMMIT == (Integer) appstatus))) {
			// ����״̬���ύ̬ʱ����������
			return;
		}
		//�������˲��ǵ�½�û������Ҳ�����Ȩ�����У����ֶ����
		String loginUser = BXUiUtil.getPk_psndoc();
		if(loginUser!=null && !loginUser.equals((String)getHeadValue(JKBXHeaderVO.JKBXR)) && !this.isInit()){
			//������Ȩ����:�ȶԽ��˹���
			BillItem headItem = this.getBillCardPanel().getHeadItem(JKBXHeaderVO.JKBXR);
			UIRefPane refPane = (UIRefPane) headItem.getComponent();
			AbstractRefGridTreeModel model = (AbstractRefGridTreeModel) refPane.getRefModel();
			model.setPk_org((String)getHeadValue(JKBXHeaderVO.DWBM));
			model.setMatchPkWithWherePart(true);
			if((String)headItem.getValueObject() != null){
				@SuppressWarnings("rawtypes")
				Vector vec = model.matchPkData((String)headItem.getValueObject());
				if (vec == null || vec.isEmpty()) {
					refPane.setPK(null);
				}
			}
			model.setMatchPkWithWherePart(false);
		}
	}

	private void filtIscusupplier() {
		Object iscusupplier = getHeadValue(JKBXHeaderVO.ISCUSUPPLIER);
		if(Boolean.TRUE.equals(iscusupplier)){
			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setValue(null);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setEnabled(false);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.SKYHZH).setEnabled(false);
		}
	}

	private void fileCenterDept() {
		BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.CENTER_DEPT);
		if(headItem != null ){
			UIRefPane center_dept = (UIRefPane) headItem.getComponent();
			center_dept.setMultiCorpRef(true);
			center_dept.setMultiRefFilterPKs(null);
			center_dept.setPk_org(null);
		}
	}

	private void filtAccount() {
		getEventHandle().getHeadFieldHandle().initAccount();
	}

	private void filtProjTask() {
		getEventHandle().getHeadFieldHandle().initProjTask();
	}

	public void filtHbbm() {
		getEventHandle().afterEditSupplier();
	}

	public void filtResaCostCenter() {
		getEventHandle().getHeadFieldHandle().initResaCostCenter();
	}


	public void filtCashProj() {
		getEventHandle().getHeadFieldHandle().initCashProj();
		
	}

	public void filtPk_Checkele() {
		getEventHandle().getHeadFieldHandle().initPk_Checkele();
		
	}

	public void filtZy() {
		getEventHandle().getHeadFieldHandle().initZy();
	}
	
	public void filtFkyhzh(){
		getEventHandle().getHeadFieldHandle().initFkyhzh();
	}
	
	public void filtSkyhzh() {
		getEventHandle().getHeadFieldHandle().initSkyhzh();
//		// �����տ��ˣ�����Ҫ�߸÷���
		getEventHandle().getHeadFieldHandle().editReceiver();
	}

	@Override
	protected void setDefaultValue() {
		try {
			String[] permissionOrgs = checkpermissionOrgs();
			
			String currentBillTypeCode = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
			DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(currentBillTypeCode);
			
			JKBXVO setBillVOtoUI = null;
			if(getResVO() != null){
				JKBXVO vo = (JKBXVO) getResVO().getBusiobj();
				// ��������ҵ��ҳǩ����ʾʱ�������������ı���������ա�
				int tabcount = getBillCardPanel().getBodyTabbedPane().getTabCount();
				if(tabcount == 0 ){
					vo.getParentVO().setYbje(getTotalAmountOfBusBody(vo));
					vo.setBxBusItemVOS(null);
				} else {
					List<String> tablecodes = new ArrayList<String>();
					for (int i = 0; i < tabcount; i++) {
						tablecodes.add(((BillScrollPane)getBillCardPanel().getBodyTabbedPane().getComponentAt(i)).getTableCode());
					}
					if(!tablecodes.contains(getBusPageCode())){
						vo.getParentVO().setYbje(getTotalAmountOfBusBody(vo));
						vo.setBxBusItemVOS(null);
					}
				}
				setBillVOtoUI = vo;
				setValue(setBillVOtoUI);
			}else{
				//����Ĭ������ֵ������
				setBillVOtoUI = NCLocator.getInstance().lookup(IErmBillUIPublic.class).
				setBillVOtoUI(currentDjlx,getModel().getContext().getNodeCode(),null);
				setValue(setBillVOtoUI);
			}
			afterDefaultValue(permissionOrgs,setBillVOtoUI.getParentVO());
		} catch (BusinessException e) {
			getExeceptionHandler().handlerExeption(e);
		}
	}
	
	private String[] checkpermissionOrgs() throws BusinessException {
		LoginContext context = getModel().getContext();
		JComponent comp = context.getEntranceUI();
		
		String[] permissionOrgs = null;
		if(comp instanceof AbstractFunclet){
			if (((AbstractFunclet)comp).getFuncletContext() == null){//���뵼��
				permissionOrgs = ErUiUtil.getPermissionOrgs(context.getNodeCode());
			}
			else{
				permissionOrgs = context.getPkorgs();
				
			}
		}
		
		//����������Ĭ��ֵǰ,��Ҫ�жϣ��û�û�з��书�ܽڵ��Ȩ��)
		if (!BXConstans.BXINIT_NODECODE_G.equals(context
				.getNodeCode())) {
			// ��֯û��Ȩ�ޣ�ֱ�����
			if (permissionOrgs == null || permissionOrgs.length == 0) {
				helper.setpk_org2Card(null);
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0066")
				/** @res* "�û�û�з��书�ܽڵ��Ȩ��"*/);
			}
		}
		return permissionOrgs;
	}
	
	/**
	 * ����Ĭ��ֵ�󣬽���Ĵ���
	 * @param permissionOrgs
	 * @param parentVO
	 * @throws BusinessException
	 */
	private void afterDefaultValue(String[] permissionOrgs,JKBXHeaderVO parentVO) throws BusinessException {
		//���õ���״̬
		getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
		//������״̬
		resetRowState();
		
		//����̯���ֶ�
		if(parentVO.getIsexpamt().booleanValue()){
			String fydwbm = getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject().toString();
			AccperiodmonthVO accperiodmonthVO;
		    try
		    {
		        accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(fydwbm, (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject());
		        getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).setEnabled(true);
		        getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setEnabled(true);
		        ((AccPeriodDefaultRefModel) ((UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).getComponent()).getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO
		                .getPk_accperiodscheme());
		        getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setValue(accperiodmonthVO.getPk_accperiodmonth());
		    } catch (InvalidAccperiodExcetion e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
		//���������ֶ�
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
		refPane.setAutoCheck(false);
		
		//��������֯
		setOrgWithPermission(permissionOrgs, parentVO);
	}

	private void setOrgWithPermission(String[] permissionOrgs,
			JKBXHeaderVO headerVO) {
		String pk_org = headerVO.getPk_org();
		helper.setpk_org2Card(pk_org);
		// �ǳ��õ��ݼ��ż��ڵ�ż����֯Ȩ��
		if (!BXConstans.BXINIT_NODECODE_G.equals(getModel().getContext().getNodeCode())) {				
			List<String> permissionList = Arrays.asList(permissionOrgs);
			if (!permissionList.contains(pk_org)) {
				helper.setpk_org2Card(null);
			}
		}
	}


	private UFDouble getTotalAmountOfBusBody(JKBXVO vo) {
		UFDouble totalAmount = UFDouble.ZERO_DBL;
		if(vo != null && vo.getBxBusItemVOS() != null && vo.getBxBusItemVOS().length > 0){
			for(BXBusItemVO busvo : vo.getBxBusItemVOS()){
				totalAmount = totalAmount.add(busvo.getAmount());
			}
		}
		return totalAmount;
	}

	/**
	 * �õ���ǰ���ݵ�ҵ��ҳǩ
	 * @return
	 */
	private String getBusPageCode()
	{
		if(isBX()){
			return BXConstans.BUS_PAGE;
		}
		return BXConstans.BUS_PAGE_JK;
	}
	
	private boolean isBX(){
		String currentBillTypeCode = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
		DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(currentBillTypeCode);
		return BXConstans.BX_DJDL.equals(currentDjlx.getDjdl());
	}

	
	//������֯�ֶ�
	private void filtOrgField() {
		String[] fields=new String[]{JKBXHeaderVO.PK_ORG_V};
		for (String field : fields) {
			getEventHandle().getHeadFieldHandle().beforeEditPkOrg_v(field);
		}
	}

	//���˲����ֶ�
	private void filtDeptField() {
		String dwbm = getEventHandle().getHeadItemStrValue(JKBXHeaderVO.DWBM);
		getEventHandle().getHeadFieldHandle().beforeEditDept_v(dwbm,
				JKBXHeaderVO.DEPTID_V);
		String fydwbm = getEventHandle().getHeadItemStrValue(
				JKBXHeaderVO.FYDWBM);
		getEventHandle().getHeadFieldHandle().beforeEditDept_v(fydwbm,
				JKBXHeaderVO.FYDEPTID_V);
	}
	
	//
	
	//���ݱ�ͷ���������ݲ��
	@Override
	protected void synchronizeDataFromModel() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData != null && selectedData.getChildrenVO() != null && selectedData.getChildrenVO().length == 0) {
			try {
				boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(selectedData.getParentVO().getPk_group(), selectedData.getParentVO().getDjlxbm(),ErmDjlxConst.BXTYPE_ADJUST);
				if(isAdjust && selectedData.getcShareDetailVo()!=null && selectedData.getcShareDetailVo().length!=0){
					
				}else{
					List<JKBXVO> jkbxvo = NCLocator.getInstance().lookup(
							IBXBillPrivate.class).queryVOsByPrimaryKeysForNewNode(
									new String[]{selectedData.getParentVO().getPrimaryKey()},selectedData.getParentVO().getDjdl(),selectedData.getParentVO().isInit(),((ErmBillBillManageModel)getModel()).getDjCondVO());
					if (jkbxvo != null) {
						selectedData = jkbxvo.get(0);
						if (selectedData.getChildrenVO() != null && selectedData.getChildrenVO().length == 0) {
							selectedData.setChildrenVO(null);
						}
						
						//����model����
						((ErmBillBillManageModel) getModel()).directlyUpdateWithoutFireEvent(selectedData);
					}else{
						ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0065")/*@res "�����Ѿ��������û�ɾ������ˢ�½���"*/,getModel().getContext());
						((ErmBillBillManageModel)getModel()).directlyDelete(selectedData);
					}
				}
			} catch (Exception e) {
				getExeceptionHandler().handlerExeption(e);
			}
		}
		if(selectedData != null){//���Ч��
			setValue(selectedData);
		}else{
			this.getBillCardPanel().getBillData().clearViewData();
		}
		
		helper.initCostPageShow(getModel().getUiState());
	}
	@Override
	public void setValue(Object object) {
		try {
			if(object!=null){
				//��Ƭ�������þ���,Ȼ��������ֵ
				JKBXHeaderVO parentVO = ((JKBXVO)object).getParentVO();
				getModel().getContext().setPk_org(parentVO.getPk_org());
				BXUiUtil.resetDecimal(getBillCardPanel(),getModel().getContext().getPk_org(),((JKBXVO)object).getParentVO().getBzbm());
				if(getModel().getUiState() == UIState.ADD){
					combineVO((JKBXVO)object, (JKBXVO)getValue());
				}
				
				super.setValue(object);
				
				if(((JKBXVO)object).getParentVO().getZy() != null){
					//�����ֶ�Ҫ���⴦��
					UIRefPane component = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
					component.getUITextField().setValue(new String[]{((JKBXVO)object).getParentVO().getZy().toString()});
				}
				
				//���������������⴦��
				if(getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject()!=null){
					String value = BXUiUtil.getDjlxNameMultiLang(getBillCardPanel().getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject().toString());
					getBillCardPanel().setHeadItem(JKBXHeaderVO.DJLXMC, value);
				}
				
				//����VO��ҳǩ����ҵ����
				resetBusItemVOs(object);
				
				((ErmBillBillManageModel)getModel()).setCurrentBillTypeCode(((JKBXVO)object).getParentVO().getDjlxbm());
				
				//�������Ҫ��
				UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).getComponent();
				String pk_pcorg = (String) getHeadValue(JKBXHeaderVO.PK_PCORG);
				if(pk_pcorg!=null){
					refPane.setEnabled(true);
					refPane.getRefModel().setPk_org(pk_pcorg);
				}
				if(getModel().getUiState()==UIState.NOT_EDIT){
					refPane.setEnabled(false);
				}
				this.getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_CHECKELE, parentVO.getPk_checkele());
				// ���ݶԹ�֧����־λ�����տ���Ϣ
				setSkInfByIscusupplier(object);
			}
			else{
				super.setValue(object);
				getModel().getContext().setPk_org(null);
				
			}
		} catch (Exception e) {
			getExeceptionHandler().handlerExeption(e);
		}
	}

	/**
	 * ��ǰ̨�ĵ���Ĭ��ֵ�봫������VOֵ�ϲ�
	 * @param backVO
	 * @param frontVO
	 */
	private void combineVO(JKBXVO backVO, JKBXVO frontVO) {
		if(backVO.getParentVO().getPk_jkbx()==null || frontVO.getParentVO().getPk_jkbx()==null
				|| backVO.getParentVO().getPk_jkbx().equals(frontVO.getParentVO().getPk_jkbx())){
			backVO.getParentVO().combineVO(frontVO.getParentVO());
		}
	}
	
	private void setSkInfByIscusupplier(Object object) {
		//�Թ�֧��ʱ���տ��ˡ����������˻����ɱ༭
		if(UFBoolean.TRUE.equals(((JKBXVO)object).getParentVO().getIscusupplier())){
			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER) != null) {
				getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setValue(null);
			}
			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setEnabled(false);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.SKYHZH).setEnabled(false);
		}
	}

	/**
	 * ���ñ�����״̬
	 */
	private void resetRowState() {
		BillCardPanel billCard = getBillCardPanel();
		String[] bodyTableCodes = billCard.getBillData().getBodyTableCodes();
		for (String tableCode : bodyTableCodes) {
			BillModel billModel = billCard.getBillModel(tableCode);
			int rowCount = billModel.getRowCount();
			if(rowCount <= 0){
				continue;
			}
			
			int rowState = BillModel.ADD;
			for (int i = 0; i < rowCount; i++) {
				if (billModel.getRowState(i) != BillModel.UNSTATE) {
					billModel.setRowState(i, rowState);
				}
			}
		}
	}
	
	/**
	 * ���õ��ݽ����ͷ��̯̯�Ƿ�ɱ༭,��ʼ̯������Ҫ���¼���
	 */
	public void setExpamtEnable() {
		if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl()
				.equals(BXConstans.BX_DJDL) && !((ErmBillBillManageModel) getModel()).getCurrentBillTypeCode().equals(BXConstans.BILLTYPECODE_RETURNBILL)) {
			Object isExpamt = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISEXPAMT).getValueObject();
			if (isExpamt != null && isExpamt.toString().equals("true")) {
                getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).setEnabled(true);
                getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setEnabled(true);
                JComponent component = getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).getComponent();
                component.repaint();
                String pk_org = getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject().toString();
    			AccperiodmonthVO accperiodmonthVO;
                try
                {
                    accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject());
                    ((AccPeriodDefaultRefModel) ((UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).getComponent()).getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO
                            .getPk_accperiodscheme());
                    getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setValue(accperiodmonthVO.getPk_accperiodmonth());
                } catch (InvalidAccperiodExcetion e) {
                	getExeceptionHandler().handlerExeption(e);
    			}
			}
		}
	}


	/**
	 * ����OV��ҳǩ����ҵ����
	 * ����ҵ���У���ҳǩ���
	 * 
	 * @param object
	 * @author: wangyhh@ufida.com.cn
	 */
	private void resetBusItemVOs(Object object) {
		if(object == null){
			return;
		}

		String defaultMetaDataPath = BXConstans.ER_BUSITEM;
		if (object instanceof JKVO) {
			defaultMetaDataPath = BXConstans.JK_BUSITEM;
		}
		
		BXBusItemVO[] childrenVO = ((JKBXVO)object).getChildrenVO();
		if(ArrayUtils.isEmpty(childrenVO)){
			return;
		}
		
		Map<String, List<BXBusItemVO>> tableCode2VOMap = VOUtils.changeCollection2MapList(Arrays.asList(childrenVO), new String[]{BXBusItemVO.TABLECODE});
		
		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		for (BillTabVO billTabVO : billTabVOs) {
			String metaDataPath = billTabVO.getMetadatapath();
			if(metaDataPath != null && !defaultMetaDataPath.equals(metaDataPath) ){
				continue;
			}
			
			BillModel billModel = getBillCardPanel().getBillModel(billTabVO.getTabcode());
			if (billModel == null) {
				//�������Ӧ�ò������
				return;
			}
			billModel.clearBodyData();
			List<BXBusItemVO> list = tableCode2VOMap.get(billTabVO.getTabcode());
			if(list == null && BXConstans.BUS_PAGE.equals(billTabVO.getTabcode())){
				//���ݱ�����ҵ���ж�ҳǩ
				list = tableCode2VOMap.get(BXConstans.ER_BUSITEM);
				if (list != null) {
					for (BXBusItemVO bxBusItemVO : list) {
						bxBusItemVO.setTablecode(billTabVO.getTabcode());
					}
				}
			}
			if(list != null){
				billModel.setBodyDataVO(list.toArray(new BXBusItemVO[0]));
				billModel.loadLoadRelationItemValue();
				billModel.execLoadFormula();//ִ����ʾ��ʽ
			}
		}
	}
	
	// �ڱ���������ʱ���Զ����һ��,�������������,����ҳǩ����������
	private void addLine() {
		JKBXVO jkbxvo = (JKBXVO) getValue();
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO,ErmDjlxConst.BXTYPE_ADJUST);
		if (!((ErmBillBillManageModel) getModel()).getCurrentBillTypeCode().equals(BXConstans.BILLTYPECODE_RETURNBILL)
				&& !isAdjust
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE)
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE_JK)
				&& (jkbxvo.getChildrenVO() == null || jkbxvo.getChildrenVO().length == 0) && getResVO() == null) {
			List<Action> actionList = new ArrayList<Action>();

			if (BXConstans.JK_DJDL.equals(currentDjLXVO.getDjdl())) {
				actionList = getBodyActionMap().get(BXConstans.BUS_PAGE_JK);
			} else if (BXConstans.BX_DJDL.equals(currentDjLXVO.getDjdl())) {
				actionList = getBodyActionMap().get(BXConstans.BUS_PAGE);
			}
			if (actionList.size() != 0) {
				AddLineAction action = (AddLineAction) actionList.get(0);
				try {
					action.doAction(null);
					// �����к�������ϵ�Ĭ��ֵ
					setBodyDefaultValue();
				} catch (Exception e) {
					getExeceptionHandler().handlerExeption(e);
				}
			}
		}
	}

	private void doPKOrgField() {
		// �ڱ�����λΪ��ʱ����Ҫ�����ñ�����λ�󣬲��ܱ༭�����ֶ�
		if (this.getHeadValue(JKBXHeaderVO.PK_ORG) == null
				&& this.getHeadValue(JKBXHeaderVO.PK_ORG_V) == null) {

			BillItem[] items = getBillCardPanel().getHeadItems();

			if (items != null && items.length > 0) {
				BillItem itemTemp = null;
				List<String> keyList = new ArrayList<String>();

				for (int i = 0; i < items.length; i++) {
					itemTemp = items[i];
					if (itemTemp.isEnabled()
							&& !JKBXHeaderVO.PK_ORG_V.equals(itemTemp.getKey())) {
						itemTemp.setEnabled(false);
						keyList.add(itemTemp.getKey());
					}
				}
				// �������������λ��Ӧ������Щitem����Ϊ�ɱ༭
				setPanelEditableKeyList(keyList);
			}
		}
	}

	private void setBodyDefaultValue() {
		setItemDefaultValue(getBillCardPanel().getBillData()
				.getBodyItemsForTable(
						getBillCardPanel().getCurrentBodyTableCode()));
		int rownum = getBillCardPanel().getRowCount() - 1;
		// �����ݴӱ�ͷ����������
		String[] keys = new String[]{JKBXHeaderVO.SZXMID,JKBXHeaderVO.JKBXR,JKBXHeaderVO.JOBID,
				JKBXHeaderVO.CASHPROJ,JKBXHeaderVO.PROJECTTASK,JKBXHeaderVO.PK_PCORG,JKBXHeaderVO.PK_PCORG_V,JKBXHeaderVO.PK_CHECKELE
				,JKBXHeaderVO.PK_RESACOSTCENTER};
		doCoresp(rownum, Arrays.asList(keys), getBillCardPanel().getCurrentBodyTableCode());

		String[] bodyKeys=new String[]{JKBXHeaderVO.YBJE,JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.ZFYBJE,JKBXHeaderVO.HKYBJE,
				JKBXHeaderVO.BBJE,JKBXHeaderVO.CJKBBJE,JKBXHeaderVO.ZFBBJE,JKBXHeaderVO.HKBBJE};
		for (String key : bodyKeys) {
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum,key);
		}

		// ����������׼
		doBodyReimAction();

		getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
		getBillCardPanel().getBillModel().execLoadFormula();
	}

	private void setItemDefaultValue(BillItem[] items) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				Object value = item.getDefaultValueObject();
				if (value != null)
					item.setValue(value);
			}
		}
	}

	private void doCoresp(int rownum, List<String> keyList, String tablecode) {
		for (String key : keyList) {
			String value = null;
			if (getBillCardPanel().getHeadItem(key) != null
					&& getBillCardPanel().getHeadItem(key).getValueObject() != null) {
				value = getBillCardPanel().getHeadItem(key).getValueObject()
						.toString();
			}

			String bodyvalue = (String) getBillCardPanel().getBodyValueAt(
					rownum, key);
			if (bodyvalue == null) {
				getBillCardPanel().setBodyValueAt(value, rownum, key);
			}
		}
	}

	@Override
	public Object getValue() {
		JKBXVO value = (JKBXVO) super.getValue();
		
		value.setNCClient(true);
		//������ҳǩ������ȫ��ҳǩ���ݣ�����tableCodeֵ
		setTableCodeAndResetBxBusItemVOs(value);
		
		fillBillItemValue(value);
		return value;
	}
	
	
	   /**
     * ȡ����Ƭ�����У���ͷ�ͱ������е�����
     * ע��getValue()ֻ��ȡ���仯�ı���
     * wangled
     */
	public JKBXVO getJKBXVO() {
		JKBXVO value = (JKBXVO) getValue();
		JKBXVO bxvo = (JKBXVO) value.clone();
		bxvo.setChildrenVO(null);
		bxvo.setContrastVO(null);
		bxvo.setcShareDetailVo(null);
		bxvo.setAccruedVerifyVO(null);

		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		for (BillTabVO billTabVO : billTabVOs) {
			String metaDataPath = billTabVO.getMetadatapath();
			if (BXConstans.ER_BUSITEM.equals(metaDataPath) || BXConstans.JK_BUSITEM.equals(metaDataPath)
					|| metaDataPath == null) {
				// ����ҵ���ж�ҳǩ
				BXBusItemVO[] bodyValueVOs = (BXBusItemVO[]) getBillCardPanel()
						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(BXBusItemVO.class.getName());
				if (!ArrayUtils.isEmpty(bodyValueVOs)) {
					for (BXBusItemVO bxBusItemVO : bodyValueVOs) {
						bxBusItemVO.setTablecode(billTabVO.getTabcode());
					}
				}

				BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
				if (!ArrayUtils.isEmpty(childrenVO)) {
					bxvo.setChildrenVO((BXBusItemVO[]) ArrayUtils.addAll(childrenVO, bodyValueVOs));
				} else {
					bxvo.setChildrenVO(bodyValueVOs);
				}
			} else if (BXConstans.CONST_PAGE.equals(metaDataPath)) {
				// ����ҳǩ
				BxcontrastVO[] bodyValueVOs = (BxcontrastVO[]) getBillCardPanel()
						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(BxcontrastVO.class.getName());
				bxvo.setContrastVO(bodyValueVOs);

			} else if (BXConstans.CS_Metadatapath.equals(metaDataPath)) {
				// ��̯ҳǩ
				CShareDetailVO[] bodyValueVOs = (CShareDetailVO[]) getBillCardPanel()
						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(CShareDetailVO.class.getName());
				bxvo.setcShareDetailVo(bodyValueVOs);
			} else if (BXConstans.AccruedVerify_Metadatapath.equals(metaDataPath)) {
				// ����Ԥ����ϸҳǩ
				AccruedVerifyVO[] bodyValueVOs = (AccruedVerifyVO[]) getBillCardPanel()
						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(AccruedVerifyVO.class.getName());
				bxvo.setAccruedVerifyVO(bodyValueVOs);
			}
		}
		helper.prepareForNullJe(bxvo);
		return bxvo;
	}
	

	public void fillBillItemValue(JKBXVO value) {
		// �����Ƿ��õ���/�ڳ�����
		JKBXHeaderVO parentVO = value.getParentVO();
		if (((ErmBillBillManageModel) getModel()).iscydj()) {
			parentVO.setInit(true);
		}
		if (((ErmBillBillManageModel) getModel()).isInit()) {
			parentVO.setQcbz(UFBoolean.TRUE);
		}

		helper.prepareForNullJe(value);
		helper.prepareContrast(value);
		helper.prepareBxVerifyAccrued(value);

//		clearCopyBodyRowPk(value);

		// ���ó�������Ľ��ҵ��
		try {
			if (value instanceof BXVO && !ArrayUtils.isEmpty(value.getContrastVO())) {
				if (getContrastaction() != null && isContrast) {
					value.setJkHeadVOs(getContrastaction().getSelectedJkVos(value));
				}
			}
		} catch (Exception e) {
			getExeceptionHandler().handlerExeption(e);
		}
		
		// ���õ�����Ĭ��ֵ����
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO,ErmDjlxConst.BXTYPE_ADJUST);
		if(isAdjust){
			parentVO.setIscostshare(UFBoolean.TRUE);
			parentVO.setIsexpamt(UFBoolean.FALSE);
			parentVO.setStart_period(null);
			parentVO.setTotal_period(null);
		}
	}

	//��ձ����е�pk�ֶ�
    @SuppressWarnings("unused")
	private void clearCopyBodyRowPk(JKBXVO value){
        BXBusItemVO[] childrenVO = value.getChildrenVO();
		if(childrenVO != null){
			for (BXBusItemVO bxBusItemVO : childrenVO) {
				bxBusItemVO.setDr(0);
				//��ҵ���и���pk
				if(bxBusItemVO.getStatus() == VOStatus.NEW && bxBusItemVO.getPrimaryKey() != null){
				    bxBusItemVO.setPrimaryKey(null);
				}
			}
		}
		
		CShareDetailVO[] cShareDetailVo = value.getcShareDetailVo();
		if(!ArrayUtils.isEmpty(cShareDetailVo)){
		    for (CShareDetailVO vo : cShareDetailVo)
            {
		        //���̯�и���pk
                if(vo.getStatus() == VOStatus.NEW && vo.getPrimaryKey() != null){
                    vo.setPrimaryKey(null);
                }
            }
		}
    }

	/**
	 * ������ҳǩ������ȫ��ҳǩ���ݣ�����tableCodeֵ
	 * 
	 * @param value
	 * @author: wangyhh@ufida.com.cn
	 */
	private void setTableCodeAndResetBxBusItemVOs(JKBXVO value) {
		if(value == null){
			return;
		}
		
		String defaultMetaDataPath = BXConstans.ER_BUSITEM;
		
		if(value instanceof JKVO){
			defaultMetaDataPath = BXConstans.JK_BUSITEM;
		}
		
		// ��ͷ��������Ӱ��
		HashMap<String, Object> map = new HashMap<String, Object>();
		BillItem[] items = getBillCardPanel().getBillData().getHeadTailItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];

				if (item.getMetaDataProperty() != null
						&& item.getIDColName() == null) {
					Object otemp = item.converType(item.getValueObject());
					map.put(item.getMetaDataAccessPath(), otemp);
				}
			}
			map.put(ElementConstant.KEY_VOSTATUS, getBillCardPanel().getBillData().getBillstatus());
		}
		
		List<SuperVO> childList = new ArrayList<SuperVO>();
		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		for (BillTabVO billTabVO : billTabVOs) {
			String metaDataPath = billTabVO.getMetadatapath();
			if (metaDataPath != null && !defaultMetaDataPath.equals(metaDataPath)) {
				continue;
			}

			BillModel billModel = getBillCardPanel().getBillModel(billTabVO.getTabcode());
			map.put(defaultMetaDataPath, billModel.getBodyChangeValueByMetaData());
			JKBXVO bxVO = (JKBXVO) DASFacade.newInstanceWithKeyValues(getBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity(), map).getContainmentObject();
			BXBusItemVO[] singleChildrenVO = bxVO.getChildrenVO();
			
			if(!ArrayUtils.isEmpty(singleChildrenVO)){
				for (BXBusItemVO bxBusItemVO : singleChildrenVO) {
					bxBusItemVO.setTablecode(billTabVO.getTabcode());
				}
				
				childList.addAll(Arrays.asList(singleChildrenVO));
			}
		}

		value.setChildrenVO(childList.toArray(new BXBusItemVO[0]));
	}

	// ��������
	public void doReimRuleAction() {
		JKBXVO vo = (JKBXVO) getValue();
		if (vo == null) {
			return;
		}
		
		// ��ͷ����������֯��+���ż�
		StringBuffer reim = new StringBuffer("");
		List<String> reimrule = BxUIControlUtil.doHeadReimAction(vo,
				getReimRuleDataMap(), getReimDimDataMap());
		List<String> reimrule1 = BxUIControlUtil.doHeadReimAction(vo,
						reimRuleDataCacheMap.get(ReimRulerVO.PKORG), reimDimDataCacheMap.get(ReimRulerVO.PKORG));
		for(String str:reimrule1){
			if(!reimrule.contains(str))
				reimrule.add(str);
		}
		for(String str:reimrule){
			reim.append(str+"\t");
		}
		if (getBillCardPanel().getHeadItem(BXConstans.REIMRULE) != null) {
			getBillCardPanel().setHeadItem(BXConstans.REIMRULE,
					reim.toString());
		}
		doBodyReimAction();
	}

	/**
	 * ���屨������
	 */
	public void doBodyReimAction() {

		JKBXVO bxvo = null;
		bxvo = (JKBXVO) getValue();

		//���û�б����ֶΣ���ֱ�ӷ���
		if (getBillCardPanel().getBillData().getBillTempletVO() == null
				|| getBillCardPanel().getBillData().getBillTempletVO()
						.getChildrenVO() == null) {
			return;
		}
		//ȡ��������ҳǩ������item
		BillTempletBodyVO[] billtempletbodyvos = (BillTempletBodyVO[]) getBillCardPanel()
				.getBillData().getBillTempletVO().getChildrenVO();
		
		//�õ����ձ�׼ֵ,������֯����׼�����߼��ż���׼
		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,
				getReimRuleDataMap(), billtempletbodyvos,getReimDimDataMap());
		List<BodyEditVO> result1 = BxUIControlUtil.doBodyReimAction(bxvo,
				reimRuleDataCacheMap.get(ReimRulerVO.PKORG),  billtempletbodyvos,reimDimDataCacheMap.get(ReimRulerVO.PKORG));
		result.addAll(result1);
		List<String> editstring = new ArrayList<String>();
		controlRule.clear();
		for (BodyEditVO vo : result) {
			//ControlBodyEditVO��ʾ������
			if(vo instanceof ControlBodyEditVO){
				if(!editstring.contains(vo.toString())){
					editstring.add(vo.toString());
					controlRule.add((ControlBodyEditVO)vo);
				}
			}
			else{
				getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(),
						vo.getItemkey(), vo.getTablecode());
			}
		}
	}

	protected Object getHeadValue(String key) {
		BillItem headItem = getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}

	private void addEventListener() {
		
		//����֯�����ռ���
		if(getBillOrgPanel() != null){
			getBillOrgPanel().getRefPane().removeValueChangedListener(getEventHandle());
			getBillOrgPanel().getRefPane().addValueChangedListener(getEventHandle());
		}

		//��ͷ�༭ǰ�¼�����
		getBillCardPanel().setBillBeforeEditListenerHeadTail(new InitBillCardBeforeEditListener(this));

		// ���ӱ༭���¼�����
		getBillCardPanel().addEditListener(getEventHandle());

		// ���ӱ���ı༭ǰ�ͱ༭���¼�����
		String[] tableCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		if (tableCodes != null) {
			for (String code : tableCodes) {
				getBillCardPanel().addEditListener(code, getbodyEventHandle());
				getBillCardPanel().addBodyEditListener2(code, getbodyEventHandle());
			}
		}
		//��������Ƭ��̯�������ӻ��ʾ��ȼ���
		BillModel cshareBodyModel = getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE);
		if(BXConstans.BX_DJDL.equals(((ErmBillBillManageModel)getModel()).getCurrentDjLXVO().getDjdl())
				&&!BXConstans.BXRB_CODE.equals(getModel().getContext().getNodeCode())&&cshareBodyModel!=null){
			new ERMCardCShareRateListener(getBillCardPanel(),cshareBodyModel, CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.BBHL}, ERMCardCShareRateListener.RATE_TYPE_LOCAL);
			new ERMCardCShareRateListener(getBillCardPanel(),cshareBodyModel, CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.GROUPBBHL}, ERMCardCShareRateListener.RATE_TYPE_GROUP);
			new ERMCardCShareRateListener(getBillCardPanel(),cshareBodyModel, CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.GLOBALBBHL}, ERMCardCShareRateListener.RATE_TYPE_GLOBAL);
			
			//��������Ƭ��̯�������ӽ��ȼ���
			new ERMCardAmontDecimalListener(cshareBodyModel, getBillCardPanel(),
					new String[]{CShareDetailVO.ASSUME_AMOUNT},
					ERMCardAmontDecimalListener.RATE_TYPE_YB);
			new ERMCardAmontDecimalListener(cshareBodyModel, getBillCardPanel(),
					new String[]{CShareDetailVO.BBJE},
					ERMCardAmontDecimalListener.RATE_TYPE_LOCAL);
			new ERMCardAmontDecimalListener(cshareBodyModel, getBillCardPanel(),
					new String[]{CShareDetailVO.GROUPBBJE},
					CSDetailCardAmontDecimalListener.RATE_TYPE_GROUP);
			new ERMCardAmontDecimalListener(cshareBodyModel, getBillCardPanel(),
					new String[]{CShareDetailVO.GLOBALBBJE},
					ERMCardAmontDecimalListener.RATE_TYPE_GLOBAL);
		}
	}

	public InitEventHandle getEventHandle() {
		if (eventHandle == null) {
			eventHandle = new InitEventHandle(this);
		}
		return eventHandle;
	}

	public InitBodyEventHandle getbodyEventHandle(){
		if(bodyEventHandle==null){
			bodyEventHandle = new InitBodyEventHandle(this);
		}
		return bodyEventHandle;
	}

	// ���ݼ��ż�������������׼���ù���,��ȡ��֯
	public String getPkOrg(){
		//��ȡ��֯
		String pk_org = null;
		try {
			String PARAM_ER8 = SysInit.getParaString(BXUiUtil.getPK_group(), BXParamConstant.PARAM_ER_REIMRULE);
			if (PARAM_ER8 != null) {
				if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_PK_ORG)) {
					pk_org = (String) getHeadValue(JKBXHeaderVO.PK_ORG);
				} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_OPERATOR_ORG)) {
					pk_org = (String) getHeadValue(JKBXHeaderVO.DWBM);
				} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_ASSUME_ORG)) {
					pk_org = (String) getHeadValue(JKBXHeaderVO.FYDWBM);
				}
			}
		} catch (BusinessException e1) {
			ExceptionHandler.consume(e1);
		}
		return pk_org;
	}
	
	public Map<String, Map<String, List<SuperVO>>> getReimRuleDataCacheMap() {
		return reimRuleDataCacheMap;
	}

	//��ȡ��ǰ��֯����׼
	public Map<String, List<SuperVO>> getReimRuleDataMap() {
		String pk_org = getPkOrg();
		//������ż���׼Ϊ������Ҫ�״λ�ȡ���ż�����֯����׼
		if (reimRuleDataCacheMap.get(ReimRulerVO.PKORG) == null) {
			List<ReimRulerVO> vos;
			List<ReimRuleDimVO> vodims;
			try {
				if (pk_org != null) {
					vos = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryGroupOrgReimRuler(null, getModel().getContext().getPk_group(),pk_org);
					vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryGroupOrgReimDim(null, getModel().getContext().getPk_group(),pk_org);
					List<SuperVO> vos1 = new ArrayList<SuperVO>();
					List<SuperVO> vos2 = new ArrayList<SuperVO>();
					if(vos!=null){
						for(ReimRulerVO vo:vos){
							if(vo.getPk_org().equals(ReimRulerVO.PKORG))
								vos1.add(vo);
							else
								vos2.add(vo);
						}
					}
					reimRuleDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
					reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
					vos1.clear();
					vos2.clear();
					if(vodims!=null){
						for(ReimRuleDimVO vo:vodims){
							if(vo.getPk_org().equals(ReimRulerVO.PKORG))
								vos1.add(vo);
							else
								vos2.add(vo);
						}
					}
					reimDimDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
					reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		else{
			//������ż���׼��Ϊ��˵���Ѿ�ȡ���ˣ�ֱ��ȡ��֯�Ϳ�����
			if (pk_org != null) {
				if (reimRuleDataCacheMap.get(pk_org) == null) {
					List<ReimRulerVO> vos;
					List<ReimRuleDimVO> vodims;
					try {
						vos = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryReimRuler(null, getModel().getContext().getPk_group(),pk_org);
						vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryReimDim(null, getModel().getContext().getPk_group(),pk_org);
						reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				} else {
					return reimRuleDataCacheMap.get(pk_org);
				}
			}
		}
		return reimRuleDataCacheMap.get(pk_org);
	}
	
	//��ȡ��ǰ��֯����׼ά��
	public Map<String, List<SuperVO>> getReimDimDataMap() {
		String pk_org = getPkOrg();
		//������ż���׼Ϊ������Ҫ�״λ�ȡ���ż�����֯����׼
		if (reimDimDataCacheMap.get(ReimRulerVO.PKORG) == null) {
			List<ReimRulerVO> vos;
			List<ReimRuleDimVO> vodims;
			try {
				if (pk_org != null) {
					vos = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryGroupOrgReimRuler(null, getModel().getContext().getPk_group(),pk_org);
					vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryGroupOrgReimDim(null, getModel().getContext().getPk_group(),pk_org);
					List<SuperVO> vos1 = new ArrayList<SuperVO>();
					List<SuperVO> vos2 = new ArrayList<SuperVO>();
					if(vos!=null){
						for(ReimRulerVO vo:vos){
							if(vo.getPk_org().equals(ReimRulerVO.PKORG))
								vos1.add(vo);
							else
								vos2.add(vo);
						}
					}
					reimRuleDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
					reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
					vos1.clear();
					vos2.clear();
					if(vodims!=null){
						for(ReimRuleDimVO vo:vodims){
							if(vo.getPk_org().equals(ReimRulerVO.PKORG))
								vos1.add(vo);
							else
								vos2.add(vo);
						}
					}
					reimDimDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
					reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		else{
			//������ż���׼��Ϊ��˵���Ѿ�ȡ���ˣ�ֱ��ȡ��֯�Ϳ�����
			if (pk_org != null) {
				if (reimDimDataCacheMap.get(pk_org) == null) {
					List<ReimRulerVO> vos;
					List<ReimRuleDimVO> vodims;
					try {
						vos = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryReimRuler(null, getModel().getContext().getPk_group(),pk_org);
						vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryReimDim(null, getModel().getContext().getPk_group(),pk_org);
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				} else {
					return reimDimDataCacheMap.get(pk_org);
				}
			}
		}
		return reimDimDataCacheMap.get(pk_org);
	}
	
	public List<String> getPanelEditableKeyList() {
		return panelEditableKeyList;
	}

	public void setPanelEditableKeyList(List<String> panelEditableKeyList) {
		this.panelEditableKeyList = panelEditableKeyList;
	}

	/**
	 * @return ���ݵ�ǰ�ĵ������ͱ���ȡҵ������VO (busitype.xml��������)
	 * @see BusiTypeVO
	 */
	public BusiTypeVO getBusTypeVO() {
		String currentBillTypeCode = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
		DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(currentBillTypeCode);
		return BXUtil.getBusTypeVO(currentBillTypeCode, currentDjlx.getDjdl());
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
			}else if(JKBXHeaderVO.PK_PAYORG.equals(orgField)){
				orgRefFieldsMap.put(orgField, getBusTypeVO().getPayorgentity_billitems());
			}
		}
		return orgRefFieldsMap.get(orgField);
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
		list.addAll(getBusTypeVO().getPayorgentity_billitems());
		return list;
	}
	
	public ErmBillBillFormHelper getHelper() {
		return helper;
	}

	/**
	 * ����˵�������ڳ�����
	 * @return
	 * @since V6.0
	 */
	public boolean isInit(){
		return false;
	}

	public MatterAppConvResVO getResVO() {
		return resVO;
	}

	public void setResVO(MatterAppConvResVO resVO) {
		this.resVO = resVO;
	}
	
	public AddFromMtAppEditorUtil getAddFromMtAppEditorUtil(){
		if(addFromMtAppUtil == null){
			addFromMtAppUtil = new AddFromMtAppEditorUtil(this);
		}
		return addFromMtAppUtil;
	}
	
	public boolean isContrast() {
		return isContrast;
	}

	public void setContrast(boolean isContrast) {
		this.isContrast = isContrast;
	}
	/**
	 * ��ʾȫ��ҳǩ�ĺϼ���
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void showTatalLine() {
		//��ʾ�ϼ���
		String[] bodyTableCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		if(bodyTableCodes != null){
			for (String tableCode : bodyTableCodes) {
				BillScrollPane bsp = getBillCardPanel().getBodyPanel(tableCode);
				bsp.setTotalRowShow(true);
			}
		}
	}
	
	
	@Override
	protected void processPopupMenu() {
		super.processPopupMenu();
		// ������ٷ�̯��ť
		BillScrollPane bodyBillScroll = getBillCardPanel().getBodyPanel(BXConstans.CSHARE_PAGE);
		if(bodyBillScroll !=null){
			bodyBillScroll.addEditAction(getRapidShareAction());
		}
	}

	public NCAction getRapidShareAction() {
		return rapidShareAction;
	}

	public void setRapidShareAction(NCAction rapidShareAction) {
		this.rapidShareAction = rapidShareAction;
	}

	public ContrastAction getContrastaction() {
		return contrastaction;
	}

	public void setContrastaction(ContrastAction contrastaction) {
		this.contrastaction = contrastaction;
	}

	@Override
	public void showMeUp() {
		super.showMeUp();
		try {// ��Ƭ������ʾʱ����ʾ
			if (isInit) {
				helper.callRemoteService(this);
				isInit = false;
			}
		} catch (BusinessException e) {
			getExeceptionHandler().handlerExeption(e);
		}
	}

	public DefaultExceptionHanler getExeceptionHandler() {
		return execeptionHandler;
	}

	public void setExeceptionHandler(DefaultExceptionHanler execeptionHandler) {
		this.execeptionHandler = execeptionHandler;
	}

	public List<ControlBodyEditVO> getControlRule() {
		return controlRule;
	}

	public Set<Integer> getRows() {
		return rows;
	}

	public boolean isVerifyAccrued() {
		return isVerifyAccrued;
	}

	public void setVerifyAccrued(boolean isVerifyAccrued) {
		this.isVerifyAccrued = isVerifyAccrued;
	}

	public ExtendedAggregatedValueObject getEavo() {
		return eavo;
	}

	public void setEavo(ExtendedAggregatedValueObject eavo) {
		this.eavo = eavo;
	}
}