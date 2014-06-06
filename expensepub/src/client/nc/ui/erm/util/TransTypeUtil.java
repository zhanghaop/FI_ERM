package nc.ui.erm.util;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class TransTypeUtil {

	public static final String TRANSTYPE = "transtype";

	/**
	 * 从AbstractFunclet获取参数transtype
	 * 
	 * @param model
	 * @return
	 * @author: wangle
	 */
	public static String getTranstype(AbstractAppModel model) {
		JComponent comp = model.getContext().getEntranceUI();
		String tempBilltype = null;
		if (comp instanceof AbstractFunclet) {
			if (((AbstractFunclet) comp).getFuncletContext() == null) {// 导入导出时会出现为空情况
				IFuncRegisterQueryService service = NCLocator.getInstance().lookup(IFuncRegisterQueryService.class);
				String[][] params;
				try {
					params = service.queryParameter(model.getContext().getNodeCode());
					int count = params == null ? 0 : params.length;
					for (int i = 0; i < count; i++) {
						if (params[i][0].equals(TRANSTYPE)) {
							tempBilltype = params[i][1];
							break;
						}
					}
				} catch (BusinessException e) {
					ExceptionHandler.handleRuntimeException(e);
				}
			} else {
				tempBilltype = ((AbstractFunclet) comp).getParameter(TRANSTYPE);
			}
		}

		if (tempBilltype == null) {
			String nodeCode = model.getContext().getNodeCode();
			if (nodeCode.endsWith(BXConstans.BXCLFJK_CODE)) {
				tempBilltype = BXConstans.BILLTYPECODE_CLFJK;
			} else if (nodeCode.endsWith(BXConstans.BXCLFBX_CODE)) {
				tempBilltype = BXConstans.BILLTYPECODE_CLFBX;
			}
		}
		return tempBilltype;
	}

}
