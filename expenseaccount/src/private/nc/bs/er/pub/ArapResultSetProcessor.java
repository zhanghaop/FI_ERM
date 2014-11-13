/**
 * @(#)ArapResultSetProcessor.java	V5.0 2005-12-29
 *
 * Copyright 1988-2005 UFIDA, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of UFSoft, Inc.
 * Use is subject to license terms.
 *
 */

package nc.bs.er.pub;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.fipub.mapping.IArapMappingMeta;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * <p>
 * 类的主要说明。类设计的目标，完成什么样的功能。
 * </p>
 * <p>
 * <Strong>主要的类使用：</Strong>
 * <ul>
 * <li>如何使用该类</li>
 * <li>是否线程安全</li>
 * <li>并发性要求</li>
 * <li>使用约束</li>
 * <li>其他</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * <ul>
 * <li>
 * <ul>
 * <li><strong>修改人:</strong>rocking</li>
 * <li><strong>修改日期：</strong>2005-12-29</li>
 * <li><strong>修改内容：<strong></li>
 * </ul>
 * </li>
 * <li> </li>
 * </ul>
 * </p>
 * 
 * @author rocking
 * @version V5.0
 * @since V3.1
 */

public class ArapResultSetProcessor implements ResultSetProcessor {
	
	private static final long serialVersionUID = -2445410147980532742L;

	@SuppressWarnings("unchecked")
	private Class voCls;

	private IArapMappingMeta meta;

	private Integer initPos = 0;

	/**
	 * 页数
	 */
	private Integer count = -1;
	
	private IRSChecker rsChecker;

	// int[] types=null;
	/**
	 * 
	 * 2005-12-29
	 * 
	 * @author:rocking
	 */
	public ArapResultSetProcessor(Class voCls, IArapMappingMeta meta) {
		super();
		this.meta = meta;
		this.voCls = voCls;
	}

	public ArapResultSetProcessor(Class voCls, IArapMappingMeta meta, Integer initPos, Integer count) {
		super();
		this.meta = meta;
		this.voCls = voCls;
		this.initPos = initPos;
		this.count = count;
	}
	public ArapResultSetProcessor(Class voCls, IArapMappingMeta meta, Integer initPos, Integer count,IRSChecker c) {
		super();
		this.meta = meta;
		this.voCls = voCls;
		this.initPos = initPos;
		this.count = count;
		rsChecker=c;
	}
	
	@SuppressWarnings("unchecked")
	public static CircularlyAccessibleValueObject createVOInstance(
			Class voClass) throws SQLException {
		try {
			return (CircularlyAccessibleValueObject) voClass.newInstance();
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}
	/**
	 * 返回查询的开始位置
	 * @return
	 */
	private int getStartPos() {
		if (null == initPos || initPos.intValue() < 0) {
			return 0;
		} else {
			return initPos.intValue();
		}
	}

	/**
	 * 处理借款报销结果集
	 * @author chendya
	 */
	public Object handleResultSet(ResultSet rs) throws SQLException {
		
		//返回的数据集
		List<CircularlyAccessibleValueObject> retLst = new ArrayList<CircularlyAccessibleValueObject>();
		
		//已循环的次数
		int nCount = 0;
		
		// 当前位置
		int currPos = 0;
		
		// 查询结果集的起始位置
		final int startPos = getStartPos();
		
		//总列数
		final int columnCount = rs.getMetaData().getColumnCount();
		
		//数据库列名
		String[] colNames = new String[columnCount + 1];
		//数据库列名对应的元数据字段名
		String[] attrNames = new String[columnCount + 1];
		//与数据字段类型
		int[] colTypes=	new int[columnCount + 1];
		for (int k = 1; k <= columnCount; k++) {
			//数据库列名
			colNames[k] = rs.getMetaData().getColumnName(k);
			
			//根据数据库列名返回vo的字段名
			attrNames[k] = meta.getAttrNameByColName(colNames[k]);
			
			//根据vo字段名返回字段类型
			colTypes[k] = meta.getDataTypeByAttrName(attrNames[k]);
		}
		
		// 进行压缩操作  
		final long start = System.currentTimeMillis();
		Map<Object,Object> hashTable = Collections.synchronizedMap(new HashMap<Object,Object>());
		//记录需要进行过滤处理的VO
		while (rs.next()
				&& (count == null || count.intValue() < 0 || nCount < count.intValue())) {
			if (currPos + 1 <= startPos) {
				//未达到查询的起始位置，继续循环
				currPos++;
				continue;
			}
			//实例化VO
			CircularlyAccessibleValueObject vo = createVOInstance(voCls);
			for (int i = 1; i <= columnCount; i++) {
				String colName = colNames[i];
				String attrName = attrNames[i];
				if (null == attrName) {
					continue;
				}
				Object obj = rs.getObject(colName);
				Object key = obj;
				switch (colTypes[i]) {
					//字符串儿优先处理提高效率 added by chendya
					case IArapMappingMeta.TYPE_STRING:
						break;
					case IArapMappingMeta.TYPE_INT:
						if (null != obj) {
							obj = setObj(obj);
							key = obj;
						}
						break;
					case IArapMappingMeta.TYPE_BOOLEAN: {
						if (obj != null) {
							obj = UFBoolean.valueOf(obj.toString());
							key = obj;
						}
						break;
					}
					case IArapMappingMeta.TYPE_DATE: {
						if (obj != null) {
							if (obj.toString().trim().length()> 0) { 
								//obj = new UFDate(obj.toString());
								//kongxl解决多时区查询后日期不正确       NCdp203487637
								obj = new UFDate(new UFDateTime(obj.toString()).getMillis());
								key = "UFDate" + obj.toString();
							}
						}
						break;
					}
					case IArapMappingMeta.TYPE_DATETIME: {
						if (obj != null) {
							obj = new UFDateTime(obj.toString());
							key = obj;
						}
						break;
					}
					case IArapMappingMeta.TYPE_DOUBLE: {
						if (obj != null) {
							try {
								obj = new UFDouble((BigDecimal) obj);
							} catch (Exception e) {
								obj = new UFDouble(obj.toString());
							}
							key = "UFDouble" + obj.toString();
						}
						break;
					}
					default: 
				}
				if (!hashTable.containsKey(key)) {
					hashTable.put(key, obj);
					vo.setAttributeValue(attrName, obj);
				} else {
					vo.setAttributeValue(attrName, hashTable.get(key));
				}
			}
			retLst.add(vo);
			nCount++;
		}
		if(rsChecker != null){
			//如果需要联查凭证，则根据选择的凭证相关查询条件过滤单据
			retLst = Arrays.asList(rsChecker.getReslut((CircularlyAccessibleValueObject[])retLst.toArray(new CircularlyAccessibleValueObject[0])));
		}
		final long end = System.currentTimeMillis();
		Logger.debug("遍历结果集ArapResultsetProcessor同时进行压缩耗时" + String.valueOf(end - start)+" 毫秒 , 列数:"+columnCount);
		return null!=count&&count>0&&retLst.size()>count ? retLst.subList(0, count-1) : retLst;
	}

	private Object setObj(Object obj) {
		if (obj.toString().equals("0E-8"))
			obj = Integer.valueOf(0);
		String temp = obj.toString();
		if (temp.indexOf(".") == -1)
			obj = Integer.valueOf(temp);
		else {
			String s = temp.substring(0, temp.indexOf("."));
			obj = Integer.valueOf(s);
		}
		return obj;
	}
}
