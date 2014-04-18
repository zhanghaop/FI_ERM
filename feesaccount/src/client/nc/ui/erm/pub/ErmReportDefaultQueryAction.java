package nc.ui.erm.pub;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IFipubReportQryDlg;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.report.AbsReportQueryAction;
import nc.ui.pub.beans.UIDialogEvent;
import nc.ui.pub.beans.UIDialogListener;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.utils.fipub.FipubReportResource;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.PubConstData;
import nc.vo.erm.pub.ErmBaseQueryCondition;
import nc.vo.fipub.report.FipubBaseQueryCondition;
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
 * <p>
 * 报销管理查询模板对话框 继承次类，需要传入对应的setM_iSysCode
 * 不同的单据传入不同的数值既可,这里loandlg为功能的对话框模板，对于查询模板以及下面的选项需要置入。 而有上角的内容需要从表中读取
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-9-19 下午06:58:51
 */
@SuppressWarnings("restriction")
public class ErmReportDefaultQueryAction extends AbsReportQueryAction implements
		PubConstData, UIDialogListener {

    protected IQueryCondition createQueryCondition(IContext context, IReportQueryCond qryCondVO) {
        IQueryCondition condition = super.createQueryCondition(context, qryCondVO);
        
        String fieldCode = "zb.pk_tradetype";
        UIRefPane refPane = 
            BXQryTplUtil.getRefPaneByFieldCode(getQryDlg(), fieldCode);
        if (refPane == null) {
            fieldCode = "src_tradetype";
            refPane = BXQryTplUtil.getRefPaneByFieldCode(getQryDlg(), fieldCode);
        }
        if (refPane != null) {
            String[] codes = refPane.getRefCodes();
            if (codes != null) {
                qryCondVO.getUserObject().put(fieldCode, codes);
            }
        }
//        Map<String, Object> fieldMap = new HashMap<String, Object>();
//        List<IFilterEditor> simpleEditorFilterEditors = getQryDlg().getSimpleEditorFilterEditors();
//        for(IFilterEditor editor: simpleEditorFilterEditors){
//            DefaultFilterEditor filterEditor = (DefaultFilterEditor) editor;
//            if (filterEditor.getFilter().getSqlString() != null) {
//                fieldMap.put(filterEditor.getFilterMeta().getFieldCode(), 
//                        filterEditor.getFilter().getSqlString());                   
//            }
//        }
//        qryCondVO.getUserObject().put("fieldSqlMap", fieldMap);
        
        return condition;
    }
    
    @Override
    protected FipubBaseQueryCondition createQueryCondition(boolean isContinue,
            IReportQueryCond qryCondVO) {
        return new ErmBaseQueryCondition(true, qryCondVO);
    }

    private void filterWhere(ReportQueryCondVO qryCondVO, String targetField) {
        if (qryCondVO.getWhereSql().indexOf(targetField) >= 0) {
            String sWhere = qryCondVO.getWhereSql();
            String rep = "'" + System.currentTimeMillis() + "'";
            sWhere = sWhere.replaceAll(targetField, rep);
            qryCondVO.setWhereSql(sWhere);
        }
    }
    
	/**
	 * 设置报表穿透参数<br>
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

		if (qryCondVO.getWhereSql() != null) {
		    //单据编号
		    filterWhere(qryCondVO, "zb.src_billno");
            //事由
            filterWhere(qryCondVO, "zb.reason");
            //报销核算方式
            filterWhere(qryCondVO, "zb.bx_jsfs");
		}
		
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

			boolean isTotal = false;
			// 处理穿透时的币种
//			ReportInitializeVO initHeadVO = (ReportInitializeVO) qryCondVO.getRepInitContext().getParentVO();
//			if (IPubReportConstants.ACCOUNT_FORMAT_FOREIGN.equals(initHeadVO.getReportformat())) {
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
					
					String org = (String)traceData.getValue("org");
					if (IErmReportConstants.getCONST_ALL_TOTAL().equals(org)) {
					    isTotal = true;
					}
				}
				//非总计，设置财务组织
				if (!isTotal) {
	                qryCondVO.setPk_orgs(pkOrgs.keySet().toArray(new String[pkOrgs.size()]));
				}
				
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
//			}

			((BaseQueryCondition) drillParam.getSrcCondition()).setUserObject(qryCondVO);
		}


		return drillParam.getSrcCondition();
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

    @Override
    protected  IFipubReportQryDlg getQryDlg(Container parent,
            final IContext context,
			String nodeCode, int iSysCode, String title) {
        TemplateInfo tempinfo = new TemplateInfo();
        String defaultOrgUnit = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group(); // 获得对应的查询模板集团
        tempinfo.setPk_Org(defaultOrgUnit); // 获得对应的查询模板的业务单元
        tempinfo.setCurrentCorpPk(defaultOrgUnit);
        tempinfo.setFunNode(nodeCode);
        tempinfo.setUserid(WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());

        AbstractFunclet funclet = (AbstractFunclet) ((LoginContext) context
                .getAttribute("key_private_context")).getEntranceUI();
        ErmReportQryDlg ermReportQryDlg = new ErmReportQryDlg(parent,
                context, nodeCode, iSysCode, tempinfo,
                FipubReportResource.getQryCondSetLbl(),
                funclet.getParameter("djlx")) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    protected List<Integer> getPanelHeightList() {
                        List<Integer> list = new ArrayList<Integer>(3);
                        list.add(Integer.valueOf(305));
                        list.add(Integer.valueOf(145));
                        list.add(Integer.valueOf(120));
                        return list;
                    }
            
        };
        ermReportQryDlg.setSize(BXConstans.WINDOW_WIDTH, BXConstans.WINDOW_HEIGHT);
        ermReportQryDlg.addUIDialogListener(this);
        ermReportQryDlg
                .registerCriteriaEditorListener(new ICriteriaChangedListener() {
                    @Override
                    public void criteriaChanged(CriteriaChangedEvent event) {
                        handleCriteriaChanged(event, context);
                    }

        });

		synchronized (ErmReportDefaultQueryAction.class) {
		    if (dlg == null) {
	            dlg = ermReportQryDlg;
		    }
		}
		return dlg;
	}

    protected ErmReportQryDlg getQryDlg() {
        return (ErmReportQryDlg)dlg;
    }

    protected void handleCriteriaChanged(CriteriaChangedEvent event,
            final IContext context) {
        if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
            String fieldCode = event.getFieldCode();
            if ("pk_org".equals(fieldCode) || "bx_org".equals(fieldCode) || "pk_fiorg".equals(fieldCode)) {
                JComponent compent = ERMQueryActionHelper
                        .getFiltComponentForInit(event);
                if (compent instanceof UIRefPane) {
                    UIRefPane refPane = (UIRefPane) compent;
                    nc.ui.bd.ref.AbstractRefModel model = refPane.getRefModel();
                    if (model instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel) {
                        LoginContext mContext = (LoginContext) context
                                .getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
                        model.setFilterPks(mContext.getPkorgs());
                    }
                }
            } else if ("pk_resacostcenter".equals(fieldCode) || "zb.pk_resacostcenter".equals(fieldCode)) {
                JComponent compent = ERMQueryActionHelper
                .getFiltComponentForInit(event);
                UIRefPane refPane = (UIRefPane) compent;
                CostCenterTreeRefModel model = (CostCenterTreeRefModel)refPane.getRefModel();
                model.setCurrentOrgCreated(false);
                model.setOrgType("pk_profitcenter");
            } else {
                JComponent compent = ERMQueryActionHelper.getFiltComponentForInit(event);
                if (compent instanceof UIRefPane) {
                    UIRefPane ref = (UIRefPane) ERMQueryActionHelper
                            .getFiltComponentForInit(event);
                    if (ref != null && ref.getRefModel() != null) {
                        String pk_org = ErUiUtil.getBXDefaultOrgUnit();
                        ref.getRefModel().setPk_org(pk_org);
                    }
                }
            }
        } else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {

//             String fieldCode = event.getFieldCode();
//             if (ExpenseBalVO.ASSUME_ORG.equals(fieldCode)) {
//                 // 费用承担单位变化后，设置费用承担部门和成本中心
//                 setFydwFilter(event);
//             }
//             if (ExpenseBalVO.PK_ORG.equals(fieldCode)) {
//                 UIRefPane refPane = (UIRefPane) ERMQueryActionHelper
//                         .getFiltComponentForValueChanged(event, "pk_org", false);
//                 filterOrg(event, refPane.getRefPK());
//             }

        }
    }

	@Override
    public void dialogClosed(UIDialogEvent event) {
	}

	@Override
    public int getSysCode() {
		/** 系统标识号:3——应收；4——应付；5——报销借款 6 ——报销费用 */
		return 5;
	}

}
