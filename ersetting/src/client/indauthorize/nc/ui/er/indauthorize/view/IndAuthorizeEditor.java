package nc.ui.er.indauthorize.view;

import java.awt.Color;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import nc.itf.org.IOrgConst;
import nc.ui.pf.pub.TranstypeRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.uif2.LoginContext;

public class IndAuthorizeEditor extends BatchBillTable implements
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
			Object obj = billModel.getBodyValueRowVO(i, IndAuthorizeVO.class
					.getName());
			if (obj != null) {
				IndAuthorizeVO vo = (IndAuthorizeVO) obj;
				if (isShareIndAuthorize(vo)) {
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

	public boolean isShareIndAuthorize(IndAuthorizeVO rvtVO) {
		NODE_TYPE nodeType = context.getNodeType();
		String pk_group = rvtVO.getPk_group();
		// String pk_controlarea = rvtVO.getPk_authorize();
		if (nodeType == NODE_TYPE.GLOBE_NODE) {
			if (IOrgConst.GLOBEORGTYPE.equals(pk_group)) {
				return false;
			}
		} else if (nodeType == NODE_TYPE.GROUP_NODE) {
			// FIXME
			if (context.getPk_group().equals(pk_group)) {
				// && pk_controlarea == null)

				return true;
			}
		}
		// else
		// if (nodeType == NODE_TYPE.ORG_NODE) {
		// if (context.getPk_group().equals(pk_group)
		// && getCaOrgPanel().getRefPane().getRefPK().equals(
		// pk_controlarea)) {
		// return false;
		// }
		// }
		return true;
	}

	@Override
	protected void doAfterEdit(BillEditEvent e) {

		// 编辑操作员带出所属组织
		if (IndAuthorizeVO.PK_OPERATOR.equals(e.getKey())) {
			String pk_operator = (String) e.getValue();
			String pk_org = getColValue("sm_user", "pk_org", "cuserid",
					pk_operator);
			if (pk_org == null || pk_org.trim().length() == 0) {
				return;
			}
			IndAuthorizeVO rowVO = (IndAuthorizeVO)getBillCardPanel().getBillModel().getBodyValueRowVO(e.getRow(), IndAuthorizeVO.class.getName());
			rowVO.setPk_org(pk_org);
			getBillCardPanel().getBillModel().setBodyRowObjectByMetaData(rowVO, e.getRow());
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		if (IndAuthorizeVO.PK_OPERATOR.equals(e.getKey())) {
			// 操作员支持多公司
			UIRefPane refpane = (UIRefPane) getBillCardPanel().getBodyItem(
					"pk_operator").getComponent();
			refpane.setMultiCorpRef(true);
		}else if(IndAuthorizeVO.PK_BILLTYPEID.equals(e.getKey())){
			// 单据类型过滤(仅包含借款报销单据类型)
			UIRefPane ref = (UIRefPane) getBillCardPanel().getBodyItem(
					"pk_billtypeid").getComponent();
			TranstypeRefModel refmodel = (TranstypeRefModel) ref.getRefModel();
			refmodel.setPkFieldCode("pk_billtypecode");
			final String filterStr = " and pk_billtypecode like '26%' and isnull(canextendtransaction,'~')='~' and pk_group='"
					+ getModel().getContext().getPk_group() + "'";
			String addWherePart = refmodel.getAddWherePart();
			if (addWherePart == null || addWherePart.trim().length() == 0) {
				addWherePart = filterStr;
			} else if (!addWherePart.contains(filterStr)) {
				addWherePart += filterStr;
			}
			refmodel.addWherePart(addWherePart);
		}
		return super.beforeEdit(e);
	}

	/**
	 * 前台公式解析器
	 */
	private static nc.ui.pub.formulaparse.FormulaParse parser;

	private static nc.ui.pub.formulaparse.FormulaParse getFormularParser() {
		if (parser == null) {
			parser = new nc.ui.pub.formulaparse.FormulaParse();
		}
		return parser;
	}

	public static String getColValue(String table, String column, String pk,
			String id) {
		FormulaParse parser = getFormularParser();
		parser.setExpress("getColValue(" + table + "," + column + "," + pk
				+ ",id);");
		parser.addVariable("id", id);
		return parser.getValue();
	}

}
