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
 * ������������ѯ<br>
 *
 * @author liansg<br>
 * @since V60 2011-01-20<br>
 */
public class LoanAccountAgeAnalyzeAryAction extends AbsReportQueryAction {

	private static final String TITLE2 = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0012")/*@res "�����ڼ�����"*/; // �Ի������
	private DateDlg dateDlg = null; // �����ڼ����öԻ���

	public IQueryCondition doQueryAction(Container parent, IContext context,
			AbsAnaReportModel reportModel, IQueryCondition oldCondition) {

		BaseQueryCondition qryCondition = (BaseQueryCondition) super.doQueryAction(parent, context,
				reportModel, oldCondition);

		if (!qryCondition.isContinue()) {
			// ȡ����ѯ
			return new BaseQueryCondition(false);
		}

		ReportQueryCondVO qryCondVO = (ReportQueryCondVO) qryCondition.getUserObject();

		if (IErmReportAnalyzeConstants.ACC_ANA_MODE_DATE.equals(qryCondVO.getAnaMode())) {
			// �����ڷ���
			if (getDateDlg(parent, TITLE2).showModal() != UIDialog.ID_OK) {
				// ȡ����ѯ
				return new BaseQueryCondition(false);
			}

			Object[][] datas = getDateDlg(parent, TITLE2).getData();
			qryCondVO.setDatas(datas);

			// �ٴν���ѯ����VO����BaseQueryCondition
			qryCondition.setUserObject(qryCondVO);
		}

		return qryCondition;
	}


	/**
	 * ���ݷ����Ľڵ�ţ������ѯ�Ի���<br>
	 *
	 * @param parent �����<br>
	 * @param context IUFO�����Ļ���<br>
	 * @param nodeCode �ڵ���<br>
	 * @param iSysCode ϵͳ��ʶ<br>
	 * @param ti ģ����Ϣ<br>
	 * @param title �Ի������<br>
	 * @return IFipubReportQryDlg<br>
	 */
	protected IFipubReportQryDlg getQryDlg(Container parent, IContext context, String nodeCode,
			int iSysCode, String title) {
		if (dlg == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			// FIXME ��ʱ�ü��Ŵ��棬ע�ⲻ��Ϊ�գ�Ϊ���Ҳ�����ѯģ��
			String defaultOrgUnit = BXUiUtil.getPK_group(); // ��ö�Ӧ�Ĳ�ѯģ�弯��
			tempinfo.setPk_Org(defaultOrgUnit); // ��ö�Ӧ�Ĳ�ѯģ���ҵ��Ԫ
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
	 * ���ñ���͸��������ִ�д�͸<br>
	 */
	public IQueryCondition doQueryByDrill(Container parent, IContext context,
			AbsAnaReportModel reportModel, FreeReportDrillParam drillParam) {

		IQueryCondition srcCondition = drillParam.getSrcCondition();
		if (srcCondition == null || ((BaseQueryCondition) srcCondition).getUserObject() == null) {
			// ȡ����ѯ
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
			// ��ȡ��͸��������
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

