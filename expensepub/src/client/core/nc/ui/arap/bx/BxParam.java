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
 * 缓存借款报销需要的参数
 *
 * nc.ui.arap.bx.BxParam
 */
public class BxParam {

    //是否常用单据节点
    private boolean isInit = false;

	// 期初标志 true期初
	private boolean m_bQc = false;

	//录入人
	private String m_Lrr;

	// 本币金额小数位数
	private Integer m_Digit_b = null;

	// 单位
	private String m_Pk_org = null;

	//节点打开方式 默认0, 审批1
	private int nodeOpenType = NodeOpenType_Default;

	public static  int NodeOpenType_Default = 0;
	public static  int NodeOpenType_Approve = 1;
	public static  int NodeOpenType_Link = 2;
	public static  int NodeOpenType_LR_PUB_Approve = 3;

	/**
	 * ARAPDjSettingParam 构造子注解。
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
	 * 根据主组织和模块名查询模块启用期间,应用于报销
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 * @author liansg
	 */
	public UFDate queryEnabledCalendar(String pk_org,String syscode) throws BusinessException {

		String[] cal = FipubTools.queryEnabledPeriod(pk_org,syscode );
		if(null == cal){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0039")/*@res "查询模块启用期间出错"*/);

		}
		return new UFDate(cal[0]);
	}
	//审核日期改动
	public UFDateTime queryEnabledCalendarTime(String pk_org,String syscode) throws BusinessException {

		String[] cal = FipubTools.queryEnabledPeriod(pk_org,syscode );
		if(null == cal){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0039")/*@res "查询模块启用期间出错"*/);
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