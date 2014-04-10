package nc.erm.pub.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.itf.uap.IUAPQueryBS;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.ermbill.fldcontrast.FiBillFieldContrastVO;
import nc.vo.pub.BusinessException;

/**
 * 财务单据预算控制字段对照数据缓存
 */
public class FiBillFieldContrastCache {


	/**
	 * 不同数据源的缓存空间
	 */
	private static Hashtable<String, HashMap<String, HashMap<String, String>>> 
	dsName_subSysField_map = new Hashtable<String, HashMap<String,HashMap<String,String>>>();
	private static Hashtable<String, HashMap<String, HashMap<String, String>>> 
	dsName_billTypeField_map = new Hashtable<String, HashMap<String,HashMap<String,String>>>();
	private static Hashtable<String, HashMap<String, String>> 
	dsName_billTypeSubSys_map = new Hashtable<String, HashMap<String,String>>();
	private static Hashtable<String, String[]> dsName_CommonAttfields_map = new Hashtable<String, String[]>();
	
	private FiBillFieldContrastCache(){}

	private static String getCurrentDs() {
		if (RuntimeEnv.getInstance().isRunningInServer()) {
			//获取当前使用的数据源名称
			String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
			return dsName;
		} else {
			//FIXME
			//获得当前登录的数据源
//			nc.vo.sm.config.Account account = ClientEnvironment.getInstance().getConfigAccount();
//			String dsName = account.getDataSourceName();
//			return dsName;
			return null;
		}
	}

	/**
	 * 获得 子系统控制字段对照缓存
	 * @return HashMap：key:子系统id ——> value:控制字段对照Map
	 */
	private static HashMap<String, HashMap<String,String>> getSubSysID_ContrastMap_Map() {
		String currDS = getCurrentDs();
		if (dsName_subSysField_map.get(currDS) == null) {		
			HashMap<String, HashMap<String, String>> subSysID_contrast_map
			= new HashMap<String, HashMap<String,String>>();
			dsName_subSysField_map.put(currDS, subSysID_contrast_map);
		}
		return dsName_subSysField_map.get(currDS);
	}


	/**
	 * 获得 单据大类控制字段对照缓存
	 * @return HashMap：key:单据大类编码 ——> value:控制字段对照Map
	 */
	private static HashMap<String, HashMap<String,String>> getBillType_ContrastMap_Map() {
		String currDS = getCurrentDs();
		if (dsName_billTypeField_map.get(currDS) == null) {
			HashMap<String, HashMap<String, String>> billType_contrast_map
			= new HashMap<String, HashMap<String,String>>();
			dsName_billTypeField_map.put(currDS, billType_contrast_map);
		}
		return dsName_billTypeField_map.get(currDS);
	}
	
	
	private static HashMap<String, String> getBillType_SubSys_Map() {
		String currDS = getCurrentDs();
		if (dsName_billTypeSubSys_map.get(currDS) == null) {
			HashMap<String, String> billType_SubSys_map
			= new HashMap<String, String>();
			dsName_billTypeSubSys_map.put(currDS, billType_SubSys_map);
		}
		return dsName_billTypeSubSys_map.get(currDS);
	}

	private static HashMap<String,String> getContrastMapBySubSysID(String subSysID){
		HashMap<String,String> hash = getSubSysID_ContrastMap_Map().get(subSysID);
		if(hash == null){
			hash = new HashMap<String,String>();
			getSubSysID_ContrastMap_Map().put(subSysID,hash);
		}
		return hash;
	}


	private static HashMap<String,String> getContrastMapByBillType(String billType){
		HashMap<String,String> hash = getBillType_ContrastMap_Map().get(billType);
		if(hash == null){
			hash = new HashMap<String,String>();
			getBillType_ContrastMap_Map().put(billType,hash);
		}
		return hash;
	}

	/**
	 * 根据子系统标识和通用字段名获取子系统对应的字段名
	 * @param subSysID
	 * @param commAttr
	 * @return
	 */
	public synchronized static String getBusiAttFieldBySubSysID(String subSysID, String commAttr) {
		HashMap<String,String> subSysMap = getContrastMapBySubSysID(subSysID);
		if(!subSysMap.containsKey(commAttr)){
			try {
				cacheDataBySubSysID(subSysID);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return subSysMap.get(commAttr);
	}
	
	
	

	/**
	 * 根据子系统标识和具体字段名获取对应的通用字段名
	 * @param subSysID
	 * @param commAttr
	 * @return
	 */
	public synchronized static String getCommonAttFieldBySubSysID(String subSysID, String busiAttr) {
		HashMap<String,String> subSysMap = getContrastMapBySubSysID(subSysID);
		if(!subSysMap.containsValue(busiAttr)){
			try {
				cacheDataBySubSysID(subSysID);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		for (Map.Entry<String, String> entry : subSysMap.entrySet()) {
			if(entry.getValue() != null && entry.getValue().equals(busiAttr))
				return entry.getKey();
		}
		return null;
	}
	


//	/**
//	 * 根据单据大类和通用字段名获取该单据类型下对应的字段名
//	 * @param subSysID
//	 * @param commAttr
//	 * @return
//	 */
//	public synchronized static String getBusiAttFieldByBillType(String billType, String commAttr) {
//		HashMap<String,String> billTypeMap = getContrastMapByBillType(billType);
//		if(!billTypeMap.containsKey(commAttr)){
//			try {
//				cacheDataByBillType(billType);
//			} catch (BusinessException e) {
//				ExceptionHandler.consume(e);
//			}
//		}
//		return billTypeMap.get(commAttr);
//	}
	
	
//	public synchronized static String getSubSysIDByBillType(String billType) {
//		HashMap<String,String> map = getBillType_SubSys_Map();
//		if(!map.containsKey(billType)){
//			try {
//				cacheData();
//			} catch (BusinessException e) {
//				ExceptionHandler.consume(e);
//			}
//		}
//		return map.get(billType);
//	}


	/**
	 * 获得子系统下所有的单据字段对照记录并缓存
	 */
	private static void cacheDataBySubSysID(String subSysID) throws BusinessException {
		HashMap<String,String> map = getContrastMapBySubSysID(subSysID);
		IUAPQueryBS uapQry = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		Collection  fldContrasts = uapQry.retrieveByClause(FiBillFieldContrastVO.class," subsys_id='"+subSysID+"'");
		FiBillFieldContrastVO vo = null;
		for (Iterator iter = fldContrasts.iterator(); iter.hasNext();) {
			vo = (FiBillFieldContrastVO) iter.next();
			map.put(vo.getCommon_attfield(), vo.getBusi_attfield());
		}
		
//		cacheCommonAttfields(getCurrentDs());
	}


	/**
	 * 获得某单据大类下所有的字段对照记录并缓存
	 */
//	private static void cacheDataByBillType(String billType) throws BusinessException {
//		HashMap<String,String> map = getContrastMapByBillType(billType);
//		IUAPQueryBS uapQry = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
//		Collection  fldContrasts = uapQry.retrieveByClause(FiBillFieldContrastVO.class," billtypecode='"+billType+"'");
//		FiBillFieldContrastVO vo = null;
//		for (Iterator iter = fldContrasts.iterator(); iter.hasNext();) {
//			vo = (FiBillFieldContrastVO) iter.next();
//			map.put(vo.getCommon_attfield(), vo.getBusi_attfield());
//		}
//		
////		cacheCommonAttfields(getCurrentDs());
//	}
	
//	private static void cacheData() throws BusinessException {
//		HashMap<String,String> map = getBillType_SubSys_Map();
//		IUAPQueryBS uapQry = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
//		Collection  fldContrasts = uapQry.retrieveAll(FiBillFieldContrastVO.class);
//		FiBillFieldContrastVO vo = null;
//		for (Iterator iter = fldContrasts.iterator(); iter.hasNext();) {
//			vo = (FiBillFieldContrastVO) iter.next();
//			map.put(vo.getBilltypecode(), vo.getSubsys_id());
//		}
//		
////		cacheCommonAttfields(getCurrentDs());
//	}
	
	
//	public synchronized static String[] getAllCommonAttfields() {
//		String currDS = getCurrentDs();
//		if (dsName_CommonAttfields_map.get(currDS) == null) {
//			try {
//				cacheCommonAttfields(currDS);
//			} catch (BusinessException e) {
//				ExceptionHandler.consume(e);
//			}
//		}
//		return dsName_CommonAttfields_map.get(currDS);
//	}
//
//	private static void cacheCommonAttfields(String currDS) throws BusinessException {
//		String[] commonAttFields = queryCommonAttfields();
//		dsName_CommonAttfields_map.put(currDS, commonAttFields);
//	}
//
//	@SuppressWarnings("unchecked")
//	private static String[] queryCommonAttfields() throws BusinessException {
//		IUAPQueryBS uapQry = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
//		ArrayList  commonAttFieldList = (ArrayList)uapQry.executeQuery("select distinct common_attfield from fibill_fieldcontrast", new ColumnListProcessor());
//		return (String[])commonAttFieldList.toArray(new String[0]);
//	}

}