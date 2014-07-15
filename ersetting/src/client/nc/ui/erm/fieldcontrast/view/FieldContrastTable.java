package nc.ui.erm.fieldcontrast.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.erm.fieldcontrast.IFieldContrastQryService;
import nc.itf.org.IOrgConst;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.IComponent;
import nc.md.model.context.MDNode;
import nc.md.util.MDUtil;
import nc.ui.md.MDTreeNode;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * @author luolch
 * 
 */
public class FieldContrastTable extends BatchBillTable {
	private static final String SRC_FIELD = "src_field";
	private static final String DES_FIELD = "des_field";
	private static final long serialVersionUID = 1L;
	private MDPropertyRefPane desRefPane;
	private MDPropertyRefPane desShareRefPane;
	private String srcEntityid;
	private String desBillType;
	private int itemState = FieldBilRefPanel.SHARESTATE;
	private String CSChildPath = "costsharedetail";
	private static final String CSRule_BeanID = "8c16817a-0d13-49ef-930c-fb3a7f932cd8"; /*费用分摊规则元数据ID*/
	private static final String MA_BeanID = "e3167d31-9694-4ea1-873f-2ffafd8fbed8"; /*费用申请单元数据ID*/
	private Map<String, MDPropertyRefPane> srcRefPaneMap = new HashMap<String, MDPropertyRefPane>();
	
	FieldBilRefPanel billRefPanel = null;

	@Override
	public void initUI() {
		super.initUI();
		getBillCardPanel().getBodyPanel().setTableCellEditor(DES_FIELD,
				new BillCellEditor(FieldBilRefPanel.SHARESTATE == itemState ? getDesShareRefPane() : getCtrlDesRefPane()));
		
		setShowField(itemState);
		initBillRefp();
	
	}

	private void initBillRefp() {
		UIRefPane uiRef = (UIRefPane) getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPEPK).getComponent();
		String where = " ncbrcode ='ma' ";
		uiRef.setWhereString(where);
	}

	public MDPropertyRefPane getSrcRefPane() {
		if (srcRefPaneMap.get(srcEntityid + itemState) == null) {
			MDPropertyRefPane srcRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"201100_0", "0201100-0011")/* @res "来源单据字段" */, srcEntityid);
			MDTreeNode rootNode = (MDTreeNode) srcRefPane.getDialog().getEntityTree().getModel().getRoot();
			DefaultTreeModel model = (DefaultTreeModel) srcRefPane.getDialog().getEntityTree().getModel();
			int count = rootNode.getMDNode().getChildNodes().size();
			List<MDNode> childNodeList = rootNode.getMDNode().getChildNodes();
			for (int index = 0; index < count; index++) {
                String path = childNodeList.get(index).getAttributePath();
                if (path.equals(CSChildPath) || path.equals("csharedetail")) {
                    if (itemState == FieldBilRefPanel.CTRLSTATE) {// 分摊场景
                        DefaultMutableTreeNode childnode = (DefaultMutableTreeNode) rootNode
                                .getChildAt(index);
                        model.removeNodeFromParent(childnode);
                    } else if (itemState == FieldBilRefPanel.SHARESTATE) {// 控制维度场景
                        DefaultMutableTreeNode childnode = (DefaultMutableTreeNode) rootNode
                                .getChildAt(index);
                        ((MDTreeNode) model.getRoot()).removeAllChildren();
                        ((MDTreeNode) model.getRoot()).add(childnode);
                        model.setRoot((MDTreeNode) model.getRoot());
                    }
                }
			}
			srcRefPaneMap.put(srcEntityid + itemState, srcRefPane);
		}
		return srcRefPaneMap.get(srcEntityid + itemState);
	}

	private MDPropertyRefPane getCtrlDesRefPane() {
		if (desRefPane == null) {
			desRefPane = new MDPropertyRefPane(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0012")/*
																									 * @res
																									 * "目标单据字段"
																									 */,
																									 MA_BeanID);
		}
		return desRefPane;
	}

	// 分摊对象
	private MDPropertyRefPane getDesShareRefPane() {
		if (desShareRefPane == null) {
			desShareRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0",
					"0201100-0013")/* @res "分摊单据字段" */, CSRule_BeanID);
		}
		return desShareRefPane;
	}

	public void setValue(Object object) {
		super.setValue(object);
		if(getItemState() != FieldBilRefPanel.BUDGETSTATE){
			for (int i = 0; i < getBillCardPanel().getBillTable().getRowCount(); i++) {
				// 根据billtype+fieldcode取元数据中的多语名称显示。
				String srcbilltype = (String) getBillCardPanel().getBodyValueAt(i, FieldcontrastVO.SRC_BILLTYPE);
				String srcfieldcode = (String) getBillCardPanel().getBodyValueAt(i, FieldcontrastVO.SRC_FIELDCODE);
				String srcVal = MDUtil.ConvertPathToDisplayName(getEntitybean(srcbilltype), srcfieldcode);
				String desbilltype = (String) getBillCardPanel().getBodyValueAt(i, FieldcontrastVO.DES_BILLTYPE);
				String desfieldcode = (String) getBillCardPanel().getBodyValueAt(i, FieldcontrastVO.DES_FIELDCODE);
				String desVal = MDUtil.ConvertPathToDisplayName(getEntitybean(desbilltype), desfieldcode);
				getBillCardPanel().setBodyValueAt(srcVal, i, SRC_FIELD);
				getBillCardPanel().setBodyValueAt(desVal, i, DES_FIELD);
				if (FieldBilRefPanel.SHARESTATE == itemState) {
					getBillCardPanel().setBodyValueAt("~", i, FieldcontrastVO.DES_BILLTYPE);
				}
			}
		}
	}

	/**
	 * 实现编辑后的逻辑
	 * 
	 * @param e
	 */
	protected void doAfterEdit(BillEditEvent e) {
		BillCardPanel cardPanel = getBillCardPanel();
		BillItem bodyItem = (BillItem) cardPanel.getBodyItem(e.getTableCode(), e.getKey());
		if (bodyItem == null) {
			return;
		}
		if (bodyItem.getKey().equals(SRC_FIELD)) {
			Map<String, String> map = srcRefPaneMap.get(srcEntityid + itemState).getDialog().getSelecteddatas();
			setBodySrc(e, cardPanel, map);
		} else if (bodyItem.getKey().equals(DES_FIELD)) {
			MDPropertyRefPane mdref = FieldBilRefPanel.CTRLSTATE == itemState ? getCtrlDesRefPane() : getDesShareRefPane();
			Map<String, String> map = mdref.getDialog().getSelecteddatas();
			setBodyDes(e, cardPanel, map);
		} else if (e.getKey().equals(FieldcontrastVO.DES_BILLTYPEPK)) {
			cardPanel
					.setBodyValueAt(((UIRefPane) bodyItem.getComponent()).getRefCode(), e.getRow(), FieldcontrastVO.DES_BILLTYPE);
		}
		super.doAfterEdit(e);

	}

	private void setBodyDes(BillEditEvent e, BillCardPanel cardPanel, Map<String, String> map) {
		String fieldcode = null;
		String fieldname = null;
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				fieldcode = entry.getKey();
				fieldname = entry.getValue();
				cardPanel.setBodyValueAt(fieldname, e.getRow(), FieldcontrastVO.DES_FIELDNAME);
				cardPanel.setBodyValueAt(fieldcode, e.getRow(), FieldcontrastVO.DES_FIELDCODE);
			}
		}
	}

	private void setBodySrc(BillEditEvent e, BillCardPanel cardPanel, Map<String, String> map) {
		String fieldcode;
		String fieldname;
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				fieldcode = entry.getKey();
				fieldname = entry.getValue();
				cardPanel.setBodyValueAt(fieldname, e.getRow(), FieldcontrastVO.SRC_FIELDNAME);
				cardPanel.setBodyValueAt(fieldcode, e.getRow(), FieldcontrastVO.SRC_FIELDCODE);
				cardPanel.setBodyValueAt(getSrcBillType(), e.getRow(), FieldcontrastVO.SRC_BILLTYPE);
			}
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (event.getType() == FieldBilRefPanel.CTRLCHANGED) {
			ctrlValueChanged(event);
		} else if (event.getType() == FieldBilRefPanel.ITEMCHANGED) {
			itemChanged(event);
		} else {
			super.handleEvent(event);
		}
	}

	private void itemChanged(AppEvent event) {
		getBillRefPanel().getDjlxRef().getRefModel().clearData();
		getBillRefPanel().getDjlxRef().getUITextField().setValue(null);

		Integer contextObject = (Integer) event.getContextObject();
		setShowField(contextObject);
		initModelData();
	}

	private void setShowField(Integer itemState) {
		if(itemState == 1){
			getBillCardPanel().getBodyItem(FieldcontrastVO.DES_FIELDCODE).setShow(true);
			getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_FIELDCODE).setShow(true);
			getBillCardPanel().getBodyItem(SRC_FIELD).setShow(false);
			getBillCardPanel().getBodyItem(DES_FIELD).setShow(false);
			getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setShow(false);
			getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setNull(false);
			getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BILLTYPE).setNull(false);
			getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPE).setNull(false);
			getBillCardPanel().initPanelByPos(IBillItem.BODY);
			return;
		}
		
		getBillCardPanel().getBodyItem(SRC_FIELD).setShow(true);
		getBillCardPanel().getBodyItem(DES_FIELD).setShow(true);
		getBillCardPanel().getBodyItem(FieldcontrastVO.DES_FIELDCODE).setShow(false);
		getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_FIELDCODE).setShow(false);
		if (FieldBilRefPanel.CTRLSTATE == itemState) {
			itemState = FieldBilRefPanel.CTRLSTATE;
			// 设置显示/隐藏字段
			getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPEPK).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0014")/*
																									 * @res
																									 * "费用申请单交易类型"
																									 */);
			getBillCardPanel().getBodyItem(DES_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0015")/*
																									 * @res
																									 * "费用申请单字段名称"
																									 */);
			getBillCardPanel().getBodyItem(SRC_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0016")/*
																									 * @res
																									 * "控制对象字段名称"
																									 */);
			// 页签隐藏
		} else if (FieldBilRefPanel.SHARESTATE == itemState) {
			itemState = FieldBilRefPanel.SHARESTATE;
			getBillCardPanel().getBodyItem(DES_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0009")/*
																									 * @res
																									 * "分摊对象"
																									 */);
			getBillCardPanel().getBodyItem(SRC_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0017")/*
																									 * @res
																									 * "单据对象字段"
																									 */);
			getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0018")/*
																									 * @res
																									 * "页签"
																									 */);

		}

		// 设置显示/隐藏字段
		boolean isShow = (FieldBilRefPanel.CTRLSTATE == itemState);
		getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setShow(!isShow);
		getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setNull(!isShow);
		getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPE).setNull(isShow);
		getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPEPK).setShow(isShow);
		getBillCardPanel().initPanelByPos(IBillItem.BODY);

		// 重置渲染器
		getBillCardPanel().getBodyPanel().setTableCellEditor(DES_FIELD,
				new BillCellEditor(FieldBilRefPanel.CTRLSTATE == itemState ? getCtrlDesRefPane() : getDesShareRefPane()));
		if (getSrcBillType() != null) {
			getBillCardPanel().getBodyPanel().setTableCellEditor(SRC_FIELD, new BillCellEditor(getSrcRefPane()));
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		MDPropertyRefPane mdref = null;
		Vector<Object> vecSelectedData = new Vector<Object>();
		String showCode = null;
		String showName = null;
		if (e.getKey().equals(SRC_FIELD)) {
			showCode = ((FieldcontrastVO) getModel().getSelectedData()).getSrc_fieldcode();
			showName = ((FieldcontrastVO) getModel().getSelectedData()).getSrc_fieldname();
			mdref = getSrcRefPane();
		} else if (e.getKey().equals(DES_FIELD)) {
			showCode = ((FieldcontrastVO) getModel().getSelectedData()).getDes_fieldcode();
			showName = ((FieldcontrastVO) getModel().getSelectedData()).getDes_fieldname();
			mdref = FieldBilRefPanel.CTRLSTATE == itemState ? getCtrlDesRefPane() : getDesShareRefPane();
		}
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
		
		if(getItemState() == FieldBilRefPanel.BUDGETSTATE){
			String pk = ((FieldcontrastVO) getModel().getSelectedData()).getPk_fieldcontrast();
			if(pk != null && pk.startsWith("contZ3")){
				return false;
			}
			
			this.getBillCardPanel().setBodyValueAt("~", e.getRow(), FieldcontrastVO.DES_BILLTYPE);
		}
		
		return super.beforeEdit(e);
	}
	
	private void ctrlValueChanged(AppEvent event) {
		int selectedIndex = billRefPanel.getBcombobox().getSelectedIndex();
		if(selectedIndex == FieldBilRefPanel.SHARESTATE){
			if (event.getContextObject() != null) {
				try {
					String billcomponent = PfDataCache.getBillType(getSrcBillType()).getComponent();
					IComponent icom = MDBaseQueryFacade.getInstance().getComponentByName(billcomponent);
					// 重置
					srcEntityid = icom.getPrimaryBusinessEntity().getID();
					getBillCardPanel().getBodyPanel().setTableCellEditor(SRC_FIELD, new BillCellEditor(getSrcRefPane()));
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			} 
		}
		
		initModelData();
	}

	public void initModelData() {
		FieldcontrastVO[] rs = null;
		if (getSrcBillType() != null) {
			IFieldContrastQryService qrySer = NCLocator.getInstance().lookup(IFieldContrastQryService.class);
			Integer app_scene = FieldBilRefPanel.CTRLSTATE == itemState ? ErmBillFieldContrastCache.FieldContrast_SCENE_MatterAppCtrlField
					: ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField;
			String pk_org = getModel().getContext().getPk_org();
			
			if(getItemState() == FieldBilRefPanel.BUDGETSTATE){
				app_scene = ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField;
				pk_org = IOrgConst.GLOBEORG;
			}
			
			try {
				rs = qrySer.qryVOs(pk_org, app_scene, getSrcBillType());
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}
		getModel().initModel(rs);
	}
	
	private IBean getEntitybean(String billtype) {
		IBean entitybean = null;
		String entityid = null;
		try {
			if (billtype == null || billtype.equals(ErmConst.NULL_VALUE)) {
				entityid = CSRule_BeanID; /* 费用分摊规则元数据ID */
			} else {
				String billcomponent = PfDataCache.getBillType(billtype).getComponent();
				IComponent icom = MDBaseQueryFacade.getInstance().getComponentByName(billcomponent);
				entityid = icom.getPrimaryBusinessEntity().getID();
			}
			entitybean = MDBaseQueryFacade.getInstance().getBeanByID(entityid);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return entitybean;
	}

	public String getDesBillType() {
		return desBillType;
	}

	public void setDesBillType(String desBillType) {
		this.desBillType = desBillType;
	}

	public String getSrcBillType() {
		String srcBillType = (String)billRefPanel.getDjlxRef().getRefModel().getValue("pk_billtypecode");
		return srcBillType;
	}
	
	public int getItemState() {
		Integer srcBillType = (Integer)billRefPanel.getBcombobox().getSelectedIndex();
		return srcBillType;
	}

	public FieldBilRefPanel getBillRefPanel() {
		return billRefPanel;
	}

	public void setBillRefPanel(FieldBilRefPanel billRefPanel) {
		this.billRefPanel = billRefPanel;
	}
}