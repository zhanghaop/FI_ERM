package nc.ui.erm.billpub.view.eventhandler;

import java.util.Map;

import nc.itf.fi.pub.Currency;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;

public class InitBodyEventHandle implements BillEditListener2, BillEditListener{
	private ErmBillBillForm editor = null;
	private EventHandleUtil eventUtil = null;
	private BodyEventHandleUtil bodyEventHandleUtil =null;
	
	public InitBodyEventHandle(ErmBillBillForm editor) {
		super();
		this.editor = editor;
		eventUtil = new EventHandleUtil(editor);
		bodyEventHandleUtil=new BodyEventHandleUtil(editor);
	}
	
	//����ı༭ǰ�¼�
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		String key = e.getKey();
		String fydwbm = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		if (e.getTableCode().equalsIgnoreCase(BXConstans.CSHARE_PAGE)) {
			// ��ǰ��̯
			ErmForCShareUiUtil.doCShareBeforeEdit(e, this.getBillCardPanel());
		}
		else if (BXBusItemVO.SZXMID.equals(key)) {// ��֧��Ŀ�������Ȩ�޿���
			// �༭ǰ����֯
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			refPane.getRefModel().setUseDataPower(true);
			refPane.setPk_org(fydwbm);
		}else if(BXBusItemVO.PK_RESACOSTCENTER.equals(key)){//�ɱ�����
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), BXBusItemVO.PK_RESACOSTCENTER);
			String wherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+fydwbm+"'"; 
			bodyEventHandleUtil.addWherePart2RefModel(refPane, fydwbm, wherePart);
		}else if(BXBusItemVO.PK_CHECKELE.equals(key)){//����Ҫ��
			// ����Ҫ�ظ����������Ĺ���
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			String pk_pcorg = (String)bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.PK_PCORG);
			if(pk_pcorg!=null){
				refPane.setEnabled(true);
				bodyEventHandleUtil.setPkOrg2RefModel(refPane, pk_pcorg);
			}else{
				refPane.setEnabled(false);
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.PK_PCORG);
			}
		}else if(BXBusItemVO.PROJECTTASK.equals(key)){//��Ŀ����
			final String pk_project = (String)bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.JOBID);
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			if (pk_project != null) {
				String wherePart = " pk_project=" + "'" + pk_project + "'";
				
				//��Ŀ����֯(�����Ǽ��ż���)
				final String pkOrg = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), BXBusItemVO.JOBID).getRefModel().getPk_org();
				String pk_org = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				if(BXUiUtil.getPK_group().equals(pkOrg)){
					//���ż���Ŀ
					pk_org = BXUiUtil.getPK_group(); 
				}
				//������Ŀ����
				refPane.setEnabled(true);
				bodyEventHandleUtil.setWherePart2RefModel(refPane,pk_org, wherePart);
			}else{
				refPane.setPK(null);
				refPane.setEnabled(false);
			}
		}else if(key != null && (key.startsWith(BXConstans.BODY_USERDEF_PREFIX))){
			filterDefItemField(key);
		}
		
		try {
			CrossCheckUtil.checkRule("N", key,editor);
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		return true;
	}
	
	/**
	 * �����Զ�����༭ǰ����
	 * 
	 * @author chenshuaia
	 * @param key
	 */
	private void filterDefItemField(String key) {
		BillItem bodyItem = ((ErmBillBillForm) editor).getBillCardPanel().getBodyItem(key);
		if (bodyItem.getComponent() instanceof UIRefPane && ((UIRefPane) bodyItem.getComponent()).getRefModel() != null) {
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

			((UIRefPane) bodyItem.getComponent()).getRefModel().setPk_org(pk_org);
		}
	}

	//����ı༭���¼�
	@Override
	public void afterEdit(BillEditEvent e){
		BillItem bodyItem = getBillCardPanel().getBodyItem(e.getTableCode(),e.getKey());
		if(bodyItem==null)
			return;
		
		//��̯��ϸ������
		if(e.getTableCode().equals(BXConstans.CSHARE_PAGE)){
			ErmForCShareUiUtil.doCShareAfterEdit(e, getBillCardPanel());
		}else{
			if(bodyItem.getKey().equals(BXBusItemVO.AMOUNT)||isAmoutField(bodyItem)){
				Object amount = getBillCardPanel().getBodyValueAt(e.getRow(), BXBusItemVO.AMOUNT);
				getBillCardPanel().setBodyValueAt(amount, e.getRow(), BXBusItemVO.YBJE);
				//��amount����ybje�¼�
				finBodyYbjeEdit();
				e.setKey(BXBusItemVO.YBJE);
				bodyEventHandleUtil.modifyFinValues(e.getKey(),e.getRow());
				e.setKey(BXBusItemVO.AMOUNT);
				try {
					editor.getHelper().calculateFinitemAndHeadTotal(editor);
					eventUtil.setHeadYFB();
				} catch (BusinessException e1) {
					ExceptionHandler.handleExceptionRuntime(e1);
				}
			}else if(bodyItem.getKey()!=null && bodyItem.getKey().equals(BXBusItemVO.SZXMID)){
				e.setKey(bodyItem.getKey());
				
			}else if(e.getKey().equals(BXBusItemVO.YBJE)||e.getKey().equals(BXBusItemVO.CJKYBJE)||e.getKey().equals(BXBusItemVO.ZFYBJE)||e.getKey().equals(BXBusItemVO.HKYBJE)){
				if(e.getKey().equals(BXBusItemVO.YBJE)){
					finBodyYbjeEdit();
				}
				bodyEventHandleUtil.modifyFinValues(e.getKey(),e.getRow());
			}else if(e.getKey().equals(BXBusItemVO.PK_PCORG_V)){//�������Ķ�汾�༭
				String pk_prong_v = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), e.getKey());
				UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(e.getKey()).getComponent();

				String oldid = MultiVersionUtil.getBillFinanceOrg(refPane.getRefModel(), pk_prong_v);
				getBillCardPanel().getBillData().getBillModel()
						.setValueAt(new DefaultConstEnum(oldid, BXBusItemVO.PK_PCORG), e.getRow(), BXBusItemVO.PK_PCORG);
				getBillCardPanel().getBillData().getBillModel().loadLoadRelationItemValue(e.getRow(), BXBusItemVO.PK_PCORG);
				afterEditPk_corp(e);
			}else if(e.getKey().equals(BXBusItemVO.PK_PCORG)){//��������
				BillItem pcorg_vItem = getBillCardPanel().getBodyItem(BXBusItemVO.PK_PCORG_V);
				if(pcorg_vItem != null){//�����������İ汾
					UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
					if (date != null) {
						String pk_pcorg = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.PK_PCORG);
						Map<String, String> map = MultiVersionUtil.getFinanceOrgVersion(
								((UIRefPane) pcorg_vItem.getComponent()).getRefModel(), new String[] { pk_pcorg }, date);
						String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
						getBillCardPanel().getBillModel().setValueAt(vid, e.getRow(),
								BXBusItemVO.PK_PCORG_V + IBillItem.ID_SUFFIX);
						getBillCardPanel().getBillModel().loadLoadRelationItemValue(e.getRow(), BXBusItemVO.PK_PCORG_V);
					}
				}
				afterEditPk_corp(e);
			}else if(e.getKey().equals(BXBusItemVO.JOBID)){//��Ŀ
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.PROJECTTASK);
			}
			if(bodyEventHandleUtil.getUserdefine(IBillItem.BODY, bodyItem.getKey(), 2)!=null){
				String formula=bodyEventHandleUtil.getUserdefine(IBillItem.BODY, bodyItem.getKey(), 2);
				String[] strings = formula.split(";");
				for(String form:strings){
					bodyEventHandleUtil.doFormulaAction(form,e.getKey(),e.getRow(),e.getTableCode(),e.getValue());
				}
			}	
			//add by chenshuai , ����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
			try {
				bodyEventHandleUtil.doContract(bodyItem, e);
			} catch (BusinessException e1) {
				ExceptionHandler.handleExceptionRuntime(e1);
			}
			
			//��������
			bodyEventHandleUtil.doBodyReimAction();
		}
	}

	private void afterEditPk_corp(BillEditEvent e) {
		getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.PK_CHECKELE);
	}
	
	private boolean isAmoutField(BillItem bodyItem) {
		String[] editFormulas = bodyItem.getEditFormulas();
		if(editFormulas==null){
			return false;
		}
		for(String formula:editFormulas){
			if(formula.indexOf(JKBXHeaderVO.AMOUNT)!=-1){
				return true;
			}
		}
		return false;
	}

	public void finBodyYbjeEdit() {
		UFDouble newHeadYbje = null;//��ͷ���

		String defaultMetaDataPath = BXConstans.ER_BUSITEM;//Ԫ����·��
		DjLXVO currentDjlx = ((ErmBillBillManageModel) editor.getModel()).getCurrentDjLXVO();

		if ((BXConstans.JK_DJDL.equals(currentDjlx.getDjdl()))) {
			defaultMetaDataPath = BXConstans.JK_BUSITEM;
		}

		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		if (billTabVOs != null && billTabVOs.length > 0) {
			for (BillTabVO billTabVO : billTabVOs) {
				String metaDataPath = billTabVO.getMetadatapath();//metaDataPath Ϊnull��ʱ��˵�����Զ���ҳǩ��Ĭ��Ϊҵ����
				if (metaDataPath != null && !defaultMetaDataPath.equals(metaDataPath)) {
					continue;
				}

				BillModel billModel = getBillCardPanel().getBillModel(billTabVO.getTabcode());
				BXBusItemVO[] details = (BXBusItemVO[]) billModel.getBodyValueVOs(BXBusItemVO.class.getName());

				int length = details.length;
				for (int i = 0; i < length; i++) {
					if (details[i].getYbje() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
						if (newHeadYbje == null) {
							newHeadYbje = details[i].getYbje();
						} else {
							newHeadYbje = newHeadYbje.add(details[i].getYbje());
						}
					}
				}
			}
		}

		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, newHeadYbje);
		if(getHeadValue(JKBXHeaderVO.PK_ORG) != null){
			setHeadYfbByHead();
		}
	}
	
	protected void setHeadYfbByHead() {

		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();

		if (valueObject == null || valueObject.toString().trim().length() == 0)
			return;

		UFDouble newYbje = new UFDouble(valueObject.toString());

		try {
			String bzbm = "null";
			if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}

			UFDouble hl = null;

			UFDouble globalhl = getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject() != null ? new UFDouble(
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString())
					: null;

			UFDouble grouphl = getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject() != null ? new UFDouble(
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString())
					: null;

			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
				hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject()
						.toString());
			}
			UFDouble[] je = Currency.computeYFB(eventUtil.getPk_org(), Currency.Change_YBCurr, bzbm, newYbje, null,
					null, null, hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);

			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			
			DjLXVO currentDjlx = ((ErmBillBillManageModel)editor.getModel()).getCurrentDjLXVO();
			if (BXConstans.JK_DJDL.equals(currentDjlx.getDjdl())|| editor.getResVO()!=null) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.TOTAL, je[0]);
			}
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBHL, money[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBHL, money[3]);

			eventUtil.resetCjkjeAndYe(je[0], bzbm, hl);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}

	}
	


	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}
	
	protected Object getHeadValue(String key) {
		BillItem headItem = getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
	       if(e.getOldrows() != null && e.getOldrows().length != e.getRows().length){
	           // resetJeAfterModifyRow();
	        }
	}
	   /**
     * 
     * ����˵���������иı���������ý��
     * @param e
     * @see 
     * @since V6.0
     */
    public void resetJeAfterModifyRow() 
    {
        if (!editor.getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE))
        {
            editor.getHelper().calculateFinitemAndHeadTotal(editor);
            try
            {
                //eventUtil.setHeadYFB();
            	eventUtil.resetHeadYFB();
                //editor.getEventHandle().resetBodyFinYFB();
            }
            catch (BusinessException e)
            {
                ExceptionHandler.handleExceptionRuntime(e);
            }
        }
    }

    public BodyEventHandleUtil getBodyEventHandleUtil()
    {
        return bodyEventHandleUtil;
    }

}
