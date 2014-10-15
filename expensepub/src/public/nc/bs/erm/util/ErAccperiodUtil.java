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
 * ����ڼ乤����
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
			// �������ں�ȡ��ǰ���ڼ伴��
			return calendar.getMonthVO();
		} else if (busiDate.trim().length() == 7) {
			String enableStr = getPeriod(busiDate);
			String year = getYear(busiDate);
			calendar.set(year, enableStr);
			// �������ں�ȡ��ǰ���ڼ伴��
			return calendar.getMonthVO();
		}
		return null;
	}

	/**
	 *
	 * �������ڲ�ѯ�������ڼ�
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
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0023")/*@res "ȡ�ڼ����"*/);
		}
		return accperiodmonthVO.getYearmth().split("-");
	}

	private static String getYear(String str) throws BusinessException {
		if (str == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0024")/*@res "�ڼ䲻��Ϊ��!"*/);
		} else if (str.length() >= 4) {
			return str.substring(0, 4);
		}
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0025")/*@res "����ȷ���ڼ��ʽ:"*/ + str);
	}

	private static String getPeriod(String str) throws BusinessException {
		if (str == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0024")/*@res "�ڼ䲻��Ϊ��!"*/);
		} else if (str.length() >= 7) {
			return str.substring(5, 7);
		}
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0025")/*@res "����ȷ���ڼ��ʽ:"*/ + str);
	}

	/**
	 * ���ݻ���¶�PK��ȡ��������
	 * [0] Ϊ�� ��[1]Ϊ��
	 * @return
	 * @throws BusinessException
	 */
	public static String[] getYearMonthByPk_accperiodMonth(String pk_accperiodmonth) throws BusinessException{
		if(pk_accperiodmonth == null){
			return null;
		}

		AccperiodmonthVO vo = getAccperiodmonthByPk(pk_accperiodmonth);

		if(vo == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0026")/*@res "δ�ҵ���Ӧ�������!"*/);
		}

		String yearMonth = vo.getYearmth();

		return yearMonth.split("-");
	}

	/**
	 * ���ݻ���¶�PK��ȡ����¶�
	 * @param pk_accperiodmonth
	 * @return
	 */
	public static AccperiodmonthVO getAccperiodmonthByPk(String pk_accperiodmonth){
		AccperiodmonthVO vo = AccperiodmonthAccessor.getInstance().queryAccperiodmonthVOByPk(pk_accperiodmonth);
		return vo;
	}

	/**
	 * ������Ȼ���ڻ�ȡ����¶�VO
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws InvalidAccperiodExcetion
	 */
	public static AccperiodmonthVO getAccperiodmonthByUFDate(String pk_org, UFDate date) throws InvalidAccperiodExcetion{
		AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
		calendar.setDate(date);
		//�������ں�ȡ��ǰ���ڼ伴��
		return calendar.getMonthVO();
	}

	/**
	 * ������ʼ����¶ȣ������ۼ���������ȡ��ֹ����¶�
	 * ��ʼ�����루���� 2012-01�� �ۼ�2 ����Ϊ 2012-01,2012-02����ֹΪ2012-02��
	 * @param startPeriodMonth ��ʼ�¶�pk
	 * @param addM �ۼӻ����
	 * @return
	 * @throws BusinessException
	 */
	public static AccperiodmonthVO getAddedAccperiodmonth(AccperiodmonthVO startMonthVo, int addM) throws BusinessException{

		if(startMonthVo == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0027")/*@res "δ�ҵ���ʼ�������!"*/);
		}

		if(addM <= 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0028")/*@res "��̯����Ӧ����0!"*/);
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
				throw new BusinessException(currentYear + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0029")/*@res "���Ӧ����ڼ䲻���ڣ�"*/);
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
	 * ������ʼ����¶ȣ������ۼ���������ȡ��ֹ����¶�
	 * ��ʼ�����루���� 2012-01�� �ۼ�2 ����Ϊ 2012-01,2012-02����ֹΪ2012-02��
	 * @param pk_org ����֯pk
	 * @param startAccYearMonth ��ʼ�¶��ַ��� ��ʽ ��2012-02��
	 * @param addMonth �ۼӻ����
	 * @return
	 * @throws BusinessException
	 */
	public static AccperiodmonthVO getAddAccperiodmonth(String pk_org, String startAccYearMonth, int addMonth) throws BusinessException{
		if(pk_org == null || startAccYearMonth == null || addMonth < 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0030")/*@res "����ȷ�Ĳ���"*/);
		}
		if(addMonth == 0){
			return getAccperiodmonthByAccMonth(pk_org, startAccYearMonth);
		}

		AccperiodmonthVO monthVo = getAccperiodmonthByAccMonth(pk_org, startAccYearMonth);

		return getAddedAccperiodmonth(monthVo, addMonth);
	}

	/**
	 * ����"2012-02"��ʽ��ʱ���ַ�����ȡ���ַ����Ļ���¶�
	 * @param accYearMonth "2012-02", ���Ϊnull���򷵻ص�ǰʱ���Ӧ�Ļ���ڼ�
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
	 * ��ȡ���»����
	 * @param currentAccMonth ���ڻ����
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