package nc.ui.er.djlx.listener;

import nc.bs.logging.Log;
import nc.impl.er.proxy.ProxyDjlx;
import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.ml.NCLangRes;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;

/** 增加按钮的处理方法 */
public class SaveButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() throws BusinessException {
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common", "UCH044"));

		getMainFrame().getBillCardPanel().stopEditing();

		try {
			getMainFrame().getBillCardPanel().dataNotNullValidate();
		} catch (ValidationException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			getMainFrame().showHintMessage(e.getMessage());
			throw new BusinessException(e.getMessage());
		}
		BillTypeVO vo = (BillTypeVO) getMainFrame().getBillCardPanel().getBillValueVO("nc.vo.er.djlx.BillTypeVO", "nc.vo.er.djlx.DjLXVO", "nc.vo.er.djlx.DjLXVO");

		check(vo);

		/** 增加一种的新的单据类型 */
		if (getMainFrame().getWorkstat() == BillWorkPageConst.WORKSTAT_NEW) {

			vo = ProxyDjlx.getIArapBillTypePublic().insertBillType(vo);
		}
		/** *修改原有的单据类型 */
		else if (getMainFrame().getWorkstat() == BillWorkPageConst.WORKSTAT_EDIT) {
			checkEdit(vo);
			vo = ProxyDjlx.getIArapBillTypePrivate().updateBillType(vo);

		}
		getBillCardPanel().setEnabled(false);
		getUITree().setEnabled(true);
		getMainFrame().setWorkstat(BillWorkPageConst.WORKSTAT_BROWSE);
		getMainFrame().showHintMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("20060101", "UPP20060101-000054")/* @res "已经成功保存！" */);
		getDataModel().setData(vo);
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common", "UCH005"));

		return true;
	}

	private void checkEdit(BillTypeVO vo) throws BusinessException {

	}

	/**
	 * 
	 * 检查合法性
	 * 
	 * @throws BusinessException
	 * 
	 */
	private void check(BillTypeVO vo) throws BusinessException {
		DjLXVO djlx = (DjLXVO) vo.getParentVO();
		if (djlx.getDwbm() == null) {
			djlx.setDwbm(ErUiUtil.getPK_group());
		}
		if (djlx.getDr() == null) {
			djlx.setDr(0);
		}

	}
}
