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
 * ���������ѯģ��Ի��� �̳д��࣬��Ҫ�����Ӧ��setM_iSysCode
 * ��ͬ�ĵ��ݴ��벻ͬ����ֵ�ȿ�,����loandlgΪ���ܵĶԻ���ģ�壬���ڲ�ѯģ���Լ������ѡ����Ҫ���롣 �����Ͻǵ�������Ҫ�ӱ��ж�ȡ
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-9-19 ����06:58:51
 */
public class ErmReportDefaultQueryAction extends AbsReportQueryAction implements
		PubConstData, UIDialogListener {

	/**
	 * ���ñ���͸����<br>
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
			// ��ȡ��͸��������
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

			// ����͸ʱ�ı���
			ReportInitializeVO initHeadVO = (ReportInitializeVO) qryCondVO.getRepInitContext().getParentVO();
			if (IPubReportConstants.ACCOUNT_FORMAT_FOREIGN.equals(initHeadVO.getReportformat())) {
				// ��ҽ��ʽ��ѯ
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
			String defaultOrgUnit = BXUiUtil.getPK_group(); // ��ö�Ӧ�Ĳ�ѯģ�弯��
			tempinfo.setPk_Org(defaultOrgUnit); // ��ö�Ӧ�Ĳ�ѯģ���ҵ��Ԫ
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
		/** ϵͳ��ʶ��:3����Ӧ�գ�4����Ӧ����5������������ */
		return 5;
	}

}
