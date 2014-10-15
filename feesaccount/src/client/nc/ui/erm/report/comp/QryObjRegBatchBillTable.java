package nc.ui.erm.report.comp;

import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.erm.util.MDPropertyDialog;
import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.logging.Logger;
import nc.itf.erm.report.IErmReportConstants;
import nc.md.model.IAttribute;
import nc.md.model.context.MDNode;
import nc.md.model.type.impl.RefType;
import nc.ui.bd.ref.AbstractRefDialog;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefTreeComponent;
import nc.ui.bill.tools.typeseteditor.MDClassRefTreeModel;
import nc.ui.erm.report.model.ErmBatchBillTableModel;
import nc.ui.erm.report.model.ErmRefModel;
import nc.ui.md.MDTreeNode;
import nc.ui.pub.beans.IBeforeRefDlgShow;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.tree.SimpleFilterByText;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.itemeditors.UFRefBillItemEditor;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.fipub.report.QueryObjVO;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.bill.BillTempletVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class QryObjRegBatchBillTable extends BatchBillTable {

    private static final long serialVersionUID = -5793264365236601818L;

    @Override
    protected void processBillData(BillData billdata) {
        BillItem item = billdata.getBodyItem(FIELD_QRY_OBJDATATYPE);
        String name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "erm_report", "qryobj_name");/* @res "查询对象" */
        item.setName(name);
        if (item.getUiSet() == null) {
            item.setWidth(200);
        }
        item = billdata.getBodyItem(FIELD_DSP_OBJNAME);
        name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "erm_report", "qryobj_showname");/* @res "显示名称" */
        item.setName(name);
        if (item.getUiSet() == null) {
            item.setWidth(200);
        }

        item = billdata.getBodyItem(FIELD_BD_MDID);
        name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "erm_report", "report_type005");/* @res "类型" */
        item.setName(name);
        if (item.getUiSet() == null) {
            item.setWidth(200);
        }

        item = billdata.getBodyItem(FIELD_BD_REFNAME);
        name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "erm_report", "report_type006");/* @res "参照名称" */
        item.setName(name);
        item.setShowOrder(item.getShowOrder() + 2);
        if (item.getUiSet() == null) {
            item.setWidth(200);
        }

        UFRefBillItemEditor editor = new UFRefBillItemEditor(item);
        // editor.setComponent(new BillItemTypeAttributeSetRefPane(16, 2,
        // true));
        editor.setComponent(getRefNameRefPanel());
        item.setDataType(IBillItem.UFREF);
        item.setItemEditor(editor);

        item = billdata.getBodyItem(FIELD_BD_MDID);
        UIRefPane ref = getDataTypeRefPane();
        editor = new UFRefBillItemEditor(item);
        editor.setComponent(ref);
        item.setDataType(IBillItem.UFREF);
        item.setItemEditor(editor);

        item = billdata.getBodyItem(FIELD_DESCRIPTION);
        if (item.getUiSet() == null) {
            item.setWidth(200);
        }

        super.processBillData(billdata);
    }

    private UIRefPane dataTypeRefPane;

    public UIRefPane getDataTypeRefPane() {
        if (dataTypeRefPane == null) {
            dataTypeRefPane = new UIRefPane() {

                private static final long serialVersionUID = 1L;
                private boolean inited = false;
                @Override
                public void showModel() {
                    if (!inited) {
                        if (dataTypeRefPane.getRefUI() instanceof AbstractRefDialog) {
                            AbstractRefDialog refDlg = (AbstractRefDialog) dataTypeRefPane
                                    .getRefUI();
                            if (refDlg.getDataShowComponent() instanceof RefTreeComponent) {
                                refDlg.getRefUIConfig().setIBeforeRefDlgShow(
                                        new IBeforeRefDlgShow() {

                                            @Override
                                            public AbstractButton[] addButtons(UIDialog dlg) {
                                                if (dlg instanceof AbstractRefDialog) {
                                                    RefTreeComponent comp = (RefTreeComponent) ((AbstractRefDialog) dlg).getDataShowComponent();
                                                    comp.getTreeFilterHandler().setIFilterByText(new SimpleFilterByText() {

                                                                        @Override
                                                                        public DefaultMutableTreeNode cloneMatchedNode(
                                                                                DefaultMutableTreeNode matchedNode) {
                                                                            return (DefaultMutableTreeNode) matchedNode.clone();
                                                                        }

                                                                    });
                                                }
                                                return null;
                                            }

                                        });

                            }
                        }
                        inited = true;
                    }
                    super.showModel();
                }
                
            };
            MDClassRefTreeModel refModel = new MDClassRefTreeModel();
            dataTypeRefPane.setRefModel(refModel);
            
        }
        return dataTypeRefPane;
    }

    @Override
    protected void setBillData(BillTempletVO template) {
        super.setBillData(template);
        BillCardPanel cardPanel = getBillCardPanel();
        BillItem item = cardPanel.getBodyItem(FIELD_BD_MDID);
        getBillCardPanel().getBillTable().getColumn(item.getName()).setCellRenderer(
                        new ErmDataTypeCellRenderer(getDataTypeRefPane()));
        item = cardPanel.getBodyItem(FIELD_QRY_OBJDATATYPE);
        getBillCardPanel().getBillTable().getColumn(item.getName())
                .setCellRenderer(new ErmMDCellRender(this));
    }

    private UIRefPane refNameRefPanel;

    private UIRefPane getRefNameRefPanel() {
        if (refNameRefPanel == null) {
            refNameRefPanel = new UIRefPane();
            ErmRefModel refModel = new ErmRefModel();
            refModel.reset();
            refNameRefPanel.setRefModel(refModel);
        }
        return refNameRefPanel;
    }

    @Override
    public void handleEvent(AppEvent event) {
        ErmBatchBillTableModel model = (ErmBatchBillTableModel) getModel();
        if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
            mdRefPane = null;
            if (model.getReportType() != null) {
                beanId = model.getReportType().getBeanId();
                initBillItemEditor();
            }
        } else if (AppEventConst.DATA_INSERTED.equals(event.getType())) {
            RowOperationInfo row = (RowOperationInfo) event.getContextObject();
            QueryObjVO vo = (QueryObjVO) row.getRowDatas()[0];
            vo.setOwnmodule(IErmReportConstants.ERM_PRODUCT_CODE_Lower);
            vo.setQry_objtablename("zb");
            vo.setDsp_objtablename(model.getReportType().getTableName());
        }
        super.handleEvent(event);
    }

    private void initBillItemEditor() {
        TableColumn tc = getBillCardPanel().getBodyPanel().getShowCol(
                FIELD_QRY_OBJDATATYPE);
        BillCellEditor ed = new BillCellEditor(getMDRefPane());
        tc.setCellEditor(ed);
    }

    private MDPropertyRefPane mdRefPane;

    private String beanId;

    public MDPropertyRefPane getMDRefPane() {
        if (mdRefPane == null) {
            mdRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl
                    .getNCLangRes().getStrByID("erm_report", "qryobj_name")/* @res "对象名称" */,
                    beanId);
            mdRefPane.setEnabled(true);
            mdRefPane.setEditable(false);
            mdRefPane.setAutoCheck(true);
            MDPropertyDialog dialog = new MDPropertyDialog(this,
                    mdRefPane.getDialogName(), mdRefPane.getEntityid(), null);
            dialog.getEntityTree().getSelectionModel()
                    .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            mdRefPane.setDialog(dialog);
        }
        return mdRefPane;
    }

    @Override
    protected void doAfterEdit(BillEditEvent e) {
        super.doAfterEdit(e);
        BillCellEditor editor = (BillCellEditor) e.getSource();
        if (editor.getComponent() instanceof MDPropertyRefPane) {
            handleObjNameAfterEdit(editor, e);
        } else if (FIELD_BD_MDID.equals(e.getKey())) {
            UIRefPane refPane = (UIRefPane) editor.getComponent();
            if (e.getOldValue() == null || (e.getOldValue() != null && 
                    !e.getOldValue().equals(refPane.getRefPK()))) {
                getBillCardPanel().setBodyValueAt(null, e.getRow(),
                        FIELD_BD_REFNAME);
            }
            getBillCardPanel().setBodyValueAt(refPane.getRefPK(), e.getRow(),
                    FIELD_BD_MDID);
            
        } else if (FIELD_BD_REFNAME.equals(e.getKey())) {
            initDataByRefModel(e, (String) e.getValue());
        } else if (FIELD_DSP_OBJNAME.equals(e.getKey())) {
            getBillCardPanel().setBodyValueAt((String) e.getValue(),
                    e.getRow(), FIELD_RESID);
        }
    }

    private static final String FIELD_RESID = "resid";

    private static final String FIELD_BD_MDID = "bd_mdid";

    private static final String FIELD_BD_REFNAME = "bd_refname";

    private static final String FIELD_DSP_OBJNAME = "dsp_objname";

    private static final String FIELD_DESCRIPTION = "description";

    private static final String FIELD_TALLYFIELDNAME = "tallyfieldname";

    private static final String FIELD_QRY_OBJDATATYPE = "qry_objdatatype";

    private static final String FIELD_BD_CODEFIELD = "bd_codefield";

    private static final String FIELD_BD_NAMEFIELD = "bd_namefield";

    private static final String FIELD_BD_TABLENAME = "bd_tablename";

    private static final String FIELD_BD_PKFIELD = "bd_pkfield";

    private static final String FIELD_DSP_OBJFIELDNAME = "dsp_objfieldname";

    private static final String FIELD_QRY_OBJFIELDNAME = "qry_objfieldname";

    private void handleObjNameAfterEdit(BillCellEditor editor, BillEditEvent e) {

        MDPropertyRefPane refPane = (MDPropertyRefPane) editor.getComponent();
        String code = refPane.getRefShowCode();
        if (code != null && code.length() > 0) {
            String[] field = code.split("\\.");
            if (field.length == 1) {
                MDTreeNode treeNode = refPane.getDialog().locateNode();
                if (treeNode != null) {
                    code = treeNode.getMDNode().getOwnerBean().getTable().getName() + "." + code;
                }
            }
            getBillCardPanel().setBodyValueAt(code, e.getRow(), FIELD_TALLYFIELDNAME);
        }

        getBillCardPanel().setBodyValueAt(refPane.getRefShowName(), e.getRow(), FIELD_QRY_OBJDATATYPE);

        TreePath path = refPane.getDialog().getEntityTree().getSelectionPath();
        if (path != null) {
            MDTreeNode treeNode = (MDTreeNode) path.getLastPathComponent();
            MDNode node = (MDNode) treeNode.getUserObject();
            IAttribute att = node.getAttribute();
            if (StringUtils.isNotBlank(att.getRefModelName())) {
                if (att.getDataType() instanceof RefType) {
                    RefType refType = (RefType) att.getDataType();
                    String id = refType.getID().substring(0,
                            refType.getID().length() - 3);
                    getBillCardPanel().setBodyValueAt(id, e.getRow(), FIELD_BD_MDID);
                }
                initDataByRefModel(e, att.getRefModelName());
                getBillCardPanel().setBodyValueAt(att.getRefModelName(), e.getRow(), FIELD_BD_REFNAME);
                getBillCardPanel().setBodyValueAt(att.getResID(), e.getRow(), FIELD_RESID);
                getBillCardPanel().setBodyValueAt(att.getDisplayName(), e.getRow(), FIELD_DSP_OBJNAME);
            } else {
                clearVal(e);
            }
            getBillCardPanel().setBodyValueAt(att.getName(), e.getRow(), FIELD_DSP_OBJFIELDNAME);
            getBillCardPanel().setBodyValueAt(att.getName(), e.getRow(), FIELD_QRY_OBJFIELDNAME);
        }
    }

    private void clearVal(BillEditEvent e) {
        getBillCardPanel().setBodyValueAt(null, e.getRow(), FIELD_BD_MDID);
        getBillCardPanel().setBodyValueAt(null, e.getRow(), FIELD_BD_REFNAME);
        getBillCardPanel().setBodyValueAt(null, e.getRow(), FIELD_RESID);
        getBillCardPanel().setBodyValueAt(null, e.getRow(), FIELD_DSP_OBJNAME);
    }

    @SuppressWarnings("rawtypes")
    private void initDataByRefModel(BillEditEvent e, String refName) {

        ErmRefModel refModel = (ErmRefModel) getRefNameRefPanel().getRefModel();
        
        if (Logger.isDebugEnabled()) {
            Vector datas = refModel.getData();
            for (int nPos = 0; nPos < datas.size(); nPos++) {
                Vector row = (Vector)datas.get(nPos);
                String refClass = (String)row.get(refModel.getFieldIndex("refclass"));
                try {
                    if (StringUtils.isEmpty(refClass)) {
                        Logger.error("empty ref : " + row.toString());
                        continue;
                    }
                    Class clazz = Class.forName(refClass);
                    Object obj = clazz.newInstance();
                    if (obj instanceof AbstractRefModel) {
                        AbstractRefModel model = (AbstractRefModel) obj;
                        model.reset();
                        if (StringUtils.isEmpty(model.getRefCodeField())) {
                            Logger.error(refClass);
                        }
                    } else {
                        Logger.error("not ref : " + row.toString());
                    }
                    
                } catch (ClassNotFoundException e1) {
                } catch (InstantiationException e2) {
                } catch (IllegalAccessException e3) {
                } catch (Throwable e4) {
                    Logger.error(e4.getMessage(), e4);
                }
            }
        }
        
        Vector vec = refModel.matchData("name", refName);
        if (vec != null && !vec.isEmpty()) {
            String refClass = (String) ((Vector) vec.get(0)).get(refModel
                    .getFieldIndex("refclass"));
            if (!StringUtils.isEmpty(refClass)) {
                try {
                    Class clazz = Class.forName(refClass);
                    Object obj = clazz.newInstance();
                    if (obj instanceof AbstractRefModel) {
                        AbstractRefModel model = (AbstractRefModel)obj;
                        model.reset();
                        if (StringUtils.isEmpty(model.getRefCodeField())) {
                            model.setRefNodeName("name");
                        }
                        if (StringUtils.isEmpty(model.getRefCodeField())) {
                            String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                    "feesaccount_0", "02011001-0130", null, 
                                    new String[]{ refClass });/* @res "参照{0}错误，不能获取fieldcode字段名。" */
                            throw new BusinessRuntimeException(errMsg);
                        }
                        getBillCardPanel().setBodyValueAt(model.getRefCodeField(), e.getRow(), FIELD_BD_CODEFIELD);
                        getBillCardPanel().setBodyValueAt(model.getRefNameField(), e.getRow(), FIELD_BD_NAMEFIELD);
                        getBillCardPanel().setBodyValueAt(parseTable(model.getTableName()), e.getRow(), FIELD_BD_TABLENAME);
                        getBillCardPanel().setBodyValueAt(model.getPkFieldCode(), e.getRow(), FIELD_BD_PKFIELD);
                    } else {
                        String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                "feesaccount_0", "02011001-0131", null, 
                                new String[]{ refClass });/* @res "{0}不是标准参照，未继承AbstractRefModel。" */
                        throw new BusinessRuntimeException(errMsg);
                    }
                } catch (InstantiationException e1) {
                    Logger.error(e1.getMessage(), e1);
                } catch (IllegalAccessException e1) {
                    Logger.error(e1.getMessage(), e1);
                } catch (ClassNotFoundException e1) {
                    Logger.error(e1.getMessage(), e1);
                }
            } else {
                String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "feesaccount_0", "02011001-0129", null, 
                        new String[]{ vec.get(0).toString() });/* @res "参照表(bd_refinfo)配置错误，refclass字段不能为空：{0}！" */
                throw new BusinessRuntimeException(errMsg);
            }
        } else {
            String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "feesaccount_0", "02011001-0128", null, 
                    new String[]{ refName });/* @res "元数据配置错误，不存在参照{0}！" */
            throw new BusinessRuntimeException(errMsg);
        }
    }

    private static final String KEY_FROM = " from ";

    private String parseTable(String name) {
        if (name != null) {
            int nPos = name.indexOf(KEY_FROM);
            if (nPos > 0) {
                int emptyIndex = name.indexOf(" ", nPos + KEY_FROM.length());
                name = name.substring(nPos + KEY_FROM.length(), emptyIndex);
            } else if (name.length() > 50) {
                String[] arr = name.trim().split(" ");
                if (!ArrayUtils.isEmpty(arr)) {
                    name = arr[0];
                }
            }
        }
        return name;
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean beforeEdit(BillEditEvent e) {
        String val = (String)getBillCardPanel().getBodyValueAt(e.getRow(), e.getKey());
        BillItem item = (BillItem)e.getSource();
        boolean editable = canEdit(e);
        if (editable) {
            if (e.getKey().equals(FIELD_BD_REFNAME)) {
                Object obj = getBillCardPanel().getBodyValueAt(e.getRow(), FIELD_BD_MDID);
                if (obj != null) {
                    AbstractRefModel refModel = getDataTypeRefPane().getRefModel();
                    refModel.setPKMatch(true);
                    refModel.setMatchPkWithWherePart(true);
                    Vector vecRow = refModel.matchPkData(obj.toString());
                    Object[] objArr = refModel.getValues(refModel.getRefCodeField(), vecRow); 
                    if (objArr != null && objArr.length > 0) {
                        String wherePart = " metadatatypename = '" + objArr[0] + "' ";
                        getRefNameRefPanel().getRefModel().setWherePart(wherePart);
                    } else {
                        getRefNameRefPanel().getRefModel().setWherePart(null);
                    }
                } else {
                    getRefNameRefPanel().getRefModel().setWherePart(null);
                }
                
                UIRefPane refPane = (UIRefPane)item.getItemEditor().getComponent();
                Vector vec = refPane.getRefModel().matchData("name", val);
                if (vec != null && !vec.isEmpty()) {
                    int index = refPane.getRefModel().getFieldIndex(refPane.getRefModel().getPkFieldCode());
                    String pk = (String)((Vector)vec.get(0)).get(index);
                    refPane.setPK(pk);
                }
            } else if (FIELD_BD_MDID.equals(e.getKey())) {
                UIRefPane refPane = (UIRefPane)item.getItemEditor().getComponent();
                refPane.setPK(val);
            } else if (FIELD_QRY_OBJDATATYPE.equals(e.getKey())) {
                initMDRefPaneSelectedData(e.getRow());
            }
        }
        return editable;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initMDRefPaneSelectedData(int nRow) {
        Vector vec = new Vector();
        Vector row = new Vector();
        row.add(parseMDField(nRow));
        row.add(parseMDShowName(nRow));
        vec.add(row);
        getMDRefPane().getRefModel().setSelectedData(vec);
    }

    private boolean canEdit(BillEditEvent e) {
        boolean edit = true;
        if (FIELD_BD_MDID.equals(e.getKey())
                || FIELD_BD_REFNAME.equals(e.getKey())) {
            initMDRefPaneSelectedData(e.getRow());
            Object val = getBillCardPanel().getBodyValueAt(e.getRow(), FIELD_QRY_OBJDATATYPE);
            if (val == null) {
                ShowStatusBarMsgUtil.showStatusBarMsg(
                        nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                "feesaccount_0", "02011001-0124")/* @res "请先选择查询对象！" */,
                        getModel().getContext());
                edit = false;
            }
            MDTreeNode treeNode = getMDRefPane().getDialog().locateNode();
            if (treeNode != null) {
                MDNode node = treeNode.getMDNode();
                if (!StringUtils.isEmpty(node.getAttribute().getRefModelName())) {
                    ShowStatusBarMsgUtil.showStatusBarMsg(
                            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
                                    .getStrByID("feesaccount_0",
                                            "02011001-0125")/* @res "该查询对象不是自定义项，不能编辑该字段！" */, 
                                            getModel().getContext());
                    edit = false;
                }
            }
        }
        return edit;
    }

    private String parseMDField(int row) {
        String field = (String) getBillCardPanel().getBodyValueAt(row,
                FIELD_TALLYFIELDNAME);
        // if (field != null && field.indexOf("zb.h_") == 0) {
        // field = field.substring(5);
        // }
        return field;
    }

    private String parseMDShowName(int row) {
        String field = (String) getBillCardPanel().getBodyValueAt(row,
                FIELD_QRY_OBJDATATYPE);
        if (field == null || "1".equals(field)) {
            field = (String) getBillCardPanel().getBodyValueAt(row,
                    FIELD_DSP_OBJNAME);
        }
        return field;
    }

}
