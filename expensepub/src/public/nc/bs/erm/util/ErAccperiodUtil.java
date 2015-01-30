package nc.bs.erm.util;

import nc.bd.accperiod.AccperiodAccessor;
import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 *
 * 会计期间工具类
 *
 * @author lvhj
 *
 */
public class ErAccperiodUtil {
	private ErAccperiodUtil() {
	}

	public static AccperiodmonthVO getAccperiodmonthVO(
			String pk_accperiodscheme, String busiDate)
			throws BusinessException {
		AccountCalendar calendar = AccountCalendar
				.getInstanceByPeriodScheme(pk_accperiodscheme);

		if (calendar == null) {
			return null;
		}
		if (busiDate.trim().length() >= 10) {
			UFDate date = new UFDate(busiDate);
			calendar.setDate(date);
			// 设置日期后取当前月期间即可
			return calendar.getMonthVO();
		} else if (busiDate.trim().length() == 7) {
			String enableStr = getPeriod(busiDate);
			String year = getYear(busiDate);
			calendar.set(year, enableStr);
			// 设置日期后取当前月期间即可
			return calendar.getMonthVO();
		}
		return null;
	}

	/**
	 *
	 * 根据日期查询所属的期间
	 *
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	public static String[] getAccPeriod(String pk_accperiodscheme, UFDate date)
			throws BusinessException {
		AccperiodmonthVO accperiodmonthVO = getAccperiodmonthVO(pk_accperiodscheme,
				date.toLocalString());
		if (accperiodmonthVO == null || accperiodmonthVO.getYearmth() == null) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0023")/*@res "取期间出错"*/);
		}
		return accperiodmonthVO.getYearmth().split("-");
	}

	private static String getYear(String str) throws BusinessException {
		if (str == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0024")/*@res "期间不能为空!"*/);
		} else if (str.length() >= 4) {
			return str.substring(0, 4);
		}
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0025")/*@res "不正确的期间格式:"*/ + str);
	}

	private static String getPeriod(String str) throws BusinessException {
		if (str == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0024")/*@res "期间不能为空!"*/);
		} else if (str.length() >= 7) {
			return str.substring(5, 7);
		}
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0025")/*@res "不正确的期间格式:"*/ + str);
	}

	/**
	 * 根据会计月度PK获取年月数组
	 * [0] 为年 ，[1]为月
	 * @return
	 * @throws BusinessException
	 */
	public static String[] getYearMonthByPk_accperiodMonth(String pk_accperiodmonth) throws BusinessException{
		if(pk_accperiodmonth == null){
			return null;
		}

		AccperiodmonthVO vo = getAccperiodmonthByPk(pk_accperiodmonth);

		if(vo == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0026")/*@res "未找到相应会计年月!"*/);
		}

		String yearMonth = vo.getYearmth();

		return yearMonth.split("-");
	}

	/**
	 * 根据会计月度PK获取会计月度
	 * @param pk_accperiodmonth
	 * @return
	 */
	public static AccperiodmonthVO getAccperiodmonthByPk(String pk_accperiodmonth){
		AccperiodmonthVO vo = AccperiodmonthAccessor.getInstance().queryAccperiodmonthVOByPk(pk_accperiodmonth);
		return vo;
	}

	/**
	 * 根据自然日期获取会计月度VO
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws InvalidAccperiodExcetion
	 */
	public static AccperiodmonthVO getAccperiodmonthByUFDate(String pk_org, UFDate date) throws InvalidAccperiodExcetion{
		AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
		calendar.setDate(date);
		//设置日期后取当前月期间即可
		return calendar.getMonthVO();
	}

	/**
	 * 根据起始会计月度，加上累加月数，获取终止会计月度
	 * 起始月算入（例如 2012-01， 累加2 ，则为 2012-01,2012-02，终止为2012-02）
	 * @param startPeriodMonth 起始月度pk
	 * @param addM 累加会计月
	 * @return
	 * @throws BusinessException
	 */
	public static AccperiodmonthVO getAddedAccperiodmonth(AccperiodmonthVO startMonthVo, int addM) throws BusinessException{

		if(startMonthVo == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0027")/*@res "未找到起始会计年月!"*/);
		}

		if(addM <= 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0028")/*@res "总摊销期应大于0!"*/);
		}

		String[] yearMonthStrs = startMonthVo.getYearmth().split("-");

		int startY = Integer.parseInt(yearMonthStrs[0]);
		int startM = Integer.parseInt(yearMonthStrs[1]);

		boolean flag = true;

		int monthNum = 0;
		int addYear = 0;
		int endMonth = 0;
		while(flag){

			String currentYear = "" + (startY + addYear);
			AccperiodVO vo = AccperiodAccessor.getInstance()
				.queryAccperiodVOByYear(startMonthVo.getPk_accperiodscheme(), currentYear);

			if(vo == null){
				throw new BusinessException(currentYear + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0029")/*@res "年对应会计期间不存在！"*/);
			}

			monthNum = monthNum + vo.getPeriodnum();

			if((startM - 1) + addM <= monthNum){
				endMonth = (startM - 1) + addM - (monthNum - vo.getPeriodnum());
				flag = false;
			}else{
				addYear ++;
			}
		}

		AccountCalendar calendar = AccountCalendar.getInstanceByPeriodScheme(startMonthVo.getPk_accperiodscheme());

		String endMonthStr = getStrMonth(endMonth);

		calendar.set("" + (startY + addYear), endMonthStr);

		return calendar.getMonthVO();
	}


	/**
	 * 根据起始会计月度，加上累加月数，获取终止会计月度
	 * 起始月算入（例如 2012-01， 累加2 ，则为 2012-01,2012-02，终止为2012-02）
	 * @param pk_org 主组织pk
	 * @param startAccYearMonth 起始月度字符串 格式 “2012-02”
	 * @param addMonth 累加会计月
	 * @return
	 * @throws BusinessException
	 */
	public static AccperiodmonthVO getAddAccperiodmonth(String pk_org, String startAccYearMonth, int addMonth) throws BusinessException{
		if(pk_org == null || startAccYearMonth == null || addMonth < 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0030")/*@res "不正确的参数"*/);
		}
		if(addMonth == 0){
			return getAccperiodmonthByAccMonth(pk_org, startAccYearMonth);
		}

		AccperiodmonthVO monthVo = getAccperiodmonthByAccMonth(pk_org, startAccYearMonth);

		return getAddedAccperiodmonth(monthVo, addMonth);
	}

	/**
	 * 根据"2012-02"格式的时间字符串获取该字符串的会计月度
	 * @param accYearMonth "2012-02", 如果为null，则返回当前时间对应的会计期间
	 * @return
	 * @throws BusinessException
	 */
	public static AccperiodmonthVO getAccperiodmonthByAccMonth(String pk_org, String accYearMonth) throws BusinessException{

		AccperiodmonthVO result = null;
		if(accYearMonth == null){
			result = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, new UFDate());
		}else{
			String[] yearMonthStrs = accYearMonth.split("-");
			AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
			calendar.set(yearMonthStrs[0], yearMonthStrs[1]);
			result = calendar.getMonthVO();
		}

		return result;
	}

	/**
	 * 获取上月会计月
	 * @param currentAccMonth 本期会计月
	 * @return
	 * @throws InvalidAccperiodExcetion
	 */
	public static AccperiodmonthVO getLastAccMonth(AccperiodmonthVO currentAccMonth) throws InvalidAccperiodExcetion {
		if (currentAccMonth != null) {
			String yearMth = currentAccMonth.getYearmth();

			String[] yearMths = yearMth.split("-");

			int year = Integer.parseInt(yearMths[0]);
			int month = Integer.parseInt(yearMths[1]);

			if (yearMths[1].equals("01")) {
				year--;
				AccperiodVO vo = AccperiodAccessor.getInstance().queryAccperiodVOByYear(currentAccMonth.getPk_accperiodscheme(), "" + (year));
				month = vo.getPeriodnum();
			} else {
				month--;
			}

			AccountCalendar calendar = AccountCalendar.getInstanceByPeriodScheme(currentAccMonth.getPk_accperiodscheme());

			calendar.set("" + year, getStrMonth(month));

			return calendar.getMonthVO();
		}

		return null;
	}
	
	/**获取下月会计月
	 * 
	 * @param currentAccMonth
	 * @return
	 * @throws BusinessException 
	 */
	public static AccperiodmonthVO getNextAccMonth(AccperiodmonthVO currentAccMonth) throws BusinessException {
		if (currentAccMonth != null) {
			return getAddedAccperiodmonth(currentAccMonth, 2);
//			String yearMth = currentAccMonth.getYearmth();
//
//			String[] yearMths = yearMth.split("-");
//
//			int year = Integer.parseInt(yearMths[0]);
//			int month = Integer.parseInt(yearMths[1]);
//
//			if (yearMths[1].equals("12")) {
//				year++;
//				AccperiodVO vo = AccperiodAccessor.getInstance().queryAccperiodVOByYear(currentAccMonth.getPk_accperiodscheme(), "" + (year));
//				month = vo.getPeriodnum();
//			} else {
//				month++;
//			}
//
//			AccountCalendar calendar = AccountCalendar.getInstanceByPeriodScheme(currentAccMonth.getPk_accperiodscheme());
//
//			calendar.set("" + year, getStrMonth(month));
//
//			return calendar.getMonthVO();
		}

		return null;
	}

	private static String getStrMonth(int month) {
		String endMonthStr = null;
		if(month < 10){
			endMonthStr = "0" + month;
		}else{
			endMonthStr = "" + month;
		}

		return endMonthStr;
	}
}