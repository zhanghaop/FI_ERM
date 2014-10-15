package nc.ui.er.reimtype.view;

import java.awt.Color;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import nc.itf.org.IOrgConst;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.uif2.LoginContext;

public class ReimTypeEditor extends BatchBillTable implements
		TableModelListener {

	/**
	 * @author liansg
	 */
	private static final long serialVersionUID = 1L;

	private LoginContext context;

	private ControlAreaOrgPanel caOrgPanel;

	@Override
	public void tableChanged(TableModelEvent e) {
		setShareReimTypeColor();
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		setShareReimTypeColor();
	}

	private void setShareReimTypeColor() {
		BillModel billModel = getBillCardPanel().getBillModel();
		int rowCount = billModel.getRowCount();
		int columnCount = billModel.getColumnCount();
		for (int i = 0; i < rowCount; i++) {
			Object obj = billModel.getBodyValueRowVO(i, ReimTypeVO.class
					.getName());
			if (obj != null) {
				ReimTypeVO vo = (ReimTypeVO) obj;
				if (isShareReimType(vo)) {
					for (int j = 0; j < columnCount; j++) {
						billModel.setForeground(Color.BLUE, i, j);
					}
				}
			}
		}
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public ControlAreaOrgPanel getCaOrgPanel() {
		return caOrgPanel;
	}

	public void setCaOrgPanel(ControlAreaOrgPanel caOrgPanel) {
		this.caOrgPanel = caOrgPanel;
	}

	public boolean isShareReimType(ReimTypeVO rvtVO) {
		NODE_TYPE nodeType = context.getNodeType();
		String pk_group = rvtVO.getPk_group();
		String pk_controlarea = rvtVO.getPk_reimtype();
		if (nodeType == NODE_TYPE.GLOBE_NODE) {
			if (IOrgConst.GLOBEORGTYPE.equals(pk_group)) {
				return false;
			}
		} else if (nodeType == NODE_TYPE.GROUP_NODE) {
			if (context.getPk_group().equals(pk_group)
					&& pk_controlarea == null) {
				return false;
			}
		} 
//		else 
//			if (nodeType == NODE_TYPE.ORG_NODE) {
//			if (context.getPk_group().equals(pk_group)
//					&& getCaOrgPanel().getRefPane().getRefPK().equals(
//							pk_controlarea)) {
//				return false;
//			}
//		}
		return true;
	}

}
