package nc.ui.erm.pub;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.report.CustomizeReportQryAction;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.QSSerializeListener;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filter.DefaultFilter;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.pub.ErmBaseQueryCondition;
import nc.vo.fipub.report.FipubBaseQueryCondition;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.query.IQueryConstants;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.querytemplate.queryscheme.QuerySchemeObject;
import nc.vo.querytemplate.queryscheme.QuerySchemeVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.FreeReportFucletContextKey;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.free.plugin.param.IReportVarService;
import com.ufida.report.free.plugin.param.ReportVariable;
import com.ufida.report.free.plugin.param.ReportVariable.DATATYPE;
import com.ufida.report.free.plugin.param.ReportVariables;
/**
 * 摊销帐表明细查询
 * @author wangled
 *
 */
@SuppressWarnings("restriction")
public class ExpamortizeAnalyzeAction extends CustomizeReportQryAction implements IReportVarService{
	private final Map<String, String[]> varMap = new HashMap<String, String[]>();
	private List<ReportVariable> vars;
	private IReportQueryCond reportQueryCond;
	
	@Override
    protected IQueryCondition createQueryCondition(IContext context) {
	    IReportQueryCond reportQueryCond = getReportQueryCond();
        BaseQueryCondition condition = new ErmBaseQueryCondition(true, reportQueryCond);
        condition.setUserObject(reportQueryCond);
        return condition;
    }
	
    @Override
    protected IQueryCondition showQueryDialog(Container parent,
			IContext context, AbsAnaReportModel reportModel,
			TemplateInfo tempinfo, IQueryCondition oldCondition) {
		IQueryCondition condition = super.showQueryDialog(parent, context,
				reportModel, tempinfo, oldCondition);
		try {
			getQuerySchemeMap((QueryScheme) getQryDlg().getQueryScheme());
			setReportHeadVas(context, reportModel);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return condition;
	}

	/**
	 * 根据指定查询方案进行查询 （IQueryAction接口的方法的实现）。
	 * @param parent
	 * @param context
	 * @param reportModel
	 * @param queryScheme
	 * @return
	 */
	@Override
	public IQueryCondition doQueryByScheme(Container parent, IContext context, AbsAnaReportModel reportModel,
			IQueryScheme queryScheme) {
	    
	    Object obj = queryScheme.get(IPubReportConstants.QRY_COND_VO);
        if (obj == null) {
            QuerySchemeVO[] vos = getQryDlg().getQuerySchemeVOs();
            for (QuerySchemeVO vo : vos) {
                QuerySchemeObject qsObj = vo.getQSObject4Blob();
                String[] userDefineKeys = qsObj.getUserDefineKeys();
                if(userDefineKeys!=null && userDefineKeys.length>0){
                    for(String userKey : userDefineKeys){
                        queryScheme.put(userKey, qsObj.get(userKey));
                    }
                }                
            }
            obj = queryScheme.get(IPubReportConstants.QRY_COND_VO);
        }

        IQueryCondition condition = null;
        if (obj != null && obj instanceof FipubBaseQueryCondition) {
            FipubBaseQueryCondition repQryCon = (FipubBaseQueryCondition)obj;
            fillDateBeginEnd(repQryCon);
        } else if (obj == null) {
            getQryDlg().checkCondition();
            condition = createQueryCondition(context);
            fillDateBeginEnd((FipubBaseQueryCondition)condition);
            
        } else {
          condition = super.doQueryByScheme(parent, context, reportModel, queryScheme);
        }
        if (condition != null) {
            reportQueryCond = (IReportQueryCond) ((BaseQueryCondition)condition).getUserObject();
        }
		try {
			getQuerySchemeMap((QueryScheme) queryScheme);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		setReportHeadVas(context, reportModel);
		
		ReportVariables varPool = ReportVariables.getInstance(reportModel.getFormatModel());
        ReportVariable var = varPool.getVariable("accperiod");
        AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(getValPKString("pk_org"));
        UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
        AccperiodmonthVO accperiodVO = null;
        try {
            calendar.setDate(curDate);
            accperiodVO = calendar.getMonthVO();
        } catch (InvalidAccperiodExcetion e) {
            Logger.error(e.getMessage(), e);
        }
        if (accperiodVO != null) {
            var.setValue(accperiodVO.getYearmth());
        }
		return condition;
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private FipubBaseQueryCondition fillDateBeginEnd(FipubBaseQueryCondition repQryCon) {
        if (repQryCon == null) {
            return null;
        }
        IReportQueryCond reportQueryCond = repQryCon.getQryCondVO();
//        CustomizeReportQryCondition con = (CustomizeReportQryCondition)repQryCon.getQryCondVO();
        String where = reportQueryCond.getWhereSql();
        int nPos = where.indexOf("pk_org");
        String pkOrg = where.substring(nPos + 10, nPos + 30);
        AccountCalendar calendar = StringUtil.isEmpty(pkOrg) ? 
                AccountCalendar.getInstance() : 
                    AccountCalendar.getInstanceByPk_org(pkOrg);
        Map data = (Map)reportQueryCond.getUserObject();
        UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
        try {
            calendar.setDate(curDate);
            data.put("acc_priod", calendar.getMonthVO().getPk_accperiodmonth());
        } catch (InvalidAccperiodExcetion e) {
            Logger.error(e.getMessage(), e);
        }
        return repQryCon;
    }
	
	/**
	 * 从查询模板上得到查询对象
	 */
    @Override
	protected IReportQueryCond getReportQueryCond() {
        reportQueryCond = new ReportQueryCondVO();
        try {
            getQryDlg().getQryCondEditor().setPowerEnable(userDataPermission());
            String whereSql = getQryDlg().getQryCondEditor().getQueryScheme()
                    .getTableListFromWhereSQL().getWhere();
            reportQueryCond.setWhereSql(whereSql);
        } finally {
            getQryDlg().getQryCondEditor().setPowerEnable(true);
        }
        
		String[] accperiods = getFieldValuesByCode("accperiod");
		String[] period_start = getFieldValuesByCode("start_period");
        String[] period_end = getFieldValuesByCode("end_period");
		if (!ArrayUtils.isEmpty(accperiods)) {
			reportQueryCond.getUserObject().put(IErmReportConstants.ACC_PERIOD,
					accperiods[0]);
		}
		if (!ArrayUtils.isEmpty(period_start)) {
		    reportQueryCond.getUserObject().put(IErmReportConstants.PERIOD_START,
		            period_start[0]);
		}
		if (!ArrayUtils.isEmpty(period_end)) {
		    reportQueryCond.getUserObject().put(IErmReportConstants.PERIOD_END,
		            period_end[0]);
        }
		return reportQueryCond;
	}

	/**
	 * 得到变量并显示到界面中
	 */
	@Override
	public Object getVarsValues(IContext context, String paraKey) {
		Object obj = context.getAttribute(this.getClass().getName());
		if (obj == null || !(obj instanceof ReportVariables)) {
			return null;
		}
		ReportVariables varPool = (ReportVariables) obj;
		return varPool.getValue(paraKey);
	}

	/**
	 * 设置需要载入的变量
	 */
	@Override
    public List<ReportVariable> getVars() {
		if (vars == null) {
			ReportVariable var = null;
			vars = new ArrayList<ReportVariable>();

			var = new ReportVariable();
			var.setKey(ExpamtinfoVO.PK_ORG);
			var.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0016")/*@res "财务组织"*/);
			vars.add(var);

			var = new ReportVariable();
			var.setKey("accperiod");
			var.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0019")/*@res "会计期间"*/);
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

	/**
	 * 设置变量的值
	 */
	private void setReportHeadVas(IContext context,
			AbsAnaReportModel reportModel) {
		ReportVariables varPool = ReportVariables.getInstance(reportModel.getFormatModel());
		ReportVariable var;
		for (int i = 0; i < getVars().size(); i++) {
			var = varPool.getVariable(getVars().get(i).getKey());
			var.setValue(getValShowString(getVars().get(i).getKey()));
		}
		context.setAttribute(this.getClass().getName(), varPool);
	}

	public String getValShowString(String key) {
		if (varMap.get(key) != null) {
			return (varMap.get(key))[0];
		}
		return null;
	}

	// 清除旧值
	public void repeatMapKey() {
		varMap.clear();
		for (int i = 0; i < getVars().size(); i++) {
			varMap.put(getVars().get(i).getKey(), null);
		}
	}

	/**
	 *将查询方案中的值设置的变量Map中
	 * @param queryScheme
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, String[]> getQuerySchemeMap(QueryScheme queryScheme)
			throws BusinessException {
		// 清除旧值
		repeatMapKey();
		IFilter[] filter = (IFilter[]) queryScheme.get(IQueryScheme.KEY_FILTERS);
		for (int i = 0; i < filter.length; i++) {
			DefaultFilter ftv = (DefaultFilter) filter[i];
			FilterMeta fmeta = (FilterMeta) ftv.getFilterMeta();
			if (!varMap.containsKey(fmeta.getFieldCode())) {
				continue;
			}
			if (IQueryConstants.UFREF == fmeta.getDataType()) {
				if (varMap.get(fmeta.getFieldCode()) != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0",null,"02011000-0016",null,new String[]{ftv.getFilterMeta().getFieldName()})/*@res ":查询条件太复杂,有两个相同字段的查询条件,报表表头无法展示！"*/);
				}
				List<IFieldValueElement> valList = (ftv.getFieldValue()).getFieldValues();
				if (!valList.isEmpty()) {
					if (valList.size() > 1) {
						StringBuffer sbuf = new StringBuffer();
						StringBuffer pkbuf = new StringBuffer();
						for (int j = 0; j < valList.size(); j++) {
							DefaultFieldValueElement defment = ((DefaultFieldValueElement) valList
									.get(j));
							sbuf.append(defment.getShowString());
							sbuf.append(",");
							pkbuf.append(((RefValueObject) defment
									.getValueObject()).getPk());
							pkbuf.append(",");
						}
						varMap.put(fmeta.getFieldCode(), new String[] {
								sbuf.substring(0, sbuf.length() - 1),
								pkbuf.substring(0, pkbuf.length() - 1) });
					} else {
						DefaultFieldValueElement defment = ((DefaultFieldValueElement) valList
								.get(0));
						RefValueObject refvaO = ((RefValueObject) defment
								.getValueObject());
						StringBuffer pkbuf = new StringBuffer();
						if (((RefValueObject) defment.getValueObject())
								.isSubIncluded()) {
							String[] pkvalue = refvaO.getRefIncludeSubInfo()
									.getSelectedTopLevelNodesPKs();
							for (int j = 0; j < pkvalue.length; j++) {
								pkbuf.append(pkvalue[j]);
								pkbuf.append(",");
							}
							varMap.put(fmeta.getFieldCode(), new String[] {
									defment.getShowString(),
									pkbuf.substring(0, pkbuf.length() - 1) });
						} else {
							varMap.put(fmeta.getFieldCode(), new String[] {
									defment.getShowString(),
									((RefValueObject) defment.getValueObject())
											.getPk() });
						}
					}
				}
			} else if (IQueryConstants.STRING == fmeta.getDataType()) {
				if (varMap.get(fmeta.getFieldCode()) != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0",null,"02011000-0016",null,new String[]{ftv.getFilterMeta().getFieldName()})/*@res ":查询条件太复杂,有两个相同字段的查询条件,报表表头无法展示！"*/);
				}
				List<IFieldValueElement> valList = (ftv.getFieldValue()).getFieldValues();
				for (int j = 0; j < valList.size(); j++) {
					DefaultFieldValueElement defment = ((DefaultFieldValueElement) valList
							.get(j));
					varMap.put(fmeta.getFieldCode(), new String[] {
							defment.getShowString(), defment.getShowString() });
				}
			}
		}
		if(reportQueryCond!=null){
			reportQueryCond.getUserObject().put(ExpamtinfoVO.PK_ORG,
					varMap.get(ExpamtinfoVO.PK_ORG));
		}
		return null;
	}
	
	public String getValPKString(String key){
        if(varMap.get(key)!=null){
            return ((String[])varMap.get(key))[1];
        }
        return null;
    }
	
	private void initDlgListener(final IQueryConditionDLG dlg, final IContext context) {

		// 查询条件控件处理，只修改一次
		dlg.registerCriteriaEditorListener(new ICriteriaChangedListener() {
			@Override
			public void criteriaChanged(CriteriaChangedEvent event) {
				if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {

					String fieldCode = event.getFieldCode();
					if (ExpamtinfoVO.PK_ORG.equals(fieldCode)) {
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
						String pk_org = ErUiUtil.getDefaultPsnOrg();
						ERMQueryActionHelper.setPk(event, pk_org, false);
                    } else if ("accperiod".equals(fieldCode)) {
                        String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                        UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                                .getFiltComponentForInit(event);
                        refPane.setMultiSelectedEnabled(false);
                        String pkAccScheme = AccperiodParamAccessor.getInstance().getAccperiodschemePkByPk_org(pk_org);
                        ((AccPeriodDefaultRefModel)refPane.getRefModel()).setDefaultpk_accperiodscheme(pkAccScheme);
                        //
                        UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
                        AccountCalendar calendar = AccountCalendar
                                .getInstanceByPk_org(pk_org);
                        AccperiodmonthVO accperiodmonthVO = null;
                        try {
                            calendar.setDate(curDate);
                            accperiodmonthVO = calendar.getMonthVO();
                        } catch (InvalidAccperiodExcetion e) {
                            Logger.error(e.getMessage(), e);
                        }
                        if (accperiodmonthVO == null)
                            return;
                        ERMQueryActionHelper.setPk(event,
                                accperiodmonthVO.getPk_accperiodmonth(), false);

					} else if ("pk_resacostcenter".equals(fieldCode)) {
		                JComponent compent = ERMQueryActionHelper
		                .getFiltComponentForInit(event);
		                UIRefPane refPane = (UIRefPane) compent;
		                CostCenterTreeRefModel model = (CostCenterTreeRefModel)refPane.getRefModel();
		                model.setCurrentOrgCreated(false);
		                model.setOrgType("pk_profitcenter");
		            } else if ("start_period".equals(fieldCode) || 
		                    "end_period".equals(fieldCode)) {
                        UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
                            .getFiltComponentForInit(event);
                        UIRefPane pk_orgRefPane = BXQryTplUtil.getRefPaneByFieldCode((QueryConditionDLG)dlg, ExpamtinfoVO.PK_ORG);
                        if (pk_orgRefPane != null) {
                            String[] pkOrgs = ((String[])pk_orgRefPane.getValueObj());
                            if (!ArrayUtils.isEmpty(pkOrgs)) {
                                initAccPeriod(refPane, pkOrgs[0]);
                            }
                        } else {
                            String pk_org = ErUiUtil.getDefaultPsnOrg();
                            initAccPeriod(refPane, pk_org);
                        }
		            }
		            JComponent compent = ERMQueryActionHelper.getFiltComponentForInit(event);
		            if (compent instanceof UIRefPane) {
		                UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
		                        .getFiltComponentForInit(event);
		                AbstractRefModel refModel = refPane.getRefModel();
		                
		                if (refModel instanceof nc.ui.bd.ref.busi.UserDefaultRefModel ||
		                        refModel instanceof nc.ui.bd.ref.model.PsndocDefaultRefModel) {
		                    LoginContext contextLogin = (LoginContext)context.getAttribute("key_private_context");
		                    refPane.setMultiCorpRef(true);
		                    refPane.setMultiOrgSelected(true);
		                    refPane.setMultiRefFilterPKs(contextLogin.getPkorgs());
		                    refModel.setPk_org(refModel.getPk_group());
		                }
		                
		                if (!(refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel)) {
                            refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
		                }
		            }
				} else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
					if (event.getFieldCode().equals(JKBXHeaderVO.PK_ORG)) {
						UIRefPane pk_orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
										JKBXHeaderVO.PK_ORG, false);
						if (pk_orgRefPane == null) {
							return;
						}
						String pk_org = pk_orgRefPane.getRefPK();
						UIRefPane period = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
										"accperiod", false);
						initAccPeriod(period, pk_org);

						UIRefPane start_period = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
						        "start_period", false);
						initAccPeriod(start_period, pk_org);
						
						UIRefPane end_period = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
                                "end_period", false);
						initAccPeriod(end_period, pk_org);
					}
				}
			}
		});
		
		dlg.getQryCondEditor().addQSSerializeListener(new QSSerializeListener() {

            @Override
            public void unserialize(QuerySchemeObject qsobject) {
                        UIRefPane refPaneOrg = BXQryTplUtil
                                .getRefPaneByFieldCode((QueryConditionDLG) dlg,
                                        "pk_org");
                        String pk_org = refPaneOrg.getRefPK();
                        AccountCalendar calendar = AccountCalendar
                                .getInstanceByPk_org(pk_org);

                        // 获取当前业务日期
                        UFDate currBusiDate = WorkbenchEnvironment
                                .getInstance().getBusiDate();
                        try {
                            calendar.setDate(currBusiDate);
                        } catch (Exception e) {
                            Logger.error(e.getMessage(), e);
                        }
                        AccperiodmonthVO accperiodmonthVO = calendar
                                .getMonthVO();
                        if (accperiodmonthVO == null)
                            return;
                        UIRefPane accRefPane = BXQryTplUtil
                                .getRefPaneByFieldCode((QueryConditionDLG) dlg,
                                        "accperiod");
                        // 设置会计年度的过滤条件
                        ((AccPeriodDefaultRefModel) accRefPane.getRefModel())
                                .setDefaultpk_accperiodscheme(accperiodmonthVO
                                        .getPk_accperiodscheme());
                        accRefPane.setPK(accperiodmonthVO
                                .getPk_accperiodmonth());
            }

            @Override
            public void serialize(QuerySchemeObject qsobject) {
                
            }
            
        });
	}
	
	private void initAccPeriod(UIRefPane refPane, String pk_org) {
	    if (refPane == null) {
            return;
        }
        AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
        String pkAccperiodscheme = calendar.getMonthVO().getPk_accperiodscheme();
        ((AccPeriodDefaultRefModel) refPane.getRefModel())
                .setDefaultpk_accperiodscheme(pkAccperiodscheme);
        refPane.setValueObjFireValueChangeEvent(calendar.getMonthVO().getPk_accperiodmonth());
	}
	
//	@Override
//	protected QueryConditionDLG createQueryDlg(Container parent, TemplateInfo ti, IContext context,
//			IQueryCondition oldCondition) {
//		QueryConditionDLG dlg = super.createQueryDlg(parent, ti, context, oldCondition);
//		initDlgListener(dlg,context);
//				return dlg;
//
//	}

	@Override
    protected QueryConditionDLG createQueryDlg(Container parent, TemplateInfo ti, final IContext context,
            IQueryCondition oldCondition) {

        QueryConditionDLG dlg = new QueryConditionDLG(parent, ti,
                getTitle(context)) {

            private static final long serialVersionUID = 5297678620252168612L;

            public String checkCondition() {
                // 执行父类校验
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
//                    totalVo.getTempletVO().setMetaclass(beanId);
                }
                return condition;
            }
            
        };
        
        dlg.getQryCondEditor().getQueryContext().setMultiTB(true);
        
        initDlgListener(dlg,context);
        qryDlg = dlg;
        return dlg;
    }
    
    private QueryConditionDLG qryDlg;

    @Override
    protected QueryConditionDLG getQryDlg() {
        return qryDlg;
    }
	
}