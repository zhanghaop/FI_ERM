package nc.ui.arap.bx;


import nc.itf.fi.pub.Currency;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.ui.er.util.BXUiUtil;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fipub.utils.FipubTools;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

/**
 * @author twei
 *
 * ���������Ҫ�Ĳ���
 *
 * nc.ui.arap.bx.BxParam
 */
public class BxParam {

    //�Ƿ��õ��ݽڵ�
    private boolean isInit = false;

	// �ڳ���־ true�ڳ�
	private boolean m_bQc = false;

	//¼����
	private String m_Lrr;

	// ���ҽ��С��λ��
	private Integer m_Digit_b = null;

	// ��λ
	private String m_Pk_org = null;

	//�ڵ�򿪷�ʽ Ĭ��0, ����1
	private int nodeOpenType = NodeOpenType_Default;

	public static  int NodeOpenType_Default = 0;
	public static  int NodeOpenType_Approve = 1;
	public static  int NodeOpenType_Link = 2;
	public static  int NodeOpenType_LR_PUB_Approve = 3;

	/**
	 * ARAPDjSettingParam ������ע�⡣
	 */
	public BxParam() {
		super();
	}


	public java.lang.Integer getDigit_b(String org) {
		if (m_Digit_b == null) {
			try {
				m_Digit_b = Currency.getCurrInfo(Currency.getOrgLocalCurrPK(org)).getCurrdigit();

			} catch (Exception e) {
				ExceptionHandler.debug(e.getMessage());
				return m_Digit_b;
			}
		}
		return m_Digit_b;
	}

	public boolean getIsQc() {
		return m_bQc;
	}

	/**
	 *
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getSysDate() {
		return BXUiUtil.getSysdate();
	}
	/**
	 *
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBusiDate() {
		return BXUiUtil.getBusiDate();
	}
	/**
	 *
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDateTime getSysDateTime() {
		return BXUiUtil.getSysdatetime();
	}
	/**
	 *
	 * @return boolean
	 */
	public java.lang.String getPk_user() {
		if (m_Lrr == null) {
			m_Lrr = BXUiUtil.getPk_user();
		}
		return m_Lrr;
	}


	/**
	 * @return boolean
	 */

	public void setIsQc(boolean newQc) {
		m_bQc = newQc;
	}


	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	public int getNodeOpenType() {
		return nodeOpenType;
	}

	public void setNodeOpenType(int nodeOpenType) {
		this.nodeOpenType = nodeOpenType;
	}

	/**
	 *
	 * ��������֯��ģ������ѯģ�������ڼ�,Ӧ���ڱ���
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 * @author liansg
	 */
	public UFDate queryEnabledCalendar(String pk_org,String syscode) throws BusinessException {

		String[] cal = FipubTools.queryEnabledPeriod(pk_org,syscode );
		if(null == cal){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0039")/*@res "��ѯģ�������ڼ����"*/);

		}
		return new UFDate(cal[0]);
	}
	//������ڸĶ�
	public UFDateTime queryEnabledCalendarTime(String pk_org,String syscode) throws BusinessException {

		String[] cal = FipubTools.queryEnabledPeriod(pk_org,syscode );
		if(null == cal){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0039")/*@res "��ѯģ�������ڼ����"*/);
		}
		return new UFDateTime(cal[0]);


	}

	public String getPk_org() throws Exception {
		if (m_Pk_org == null) {
			m_Pk_org = OrgSettingAccessor.getDefaultOrgUnit();
		}
		return m_Pk_org;
	}



}