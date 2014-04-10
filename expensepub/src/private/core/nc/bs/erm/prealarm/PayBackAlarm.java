package nc.bs.erm.prealarm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import nc.bs.arap.bx.BXZbBO;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.IPreAlertPlugin;
import nc.bs.pub.pa.PreAlertContext;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.itf.fi.pub.Currency;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * ������Ԥ��
 * </p>
 * 
 * �޸ļ�¼�����������ش��޸�<br>
 * <li>�޸��ˣ�chendya �޸����ڣ�2011-11-03 �޸����ݣ������ع�</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-10-16 ����08:25:42
 */
@SuppressWarnings("deprecation")
public class PayBackAlarm implements IPreAlertPlugin {
	
	public static final String KEY_ISPERSONAL = "ispersonal";
	public static final String KEY_DEPT = "pk_dept";
	public static final String KEY_PERSON = "pk_psndoc";
	public static final String KEY_TRADETYPE= "pk_tradetype";
	public static final String KEY_DAYS = "ndays";
	public static final String KEY_CURRTYPE = "pk_currtype";
	
	@Override
	public PreAlertObject executeTask(PreAlertContext context) throws BusinessException {
		String pk_user = context.getPk_user();
		String pk_psndoc = ((IUserPubService) NCLocator.getInstance().lookup(
				IUserPubService.class.getName())).queryPsndocByUserid(pk_user);
		String pk_jkbxr = null;
		if (pk_psndoc != null) {
			pk_jkbxr = pk_psndoc;
		}

		// Ĭ��10��
		int defaultDays = 10;

		String paramDjlx = null;
		String paramJkbx = null;
		String paramDept = null;
		String paramPkCurrtype = null;
		String paramIsSelf = null;

		LinkedHashMap<String, Object> keyMap = context.getKeyMap();
		Set<String> keys = keyMap.keySet();
		
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String key = iter.next();
			if (key.equalsIgnoreCase(KEY_ISPERSONAL)) {
				if (key != null) {
					paramIsSelf = (String)keyMap.get(key);
				}
			} else if (key.equalsIgnoreCase(KEY_DEPT)) {
				if (key != null && key.toString() != "") {
					Object obj = keyMap.get(key);
					if (obj != null) {
						paramDept = obj.toString();
					}
				}
			} else if (key.equalsIgnoreCase(KEY_PERSON)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						paramJkbx = keyMap.get(key).toString();
					}
				}

			} else if (key.equalsIgnoreCase(KEY_TRADETYPE)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null)
						paramDjlx = getBilltypeName(obj.toString());
				}
			} else if (key
					.equalsIgnoreCase(KEY_DAYS)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						defaultDays = new Integer(obj.toString());
					}
				}
			} else if (key.equalsIgnoreCase(KEY_CURRTYPE)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						paramPkCurrtype = keyMap.get(key).toString();
					}
				}
			}

		}

		UFDate date = new UFDate();
		UFDate dateAfter = date.getDateAfter(defaultDays);
		String sql = " where zhrq<='" + dateAfter + "' and qzzt=0 and sxbz=1 ";
		if (paramDjlx != null && paramDjlx.trim().length() > 0) {
			sql += "and djlxbm='" + paramDjlx + "' ";
		}
		if (paramJkbx != null && paramJkbx.trim().length() > 0) {
			try {
				sql += " and " + SqlUtils.getInStr("jkbxr", new String[] { pk_jkbxr });
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
		if (!StringUtils.isEmpty(context.getPk_org())) {
			sql += " and pk_org = '" + context.getPk_org() + "' ";
		}
		sql += " and pk_group= '" + context.getGroupId() + "' ";
		if (paramDept != null && paramDept.trim().length() > 0) {
			sql += "and deptid = '" + paramDept + "' ";
		}
		if (paramPkCurrtype != null && paramPkCurrtype.trim().length() > 0) {
			sql += "and bzbm = '" + paramPkCurrtype + "' ";
		}
		if (paramIsSelf != null && ("Y".equals(paramIsSelf) || "true".equals(paramIsSelf))) {
			sql += "and ( operator='" + pk_user + "' ";
			if (pk_jkbxr != null && pk_jkbxr != "") {
				try {
					sql += " or " + SqlUtils.getInStr("jkbxr", new String[] { pk_jkbxr });
				} catch (SQLException e) {
					throw ExceptionHandler.handleException(e);
				}
			}
			sql += " ) ";
		}

		// ����������ѯ����
		List<JKBXHeaderVO> headerVOList = new BXZbBO().queryHeadersByWhereSql(sql, BXConstans.JK_DJDL);

		if (headerVOList == null || headerVOList.size() == 0) {
			return null;
		}

		// ������
		for (Iterator<JKBXHeaderVO> iterator = headerVOList.iterator(); iterator.hasNext();) {
			JKBXHeaderVO bxHeaderVO = iterator.next();
			dealCurrencyDigital(VOFactory.createVO(bxHeaderVO));
		}

		// ��ʽ�����ص���Ϣ
		int row = headerVOList.size();
		List<ErmPrealarmBaseVO> dataList = new ArrayList<ErmPrealarmBaseVO>();
		ErmPrealarmBaseVO ermPrealarmVO = null;
		for (int i = 0; i < row; i++) {
			JKBXHeaderVO headerVO = headerVOList.get(i);
			ermPrealarmVO = new ErmPrealarmBaseVO();
			// ���ݺ�
			ermPrealarmVO.setBillno(headerVO.getDjbh());
			// ԭ�ҽ����
			ermPrealarmVO.setMoney(headerVO.getYbje());
			ermPrealarmVO.setMoneybal(headerVO.getYbye());
			
			// ���ҽ����
			ermPrealarmVO.setLocalmoney(headerVO.getBbje());
			ermPrealarmVO.setLocalmoneybal(headerVO.getBbye());
			
			// ���ű��ҽ����
			ermPrealarmVO.setGroupmoney(headerVO.getGroupbbje());
			ermPrealarmVO.setGroupmoneybal(headerVO.getGroupbbye());
			
			// ȫ�ֱ��ҽ����
			ermPrealarmVO.setGlobalmoney(headerVO.getGlobalbbje());
			ermPrealarmVO.setGlobalmoneybal(headerVO.getGlobalbbye());
			
			// ժҪ
			ermPrealarmVO.setBrief(headerVO.getZy());
			
			// ����
			ermPrealarmVO.setCurrency(getCurrencyName(headerVO.getBzbm()));
			
			// ��������
			ermPrealarmVO.setBilldate(headerVO.getDjrq() != null ? headerVO.getDjrq().toStdString() : "");
			
			// ������
			ermPrealarmVO.setJkbxr(getPsnName(headerVO.getJkr()));
			
			// ��ٻ�������
			ermPrealarmVO.setZhrq(headerVO.getZhrq() != null ? headerVO.getZhrq().toStdString() : "");
			
			dataList.add(ermPrealarmVO);
		}

		ErmPrealarmDataSource dataSource = new ErmPrealarmDataSource(dataList, ErmPrealarmBaseVO.class);

		PreAlertObject preAlertObject = new PreAlertObject();
		preAlertObject.setReturnObj(dataSource);
		if (dataSource.getDatas() == null || dataSource.getDatas().size() == 0) {
			preAlertObject.setReturnType(PreAlertReturnType.RETURNNOTHING);
		} else {
			preAlertObject.setReturnType(PreAlertReturnType.RETURNDATASOURCE);
		}

		return preAlertObject;
	}

	/**
	 * �������ֶξ���billtypeVO
	 */
	private void dealCurrencyDigital(JKBXVO vo){
		CurrencyControlBO ctrl = new CurrencyControlBO();
		ctrl.dealBXVOdigit(vo);
	}

	private String getPsnName(String id) {
		try {
			PsndocVO person = (PsndocVO) (new BaseDAO().retrieveByPK(
					PsndocVO.class, id));
			if (person != null) {
				return person.getName();
			}
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
		}
		return "";
	}

	/**
	 * ���ر�������
	 * 
	 * @param bm
	 * @return
	 */
	private String getCurrencyName(String pk_currtype) {
		return (String) Currency.getCurrInfo(pk_currtype).getAttributeValue(
				CurrtypeVO.NAME + PubCommonReportMethod.getMultiLangIndex());
	}

	private String getBilltypeName(String pk_billtype) throws BusinessException {
		Collection<?> coll = new BaseDAO().retrieveByClause(BilltypeVO.class,
				" pk_billtypeid = '" + pk_billtype + "' ");
		if (coll == null || coll.size() == 0) {
			return null;
		}
		BilltypeVO billtypeVO = (BilltypeVO) coll.toArray()[0];
		return billtypeVO.getPk_billtypecode();
	}

}
