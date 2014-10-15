package nc.ui.er.reimrule;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.reimtype.IReimTypeService;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.ui.bd.ref.RefInfoHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.vo.arap.utils.StringUtil;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;

/**
 * ������׼�����ý���
 * @author shiwla
 * 
 *
 */
public class ReimConfigPanel implements BillEditListener{
	
	private static final long serialVersionUID = -5305545020862306715L;
	//����ģ����
	private String billtemplatecode = null;
	private int DEFNO = 20;
	private String DEF = "DEF";
	private BillCardPanel cardPanel = null;
	protected BillEditListener2 editListener = new EditListener2();
	private String djlx;
	//����ѡ��������
	UIComboBox dtRefPane = null;
	//�������Ͳ���map
	private Map<String, RefInfoVO[]> map = new HashMap<String, RefInfoVO[]>();
	
	//���ڵ����༭�Ŀؼ��������򣩣����ȵ���beforeEdit������Ȼ�����bodyRowChange�����
	//����bodyRowChange��ͬ��ѡ���У���ᵼ����beforeEdit�л�ȡģ��ѡ���д���
	class EditListener2 implements BillEditListener2
	{
		@Override
		public boolean beforeEdit(BillEditEvent e) {
			
			return ReimConfigPanel.this.beforeEdit(e);
		}
		
	}

	public ReimConfigPanel(String billtemplatecode) {
		super();
		this.billtemplatecode=billtemplatecode;
	}

	public BillCardPanel getBillCardPanel() {
		if (cardPanel == null) {
			try {
				cardPanel = new BillCardPanel();
				//����ģ��
				cardPanel.loadTemplet(billtemplatecode, null, ErUiUtil.getPk_user(),
						ErUiUtil.getBXDefaultOrgUnit());
				//����ѡ��༭��
				cardPanel.getBodyPanel().setTableCellEditor(ReimRuleDimVO.REFERENTIAL,
						new BillCellEditor(getDtRefPane()));
				
				//������
				cardPanel.addEditListener(this);
				cardPanel.addBodyEditListener2(editListener);
			} catch (NullPointerException e) {
				//����ģ��ʧ��
	    		MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "����" */, "����ģ��ʧ��");
				Logger.error(e.getMessage(), e);
			}
		}
		return cardPanel;
	}
	
	//����ѡ��������༭��
	private UIComboBox getDtRefPane() {
		if (dtRefPane == null) {
			dtRefPane = new UIComboBox();
			dtRefPane.setPreferredSize(new Dimension(150, 22));
		}
		return dtRefPane;
	}
	
	private void fillDiaplayName(BillEditEvent e) {
		String entityid = null;
        if(e.getSource() != null && (e.getSource() instanceof BillCellEditor))
        {
            java.awt.Component component = ((BillCellEditor)e.getSource()).getComponent();
            if(component != null && (component instanceof UIRefPane))
                entityid = ((UIRefPane)component).getRefPK();
        }
        if(entityid == null || entityid.trim().length() == 0)
            return;
        IBean bean = null;
        try
        {
        	bean = MDBaseQueryFacade.getInstance().getBeanByID(entityid);
        }
        catch(MetaDataException e1)
        {
        	//����ѡ���ʼ��ʧ�ܣ���������ֻ��¼��־
			MessageDialog.showHintDlg(null,"�������ʹ�","�������� ����!");
			Logger.error(e1.getMessage(), e1);
        }
        Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
        if(name == null && bean != null){
            getBillCardPanel().setBodyValueAt(bean.getDisplayName(), e.getRow(), ReimRuleDimVO.DISPLAYNAME);
            addRef(e);
        }
//		Object value = getBillCardPanel().getBodyItem(e.getTableCode(), e.getKey()).getValueObject();
//		setDataStyle(e.getRow(),entityid);
		getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.DATATYPENAME);
		getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.BEANNAME);
		if(entityid.equals("BS000010000100001031") || entityid.equals("BS000010000100001052"))
			getBillCardPanel().setBodyValueAt(2, e.getRow(), ReimRuleDimVO.DATATYPENAME);
		if(bean != null)
		{
			String beanName=bean.getName();
			getBillCardPanel().setBodyValueAt(bean.getDisplayName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
			getBillCardPanel().setBodyValueAt(beanName, e.getRow(), ReimRuleDimVO.BEANNAME);
			RefInfoVO[] infoVOs=map.get(beanName);
			if(infoVOs == null || infoVOs.length == 0)
			{
				infoVOs = RefInfoHelper.getInstance().getRefinfoVOs(beanName);
				if(infoVOs!=null && infoVOs.length>0)
				{
					map.put(beanName, infoVOs);
				}
			}
		}
	}
	
	private void initDtInfoData(BillEditEvent e) {
		//���ԭ��ֵ
		getDtRefPane().removeAllItems();
		Object beanname = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BEANNAME);
		if(beanname==null)
			return;
		String value = beanname.toString();
//		Object value = getBillCardPanel().getBodyItem(e.getTableCode(), e.getKey()).getValueObject();
		RefInfoVO[] infoVOs=map.get(value);
		if(infoVOs == null || infoVOs.length == 0)
		{
			infoVOs = RefInfoHelper.getInstance().getRefinfoVOs(value);
			if(infoVOs!=null && infoVOs.length>0)
			{
				map.put(value, infoVOs);
			}
		}
		if(infoVOs!=null && infoVOs.length>0)
		{
			getDtRefPane().addItems(infoVOs);
		}
	}
	
	public void addRef(BillEditEvent e) {
		Object value=getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
		if(!StringUtil.isNullWithTrim((String) value))
			return;
		/**
		 * Ԥ���߸�Ĭ�ϵı�׼�����������ʾ��������Щֵʱ����Ӧ����ģ���ϵ���һ��
		 */
		String[] names = new String[] { "��������", "��������", "����", "ְλ", 
				"����", "���","��ע"};
		String[] items = new String[] { "PK_EXPENSETYPE", "PK_REIMTYPE", "PK_DEPTID", "PK_POSITION", 
				"PK_CURRTYPE", "AMOUNT","MEMO"};
		//�жϽ����ϵ���ʾ�����Ƿ�������Ԥ�Ʒ�Χ�ڣ������ڵ���ģ��Ķ�Ӧ���������
		String displayname = (String)e.getValue();
		boolean flag = false;
		for(int j=0;j<names.length && flag ==false;j++)
		{
			if(displayname.equals(names[j]))
			{
				cardPanel.setBodyValueAt(items[j], e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
				flag = true;
			}
		}
		//�������Ԥ�Ʒ�Χ�ڣ�����Ҫȫ�����ж�Ӧ�ö�Ӧ�ĸ��Զ�����
		if(flag == false)
		{
			String currentBodyTableCode = getBillCardPanel()
			.getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
			.getBillData().getBodyValueVOs(currentBodyTableCode,
					ReimRuleDimVO.class.getName());
			ReimRuleDimVO[] reimDimVOs = (ReimRuleDimVO[]) bodyValueVOs;
			List<String> list = new ArrayList<String>();
			for (ReimRuleDimVO rule : reimDimVOs) {
				list.add(rule.getCorrespondingitem());
			}
			for(int i=0;i<DEFNO && flag==false;i++)
			{
				String tmp=DEF+(i+1);
				if(!list.contains(tmp))
				{
					cardPanel.setBodyValueAt(tmp, e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
					flag=true;
				}
			}
		}
		//���Ҳ�����Զ������У������
		if(flag == false)
		{
			MessageDialog.showHintDlg(null,"�е���������","�Զ����е���������Ϊ20!");
		}
	}
	
	public boolean beforeEdit(BillEditEvent e) {
		Object name = cardPanel.getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
		if (name != null && (name.toString().equals("����") || name.toString().equals("���"))) {
			MessageDialog.showHintDlg(null,"���������","���������в��������!");
		}
		//����ѡ����
		if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			initDtInfoData(e);
			return true;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterEdit(BillEditEvent e) {
		Object value=cardPanel.getBodyValueAt(e.getRow(), e.getKey());
		//��ʾ�����У��༭�����Ҫд���Ӧ��
		if (e.getKey().equals(ReimRuleDimVO.DISPLAYNAME)) {
			if(value==null)
				return;
			addRef(e);
		}
		//���������У��༭�����Ҫ�����������͵�ֵ��DATATYPENAME��
		else if (e.getKey().equals(ReimRuleDimVO.DATATYPE)) {
			fillDiaplayName(e);
		}
		//����ѡ����
		else if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			if(value==null)
				return;
			getBillCardPanel().setBodyValueAt(((RefInfoVO)value).getName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
		}
		//���༭��Ϊ���ݶ�Ӧ���У��༭�����Ҫд�뵥�ݶ�Ӧ����룬�Ա㱨����׼���õ�����ĵ�����
		else if (e.getKey().equals(ReimRuleDimVO.BILLREF)) {
			Object name = cardPanel.getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
			if (name.toString().equals("����")) {
				getBillCardPanel().setBodyValueAt("����", e.getRow(), ReimRuleDimVO.BILLREF);
				getBillCardPanel().setBodyValueAt("bzbm", e.getRow(), ReimRuleDimVO.BILLREFCODE);
			}
			else{
				getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.BILLREFCODE);
				Vector vec;
				if(djlx.startsWith("263"))
					vec = ReimRuleUtil.getJKBillRefPane().getRefModel().getSelectedData();
				else
					vec = ReimRuleUtil.getBXBillRefPane().getRefModel().getSelectedData();
				if (vec != null) {
					for (Object object:vec) {
						String fieldcode = ((Vector)object).get(0).toString();
						getBillCardPanel().setBodyValueAt(fieldcode, e.getRow(), ReimRuleDimVO.BILLREFCODE);
					}
				}
			}
		}
	}

	//���ݵ�ǰ�ĵ����������õ��ݶ�Ӧ��Ӧ�ò��ձ��������ǽ�
	public void setCellEditor(String djlx){
		this.djlx=djlx;
		//���ݶ�Ӧ��༭��
		if(djlx.startsWith("263"))
			getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.BILLREF,
				new BillCellEditor(ReimRuleUtil.getJKBillRefPane()));
		else
			getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.BILLREF,
					new BillCellEditor(ReimRuleUtil.getBXBillRefPane()));	
	}
	
	@Override
	public void bodyRowChange(BillEditEvent billeditevent) {
	}

	//���ñ���ľ���ֵ
	public void setData(List<SuperVO> vos) {
		// TODO Auto-generated method stub
		if (vos != null) {
			getBillCardPanel().getBillData().setBodyValueVO(
					vos.toArray(new SuperVO[] {}));
			//ģ��ΪԪ����ʱ��Ҫ���ַ�ʽ������ʾ����
			if(getBillCardPanel().getBillModel()!=null)
				getBillCardPanel().getBillModel().loadLoadRelationItemValue();
		} else {
			getBillCardPanel().getBillData().setBodyValueVO(null);
		}
	}

	//list��Ӧ�õ������������б�����׼������
	public void delLine(List<SuperVO> list) throws BusinessException{
		//���������в�����ɾ��
		String bz = (String) getBillCardPanel().getBodyValueAt(getBillCardPanel().getBodyPanel().getTable().getSelectedRow(), ReimRuleDimVO.DISPLAYNAME);
		if(bz.equals("����") || bz.equals("���"))
		{	
			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "����" */, "���������в�����ɾ��");
			return;
		}
		//ɾ��ĳ��ʱ����Ҫ�жϸ��ж�Ӧ������û�б�����׼��ֵ
		int row = getBillCardPanel().getBodyPanel().getTable().getSelectedRow();
		String cosItem = (String) getBillCardPanel().getBodyValueAt(row, ReimRuleDimVO.CORRESPONDINGITEM);
		if(cosItem == null || cosItem == "")
		{
			//������׼��û�ж�Ӧ����ֱ��ɾ��
			getBillCardPanel().delLine();
		}
		else
		{
			//�鿴�ö�Ӧ���Ƿ���ֵ
			boolean hasValue=false;
			if(list != null)
				for(SuperVO vo:list)
				{
					vo=(ReimRulerVO)vo;
					if(vo.getAttributeValue(cosItem) != null && (String) vo.getAttributeValue(cosItem) != "")
					{
						hasValue=true;
						break;
					}
				}
			if(hasValue==false)
			{
				getBillCardPanel().delLine();
			}
			else
			{
				MessageDialog.showHintDlg(null,"�޷�ɾ��","���ж�Ӧ�ı�����׼����ֵ���޷�ɾ��!");
			}
		}
	}
	
	public void addLine(){
		getBillCardPanel().addLine();
	}

	public void doCopy(String pk_group,String pk_org,String pk_billtype,String corp,String djlx) {
		// ͬ��˾ͬ�������Ͳ����и���
		if (djlx.equals(pk_billtype) && pk_org.equals(corp)) 
			return;
		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		ReimRuleDimVO[] reimDimVos = (ReimRuleDimVO[]) getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRuleDimVO.class.getName());
		if (reimDimVos == null || reimDimVos.length == 0) {
			MessageDialog.showErrorDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/* @res "����" */, "ѡ�еı�����׼��û�о����ֵ,������ȡ��!");
			return;
		}
		int count = 0;
		
		try {
			// ���ά��ֵ�������뵥�����͡���������֯
			count=ReimRuleUtil.checkReimDims(reimDimVos,djlx,pk_group,corp);
			if(count>0)
			{
				// ֱ�ӽ��б���Ķ���
				List<ReimRuleDimVO> returnVos = NCLocator.getInstance()
						.lookup(IReimTypeService.class).saveReimDim(
								djlx, pk_group,corp, reimDimVos); 
				// �����ͬ��˾�ĸ��ƣ���Ҫͬʱ����datamaprule
				if (corp.equals(pk_org)) { 
					List<SuperVO> list = new ArrayList<SuperVO>();
					list.addAll(returnVos);
					ReimRuleUtil.putDim(djlx, list);
	//				ReimRuleUtil.getTemplateBillDataMap().put(pk_billtype, null);
				}
				MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "����" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000495",null,new String[]{String.valueOf(count)})/* @res "���Ƴɹ�,i����¼�Ѹ���!" */);
			}
			
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(null,nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/* @res "����" */,nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011",
							"UPP2011-000494")/* @res "����ʧ�ܣ�" */
					+ e.getMessage());
		}
	}
		
}
