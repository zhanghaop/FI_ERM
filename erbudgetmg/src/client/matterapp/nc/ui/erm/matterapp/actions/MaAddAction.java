package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.actions.AddLineAction;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

public class MaAddAction extends AddAction {
	private static final long serialVersionUID = 1L;

	private AddLineAction addLineAction;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 校验交易类型是否封存
		checkBilltype();
		super.doAction(e);
		getAddLineAction().doAction(e);
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
		String errorMsg = this.getBtnName() + ErmActionConst.FAIL_MSG;
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
}
