package nc.vo.erm.termendtransact;

/**
 * ��ý�����Ϣ��
 * �������ڣ�(2001-11-16 15:29:56)
 * @author��wyan
 */
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.lang.UFDate;

public class AccountInfo {
	public static FirstNotClosedAccountMonthVO m_voFirstNotClosedAccountMonth;/*��һ��δ���˻����*/
/**
 * AccountInfo ������ע�⡣
 */
public AccountInfo() {
	super();
}
/**
 * ȡ��ȡ�����˵���һ���ڼ䡣
 * �������ڣ�(2001-11-16 15:36:58)
 * @return nc.pub.arap.transaction.NotClosedAccountMonthVO
 * @param vo nc.pub.arap.transaction.SystemInfoVO
 */
public static FirstNotClosedAccountMonthVO getFirstDisAccountMonth(SystemInfoVO vo) {

	return getFirstDisAccountMonth(null, vo);
}
/**
 * ȡ�õ�һ��δ�����ڼ䡣
 * �������ڣ�(2001-11-16 15:36:58)
 * @return nc.pub.arap.transaction.NotClosedAccountMonthVO
 * @param vo nc.pub.arap.transaction.SystemInfoVO
 */
public static FirstNotClosedAccountMonthVO getFirstNotClosedAccountMonth(SystemInfoVO vo) {

	return getFirstNotClosedAccountMonth(null,null,vo);
}
/**
 * ȷ��ָ�����������ڼ��Ƿ���ˡ�
 * �������ڣ�(2002-1-2 10:47:22)
 * @return boolean
 * @param pkCorp java.lang.String  ��˾pk
 * @param prodID java.lang.String  ϵͳ����
 * @param date nc.vo.pub.lang.UFDate  ָ������
 */
public static boolean getPeriodIsAcc(
	String pkCorp, 
	String prodID, 
	UFDate date) {
	//��ʱ����true
	try
	{
//		//�ж���������
//		String[] accountInfo = Proxy.getICreateCorpQueryService().queryEnabledPeriod(pkCorp, prodID); 
//		return date.after(new UFDate(accountInfo[0]));
//		
		//FIXME,���˴������˴���
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
    boolean bIsAccounted = false; /*�Ƿ��ҵ�δ������*/
    String pkCorp = vo.getCurDwbm(); /*��λpk*/
    String prodID = vo.getProdID(); /*ģ��id(AR,AP,EP)*/
    
    try {
    	if(accountInfo==null){
	        accountInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
    	}
        /*ϵͳ������Ϣ*/
        String accYear = accountInfo[0]; /*��ǰ������*/
        String accMonth = accountInfo[1]; /*��ǰ������*/

        if(enableInfo==null){
	        enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
        }
        
        String enabledYear = enableInfo[0]; /*������*/
        String enabledMonth = enableInfo[1]; /*������*/

        /*���û�н���ˣ�ϵͳ��ʼ��Ϊ��һ��δ������*/
        if ((accMonth == null) || (accMonth.trim().length() == 0)) {
            notClosedMonth = enabledMonth;
            notClosedYear = enabledYear;
            bIsAccounted = true;

        } else {
        	AccountCalendar ac = AccountCalendar.getInstance();
            /*ϵͳ�������ȫ������ڼ�*/
        	ac.set(accYear);
//            AccperiodVO periodVO = ac.getYearVO();//CallPeriod.findAccperiodByYear(accYear);
            /*������ȵ����һ������ڼ�*/
            nc.vo.bd.period2.AccperiodmonthVO[] vo_moths = ac.getMonthVOsOfCurrentYear();//periodVO.getVosMonth();
            
            /*Update by Top 2004-2-19 for */
            int sumMonth =
                vo_moths == null ? 0 : Integer.parseInt(vo_moths[vo_moths.length - 1].getAccperiodmth());
            /*Update end */
            
            /*�����ǰ�����ڼ�������һ���ڼ䣬ȡ��һ��ȵĵ�һ���ڼ�*/
            if (new Integer(accMonth).intValue() >= sumMonth) {
                notClosedYear = new Integer(Integer.valueOf(accYear).intValue() + 1).toString();
                ac.set(accYear);
//                periodVO = ac.getYearVO();//CallPeriod.findAccperiodByYear(notClosedYear); /*ϵͳȫ������ڼ�*/
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
		String year = vo.getCurNd(); /*ѡ�е�Ҫȡ�����˵������ڵ����*/
		String cope = vo.getCurQj(); /*ѡ�е�Ҫȡ�����˵������ڵ��ڼ�*/
		String pkCorp = vo.getCurDwbm();
		String prodID = vo.getProdID();

		if(enableInfo==null){
			enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
		}
		
		String enabledYear = enableInfo[0]; /*������*/
		String enabledMonth = enableInfo[1]; /*������*/

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
