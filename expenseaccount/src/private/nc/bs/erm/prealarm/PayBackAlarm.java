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
import nc.bs.framework.common.InvocationInfoProxy;
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

/**
 * <p>
 * 借款单还款预警
 * </p>
 * 
 * 修改记录：此类已作重大修改<br>
 * <li>修改人：chendya 修改日期：2011-11-03 修改内容：几乎重构</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-10-16 下午08:25:42
 */
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

		// 默认10天
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
		String[] pk_orgs = context.getPk_orgs();
		if (pk_orgs != null && pk_orgs.length > 0) {
			try {
				sql += " and " + SqlUtils.getInStr("pk_org", pk_orgs);
			} catch (SQLException e) {
				throw ExceptionHandler.handleException(e);
			}
		}
		sql += " and pk_group= '" + InvocationInfoProxy.getInstance().getGroupId() + "' ";
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

		// 根据条件查询出借款单
		List<JKBXHeaderVO> headerVOList = new BXZbBO().queryHeadersByWhereSql(sql, BXConstans.JK_DJDL);

		if (headerVOList == null || headerVOList.size() == 0) {
			return null;
		}

		// 处理精度
		for (Iterator<JKBXHeaderVO> iterator = headerVOList.iterator(); iterator.hasNext();) {
			JKBXHeaderVO bxHeaderVO = iterator.next();
			dealCurrencyDigital(VOFactory.createVO(bxHeaderVO));
		}

		// 格式化返回的消息
		int row = headerVOList.size();
		List<ErmPrealarmBaseVO> dataList = new ArrayList<ErmPrealarmBaseVO>();
		ErmPrealarmBaseVO ermPrealarmVO = null;
		for (int i = 0; i < row; i++) {
			JKBXHeaderVO headerVO = headerVOList.get(i);
			ermPrealarmVO = new ErmPrealarmBaseVO();
			// 单据号
			ermPrealarmVO.setBillno(headerVO.getDjbh());
			// 原币金、余额
			ermPrealarmVO.setMoney(headerVO.getYbje());
			ermPrealarmVO.setMoneybal(headerVO.getYbye());
			
			// 本币金、余额
			ermPrealarmVO.setLocalmoney(headerVO.getBbje());
			ermPrealarmVO.setLocalmoneybal(headerVO.getBbye());
			
			// 集团本币金、余额
			ermPrealarmVO.setGroupmoney(headerVO.getGroupbbje());
			ermPrealarmVO.setGroupmoneybal(headerVO.getGroupbbye());
			
			// 全局本币金、余额
			ermPrealarmVO.setGlobalmoney(headerVO.getGlobalbbje());
			ermPrealarmVO.setGlobalmoneybal(headerVO.getGlobalbbye());
			
			// 摘要
			ermPrealarmVO.setBrief(headerVO.getZy());
			
			// 币种
			ermPrealarmVO.setCurrency(getCurrencyName(headerVO.getBzbm()));
			
			// 单据日期
			ermPrealarmVO.setBilldate(headerVO.getDjrq() != null ? headerVO.getDjrq().toStdString() : "");
			
			// 借款报销人
			ermPrealarmVO.setJkbxr(getPsnName(headerVO.getJkr()));
			
			// 最迟还款日期
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
	 * 处理金额字段精度billtypeVO
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
	 * 返回币种名称
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
