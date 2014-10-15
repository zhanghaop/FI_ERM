package nc.vo.erm.jkbx.ext;

import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;

/**
 * 报销单拉单超申请回写预算数据结构
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class BXMaYsControlVOExt extends YsControlVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String getAttributesValue(String attr) {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().getAttributesValue(attr);
		}
		if (attr.trim().equalsIgnoreCase("zb.pk_org") || attr.trim().equalsIgnoreCase("pk_org")
				|| attr.trim().equalsIgnoreCase("pkorg")){
			return getPKOrg();
		}
		
		// 不进行维度对照翻译直接，获得由item进行处理
		Object value = getItem().getItemValue(attr);
		return value == null ? null : value.toString();
	}

	private IFYControl getItem() {
		IFYControl[] items = getItems();
		if (items == null || items.length == 0)
			return null;
		else
			return items[0];
	}
	
}
