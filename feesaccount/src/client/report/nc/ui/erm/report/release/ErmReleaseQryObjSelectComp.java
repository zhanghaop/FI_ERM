package nc.ui.erm.report.release;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.queryobjreg.IReportQueryObjRegQuery;
import nc.itf.fipub.report.IPubReportConstants;
import nc.ui.fipub.comp.PubUIComboBox;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.utils.fipub.FipubReportResource;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.report.QueryObjVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * 报销管理帐表节点发布-查询对象选择面板
 * 
 * @since V60 Create at 2010-11-09
 * 
 */
@SuppressWarnings("restriction")
public class ErmReleaseQryObjSelectComp extends UIPanel {
    private static final long serialVersionUID = 1L;

    // 查询对象选择表行数，暂时支持5个查询对象选择
    private int qryObjTblRowCount = 5;
    // 查询对象选择表列数，暂时支持2列属性
    private int qryObjTblColCount = 2;
    // 查询对象选择表
    private UITable qryObjTable = null;
    // 查询对象选择表列名
    private List<String> colNameList = Arrays.asList(new String[] {
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
            "UC000-0002770")/* @res "查询对象" */,
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "feesaccount_0", "02011001-0040") /* @res "查询对象的次序" */});

    // 查询对象列表
    private List<QueryObjVO> qryObjVOList = new ArrayList<QueryObjVO>();
    private String qryObjSqlWhere;

    private UIComboBox reportFormatCombo = null; // 账页格式组合框(可选：金额式/外币金额式)
    private UIComboBox showFormatCombo = null; // 显示格式组合框(可选：名称/编码/名称+编码)

    // private UICheckBox showVoucherChk = null; // 是否显示凭证号
    // private UICheckBox multiUnitSepChk = null; // 是否按单位列示
    // private UICheckBox multiUnitMerChk = null; // 是否多单位合并

    private String reportType = null; // 报表类型

    // 显示凭证号的报表列表
    // private List<String> showVoucherReportList = new ArrayList<String>();

    // 显示多单位选项的报表列表
    // private List<String> multiUnitReportList = new ArrayList<String>();

    public ErmReleaseQryObjSelectComp() {
        setName("qryObjSelectPanel");
        setSize(new Dimension(590, 340));
        setPreferredSize(new Dimension(590, 340));
        setLayout(new BorderLayout());
        setVisible(true);
        // 应用多语 暂时注销掉
        // initialize();
        initTable();
        initOther();
    }

    // private void initialize() {
    //
    // showVoucherReportList.add(IPubReportConstants.DETAIL_REPORT);
    //
    // multiUnitReportList.add(IPubReportConstants.YSZLFX_REPORT);
    // multiUnitReportList.add(IPubReportConstants.YFZLFX_REPORT);
    // }

    /**
     * 功能：初始化查询对象列表
     */
    private void initTable() {
        if (qryObjTable == null) {
            // 设置表模型
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new String[] { colNameList.get(0),
                    colNameList.get(1) });
            tableModel.setRowCount(qryObjTblRowCount);
            tableModel.setColumnCount(qryObjTblColCount);

            qryObjTable = new UITable(qryObjTblRowCount, qryObjTblColCount) {
                private static final long serialVersionUID = 1L;
                // 缓存单元格编辑器
                private TableCellEditor[] cellEditors = new TableCellEditor[qryObjTblRowCount];

                @Override
                public TableCellEditor getCellEditor(final int row,
                        final int column) {
                    if ((column == 0) && (cellEditors[row] != null)) {
                        return cellEditors[row];
                    }

                    switch (column) {
                    case 0:
                        PubUIComboBox comboBox = new PubUIComboBox();

                        comboBox.addItemListener(new ComboBoxItemListener());
                        cellEditors[row] = new DefaultCellEditor(comboBox);
                        return cellEditors[row];
                    default:
                        return null;
                    }

                }

            };
            qryObjTable.setName("qryObjTable");
            qryObjTable.setModel(tableModel);
            qryObjTable.getTableHeader().setBackground(super.getBackground());
            qryObjTable.setVisible(true);

            JScrollPane scrollPane = new JScrollPane(qryObjTable);
            scrollPane.setPreferredSize(new Dimension(100, 124));
            add(scrollPane, BorderLayout.NORTH);
        }
    }

    private void initOther() {

        UIPanel otherPanel = new UIPanel();
        otherPanel.setName("otherPanel");
        FlowLayout otherPanelLayout = new FlowLayout();
        otherPanelLayout.setAlignment(FlowLayout.LEFT);
        otherPanelLayout.setHgap(25);
        otherPanelLayout.setVgap(15);

        otherPanel.setLayout(otherPanelLayout);

        UIPanel generalPanel = new UIPanel();
        generalPanel.setName("generalPanel");
        FlowLayout generalPanelLayout = new FlowLayout();
        generalPanelLayout.setAlignment(FlowLayout.LEFT);
        generalPanelLayout.setHgap(20);
        generalPanelLayout.setVgap(15);
        generalPanel.setLayout(generalPanelLayout);

        Dimension labelSize = new Dimension(50, 30);
        UILabel accountFormatLabel = new UILabel(nc.vo.ml.NCLangRes4VoTransl
                .getNCLangRes().getStrByID("feesaccount_0", "02011001-0041")/*@res "帐页格式" */);
        accountFormatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        accountFormatLabel.setPreferredSize(labelSize);
        accountFormatLabel.setSize(100, 30);
        accountFormatLabel.setPreferredSize(new Dimension(100, 30));

        UILabel showFormatLabel = new UILabel(nc.vo.ml.NCLangRes4VoTransl
                .getNCLangRes().getStrByID("common", "UC000-0002449")/*@res "显示格式" */);
        showFormatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        showFormatLabel.setSize(100, 30);
        showFormatLabel.setPreferredSize(new Dimension(100, 30));

        // 帐页格式
        Dimension compSize = new Dimension(85, 30);
        reportFormatCombo = new UIComboBox();
        reportFormatCombo.addItems(new DefaultConstEnum[] {
                new DefaultConstEnum(IPubReportConstants.ACCOUNT_FORMAT_LOCAL,
                        FipubReportResource.getAccountFormatLocalLbl()),
                        new DefaultConstEnum(
                                IPubReportConstants.ACCOUNT_FORMAT_FOREIGN,
                                FipubReportResource.getAccountFormatForeignLbl()) });
        reportFormatCombo.setPreferredSize(compSize);

        // 显示格式
        showFormatCombo = new UIComboBox();
        showFormatCombo.addItems(new DefaultConstEnum[] {
                new DefaultConstEnum(IPubReportConstants.SHOW_FORMAT_NAME,
                        FipubReportResource.getShowFormatNameLbl()),
                        new DefaultConstEnum(IPubReportConstants.SHOW_FORMAT_CODE,
                                FipubReportResource.getShowFormatCodeLbl()),
                                new DefaultConstEnum(IPubReportConstants.SHOW_FORMAT_NAME_CODE,
                                        FipubReportResource.getShowFormatNameCodeLbl()) });
        showFormatCombo.setPreferredSize(compSize);

        generalPanel.add(accountFormatLabel);
        generalPanel.add(reportFormatCombo);

        generalPanel.add(showFormatLabel);
        generalPanel.add(showFormatCombo);

        otherPanel.add(generalPanel);

        // ==============================================================

        UIPanel specialPanel = new UIPanel();
        specialPanel.setName("specialPanel");
        FlowLayout specialPanelLayout = new FlowLayout();
        specialPanelLayout.setAlignment(FlowLayout.LEFT);
        specialPanelLayout.setHgap(20);
        specialPanelLayout.setVgap(15);
        specialPanel.setLayout(specialPanelLayout);
        // specialPanel.setBorder(BorderFactory.createEtchedBorder());

        compSize.setSize(new Dimension(155, 30));
        // showVoucherChk = new UICheckBox("显示凭证号");
        // multiUnitSepChk = new UICheckBox("按单位列示");
        // multiUnitMerChk = new UICheckBox("多单位合并");
        // showVoucherChk.setPreferredSize(compSize);
        // multiUnitSepChk.setPreferredSize(compSize);
        // multiUnitMerChk.setPreferredSize(compSize);
        //
        // specialPanel.add(showVoucherChk);
        // specialPanel.add(multiUnitSepChk);
        // specialPanel.add(multiUnitMerChk);
        //
        // ButtonGroup multiGroup = new ButtonGroup();
        // multiGroup.add(multiUnitSepChk);
        // multiGroup.add(multiUnitMerChk);
        // multiUnitSepChk.doClick();

        otherPanel.add(specialPanel);

        add(otherPanel, BorderLayout.CENTER);

    }

    /**
     * 功能：返回查询对象的列表<br>
     * 说明：奇数位置为查询对象，偶数位置为查询对象的次序<br>
     * 
     * @return 查询对象的列表<br>
     */
    public List<Object> getSelectQryObj() {
        List<Object> qryObjList = new ArrayList<Object>();
        for (int i = 0; i < qryObjTblRowCount; i++) {
            Object qryObj = ((PubUIComboBox) ((DefaultCellEditor) qryObjTable
                    .getCellEditor(i, 0)).getComponent()).getSelectdItem();
            if (qryObj == null) {
                continue;
            }
            qryObjList.add(qryObj);
            qryObjList.add(qryObjTable.getValueAt(i, 1));
        }
        return qryObjList;
    }

    /**
     * 功能：获得其他选项条件<br>
     * 
     * @return Map<String, String><br>
     */
    public Map<String, String> getOtherConds() {
        Map<String, String> otherConds = new HashMap<String, String>();
        otherConds.put("accountFormat", reportFormatCombo.getSelectdItemValue()
                .toString());
        otherConds.put("showFormat", showFormatCombo.getSelectdItemValue()
                .toString());
        return otherConds;
    }

    /**
     * 得到该查询节点定义的查询对象<br>
     */
    public void getUseableQryObj() {
        String objtablename;
        if (IErmReportConstants.MATTERAPP_REP_NAME.equals(reportType)) {
            // 查询选择帐表类型注册的查询对象
            objtablename = MtAppDetailVO.getDefaultTableName();
        } else if (IErmReportConstants.EXPENSE_DETAIL_REP_NAME
                .equals(reportType)
                || IErmReportConstants.EXPENSE_BALANCE_REP_NAME
                .equals(reportType)) {
            objtablename = "cs";
        } else {
            objtablename = "zb";
        }
        String sqlWhere = " ownmodule='"
            + IErmReportConstants.ERM_PRODUCT_CODE_Lower
            + "' and dsp_objtablename = '" + objtablename + "'";
        if (sqlWhere.equals(qryObjSqlWhere)) {
            return;
        } else {
            qryObjSqlWhere = sqlWhere;
        }
        try {
            qryObjVOList = NCLocator.getInstance()
            .lookup(IReportQueryObjRegQuery.class)
            .getRegisteredQueryObjByClause(sqlWhere);
        } catch (BusinessException e) {
            MessageDialog.showErrorDlg(
                    this,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                            "feesaccount_0", "02011001-0005")/* @res "错误" */,
                            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                    "feesaccount_0", "02011001-0042")/*@res "获取查询对象时发生错误！" */);
        }
        // 设置可选查询对象的值
        setQryObjTableData();
    }

    public void updateUIOptions() {

        // showVoucherChk.setVisible(false);
        // multiUnitSepChk.setVisible(false);
        // multiUnitMerChk.setVisible(false);
        //
        //
        //
        // if (showVoucherReportList.contains(reportType)) {
        // showVoucherChk.setVisible(true);
        // showVoucherChk.setSelected(false);
        // }
        //
        // if (multiUnitReportList.contains(reportType)) {
        // multiUnitSepChk.setVisible(true);
        // multiUnitMerChk.setVisible(true);
        // multiUnitSepChk.doClick();
        // }

        this.repaint();
    }

    /**
     * 设置可选查询对象的值<br>
     */
    private void setQryObjTableData() {
        String prop = QueryObjVO.DSP_OBJNAME;
        for (int i = 0; i < qryObjTblRowCount; i++) {
            PubUIComboBox combox = ((PubUIComboBox) ((DefaultCellEditor) qryObjTable
                    .getCellEditor(i, 0)).getComponent());
            combox.addItems(qryObjVOList, prop, true);
            combox.setSelectedItem(null);
            qryObjTable.getModel().setValueAt("", i, 0);
            qryObjTable.getValueAt(i, 0);
        }
    }

    /**
     * 功能：设置查询对象顺序
     */
    private void setQryObjOrder() {
        int order = 1;
        for (int i = 0; i < qryObjTblRowCount; i++) {
            if (StringUtils.isEmpty((String) qryObjTable.getCellEditor(i, 0)
                    .getCellEditorValue())) {
                qryObjTable.setValueAt("", i, 1);
                continue;
            }
            qryObjTable.setValueAt(order, i, 1);
            order++;
        }
    }

    public void setReportType(final String reportType) {
        this.reportType = reportType;
    }

    /**
     * 获取账页格式
     * 
     * @return String
     */
    public String getReportFormat() {
        return reportFormatCombo.getSelectdItemValue().toString();
    }

    /**
     * 获取显示格式
     * 
     * @return String
     */
    public String getShowFormat() {
        return showFormatCombo.getSelectdItemValue().toString();
    }

    /**
     * 是否显示凭证号
     * 
     * @return String
     */
    // public UFBoolean getShowVoucher() {
    // if (showVoucherReportList.contains(reportType)) {
    // return new UFBoolean(showVoucherChk.isSelected());
    // }
    // return null;
    // }

    /**
     * 获取多组织显示方式
     * 
     * @return String 0：无/1：按单位列示/2：多组织合并
     */
    // public int getMultiUnitShowMode() {
    // if (multiUnitReportList.contains(reportType)) {
    // if (multiUnitSepChk.isSelected()) {
    // return 1;
    // } else if (multiUnitMerChk.isSelected()) {
    // return 2;
    // }
    // }
    // return 0;
    // }

    class ComboBoxItemListener implements ItemListener {
        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setQryObjOrder();
            }
        }
    }

}
