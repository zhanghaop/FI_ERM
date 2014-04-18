package nc.ui.erm.linkquery.operator;

import java.awt.Container;

import javax.swing.Action;

import nc.pub.smart.data.IRowData;
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.expamortize.ExpamtinfoVO;

import com.ufida.report.free.userdef.DefaultMenu;
import com.ufida.report.free.userdef.IMenuActionInfo;

@SuppressWarnings("restriction")
public class ExpamortizeLinkQueryOperator  implements ITraceDataOperator{

	@Override
	public Action[] ctreateExtensionActions() {
		return null;
	}

	@Override
	public IMenuActionInfo getMenuItemInfo() {
		String actionName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0018")/*@res "联查摊销信息"*/;
		DefaultMenu action = new DefaultMenu("LinkQueryExpInfo", actionName);
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

		// 联查具体摊销信息
		SFClientUtil.openLinkedQueryDialog(nodecode, container, new LinkQuery(pk_org,pk_bill));
	}
}