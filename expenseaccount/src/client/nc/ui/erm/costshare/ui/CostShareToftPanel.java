package nc.ui.erm.costshare.ui;

import nc.bs.logging.Logger;
import nc.ui.erm.view.ErmToftPanel;
import nc.ui.pub.linkoperate.ILinkQuery;
import nc.ui.pub.linkoperate.ILinkQueryData;

/**
 * 费用结转单节点入口类
 *
 * @author lvhj
 *
 */
public class CostShareToftPanel extends ErmToftPanel implements ILinkQuery{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	boolean initialized;

	public void doQueryAction(final ILinkQueryData querydata) {
		doQueryAction2(querydata);
	}

	private void doQueryAction2(ILinkQueryData querydata) {
		try {
			Object[] userObject = (Object[]) querydata.getUserObject();
			String[] pks = null;

			if (userObject != null && userObject.length != 0)
				pks = (String[]) (userObject)[0];
			else if (querydata.getBillID() != null)
				pks = new String[] { querydata.getBillID() };

			CostShareDataManager m = (CostShareDataManager) getFactory().getBean("modelDataManager");
			m.initModelByPKs(pks);
		} catch (Exception e) {
			Logger.error("联查费用结转单失败", e);
			showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0035")/*@res "错误"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0062")/*@res "联查费用结转单失败！"*/);
		}
	}

}