package nc.util.erm.expamortize;

import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IBatchCloseAccQryPubServicer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class ExpamtVoChecker {
	/**
	 * 摊销验证
	 *
	 * @param aggvo
	 * @param currYearMonth
	 *            当前会计月 例“2012-02”
	 * @throws BusinessException
	 */
	public boolean checkAmortize(AggExpamtinfoVO aggvo, String currYearMonth) throws BusinessException {
		boolean check = aggvo.getParentVO().getAttributeValue("start_period").toString().compareToIgnoreCase(currYearMonth) <= 0;
		if (!check) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0118")/*@res "会计期间不应该小于开始摊销期间"*/);
		}
		if (check) {
			// 校验本期如果执行过摊销，则删除凭证后,重新生成凭证。
			if (((ExpamtinfoVO) aggvo.getParentVO()).getAmt_status() != null
					&& ((ExpamtinfoVO) aggvo.getParentVO()).getAmt_status().booleanValue() == true) {
				return true;
			}
			// 校验是否连续摊销，得到它的上一期的摊销信息
			if (aggvo.getParentVO().getAttributeValue("start_period").toString().compareToIgnoreCase(currYearMonth) == 0) {
				// 如果等于开始摊销期间，就不判断上一期

			} else {
				String[] startYearMonth = currYearMonth.split("-");
				AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(((ExpamtinfoVO) aggvo.getParentVO()).getPk_org());
				calendar.set(startYearMonth[0], startYearMonth[1]);
				// 先得到本期会计期间的会计月
				AccperiodmonthVO monthvo = calendar.getMonthVO();
				// 再得到上一期的会计月
				AccperiodmonthVO lastmonthvo = ErAccperiodUtil.getLastAccMonth(monthvo);
				// 得到上一期的摊销状态
				if (!ExpamtUtil.getAmtStatus(((ExpamtinfoVO) aggvo.getParentVO()), lastmonthvo.getYearmth()).booleanValue()) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0119")/*@res "上期没有摊销完成,不可以进行摊销"*/);
				}
			}
		}
		return false;
	}
	
	/**
	 * 取消摊销验证
	 *
	 * @param aggvo
	 * @param currYearMonth
	 *            当前会计月 例“2012-02”
	 * @throws BusinessException
	 */
	public void checkUnAmortize(AggExpamtinfoVO aggvo, String currYearMonth) throws BusinessException {
		boolean checkStartPeriod = aggvo.getParentVO().getAttributeValue(ExpamtinfoVO.START_PERIOD).toString().compareToIgnoreCase(currYearMonth) > 0;
		boolean checkEndPeriod = aggvo.getParentVO().getAttributeValue(ExpamtinfoVO.END_PERIOD).toString().compareToIgnoreCase(currYearMonth) < 0;
		
		if(checkStartPeriod){
			throw new BusinessException("会计期间不应该小于开始摊销期间");
		}
		
		if(checkEndPeriod){
			throw new BusinessException("会计期间不应该大于结束摊销期间");
		}
		
		//校验本期是否有可供取消摊销数据
		if(((ExpamtinfoVO)aggvo.getParentVO()).getAmt_status() ==null || ((ExpamtinfoVO)aggvo.getParentVO()).getAmt_status().booleanValue()==false){
			throw new BusinessException("摊销信息未摊销，不允许反摊销");
		}
		
		// 校验是否逐级取消摊销// 如果等于最后摊销期间，就不判断下一期
		if (aggvo.getParentVO().getAttributeValue(ExpamtinfoVO.END_PERIOD).toString().compareToIgnoreCase(currYearMonth) != 0) {
			String[] startYearMonth = currYearMonth.split("-");
			AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(((ExpamtinfoVO) aggvo.getParentVO()).getPk_org());
			calendar.set(startYearMonth[0], startYearMonth[1]);
			// 先得到本期会计期间的会计月
			AccperiodmonthVO monthvo = calendar.getMonthVO();
			// 再得到下一期的会计月
			AccperiodmonthVO nextmonthvo = ErAccperiodUtil.getNextAccMonth(monthvo);
			
			// 得到下一期的摊销状态
			if (ExpamtUtil.getAmtStatus(((ExpamtinfoVO) aggvo.getParentVO()), nextmonthvo.getYearmth()).booleanValue()) {
				throw new BusinessException("下一期已经摊销，不能取消摊销本期");
			}
		}
		
	}
	
	/**
	 * 校验这个期间是否组织结账
	 * @param vo
	 * @param currYearMonth
	 * @return
	 * @throws BusinessException
	 */
	public static void checkEndAcc(ExpamtinfoVO vo, String currYearMonth) throws BusinessException {
		StringBuffer msg = new StringBuffer();
		String pk_group=getGroupId();
		String pk_org=(String) vo.getAttributeValue("pk_org");
		AccperiodmonthVO accperiodmonthVO=ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org, currYearMonth);
		String pk_accperiodmonth=accperiodmonthVO.getPk_accperiodmonth();
		String pk_accperiodscheme = accperiodmonthVO.getPk_accperiodscheme();
		BatchCloseAccBookVO[] ermCloseAccBook = NCLocator.getInstance().lookup(
				IBatchCloseAccQryPubServicer.class).queryCloseAccBookVOs(
				pk_group, BXConstans.ERM_MODULEID, pk_accperiodscheme,
				pk_accperiodmonth, new String[] { pk_org });
		if (ermCloseAccBook != null && ermCloseAccBook.length > 0) {
			BatchCloseAccBookVO accvo = ermCloseAccBook[0];
			if (accvo.getIsendacc().equals(UFBoolean.TRUE)) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0120")/*@res "这个会计期间费用管理已经结账，不可以再摊销"*/);
			}
		}
		if (msg.length() != 0) {
			throw new BusinessException(msg.toString());
		}
	}

	private static String getGroupId() {
		return InvocationInfoProxy.getInstance().getGroupId();
	}



}