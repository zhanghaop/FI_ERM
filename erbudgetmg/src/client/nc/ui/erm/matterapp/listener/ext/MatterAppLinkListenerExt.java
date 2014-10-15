package nc.ui.erm.matterapp.listener.ext;

import java.util.HashSet;
import java.util.Set;

import nc.ui.erm.matterapp.listener.MatterAppLinkListener;
import nc.ui.pub.linkoperate.ILinkQueryData;

/**
 * ���뵥֧�֣����ڷ�̯ƾ֤������
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class MatterAppLinkListenerExt extends MatterAppLinkListener {
	
	
	protected String[] getBillIDs(ILinkQueryData querydata) {
		
		String[] billIDs = super.getBillIDs(querydata);
		
		if(billIDs != null && billIDs.length > 0){
			// ����ƾ֤��������
			Set<String> newIds = new HashSet<String>();
			for (int i = 0; i < billIDs.length; i++) {
				String id = billIDs[i];
				if(id.length() > 20){
					id = id.substring(0, 20);
				}
				newIds.add(id);
			}
			billIDs = newIds.toArray(new String[newIds.size()]);
		}
		return billIDs;
	}
	
	
}
