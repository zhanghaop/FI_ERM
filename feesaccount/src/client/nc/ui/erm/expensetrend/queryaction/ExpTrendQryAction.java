package nc.ui.erm.expensetrend.queryaction;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.erm.expensetrend.ExpTrendQryVO;
import nc.itf.fi.pub.Currency;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.itf.iufo.freereport.extend.ISubscribeQueryCondition;
import nc.itf.org.IOrgConst;
import nc.pubimpl.fipub.subscribe.ExpSubConvertor;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.uapbd.CurrencyRateUtilHelper;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefInitializeCondition;
import nc.ui.bd.ref.model.AccperiodYearRefModel;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.util.report.ErmDefaultQryAction;
import nc.ui.fipub.comp.ReportNormalPanel;
import nc.ui.iufo.extend.ISubscribeAction;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.org.ref.LiabilityCenterDefaultRefModel;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.queryarea.QueryArea;
import nc.ui.queryarea.quick.QuickQueryArea;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.QSSerializeListener;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.simpleeditor.SimpleEditor;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.utils.fipub.FipubReportResource;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.erm.pub.ErmBaseQueryCondition;
import nc.vo.erm.pub.ErmReportPubUtil;
import nc.vo.fipub.report.FipubBaseQueryCondition;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.iufo.freereport.FreeReportBox;
import nc.vo.iufo.freereport.FreeReportVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.querytemplate.queryscheme.QuerySchemeObject;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.FreeReportFucletContextKey;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.base.BaseSubscribeCondition;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.free.plugin.param.ReportVariable;
import com.ufida.report.free.plugin.param.ReportVariable.DATATYPE;
import com.ufida.report.free.plugin.param.ReportVariables;
import com.ufsoft.report.ReportContextKey;

@SuppressWarnings("restriction")
public class ExpTrendQryAction extends ErmDefaultQryAction implements ISubscribeAction  {
	private static final long serialVersionUID = 1L;
	private ExpTrendQryVO expQry ;
	private List<ReportVariable> vars;
	@Override
	public List<ReportVariable> getVars() {
		if (vars == null) {
			ReportVariable var = null;
			vars = new ArrayList<ReportVariable>();

			var = new ReportVariable();
			var.setKey(ExpenseBalVO.ACCYEAR);
			var.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0015")/*@res "会计年度"*/);
			vars.add(var);

			var = new ReportVariable();
			var.setKey(ExpenseBalVO.PK_ORG);
			var.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0016")/*@res "财务组织"*/);
			vars.add(var);

			var = new ReportVariable();
			var.setKey(ExpenseBalVO.PK_CURRTYPE);
			var.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "币种"*/);
			vars.add(var);
			for (ReportVariable v : vars) {
				v.setDataType(DATATYPE.DT_STRING);
				v.setValue("");
				v.setTip(null);
				v.setArea(null);
				v.setExtType(DATATYPE.EXT_SINGLE);
			}
		}
		return vars;
	}

	@Override
    public void deforeShowDialog(IContext context,String sqlWhere){
		FreeReportBox reportbox = (FreeReportBox) context.getAttribute("report_freereportbox");
		FreeReportVO reportvo = reportbox.getVoMap().get(context.getAttribute(ReportContextKey.REPORT_PK));
		String reportcode = reportvo.getReportcode();
		if(expQry==null){
			expQry = new ExpTrendQryVO();
		}
		expQry.convertQryObjVO(reportcode);
		expQry.setSqlWhere(sqlWhere);
		setQryExpTrendQryVO(context, null, sqlWhere);
	}

	public void setExpQry(ExpTrendQryVO expQry) {
		this.expQry = expQry;
	}

	public ExpTrendQryVO getExpQry() {
		return expQry;
	}

	@Override
	public ISubscribeQueryCondition doSubscribeAction(Container parent,
			IContext context, AbsAnaReportModel reportModel,
			ISubscribeQueryCondition oldCondition) {
		QuerySchemeObject oldSchemeObj = null;
		if (oldCondition != null && oldCondition instanceof BaseSubscribeCondition) {
			oldSchemeObj = ((BaseSubscribeCondition) oldCondition).getQueryResult();
		}
		// 将旧的查询条件带到查询界面，以备修改订阅任务时，复原界面
		context.setAttribute(IPubReportConstants.QRY_OLD_SCHEME_OBJ, oldSchemeObj);

		// 调用“查询”按钮进行响应，取得订阅条件
		IQueryCondition qryCond = getQueryCondition(parent, context, reportModel, null);

		QuerySchemeObject querySchemeObj = (getQueryConditionDlg(parent, context, reportModel, null)).getQryCondEditor().getQuerySchemeObject();
		querySchemeObj.put(ExpSubConvertor.class.getName(),  qryCond);

		BaseSubscribeCondition subscribeCond = new BaseSubscribeCondition(querySchemeObj, null);

		return subscribeCond;
	}
	@Override
	public IQueryCondition setQryExpTrendQryVO(IContext context,AbsAnaReportModel reportModel,
			String sqlWhere) {
		IQueryCondition condition = createQueryCondition(context);
		if (condition == null || !(condition instanceof BaseQueryCondition))
			return condition;
		FipubBaseQueryCondition result = (FipubBaseQueryCondition) condition;
		ExpTrendQryVO expTrendQryVO = getExpQry();
		expTrendQryVO.setErmBaseQueryCondition((ErmBaseQueryCondition)result);
		result.setUserObject(expTrendQryVO);
		LoginContext loginContext = (LoginContext)context.getAttribute("key_private_context");
		result.getQryCondVO().setPk_group((loginContext.getPk_group()));
        result.getQryCondVO().setWhereSql(sqlWhere);
		return condition;
	}

    protected IQueryCondition createQueryCondition(IContext context) {
        IReportQueryCond reportQueryCond = getReportQueryCond();
        BaseQueryCondition condition = new ErmBaseQueryCondition(true, reportQueryCond);
        condition.setUserObject(reportQueryCond);
        return condition;
    }
    
    protected IReportQueryCond getReportQueryCond() {
        QueryConditionDLG dlg = getQueryConditionDlg(null, null, null, null);
        ReportQueryCondVO condition = new ReportQueryCondVO();
        UIRefPane refPane = BXQryTplUtil.getRefPaneByFieldCode(dlg, "pk_org");
        String[] pkOrgs = refPane.getRefPKs();
        if (!ArrayUtils.isEmpty(pkOrgs)) {
            condition.setPk_orgs(pkOrgs);
        }
        try {
            dlg.getQryCondEditor().setPowerEnable(true);
            String whereSql = dlg.getQryCondEditor().getQueryScheme()
                    .getTableListFromWhereSQL().getWhere();
            condition.setWhereSql(whereSql);
        } finally {
            dlg.getQryCondEditor().setPowerEnable(true);
        }
//        String whereSql = dlg.getQryCondEditor().getQueryScheme()
//                .getTableListFromWhereSQL().getWhere();
//        condition.setWhereSql(whereSql);
        condition.setLocalCurrencyType((String) ((UIComboBox) normalCondCompMap.get(LOCAL_CURRENCY_TYPE_COMB)).getSelectdItemValue());
        condition.getUserObject().put("isPkorgSameAssumeOrg", Boolean.TRUE);
        return condition;
    }
    
	/**
	 * 获取查询条件
	 *
	 * @param parent
	 * @param context
	 * @param reportModel
	 * @param oldCondition
	 * @return
	 */
	protected IQueryCondition getQueryCondition(Container parent, IContext context,
			AbsAnaReportModel reportModel, IQueryCondition oldCondition) {
		return doQueryAction(parent, context, reportModel, oldCondition);
	}
	
    @Override
    public IQueryCondition doQueryByScheme(Container parent, IContext context,
            AbsAnaReportModel reportModel, IQueryScheme queryScheme) {
        IQueryCondition qryCondition = super.doQueryByScheme(parent, context, reportModel, queryScheme);
        ReportVariables varPool = ReportVariables.getInstance(reportModel.getFormatModel());
        ReportVariable var = varPool.getVariable("accyear");
        AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(getValPKString("pk_org"));
        UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
        AccperiodVO accperiodVO = null;
        try {
            calendar.setDate(curDate);
            accperiodVO = calendar.getYearVO();
        } catch (InvalidAccperiodExcetion e) {
            Logger.error(e.getMessage(), e);
        }
        if (accperiodVO != null) {
            var.setValue(accperiodVO.getPeriodyear());
        }
        return qryCondition;
    }

    private void handleCriteriaChanged(CriteriaChangedEvent event,
            final IContext context) {
        if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
            String fieldCode = event.getFieldCode();
//            String pk_org = ErUiUtil.getBXDefaultOrgUnit();
            String pk_org = ErUiUtil.getReportDefaultOrgUnit();
            LoginContext mContext = (LoginContext) context
                .getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
            String[] orgArray = parseDataPowerOrgs(new String[]{pk_org}, mContext, event.getFieldCode());
            if (!hasPerm(pk_org, orgArray)) {
                pk_org = null;
            }
            if (ExpenseBalVO.PK_ORG.equals(fieldCode)) {
                String[] pks = mContext.getPkorgs();
                UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForInit(event);
                // 允许参照出已停用的组织
                refPane.getRefModel().setDisabledDataShow(true);
                // 不受数据权限控制
                refPane.getRefModel().setUseDataPower(false);
                // 功能权限过滤
                ERMQueryActionHelper.filtOrgsForQueryAction(event, pks);
                // 获得个性化中默认主组织
//                String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                ERMQueryActionHelper.setPk(event, pk_org, false);
            } else if (ExpenseBalVO.PK_RESACOSTCENTER.equals(fieldCode)) {
                //成本中心
                UIRefPane ref = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForInit(event);
                if (ref != null) {
                    CostCenterTreeRefModel model = (CostCenterTreeRefModel)ref.getRefModel();
                    if (model != null) {
                        model.setCurrentOrgCreated(false);
                        model.setOrgType("pk_profitcenter");
                    }                    
                }
//                String pk_org = ErUiUtil.getBXDefaultOrgUnit();
//                if (ref != null && ref.getRefModel() != null) {
//                    ref.getRefModel().setPk_org(pk_org);
//                }
            } else if (ExpenseBalVO.ASSUME_DEPT.equals(fieldCode)) {
                // 部门
                UIRefPane ref = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForInit(event);
                if (ref != null && ref.getRefModel() != null) {
//                    String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                    ref.getRefModel().setPk_org(pk_org);
                }
            } else if ("accyear".equals(fieldCode)) {
//                String pk_org = ErUiUtil.getBXDefaultOrgUnit();

                UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForInit(event);
                refPane.setMultiSelectedEnabled(false);
                String pkAccScheme = AccperiodParamAccessor.getInstance().getAccperiodschemePkByPk_org(pk_org);
                ((AccperiodYearRefModel)refPane.getRefModel()).setPk_accperiodscheme(pkAccScheme);
                AccountCalendar calendar = AccountCalendar
                        .getInstanceByPk_org(pk_org);
                UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
                AccperiodVO accperiodVO = null;
                try {
                    calendar.setDate(curDate);
                    accperiodVO = calendar.getYearVO();
                } catch (InvalidAccperiodExcetion e) {
                    Logger.error(e.getMessage(), e);
                }
                if (accperiodVO == null)
                    return;
                // 设置会计年度的过滤条件
//                ((AccperiodYearRefModel) refPane.getRefModel())
//                        .setPk_accperiodscheme(accperiodVO
//                                .getPk_accperiodscheme());
                ERMQueryActionHelper.setPk(event,
                        accperiodVO.getPk_accperiod(), false);
            } else if ("pk_currtype".equals(fieldCode)) {
//                String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                if (StringUtils.isEmpty(pk_org)) {
                    pk_org = mContext.getPk_group();
                }
                String pkCurrtype = CurrencyRateUtilHelper.getInstance()
                        .getLocalCurrtypeByOrgID(pk_org);
                if (StringUtils.isEmpty(pkCurrtype)) {
                    pkCurrtype = CurrencyRateUtilHelper.getInstance()
                            .getLocalCurrtypeByOrgID(IOrgConst.GLOBEORG);
                }
                ERMQueryActionHelper.setPk(event, pkCurrtype, false);
                UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                .getFiltComponentForInit(event);
                refPane.setMultiSelectedEnabled(false);
            } else {
                JComponent compent = ERMQueryActionHelper.getFiltComponentForInit(event);
                if (compent instanceof UIRefPane) {
                    UIRefPane ref = (UIRefPane) ERMQueryActionHelper
                            .getFiltComponentForInit(event);
                    if (ref != null && ref.getRefModel() != null) {
                        AbstractRefModel refModel = ref.getRefModel();
                        ref.setDataPowerOperation_code("fi");
//                        String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                        refModel.setPk_org(pk_org);
                    }
                }
            }
            JComponent compent = ERMQueryActionHelper.getFiltComponentForInit(event);
            if (compent instanceof UIRefPane) {
                UIRefPane ref = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForInit(event);
                AbstractRefModel refModel = ref.getRefModel();
                
                if (refModel instanceof nc.ui.bd.ref.busi.UserDefaultRefModel ||
                        refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel ||
                        refModel instanceof nc.ui.org.ref.DeptDefaultRefModel) {
//                    LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
                    ref.setMultiCorpRef(true);
                    ref.setMultiOrgSelected(true);
                    ref.setMultiRefFilterPKs(orgArray);
                    refModel.setPk_org(pk_org);
                    if (refModel instanceof nc.ui.org.ref.DeptDefaultRefModel) {
                        orgArray = insertHeadOneOrg(pk_org, orgArray);
                    }
//                    String[] orgArray = parseDataPowerOrgs(new String[]{pk_org}, contextLogin, event.getFieldCode());
                    configDataPowerRef(ref, orgArray);
                } else if (refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel) {
                    refModel.setFilterPks(orgArray);
                }
                
                if (!(refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel)) {
                    ref.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
                }
                if ("bx_jkbxr".equals(fieldCode)) {
                    String powerSql = refModel.getDataPowerSubSql(refModel.getTableName(),
                            refModel.getDataPowerColumn(), refModel.getResourceID());
                    if (powerSql == null) {
                        ref.setDataPowerOperation_code(null);
                    }
                }
            }
        } else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
            String fieldCode = event.getFieldCode();
            if (ExpenseBalVO.ASSUME_ORG.equals(fieldCode)) {
                // 费用承担单位变化后，设置费用承担部门和成本中心
                setFydwFilter(event);
            } else if (ExpenseBalVO.PK_ORG.equals(fieldCode)) {
                UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForValueChanged(event, "pk_org", false);
                filterOrg(event, refPane.getRefPK());
                setFydwFilter(event);
            } else if ("bx_jkbxr".equals(fieldCode)) {
                LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
                String[] orgArray = parseDataPowerOrgs(null, contextLogin, fieldCode);

                List<UIRefPane> listRefPane = BXQryTplUtil.getRefPaneListByFieldCode(event,
                        fieldCode);
                orgArray = insertHeadOneOrg(listRefPane.get(0).getRefModel().getPk_org(), orgArray);
                for (UIRefPane costshareRefPane : listRefPane) {
                    configDataPowerRef(costshareRefPane, orgArray);
                }
            }
        }
        
        if (ExpenseBalVO.PK_RESACOSTCENTER.equals(event.getFieldCode())) {
            //成本中心
            handleCostCenterChange(event, context);
        } else if ("pk_pcorg".equals(event.getFieldCode())) {
            //利润中心
            handleLiabilityCenterChange(event, context);
            LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
            UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
            .getFiltComponentForInit(event);
            refPane.getRefModel().setFilterPks(contextLogin.getPkorgs());
        }
    }

    protected String[] getRefPks(CriteriaChangedEvent evt, String fieldOrg) {
        UIRefPane refpanel = ((UIRefPane) BXQryTplUtil.getRefPaneByFieldCode(evt, fieldOrg));
        if(refpanel==null){
            return null;
        }
        return refpanel.getRefPKs();
    }
    
    private void handleLiabilityCenterChange(CriteriaChangedEvent evt, IContext context) {
        String[] pks = getRefPks(evt, evt.getFieldCode());
        List<UIRefPane> refpanelList = BXQryTplUtil.getRefPaneListByFieldCode(evt, "pk_resacostcenter");
        String costCenterFieldCode = "pk_resacostcenter";
        if (refpanelList == null || refpanelList.size() == 0) {
            refpanelList = BXQryTplUtil.getRefPaneListByFieldCode(evt, "zb.pk_resacostcenter");
            costCenterFieldCode = "zb.pk_resacostcenter";
        }
        if (refpanelList != null) {
            if (evt.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
                initCostshareRefPanel(evt, context, ExpenseBalVO.PK_RESACOSTCENTER);
            } else {
                setCostCenterRef(pks, context,  refpanelList, costCenterFieldCode);
            }
        }
    }
    
    private void handleCostCenterChange(CriteriaChangedEvent evt, IContext context) {
        if (evt.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
            return;
        }
        if (evt.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
            initCostshareRefPanel(evt, context, evt.getFieldCode());
            return;
        }
        UIRefPane refpane = BXQryTplUtil.getRefPaneByFieldCode(getQryDlg(), "pk_pcorg");//利润中心
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
            List<UIRefPane> costshareRefPaneList = BXQryTplUtil.getRefPaneListByFieldCode(evt, evt.getFieldCode());
            setCostCenterRef(pkOrgs, context, costshareRefPaneList, evt.getFieldCode());
        } else {
            initCostshareRefPanel(evt, context, evt.getFieldCode());
        }
    }
    
    private void initCostshareRefPanel(CriteriaChangedEvent evt, IContext context, String fieldCode) {
        LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
        String[] orgArray = parseDataPowerOrgs(null, contextLogin, fieldCode);

        List<UIRefPane> listRefPane = BXQryTplUtil.getRefPaneListByFieldCode(evt,
                fieldCode);
        if (evt.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
            orgArray = insertHeadOneOrg(listRefPane.get(0).getRefModel().getPk_org(), orgArray);
        }
        for (UIRefPane costshareRefPane : listRefPane) {
            configDataPowerRef(costshareRefPane, orgArray);
        }
    }
    
    private void setCostCenterRef(String[] pkOrgs,IContext context,
            List<UIRefPane> costCenterRefPaneList, String costCenterFieldCode) {
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
                    LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
                    String[] orgArray = parseDataPowerOrgs(pkOrgs, contextLogin, costCenterFieldCode);
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
    
//    private void configDataPowerRef(UIRefPane refPane, String[] pk_orgs) {
//        AbstractRefModel model = refPane.getRefModel();
//        if (model instanceof nc.ui.org.ref.LiabilityCenterDefaultRefModel) {
//            refPane.setMultiCorpRef(false);
//        } else {
//            refPane.setMultiCorpRef(true);
//        }
//        refPane.setMultiOrgSelected(true);
//        refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
//        refPane.setMultiRefFilterPKs(pk_orgs);
//        RefInitializeCondition[] conditions = refPane.getRefUIConfig().getRefFilterInitconds();
//        if (conditions != null) {
//            for (RefInitializeCondition condition : conditions) {
//                condition.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
//                if (pk_orgs != null && pk_orgs.length > 0) {
//                    condition.setDefaultPk(pk_orgs[0]);
//                }
//            }
//        }
//    }
    
    private void configDataPowerRef(UIRefPane refPane, String[] pk_orgs) {
        AbstractRefModel model = refPane.getRefModel();
        if (model instanceof LiabilityCenterDefaultRefModel) {
            refPane.setMultiCorpRef(false);
        } else if (model instanceof CostCenterTreeRefModel) {
            model.setFilterRefNodeName(new String[]{"利润中心"/* -=notranslate=- */});
            refPane.setMultiCorpRef(true);
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
                
//                String[] pkOrgs = setDefPkOrgFirst(condition.getDefaultPk(), pk_orgs);
                String[] pkOrgs = pk_orgs;
                if (pkOrgs != null && pkOrgs.length > 0 && 
                        (needMatch && matchedLiabilityCenter(pkOrgs[0]) || 
                                !needMatch)) {
                    condition.setDefaultPk(pkOrgs[0]);
                } else {
                    condition.setDefaultPk(Long.toString(System.currentTimeMillis()));
                }
            }
        }
    }
    
    private boolean matchedLiabilityCenter(String pkOrg) {
        LiabilityCenterDefaultRefModel refModel = new LiabilityCenterDefaultRefModel();
        refModel.setPk_org(pkOrg);
        refModel.setMatchPkWithWherePart(true);
        refModel.setPKMatch(true);
        @SuppressWarnings("rawtypes")
        Vector vec = refModel.matchPkData(pkOrg);
        return !(vec == null || vec.isEmpty());
    }
    
//    private String[] setDefPkOrgFirst(String defPk, String[] pkOrgs) {
//        String[] results = null;
//        if (StringUtil.isEmpty(defPk) || pkOrgs == null || pkOrgs.length == 0) {
//            results = pkOrgs;
//        } else {
//            for (String pkOrg : pkOrgs) {
//                if (defPk.equals(pkOrg)) {
//                    results = swap(pkOrg, pkOrgs);
//                    break;
//                }
//            }
//            results = (results == null ? pkOrgs : results);
//        }
//        return results;
//    }
    
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
    
    private String[] insertHeadOneOrg(String pkOrg, Object[] pkOrgs) {
        String[] pksOrg = null;
        if (pkOrgs != null) {
            pksOrg = new String[pkOrgs.length + 1];
            pksOrg[0] = pkOrg;
            for (int nPos = 0; nPos < pkOrgs.length; nPos++) {
                pksOrg[nPos + 1] = (String)pkOrgs[nPos];
            }
        } else {
            pksOrg = new String[] { pkOrg };
        }
        return pksOrg;
    }
    
    private String[] parseDataPowerOrgs(String[] pk_orgs, LoginContext context, String fieldCode) {
        Object[] pkOrgs = null;
        if ("pk_pcorg".equals(fieldCode)) {
            //走功能权限
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
                orgArray = insertHeadOneOrg(pk_orgs[0], pkOrgs);
            } else {
                orgArray = swap(pkOrgs[0].toString(), pkOrgs);;
            }
        } else if (pkOrgs != null && pkOrgs.length > 0) {
            orgArray = swap(pkOrgs[0].toString(), pkOrgs);
        }
        return orgArray;
    }
    
    private boolean hasPerm(String pkOrg, Object[] permOrgs) {
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

    private void filterOrg(CriteriaChangedEvent event, String pkOrg) {
        List<IFilterEditor> list = null;
        if (event.getCriteriaEditor() instanceof SimpleEditor) {
            SimpleEditor simpleEditor = ((SimpleEditor) event
                    .getCriteriaEditor());
            list = simpleEditor.getFilterEditors();
        } else if (event.getCriteriaEditor() instanceof QuickQueryArea) {
            list = ((QuickQueryArea) event.getCriteriaEditor())
                    .getFilterEditors();
        }
        if (list != null) {
            for (IFilterEditor filterEditor : list) {
                FilterEditorWrapper wapper = new FilterEditorWrapper(
                        filterEditor);
                JComponent component = wapper
                        .getFieldValueElemEditorComponent();
                if (component instanceof UIRefPane) {
                    UIRefPane refPane = (UIRefPane) component;
                    if (!(refPane.getRefModel() instanceof FinanceOrgDefaultRefTreeModel)) {
                        refPane.getRefModel().setPk_org(pkOrg);
                    }
                    if (refPane.getRefModel() instanceof AccperiodYearRefModel) {
                        String pkAccScheme = AccperiodParamAccessor.getInstance().getAccperiodschemePkByPk_org(pkOrg);
                        ((AccperiodYearRefModel)refPane.getRefModel()).setPk_accperiodscheme(pkAccScheme);
                    }
                }
            }
        }
    }

	private void initDlgListener(final IQueryConditionDLG dlg,final IContext context) {

			// 查询条件控件处理，只修改一次
        dlg.registerCriteriaEditorListener(new ICriteriaChangedListener() {
            @Override
            public void criteriaChanged(CriteriaChangedEvent event) {
                handleCriteriaChanged(event, context);
            }

        });
        
        dlg.getQryCondEditor().addQSSerializeListener(new QSSerializeListener() {

            @Override
            public void unserialize(QuerySchemeObject qsobject) {
                UIRefPane refPaneOrg = BXQryTplUtil.getRefPaneByFieldCode((QueryConditionDLG)dlg, "pk_org");
                String pk_org = refPaneOrg.getRefPK();
                AccountCalendar calendar = AccountCalendar
                        .getInstanceByPk_org(pk_org);
                //获取当前业务日期
                UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
                try {
                    calendar.setDate(currBusiDate);
                } catch (Exception e) {
                    Logger.error(e.getMessage(), e);
                }

                AccperiodVO accperiodVO = calendar.getYearVO();
                if (accperiodVO == null)
                    return;
                

                UIRefPane[] accYearRefPane = BXQryTplUtil.getRefPaneByFieldCode((QueryConditionDLG)dlg, new String[] {"accyear"});
                for (UIRefPane refPane : accYearRefPane) {
                    refPane.setPK(accperiodVO.getPk_accperiod());
                }
//                UIRefPane accYearRefPane = 
//                    BXQryTplUtil.getRefPaneByFieldCode((QueryConditionDLG)dlg, "accyear");
//                accYearRefPane.setPK(accperiodVO.getPk_accperiod());
            }

            @Override
            public void serialize(QuerySchemeObject qsobject) {
                
            }
            
        });
	}
	

	private void setFydwFilter(CriteriaChangedEvent fydwevent) {
		UIRefPane fydw = (UIRefPane)ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent, ExpenseBalVO.ASSUME_ORG, false);
		String[] headItems = new String[]{ExpenseBalVO.PK_RESACOSTCENTER, ExpenseBalVO.ASSUME_DEPT, ExpenseBalVO.PK_IOBSCLASS};
		if(fydw == null){
		    fydw = (UIRefPane)ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent, ExpenseBalVO.PK_ORG, false);
		}
		String fywd = fydw.getRefPK();

        String[] orgArray = null;
        Object[] objArray = fydw.getRefModel().getValues(fydw.getRefModel().getPkFieldCode(), fydw.getRefModel().getVecData());
        if (objArray != null && objArray.length > 0) {
            orgArray = new String[objArray.length];
            for (int nPos = 0; nPos < orgArray.length; nPos++) {
                orgArray[nPos] = (String)objArray[nPos];
            }
        }
        orgArray = insertHeadOneOrg(fywd, orgArray);
        
		for (int i = 0; i < headItems.length; i++) {
			UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent, headItems[i],false);
			if(ref != null && ref.getRefModel() != null){
				ref.getRefModel().setPk_org(fywd);
	            if (headItems[i].equals(ExpenseBalVO.ASSUME_DEPT)) {
	                configDataPowerRef(ref, orgArray);
	            }
			}
		}
	}

	@Override
	protected QueryConditionDLG createQueryDlg(Container parent, TemplateInfo ti, final IContext context,
			IQueryCondition oldCondition) {

	    QueryConditionDLG dlg = new QueryConditionDLG(parent, ti,
                getTitle(context)) {
            private static final long serialVersionUID = 1L;

            class QuickQueryAreaInner extends QuickQueryArea {

                private static final long serialVersionUID = 7469379677470881161L;

                public QuickQueryAreaInner(QueryConditionDLG qcd) {
                    super(qcd);
                    addCriteriaChangedListener(new ICriteriaChangedListener() {
                        @Override
                        public void criteriaChanged(CriteriaChangedEvent event) {
                            handleCriteriaChanged(event, context);
                        }
                    });

                    final List<IFilterEditor> fes = this.getFilterEditors();
                    for (IFilterEditor filterEditor : fes) {
                        this.fireCriteriaChanged(new CriteriaChangedEvent(
                                filterEditor, QuickQueryAreaInner.this));
                    }
                }

            }
            private QueryConditionDLG getDialog() {
                return this;
            }

            @Override
            public QueryArea createQueryArea() {
                beforeCreateQueryArea();
                QueryArea queryArea = new QueryArea(this) {
                    private static final long serialVersionUID = 6286495361906513196L;
                    QuickQueryArea quickQueryArea;
                    @Override
                    public QuickQueryArea getQuickQueryArea() {
                        if (quickQueryArea == null) {
                            quickQueryArea = new QuickQueryAreaInner(
                                    getDialog());
                        }
                        return quickQueryArea;
                    }

                };

                queryArea.registerCriteriaEditorListener(new ICriteriaChangedListener() {
                    @Override
                    public void criteriaChanged(CriteriaChangedEvent event) {
                        handleCriteriaChanged(event, context);
                    }

                });
                return queryArea;
            }
            

            public String checkCondition() {
                // 执行父类校验
                String strHint = super.getQryCondEditor().checkCondition(false);
                if (StringUtils.isEmpty(strHint)) {
                    try {
                        // 执行子类校验
                        UIRefPane refpanel = BXQryTplUtil
                                .getRefPaneByFieldCode(this, "pk_org");
                        if (refpanel == null) {
                            return null;
                        }
                        ReportQueryCondVO queryCondVO = (ReportQueryCondVO)getReportQueryCondVO();
                        String currencyType = queryCondVO.getLocalCurrencyType();
                        if ("org_local".equals(currencyType)) {
                            // ③财务组织本位币必须一致
                            strHint = StringUtils.EMPTY;
                            Set<String> localCurrtype = new HashSet<String>();
                            CurrtypeVO[] currtypeVOs = ErmReportPubUtil
                                    .getLocalCurrencyByOrgID(refpanel.getRefPKs());
                            for (CurrtypeVO vo : currtypeVOs) {
                                localCurrtype.add(vo.getPk_currtype());
                            }
                            if (localCurrtype.size() > 1) {
                                strHint += nc.vo.ml.NCLangRes4VoTransl
                                        .getNCLangRes().getStrByID("feesaccount_0",
                                                "02011001-0029")/*@res "\r\n多个查询组织的本位币不一致，请分别查询！"*/;
                            }
                        }
                    } catch (Exception e) {
                        strHint = e.getMessage();
                    }
                }
                if (StringUtils.isEmpty(strHint)) {
                    strHint = null;
                }
                return strHint;
            }

            private boolean isbeforeShow =false;
            
            public int beforeShowModal() {
                if(!isIsbeforeShow()){
                    getQryCondEditor().setNormalPanel(getNormalPanel());
                    isbeforeShow = true;
                }
                int tempaltestate = super.beforeShowModal();
                return tempaltestate;
            }

            public boolean isIsbeforeShow() {
                return isbeforeShow;
            }
            
            protected UIPanel getOptionPanel(List<Component> componentList) {
                UIPanel optionPanel = new UIPanel();
                optionPanel.setPreferredSize(new Dimension(490, 32));

                for (Component comp : componentList) {
                    Dimension dim = new Dimension();
                    if (comp instanceof UICheckBox) {
                        // 选择框显示尺寸大小
                        dim.setSize(200, 32);
                    } else if (comp instanceof UILabel && StringUtils.isEmpty(((UILabel) comp).getText())) {
                        // 空Label显示尺寸大小
                        dim.setSize(20, 32);
                    } else {
                        // 其他控件显示尺寸大小
                        dim.setSize(80, 32);
                    }
                    comp.setSize(dim);
                    comp.setPreferredSize(dim);
                    if (comp instanceof JLabel) {
                        ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    optionPanel.add(comp);
                }

                return optionPanel;
            }
            
            /** 新的NormalPanel */
            protected ReportNormalPanel m_NormalPanel = null;
            
            /**
             * 返回用户自定义查询条件面板
             */
            public ReportNormalPanel getNormalPanel() {
                if (m_NormalPanel == null) {
                    try {
                        initUINormalPanel();
                    } catch (BusinessException e) {
                        Logger.debug(e.getMessage(), this.getClass(), "getNormalPanel");
                    }
                }
                return m_NormalPanel;
            }
            
            private List<Integer> getPanelHeightList() {
                List<Integer> heightList = new ArrayList<Integer>(3);
                heightList.add(Integer.valueOf(40));
                heightList.add(Integer.valueOf(0));
                heightList.add(Integer.valueOf(0));
                return heightList;
            }
            
            protected void initUINormalPanel() throws BusinessException {
                // 为方便起见，单独维护一个类ReportNormalPanel，包括对话框右侧的两个面板内容
                m_NormalPanel = new ReportNormalPanel(null, null, null, getPanelHeightList());

                List<Component> componentList = getCommonExtendCondCompList();
                if (componentList.size() > 0) {
                    m_NormalPanel.add(getOptionPanel(componentList));
                }

            }

            private List<Component> extendCommonCondCompList = new ArrayList<Component>(); // 适合遍历
            
            protected List<Component> getCommonExtendCondCompList() throws BusinessException {
                if (extendCommonCondCompList.size() == 0) {
                    LoginContext loginContext = (LoginContext) context.getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
                    extendCommonCondCompList.add(new UILabel(FipubReportResource.getLocalCurrencyTpyLbl()));
                    UIComboBox locCurrTypeComb = new UIComboBox();
                    locCurrTypeComb.addItem(new DefaultConstEnum(IPubReportConstants.ORG_LOCAL_CURRENCY, FipubReportResource.getOrgLocalCurrencyLbl()));
                    if (Currency.isStartGroupCurr(loginContext.getPk_group())) {
                        locCurrTypeComb.addItem(new DefaultConstEnum(IPubReportConstants.GROUP_LOCAL_CURRENCY, FipubReportResource.getGroupLocalCurrencyLbl()));
                    }
                    if (Currency.isStartGlobalCurr(null)) {
                        locCurrTypeComb.addItem(new DefaultConstEnum(IPubReportConstants.GLOBLE_LOCAL_CURRENCY, FipubReportResource.getGlobleLocalCurrencyLbl()));
                    }
                    extendCommonCondCompList.add(locCurrTypeComb);
                    addComponent(LOCAL_CURRENCY_TYPE_COMB, locCurrTypeComb);
                }

                return extendCommonCondCompList;
            }


            /**
             * 将控件添加到控件容器
             *
             * @param key
             * @param comp
             */
            public void addComponent(String key, Component comp) {
                normalCondCompMap.put(key, comp);
            }

            /**
             * 从控件容器中获取指定控件
             *
             * @param key
             */
            public Component getComponent(String key) {
                return normalCondCompMap.get(key);
            }
            
            public IReportQueryCond getReportQueryCondVO() throws BusinessException {
                IReportQueryCond queryCondVO = getNormalPanel().getReportQueryCondVO();
                queryCondVO.setLocalCurrencyType((String) ((UIComboBox) getComponent(LOCAL_CURRENCY_TYPE_COMB)).getSelectdItemValue());
                return queryCondVO;
            }
        };
        
        dlg.getQryCondEditor().getQueryContext().setMultiTB(true);
		
		initDlgListener(dlg,context);
		qryDlg = dlg;
        return dlg;
	}
	
	private QueryConditionDLG qryDlg;
	
	protected QueryConditionDLG getQryDlg() {
	    return qryDlg;
	}

    /** 本币类型 */
    protected static final String LOCAL_CURRENCY_TYPE_COMB = "localCurrencyTypeComb";
    private Map<String, Component> normalCondCompMap = new HashMap<String, Component>(); // 适合读取
    
}