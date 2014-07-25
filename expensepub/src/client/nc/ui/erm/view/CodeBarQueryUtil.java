package nc.ui.erm.view;


import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;

import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;

import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;

import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;


public class CodeBarQueryUtil {
	
	//��������������ѯ���ݣ���ѯ������ʱ����true��δ��ѯ�����ݷ���false
	public static List<JKBXVO> doBarCodeQuery(String pk, BillManageModel model) {
		if (pk == null || pk.trim().length() == 0)
			return null;
		
		String nodeCode = model.getContext().getNodeCode();
		if (BXConstans.BXBILL_QUERY.equals(nodeCode)) {
			// ���ݲ�ѯ
			return doBillManageQuery(" where zb.djbh='" + pk.trim()
					+ "' and zb.pk_group='" + BXUiUtil.getPK_group()
					+ "' and QCBZ='N' and DR ='0'", model);
		} else if (BXConstans.BXMNG_NODECODE.equals(nodeCode)) {// ���ݹ���
			return doBillManageQuery(
					" where zb.djbh='"
							+ pk.trim()
							+ "' and zb.pk_group='"
							+ BXUiUtil.getPK_group()
							+ "' and dr=0 AND (APPROVER= '"
							+ model.getContext().getPk_loginUser()
							+ "'OR (PK_JKBX IN(SELECT BILLID FROM PUB_WORKFLOWNOTE WF WHERE WF.CHECKMAN='"
							+ model.getContext().getPk_loginUser()
							+ "' AND ISNULL(WF.DR,0)   = 0 AND WF.ACTIONTYPE <> 'MAKEBILL')))",
					model);
		} else if (BXConstans.BXLR_QCCODE.equals(nodeCode)) {
			// �ڳ�����¼��ڵ�
			return doBillManageQuery(" where djbh='" + pk.trim()
					+ "' and pk_group='" + BXUiUtil.getPK_group()
					+ "'  and QCBZ='Y' and DR <>'1'", model);
		} else {
			// ���÷ѽ� ����ѽ� ���÷ѱ����� ��ͨ�ѱ����� ͨѶ�ѱ����� ��Ʒ�ѱ����� �д��ѱ����� ����ѱ�������¼��ڵ�
			String[] result;
			try {
				result = NCLocator
						.getInstance()
						.lookup(IBXBillPrivate.class)
						.queryPsnidAndDeptid(
						model.getContext().getPk_loginUser(), null);
				String pk_psn = result[0];
				String pk_group = InvocationInfoProxy.getInstance().getGroupId();
				String whereStr = " PK_GROUP= '" + pk_group + "' AND ( JKBXR='"
						+ pk_psn + "' OR OPERATOR='"
						+ model.getContext().getPk_loginUser() + "' )";
				return doBillManageQuery(
						" where zb.djbh='"
								+ pk.trim()
								+ "' and "
								+ whereStr
								+ " and djlxbm = '"
								+ ((ErmBillBillManageModel) model).getCurrentBillTypeCode()
								+ "' and QCBZ='N' and DR ='0'", model);
			} catch (BusinessException e) {
				ExceptionHandler.handleRuntimeException(e);
			}
			return null;
		}
	}

    
	public static List<JKBXVO> doBillManageQuery(String sql,BillManageModel model){
        List<JKBXVO> values = null;
        try
        {
            values = ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName())).queryVOsByWhereSql(sql, "");
        }
        catch (Exception e)
        {
            ExceptionHandler.handleRuntimeException(e);
        }
        return values;
    }
}
