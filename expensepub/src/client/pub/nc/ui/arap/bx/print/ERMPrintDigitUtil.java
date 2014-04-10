package nc.ui.arap.bx.print;

import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.fi.pub.Currency;
import nc.pubitf.uapbd.CurrencyRateUtil;
import nc.vo.bd.currinfo.CurrinfoVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IExAggVO;

import org.apache.commons.lang.StringUtils;
/**
 * 报销打印工具类
 * @author wangled
 *
 */
public class ERMPrintDigitUtil {

	public class ErmPrintDigitConst {
		public static final String FIELD_GLOBAL = "field_global_money"; // 全局本币
		public static final String FIELD_GROUP = "field_group_money"; // 集团本币
		public static final String FIELD_LOCAL = "field_local_currtype"; // 组织本币
		public static final String FIELD_GLOBAL_RATE = "field_global_rate"; // 全局本币汇率
		public static final String FIELD_GROUP_RATE = "field_group_rate"; // 集团本币汇率
		public static final String FIELD_LOCAL_RATE = "field_local_rate"; // 组织本币汇率
		public static final String FIELD_MONEY = "field_money"; // 原币
		public static final String FIELD_QUANTITY = "field_quantity"; //数量
		public static final String FIELD_PRICE = "field_price"; //价格
		public static final String FIELD_TAXRATE = "field_taxrate"; //价格
	}
	
	/**
	 * 根据币种精度对数据进行调整，适用于有多个币种的情况。需要将各币种所对应的列字段数组以Map形式传入，目前支持全局本币，集团本币，组织本币,原币
	 * 添加了对汇率精度的设置
	 * 
	 * @param datas
	 * @param fieldsMap
	 * @param orgFiledName // 财务组织字段名
	 * @param currFiledName // 原币字段名
	 * @return
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws BusinessException
	 */
	public static Object[] getDatas(Object[] datas, Map<String, String[]> fieldsMap,
			String orgFiledName, String currFieldName) throws NumberFormatException,
			IllegalArgumentException, IllegalAccessException, BusinessException {

		return getDatas (datas,fieldsMap,orgFiledName,currFieldName,null);
	}
	
	/**
	 * 添加了对汇率、数量精度的设置
	 * @param datas
	 * @param fieldsMap
	 * @param orgFiledName
	 * @param currFiledName
	 * @param materialFieldName
	 * @return
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws BusinessException
	 */
	public static Object[] getDatas(Object[] datas, Map<String, String[]> fieldsMap,
			String orgFiledName, String currFiledName, String materialFieldName) throws NumberFormatException,
			IllegalArgumentException, IllegalAccessException, BusinessException {

		if (datas == null || datas.length == 0) {
			return null;
		}
		
		//如果物料字段是空，设置一个默认值
		if(materialFieldName == null) {
			materialFieldName = IBillFieldGet.MATERIAL;
		}

		String pk_org = null;
		String pk_group = null;
		if (datas[0] instanceof AggregatedValueObject) {
			pk_org = (String) ((AggregatedValueObject) datas[0]).getParentVO().getAttributeValue(orgFiledName);
			pk_group = (String) ((AggregatedValueObject) datas[0]).getParentVO().getAttributeValue(IBillFieldGet.PK_GROUP);
		} else {
			pk_org = (String) ((SuperVO) datas[0]).getAttributeValue(orgFiledName);
			pk_group = (String) ((SuperVO) datas[0]).getAttributeValue(IBillFieldGet.PK_GROUP);
		}
		
		//币种数组，分别是全局、集团、组织币种
		String[] currtypeArray = new String[3];
		currtypeArray[0] = Currency.getGlobalCurrPk(pk_org);
		currtypeArray[1] = Currency.getGroupCurrpk(InvocationInfoProxy.getInstance().getGroupId());
		currtypeArray[2] = Currency.getLocalCurrPK(pk_org);

		setCurrDigit(datas,fieldsMap,currFiledName,currtypeArray);
		setRateDigit(datas,fieldsMap,currFiledName,currtypeArray,pk_org);
		setQuantityDigit(datas,fieldsMap,materialFieldName);
		setPriceDigit(datas,fieldsMap,pk_group);
		setTaxRateDigit(datas,fieldsMap);

		return datas;
	}
	
	private static Object[] setRateDigit(Object[] datas,Map<String, String[]> fieldsMap, String currFiledName,String[] currtypeArray,String pk_org) 
		throws NumberFormatException, IllegalArgumentException, IllegalAccessException, BusinessException {
		
		String[] field_grobal_rate = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_GLOBAL_RATE);
		if (field_grobal_rate != null && field_grobal_rate.length > 0) {
			
			formatDatasOnRateFields(datas, field_grobal_rate, currFiledName,currtypeArray[0],pk_org);
		}

		String[] field_group_rate = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_GROUP_RATE);
		if (field_group_rate != null && field_group_rate.length > 0) {
			
			formatDatasOnRateFields(datas, field_group_rate, currFiledName, currtypeArray[1],pk_org);
		}

		String[] field_local_rate = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_LOCAL_RATE);
		if (field_local_rate != null && field_local_rate.length > 0) {
			
			formatDatasOnRateFields(datas, field_local_rate, currFiledName, currtypeArray[2],pk_org);
		}
		
		return datas;
	}
	
	private static Object[] formatDatasOnRateFields(Object[] datas, String[] fields, String currFieldName,String dest_currtype,String pk_org) 
			throws NumberFormatException,IllegalArgumentException, IllegalAccessException, BusinessException {
		if (datas[0] instanceof AggregatedValueObject) {
			for (int i = 0; i < datas.length; i++) {
				AggregatedValueObject aggVo = (AggregatedValueObject) datas[i];
				formatVOOnRateFields(aggVo.getParentVO(), fields, currFieldName, dest_currtype,pk_org);
				if(aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0) {
					continue;
				}
				for (Object bodyvo : aggVo.getChildrenVO()) {
					formatVOOnRateFields(bodyvo, fields, currFieldName, dest_currtype,pk_org);
				}
			}
		} else {
			for (Object vo : datas) {
				formatVOOnRateFields(vo, fields, currFieldName, dest_currtype,pk_org);
			}
		}

		return datas;
	}
	
	private static void formatVOOnRateFields(Object vo, String[] fields, String currFieldName,String dest_currtype,String pk_org) 
		throws NumberFormatException,IllegalArgumentException, IllegalAccessException, BusinessException {
		if (fields == null || fields.length == 0 || currFieldName==null || dest_currtype == null) {
			return ;
		}
		String src_currtype = (String) ((SuperVO) vo).getAttributeValue(currFieldName);
		CurrinfoVO currinfoVO = CurrencyRateUtil.getInstanceByOrg(pk_org).getCurrinfoVO(src_currtype,dest_currtype);
		
		int rateDigit = 2;
		
		try{
			rateDigit = currinfoVO.getRatedigit();
		}catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		
		formatVOOnFields(vo,fields,rateDigit);
	}
	
	private static Object[] setQuantityDigit(Object[] datas, Map<String, String[]> fieldsMap, String materialFieldName) 
		throws NumberFormatException,IllegalArgumentException, IllegalAccessException, BusinessException {
		String[] field_quantity = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_QUANTITY);
		if (field_quantity == null || field_quantity.length == 0) {
			return datas;
		}		
		
		Map<String,Integer> materialDigMap=new HashMap<String, Integer>();
		if (datas[0] instanceof AggregatedValueObject) {
			for (int i = 0; i < datas.length; i++) {
				AggregatedValueObject aggVo = (AggregatedValueObject) datas[i];
				formatVOOnQuantityFields(aggVo.getParentVO(), field_quantity, materialFieldName,materialDigMap);
				if(aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0) {
					continue;
				}
				for (Object bodyvo : aggVo.getChildrenVO()) {
					formatVOOnQuantityFields(bodyvo, field_quantity, materialFieldName,materialDigMap);
				}
			}
		} else {
			for (Object vo : datas) {
				formatVOOnQuantityFields(vo, field_quantity, materialFieldName,materialDigMap);
			}
		}
		
		return datas;
	}	
	
	private static Object[] setPriceDigit(Object[] datas, Map<String, String[]> fieldsMap,String pk_group) 
		throws NumberFormatException,IllegalArgumentException, IllegalAccessException, BusinessException {
		String[] field_price = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_PRICE);
		if (field_price == null || field_price.length == 0 || pk_group == null ) {
			return datas;
		}	
		
		Integer digit = nc.pubitf.para.SysInitQuery.getParaInt(pk_group, "NC004");
		
		if (datas[0] instanceof AggregatedValueObject) {
			for (int i = 0; i < datas.length; i++) {
				AggregatedValueObject aggVo = (AggregatedValueObject) datas[i];
				formatVOOnFields(aggVo.getParentVO(), field_price, digit);
				if(aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0) {
					continue;
				}
				for (Object bodyvo : aggVo.getChildrenVO()) {
					formatVOOnFields(bodyvo, field_price, digit);
				}
			}
		} else {
			for (Object vo : datas) {
				formatVOOnFields(vo, field_price, digit);
			}
		}		
		return datas;
	}
	
	private static Object[] setTaxRateDigit(Object[] datas, Map<String, String[]> fieldsMap) 
		throws NumberFormatException,IllegalArgumentException, IllegalAccessException, BusinessException {
		String[] field_taxrate = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_TAXRATE);
		if (field_taxrate == null || field_taxrate.length == 0) {
			return datas;
		}	
		
		Integer digit = 2;
		
		if (datas[0] instanceof AggregatedValueObject) {
			for (int i = 0; i < datas.length; i++) {
				AggregatedValueObject aggVo = (AggregatedValueObject) datas[i];
				formatVOOnFields(aggVo.getParentVO(), field_taxrate, digit);
				if(aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0) {
					continue;
				}
				for (Object bodyvo : aggVo.getChildrenVO()) {
					formatVOOnFields(bodyvo, field_taxrate, digit);
				}
			}
		} else {
			for (Object vo : datas) {
				formatVOOnFields(vo, field_taxrate, digit);
			}
		}		
		return datas;
	}
	
	private static void formatVOOnFields (Object vo, String[] fields, int digit) throws BusinessException {
		if (fields == null || fields.length == 0 ) {
			return ;
		}
		for (int i = 0; i < fields.length; i++) {
			String fieldname = fields[i];
			Object value = ((SuperVO) vo).getAttributeValue(fieldname);
			if (value != null && value instanceof UFDouble) {
				((SuperVO) vo).setAttributeValue(fieldname, new UFDouble(Double.valueOf(
						String.valueOf(value)).doubleValue(), digit));
			}
		}	
	}
	
	private static void formatVOOnQuantityFields (Object vo, String[] fields, String materialFieldName, Map<String, Integer> materialDigMap) throws BusinessException {
		if (fields == null || fields.length == 0 || materialFieldName == null) {
			return ;
		}
		int digit = 2;
		String material = (String) ((SuperVO) vo).getAttributeValue(materialFieldName);
		if(material != null) {
			if(materialDigMap.containsKey(material)){
				digit=materialDigMap.get(material).intValue();
			}else{
				Map<String, MaterialVO> mmaterialMap;
				mmaterialMap = NCLocator.getInstance().lookup(nc.pubitf.uapbd.IMaterialPubService.class).queryMaterialBaseInfoByPks(new String[]{material.toString()},new String[]{"pk_measdoc"});
				Integer[] precisionByPks = nc.pubitf.uapbd.MeasdocUtil.getInstance().getPrecisionByPks(new String[]{mmaterialMap.get(material.toString()).getPk_measdoc()});
				if(precisionByPks!=null && precisionByPks.length!=0 && precisionByPks[0]!=null){
					digit = precisionByPks[0].intValue();
				}
				materialDigMap.put(material, Integer.valueOf(digit));
			}
		} 
		formatVOOnFields(vo,fields,digit);
	}
	
	/**
	 * 设置币种精度
	 * @param datas
	 * @param fieldsMap
	 * @param orgFiledName
	 * @param currFiledName
	 * @param currtypeArray
	 * @return
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws BusinessException 
	 */
	private static Object[] setCurrDigit(Object[] datas, Map<String, String[]> fieldsMap,
			String currFiledName,String[] currtypeArray) throws NumberFormatException, 
			IllegalArgumentException, IllegalAccessException, BusinessException {
		String[] field_grobal = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_GLOBAL);
		if (field_grobal != null && field_grobal.length > 0) {
			
			formatDatasOnCurrFields(datas, field_grobal, currtypeArray[0], null);
		}

		String[] field_group = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_GROUP);
		if (field_group != null && field_group.length > 0) {
			
			formatDatasOnCurrFields(datas, field_group, currtypeArray[1], null);
		}

		String[] field_local = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_LOCAL);
		if (field_local != null && field_local.length > 0) {
			
			formatDatasOnCurrFields(datas, field_local, currtypeArray[2], null);
		}

		String[] field_money = (String[]) fieldsMap.get(ErmPrintDigitConst.FIELD_MONEY);
		if (field_money != null && field_money.length > 0) {
			formatDatasOnCurrFields(datas, field_money, null, currFiledName);
		}
		
		return datas;
	}

	private static Object[] formatDatasOnCurrFields(Object[] datas, String[] fields,
			String pk_currtype, String currFiledName) throws NumberFormatException,
			IllegalArgumentException, IllegalAccessException, BusinessException {
		Integer maxDigit = 0;
		if (datas[0] instanceof IExAggVO) {
			// 处理多子表情况
			for (int i = 0; i < datas.length; i++) {
				IExAggVO aggVo = (IExAggVO) datas[i];
				maxDigit = Math.max(maxDigit, formatVOOnCurrFields(aggVo.getParentVO(), fields, pk_currtype, currFiledName, maxDigit));
				if(aggVo.getAllChildrenVO() == null || aggVo.getAllChildrenVO().length == 0) {
					continue;
				}
				for (Object bodyvo : aggVo.getAllChildrenVO()) {
					maxDigit = Math.max(maxDigit, formatVOOnCurrFields(bodyvo, fields, pk_currtype, currFiledName, maxDigit));
				}
			}
		} else if (datas[0] instanceof AggregatedValueObject) {
			for (int i = 0; i < datas.length; i++) {
				AggregatedValueObject aggVo = (AggregatedValueObject) datas[i];
				maxDigit = Math.max(maxDigit, formatVOOnCurrFields(aggVo.getParentVO(), fields, pk_currtype, currFiledName, maxDigit));
				if(aggVo.getChildrenVO() == null || aggVo.getChildrenVO().length == 0) {
					continue;
				}
				for (Object bodyvo : aggVo.getChildrenVO()) {
					maxDigit = Math.max(maxDigit, formatVOOnCurrFields(bodyvo, fields, pk_currtype, currFiledName, maxDigit));
				}
			}
		} else {
			for (Object vo : datas) {
				maxDigit = Math.max(maxDigit, formatVOOnCurrFields(vo, fields, pk_currtype,
						currFiledName, maxDigit));
			}
		}

		return datas;
	}

	private static Integer formatVOOnCurrFields(Object vo, String[] fields, String pk_currtype,
			String currFiledName, Integer maxDigit) throws NumberFormatException,
			IllegalArgumentException, IllegalAccessException, BusinessException {
		if (fields == null || fields.length == 0) {
			return maxDigit;
		}

		// 如果pk_currtype为空，根据币种字段取币种pk。一般这个字段指原币字段
		if (pk_currtype == null && currFiledName != null) {
			pk_currtype = (String) ((SuperVO) vo).getAttributeValue(currFiledName);
		}

		int currDigit = Currency.getCurrDigit(pk_currtype);
		if (!StringUtils.isEmpty(pk_currtype)) {
			maxDigit = Math.max(maxDigit, currDigit);
		} else {
			currDigit = maxDigit;
		}
		
		formatVOOnFields(vo,fields,currDigit);
		return maxDigit;
	}

}
