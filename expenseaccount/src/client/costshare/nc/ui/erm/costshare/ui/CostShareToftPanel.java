package nc.ui.erm.costshare.ui;

import nc.bs.logging.Logger;
import nc.ui.erm.view.ErmToftPanel;
import nc.ui.pub.linkoperate.ILinkQuery;
import nc.ui.pub.linkoperate.ILinkQueryData;

/**
 * ���ý�ת���ڵ������
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
			Logger.error("������ý�ת��ʧ��", e);
			showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0035")/*@res "����"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0062")/*@res "������ý�ת��ʧ�ܣ�"*/);
		}
	}

}