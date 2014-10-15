package nc.ui.erm.mactrlschema.view;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;

import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.logging.Logger;
import nc.md.MDBaseQueryFacade;
import nc.md.model.MetaDataException;
import nc.md.util.MDUtil;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.pub.VOStatus;

public class MaCtrlFieldTable extends BatchBillTable implements IComponentWithActions {

	private static final long serialVersionUID = 5732479916714526380L;

	private List<Action> actions = null;
	private MDPropertyRefPane refPane;
	private String title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0007")/*@res "����ά��"*/;
	private String entityid = "e3167d31-9694-4ea1-873f-2ffafd8fbed8";

	public void initUI() {
		super.initUI();
		getBillCardPanel().getBodyPanel().setTableCellEditor(MtappCtrlfieldVO.FIELDNAME, new BillCellEditor(getRefPane()));
	}

	public MDPropertyRefPane getRefPane() {
		if (refPane == null) {
			refPane = new MDPropertyRefPane(title, entityid);
		}
		return refPane;
	}

	/**
	 * ʵ�ֱ༭����߼�
	 *
	 * @param e
	 */
	protected void doAfterEdit(BillEditEvent e) {
		BillItem bodyItem = (BillItem) billCardPanel.getBodyItem(e.getTableCode(), e.getKey());
		if (bodyItem != null) {
			if (bodyItem.getKey().equals(MtappCtrlfieldVO.FIELDNAME)) {
				Map<String, String> map = getRefPane().getDialog().getSelecteddatas();
				if (map != null) {
					int i = 0;
					for (Map.Entry<String, String> entry : map.entrySet()) {
						String fieldcode = entry.getKey();
						String fieldname = entry.getValue();
						if (i == 0) {
							billCardPanel.setBodyValueAt(fieldname, e.getRow(), MtappCtrlfieldVO.FIELDNAME);
							billCardPanel.setBodyValueAt(fieldcode, e.getRow(), MtappCtrlfieldVO.FIELDCODE);
						} else {
							// �ȿ�¡ѡ�������ݣ��ٸ��ģ��������
							MtappCtrlfieldVO selectVO = (MtappCtrlfieldVO) getModel().getRow(e.getRow());
							MtappCtrlfieldVO newobj = (MtappCtrlfieldVO) selectVO.clone();
							newobj.setFieldcode(fieldcode);
							newobj.setFieldname(fieldname);
							newobj.setStatus(VOStatus.NEW);
							newobj.setPk_mtapp_cfield(null);
							getModel().addLines(new Object[] { newobj });// ����
						}
						i++;
					}
				}

			}
		} else {
			billCardPanel.setBodyValueAt(null, e.getRow(), MtappCtrlfieldVO.FIELDNAME);
			billCardPanel.setBodyValueAt(null, e.getRow(), MtappCtrlfieldVO.FIELDCODE);
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		if(e.getKey().equals(MtappCtrlfieldVO.FIELDNAME)){
			// �������ҪĿ���ǣ�����Ԫ����ѡ������ڲ�������ʱ��������ֵ
            MtappCtrlfieldVO mtappCtrlfieldVO = (MtappCtrlfieldVO)getModel().getSelectedData();
			String showCode = mtappCtrlfieldVO.getFieldcode();
            String showName = mtappCtrlfieldVO.getFieldname();
            if(showCode != null && showName != null){
            	String[] codeArray = showCode.split(",");
            	String[] nameArray = showName.split(",");
            	Vector<Object> vecSelectedData = new Vector<Object>();
            	for (int nPos = 0; nPos < codeArray.length; nPos++) {
            		Vector<String> row = new Vector<String>();
            		row.add(codeArray[nPos]);
            		row.add(nameArray[nPos]);
            		vecSelectedData.add(row);
            	}
            	getRefPane().getRefModel().setSelectedData(vecSelectedData);
            	getRefPane().getDialog().getEntityTree().setSelectionPath(null);
            }else{
            	getRefPane().getRefModel().setSelectedData(null);
            	getRefPane().getDialog().getEntityTree().setSelectionPath(null);
            }
            
//			Integer status = (Integer) getBillCardPanel().getBillModel()
//					.getBodyValueRowVO(e.getRow(), MtappCtrlfieldVO.class.getName()).getAttributeValue("status");
//			if (getModel().getUiState() == UIState.EDIT && status != VOStatus.NEW) {
//				return false;
//			}
		}
		return super.beforeEdit(e);
	}
	
	public void setValue(Object object) {
		super.setValue(object);
		for (int i = 0; i < getBillCardPanel().getBillTable().getRowCount(); i++) {
			String fieldcode = (String) getBillCardPanel().getBodyValueAt(i, MtappCtrlfieldVO.FIELDCODE);
			String mul_fieldname = null;
			try {
				mul_fieldname = MDUtil.ConvertPathToDisplayName(MDBaseQueryFacade.getInstance().getBeanByID(entityid), fieldcode);
			} catch (MetaDataException e) {
				Logger.error(e.getMessage(), e);
			}
			getBillCardPanel().setBodyValueAt(mul_fieldname, i, MtappCtrlfieldVO.FIELDNAME);
		}
	}
	
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

}