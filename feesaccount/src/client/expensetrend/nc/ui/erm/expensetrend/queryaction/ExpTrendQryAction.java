package nc.ui.erm.expensetrend.queryaction;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.erm.expensetrend.ExpTrendQryVO;
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
import nc.ui.iufo.extend.ISubscribeAction;
import nc.ui.org.ref.FinanceOrgDefaultRefTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.queryarea.QueryArea;
import nc.ui.queryarea.quick.QuickQueryArea;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.QSSerializeListener;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.simpleeditor.SimpleEditor;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.erm.pub.ErmReportPubUtil;
import nc.vo.fipub.query.ReportQueryVO;
import nc.vo.fipub.report.FipubBaseQueryCondition;
import nc.vo.iufo.freereport.FreeReportBox;
import nc.vo.iufo.freereport.FreeReportVO;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.querytemplate.queryscheme.QuerySchemeObject;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.FreeReportFucletContextKey;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.base.BaseSubscribeCondition;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.free.plugin.param.ReportVariable;
import com.ufida.report.free.plugin.param.ReportVariable.DATATYPE;
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
		BaseQueryCondition result = (BaseQueryCondition) condition;
		result.setUserObject(getExpQry());
		return condition;
	}

    protected IQueryCondition createQueryCondition(IContext context) {
        IReportQueryCond reportQueryCond = getReportQueryCond();
        BaseQueryCondition condition = new FipubBaseQueryCondition(true, reportQueryCond);
        condition.setUserObject(reportQueryCond);
        return condition;
    }
    
    protected IReportQueryCond getReportQueryCond() {
        QueryConditionDLG dlg = getQueryConditionDlg(null, null, null, null);
        ReportQueryVO condition = new ReportQueryVO();
        UIRefPane refPane = BXQryTplUtil.getRefPaneByFieldCode(dlg, "pk_org");
        String[] pkOrgs = refPane.getRefPKs();
        if (!ArrayUtil.isEmpty(pkOrgs)) {
            condition.setPk_orgs(pkOrgs[0]);
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

    private void handleCriteriaChanged(CriteriaChangedEvent event,
            final IContext context) {
        if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
            String fieldCode = event.getFieldCode();
            String pk_org = ErUiUtil.getBXDefaultOrgUnit();
            if (ExpenseBalVO.PK_ORG.equals(fieldCode)) {
                LoginContext mContext = (LoginContext) context
                        .getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
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
                        model.setOrgType("pk_financeorg");
                    }                    
                }
//                String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                if (ref != null && ref.getRefModel() != null) {
                    ref.getRefModel().setPk_org(pk_org);
                }
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
                    LoginContext mContext = (LoginContext) context
                            .getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
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
                        refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel) {
                    LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
                    ref.setMultiCorpRef(true);
                    ref.setMultiOrgSelected(true);
                    ref.setMultiRefFilterPKs(contextLogin.getPkorgs());
                    refModel.setPk_org(refModel.getPk_group());
                    
                    String[] orgArray = parseDataPowerOrgs(new String[]{pk_org}, contextLogin, event.getFieldCode());
                    configDataPowerRef(ref, orgArray);
                }
                
                if (!(refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel)) {
                    ref.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
                }
            }
        } else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
            String fieldCode = event.getFieldCode();
            if (ExpenseBalVO.ASSUME_ORG.equals(fieldCode)) {
                // 费用承担单位变化后，设置费用承担部门和成本中心
                setFydwFilter(event);
            }
            if (ExpenseBalVO.PK_ORG.equals(fieldCode)) {
                UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                        .getFiltComponentForValueChanged(event, "pk_org", false);
                filterOrg(event, refPane.getRefPK());
            }

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
        refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
        refPane.setMultiRefFilterPKs(pk_orgs);
        RefInitializeCondition[] conditions = refPane.getRefUIConfig().getRefFilterInitconds();
        if (conditions != null) {
            for (RefInitializeCondition condition : conditions) {
                condition.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
                if (pk_orgs != null && pk_orgs.length > 0) {
                    condition.setDefaultPk(pk_orgs[0]);
                }
            }
        }
    }
    
    private String[] parseDataPowerOrgs(String[] pk_orgs, LoginContext context, String fieldCode) {
        nc.ui.org.ref.FinanceOrgDefaultRefTreeModel fiRefModel = new nc.ui.org.ref.FinanceOrgDefaultRefTreeModel();
        fiRefModel.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
//        LoginContext context = (LoginContext)getContext().getAttribute("key_private_context");
        Object[] pkOrgs = null;
//        if (isPkOrgSameAssumeOrg() && getExcludeAssumeOrgRefList().contains(fieldCode)) {
            //走功能权限
//            pkOrgs = context.getPkorgs();
//        } else {
            @SuppressWarnings("rawtypes")
            Vector vecFiOrg = fiRefModel.getData();
            fiRefModel.setSelectedData(vecFiOrg);
            pkOrgs = fiRefModel.getValues("pk_financeorg", true);
//        }
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
                AccperiodVO accperiodVO = calendar.getYearVO();
                if (accperiodVO == null)
                    return;
                
                UIRefPane accYearRefPane = 
                    BXQryTplUtil.getRefPaneByFieldCode((QueryConditionDLG)dlg, "accyear");
                accYearRefPane.setPK(accperiodVO.getPk_accperiod());
            }

            @Override
            public void serialize(QuerySchemeObject qsobject) {
                
            }
            
        });
	}
	

	private void setFydwFilter(CriteriaChangedEvent fydwevent) {
		UIRefPane fydw = (UIRefPane)ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent, ExpenseBalVO.ASSUME_ORG, false);
		String[] headItems = new String[]{ExpenseBalVO.PK_RESACOSTCENTER, ExpenseBalVO.ASSUME_DEPT};
		if(fydw == null){
			return;
		}
		String fywd = fydw.getRefPK();
		
		for (int i = 0; i < headItems.length; i++) {
			UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent, headItems[i],false);
			if(ref != null && ref.getRefModel() != null){
				ref.getRefModel().setPk_org(fywd);
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
                String strHint = super.getQryCondEditor().checkCondition(true);
                if (StringUtils.isEmpty(strHint)) {
                    try {
                        // 执行子类校验
                        UIRefPane refpanel = BXQryTplUtil
                                .getRefPaneByFieldCode(this, "pk_org");
                        if (refpanel == null) {
                            return null;
                        }
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
                    } catch (Exception e) {
                        strHint = e.getMessage();
                    }
                }
                if (StringUtils.isEmpty(strHint)) {
                    strHint = null;
                }
                return strHint;
            }

        };
        dlg.getQryCondEditor().getQueryContext().setMultiTB(true);
		
		initDlgListener(dlg,context);
		
        return dlg;
	}

}