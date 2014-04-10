package nc.vo.erm.termendtransact;

/**
 * 获得结账信息。
 * 创建日期：(2001-11-16 15:29:56)
 * @author：wyan
 */
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.lang.UFDate;

public class AccountInfo {
	public static FirstNotClosedAccountMonthVO m_voFirstNotClosedAccountMonth;/*第一个未结账会计月*/
/**
 * AccountInfo 构造子注解。
 */
public AccountInfo() {
	super();
}
/**
 * 取得取消结账的上一个期间。
 * 创建日期：(2001-11-16 15:36:58)
 * @return nc.pub.arap.transaction.NotClosedAccountMonthVO
 * @param vo nc.pub.arap.transaction.SystemInfoVO
 */
public static FirstNotClosedAccountMonthVO getFirstDisAccountMonth(SystemInfoVO vo) {

	return getFirstDisAccountMonth(null, vo);
}
/**
 * 取得第一个未结账期间。
 * 创建日期：(2001-11-16 15:36:58)
 * @return nc.pub.arap.transaction.NotClosedAccountMonthVO
 * @param vo nc.pub.arap.transaction.SystemInfoVO
 */
public static FirstNotClosedAccountMonthVO getFirstNotClosedAccountMonth(SystemInfoVO vo) {

	return getFirstNotClosedAccountMonth(null,null,vo);
}
/**
 * 确定指定日期所在期间是否结账。
 * 创建日期：(2002-1-2 10:47:22)
 * @return boolean
 * @param pkCorp java.lang.String  公司pk
 * @param prodID java.lang.String  系统编码
 * @param date nc.vo.pub.lang.UFDate  指定日期
 */
public static boolean getPeriodIsAcc(
	String pkCorp, 
	String prodID, 
	UFDate date) {
	//暂时返回true
	try
	{
//		//判断启用日期
//		String[] accountInfo = Proxy.getICreateCorpQueryService().queryEnabledPeriod(pkCorp, prodID); 
//		return date.after(new UFDate(accountInfo[0]));
//		
		//FIXME,结账处理，关账处理
	}
	catch(Exception e)
	{
		ExceptionHandler.consume(e);
	}
//	return false;
	return true;
	
}
public static FirstNotClosedAccountMonthVO getFirstNotClosedAccountMonth(String[] enableInfo, String[] accountInfo, SystemInfoVO vo) {
	FirstNotClosedAccountMonthVO firstNoAccVO = new FirstNotClosedAccountMonthVO();
    String notClosedMonth = null;
    String notClosedYear = null;
    boolean bIsAccounted = false; /*是否找到未结账月*/
    String pkCorp = vo.getCurDwbm(); /*单位pk*/
    String prodID = vo.getProdID(); /*模块id(AR,AP,EP)*/
    
    try {
    	if(accountInfo==null){
	        accountInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
    	}
        /*系统结账信息*/
        String accYear = accountInfo[0]; /*当前结账年*/
        String accMonth = accountInfo[1]; /*当前结账月*/

        if(enableInfo==null){
	        enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
        }
        
        String enabledYear = enableInfo[0]; /*启用年*/
        String enabledMonth = enableInfo[1]; /*启用月*/

        /*如果没有结过账，系统起始月为第一个未结账月*/
        if ((accMonth == null) || (accMonth.trim().length() == 0)) {
            notClosedMonth = enabledMonth;
            notClosedYear = enabledYear;
            bIsAccounted = true;

        } else {
        	AccountCalendar ac = AccountCalendar.getInstance();
            /*系统结账年度全部会计期间*/
        	ac.set(accYear);
//            AccperiodVO periodVO = ac.getYearVO();//CallPeriod.findAccperiodByYear(accYear);
            /*结账年度的最后一个会计期间*/
            nc.vo.bd.period2.AccperiodmonthVO[] vo_moths = ac.getMonthVOsOfCurrentYear();//periodVO.getVosMonth();
            
            /*Update by Top 2004-2-19 for */
            int sumMonth =
                vo_moths == null ? 0 : Integer.parseInt(vo_moths[vo_moths.length - 1].getAccperiodmth());
            /*Update end */
            
            /*如果当前结账期间等于最后一个期间，取下一年度的第一个期间*/
            if (new Integer(accMonth).intValue() >= sumMonth) {
                notClosedYear = new Integer(Integer.valueOf(accYear).intValue() + 1).toString();
                ac.set(accYear);
//                periodVO = ac.getYearVO();//CallPeriod.findAccperiodByYear(notClosedYear); /*系统全部会计期间*/
                notClosedMonth = ac.getMonthVO().getAccperiodmth();//periodVO.getVosMonth()[0].getMonth();
            } else {
                notClosedYear = accYear;
                Integer iMon = new Integer(Integer.valueOf(accMonth).intValue() + 1);
                if (iMon.intValue() < 10)
                    notClosedMonth = "0" + iMon.toString();
                else
                    notClosedMonth = iMon.toString();
            }
        }

        firstNoAccVO.setNotAccMonth(notClosedMonth);
        firstNoAccVO.setNotAccYear(notClosedYear);
        firstNoAccVO.setIsAccounted(bIsAccounted);

    } catch (Exception ex) {
    	ExceptionHandler.consume(ex);
    }

    return firstNoAccVO;
}
public static FirstNotClosedAccountMonthVO getFirstDisAccountMonth(String[] enableInfo, SystemInfoVO vo) {
	FirstNotClosedAccountMonthVO firstNoAccVO = new FirstNotClosedAccountMonthVO();
	String accYear = null;
	String accMonth = null;

	try {
		String year = vo.getCurNd(); /*选中的要取消结账的月所在地年度*/
		String cope = vo.getCurQj(); /*选中的要取消结账的月所在地期间*/
		String pkCorp = vo.getCurDwbm();
		String prodID = vo.getProdID();

		if(enableInfo==null){
			enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
		}
		
		String enabledYear = enableInfo[0]; /*启用年*/
		String enabledMonth = enableInfo[1]; /*启用月*/

		if (year.equals(enabledYear) && (cope.equals(enabledMonth))) {
			accYear = null;
			accMonth = null;
			firstNoAccVO.setNotAccMonth(accMonth);
			firstNoAccVO.setNotAccYear(accYear);
			return firstNoAccVO;
		}
		if (cope.equals("01")) {
			accYear = new Integer(Integer.valueOf(year).intValue() - 1).toString();
			accMonth = "12";
		}
		if (!cope.equals("01")) {
			accYear = year;
			Integer iMon = new Integer(Integer.valueOf(cope).intValue() - 1);
			if (iMon.intValue() < 10)
				accMonth = "0" + iMon.toString();
			else
				accMonth = iMon.toString();

		}
		firstNoAccVO.setNotAccMonth(accMonth);
		firstNoAccVO.setNotAccYear(accYear);
	} catch (Exception ex) {
		ExceptionHandler.consume(ex);
	}

	return firstNoAccVO;
}
}
