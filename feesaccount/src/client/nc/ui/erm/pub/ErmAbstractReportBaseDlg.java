package nc.ui.erm.pub;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmBillTypeUtil;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefInitializeCondition;
import nc.ui.er.djlx.ErmTrantypeRefModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.comp.ReportNormalPanel;
import nc.ui.fipub.comp.ReportQryDlg;
import nc.ui.fipub.comp.ReportUiUtil;
import nc.ui.fipub.comp.editorandrender.QueryObjTableCellEditor;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.org.ref.LiabilityCenterDefaultRefModel;
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
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.erm.pub.ErmReportPubUtil;
import nc.vo.fipub.report.AggReportInitializeVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportInitializeItemVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.fipub.utils.RefConstant;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;

/**
 * 报销管理帐表查询对话框基类
 * @author chendya
 *
 */
@SuppressWarnings("restriction")
public abstract class ErmAbstractReportBaseDlg extends ReportQryDlg {

	private static final long serialVersionUID = 1416820562038177684L;

	protected static final String QRY_MODE_COMB = "qryModeComb"; // 查询方式
	protected static final String BILL_STATE_COMB = "billStateComb"; // 单据状态
	protected static final String BEGIN_TIME_REF = "beginTimeRef"; // 开始时间
	protected static final String END_TIME_REF = "endTimeRef"; // 结束时间
	protected static final String CURRENCY_REF = "currencyRef"; // 币种
	protected static final String FINANCIAL_ORG_REF = "financialOrgRef"; // 财务组织
	protected static final String TIME_TYPE_LABEL = "timeTypeLabel";
	
	private static final String BX_PK_ORG = "pk_org";
//	private static final String BX_DJLXBM = "djlxbm";
	private static final String BX_FYDWBM = "fydwbm";
	private static final String BX_DWBM = "dwbm";
	private BusiTypeVO busTypeVO;
    private String djlx;

    private boolean filterByFunPermLiabilityCenter = true;
    
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
		if (StringUtils.isEmpty(djlx)) {
		    this.djlx = parseDjlx(strNodeCode);
		} else {
	        this.djlx = djlx;
		}
		initialize();
	}
	
	private static final String[][] funccodes = new String[][] {
	  new String[]{"20110501", "263X"},//借款余额
	  new String[]{"20110502", "263X"},//借款明细
	  new String[]{"20110503", "263X"},//借款账龄
	  new String[]{"20110504", "262X,264X,265X,266X"},//费用汇总
	  new String[]{"20110505", "262X,264X,265X,266X"},//费用明细
	  new String[]{"20110506", null},//费用趋势图
	  new String[]{"20110507", "261X"}, //费用申请
	};

    private String parseDjlx(String nodeCode) {
        String sDjlx = null;
        for (int nPos = 0; nPos < funccodes.length; nPos++) {
            if (nodeCode.startsWith(funccodes[nPos][0])) {
                sDjlx = funccodes[nPos][1];
                break;
            }
        }
        return sDjlx;
    }
    
	private void initialize(){
		// 修改查询条件
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
							//锁定借款 报销单位
							vo.setIfImmobility(UFBoolean.TRUE);
						}
					}
				}
			}
		});
	}

	/**
	 * 返回查询对象中受财务组织影响的字段
	 * @return
	 */
	protected abstract String[] getOrgRelationField();

	@Override
    public String checkCondition() {
        String condition = null;
        String beanId = null;
        QueryTempletTotalVO totalVo = null;
        try {
            totalVo = getQryCondEditor().getTotalVO();
            if (totalVo != null && totalVo.getTempletVO() != null) {
                beanId = totalVo.getTempletVO().getMetaclass(); 
                totalVo.getTempletVO().setMetaclass(null);
            }
        } catch (BusinessException e) {
            Logger.error(e.getMessage(), e);
        }
        condition =  super.checkCondition();
        if (beanId != null && totalVo != null) {
//            totalVo.getTempletVO().setMetaclass(beanId);
        }
        return condition;
    }
	
	/**
	 * 业务单元变化监听器<br>
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
		//财务组织设置pk
		UIRefPane refPaneOrg = BXQryTplUtil.getRefPaneByFieldCode(this,BX_PK_ORG);
		if(refPaneOrg!=null){
			refPaneOrg.setPKs(newPk_orgs);
			List<IFilter> filtersByFieldCode = this.getFiltersByFieldCode(BX_PK_ORG);
			for(IFilter f:filtersByFieldCode){
				f.setFieldValue(null);
			}
		}
		
		//组织相关字段设置过滤pk
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
                if (!ArrayUtils.isEmpty(components)) {
                    for (Component component : components) {
                        if (component instanceof UIRefPane) {
                            UIRefPane refPane = (UIRefPane) component;
                            refModel = refPane.getRefModel();
                            if (refModel instanceof nc.ui.bd.ref.busi.UserDefaultRefModel ||
                                    refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel || 
                                    excludeOrgChange(filterMeta.getFieldCode())) {
                                if (refModel != null && !(isOrgRefModel(refModel))) {
                                    String[] orgArray = parseDataPowerOrgs(context.getPkorgs(), filterMeta.getFieldCode());
                                    if (isPkOrgSameAssumeOrg() && 
                                            getExcludeAssumeOrgRefList().contains(filterMeta.getFieldCode())) {
                                        orgArray = ErUtil.insertHeadOneOrg(newPk_orgs[0], orgArray);
                                    } else {
                                        RefInitializeCondition[] conditions = refPane.getRefUIConfig().getRefFilterInitconds();
                                        String defPkOrg = null;
                                        if (conditions != null) {
                                            for (RefInitializeCondition condition : conditions) {
                                                defPkOrg = condition.getDefaultPk();
                                                break;
                                            }
                                        }
                                        orgArray = ErUtil.insertHeadOneOrg(defPkOrg, orgArray);
                                    }
                                    configDataPowerRef(refPane, orgArray);
                                }
                                //费用账需要联动过滤费用承担单位区域字段
                                if (isPkOrgSameAssumeOrg() && 
                                        getExcludeAssumeOrgRefList().contains(filterMeta.getFieldCode()) && 
                                        refModel != null && 
                                        !isOrgRefModel(refModel)) {
                                    if (!ArrayUtils.isEmpty(newPk_orgs)) {
                                        refPane.setMultiCorpRef(false);
                                        refModel.setPk_org(newPk_orgs[0]);
                                        if (refModel instanceof nc.ui.resa.refmodel.CostCenterTreeRefModel) {
                                            CostCenterTreeRefModel costModel = (CostCenterTreeRefModel)refModel;
                                            costModel.setCurrentOrgCreated(false);
                                            costModel.setOrgType(CostCenterVO.PK_PROFITCENTER);
                                        }
                                    }
                                }
                            } else {
//                                refPane.setMultiCorpRef(false);
                                if (!ArrayUtils.isEmpty(newPk_orgs)) {
                                    refModel.setPk_org(newPk_orgs[0]);
                                    configDataPowerRef(refPane, new String[] { newPk_orgs[0]});
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
	
	private boolean isOrgRefModel(AbstractRefModel refModel) {
	    boolean isOrgRef = false;
	    if (refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel ||
	            refModel instanceof nc.ui.org.ref.BusinessUnitDefaultRefModel ||
	            refModel instanceof nc.ui.org.ref.AdminOrgDefaultRefModel) {
	        isOrgRef = true;
	    }
	    return isOrgRef;
	}
	
	private boolean pkOrgSameAssumeOrg = false;
	
	public boolean isPkOrgSameAssumeOrg() {
	    if (pkOrgSameAssumeOrg) {
            IReportQueryCond qryCond = getNormalPanel().getReportQueryCondVO();
            List<QryObj> qryObjList = qryCond.getQryObjs();
            if (!CollectionUtils.isEmpty(qryObjList)) {
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
        if (isPkOrgSameAssumeOrg() && getExcludeAssumeOrgRefList().contains(fieldCode)) {
            exclude = false;
        } else {
            if (getExcludePayOrgRefList().contains(fieldCode) ||
                    getExcludeAssumeOrgRefList().contains(fieldCode) ||
                    getExcludeJkOrgRefList().contains(fieldCode) ||
                    getExcludeBxOrgRefList().contains(fieldCode) ||
                    getExcludeCostCenterRefList().contains(fieldCode) ||
                    getExcludeLiabilityCenterRefList().contains(fieldCode)) {
                exclude = true;
            }
        }
	    
	    return exclude;
	}
	
	private static List<String> excludePayOrgField = new ArrayList<String>();

	private static List<String> getExcludePayOrgRefList() {
	    if (excludePayOrgField.isEmpty()) {
	        excludePayOrgField.add("pk_payorg");
	        excludePayOrgField.add("fkyhzh");//单位银行账户
	        excludePayOrgField.add("cashproj");
	        excludePayOrgField.add("cashitem");
	        excludePayOrgField.add("pk_cashaccount");

            excludePayOrgField.add("zb.pk_payorg");
            excludePayOrgField.add("zb.fkyhzh");//单位银行账户
            excludePayOrgField.add("zb.cashproj");
            excludePayOrgField.add("zb.cashitem");
            excludePayOrgField.add("zb.pk_cashaccount");
	    }
//	    if (excludePayOrgRefModel.isEmpty()) {
//	        excludePayOrgRefModel.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//财务组织
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.BankaccSubDefaultRefModel");//使用权参照
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.FundPlanDefaultRefModel");//资金计划项目
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.CashflowDefaultRefModel");//现金流量项目
//	        excludePayOrgRefModel.add("nc.ui.bd.ref.model.CashAccountRefModel");//现金账户
//	    }
	    return excludePayOrgField;
	}
	
	private static List<String> excludeApplyOrgField = new ArrayList<String>();

    private static List<String> getExcludeApplyOrgRefList() {
        if (excludeApplyOrgField.isEmpty()) {
            excludeApplyOrgField.add("zb.apply_org");
            excludeApplyOrgField.add("zb.apply_dept");
            excludeApplyOrgField.add("zb.billmaker");
        }
        return excludeApplyOrgField;
    }
    
	private static List<String> excludeCostCenter = new ArrayList<String>();
	
	private static List<String> getExcludeCostCenterRefList() {
        if (excludeCostCenter.isEmpty()) {
            excludeCostCenter.add("pk_resacostcenter");
//
            excludeCostCenter.add("zb.pk_resacostcenter");
        }

        return excludeCostCenter;
    }
	
    private static List<String> excludeLiabilityCenter = new ArrayList<String>();
    
    private static List<String> getExcludeLiabilityCenterRefList() {
        if (excludeLiabilityCenter.isEmpty()) {
            excludeLiabilityCenter.add("pk_pcorg");//利润中心
            excludeLiabilityCenter.add("zb.pk_pcorg");
        }

        return excludeLiabilityCenter;
    }	
    
    private static List<String> excludeAssumeOrgRefField = new ArrayList<String>();

    private static List<String> getExcludeAssumeOrgRefList() {
        if (excludeAssumeOrgRefField.isEmpty()) {
            excludeAssumeOrgRefField.add("fydwbm");
            excludeAssumeOrgRefField.add("assume_org");
            excludeAssumeOrgRefField.add("fydeptid");
            excludeAssumeOrgRefField.add("assume_dept");
//            excludeAssumeOrgRefField.add("pk_resacostcenter");
            excludeAssumeOrgRefField.add("szxmid");//收支项目
            excludeAssumeOrgRefField.add("pk_iobsclass");//收支项目
            excludeAssumeOrgRefField.add("jobid");//项目
            excludeAssumeOrgRefField.add("pk_project");//项目
            excludeAssumeOrgRefField.add("projecttask");//项目任务
            excludeAssumeOrgRefField.add("pk_wbs");//项目任务
            excludeAssumeOrgRefField.add("hbbm");//供应商
            excludeAssumeOrgRefField.add("pk_supplier");//供应商
            excludeAssumeOrgRefField.add("customer");//客户
            excludeAssumeOrgRefField.add("pk_customer");//客户
            
            excludeAssumeOrgRefField.add("zb.fydwbm");
            excludeAssumeOrgRefField.add("zb.assume_org");
            excludeAssumeOrgRefField.add("zb.fydeptid");
            excludeAssumeOrgRefField.add("zb.assume_dept");
//            excludeAssumeOrgRefField.add("zb.pk_resacostcenter");
            excludeAssumeOrgRefField.add("zb.szxmid");//收支项目
            excludeAssumeOrgRefField.add("zb.pk_iobsclass");//收支项目
            excludeAssumeOrgRefField.add("zb.pk_wbs");//项目任务
            excludeAssumeOrgRefField.add("zb.jobid");//项目
            excludeAssumeOrgRefField.add("zb.pk_project");//项目
            excludeAssumeOrgRefField.add("zb.projecttask");//项目任务
            excludeAssumeOrgRefField.add("zb.hbbm");//供应商
            excludeAssumeOrgRefField.add("zb.pk_supplier");//供应商
            excludeAssumeOrgRefField.add("zb.customer");//客户
            excludeAssumeOrgRefField.add("zb.pk_customer");//客户
        }
//        if (excludeAssumeOrgRefM.isEmpty()) {
//            excludeAssumeOrgRefM.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//财务组织
//            excludeAssumeOrgRefM.add("nc.ui.org.ref.DeptDefaultRefModel");//部门
//            excludeAssumeOrgRefM.add("nc.ui.resa.refmodel.CostCenterTreeRefModel");//成本中心
//            excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.InoutBusiClassDefaultRefModel");//收支项目
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
//            excludeJkOrgRefField.add("pk_project");
//            excludeJkOrgRefField.add("pk_wbs");
//            excludeJkOrgRefField.add("pk_supplier");

            excludeJkOrgRefField.add("zb.dwbm");
            excludeJkOrgRefField.add("zb.deptid");
            excludeJkOrgRefField.add("zb.jkbxr");
            excludeJkOrgRefField.add("zb.skyhzh");
//            excludeJkOrgRefField.add("zb.pk_project");
//            excludeJkOrgRefField.add("zb.pk_wbs");
//            excludeJkOrgRefField.add("zb.pk_supplier");
        }
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//财务组织
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.DeptDefaultRefModel");//部门
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsndocDefaultRefModel");//人员
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsnbankaccDefaultRefModel");//个人银行账户
//      excludeAssumeOrgRefM.add("nc.ui.pmpub.ref.ProjectDefaultRefModel");//项目
//      excludeAssumeOrgRefM.add("nc.ui.pmpub.ref.WBSDefaultRefModel");//项目工作分解结构(WBS)
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.SupplierDefaultRefModel");//供应商档案
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
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.FinanceOrgDefaultRefTreeModel");//报销人单位
//      excludeAssumeOrgRefM.add("nc.ui.org.ref.DeptDefaultRefModel");//部门
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsndocDefaultRefModel");//人员
//      excludeAssumeOrgRefM.add("nc.ui.bd.ref.model.PsnbankaccDefaultRefModel");//个人银行账户
        return excludeBxOrgRefField;
    }
    
	@Override
	protected String doBusiCheck() {
		// ①执行父类校验
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}

		try {
			errMsg = "";
	        ReportQueryCondVO queryCondVO = (ReportQueryCondVO) super.getReportQueryCondVO();
	        String currencyType = queryCondVO.getLocalCurrencyType();
			// ②财务组织不允许为空
			String[] pk_orgs = getPk_org();
			if (ArrayUtils.isEmpty(pk_orgs) || StringUtils.isEmpty(pk_orgs[0])) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0028")/*@res "财务组织不允许为空"*/;
			} else if (pk_orgs.length > 1 && "org_local".equals(currencyType)) {
				// ③财务组织本位币必须一致
				Set<String> localCurrtype = new HashSet<String>();
				CurrtypeVO[] currtypeVOs = ErmReportPubUtil.getLocalCurrencyByOrgID(pk_orgs);
				for (CurrtypeVO vo : currtypeVOs) {
					localCurrtype.add(vo.getPk_currtype());
				}
				if (localCurrtype.size() > 1) {
					errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0029")/*@res "\r\n多个查询组织的本位币不一致，请分别查询！"*/;
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

	public String convertQueryTemplateSql(Map<String, Object> fieldMap, String oriSql) {
		if (StringUtils.isEmpty(oriSql)) {
			return "";
		}
		if(getSysCode()==ErmReportQryDlg.ERM_MATTERAPP){
			return oriSql;
		}

		Map<String, String> filters = new HashMap<String, String>();
		//兼容元数据
		if(getSysCode()==ErmReportQryDlg.ERM_EXPDETAIL){
			filters.put("er_expenseaccount.", "zb.");
		}else if(ErmReportQryDlg.ERM_EXPBAL == getSysCode()){
			filters.put("er_expensebal.", "zb.");
		}else {
			filters.put("er_bxzb.", "zb.");
			filters.put("er_jkzb.", "zb.");
		}
		
//        Map<String, String> tableAlasMap = new HashMap<String, String>();
//        tableAlasMap.put("er_expenseaccount", "zb");
//        tableAlasMap.put("er_expensebal", "zb");
//        tableAlasMap.put("er_bxzb", "zb");
//        tableAlasMap.put("er_jkzb", "zb");
//        tableAlasMap.put("er_busitem", "fb");

		Map<String, String> tableAlas = new HashMap<String, String>();
		Set<Entry<String, String>> entrySet = filters.entrySet();
		for (Entry<String, String> entry : entrySet) {
			if(oriSql.contains(entry.getKey())){
				tableAlas.put(entry.getKey(), entry.getValue());
			}
		}
		if(tableAlas.isEmpty() && !fieldMap.isEmpty()){
		    Iterator<Entry<String, Object>> iter = fieldMap.entrySet().iterator();
		    while (iter.hasNext()) {
		        Entry<String, Object> entry = iter.next();
//		        String[] nameArr = entry.getKey().split("\\.");
//		        String table = nameArr[0];
		        oriSql = oriSql.replaceAll(entry.getKey(), "zb." + entry.getKey());
		    }
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
//			getQryCondEditor().setPowerEnable(false);
			String sqlWhere = null;
			if(getSysCode()==ErmReportQryDlg.ERM_MATTERAPP){
				sqlWhere = getQryCondEditor().getQueryScheme()
				.getWhereSQLOnly();
			}else {
				sqlWhere = getQryCondEditor().getQueryScheme()
				.getTableListFromWhereSQL().getWhere();
			}

	        Map<String, Object> fieldMap = new HashMap<String, Object>();
	        List<IFilterEditor> simpleEditorFilterEditors = getSimpleEditorFilterEditors();
	        for(IFilterEditor editor: simpleEditorFilterEditors){
	            DefaultFilterEditor filterEditor = (DefaultFilterEditor) editor;
	            if (filterEditor.getFilter().getSqlString() != null) {
	                fieldMap.put(filterEditor.getFilterMeta().getFieldCode(), 
	                        filterEditor.getFilter().getSqlString());	                
	            }
	        }
	        queryCondVO.getUserObject().put("fieldSqlMap", fieldMap);
	        queryCondVO.getUserObject().put("isPkorgSameAssumeOrg", Boolean.valueOf(isPkOrgSameAssumeOrg()));
            queryCondVO.setWhereSql(convertQueryTemplateSql(fieldMap, sqlWhere));
	        
			setQueryCond(queryCondVO);
		} catch (InvalidAccperiodExcetion e) {
			Logger.error(e.getMessage(), e, this.getClass(), "getReportQueryCondVO");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0022")/*@res "获取界面查询条件异常。"*/);
		} finally{
//			getQryCondEditor().setPowerEnable(true);
		}

		return queryCondVO;
	}
	
    protected Dimension labelSize = new Dimension(110, 20);
    protected Dimension compSize = new Dimension(105, 20);
    
    protected int qryObjTblRowCount = 5; // 查询对象选择表行数，暂时支持5个查询对象选择

	protected void initUINormalPanel() throws BusinessException {
        // 为方便起见，单独维护一个类ReportNormalPanel，包括对话框右侧的两个面板内容
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
                            String[] orgArray = null;
                            QryObj qryObj = qryObjList.get(i);
                            AbstractRefModel refModel = refPane.getRefModel();
                            if (refModel instanceof nc.ui.bd.ref.busi.UserDefaultRefModel ||
                                    (!"261X".equals(djlx) && refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel)) {
                                orgArray = parseDataPowerOrgs(pk_orgs, qryObj.getOriginFld());
                            } else if (pk_orgs != null && pk_orgs.length > 0){
                                orgArray = pk_orgs;
                            } else {
                                orgArray = parseDataPowerOrgs(pk_orgs, qryObj.getOriginFld());
                            }
                            configDataPowerRef(refPane, orgArray);
                            super.setPk_org(orgArray);
                            
                            if (RefConstant.REF_NODENAME_COSTCENTER.equals(refModel.getRefNodeName())) {
                                refModel.addWherePart(null);
                            }
                            
                            if (refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel) {
                                String powerSql = refModel.getDataPowerSubSql(refModel.getTableName(),
                                        refModel.getDataPowerColumn(), refModel.getResourceID());
                                if (powerSql == null) {
                                    refPane.setDataPowerOperation_code(null);
                                }
                            }
                        } else {
                            refPane.setMultiCorpRef(false);
                            super.setPk_org(pk_orgs);
                        }
                    }
                }
            }

            private AggReportInitializeVO reportVo;
            
            @Override
            public AggReportInitializeVO getAllQueryObj(String nodeCode)
                    throws BusinessException {
                if (reportVo == null) {
                    reportVo = super.getAllQueryObj(nodeCode);
                    String name;
                    String headPrefix = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("reportqueryobj","2repqryobj-0023");
                    String bodyPrefix = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("reportqueryobj","2repqryobj-0024");
                    for (ReportInitializeItemVO item : (ReportInitializeItemVO[])reportVo.getChildrenVO()) {
                        if (StringUtils.isNotBlank(item.getResid())) {
                            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap", item.getResid());
                        } else {
                            name = null;
                        }
                        String dspName = item.getDsp_objname();
                        if (StringUtils.isNotEmpty(name)) {
                            item.setDsp_objname(name);
                        } else if (StringUtils.isNotEmpty(dspName)){
                            if (dspName.startsWith(headPrefix)) {
                                item.setDsp_objname(dspName.substring(headPrefix.length()));
                            } else if (dspName.startsWith(bodyPrefix)) {
                                item.setDsp_objname(dspName.substring(bodyPrefix.length()));
                            }
                            
                        }
                    }
                }
                return reportVo;
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
	    if (model instanceof LiabilityCenterDefaultRefModel) {
	        refPane.setMultiCorpRef(false);
	    } else if (model instanceof CostCenterTreeRefModel) {
	        model.setFilterRefNodeName(new String[]{"利润中心"/* -=notranslate=- */});
	        CostCenterTreeRefModel costModel = (CostCenterTreeRefModel)model;
            costModel.setCurrentOrgCreated(false);
            costModel.setOrgType(CostCenterVO.PK_PROFITCENTER);
	        refPane.setMultiCorpRef(true);
	    } else if (model instanceof nc.ui.org.ref.BusinessUnitDefaultRefModel ||
	            model instanceof nc.ui.org.ref.AdminOrgDefaultRefModel ||
	            model instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel) {
            refPane.setMultiCorpRef(false);
            model.setFilterRefNodeName(null);
	    } else {
	        refPane.setMultiCorpRef(true);
	    }
	    boolean needMatch = false;
	    if (model instanceof CostCenterTreeRefModel || 
                model instanceof LiabilityCenterDefaultRefModel) {
	        needMatch = true;
        }
        refPane.setMultiOrgSelected(true);
        refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
        refPane.setMultiRefFilterPKs(pk_orgs);
        
        RefInitializeCondition[] conditions = refPane.getRefUIConfig().getRefFilterInitconds();
        if (conditions != null) {
            for (RefInitializeCondition condition : conditions) {
                condition.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
                if (pk_orgs != null && pk_orgs.length > 0 && 
                        (needMatch && matchedLiabilityCenter(pk_orgs[0]) || 
                                !needMatch)) {
                    condition.setDefaultPk(pk_orgs[0]);
                } else {
                    condition.setDefaultPk(Long.toString(System.currentTimeMillis()));
                }
            }
        }
	}
	
//	private String[] setDefPkOrgFirst(String defPk, String[] pkOrgs) {
//	    String[] results = null;
//	    if (StringUtil.isEmpty(defPk) || pkOrgs == null || pkOrgs.length == 0) {
//	        results = pkOrgs;
//	    } else {
//	        for (String pkOrg : pkOrgs) {
//	            if (defPk.equals(pkOrg)) {
//	                results = swap(pkOrg, pkOrgs);
//	                break;
//	            }
//	        }
//	        results = (results == null ? pkOrgs : results);
//	    }
//	    return results;
//	}
	
	private boolean matchedLiabilityCenter(String pkOrg) {
	    LiabilityCenterDefaultRefModel refModel = new LiabilityCenterDefaultRefModel();
        refModel.setPk_org(pkOrg);
        refModel.setMatchPkWithWherePart(true);
        refModel.setPKMatch(true);
        @SuppressWarnings("rawtypes")
        Vector vec = refModel.matchPkData(pkOrg);
        return !(vec == null || vec.isEmpty());
	}
	
	private String[] parseDataPowerOrgs(String[] pk_orgs, String fieldCode) {
        LoginContext context = (LoginContext)getContext().getAttribute("key_private_context");
        Object[] pkOrgs = null;
        if (isPkOrgSameAssumeOrg() && !getExcludeCostCenterRefList().contains(fieldCode.toLowerCase()) 
                && getExcludeAssumeOrgRefList().contains(fieldCode.toLowerCase())) {
            //走功能权限
            pkOrgs = context.getPkorgs();
        } else if (getExcludeCostCenterRefList().contains(fieldCode.toLowerCase()) && filterByFunPermLiabilityCenter) {
            pkOrgs = context.getPkorgs();
        } else if (getExcludeLiabilityCenterRefList().contains(fieldCode.toLowerCase()) && filterByFunPermLiabilityCenter) {
            pkOrgs = context.getPkorgs();
        } else {
            FinanceOrgDefaultRefTreeModel fiRefModel = new FinanceOrgDefaultRefTreeModel();
            fiRefModel.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
            String dataPower = fiRefModel.getDataPowerSubSql(fiRefModel.getTableName(),
                    fiRefModel.getDataPowerColumn(), fiRefModel.getResourceID());
            if (dataPower == null) {
                pkOrgs = context.getPkorgs();
            } else {
                @SuppressWarnings("rawtypes")
                Vector vecFiOrg = fiRefModel.getData();
                fiRefModel.setSelectedData(vecFiOrg);
                pkOrgs = fiRefModel.getValues("pk_financeorg", true);
            }
        }
        if (pkOrgs == null || pkOrgs.length == 0) {
            pkOrgs = new Object[] { Long.toString(System.currentTimeMillis()) };
        }
        String[] orgArray = null;
        if (pk_orgs != null && pk_orgs.length > 0) {
            if (hasPerm(pk_orgs[0], pkOrgs)) {
                orgArray = ErUtil.insertHeadOneOrg(pk_orgs[0], pkOrgs);
            } else {
                orgArray = swap(pkOrgs[0].toString(), pkOrgs);;
            }
        } else if (pkOrgs != null && pkOrgs.length > 0) {
            orgArray = swap(pkOrgs[0].toString(), pkOrgs);
        }
        return orgArray;
	}
	
	protected boolean hasPerm(String pkOrg, Object[] permOrgs) {
	    boolean hasPerm = false;
	    if (permOrgs != null && permOrgs.length > 0) {
	        for (Object permOrg : permOrgs) {
	            if (permOrg.equals(pkOrg)) {
	                hasPerm = true;
	                break;
	            }
	        }
	    }
	    return hasPerm;
	}
	

    protected String[] swap(String pkOrg, Object[] pkOrgs) {
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
	        String key = PsnVoCall.FIORG_PK_ + ErUiUtil.getPk_psndoc() + ErUiUtil.getPK_group();
            String fiorg = (String) WorkbenchEnvironment.getInstance().getClientCache(key); // 人员所属组织
            String defPkOrg = StringUtils.isEmpty(ReportUiUtil.getDefaultOrgUnit()) ? fiorg : ReportUiUtil.getDefaultOrgUnit();
            List<UIRefPane> listRefPane = BXQryTplUtil.getRefPaneListByFieldCode(evt,
                    evt.getFieldCode());
		    if (listRefPane != null && listRefPane.size() > 0) {
		        for (UIRefPane refPane : listRefPane) {
		            String[] pkOrgs = getPk_org();
	                AbstractRefModel refModel = refPane.getRefModel();
	                if (refModel != null) {
	                    LoginContext context = (LoginContext)getContext().getAttribute("key_private_context");
	                    if (excludeOrgChange(evt.getFieldCode())) {
	                        //不与主组织联动
                            pkOrgs = null;
	                    } else if (!ArrayUtils.isEmpty(pkOrgs)) {
	                        refModel.setPk_org(pkOrgs[0]);
	                    }
	                    if (refModel instanceof ErmTrantypeRefModel
	                            && StringUtils.isNotEmpty(djlx)) {
	                        if (!isPkOrgSameAssumeOrg() && djlx.indexOf("264X") >= 0) {
                                ((ErmTrantypeRefModel)refModel).setDjlx("264X");
	                        } else {
	                            ((ErmTrantypeRefModel)refModel).setDjlx(djlx);
	                        }
	                    }

                        String[] orgArray = parseDataPowerOrgs(pkOrgs, evt.getFieldCode());
                        if (refModel instanceof nc.ui.bd.ref.busi.UserDefaultRefModel) {
                            orgArray = ErUtil.insertHeadOneOrg(context.getPk_group(), orgArray);
                        }

                        if (evt.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
                            if (pkOrgs != null && pkOrgs.length > 0 && 
                                    isPkOrgSameAssumeOrg() && 
                                    getExcludeAssumeOrgRefList().contains(evt.getFieldCode().toLowerCase())) {
                                orgArray = ErUtil.insertHeadOneOrg(pkOrgs[0], pkOrgs);
                            } else {
                                if (hasPerm(defPkOrg, getLoginContext().getPkorgs())) {
                                    orgArray = ErUtil.insertHeadOneOrg(defPkOrg, orgArray);
                                }                                
                            }
                            if (refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel) {
                                String powerSql = refModel.getDataPowerSubSql(refModel.getTableName(),
                                        refModel.getDataPowerColumn(), refModel.getResourceID());
                                if (powerSql == null) {
                                    refPane.setDataPowerOperation_code(null);
                                }
                            }
                        } else if (evt.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
                            orgArray = ErUtil.insertHeadOneOrg(refModel.getPk_org(), orgArray);
                        }
                        configDataPowerRef(refPane, orgArray);
                        if (!hasPerm(refModel.getPk_org(), orgArray)) {
                            refModel.setPk_org(orgArray[0]);
                        }
	                    
	                    if ((isOrgRefModel(refModel) ||
	                            refModel instanceof LiabilityCenterDefaultRefModel) &&
	                            evt.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
	                        if (isPkOrgSameAssumeOrg() && getExcludeAssumeOrgRefList().contains(evt.getFieldCode().toLowerCase())) {
	                            refModel.setFilterPks(context.getPkorgs());   
	                        } else if (getExcludeLiabilityCenterRefList().contains(evt.getFieldCode().toLowerCase()) && 
	                                filterByFunPermLiabilityCenter) {
                                refModel.setFilterPks(context.getPkorgs());
	                        } else {
	                            refModel.setFilterPks(orgArray);
	                        }
	                        refPane.setMultiCorpRef(false);
	                    }
	                    refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
	                }
		        }
		    }
		    
		    if ("zb.apply_org".equals(evt.getFieldCode())) {
		        //申请单位
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeApplyOrgRefList());
		    } else if ("pk_payorg".equals(evt.getFieldCode())) {
                //支付单位
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludePayOrgRefList());
            } else if ("fydwbm".equals(evt.getFieldCode()) || 
                    "assume_org".equals(evt.getFieldCode())) {
                //费用承担单位
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeAssumeOrgRefList());
            } else if ("dwbm".equals(evt.getFieldCode())) {
                //借款人单位
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeJkOrgRefList());
            } else if ("bx_dwbm".equals(evt.getFieldCode())) {
                //报销人单位
                BXQryTplUtil.orgCriteriaChanged(evt, getExcludeBxOrgRefList());
            } else if (getExcludeApplyOrgRefList().contains(evt.getFieldCode())) {
                //申请单位
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "zb.apply_org", getPk_org(evt, "zb.apply_org"));
            } else if (getExcludePayOrgRefList().contains(evt.getFieldCode())) {
                //支付单位
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "pk_payorg", getPk_org(evt, "pk_payorg"));
            } else if (getExcludeAssumeOrgRefList().contains(evt.getFieldCode())) {
                //费用承担单位
                String[] pkOrgs = getPk_org(evt, "fydwbm");
                if (ArrayUtils.isEmpty(pkOrgs)) {
                    pkOrgs = getPk_org(evt, "assume_org");
                    BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "assume_org", pkOrgs);
                } else {
                    BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "fydwbm", pkOrgs);
                }
            } else if (getExcludeJkOrgRefList().contains(evt.getFieldCode())) {
                //借款人单位
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "dwbm",getPk_org(evt, "dwbm"));
            } else if (getExcludeBxOrgRefList().contains(evt.getFieldCode())) {
                //报销人单位
                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "bx_dwbm",getPk_org(evt, "bx_dwbm"));
            } else if (getExcludeLiabilityCenterRefList().contains(evt.getFieldCode().toLowerCase())) {
                //利润中心
//                BXQryTplUtil.orgRelFieldCriteriaChanged(evt, "bx_dwbm",getPk_orgs(evt, "pk_pcorg"));
                handleLiabilityCenterChange(evt);
            } else if (getExcludeCostCenterRefList().contains(evt.getFieldCode().toLowerCase())) {
                //成本中心 
                handleCostCenterChange(evt);
            }
            if (listRefPane != null && listRefPane.size() > 0) {
                for (UIRefPane refPane : listRefPane) {
                    AbstractRefModel refModel = refPane.getRefModel();
                    if (refPane.getRefModel() instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel) {
                        String powerSql = refModel.getDataPowerSubSql(refModel.getTableName(),
                                refModel.getDataPowerColumn(), refModel.getResourceID());
                        if (powerSql == null) {
                            refPane.setDataPowerOperation_code(null);
                        }
                    }
                }
            }
            handleRemoveEvent(evt, listRefPane);
		}
	}
	
	private void handleRemoveEvent(CriteriaChangedEvent evt, List<UIRefPane> listRefPane) {
	    if (evt.getEventtype() != CriteriaChangedEvent.FILTER_REMOVED) {
            return;
        }
	    String fldCode = evt.getFieldCode();
	    if (getExcludeJkOrgRefList().contains(fldCode)) {
	        handleInitRefModel(evt, getExcludeJkOrgRefList(), fldCode);
	    } else if (getExcludeBxOrgRefList().contains(fldCode)) {
	        handleInitRefModel(evt, getExcludeBxOrgRefList(), fldCode);
	    }
	}
	
	private void handleInitRefModel(CriteriaChangedEvent evt, 
	        List<String> excludeField, String orgField) {
	    if (excludeField == null || excludeField.isEmpty() || StringUtils.isEmpty(orgField)) {
	        return;
	    }
	    for (String item : excludeField) {
            List<UIRefPane> listRefPane = BXQryTplUtil.getRefPaneListByFieldCode(evt,
                    item);
            if (listRefPane == null || listRefPane.isEmpty()) {
                continue;
            }
            for (UIRefPane refPane : listRefPane) {
                if (isOrgRefModel(refPane.getRefModel())) {
                    break;
                }
                refPane.setMultiCorpRef(true);
            }
        }
	}
	

	private void handleLiabilityCenterChange(CriteriaChangedEvent evt) {
	    String[] pks = getRefPks(evt, evt.getFieldCode());
        List<UIRefPane> refpanelList = BXQryTplUtil.getRefPaneListByFieldCode(evt, "pk_resacostcenter");
        String costCenterFieldCode = "pk_resacostcenter";
        if (refpanelList == null || refpanelList.size() == 0) {
            refpanelList = BXQryTplUtil.getRefPaneListByFieldCode(evt, "zb.pk_resacostcenter");
            costCenterFieldCode = "zb.pk_resacostcenter";
        }
        
        if (refpanelList != null) {
            if (evt.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
                initCostshareRefPanel(evt, ExpenseBalVO.PK_RESACOSTCENTER);
            } else {
                setCostCenterRef(pks, refpanelList, costCenterFieldCode);
            }
        }
	}
	
	private void initCostshareRefPanel(CriteriaChangedEvent evt, String fieldCode) {
        String[] orgArray = parseDataPowerOrgs(null, fieldCode);
        List<UIRefPane> listRefPane = BXQryTplUtil.getRefPaneListByFieldCode(evt,
                fieldCode);
        for (UIRefPane costshareRefPane : listRefPane) {
            configDataPowerRef(costshareRefPane, orgArray);
        }
    }
	
	private void handleCostCenterChange(CriteriaChangedEvent evt) {
	    if (evt.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED || 
	            evt.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
	        return;
	    }
	    UIRefPane refpane = BXQryTplUtil.getRefPaneByFieldCode(this, "pk_pcorg");//利润中心
        if (refpane == null) {
            refpane = BXQryTplUtil.getRefPaneByFieldCode(evt, "zb.pk_pcorg");
        }
        if (refpane != null) {
            //利润中心pk
            String[] pkOrgs = null; 
            String pkOrg = refpane.getRefPK(); 
            if (pkOrg != null && pkOrg.length() > 0) {
                pkOrgs = new String[] { refpane.getRefPK() }; 
            }
            List<UIRefPane> refPaneList = BXQryTplUtil.getRefPaneListByFieldCode(evt, evt.getFieldCode());
            setCostCenterRef(pkOrgs, refPaneList, evt.getFieldCode());
        }
	}
	
	private void setCostCenterRef(String[] pkOrgs, List<UIRefPane> costCenterRefPaneList, String costCenterFieldCode) {
	    if (costCenterRefPaneList != null && costCenterRefPaneList.size() > 0) {
            for (UIRefPane refPaneCos : costCenterRefPaneList) {
                CostCenterTreeRefModel costModel = (CostCenterTreeRefModel)refPaneCos.getRefModel();
                costModel.setCurrentOrgCreated(false);
//                boolean setNull = false;
                costModel.setOrgType(CostCenterVO.PK_PROFITCENTER);
                if (pkOrgs == null || pkOrgs.length == 0) {
//                    setNull = true;
                    costModel.setPk_org(null);
//                    pkOrgs = new String[] {Long.toString(System.currentTimeMillis()) };
                    String[] orgArray = parseDataPowerOrgs(pkOrgs, costCenterFieldCode);
                    configDataPowerRef(refPaneCos, orgArray);
                } else {
//                    if (!pkOrgs[0].equals(costModel.getPk_org())) {
//                        setNull = true;
//                    }
                    costModel.setPk_org(pkOrgs[0]);
                    configDataPowerRef(refPaneCos, pkOrgs);
                }
//                if (setNull) {
//                    refPaneCos.setPK(null);
//                }
//                refPaneCos.setMultiCorpRef(false);
            }
        }
	}
	
	/**
	 * 缓存根据组织字段过滤的字段[key=组织，value=关联字段]
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
	 * 返回报销管理查询对话框组织相关字段
	 * @return
	 */
	protected List<String> getOrgFieldList(){
		return Arrays.asList(new String[]{BX_PK_ORG,BX_FYDWBM,BX_DWBM});
	}

	/**
	 * 处理报销管理帐表查询对话框查询条件的切换拖拽事件
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
	 * 功能：设置常用查询条件VO
	 */
	protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		//
	}

	/**
	 * 功能：得到查询组织<br>
	 * 默认或者为空时，返回当前登陆组织 暂时注销<br>
	 *
	 * @return
	 */
	protected String[] getPk_org() {
		UIRefPane refpanel = ((UIRefPane) getComponent(FINANCIAL_ORG_REF));
		if(refpanel==null){
			return null;
		}
		return refpanel.getRefPKs();
	}
	
	protected String[] getPk_org(CriteriaChangedEvent evt, String fieldOrg) {
	    UIRefPane refpanel = ((UIRefPane) BXQryTplUtil.getRefPaneByFieldCode(evt, fieldOrg));
        if(refpanel==null){
            return null;
        }
        return refpanel.getRefPKs();
	}

    protected String[] getRefPks(CriteriaChangedEvent evt, String fieldOrg) {
        UIRefPane refpanel = ((UIRefPane) BXQryTplUtil.getRefPaneByFieldCode(evt, fieldOrg));
        if(refpanel==null){
            return null;
        }
        return refpanel.getRefPKs();
    }
    
	protected String getDefaultOrg() {
		// 获得个性化中默认主组织
		String defaultOrg = ErUiUtil.getBXDefaultOrgUnit();
		String pk_org = null;
		if (!StringUtils.isEmpty(defaultOrg)) {
			String[] values = ErUiUtil.getPermissionOrgs(getNodeCode());
			if (ArrayUtils.isEmpty(values)
					|| Arrays.asList(values).contains(defaultOrg)) {
				// 取默认组织
				pk_org = defaultOrg;
			}
		}

		return pk_org;
	}

	/**
	 * 功能：设置财务组织
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
	 * 返回需要根据组织过滤查询对象的查询对象对应的元数据ID
	 * 此处需要对应的此功能的通过业务单元过滤的情况，添加对应的元数据id
	 *
	 * @return
	 */
	@Override
	protected List<String> getMultiOrgRef() {
		List<String> list = super.getMultiOrgRef();
//		list.add(IPubReportConstants.BUSINESS_UNIT); // 财务组织
		list.add(IBDMetaDataIDConst.DEPT); // 部门
		list.add(IBDMetaDataIDConst.PSNDOC); // 人员
        list.add(IBDMetaDataIDConst.USER); // 用户
        list.add(IPubReportConstants.MDID_PROJECT); // 项目
        
		//v6.1新增成本中心
//		list.add(IPubReportConstants.MDID_COSTCENTER);
		return list;
	}
	
	protected List<String> getSingleOrgRef() {
	  List<String> list = new ArrayList<String>();
      // 成本中心
//      list.add(IPubReportConstants.MDID_COSTCENTER);
      list.add(IPubReportConstants.MDID_PCORG);
	  return list;
	}

}