package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.org.IFinanceOrgQryService;
import nc.itf.org.IGroupQryService;
import nc.itf.org.IOrgUnitQryService;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.org.FinanceOrgTreeVO;
import nc.vo.org.GroupVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

public class LinkAppStatusAction extends NCAction{
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	
	public LinkAppStatusAction(){
		super();
		setCode(ErmActionConst.LINKAPPSTATUS);
		setBtnName(ErmActionConst.getLinkAppStatusName());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;
//		NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(null, 
//				null,null,null);
//		ErmFlowCheckInfo check = new ErmFlowCheckInfo();
//		if(check.checkReimRule(selectedVO)== UFBoolean.TRUE)
//			ShowStatusBarMsgUtil.showErrorMsg("´íÎó","³¬±ê×¼", getModel().getContext());
		String pk_jkbx = selectedVO.getParentVO().getPk_jkbx();
		String djlxbm = selectedVO.getParentVO().getDjlxbm();

		FlowStateDlg app = new FlowStateDlg(getEditor(), djlxbm, pk_jkbx, WorkflowTypeEnum.Approveflow.getIntValue());

		app.showModal();
	}
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null) {
			return false;
		}
		return true;
	}
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);

	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	
}
