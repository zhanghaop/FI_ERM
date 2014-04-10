package nc.ui.arap.bx.actions;

import nc.bs.logging.Logger;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 *        ���л���Ƭ�б���桡 �� nc.ui.arap.bx.actions.CardAction
 */
public class CardAction extends BXDefaultAction {

	public void changeTab() throws BusinessException {

		BXBillMainPanel mainPanel = getMainPanel();

		if (mainPanel.getCurrWorkPage() == BillWorkPageConst.LISTPAGE)
			changeTab(BillWorkPageConst.CARDPAGE, true, false,
					getCurrentSelectedVO());
		else
			changeTab(BillWorkPageConst.LISTPAGE, true, false,
					getCurrentSelectedVO());

	}

	/**
	 * @param tabIndex
	 * @param isChange
	 * @param bInitializing
	 * @throws BusinessException
	 */
	@SuppressWarnings("deprecation")
	public void changeTab(int tabIndex, boolean isChange,
			boolean bInitializing, JKBXVO zbvo) throws BusinessException {
		BXBillMainPanel mainPanel = (BXBillMainPanel) getActionRunntimeV0();
		if (isChange) {
			if (tabIndex == BillWorkPageConst.LISTPAGE) {
				if (mainPanel.getComponentCount() == 1) {
					mainPanel.add(mainPanel.getListContentPanel(), "LIST");
				}
				(getLayout()).show(mainPanel, "LIST");

			} else {
				if (mainPanel.getComponentCount() == 1) {
					mainPanel.add(mainPanel.getCardContentPanel(), "CARD");
				}
				(getLayout()).show(mainPanel, "CARD");
			}
		}
		mainPanel.setCurrentpage(tabIndex);
		if (tabIndex == BillWorkPageConst.CARDPAGE) { // �л�������
			if (zbvo != null) {

				String djlxbm = getVoCache().getCurrentDjlxbm();

				JKBXHeaderVO parentVO = zbvo.getParentVO();

				if (djlxbm == null || !djlxbm.equals(parentVO.getDjlxbm())) {

					DjLXVO djlxVO = getVoCache()
							.getDjlxVO(parentVO.getDjlxbm());

					if (djlxVO == null)
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2011",
										"UPP2011-000008")/* @res "����������Ϣ��ʧ!" */);

					getVoCache().setCurrentDjlx(djlxVO);

					getVoCache()
							.setCurrentDjpk(zbvo.getParentVO().getPk_jkbx());

					try {
						mainPanel.loadCardTemplet();
					} catch (Exception e) {
						ExceptionHandler.consume(e);
					}
				}
				if ((zbvo.getChildrenVO() == null || zbvo.getChildrenVO().length == 0)
						&& !zbvo.isChildrenFetched()) { // ��ʼ������vo
					zbvo = retrieveChidren(zbvo);
				}

				// ���»����е�����
				getVoCache().addVO(zbvo);

			}

			if (!mainPanel.isCardTemplateLoaded()) {
				try {
					mainPanel.loadCardTemplet();
				} catch (Exception e) {
					ExceptionHandler.consume(e);
				}
			}

			getBxBillCardPanel().setBillValueVO(zbvo);
			getBxBillCardPanel().execHeadTailLoadFormulas();

		} else if (mainPanel.getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {

			if (getVoCache().isChangeView()) {
				mainPanel.updateView();
				getVoCache().setChangeView(false);
			}
		}

		mainPanel.setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);

		String str = getMainPanel().getCurrWorkPage() == BillWorkPageConst.LISTPAGE ? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000107")/*@res "�б�"*/
				: nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000106")/*@res "��Ƭ"*/;
		getMainPanel().showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0001",null,new String[]{str})/*@res "���л���i����"*/);

	}

}