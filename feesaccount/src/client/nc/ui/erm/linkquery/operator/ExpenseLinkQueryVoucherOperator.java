package nc.ui.erm.linkquery.operator;

import java.awt.Container;

import javax.swing.Action;

import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.pub.smart.data.IRowData;
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessRuntimeException;

import org.apache.commons.lang.StringUtils;

import com.ufida.report.free.userdef.DefaultMenu;
import com.ufida.report.free.userdef.IMenuActionInfo;

/**
 * ����������ƾ֤
 * @author wangled
 *
 */
@SuppressWarnings("restriction")
public class ExpenseLinkQueryVoucherOperator implements ITraceDataOperator{

	@Override
	public Action[] ctreateExtensionActions() {
		return null;
	}

	@Override
	public IMenuActionInfo getMenuItemInfo() {
		String actionName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0025")/*@res "����ƾ֤"*/;
		DefaultMenu action = new DefaultMenu("LinkQueryVoucher", actionName);
		action.setToolTipText(actionName);
		return action;
	}

	@Override
	public void traceData(Container container, TraceDataParam param) {
		
		// ȡ��ѡ�������ݼ�
		IRowData rowData = param.getRowData();
		
		// ȡ��ѡ���е�������
		Object tempValue = rowData.getData(ExpenseAccountVO.SRC_ID);
		
        if (tempValue == null || StringUtil.isEmpty(tempValue.toString())) {
//            MessageDialog.showHintDlg(container, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "��ʾ"*/, 
//                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "��ѡ�񵥾ݽ������飡"*/);
            AbstractFunclet funclet = (AbstractFunclet)container.getParent();
            funclet.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "��ʾ"*/, 
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0121")/*@res "��ѡ�񵥾ݽ������飡"*/);
            return;
        }
        
		String pk_bill = tempValue.toString();
		
		// ȡ��ѡ���е�������
		tempValue = rowData.getData("pk_billtype");
		String pk_billtype = tempValue == null ? null : tempValue.toString();
		
		// ȡ��ѡ���м��� 
		String pk_group = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
		
		// ȡ��ѡ����ҵ��Ԫ
		tempValue = rowData.getData("pk_org");
		String pk_org = tempValue == null ? null : tempValue.toString();
		
		if (StringUtils.isEmpty(pk_bill) || StringUtils.isEmpty(pk_billtype)) {
			return;
		}
		
		FipRelationInfoVO srcInfoVO = new FipRelationInfoVO();
		srcInfoVO.setPk_group(pk_group);
		srcInfoVO.setPk_org(pk_org);
		srcInfoVO.setRelationID(pk_bill);
		srcInfoVO.setPk_billtype(pk_billtype);
		
		try {
			// ��Դ����Ŀ�귽
			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg((AbstractFunclet) container.getParent(), srcInfoVO);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0024")/*@res "��ѡ����ϸ������Ϣ��ѯ��"*/);
		}
		
	}

}
