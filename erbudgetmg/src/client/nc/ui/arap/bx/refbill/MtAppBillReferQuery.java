package nc.ui.arap.bx.refbill;

import java.awt.Container;

import nc.funcnode.ui.FuncletContext;
import nc.ui.erm.matterapp.listener.MatterQueryActionListener;
import nc.ui.pubapp.billref.src.DefaultBillReferQuery;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.uif2.ToftPanelAdaptor;
import nc.vo.querytemplate.TemplateInfo;

@SuppressWarnings("restriction")
public class MtAppBillReferQuery extends DefaultBillReferQuery {

	public MtAppBillReferQuery(Container c, TemplateInfo info) {
		super(c, info);
	}
	
	
	@Override
	protected void initQueryConditionDLG(QueryConditionDLGDelegator dlgDelegator) {
		super.initQueryConditionDLG(dlgDelegator);

		MtQueryCriteriaEditorListener listener = new MtQueryCriteriaEditorListener();
		FuncletContext context = ((ToftPanelAdaptor) getContain()).getFuncletContext();
		listener.setFuncSubInfo(context.getFuncSubInfo());
		getQueryDlg().registerCriteriaEditorListener(listener);
	}

}
