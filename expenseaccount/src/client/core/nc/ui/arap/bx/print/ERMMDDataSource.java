package nc.ui.arap.bx.print;

import java.util.List;

import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.MDDataSource;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;

public class ERMMDDataSource extends MDDataSource {

	/**
	 * ��ͷǰ׺
	 */
	static String HEAD_PREFIX = "h_";

	/**
	 * ����ǰ׺
	 */
	static String BODY_PREFIX = "b_";

	private static final long serialVersionUID = 1L;

	public ERMMDDataSource(IDataSource ds, List<String> varList) {
		super(ds, varList);
	}

	@Override
	public String[] getItemValuesByExpress(String express) {
		if (express.startsWith(BODY_PREFIX)) {
			express = express.replace(BODY_PREFIX, "");
			if(express.indexOf(".") > 0){
				return getItemValuesByExpressBody(express);
			}
		}
		if (express.startsWith(HEAD_PREFIX)) {
			express = express.replace(HEAD_PREFIX, "");
		}
		//��Ԫ����ȡֵ
		String[] value = super.getItemValuesByExpress(express);
		String[] retValues = null;
		if(value != null && value.length>0){
			Object[] arr = (Object[]) value;
			retValues = new String[arr.length];
			for (int i = 0; i < arr.length; i++) {
				if(!StringUtil.isEmpty((String)arr[i])){
					retValues[i] = arr[i].toString();
				}
			}
		}
		if(retValues!=null && retValues.length>0 && (!StringUtil.isEmpty(retValues[0]))){
			return retValues;
		}else{
			//Ԫ����δȡ��ֵ�����vo��ȡ�������ֹ�¼���ժҪ
			if (express.contains("zy") && express.indexOf(".") > 0) {
				express = express.replace(".", "#");
				Object val = getVO()[0].getParentVO().getAttributeValue(express.split("#")[0]);
				if (val != null) {
					// Ӧ���Ե���������ʾ����Ϊ19λ��Ӧ�ø�Ϊ10λ��������,���д������⴦��
					if (val instanceof UFDate) {
						return new String[] { ((UFDate) val).toStdString() };
					} else {
						return new String[] { val.toString() };
					}
				}
			}
		}
		return new String[0];

	}

	private JKBXVO[] bxvos;

	public JKBXVO[] getVO() {
		return bxvos;
	}

	public void setBxvos(JKBXVO[] bxvos) {
		this.bxvos = bxvos;
	}
	
	/**
	 * ȡ����ֵ
	 * 
	 * @param itemExpress
	 * @return
	 */
	public String[] getItemValuesByExpressBody(String itemExpress) {
		itemExpress = itemExpress.replace(".", "#");
		String[] str = itemExpress.split("#");
		final String tableCode = str[0];
		final String attributeName = str[1];
		CircularlyAccessibleValueObject[] tableVOs = getVO()[0].getTableVO(tableCode);
		if (tableVOs != null) {
			String[] retValue = new String[tableVOs.length];
			for (int i = 0; i < tableVOs.length; i++) {
				Object o = tableVOs[i].getAttributeValue(attributeName);
				if (o == null) {
					retValue[i] = null;
				} else {
					retValue[i] = o.toString();
					//begin--added by chendya Ӧ��������������ڣ�����������ʾ����Ӧ����ʾ10λ��������Ҳȷ��ͬ���޸ģ����������⴦��
					if("defitem1".equals(attributeName)||"defitem2".equals(attributeName)){
						retValue[i] = retValue[i].substring(0,10);
					}
					//--end
				}
			}
			return retValue;
		}
		return null;
	}
}
