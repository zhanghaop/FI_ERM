package nc.ui.erm.report.release;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fipub.report.IPubReportConstants;
import nc.ui.fipub.report.BaseReportNodeInitialize;
import nc.ui.fipub.report.ReportReleaseFuncSelectComp;
import nc.ui.fipub.report.ReportReleaseMenuSelectComp;
import nc.ui.fipub.report.ReportReleaseSystemSetComp;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.utils.fipub.FipubReportResource;
import nc.vo.fipub.report.AggReportInitializeVO;
import nc.vo.fipub.report.QueryObjVO;
import nc.vo.fipub.report.ReportInitializeItemVO;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.sm.funcreg.MenuItemVO;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class ErmNodeReleaseInitialize extends BaseReportNodeInitialize {
    private static final long serialVersionUID = 1L;

    // �˱��ʼ�������
    private ErmReleaseReportTypeSelectComp step1Comp = null;
    private ErmReleaseQryObjSelectComp step2Comp = null;
    private ReportReleaseFuncSelectComp step3Comp = null;
    private ReportReleaseMenuSelectComp step4Comp = null;
    private ReportReleaseSystemSetComp step5Comp = null;

    private List<WizardStep> stepList = new ArrayList<WizardStep>();

	/**
	 * Ĭ�Ϲ�����<br>
	 */
	public ErmNodeReleaseInitialize() {
		super(IPubReportConstants.MODULE_ERM);
		m_btnInitialize.setCode("Init");
		m_btnDelete.setCode("Delete");
	}

    @Override
    public String getTitle() {
        return FipubReportResource.getInitReportTitleLbl(0);
    }

    @Override
    protected List<WizardStep> getWizardSteps() {

        stepList.clear();
        IWizardStepValidator validator = new NextSetpValidator();

        // ����һ��ѡ���ʱ�����
        WizardStep step1 = new WizardStep();
        step1.setTitle(FipubReportResource.getInitReportTitleLbl(1));
        step1.setDescription(FipubReportResource.getInitReportDescLbl(1));
        step1.setComp(getStep1Comp());
        step1.addValidator(validator);
        stepList.add(step1);

        // �������ѡ���ѯ����
        WizardStep step2 = new WizardStep();
        step2.setTitle(FipubReportResource.getInitReportTitleLbl(2));
        step2.setDescription(FipubReportResource.getInitReportDescLbl(2));
        step2.setComp(getStep2Comp());
        step2.addValidator(validator);
        step2.addListener(new Go2Step2Listener());
        stepList.add(step2);

        // ������������Ϊ���ܽڵ�
        WizardStep step3 = new WizardStep();
        step3.setTitle(FipubReportResource.getInitReportTitleLbl(3));
        step3.setDescription(FipubReportResource.getInitReportDescLbl(3));
        step3.setComp(getStep3Comp());
        step3.addValidator(validator);
        stepList.add(step3);

        // �����ģ�����Ϊ�˵�
        WizardStep step4 = new WizardStep();
        step4.setTitle(FipubReportResource.getInitReportTitleLbl(4));
        step4.setDescription(FipubReportResource.getInitReportDescLbl(4));
        step4.setComp(getStep4Comp());
        step4.addValidator(validator);
        stepList.add(step4);

        // �����壺ϵͳ����
        WizardStep step5 = new WizardStep();
        step5.setTitle(FipubReportResource.getInitReportTitleLbl(5));
        step5.setDescription(FipubReportResource.getInitReportDescLbl(5));
        step5.setComp(getStep5Comp());
        step5.addValidator(validator);
        stepList.add(step5);

        return stepList;
    }

    @Override
    protected void postInit() {
        doQuery(false);
    }

    protected boolean doModify() {
        return false;
    }

    @Override
    protected boolean openReportInitializeDlg() {
        getWizardDlg().getModel().gotoStepForwardNoValidate(0);
        int result = getWizardDlg().showModal();
        if (result != UIDialog.ID_OK) {
            Logger.info("ȡ�����˱��ʼ��������");
            return false;
        }
        return true;
    }
    
    @Override
    protected AggReportInitializeVO getRepInitVO() {
        AggReportInitializeVO repInitVO = new AggReportInitializeVO();

        String reportType = getStep1Comp().getReportType(); // �˱�����
        List<?> qryObjList = getStep2Comp().getSelectQryObj(); // ��ѯ����
        String reportFormat = getStep2Comp().getReportFormat(); // ��ҳ��ʽ
        String showFormat = getStep2Comp().getShowFormat(); // ��ʾ��ʽ
        //		int multiUnitShowMode = getStep2Comp().getMultiUnitShowMode(); // ����֯��ʾ��ʽ
        FuncRegisterVO funcRegisterVO = getStep3Comp().getFuncRegisterVO(); // ���ܽڵ�VO
        MenuItemVO menuItemVO = getStep4Comp().getMenuItemVO(); // �˵��ڵ�VO
        int iCPT = getStep5Comp().iCreatePhysicalTable(); // �Ƿ񴴽������
        String publishType = getStep5Comp().getPublishType(); // �������ͣ���ѯ������
        UFBoolean iShowQuickQueryPane = getStep5Comp().iShowQuickQueryPane(); // �Ƿ���ʾ���ٲ�ѯ���

        String pk_group = WorkbenchEnvironment.getInstance().getLoginUser().getPk_group();
        String pk_org = WorkbenchEnvironment.getInstance().getLoginUser().getPk_org();
        String userId = WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
        UFDateTime ts = WorkbenchEnvironment.getServerTime();

        ReportInitializeVO headVO = new ReportInitializeVO();
        headVO.setReporttype(reportType);
        headVO.setReportformat(reportFormat);
        headVO.setShowformat(showFormat);
        // headVO.setMultiunitshowmode(multiUnitShowMode);
        headVO.setOwnmodule(getOwnModule());
        headVO.setNode_code(menuItemVO.getMenuitemcode());
        headVO.setNode_name(menuItemVO.getMenuitemname());
        headVO.setCreatetable(iCPT);
        headVO.setPublishtype(publishType);
        headVO.setIsshowquickqrypane(iShowQuickQueryPane);
        headVO.setPk_group(pk_group);
        headVO.setPk_org(pk_org);
        headVO.setCreator(userId);
        headVO.setTs(ts);
        headVO.setDr(0);
        headVO.setStatus(VOStatus.NEW);
        funcRegisterVO.setFuncode(menuItemVO.getMenuitemcode()); // ���ù��ܱ���
        funcRegisterVO.setFun_name(menuItemVO.getMenuitemname()); // ���ù�������
        menuItemVO.setFuncode(funcRegisterVO.getFuncode()); // ���˵��빦�ܹ�������
        headVO.setFuncRegisterVO(funcRegisterVO);
        headVO.setMenuItemVO(menuItemVO);
        repInitVO.setParentVO(headVO);

        QueryObjVO qryObjVO = null;
        ReportInitializeItemVO itemVO = null;
        List<ReportInitializeItemVO> itemVOList = new ArrayList<ReportInitializeItemVO>();
        for (int i = 0; i < qryObjList.size(); i += 2) {
            qryObjVO = (QueryObjVO) qryObjList.get(i);
            itemVO = new ReportInitializeItemVO();
            itemVO.setQry_objtablename(qryObjVO.getQry_objtablename());
            itemVO.setQry_objfieldname(qryObjVO.getQry_objfieldname());
            itemVO.setDsp_objname(qryObjVO.getDsp_objname());
            itemVO.setQry_objdatatype(qryObjVO.getQry_objdatatype());
            itemVO.setBd_refname(qryObjVO.getBd_refname());
            itemVO.setBd_mdid(qryObjVO.getBd_mdid());
            itemVO.setBd_tablename(qryObjVO.getBd_tablename());
            itemVO.setBd_pkfield(qryObjVO.getBd_pkfield());
            itemVO.setBd_codefield(qryObjVO.getBd_codefield());
            itemVO.setBd_namefield(qryObjVO.getBd_namefield());
            itemVO.setDsp_order((Integer) qryObjList.get(i + 1));
            itemVO.setBillfieldname(qryObjVO.getBillfieldname());
            itemVO.setTallyfieldname(qryObjVO.getTallyfieldname());
            itemVO.setBalfieldname(qryObjVO.getBalfieldname());
            itemVO.setCreator(userId);
            itemVO.setResid(qryObjVO.getResid());
            itemVO.setTs(ts);
            itemVO.setDr(0);
            itemVO.setStatus(VOStatus.NEW);
            itemVOList.add(itemVO);
        }

        repInitVO.setChildrenVO(itemVOList.toArray(new ReportInitializeItemVO[0]));
        //        step3Comp = new ReportReleaseFuncSelectComp();
        //        wizardDlg.getModel().getSteps().get(2).setComp(step3Comp);
        //        step4Comp = new ReportReleaseMenuSelectComp();
        //        wizardDlg.getModel().getSteps().get(3).setComp(step4Comp);

        step3Comp = null;
        step4Comp = null;
        return repInitVO;
    }

    private ErmReleaseReportTypeSelectComp getStep1Comp() {
        if (step1Comp == null) {
            step1Comp = new ErmReleaseReportTypeSelectComp();
        }
        return step1Comp;
    }

    private ErmReleaseQryObjSelectComp getStep2Comp() {
        if (step2Comp == null) {
            step2Comp = new ErmReleaseQryObjSelectComp();
        }
        return step2Comp;
    }

    private ReportReleaseFuncSelectComp getStep3Comp() {
        if (step3Comp == null) {
            step3Comp = new ReportReleaseFuncSelectComp();
        }
        return step3Comp;
    }

    private ReportReleaseMenuSelectComp getStep4Comp() {
        if (step4Comp == null) {
            step4Comp = new ReportReleaseMenuSelectComp();
        }
        return step4Comp;
    }

    private ReportReleaseSystemSetComp getStep5Comp() {
        if (step5Comp == null) {
            step5Comp = new ReportReleaseSystemSetComp();
        }
        return step5Comp;
    }

    /**
     * ˽��У����<br>
     *
     * @author ������<br>
     * @since V60<br>
     */
    class NextSetpValidator implements IWizardStepValidator {

        @Override
        public void validate(final JComponent comp, final WizardModel model)
        throws WizardStepValidateException {
            String currStep = model.getCurStep().getDescription();
            if (FipubReportResource.getInitReportDescLbl(1).equals(currStep)) {
                doStep1Check();
            } else if (FipubReportResource.getInitReportDescLbl(2).equals(currStep)) {
                doStep2Check();
            } else if (FipubReportResource.getInitReportDescLbl(3).equals(currStep)) {
                doStep3Check();
            } else if (FipubReportResource.getInitReportDescLbl(4).equals(currStep)) {
                doStep4Check();
            } else if (FipubReportResource.getInitReportDescLbl(5).equals(currStep)) {
                doStep5Check();
            }

        }

        /**
         * �򵼵�һ��У��<br>
         *
         * @throws WizardStepValidateException<br>
         */
        void doStep1Check() throws WizardStepValidateException {
            if (StringUtils.isEmpty(getStep1Comp().getReportType())) {
                // ����ѡ���˱����ͣ�
                WizardStepValidateException e = new WizardStepValidateException();
                e.addMsg("step11", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0033")/*@res "����ѡ���˱����ͣ�"*/);
                throw e;
            }
        }

        /**
         * �򵼵ڶ���У��<br>
         *
         * @throws WizardStepValidateException<br>
         */
        void doStep2Check() throws WizardStepValidateException {
            List<Object> qryObjList = getStep2Comp().getSelectQryObj();
            if (qryObjList.size() == 0) {
                // ����ѡ��һ����ѯ����
                WizardStepValidateException e = new WizardStepValidateException();
                e.addMsg("step21", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0034")/*@res "����ѡ��һ����ѯ����"*/);
                throw e;
            }

            if (qryObjList.size() == 1) {
                return;
            }

            Set<Object> qryObjSet = new HashSet<Object>();
            qryObjSet.addAll(qryObjList);

            if (qryObjSet.size() == qryObjList.size()) {
                return;
            }

            // ��ѯ�������ظ���
            WizardStepValidateException e = new WizardStepValidateException();
            e.addMsg("step22", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0035")/*@res "��ѯ�������ظ���"*/);
            throw e;
        }

        /**
         * �򵼵�����У��<br>
         *
         * @throws WizardStepValidateException<br>
         */
        void doStep3Check() throws WizardStepValidateException {
            WizardStepValidateException e = null;
            if (StringUtils.isEmpty(getStep3Comp().getNewFuncCode())) {
                e = new WizardStepValidateException();
                e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0036")/*@res "�����빦�ܽڵ���룡"*/);
                // throw e;
            }

            if (StringUtils.isEmpty(getStep3Comp().getNewFuncName())) {
                e = new WizardStepValidateException();
                e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0037")/*@res "�����빦�ܽڵ����ƣ�"*/);
                // throw e;
            }
        }

        /**
         * �򵼵�����У��<br>
         *
         * @throws WizardStepValidateException<br>
         */
        void doStep4Check() throws WizardStepValidateException {
            WizardStepValidateException e = null;
            if (StringUtils.isEmpty(getStep4Comp().getNewMenuItemCode())) {
                e = new WizardStepValidateException();
                e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0038")/*@res "��ѡ��˵�Ŀ¼��"*/);
                throw e;
            }

            if (StringUtils.isEmpty(getStep4Comp().getNewMenuItemName())) {
                e = new WizardStepValidateException();
                e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0039")/*@res "������˵����ƣ�"*/);
                throw e;
            }
        }

        /**
         * �򵼵�����У��<br>
         *
         * @throws WizardStepValidateException<br>
         */
        void doStep5Check() throws WizardStepValidateException {

        }

    }

    class Go2Step2Listener implements IWizardStepListener {

        @Override
        public void stepActived(final WizardStepEvent event) throws WizardStepException {
            String currStep = event.getStep().getDescription();
            String preStep = event.getPreStep().getDescription();
            if (FipubReportResource.getInitReportDescLbl(2).equals(currStep)
                    && FipubReportResource.getInitReportDescLbl(1).equals(preStep)
                    && (WizardStepEvent.EVENT_STEP_ACTIVED == event.getType())) {
                // step1��step2
                getStep2Comp().setReportType(getStep1Comp().getReportType());
                getStep2Comp().getUseableQryObj();
            }
        }

        @Override
        public void stepDisactived(final WizardStepEvent event) throws WizardStepException {
        }

    }

}

// /:~