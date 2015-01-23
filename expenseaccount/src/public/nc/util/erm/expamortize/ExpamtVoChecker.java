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
	 * ̯����֤
	 *
	 * @param aggvo
	 * @param currYearMonth
	 *            ��ǰ����� ����2012-02��
	 * @throws BusinessException
	 */
	public boolean checkAmortize(AggExpamtinfoVO aggvo, String currYearMonth) throws BusinessException {
		boolean check = aggvo.getParentVO().getAttributeValue("start_period").toString().compareToIgnoreCase(currYearMonth) <= 0;
		if (!check) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0118")/*@res "����ڼ䲻Ӧ��С�ڿ�ʼ̯���ڼ�"*/);
		}
		if (check) {
			// У�鱾�����ִ�й�̯������ɾ��ƾ֤��,��������ƾ֤��
			if (((ExpamtinfoVO) aggvo.getParentVO()).getAmt_status() != null
					&& ((ExpamtinfoVO) aggvo.getParentVO()).getAmt_status().booleanValue() == true) {
				return true;
			}
			// У���Ƿ�����̯�����õ�������һ�ڵ�̯����Ϣ
			if (aggvo.getParentVO().getAttributeValue("start_period").toString().compareToIgnoreCase(currYearMonth) == 0) {
				// ������ڿ�ʼ̯���ڼ䣬�Ͳ��ж���һ��

			} else {
				String[] startYearMonth = currYearMonth.split("-");
				AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(((ExpamtinfoVO) aggvo.getParentVO()).getPk_org());
				calendar.set(startYearMonth[0], startYearMonth[1]);
				// �ȵõ����ڻ���ڼ�Ļ����
				AccperiodmonthVO monthvo = calendar.getMonthVO();
				// �ٵõ���һ�ڵĻ����
				AccperiodmonthVO lastmonthvo = ErAccperiodUtil.getLastAccMonth(monthvo);
				// �õ���һ�ڵ�̯��״̬
				if (!ExpamtUtil.getAmtStatus(((ExpamtinfoVO) aggvo.getParentVO()), lastmonthvo.getYearmth()).booleanValue()) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0119")/*@res "����û��̯�����,�����Խ���̯��"*/);
				}
			}
		}
		return false;
	}
	
	/**
	 * ̯����֤
	 *
	 * @param aggvo
	 * @param currYearMonth
	 *            ��ǰ����� ����2012-02��
	 * @throws BusinessException
	 */
	public void checkUnAmortize(AggExpamtinfoVO aggvo, String currYearMonth) throws BusinessException {
		//TODO 
	}
	
	/**
	 * У������ڼ��Ƿ���֯����
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
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0120")/*@res "�������ڼ���ù����Ѿ����ˣ���������̯��"*/);
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