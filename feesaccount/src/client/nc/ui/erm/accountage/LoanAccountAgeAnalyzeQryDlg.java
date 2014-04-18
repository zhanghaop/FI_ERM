package nc.ui.erm.accountage;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fipub.report.IReportQueryCond;
import nc.ui.erm.pub.ErmAbstractReportBaseDlg;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.comp.ReportUiUtil;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.fipub.utils.RefConstant;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;

import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;

/**
 * <p>
 * 报销管理借款账龄分析对话框
 * </p>
 * 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2011-2-24 下午05:06:41
 */
@SuppressWarnings("restriction")
public class LoanAccountAgeAnalyzeQryDlg extends ErmAbstractReportBaseDlg {


	private static final long serialVersionUID = 2992805299331282686L;
	
	private static final String DATELINE_REF = "deadlineRef"; // 截止日期
	private static final String ACC_AGE_PLAN_REF = "accAgePlanRef"; // 账龄方案
	private static final String ANA_DATE_COMB = "anaDateComb"; // 分析日期
	private static final String ANA_PATTERN_COMB = "anaPatternComb"; // 分析方式
	private static final String ANA_MODE_COMB = "anaModeComb"; // 分析模式
	private static final String EFFECT_CHECK = "effectCheck"; // 生效状态

	public LoanAccountAgeAnalyzeQryDlg(Container parent, IContext context,
            String strNodeCode, int iSysCode, TemplateInfo ti, String title,
            String djlx) {
        super(parent, context, strNodeCode, iSysCode, ti, title, djlx);
	}

    @Override
    public void initUIData() throws BusinessException {
        beforeShowModal();
    }

    @Override
	protected List<Component> getComponentList() throws BusinessException {
		List<Component> normalCondCompList = super.getNormalCondCompList();
		if (normalCondCompList.size() > 0) {
			return normalCondCompList;
		}

//        final String key = PsnVoCall.FIORG_PK_ + ErUiUtil.getPk_psndoc() + ErUiUtil.getPK_group();
//        String fiorg = (String) WorkbenchEnvironment.getInstance().getClientCache(key); // 人员所属组织
//        String pk_org = StringUtils.isEmpty(ReportUiUtil.getDefaultOrgUnit()) ? fiorg : ReportUiUtil.getDefaultOrgUnit();
        String pk_org = ErUiUtil.getReportDefaultOrgUnit();
        
		// 获取当前业务日期
		String currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate().toStdString();
		
		// 截止日期
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0115")/*@res "截止日期"*/));
		UIRefPane datelineRef = new UIRefPane(RefConstant.REF_NODENAME_CALENDAR);
		datelineRef.setValue(currBusiDate);
		datelineRef.setEnabled(false); // 默认为不可编辑
		normalCondCompList.add(datelineRef);
		addComponent(DATELINE_REF, datelineRef);

		// 账龄方案
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0013")/*@res "账龄方案"*/));
		UIRefPane accAgePlanRef = new UIRefPane(RefConstant.REF_NODENAME_ACCAGEPLAN);
		if(pk_org!=null){
			accAgePlanRef.setPk_org(pk_org);
		}
		normalCondCompList.add(accAgePlanRef);
		addComponent(ACC_AGE_PLAN_REF, accAgePlanRef);

		// 币种
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0014")/*@res "币　　种"*/));
		UIRefPane currencyRef = new UIRefPane(RefConstant.REF_NODENAME_CURRENCY);
		if(pk_org!=null){
			currencyRef.setPk_org(pk_org);
		}	
		normalCondCompList.add(currencyRef);
		addComponent(CURRENCY_REF, currencyRef);

		// 分析模式：按日期、按账龄
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0016")/*@res "分析模式"*/));
		UIComboBox anaModeComb = new UIComboBox();
		//目前报销只需要按照账期
		anaModeComb.addItems(new String[] {
				IErmReportAnalyzeConstants.getACC_ANA_MODE_AGE()
				 });
		anaModeComb.addItemListener(new AnaModeChangeListener());
		normalCondCompList.add(anaModeComb);
		addComponent(ANA_MODE_COMB, anaModeComb);


		// 分析日期：到期日、单据日期、审核日期、生效日期、(起算日期/内控到期日期)
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0017")/*@res "分析日期"*/));
		UIComboBox anaDateComb = new UIComboBox();
		anaDateComb.addItems(new String[] {
				IErmReportAnalyzeConstants.getACC_ANA_DATE_LASTPAYDATE(),
				IErmReportAnalyzeConstants.getACC_ANA_DATE_BILLDATE(),
				IErmReportAnalyzeConstants.getACC_ANA_DATE_AUDITDATE(),
				IErmReportAnalyzeConstants.getACC_ANA_DATE_EFFECTDATE() });

		normalCondCompList.add(anaDateComb);
		addComponent(ANA_DATE_COMB, anaDateComb);


		// 分析方式：最终余额、点余额
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0018")/*@res "分析方式"*/));
		UIComboBox anaPatternComb = new UIComboBox();
		anaPatternComb.addItems(new String[] {
				IErmReportAnalyzeConstants.getACC_ANA_PATTERN_FINAL() });
		anaPatternComb.addItemListener(new ForDatelineListener());
		normalCondCompList.add(anaPatternComb);
		addComponent(ANA_PATTERN_COMB, anaPatternComb);


		// 财务组织
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000006")/*@res "财务组织"*/));
		UIRefPane financialOrgRefV = new UIRefPane(RefConstant.REF_NODENAME_FINANCEORG);
		financialOrgRefV.setPK(pk_org);
		financialOrgRefV.addValueChangedListener(new OrgChangedListener());
		financialOrgRefV.setMultiSelectedEnabled(true);
		if(financialOrgRefV.getRefModel()!=null){
            financialOrgRefV.getRefModel().setFilterPks(getAllPermissionOrgs(),IFilterStrategy.INSECTION);
            financialOrgRefV.getRefModel().setUseDataPower(false);
        }
		financialOrgRefV.setDisabledDataButtonShow(true);
		
        normalCondCompList.add(financialOrgRefV);
        addComponent(FINANCIAL_ORG_REF, financialOrgRefV);

		// 设置查询对象可参照组织
		if (pk_org != null) {
			setPk_org(new String[] { pk_org });
		}

		// 包含未生效报销单
		UICheckBox effectCheck= new UICheckBox();
		effectCheck.setName("unSettleChkBox");
//		effectCheck.setPreferredSize(new java.awt.Dimension(180, 22));
//		effectCheck.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0019")/*@res "含未生效报销单"*/);		
		effectCheck.setSelected(false);
		normalCondCompList.add(effectCheck);
		UILabel label = getShowLabel0(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0019")/*@res "含未生效报销单"*/);
		label.setName("unSettledLbl");
		normalCondCompList.add(label);
        addComponent(EFFECT_CHECK, effectCheck);

		return normalCondCompList;
	}

    @Override
    protected List<Integer> getPanelHeightList() {
	    List<Integer> list = new ArrayList<Integer>(3);
        list.add(Integer.valueOf(325));
        list.add(Integer.valueOf(145));
        list.add(Integer.valueOf(140));
        return list;
    }

    /**
	 * 覆写父类方法，执行必要的业务校验
	 */
	@Override
	protected String doBusiCheck() {
		// ①执行父类校验
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}

		try {
			errMsg = "";

			ReportQueryCondVO queryCondVO = (ReportQueryCondVO) getReportQueryCondVO();
			// ②按账龄分析时，必须选择账龄方案
			if (IErmReportAnalyzeConstants.getACC_ANA_MODE_AGE().equals(queryCondVO.getAnaMode())
					&& StringUtils.isEmpty(queryCondVO.getAccAgePlan())) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0020")/*@res "按账龄分析必须选择账龄方案！"*/;
			}

			// ③财务组织不允许为空
			String[] pk_orgs = queryCondVO.getPk_orgs();
			if (pk_orgs == null || pk_orgs.length < 1 || StringUtils.isEmpty(pk_orgs[0])) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0021")/*@res "\r\n财务组织不允许为空"*/;
			}
		} catch (BusinessException e) {
			errMsg = e.getMessage();
		}

		return StringUtils.isEmpty(errMsg) ? null : errMsg;
	}

	/**
	 * 功能：设置常用查询条件VO
	 */
    @Override
    protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		UIComboBox tempComboBox = null;
		UIRefPane tempRefPane = null;

		// 账龄方案
		tempRefPane = (UIRefPane) getComponent(ACC_AGE_PLAN_REF);
		if (tempRefPane != null) {
			qryCondVO.setAccAgePlan(tempRefPane.getRefPK());
		}

		// 分析模式：按日期、按账龄
		tempComboBox = (UIComboBox) getComponent(ANA_MODE_COMB);
		if (tempComboBox != null) {
			qryCondVO.setAnaMode((String) tempComboBox.getSelectdItemValue());
		}

		// 查询方式：最终余额、点余额
		tempComboBox = (UIComboBox) getComponent(ANA_PATTERN_COMB);
		if (tempComboBox != null) {
			qryCondVO.setAnaPattern((String) tempComboBox.getSelectdItemValue());
		}

		// 分析日期
		tempComboBox = ((UIComboBox) getComponent(ANA_DATE_COMB));
		if (tempComboBox != null) {
			qryCondVO.setAnaDate((String) tempComboBox.getSelectdItemValue());
		}

		// 截止日期
		UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
		tempRefPane = (UIRefPane) getComponent(DATELINE_REF);
		if (tempRefPane != null) {
			Object dateline = tempRefPane.getValueObj();
			String datelineDate = null;
			if (dateline == null) {
				datelineDate = busiDate.toLocalString();
			} else if (dateline instanceof UFDate) {
				datelineDate = ((UFDate) dateline).toLocalString();
			} else if (dateline.getClass().isArray()) {
				datelineDate = ((Object[]) dateline)[0].toString();
			} else {
				datelineDate = dateline.toString();
			}
			qryCondVO.setDateline(new UFDate(datelineDate, false));
		}

		// 币种
		qryCondVO.setPk_currency(((UIRefPane) getComponent(CURRENCY_REF)).getRefPK());
		// 单据状态
		qryCondVO.getUserObject().put(IErmReportAnalyzeConstants.INCLUDE_UNEFFECT,
				UFBoolean.valueOf(((UICheckBox) getComponent(EFFECT_CHECK)).isSelected()));

		// 财务组织
		qryCondVO.setPk_orgs(getPk_org());
		qryCondVO.setPk_group(ReportUiUtil.getPK_group());
		ReportInitializeVO reportInitializeVO = (ReportInitializeVO) getReportInitializeVO().getParentVO();
		qryCondVO.setOwnModule(reportInitializeVO.getOwnmodule());
		qryCondVO.setModuleId(BXConstans.ERM_MODULEID);

	}

    @Override
	protected void resetUserReportQueryCondVO(IReportQueryCond queryCond) {
		ReportQueryCondVO queryCondVO = (ReportQueryCondVO) queryCond;
		((UIComboBox) getComponent(ANA_MODE_COMB)).setSelectedItem(queryCondVO.getAnaMode()); // 分析模式
		((UIRefPane) getComponent(ACC_AGE_PLAN_REF)).setPK(queryCondVO.getAccAgePlan()); // 账龄方案
		((UIComboBox) getComponent(ANA_DATE_COMB)).setSelectedItem(queryCondVO.getAnaDate()); // 分析日期
		((UIRefPane) getComponent(CURRENCY_REF)).setPK(queryCondVO.getPk_currency()); // 币种
		((UIRefPane) getComponent(FINANCIAL_ORG_REF)).setPKs(queryCondVO.getPk_orgs()); // 财务组织

		UIRefPane dateRef = null;
		if (getComponent(DATELINE_REF) != null) {
			dateRef = (UIRefPane) getComponent(DATELINE_REF); // 截止日期
//			dateRef.setValue(queryCondVO.getDateline().toLocalString());
	        UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
	        dateRef.setValue(currBusiDate.toLocalString());
		}

		UIComboBox comboBox = null;
		if (getComponent(ANA_PATTERN_COMB) != null) {
			comboBox = (UIComboBox) getComponent(ANA_PATTERN_COMB); // 分析方式：最终余额、点余额
			comboBox.setSelectedItem(queryCondVO.getAnaPattern());
		}

		// =================================
		setPk_org(queryCondVO.getPk_orgs());
		// =================================
	}

	
	@Override
	protected String[] getOrgRelationField() {
		return new String[]{ CURRENCY_REF,ACC_AGE_PLAN_REF };
	}

	/**
	 * 分析模式变更监听器<br>
	 *
	 * @since V60<br>
	 */
	class AnaModeChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
			Component comp = null;
			if (e.getStateChange() == ItemEvent.SELECTED && IErmReportAnalyzeConstants.getACC_ANA_MODE_AGE().equals(e.getItem())) {
				// 按账龄分析
				comp = getComponent(ACC_AGE_PLAN_REF); // 账龄方案
				if (comp != null) {
					comp.setEnabled(true);
				}

				comp = getComponent(ANA_PATTERN_COMB); // 分析方式
				if (comp != null) {
					comp.setEnabled(true);
					((UIComboBox) comp).setSelectedIndex(0);
					getComponent(DATELINE_REF).setEnabled(false); // 截止日期
				}
			} else if (e.getStateChange() == ItemEvent.SELECTED && IErmReportAnalyzeConstants.getACC_ANA_MODE_DATE().equals(e.getItem())) {
				// 按日期分析
				comp = getComponent(ACC_AGE_PLAN_REF); // 账龄方案
				if (comp != null) {
					comp.setEnabled(false);
				}

				comp = getComponent(ANA_PATTERN_COMB); // 分析方式
				if (comp != null) {
					comp.setEnabled(false);
					((UIComboBox) comp).setSelectedIndex(0);
					getComponent(DATELINE_REF).setEnabled(false); // 截止日期
				}
			}
		}
	}

	class ForDatelineListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
			Component comp = getComponent(DATELINE_REF); // 截止日期
			if (e.getStateChange() == ItemEvent.SELECTED
					&& (IErmReportAnalyzeConstants.getACC_ANA_PATTERN_POINT().equals(e.getItem())
							|| IErmReportAnalyzeConstants.getACC_ANA_TYP_DEADLINE().equals(e.getItem()))) {
				comp.setEnabled(true);
			} else if (e.getStateChange() == ItemEvent.SELECTED
					&& (IErmReportAnalyzeConstants.getACC_ANA_PATTERN_FINAL().equals(e.getItem())
							|| IErmReportAnalyzeConstants.getACC_ANA_TYP_SETTLE().equals(e.getItem()))) {
				comp.setEnabled(false);
			}
		}
	}
}

