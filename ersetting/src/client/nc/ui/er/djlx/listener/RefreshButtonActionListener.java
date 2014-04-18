package nc.ui.er.djlx.listener;


import nc.impl.er.proxy.ProxyDjlx;
import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.ml.NCLangRes;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;

/**刷新按钮的处理方法*/
public class RefreshButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() throws BusinessException {
		getMainFrame().showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("20060610","UPP20060610-000001")/*@res "正在刷新"*/);
		BillTypeVO[] vos = ProxyDjlx.getIArapBillTypePrivate().queryBillType(ErUiUtil.getBXDefaultOrgUnit());
		getMainFrame().getDataModel().setDatas(vos);
		getMainFrame().setWorkstat(BillWorkPageConst.WORKSTAT_BROWSE);

		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common","UCH007"));

		return true;
	}
}