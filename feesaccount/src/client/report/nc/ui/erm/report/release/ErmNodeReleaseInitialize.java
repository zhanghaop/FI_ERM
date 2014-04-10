package nc.ui.erm.report.release;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fipub.report.IPubReportConstants;
import nc.ui.fipub.report.BaseReportNodeInitialize;
import nc.ui.fipub.report.ReportReleaseFuncSelectComp;
import nc.ui.fipub.report.ReportReleaseMenuSelectComp;
import nc.ui.fipub.report.ReportReleaseSystemSetComp;
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

public class ErmNodeReleaseInitialize extends BaseReportNodeInitialize {
	private static final long serialVersionUID = 1L;

	// 账表初始化向导组件
	private ErmReleaseReportTypeSelectComp step1Comp = null;
	private ErmReleaseQryObjSelectComp step2Comp = null;
	private ReportReleaseFuncSelectComp step3Comp = null;
	private ReportReleaseMenuSelectComp step4Comp = null;
	private ReportReleaseSystemSetComp step5Comp = null;

	private List<WizardStep> stepList = new ArrayList<WizardStep>();

	/**
	 * 默认构造器<br>
	 */
	public ErmNodeReleaseInitialize() {
		super(IPubReportConstants.MODULE_ERM);
	}

	public String getTitle() {
		return FipubReportResource.getInitReportTitleLbl(0);
	}

	protected List<WizardStep> getWizardSteps() {
		if (stepList.size() == 0) {
			IWizardStepValidator validator = new NextSetpValidator();

			// 步骤一：选择帐表类型
			WizardStep step1 = new WizardStep();
			step1.setTitle(FipubReportResource.getInitReportTitleLbl(1));
			step1.setDescription(FipubReportResource.getInitReportDescLbl(1));
			step1.setComp(getStep1Comp());
			step1.addValidator(validator);
			stepList.add(step1);

			// 步骤二：选择查询对象
			WizardStep step2 = new WizardStep();
			step2.setTitle(FipubReportResource.getInitReportTitleLbl(2));
			step2.setDescription(FipubReportResource.getInitReportDescLbl(2));
			step2.setComp(getStep2Comp());
			step2.addValidator(validator);
			step2.addListener(new Go2Step2Listener());
			stepList.add(step2);

			// 步骤三：发布为功能节点
			WizardStep step3 = new WizardStep();
			step3.setTitle(FipubReportResource.getInitReportTitleLbl(3));
			step3.setDescription(FipubReportResource.getInitReportDescLbl(3));
			step3.setComp(getStep3Comp());
			step3.addValidator(validator);
			stepList.add(step3);

			// 步骤四：发布为菜单
			WizardStep step4 = new WizardStep();
			step4.setTitle(FipubReportResource.getInitReportTitleLbl(4));
			step4.setDescription(FipubReportResource.getInitReportDescLbl(4));
			step4.setComp(getStep4Comp());
			step4.addValidator(validator);
			stepList.add(step4);

			// 步骤五：系统设置
			WizardStep step5 = new WizardStep();
			step5.setTitle(FipubReportResource.getInitReportTitleLbl(5));
			step5.setDescription(FipubReportResource.getInitReportDescLbl(5));
			step5.setComp(getStep5Comp());
			step5.addValidator(validator);
			stepList.add(step5);
		}

		return stepList;
	}

	protected void postInit() {
		doQuery();
	}

	protected boolean doModify() {
		return false;
	}

	protected AggReportInitializeVO getRepInitVO() {
		AggReportInitializeVO repInitVO = new AggReportInitializeVO();

		String reportType = getStep1Comp().getReportType(); // 账表类型
		List<?> qryObjList = getStep2Comp().getSelectQryObj(); // 查询对象
		String reportFormat = getStep2Comp().getReportFormat(); // 账页格式
		String showFormat = getStep2Comp().getShowFormat(); // 显示格式
//		int multiUnitShowMode = getStep2Comp().getMultiUnitShowMode(); // 多组织显示方式
		FuncRegisterVO funcRegisterVO = getStep3Comp().getFuncRegisterVO(); // 功能节点VO
		MenuItemVO menuItemVO = getStep4Comp().getMenuItemVO(); // 菜单节点VO
		int iCPT = getStep5Comp().iCreatePhysicalTable(); // 是否创建物理表
		String publishType = getStep5Comp().getPublishType(); // 发布类型：查询、报表
		UFBoolean iShowQuickQueryPane = getStep5Comp().iShowQuickQueryPane(); // 是否显示快速查询面板

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
		funcRegisterVO.setFuncode(menuItemVO.getMenuitemcode()); // 设置功能编码
		funcRegisterVO.setFun_name(menuItemVO.getMenuitemname()); // 设置功能名称
		menuItemVO.setFuncode(funcRegisterVO.getFuncode()); // 将菜单与功能关联起来
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
	 * 私有校验器<br>
	 *
	 * @author 连树国<br>
	 * @since V60<br>
	 */
	class NextSetpValidator implements IWizardStepValidator {

		public void validate(JComponent comp, WizardModel model)
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
		 * 向导第一步校验<br>
		 *
		 * @throws WizardStepValidateException<br>
		 */
		void doStep1Check() throws WizardStepValidateException {
			if (StringUtils.isEmpty(getStep1Comp().getReportType())) {
				// 请先选择账表类型！
				WizardStepValidateException e = new WizardStepValidateException();
				e.addMsg("step11", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0033")/*@res "请先选择账表类型！"*/);
				throw e;
			}
		}

		/**
		 * 向导第二步校验<br>
		 *
		 * @throws WizardStepValidateException<br>
		 */
		void doStep2Check() throws WizardStepValidateException {
			List<Object> qryObjList = getStep2Comp().getSelectQryObj();
			if (qryObjList.size() == 0) {
				// 至少选择一个查询对象！
				WizardStepValidateException e = new WizardStepValidateException();
				e.addMsg("step21", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0034")/*@res "至少选择一个查询对象！"*/);
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

			// 查询对象不能重复！
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg("step22", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0035")/*@res "查询对象不能重复！"*/);
			throw e;
		}

		/**
		 * 向导第三步校验<br>
		 *
		 * @throws WizardStepValidateException<br>
		 */
		void doStep3Check() throws WizardStepValidateException {
			WizardStepValidateException e = null;
			if (StringUtils.isEmpty(getStep3Comp().getNewFuncCode())) {
				e = new WizardStepValidateException();
				e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0036")/*@res "请输入功能节点编码！"*/);
				// throw e;
			}

			if (StringUtils.isEmpty(getStep3Comp().getNewFuncName())) {
				e = new WizardStepValidateException();
				e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0037")/*@res "请输入功能节点名称！"*/);
				// throw e;
			}
		}

		/**
		 * 向导第三步校验<br>
		 *
		 * @throws WizardStepValidateException<br>
		 */
		void doStep4Check() throws WizardStepValidateException {
			WizardStepValidateException e = null;
			if (StringUtils.isEmpty(getStep4Comp().getNewMenuItemCode())) {
				e = new WizardStepValidateException();
				e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0038")/*@res "请选择菜单目录！"*/);
				throw e;
			}

			if (StringUtils.isEmpty(getStep4Comp().getNewMenuItemName())) {
				e = new WizardStepValidateException();
				e.addMsg("step31", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0039")/*@res "请输入菜单名称！"*/);
				throw e;
			}
		}

		/**
		 * 向导第三步校验<br>
		 *
		 * @throws WizardStepValidateException<br>
		 */
		void doStep5Check() throws WizardStepValidateException {

		}

	}

	class Go2Step2Listener implements IWizardStepListener {

		public void stepActived(WizardStepEvent event) throws WizardStepException {
			String currStep = event.getStep().getDescription();
			String preStep = event.getPreStep().getDescription();
			if (FipubReportResource.getInitReportDescLbl(2).equals(currStep)
					&& FipubReportResource.getInitReportDescLbl(1).equals(preStep)
					&& WizardStepEvent.EVENT_STEP_ACTIVED == event.getType()) {
				// step1→step2
				getStep2Comp().getUseableQryObj();
			}
		}

		public void stepDisactived(WizardStepEvent event) throws WizardStepException {
		}

	}

}

// /:~