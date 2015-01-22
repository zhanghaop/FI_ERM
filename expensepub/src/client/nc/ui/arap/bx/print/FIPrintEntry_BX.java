package nc.ui.arap.bx.print;

import nc.ui.erm.util.ErUiUtil;
import nc.ui.fi_print.data.IData;
import nc.ui.fi_print.data.PrintETL;
import nc.ui.fi_print.entry.FiPrintEntry;
import nc.ui.fi_print.entry.TempletDataSource;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.report.ReportBaseClass;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class FIPrintEntry_BX extends FiPrintEntry{
	
	public FIPrintEntry_BX(BillCardPanel billCardPanel) {
		super(billCardPanel);
		
		IData data = PrintETL_BX.ETL_NO_TOTAL(billCardPanel);
		
		addData(data);
	}


	
	public FIPrintEntry_BX() {
		super();
	}

	
	@Override
	public void batchPrint(AggregatedValueObject[] aggvos, BillCardPanel[] billCardPanels, String pk_corp, String nodeCode, String nodeKey) {
		if (pk_corp == null) {
			pk_corp = getPk_corp();
		}

		if (nodeCode == null && billCardPanels != null && billCardPanels.length > 0) {
			nodeCode = billCardPanels[0].getNodeKey();
		}

		int result = this.initTemplet(pk_corp, nodeCode, nodeKey);
		if (result == -1 || result == 0) {
			return;
		}

		getPrintEntry().beginBatchPrint();
		for (int i = 0; i < aggvos.length; i++) {
			if (i < billCardPanels.length) {
				init(aggvos[i], billCardPanels[i]);
			} else {
				init(aggvos[i], billCardPanels[0]);
			}
		}
		getPrintEntry().endBatchPrint();
	}
	
	/**
	 * 
	 * @param aggvo
	 * @param billCardPanel
	 */
	private void init(AggregatedValueObject aggvo, BillCardPanel billCardPanel) {
		if (aggvo == null) {
			return;
		}
		CircularlyAccessibleValueObject headvo = aggvo.getParentVO();
		CircularlyAccessibleValueObject[] bodyvos = aggvo.getChildrenVO();
		init(headvo, bodyvos, billCardPanel);

	}
	
	/**
	 * ��ӡ��ڳ�ʼ��
	 * 
	 * @param headvo ��ͷ����
	 * @param bodyvos ��������
	 * @param billCardPanel ���ݿ�Ƭģ�壬Ҳ�����Ǳ���ģ��
	 */
	private void init(CircularlyAccessibleValueObject headvo, CircularlyAccessibleValueObject[] bodyvos, BillCardPanel billCardPanel) {

		TempletDataSource ds = new TempletDataSource();
		IData data = null;
		/** ����ģ�� */
		if (billCardPanel != null && billCardPanel instanceof ReportBaseClass) {
			ReportBaseClass reportBase = (ReportBaseClass) billCardPanel;
			reportBase.setHeadDataVO(headvo);
			reportBase.setBodyDataVO(bodyvos);

			data = PrintETL.ETL_Report((ReportBaseClass) billCardPanel);

		} else if (billCardPanel != null) {
			billCardPanel.getBillData().setHeaderValueVO(headvo);
			billCardPanel.execHeadTailLoadFormulas();
			if (billCardPanel.getBillModel() != null) {
				billCardPanel.getBillModel().setBodyDataVO(bodyvos);
				billCardPanel.getBillModel().execLoadFormula();
			}
			data = PrintETL_BX.ETL(billCardPanel);
		}
		ds.addData(data);
		getPrintEntry().setDataSource(ds);

	}
	
	private String getPk_corp() {

		String pk_corp = null;
		try {
			pk_corp = ErUiUtil.getBXDefaultOrgUnit();
		} catch (Exception e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);;
		}
		return pk_corp;
	}
	
	private int initTemplet(String pk_corp, String nodeCode, String nodeKey) {
		if (getPrintEntry().getTemplateID() != null) {
			return 1;
		}
		if (pk_corp == null) {
			pk_corp = ErUiUtil.getBXDefaultOrgUnit();
		}
		String pk_user = ErUiUtil.getPk_user();
		getPrintEntry().setTemplateID(pk_corp, nodeCode, pk_user, null, nodeKey);

		// * ����û����õ�ģ���ж��,�����Ի������û�ѡ��һ����ӡģ����д�ӡ.
		// * @return �����0,��ȡ��; -1,�д�;, 1,��ȷѡ����һ��ģ��
		return getPrintEntry().selectTemplate();
	}
	
	public void output(String pk_corp, String nodeCode, String nodeKey) {
		{
			int result = initTemplet(pk_corp, nodeCode, nodeKey);
			if (result == -1 || result == 0) {
				return;
			} else {
				getPrintEntry().output();
				return;
			}
		}
	}
}
