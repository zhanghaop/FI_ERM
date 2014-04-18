package nc.ui.erm.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.pub.bill.BillItem;
import nc.ui.trade.excelimport.convertor.DefaultRefValueGetter;

/**
 * 事由字段取值Getter
 * @author chenshuaia
 *
 */
public class ReasonRefValueGetter extends DefaultRefValueGetter {
	@Override
	public Map<String, Object[]> getRefValue(BillItem billItem, List<String> pkList) {
		Map<String, Object[]> valueMap = new HashMap<String, Object[]>();
		valueMap.put(getPkCodeAndNameFieldName()[0], new Object[pkList.size()]);
		valueMap.put(getPkCodeAndNameFieldName()[1], new Object[pkList.size()]);
		valueMap.put(getPkCodeAndNameFieldName()[2], new Object[pkList.size()]);
		if (pkList != null) {
			for (int i = 0; i < pkList.size(); i++) {
				String pk = pkList.get(i);
				if (pk != null) {
					valueMap.get(getPkCodeAndNameFieldName()[0])[i] = pk;
					valueMap.get(getPkCodeAndNameFieldName()[1])[i] = pk;
					valueMap.get(getPkCodeAndNameFieldName()[2])[i] = pk;
				}
			}
		}
		return valueMap;
	}

	@Override
	public String[] getPkCodeAndNameFieldName() {
		return new String[] { "pk_summary", "code", "summaryname" };
	}
}
