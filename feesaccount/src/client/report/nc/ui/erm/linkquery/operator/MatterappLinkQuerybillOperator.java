package nc.ui.erm.linkquery.operator;

import java.awt.Container;

import javax.swing.Action;

import nc.bs.pf.pub.PfDataCache;
import nc.pub.smart.data.IRowData;
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.jcom.lang.StringUtil;

import com.ufida.report.free.userdef.DefaultMenu;
import com.ufida.report.free.userdef.IMenuActionInfo;

/**
 * 费用帐联查单据
 * @author luolch
 *
 */
@SuppressWarnings("restriction")
public class MatterappLinkQuerybillOperator implements ITraceDataOperator{

	@Override
	public Action[] ctreateExtensionActions() {
		return null;
	}

	@Override
	public IMenuActionInfo getMenuItemInfo() {
		String actionName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0023")/*@res "联查单据"*/;
		DefaultMenu action = new DefaultMenu("LinkQueryBill", actionName);
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
		
		// 取得选中行单据主键
		value = rowData.getData(MtAppDetailVO.PK_MTAPP_BILL);
		
		if (value == null || StringUtil.isEmpty(value.toString())) {
            MessageDialog.showHintDlg(container, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "提示"*/, 
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "请选择单据进行联查！"*/);
            return;
        }
		
		String pk_bill = value == null ? null : value.toString();
		
		// 取得选中行单据类型
		value = rowData.getData(MtAppDetailVO.PK_BILLTYPE);
		String pk_billtype = value == null ? null : value.toString();
		
		final String[] pkbills = new String[] { pk_bill };
		String nodecode = BXConstans.MTAMN_NODE;
		if (!StringUtil.isEmptyWithTrim(pk_billtype)) {
			nodecode = PfDataCache.getBillType(pk_billtype).getNodecode();
		}
		
		// 联查具体单据
		SFClientUtil.openLinkedQueryDialog(nodecode, container, new LinkQuery(
				pkbills));
		
	}

}
