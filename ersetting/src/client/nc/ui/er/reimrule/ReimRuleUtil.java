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
	 * 某个交易类型(比如交通费报销单)的初始数据结构(表头)
	 */
	private static Map<String, BillData> templateBillDataMap = new HashMap<String, BillData>();
	/**
	 * 某个交易类型(比如交通费报销单)的具体报销标准(表体)
	 */
	private static Map<String, List<SuperVO>> dataMapRule = new HashMap<String, List<SuperVO>>();
	/**
	 * 某个交易类型(比如交通费报销单)的报销维度(具有哪些列)
	 */
	private static Map<String, List<SuperVO>> dataMapDim = new HashMap<String, List<SuperVO>>();

	/* 报销单元数据ID */
	private static String bx_beanid = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";

	// 报销单元数据选择对话框
	private static MDPropertyRefPane bxBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0011")/*
																																							 * @
																																							 * res
																																							 * "来源单据字段"
																																							 */, bx_beanid, null);

	/* 借款单元数据ID */
	private static String jk_beanid = "e0499b58-c604-48a6-825b-9a7e4d6dacca";

	// 借款单元数据选择对话框
	private static MDPropertyRefPane jkBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0011")/*
																																							 * @
																																							 * res
																																							 * "来源单据字段"
																																							 */, jk_beanid, null);

	// 定义公式参照 and show Formula referPane
	// private static MDFormulaRefPane jk_referPanlFormula = null;
	// 定义公式参照 and show Formula referPane
	// private static MDFormulaRefPane bx_referPanlFormula = null;

	static class CentCellEditor implements TableCellEditor {

		public boolean isCellEditable(EventObject eventobject) {
			return false;
		}

		@Override
		public Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int i, int j) {
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
		public void removeCellEditorListener(CellEditorListener celleditorlistener) {
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
		return templateBillDataMap;
	}

	public static BillData getTemplateInitBillData(final String tradetype, final String nodecode) {
		if (getTemplateBillDataMap().get(tradetype) == null) {
			final BillCardPanel billCardPanel = new BillCardPanel();
			billCardPanel.loadTemplet(nodecode, null, ErUiUtil.getPk_user(), ErUiUtil.getBXDefaultOrgUnit());
			getTemplateBillDataMap().put(tradetype, billCardPanel.getBillData());
		}
		return getTemplateBillDataMap().get(tradetype);
	}

	// dim
	public static Map<String, List<SuperVO>> getDataMapDim() {
		return dataMapDim;
	}

	public static void setDataMapDim(Map<String, List<SuperVO>> dataMapDim1) {
		dataMapDim = dataMapDim1;
	}

	public static void putDim(String pk_billtype, List<SuperVO> list) {
		dataMapDim.put(pk_billtype, list);
	}

	// rule
	public static Map<String, List<SuperVO>> getDataMapRule() {
		return dataMapRule;
	}

	public static void setDataMapRule(Map<String, List<SuperVO>> dataMapRule1) {
		dataMapRule = dataMapRule1;
	}

	public static void putRule(String pk_billtype, List<SuperVO> list) {
		dataMapRule.put(pk_billtype, list);
	}

	// check
	public static void checkReimRules(ReimRulerVO[] reimRuleVOs, String pk_billtype, String pk_group, String pk_org, String controlitem) throws BusinessException {
		List<String> keys = new ArrayList<String>();
		int i = 0;

		// 提取出需要进行比较的属性，存入attrs
		List<SuperVO> dim = dataMapDim.get(pk_billtype);
		List<String> attrs = new ArrayList<String>();
		for (SuperVO vo : dim) {
			attrs.add(((ReimRuleDimVO) vo).getCorrespondingitem());
		}

		// 逐条标准进行检查
		for (ReimRulerVO rule : reimRuleVOs) {
			// 检查是否有空值
			if (controlitem != null && rule.getAttributeValue(controlitem) == null) {
				throw new BusinessException("核心控制项不能为空！第" + String.valueOf(i + 1) + "行");
			}

			if (rule.getAttributeValue("pk_currtype") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000497", null, new String[] { String.valueOf(i + 1) })/*
																																										 * @
																																										 * res
																																										 * "规则设置必须填写币种（第i行），保存失败!"
																																										 */);
			}

			if (rule.getAttributeValue("amount") == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000500", null, new String[] { String.valueOf(i + 1) })/*
																																										 * @
																																										 * res
																																										 * "规则设置必须填写金额（第i行），保存失败!"
																																										 */);
			}
			// 检查是否有重复标准
			StringBuffer key = new StringBuffer();
			for (String attr : attrs) {
				Object value = rule.getAttributeValue(attr);
				if (value instanceof IConstEnum)
					rule.setAttributeValue(attr, ((IConstEnum) value).getValue());
				key.append(rule.getAttributeValue(attr));

				if (attr.equals("pk_currtype")) {
					int scale = 2;
					try {

						scale = Currency.getCurrDigit(rule.getAttributeValue(attr).toString());
					} catch (Exception e) {
						ExceptionHandler.consume(e);
					}
					rule.setAmount(rule.getAmount().setScale(scale, UFDouble.ROUND_HALF_UP));
				}
			}
			if (keys.contains(key.toString())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000501")/*
																															 * @
																															 * res
																															 * "规则设置中包含各个维度全部相同的记录，保存失败!"
																															 */);
			} else {
				keys.add(key.toString());
			}
			// 赋值，费用类型需要特殊处理
			if (rule.getPk_expensetype() != null && getExpenseMap().get(rule.getPk_expensetype()) == null)
				rule.setPk_expensetype(getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
			rule.setPriority(Integer.valueOf(i++));
			rule.setPk_billtype(pk_billtype);
			rule.setPk_group(pk_group);
			rule.setPk_org(pk_org);
		}
	}

	public static void addReimRules(ReimRulerVO[] reimRuleVOs) {
		// 逐条标准进行检查
		for (ReimRulerVO rule : reimRuleVOs) {
			if (rule.getPk_expensetype() != null)
				rule.setPk_expensetype(getExpenseNameMap().get(rule.getPk_expensetype()).getPrimaryKey());
		}
	}

	public static int checkReimDims(ReimRuleDimVO[] reimDimVOs, String pk_billtype, String pk_group, String pk_org) throws BusinessException {
		List<ReimRuleDimVO> returnVOs = new ArrayList<ReimRuleDimVO>();
		List<String> keys = new ArrayList<String>();
		int currow = 0;
		int controlflags = 0;
		for (ReimRuleDimVO dim : reimDimVOs) {
			currow++;
			if (dim.getDisplayname() == null && dim.getDatatype() == null && dim.getReferential() == null && dim.getBillref() == null && dim.getShowflag().equals(UFBoolean.FALSE)
					&& dim.getControlflag().equals(UFBoolean.FALSE)) {
				continue;
			}
			String displayname = dim.getDisplayname();
			if (displayname == null) {
				throw new BusinessException("维度设置必须填写显示名称(第" + currow + "行)，保存失败!");
			} else {
				if (keys.contains(displayname)) {
					throw new BusinessException("维度设置中包含内容相同的记录，保存失败!");
				} else {
					keys.add(displayname);
				}
			}
			if (dim.getDatatype() == null) {
				throw new BusinessException("维度设置必须填写数据类型(第" + currow + "行)，保存失败!");
			}
			if (dim.getControlflag().booleanValue())
				controlflags++;
			if (controlflags > 1) {
				throw new BusinessException("核心控制项最多只能有一项！");
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
	 * 控制状态页面，因为只有在浏览态控制按钮才可用，所以不需要修改右侧界面，只需要简单配置
	 */
	public static void showControlPage(BillCardPanel cardPanel, BillData billData, List<SuperVO> reimruledim, String centControlItem, String pk_org, String djlx) {
		if (centControlItem == null) {
			MessageDialog.showHintDlg(cardPanel, "错误", "未设置核心控制项，请在配置界面中进行选择！");
			cardPanel.setVisible(false);
		}
		// 将需要显示的列加入items中
		BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
		if (bodyItems == null)
			return;
		List<BillItem> items = new ArrayList<BillItem>();
		for (BillItem item : bodyItems) {
			if (item.getKey().equals(ReimRulerVO.SHOWITEM) || item.getKey().equals(ReimRulerVO.CONTROLITEM) || item.getKey().equals(ReimRulerVO.CONTROLFLAG)
					|| item.getKey().equalsIgnoreCase(centControlItem) || item.getKey().equals(ReimRulerVO.CONTROLFORMULA)) {
				item.setShow(true);
				items.add(item);
			} else {
				item.setShow(false);
				items.add(item);
			}
		}
		// 设置对应参照的财务组织
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

		setTableCellEditor(cardPanel, djlx, centControlItem);
		List<SuperVO> vos = getDataMapRule().get(djlx);
		if (vos != null) {
			List<SuperVO> vos1 = new ArrayList<SuperVO>();
			StringBuilder cents = new StringBuilder();
			for (SuperVO vo : vos) {
				if (!cents.toString().contains((String) vo.getAttributeValue(centControlItem))) {
					vos1.add(vo);
					cents.append((String) vo.getAttributeValue(centControlItem));
				}
			}
			cardPanel.getBillData().setBodyValueVO(vos1.toArray(new SuperVO[] {}));
		}
		if (cardPanel.getBillModel() != null) {
			cardPanel.getBillModel().execLoadFormula();
			cardPanel.getBillModel().loadLoadRelationItemValue();
		}
	}

	/**
	 * 控制状态页面的编辑器
	 */
	private static void setTableCellEditor(BillCardPanel cardPanel, String djlx, String centControlItem) {
		if (djlx.startsWith("263")) {
			// 263表示单据对应项编辑器应该为借款单
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.SHOWITEM, new BillCellEditor(getJKBillRefPane()));
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLITEM, new BillCellEditor(getJKBillRefPane()));
			// 借款单公式编辑器
			MDFormulaRefPane jk_referPanlFormula = new MDFormulaRefPane(cardPanel, jk_beanid, null);
			jk_referPanlFormula.setAutoCheck(false);
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFORMULA, new BillCellEditor(jk_referPanlFormula));
		} else {
			// 264表示单据对应项编辑器应该为报销单
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.SHOWITEM, new BillCellEditor(getBXBillRefPane()));
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLITEM, new BillCellEditor(getBXBillRefPane()));
			// 报销单公式编辑器

			MDFormulaRefPane bx_referPanlFormula = new MDFormulaRefPane(cardPanel, bx_beanid, null);
			bx_referPanlFormula.setAutoCheck(false);

			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFORMULA, new BillCellEditor(bx_referPanlFormula));
		}
		// 控制模式编辑器 显示和控制两种
		// cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFLAG,
		// new BillCellEditor(getControlModelPane()));
		// 核心控制项不可编辑的编辑器
		cardPanel.getBodyPanel().setTableCellEditor(centControlItem.toLowerCase(), new CentCellEditor());
	}

	public static void showModifyPage(BillCardPanel cardPanel, BillData billData, List<SuperVO> reimruledim, String pk_org) {
		BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
		if (bodyItems == null)
			return;
		// 将需要显示的列加入items中
		List<BillItem> items = new ArrayList<BillItem>();
		for (BillItem item : bodyItems) {
			if (item.getKey().equals(ReimRulerVO.SHOWITEM) || item.getKey().equals(ReimRulerVO.CONTROLITEM) || item.getKey().equals(ReimRulerVO.SHOWITEM_NAME)
					|| item.getKey().equals(ReimRulerVO.CONTROLITEM_NAME) || item.getKey().equals(ReimRulerVO.CONTROLFORMULA) || item.getKey().equals(ReimRulerVO.CONTROLFLAG)) {
				item.setShow(false);
				items.add(item);
				continue;
			}
			boolean flag = false;
			for (SuperVO vo : reimruledim) {
				if (item.getKey().equalsIgnoreCase(((ReimRuleDimVO) vo).getCorrespondingitem())) {
					item.setShow(true);
					item.setNull(false);
					item.setName(((ReimRuleDimVO) vo).getDisplayname());
					item.setShowOrder(((ReimRuleDimVO) vo).getOrders());
					String datatypename = ((ReimRuleDimVO) vo).getDatatypename();
					// 费用类型不需要做处理
					if (!item.getKey().equals(ReimRulerVO.PK_EXPENSETYPE)) {
						// 修改item的reftype，修改时弹出相应对话框
						String reftype = ((ReimRuleDimVO) vo).getDatatype();
						if (datatypename != null)
							reftype += ":" + datatypename;
						initAttribByParseReftype(datatypename, item, reftype);
						// System.out.println("new:"+item.getDataType()+","+item.getRefType()+";"
						// +item.getMetaDataProperty().getDataType()+","+item.getMetaDataProperty().getRefType()+";"
						// +item.getMetaDataProperty().getRefBusinessEntity()+","+datatypename+";");
					}
					items.add(item);
					flag = true;
				}
			}
			if (flag == false) {
				item.setShow(false);
				items.add(item);
			}
		}
		// 设置对应参照的财务组织
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
	 * 解析类型设置
	 */
	private static void initAttribByParseReftype(String datatypename, BillItem item, String reftype) {

		if (reftype == null || reftype.trim().length() == 0) {
			return;
		}
		reftype = reftype.trim();

		String[] tokens = BillUtil.getStringTokensWithNullToken(reftype, ":");

		if (tokens.length > 0) {
			try {
				// 非原数据数据类型
				if (item.getMetaDataProperty() != null) {
					IMetaDataProperty mdp = MetaDataPropertyFactory.creatMetaDataUserDefPropertyByType(item.getMetaDataProperty(), tokens[0]);
					item.setMetaDataProperty(mdp);
				} else {
					int datatype = Integer.parseInt(tokens[0]);
					if (datatype <= IBillItem.USERDEFITEM)
						item.setDataType(datatype);
				}
				if (tokens.length > 1) {
					// 修改元数据的datatype,展示时使用
					if (datatypename != null && !datatypename.equals("2")) {
						tokens[1] += ",code=N";
					}
					item.setRefType(tokens[1]);
				} else {
					item.setRefType(null);
					return;
				}
			} catch (NumberFormatException e) {
				Logger.warn("数据类型设置错误：" + tokens[0]);
				item.setDataType(IBillItem.STRING);
				item.setRefType(null);
				return;
			}
			// catch (MetaDataException e) {
			// Logger.warn("数据类型设置错误：" + tokens[0]);
			// item.setDataType(IBillItem.STRING);
			// item.setRefType(null);
			// return;
			// }
		}
	}

	// 报销单对应项元数据编辑器
	public static MDPropertyRefPane getBXBillRefPane() {
		return bxBillRefPane;
	}

	// 借款单对应项元数据编辑器
	public static MDPropertyRefPane getJKBillRefPane() {
		return jkBillRefPane;
	}

	private static Map<String, SuperVO> expenseMap; // 费用类型缓存数据
	private static Map<String, SuperVO> expenseNameMap; // 费用类型缓存数据

	static synchronized Map<String, SuperVO> getExpenseMap() {
		if (expenseMap == null) {
			IArapCommonPrivate service = (IArapCommonPrivate) NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
			try {
				Collection<SuperVO> expenseType = service.getVOs(ExpenseTypeVO.class, "pk_group='" + BXUiUtil.getPK_group() + "'", false);
				expenseMap = VOUtils.changeCollectionToMap(expenseType);
				expenseNameMap = VOUtils.changeCollectionToMap(expenseType, getMultiFieldName("name"));
			} catch (BusinessException e) {
			}
		}
		return expenseMap;
	}

	static synchronized Map<String, SuperVO> getExpenseNameMap() {
		if (expenseNameMap == null) {
			IArapCommonPrivate service = (IArapCommonPrivate) NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
			try {
				Collection<SuperVO> expenseType = service.getVOs(ExpenseTypeVO.class, "pk_group='" + BXUiUtil.getPK_group() + "'", false);
				expenseMap = VOUtils.changeCollectionToMap(expenseType);
				expenseNameMap = VOUtils.changeCollectionToMap(expenseType, getMultiFieldName("name"));
			} catch (BusinessException e) {
			}
		}
		return expenseNameMap;
	}

	/**
	 * 获取多语字段名称（name2,name,name3）
	 * 
	 * @param namefield
	 * @return
	 */
	private static String getMultiFieldName(String namefield) {
		int intValue = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		if (intValue > 1) {
			namefield = namefield + intValue;
		}
		return namefield;
	}

	@SuppressWarnings("rawtypes")
	public static void afterEdit(BillCardPanel cardPanel, String djlx, BillEditEvent e) {
		Object value = cardPanel.getBodyValueAt(e.getRow(), e.getKey());
		if (value == null)
			return;
		if (e.getKey().equals(ReimRulerVO.PK_EXPENSETYPE)) {
			ExpenseTypeVO exvo = (ExpenseTypeVO) getExpenseMap().get(value);
			cardPanel.setBodyValueAt(exvo.getAttributeValue(getMultiFieldName("name")), e.getRow(), e.getKey() + "_name");
			cardPanel.getBillModel().execLoadFormulaByRow(e.getRow());
		} else
			cardPanel.setBodyValueAt(value.toString(), e.getRow(), e.getKey() + "_name");

		// 精度处理
		if (e.getKey().equals(ReimRulerVO.PK_CURRTYPE)) {
			String currentBodyTableCode = cardPanel.getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = cardPanel.getBillData().getBodyValueVOs(currentBodyTableCode, ReimRulerVO.class.getName());
			ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;

			int currencyPrecision = 0;// 币种精度

			for (ReimRulerVO vo : reimRuleVOs) {
				try {
					currencyPrecision = Currency.getCurrDigit((String) vo.getPk_currtype());
				} catch (Exception e1) {
					ExceptionHandler.consume(e1);
				}
				cardPanel.getBodyItem(ReimRulerVO.AMOUNT).setDecimalDigits(currencyPrecision);

				cardPanel.setBodyValueAt(vo.getAmount() == null ? UFDouble.ZERO_DBL : vo.getAmount().setScale(currencyPrecision, UFDouble.ROUND_HALF_UP), e.getRow(), ReimRulerVO.AMOUNT);

			}
		} else if (e.getKey().equals(ReimRulerVO.SHOWITEM)) {
			cardPanel.setBodyValueAt(null, e.getRow(), ReimRulerVO.SHOWITEM_NAME);
			Vector vec;
			if (djlx.startsWith("263"))
				vec = getJKBillRefPane().getRefModel().getSelectedData();
			else
				vec = getBXBillRefPane().getRefModel().getSelectedData();
			if (vec != null) {
				for (Object object : vec) {
					String fieldcode = ((Vector) object).get(0).toString();
					cardPanel.setBodyValueAt(fieldcode, e.getRow(), ReimRulerVO.SHOWITEM_NAME);
				}
			}
		} else if (e.getKey().equals(ReimRulerVO.CONTROLITEM)) {
			cardPanel.setBodyValueAt(null, e.getRow(), ReimRulerVO.CONTROLITEM_NAME);
			Vector vec;
			if (djlx.startsWith("263"))
				vec = getJKBillRefPane().getRefModel().getSelectedData();
			else
				vec = getBXBillRefPane().getRefModel().getSelectedData();
			if (vec != null) {
				for (Object object : vec) {
					String fieldcode = ((Vector) object).get(0).toString();
					cardPanel.setBodyValueAt(fieldcode, e.getRow(), ReimRulerVO.CONTROLITEM_NAME);
				}
			}
		}
	}
}
