package nc.erm.mobile.view;

import java.util.List;

import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.pub.bill.MetaDataPropertyAdpter;

public class BillItemTranslate {
	//查看字段dsl生成
	public String translateShowItem(String prefix,MobileBillItem item){
		StringBuffer input = new StringBuffer();
		int dataType = item.getDataType();
		if("zy".equals(item.getKey()) || "zy2".equals(item.getKey())){
			dataType = IBillItem.STRING ;
		}
		switch (dataType) {
			case IBillItem.STRING:
				input.append("<input id=\"textbox" + item.getKey() 
						+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//字符串直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DATE:
			case IBillItem.DATETIME:
				input.append("<input id=\"dateinput" + item.getKey() 
						+ "\" readonly =\"true\" format=\"yyyy-MM-dd\" type=\"date\" height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" "
						+ " bindfield=\"" + prefix + item.getKey() + "_name\"");
				//日期直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DECIMAL:
			case IBillItem.MONEY: 
				input.append("<input id=\"textbox" + item.getKey() 
						+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//金额直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.INTEGER:
				input.append("<input id=\"textbox" + item.getKey() 
						+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//金额直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			default:
				//参照或下拉
				input.append("<input id=\"textbox" + item.getKey() 
						+ "\" readonly =\"true\" maxlength=\"256\" type=\"text\" height=\"44\"  color=\"#000000\" halign=\"right\" "
						+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//参照赋默认值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;  
		}
		return input.toString();
	}  
	
	//编辑字段dsl生成
	public String translateEditItem(String prefix,MobileBillItem item){
		StringBuffer input = new StringBuffer();
		int dataType = item.getDataType();
		if("zy".equals(item.getKey()) || "zy2".equals(item.getKey())){
			dataType = IBillItem.STRING ;
		}
		switch (dataType) {
			case IBillItem.STRING:
				input.append("<input id=\"textbox" + item.getKey() 
				+ "\" maxlength=\"256\" placeholder=\"可空\" type=\"text\""
				+ " height=\"44\"  color=\"#000000\" "
				+ "font-size=\"16\" width=\"fill\" font-family=\"default\" ");//padding-left=\"12\"
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//字符串直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DATE:
			case IBillItem.DATETIME:
				input.append("<input id=\"dateinput" + item.getKey() 
				+ "\" format=\"yyyy-MM-dd\" placeholder=\"可空\" type=\"date\""
				+ " height=\"44\" weight=\"1\" color=\"#000000\" "
				+ " font-size=\"16\" width=\"fill\" font-family=\"default\" "
				+ " bindfield=\"" + prefix + item.getKey() + "\"");
				//日期直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.DECIMAL:
			case IBillItem.MONEY: 
				input.append( "<input id=\"number" + item.getKey() 
				+ "\" min=\"-9.99999999E8\" precision=\"2\" max=\"9.99999999E8\" roundValue=\"5\" type=\"number\" roundType=\"value\" "
				+ " height=\"44\" color=\"#000000\" background=\"#ffffff\" "
				+ "font-size=\"16\" width=\"fill\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//金额直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			case IBillItem.INTEGER:
				input.append( "<input id=\"number" + item.getKey() 
				+ "\" min=\"-9.99999999E8\" max=\"9.99999999E8\" roundValue=\"5\" type=\"number\" roundType=\"value\" "
				+ " height=\"44\" color=\"#000000\" background=\"#ffffff\" "
				+ "font-size=\"16\" width=\"fill\" padding-left=\"12\" font-family=\"default\" halign=\"LEFT\" ");
				input.append(" bindfield=\"" + prefix + item.getKey() + "\"");
				//金额直接赋值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;
			default:
				String reftype = item.getRefType();
				//下拉类型和参照类型分别需要对reftype做处理
				if(dataType == IBillItem.COMBO){
//					JSONObject jsonObj = new JSONObject();
					boolean isFromMeta = reftype
			                   .startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
					String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
					List<DefaultConstEnum> combodata = ComboBoxUtil.getInitData(reftype1, isFromMeta);
					StringBuffer typestr = new StringBuffer();
					for(int i=0;i<combodata.size();i++){
						DefaultConstEnum enumvalue = combodata.get(i);
						if(i != 0)
							typestr.append(",");
						typestr.append(enumvalue.getName()).append("=").append(enumvalue.getValue());
					}
					reftype = "COMBO," + item.getName() + ":" +typestr.toString();	
				}else{
					if(item.getRefType() == null)
						reftype = "UFREF,没有参照";
					else if(item.getRefType()!=null && item.getRefType().contains(","))
						reftype = "UFREF," + item.getRefType().split(",")[0];
				}
				input.append("<label id=\"label" + item.getKey() 
				+ "\" height=\"44\" color=\"#000000\" "
				+"font-size=\"16\" weight=\"1\" padding-left=\"12\" onclick =\"open_reflist\" font-family=\"default\" " 
		 		+ " onclick-reftype=\"" + reftype
				+ "\" onclick-mapping=\"{'" + prefix + item.getKey() + "':'pk_ref','" + prefix + item.getKey() + "_name':'refname'}\"");
				input.append(" bindfield=\"" + prefix + item.getKey() + "_name\"");
				//参照赋默认值
				if(item.getDefaultValue()!=null && !item.getDefaultValue().equals(""))
					input.append(" value=\"" + item.getDefaultValue() + "\"");
				input.append("/>"); 
				break;  
		}
		return input.toString();
	}  
}
