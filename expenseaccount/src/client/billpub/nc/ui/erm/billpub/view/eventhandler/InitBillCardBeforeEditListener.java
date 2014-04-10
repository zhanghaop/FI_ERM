package nc.ui.erm.billpub.view.eventhandler;

import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.org.ref.DeptDefaultRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class InitBillCardBeforeEditListener implements BillCardBeforeEditListener {

	private BillForm editor;
	
	
	public InitBillCardBeforeEditListener(BillForm editor) {
		super();
		this.editor = editor;
	}

	@Override
	public boolean beforeEdit(BillItemEvent e) {
		String key = e.getItem().getKey();
		try {
			if(JKBXHeaderVO.DEPTID_V.equals(key)){
				BillItem headItem = ((ErmBillBillForm)editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if(headItem!=null){
					String dwbm=(String) headItem.getValueObject();
					((ErmBillBillForm)editor).getEventHandle().getHeadFieldHandle().beforeEditDept_v(dwbm, JKBXHeaderVO.DEPTID_V);
				}
			}else if(JKBXHeaderVO.FYDEPTID_V.equals(key)){
				BillItem headItem = ((ErmBillBillForm)editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if(headItem!=null){
					String fydwbm=(String) headItem.getValueObject();
					((ErmBillBillForm)editor).getEventHandle().getHeadFieldHandle().beforeEditDept_v(fydwbm, JKBXHeaderVO.FYDEPTID_V);
				}
			}else if(JKBXHeaderVO.DEPTID.equals(key)){
				BillItem headItem = ((ErmBillBillForm)editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if(headItem!=null){
					String dwbm=(String) headItem.getValueObject();
					beforeEditDept(dwbm, JKBXHeaderVO.DEPTID);
				}
			}else if(JKBXHeaderVO.FYDEPTID.equals(key)){
				BillItem headItem = ((ErmBillBillForm)editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if(headItem!=null){
					String dwbm=(String) headItem.getValueObject();
					beforeEditDept(dwbm, JKBXHeaderVO.FYDEPTID);
				}
			}else if(JKBXHeaderVO.JKBXR.equals(key)){
				((ErmBillBillForm)editor).getEventHandle().getHeadFieldHandle().initJkbxr();
			}else if (key != null && (key.startsWith(BXConstans.HEAD_USERDEF_PREFIX))) {//自定义项过滤
				filterZyxField(key);
			}
			
			if (!JKBXHeaderVO.PK_ORG_V.equals(key) && !JKBXHeaderVO.PK_ORG.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(key);
				if (headItem != null && headItem.getComponent() instanceof UIRefPane
						&& ((UIRefPane) headItem.getComponent()).getRefModel() != null) {
					CrossCheckUtil.checkRule("Y", key, editor);
				}
			}
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		return true;
	}
	
	/**
	 * 自定义项过滤 根据配置文件中设置的值进行过滤设置pk_org
	 * 
	 * @author chenshuaia
	 * @param key
	 */
	private void filterZyxField(String key) {
		BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(key);
		if (headItem.getComponent() instanceof UIRefPane && ((UIRefPane) headItem.getComponent()).getRefModel() != null) {
			ErmBillBillForm ermBillFom = (ErmBillBillForm) editor;
			String pk_org = null;
			if (ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_ORG) != null
					&& ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_ORG).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.DWBM) != null
					&& ermBillFom.getOrgRefFields(JKBXHeaderVO.DWBM).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.FYDWBM) != null
					&& ermBillFom.getOrgRefFields(JKBXHeaderVO.FYDWBM).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_PAYORG) != null
					&& ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_PAYORG).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			}

			((UIRefPane) headItem.getComponent()).getRefModel().setPk_org(pk_org);
		}
	}

	private void beforeEditDept(String dwbm, String deptid) {
		BillItem headItem = editor.getBillCardPanel().getHeadItem(deptid);
		if(headItem!=null){
			UIRefPane refPane = (UIRefPane)headItem.getComponent();
			DeptDefaultRefModel model = (DeptDefaultRefModel) refPane.getRefModel();
			model.setPk_org(dwbm);
		}
	}

}
