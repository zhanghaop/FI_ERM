package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.UFRefManage;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.event.BillTypeChangeEvent;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.bx.pub.ref.BXBilltypeRefModel;


/**
 * 切换交易类型。根据交易类型切换模板
 * <b>Date:</b>2012-12-11<br>
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */ 
public class ERMBillTypeAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;
	private UIRefPane billTypeRef;
	
	public ERMBillTypeAction() {
		super();
		setCode(ErmActionConst.BILLTYE);
		setBtnName(ErmActionConst.getBillTypeName());
		putValue(SHORT_DESCRIPTION, ErmActionConst.getBillTypeName());
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception { 
		getBillTypeRef(((ErmBillBillForm)getEditor()).isInit()).showModel();
		if(billTypeRef.getReturnButtonCode() != UFRefManage.ID_OK ){//点击确认按钮时才进行模板切换
			return;
		}
		
		Object billTypeCodeObj = billTypeRef.getRefValue(billTypeRef.getRefModel().getPkFieldCode());
		String newbillTypeCode = billTypeCodeObj == null ? null : String.valueOf(billTypeCodeObj);
		
		ErmBillBillForm editor2 = (ErmBillBillForm)getEditor();
		String currentBillTypeCode = ((ErmBillBillManageModel)editor2.getModel()).getCurrentBillTypeCode();

		((ErmBillBillManageModel)editor2.getModel()).setSelectBillTypeCode(newbillTypeCode);
		((ErmBillBillManageModel)editor2.getModel()).setCurrentBillTypeCode(newbillTypeCode);
		
		if(!currentBillTypeCode.equals(newbillTypeCode)){
			BillTypeChangeEvent billtypeevent = new BillTypeChangeEvent(currentBillTypeCode,newbillTypeCode);
			getModel().fireEvent(billtypeevent);
		}
	}
	
	/**
	 * 初始化单据类型参照
	 * 
	 * @param isqc
	 */
	public UIRefPane getBillTypeRef(boolean isqc) {
		if (billTypeRef == null) {
			billTypeRef = new UIRefPane();
			billTypeRef.setName("trantsyperef");
			billTypeRef.setLocation(578, 458);
			billTypeRef.setIsCustomDefined(true);
			billTypeRef.setVisible(false);
			billTypeRef.setRefModel(new BXBilltypeRefModel());
			
			String strWherePart = "";
			if (isqc) {
				strWherePart = " parentbilltype in ('263X') ";
			} else {
				strWherePart = " parentbilltype in ('263X','264X') ";
			}
			strWherePart += " and istransaction = 'Y' and (isLock='N' or isLock is null) and  ( pk_group='" + WorkbenchEnvironment.getInstance().getGroupVO().getPk_group() + "')";
			billTypeRef.getRefModel().setWherePart(strWherePart);
		}
		return billTypeRef;

	}
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
}
