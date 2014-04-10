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
import nc.ui.erm.report.comp.ErmUIComboBox;
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
 * ���������ʱ�ڵ㷢��-��ѯ����ѡ�����
 * 
 * @since V60 Create at 2010-11-09
 * 
 */
//@SuppressWarnings("restriction")
public class ErmReleaseQryObjSelectComp extends UIPanel {
    private static final long serialVersionUID = 1L;

    // ��ѯ����ѡ�����������ʱ֧��5����ѯ����ѡ��
    private int qryObjTblRowCount = 5;
    // ��ѯ����ѡ�����������ʱ֧��2������
    private int qryObjTblColCount = 2;
    // ��ѯ����ѡ���
    private UITable qryObjTable = null;
    // ��ѯ����ѡ�������
    private List<String> colNameList = Arrays.asList(new String[] {
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
            "UC000-0002770")/* @res "��ѯ����" */,
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "feesaccount_0", "02011001-0040") /* @res "��ѯ����Ĵ���" */});

    // ��ѯ�����б�
    private List<QueryObjVO> qryObjVOList = new ArrayList<QueryObjVO>();
    private String qryObjSqlWhere;

    private UIComboBox reportFormatCombo = null; // ��ҳ��ʽ��Ͽ�(��ѡ�����ʽ/��ҽ��ʽ)
    private UIComboBox showFormatCombo = null; // ��ʾ��ʽ��Ͽ�(��ѡ������/����/����+����)

    // private UICheckBox showVoucherChk = null; // �Ƿ���ʾƾ֤��
    // private UICheckBox multiUnitSepChk = null; // �Ƿ񰴵�λ��ʾ
    // private UICheckBox multiUnitMerChk = null; // �Ƿ�൥λ�ϲ�

    private String reportType = null; // ��������

    // ��ʾƾ֤�ŵı����б�
    // private List<String> showVoucherReportList = new ArrayList<String>();

    // ��ʾ�൥λѡ��ı����б�
    // private List<String> multiUnitReportList = new ArrayList<String>();

    public ErmReleaseQryObjSelectComp() {
        setName("qryObjSelectPanel");
        setSize(new Dimension(590, 340));
        setPreferredSize(new Dimension(590, 340));
        setLayout(new BorderLayout());
        setVisible(true);
        // Ӧ�ö��� ��ʱע����
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
     * ���ܣ���ʼ����ѯ�����б�
     */
    private void initTable() {
        if (qryObjTable == null) {
            // ���ñ�ģ��
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new String[] { colNameList.get(0),
                    colNameList.get(1) });
            tableModel.setRowCount(qryObjTblRowCount);
            tableModel.setColumnCount(qryObjTblColCount);

            qryObjTable = new UITable(qryObjTblRowCount, qryObjTblColCount) {
                private static final long serialVersionUID = 1L;
                // ���浥Ԫ��༭��
                private TableCellEditor[] cellEditors = new TableCellEditor[qryObjTblRowCount];

                @Override
                public TableCellEditor getCellEditor(final int row,
                        final int column) {
                    if ((column == 0) && (cellEditors[row] != null)) {
                        return cellEditors[row];
                    }

                    switch (column) {
                    case 0:
                        ErmUIComboBox comboBox = new ErmUIComboBox();

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
                .getNCLangRes().getStrByID("feesaccount_0", "02011001-0041")/*@res "��ҳ��ʽ" */);
        accountFormatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        accountFormatLabel.setPreferredSize(labelSize);
        accountFormatLabel.setSize(100, 30);
        accountFormatLabel.setPreferredSize(new Dimension(100, 30));

        UILabel showFormatLabel = new UILabel(nc.vo.ml.NCLangRes4VoTransl
                .getNCLangRes().getStrByID("common", "UC000-0002449")/*@res "��ʾ��ʽ" */);
        showFormatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        showFormatLabel.setSize(100, 30);
        showFormatLabel.setPreferredSize(new Dimension(100, 30));

        // ��ҳ��ʽ
        Dimension compSize = new Dimension(85, 30);
        reportFormatCombo = new UIComboBox();
        reportFormatCombo.addItems(new DefaultConstEnum[] {
                new DefaultConstEnum(IPubReportConstants.ACCOUNT_FORMAT_LOCAL,
                        FipubReportResource.getAccountFormatLocalLbl()),
                        new DefaultConstEnum(
                                IPubReportConstants.ACCOUNT_FORMAT_FOREIGN,
                                FipubReportResource.getAccountFormatForeignLbl()) });
        reportFormatCombo.setPreferredSize(compSize);

        // ��ʾ��ʽ
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
        // showVoucherChk = new UICheckBox("��ʾƾ֤��");
        // multiUnitSepChk = new UICheckBox("����λ��ʾ");
        // multiUnitMerChk = new UICheckBox("�൥λ�ϲ�");
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
     * ���ܣ����ز�ѯ������б�<br>
     * ˵��������λ��Ϊ��ѯ����ż��λ��Ϊ��ѯ����Ĵ���<br>
     * 
     * @return ��ѯ������б�<br>
     */
    public List<Object> getSelectQryObj() {
        List<Object> qryObjList = new ArrayList<Object>();
        for (int i = 0; i < qryObjTblRowCount; i++) {
            Object qryObj = ((ErmUIComboBox) ((DefaultCellEditor) qryObjTable
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
     * ���ܣ��������ѡ������<br>
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
     * �õ��ò�ѯ�ڵ㶨��Ĳ�ѯ����<br>
     */
    public void getUseableQryObj() {
        String objtablename;
        if (IErmReportConstants.MATTERAPP_REP_NAME.equals(reportType)) {
            // ��ѯѡ���ʱ�����ע��Ĳ�ѯ����
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
            + "' and dsp_objtablename = '" + objtablename + "' and dr = 0 ";
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
                            "feesaccount_0", "02011001-0005")/* @res "����" */,
                            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                    "feesaccount_0", "02011001-0042")/*@res "��ȡ��ѯ����ʱ��������" */);
        }
//        if (IErmReportConstants.MATTERAPP_REP_NAME.equals(reportType)) {
//            if (qryObjVOList != null && qryObjVOList.size() > 0) {
//                List<QueryObjVO> qryObjVOLista = new ArrayList<QueryObjVO>();
//                for (QueryObjVO qryObj : qryObjVOList) {
////                    if ("billmaker".equals(qryObj.getDsp_objfieldname()) ||
////                            "apply_dept".equals(qryObj.getDsp_objfieldname())) {
//                        qryObjVOLista.add(qryObj);
////                    }
//                }
//                qryObjVOList = qryObjVOLista;
//            }
//        }
        // ���ÿ�ѡ��ѯ�����ֵ
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
     * ���ÿ�ѡ��ѯ�����ֵ<br>
     */
    private void setQryObjTableData() {
        String prop = QueryObjVO.DSP_OBJNAME;
        for (int i = 0; i < qryObjTblRowCount; i++) {
            ErmUIComboBox combox = ((ErmUIComboBox) ((DefaultCellEditor) qryObjTable
                    .getCellEditor(i, 0)).getComponent());
            combox.addItems(qryObjVOList, prop, true);
            combox.setSelectedItem(null);
            qryObjTable.getModel().setValueAt("", i, 0);
            qryObjTable.getValueAt(i, 0);
        }
    }

    /**
     * ���ܣ����ò�ѯ����˳��
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
     * ��ȡ��ҳ��ʽ
     * 
     * @return String
     */
    public String getReportFormat() {
        return reportFormatCombo.getSelectdItemValue().toString();
    }

    /**
     * ��ȡ��ʾ��ʽ
     * 
     * @return String
     */
    public String getShowFormat() {
        return showFormatCombo.getSelectdItemValue().toString();
    }

    /**
     * �Ƿ���ʾƾ֤��
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
     * ��ȡ����֯��ʾ��ʽ
     * 
     * @return String 0����/1������λ��ʾ/2������֯�ϲ�
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
