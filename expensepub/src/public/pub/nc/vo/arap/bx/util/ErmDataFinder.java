package nc.vo.arap.bx.util;

import nc.bs.trade.billsource.BillTypeSetDataFinder;
import nc.bs.trade.billsource.IBillFlow;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.jcom.lang.StringUtil;

public class ErmDataFinder extends BillTypeSetDataFinder {
	
	/**
	 * ��߲�ѯ���ε���Ч�ʣ��������ͽ�
	 */
	@Override
	protected String createSQL1(String curBillType, String... srcBillID) {
		if(curBillType.equals(BXConstans.JK_DJLXBM) || curBillType.equals(BXConstans.BX_DJLXBM)){
			IBillFlow billflow = getBillFlow(curBillType);
			if (billflow == null) {
				nc.bs.logging.Logger.error("��������:" + curBillType + "û���ҵ�����������Ϣ��");
				return null;
			}	
			String hTable = billflow.getMainTableName();
			String hPkField = billflow.getMainTablePrimaryKeyFiled();
			String hPkCorp = billflow.getBillCorp();
			String hBillCodeField = billflow.getBillNOField();
			
			String hBillTypeField = billflow.getBillTypeField();
			String hTransTypeField = billflow.getTransTypeField();
			String hTransTypePkField = billflow.getTransTypePkField();
			
			String bTableSourceIDField = billflow.getSourceIDField();

			// �������û����Դ���������ֶ�,�򷵻ؿ�.����������͵���û�б�ʶ��Դ����
			// ����,���޷���λ���Ƿ��Ǻ󵥾�.
			// ͨ���������:����ĳ�̶ֹ����͵��ݵĺ�������.
			if (bTableSourceIDField == null)
				return null;

			// �ڸ����͵ĵ����в���ĳ�����͵��ݵĺ�������
			StringBuffer sb = new StringBuffer("SELECT DISTINCT");
			sb.append(" ");
			sb.append(hTable + "." + hPkField);
			sb.append(" id, ");
			sb.append(hTable + "." + hPkCorp);
			sb.append(" corp, ");
			sb.append(hTable + "." + hBillCodeField);
			sb.append(" code ");
			
			if(!StringUtil.isEmptyWithTrim(hBillTypeField))
			{
				sb.append( "," + hTable + "." + hBillTypeField);
				sb.append(" type");
			}
			if(!StringUtil.isEmptyWithTrim(hTransTypeField))
			{
				sb.append(", ");
				sb.append(hTable + "." + hTransTypeField);
				sb.append(" transtype ");
			}
			if(!StringUtil.isEmptyWithTrim(hTransTypePkField))
			{
				sb.append(", ");
				sb.append(hTable + "." + hTransTypePkField);
				sb.append(" transtypepk ");
			}
			sb.append(", ");
			sb.append(hTable + "." + bTableSourceIDField);
			sb.append(" sourceID ");
			sb.append(" ");
			sb.append("FROM");
			sb.append(" ");
			sb.append(hTable);
			
			sb.append(" ");
			sb.append("WHERE ");

			sb.append(hTable + "." + bTableSourceIDField);
			sb.append(" in(");

		    // ������Դ������������
		    for (String id : srcBillID) {
		    	sb.append("'");
		    	sb.append(id);
		    	sb.append("'");
		    	sb.append(",");
		    }
		    sb.deleteCharAt(sb.length() - 1);
		    sb.append(")");

			sb.append(" and ");
			sb.append(hTable + ".dr =0");

			return sb.toString();
		}
		
		return super.createSQL1(curBillType, srcBillID);
	}
}
