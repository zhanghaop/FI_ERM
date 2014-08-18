package nc.ui.er.reimrule.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;

import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.reimtype.IReimTypeService;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.RefInfoHelper;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

public class DimensTable extends BatchBillTable implements IComponentWithActions {

	private static final long serialVersionUID = 5732479916714526380L;

	private int DEFNO = 20;
	private String DEF = "DEF";
	private String djlx = null;
	private List<Action> actions = null;
	// 参照选择下拉框
	UIComboBox dtRefPane = null;
	private BDOrgPanel orgPanel = null;
	// 数据类型参照map
	private Map<String, RefInfoVO[]> map = new HashMap<String, RefInfoVO[]>();

	// 报销单元数据选择对话框
	private MDPropertyRefPane bxBillRefPane = null;
	/* 报销单元数据ID */
	private static String bx_beanid = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";

	// 借款单元数据选择对话框
	private MDPropertyRefPane jkBillRefPane = null;
	/* 借款单元数据ID */
	private static String jk_beanid = "e0499b58-c604-48a6-825b-9a7e4d6dacca";

	// 报销单对应项元数据编辑器
	public MDPropertyRefPane getBXBillRefPane() {
		if (bxBillRefPane == null) {
			bxBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0011")/*
																																	 * @
																																	 * res
																																	 * "来源单据字段"
																																	 */, bx_beanid, null);
		}
		return bxBillRefPane;
	}

	// 借款单对应项元数据编辑器
	public MDPropertyRefPane getJKBillRefPane() {
		if (jkBillRefPane == null) {
			jkBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0011")/*
																																	 * @
																																	 * res
																																	 * "来源单据字段"
																																	 */, jk_beanid, null);
		}

		return jkBillRefPane;
	}

	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (event.getType() == AppEventConst.MODEL_INITIALIZED) {
			// 根据当前的单据类型设置单据对应项应该参照报销单还是借款单
			djlx = getOrgPanel().getRefPane().getUITextField().getValue().toString();
			// 单据对应项编辑器
			if (djlx == null)
				return;
			if (djlx.startsWith("263"))
				getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.BILLREF, new BillCellEditor(getJKBillRefPane()));
			else
				getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.BILLREF, new BillCellEditor(getBXBillRefPane()));
		}
	}

	public void initUI() {
		super.initUI();
		// 参照选择下拉框编辑器
		getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.REFERENTIAL, new BillCellEditor(getDtRefPane()));
	}

	public ReimRuleDimVO[] checkReimDims(ReimRuleDimVO[] reimDimVOs, String pk_billtype, String pk_group, String pk_org) throws BusinessException {
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
		if (controlflags == 0) {
			throw new BusinessException("核心控制项必须选择一项，请选择！");
		}
		return returnVOs.toArray(new ReimRuleDimVO[0]);
	}

	public List<SuperVO> Save() throws Exception {
		super.beforeSave();
		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		ReimRuleDimVO[] reimDimVOs = (ReimRuleDimVO[]) getBillCardPanel().getBillData().getBodyValueVOs(currentBodyTableCode, ReimRuleDimVO.class.getName());

		String[] str = djlx.split(";");
		// 检查维度值，并加入单据类型、组织与对应项
		ReimRuleDimVO[] returnvo = checkReimDims(reimDimVOs, str[0], getModel().getContext().getPk_group(), str[1]);
		// 保存
		List<ReimRuleDimVO> returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).saveReimDim(str[0], getModel().getContext().getPk_group(), str[1], returnvo);
		List<SuperVO> list = new ArrayList<SuperVO>();
		list.addAll(returnVos);
		ReimRuleUtil.putDim(str[0], list);
		return list;
	}

	// 参照选择下拉框编辑器
	private UIComboBox getDtRefPane() {
		if (dtRefPane == null) {
			dtRefPane = new UIComboBox();
			dtRefPane.setPreferredSize(new Dimension(150, 22));
		}
		return dtRefPane;
	}

	public void addRef(BillEditEvent e) {
		Object value = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
		if (!nc.vo.arap.utils.StringUtil.isNullWithTrim((String) value))
			return;
		/**
		 * 预制七个默认的标准，当填入的显示名称是这些值时，对应单据模板上的这一项
		 */
		String[] names = new String[] { "费用类型", "报销类型", "部门", "职位", "币种", "金额", "备注" };
		String[] items = new String[] { "PK_EXPENSETYPE", "PK_REIMTYPE", "PK_DEPTID", "PK_POSITION", "PK_CURRTYPE", "AMOUNT", "MEMO" };
		// 判断界面上的显示名称是否在以上预制范围内，是则将于单据模板的对应项关联起来
		String displayname = (String) e.getValue();
		boolean flag = false;
		for (int j = 0; j < names.length && flag == false; j++) {
			if (displayname.equals(names[j])) {
				getBillCardPanel().setBodyValueAt(items[j], e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
				flag = true;
			}
		}
		// 如果不在预制范围内，则需要全表来判断应该对应哪个自定义项
		if (flag == false) {
			String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel().getBillData().getBodyValueVOs(currentBodyTableCode, ReimRuleDimVO.class.getName());
			ReimRuleDimVO[] reimDimVOs = (ReimRuleDimVO[]) bodyValueVOs;
			List<String> list = new ArrayList<String>();
			for (ReimRuleDimVO rule : reimDimVOs) {
				list.add(rule.getCorrespondingitem());
			}
			for (int i = 0; i < DEFNO && flag == false; i++) {
				String tmp = DEF + (i + 1);
				if (!list.contains(tmp)) {
					getBillCardPanel().setBodyValueAt(tmp, e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
					flag = true;
				}
			}
		}
		// 如果也不在自定义列中，则出错
		if (flag == false) {
			MessageDialog.showHintDlg(null, "列的数量超出", "自定义列的数量上限为20!");
		}
	}

	private void fillDiaplayName(BillEditEvent e) {
		String entityid = null;
		if (e.getSource() != null && (e.getSource() instanceof BillCellEditor)) {
			java.awt.Component component = ((BillCellEditor) e.getSource()).getComponent();
			if (component != null && (component instanceof UIRefPane))
				entityid = ((UIRefPane) component).getRefPK();
		}
		if (entityid == null || entityid.trim().length() == 0)
			return;
		IBean bean = null;
		try {
			bean = MDBaseQueryFacade.getInstance().getBeanByID(entityid);
		} catch (MetaDataException e1) {
			// 参照选择初始化失败，不做处理，只记录日志
			MessageDialog.showHintDlg(null, "数据类型错", "数据类型 错误!");
			Logger.error(e1.getMessage(), e1);
		}
		Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
		if (name == null && bean != null) {
			getBillCardPanel().setBodyValueAt(bean.getDisplayName(), e.getRow(), ReimRuleDimVO.DISPLAYNAME);
			addRef(e);
		}
		// Object value = getBillCardPanel().getBodyItem(e.getTableCode(),
		// e.getKey()).getValueObject();
		// setDataStyle(e.getRow(),entityid);
		getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.DATATYPENAME);
		getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.BEANNAME);
		if (entityid.equals("BS000010000100001031") || entityid.equals("BS000010000100001052"))
			getBillCardPanel().setBodyValueAt(2, e.getRow(), ReimRuleDimVO.DATATYPENAME);
		if (bean != null) {
			String beanName = bean.getName();
			getBillCardPanel().setBodyValueAt(bean.getDisplayName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
			getBillCardPanel().setBodyValueAt(beanName, e.getRow(), ReimRuleDimVO.BEANNAME);
			RefInfoVO[] infoVOs = map.get(beanName);
			if (infoVOs == null || infoVOs.length == 0) {
				infoVOs = RefInfoHelper.getInstance().getRefinfoVOs(beanName);
				if (infoVOs != null && infoVOs.length > 0) {
					map.put(beanName, infoVOs);
				}
			}
		}
	}

	private void initDtInfoData(BillEditEvent e) {
		// 清空原有值
		getDtRefPane().removeAllItems();
		Object beanname = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BEANNAME);
		if (beanname == null)
			return;
		String value = beanname.toString();
		// Object value = getBillCardPanel().getBodyItem(e.getTableCode(),
		// e.getKey()).getValueObject();
		RefInfoVO[] infoVOs = map.get(value);
		if (infoVOs == null || infoVOs.length == 0) {
			infoVOs = RefInfoHelper.getInstance().getRefinfoVOs(value);
			if (infoVOs != null && infoVOs.length > 0) {
				map.put(value, infoVOs);
			}
		}
		if (infoVOs != null && infoVOs.length > 0) {
			getDtRefPane().addItems(infoVOs);
		}
	}

	public boolean beforeEdit(BillEditEvent e) {
		Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
		if (name != null && (name.toString().equals("币种") || name.toString().equals("金额"))) {
			MessageDialog.showHintDlg(null, "不允许更改", "币种与金额行不允许更改!");
		}
		// 参照选择列
		if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			initDtInfoData(e);
			return true;
		} else if (e.getKey().equals(ReimRuleDimVO.BILLREF)) {
			MDPropertyRefPane mdref = null;
			Vector<Object> vecSelectedData = new Vector<Object>();
			mdref = djlx.startsWith("263") ? getJKBillRefPane() : getBXBillRefPane();
			String showCode = null;
			String showName = null;
			if (getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREFCODE) != null)
				showCode = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREFCODE).toString();
			if (getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREF) != null)
				showName = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREF).toString();
			if (mdref != null) {
				if (showCode != null && showName != null) {
					String[] codeArray = showCode.split(",");
					String[] nameArray = showName.split(",");
					for (int nPos = 0; nPos < codeArray.length; nPos++) {
						Vector<String> row = new Vector<String>();
						row.add(codeArray[nPos]);
						row.add(nameArray[nPos]);
						vecSelectedData.add(row);
					}
				}
				mdref.getRefModel().setSelectedData(vecSelectedData);
				mdref.getDialog().getEntityTree().setSelectionPath(null);
			}
		}
		return true;
	}

	/**
	 * 实现编辑后的逻辑
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	protected void doAfterEdit(BillEditEvent e) {
		super.doAfterEdit(e);
		Object value = getBillCardPanel().getBodyValueAt(e.getRow(), e.getKey());
		// 显示名称列，编辑完后需要写入对应项
		if (e.getKey().equals(ReimRuleDimVO.DISPLAYNAME)) {
			if (value == null)
				return;
			addRef(e);
		}
		// 数据类型列，编辑完后需要保存数据类型的值到DATATYPENAME中
		else if (e.getKey().equals(ReimRuleDimVO.DATATYPE)) {
			fillDiaplayName(e);
		}
		// 参照选择列
		else if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			if (value == null)
				return;
			getBillCardPanel().setBodyValueAt(((RefInfoVO) value).getName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
		}
		// 若编辑列为单据对应项列，编辑完后需要写入单据对应项编码，以便报销标准作用到具体的单据项
		else if (e.getKey().equals(ReimRuleDimVO.BILLREF)) {
			Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
			if (name.toString().equals("币种")) {
				getBillCardPanel().setBodyValueAt("币种", e.getRow(), ReimRuleDimVO.BILLREF);
				getBillCardPanel().setBodyValueAt("bzbm", e.getRow(), ReimRuleDimVO.BILLREFCODE);
			} else {
				getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.BILLREFCODE);
				Vector vec;
				if (djlx.startsWith("263"))
					vec = getJKBillRefPane().getRefModel().getSelectedData();
				else
					vec = getBXBillRefPane().getRefModel().getSelectedData();
				if (vec != null) {
					for (Object object : vec) {
						String fieldcode = ((Vector) object).get(0).toString();
						getBillCardPanel().setBodyValueAt(fieldcode, e.getRow(), ReimRuleDimVO.BILLREFCODE);
					}
				}
			}
		}
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public BDOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(BDOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
	}
}