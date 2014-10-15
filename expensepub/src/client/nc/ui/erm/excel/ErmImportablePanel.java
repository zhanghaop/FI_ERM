package nc.ui.erm.excel;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.trade.excelimport.Uif2ImportablePanel;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;

/**
 * ���õ��뵼��Panel
 * �ɲ������࿪��
 * @author chenshuaia
 *
 */
public abstract class ErmImportablePanel extends Uif2ImportablePanel {
	/**
	 * UI����billForm
	 */
	private BillForm uiEditor = null;

	/**
	 * ͨ�����췽��������ע��
	 * 
	 * @param title
	 * @param appModel
	 * @param configPath
	 */
	public ErmImportablePanel(String title, AbstractUIAppModel appModel, String configPath) {
		super(title, appModel.getContext().getFuncInfo().getFuncode(), configPath);
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	abstract protected String getBillType();

	@Override
	protected String getAddActionBeanName() {
		return ErmActionConst.ADD_ACTION_BEAN_NAME;
	}

	@Override
	protected String getSaveActionBeanName() {
		return ErmActionConst.SAVE_ACTION_BEAN_NAME;
	}

	@Override
	protected String getCancelActionBeanName() {
		return ErmActionConst.CANCEL_ACTION_BEAN_NAME;
	}

	@Override
	protected String getAppModelBeanName() {
		return ErmActionConst.APPMODEL_BEAN_NAME;
	}

	@Override
	protected String getBillCardEditorBeanName() {
		return ErmActionConst.BILLCARD_EDITOR_BEAN_NAME;
	}

	protected BillCardPanel getEditorBillCardPanel() {
		return getEditor().getBillCardPanel();
	}

	protected BillData getEditorBillData() {
		return this.getEditor().getBillCardPanel().getBillData();
	}

	public BillForm getEditor() {
		return (BillForm) this.getBean(getBillCardEditorBeanName());
	}

	public BillForm getUiEditor() {
		return uiEditor;
	}

	public void setUiEditor(BillForm uiEditor) {
		this.uiEditor = uiEditor;
	}

	protected BillCardPanel getUiBillCardPanel() {
		if (getUiEditor().getBillCardPanel() == null) {
			return null;
		} else {
			return getUiEditor().getBillCardPanel();
		}
	}
	
	/**
	 * �����������Ҫ���˵����������� 
	 */
	@Override
	protected Object[] getSelectedObject() {
		Object[] selectedObject = super.getSelectedObject();
		List<Object> retrunObject = new ArrayList<Object>();
		for(Object select : selectedObject ){
			if(select instanceof JKBXVO){
				String mPk= ((JKBXVO)select).getParentVO().getPk_item();//���뵥pk
				if (mPk == null
						&& ((JKBXVO) select).getParentVO().getDjzt() != BXStatusConst.DJZT_Invalid) {
					retrunObject.add(select);
				}
			}else{
				retrunObject.add(select);
			}
		}
		return retrunObject.toArray(new Object[]{});
	}
	
	
}
