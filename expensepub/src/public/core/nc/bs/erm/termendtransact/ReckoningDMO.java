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
	 * ReckoningDMO ������ע�⡣
	 * 
	 * @exception javax.naming.NamingException
	 *                �쳣˵����
	 * @exception nc.bs.pub.SystemException
	 *                �쳣˵����
	 */
	public ReckoningDMO() throws javax.naming.NamingException,
			nc.bs.pub.SystemException {
		super();
	}

	/**
	 * a����: ���ߣ����� ����ʱ�䣺(2004-6-16 13:42:59) ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ��� ע�⣺�ִ�Bug
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
	 * a����: ���ߣ����� ����ʱ�䣺(2004-6-16 13:42:59) ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ��� ע�⣺�ִ�Bug
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
	 * ��Ҫ���ܣ������ֵ���ת����VO ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-8-10 17:56:11) ����޸����ڣ�(2001-8-10
	 * 17:56:11)
	 * 
	 * @author��wyan
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
	 * ��鵥���Ƿ�Ϊ��ϵͳ�����ɵĵ��ݡ� �������ڣ�(2002-6-19 12:35:26)
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
//	 * ��鵥���Ƿ�Ϊ��ϵͳ�����ɵĵ��ݡ� �������ڣ�(2002-6-19 12:35:26)
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
	 *У���ǲ������е��ݶ���Ч
	 */
	public Vector<Vector<String>> onReckoningCheckStep1(FilterCondVO vo)
			throws Exception {

		/** ********************************************************** */
		// ������ϵͳ����ӿڣ�
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
															  * @res "����δ��Ч����"
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
															  * @res "��0��"
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
		// ������ϵͳ����ӿڣ�
		afterCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep1", new Object[] { vo });
		/** ********************************************************** */

		int iCount = v.size() - 1;
		if (iCount != 0) {
			String sCount = new Integer(iCount).toString();
			//((Vector)v.elementAt(0)).setElementAt(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000045")/*@res
			// "��"*/+sCount+nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000046")/*@res
			// "��"*/,2);
			v.elementAt(0).setElementAt(nc.bs.ml.NCLangResOnserver
					.getInstance().getStrByID("200604", "UPP200604-000045",
							null, new String[] { sCount.toString() }), 2)/*
																		  * @res
																		  * "��{0}��"
																		  */;
		}
		return v;
	}

	/**
	 * ��Ҫ���ܣ������տ�Ƿ�ȫ������ ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20 19:06:53) ����޸����ڣ�(2001-9-20
	 * 19:06:53)
	 * 
	 * @author��wyan
	 * @return nc.vo.pub.ValueObject
	 * @param vo
	 *            nc.vo.arap.termendtransact.FilterCondVO
	 */
	public Vector<Vector<String>> onReckoningCheckStep2(FilterCondVO vo)
			throws Exception {

		/** ********************************************************** */
		// ������ϵͳ����ӿڣ�
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
		// ������ϵͳ����ӿڣ�
		afterCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep2", new Object[] { vo });
		/** ********************************************************** */

		return v;
	}

	/**
	 * ��Ҫ���ܣ����µ����Ƿ���������� ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20 19:06:53) ����޸����ڣ�(2001-9-20
	 * 19:06:53)
	 * 
	 * @author��wyan
	 * @return nc.vo.pub.ValueObject
	 * @param vo
	 *            nc.vo.arap.termendtransact.FilterCondVO
	 */
	public ReportVO onReckoningCheckStep3(FilterCondVO vo,
			AgiotageVO voCurrency) throws Exception {

		/** ********************************************************** */
		// ������ϵͳ����ӿڣ�
		beforeCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep5", new Object[] { vo, voCurrency });
		/** ********************************************************** */

		Vector vAllBz = new Vector();
		Vector vDj = new Vector();
		UFDouble zero = new UFDouble(0);
		String sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
				"200604", "UPP200604-000050")/*
											  * @res "��ֹ�����µ���ȫ��������������"
											  */;
		String condFirst = null;
		vAllBz = voCurrency.getSelBzbm(); /* ���ϻ�������������ĵ��ݰ��������б��� */

		boolean isPassed = true;/* �Ƿ�ͨ����� */

		if (vAllBz.size() == 0) {
			sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
					"200604", "UPP200604-000051")/*
												  * @res "��ֹ������û����Ҫ����������ĵ���"
												  */;
			isPassed = true;
		}
		
		




		for (int i = 0; i < vAllBz.size(); i++) {

			/* �����һ��ϸ����ֹ��� */
			if (!isPassed)
				break;


			/** ***********ȡ�ñ��ּ���*********** */
			/* ����ڼ����κ�һ�������鲻�ϸ�Ҫ��ֹ���û�б�Ҫ���� */
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
				vDj = onChangeToVO(rs); /* �������ת��ΪDJCLBVO */

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
			
			boolean isError = voBz.getState(); /* ���ʼ���Ƿ���ʷǷ� */
			/* �˴�Ҫ�������Ƿ���ȷ(���������Һ����Ƿ��и��һ��ʣ������Ƿ�Ϸ���) */
			if (!isError) {
				sMsg = nc.bs.ml.NCLangResOnserver.getInstance().getStrByID(
						"200604", "UPP200604-000052")/*
													  * @res
													  * "��ֹ�����µ���û��ȫ��������������"
													  */;
				isPassed = false;
				break;
			}
			/* ֻҪ��һ�����ֻ��ʷǷ�����ʾ�û�û�м��������� */

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
														  * "��ֹ�����µ���û��ȫ��������������"
														  */;
					isPassed = false;
					break;
					/* ֻҪ��һ�����ֵĵ��ݴ��ڲ�����ʾ�û�û�м��������� */
				}
			}
		}
		ReportVO repVO = new ReportVO();
		repVO.setInfo(sMsg);
		repVO.setState(isPassed);

		/** ********************************************************** */
		// ������ϵͳ����ӿڣ�
		afterCallMethod("nc.bs.arap.termendtransact.ReckoningDMO",
				"onReckoningCheckStep5", new Object[] { vo, voCurrency });
		/** ********************************************************** */

		return repVO;
	}

	/**
	 * a����: ���ߣ����� ����ʱ�䣺(2004-6-16 13:57:08) ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ��� ע�⣺�ִ�Bug
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
			//FIXME ע������ʵ�ַ�ʽ
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
			// TODO �Զ����� catch ��
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(this.getClass(), e);
		} 
		return vTem;
	}

	/**
	 * a����: ���ߣ����� ����ʱ�䣺(2004-6-16 13:19:06) ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ��� ע�⣺�ִ�Bug
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