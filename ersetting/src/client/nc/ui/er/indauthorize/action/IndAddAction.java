package nc.ui.er.indauthorize.action;

import java.awt.event.ActionEvent;

import nc.itf.org.IOrgConst;
import nc.ui.er.indauthorize.view.ControlAreaOrgPanel;
import nc.ui.pf.pub.TranstypeRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.actions.batch.BatchAddLineWithDefValueAction;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.er.indauthorize.IndAuthorizeVO;

public class IndAddAction extends BatchAddLineWithDefValueAction {
	/**
	 * 实现个人授权代理设置的增加功能
	 * 
	 * @see
	 * @author liansg
	 * @since V6.0
	 */
	private static final long serialVersionUID = 1L;

	private ControlAreaOrgPanel caOrgPanel;

	protected void setDefaultValue(Object obj) {
		super.setDefaultValue(obj);
		if (!(obj instanceof IndAuthorizeVO)) {
			return;
		}
		IndAuthorizeVO ind = (IndAuthorizeVO) obj;
		NODE_TYPE nodeType = getModel().getContext().getNodeType();
		String pk_group;
		if (nodeType == NODE_TYPE.GLOBE_NODE) {
			pk_group = IOrgConst.GLOBEORGTYPE;
		} else {
			pk_group = getModel().getContext().getPk_group();
		}
		// 设置集团
		ind.setPk_group(pk_group);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
	}

	public ControlAreaOrgPanel getCaOrgPanel() {
		return caOrgPanel;
	}

	public void setCaOrgPanel(ControlAreaOrgPanel caOrgPanel) {
		this.caOrgPanel = caOrgPanel;
	}
}
