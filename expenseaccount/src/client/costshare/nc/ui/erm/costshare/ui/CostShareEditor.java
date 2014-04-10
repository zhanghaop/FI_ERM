package nc.ui.erm.costshare.ui;

import java.util.List;

import javax.swing.Action;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.costshare.actions.AddRowAction;
import nc.ui.erm.costshare.actions.DelRowAction;
import nc.ui.erm.costshare.actions.InsertRowAction;
import nc.ui.erm.costshare.common.CSBillForm;
import nc.ui.erm.costshare.common.CSDecimalUtil;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.erm.util.PasteLineAction;
import nc.ui.erm.util.PasteLineToTailAction;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillActionListener;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.action.BillTableLineAction;
import nc.ui.pub.bill.itemeditors.StringBillItemEditor;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillTempletVO;

@SuppressWarnings("serial")
public class CostShareEditor extends CSBillForm implements BillEditListener2,BillActionListener,BillEditListener,BillCardBeforeEditListener{
	
	private int billAction=-1;
	private NCAction rapidShareAction;
	private NCAction returnaction;
	private JKBXHeaderVO bxhead;
	/**
	 * 超链接监听器
	 */
	private BillItemHyperlinkListener linklistener;
	
	

	@Override
	protected void processBillData(BillData data) {
		super.processBillData(data);
		// 设置主组织不可编辑
		BillItem pkorgItem = data.getHeadItem(CostShareVO.PK_ORG);
		BillItem pkorgvItem = data.getHeadItem(CostShareVO.PK_ORG_V);
		pkorgItem.setEdit(false);
		pkorgvItem.setEdit(false);
		// 设置来源单据字段为 String类型，否则报销单无参照会导致数据丢失
		BillItem src_id = data.getHeadItem(CostShareVO.SRC_ID);
		if(src_id!=null){
			src_id.setDataType(IBillItem.STRING);
			src_id.setItemEditor(new StringBillItemEditor(src_id));
		}
		
	}
	
	@Override
	public void initBillCardPanel() {
		try {
			super.initBillCardPanel();
		} catch (Exception e) {
			getModel().setUiState(UIState.NOT_EDIT);
			try {
				returnaction.doAction(null);
			} catch (Exception e1) {
				//没必要的异常信息，所以吃掉！
			}
			setNodekey(null);
			nc.ui.uif2.ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0000")/*@res "错误"*/, NCLangRes.getInstance().
					getStrByID("uif2", "BillCardPanelForm-000000", null, new String[]{getNodekey()}), getModel().getContext());
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTable-000000")/*没有找到设置的单据模板信息*/);
			
		}
		initCsharePage();
		getBillCardPanel().addBodyEditListener2(this);
		getBillCardPanel().addActionListener(this);
		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		
		
		BillScrollPane bodyBillScroll = getBillCardPanel().getBodyPanel(BXConstans.CSHARE_PAGE);
		bodyBillScroll.removeEditAction(getRapidShareAction());
		bodyBillScroll.addEditAction(getRapidShareAction());
		//清除单据模版的粘贴按钮
		bodyBillScroll.replaceDefaultAction(BillScrollPane.PASTELINE,
				new PasteLineAction(bodyBillScroll,getModel().getContext(),CShareDetailVO.PK_CSHARE_DETAIL));
		
		bodyBillScroll.replaceDefaultAction(BillScrollPane.PASTELINETOTAIL,
				new PasteLineToTailAction(bodyBillScroll,getModel().getContext(),CShareDetailVO.PK_CSHARE_DETAIL));
		List<Action> listAction = getBodyActionMap().get("er_cshare_detail");
		for (int i = 0; i < listAction.size(); i++) {
			if(listAction.get(i) instanceof AddRowAction){
				bodyBillScroll.replaceDefaultAction(BillScrollPane.ADDLINE,
						listAction.get(i));
			}
			else if(listAction.get(i) instanceof DelRowAction){
				bodyBillScroll.replaceDefaultAction(BillScrollPane.DELLINE,
						listAction.get(i));
			}
			else if(listAction.get(i) instanceof InsertRowAction){
				bodyBillScroll.replaceDefaultAction(BillScrollPane.INSERTLINE,
						listAction.get(i));
			}
		}
		
		
		getBillCardPanel().getBodyPanel(BXConstans.CSHARE_PAGE).removeEditAction(getRapidShareAction());
		//表体原币精度
		new DefaultCurrTypeBizDecimalListener(getBillCardPanel().getBillModel(),
				CShareDetailVO.BZBM,CShareDetailVO.ASSUME_AMOUNT);
		new DefaultCurrTypeBizDecimalListener(getBillCardPanel().getBillModel(),
				CShareDetailVO.BZBM,CShareDetailVO.SHARE_RATIO);
		//表体汇率
		new CsDetailCardDecimalListener(getBillCardPanel().getBillModel(), CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.BBHL}, CsDetailCardDecimalListener.RATE_TYPE_LOCAL);
		new CsDetailCardDecimalListener(getBillCardPanel().getBillModel(), CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.GROUPBBHL}, CsDetailCardDecimalListener.RATE_TYPE_GROUP);
		new CsDetailCardDecimalListener(getBillCardPanel().getBillModel(), CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.GLOBALBBHL}, CsDetailCardDecimalListener.RATE_TYPE_GLOBAL);

		new CSDetailCardAmontDecimalListener(getBillCardPanel().getBillModel(), getBillCardPanel(),
				new String[]{CShareDetailVO.ASSUME_AMOUNT},
				CSDetailCardAmontDecimalListener.RATE_TYPE_YB);
		new CSDetailCardAmontDecimalListener(getBillCardPanel().getBillModel(), getBillCardPanel(),
				new String[]{CShareDetailVO.BBJE},
				CSDetailCardAmontDecimalListener.RATE_TYPE_LOCAL);
		new CSDetailCardAmontDecimalListener(getBillCardPanel().getBillModel(), getBillCardPanel(),
				new String[]{CShareDetailVO.GROUPBBJE},
				CSDetailCardAmontDecimalListener.RATE_TYPE_GROUP);
		new CSDetailCardAmontDecimalListener(getBillCardPanel().getBillModel(), getBillCardPanel(),
				new String[]{CShareDetailVO.GLOBALBBJE},
				CSDetailCardAmontDecimalListener.RATE_TYPE_GLOBAL);
	
		BillItem item = getBillCardPanel().getHeadItem(CostShareVO.DJBH);
		item.addBillItemHyperlinkListener(getLinklistener());
		
		setUnEditBillitemWhereSql();
	}
	
	private void setUnEditBillitemWhereSql() {
		String[] itemKeys = new String[]{CostShareVO.PK_CHECKELE};
		for(String itemKey : itemKeys){
			BillItem item = getBillCardPanel().getHeadItem(itemKey);
			if(item != null){
				if(((UIRefPane)item.getComponent()).getRefModel() != null){
					((UIRefPane)item.getComponent()).getRefModel().setMatchPkWithWherePart(false);
				}
			}
		}
	}

	protected BillTempletVO createBillTempletVO()
	{
		if(getTemplateContainer()==null){
			return super.createBillTempletVO();
		}else {
			BillTempletVO template = getTemplateContainer().getTemplate(getNodekey(), getPos(), getTabCode());
			if(template==null){
				billCardPanel.setBillType(getModel().getContext().getNodeCode());
				billCardPanel.setBusiType(null);
				billCardPanel.setOperator(getModel().getContext().getPk_loginUser());
				billCardPanel.setCorp(getModel().getContext().getPk_group());
				template = billCardPanel.getDefaultTemplet(billCardPanel.getBillType(), 
						null, 
						billCardPanel.getOperator(), 
						billCardPanel.getCorp(), 
						getNodekey(),
						null);
			}
			return template;
		}
	}
	
	@Override
	public void setValue(Object object) {
		if(object !=null){
			CostShareVO costShareVO = (CostShareVO)((AggCostShareVO)object).getParentVO();
			BillItem headZyItem = getBillCardPanel().getHeadItem("zy");
			if(headZyItem!=null){
				String defaultValue = headZyItem.getDefaultValue();
				if(StringUtil.isEmpty(costShareVO.getZy()) && !StringUtil.isEmpty(defaultValue)){
					costShareVO.setZy(defaultValue);
				}
			}
		}
		super.setValue(object);
		if (object!=null) {
			String bzbm = (String)getHeadValue(CostShareVO.BZBM);
			if (bzbm!=null) {
//				getBillCardPanel().getHeadItem(CostShareVO.YBJE).setDecimalDigits(Currency.getCurrDigit(bzbm));
//				getBillCardPanel().getHeadItem(CostShareVO.TOTAL).setDecimalDigits(Currency.getCurrDigit(bzbm));
				try {
					CSDecimalUtil.resetDecimal(getBillCardPanel(),getBillCardPanel().getHeadItem(CostShareVO.PK_ORG).getValueObject().toString(),getBillCardPanel().getHeadItem(CostShareVO.BZBM).toString());
				} catch (Exception e) {
					ExceptionHandler.handleRuntimeException(e);
				}
			}
		}

		if(object != null && object instanceof AggCostShareVO){
			CostShareVO parent = (CostShareVO)((AggCostShareVO)object).getParentVO();
			bxhead = parent.getBxheadvo();
			//单据类型名称特殊处理
			String value = BXUiUtil.getDjlxNameMultiLang(parent.getPk_tradetype());
			getBillCardPanel().setHeadItem("tradetype", value);
		}else{
			bxhead = null;
		}
	}
	
	
	

	@Override
	public Object getValue() {
		Object value = super.getValue();
		if(value != null && value instanceof AggCostShareVO){
			CostShareVO parent = (CostShareVO)((AggCostShareVO)value).getParentVO();
			parent.setBxheadvo(bxhead);
		}
		return value;
	}
	
	
	@Override
	protected void onAdd() {
		initNewBillCardPanel();
		super.onAdd();
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			if (getModel().getSelectedData() != null) {
				AggCostShareVO aggVo = (AggCostShareVO) getModel().getSelectedData();
				((CsBillManageModel) getModel()).setTrTypeCode(((CostShareVO) aggVo.getParentVO()).getPk_tradetype());
				initNewBillCardPanel();
			}
		}
	}

	public void loadFormula() {
		getBillCardPanel().getHeadItem(CostShareVO.PK_GROUP).setValue(getModel().getContext().getPk_group());
		getBillCardPanel().getHeadItem(CostShareVO.PK_TRADETYPE).setValue(((CsBillManageModel)getModel()).getTrTypeCode());
	}
	
	private void initNewBillCardPanel() {
		if (!getNewNodeKey().equals(getNodekey())) {
			((CsBillManageModel)getModel()).setTrTypeCode(getNewNodeKey());
			setNodekey(getNewNodeKey());
			initBillCardPanel();
			setValue(getModel().getSelectedData());
		}
	}
	
	private String getNewNodeKey() {
		return ((CsBillManageModel) getModel()).getTrTypeCode();
	}
	
	/**
	 * 表体行改变事件
	 */
	public void bodyRowChange(BillEditEvent e) {
		if(billAction==BillTableLineAction.ADDLINE||billAction==BillTableLineAction.INSERTLINE ){
			UIRefPane zfRef = (UIRefPane) getBillCardPanel().getHeadItem(CostShareVO.PK_ORG).getComponent();
			getBillCardPanel().setBodyValueAt(new DefaultConstEnum(zfRef.getRefPK(),zfRef.getRefName()), e.getRow(), CShareDetailVO.ASSUME_ORG);
			UIRefPane fydept = (UIRefPane) getBillCardPanel().getHeadItem(CostShareVO.FYDEPTID).getComponent();
			getBillCardPanel().setBodyValueAt(new DefaultConstEnum(fydept.getRefPK(),fydept.getRefName()), e.getRow(), CShareDetailVO.ASSUME_DEPT);
			setBodyVal(e, CostShareVO.FYDWBM, CShareDetailVO.ASSUME_ORG);
			setBodyVal(e, CostShareVO.FYDEPTID, CShareDetailVO.ASSUME_DEPT);
			setBodyVal(e, CostShareVO.BZBM, CShareDetailVO.BZBM);
			billAction=-1;
		}
	}
	
	/**
	 * 取表体的值设置到表体
	 * @param e
	 * @param h_field
	 * @param b_field
	 */
	public void setBodyVal(BillEditEvent e,String h_field,String b_field){
		UIRefPane fydept = (UIRefPane) getBillCardPanel().getHeadItem(h_field).getComponent();
		getBillCardPanel().setBodyValueAt(new DefaultConstEnum(fydept.getRefPK(),fydept.getRefName()), e.getRow(), b_field);
	}
	
	/**
	  * 初始化分摊页签中的字段
	  * @throws ValidationException 
	  */
	private void initCsharePage() {
	  try {
		  String[] names = AggCostShareVO.getBodyMultiSelectedItems();
			
			for(String name : names){
				BillItem item = this.getBillCardPanel().getBodyItem(name);
				if(item != null && item.getComponent() instanceof UIRefPane){
					((UIRefPane) item.getComponent()).setMultiSelectedEnabled(true);
				}
			}
	  } catch (Exception e) {
	   Logger.error(e.getMessage(), e);
	  }
	 }
	
	/** 
	 * 编辑后事件处理
	 */
	public void afterEdit(BillEditEvent e) {
		// 分摊控制
		if(e.getKey().equals(CShareDetailVO.JOBID)){
			getBillCardPanel().setBodyValueAt(null, e.getRow(), CShareDetailVO.PROJECTTASK + "_ID");
		}
		
		ErmForCShareUiUtil.doCShareAfterEdit(e, getBillCardPanel());
		if (e.getKey().equals(CShareDetailVO.PK_ORG)) {
			afterDefaultData();
		}
	}
	
	@Override
	public void setDefaultValue() {
		getBillCardPanel().getHeadItem(CostShareVO.BILLDATE).setValue(WorkbenchEnvironment.getInstance().getBusiDate());
		getBillCardPanel().getHeadItem(CostShareVO.SRC_TYPE).setValue(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL);
		getBillCardPanel().getHeadItem(CostShareVO.PK_GROUP).setValue(getModel().getContext().getPk_group());
		getBillCardPanel().getHeadItem(CostShareVO.PK_BILLTYPE).setValue("265X");

	}
	
	private void afterDefaultData(){
		String orgVal = (String) getBillCardPanel().getHeadItem(CostShareVO.PK_ORG).getValueObject();
		BillItem[] headItems = getBillCardPanel().getHeadItems();
		if (orgVal!=null) {
			getModel().getContext().setPk_org(orgVal);
			for (int i = 0; i < headItems.length; i++) {
				if(headItems[i].getComponent() instanceof UIRefPane && ((UIRefPane)headItems[i].getComponent()).getRefModel() != null){
					((UIRefPane)headItems[i].getComponent()).setPk_org(orgVal);
				}
			}
		}else {
			for (int i = 0; i < headItems.length; i++) {
				if(headItems[i].getComponent() instanceof UIRefPane && ((UIRefPane)headItems[i].getComponent()).getRefModel() != null){
					((UIRefPane)headItems[i].getComponent()).setValue(null);
				}
			}
		}
		
	}
	
	/** 
	 * 表体编辑前事件处理
	 */
	public boolean beforeEdit(BillEditEvent e) {
		ErmForCShareUiUtil.doCShareBeforeEdit(e, getBillCardPanel());
//		BillItem item = (BillItem) e.getSource();
//		if(e.getKey().equals(CShareDetailVO.ASSUME_AMOUNT)){
//			String bzbm = (String)getHeadValue(CostShareVO.BZBM);
//			if (bzbm!=null) {
//				item.setDecimalDigits(Currency.getCurrDigit(bzbm));
//			}else {
//				item.setValue(null);
//			}
//		}
		try {
			ErmForCShareUiUtil.crossCheck(e.getKey(), this, "N");
		} catch (BusinessException e1) {
			ExceptionHandler.handleRuntimeException(e1);
		}
		return true;
	}
	protected Object getHeadValue(String key) {
		return getBillCardPanel().getHeadItem(key).getValueObject();
	}

	public boolean onEditAction(int action) {
		billAction = action;
		return true;
	}

	/**
	 * 费用结转单表头编辑前事件
	 */
	@Override
	public boolean beforeEdit(BillItemEvent e) {
		//事由 字段需要根据组织过滤.
		String key = e.getItem().getKey();
		if(CostShareVO.ZY.equals(key)){
			String pk_org = getBillCardPanel().getHeadItem(CostShareVO.PK_ORG).getValueObject().toString();
			((UIRefPane) e.getItem().getComponent()).setPk_org(pk_org);
		}
		try {
			ErmForCShareUiUtil.crossCheck(e.getItem().getKey(), this, "Y");
		} catch (BusinessException e1) {
			ExceptionHandler.handleRuntimeException(e1);
		}
		return e.getItem().isEdit();
	}

	public NCAction getRapidShareAction() {
		return rapidShareAction;
	}

	public void setRapidShareAction(NCAction rapidShareAction) {
		this.rapidShareAction = rapidShareAction;
	}

	public void setReturnaction(NCAction returnaction) {
		this.returnaction = returnaction;
	}

	public NCAction getReturnaction() {
		return returnaction;
	}

	public void setLinklistener(BillItemHyperlinkListener linklistener) {
		this.linklistener = linklistener;
	}

	public BillItemHyperlinkListener getLinklistener() {
		return linklistener;
	}
 
}
