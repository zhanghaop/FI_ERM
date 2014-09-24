package nc.ui.er.reimrule.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.reimtype.IReimTypeService;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.er.reimrule.MDFormulaRefPane;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.SuperVO;

public class ControlTable extends BatchBillTable implements IComponentWithActions {

	private static final long serialVersionUID = 5732479916714526380L;
	private String centControlItem = null;//���Ŀ�����

	private String djlx = null;
	private String pkorg = null;
	private BDOrgPanel orgPanel = null;
	class CentCellEditor implements TableCellEditor{
		
		public boolean isCellEditable(EventObject eventobject)
	    {
	        return false;
	    }

		@Override
		public Component getTableCellEditorComponent(JTable jtable, Object obj,
				boolean flag, int i, int j) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addCellEditorListener(CellEditorListener celleditorlistener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cancelCellEditing() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeCellEditorListener(
				CellEditorListener celleditorlistener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean shouldSelectCell(EventObject eventobject) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	//������Ԫ����ѡ��Ի���
	private MDPropertyRefPane bxBillRefPane = null;
	/*������Ԫ����ID*/
	private static String bx_beanid = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";
	
	//��Ԫ����ѡ��Ի���
	private MDPropertyRefPane jkBillRefPane = null;
	/*��Ԫ����ID*/
	private static String jk_beanid = "e0499b58-c604-48a6-825b-9a7e4d6dacca";
	// ���幫ʽ���� and show Formula referPane
	private MDFormulaRefPane jk_referPanlFormula = null;
	// ���幫ʽ���� and show Formula referPane
	private MDFormulaRefPane bx_referPanlFormula = null;
	//��������Ӧ��Ԫ���ݱ༭��
	public MDPropertyRefPane getBXBillRefPane() {
		if (bxBillRefPane == null) {
			bxBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"201100_0", "0201100-0011")/* @res "��Դ�����ֶ�" */, bx_beanid,null);
			}
		return bxBillRefPane;
	}
	
	//����Ӧ��Ԫ���ݱ༭��
	public MDPropertyRefPane getJKBillRefPane() {
		if (jkBillRefPane == null) {
			jkBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"201100_0", "0201100-0011")/* @res "��Դ�����ֶ�" */, jk_beanid,null);
			}
		
		return jkBillRefPane;
	}
	
	//����ʽ������� 
	private MDFormulaRefPane getJKReferFormula() {
		try {
			if (jk_referPanlFormula == null) {
				jk_referPanlFormula = new MDFormulaRefPane(getBillCardPanel(),jk_beanid,null);
				jk_referPanlFormula.setAutoCheck(false);
			}
		} catch (Exception ex) {
			Logger.debug(ex);
		}
		return jk_referPanlFormula;
	}
	
	//��������ʽ������� 
	private MDFormulaRefPane getBXReferFormula() {
		try {
			if (bx_referPanlFormula == null) {
				bx_referPanlFormula = new MDFormulaRefPane(getBillCardPanel(),bx_beanid,null);
				bx_referPanlFormula.setAutoCheck(false);
			}
		} catch (Exception ex) {
			Logger.debug(ex);
		}
		return bx_referPanlFormula;
	}
	
	/**
	 * ����״̬ҳ��ı༭��
	 */
	private void setTableCellEditor(BillCardPanel cardPanel,String djlx,String centControlItem){
		if(djlx.startsWith("263")){
			//263��ʾ���ݶ�Ӧ��༭��Ӧ��Ϊ��
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.SHOWITEM,
					new BillCellEditor(getJKBillRefPane()));
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLITEM,
					new BillCellEditor(getJKBillRefPane()));
			//����ʽ�༭��
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFORMULA, new BillCellEditor(getJKReferFormula()));
		}
		else{
			//264��ʾ���ݶ�Ӧ��༭��Ӧ��Ϊ������
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.SHOWITEM,
					new BillCellEditor(getBXBillRefPane()));
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLITEM,
					new BillCellEditor(getBXBillRefPane()));
			//��������ʽ�༭��
			cardPanel.getBodyPanel().setTableCellEditor(ReimRulerVO.CONTROLFORMULA, new BillCellEditor(getBXReferFormula()));
		}
		//���Ŀ�����ɱ༭�ı༭��
		cardPanel.getBodyPanel().setTableCellEditor(centControlItem.toLowerCase(), new CentCellEditor());
	}
	
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if(event.getType() == AppEventConst.MODEL_INITIALIZED){
			getBillCardPanel().getBillData().setBodyValueVO(null);
			//��һ����Ҫ��ʼ���������ͺ�ҵ��Ԫ
			if(djlx == null){
				String djlxOrg = getOrgPanel().getRefPane().getUITextField().getValue().toString();
				if(!djlxOrg.contains(";"))
					return;
				String[] str = djlxOrg.split(";");
				djlx = str[0];
				pkorg = str[1];
			}
			//�޸Ľ����celleditor
			BillData billData = getBillCardPanel().getBillData();
			List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(djlx);
			if (reimruledim!=null && reimruledim.size()>0) {
				getBillCardPanel().setVisible(true);
				for(SuperVO vo:reimruledim)
				{
					if(((ReimRuleDimVO)vo).getControlflag().booleanValue())
					{
						centControlItem=((ReimRuleDimVO)vo).getCorrespondingitem();
						break;
					}
				}
				if(centControlItem == null){
					MessageDialog.showHintDlg(getBillCardPanel(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("ersetting_2", "22011rr-000039")/**
					 * @*
					 * res* "����"
					 */
					, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000047")/**
					 * @*
					 * res* "δ���ú��Ŀ�����������ý����н���ѡ��"
					 */
					);
					getBillCardPanel().setVisible(false);
				}
				//����Ҫ��ʾ���м���items��
				BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
				if(bodyItems==null)
					return;
				List<BillItem> items = new ArrayList<BillItem>();
				for (BillItem item : bodyItems) {
					if(item.getKey().equals(ReimRulerVO.SHOWITEM) || item.getKey().equals(ReimRulerVO.CONTROLITEM)
							|| item.getKey().equals(ReimRulerVO.CONTROLFLAG) || item.getKey().equalsIgnoreCase(centControlItem)
							|| item.getKey().equals(ReimRulerVO.CONTROLFORMULA))
					{
						item.setShow(true);
						items.add(item);
					}
					else
					{
						item.setShow(false);
						items.add(item);
					}
				}
				// ���ö�Ӧ���յĲ�����֯
				BillItem[] itemArray = items.toArray(new BillItem[] {});
				for (BillItem item : itemArray) {
					if (item.getComponent() instanceof UIRefPane) {
						UIRefPane ref = (UIRefPane) item.getComponent();
						if (ref.getRefModel() != null) {
							ref.getRefModel().setPk_org(pkorg);
						}
					}
				}

				billData.setBodyItems(itemArray);
				getBillCardPanel().setBillData(billData);
				//���ò���
				setTableCellEditor(getBillCardPanel(),djlx,centControlItem);
				//����ֵ
				List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(djlx);
				if(vos!=null){
					List<SuperVO> vos1 = new ArrayList<SuperVO>();
					StringBuilder cents = new StringBuilder();
					for(SuperVO vo:vos){
						if(!cents.toString().contains((String) vo.getAttributeValue(centControlItem))){
							vos1.add(vo);
							cents.append((String) vo.getAttributeValue(centControlItem));
						}
					}
					getBillCardPanel().getBillData().setBodyValueVO(
							vos1.toArray(new SuperVO[] {}));
				}
				getBillCardPanel().getBillModel().loadLoadRelationItemValue();
			}
			else
				getBillCardPanel().setVisible(false);
		}
	}

	public List<SuperVO> Save() throws Exception{
		getBillCardPanel().stopEditing();
		String currentBodyTableCode = getBillCardPanel()
			.getCurrentBodyTableCode();
		ReimRulerVO[] controlVOs = (ReimRulerVO[])getBillCardPanel()
			.getBillData().getBodyValueVOs(currentBodyTableCode,
				ReimRulerVO.class.getName());

		List<ReimRulerVO> returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).
				saveControlItem(centControlItem,djlx, getModel().getContext().getPk_group(),pkorg,controlVOs);
		List<SuperVO> list = new ArrayList<SuperVO>();
		list.addAll(returnVos);
		ReimRuleUtil.putRule(djlx, list);
		return list;
	}
	
	public boolean beforeEdit(BillEditEvent e) {
		MDPropertyRefPane mdref = null;
		Vector<Object> vecSelectedData = new Vector<Object>();
		mdref = djlx.startsWith("263") ? getJKBillRefPane():getBXBillRefPane();
		String showCode = null;
		String showName = null;
		if(e.getKey().equals(ReimRulerVO.SHOWITEM)) {
			if(getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.SHOWITEM_NAME)!=null)
				showCode = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.SHOWITEM_NAME).toString();
			if(getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.SHOWITEM)!=null)
			showName = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.SHOWITEM).toString();
		}
		else if(e.getKey().equals(ReimRulerVO.CONTROLITEM)) {
			if(getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.CONTROLITEM_NAME)!=null)
				showCode = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.CONTROLITEM_NAME).toString();
			if(getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.CONTROLITEM)!=null)
				showName = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.CONTROLITEM).toString();
		}
		else if(e.getKey().equals(ReimRulerVO.CONTROLFORMULA)) {
			MDFormulaRefPane mdformula = djlx.startsWith("263") ? getJKReferFormula():getBXReferFormula();
			if(mdformula != null){
				if(getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.CONTROLFORMULA)!=null)
					mdformula.setFormulaRule(getBillCardPanel().getBodyValueAt(e.getRow(), ReimRulerVO.CONTROLFORMULA).toString());
				else
					mdformula.setFormulaRule(null);
			}
			return true;
		}
		if (mdref != null) {
			if (showCode != null && showName != null) {
				String[] codeArray = showCode.split(",");
				String[] nameArray = showName.split(",");
				for (int nPos = 0; nPos < codeArray.length; nPos++) {
					Vector<String> row = new Vector<String>();
					row.add(codeArray[nPos]);
					row.add(nameArray[nPos]);
					vecSelectedData.add(row);
				}
			}
			mdref.getRefModel().setSelectedData(vecSelectedData);
			mdref.getDialog().getEntityTree().setSelectionPath(null);
		}
		return true;
	}
	
	/**
	 * ʵ�ֱ༭����߼�
	 *
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	protected void doAfterEdit(BillEditEvent e) {
		if(e.getKey().equals(ReimRulerVO.SHOWITEM)) {
			getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRulerVO.SHOWITEM_NAME);
			Vector vec;
			if(djlx.startsWith("263"))
				vec = getJKBillRefPane().getRefModel().getSelectedData();
			else
				vec = getBXBillRefPane().getRefModel().getSelectedData();
			if (vec != null) {
				for (Object object:vec) {
					String fieldcode = ((Vector)object).get(0).toString();
					getBillCardPanel().setBodyValueAt(fieldcode, e.getRow(), ReimRulerVO.SHOWITEM_NAME);
				}
			}
		} 
		else if(e.getKey().equals(ReimRulerVO.CONTROLITEM)) {
			getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRulerVO.CONTROLITEM_NAME);
			Vector vec;
			if(djlx.startsWith("263"))
				vec = getJKBillRefPane().getRefModel().getSelectedData();
			else
				vec = getBXBillRefPane().getRefModel().getSelectedData();
			if (vec != null) {
				for (Object object:vec) {
					String fieldcode = ((Vector)object).get(0).toString();
					getBillCardPanel().setBodyValueAt(fieldcode, e.getRow(), ReimRulerVO.CONTROLITEM_NAME);
				}
			}
		}
		else if(e.getKey().equals(ReimRulerVO.CONTROLFORMULA) && e.getValue()==null){
			getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRulerVO.CONTROLFORMULA);
		}
	}
	
	public BDOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(BDOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
	}

	@Override
	public List<Action> getActions() {
		// TODO Auto-generated method stub
		return null;
	}
}