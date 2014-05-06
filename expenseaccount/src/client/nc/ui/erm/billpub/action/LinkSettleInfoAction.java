package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.cmp.settlement.ICmpSettlementPubQueryService;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.pub.BusinessException;

public class LinkSettleInfoAction extends NCAction {
	
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	
	public LinkSettleInfoAction() {
		super();
		setCode("LinkSettleInfo");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000578")/*@res "联查结算信息"*/);
	}


	@Override
	public void doAction(ActionEvent e) throws Exception {

		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		JKBXHeaderVO head = selectedVO.getParentVO();
		

		SettlementAggVO[] bills = null;

		boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(), BXConstans.TM_CMP_FUNCODE);

		if (!iscmpused) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0005")/*
																																 * @res
																																 * "没有安装现金结算产品，不能查询结算信息情况！"
																																 */);
		} else {
			String pk_jkbx = selectedVO.getParentVO().getPk_jkbx();
			bills = NCLocator.getInstance().lookup(ICmpSettlementPubQueryService.class).queryBillsBySourceBillID(new String[] { pk_jkbx });
		}

		if (bills != null && bills[0] != null) {
			LinkQuery linkQuery = new LinkQuery(bills[0].getParentVO().getPrimaryKey());
			SFClientUtil.openLinkedQueryDialog(BXConstans.SETTLE_FUNCCODE, getEditor(), linkQuery);
		} else {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0003")/*
																																 * @res
																																 * "没有结算相关信息,没有还款金额也没有支付金额,无需结算"
																																 */);
		}

	}
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null
				|| selectedData.getParentVO().getDjzt() == BXStatusConst.DJZT_Invalid) {
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
