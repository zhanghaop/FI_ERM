package nc.ui.erm.accountage;

import java.awt.Container;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fipub.report.IFipubReportQryDlg;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.pub.ErmReportDefaultQueryAction;
import nc.ui.fipub.report.AbsReportQueryAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.utils.fipub.FipubReportResource;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.querytemplate.queryscheme.QuerySchemeObject;
import nc.vo.querytemplate.queryscheme.QuerySchemeVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

import com.ufida.dataset.IContext;
import com.ufida.iufo.table.drill.ReportDrillItem;
import com.ufida.iufo.table.drill.SimpleRowDataParam;
import com.ufida.report.anareport.FreeReportFucletContextKey;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.base.FreeReportDrillParam;
import com.ufida.report.anareport.model.AbsAnaReportModel;

/**
 * 借款账龄分析查询<br>
 *
 * @author liansg<br>
 * @since V60 2011-01-20<br>
 */
@SuppressWarnings("restriction")
public class LoanAccountAgeAnalyzeAryAction extends AbsReportQueryAction {

	private static String getTITLE2() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0012")/*@res "日期期间设置"*/; // 对话框标题
	}
	private DateDlg dateDlg = null; // 日期期间设置对话框

    @Override
    public IQueryCondition doQueryAction(Container parent, IContext context,
			AbsAnaReportModel reportModel, IQueryCondition oldCondition) {

        BaseQueryCondition qryCondition = (BaseQueryCondition) super.doQueryAction(parent, context,
				reportModel, oldCondition);

		if (!qryCondition.isContinue()) {
			// 取消查询
			return new BaseQueryCondition(false);
		}

		ReportQueryCondVO qryCondVO = (ReportQueryCondVO) qryCondition.getUserObject();

		if (IErmReportAnalyzeConstants.getACC_ANA_MODE_DATE().equals(qryCondVO.getAnaMode())) {
			// 按日期分析
			if (getDateDlg(parent, getTITLE2()).showModal() != UIDialog.ID_OK) {
				// 取消查询
				return new BaseQueryCondition(false);
			}

			Object[][] datas = getDateDlg(parent, getTITLE2()).getData();
			qryCondVO.setDatas(datas);

			// 再次将查询条件VO放入BaseQueryCondition
			qryCondition.setUserObject(qryCondVO);
		}

		return qryCondition;
	}

    protected LoanAccountAgeAnalyzeQryDlg getQryDlg() {
        return (LoanAccountAgeAnalyzeQryDlg)dlg;
    }
    
    @Override
    public IQueryCondition doQueryByScheme(Container parent, IContext context,
            AbsAnaReportModel reportModel, IQueryScheme queryScheme) {
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

        if (obj != null && obj instanceof ReportQueryCondVO) {
            ReportQueryCondVO repQryCon = (ReportQueryCondVO)obj;
            fillDateBeginEnd(repQryCon);
        }
        
        if (obj == null) {
            // 取消查询
            return new BaseQueryCondition(false);
        }
        
        return createQueryCondition(context, (IReportQueryCond) obj);
    }

    private ReportQueryCondVO fillDateBeginEnd(ReportQueryCondVO repQryCon) {
        if (repQryCon == null) {
            return null;
        }
        //获取当前业务日期
        UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
        if (IPubReportConstants.QUERY_MODE_MONTH.equals(repQryCon.getQryMode())) {
            String[] pk_orgs = repQryCon.getPk_orgs();
            AccountCalendar calendar = ArrayUtils.isEmpty(pk_orgs) ? 
                    AccountCalendar.getInstance() : 
                        AccountCalendar.getInstanceByPk_org(pk_orgs[0]);
            try {
                calendar.setDate(currBusiDate);
                UFDate beginDate = calendar.getMonthVO().getBegindate();
                UFDate endDate = calendar.getMonthVO().getEnddate();
                repQryCon.setBeginDate(beginDate);
                repQryCon.setEndDate(endDate);
            } catch (InvalidAccperiodExcetion e) {
                Logger.error(e.getMessage(), e);
                repQryCon.setBeginDate(currBusiDate);
                repQryCon.setEndDate(currBusiDate);
            }
        } else {
            repQryCon.setBeginDate(currBusiDate);
            repQryCon.setEndDate(currBusiDate);
        }
        return repQryCon;
    }
    
	/**
	 * 根据发布的节点号，构造查询对话框<br>
	 *
	 * @param parent 父组件<br>
	 * @param context IUFO上下文环境<br>
	 * @param nodeCode 节点编号<br>
	 * @param iSysCode 系统标识<br>
	 * @param ti 模板信息<br>
	 * @param title 对话框标题<br>
	 * @return IFipubReportQryDlg<br>
	 */
	@Override
    protected IFipubReportQryDlg getQryDlg(Container parent, 
            final IContext context, String nodeCode, int iSysCode, String title) {

        TemplateInfo tempinfo = new TemplateInfo();
        // FIXME 暂时用集团代替，注意不能为空，为空找不到查询模板
        String defaultOrgUnit = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group(); // 获得对应的查询模板集团
        tempinfo.setPk_Org(defaultOrgUnit); // 获得对应的查询模板的业务单元
        tempinfo.setCurrentCorpPk(defaultOrgUnit);
        tempinfo.setFunNode(nodeCode);
        tempinfo.setUserid(WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());
        LoanAccountAgeAnalyzeQryDlg qryDlg = new LoanAccountAgeAnalyzeQryDlg(
                parent, context, nodeCode,
                iSysCode, tempinfo, FipubReportResource.getQryCondSetLbl(),
                BXConstans.JK_DJLXBM);
        qryDlg.registerCriteriaEditorListener(new ICriteriaChangedListener() {
            @Override
            public void criteriaChanged(CriteriaChangedEvent event) {
                handleCriteriaChanged(event, context);
            }

        });
        
        synchronized (ErmReportDefaultQueryAction.class) {
            if (dlg == null) {
                dlg = qryDlg;
            }
        }
        
		return dlg;
	}

    private void handleCriteriaChanged(CriteriaChangedEvent event,
            final IContext context) {
        if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
            String fieldCode = event.getFieldCode();
            JComponent compent = ERMQueryActionHelper
            .getFiltComponentForInit(event);
            if ("pk_org".equals(fieldCode) || "pk_fiorg".equals(fieldCode)) {
                if (compent instanceof UIRefPane) {
                    UIRefPane refPane = (UIRefPane) compent;
                    nc.ui.bd.ref.AbstractRefModel model = refPane.getRefModel();
                    if (model instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel) {
                        LoginContext mContext = (LoginContext) context
                                .getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
                        model.setFilterPks(mContext.getPkorgs());
                    }
                }
            } else if ("pk_resacostcenter".equals(fieldCode)) {
                UIRefPane refPane = (UIRefPane) compent;
                CostCenterTreeRefModel model = (CostCenterTreeRefModel)refPane.getRefModel();
                model.setCurrentOrgCreated(false);
                model.setOrgType("pk_financeorg");
            }
            if (compent instanceof UIRefPane) {
                UIRefPane refPane = (UIRefPane)compent;
                refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER);
            }
        } else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {

        }
    }

	protected DateDlg getDateDlg(Container parent, String title) {
		if (dateDlg == null) {
			dateDlg = new DateDlg(parent, title);
		}
		return dateDlg;
	}


	@Override
    protected int getSysCode() {
		return 5;
	}
	/**
	 * 设置报表穿透参数，并执行穿透<br>
	 */
	@Override
    public IQueryCondition doQueryByDrill(Container parent, IContext context,
			AbsAnaReportModel reportModel, FreeReportDrillParam drillParam) {

	    IQueryCondition srcCondition = drillParam.getSrcCondition();
        if (srcCondition == null || ((BaseQueryCondition) srcCondition).getUserObject() == null) {
            return new BaseQueryCondition(false);
        }

        ReportQueryCondVO qryCondVO = (ReportQueryCondVO) ((BaseQueryCondition) srcCondition)
                .getUserObject();

        qryCondVO = (ReportQueryCondVO)qryCondVO.clone();
        List<QryObj> qryObjList = qryCondVO.getQryObjs();

        if (drillParam.getDrillRule() != null && drillParam.getDrillRule().getDrillItem() != null) {
            for (QryObj qryObj : qryObjList) {
                qryObj.getValues().clear();
            }

            String drillItemName = null;
            Object drillItemVaule = null;
            SimpleRowDataParam[] traceDatas = drillParam.getTraceDatas();
            // 获取穿透规则子项
            ReportDrillItem[] drillItems = drillParam.getDrillRule().getDrillItem();

            for (SimpleRowDataParam traceData : traceDatas) {
                for (int i = 0; i < drillItems.length; i++) {
                    drillItemName = drillItems[i].getConditionName();
                    drillItemVaule = traceData.getValue(drillItemName);
                    if (i < qryObjList.size()) {
                        if (drillItemVaule != null
                                && !"".equals(drillItemVaule)
                                && !qryObjList.get(i).getValues()
                                        .contains(drillItemVaule)) {
//                            qryObjList.get(i).getValues().clear();
                            qryObjList.get(i).getValues().add(drillItemVaule);
                        }
                    }
                }
            }

            // 处理穿透时的币种
//          ReportInitializeVO initHeadVO = (ReportInitializeVO) qryCondVO.getRepInitContext().getParentVO();
//          if (IPubReportConstants.ACCOUNT_FORMAT_FOREIGN.equals(initHeadVO.getReportformat())) {
                // 外币金额式查询
                Map<String, String> pkOrgs = new HashMap<String, String>();
                Map<String, String> pkCurrTypeMap = new HashMap<String, String>();
                for (SimpleRowDataParam traceData : traceDatas) {
                    drillItemVaule = traceData.getValue("pk_org");
                    if (drillItemVaule != null && !"".equals(drillItemVaule)) {
                        pkOrgs.put(drillItemVaule.toString(), null);
                    }
                    
                    drillItemVaule = traceData.getValue("pk_currtype");
                    if (drillItemVaule != null && !"".equals(drillItemVaule)) {
                        pkCurrTypeMap.put(drillItemVaule.toString(), null);
                    }
                }
                qryCondVO.setPk_orgs(pkOrgs.keySet().toArray(new String[pkOrgs.size()]));
                StringBuilder sb = new StringBuilder();
                for (String pkCurrType : pkCurrTypeMap.keySet()) {
                    sb.append(pkCurrType).append(",");
                }
                if (pkCurrTypeMap.keySet().size() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    qryCondVO.setPk_currency(sb.toString());
                } else {
                    qryCondVO.setPk_currency(null);
                }
//          }

            ((BaseQueryCondition) drillParam.getSrcCondition()).setUserObject(qryCondVO);
        }


        return drillParam.getSrcCondition();
	}

}

