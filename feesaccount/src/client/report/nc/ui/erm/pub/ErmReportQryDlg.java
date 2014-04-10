package nc.ui.erm.pub;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.comp.ReportUiUtil;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.utils.fipub.FipubReportResource;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.fipub.utils.RefConstant;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;

/**
 * <p>
 * 报销管理帐表默认的查询对话框
 * </p>
 *
 * @author chendya
 * @version V6.1
 * @since V6.1 创建时间：2010-12-16 下午01:45:55
 */
@SuppressWarnings("restriction")
public class ErmReportQryDlg extends ErmAbstractReportBaseDlg {

	private static final long serialVersionUID = 8970799451247766950L;
	
	//费用余额
	public static final int ERM_EXPBAL= 6;
	//费用明细
	public static final int ERM_EXPDETAIL = 7;
	//费用申请单明细
	public static final int ERM_MATTERAPP= 8;

	public ErmReportQryDlg(Container parent, IContext context,
            String strNodeCode, int iSysCode, TemplateInfo ti, String title,
            String djlx) {
        super(parent, context, strNodeCode, iSysCode, ti, title, djlx);
	}
	
	private class ComboBoxItemListener implements ItemListener {
		@Override
        public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				try {
					setQueryPeriod((String) ((DefaultConstEnum) e.getItem()).getValue(), null);
				} catch (BusinessException ex) {
					Log.getInstance(ErmReportQryDlg.this.getClass()).error(ex);
				}
			}
		}
	}
	
	@Override
	protected void handleOrgChangeEvent(ValueChangedEvent event)
			throws BusinessException {
		super.handleOrgChangeEvent(event);
		ReportQueryCondVO queryCondVO = (ReportQueryCondVO) getReportQueryCondVO();
		setQueryPeriod(queryCondVO.getQryMode(), queryCondVO);
	}
	
	@Override
    public void initUIData() throws BusinessException {
	    beforeShowModal();
    }

    @Override
	protected String doBusiCheck() {
		// ①执行父类校验
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}
		
		try{
			Object startVal = ((UIRefPane) getComponent(BEGIN_TIME_REF)).getValueObj();
			Object endVal = ((UIRefPane) getComponent(END_TIME_REF)).getValueObj();
			
			if (endVal == null || startVal == null) {
				errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0117")/*@res "查询日期不能为空！"*/;
			}else if(endVal != null && startVal != null){
				UFDate startDate = getRefDateByObj(startVal, true);
				UFDate endDate = getRefDateByObj(endVal, false);
				if(startDate.compareTo(endDate) > 0){
					errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0116")/*@res "查询开始时间不能晚于结束时间！"*/;
				}
			}
		}catch(BusinessException e) {
			errMsg = e.getMessage();
		}
		
		return StringUtils.isEmpty(errMsg) ? null : errMsg;
	}

    @Override
	protected void resetUserReportQueryCondVO(IReportQueryCond queryCond) {
		ReportQueryCondVO queryCondVO = (ReportQueryCondVO) queryCond;
		((UIComboBox) getComponent(QRY_MODE_COMB)).setSelectedItem(queryCondVO.getQryMode()); // 查询方式
		((UIComboBox) getComponent(BILL_STATE_COMB)).setSelectedItem(queryCondVO.getBillState()); // 单据状态
		((UIRefPane) getComponent(CURRENCY_REF)).setPK(queryCondVO.getPk_currency()); // 币种
		((UIRefPane) getComponent(FINANCIAL_ORG_REF)).setPKs(queryCondVO.getPk_orgs()); // 财务组织
		
		queryCondVO.setBeginDate(null);
		queryCondVO.setEndDate(null);
		
		try {
			setQueryPeriod(queryCondVO.getQryMode(), queryCondVO);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		// =================================
		setPk_org(queryCondVO.getPk_orgs());
		// =================================
	}
	
	@Override
	protected List<Component> getComponentList() throws BusinessException {

		List<Component> normalCondCompList = super.getNormalCondCompList();
		if (normalCondCompList.size() == 0) {
			// 查询方式
			normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002777")/*@res "查询方式"*/));
			UIComboBox qryModeComb = new UIComboBox();

			qryModeComb.addItems(new DefaultConstEnum[] {
					new DefaultConstEnum(IPubReportConstants.QUERY_MODE_MONTH, FipubReportResource.getQueryModeMonthLbl()),
					new DefaultConstEnum(IPubReportConstants.QUERY_MODE_DATE, FipubReportResource.getQueryModeDateLbl()) });

			qryModeComb.addItemListener(new ComboBoxItemListener());
			normalCondCompList.add(qryModeComb);
			addComponent(QRY_MODE_COMB, qryModeComb);
			// 币种
			normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0014")/*@res "币　　种"*/));
			UIRefPane currencyRef = new UIRefPane(RefConstant.REF_NODENAME_CURRENCY);
			currencyRef.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
			normalCondCompList.add(currencyRef);
			addComponent(CURRENCY_REF, currencyRef);
			currencyRef.setDisabledDataButtonShow(true);
			// 开始时间
			UILabel timeTypeLabel = getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0026")/*@res "月　　份"*/);
			normalCondCompList.add(timeTypeLabel);
			UIRefPane beginTimeRef = new UIRefPane(RefConstant.REF_NODENAME_ACCPERIOD);
			normalCondCompList.add(beginTimeRef);
			addComponent(TIME_TYPE_LABEL, timeTypeLabel);
			addComponent(BEGIN_TIME_REF, beginTimeRef);
			// 结束时间
			normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003365")/*@res "至"*/));
			UIRefPane endTimeRef = new UIRefPane(RefConstant.REF_NODENAME_ACCPERIOD);
			normalCondCompList.add(endTimeRef);
			addComponent(END_TIME_REF, endTimeRef);

			// 财务组织
			normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0062")/*@res "财务组织"*/));
			UIRefPane financialOrgRef = new UIRefPane(RefConstant.REF_NODENAME_FINANCEORG);
			financialOrgRef.setMultiSelectedEnabled(true);
			financialOrgRef.addValueChangedListener(new OrgChangedListener());
			normalCondCompList.add(financialOrgRef);
			addComponent(FINANCIAL_ORG_REF, financialOrgRef);
			final String key = PsnVoCall.FIORG_PK_ + ErUiUtil.getPk_psndoc() + ErUiUtil.getPK_group();
			String fiorg = (String) WorkbenchEnvironment.getInstance().getClientCache(key); // 人员所属组织
			String pk_org = StringUtils.isEmpty(ReportUiUtil.getDefaultOrgUnit()) ? fiorg : ReportUiUtil.getDefaultOrgUnit();
			if (!hasPerm(pk_org, getLoginContext().getPkorgs())) {
			    pk_org = null;
			}
			financialOrgRef.setPK(pk_org);
            financialOrgRef.getRefModel().setFilterPks(getAllPermissionOrgs());
            financialOrgRef.setDisabledDataButtonShow(true);
            
			// added by chendya 特殊处理->去掉数据权限控制
			if (!ArrayUtils.isEmpty(getAllPermissionOrgs())) {
				AbstractRefModel refModel = financialOrgRef.getRefModel();
				refModel.setFilterPks(getAllPermissionOrgs(), IFilterStrategy.INSECTION);
				refModel.setUseDataPower(false);
			}

			// 设置查询对象可参照组织
			if (pk_org != null) {
		         setPk_org(new String[] { pk_org });
			} else {
			    setPk_org(null);
			}

			// 单据状态
			normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000804")/*@res "单据状态"*/));
			UIComboBox billStateComb = new UIComboBox();
			if(getSysCode()==ERM_MATTERAPP){
				billStateComb.addItems(new DefaultConstEnum[] {
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_ALL, FipubReportResource
								.getBillStatusAllLbl()),
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_SAVE, FipubReportResource
								.getBillStatusSaveLbl()),
						new DefaultConstEnum(IErmReportConstants.BILL_STATUS_COMMIT,
						        nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						                "201212_0", "0201212-0081")/* @res "已提交" */),// 单据状态——已提交
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_CONFIRM, FipubReportResource
								.getBillStatusAuditLbl()) });
			}else {
				billStateComb.addItems(new DefaultConstEnum[] {
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_ALL, FipubReportResource.getBillStatusAllLbl()),
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_SAVE, FipubReportResource.getBillStatusSaveLbl()),
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_CONFIRM, FipubReportResource.getBillStatusAuditLbl()),
						new DefaultConstEnum(IPubReportConstants.BILL_STATUS_EFFECT, FipubReportResource.getBillStatusEffectLbl())});
			}
			normalCondCompList.add(billStateComb);
			addComponent(BILL_STATE_COMB, billStateComb);
		}

		setQueryPeriod(IPubReportConstants.QUERY_MODE_MONTH, null);

		return normalCondCompList;
	}

	/**
	 * 设置查询期间范围<br>
	 *
	 */
	private void setQueryPeriod(String qryMode, IReportQueryCond queryCondVO) throws BusinessException {
		//获取当前业务日期
		UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
		//获取界面控件
		UILabel tempLabel = (UILabel) getComponent(TIME_TYPE_LABEL);
		UIRefPane tempBeginRefPane = (UIRefPane) getComponent(BEGIN_TIME_REF);
		UIRefPane tempEndRefPane = (UIRefPane) getComponent(END_TIME_REF);
		if (IPubReportConstants.QUERY_MODE_MONTH.equals(qryMode)) {
			tempLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0026")/*@res "月　　份"*/);
			tempBeginRefPane.setRefNodeName(RefConstant.REF_NODENAME_ACCPERIOD);
			tempEndRefPane.setRefNodeName(RefConstant.REF_NODENAME_ACCPERIOD);

			String[] pk_orgs = getPk_org();
			AccountCalendar calendar = ArrayUtils.isEmpty(pk_orgs) ? AccountCalendar
			.getInstance() : AccountCalendar.getInstanceByPk_org(pk_orgs[0]);
			if (!ArrayUtils.isEmpty(pk_orgs)) {
				String defaultpk_accperiodscheme = calendar.getMonthVO()==null ? null :calendar.getMonthVO().getPk_accperiodscheme();
				((AccPeriodDefaultRefModel) tempBeginRefPane.getRefModel())
						.setDefaultpk_accperiodscheme(defaultpk_accperiodscheme);
				((AccPeriodDefaultRefModel) tempEndRefPane.getRefModel())
						.setDefaultpk_accperiodscheme(defaultpk_accperiodscheme);
			}
			
			String pk_accperiodmonthbegin = null;
			String pk_accperiodmonthend = null;
            try {
                calendar.setDate((queryCondVO == null || queryCondVO
                        .getBeginDate() == null) ? currBusiDate : queryCondVO
                        .getBeginDate());
                pk_accperiodmonthbegin = calendar.getMonthVO()
                        .getPk_accperiodmonth();
                calendar.setDate((queryCondVO == null || queryCondVO
                        .getEndDate() == null) ? currBusiDate : queryCondVO
                        .getEndDate());
                pk_accperiodmonthend = calendar.getMonthVO()
                        .getPk_accperiodmonth();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }

			tempBeginRefPane.setPK(pk_accperiodmonthbegin);
			tempEndRefPane.setPK(pk_accperiodmonthend);
		} else {
			tempLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0031")/*@res "日　　 期"*/);

			tempBeginRefPane.setRefNodeName(RefConstant.REF_NODENAME_CALENDAR); // 日历
			tempEndRefPane.setRefNodeName(RefConstant.REF_NODENAME_CALENDAR); // 日历

			tempBeginRefPane.setValue((queryCondVO == null || queryCondVO.getBeginDate() == null)? currBusiDate.toLocalString() : queryCondVO.getBeginDate().toLocalString()); // 开始时间
			tempEndRefPane.setValue((queryCondVO == null || queryCondVO.getEndDate() == null)? currBusiDate.toLocalString() : queryCondVO.getEndDate().toLocalString()); // 结束时间
		}

		tempLabel.repaint();
	}
	
	@Override
	protected String[] getOrgRelationField() {
		return new String[]{ CURRENCY_REF };
	}
	
	/**
	 * 功能：设置常用查询条件VO
	 */
	@Override
    protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		// 查询方式
		UIComboBox tempComboBox = (UIComboBox) getComponent(QRY_MODE_COMB);
		if (tempComboBox != null) {
			qryCondVO.setQryMode((String) tempComboBox.getSelectdItemValue());
		}

		// 开始日期
		Object beginVal = ((UIRefPane) getComponent(BEGIN_TIME_REF)).getValueObj();
		UFDate beginDate = getRefDateByObj(beginVal, true);
		qryCondVO.setBeginDate(beginDate);

		// 结束日期
		Object endVal = ((UIRefPane) getComponent(END_TIME_REF)).getValueObj();
		UFDate endDate = getRefDateByObj(endVal, false);
		qryCondVO.setEndDate(endDate);

		// 币种
		qryCondVO.setPk_currency(((UIRefPane) getComponent(CURRENCY_REF)).getRefPK());
		// 单据状态
		qryCondVO.setBillState((String) ((UIComboBox) getComponent(BILL_STATE_COMB)).getSelectdItemValue());
		// 财务组织
		qryCondVO.setPk_orgs(getPk_org());
		//查询集团
		qryCondVO.setPk_group(ReportUiUtil.getPK_group());

		// 双引擎需要注册内容
		qryCondVO.setModuleId(BXConstans.ERM_PRODUCT_CODE_number);
		qryCondVO.setOwnModule(BXConstans.ERM_PRODUCT_CODE_Lower);
	}

	/**
	 * 根据日期参照值获取日期
	 * @param dateVal 
	 * @param begin 如果为true,则为0时0分0秒；否则为23时59分59秒
	 * @return
	 * @throws InvalidAccperiodExcetion
	 */
	private UFDate getRefDateByObj(Object dateVal, boolean begin) throws InvalidAccperiodExcetion {
		UFDate date;
		if (dateVal == null || "".equals(dateVal)) {
			date = null;
		} else if (dateVal instanceof UFDate) {
			date = new UFDate(((UFDate) dateVal).toLocalString(), begin);
		} else if (dateVal.getClass().isArray()) {
			Object[] arrObj = (Object[]) dateVal;
			if(begin){
				return AccountCalendar.getInstanceByAccperiodMonth(arrObj[0].toString()).getMonthVO().getBegindate();
			}
			return AccountCalendar.getInstanceByAccperiodMonth(arrObj[0].toString()).getMonthVO().getEnddate();
		} else {
			date = new UFDate(dateVal.toString(), begin);
		}
		return date;
	}
}
