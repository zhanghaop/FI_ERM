package nc.ui.er.reimrule.action;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.IActionCode;
import nc.itf.fi.pub.Currency;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.AbstractShowMsgExceptionHandler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.BatchAddLineAction;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class BatchAddRuleAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;
	
	public BatchAddRuleAction() {
		//控制维度新增按钮快捷键：Alt+I
		String addStr = NCLangRes.getInstance().getStrByID("uif2", "BatchAddLineAction-000000")/*新增*/;
		ActionInitializer.initializeAction(this, IActionCode.ADDLINE);
		setBtnName(addStr);
		setCode(IActionCode.ADD); 
		putValue(Action.SHORT_DESCRIPTION, addStr + "(Alt+I)");
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.ALT_MASK));
	}

	@Override
	protected boolean isActionEnable() {
		return getModel().getUiState() == UIState.EDIT;
	}

	@Override
	protected void setDefaultData(Object obj) {
		if (obj != null) {
			String pk = null;
			try {
				if(getModel().getContext().getPk_org().equals(ReimRulerVO.PKORG))
					pk = Currency.getGroupLocalCurrPK(getModel().getContext().getPk_group());
				else
					pk = Currency.getOrgLocalCurrPK(getModel().getContext().getPk_org());
			} catch (BusinessException e) {
				
			}
			if(pk==null)
				pk="";
			((SuperVO)obj).setAttributeValue(ReimRulerVO.PK_CURRTYPE, pk);
			((SuperVO)obj).setAttributeValue(ReimRulerVO.PK_CURRTYPE_NAME,Currency.getCurrInfo(pk).getAttributeValue(getMultiFieldName("name")));
		}
	}

	/**
	 * 获取多语字段名称（name2,name,name3）
	 * @param namefield
	 * @return
	 */
	private String getMultiFieldName(String namefield) {
		int intValue = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		if(intValue>1){
			namefield=namefield+intValue;
		}
		return namefield;
	}
	
	/**
	 * 调整action的错误提示信息
	 */
	protected void processExceptionHandler(Exception ex) {
		if (!(exceptionHandler instanceof AbstractShowMsgExceptionHandler))
			exceptionHandler.handlerExeption(ex);
		else {
			AbstractShowMsgExceptionHandler dhandler = (AbstractShowMsgExceptionHandler) exceptionHandler;
			dhandler.setErrormsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0029")/*@res "新增失败!"*/);
			dhandler.handlerExeption(ex);
		}
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}

}
