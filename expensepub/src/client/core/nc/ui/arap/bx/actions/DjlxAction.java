package nc.ui.arap.bx.actions;

import nc.bs.logging.Logger;
import nc.ui.bd.ref.IRefModel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 *         切换单据类型
 * 
 *         nc.ui.arap.bx.actions.DjlxAction
 */
public class DjlxAction extends BXDefaultAction {

	@SuppressWarnings("deprecation")
	public void show() throws BusinessException {

		String nodeCode = getNodeCode();

		boolean isqc = nodeCode.equals(BXConstans.BXLR_QCCODE);
		
		UIRefPane djlxRefPane = getMainPanel().getDjlxRef(isqc);
		
		djlxRefPane.showModel();

		String djlxbm = djlxRefPane.getRefValue("pk_billtypecode") == null ? null
				: String.valueOf(djlxRefPane.getRefValue("pk_billtypecode"));

		if (djlxbm == null || djlxbm.trim().length() < 1) {
			return;
		}

		String strOldDjlxbm = getVoCache().getCurrentDjlxbm();

		if (strOldDjlxbm == null || !strOldDjlxbm.equals(djlxbm)) {

			getVoCache().setCurrentDjlxByPk(djlxbm);
			
			getVoCache().setCurrentDjpk(null);

			getParent().setTitleText(
					String.valueOf(djlxRefPane.getRefValue("pk_billtypecode")));

			// 重新加载卡片模板，使用模板缓存
			try {
				getMainPanel().loadCardTemplet();
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			getMainPanel().getBillCardPanel().getBillData().clearViewData();

			getMainPanel().setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);
			
			getMainPanel().refreshBtnStatus();

		}
	}

	public static DjLXVO getDjlxvoFromRef(IRefModel model) {

		DjLXVO djlxvo = new DjLXVO();

		djlxvo.setAttributeValue("djlxoid", model.getValue("djlxoid"));
		djlxvo.setAttributeValue("djdl", model.getValue("djdl"));
		djlxvo.setAttributeValue("djlxbm", model.getValue("djlxbm"));
		djlxvo.setAttributeValue("djlxmc", model.getValue("djlxmc"));
		djlxvo.setAttributeValue("dwbm", model.getValue("dwbm"));

		return djlxvo;
	}
}
