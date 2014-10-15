package nc.vo.erm.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.md.MDBaseQueryFacade;
import nc.md.common.AssociationKind;
import nc.md.model.IAssociation;
import nc.md.model.ICardinality;
import nc.md.model.impl.BusinessEntity;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;

import org.apache.commons.lang.ArrayUtils;

public class VOUtils {

	public static void main(String[] args) {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Collection coll) {
		return coll == null || coll.size() == 0;
	}

	/**
	 * @param e
	 * @param e2
	 * @return �����Ƿ����, ����һ��Ϊ�յĻ�����false.
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
	 * ȡ������VO��һ������, ����Map
	 */
	@SuppressWarnings("deprecation")
	public static Map<String,SuperVO> changeCollectionToMap(Collection<? extends SuperVO> collection){
		if(collection==null || collection.size()==0)
			return new HashMap<String, SuperVO>();

		return changeCollectionToMap(collection,new String[]{collection.iterator().next().getPKFieldName()});
	}

	/**
	 * @param collection
	 * @param field
	 * @return
	 * ȡ������VO��һ������, ����Map
	 */
	public static Map<String,SuperVO> changeCollectionToMap(Collection<? extends SuperVO> collection,String field){

		return changeCollectionToMap(collection,new String[]{field});
	}

	/**
	 * @param collection
	 * @param field
	 * @return
	 * ȡ������VO�Ķ������, ����Map
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
	 * ȡ������VO��һ������, �����ַ�������
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
	 * ȡ������VO��һ������, ����Map<String,List>
	 */
	public static Map<String,List<SuperVO>> changeCollectionToMapList(Collection<? extends SuperVO> collection,String field){

		return changeCollectionToMapList(collection,new String[]{field});
	}

	/**
	 * @param collection
	 * @param field
	 * @return
	 * ȡ������VO�Ķ������, ����Map<String,List>
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
	 * ȡ������VO�Ķ������, ����Map<String,List>
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

	/**
	 *
	 * �����ֶ�����ȡ����,nameΪ��ȡ����
	 *
	 * @param <T>
	 * @param vos
	 * @param name
	 * @return
	 */
	public static <T> String[] getAttributeValues(T[] vos, String name) {
		if (vos == null || vos.length == 0)
			return null;
		String[] result = new String[vos.length];
		//nameΪ��,ȡpk
		if (name == null) {
			if (vos[0] instanceof SuperVO) {
				for (int i = 0; i < result.length; i++) {
					result[i] = (String) ((SuperVO) vos[i]).getPrimaryKey();
				}
			} else if (vos[0] instanceof AggregatedValueObject) {
				for (int i = 0; i < result.length; i++) {
					result[i] = (String) ((SuperVO) ((AggregatedValueObject) vos[i])
							.getParentVO()).getPrimaryKey();
				}
			} else {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0035")/*@res "��֧�ֵ�����:"*/
						+ vos[0].getClass().getName());
			}
		} else {
			if (vos[0] instanceof SuperVO) {
				for (int i = 0; i < result.length; i++) {
					result[i] = (String) ((SuperVO) vos[i])
							.getAttributeValue(name);
				}
			} else if (vos[0] instanceof AggregatedValueObject) {
				for (int i = 0; i < result.length; i++) {
					result[i] = (String) ((AggregatedValueObject) vos[i])
							.getParentVO().getAttributeValue(name);
				}
			} else {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0035")/*@res "��֧�ֵ�����:"*/
						+ vos[0].getClass().getName());
			}
		}
		return result;
	}

	/**
	 *
	 * �����ֶ�����ȡ����,nameΪ��ȡ����
	 *
	 * @param <T>
	 * @param vos
	 * @param name
	 * @return
	 */
	public static <T> String[] getAttributeValuesWithoutNullValue(T[] vos, String name) {
		if (ArrayUtils.isEmpty(vos)){
			return null;
		}

		List<String> result = new ArrayList<String>();
		//nameΪ��,ȡpk
		if (name == null) {
			if (vos[0] instanceof SuperVO) {
				for (int i = 0; i < vos.length; i++) {
					String value = (String) ((SuperVO) vos[i]).getPrimaryKey();
					if (value != null) {
						result.add(value);
					}
				}
			} else if (vos[0] instanceof AggregatedValueObject) {
				for (int i = 0; i < vos.length; i++) {
					String value = (String) ((SuperVO) ((AggregatedValueObject) vos[i])
							.getParentVO()).getPrimaryKey();
					if (value != null) {
						result.add(value);
					}
				}
			} else {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0035")/*@res "��֧�ֵ�����:"*/
						+ vos[0].getClass().getName());
			}
		} else {
			if (vos[0] instanceof SuperVO) {
				for (int i = 0; i < vos.length; i++) {
					String value = (String) ((SuperVO) vos[i])
					.getAttributeValue(name);
					if (value != null) {
						result.add(value);
					}
				}
			} else if (vos[0] instanceof AggregatedValueObject) {
				for (int i = 0; i < vos.length; i++) {
					String value = (String) ((AggregatedValueObject) vos[i])
					.getParentVO().getAttributeValue(name);
					if (value != null) {
						result.add(value);
					}
				}
			} else {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0035")/*@res "��֧�ֵ�����:"*/
						+ vos[0].getClass().getName());
			}
		}
		return result.toArray(new String[0]);
	}

	/**
	 * ��voListת����ָ���ֶ�ֵΪkey��map
	 *
	 *
	 * @param <T>
	 * @param voList
	 * @param field  �����ֶ�
	 * @return   Map<fieldValue,List<T>>
	 * @author: wangyhh@ufida.com.cn
	 */
	public static <T> Map<String, List<T>> changeCollection2MapList(List<T> voList, String[] field) {
		if (voList == null || voList.size() == 0) {
			return null;
		}

		Map<String, List<T>> map = new HashMap<String, List<T>>();
		for (int i = 0; i < voList.size(); i++) {
			CircularlyAccessibleValueObject vo = null;
			if (voList.get(i) instanceof SuperVO) {
				vo = (SuperVO) voList.get(i);
			}else if(voList.get(i) instanceof AggregatedValueObject){
				vo = ((AggregatedValueObject) voList.get(i)).getParentVO();
			}else{
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0036")/*@res "��֧�ֵ�����"*/);
			}

			StringBuffer keyBuf = new StringBuffer("");
			for (String string : field) {
				Object attributeValue = vo.getAttributeValue(string);
				keyBuf.append(attributeValue == null ? "" : attributeValue);

			}

			String key = keyBuf.toString();
			if (map.containsKey(key)) {
				map.get(key).add((T) voList.get(i));
			} else {
				ArrayList<T> list = new ArrayList<T>();
				list.add((T) voList.get(i));
				map.put(key, list);
			}
		}

		return map;
	}
	
	/**
	 * ��voListת����PKֵΪkey��map
	 *
	 *
	 * @param <T>
	 * @param voList
	 * @return   Map<fieldValue,T>
	 */
	public static <T> Map<String, T> changeCollection2Map(List<T> voList) {
		Map<String, T> map = new HashMap<String,T>();
		if (voList == null || voList.size() == 0) {
			return map;
		}

		for (int i = 0; i < voList.size(); i++) {
			CircularlyAccessibleValueObject vo = null;
			if (voList.get(i) instanceof SuperVO) {
				vo = (SuperVO) voList.get(i);
			}else if(voList.get(i) instanceof AggregatedValueObject){
				vo = ((AggregatedValueObject) voList.get(i)).getParentVO();
			}else{
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0036")/*@res "��֧�ֵ�����"*/);
			}

			String key;
			try {
				key = vo.getPrimaryKey();
			} catch (BusinessException e) {
				throw new RuntimeException(e);
			}
			map.put(key, (T)voList.get(i));
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AggregatedValueObject> T cloneAggVO(T vo) throws BusinessException {
		if (vo == null) {
			return null;
		}
		T result = null;
		try {
			result = (T) vo.getClass().newInstance();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		CircularlyAccessibleValueObject parentVO = vo.getParentVO();
		if (parentVO != null) {
			result.setParentVO((CircularlyAccessibleValueObject) parentVO.clone());
		}
		// �����ӱ�
		CircularlyAccessibleValueObject[] childrenVOSrc = vo.getChildrenVO();
		if (childrenVOSrc != null) {
			CircularlyAccessibleValueObject[] childrenVO = new CircularlyAccessibleValueObject[childrenVOSrc.length];
			for (int i = 0; i < childrenVO.length; i++) {
				childrenVO[i] = (CircularlyAccessibleValueObject) childrenVOSrc[i].clone();
			}
			result.setChildrenVO(childrenVO);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T extends SuperVO> T cloneSuperVO(T vo) throws BusinessException {
		BusinessEntity be = (BusinessEntity) MDBaseQueryFacade.getInstance().getBeanByFullClassName(vo.getClass().getName());

		T result = (T) vo.clone();

		if (be == null) {
			return result;
		}
		List<IAssociation> associations = be.getAssociationsByKind(AssociationKind.Composite, ICardinality.ASS_ALL);
		if (associations != null) {
			for (IAssociation association : associations) {
				Object object = vo.getAttributeValue(association.getStartAttribute().getName());

				if (object != null && object.getClass().isArray()) {
					Object[] objs = (Object[]) object;

					if (objs.length != 0 && objs[0] instanceof SuperVO) {
						T[] clone = (T[]) objs.clone();
						for (int i = 0; i < objs.length; i++) {
							T t = (T) objs[i];
							clone[i] = (T) t.clone();
						}
						result.setAttributeValue(association.getStartAttribute().getName(), clone);
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * ��þۺ�vo������vos
	 * 
	 * @param aggvos
	 * @return
	 */
	public static SuperVO[] getHeadVOs(AggregatedValueObject[] aggvos){
		if(aggvos== null || aggvos.length == 0){
			return null; 
		}
		SuperVO[] headvos = new SuperVO[aggvos.length];
		for (int i = 0; i < aggvos.length; i++) {
			headvos[i] = (SuperVO) aggvos[i].getParentVO();
		}
		return headvos;
	}
}