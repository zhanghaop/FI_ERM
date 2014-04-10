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
 * ���������鵥��
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
		String actionName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0023")/*@res "���鵥��"*/;
		DefaultMenu action = new DefaultMenu("LinkQueryBill", actionName);
		action.setToolTipText(actionName);
		return action;
	}

	@Override
	public void traceData(Container container, TraceDataParam param) {
		if (param == null || param.getRowData() == null) {
			return;
		}
		
		// ȡ��ѡ�������ݼ�
		IRowData rowData = param.getRowData();
		Object value = null;
		
		// ȡ��ѡ���е�������
		value = rowData.getData(MtAppDetailVO.PK_MTAPP_BILL);
		
		if (value == null || StringUtil.isEmpty(value.toString())) {
            MessageDialog.showHintDlg(container, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "��ʾ"*/, 
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "��ѡ�񵥾ݽ������飡"*/);
            return;
        }
		
		String pk_bill = value == null ? null : value.toString();
		
		// ȡ��ѡ���е�������
		value = rowData.getData(MtAppDetailVO.PK_BILLTYPE);
		String pk_billtype = value == null ? null : value.toString();
		
		final String[] pkbills = new String[] { pk_bill };
		String nodecode = BXConstans.MTAMN_NODE;
		if (!StringUtil.isEmptyWithTrim(pk_billtype)) {
			nodecode = PfDataCache.getBillType(pk_billtype).getNodecode();
		}
		
		// ������嵥��
		SFClientUtil.openLinkedQueryDialog(nodecode, container, new LinkQuery(
				pkbills));
		
	}

}
