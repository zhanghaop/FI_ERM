package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.RefreshSingleAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class MaRefreshSingleAction extends RefreshSingleAction {
	private static final long serialVersionUID = 1L;
	
	private BillForm cardpanel;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object obj = model.getSelectedData();
		if (obj != null) {
			AggregatedValueObject billvo = (AggregatedValueObject) obj;
			CircularlyAccessibleValueObject parentvo = billvo.getParentVO();
			if (parentvo != null) {
				AggMatterAppVO aggvo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
						.queryBillByPK(billvo.getParentVO().getPrimaryKey());

				if (aggvo != null) {
					model.directlyUpdate(aggvo);
					MatterAppUiUtil.resetHeadDigit(getCardpanel().getBillCardPanel(), aggvo.getParentVO().getPk_org(), aggvo
							.getParentVO().getPk_currtype());
				} else {
					// �����Ѿ���ɾ��
					throw new BusinessException(NCLangRes.getInstance()
							.getStrByID("uif2", "RefreshSingleAction-000000")/*
																			 * �����Ѿ���ɾ��
																			 * ��
																			 * �뷵���б����
																			 * ��
																			 */);
				}
			}

			ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes.getInstance().getStrByID("common", "UCH007")/* "ˢ�³ɹ���" */,
					model.getContext());
		}
	}

	public BillForm getCardpanel() {
		return cardpanel;
	}

	public void setCardpanel(BillForm cardpanel) {
		this.cardpanel = cardpanel;
	}
}
