package nc.ui.erm.billpub.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.md.data.access.DASFacade;
import nc.mddb.constant.ElementConstant;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
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
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.erm.costshare.ui.CSDetailCardAmontDecimalListener;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.util.TransTypeUtil;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.erm.view.ERMUserdefitemContainerPreparator;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemHyperlinkEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.itemeditors.StringBillItemEditor;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.AppEvent;
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
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.cmp.BusiInfo;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.link.LinkQuery;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

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
	private Map<String, List<SuperVO>> reimRuleDataMap = new HashMap<String, List<SuperVO>>(); // �������򻺴�����
	private Map<String, Map<String, List<SuperVO>>> reimRuleDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>(); // �������򻺴����ݻ���
	private Map<String, SuperVO> expenseMap = new HashMap<String, SuperVO>(); // �������ͻ�������
	private Map<String, SuperVO> reimtypeMap = new HashMap<String, SuperVO>(); // �������ͻ�������
	private List<String> panelEditableKeyList;
	private Map<String,List<String>> orgRefFieldsMap = new HashMap<String, List<String>>();
	private ErmBillBillFormHelper helper = new ErmBillBillFormHelper(this);
	private boolean isContrast = false; // �Ƿ�����˳������
	
	//ģ�建��
	private Map<String,BillData> containerCacheMap = new HashMap<String, BillData>();
	
	//������ť
	private ContrastAction contrastaction;

	/**
	 * �������ɵ�vo����
	 */
	private MatterAppConvResVO resVO = null;

	private NCAction rapidShareAction;
	
	/**
	 * �Ƿ��һ�ο�Ƭ��ʾ
	 */
	private boolean isInit = true;
	
	@Override
	public void initUI() {
		super.initUI();
		
		// ��ʼ����̯ҳǩ��һЩ����
		initCsharePage();
		//��ʾ�ϼ���
		showTatalLine();
		
		//�ڳ����ǳ��õ���,���ر���������뵥�ֶ�
		if(isInit()||((ErmBillBillManageModel)getModel()).iscydj()){
			if(getBillCardPanel().getHeadItem("pk_item.billno") != null){
				getBillCardPanel().hideHeadItem(new String[]{"pk_item.billno"});
			}
		}
		
		//����֯�����ռ���
		if(getBillOrgPanel() != null){
			getBillOrgPanel().getRefPane().addValueChangedListener(getEventHandle());
		}

		containerCacheMap.put(getNodekey(), getBillCardPanel().getBillData());
		
		//���õ�ǰ���ܽڵ�Ľ�������
		String nodeCode = getModel().getContext().getNodeCode();
		if(!nodeCode.equals(BXConstans.BXLR_QCCODE)&& !nodeCode.equals(BXConstans.BXMNG_NODECODE)&& !nodeCode.equals(BXConstans.BXBILL_QUERY) &&
		        !nodeCode.equals(BXConstans.BXINIT_NODECODE_G) && !nodeCode.equals(BXConstans.BXINIT_NODECODE_U)){
		    String transtype = TransTypeUtil.getTranstype(getModel());
		    ((ErmBillBillManageModel)getModel()).setCurrentBillTypeCode(transtype);
		    ((ErmBillBillManageModel)getModel()).setSelectBillTypeCode(transtype);
		}
		
		((ErmBillBillAppModelService) ((BillManageModel) getModel()).getService()).setEditor(this);
		
		//���ӿ�Ƭ���б������
		addEventListener();
		
		// �������뵥�ֶμӳ�����
		addHyperlinkListenerForPK_ITEM_NO();
	}
	
	/**
	 *���ӽ����ı����Զ���ҳǩ �ϵİ�ť
	 */
	@Override
	protected void setBodyTabActive(String tabcode) {
		Map<String, List<Action>> bodyActionMap = getBodyActionMap();
		List<Action> actions = bodyActionMap.get(tabcode);
		if (actions == null) {
			if(BXConstans.JK_DJDL.equals(((ErmBillBillManageModel)getModel()).getCurrentDjLXVO().getDjdl()))
			{
				actions = bodyActionMap.get(BXConstans.BUS_PAGE_JK);
			}else if(BXConstans.BX_DJDL.equals(((ErmBillBillManageModel)getModel()).getCurrentDjLXVO().getDjdl())){
				actions = bodyActionMap.get(BXConstans.BUS_PAGE);
			}
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
	}
	
	/**
	 * ��Ƭ���泬����
	 */
	private class CardBillItemHyperlinkListener implements BillItemHyperlinkListener {

		@Override
		public void hyperlink(BillItemHyperlinkEvent event) {
			String pk =getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM).getValueObject().toString();
			LinkQuery linkQuery = new LinkQuery(ErmBillConst.MatterApp_DJDL, new String[] { pk });
			SFClientUtil.openLinkedQueryDialog(BXConstans.MTAMN_NODE, getBillCardPanel(), linkQuery);
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
                        || nodeCode.equals(BXConstans.BXINIT_NODECODE_U))
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
					ExceptionHandler.consume(e);
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
//		filtZy();
//		filtPk_Checkele();
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
					getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).setEnabled(false);
					getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setEnabled(false);
					setNotEditFieldFromMtapp(jkbxvo);
				}
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
		
		// ������ػ����ܷ�༭,����Ҫ�������û��ʣ��ͻ��ʵľ���
		String pk_currtype =(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject();//ԭ�ұ���
		getHelper().setCurrRateEnable(jkbxvo.getParentVO().getPk_org(), pk_currtype);
		
	}
	
	private void setNotEditFieldFromMtapp(JKBXVO bxvo) throws BusinessException {
		String[] bodytablecodes = getBillCardPanel().getBillData().getBodyTableCodes();

		BXBusItemVO[] bxBusItemVOS = bxvo.getBxBusItemVOS();
		if(bxBusItemVOS==null){
			return;
		}
		String ma_tradeType = bxBusItemVOS[0].getSrcbilltype();
		
		// VO����
		IMatterAppCtrlService ctrlService = NCLocator.getInstance().lookup(IMatterAppCtrlService.class);
		List<String> mtCtrlBusiFieldList = ctrlService.getMtCtrlBusiFieldList(bxvo.getParentVO().getDjlxbm(),
				ma_tradeType, bxvo.getParentVO().getFydwbm());

		// �����������ֶζ����ɱ༭,���������ֶΣ���ͷ���嶼���ɱ༭
		List<String> specialField = AddFromMtAppEditorUtil.getSpecialField();
		for (int i = 0; i < bxBusItemVOS.length; i++) {
			if (mtCtrlBusiFieldList != null && mtCtrlBusiFieldList.size() > 0) {
				for (String fieldcode : mtCtrlBusiFieldList) {
					if (fieldcode.indexOf('.') != -1) {
						String[] keys = StringUtil.split(fieldcode, ".");
						BillItem item = getBillCardPanel().getBodyItem(bodytablecodes[0],keys[1]);
						if (item != null) {
							getBillCardPanel().getBillModel(bodytablecodes[0]).setCellEditable(i, keys[1], false);
							// ���⣺���Ƶı�������������ֶΣ����汾�ֶ�Ҳ���ɱ༭
							if(keys[1].equals(BXBusItemVO.PK_PCORG) || keys[1].equals(BXBusItemVO.PK_PCORG_V)){
								getBillCardPanel().getBillModel(bodytablecodes[0]).setCellEditable(i, BXBusItemVO.PK_PCORG, false);
								getBillCardPanel().getBillModel(bodytablecodes[0]).setCellEditable(i, BXBusItemVO.PK_PCORG_V, false);
							}
						}
						// ����������ֶΣ����ͷҲҪ����Ϊ���ɱ༭
						if (specialField.contains(keys[1])) {
							BillItem specialItem = getBillCardPanel().getHeadItem(keys[1]);
							if (specialItem != null) {
								specialItem.setEnabled(false);
							}
							BillItem specialItem_v = null;
							if (JKBXHeaderVO.getOrgMultiVersionFieldMap().containsKey(keys[1])) {
								specialItem_v = getBillCardPanel().getHeadItem(JKBXHeaderVO.getOrgVFieldByField(keys[1]));
							}
							if (specialItem_v != null) {
								specialItem_v.setEnabled(false);
							}
						}
					}else {
						// ��ͷ�ֶ�ע���汾
						String fieldcode_v = null;
						if (JKBXHeaderVO.getOrgMultiVersionFieldMap().containsKey(fieldcode)) {
							fieldcode_v = JKBXHeaderVO.getOrgVFieldByField(fieldcode);
						}
						BillItem item = getBillCardPanel().getHeadItem(fieldcode);
						BillItem item_v = getBillCardPanel().getHeadItem(fieldcode_v);
						if (item != null) {
							item.setEnabled(false);
						}
						if (item_v != null) {
							item_v.setEnabled(false);
						}
						// ����������ֶΣ������ҲҪ����Ϊ���ɱ༭
						if (specialField.contains(fieldcode)) {
							BillItem bodyitem = getBillCardPanel().getBodyItem(bodytablecodes[0],fieldcode);
							if (bodyitem != null) {
								bodyitem.setEnabled(false);
							}
							if (fieldcode_v != null) {
								BillItem bodyitem_v = getBillCardPanel().getBodyItem(bodytablecodes[0],fieldcode_v);
								if (bodyitem_v != null) {
									bodyitem_v.setEnabled(false);
								}
							}
						}
					}
				}
			}
		}
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
			
			if (resVO != null) {
				// ����ҳ���ֶδ����������Ʋ��ɱ༭���ֶΣ�����Ĭ��ֵҲ�����������
				getAddFromMtAppEditorUtil().resetBillItem();
			}
			
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
			helper.setPsnInfoByUserId();

			String pkOrg = getModel().getContext().getPk_org();

			//������֯���õ���Ĭ��ֵ
			String currentBillTypeCode = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
			DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(currentBillTypeCode);
			helper.setDefaultWithOrg(currentDjlx.getDjdl(), currentBillTypeCode, pkOrg, false);
			
			pkOrg = getModel().getContext().getPk_org();
//			// ��ʼ�����ݷ�̯��־��ʾ�����ط�̯ҳǩ
			helper.initCostPageShow(getModel().getUiState());
			
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
			
//			//���ݷ��óе���λ�ͳе��������óɱ�����setDefaultWithOrg���д˷�����
//			BillItem centerHeadItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER);
//			if (centerHeadItem != null && centerHeadItem.isEnabled() && centerHeadItem.isShow()) {
//				Object pk_center = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject();
//				Object pk_body_center = getBillCardPanel().getBodyValueAt(0, BXBusItemVO.PK_RESACOSTCENTER);
//				if(pk_center == null && pk_body_center == null){//�ڽ���û�гɱ����ĵ����������Ĭ��ֵ
//					Object pk_fydept = getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDEPTID).getValueObject();
//					Object pk_fydwbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
//					Object pk_resacostcenter = centerHeadItem.getValueObject();
//					if (pk_fydept != null && pk_fydwbm != null && pk_resacostcenter == null) {
//						getEventHandle().setCostCenter(pk_fydept.toString(), pk_fydwbm.toString());
//					}
//				}
//			}
			
			// ��ͷ�ֶ����ൽ���壨���õ��ݻ����������ɱ������ݣ�
//			changeBodyValueByHeadValue();
			
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
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	public void filterHeadItem() {
		//��������
		filtZy();
		
		// ��λ�����˻���Ҫ���ֺ�֧����λ��������������������ʱ���¹��ˡ�
		filtFkyhzh();
		
		// �տ������ʺŸ��ݽ����˺ͱ��ֱ������
		filtSkyhzh();
		
		//�����ֽ��˻�
		filtAccount();
		
		//�����������Ĺ��˺���Ҫ��
		filtPk_Checkele();
		
		//�����ʽ�ƻ���Ŀ
		filtCashProj();
		
		//���˳ɱ�����
		filtResaCostCenter();
		
		//���ݹ�Ӧ�̹��˿��������ʻ�
		filtHbbm();
		
		//������Ŀ����
		filtProjTask();
		
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
		
		// �����տ��ˣ�����Ҫ�߸÷���
		if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			getEventHandle().getHeadFieldHandle().editReceiver();
		}
	}

	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		// ����Ĭ��ֵ
		try {
			String currentBillTypeCode = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
			DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(currentBillTypeCode);

			if(getResVO() != null){
				JKBXVO vo = (JKBXVO) getResVO().getBusiobj();
				// ��������ҵ��ҳǩ����ʾʱ�������������ı���������ա�
				int tabcount = getBillCardPanel().getBodyTabbedPane().getTabCount();
				if(tabcount == 0 ){
					vo.setBxBusItemVOS(null);
				} else {
					List<String> tablecodes = new ArrayList<String>();
					for (int i = 0; i < tabcount; i++) {
						tablecodes.add(((BillScrollPane)getBillCardPanel().getBodyTabbedPane().getComponentAt(i)).getTableCode());
					}
					if(!tablecodes.contains(getBusCode())){
						vo.setBxBusItemVOS(null);
					}
				}
				setValue(vo);
				// ���������ñ���״̬
				getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
				resetRowState();
			}
			helper.setBillDefaultValue(currentDjlx.getDjdl(), currentBillTypeCode);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}

	}
	
	/**
	 * �õ���ǰ���ݵ�ҵ��ҳǩ
	 * @return
	 */
	private String getBusCode()
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

			} catch (Exception e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
		
		if(selectedData != null){//���Ч��
			setValue(selectedData);
		}else{
			this.getBillCardPanel().getBillData().clearViewData();
		}
	}
	@Override
	public void setValue(Object object) {
		try {
			if(object!=null){
				//��Ƭ�������þ���,Ȼ��������ֵ
				JKBXHeaderVO parentVO = ((JKBXVO)object).getParentVO();
				getModel().getContext().setPk_org(parentVO.getPk_org());
				BXUiUtil.resetDecimal(getBillCardPanel(),getModel().getContext().getPk_org(),((JKBXVO)object).getParentVO().getBzbm());
				
				super.setValue(object);
				
				if(((JKBXVO)object).getParentVO().getZy() != null){
					//�����ֶ�Ҫ���⴦��
					UIRefPane component = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
					component.getUITextField().setValue(new String[]{((JKBXVO)object).getParentVO().getZy().toString()});
				}
				
				//���������������⴦��
				String value = BXUiUtil.getDjlxNameMultiLang(((ErmBillBillManageModel) getModel())
						.getCurrentBillTypeCode());getBillCardPanel().setHeadItem(JKBXHeaderVO.DJLXMC, value);
				
				//����VO��ҳǩ����ҵ����
				resetBusItemVOs(object);
				
				((ErmBillBillManageModel)getModel()).setCurrentBillTypeCode(((JKBXVO)object).getParentVO().getDjlxbm());
				
				setCostPageShow(object);
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
			}
			else{
				super.setValue(object);
				getModel().getContext().setPk_org(null);
				
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
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
	 * ���õ��ݽ���ķ�̯ҳǩ�Ƿ���ʾ
	 * @param object
	 */
	private void setCostPageShow(Object object) {
		if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl()
				.equals(BXConstans.BX_DJDL) && !((ErmBillBillManageModel) getModel()).getCurrentBillTypeCode().equals(BXConstans.BILLTYPECODE_RETURNBILL)) {
			if (((JKBXVO)object).getParentVO().getIscostshare() == UFBoolean.TRUE) {
				ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(), true);
			}else{
				ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(), false);
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
    				ExceptionHandler.handleExceptionRuntime(e);
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
			}
		}
	}
	
	/**
	 * ��ʼ����̯ҳǩ�е��ֶ�
	 * 
	 * @throws BusinessException
	 * @throws ValidationException
	 */
	private void initCsharePage() {
		BillModel model = this.getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE);
		if (model != null) {
			String[] names = AggCostShareVO.getBodyMultiSelectedItems();
			
			for(String name : names){
				BillItem item = model.getItemByKey(name);
				if(item != null && item.getComponent() instanceof UIRefPane){
					((UIRefPane) item.getComponent()).setMultiSelectedEnabled(true);
				}
			}
		}
	}

	// �ڱ���������ʱ���Զ����һ��,�������������,����ҳǩ����������
	private void addLine() {
		JKBXVO jkbxvo = (JKBXVO) getValue();
		if (!((ErmBillBillManageModel) getModel()).getCurrentBillTypeCode().equals(BXConstans.BILLTYPECODE_RETURNBILL)
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE)
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)
				&& !getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE_JK)
				&& (jkbxvo.getChildrenVO() == null || jkbxvo.getChildrenVO().length == 0) && getResVO() == null) {
			List<Action> actionList = new ArrayList<Action>();

			if (BXConstans.JK_DJDL.equals(((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl())) {
				actionList = getBodyActionMap().get(BXConstans.BUS_PAGE_JK);
			} else if (BXConstans.BX_DJDL.equals(((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl())) {
				actionList = getBodyActionMap().get(BXConstans.BUS_PAGE);
			}
			if (actionList.size() != 0) {
				AddLineAction action = (AddLineAction) actionList.get(0);
				try {
					action.doAction(null);
					// �����к�������ϵ�Ĭ��ֵ
					setBodyDefaultValue();
				} catch (Exception e) {
					ExceptionHandler.handleExceptionRuntime(e);
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
//		else{//ת�Ƶ��༭ǰ����
//			// ���˽�����
//			getEventHandle().getHeadFieldHandle().initJkbxr();
//		}
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
			getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum,key);
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

	public void fillBillItemValue(JKBXVO value) {
		// �����Ƿ��õ���/�ڳ�����
		if (((ErmBillBillManageModel) getModel()).iscydj()) {
			value.getParentVO().setInit(true);
			if(value.getParentVO().getDjrq()==null){
				value.getParentVO().setDjrq(WorkbenchEnvironment.getInstance().getBusiDate());
			}
		}
		if (((ErmBillBillManageModel) getModel()).isInit()) {
			value.getParentVO().setQcbz(UFBoolean.TRUE);
		}

		helper.prepareForNullJe(value);
		helper.prepareContrast(value);

		clearCopyBodyRowPk(value);

		// ���ó�������Ľ��ҵ��
		try {
			if (value instanceof BXVO && !ArrayUtils.isEmpty(value.getContrastVO())) {
				if (getContrastaction() != null && isContrast) {
					value.setJkHeadVOs(getContrastaction().getSelectedJkVos(value));
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	//��ձ����е�pk�ֶ�
    private void clearCopyBodyRowPk(JKBXVO value)
    {
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
		Map<String, SuperVO> expenseType = getExpenseMap();// ��������
		Map<String, SuperVO> reimtypeMap = getReimtypeMap();// ��������
		// ��ͷ��������
		String reimrule = BxUIControlUtil.doHeadReimAction(vo,
				getReimRuleDataMap(), expenseType, reimtypeMap);
		if (getBillCardPanel().getHeadItem(BXConstans.REIMRULE) != null) {
			getBillCardPanel().setHeadItem(BXConstans.REIMRULE,
					reimrule.toString());
		}
		doBodyReimAction();
	}

	/**
	 * ���屨������
	 */
	protected void doBodyReimAction() {

		JKBXVO bxvo = null;
		bxvo = (JKBXVO) getValue();

		HashMap<String, String> bodyReimRuleMap = getBodyReimRuleMap();
		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,
				getReimRuleDataMap(), bodyReimRuleMap);
		for (BodyEditVO vo : result) {
			getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(),
					vo.getItemkey(), vo.getTablecode());
		}
	}

	/**
	 * ��ȡ���屨����׼Map(tablecode@itemkey,��������pk)
	 *
	 * @return
	 */
	public HashMap<String, String> getBodyReimRuleMap() {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (getBillCardPanel().getBillData().getBillTempletVO() == null
				|| getBillCardPanel().getBillData().getBillTempletVO()
						.getChildrenVO() == null) {
			return hashMap;
		}

		BillTempletBodyVO[] abilltempletbodyvo = (BillTempletBodyVO[]) getBillCardPanel()
				.getBillData().getBillTempletVO().getChildrenVO();

		int i = 0;
		for (int j = abilltempletbodyvo.length; i < j; i++) {
			BillTempletBodyVO bodyvo = abilltempletbodyvo[i];
			String userdefine1 = bodyvo.getUserdefine1();
			if (userdefine1 != null && userdefine1.startsWith("getReimvalue")) {
				String expenseName = userdefine1.substring(userdefine1
						.indexOf("(") + 1, userdefine1.indexOf(")"));
				Collection<SuperVO> values = getExpenseMap().values();
				for (SuperVO vo : values) {
					// ���ڸ÷������ͣ���<tablecode@itemkey,��������pk>����Map��
					if (("\"" + vo.getAttributeValue(ExpenseTypeVO.CODE) + "\"")
							.equals(expenseName)) {
						userdefine1 = vo.getPrimaryKey();
						hashMap.put(bodyvo.getTable_code()
								+ ReimRuleVO.REMRULE_SPLITER
								+ bodyvo.getItemkey(), userdefine1);
					}
				}
			}
		}
		return hashMap;
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

	public Map<String, SuperVO> getExpenseMap() {
		return expenseMap;
	}

	public void setExpenseMap(Map<String, SuperVO> expenseMap) {
		this.expenseMap = expenseMap;
	}

	public Map<String, SuperVO> getReimtypeMap() {
		return reimtypeMap;
	}

	public void setReimtypeMap(Map<String, SuperVO> reimtypeMap) {
		this.reimtypeMap = reimtypeMap;
	}

	public void setReimRuleDataMap(Map<String, List<SuperVO>> reimRuleDataMap) {
		this.reimRuleDataMap = reimRuleDataMap;
	}
	
	public void setReimRuleDataMap(String pk_org, Map<String, List<SuperVO>> reimRuleDataMap) {
		setReimRuleDataMap(reimRuleDataMap);
		reimRuleDataCacheMap.put(pk_org, reimRuleDataMap);
	}
	
	public Map<String, Map<String, List<SuperVO>>> getReimRuleDataCacheMap() {
		return reimRuleDataCacheMap;
	}

	public Map<String, List<SuperVO>> getReimRuleDataMap() {
		String pk_org = null;
		try {// ���ݼ��ż�������������׼���ù���,��ȡ��֯
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

		if (pk_org != null) {
			if (reimRuleDataCacheMap.get(pk_org) == null) {
				List<ReimRuleVO> vos;
				try {
					vos = NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class)
							.queryReimRule(null, pk_org);
					setReimRuleDataMap(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
				reimRuleDataCacheMap.put(pk_org, reimRuleDataMap);
			} else {
				return reimRuleDataCacheMap.get(pk_org);
			}
		}

		return reimRuleDataMap;
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
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
}