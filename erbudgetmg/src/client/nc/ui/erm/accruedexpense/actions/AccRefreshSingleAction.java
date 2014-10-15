package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.RefreshSingleAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class AccRefreshSingleAction extends RefreshSingleAction {

	private static final long serialVersionUID = 1L;

	private BillForm billForm;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object obj = model.getSelectedData();
		if (obj != null) {
			AggregatedValueObject billvo = (AggregatedValueObject) obj;
			CircularlyAccessibleValueObject parentvo = billvo.getParentVO();
			if (parentvo != null) {
				AggAccruedBillVO aggvo = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPk(
						billvo.getParentVO().getPrimaryKey());

				if (aggvo != null) {
					model.directlyUpdate(aggvo);
					AccUiUtil.resetHeadDigit(getBillForm().getBillCardPanel(), aggvo.getParentVO().getPk_org(), aggvo
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

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

}
