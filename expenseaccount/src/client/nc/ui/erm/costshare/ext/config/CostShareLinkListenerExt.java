package nc.ui.erm.costshare.ext.config;

import java.util.HashSet;
import java.util.Set;

import nc.ui.erm.costshare.ui.CostShareLinkListener;

/**
 * 费用结转单支持联查，处理分期分摊凭证联查
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class CostShareLinkListenerExt extends CostShareLinkListener {

	@Override
	protected String[] getBillIDs() {
		
		String[] billIDs = super.getBillIDs();
		
		if(billIDs != null && billIDs.length > 0){
			// 处理凭证联查问题
			Set<String> newIds = new HashSet<String>();
			for (int i = 0; i < billIDs.length; i++) {
				String id = billIDs[i];
				if(id.length() > 20){
					id = id.substring(0, 20);
				}
				newIds.add(id);
			}
			billIDs = newIds.toArray(new String[0]);
		}
		return billIDs;
	}
}
