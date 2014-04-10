package nc.ui.arap.bx.print;

import java.util.Vector;
import nc.ui.fi_print.data.TempletData;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class TempletData_BX extends TempletData {

	public TempletData_BX(CircularlyAccessibleValueObject h, CircularlyAccessibleValueObject[] bs) {
		super(h, bs);
	}

	public Object[] getBodyData(String key) {

		Object[] rs = null;
		int rowCount = bodyvos == null ? 0 : bodyvos.length;
		if (rowCount > 0) {
			
			if (key.indexOf(".") != -1) {
				
				Vector valuesVector = new Vector();
				
				int pos=key.indexOf(".");
				String tableCode=key.substring(0,pos);
				String mkey=key.substring(pos+1,key.length());
				
				for (int i = 0; i < rowCount; i++) {
					if(bodyvos[i].getAttributeValue("tablecode")!=null && bodyvos[i].getAttributeValue("tablecode").equals(tableCode)){
						Object value = bodyvos[i].getAttributeValue(mkey);
						valuesVector.addElement(value);
					}
				}
				
				rs = new String[valuesVector.size()];
				
				valuesVector.copyInto(rs);
				
			}else{
				rs = new Object[rowCount];
				for (int i = 0; i < rowCount; i++) {
					rs[i] = bodyvos[i].getAttributeValue(key);
				}
			}
		}
		return rs;
	}
}
