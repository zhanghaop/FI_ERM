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
	 * 表头前缀
	 */
	static String HEAD_PREFIX = "h_";

	/**
	 * 表体前缀
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
		//从元数据取值
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
			//元数据未取到值，则从vo中取，比如手工录入的摘要
			if (express.contains("zy") && express.indexOf(".") > 0) {
				express = express.replace(".", "#");
				Object val = getVO()[0].getParentVO().getAttributeValue(express.split("#")[0]);
				if (val != null) {
					// 应测试单据日期显示长度为19位，应该改为10位特殊需求,下行代码特殊处理
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
	 * 取表体值
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
					//begin--added by chendya 应测试提出出发日期，结束日期显示长度应该显示10位需求，需求也确认同意修改，作下列特殊处理
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
