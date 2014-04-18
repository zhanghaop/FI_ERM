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
 * 报销标准的配置界面
 * @author shiwla
 * 
 *
 */
public class ReimConfigPanel implements BillEditListener{
	
	private static final long serialVersionUID = -5305545020862306715L;
	//单据模板编号
	private String billtemplatecode = null;
	private int DEFNO = 20;
	private String DEF = "DEF";
	private BillCardPanel cardPanel = null;
	protected BillEditListener2 editListener = new EditListener2();
	private String djlx;
	//参照选择下拉框
	UIComboBox dtRefPane = null;
	//数据类型参照map
	private Map<String, RefInfoVO[]> map = new HashMap<String, RefInfoVO[]>();
	
	//对于单击编辑的控件（下拉框），会先调用beforeEdit方法，然后调用bodyRowChange，如果
	//仅在bodyRowChange中同步选择行，则会导致在beforeEdit中获取模型选择行错误。
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
				//加载模板
				cardPanel.loadTemplet(billtemplatecode, null, ErUiUtil.getPk_user(),
						ErUiUtil.getBXDefaultOrgUnit());
				//参照选择编辑器
				cardPanel.getBodyPanel().setTableCellEditor(ReimRuleDimVO.REFERENTIAL,
						new BillCellEditor(getDtRefPane()));
				
				//监听器
				cardPanel.addEditListener(this);
				cardPanel.addBodyEditListener2(editListener);
			} catch (NullPointerException e) {
				//加载模板失败
	    		MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "警告" */, "加载模板失败");
				Logger.error(e.getMessage(), e);
			}
		}
		return cardPanel;
	}
	
	//参照选择下拉框编辑器
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
        	//参照选择初始化失败，不做处理，只记录日志
			MessageDialog.showHintDlg(null,"数据类型错","数据类型 错误!");
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
		//清空原有值
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
		 * 预制七个默认的标准，当填入的显示名称是这些值时，对应单据模板上的这一项
		 */
		String[] names = new String[] { "费用类型", "报销类型", "部门", "职位", 
				"币种", "金额","备注"};
		String[] items = new String[] { "PK_EXPENSETYPE", "PK_REIMTYPE", "PK_DEPTID", "PK_POSITION", 
				"PK_CURRTYPE", "AMOUNT","MEMO"};
		//判断界面上的显示名称是否在以上预制范围内，是则将于单据模板的对应项关联起来
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
		//如果不在预制范围内，则需要全表来判断应该对应哪个自定义项
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
		//如果也不在自定义列中，则出错
		if(flag == false)
		{
			MessageDialog.showHintDlg(null,"列的数量超出","自定义列的数量上限为20!");
		}
	}
	
	public boolean beforeEdit(BillEditEvent e) {
		Object name = cardPanel.getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
		if (name != null && (name.toString().equals("币种") || name.toString().equals("金额"))) {
			MessageDialog.showHintDlg(null,"不允许更改","币种与金额列不允许更改!");
		}
		//参照选择列
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
		//显示名称列，编辑完后需要写入对应项
		if (e.getKey().equals(ReimRuleDimVO.DISPLAYNAME)) {
			if(value==null)
				return;
			addRef(e);
		}
		//数据类型列，编辑完后需要保存数据类型的值到DATATYPENAME中
		else if (e.getKey().equals(ReimRuleDimVO.DATATYPE)) {
			fillDiaplayName(e);
		}
		//参照选择列
		else if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			if(value==null)
				return;
			getBillCardPanel().setBodyValueAt(((RefInfoVO)value).getName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
		}
		//若编辑列为单据对应项列，编辑完后需要写入单据对应项编码，以便报销标准作用到具体的单据项
		else if (e.getKey().equals(ReimRuleDimVO.BILLREF)) {
			Object name = cardPanel.getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
			if (name.toString().equals("币种")) {
				getBillCardPanel().setBodyValueAt("币种", e.getRow(), ReimRuleDimVO.BILLREF);
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

	//根据当前的单据类型设置单据对应项应该参照报销单还是借款单
	public void setCellEditor(String djlx){
		this.djlx=djlx;
		//单据对应项编辑器
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

	//设置表体的具体值
	public void setData(List<SuperVO> vos) {
		// TODO Auto-generated method stub
		if (vos != null) {
			getBillCardPanel().getBillData().setBodyValueVO(
					vos.toArray(new SuperVO[] {}));
			//模板为元数据时需要这种方式才能显示名称
			if(getBillCardPanel().getBillModel()!=null)
				getBillCardPanel().getBillModel().loadLoadRelationItemValue();
		} else {
			getBillCardPanel().getBillData().setBodyValueVO(null);
		}
	}

	//list对应该单据类型下所有报销标准的数据
	public void delLine(List<SuperVO> list) throws BusinessException{
		//币种与金额列不允许删除
		String bz = (String) getBillCardPanel().getBodyValueAt(getBillCardPanel().getBodyPanel().getTable().getSelectedRow(), ReimRuleDimVO.DISPLAYNAME);
		if(bz.equals("币种") || bz.equals("金额"))
		{	
			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "警告" */, "币种与金额列不允许删除");
			return;
		}
		//删除某行时，需要判断该行对应项上有没有报销标准数值
		int row = getBillCardPanel().getBodyPanel().getTable().getSelectedRow();
		String cosItem = (String) getBillCardPanel().getBodyValueAt(row, ReimRuleDimVO.CORRESPONDINGITEM);
		if(cosItem == null || cosItem == "")
		{
			//报销标准中没有对应项则直接删除
			getBillCardPanel().delLine();
		}
		else
		{
			//查看该对应项是否有值
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
				MessageDialog.showHintDlg(null,"无法删除","该列对应的报销标准上有值，无法删除!");
			}
		}
	}
	
	public void addLine(){
		getBillCardPanel().addLine();
	}

	public void doCopy(String pk_group,String pk_org,String pk_billtype,String corp,String djlx) {
		// 同公司同交易类型不进行复制
		if (djlx.equals(pk_billtype) && pk_org.equals(corp)) 
			return;
		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		ReimRuleDimVO[] reimDimVos = (ReimRuleDimVO[]) getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRuleDimVO.class.getName());
		if (reimDimVos == null || reimDimVos.length == 0) {
			MessageDialog.showErrorDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/* @res "错误" */, "选中的报销标准中没有具体的值,复制已取消!");
			return;
		}
		int count = 0;
		
		try {
			// 检查维度值，并加入单据类型、集团与组织
			count=ReimRuleUtil.checkReimDims(reimDimVos,djlx,pk_group,corp);
			if(count>0)
			{
				// 直接进行保存的动作
				List<ReimRuleDimVO> returnVos = NCLocator.getInstance()
						.lookup(IReimTypeService.class).saveReimDim(
								djlx, pk_group,corp, reimDimVos); 
				// 如果是同公司的复制，需要同时更新datamaprule
				if (corp.equals(pk_org)) { 
					List<SuperVO> list = new ArrayList<SuperVO>();
					list.addAll(returnVos);
					ReimRuleUtil.putDim(djlx, list);
	//				ReimRuleUtil.getTemplateBillDataMap().put(pk_billtype, null);
				}
				MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "警告" */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000495",null,new String[]{String.valueOf(count)})/* @res "复制成功,i条记录已复制!" */);
			}
			
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(null,nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/* @res "错误" */,nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011",
							"UPP2011-000494")/* @res "复制失败：" */
					+ e.getMessage());
		}
	}
		
}
