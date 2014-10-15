package nc.ui.erm.linkquery.operator;

import java.awt.Container;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.pub.smart.data.IRowData;
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.pubitf.erm.expamortize.IExpAmortizeprocQuery;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.expamortize.view.ExpamtprocDialog;
import nc.ui.pub.beans.UIDialog;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;

import com.ufida.report.free.userdef.DefaultMenu;
import com.ufida.report.free.userdef.IMenuActionInfo;

@SuppressWarnings("restriction")
public class ExpamDetailLinkQueryOperator implements ITraceDataOperator {
	private ExpamtprocDialog dialog;

	@Override
	public Action[] ctreateExtensionActions() {
		return null;
	}

	@Override
	public IMenuActionInfo getMenuItemInfo() {
		String actionName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0017")/*@res "联查摊销明细"*/;
		DefaultMenu action = new DefaultMenu("LinkQueryExpDetail", actionName);
		action.setToolTipText(actionName);
		return action;
	}

	@Override
	public void traceData(Container container, TraceDataParam param) {
		if (param == null || param.getRowData() == null) {
			return;
		}
		// 取得选中行数据集
		IRowData rowData = param.getRowData();
		Object value = null;

		// 取得选中行
		value = rowData.getData(ExpamtinfoVO.PK_ORG);
		String pk_org = value == null ? null : value.toString();

		// 取得选中行单据主键
		value = rowData.getData(ExpamtinfoVO.BX_BILLNO);
		String pk_bill = value == null ? null : value.toString();
		String nodecode = BXConstans.EXPAMORTIZE_NODE;

		// 联查具体摊销明细
		String usercode = BXUiUtil.getPk_user();
		String pk_group = BXUiUtil.getPK_group();
		try {
			ExpamtprocVO[] vos = getSerivce().queryByDjbhAndPKOrg(pk_bill,
					pk_org);
			if (vos != null) {
				((ExpamtprocDialog) getAmtPeriodDialog(nodecode, usercode,
						pk_group)).getBillListPanel().setHeaderValueVO(vos);
				dialog.showModal();
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	private UIDialog getAmtPeriodDialog(String nodecode, String usercode,
			String pk_group) {
		if (dialog == null) {
			dialog = new ExpamtprocDialog(nodecode, usercode, pk_group);
		}
		return dialog;
	}

	private IExpAmortizeprocQuery getSerivce() {
		return NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
	}

}