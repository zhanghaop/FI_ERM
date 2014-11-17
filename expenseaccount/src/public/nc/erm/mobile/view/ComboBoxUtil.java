package nc.erm.mobile.view;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.ui.bd.mmpub.DataDictionaryReader;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bill.pub.MiscUtil;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
 
public class ComboBoxUtil {
	
	public static String getComboBoxDiv(int panel,String prefix,MobileBillItem item,String flag){
		StringBuffer input = new StringBuffer();
		if(flag.equals("addcard")){
			input.append( "<select id=\"combobox" + panel 
			+ "\" height=\"44\" color=\"#000000\" pressed-image=\"combobox\" "
			+" font-size=\"16\" width=\"fill\" font-family=\"default\" "
			+" background-image=\"combobox\" bindfield=\"" 
			+ prefix + item.getKey() + "\" >" );
			//加一个空选项
			int count = 0;
//			input.append("<option id=\"combobox" + panel + "_opt" + count 
//					+ "\" selected=\"selected\"/>");
			List<DefaultConstEnum> list = initComboBoxData(item);
			for(int i=0;i<1;i++){
				count++;
				input.append("<option id=\"combobox" + panel + "_opt" + count 
						+ "\" value=\""+list.get(i).getValue()
						+ "\">"+list.get(i).getName()+"</option>");
			}
			input.append("</select>");
		}
		else if(flag.equals("editcard")){
			input.append( "<select id=\"combobox" + panel 
					+ "\" height=\"44\" color=\"#000000\" pressed-image=\"combobox\" "
					+"font-size=\"16\" width=\"fill\" font-family=\"default\" "
					+"background-image=\"combobox\" " );
		}
		return input.toString();
	}
	public static List<DefaultConstEnum> initComboBoxData(MobileBillItem item){
		String comboitems = item.getRefType();
		if (comboitems != null
				&& (comboitems = comboitems.trim()).length() > 0) {
			boolean isFromMeta = comboitems
					.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
			comboitems = comboitems.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
			return parseInitData(item,comboitems, isFromMeta);
		}
		return null;
	}
	
	private static int getStringIndexOfArray(final String[] ss, final String s) {
		if (ss != null) {
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].equals(s))
					return i;
			}
		}
		return -1;
	}
	public static List<DefaultConstEnum> parseInitData(MobileBillItem item,String comboitems, boolean isFromMeta) {
		boolean isSX;
		ArrayList<String> list = new ArrayList<String>();

		String[] items = MiscUtil.getStringTokens(comboitems, ",");
		if (items != null) {
			// 返回索引
			String[] strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_DBFIELD };// , COMBOTYPE_INDEX_X
			// };

			item.setWithIndex(getStringIndexOfArray(strArray, items[0]) >= 0);

			// 获得下拉项目值
			strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_X, IBillItem.COMBOTYPE_VALUE,
					IBillItem.COMBOTYPE_VALUE_X };

			isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
			boolean isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX

			if (getStringIndexOfArray(strArray, items[0]) >= 0) {
				for (int i = 1; i < items.length; i++) {
					list.add(items[i].trim());
				}
			} else if (items.length == 3
					&& getStringIndexOfArray(new String[] {
							IBillItem.COMBOTYPE_INDEX_DBFIELD,
							IBillItem.COMBOTYPE_VALUE_DBFIELD }, items[0]) >= 0) {
				items = new DataDictionaryReader(items[1], items[2]).getQzsm();
				if (items != null) {
					for (int i = 0; i < items.length; i++) {
						list.add(items[i].trim());
					}
				}
			}

			// 解析值
			if (list.size() > 0) {
				String[] ss = list.toArray(new String[list.size()]);
				if (isSX || isIX) {
					List<DefaultConstEnum> ces = new ArrayList<DefaultConstEnum>();
					Object value = null;
					for (int i = 0; i < ss.length; i++) {

						int pos = ss[i].indexOf('=');

						String name = pos >= 0 ? ss[i].substring(0, pos)
								: ss[i];
						name = getDecodeStr(name, isFromMeta);

						if (pos >= 0) {
							value = getDecodeStr(ss[i].substring(pos + 1),isFromMeta);
							if (isIX) {
								value = Integer.valueOf(value.toString());
							}
						} else {
							
							if (isSX) {
								value = getDecodeStr(ss[i], isFromMeta);
							} else {
								value = Integer.valueOf(i);
							}
						}

						ces.add(new DefaultConstEnum(value, name));
					}
					return ces;
				}
			}
		}
		return null;
	}
	public static List<DefaultConstEnum> getInitData(String comboitems, boolean isFromMeta) {
		boolean isSX;
		ArrayList<String> list = new ArrayList<String>();

		String[] items = MiscUtil.getStringTokens(comboitems, ",");
		if (items != null) {
			// 返回索引
			String[] strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_DBFIELD };// , COMBOTYPE_INDEX_X
			// };

			// 获得下拉项目值
			strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_X, IBillItem.COMBOTYPE_VALUE,
					IBillItem.COMBOTYPE_VALUE_X };

			isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
			boolean isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX

			if (getStringIndexOfArray(strArray, items[0]) >= 0) {
				for (int i = 1; i < items.length; i++) {
					list.add(items[i].trim());
				}
			} else if (items.length == 3
					&& getStringIndexOfArray(new String[] {
							IBillItem.COMBOTYPE_INDEX_DBFIELD,
							IBillItem.COMBOTYPE_VALUE_DBFIELD }, items[0]) >= 0) {
				items = new DataDictionaryReader(items[1], items[2]).getQzsm();
				if (items != null) {
					for (int i = 0; i < items.length; i++) {
						list.add(items[i].trim());
					}
				}
			}

			// 解析值
			if (list.size() > 0) {
				String[] ss = list.toArray(new String[list.size()]);
				if (isSX || isIX) {
					List<DefaultConstEnum> ces = new ArrayList<DefaultConstEnum>();
					Object value = null;
					for (int i = 0; i < ss.length; i++) {

						int pos = ss[i].indexOf('=');

						String name = pos >= 0 ? ss[i].substring(0, pos)
								: ss[i];
						name = getDecodeStr(name, isFromMeta);

						if (pos >= 0) {
							value = getDecodeStr(ss[i].substring(pos + 1),isFromMeta);
							if (isIX) {
								value = Integer.valueOf(value.toString());
							}
						} else {
							
							if (isSX) {
								value = getDecodeStr(ss[i], isFromMeta);
							} else {
								value = Integer.valueOf(i);
							}
						}

						ces.add(new DefaultConstEnum(value, name));
					}
					return ces;
				}
			}
		}
		return null;
	}
	private static String getDecodeStr(String str, boolean isFromMeta) {

		if (isFromMeta) {
			try {
				return URLDecoder.decode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Logger.debug(e.getMessage());
			}
		}

		return str;
	}
}
