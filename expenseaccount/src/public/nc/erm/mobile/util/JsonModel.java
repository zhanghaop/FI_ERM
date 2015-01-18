package nc.erm.mobile.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.erm.mobile.pub.formula.WebFormulaParser;
import nc.md.data.access.NCObject;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bill.pub.MiscUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonModel{

	protected JsonItem[] m_biBodyItems = null; // 表体元素数组

	protected Hashtable<String, Integer> htBodyItems = null; // 记录表体元素位置

	private Hashtable<String, JsonItem> htJsonItems = new Hashtable<String, JsonItem>();

	private BillTabVO tabvo = null;

	/**
	 * BillModel 构造子注解.
	 */
	public JsonModel() {
	}

	/**
	 * 表格元素. 创建日期:(01-2-21 10:08:48)
	 */
	public JsonItem[] getBodyItems() {
		return m_biBodyItems;
	}

	/**
	 * JsonItem.
	 */
	public JsonItem getItemByKey(String key) {
		return htJsonItems.get(key);
	}

	/**
	 * 表格列数.
	 */
	public int getItemIndex(Object itemOrKey) {
		if (itemOrKey == null)
			throw new NullPointerException("itemOrkey is null.");
		JsonItem[] items = getBodyItems();
		if (items == null)
			return -1;
		if (itemOrKey instanceof String) {
			for (int i = 0; i < items.length; i++) {
				if ((itemOrKey).equals(items[i].getKey()))
					return i;
			}
		} else if (itemOrKey instanceof JsonItem) {
			JsonItem item = (JsonItem) itemOrKey;
			for (int i = 0; i < items.length; i++) {
				if (item.getKey().equals(items[i].getKey()))
					return i;
			}
		}
		throw new java.lang.IllegalArgumentException("itemOrKey not found!");
	}

	/**
	 * 设置表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	protected JSONArray setBodyObjectByMetaData(NCObject[] bodys) {
		JSONArray bodyarray = null;
		Map<Integer, JSONObject> bodyMap = new HashMap<Integer, JSONObject>();
		if (bodys == null || bodys.length == 0)
			return bodyarray;
		try {
			int itemnum = 0;
			for (int i = 0; i < bodys.length; i++) {
				JSONObject bodyJson = setBodyRowObjectModelByMetaData(bodys[i], i);
				if(bodyJson != null){
					//gaotn
					bodyMap.put(itemnum, bodyJson);
					itemnum++;
					//gaotn
	//				bodyarray.put(bodyJson);
				}
			}
			//gaotn
			JsonItem[] items = getBodyItems();
			bodyarray = WebFormulaParser.getInstance().processFormulasForBody(bodyMap,items,bodys);
			//gaotn
		} catch (Exception ex) {
			Logger.debug(ex);
		}
		return bodyarray;
	}

	
	/***
	 * 将前台传过来的表体的json数据转换成SuperVO
	 * @param bodyvo 新new出来的vo
	 * @param json 前台传过来的表体值
	 * @throws JSONException 
	 */
	public void translateJsonToValueObject(SuperVO bodyvo,JSONObject json) throws JSONException{
		Iterator<String> keys = json.keys();  
        String headvalue;  
        String key;  
        while(keys.hasNext()){
            key = keys.next();  
            headvalue = (String) json.get(key);
            JsonItem item = getItemByKey(key);
            if(item!=null && !StringUtil.isEmpty(headvalue)){
//            ncobject.setAttributeValue(item.getMetaDataProperty().getAttribute(), headvalue);
	            if(item.getDataType() == IBillItem.DATE){
	            	bodyvo.setAttributeValue(key, new UFDate(headvalue));
	            }else if(item.getDataType() == IBillItem.MONEY || item.getDataType() == IBillItem.DECIMAL){
	            	bodyvo.setAttributeValue(key, new UFDouble(headvalue));
	            }else if(item.getDataType() == IBillItem.DATETIME){
	            	bodyvo.setAttributeValue(key, new UFDateTime(headvalue));
	            }else if(item.getDataType() == IBillItem.COMBO){
	            	String reftype = item.getRefType();
	            	reftype = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
	            	boolean isSX = true;
	            	boolean isIX = false;
	        		ArrayList<String> list = new ArrayList<String>();

	        		String[] items = MiscUtil.getStringTokens(reftype, ",");
	        		if (items != null) {
		    			isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
		    			isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX
	        		}
	        		if(isSX){
	        			try{
	        				bodyvo.setAttributeValue(key, headvalue);
		            	}catch(Exception e){
		            		continue;
		            	}
	        		}else
	        			bodyvo.setAttributeValue(key, new Integer(headvalue));
	            }else{
	            	try{
	            		bodyvo.setAttributeValue(key, headvalue);
	            	}catch(Exception e){
	            		continue;
	            	}
	            }
            }
        }  
	}
	
	
	private JSONObject setBodyRowObjectModelByMetaData(NCObject o, int row){
		if(o == null)
			return null;
		JSONObject bodyJson = new JSONObject();
		StringBuffer text = new StringBuffer();
		if (o == null || row < 0)
			return bodyJson;
		try{
			JsonItem[] items = getBodyItems();
			Object value;
			for (int col = 0; col < items.length; col++) {
				JSONObject itemJson = new JSONObject();
				JsonItem item = items[col];
				if (item.getMetaDataProperty() != null) {
					value = o.getAttributeValue(item.getMetaDataProperty()
							.getAttribute());
					if(value != null){
						if(item.getDataType() == IBillItem.UFREF && item.getMetaDataProperty() != null){
							//如果是参照，就需要取参照的值
							itemJson.put("pk", value);
							IGeneralAccessor accessor = GeneralAccessorFactory.getAccessor(item.getMetaDataProperty().getRefBusinessEntity().getID());
							if(accessor != null){
								IBDData[] data = accessor.getDocbyPks(new String[]{value.toString()});
								if(data != null && data.length>0 && data[0] != null)
									itemJson.put("name", data[0].getName());
							}else{
								itemJson.put("name", "");
							}
						}else if(item.getDataType() == IBillItem.COMBO && item.getMetaDataProperty() != null){
							//如果是参照，就需要取参照的值
							itemJson.put("pk", value);
							String reftype = item.getRefType();
							if (reftype != null
									&& (reftype = reftype.trim()).length() > 0) {
								boolean isFromMeta = reftype
										.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
								String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
								List<DefaultConstEnum> combodata = JsonData.getInitData(reftype1, isFromMeta);
								for(int index=0;index<combodata.size();index++){
									DefaultConstEnum enumvalue = combodata.get(index);
									if(value.equals(enumvalue.getValue()))
										itemJson.put("name", enumvalue.getName());
								}
							}
						}else if(item.getDataType() == IBillItem.MONEY || item.getDataType() == IBillItem.DECIMAL){
							itemJson.put("pk", value);
							itemJson.put("name", NumberFormatUtil.formatDouble((UFDouble)value));
						}else if(item.getDataType() == IBillItem.DATE){
							//NumberFormat 的 setTheMark方法直接拿过来用
							itemJson.put("pk", value);
							String str = value.toString();
							itemJson.put("name", str.substring(0, 10));
						}else{
							itemJson.put("pk", value);
							itemJson.put("name", value);
						}
//						bodyJson.put(item.getKey(), itemJson);
						bodyJson.put(item.getKey(), itemJson.get("pk"));
						bodyJson.put(item.getKey()+"_name", itemJson.get("name"));
						//拼接字符串
						if(item.isShowFlag() == true && !"amount".equals(item.getKey())){
							text.append(itemJson.get("name") +"  ");
						}
					}
				}
			}
			bodyJson.put("textaera",text);
		}catch(Exception e){
			//e.printStackTrace();
		}
		return bodyJson;
	}

	/**
	 * 设置单元值.
	 */
	
	/**
	 * 表格元素. 创建日期:(01-2-21 10:08:48)
	 */
	public void setBodyItems(JsonItem[] newItems) {
		m_biBodyItems = newItems;
		htBodyItems = new Hashtable<String, Integer>();
		htJsonItems.clear();
	
		if (newItems != null) {
			for (int i = 0; i < newItems.length; i++) {
				htBodyItems.put(newItems[i].getKey(), Integer.valueOf(i));
				if (newItems[i].getDataType() == IBillItem.UFREF
						|| newItems[i].getDataType() == IBillItem.COMBO) {
					htBodyItems.put(newItems[i].getKey() + IBillItem.ID_SUFFIX,
							Integer.valueOf(i));
				}

				htJsonItems.put(newItems[i].getKey(), newItems[i]);
			}

			for (int i = 0; i < newItems.length; i++) {
				JsonItem item = newItems[i];
				if (item != null) {
					if (item.getIDColName() != null
							&& !item.getKey().equals(item.getIDColName())) {
						JsonItem iditem = htJsonItems.get(item.getIDColName());
						if (iditem != null)
							iditem.addRelationItem(item);
					}
				}
			}
		}

	}

	/**
	 * lkp add 在处理自定义项对JsonItem进行调整时，如果是参照或Combox类型，则需要增加_ID列的列号。 通过此方法设置。
	 * 
	 * @param key
	 * @param index
	 */
	public void addBodyItemIndex(String key, int index) {
		htBodyItems.put(key, index);
	}
	
	public void setTabvo(BillTabVO tabvo) {
		this.tabvo = tabvo;
	}

	public BillTabVO getTabvo() {
		return tabvo;
	}

	
	
}