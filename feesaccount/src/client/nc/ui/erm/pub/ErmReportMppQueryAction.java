package nc.ui.erm.pub;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.fipub.report.IFipubReportQryDlg;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.utils.fipub.FipubReportResource;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.uif2.LoginContext;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.model.AbsAnaReportModel;

/**
 * 费用申请
 * @author luolch 
 *
 */
@SuppressWarnings("restriction")
public class ErmReportMppQueryAction extends ErmReportDefaultQueryAction {
	@Override
	public int getSysCode() {
		return ErmReportQryDlg.ERM_MATTERAPP;
	}

    protected IQueryCondition createQueryCondition(IContext context, IReportQueryCond qryCondVO) {
        IQueryCondition condition = super.createQueryCondition(context, qryCondVO);
        
        List<IFilterEditor> simpleEditorFilterEditors = getQryDlg().getSimpleEditorFilterEditors();
        for(IFilterEditor editor: simpleEditorFilterEditors){
            DefaultFilterEditor filterEditor = (DefaultFilterEditor) editor;
            if("zb.orig_amount".equals(filterEditor.getFilterMeta().getFieldCode())){
                qryCondVO.getUserObject().put("zb.orig_amount", 
                        filterEditor.getFilter().getSqlString());
            }
        }
        return condition;
    }
    
    @Override
    public IQueryCondition doQueryByScheme(Container parent, IContext context,
            AbsAnaReportModel reportModel, IQueryScheme queryScheme) {
        IQueryCondition qryCondition = super.doQueryByScheme(parent, context,
                reportModel, queryScheme);
//        nodeValidate(qryCondition);
        return qryCondition;
    }

    @Override
    public IQueryCondition doQueryAction(Container parent, IContext context,
            AbsAnaReportModel reportModel, IQueryCondition oldCondition) {
        IQueryCondition qryCondition = super.doQueryAction(parent, context, reportModel, oldCondition);
//        nodeValidate(qryCondition);
        return qryCondition;
    }
    
//    private void nodeValidate(IQueryCondition qryCondition) {
//        if (qryCondition instanceof ErmBaseQueryCondition) {
//            ErmBaseQueryCondition qryCon = (ErmBaseQueryCondition)qryCondition;
//            if (qryCon.getQryCondVO() != null && qryCon.getQryCondVO().getQryObjs() != null) {
//                List<QryObj> qryObjList = qryCon.getQryCondVO().getQryObjs();
//                boolean validate = true;
//                for (QryObj qryObj : qryObjList) {
//                    if (!IBDMetaDataIDConst.DEPT.equals(qryObj.getPk_bdinfo()) &&
//                            !IBDMetaDataIDConst.PSNDOC.equals(qryObj.getPk_bdinfo())) {
//                        validate = false;
//                        break;
//                    }
//                }
//                if (!validate) {
////                    throw new ErmBusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0122")/*@res "该自定义查询已不可用，请在费用管理-账表初始化中删除该查询节点！"*/);
//                }
//            }
//        }
//    }

    @SuppressWarnings("serial")
    @Override
    protected IFipubReportQryDlg getQryDlg(Container parent,
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
            
            @Override
            protected List<String> getMultiOrgRef() {
                List<String> list = new ArrayList<String>();
                list.add(IPubReportConstants.BUSINESS_UNIT); // 财务组织
//                list.add(IBDMetaDataIDConst.DEPT); // 部门
//                list.add(IBDMetaDataIDConst.PSNDOC); // 人员
                list.add(IBDMetaDataIDConst.USER); // 用户
                //v6.1新增成本中心
//                list.add(IPubReportConstants.MDID_COSTCENTER);
                return list;
            }

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
    
}
