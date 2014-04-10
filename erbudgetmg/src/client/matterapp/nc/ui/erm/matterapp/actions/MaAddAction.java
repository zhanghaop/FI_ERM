package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.actions.AddLineAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

/**
 * 申请单新增按钮
 * @author chenshuaia
 *
 */
public class MaAddAction extends AddAction {
	private static final long serialVersionUID = 1L;

	private AddLineAction addLineAction;
	
	private BillForm billform;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 校验交易类型是否封存
		checkBilltype();
		
		// 新增切换交易类型
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(getModel().getContext().getNodeCode())) {
			String oldDjlxbm = ((MAppModel) getModel()).getDjlxbm();
			try {
				String selectDjlxbm = ((MAppModel) getModel()).getSelectBillTypeCode();
				((MAppModel) getModel()).setDjlxbm(selectDjlxbm);
				if (selectDjlxbm != null && !selectDjlxbm.equals(billform.getNodekey())) {
					((MatterAppMNBillForm) billform).loadCardTemplet(selectDjlxbm);
				}
			} catch (Exception e2) {
				((MAppModel) getModel()).setDjlxbm(oldDjlxbm);
				billform.setNodekey(oldDjlxbm);
				throw e2;
			}
		}
		
		super.doAction(e);

		BillTabbedPane tabPane = billform.getBillCardPanel().getBodyTabbedPane();
		int index = tabPane.indexOfComponent(billform.getBillCardPanel().getBodyPanel(
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL));
		if (index >= 0) {
			billform.getBillCardPanel().getBodyTabbedPane().setSelectedIndex(index);
			getAddLineAction().doAction(e);
		}
	}

	private void checkBilltype() throws BusinessException {
		String djlxbm = ((MAppModel) getModel()).getDjlxbm();
		DjLXVO tradeTypeVo = ((MAppModel) getModel()).getTradeTypeVo(djlxbm);
		if (tradeTypeVo == null || tradeTypeVo.getFcbz().booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000171")/**
			 * @res
			 *      * "该节点单据类型已被封存，不可操作节点！"
			 */
			);
		}
		
		
		
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
													 * @ res "{0}失败！"
													 */;
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(null);
	}

	public AddLineAction getAddLineAction() {
		return addLineAction;
	}

	public void setAddLineAction(AddLineAction addLineAction) {
		this.addLineAction = addLineAction;
	}

	public BillForm getBillform() {
		return billform;
	}

	public void setBillform(BillForm billform) {
		this.billform = billform;
	}
}
