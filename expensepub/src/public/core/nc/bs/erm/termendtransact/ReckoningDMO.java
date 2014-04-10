package nc.bs.erm.termendtransact;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;
import nc.bs.er.data.DataManager;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.fi.pub.Currency;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.termendtransact.AgiotageBzVO;
import nc.vo.erm.termendtransact.AgiotageDJVO;
import nc.vo.erm.termendtransact.AgiotageVO;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.Je;
import nc.vo.erm.termendtransact.ReportVO;
import nc.vo.erm.termendtransact.RetBillVo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ReckoningDMO extends DataManager {
	/**
	 * ReckoningDMO 构造子注解。
	 * 
	 * @exception javax.naming.NamingException
	 *                异常说明。
	 * @exception nc.bs.pub.SystemException
	 *                异常说明。
	 */
	public ReckoningDMO() throws javax.naming.NamingException,
			nc.bs.pub.SystemException {
		super();
	}

	/**
	 * a功能: 作者：宋涛 创建时间：(2004-6-16 13:42:59) 使用说明：以及别人可能感兴趣的介绍 注意：现存Bug
	 * 
	 * 
	 * @return java.util.Vector
	 * @param param
	 *            java.sql.ResultSet
	 */
	public Vector<Vector<String>> onchange(ResultSet rs, java.util.Hashtable<String,String> hash_type)
			throws java.sql.SQLException {

		Vector<Vector<String>> vDJCLBs = new Vector<Vector<String>>();

		while (rs.next()) {
			Vector<String> v = new Vector<String>();
			// BillTypeNm :
			String sBillTypeNm = rs.getString(1);
			if (sBillTypeNm == null) {
				continue;
			}
			// BillNum :
			String sBillNum = rs.getString(2);
			v.addElement("");
			v.addElement(hash_type.get(sBillTypeNm) + sBillNum);
			v.addElement("");
			vDJCLBs.addElement(v);
		}
		return vDJCLBs;
	}

	/**
	 * a功能: 作者：宋涛 创建时间：(2004-6-16 13:42:59) 使用说明：以及别人可能感兴趣的介绍 注意：现存Bug
	 * 
	 * 
	 * @return java.util.Vector
	 * @param param
	 *            java.sql.ResultSet
	 */
	public Vector<Vector<String>> onchange(ResultSet rs, java.util.Hashtable<String,String> hash_type,
			Vector<Vector<String>> vetResult) throws java.sql.SQLException {

		while (rs.next()) {
			Vector<String> v = new Vector<String>();
			// BillTypeNm :
			String sBillTypeNm = rs.getString(1);
			if (sBillTypeNm == null) {
				continue;
			}
			// BillNum :
			String sBillNum = rs.getString(2);
			v.addElement("");
			v.addElement(hash_type.get(sBillTypeNm) + sBillNum);
			v.addElement("");
			vetResult.addElement(v);
		}
		return vetResult;
	}

	/**
	 * 主要功能：将币种单据转化成VO 主要算法： 异常描述： 创建日期：(2001-8-10 17:56:11) 最后修改日期：(2001-8-10
	 * 17:56:11)
	 * 
	 * @author：wyan
	 * @return java.util.Vector
	 * @param rs
	 *            java.sql.ResultSet
	 */
	public Vector<AgiotageDJVO> onChangeToVO(ResultSet rs) throws java.sql.SQLException {

		Vector<AgiotageDJVO> vResult = new Vector<AgiotageDJVO>();

		try {
			while (rs.next()) {

				AgiotageDJVO voDj = new AgiotageDJVO();

				// ybye :
				BigDecimal ybye = (BigDecimal) rs.getObject(1);
				voDj.setYbye(ybye == null ? null : new UFDouble(ybye));
			
				// bbye :
				BigDecimal bbye = (BigDecimal) rs.getObject(2);
				voDj.setBbye(bbye == null ? null : new UFDouble(bbye));
				
				// groupbb :
				BigDecimal groupbb = (BigDecimal) rs.getObject(3);
				voDj.setGroupbb(groupbb == null ? null : new UFDouble(groupbb));
				
				// globalbb :
				BigDecimal globalbb = (BigDecimal) rs.getObject(4);
				voDj.setGlobalbb(globalbb == null ? null : new UFDouble(globalbb));
				vResult.addElement(voDj);
			}
		} catch (java.sql.SQLException ex) {
			throw ex;
		}

		return vResult;

	}

	/**
	 * 检查单据是否为本系统所生成的单据。 创建日期：(2002-6-19 12:35:26)
	 * 
	 * @return int
	 * @param billnum
	 *            java.lang.String
	 */
//	public int onIsCurrSystemDoc(String billnum) throws Exception {
//
//		int iSysBz = -1;
//		String sql = "select pzglh from arap_djzb where djbh = '" + billnum
//				+ "' and dr = 0";
//
//		Connection con = null;
//		PreparedStatement stmt = null;
//		try {
//			con = getConnection();
//			stmt = con.prepareStatement(sql);
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				iSysBz = rs.getInt(1);
//			}
//		} finally {
//			try {
//				if (stmt != null) {
//					stmt.close();
//				}
//			} catch (Exception e) {
//			}
//			try {
//				if (con != null) {
//					con.close();
//				}
//			} catch (Exception e) {
//			}
//		}
//
//		return iSysBz;
//	}

//	/**
//	 * 检查单据是否为本系统所生成的单据。 创建日期：(2002-6-19 12:35:26)
//	 * 
//	 * @return int
//	 * @param billnum
//	 *            java.lang.String
//	 */
//	public int onIsCurrSystemDoc(String billnum, String dwbm, String djlx)
//			throws DAOException {
//
//		int iSysBz = -1;
//
//		String sql=" djbh = '" + billnum
//				+ "' and dwbm = '" + dwbm + "' and djlxbm= '" + djlx
//				+ "' and dr = 0";
//
//		try {
//
//			BaseDAO dao=new BaseDAO();
//			DJZBHeaderVO vo=(DJZBHeaderVO)dao.retrieveByClause(DJZBHeaderVO.class,sql);
//			iSysBz=vo.getPzglh().intValue();
//		} finally {
//
//		}
//
//		return iSysBz;
//	}

	/**
	 *校验是不是所有单据都生效
	 */
	public Vector<Vector<String>> onReckoningCheckStep1(FilterCondVO vo)
			throws Exception {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep1", new Object[] { vo });
		/** ********************************************************** */

		String sql = "";
		StringBuffer sb = new StringBuffer();
		String pk_org = vo.getPk_org();
		String date = vo.getEndDate();
		if (vo.getSfbz().equals("ys")) {
			
			sb.append("SELECT bill.billno as billno,type.billtypename as billtypename FROM ( ");
			sb.append("SELECT billno, pk_tradetype FROM ar_recbill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT billno, pk_tradetype FROM ap_cuspaybill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT billno, pk_tradetype FROM ar_gatherbill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT billno, pk_tradetype FROM ap_cuspayablebill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("') bill, bd_billtype type WHERE bill.pk_tradetype = type.pk_billtypecode ");
		}
		if (vo.getSfbz().equals("yf")) {
			sb.append("SELECT bill.billno as billno,type.billtypename as billtypename FROM ( ");
			sb.append("SELECT billno, pk_tradetype FROM ar_suprecbill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT billno, pk_tradetype FROM ap_paybill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT billno, pk_tradetype FROM ar_supgatherbill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT billno, pk_tradetype FROM ap_payablebill WHERE dr!=1 and effectstatus != 10 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("') bill, bd_billtype type WHERE bill.pk_tradetype = type.pk_billtypecode ");
		}
		
		sql = sb.toString();

		Vector<Vector<String>> v = new Vector<Vector<String>>();
		Connection con = null;
		PreparedStatement stmt = null;
		try {

			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			Vector<String> vTem = new Vector<String>();
			vTem.addElement("1");
			vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("200604", "UPP200604-000048")/*
															  * @res "本月未生效单据"
															  */);
			
			v.addElement(vTem);
			while (rs.next()) {
				Vector<String> vReport = new Vector<String>();
				String bh = "";
				vReport.addElement(bh);
				String djlx = rs.getString("billtypename") + rs.getString("billno");
				vReport.addElement(djlx == null ? null : djlx.trim());
				String count = "";
				vReport.addElement(count);
				v.addElement(vReport);
			}
			vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("200604", "UPP200604-000049"/*
															  * @res "共0张"
															  */,null, new String[] {(v.size()-1)+""}));
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep1", new Object[] { vo });
		/** ********************************************************** */

		int iCount = v.size() - 1;
		if (iCount != 0) {
			String sCount = new Integer(iCount).toString();
			//((Vector)v.elementAt(0)).setElementAt(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000045")/*@res
			// "共"*/+sCount+nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000046")/*@res
			// "张"*/,2);
			v.elementAt(0).setElementAt(nc.bs.ml.NCLangResOnserver
					.getInstance().getStrByID("200604", "UPP200604-000045",
							null, new String[] { sCount.toString() }), 2)/*
																		  * @res
																		  * "共{0}张"
																		  */;
		}
		return v;
	}

	/**
	 * 主要功能：本月收款单是否全部核销 主要算法： 异常描述： 创建日期：(2001-9-20 19:06:53) 最后修改日期：(2001-9-20
	 * 19:06:53)
	 * 
	 * @author：wyan
	 * @return nc.vo.pub.ValueObject
	 * @param vo
	 *            nc.vo.arap.termendtransact.FilterCondVO
	 */
	public Vector<Vector<String>> onReckoningCheckStep2(FilterCondVO vo)
			throws Exception {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep2", new Object[] { vo });
		/** ********************************************************** */
		
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		String pk_org = vo.getPk_org();
		String date = vo.getEndDate();
		if (vo.getSfbz().equals("ys")) {
			
			sb.append("SELECT bill.billno as billno,type.billtypename as billtypename FROM ( ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ar_recitem WHERE dr!=1 and  money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ap_cuspayitem WHERE  dr!=1 and money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ar_gatheritem WHERE dr!=1 and money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ap_cuspayableitem WHERE dr!=1 and  money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("') bill, bd_billtype type WHERE bill.pk_tradetype = type.pk_billtypecode ");
		}
		if (vo.getSfbz().equals("yf")) {
			sb.append("SELECT bill.billno as billno,type.billtypename as billtypename FROM ( ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ar_suprecitem WHERE  dr!=1 and money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ap_payitem WHERE  dr!=1 and money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ar_supgatheritem WHERE  dr!=1 and money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("' UNION ");
			sb.append("SELECT distinct billno, pk_tradetype FROM ap_payableitem WHERE  dr!=1 and money_bal != 0 and pk_org = '");
			sb.append(pk_org);
			sb.append("'and billdate  < '");
			sb.append(date);
			sb.append("') bill, bd_billtype type WHERE bill.pk_tradetype = type.pk_billtypecode ");
		}
		
		sql = sb.toString();

		Vector<Vector<String>> v = new Vector<Vector<String>>();
		Connection con = null;
		PreparedStatement stmt = null;
		try {

			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Vector<String> vReport = new Vector<String>();
				String bh = "";
				vReport.addElement(bh);
				String djlx = rs.getString("billtypename") + rs.getString("billno");
				vReport.addElement(djlx == null ? null : djlx.trim());
				String count = "";
				vReport.addElement(count);
				v.addElement(vReport);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep2", new Object[] { vo });
		/** ********************************************************** */

		return v;
	}

	/**
	 * 主要功能：本月单据是否计算汇兑损益 主要算法： 异常描述： 创建日期：(2001-9-20 19:06:53) 最后修改日期：(2001-9-20
	 * 19:06:53)
	 * 
	 * @author：wyan
	 * @return nc.vo.pub.ValueObject
	 * @param vo
	 *            nc.vo.arap.termendtransact.FilterCondVO
	 */
	public ReportVO onReckoningCheckStep3(FilterCondVO vo,
			AgiotageVO voCurrency) throws Exception {

		/** ********************************************************** */
		// 保留的系统管理接口：
		beforeCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep5", new Object[] { vo, voCurrency });
		/** ********************************************************** */

		Vector vAllBz = new Vector();
		Vector vDj = new Vector();
		UFDouble zero = new UFDouble(0);
		String sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
				"200604", "UPP200604-000050")/*
											  * @res "截止到本月单据全部计算过汇兑损益"
											  */;
		String condFirst = null;
		vAllBz = voCurrency.getSelBzbm(); /* 符合汇兑损益检查条件的单据包含的所有币种 */

		boolean isPassed = true;/* 是否通过检查 */

		if (vAllBz.size() == 0) {
			sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
					"200604", "UPP200604-000051")/*
												  * @res "截止到本月没有需要计算汇兑损益的单据"
												  */;
			isPassed = true;
		}
		
		




		for (int i = 0; i < vAllBz.size(); i++) {

			/* 如果有一项不合格就中止检查 */
			if (!isPassed)
				break;


			/** ***********取得币种集合*********** */
			/* 如果在检查的任何一个步骤检查不合格都要中止检查没有必要继续 */
			AgiotageBzVO voBz = (AgiotageBzVO) vAllBz.elementAt(i);
			String bzbm = voBz.getBzbm();
			

			String sql = "";
			StringBuffer sb = new StringBuffer();
			String pk_org = vo.getPk_org();
			String date = vo.getEndDate();
			if (vo.getSfbz().equals("ys")) {
				sb.append("SELECT item.money_bal as ybye, item.local_money_bal as bbye,bill.grouplocal as groupbb, bill.globallocal as globalbb FROM arap_termitem item ,( ");
				sb.append("SELECT pk_recbill as pk_bill, grouplocal , globallocal FROM ar_recbill WHERE dr! = 1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("' UNION ");
				sb.append("SELECT pk_cuspaybill as pk_bill, grouplocal , globallocal FROM ap_cuspaybill WHERE  dr!=1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("' UNION ");
				sb.append("SELECT pk_gatherbill as pk_bill, grouplocal , globallocal FROM ar_gatherbill WHERE dr!=1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("' UNION ");
				sb.append("SELECT pk_cuspayablebill as pk_bill, grouplocal , globallocal FROM ap_cuspayablebill WHERE dr!=1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("') bill WHERE bill.pk_bill = item.pk_bill");
			}
			if (vo.getSfbz().equals("yf")) {
				sb.append("SELECT item.money_bal as ybye, item.local_money_bal as bbye,bill.grouplocal as groupbb, bill.globallocal as globalbb FROM arap_termitem item ,( ");
				sb.append("SELECT pk_suprecbill as pk_bill, grouplocal , globallocal FROM ar_suprecbill WHERE dr! = 1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("' UNION ");
				sb.append("SELECT pk_paybill as pk_bill, grouplocal , globallocal FROM ap_paybill WHERE  dr!=1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("' UNION ");
				sb.append("SELECT pk_supgatherbill as pk_bill, grouplocal , globallocal FROM ar_supgatherbill WHERE dr!=1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("' UNION ");
				sb.append("SELECT pk_payablebill as pk_bill, grouplocal , globallocal FROM ap_payablebill WHERE dr!=1 and effectstatus = 10 and pk_org = '");
				sb.append(pk_org);
				sb.append("'and billdate  < '");
				sb.append(date);
				sb.append("') bill WHERE bill.pk_bill = item.pk_bill");
			}
			sql = sb.toString();

			Connection con = null;
			PreparedStatement stmt = null;
			try {
				con = getConnection();
				stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				vDj = onChangeToVO(rs); /* 将结果集转化为DJCLBVO */

			} finally {
				try {
					if (stmt != null) {
						stmt.close();
					}
				} catch (Exception e) {
				}
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
				}
			}
			
			if(vDj==null || vDj.size()==0)
				continue;
			
			boolean isError = voBz.getState(); /* 汇率检查是否汇率非法 */
			/* 此处要检查汇率是否正确(包括主辅币核算是否有辅币汇率，汇率是否合法等) */
			if (!isError) {
				sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
						"200604", "UPP200604-000052")/*
													  * @res
													  * "截止到本月单据没有全部计算过汇兑损益"
													  */;
				isPassed = false;
				break;
			}
			/* 只要有一个币种汇率非法就提示用户没有计算汇兑损益 */

			for (int j = 0; j < vDj.size(); j++) {
				AgiotageDJVO vDjcl = (AgiotageDJVO) vDj.elementAt(j);
				UFDouble yb = vDjcl.getYbye();
				UFDouble bb = vDjcl.getBbye();
				UFDouble groupbb = vDjcl.getGroupbb();
				UFDouble globalbb = vDjcl.getGlobalbb();
				Je je = new Je(bzbm,yb,bb,groupbb,globalbb);
				UFDouble bbye = null;

				bbye=Currency.getAmountByOpp(voCurrency.getDwbm(),voBz.getBzbm(),Currency.getLocalCurrPK(voCurrency.getDwbm()),yb,voBz.getBbhl(),null);
		
				Je ye = new Je(bzbm,yb,bbye,groupbb,globalbb);
				Je ce = je.subtract(ye);
				if (!ce.isAllZero()) {
					sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
							"200604", "UPP200604-000052")/*
														  * @res
														  * "截止到本月单据没有全部计算过汇兑损益"
														  */;
					isPassed = false;
					break;
					/* 只要有一个币种的单据存在差额，就提示用户没有计算汇兑损益 */
				}
			}
		}
		ReportVO repVO = new ReportVO();
		repVO.setInfo(sMsg);
		repVO.setState(isPassed);

		/** ********************************************************** */
		// 保留的系统管理接口：
		afterCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep5", new Object[] { vo, voCurrency });
		/** ********************************************************** */

		return repVO;
	}

	/**
	 * a功能: 作者：宋涛 创建时间：(2004-6-16 13:57:08) 使用说明：以及别人可能感兴趣的介绍 注意：现存Bug
	 * 
	 * 
	 * @return java.util.Vector
	 * @param spk_corp
	 *            java.lang.String
	 * @throws BusinessException 
	 */
	public Vector<Vector<String>> queryBillType(String spk_corp) throws BusinessException  {
		Vector<Vector<String>> vTem = new Vector<Vector<String>>();
		try {
			//FIXME 注意下面实现方式
//			DjLXVO[] vos = Proxy.getIBillTypePublic().queryAllBillTypeByGroup(spk_corp);
			DjLXVO[] vos = NCLocator.getInstance().lookup(IArapBillTypePublic.class).queryAllBillTypeByCorp("@@@@");
			for(int i=0;i<vos.length;i++)
			{
				Vector<String> v = new Vector<String>();
				v.addElement(vos[i].getDjlxbm());
				v.addElement(vos[i].getDjlxjc_remark());
				vTem.addElement(v);
			}

		} catch (ComponentException e) {
			// TODO 自动生成 catch 块
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(this.getClass(), e);
		} 
		return vTem;
	}

	/**
	 * a功能: 作者：宋涛 创建时间：(2004-6-16 13:19:06) 使用说明：以及别人可能感兴趣的介绍 注意：现存Bug
	 * 
	 * 
	 * @return java.util.Vector
	 * @param param
	 *            java.lang.String
	 */
	public Vector<Vector<String>> queryNoVouchBills(String sql, Hashtable<String,String> hash_type,
			Vector<Vector<String>> vetResult) throws Exception {

		Connection con = null;
		PreparedStatement stmt = null;
		try {

			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			vetResult = onchange(rs, hash_type, vetResult);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return vetResult;
	}

public String createHeaderTempTable(Hashtable hash_bill)throws Exception{
	String tablename =null;
	PreparedStatement stmt=null;
	Connection con=null;
	try{
		java.util.Enumeration em = hash_bill.elements();
		con = getConnection();
	    nc.bs.mw.sqltrans.TempTable tmptab = new nc.bs.mw.sqltrans.TempTable();
	    tablename = tmptab.createTempTable(con,
	            "arap_termend_1",
	            "djlxbm varchar(20),djbh varchar(30),ts char(19)",
	            "djlxbm,djbh");
	
	    String sql_temp = " insert into  " + tablename + " (djlxbm,djbh) values(?,?)";
	
	    stmt = prepareStatement(con, sql_temp);
	    while (em.hasMoreElements()) {
			RetBillVo voBill = (RetBillVo) em.nextElement();
			stmt.setString(1, voBill.getBillType());
			stmt.setString(2, voBill.getBillNo());
			stmt.addBatch();
		}
	    stmt.executeBatch();
	}finally{
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
		}
	}
	return tablename;
}
}