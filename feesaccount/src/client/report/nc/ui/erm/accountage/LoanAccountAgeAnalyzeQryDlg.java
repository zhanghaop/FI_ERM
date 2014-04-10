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
 * ������������������Ի���
 * </p>
 * 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2011-2-24 ����05:06:41
 */
@SuppressWarnings("restriction")
public class LoanAccountAgeAnalyzeQryDlg extends ErmAbstractReportBaseDlg {


	private static final long serialVersionUID = 2992805299331282686L;
	
	private static final String DATELINE_REF = "deadlineRef"; // ��ֹ����
	private static final String ACC_AGE_PLAN_REF = "accAgePlanRef"; // ���䷽��
	private static final String ANA_DATE_COMB = "anaDateComb"; // ��������
	private static final String ANA_PATTERN_COMB = "anaPatternComb"; // ������ʽ
	private static final String ANA_MODE_COMB = "anaModeComb"; // ����ģʽ
	private static final String EFFECT_CHECK = "effectCheck"; // ��Ч״̬

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
//        String fiorg = (String) WorkbenchEnvironment.getInstance().getClientCache(key); // ��Ա������֯
//        String pk_org = StringUtils.isEmpty(ReportUiUtil.getDefaultOrgUnit()) ? fiorg : ReportUiUtil.getDefaultOrgUnit();
        String pk_org = ErUiUtil.getReportDefaultOrgUnit();
        
		// ��ȡ��ǰҵ������
		String currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate().toStdString();
		
		// ��ֹ����
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0115")/*@res "��ֹ����"*/));
		UIRefPane datelineRef = new UIRefPane(RefConstant.REF_NODENAME_CALENDAR);
		datelineRef.setValue(currBusiDate);
		datelineRef.setEnabled(false); // Ĭ��Ϊ���ɱ༭
		normalCondCompList.add(datelineRef);
		addComponent(DATELINE_REF, datelineRef);

		// ���䷽��
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0013")/*@res "���䷽��"*/));
		UIRefPane accAgePlanRef = new UIRefPane(RefConstant.REF_NODENAME_ACCAGEPLAN);
		if(pk_org!=null){
			accAgePlanRef.setPk_org(pk_org);
		}
		normalCondCompList.add(accAgePlanRef);
		addComponent(ACC_AGE_PLAN_REF, accAgePlanRef);

		// ����
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0014")/*@res "�ҡ�����"*/));
		UIRefPane currencyRef = new UIRefPane(RefConstant.REF_NODENAME_CURRENCY);
		if(pk_org!=null){
			currencyRef.setPk_org(pk_org);
		}	
		normalCondCompList.add(currencyRef);
		addComponent(CURRENCY_REF, currencyRef);

		// ����ģʽ�������ڡ�������
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0016")/*@res "����ģʽ"*/));
		UIComboBox anaModeComb = new UIComboBox();
		//Ŀǰ����ֻ��Ҫ��������
		anaModeComb.addItems(new String[] {
				IErmReportAnalyzeConstants.getACC_ANA_MODE_AGE()
				 });
		anaModeComb.addItemListener(new AnaModeChangeListener());
		normalCondCompList.add(anaModeComb);
		addComponent(ANA_MODE_COMB, anaModeComb);


		// �������ڣ������ա��������ڡ�������ڡ���Ч���ڡ�(��������/�ڿص�������)
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0017")/*@res "��������"*/));
		UIComboBox anaDateComb = new UIComboBox();
		anaDateComb.addItems(new String[] {
				IErmReportAnalyzeConstants.getACC_ANA_DATE_LASTPAYDATE(),
				IErmReportAnalyzeConstants.getACC_ANA_DATE_BILLDATE(),
				IErmReportAnalyzeConstants.getACC_ANA_DATE_AUDITDATE(),
				IErmReportAnalyzeConstants.getACC_ANA_DATE_EFFECTDATE() });

		normalCondCompList.add(anaDateComb);
		addComponent(ANA_DATE_COMB, anaDateComb);


		// ������ʽ�������������
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0018")/*@res "������ʽ"*/));
		UIComboBox anaPatternComb = new UIComboBox();
		anaPatternComb.addItems(new String[] {
				IErmReportAnalyzeConstants.getACC_ANA_PATTERN_FINAL() });
		anaPatternComb.addItemListener(new ForDatelineListener());
		normalCondCompList.add(anaPatternComb);
		addComponent(ANA_PATTERN_COMB, anaPatternComb);


		// ������֯
		normalCondCompList.add(getShowLabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000006")/*@res "������֯"*/));
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

		// ���ò�ѯ����ɲ�����֯
		if (pk_org != null) {
			setPk_org(new String[] { pk_org });
		}

		// ����δ��Ч������
		UICheckBox effectCheck= new UICheckBox();
		effectCheck.setName("unSettleChkBox");
//		effectCheck.setPreferredSize(new java.awt.Dimension(180, 22));
//		effectCheck.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0019")/*@res "��δ��Ч������"*/);		
		effectCheck.setSelected(false);
		normalCondCompList.add(effectCheck);
		UILabel label = getShowLabel0(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0019")/*@res "��δ��Ч������"*/);
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
	 * ��д���෽����ִ�б�Ҫ��ҵ��У��
	 */
	@Override
	protected String doBusiCheck() {
		// ��ִ�и���У��
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}

		try {
			errMsg = "";

			ReportQueryCondVO queryCondVO = (ReportQueryCondVO) getReportQueryCondVO();
			// �ڰ��������ʱ������ѡ�����䷽��
			if (IErmReportAnalyzeConstants.getACC_ANA_MODE_AGE().equals(queryCondVO.getAnaMode())
					&& StringUtils.isEmpty(queryCondVO.getAccAgePlan())) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0020")/*@res "�������������ѡ�����䷽����"*/;
			}

			// �۲�����֯������Ϊ��
			String[] pk_orgs = queryCondVO.getPk_orgs();
			if (pk_orgs == null || pk_orgs.length < 1 || StringUtils.isEmpty(pk_orgs[0])) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0021")/*@res "\r\n������֯������Ϊ��"*/;
			}
		} catch (BusinessException e) {
			errMsg = e.getMessage();
		}

		return StringUtils.isEmpty(errMsg) ? null : errMsg;
	}

	/**
	 * ���ܣ����ó��ò�ѯ����VO
	 */
    @Override
    protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		UIComboBox tempComboBox = null;
		UIRefPane tempRefPane = null;

		// ���䷽��
		tempRefPane = (UIRefPane) getComponent(ACC_AGE_PLAN_REF);
		if (tempRefPane != null) {
			qryCondVO.setAccAgePlan(tempRefPane.getRefPK());
		}

		// ����ģʽ�������ڡ�������
		tempComboBox = (UIComboBox) getComponent(ANA_MODE_COMB);
		if (tempComboBox != null) {
			qryCondVO.setAnaMode((String) tempComboBox.getSelectdItemValue());
		}

		// ��ѯ��ʽ�������������
		tempComboBox = (UIComboBox) getComponent(ANA_PATTERN_COMB);
		if (tempComboBox != null) {
			qryCondVO.setAnaPattern((String) tempComboBox.getSelectdItemValue());
		}

		// ��������
		tempComboBox = ((UIComboBox) getComponent(ANA_DATE_COMB));
		if (tempComboBox != null) {
			qryCondVO.setAnaDate((String) tempComboBox.getSelectdItemValue());
		}

		// ��ֹ����
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

		// ����
		qryCondVO.setPk_currency(((UIRefPane) getComponent(CURRENCY_REF)).getRefPK());
		// ����״̬
		qryCondVO.getUserObject().put(IErmReportAnalyzeConstants.INCLUDE_UNEFFECT,
				UFBoolean.valueOf(((UICheckBox) getComponent(EFFECT_CHECK)).isSelected()));

		// ������֯
		qryCondVO.setPk_orgs(getPk_org());
		qryCondVO.setPk_group(ReportUiUtil.getPK_group());
		ReportInitializeVO reportInitializeVO = (ReportInitializeVO) getReportInitializeVO().getParentVO();
		qryCondVO.setOwnModule(reportInitializeVO.getOwnmodule());
		qryCondVO.setModuleId(BXConstans.ERM_MODULEID);

	}

    @Override
	protected void resetUserReportQueryCondVO(IReportQueryCond queryCond) {
		ReportQueryCondVO queryCondVO = (ReportQueryCondVO) queryCond;
		((UIComboBox) getComponent(ANA_MODE_COMB)).setSelectedItem(queryCondVO.getAnaMode()); // ����ģʽ
		((UIRefPane) getComponent(ACC_AGE_PLAN_REF)).setPK(queryCondVO.getAccAgePlan()); // ���䷽��
		((UIComboBox) getComponent(ANA_DATE_COMB)).setSelectedItem(queryCondVO.getAnaDate()); // ��������
		((UIRefPane) getComponent(CURRENCY_REF)).setPK(queryCondVO.getPk_currency()); // ����
		((UIRefPane) getComponent(FINANCIAL_ORG_REF)).setPKs(queryCondVO.getPk_orgs()); // ������֯

		UIRefPane dateRef = null;
		if (getComponent(DATELINE_REF) != null) {
			dateRef = (UIRefPane) getComponent(DATELINE_REF); // ��ֹ����
//			dateRef.setValue(queryCondVO.getDateline().toLocalString());
	        UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
	        dateRef.setValue(currBusiDate.toLocalString());
		}

		UIComboBox comboBox = null;
		if (getComponent(ANA_PATTERN_COMB) != null) {
			comboBox = (UIComboBox) getComponent(ANA_PATTERN_COMB); // ������ʽ�������������
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
	 * ����ģʽ���������<br>
	 *
	 * @since V60<br>
	 */
	class AnaModeChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
			Component comp = null;
			if (e.getStateChange() == ItemEvent.SELECTED && IErmReportAnalyzeConstants.getACC_ANA_MODE_AGE().equals(e.getItem())) {
				// ���������
				comp = getComponent(ACC_AGE_PLAN_REF); // ���䷽��
				if (comp != null) {
					comp.setEnabled(true);
				}

				comp = getComponent(ANA_PATTERN_COMB); // ������ʽ
				if (comp != null) {
					comp.setEnabled(true);
					((UIComboBox) comp).setSelectedIndex(0);
					getComponent(DATELINE_REF).setEnabled(false); // ��ֹ����
				}
			} else if (e.getStateChange() == ItemEvent.SELECTED && IErmReportAnalyzeConstants.getACC_ANA_MODE_DATE().equals(e.getItem())) {
				// �����ڷ���
				comp = getComponent(ACC_AGE_PLAN_REF); // ���䷽��
				if (comp != null) {
					comp.setEnabled(false);
				}

				comp = getComponent(ANA_PATTERN_COMB); // ������ʽ
				if (comp != null) {
					comp.setEnabled(false);
					((UIComboBox) comp).setSelectedIndex(0);
					getComponent(DATELINE_REF).setEnabled(false); // ��ֹ����
				}
			}
		}
	}

	class ForDatelineListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
			Component comp = getComponent(DATELINE_REF); // ��ֹ����
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

