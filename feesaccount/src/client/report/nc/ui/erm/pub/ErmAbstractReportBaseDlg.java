package nc.ui.erm.pub;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErmBillTypeUtil;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefInitializeCondition;
import nc.ui.er.djlx.ErmTrantypeRefModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.comp.ReportNormalPanel;
import nc.ui.fipub.comp.ReportQryDlg;
import nc.ui.fipub.comp.editorandrender.QueryObjTableCellEditor;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.IQueryTemplateTotalVOProcessor;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.util.fi.pub.SqlUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.erm.pub.ErmReportPubUtil;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportInitializeItemVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.fipub.utils.RefConstant;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.granite.lang.util.Collections;

import com.ufida.dataset.IContext;

/**
 * ���������ʱ��ѯ�Ի������
 * @author chendya
 *
 */
@SuppressWarnings("restriction")
public abstract class ErmAbstractReportBaseDlg extends ReportQryDlg {

	private static final long serialVersionUID = 1416820562038177684L;

	protected static final String QRY_MODE_COMB = "qryModeComb"; // ��ѯ��ʽ
	protected static final String BILL_STATE_COMB = "billStateComb"; // ����״̬
	protected static final String BEGIN_TIME_REF = "beginTimeRef"; // ��ʼʱ��
	protected static final String END_TIME_REF = "endTimeRef"; // ����ʱ��
	protected static final String CURRENCY_REF = "currencyRef"; // ����
	protected static final String FINANCIAL_ORG_REF = "financialOrgRef"; // ������֯
	protected static final String TIME_TYPE_LABEL = "timeTypeLabel";
	
	private static final String BX_PK_ORG = "pk_org";
//	private static final String BX_DJLXBM = "djlxbm";
	private static final String BX_FYDWBM = "fydwbm";
	private static final String BX_DWBM = "dwbm";
	private BusiTypeVO busTypeVO;
    private final String djlx;
	
	protected BusiTypeVO getBusTypeVO(){
		if(busTypeVO==null){
			busTypeVO = ErmBillTypeUtil.getBusTypeVO(BXConstans.BILLTYPECODE_CLFBX,BXConstans.BX_DJDL);
		}
		return busTypeVO;
	}

	public ErmAbstractReportBaseDlg(Container parent, IContext context,
            String strNodeCode, int iSysCode, TemplateInfo ti, String title,
            String djlx) {
		super(parent, context, strNodeCode, iSysCode, ti, title);
        this.djlx = djlx;
		initialize();
	}

	private void initialize(){
		// �޸Ĳ�ѯ����
		registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
			@Override
            public void processQueryTempletTotalVO(QueryTempletTotalVO totalVO) {
				QueryConditionVO[] vos = totalVO.getConditionVOs();
				if (vos == null || vos.length == 0) {
					return;
				}
				for(QueryConditionVO vo: vos){
					if(BX_PK_ORG.equals(vo.getFieldCode())){
						String[] pkOrg = getPk_org();
						if(pkOrg!=null && pkOrg.length>0){
							vo.setValue(pkOrg[0]);
							//������� ������λ
							vo.setIfImmobility(UFBoolean.TRUE);
						}
					}
				}
			}
		});
	}

	/**
	 * ���ز�ѯ�������ܲ�����֯Ӱ����ֶ�
	 * @return
	 */
	protected abstract String[] getOrgRelationField();

	/**
	 * ҵ��Ԫ�仯������<br>
	 *
	 * @since V60<br>
	 */
	public class OrgChangedListener implements ValueChangedListener {
		@Override
        public void valueChanged(ValueChangedEvent event) {
			try {
				handleOrgChangeEvent(event);
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e.getMessage());
			}
		}
	}

	protected void handleOrgChangeEvent(ValueChangedEvent event)
			throws BusinessException {
		String[] newPk_orgs = (String[])event.getNewValue();
        setPk_org(newPk_orgs);
		//������֯����pk
		UIRefPane refPaneOrg = BXQryTplUtil.getRefPaneByFieldCode(this,BX_PK_ORG);
		if(refPaneOrg!=null){
			refPaneOrg.setPKs(newPk_orgs);
			List<IFilter> filtersByFieldCode = this.getFiltersByFieldCode(BX_PK_ORG);
			for(IFilter f:filtersByFieldCode){
				f.setFieldValue(null);
			}
		}
		
		//��֯����ֶ����ù���pk
		UIRefPane[] refPanes = BXQryTplUtil.getRefPaneByFieldCode(this, getOrgRefFields(BX_PK_ORG).toArray(new String[0]));
		for(UIRefPane refPane : refPanes){
			if(refPane != null){
				refPane.getRefModel().setPk_org(newPk_orgs[0]);
			}
		}

        List<IFilterEditor> editorList = getSimpleEditorFilterEditors();
        if (editorList != null) {
            AbstractRefModel refModel;
            LoginContext context = (LoginContext)getContext().getAttribute("key_private_context");
            for (IFilterEditor editor : editorList) {
                DefaultFilterEditor filter = (DefaultFilterEditor) editor
                        .getFilterEditorComponent();
                FilterMeta filterMeta = filter.getFilterMeta();
                Component[] components = filter.getFieldValueEditor()
                .getFieldValueEditorComponent().getComponents();
                if (!ArrayUtil.isEmpty(components)) {
                    for (Component component : components) {
                        if (component instanceof UIRefPane) {
                            UIRefPane refPane = (UIRefPane) component;
                            refModel = refPane.getRefModel();
                            if (refModel instanceof nc.ui.bd.ref.busi.UserDefaultRefModel ||
                                    refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel || 
                                    excludeOrgChange(filterMeta.getFieldCode())) {
                                if (refModel != null && !(refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel)) {
                                    String[] orgArray = parseDataPowerOrgs(context.getPkorgs(), filterMeta.getFieldCode());
                                    configDataPowerRef(refPane, orgArray);
                                }
                                //��������Ҫ�������˷��óе���λ�����ֶ�
                                if (isPkOrgSameAssumeOrg() && 
                                        getExcludeAssumeOrgRefList().contains(filterMeta.getFieldCode()) && 
                                        refModel != null && 
                                        !(refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel)) {
                                    if (!ArrayUtils.isEmpty(newPk_orgs)) {
                                        refPane.setMultiCorpRef(false);
                                        refModel.setPk_org(newPk_orgs[0]);
                                        if (refModel instanceof nc.ui.resa.refmodel.CostCenterTreeRefModel) {
                                            refModel.setWherePart(" " + CostCenterVO.PK_FINANCEORG + "=" + "'"
                                                    + newPk_orgs[0] + "'");
                                        }
                                    }
                                }
                            } else {
                                refPane.setMultiCorpRef(false);
                                if (!ArrayUtils.isEmpty(newPk_orgs)) {
                                    refModel.setPk_org(newPk_orgs[0]);
                                } else {
                                    refModel.setPk_org(null);
                                }
                            }

                        }
                    }
                }
            }
        }
}
	
	private boolean pkOrgSameAssumeOrg = false;
	
	public boolean isPkOrgSameAssumeOrg() {
	    if (pkOrgSameAssumeOrg) {
            IReportQueryCond qryCond = getNormalPanel().getReportQueryCondVO();
            List<QryObj> qryObjList = qryCond.getQryObjs();
            if (!Collections.isEmpty(qryObjList)) {
                if (ExpenseBalVO.BX_JKBXR.equals(qryObjList.get(0).getOriginFld())) {
                    setPkOrgSameAssumeOrg(false);
                }
            }
	    }
        return pkOrgSameAssumeOrg;
    }

    public void setPkOrgSameAssumeOrg(boolean pkOrgSameAssumeOrg) {
        this.pkOrgSameAssumeOrg = pkOrgSameAssumeOrg;
    }

    private boolean excludeOrgChange(String fieldCode) {
        boolean exclude = false;
	    if (getExcludePayOrgRefList().contains(fieldCode) ||
	            getExcludeAssumeOrgRefList().contains(fieldCode) ||
	            getExcludeJkOrgRefList().contains(fieldCode) ||
	            getExcludeBxOrgRefList().contains(fieldCode)) {
	        exclude = true;
	    }
	    return exclude;
	}
	
	private static List<String> excludePayOrgField = new ArrayList<String>();

	private static List<String> getExcludePayOrgRefList() {
	    if (excludePayOrgField.isEmpty()) {
	        excludePayOrgField.add("pk_payorg");
	        excludePayOrgField.add("fkyhzh");//��λ�����˻�
	        excludePayOrgField.add("cashproj");
	        excludePayOrgField.add("cashitem");
	        excludePayOrgField.add("pk_cashaccount");

            excludePayOrgField.add("zb.pk_payorg");
            excludePayOrgField.add("zb.fkyhzh");//��λ�����˻�
            excludePayOrgField.add("zb.cashproj");
            excludePayOrgField.add("zb.cashitem");
            excludePayOrgField.add("zb.pk_cashaccount");
	    }
//	    if (excludePayOrgRefModel.isEmpty()) {
//	        excludePayOrgRefModel.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//������֯
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.BankaccSubDefaultRefModel");//ʹ��Ȩ����
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.FundPlanDefaultRefModel");//�ʽ�ƻ���Ŀ
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.CashflowDefaultRefModel");//�ֽ�������Ŀ
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.CashAccountRefModel");//�ֽ��˻�
//	    }
	    return excludePayOrgField;
	}
	   
    private static List<String> excludeAssumeOrgRefField = new ArrayList<String>();

    private static List<String> getExcludeAssumeOrgRefList() {
        if (excludeAssumeOrgRefField.isEmpty()) {
            excludeAssumeOrgRefField.add("fydwbm");
            excludeAssumeOrgRefField.add("assume_org");
            excludeAssumeOrgRefField.add("fydeptid");
            excludeAssumeOrgRefField.add("assume_dept");
            excludeAssumeOrgRefField.add("pk_resacostcenter");
            excludeAssumeOrgRefField.add("szxmid");//��֧��Ŀ
            excludeAssumeOrgRefField.add("pk_iobsclass");//��֧��Ŀ

            excludeAssumeOrgRefField.add("zb.fydwbm");
            excludeAssumeOrgRefField.add("zb.assume_org");
            excludeAssumeOrgRefField.add("zb.fydeptid");
            excludeAssumeOrgRefField.add("zb.assume_dept");
            excludeAssumeOrgRefField.add("zb.pk_resacostcenter");
            excludeAssumeOrgRefField.add("zb.szxmid");//��֧��Ŀ
            excludeAssumeOrgRefField.add("zb.pk_iobsclass");//��֧��Ŀ
        }
//        if (excludeAssumeOrgRefM.isEmpty()) {
//            excludeAssumeOrgRefM.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//������֯
//            excludeAssumeOrgRefM.add("nc.ui.org.ref.DeptDefaultRefModel");//����
//            excludeAssumeOrgRefM.add("nc.ui.resa.refmodel.CostCenterTreeRefModel");//�ɱ�����
//            excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.InoutBusiClassDefaultRefModel");//��֧��Ŀ
//        }
        return excludeAssumeOrgRefField;
    }
    
    private static List<String> excludeJkOrgRefField = new ArrayList<String>();

    private static List<String> getExcludeJkOrgRefList() {
        if (excludeJkOrgRefField.isEmpty()) {
            excludeJkOrgRefField.add("dwbm");
            excludeJkOrgRefField.add("deptid");
            excludeJkOrgRefField.add("jkbxr");
            excludeJkOrgRefField.add("skyhzh");
            excludeJkOrgRefField.add("pk_project");
            excludeJkOrgRefField.add("pk_wbs");
            excludeJkOrgRefField.add("pk_supplier");

            excludeJkOrgRefField.add("zb.dwbm");
            excludeJkOrgRefField.add("zb.deptid");
            excludeJkOrgRefField.add("zb.jkbxr");
            excludeJkOrgRefField.add("zb.skyhzh");
            excludeJkOrgRefField.add("zb.pk_project");
            excludeJkOrgRefField.add("zb.pk_wbs");
            excludeJkOrgRefField.add("zb.pk_supplier");
        }
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//������֯
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.DeptDefaultRefModel");//����
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsndocDefaultRefModel");//��Ա
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsnbankaccDefaultRefModel");//���������˻�
//      excludeAssumeOrgRefM.add("nc.ui.pmpub.ref.ProjectDefaultRefModel");//��Ŀ
//      excludeAssumeOrgRefM.add("nc.ui.pmpub.ref.WBSDefaultRefModel");//��Ŀ�����ֽ�ṹ(WBS)
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.SupplierDefaultRefModel");//��Ӧ�̵���
        return excludeJkOrgRefField;
    }
    
    private static List<String> excludeBxOrgRefField = new ArrayList<String>();

    private static List<String> getExcludeBxOrgRefList() {
        if (excludeBxOrgRefField.isEmpty()) {
            excludeBxOrgRefField.add("bx_dwbm");
            excludeBxOrgRefField.add("bx_deptid");
            excludeBxOrgRefField.add("bx_jkbxr");
            excludeBxOrgRefField.add("skyhzh");

            excludeBxOrgRefField.add("zb.bx_dwbm");
            excludeBxOrgRefField.add("zb.bx_deptid");
            excludeBxOrgRefField.add("zb.bx_jkbxr");
            excludeBxOrgRefField.add("zb.skyhzh");
        }
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//�����˵�λ
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.DeptDefaultRefModel");//����
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsndocDefaultRefModel");//��Ա
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsnbankaccDefaultRefModel");//���������˻�
        return excludeBxOrgRefField;
    }
    
    
    
	@Override
	protected String doBusiCheck() {
		// ��ִ�и���У��
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}

		try {
			errMsg = "";

	        ReportQueryCondVO queryCondVO = (ReportQueryCondVO) super.getReportQueryCondVO();
	        String currencyType = queryCondVO.getLocalCurrencyType();
			// �ڲ�����֯������Ϊ��
			String[] pk_orgs = getPk_org();
			if (ArrayUtils.isEmpty(pk_orgs) || StringUtils.isEmpty(pk_orgs[0])) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0028")/*@res "������֯������Ϊ��"*/;
			} else if (pk_orgs.length > 1 && "org_local".equals(currencyType)) {
				// �۲�����֯��λ�ұ���һ��
				Set<String> localCurrtype = new HashSet<String>();
				CurrtypeVO[] currtypeVOs = ErmReportPubUtil.getLocalCurrencyByOrgID(pk_orgs);
				for (CurrtypeVO vo : currtypeVOs) {
					localCurrtype.add(vo.getPk_currtype());
				}
				if (localCurrtype.size() > 1) {
					errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0029")/*@res "\r\n�����ѯ��֯�ı�λ�Ҳ�һ�£���ֱ��ѯ��"*/;
				}
			}
		} catch (BusinessException e) {
			errMsg = e.getMessage();
		}

		return StringUtils.isEmpty(errMsg) ? null : errMsg;
	}

	protected UILabel getShowLabel(String labelName) {
		UILabel tempLabel = new UILabel(labelName);
		tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
		return tempLabel;
	}

	protected UILabel getShowLabel0(String labelName) {
		UILabel tempLabel = new UILabel(labelName);
		tempLabel.setHorizontalAlignment(SwingConstants.LEFT);
		return tempLabel;
	}

	public String convertQueryTemplateSql(String oriSql) {
		if (StringUtils.isEmpty(oriSql)) {
			return "";
		}
		if(getSysCode()==ErmReportQryDlg.ERM_MATTERAPP){
			return oriSql;
		}

		Map<String, String> filters = new HashMap<String, String>();
		//����Ԫ����
		if(getSysCode()==ErmReportQryDlg.ERM_EXPDETAIL){
			filters.put("er_expenseaccount.", "zb.");
		}else if(ErmReportQryDlg.ERM_EXPBAL == getSysCode()){
			filters.put("er_expensebal.", "zb.");
		}else {
			filters.put("er_bxzb.", "zb.");
			filters.put("er_jkzb.", "zb.");
		}

		Map<String, String> tableAlas = new HashMap<String, String>();
		Set<Entry<String, String>> entrySet = filters.entrySet();
		for (Entry<String, String> entry : entrySet) {
			if(oriSql.contains(entry.getKey())){
				tableAlas.put(entry.getKey(), entry.getValue());
			}
		}
		if(tableAlas.isEmpty()){
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0015")/*@res "��֧�ֱ���"*/);
		}
		for (Entry<String, String> entry : tableAlas.entrySet()) {
			oriSql = StringUtils.replace(oriSql, entry.getKey(), entry.getValue());
		}
		return oriSql;
	}

	@Override
	public IReportQueryCond getReportQueryCondVO() throws BusinessException {
		ReportQueryCondVO queryCondVO = (ReportQueryCondVO) super.getReportQueryCondVO();
		try {
			getQryCondEditor().setPowerEnable(false);
			String sqlWhere = null;
			if(getSysCode()==ErmReportQryDlg.ERM_MATTERAPP){
				sqlWhere = getQryCondEditor().getQueryScheme()
				.getWhereSQLOnly();
			}else {
				sqlWhere = getQryCondEditor().getQueryScheme()
				.getTableListFromWhereSQL().getWhere();
			}
			queryCondVO.setWhereSql(convertQueryTemplateSql(sqlWhere));
			setQueryCond(queryCondVO);
		} catch (InvalidAccperiodExcetion e) {
			Logger.error(e.getMessage(), e, this.getClass(), "getReportQueryCondVO");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0022")/*@res "��ȡ�����ѯ�����쳣��"*/);
		} finally{
			getQryCondEditor().setPowerEnable(true);
		}

		return queryCondVO;
	}
	
    protected Dimension labelSize = new Dimension(110, 20);
    protected Dimension compSize = new Dimension(105, 20);
    
    protected int qryObjTblRowCount = 5; // ��ѯ����ѡ�����������ʱ֧��5����ѯ����ѡ��

	protected void initUINormalPanel() throws BusinessException {
        // Ϊ�������������ά��һ����ReportNormalPanel�������Ի����Ҳ�������������
        m_NormalPanel = new ReportNormalPanel(getNodeCode(), getLoginContext(), getMultiOrgRef(), getPanelHeightList()) {

            private static final long serialVersionUID = 1L;
            
            public void setPk_org(String[] pk_orgs) {

                UIRefPane refPane = null;
                IReportQueryCond qryCond = getReportQueryCondVO();
                List<QryObj> qryObjList = qryCond.getQryObjs();
                for (int i = 0; i < qryObjTblRowCount; i++) {
                    QueryObjTableCellEditor cellEditor = (QueryObjTableCellEditor) getQryObjTable().getCellEditor(i, 1);
                    if (cellEditor == null) {
                        continue;
                    }
                    refPane = (UIRefPane) cellEditor.getTableCellEditorComponent(getQryObjTable(), null, true, i, 1);
                    if (refPane != null && refPane.getRefModel() != null) {
//                        List<String> list = getMultiOrgRef();
                        List<String> list = getSingleOrgRef();
                        
                        ReportInitializeItemVO[] itemVOs;
                        try {
                            itemVOs = (ReportInitializeItemVO[])getAllQueryObj(getNodeCode()).getChildrenVO();
                        } catch (BusinessException e) {
                            Logger.error(e.getMessage(), e);
                            return;
                        }
                        if (!list.contains(itemVOs[i].getBd_mdid())) {
                            QryObj qryObj = qryObjList.get(i);
                            String[] orgArray = parseDataPowerOrgs(pk_orgs, qryObj.getOriginFld());
                            configDataPowerRef(refPane, orgArray);
                            super.setPk_org(orgArray);

                            AbstractRefModel refModel = refPane.getRefModel();
                            if (RefConstant.REF_NODENAME_COSTCENTER.equals(refModel.getRefNodeName())) {
                                refModel.addWherePart(null);
                            }
                        } else {
                            refPane.setMultiCorpRef(false);
                            super.setPk_org(pk_orgs);
                        }
                    }
                }
            }
        };

        List<Component> componentList = getComponentList();
        if (componentList != null) {
            for (Component comp : componentList) {
                if (comp instanceof JLabel) {
                    comp.setSize(labelSize);
                    comp.setPreferredSize(labelSize);
                    if ("unSettledLbl".equals(comp.getName())) {
                        ((JLabel) comp).setHorizontalAlignment(SwingConstants.LEFT);
                    } else {
                        ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                } else if (comp instanceof JRadioButton) {
                    ((JRadioButton) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    comp.setSize(labelSize);
                    comp.setPreferredSize(labelSize);
                } else if (comp instanceof UICheckBox && "unSettleChkBox".equals(comp.getName())) {
                    ((UICheckBox) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    comp.setSize(labelSize);
                    comp.setPreferredSize(labelSize);
                } else {
                    comp.setSize(compSize);
                    comp.setPreferredSize(compSize);
                }
                m_NormalPanel.getCondPane().add(comp);
            }
        }

        componentList = getCommonExtendCondCompList();
        if (componentList.size() > 0) {
            m_NormalPanel.add(getOptionPanel(componentList));
        }

        componentList = getExtendCondCompList();
        if (componentList.size() > 0) {
            m_NormalPanel.add(getOptionPanel(componentList));
        }
    }
	
	private void configDataPowerRef(UIRefPane refPane, String[] pk_orgs) {
	    AbstractRefModel model = refPane.getRefModel();
	    if (model instanceof nc.ui.org.ref.LiabilityCenterDefaultRefModel) {
	        refPane.setMultiCorpRef(false);
	    } else {
	        refPane.setMultiCorpRef(true);
	    }
        refPane.setMultiOrgSelected(true);
        refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // ����Ȩ�޿���
        refPane.setMultiRefFilterPKs(pk_orgs);
        RefInitializeCondition[] conditions = refPane.getRefUIConfig().getRefFilterInitconds();
        if (conditions != null) {
            for (RefInitializeCondition condition : conditions) {
                condition.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // ����Ȩ�޿���
                if (pk_orgs != null && pk_orgs.length > 0) {
                    condition.setDefaultPk(pk_orgs[0]);
                }
            }
        }
	}
	
	private String[] parseDataPowerOrgs(String[] pk_orgs, String fieldCode) {
	    nc.ui.org.ref.FinanceOrgDefaultRefTreeModel fiRefModel = new nc.ui.org.ref.FinanceOrgDefaultRefTreeModel();
        fiRefModel.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // ����Ȩ�޿���
//        String dataPower = fiRefModel.getDataPowerSubSql(fiRefModel.getTableName(),
//                fiRefModel.getDataPowerColumn(), fiRefModel.getResourceID());
        LoginContext context = (LoginContext)getContext().getAttribute("key_private_context");
        Object[] pkOrgs = null;
        if (isPkOrgSameAssumeOrg() && getExcludeAssumeOrgRefList().contains(fieldCode)) {
            //�߹���Ȩ��
            pkOrgs = context.getPkorgs();
        } else {
//            if (dataPower == null) {
//                pkOrgs = context.getPkorgs();
//            } else {
                @SuppressWarnings("rawtypes")
                Vector vecFiOrg = fiRefModel.getData();
                fiRefModel.setSelectedData(vecFiOrg);
                pkOrgs = fiRefModel.getValues("pk_financeorg", true);
//            }
        }
        String[] orgArray = null;
        if (pk_orgs != null && pk_orgs.length > 0) {
            orgArray = swap(pk_orgs[0], pkOrgs);
        } else if (pkOrgs != null && pkOrgs.length > 0) {
            orgArray = swap(pkOrgs[0].toString(), pkOrgs);
        }
        return orgArray;
	}

    private String[] swap(String pkOrg, Object[] pkOrgs) {
        String[] pksOrg = null;
        if (pkOrgs != null) {
            pksOrg = new String[pkOrgs.length];
            for (int nPos = 0; nPos < pkOrgs.length; nPos++) {
                pksOrg[nPos] = (String)pkOrgs[nPos];
                if (pksOrg[nPos].equals(pkOrg)){
                    String tmp = (String)pksOrg[0];
                    pksOrg[0] = pksOrg[nPos];
                    pksOrg[nPos] = tmp;
                }
            }
        }
        return pksOrg;
    }
    
	@Override
	protected void resetTemplateField(CriteriaChangedEvent evt) {
		if(evt.getCriteriaEditor()!=null){
		    List<UIRefPane> listRefPane = BXQryTplUtil.getRefPaneListByFieldCode(evt,
                    evt.getFieldCode());
		    if (listRefPane != null && listRefPane.size() > 0) {
		        for (UIRefPane refPane : listRefPane) {
		            String[] pkOrgs = getPk_org();
	                
	                AbstractRefModel refModel = refPane.getRefModel();
	                if (refModel != null) {
	                    LoginContext context = (LoginContext)getContext().getAttribute("key_private_context");
	                    if (!ArrayUtils.isEmpty(pkOrgs)) {
	                        refModel.setPk_org(pkOrgs[0]);
	                    }
	                    if (refModel instanceof ErmTrantypeRefModel
	                            && StringUtils.isNotEmpty(djlx)) {
	                        String[] djlxArr = djlx.split(",");
	                        try {
	                            String billtypeWhere = SqlUtils.getInStr("parentbilltype", djlxArr);
                                refModel.setWherePart(billtypeWhere);
                            } catch (BusinessException e) {
                                Logger.error(e.getMessage(), e);
                            }
	                    }

	                    String[] orgArray = parseDataPowerOrgs(pkOrgs, evt.getFieldCode());
	                    configDataPowerRef(refPane, orgArray);
	                    
	                    if (refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel &&
	                            evt.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
	                        if (isPkOrgSameAssumeOrg() && getExcludeAssumeOrgRefList().contains(evt.getFieldCode())) {
	                            refModel.setFilterPks(context.getPkorgs());   
	                        }
	                        refPane.setMultiCorpRef(false);
	                    }
	                    refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // ����Ȩ�޿���,
	                }
		        }
		    }
            
            if ("pk_payorg".equals(evt.getFieldCode())) {
                //֧����λ
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludePayOrgRefList());
            } else if ("fydwbm".equals(evt.getFieldCode()) || 
                    "assume_org".equals(evt.getFieldCode())) {
                //���óе���λ
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeAssumeOrgRefList());
            } else if ("dwbm".equals(evt.getFieldCode())) {
                //����˵�λ
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeJkOrgRefList());
            } else if ("bx_dwbm".equals(evt.getFieldCode())) {
                //�����˵�λ
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeBxOrgRefList());
            } else if (getExcludePayOrgRefList().contains(evt.getFieldCode())) {
                //֧����λ
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "pk_payorg", getPk_org(evt, "pk_payorg"));
            } else if (getExcludeAssumeOrgRefList().contains(evt.getFieldCode())) {
                //���óе���λ
                String[] pkOrgs = getPk_org(evt, "fydwbm");
                if (ArrayUtil.isEmpty(pkOrgs)) {
                    pkOrgs = getPk_org(evt, "assume_org");
                    BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "assume_org", pkOrgs);
                } else {
                    BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "fydwbm", pkOrgs);
                }
            } else if (getExcludeJkOrgRefList().contains(evt.getFieldCode())) {
                //����˵�λ
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "dwbm",getPk_org(evt, "dwbm"));
            } else if (getExcludeBxOrgRefList().contains(evt.getFieldCode())) {
                //�����˵�λ
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "bx_dwbm",getPk_org(evt, "bx_dwbm"));
            }
//            if (BX_PK_ORG.equals(evt.getFieldCode())) {
//				//�༭������λʱ�����������֯��صĲ�ѯ�������ݴ˹���
//				BXQryTplUtil.orgCriteriaChanged(evt, getBusTypeVO().getPayentity_billitems());
//			}else if(BX_FYDWBM.equals(evt.getFieldCode())){
//				//�༭���óе���λʱ�����������֯��صĲ�ѯ�������ݴ˹���
//				BXQryTplUtil.orgCriteriaChanged(evt, getBusTypeVO().getCostentity_billitems());
//			}else if(BX_DWBM.equals(evt.getFieldCode())){
//				//�༭�����˵�λʱ�����������֯��صĲ�ѯ�������ݴ˹���
//				BXQryTplUtil.orgCriteriaChanged(evt, getBusTypeVO().getUseentity_billitems());
//			}else if (getBusTypeVO().getPayentity_billitems().contains(evt.getFieldCode())) {
//				//�༭������λ����ֶ�ʱ������ֶ�������֯Ϊ������λ
//				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,BX_PK_ORG,getPk_org());
//			}else if (getBusTypeVO().getCostentity_billitems().contains(evt.getFieldCode())) {
//				//�༭���óе���λ����ֶ�ʱ������ֶ�������֯Ϊ���óе���λ
//				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,BX_FYDWBM,null);
//			}else if (getBusTypeVO().getUseentity_billitems().contains(evt.getFieldCode())) {
//				//�༭�����˵�λ����ֶ�ʱ������ֶ�������֯Ϊ�����˵�λ
//				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,BX_DWBM,null);
//            }
		}
	}

	/**
	 * ���������֯�ֶι��˵��ֶ�[key=��֯��value=�����ֶ�]
	 */
	protected Map<String, List<String>> orgRefFieldsMap = new HashMap<String, List<String>>();

	protected List<String> getOrgRefFields(String orgField) {
		if(!orgRefFieldsMap.containsKey(orgField)){
			List<String> refFields = new ArrayList<String>();
			if (BX_PK_ORG.equals(orgField)) {
				refFields = ErmBillTypeUtil.getBusTypeVO(null, BXConstans.JK_DJDL).getPayentity_billitems();
			} else if (BX_FYDWBM.equals(orgField)) {
				refFields = ErmBillTypeUtil.getBusTypeVO(null, BXConstans.JK_DJDL).getCostentity_billitems();
			} else if (BX_DWBM.equals(orgField)) {
				refFields = ErmBillTypeUtil.getBusTypeVO(null, BXConstans.JK_DJDL).getUseentity_billitems();
			}
			orgRefFieldsMap.put(orgField, refFields);
		}
		return orgRefFieldsMap.get(orgField);

	}

	/**
	 * ���ر��������ѯ�Ի�����֯����ֶ�
	 * @return
	 */
	protected List<String> getOrgFieldList(){
		return Arrays.asList(new String[]{BX_PK_ORG,BX_FYDWBM,BX_DWBM});
	}

	/**
	 * �����������ʱ��ѯ�Ի����ѯ�������л���ק�¼�
	 * @param event
	 */
	protected void orgCriteriaChangedEvent(CriteriaChangedEvent evt,List<String> orgRelKeyList) throws BusinessException{
		if(BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt) != null
				&&BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt).length>0){
			String pk_org = BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt)[0];
			for (String key : orgRelKeyList) {
				BXQryTplUtil.setOrgForField(evt.getCriteriaEditor(), key, pk_org);
			}
		}
	}

	/**
	 * ���ܣ����ó��ò�ѯ����VO
	 */
	protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		//
	}

	/**
	 * ���ܣ��õ���ѯ��֯<br>
	 * Ĭ�ϻ���Ϊ��ʱ�����ص�ǰ��½��֯ ��ʱע��<br>
	 *
	 * @return
	 */
	protected String[] getPk_org() {
		UIRefPane refpanel = ((UIRefPane) getComponent(FINANCIAL_ORG_REF));
		if(refpanel==null){
			return null;
		}
		return ((UIRefPane) getComponent(FINANCIAL_ORG_REF)).getRefPKs();
	}
	
	protected String[] getPk_org(CriteriaChangedEvent evt, String fieldOrg) {
	    UIRefPane refpanel = ((UIRefPane) BXQryTplUtil.getRefPaneByFieldCode(evt, fieldOrg));
        if(refpanel==null){
            return null;
        }
        return ((UIRefPane) getComponent(FINANCIAL_ORG_REF)).getRefPKs();
	}

	protected String getDefaultOrg() {
		// ��ø��Ի���Ĭ������֯
		String defaultOrg = ErUiUtil.getBXDefaultOrgUnit();
		String pk_org = null;
		if (!StringUtils.isEmpty(defaultOrg)) {
			String[] values = ErUiUtil.getPermissionOrgs(getNodeCode());
			if (ArrayUtils.isEmpty(values)
					|| Arrays.asList(values).contains(defaultOrg)) {
				// ȡĬ����֯
				pk_org = defaultOrg;
			}
		}

		return pk_org;
	}

	/**
	 * ���ܣ����ò�����֯
	 *
	 * @return String[]
	 */
	@Override
    public void setPk_org(String[] pkOrgs) {
		super.setPk_org(pkOrgs);
		String pk_org = null;
		if (pkOrgs != null && pkOrgs.length > 0) {
			pk_org = pkOrgs[0];
		}
		String[] refKey = getOrgRelationField();
		UIRefPane ref = null;
		AbstractRefModel refModel;
		for (String key : refKey) {
			ref = (UIRefPane) (getComponent(key));
            refModel = ref.getRefModel();
			if (refModel != null) {
				ref.setPk_org(pk_org);
				String pk = ref.getRefPK();
				refModel.setMatchPkWithWherePart(true);
				refModel.setPKMatch(true);
				@SuppressWarnings("rawtypes")
                Vector data = ref.getRefModel().matchPkData(pk);
				if (data == null || data.isEmpty()) {
				    ref.setPK(null);
				}
			}
		}
	}
	
	/**
	 * ������Ҫ������֯���˲�ѯ����Ĳ�ѯ�����Ӧ��Ԫ����ID
	 * �˴���Ҫ��Ӧ�Ĵ˹��ܵ�ͨ��ҵ��Ԫ���˵��������Ӷ�Ӧ��Ԫ����id
	 *
	 * @return
	 */
	@Override
	protected List<String> getMultiOrgRef() {
		List<String> list = super.getMultiOrgRef();
		list.add(IPubReportConstants.BUSINESS_UNIT); // ������֯
		list.add(IBDMetaDataIDConst.DEPT); // ����
		list.add(IBDMetaDataIDConst.PSNDOC); // ��Ա
        list.add(IBDMetaDataIDConst.USER); // �û�
        list.add(IPubReportConstants.MDID_PROJECT); // ��Ŀ
        
        
		//v6.1�����ɱ�����
//		list.add(IPubReportConstants.MDID_COSTCENTER);
		return list;
	}
	
	protected List<String> getSingleOrgRef() {
	  List<String> list = new ArrayList<String>();
      // �ɱ�����
//      list.add(IPubReportConstants.MDID_COSTCENTER);
      list.add(IPubReportConstants.MDID_PCORG);
	  return list;
	}

}