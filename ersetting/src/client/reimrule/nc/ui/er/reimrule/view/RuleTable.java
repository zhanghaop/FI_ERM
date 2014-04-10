package nc.ui.er.reimrule.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.reimtype.IReimTypeService;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bill.pub.BillUtil;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;

public class RuleTable extends BatchBillTable implements IComponentWithActions {

	private static final long serialVersionUID = 1L;
	private AbstractUIAppModel treemodel;
	private String centControlItem = null;//���Ŀ�����
	private List<Action> actions = null;
	public void handleEvent(AppEvent event) {
		if(event.getType() == AppEventConst.SELECTION_CHANGED && event.getSource() == getTreemodel())
		{
			refreshTemplate();
		}
		else{
			super.handleEvent(event);
			if(event.getType() == AppEventConst.MODEL_INITIALIZED){
				String pk_billtype = null;
				if (getTreemodel().getSelectedData() != null) {
					pk_billtype = ((DjLXVO) getTreemodel().getSelectedData()).getDjlxbm();
				}
				List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(pk_billtype);
				if (reimruledim!=null && reimruledim.size()>0) {
					for(SuperVO vo:reimruledim)
					{
						if(((ReimRuleDimVO)vo).getControlflag().booleanValue())
						{
							centControlItem=((ReimRuleDimVO)vo).getCorrespondingitem();
							break;
						}
					}
				}
			}
		}
	}
	
	private void refreshTemplate() {
		if(getTreemodel().getSelectedData() == null)
			return;
		final String djlxbm = ((DjLXVO) getTreemodel().getSelectedData()).getDjlxbm();
		if (djlxbm == null){
			return;
		}
		BillData billData = ReimRuleUtil.getTemplateInitBillData(djlxbm,getNodekey());
		List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(djlxbm);
		if (reimruledim!=null && reimruledim.size()>0) {
			getBillCardPanel().setVisible(true);
			BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
			if(bodyItems==null)
				return;
			//����Ҫ��ʾ���м���items��
			List<BillItem> items = new ArrayList<BillItem>();
			for (BillItem item : bodyItems) {
				if(item.getKey().equals(ReimRulerVO.SHOWITEM) || item.getKey().equals(ReimRulerVO.CONTROLITEM)
						|| item.getKey().equals(ReimRulerVO.SHOWITEM_NAME) || item.getKey().equals(ReimRulerVO.CONTROLITEM_NAME)
						|| item.getKey().equals(ReimRulerVO.CONTROLFORMULA) || item.getKey().equals(ReimRulerVO.CONTROLFLAG))
				{
					item.setShow(false);
					items.add(item);
					continue;
				}
				boolean flag=false;
				for(SuperVO vo:reimruledim)
				{
					if(item.getKey().equalsIgnoreCase(((ReimRuleDimVO)vo).getCorrespondingitem()))
					{
						item.setShow(true);
						item.setNull(false);
						item.setName(((ReimRuleDimVO)vo).getDisplayname());
						item.setShowOrder(((ReimRuleDimVO)vo).getOrders());
						String datatypename = ((ReimRuleDimVO)vo).getDatatypename();
						//�޸�item��reftype���޸�ʱ������Ӧ�Ի���
						String reftype = ((ReimRuleDimVO)vo).getDatatype();
						if(datatypename != null)
							reftype += ":" + datatypename;
						initAttribByParseReftype(datatypename,item,reftype);
						items.add(item);
						flag=true;
					}
				}
				if(flag==false)
				{
					item.setShow(false);
					items.add(item);
				}
			}
			// ���ö�Ӧ���յĲ�����֯
			BillItem[] itemArray = items.toArray(new BillItem[] {});
			for (BillItem item : itemArray) {
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane ref = (UIRefPane) item.getComponent();
					if (ref.getRefModel() != null) {
						ref.getRefModel().setPk_org(getModel().getContext().getPk_org());
					}
				}
			}

			billData.setBodyItems(itemArray);
			getBillCardPanel().setBillData(billData);
            //���浽�õ������Ͷ�Ӧ��billdata����
			ReimRuleUtil.getTemplateBillDataMap().put(djlxbm, billData);
		}
		else{
			getBillCardPanel().setVisible(false);
		}
	}
	
	/*
	 * ������������
	 */
	private void initAttribByParseReftype(String datatypename,BillItem item,String reftype) {

		if (reftype == null || reftype.trim().length() == 0) {
			return;
		}
		reftype = reftype.trim();

		String[] tokens = BillUtil.getStringTokensWithNullToken(reftype,
				":");

		if (tokens.length > 0) {
			try {
				// ��ԭ������������
				if (item.getMetaDataProperty() != null) {
					IMetaDataProperty mdp = MetaDataPropertyFactory
							.creatMetaDataUserDefPropertyByType(
									item.getMetaDataProperty(), tokens[0]);
					item.setMetaDataProperty(mdp);
				} else {
					int datatype = Integer.parseInt(tokens[0]);
					if (datatype <= IBillItem.USERDEFITEM)
						item.setDataType(datatype);
				}
				if (tokens.length > 1) {
					//�޸�Ԫ���ݵ�datatype,չʾʱʹ��
					if(datatypename!=null && !datatypename.equals("2")){
						tokens[1]+=",code=N";
					}
					item.setRefType(tokens[1]);
				} else {
					item.setRefType(null);
					return;
				}
			} catch (NumberFormatException e) {
				Logger.warn("�����������ô���" + tokens[0]);
				item.setDataType(IBillItem.STRING);
				item.setRefType(null);
				return;
			}
		}
	}
	
	public void Save() throws BusinessException
	{
		//������׼�޸Ľ���ı���
		getBillCardPanel().stopEditing();
		String currentBodyTableCode = getBillCardPanel()
				.getCurrentBodyTableCode();
		CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRulerVO.class.getName());
		String pk_billtype = null;
		if (getTreemodel().getSelectedData() != null) {
			pk_billtype = ((DjLXVO) getTreemodel().getSelectedData()).getDjlxbm();
		}
		if(pk_billtype==null)
			return;
		String pk_org = getTreemodel().getContext().getPk_org();
		if(pk_org==null)
			pk_org=ReimRulerVO.PKORG;
		ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;

		// ��鱨����׼��ֵ�Ƿ���ȷ�����к��Ŀ�������֡�����Ϊ��
		ReimRuleUtil.checkReimRules(reimRuleVOs,pk_billtype,getTreemodel().getContext().getPk_group(),pk_org,centControlItem);
		//����ʱ,�ȴ�ԭ��׼����ȡ�������ֵ��Ȼ�󱣴��׼��Ȼ��д�����ֵ
		List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(pk_billtype);
		List<ReimRulerVO> returnVos;
		if(vos==null){
			//��ԭ��׼Ϊ�գ���ֱ�ӱ���
			returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(pk_billtype, 
					getTreemodel().getContext().getPk_group(),pk_org,reimRuleVOs);
		}
		else{
			List<SuperVO> vos1 = new ArrayList<SuperVO>();
			StringBuilder cents = new StringBuilder();
			for(SuperVO vo:vos){
				if(!cents.toString().contains((String) vo.getAttributeValue(centControlItem))){
					vos1.add(vo);
					cents.append((String) vo.getAttributeValue(centControlItem));
				}
			}
			NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(pk_billtype, 
					getTreemodel().getContext().getPk_group(),pk_org,reimRuleVOs);
			returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).
					saveControlItem(centControlItem,pk_billtype, getTreemodel().getContext().getPk_group(),
					pk_org,vos1.toArray(new ReimRulerVO[0]));
		}
		List<SuperVO> list = new ArrayList<SuperVO>();
		list.addAll(returnVos);
		ReimRuleUtil.putRule(pk_billtype, list);
		getModel().initModel(list.toArray(new SuperVO[0]));
	} 
	
	/**
	 * ʵ�ֱ༭����߼�
	 * @param e
	 */
	protected void doAfterEdit(BillEditEvent e) {
		Object value = getBillCardPanel().getBodyValueAt(e.getRow(), e.getKey());
		if(value==null){
			getBillCardPanel().setBodyValueAt(null,e.getRow(), e.getKey()+"_name");
			return;
		}
		getBillCardPanel().setBodyValueAt(value.toString(),e.getRow(), e.getKey()+"_name");

		//���ȴ���
//		if (e.getKey().equals(ReimRulerVO.PK_CURRTYPE)) {
//			String currentBodyTableCode = getBillCardPanel()
//					.getCurrentBodyTableCode();
//			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
//					.getBillData().getBodyValueVOs(
//							currentBodyTableCode,
//							ReimRulerVO.class.getName());
//			ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;
//	
//			int currencyPrecision = 0;// ���־���
//	
//			for (ReimRulerVO vo : reimRuleVOs) {
//				try {
//					currencyPrecision = Currency
//							.getCurrDigit((String)vo.getPk_currtype());
//				} catch (Exception e1) {
//					ExceptionHandler.consume(e1);
//				}
//				getBillCardPanel().getBodyItem(ReimRulerVO.AMOUNT).setDecimalDigits(currencyPrecision);
//				
//				getBillCardPanel().setBodyValueAt(vo.getAmount() == null ? UFDouble.ZERO_DBL: 
//					vo.getAmount().setScale(currencyPrecision,UFDouble.ROUND_HALF_UP),
//						e.getRow(), ReimRulerVO.AMOUNT);
//				
//			}
//		}
	}

	public AbstractUIAppModel getTreemodel() {
		return treemodel;
	}

	public void setTreemodel(AbstractUIAppModel treemodel) {
		this.treemodel = treemodel;
		treemodel.addAppEventListener(this);
	}
	
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
}