package nc.vo.arap.bx.util;

import nc.bs.trade.billsource.BillTypeSetDataFinder;
import nc.bs.trade.billsource.IBillFlow;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.jcom.lang.StringUtil;

public class ErmDataFinder extends BillTypeSetDataFinder {
	
	/**
	 * 提高查询下游单据效率，报销单和借款单
	 */
	@Override
	protected String createSQL1(String curBillType, String... srcBillID) {
		if(curBillType.equals(BXConstans.JK_DJLXBM) || curBillType.equals(BXConstans.BX_DJLXBM)){
			IBillFlow billflow = getBillFlow(curBillType);
			if (billflow == null) {
				nc.bs.logging.Logger.error("单据类型:" + curBillType + "没有找到单据流程信息！");
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

			// 如果单据没有来源单据类型字段,则返回空.即如果该类型单据没有标识来源单据
			// 类型,就无法定位它是否是后单据.
			// 通常该情况是:它是某种固定类型单据的后续单据.
			if (bTableSourceIDField == null)
				return null;

			// 在该类型的单据中查找某种类型单据的后续单据
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

		    // 构造来源单据主键参数
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
