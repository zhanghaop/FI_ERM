package nc.bs.erm.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.dbcache.DBCacheQueryFacade;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class CacheUtil {
    
    /**
     * �����ֶ������ֶ�ֵ���飬������ȡȡVO
     * @return �޼�¼ʱ����null
     */
    public static <T extends SuperVO> T[] getVOArrayByPkArray(Class<T> clazz,
            String fieldName, String[] fieldValueArray) throws BusinessException {
        return getValueFromCacheByForeign(clazz, fieldName, fieldValueArray);
    }

    /**
     * ����where������������ȡָ��class��VO
     * @param <T>
     * @param clazz
     * @param wherePart 
     * @return �޼�¼ʱ����null
     * @throws BusinessException
     */
    public static <T extends SuperVO> T[] getValueFromCacheByWherePart(
            Class<T> clazz, String wherePart) throws BusinessException {
        T vo = getCache(clazz);
        if (vo == null)
            return null;
        return getValueFromCache(vo, null, wherePart);
    }

    /**
     * ����where������������ȡָ��class��VO
     * @param <T>
     * @param clazz
     * @param excludeSelectField �ų����ֶ�
     * @param wherePart 
     * @return �޼�¼ʱ����null
     * @throws BusinessException
     */
    public static <T extends SuperVO> T[] getValueFromCacheByWherePart(
            Class<T> clazz, String[] excludeSelectField, String wherePart) throws BusinessException {
        T vo = getCache(clazz);
        if (vo == null)
            return null;
        return getValueFromCache(vo, excludeSelectField, wherePart);
    }
    
    /**
     * ������������ȡVO
     * @return �޼�¼ʱ����null
     */
    public static <T extends SuperVO> T[] getVOArrayByPkArray(Class<T> clazz,
            String[] pkValueArray) throws BusinessException {
        return getValueFromCacheByPk(clazz, pkValueArray);
    }

    /**
     * ��������ȡVO
     * @return �޼�¼ʱ����null
     */
    public static <T extends SuperVO> T getVOByPk(Class<T> clazz, String pkValue)
            throws BusinessException {
        if (StringUtils.isEmpty(pkValue))
            return null;
        T[] result = getValueFromCacheByPk(clazz, pkValue == null ? null
                : new String[] { pkValue });
        return ArrayUtils.isEmpty(result) ? null : result[0];

    }

    private static <T extends SuperVO> T[] getValueFromCacheByForeign(
            Class<T> clazz, String fieldName, String[] fieldValueArray) throws BusinessException {
        T vo = getCache(clazz);
        if (vo == null)
            return null;
        return getValueFromCache(clazz, vo, fieldName, fieldValueArray);
    }
    
    private static <T extends SuperVO> T[] getValueFromCacheByPk(
            Class<T> clazz, String[] pkValueArray) throws BusinessException {
        T vo = getCache(clazz);
        if (vo == null)
            return null;
        @SuppressWarnings("deprecation")
        String sFieldName = vo.getPKFieldName();
        return getValueFromCache(clazz, vo, sFieldName, pkValueArray);
    }
    
    /**
     * ͨ��ǰ̨�����ѯ����������Ϣ
     * 
     * @param tablename
     * @param pkFieldName
     * @param pkValue
     * @param attrs
     * @return
     * @throws BusinessException
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    private static <T extends SuperVO> T[] getValueFromCache(Class<T> clazz, T vo,
            String sFieldName, String[] pkValueArray) throws BusinessException {
        String condition = SqlUtils.getInStr(sFieldName, pkValueArray, false);
        if (StringUtils.isEmpty(condition))
            condition = StringUtils.EMPTY;
        else
            condition = " where " + condition;
        String[] attArray = vo.getAttributeNames();
        String sql = "select " + StringUtil.toString(attArray, ",") + " from "
                + vo.getTableName() + condition;
        List<T> result = (List<T>) DBCacheQueryFacade.runQuery(sql,
                new BeanListProcessor(clazz));
        if (CollectionUtils.isEmpty(result))
            return null;
        T[] tArray = (T[]) Array.newInstance(clazz,
                result.size());
        return result.toArray(tArray);
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    private static <T extends SuperVO> T[] getValueFromCache(T vo, 
            String[] excludeSelectField, String wherePart) throws BusinessException {
        String condition = StringUtils.EMPTY;
        if (StringUtils.isEmpty(wherePart)) {
            condition = " where 1 = 1";
        } else {
            if (wherePart.indexOf("where") < 0) {
                condition = " where " + wherePart;
            } else {
                condition = " " + wherePart;
            }
        }
        
        String sql = StringUtils.EMPTY;
        if (ArrayUtils.isEmpty(excludeSelectField)) {
            sql = "select * from " + vo.getTableName() + condition;
        } else {
            String[] attArray = parseSelectField(vo, excludeSelectField);
            sql = "select " + StringUtil.toString(attArray, ",") + " from "
            + vo.getTableName() + condition;
        }
        List<T> result = (List<T>) DBCacheQueryFacade.runQuery(sql,
                new BeanListProcessor(vo.getClass()));
        if (CollectionUtils.isEmpty(result))
            return null;
        T[] tArray = (T[]) Array.newInstance(vo.getClass(),
                result.size());
        return result.toArray(tArray);
    }
    
    private static String[] parseSelectField(SuperVO vo, String[] excludeShowField) {
        String[] attArray = vo.getAttributeNames();
        if (!ArrayUtil.isEmpty(excludeShowField)) {
            List<String> attList = new ArrayList<String>(attArray.length);
            for (String att : attArray) {
                boolean bExclude = false;
                for (String exclude : excludeShowField) {
                    if (att.equals(exclude)) {
                        bExclude = true;
                        break;
                    }
                }
                if (!bExclude) {
                    attList.add(att);
                }
            }
            attArray = attList.toArray(new String[attList.size()]);
        }
        return attArray;
    }
    

    private static Map<Class<SuperVO>, SuperVO> instanceCache = new HashMap<Class<SuperVO>, SuperVO>();

    @SuppressWarnings("unchecked")
    private static <T extends SuperVO> T getCache(Class<T> clazz) {
        if (clazz == null)
            return null;
        T instance = (T) instanceCache.get(clazz);
        if (instance == null) {
            try {
                instance = clazz.newInstance();
                instanceCache.put((Class<SuperVO>) clazz, (SuperVO) instance);
            } catch (InstantiationException e) {
                Logger.error(e.getMessage());
            } catch (IllegalAccessException e) {
                Logger.error(e.getMessage());
            }
        }
        return instance;
    }

}
