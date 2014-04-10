package nc.ui.arap.bx.print;

import nc.ui.fi_print.data.IData;
import nc.ui.fi_print.data.PrintETL;
import nc.ui.fi_print.entry.FiPrintEntry;
import nc.ui.fi_print.entry.TempletDataSource;
import nc.ui.pub.ClientEnvironment;
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
	 * 打印入口初始化
	 * 
	 * @param headvo 表头数据
	 * @param bodyvos 表体数据
	 * @param billCardPanel 单据卡片模板，也可已是报表模板
	 */
	private void init(CircularlyAccessibleValueObject headvo, CircularlyAccessibleValueObject[] bodyvos, BillCardPanel billCardPanel) {

		TempletDataSource ds = new TempletDataSource();
		IData data = null;
		/** 报表模板 */
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
			pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
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
			pk_corp = nc.ui.pub.ClientEnvironment.getInstance().getCorporation().getPk_corp();
		}
		String pk_user = nc.ui.pub.ClientEnvironment.getInstance().getUser().getPrimaryKey();
		getPrintEntry().setTemplateID(pk_corp, nodeCode, pk_user, null, nodeKey);

		// * 如果用户可用的模板有多个,弹出对话框让用户选择一个打印模板进行打印.
		// * @return 如果是0,则取消; -1,有错;, 1,正确选了了一个模板
		return getPrintEntry().selectTemplate();
	}
}
