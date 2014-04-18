package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.UFRefManage;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.vo.bx.pub.ref.BXBilltypeRefModel;

public class TradeTypeAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private UIRefPane djlxRef;

	private IEditor editor;

	private MAppModel model;

	public TradeTypeAction() {
		super();
		this.setBtnName(ErmActionConst.getBillTypeName());
		this.setCode(ErmActionConst.BILLTYE);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		UIRefPane djlxRefPane = getDjlxRef();

		djlxRefPane.showModel();

		if (djlxRefPane.getReturnButtonCode() != UFRefManage.ID_OK) {// 点击确认按钮时才进行模板切换
			return;
		}

		String djlxbm = djlxRefPane.getRefValue("pk_billtypecode") == null ? null : String.valueOf(djlxRefPane
				.getRefValue("pk_billtypecode"));

		if (djlxbm == null || djlxbm.trim().length() < 1) {
			return;
		}

		((MAppModel) model).setSelectBillTypeCode(djlxbm);// 设置当前单据类型
	}

	/**
	 * 获取单据类型参照
	 *
	 * @param isqc
	 */
	public UIRefPane getDjlxRef() {
		if (this.djlxRef == null) {
			this.djlxRef = new UIRefPane();
			this.djlxRef.setName("trantsyperef");
			this.djlxRef.setLocation(578, 458);
			this.djlxRef.setIsCustomDefined(true);
			this.djlxRef.setVisible(false);
			this.djlxRef.setRefModel(new BXBilltypeRefModel());
			String strWherePart = " parentbilltype in ('261X') ";

			strWherePart = strWherePart
					+ " and istransaction = 'Y' and islock ='N' and  ( pk_group='"
					+ WorkbenchEnvironment.getInstance().getGroupVO().getPk_group() + "')";
			djlxRef.getRefModel().setWherePart(strWherePart);
		}
		return djlxRef;
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
}