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
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonModel{

	protected JsonItem[] m_biBodyItems = null; // ����Ԫ������

	protected Hashtable<String, Integer> htBodyItems = null; // ��¼����Ԫ��λ��

	private Hashtable<String, JsonItem> htJsonItems = new Hashtable<String, JsonItem>();

	private BillTabVO tabvo = null;

	/**
	 * BillModel ������ע��.
	 */
	public JsonModel() {
	}

	/**
	 * ���Ԫ��. ��������:(01-2-21 10:08:48)
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
	 * �������.
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
	 * ���ñ�������. ��������:(01-2-23 14:22:07)
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
			ExceptionUtils.wrappBusinessException("����������Ϣת���쳣��" + ex.getMessage());
		}
		return bodyarray;
	}

	
	/***
	 * ��ǰ̨�������ı����json����ת����SuperVO
	 * @param bodyvo ��new������vo
	 * @param json ǰ̨�������ı���ֵ
	 * @throws JSONException 
	 * @throws BusinessException 
	 */
	public String translateJsonToValueObject(SuperVO bodyvo,JSONObject json) throws JSONException, BusinessException{
		Iterator<String> keys = json.keys();  
        String headvalue;  
        String key;  
        ArrayList<String[]> formulasList = new ArrayList<String[]>();    //gaotn
		HashMap<String,Object> defValueMap = new HashMap<String,Object>();    //gaotn
        while(keys.hasNext()){
            key = keys.next();  
            if(key.equals("bodydeftype"))
				continue;
            headvalue = (String) json.get(key);
            JsonItem item = getItemByKey(key);
            JsonData.setVoFromItem(formulasList,defValueMap,item,bodyvo,headvalue);
        }  
        return WebFormulaParser.getInstance().processFormulasForHead(formulasList,bodyvo,defValueMap);    //gaotn
	}
	
	public JSONObject getEditFormular() throws JSONException{
		JSONObject bodyJson = new JSONObject();
		JsonItem[] items = getBodyItems();
		String key = null;
		for (int i = 0; i < items.length; i++) {
			key = items[i].getKey();
			bodyJson.put(key+"_editformulas", items[i].getEditFormulas());
		}
		return bodyJson;
	}
	private JSONObject setBodyRowObjectModelByMetaData(NCObject o, int row){
		if(o == null)
			return null;
		JSONObject bodyJson = new JSONObject();
		StringBuffer text = new StringBuffer();
		if (o == null || row < 0)
			return bodyJson;
		try{
			JSONObject bdeftype = new JSONObject();
			JsonItem[] items = getBodyItems();
			Object value;
			for (int col = 0; col < items.length; col++) {
				JSONObject itemJson = new JSONObject();
				JsonItem item = items[col];
				String key = item.getKey();
				//�Զ������¼��������
				if(item.isIsDef()){
					bdeftype.put(key, item.getDataType());
				}
				if (item.getMetaDataProperty() != null) {
					value = o.getAttributeValue(item.getMetaDataProperty()
							.getAttribute());
					if(value != null){
						itemJson = JsonData.getJsonObjectFromItem(item,value);
//						bodyJson.put(item.getKey(), itemJson);
						bodyJson.put(key, itemJson.get("pk"));
						bodyJson.put(key+"_name", itemJson.get("name"));
						//ƴ���ַ���
						if(item.isShowFlag() == true && !"amount".equals(item.getKey())){
							text.append(itemJson.get("name") +"  ");
						}
					}
				}
			}
			bodyJson.put("bodydeftype", bdeftype);
			bodyJson.put("textaera",text);
		}catch(Exception e){
			ExceptionUtils.wrappBusinessException("����ת���쳣��" + e.getMessage());
		}
		return bodyJson;
	}

	/**
	 * ���õ�Ԫֵ.
	 */
	
	/**
	 * ���Ԫ��. ��������:(01-2-21 10:08:48)
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
	 * lkp add �ڴ����Զ������JsonItem���е���ʱ������ǲ��ջ�Combox���ͣ�����Ҫ����_ID�е��кš� ͨ���˷������á�
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