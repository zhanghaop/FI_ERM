package nc.ui.erm.linkquery.operator;

import java.awt.Container;

import javax.swing.Action;

import nc.bs.pf.pub.PfDataCache;
import nc.funcnode.ui.AbstractFunclet;
import nc.pub.smart.data.IRowData;
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.ui.uap.sf.SFClientUtil;
import nc.util.erm.expamortize.ExpamortizeLinkQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.jcom.lang.StringUtil;

import com.ufida.report.free.userdef.DefaultMenu;
import com.ufida.report.free.userdef.IMenuActionInfo;

/**
 * ���������鵥��
 * @author wangled
 *
 */
@SuppressWarnings("restriction")
public class ExpenseLinkQuerybillOperator implements ITraceDataOperator{

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
//		Object value = null;
		
		// ȡ��ѡ���е�������
		Object value = rowData.getData(ExpenseAccountVO.SRC_ID);
		
        if (value == null || StringUtil.isEmpty(value.toString())) {
//            MessageDialog.showHintDlg(container, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "��ʾ"*/, 
//                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "��ѡ�񵥾ݽ������飡"*/);
            AbstractFunclet funclet = (AbstractFunclet)container.getParent();
            funclet.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "��ʾ"*/, 
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "��ѡ�񵥾ݽ������飡"*/);
            return;
        }
        
		String pk_bill = value.toString();
		
		// ȡ��ѡ���е�������
		value = rowData.getData("pk_billtype");
		String pk_billtype = value == null ? null : value.toString();
		
		final String[] pkbills = new String[] { pk_bill };
		String nodecode = BXConstans.BXMNG_NODECODE;
		if (!StringUtil.isEmptyWithTrim(pk_billtype)) {
			nodecode = PfDataCache.getBillType(pk_billtype).getNodecode();
		}
		LinkQuery linkQuery = null;
		if("266X".equals(pk_billtype)){
			linkQuery  = new ExpamortizeLinkQuery(pk_bill);
			linkQuery.setPkOrg((String) rowData.getData(ExpenseAccountVO.PK_ORG));
		}else {
			linkQuery = new LinkQuery(
					pkbills);
		}
		
		// ������嵥��
		SFClientUtil.openLinkedQueryDialog(nodecode, container, linkQuery);
		
	}

}
