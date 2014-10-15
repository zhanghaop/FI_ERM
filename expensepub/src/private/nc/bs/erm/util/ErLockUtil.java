package nc.bs.erm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.vo.fipub.utils.KeyLock;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 业务锁工具类
 * 
 * @author lvhj
 *
 */
public class ErLockUtil {
	
	
	public interface IErLockProvider{
		
		public String getLockPK(Object obj);
	}
	

	/**
	 * 根据接口IErLockProvider获得锁pk的方式，加业务锁
	 * 
	 * @param objs
	 * @param lockprovider
	 * @throws BusinessException
	 */
	public static void lockObject(Object[] objs,IErLockProvider lockprovider) throws BusinessException {
		List<String> pkList = new ArrayList<String>();
		for (Object vo : objs) {
			String lockPK = lockprovider.getLockPK(vo);
			pkList.add(lockPK);
		}
		KeyLock.dynamicLockWithException(pkList);
	}
	
	/**
	 * CircularlyAccessibleValueObject按字段进行加锁，生成的key格式为 message-fieldvalue1-fieldvalue2-fieldvaluen
	 * 
	 * @param vos
	 * @param fields
	 * @param message	
	 * @throws BusinessException
	 */
	public static void lockVO(String[] fields,String message,CircularlyAccessibleValueObject... vos) throws BusinessException {
		if(vos == null || vos.length ==0||fields==null||fields.length ==0){
			return ;
		}
		List<String> pkList = new ArrayList<String>();
		StringBuffer bf = new StringBuffer(100);
		for (CircularlyAccessibleValueObject vo : vos) {
			if(bf.length() > 0){
				bf.delete(0, bf.length());
			}
			bf.append(message);
			for (int i = 0; i < fields.length; i++) {
				bf.append("-");
				bf.append(vo.getAttributeValue(fields[i]));
			}
			pkList.add(bf.toString());
		}
		KeyLock.dynamicLockWithException(pkList);
	}
	/**
	 * CircularlyAccessibleValueObject按主键进行加锁，生成的key格式为 message-pkvalue
	 * 
	 * @param vos
	 * @param message	
	 * @throws BusinessException
	 */
	public static void lockVOByPk(String message,CircularlyAccessibleValueObject... vos) throws BusinessException {
		if(vos == null || vos.length ==0){
			return ;
		}
		List<String> pkList = new ArrayList<String>();
		for (CircularlyAccessibleValueObject vo : vos) {
			pkList.add(message+"-"+vo.getPrimaryKey());
		}
		KeyLock.dynamicLockWithException(pkList);
	}
	
	/**
	 * AggregatedValueObject的主表按字段进行加锁，生成的key格式为 message-fieldvalue1-fieldvalue2-fieldvaluen
	 * 
	 * @param vos
	 * @param fields
	 * @param message	
	 * @throws BusinessException
	 */
	public static void lockAggVO(String[] fields,String message,AggregatedValueObject... vos) throws BusinessException {
		if(vos == null || vos.length ==0||fields==null||fields.length ==0){
			return ;
		}
		List<String> pkList = new ArrayList<String>();
		StringBuffer bf = new StringBuffer(100);
		for (AggregatedValueObject vo : vos) {
			if(bf.length() > 0){
				bf.delete(0, bf.length());
			}
			bf.append(message);
			for (int i = 0; i < fields.length; i++) {
				bf.append("-");
				bf.append(vo.getParentVO().getAttributeValue(fields[i]));
			}
			pkList.add(bf.toString());
		}
		KeyLock.dynamicLockWithException(pkList);
	}
	/**
	 * AggregatedValueObject的主表按主键进行加锁，生成的key格式为 message-pkvalue
	 * 
	 * @param vos
	 * @param message	
	 * @throws BusinessException
	 */
	public static void lockAggVOByPk(String message,AggregatedValueObject... vos) throws BusinessException {
		if(vos == null || vos.length ==0){
			return ;
		}
		List<String> pkList = new ArrayList<String>();
		for (AggregatedValueObject vo : vos) {
			pkList.add(message+"-"+vo.getParentVO().getPrimaryKey());
		}
		KeyLock.dynamicLockWithException(pkList);
	}
	
	/**
	 * 主键进行加锁，生成的key格式为 message-pkvalue
	 * @param message
	 * @param pkList 主键集合
	 * @throws BusinessException
	 */
	public static void lockByPk(String message, Collection<String> pkList) throws BusinessException {
		if (pkList == null || pkList.size() == 0) {
			return;
		}
		List<String> lockList = new ArrayList<String>();
		for (String pk : pkList) {
			lockList.add(message + "-" + pk);
		}
		KeyLock.dynamicLockWithException(lockList);
	}
}
