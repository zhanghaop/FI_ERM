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
import nc.utils.fipub.FipubReportResource;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.uif2.LoginContext;

import com.ufida.dataset.IContext;

/**
 * ��������
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
    
    @SuppressWarnings("serial")
    @Override
    protected IFipubReportQryDlg getQryDlg(Container parent,
            final IContext context,
            String nodeCode, int iSysCode, String title) {

        TemplateInfo tempinfo = new TemplateInfo();
        String defaultOrgUnit = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group(); // ��ö�Ӧ�Ĳ�ѯģ�弯��
        tempinfo.setPk_Org(defaultOrgUnit); // ��ö�Ӧ�Ĳ�ѯģ���ҵ��Ԫ
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
                list.add(IPubReportConstants.BUSINESS_UNIT); // ������֯
//                list.add(IBDMetaDataIDConst.DEPT); // ����
//                list.add(IBDMetaDataIDConst.PSNDOC); // ��Ա
                list.add(IBDMetaDataIDConst.USER); // �û�
                //v6.1�����ɱ�����
//                list.add(IPubReportConstants.MDID_COSTCENTER);
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
