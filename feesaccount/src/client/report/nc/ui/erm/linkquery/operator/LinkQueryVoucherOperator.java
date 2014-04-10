package nc.ui.erm.linkquery.operator;

import java.awt.Container;

import javax.swing.Action;

import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.pub.smart.data.IRowData;
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessRuntimeException;

import org.apache.commons.lang.StringUtils;

import com.ufida.report.free.userdef.DefaultMenu;
import com.ufida.report.free.userdef.IMenuActionInfo;

/**
 * 联查凭证操作<br>
 *
 * @author liansg<br>
 * @since V60<br>
 */
@SuppressWarnings("restriction")
public class LinkQueryVoucherOperator implements ITraceDataOperator {

	public Action[] ctreateExtensionActions() {
		return null;
	}

	public void traceData(Container container, TraceDataParam param) {
		if (param == null || param.getRowData() == null) {
			return;
		}

		// 取得选中行数据集
		IRowData rowData = param.getRowData();

		// 取得选中行单据主键
		Object tempValue = rowData.getData("pk_jkbx");
		
		if (tempValue == null || StringUtil.isEmpty(tempValue.toString())) {
            MessageDialog.showHintDlg(container, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "提示"*/, 
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "请选择单据进行联查！"*/);
            return;
        }
		
		String pk_bill = tempValue == null ? null : tempValue.toString();

		// 取得选中行单据类型
		tempValue = rowData.getData("pk_billtype");
		String pk_billtype = tempValue == null ? null : tempValue.toString();

		// 取得选中行集团
		String pk_group = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();

		// 取得选中行业务单元
		tempValue = rowData.getData("pk_org");
		String pk_org = tempValue == null ? null : tempValue.toString();

		if (StringUtils.isEmpty(pk_bill) || StringUtils.isEmpty(pk_billtype)) {
			return;
		}

		// 以下联查凭证
		// 构造FipRelationInfoVO
		FipRelationInfoVO srcInfoVO = new FipRelationInfoVO();
		srcInfoVO.setPk_group(pk_group);
		srcInfoVO.setPk_org(pk_org);
		srcInfoVO.setRelationID(pk_bill);
		srcInfoVO.setPk_billtype(pk_billtype);

		try {
			// 来源方查目标方
			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg((AbstractFunclet) container.getParent(), srcInfoVO);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0024")/*@res "请选择详细单据信息查询！"*/);
		}

	}

	public IMenuActionInfo getMenuItemInfo() {
		String actionName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0025")/*@res "联查凭证"*/;
		DefaultMenu action = new DefaultMenu("LinkQueryVoucher", actionName);
		action.setToolTipText(actionName);
		return action;
	}
}
