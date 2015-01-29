package nc.erm.mobile.eventhandler;

import java.util.HashMap;
import java.util.Iterator;

import nc.arap.mobile.itf.IWebPubService;
import nc.bs.framework.common.NCLocator;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * 
 * @author gaotn
 *
 */

public class JsonVoTransform {
	
	public Object getBodyValueAt(int row,String key) {
		HashMap<String,HashMap<Integer,SuperVO>> bodysMap = this.getBodysMap();
		return bodysMap.values().iterator().next().get(row).getAttributeValue(key);
	}
	
	public Object getBodyValueAt(String tab,int row,String key) {
		HashMap<String,HashMap<Integer,SuperVO>> bodysMap = this.getBodysMap();
		return bodysMap.get(tab).get(row).getAttributeValue(key);
	}
	public Object getHeadValue(String key) {
		SuperVO headVO = this.getHeadVO();
		return headVO.getAttributeValue(key);
	}
	
	public void setBodyValueAt(int row,String key,Object value) {
		HashMap<String,HashMap<Integer,SuperVO>> bodysMap = this.getBodysMap();
		bodysMap.values().iterator().next().get(row).setAttributeValue(key,value);
	}
	
	public void setBodyValueAt(String tab,int row,String key,Object value) {
		HashMap<String,HashMap<Integer,SuperVO>> bodysMap = this.getBodysMap();
		bodysMap.get(tab).get(row).setAttributeValue(key,value);
	}
	public void setHeadValue(String key,Object value) {
		SuperVO headVO = this.getHeadVO();
		headVO.setAttributeValue(key,value);
	}
	
	private EditItemInfoVO editItemInfoVO;
	
	private SuperVO headVO;
	
	private HashMap<String,HashMap<Integer,SuperVO>> bodysMap;    //������Ԫ���ݶ��󣬡�����������VO����
	
	private ItemValueInfoVO itemValueInfoVO;    //���ݷ�����ֵ�仯��Ϣ
	
	private EnabledItemInfoVO enabledItemInfoVO;
	
	private DigiItemInfoVO digiItemInfoVO;
	
	private FilterItemInfoVO filterItemInfoVO;
	
	private NotNullItemInfoVO notNullItemInfoVO;
	
//	private EditFormulasResultVO editFormulasResultVO;
	
	private JSONObject jSONObject;    //����ǰ̨
	
	
	public JsonVoTransform(String jSONString){
		try {
			JSONObject json = new JSONObject(jSONString);
			HashMap<String,String> classnameMap;    //VO����ӳ����Ϣ
			//�༭Ԫ����Ϣ
			JSONObject edititeminfo = (JSONObject)json.get("edititeminfo");
			this.editItemInfoVO = new EditItemInfoVO();
			this.editItemInfoVO.setId((String)(edititeminfo.get("id")));
			this.editItemInfoVO.setOldvalue(edititeminfo.get("oldvalue"));
			this.editItemInfoVO.setValue(edititeminfo.get("value"));
			this.editItemInfoVO.setFormula((String)(edititeminfo.get("formula")));
			this.editItemInfoVO.setSelectrow(new Integer(edititeminfo.get("selectrow").toString()));
			this.editItemInfoVO.setClassname((String)(edititeminfo.get("classname")));
			//��ͷ
			JSONObject head = (JSONObject)json.get("head");
			String headvoclassname = (String)head.get("classname");
			classnameMap = getClassnameMap(headvoclassname);
			String trueheadvoclassname = classnameMap.get(headvoclassname);
			this.headVO = (SuperVO)Class.forName(trueheadvoclassname).newInstance();
			JSONObject headvalue = (JSONObject)head.get("value");
			Iterator<String> headvaluekeys = headvalue.keys();
	        String headvaluekey;
	        while(headvaluekeys.hasNext()){
	        	headvaluekey = headvaluekeys.next();
	        	if(headvalue.get(headvaluekey) != null && !"".equals(headvalue.get(headvaluekey).toString())){
	        		headVO.setAttributeValue(headvaluekey, headvalue.get(headvaluekey));
	        	}
	        }
	        //����
	        JSONArray bodys = (JSONArray)json.get("body");
	        if(bodys != null && bodys.length()>0){
	        	this.bodysMap = new HashMap<String,HashMap<Integer,SuperVO>>();
	        	for(int i=0,n=bodys.length(); i<n; i++){
		        	JSONObject body = (JSONObject)bodys.get(i);
					String bodyvoclassname = (String)body.get("classname");
					String truebodyvoclassname = classnameMap.get(bodyvoclassname);
					HashMap<Integer,SuperVO> bodyVOMap = new HashMap<Integer,SuperVO>();
					JSONArray values = (JSONArray)body.get("value");
			        for(int j=0,l=values.length(); j<l; j++){
			        	JSONObject value = (JSONObject)values.get(j);
			        	SuperVO bodyVO = (SuperVO)Class.forName(truebodyvoclassname).newInstance();
			        	Iterator<String> keys = value.keys();
				        String key;
				        while(keys.hasNext()){
				        	key = keys.next();
				        	if(value.get(key) != null && !"".equals(value.get(key).toString())){
				        		bodyVO.setAttributeValue(key, value.get(key));
				        	}
				        }
				        //Ŀǰֻ����ǰ̨���崫����̨������һ�����ݵ����
				        if(bodyVOMap.containsKey(new Integer(edititeminfo.get("selectrow").toString()))){
				        	bodyVOMap.remove(new Integer(edititeminfo.get("selectrow").toString()));
				        }
				        bodyVOMap.put(new Integer(edititeminfo.get("selectrow").toString()), bodyVO);
			        }
			        this.bodysMap.put(bodyvoclassname, bodyVOMap);
		        }
	        }
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("ǰ̨��Ϣת���쳣��" + e.getMessage());
		}
	}
	
	private HashMap<String,String> getClassnameMap(String headvoclassname) throws Exception{
		IWebPubService iWebPubService = (IWebPubService) (NCLocator.getInstance()
		.lookup(IWebPubService.class));
		return iWebPubService.getClassnameMap(headvoclassname);
	}
	
	public EditItemInfoVO getEditItemInfoVO() {
		return editItemInfoVO;
	}

	public void setEditItemInfoVO(EditItemInfoVO editItemInfoVO) {
		this.editItemInfoVO = editItemInfoVO;
	}

	public SuperVO getHeadVO() {
		return headVO;
	}

	public void setHeadVO(SuperVO headVO) {
		this.headVO = headVO;
	}

	public HashMap<String, HashMap<Integer,SuperVO>> getBodysMap() {
		return bodysMap;
	}

	public void setBodysMap(HashMap<String, HashMap<Integer,SuperVO>> bodysMap) {
		this.bodysMap = bodysMap;
	}

	public ItemValueInfoVO getItemValueInfoVO() {
		if(itemValueInfoVO == null){
			itemValueInfoVO = new ItemValueInfoVO();
		}
		return itemValueInfoVO;
	}

	public void setItemValueInfoVO(ItemValueInfoVO itemValueInfoVO) {
		this.itemValueInfoVO = itemValueInfoVO;
	}

	public JSONObject getjSONObject() {
		this.jSONObject = new JSONObject();
		try{
			//��ͷ����
			HashMap<String,Object> returnHeadMap = this.getItemValueInfoVO().getReturnHeadMap();
			JSONObject headItems = new JSONObject();
			if(returnHeadMap != null && returnHeadMap.size() > 0){
				Iterator returnHeadMapI = returnHeadMap.keySet().iterator();
				while(returnHeadMapI.hasNext()){
					String itemId = (String)returnHeadMapI.next();
					JSONObject itemValue = new JSONObject();
					Object itemValueObject = returnHeadMap.get(itemId);
					if(itemValueObject instanceof ValueDetailInfoVO){
						ValueDetailInfoVO valueDetailInfoVO = (ValueDetailInfoVO)itemValueObject;
						itemValue.put("pk", valueDetailInfoVO.getValue());
						itemValue.put("name", valueDetailInfoVO.getName());
						itemValue.put("code", valueDetailInfoVO.getCode());
					} else {
						itemValue.put("pk", itemValueObject);
						itemValue.put("name", itemValueObject);
						itemValue.put("code", "");
					}
					headItems.put(itemId, itemValue);
				}
			}
			this.jSONObject.put("head", headItems);
//			JSONObject headJson = new JSONObject();
//			Class headClassType = this.headVO.getClass();
//			Field[] headFields = headClassType.getDeclaredFields();
//			String headFieldName;
//			Object valueh;
//			for(int i=0,n=headFields.length; i<n; i++){
//				headFieldName = headFields[i].getName().toLowerCase();
//				valueh = this.headVO.getAttributeValue(headFieldName);
//				headJson.put(headFieldName, valueh);
//			}
//			this.jSONObject.put("head", headJson);
			//��������
			HashMap<String,HashMap<Integer,HashMap<String,Object>>> returnBodysMap = 
					this.getItemValueInfoVO().getReturnBodysMap();
			JSONArray bodys = new JSONArray();
			if(returnBodysMap != null && returnBodysMap.size() > 0){
				Iterator returnBodysMapI = returnBodysMap.keySet().iterator();
				String classname;
				while(returnBodysMapI.hasNext()){
					classname = (String)returnBodysMapI.next();
					HashMap<Integer,HashMap<String,Object>> returnBodyMap = returnBodysMap.get(classname);
					if(returnBodyMap != null && returnBodyMap.size() > 0){
						Iterator returnBodyMapI = returnBodyMap.keySet().iterator();
						JSONObject body;
						Integer row;
						JSONObject bodyItems;
						while(returnBodyMapI.hasNext()){
							body = new JSONObject();
							row = (Integer)returnBodyMapI.next();
							HashMap<String,Object> bodyItemMap = returnBodyMap.get(row);
							bodyItems = new JSONObject();
							if(bodyItemMap != null && bodyItemMap.size() > 0){
								Iterator bodyItemMapI = bodyItemMap.keySet().iterator();
								while(bodyItemMapI.hasNext()){
									String itemId = (String)bodyItemMapI.next();
									JSONObject itemValue = new JSONObject();
									Object itemValueObject = bodyItemMap.get(itemId);
									if(itemValueObject instanceof ValueDetailInfoVO){
										ValueDetailInfoVO valueDetailInfoVO = (ValueDetailInfoVO)itemValueObject;
										itemValue.put("pk", valueDetailInfoVO.getValue());
										itemValue.put("name", valueDetailInfoVO.getName());
										itemValue.put("code", valueDetailInfoVO.getCode());
									} else {
										itemValue.put("pk", itemValueObject);
										itemValue.put("name", itemValueObject);
										itemValue.put("code", "");
									}
									bodyItems.put(itemId, itemValue);
								}
							}
							body.put("classname", classname);
							body.put("row", row);
							body.put("value", bodyItems);
							bodys.put(body);
						}
					}
				}
			}
			this.jSONObject.put("body", bodys);
				
//				JSONArray bodyvos;
//				while(returnBodysMapI.hasNext()){
//					body = new JSONObject();
//					classname = (String)returnBodysMapI.next();
//					body.put("classname", classname);
//					HashMap<Integer,HashMap<String,Object>> returnBodyMap = returnBodysMap.get(classname);
//					if(returnBodyMap != null && returnBodyMap.size() > 0){
//						bodyvos = new JSONArray();
//						Iterator returnBodyMapI = returnBodyMap.keySet().iterator();
//						JSONObject bodyvo;
//						Integer row;
//						JSONObject bodyItems;
//						while(returnBodyMapI.hasNext()){
//							row = (Integer)returnBodyMapI.next();
//							bodyvo =  new JSONObject();
//							bodyvo.put("row", row);
//							HashMap<String,Object> bodyItemMap = returnBodyMap.get(row);
//							if(bodyItemMap != null && bodyItemMap.size() > 0){
//								Iterator bodyItemMapI = bodyItemMap.keySet().iterator();
//								bodyItems = new JSONObject();
//								while(bodyItemMapI.hasNext()){
//									String itemId = (String)bodyItemMapI.next();
//									Object itemValue = bodyItemMap.get(itemId);
//									bodyItems.put(itemId, itemValue);
//								}
//								bodyvo.put("value", bodyItems);
//							}
//							bodyvos.put(bodyvo);
//						}
//						body.put("value", bodyvos);
//					}
//					bodys.put(body);
//				}
//				this.jSONObject.put("body", bodys);
				
//				JSONArray bodys = new JSONArray();
//				JSONObject body;
//				JSONArray bodyvalues;
//				Iterator iteratorb = this.bodysMap.keySet().iterator();
//				while(iteratorb.hasNext()){
//					String classname = (String)iteratorb.next();
//					HashMap<Integer,SuperVO> bodyVOMap = this.bodysMap.get(classname);
//					body = new JSONObject();
//					body.put("classname", classname);
//					bodyvalues = new JSONArray();
//					if(bodyVOMap != null && bodyVOMap.size() > 0){
//						Class bodyClassType = bodyVOMap.get(0).getClass();
//						Field[] bodyFields = bodyClassType.getDeclaredFields();
//						for(int j=0,m=bodyVOMap.size(); j<m; j++){
//							SuperVO bodyVO = bodyVOMap.get(j);
//							JSONObject bodyvalue = new JSONObject();
//							String bodyFieldName;
//							Object valueb;
//							for(int k=0,l=bodyFields.length; k<l; k++){
//								bodyFieldName = bodyFields[k].getName().toLowerCase();
//								valueb = bodyVO.getAttributeValue(bodyFieldName);
//								bodyvalue.put(bodyFieldName, valueb);
//							}
//							bodyvalues.put(bodyvalue);
//						}
//						body.put("value", bodyvalues);
//					}
//					bodys.put(body);
//				}
//				this.jSONObject.put("body", bodys);
//			}
			//�༭��ʽ������
//			HashMap<String,Object> formulasResultMap = getEditFormulasResultVO().getResultMap();
//			Iterator iteratorf = formulasResultMap.keySet().iterator();
//			JSONObject formulasResults = new JSONObject();
//			while(iteratorf.hasNext()){
//				String itemId = (String)iteratorf.next();
//				Object itemValue = formulasResultMap.get(itemId);
//				formulasResults.put(itemId, itemValue);
//			}
//			this.jSONObject.put("formula", formulasResults);
			
			//�ɱ༭�Ըı���ֶ�
			HashMap<String,UFBoolean> headEnabledMap = getEnabledItemInfoVO().getHeadEnabledMap();
			Iterator headiterator = headEnabledMap.keySet().iterator();
			JSONObject headEnabledItems = new JSONObject();
			while(headiterator.hasNext()){
				String itemId = (String)headiterator.next();
				UFBoolean isEnabled = headEnabledMap.get(itemId);
				headEnabledItems.put(itemId, isEnabled.booleanValue());
			}
			this.jSONObject.put("headenabled", headEnabledItems);
			HashMap<String,HashMap<String,UFBoolean>> bodysEnabledMap = getEnabledItemInfoVO().getBodysEnabledMap();
			Iterator bodysiterator = bodysEnabledMap.keySet().iterator();
			JSONObject bodysEnabled = new JSONObject();
			while(bodysiterator.hasNext()){
				String classname = (String)bodysiterator.next();
				HashMap<String,UFBoolean> bodyEnabledMap = bodysEnabledMap.get(classname);
				JSONObject bodyEnabledItems = new JSONObject();
				Iterator bodyiterator = bodyEnabledMap.keySet().iterator();
				while(bodyiterator.hasNext()){
					String itemId = (String)bodyiterator.next();
					UFBoolean isEnabled = bodyEnabledMap.get(itemId);
					bodyEnabledItems.put(itemId, isEnabled.booleanValue());
				}
				bodysEnabled.put(classname, bodyEnabledItems);
			}
			this.jSONObject.put("bodysenabled", bodysEnabled);
			//�����Ըı���ֶ�
			HashMap<String,UFBoolean> headNotNullMap = getNotNullItemInfoVO().getHeadNotNullMap();
			Iterator headNotNullMapI = headNotNullMap.keySet().iterator();
			JSONObject headNotNullItems = new JSONObject();
			while(headNotNullMapI.hasNext()){
				String itemId = (String)headNotNullMapI.next();
				UFBoolean isNotNull = headNotNullMap.get(itemId);
				headNotNullItems.put(itemId, isNotNull.booleanValue());
			}
			this.jSONObject.put("headnotnull", headNotNullItems);
			HashMap<String,HashMap<String,UFBoolean>> bodysNotNullMap = getNotNullItemInfoVO().getBodysNotNullMap();
			Iterator bodysNotNullMapI = bodysNotNullMap.keySet().iterator();
			JSONObject bodysNotNull = new JSONObject();
			while(bodysNotNullMapI.hasNext()){
				String classname = (String)bodysNotNullMapI.next();
				HashMap<String,UFBoolean> bodyNotNullMap = bodysNotNullMap.get(classname);
				JSONObject bodyNotNullItems = new JSONObject();
				Iterator bodyNotNullMapI = bodyNotNullMap.keySet().iterator();
				while(bodyNotNullMapI.hasNext()){
					String itemId = (String)bodyNotNullMapI.next();
					UFBoolean isNotNull = bodyNotNullMap.get(itemId);
					bodyNotNullItems.put(itemId, isNotNull.booleanValue());
				}
				bodysNotNull.put(classname, bodyNotNullItems);
			}
			this.jSONObject.put("bodysnotnull", bodysNotNull);
			//���ȸı���ֶ�
			HashMap<String, Integer> headDigiMap = getDigiItemInfoVO().getHeadDigiMap();
			Iterator headDigiMapI = headDigiMap.keySet().iterator();
			JSONObject headDigiItems = new JSONObject();
			while(headDigiMapI.hasNext()){
				String itemId = (String)headDigiMapI.next();
				Integer digit = headDigiMap.get(itemId);
				headDigiItems.put(itemId, digit);
			}
			this.jSONObject.put("headdigit", headDigiItems);
			HashMap<String, HashMap<String, Integer>> bodysDigiMap = getDigiItemInfoVO().getBodysDigiMap();
			Iterator bodysDigiMapI = bodysDigiMap.keySet().iterator();
			JSONObject bodysDigi = new JSONObject();
			while(bodysDigiMapI.hasNext()){
				String classname = (String)bodysDigiMapI.next();
				HashMap<String, Integer> bodyDigiMap = bodysDigiMap.get(classname);
				JSONObject bodyDigiItems = new JSONObject();
				Iterator bodyDigiMapI = bodyDigiMap.keySet().iterator();
				while(bodyDigiMapI.hasNext()){
					String itemId = (String)bodyDigiMapI.next();
					Integer digit = bodyDigiMap.get(itemId);
					bodyDigiItems.put(itemId, digit);
				}
				bodysDigi.put(classname, bodyDigiItems);
			}
			this.jSONObject.put("bodysdigit", bodysDigi);
			
			//���������ı���ֶ���Ϣ
			HashMap<String,JSONObject> headFilterMap = getFilterItemInfoVO().getHeadFilterMap();
			Iterator headFilterMapI = headFilterMap.keySet().iterator();
			JSONObject headFilterItems = new JSONObject();
			while(headFilterMapI.hasNext()){
				String itemId = (String)headFilterMapI.next();
				JSONObject filtersql = headFilterMap.get(itemId);
				headFilterItems.put(itemId, filtersql);
			}
			this.jSONObject.put("headfilter", headFilterItems);
			HashMap<String,HashMap<String,JSONObject>> bodysFilterMap = getFilterItemInfoVO().getBodysFilterMap();
			Iterator bodysFilterMapI = bodysFilterMap.keySet().iterator();
			JSONObject bodysFilter = new JSONObject();
			while(bodysFilterMapI.hasNext()){
				String classname = (String)bodysFilterMapI.next();
				HashMap<String,JSONObject> bodyFilterMap = bodysFilterMap.get(classname);
				JSONObject bodyFilterItems = new JSONObject();
				Iterator bodyFilterMapI = bodyFilterMap.keySet().iterator();
				while(bodyFilterMapI.hasNext()){
					String itemId = (String)bodyFilterMapI.next();
					JSONObject filtersql = bodyFilterMap.get(itemId);
					bodyFilterItems.put(itemId, filtersql);
				}
				bodysFilter.put(classname, bodyFilterItems);
			}
			this.jSONObject.put("bodysfilter", bodysFilter);
			this.jSONObject.put("success", "true");
		} catch (Exception e) {
			try {
				this.jSONObject.put("success", "false");
				this.jSONObject.put("message", "��̨��Ϣת���쳣��" + e.getMessage());
				return jSONObject;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			//ExceptionUtils.wrappBusinessException("��̨��Ϣת���쳣��" + e.getMessage());
		}
		return jSONObject;
	}

	public void setjSONObject(JSONObject jSONObject) {
		this.jSONObject = jSONObject;
	}

	public EnabledItemInfoVO getEnabledItemInfoVO() {
		if(enabledItemInfoVO == null){
			enabledItemInfoVO = new EnabledItemInfoVO();
		}
		return enabledItemInfoVO;
	}

	public void setEnabledItemInfoVO(EnabledItemInfoVO enabledItemInfoVO) {
		this.enabledItemInfoVO = enabledItemInfoVO;
	}
	
	public DigiItemInfoVO getDigiItemInfoVO() {
		if(digiItemInfoVO == null){
			digiItemInfoVO = new DigiItemInfoVO();
		}
		return digiItemInfoVO;
	}

	public void setDigiItemInfoVO(DigiItemInfoVO digiItemInfoVO) {
		this.digiItemInfoVO = digiItemInfoVO;
	}

	public FilterItemInfoVO getFilterItemInfoVO() {
		if(filterItemInfoVO == null){
			filterItemInfoVO = new FilterItemInfoVO();
		}
		return filterItemInfoVO;
	}

	public void setFilterItemInfoVO(FilterItemInfoVO filterItemInfoVO) {
		this.filterItemInfoVO = filterItemInfoVO;
	}

	public NotNullItemInfoVO getNotNullItemInfoVO() {
		if(notNullItemInfoVO == null){
			notNullItemInfoVO = new NotNullItemInfoVO();
		}
		return notNullItemInfoVO;
	}

	public void setNotNullItemInfoVO(NotNullItemInfoVO notNullItemInfoVO) {
		this.notNullItemInfoVO = notNullItemInfoVO;
	}
	
//	public EditFormulasResultVO getEditFormulasResultVO() {
//		if(editFormulasResultVO == null){
//			editFormulasResultVO = new EditFormulasResultVO();
//		}
//		return editFormulasResultVO;
//	}
//
//	public void setEditFormulasResultVO(EditFormulasResultVO editFormulasResultVO) {
//		this.editFormulasResultVO = editFormulasResultVO;
//	}
	
}
