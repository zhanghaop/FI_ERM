package nc.ui.er.reimrule;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.fi.pub.Currency;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bill.pub.BillUtil;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class ReimRuleUtil {
	/**
	 * ĳ����������(���罻ͨ�ѱ�����)�ĳ�ʼ���ݽṹ(��ͷ)
	 */
	private static Map<String,BillData> templateBillDataMap;
	/**
	 * ĳ����������(���罻ͨ�ѱ�����)�ľ��屨����׼(����)
	 */
	private static Map<String, List<SuperVO>> dataMapRule = new HashMap<String, List<SuperVO>>();
	/**
	 * ĳ����������(���罻ͨ�ѱ�����)�ı���ά��(������Щ��)
	 */
	private static Map<String, List<SuperVO>> dataMapDim = new HashMap<String, List<SuperVO>>();
	//������Ԫ����ѡ��Ի���
	private static MDPropertyRefPane bxBillRefPane = null;
	/*������Ԫ����ID*/
	private static String bx_beanid = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";
	
	//��Ԫ����ѡ��Ի���
	private static MDPropertyRefPane jkBillRefPane = null;
	/*��Ԫ����ID*/
	private static String jk_beanid = "e0499b58-c604-48a6-825b-9a7e4d6dacca";
	
	// ���幫ʽ���� and show Formula referPane
	private static MDFormulaRefPane jk_referPanlFormula = null;
	// ���幫ʽ���� and show Formula referPane
	private static MDFormulaRefPane bx_referPanlFormula = null;
	
	static class CentCellEditor implements TableCellEditor{
		
		public boolean isCellEditable(EventObject eventobject)
	    {
	        return false;
	    }

		@Override
		public Component getTableCellEditorComponent(JTable jtable, Object obj,
				boolean flag, int i, int j) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addCellEditorListener(CellEditorListener celleditorlistener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cancelCellEditing() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeCellEditorListener(
				CellEditorListener celleditorlistener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean shouldSelectCell(EventObject eventobject) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public static Map<String, BillData> getTemplateBillDataMap() {
		if(templateBillDataMap==null){
			templateBillDataMap = new HashMap<String, BillData>();
		}
		return templateBillDataMap;
	}
	
	public static BillData getTemplateInitBillData(final String tradetype,final String nodecode) {
		if (getTemplateBillDataMap().get(tradetype) == null) {
			final BillCardPanel billCardPanel = new BillCardPanel();
			billCardPanel.loadTemplet(nodecode, null, ErUiUtil.getPk_user(),ErUiUtil.getBXDefaultOrgUnit());
			getTemplateBillDataMap().put(tradetype, billCardPanel.getBillData());
		}
		return getTemplateBillDataMap().get(tradetype);
	}

	//dim
	public static Map<String, List<SuperVO>> getDataMapDim() {
		return dataMapDim;
	}

	public static void setDataMapDim(Map<String, List<SuperVO>> dataMapDim1) {
		dataMapDim = dataMapDim1;
	}
	
	public static void putDim(String pk_billtype, List<SuperVO> list){
		dataMapDim.put(pk_billtype, list);
	}
	
	//rule
	public static Map<String, List<SuperVO>> getDataMapRule() {
		return dataMapRule;
	}

	public static void setDataMapRule(Map<String, List<SuperVO>> dataMapRule1) {
		dataMapRule = dataMapRule1;
	}

	public static void putRule(String pk_billtype, List<SuperVO> list){
		dataMapRule.put(pk_billtype, list);
	}
	
	//check
	public static void checkReimRules(ReimRulerVO[] reimRuleVOs,String pk_billtype,String pk_group,String pk_org,String controlitem)
		throws BusinessException {
		List<String> keys = new ArrayList<String>();
		int i = 0;
		
		//��ȡ����Ҫ���бȽϵ����ԣ�����attrs
		List<SuperVO> dim=dataMapDim.get(pk_billtype);
		List<String> attrs = new ArrayList<String>();
		for(SuperVO vo:dim){
			attrs.add(((ReimRuleDimVO)vo).getCorrespondingitem());
		}
		
		//������׼���м��
		for (ReimRulerVO rule : reimRuleVOs) {
			//����Ƿ��п�ֵ
			if (controlitem != null && rule.getAttributeValue(controlitem) == null) {
				throw new BusinessException("���Ŀ������Ϊ�գ���"+String.valueOf(i + 1)+"��");
			}
			
			if (rule.getAttributeValue("pk_currtype") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000497",null,new String[]{String.valueOf(i + 1)})/*
																			 * @res
																			 * "�������ñ�����д���֣���i�У�������ʧ��!"
																	 */);
			}
		
			if (rule.getAttributeValue("amount") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000500",null,new String[]{String.valueOf(i + 1)})/*
																			 * @res
																			 * "�������ñ�����д����i�У�������ʧ��!"
																	 */);
			}
			//����Ƿ����ظ���׼
			StringBuffer key = new StringBuffer();
			for (String attr : attrs) {
				Object value = rule.getAttributeValue(attr);
				if(value instanceof IConstEnum)
					rule.setAttributeValue(attr,((IConstEnum)value).getValue());
				key.append(rule.getAttributeValue(attr));
		
				if (attr.equals("pk_currtype")) {
					int scale = 2;
					try {
		
						scale = Currency.getCurrDigit(rule.getAttributeValue(
								attr).toString());
					} catch (Exception e) {
						ExceptionHandler.consume(e);
					}
					rule.setAmount(rule.getAmount().setScale(scale,
							UFDouble.ROUND_HALF_UP));
				}
			}
			if (keys.contains(key.toString())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000501")/*
																			 * @res
																			 * "���������а�������ά��ȫ����ͬ�ļ�¼������ʧ��!"
																			 */);
			} else {
				keys.add(key.toString());
			}
			//��ֵ������������Ҫ���⴦��
			if(rule.getPk_expensetype()!=null && getExpenseMap().get(rule.getPk_expensetype())==null)
				rule.setPk_expensetype(getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
			rule.setPriority(Integer.valueOf(i++));
			rule.setPk_billtype(pk_billtype);
			rule.setPk_group(pk_group);
			rule.setPk_org(pk_org);
		}
	}
	
	public static void addReimRules(ReimRulerVO[] reimRuleVOs){
		//������׼���м��
		for (ReimRulerVO rule : reimRuleVOs) {
			if(rule.getPk_expensetype()!=null)
				rule.setPk_expensetype(getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
		}
	}

	public static int checkReimDims(ReimRuleDimVO[] reimDimVOs,String pk_billtype,String pk_group,String pk_org)
		throws BusinessException {
		List<ReimRuleDimVO> returnVOs = new ArrayList<ReimRuleDimVO>();
		List<String> keys = new ArrayList<String>();
		int currow = 0;
		int controlflags=0;
		for (ReimRuleDimVO dim : reimDimVOs) {
			currow++;
			if(dim.getDisplayname()==null && dim.getDatatype()==null && dim.getReferential()==null && dim.getBillref()==null && dim.getShowflag().equals(UFBoolean.FALSE) && dim.getControlflag().equals(UFBoolean.FALSE)){
				continue;
			}
			String displayname = dim.getDisplayname();
			if (displayname == null) {
				throw new BusinessException("ά�����ñ�����д��ʾ����(��"+currow+"��)������ʧ��!");
			}
			else{
				if (keys.contains(displayname)) {
					throw new BusinessException("ά�������а���������ͬ�ļ�¼������ʧ��!");
				} else {
					keys.add(displayname);
				}
			}
			if (dim.getDatatype() == null) {
				throw new BusinessException("ά�����ñ�����д��������(��"+currow+"��)������ʧ��!");
			}
			if(dim.getControlflag().booleanValue())
				controlflags++;
			if(controlflags>1){
				throw new BusinessException("���Ŀ��������ֻ����һ�");
			}
			dim.setPk_billtype(pk_billtype);
			dim.setPk_group(pk_group);
			dim.setPk_org(pk_org);
			dim.setOrders(currow);
			returnVOs.add(dim);
		}
		reimDimVOs = returnVOs.toArray(new ReimRuleDimVO[0]);
		return currow;
	}
	
	/**
	 * ����״̬ҳ�棬��Ϊֻ�������̬���ư�ť�ſ��ã����Բ���Ҫ�޸��Ҳ���棬ֻ��Ҫ������
	 */
	public static void showControlPage(BillCardPanel cardPanel,BillData billData,List<SuperVO> reimruledim,
			String centControlItem,String pk_org,String djlx) {
		if(centControlItem == null){
			MessageDialog.showHintDlg(cardPanel,"����","δ���ú��Ŀ�����������ý����н���ѡ��");
			cardPanel.setVisible(false);
		}
		//����Ҫ��ʾ���м���items��
		BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
		if(bodyItems==null)
			return;
		List<BillItem> items = new ArrayList<BillItem>();
		for (BillItem item : bodyItems) {
			if(item.getKey().equals(ReimRulerVO.SHOWITEM) || item.getKey().equals(ReimRulerVO.CONTROLITEM)
					|| item.getKey().equals(ReimRulerVO.CONTROLFLAG) || item.getKey().equalsIgnoreCase(centControlItem)
					|| item.getKey().equals(ReimRulerVO.CONTROLFORMULA))
			{
				item.setShow(true);
				items.add(item);
			}
			else
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
					ref.getRefModel().setPk_org(pk_org);
				}
			}
		}

		billData.setBodyItems(itemArray);
		cardPanel.setBillData(billData);
		
		setTableCellEditor(cardPanel,djlx,centControlItem);
		List<SuperVO> vos = getDataMapRule().get(djlx);
		if(vos!=null){
			List<SuperVO> vos1 = new ArrayList<SuperVO>();
			StringBuilder cents = new StringBuilder();
			for(SuperVO vo:vos){
				if(!cents.toString().contains((String) vo.getAttributeValue(centControlItem))){
					vos1.add(vo);
					cents.append((String) vo.getAttributeValue(centControlItem));
				}
			}
			cardPanel.getBillData().setBodyValueVO(
					vos1.toArray(new SuperVO[] {}));
		}
		if(cardPanel.getBillModel()!=null){
			cardPanel.getBillModel().execLoadFormula();
			cardPanel.getBillModel().loadLoadRelationItemValue();
		}
	}
	
	/**
	 * ����״̬ҳ��ı༭��
	 */
	private static void setTableCellEditor(BillCardPanel cardPanel,String djlx,String centControlItem){
		if(djlx.startsWith("263")){
			//263��ʾ���ݶ�Ӧ��༭��Ӧ��Ϊ��
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.SHOWITEM,
					new BillCellEditor(getJKBillRefPane()));
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLITEM,
					new BillCellEditor(getJKBillRefPane()));
			//����ʽ�༭��
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFORMULA, new BillCellEditor(getJKReferFormula(cardPanel)));
		}
		else{
			//264��ʾ���ݶ�Ӧ��༭��Ӧ��Ϊ������
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.SHOWITEM,
					new BillCellEditor(getBXBillRefPane()));
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLITEM,
					new BillCellEditor(getBXBillRefPane()));
			//��������ʽ�༭��
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFORMULA, new BillCellEditor(getBXReferFormula(cardPanel)));
		}
		//����ģʽ�༭��  ��ʾ�Ϳ�������
//		cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFLAG,
//				new BillCellEditor(getControlModelPane()));
		//���Ŀ�����ɱ༭�ı༭��
		cardPanel.getBodyPanel().setTableCellEditor(centControlItem.toLowerCase(), new CentCellEditor());
	}
	
	//����ʽ������� 
	private static MDFormulaRefPane getJKReferFormula(BillCardPanel panel) {
		try {
			if (jk_referPanlFormula == null) {
				jk_referPanlFormula = new MDFormulaRefPane(panel,jk_beanid,null);
				jk_referPanlFormula.setAutoCheck(false);
			}
		} catch (Exception ex) {
			Logger.debug(ex);
		}
		return jk_referPanlFormula;
	}
	
	//��������ʽ������� 
	private static MDFormulaRefPane getBXReferFormula(BillCardPanel panel) {
		try {
			if (bx_referPanlFormula == null) {
				bx_referPanlFormula = new MDFormulaRefPane(panel,bx_beanid,null);
				bx_referPanlFormula.setAutoCheck(false);
			}
		} catch (Exception ex) {
			Logger.debug(ex);
		}
		return bx_referPanlFormula;
	}
	
	
	public static void showModifyPage(BillCardPanel cardPanel,BillData billData,List<SuperVO> reimruledim,String pk_org) {
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
					//�������Ͳ���Ҫ������
					if(!item.getKey().equals(ReimRulerVO.PK_EXPENSETYPE)){
						//�޸�item��reftype���޸�ʱ������Ӧ�Ի���
						String reftype = ((ReimRuleDimVO)vo).getDatatype();
						if(datatypename != null)
							reftype += ":" + datatypename;
						initAttribByParseReftype(datatypename,item,reftype);
//						System.out.println("new:"+item.getDataType()+","+item.getRefType()+";"
//								+item.getMetaDataProperty().getDataType()+","+item.getMetaDataProperty().getRefType()+";"
//								+item.getMetaDataProperty().getRefBusinessEntity()+","+datatypename+";");
					}
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
					ref.getRefModel().setPk_org(pk_org);
				}
			}
		}

		billData.setBodyItems(itemArray);
		cardPanel.setBillData(billData);
	}
	/*
	 * ������������
	 */
	private static void initAttribByParseReftype(String datatypename,BillItem item,String reftype) {

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
//			catch (MetaDataException e) {
//				Logger.warn("�����������ô���" + tokens[0]);
//				item.setDataType(IBillItem.STRING);
//				item.setRefType(null);
//				return;
//			}
		}
	}
	
	//��������Ӧ��Ԫ���ݱ༭��
	public static MDPropertyRefPane getBXBillRefPane() {
		if (bxBillRefPane == null) {
			bxBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"201100_0", "0201100-0011")/* @res "��Դ�����ֶ�" */, bx_beanid,null);
			}
		return bxBillRefPane;
	}
	
	//����Ӧ��Ԫ���ݱ༭��
	public static MDPropertyRefPane getJKBillRefPane() {
		if (jkBillRefPane == null) {
			jkBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"201100_0", "0201100-0011")/* @res "��Դ�����ֶ�" */, jk_beanid,null);
			}
		
		return jkBillRefPane;
	}
	
	private static Map<String, SuperVO> expenseMap; // �������ͻ�������
	private static Map<String, SuperVO> expenseNameMap; // �������ͻ�������
	
	static Map<String, SuperVO> getExpenseMap() {
		if(expenseMap == null)
		{
			IArapCommonPrivate service = (IArapCommonPrivate)NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
			try {
				Collection<SuperVO> expenseType = service.getVOs(ExpenseTypeVO.class, "pk_group='" + BXUiUtil.getPK_group()+"'", false);
				expenseMap = VOUtils.changeCollectionToMap(expenseType);
				expenseNameMap = VOUtils.changeCollectionToMap(expenseType,getMultiFieldName("name"));
			} catch (BusinessException e) {
			}
		}
		return expenseMap;
	}
	
	static Map<String, SuperVO> getExpenseNameMap() {
		if(expenseNameMap == null)
		{
			IArapCommonPrivate service = (IArapCommonPrivate)NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
			try {
				Collection<SuperVO> expenseType = service.getVOs(ExpenseTypeVO.class, "pk_group='" + BXUiUtil.getPK_group()+"'", false);
				expenseMap = VOUtils.changeCollectionToMap(expenseType);
				expenseNameMap = VOUtils.changeCollectionToMap(expenseType,getMultiFieldName("name"));
			} catch (BusinessException e) {
			}
		}
		return expenseNameMap;
	}
	
	/**
	 * ��ȡ�����ֶ����ƣ�name2,name,name3��
	 * @param namefield
	 * @return
	 */
	private static String getMultiFieldName(String namefield) {
		int intValue = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		if(intValue>1){
			namefield=namefield+intValue;
		}
		return namefield;
	}
	
	
	@SuppressWarnings("unchecked")
	public static void afterEdit(BillCardPanel cardPanel,String djlx,BillEditEvent e) {
		Object value = cardPanel.getBodyValueAt(e.getRow(), e.getKey());
		if(value==null)
			return;
		if(e.getKey().equals(ReimRulerVO.PK_EXPENSETYPE))
		{
			ExpenseTypeVO exvo = (ExpenseTypeVO)getExpenseMap().get(value);
			cardPanel.setBodyValueAt(exvo.getAttributeValue(getMultiFieldName("name")),e.getRow(), e.getKey()+"_name");
			cardPanel.getBillModel().execLoadFormulaByRow(e.getRow());
		}
		else
			cardPanel.setBodyValueAt(value.toString(),e.getRow(), e.getKey()+"_name");

		//���ȴ���
		if (e.getKey().equals(ReimRulerVO.PK_CURRTYPE)) {
			String currentBodyTableCode = cardPanel
					.getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = cardPanel
					.getBillData().getBodyValueVOs(
							currentBodyTableCode,
							ReimRulerVO.class.getName());
			ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;
	
			int currencyPrecision = 0;// ���־���
	
			for (ReimRulerVO vo : reimRuleVOs) {
				try {
					currencyPrecision = Currency
							.getCurrDigit((String)vo.getPk_currtype());
				} catch (Exception e1) {
					ExceptionHandler.consume(e1);
				}
				cardPanel.getBodyItem(ReimRulerVO.AMOUNT).setDecimalDigits(currencyPrecision);
				
				cardPanel.setBodyValueAt(vo.getAmount() == null ? UFDouble.ZERO_DBL: 
					vo.getAmount().setScale(currencyPrecision,UFDouble.ROUND_HALF_UP),
						e.getRow(), ReimRulerVO.AMOUNT);
				
			}
		}
		else if(e.getKey().equals(ReimRulerVO.SHOWITEM)) {
			cardPanel.setBodyValueAt(null, e.getRow(), ReimRulerVO.SHOWITEM_NAME);
			Vector vec;
			if(djlx.startsWith("263"))
				vec = getJKBillRefPane().getRefModel().getSelectedData();
			else
				vec = getBXBillRefPane().getRefModel().getSelectedData();
			if (vec != null) {
				for (Object object:vec) {
					String fieldcode = ((Vector)object).get(0).toString();
					cardPanel.setBodyValueAt(fieldcode, e.getRow(), ReimRulerVO.SHOWITEM_NAME);
				}
			}
		} 
		else if(e.getKey().equals(ReimRulerVO.CONTROLITEM)) {
			cardPanel.setBodyValueAt(null, e.getRow(), ReimRulerVO.CONTROLITEM_NAME);
			Vector vec;
			if(djlx.startsWith("263"))
				vec = getJKBillRefPane().getRefModel().getSelectedData();
			else
				vec = getBXBillRefPane().getRefModel().getSelectedData();
			if (vec != null) {
				for (Object object:vec) {
					String fieldcode = ((Vector)object).get(0).toString();
					cardPanel.setBodyValueAt(fieldcode, e.getRow(), ReimRulerVO.CONTROLITEM_NAME);
				}
			}
		} 
	}
}
