package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;

import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.expamortize.ExpamtinfoVO;

/**
 *
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class LinkBxAction extends NCAction {
	private BillManageModel model;

	public LinkBxAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0058")/*@res "联查报销单"*/);
		setCode("LinkBxd");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ExpamtinfoVO vo = (ExpamtinfoVO) getModel().getSelectedData();
		if (vo != null) {
			String pk_jkbx = vo.getPk_jkbx();
			LinkQuery linkQuery = new LinkQuery(new String[] { pk_jkbx });
			linkQuery.setBillType(BXConstans.BX_DJDL);
			SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE, this
					.getModel().getContext().getEntranceUI(), linkQuery);
		}
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null&&model.getUiState()== UIState.NOT_EDIT;
	}

}