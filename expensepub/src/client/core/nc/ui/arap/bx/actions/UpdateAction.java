package nc.ui.arap.bx.actions;

import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.listeners.BxCardHeadEditListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;

/**
 * nc.ui.arap.bx.actions.UpdateAction
 * 
 * @author twei
 * 
 *         �޸ĵ���
 * 
 */
public class UpdateAction extends AddAction {

	public void update() throws BusinessException {
		String msg = null;
		JKBXVO bxvo = getSelBxvos()[0];
		if (bxvo.getParentVO().getQcbz().booleanValue()) {
			msg = ActionUtils.checkBillStatus(bxvo
					.getParentVO().getDjzt(), MessageVO.EDIT, new int[] {
					BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved,
					BXStatusConst.DJZT_Sign });
			if (bxvo.getContrastVO() != null
					&& bxvo.getContrastVO().length != 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000418")/*
																			 * @res
																			 * "�ڳ����Ѿ������˳������������ܽ����޸�!"
																			 */);
			}

		} else if (bxvo.getParentVO().isInit()) {
			String funcode = getMainPanel().getFuncCode();
			UFBoolean isGroup = BXUiUtil.isGroup(funcode);

			if (!isGroup.equals(bxvo.getParentVO().getIsinitgroup())) {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("expensepub_0",
								"02011002-0018")/* @res "�����޸�������λ�����ţ����õĳ��õ������ݣ�" */);
			}

		} else {
			msg = ActionUtils.checkBillStatus(bxvo
					.getParentVO().getDjzt(), MessageVO.EDIT, new int[] {
					BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved });
		}

		if (!StringUtils.isNullWithTrim(msg)) {
			throw new BusinessException(msg);
		}

		if (!getBxParam().getIsQc()) {
			// ������Ȩ������
			BxCardHeadEditListener.initSqdlr(getMainPanel(), getCardPanel()
					.getHeadItem(JKBXHeaderVO.JKBXR), getVoCache()
					.getCurrentDjlxbm(), getCardPanel().getHeadItem(
					JKBXHeaderVO.DWBM));
		}

		if (isList()) {
			CardAction action = new CardAction();
			action.setActionRunntimeV0(getActionRunntimeV0());
			action.changeTab(BillWorkPageConst.CARDPAGE, true, false, bxvo);
		}

		getMainPanel().setCurrentPageStatus(BillWorkPageConst.WORKSTAT_EDIT);

		BxCardHeadEditListener bxCardHeadEditListener = new BxCardHeadEditListener();
		bxCardHeadEditListener.setActionRunntimeV0(this.getActionRunntimeV0());

		// �༭״̬Ĭ��Ϊ���ɸ���
		UFBoolean isGroup = BXUiUtil.isGroup(getMainPanel().getFuncCode());
		if (!isGroup.booleanValue()) {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setEnabled(false);
			//v6.1����
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setEnabled(false);
		}
		getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).setEnabled(false);
		getBillCardPanel().getHeadItem(JKBXHeaderVO.DJZT).setEnabled(false);
		getBillCardPanel().getHeadItem(JKBXHeaderVO.OPERATOR).setEnabled(false);

		getBillCardPanel().getTailItem(JKBXHeaderVO.APPROVER).setEnabled(false);
		getBillCardPanel().getTailItem(JKBXHeaderVO.SHRQ).setEnabled(false);

		// �����Ϣ
		getBillCardPanel().getTailItem(JKBXHeaderVO.CREATOR).setEnabled(false);
		getBillCardPanel().getTailItem(JKBXHeaderVO.CREATIONTIME).setEnabled(
				false);

		getBillCardPanel().getTailItem(JKBXHeaderVO.MODIFIER).setEnabled(false);
		getBillCardPanel().setTailItem(JKBXHeaderVO.MODIFIER,
				getBxParam().getPk_user());

		getBillCardPanel().getTailItem(JKBXHeaderVO.MODIFIEDTIME).setEnabled(
				false);

		if (bxvo.getParentVO().getPk_org() != null) {
			// ���ñ��һ����Ƿ��ܱ༭
			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM)
					.getValueObject() != null
					&& !getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM)
							.getValueObject().equals(
									Currency.getOrgLocalCurrPK(bxvo
											.getParentVO().getPk_org())))
				getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL)
						.setEnabled(true);
			else
				getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(
						false);
		}
		
		// ������ػ����ܷ�༭,����Ҫ�������û��ʣ��ͻ��ʵľ���
		String pk_currtype =(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject();//ԭ�ұ���
		setCurrRateEnable(bxvo.getParentVO().getPk_org(), pk_currtype);
	}
}