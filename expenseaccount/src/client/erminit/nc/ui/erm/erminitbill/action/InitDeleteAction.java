package nc.ui.erm.erminitbill.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBXBillPublic;
import nc.pubitf.erm.erminit.IErminitQueryService;
import nc.ui.uif2.actions.DeleteAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class InitDeleteAction extends DeleteAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {

		BillManageModel billModel = (BillManageModel) getModel();
		Object[] datas = billModel.getSelectedOperaDatas();
		List<String> orgList = new ArrayList<String>();

		for (Object object : datas) {
			orgList.add(((JKBXVO) object).getParentVO().getPk_org());
		}

		String[] closedOrgs = NCLocator.getInstance().lookup(IErminitQueryService.class)
				.queryStatusByOrgs(orgList.toArray(new String[0]));
		if (ArrayUtils.isEmpty(closedOrgs)) {
			closedOrgs = new String[0];
		}
		List<JKBXVO> handleVOList = new ArrayList<JKBXVO>();
		StringBuffer msg = new StringBuffer();
		for (Object object : datas) {
			List<String> closedOrgList = Arrays.asList(closedOrgs);
			if (closedOrgList.contains(((JKBXVO) object).getParentVO().getPk_org())) {
				// 组织关闭的处理
				msg.append("[" + ((JKBXVO) object).getParentVO().djbh + "]");
			} else {
				handleVOList.add((JKBXVO) object);
			}
		}
		boolean isSuccess = true;
		String msgStr = "";
		if (msg.length() != 0) {
			msgStr = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0055")/*
																									 * @
																									 * res
																									 * "单据号:"
																									 */+ msg.toString()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0056")/*
																										 * @
																										 * res
																										 * " 组织关闭，不能删除 \n"
																										 */;
			isSuccess = false;
		}

		if (handleVOList.size() > 0) {
			MessageVO[] returnMsg = NCLocator.getInstance().lookup(IBXBillPublic.class)
					.deleteBills(handleVOList.toArray(new JKBXVO[0]));
			for (MessageVO msgVO : returnMsg) {
				if (msgVO.isSuccess()) {
					billModel.directlyDelete(msgVO.getSuccessVO());
				} else {
					isSuccess = false;
				}
				msgStr += msgVO.toString();
			}
		}
		if (!isSuccess) {
			throw new BusinessException(msgStr);
		}
	}

	@Override
	protected boolean isActionEnable() {

		return super.isActionEnable();
	}

}