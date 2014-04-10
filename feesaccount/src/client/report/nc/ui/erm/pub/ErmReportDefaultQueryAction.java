package nc.ui.erm.pub;

import java.awt.Container;
import java.util.List;
import nc.itf.fipub.report.IFipubReportQryDlg;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.report.AbsReportQueryAction;
import nc.ui.pub.beans.UIDialogEvent;
import nc.ui.pub.beans.UIDialogListener;
import nc.utils.fipub.FipubReportResource;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.PubConstData;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.querytemplate.TemplateInfo;
import com.ufida.dataset.IContext;
import com.ufida.iufo.table.drill.ReportDrillItem;
import com.ufida.iufo.table.drill.SimpleRowDataParam;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.base.FreeReportDrillParam;
import com.ufida.report.anareport.model.AbsAnaReportModel;

/**
 * <p>
 * 报销管理查询模板对话框 继承次类，需要传入对应的setM_iSysCode
 * 不同的单据传入不同的数值既可,这里loandlg为功能的对话框模板，对于查询模板以及下面的选项需要置入。 而有上角的内容需要从表中读取
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-9-19 下午06:58:51
 */
public class ErmReportDefaultQueryAction extends AbsReportQueryAction implements
		PubConstData, UIDialogListener {

	/**
	 * 设置报表穿透参数<br>
	 */
	public IQueryCondition doQueryByDrill(Container parent, IContext context,
			AbsAnaReportModel reportModel, FreeReportDrillParam drillParam) {

		IQueryCondition srcCondition = drillParam.getSrcCondition();
		if (srcCondition == null || ((BaseQueryCondition) srcCondition).getUserObject() == null) {
			return new BaseQueryCondition(false);
		}

		ReportQueryCondVO qryCondVO = (ReportQueryCondVO) ((BaseQueryCondition) srcCondition)
				.getUserObject();

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
					if (drillItemVaule != null && !"".equals(drillItemVaule) && !qryObjList.get(i).getValues().contains(drillItemVaule)) {
						qryObjList.get(i).getValues().add(drillItemVaule);
					}
				}
			}

			// 处理穿透时的币种
			ReportInitializeVO initHeadVO = (ReportInitializeVO) qryCondVO.getRepInitContext().getParentVO();
			if (IPubReportConstants.ACCOUNT_FORMAT_FOREIGN.equals(initHeadVO.getReportformat())) {
				// 外币金额式查询
				for (SimpleRowDataParam traceData : traceDatas) {
					drillItemVaule = traceData.getValue("pk_currtype");
					if (drillItemVaule != null && !"".equals(drillItemVaule)) {
						qryCondVO.setPk_currency(drillItemVaule.toString());
						break;
					}
				}
			}

			((BaseQueryCondition) drillParam.getSrcCondition()).setUserObject(qryCondVO);
		}


		return drillParam.getSrcCondition();
	}

	protected IFipubReportQryDlg getQryDlg(Container parent, IContext context,
			String nodeCode, int iSysCode, String title) {
		if (dlg == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			String defaultOrgUnit = BXUiUtil.getPK_group(); // 获得对应的查询模板集团
			tempinfo.setPk_Org(defaultOrgUnit); // 获得对应的查询模板的业务单元
			tempinfo.setCurrentCorpPk(defaultOrgUnit);
			tempinfo.setFunNode(nodeCode);
			tempinfo.setUserid(BXUiUtil.getPk_user());

			ErmReportQryDlg ermReportQryDlg = new ErmReportQryDlg(parent, context, nodeCode, iSysCode, tempinfo, FipubReportResource.getQryCondSetLbl());
			ermReportQryDlg.setSize(BXConstans.WINDOW_WIDTH, BXConstans.WINDOW_HEIGHT);
			ermReportQryDlg.addUIDialogListener(this);

			dlg = ermReportQryDlg;
		}

		return dlg;
	}

	public void dialogClosed(UIDialogEvent event) {
	}

	protected int getSysCode() {
		/** 系统标识号:3——应收；4——应付；5——报销管理 */
		return 5;
	}

}
