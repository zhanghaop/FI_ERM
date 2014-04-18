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
	private String srcBillType;
	private String desBillType;
	private int itemState = FieldBilRefPanel.SHARESTATE;
	private String CSChildPath = "costsharedetail";
	private static final String CSRule_BeanID = "8c16817a-0d13-49ef-930c-fb3a7f932cd8"; /*���÷�̯����Ԫ����ID*/
	private static final String MA_BeanID = "e3167d31-9694-4ea1-873f-2ffafd8fbed8"; /*�������뵥Ԫ����ID*/
	private Map<String, MDPropertyRefPane> srcRefPaneMap = new HashMap<String, MDPropertyRefPane>();

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
					"201100_0", "0201100-0011")/* @res "��Դ�����ֶ�" */, srcEntityid);
			MDTreeNode rootNode = (MDTreeNode) srcRefPane.getDialog().getEntityTree().getModel().getRoot();
			DefaultTreeModel model = (DefaultTreeModel) srcRefPane.getDialog().getEntityTree().getModel();
			int count = rootNode.getMDNode().getChildNodes().size();
			List<MDNode> childNodeList = rootNode.getMDNode().getChildNodes();
			for (int index = 0; index < count; index++) {
                String path = childNodeList.get(index).getAttributePath();
                if (path.equals(CSChildPath) || path.equals("csharedetail")) {
                    if (itemState == FieldBilRefPanel.CTRLSTATE) {// ��̯����
                        DefaultMutableTreeNode childnode = (DefaultMutableTreeNode) rootNode
                                .getChildAt(index);
                        model.removeNodeFromParent(childnode);
                    } else if (itemState == FieldBilRefPanel.SHARESTATE) {// ����ά�ȳ���
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
																									 * "Ŀ�굥���ֶ�"
																									 */,
																									 MA_BeanID);
		}
		return desRefPane;
	}

	// ��̯����
	private MDPropertyRefPane getDesShareRefPane() {
		if (desShareRefPane == null) {
			desShareRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0",
					"0201100-0013")/* @res "��̯�����ֶ�" */, CSRule_BeanID);
		}
		return desShareRefPane;
	}

	public void setValue(Object object) {
		super.setValue(object);
		for (int i = 0; i < getBillCardPanel().getBillTable().getRowCount(); i++) {
			// ����billtype+fieldcodeȡԪ�����еĶ���������ʾ��
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

	/**
	 * ʵ�ֱ༭����߼�
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
				// cardPanel.setBodyValueAt(getDesBillType(), e.getRow(),
				// FieldcontrastVO.DES_BILLTYPE);
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
		Integer contextObject = (Integer) event.getContextObject();
		setShowField(contextObject);
		initModelData();
	}

	private void setShowField(Integer itemState) {
		if (FieldBilRefPanel.CTRLSTATE == itemState) {
			itemState = FieldBilRefPanel.CTRLSTATE;
			// ������ʾ/�����ֶ�
			getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPEPK).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0014")/*
																									 * @res
																									 * "�������뵥��������"
																									 */);
			getBillCardPanel().getBodyItem(DES_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0015")/*
																									 * @res
																									 * "�������뵥�ֶ�����"
																									 */);
			getBillCardPanel().getBodyItem(SRC_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0016")/*
																									 * @res
																									 * "���ƶ����ֶ�����"
																									 */);
			// ҳǩ����
		} else if (FieldBilRefPanel.SHARESTATE == itemState) {
			itemState = FieldBilRefPanel.SHARESTATE;
			getBillCardPanel().getBodyItem(DES_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0009")/*
																									 * @res
																									 * "��̯����"
																									 */);
			getBillCardPanel().getBodyItem(SRC_FIELD).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0017")/*
																									 * @res
																									 * "���ݶ����ֶ�"
																									 */);
			getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setName(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0018")/*
																									 * @res
																									 * "ҳǩ"
																									 */);

		}

		// ������ʾ/�����ֶ�
		boolean isShow = (FieldBilRefPanel.CTRLSTATE == itemState);
		getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setShow(!isShow);
		getBillCardPanel().getBodyItem(FieldcontrastVO.SRC_BUSITYPE).setNull(!isShow);
		getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPE).setNull(isShow);
		getBillCardPanel().getBodyItem(FieldcontrastVO.DES_BILLTYPEPK).setShow(isShow);
		getBillCardPanel().initPanelByPos(IBillItem.BODY);

		// ������Ⱦ��
		getBillCardPanel().getBodyPanel().setTableCellEditor(DES_FIELD,
				new BillCellEditor(FieldBilRefPanel.CTRLSTATE == itemState ? getCtrlDesRefPane() : getDesShareRefPane()));
		if (srcBillType != null) {
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
		return super.beforeEdit(e);
	}
	
	private void ctrlValueChanged(AppEvent event) {
		if (event.getContextObject() != null) {
			try {
				srcBillType = ((String[]) event.getContextObject())[0];
				String billcomponent = PfDataCache.getBillType(srcBillType).getComponent();
				IComponent icom = MDBaseQueryFacade.getInstance().getComponentByName(billcomponent);
				// ����
				srcEntityid = icom.getPrimaryBusinessEntity().getID();
				getBillCardPanel().getBodyPanel().setTableCellEditor(SRC_FIELD, new BillCellEditor(getSrcRefPane()));
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		} else {
			srcBillType = null;
		}
		initModelData();
	}

	public void initModelData() {
		FieldcontrastVO[] rs = null;
		if (srcBillType != null) {
			IFieldContrastQryService qrySer = NCLocator.getInstance().lookup(IFieldContrastQryService.class);
			Integer app_scene = FieldBilRefPanel.CTRLSTATE == itemState ? ErmBillFieldContrastCache.FieldContrast_SCENE_MatterAppCtrlField
					: ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField;
			String pk_org = getModel().getContext().getPk_org();
			try {
				rs = qrySer.qryVOs(pk_org, app_scene, srcBillType);
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
				entityid = CSRule_BeanID; /* ���÷�̯����Ԫ����ID */
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
		return srcBillType;
	}

	public void setSrcBillType(String srcBillType) {
		this.srcBillType = srcBillType;
	}

}