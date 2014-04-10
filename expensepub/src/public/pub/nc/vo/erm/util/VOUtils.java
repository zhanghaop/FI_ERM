package nc.vo.erm.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;

public class VOUtils {
	
	public static void main(String[] args) {

	}
	
	public static Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}
	
	public static boolean isEmpty(Collection coll) {
		return coll == null || coll.size() == 0;
	}
	
	/**
	 * @param e
	 * @param e2
	 * @return 对象是否相等, 其中一个为空的话返回false.
	 */
	public static boolean simpleEquals(Object e,Object e2){
		if(e==null && e2==null){
			return true;
		}else if(e!=null && e2!=null){
			return e.equals(e2);
		}else
			return false;
	}
	
	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的一个属性, 返回Map
	 */
	public static Map<String,SuperVO> changeCollectionToMap(Collection<? extends SuperVO> collection){
		if(collection==null || collection.size()==0)
			return new HashMap<String, SuperVO>();
		
		return changeCollectionToMap(collection,new String[]{collection.iterator().next().getPKFieldName()});
	}

	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的一个属性, 返回Map
	 */
	public static Map<String,SuperVO> changeCollectionToMap(Collection<? extends SuperVO> collection,String field){
		
		return changeCollectionToMap(collection,new String[]{field});
	}
	
	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的多个属性, 返回Map
	 */
	public static Map<String,SuperVO> changeCollectionToMap(Collection<? extends SuperVO> collection,String[] field){
		if(collection==null||collection.size()==0){
			return new HashMap<String, SuperVO>();
		}
		Map<String, SuperVO> values=new HashMap<String, SuperVO>();
		
		for (Iterator<? extends SuperVO> iter = collection.iterator(); iter.hasNext();) {
			SuperVO vo = iter.next();
			
			StringBuffer key=new StringBuffer("");
			for (int i = 0; i < field.length; i++) {
				key.append(vo.getAttributeValue(field[i]));
			}
			
			values.put(key.toString(),vo);
		}

		return values;
	}
	
	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的一个属性, 返回字符串数组
	 */
	public static String[] changeCollectionToArray(Collection<? extends SuperVO> collection,String field){
		if(collection==null||collection.size()==0){
			return new String[]{};
		}
		List<String> values=new ArrayList<String>();
		
		for (Iterator<? extends SuperVO> iter = collection.iterator(); iter.hasNext();) {
			SuperVO vo = iter.next();
			if(vo.getAttributeValue(field)!=null){
				values.add(vo.getAttributeValue(field).toString());
			}
		}

		return values.toArray(new String[]{});
	}

	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的一个属性, 返回Map<String,List>
	 */
	public static Map<String,List<SuperVO>> changeCollectionToMapList(Collection<? extends SuperVO> collection,String field){
		
		return changeCollectionToMapList(collection,new String[]{field});
	}
	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的多个属性, 返回Map<String,List>
	 */
	public static Map<String,List<SuperVO>> changeCollectionToMapList(Collection<? extends SuperVO> collection,String[] field){
		
		if(collection==null||collection.size()==0){
			return new HashMap<String,List<SuperVO>>();
		}
		
		Map<String,List<SuperVO>> map=new HashMap<String,List<SuperVO>>();
		
		for (Iterator<? extends SuperVO> iter = collection.iterator(); iter.hasNext();) {
			
			SuperVO vo = iter.next();
			
			StringBuffer keyb=new StringBuffer("");
			for (int i = 0; i < field.length; i++) {
				
				Object attributeValue = vo.getAttributeValue(field[i]);
				if(attributeValue==null){
					attributeValue="";
				}
				
				keyb.append(attributeValue);
			}
			
			String key = keyb.toString();
			
			if (map.containsKey(key)) {
				
				List<SuperVO> list = map.get(key);
				list.add(vo);

			} else {
				
				ArrayList<SuperVO> list = new ArrayList<SuperVO>();
				list.add(vo);
				map.put(key, list);
			}
			
		}

		return map;
	}
	
	/**
	 * @param collection
	 * @param field
	 * @return
	 * 取集合中VO的多个属性, 返回Map<String,List>
	 */
	public static Map<String,List<CircularlyAccessibleValueObject>> changeArrayToMapList(CircularlyAccessibleValueObject[] vos,String[] field){
		
		if(vos==null||vos.length==0){
			return new HashMap<String,List<CircularlyAccessibleValueObject>>();
		}
		
		Map<String,List<CircularlyAccessibleValueObject>> map=new HashMap<String,List<CircularlyAccessibleValueObject>>();
		
		for (CircularlyAccessibleValueObject vo:vos) {
			
			StringBuffer keyb=new StringBuffer("");
			for (int i = 0; i < field.length; i++) {
				
				Object attributeValue = vo.getAttributeValue(field[i]);
				if(attributeValue==null){
					attributeValue="";
				}
				
				keyb.append(attributeValue);
			}
			
			String key = keyb.toString();
			
			if (map.containsKey(key)) {
				
				List<CircularlyAccessibleValueObject> list = map.get(key);
				list.add(vo);

			} else {
				
				ArrayList<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();
				list.add(vo);
				map.put(key, list);
			}
			
		}

		return map;
	}


	public static Map<String,String> changeCollectionToMap(List<String> collection) {
		if(collection==null||collection.size()==0){
			return new HashMap<String, String>();
		}
		Map<String, String> values=new HashMap<String, String>();
		
		for (Iterator<? extends String> iter = collection.iterator(); iter.hasNext();) {
			String str = iter.next();
			values.put(str,str);
		}
		return values;
	}


}
