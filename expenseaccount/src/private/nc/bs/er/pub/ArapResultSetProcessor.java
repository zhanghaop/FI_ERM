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
 * �����Ҫ˵��������Ƶ�Ŀ�꣬���ʲô���Ĺ��ܡ�
 * </p>
 * <p>
 * <Strong>��Ҫ����ʹ�ã�</Strong>
 * <ul>
 * <li>���ʹ�ø���</li>
 * <li>�Ƿ��̰߳�ȫ</li>
 * <li>������Ҫ��</li>
 * <li>ʹ��Լ��</li>
 * <li>����</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * <ul>
 * <li>
 * <ul>
 * <li><strong>�޸���:</strong>rocking</li>
 * <li><strong>�޸����ڣ�</strong>2005-12-29</li>
 * <li><strong>�޸����ݣ�<strong></li>
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
	 * ҳ��
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
	 * ���ز�ѯ�Ŀ�ʼλ��
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
	 * ������������
	 * @author chendya
	 */
	public Object handleResultSet(ResultSet rs) throws SQLException {
		
		//���ص����ݼ�
		List<CircularlyAccessibleValueObject> retLst = new ArrayList<CircularlyAccessibleValueObject>();
		
		//��ѭ���Ĵ���
		int nCount = 0;
		
		// ��ǰλ��
		int currPos = 0;
		
		// ��ѯ���������ʼλ��
		final int startPos = getStartPos();
		
		//������
		final int columnCount = rs.getMetaData().getColumnCount();
		
		//���ݿ�����
		String[] colNames = new String[columnCount + 1];
		//���ݿ�������Ӧ��Ԫ�����ֶ���
		String[] attrNames = new String[columnCount + 1];
		//�������ֶ�����
		int[] colTypes=	new int[columnCount + 1];
		for (int k = 1; k <= columnCount; k++) {
			//���ݿ�����
			colNames[k] = rs.getMetaData().getColumnName(k);
			
			//�������ݿ���������vo���ֶ���
			attrNames[k] = meta.getAttrNameByColName(colNames[k]);
			
			//����vo�ֶ��������ֶ�����
			colTypes[k] = meta.getDataTypeByAttrName(attrNames[k]);
		}
		
		// ����ѹ������  
		final long start = System.currentTimeMillis();
		Map<Object,Object> hashTable = Collections.synchronizedMap(new HashMap<Object,Object>());
		//��¼��Ҫ���й��˴����VO
		while (rs.next()
				&& (count == null || count.intValue() < 0 || nCount < count.intValue())) {
			if (currPos + 1 <= startPos) {
				//δ�ﵽ��ѯ����ʼλ�ã�����ѭ��
				currPos++;
				continue;
			}
			//ʵ����VO
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
					//�ַ��������ȴ������Ч�� added by chendya
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
								//kongxl�����ʱ����ѯ�����ڲ���ȷ       NCdp203487637
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
			//�����Ҫ����ƾ֤�������ѡ���ƾ֤��ز�ѯ�������˵���
			retLst = Arrays.asList(rsChecker.getReslut((CircularlyAccessibleValueObject[])retLst.toArray(new CircularlyAccessibleValueObject[0])));
		}
		final long end = System.currentTimeMillis();
		Logger.debug("���������ArapResultsetProcessorͬʱ����ѹ����ʱ" + String.valueOf(end - start)+" ���� , ����:"+columnCount);
		return null!=count&&count>0&&retLst.size()>count ? retLst.subList(0, count-1) : retLst;
	}

	private Object setObj(Object obj) {
		if (obj.toString().equals("0E-8"))
			obj = new Integer(0);
		String temp = obj.toString();
		if (temp.indexOf(".") == -1)
			obj = new Integer(temp);
		else {
			String s = temp.substring(0, temp.indexOf("."));
			obj = new Integer(s);
		}
		return obj;
	}
}
