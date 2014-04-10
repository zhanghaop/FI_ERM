package nc.ui.erm.accountage;

import java.awt.Container;
import java.util.List;

import nc.itf.fipub.report.IFipubReportQryDlg;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.report.AbsReportQueryAction;
import nc.ui.pub.beans.UIDialog;
import nc.utils.fipub.FipubReportResource;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.querytemplate.TemplateInfo;

import com.ufida.dataset.IContext;
import com.ufida.iufo.table.drill.ReportDrillItem;
import com.ufida.iufo.table.drill.SimpleRowDataParam;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.base.FreeReportDrillParam;
import com.ufida.report.anareport.model.AbsAnaReportModel;

/**
 * 借款账龄分析查询<br>
 *
 * @author liansg<br>
 * @since V60 2011-01-20<br>
 */
public class LoanAccountAgeAnalyzeAryAction extends AbsReportQueryAction {

	private static final String TITLE2 = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0012")/*@res "日期期间设置"*/; // 对话框标题
	private DateDlg dateDlg = null; // 日期期间设置对话框

	public IQueryCondition doQueryAction(Container parent, IContext context,
			AbsAnaReportModel reportModel, IQueryCondition oldCondition) {

		BaseQueryCondition qryCondition = (BaseQueryCondition) super.doQueryAction(parent, context,
				reportModel, oldCondition);

		if (!qryCondition.isContinue()) {
			// 取消查询
			return new BaseQueryCondition(false);
		}

		ReportQueryCondVO qryCondVO = (ReportQueryCondVO) qryCondition.getUserObject();

		if (IErmReportAnalyzeConstants.ACC_ANA_MODE_DATE.equals(qryCondVO.getAnaMode())) {
			// 按日期分析
			if (getDateDlg(parent, TITLE2).showModal() != UIDialog.ID_OK) {
				// 取消查询
				return new BaseQueryCondition(false);
			}

			Object[][] datas = getDateDlg(parent, TITLE2).getData();
			qryCondVO.setDatas(datas);

			// 再次将查询条件VO放入BaseQueryCondition
			qryCondition.setUserObject(qryCondVO);
		}

		return qryCondition;
	}


	/**
	 * 根据发布的节点号，构造查询对话框<br>
	 *
	 * @param parent 父组件<br>
	 * @param context IUFO上下文环境<br>
	 * @param nodeCode 节点编号<br>
	 * @param iSysCode 系统标识<br>
	 * @param ti 模板信息<br>
	 * @param title 对话框标题<br>
	 * @return IFipubReportQryDlg<br>
	 */
	protected IFipubReportQryDlg getQryDlg(Container parent, IContext context, String nodeCode,
			int iSysCode, String title) {
		if (dlg == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			// FIXME 暂时用集团代替，注意不能为空，为空找不到查询模板
			String defaultOrgUnit = BXUiUtil.getPK_group(); // 获得对应的查询模板集团
			tempinfo.setPk_Org(defaultOrgUnit); // 获得对应的查询模板的业务单元
			tempinfo.setCurrentCorpPk(defaultOrgUnit);
			tempinfo.setFunNode(nodeCode);
			tempinfo.setUserid(BXUiUtil.getPk_user());
			dlg = new LoanAccountAgeAnalyzeQryDlg(parent, context, nodeCode, iSysCode, tempinfo, FipubReportResource.getQryCondSetLbl());
		}

		return dlg;
	}

	protected DateDlg getDateDlg(Container parent, String title) {
		if (dateDlg == null) {
			dateDlg = new DateDlg(parent, title);
		}
		return dateDlg;
	}


	protected int getSysCode() {
		return 5;
	}
	/**
	 * 设置报表穿透参数，并执行穿透<br>
	 */
	public IQueryCondition doQueryByDrill(Container parent, IContext context,
			AbsAnaReportModel reportModel, FreeReportDrillParam drillParam) {

		IQueryCondition srcCondition = drillParam.getSrcCondition();
		if (srcCondition == null || ((BaseQueryCondition) srcCondition).getUserObject() == null) {
			// 取消查询
			return new BaseQueryCondition(false);
		}

		ReportQueryCondVO qryCondVO = (ReportQueryCondVO) ((BaseQueryCondition) srcCondition).getUserObject();

		List<QryObj> qryObjList = qryCondVO.getQryObjs();
		if (drillParam.getDrillRule() != null && drillParam.getDrillRule().getDrillItem() != null) {
			for (QryObj qryObj : qryObjList) {
				qryObj.getValues().clear();
			}

			String drillItemName = null;
			Object drillItemVaule = null;
			SimpleRowDataParam[] traceDatas = drillParam.getTraceDatas();
			// 获取穿透规则子项
			ReportDrillItem[] drillItems = drillParam.getDrillRule().getDrillItem();
			for (SimpleRowDataParam traceData : traceDatas) {
				for (int i = 0; i < drillItems.length; i++) {
					drillItemName = drillItems[i].getConditionName();
					drillItemVaule = traceData.getValue(drillItemName);
					if (drillItemVaule != null && !"".equals(drillItemVaule)) {
						qryObjList.get(i).getValues().add(drillItemVaule);
					}
				}
			}

			((BaseQueryCondition) drillParam.getSrcCondition()).setUserObject(qryCondVO);
		}

		return drillParam.getSrcCondition();
	}

}

