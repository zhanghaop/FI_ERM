package nc.ui.arap.bx.btnstatus;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.ui.arap.bx.BxParam;
import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.arap.engine.IButtonStatus;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.pf.IPfRetCheckInfo;

/**
 * nc.ui.arap.bx.btnstatus.BXDefaultBtnStatusListener
 * 
 * @author twei
 * 
 *         借款报销默认按钮状态监听器
 * 
 */
public class BXDefaultBtnStatusListener extends BXDefaultAction implements
		IButtonStatus {

	protected boolean status = true;

	/**
	 * 联查状态下，只显示下列的按钮 卡片，列表，联查，打印
	 */
	public void dealForLinkMode(ExtButtonObject bo, IActionRuntime runtime) {
		if (!bo.isVisible())
			return;
		if (getBxParam().getNodeOpenType() == BxParam.NodeOpenType_Link) {
			if (bo.getBtninfo().getParentid().equals("print_action")
					|| bo.getBtninfo().getId().equals("print_action")
					|| bo.getBtninfo().getParentid().equals("fi_arap_boLinkQuery")
					|| bo.getBtninfo().getId().equals("fi_arap_boLinkQuery")) {
				bo.setVisible(true);
				if (bo.getParent() != null) {
					bo.getParent().setVisible(true);
				}
			} else if (bo.getBtninfo().getBtncode().equals("Document")
					|| bo.getBtninfo().getBtncode().equals("Card")
					|| bo.getBtninfo().getBtncode().equals("Return")) {
					bo.setVisible(true);
				if (bo.getParent() != null) {
					bo.getParent().setVisible(true);
				}
			} else {
				bo.setVisible(false);
			}
		} else if (getBxParam().getNodeOpenType() == BxParam.NodeOpenType_Approve) {

			String[] butns = new String[] { "Add", "Return", "Tempsave","Copy", "Delete", "Bill" };
			for (String butn : butns) {
				if (bo.getBtninfo().getBtncode().equals(butn)) {
					bo.setVisible(false);
				}
			}
		} else {
			if (runtime.getCurrWorkPage() == Integer.parseInt(bo.getBtninfo()
					.getPageid())) {
				bo.setVisible(true);
			} else {
				bo.setVisible(false);
			}
		}
		final String[] buttonsQcNotVisi = new String[] { "Approve", "UnApprove",
				"预算执行情况", "联查凭证", "审批情况", "联查事项审批单", "联查往来单", "联查借款单",/*-=notranslate=-*/
				"联查报销制度", "联查报销标准", "联查资金计划", "Bill" };/*-=notranslate=-*/
		if (getMainPanel().getBxParam().getIsQc()) {
			for (String button : buttonsQcNotVisi) {
				if (bo.getBtninfo().getBtncode().equals(button)) {
					bo.setVisible(false);
				}
			}
		}
	}

	public void updateButtonStatus(ExtButtonObject bo, IActionRuntime runtime) {

		setActionRunntimeV0(runtime);

		if (bo == null || runtime == null) {
			return;
		}

		dealForLinkMode(bo, runtime);

		// 联查按钮控制
		if (bo.getBtninfo().getParentid().equals("fi_arap_boLinkQuery")
				|| bo.getBtninfo().getId().equals("fi_arap_boLinkQuery")) {
			if (getCurrentSelectedVO() == null
					|| getCurrentSelectedVO().getParentVO() == null
					|| StringUtils.isNullWithTrim(getCurrentSelectedVO()
							.getParentVO().getPk_jkbx())) {
				setStatus(bo, false);
				return;
			} else {
				if (runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT) {
					setStatus(bo, false);
					return;
				} else {
					setStatus(bo, true);
					return;
				}
			}
		}
		if (bo.getBtninfo().getBtncode().equals("Document")) {
			if (runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE
					|| runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT) {
				setStatus(bo, true);
				bo.getParent().setEnabled(true);
				return;
			} else {
				setStatus(bo, false);
				return;
			}
		}
		if (bo.getBtninfo().getBtncode().equals("条码输入")) {/*-=notranslate=-*/
			if (runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE) {
				setStatus(bo, true);
				return;
			} else {
				setStatus(bo, false);
				return;
			}
		}
		if (runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_NEW
				|| runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT) {
			if (bo.getBtninfo().getBtncode().equals("Tempsave")) {
				bo.setEnabled(runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_NEW
						|| getSelBxvos()[0].getParentVO().getDjzt().equals(BXStatusConst.DJZT_TempSaved));
				return;
			}
			if (bo.getBtninfo().getBtncode().equals("单据操作")/*-=notranslate=-*/
					|| bo.getBtninfo().getBtncode().equals("Save")
					|| bo.getBtninfo().getBtncode().equals("Cancel")
					|| (bo.getBtninfo().getParentid().equals("slinebutton")
					|| bo.getBtninfo().getBtncode().equals("行操作")/*-=notranslate=-*/
					|| bo.getBtninfo().getBtncode().equals("AddLine") 
					|| bo.getBtninfo().getBtncode().equals("DelLine"))) {
				setStatus(bo, true);
			} else if (runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT
					&& (bo.getBtninfo().getParentid().equals("fi_arap_boLinkQuery") 
							|| bo.getBtninfo().getId().equals("fi_arap_boLinkQuery"))) {
				setStatus(bo, true);
			} else {
				setStatus(bo, false);
				return;
			}

		} else if (runtime.getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE) {
			if (bo.getBtninfo().getBtncode().equals("Tempsave")
					|| bo.getBtninfo().getBtncode().equals("Save")
					|| bo.getBtninfo().getBtncode().equals("Cancel")
					|| (bo.getBtninfo().getParentid().equals("slinebutton")
					|| bo.getBtninfo().getBtncode().equals("行操作")/*-=notranslate=-*/
					|| bo.getBtninfo().getBtncode().equals("AddLine") 
					|| bo.getBtninfo().getBtncode().equals("DelLine"))) {
				setStatus(bo, false);
				return;
			} else {
				setStatus(bo, true);
			}
		}

		if (!getBxParam().getIsQc() && !getBxParam().isInit()) {
			if (!isCanAddRow(getCardPanel().getCurrentBodyTableCode())) {
				if ((bo.getBtninfo().getParentid().equals("slinebutton")
						|| bo.getBtninfo().getBtncode().equals("行操作")/*-=notranslate=-*/
						|| bo.getBtninfo().getBtncode().equals("AddLine") 
						|| bo.getBtninfo().getBtncode().equals("DelLine"))) {
					setStatus(bo, false);
					return;
				}
			}
		}

		if (getVoCache().getDjlxVOS().length == 1) {
			if (bo.getBtninfo().getBtncode().equals("交易类型")) {/*-=notranslate=-*/
				bo.setVisible(false);
				setStatus(bo, false);
			}
		}

		if (bo.getBtninfo().getBtncode().equals("Refresh")) {
			setStatus(bo, true);
		}

		if (bo.getBtninfo().getBtncode().equals("Query")) {
			if (runtime.getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {
				if (getBxParam().getNodeOpenType() == BxParam.NodeOpenType_Link
						|| getBxParam().getNodeOpenType() == BxParam.NodeOpenType_Approve) {
					bo.setVisible(false);
				} else {
					bo.setVisible(true);
				}
			} else {
				bo.setVisible(false);
			}
		}

		if (bo.getBtninfo().getBtncode().equals("Edit")) {
			try {
				JKBXVO vo = getBillValueVO();
				Integer spzt = vo.getParentVO().getSpzt();
				if (spzt != null && (spzt.equals(IPfRetCheckInfo.GOINGON))) {
					String userId = BXUiUtil.getPk_user();
					String billId = vo.getParentVO().getPk_jkbx();
					String billType = vo.getParentVO().getDjlxbm();
					if (!((IPFWorkflowQry) NCLocator.getInstance().lookup(
							IPFWorkflowQry.class.getName())).isCheckman(billId,
							billType, userId)) {
						setStatus(bo, false);
					}
				}
			} catch (Exception e) {

			}
		}
		if (bo.getBtninfo().getBtncode().equals("单据操作")) {/*-=notranslate=-*/
			setStatus(bo, true);
		}
	}

	private void setStatus(ExtButtonObject bo, boolean status) {
		bo.setEnabled(status);
		if (!status) {
			this.status = status;
		}
	}
}
