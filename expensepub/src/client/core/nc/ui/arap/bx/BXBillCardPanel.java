package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.pf.pub.PfDataCache;
import nc.ui.arap.bx.actions.AddRowAction;
import nc.ui.arap.bx.actions.BXDefaultTabAction;
import nc.ui.arap.bx.actions.CopyLineAction;
import nc.ui.arap.bx.actions.DelRowAction;
import nc.ui.arap.bx.actions.InsertLineAction;
import nc.ui.arap.bx.actions.PasteLineAction;
import nc.ui.arap.bx.actions.PasteLineToTailAction;
import nc.ui.arap.bx.celleditor.BXSummaryCellEditor;
import nc.ui.arap.bx.listeners.BxCardHeadEditListener;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.ref.BXSummaryRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.bill.pub.BillUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 * 报销单据卡片面板
 *
 * nc.ui.arap.bx.BXBillCardPanel
 */
public class BXBillCardPanel extends BillCardPanel{

	private static final long serialVersionUID = -8133733599114546040L;

	private String pkcorp;

	private BaseUIPanel baseui;

	private JKBXVO djvo = VOFactory.createVO(getBillType());  //报销vo

	private boolean isContrast=false; //是否进行了冲借款操作

	private List<BxcontrastVO> contrasts; //冲借款数据

	private List<Action> bodyActions;

	public List<Action> getBodyActions() {
		return bodyActions;
	}

	/**
	 * 切换到卡片界面//卡片界面内容更新时初始化数据
	 */
	public void initData(){
		this.isContrast=false;
		this.contrasts=null;
	}

	public BXBillCardPanel(BaseUIPanel parent, String name, String billtype, String pkcorp, String operator) throws Exception {

		super();

		setBaseui(parent);

		setName(name);
		setBillType(billtype);
		setCorp(pkcorp);
		setOperator(operator);

	}

	public void initProp(){
		//处理菜单项与表体按钮(适配UIFactory2)
		initMenuItem();

		//初始化参照
		initUIRefpane();
	}

	/**
	 * @author zhangxiao1
	 */
	protected void initUIRefpane() {
		JComponent component = null;

		// 设置个人账号为显示编码
		BillItem headItem = getHeadItem(JKBXHeaderVO.SKYHZH);
		if(headItem!=null){
			component = headItem.getComponent();
		}
		if (component == null)
			return;
		if (component instanceof UIRefPane) {
			UIRefPane ref = (UIRefPane) component;
			ref.setReturnCode(true);
		}
//		 设置公司账号为显示编码
		component = getHeadItem(JKBXHeaderVO.SKYHZH).getComponent();
		if (component == null)
			return;
		if (component instanceof UIRefPane) {
			UIRefPane ref = (UIRefPane) component;
			ref.setReturnCode(true);
		}
		//事由字段特殊处理 
		initZyRefpane();
	}
	
	private UIRefPane getHeadZyRefPane(){
		return (UIRefPane)getHeadItem(JKBXHeaderVO.ZY).getComponent();
	}
	
	private UIRefPane getBodyZyRefPane(){
		return (UIRefPane)getBodyItem(JKBXHeaderVO.ZY).getComponent();
	}
	
	/**
	 * 事由字段参照特殊处理
	 */
	private void initZyRefpane() {
		//表头事由
		UIRefPane refPane = getHeadZyRefPane();
		refPane.setRefModel(new BXSummaryRefModel());
		
		//表体事由
		int index = getBodyColByKey(JKBXHeaderVO.ZY);
		if (index > -1) {
			TableColumnModel columnModel = getBillTable().getColumnModel();
			columnModel.getColumn(index).setCellEditor(new BXSummaryCellEditor(getBodyZyRefPane()));
		}
	}

	private void initMenuItem() {
		String[] tables = this.getBillData().getBodyTableCodes();
		if(null == tables){
			return;
		}

		//构造产生表体按钮事件
		List<Action> acts=new ArrayList<Action>();
		//行操作转换类
		constructBodyAction(acts);
		//加入action
		setBodyActions(acts);

		addTabAction(BillItem.BODY, acts);
		for(final String tab:tables){
			final BaseUIPanel baseui2 = getBaseui();
			BillScrollPane billScrollPane = ((BillScrollPane)getScrollPane(BillItem.BODY, tab));

			if(billScrollPane==null)
				continue;

			setTatolRowShow(tab,true);
		}

		//处理冲销明细页签
		BillScrollPane bodyPanel = this.getBodyPanel(BXConstans.CONST_PAGE);

	}

	private void constructBodyAction(List<Action> acts) {
		//增行
		Action addRowAction = new AddRowAction();
		BXDefaultTabAction act=(BXDefaultTabAction) addRowAction;
		act.setActionRunntimeV0(this.getBaseui());
		acts.add(addRowAction);
		//行拷贝
		Action copyLineAction = new CopyLineAction();
		BXDefaultTabAction copyline=(BXDefaultTabAction)copyLineAction ;
		copyline.setActionRunntimeV0(this.getBaseui());
		acts.add(copyLineAction);
		//行删除
		Action delLineAction = new DelRowAction();
		BXDefaultTabAction delline=(BXDefaultTabAction)delLineAction ;
		delline.setActionRunntimeV0(this.getBaseui());
		acts.add(delLineAction);
		//插入行
		Action insertLineAction = new InsertLineAction();
		BXDefaultTabAction insertline=(BXDefaultTabAction)insertLineAction ;
		insertline.setActionRunntimeV0(this.getBaseui());
		acts.add(insertLineAction);
		//粘贴行
		Action pasteLineAction = new PasteLineAction();
		BXDefaultTabAction pasteline=(BXDefaultTabAction)pasteLineAction ;
		pasteline.setActionRunntimeV0(this.getBaseui());
		acts.add(pasteLineAction);
		//粘贴行到表尾
		Action pasteLineToTailAction = new PasteLineToTailAction();
		BXDefaultTabAction pasteLineToTail=(BXDefaultTabAction)pasteLineToTailAction ;
		pasteLineToTail.setActionRunntimeV0(this.getBaseui());
		acts.add(pasteLineToTail);
	}



	private void setBodyActions(List<Action> acts) {
		this.bodyActions=acts;
	}

	/**
	 * @return 取所有表体行合计金额
	 */
	public UFDouble getBodySumJe() {

		UFDouble je = new UFDouble(0);

		String[] bodyTableCodes = getBillData().getBodyTableCodes();

		if (bodyTableCodes != null) {
			for (int i = 0; i < bodyTableCodes.length; i++) {

				if (getBaseui().isTempletView(bodyTableCodes[i])) {
					BillModel billModel = getBillModel(bodyTableCodes[i]);
					int rowCount = billModel.getRowCount();
					for (int j = 0; j < rowCount; j++) {
						Object value = billModel.getValueAt(j, BXBusItemVO.AMOUNT);
						if (value == null)
							continue;
						je = je.add(new UFDouble(value.toString()));
					}
				} else {

					BXBusItemVO[] itemVOs = getBaseui().getBodyUIController().save();

					for (int k = 0; k < itemVOs.length; k++) {
						je = je.add(itemVOs[k].getAmount());
					}
				}
			}
		}

		return je;
	}

	/** (non-Javadoc)
	 * @see nc.ui.pub.bill.BillCardPanel#getBillValueVOExtended(nc.vo.pub.ExtendedAggregatedValueObject)
	 *
	 * 重写父类的方法，根据是否摸板显示取得 Extended-VO
	 */
	@Override
	public void getBillValueVOExtended(ExtendedAggregatedValueObject bxvo) {

		if(isEnabled()){//如果是编辑态，停止编辑操作
			stopEditing();
		}

		String[] bodyTableCodes = getBillData().getBodyTableCodes();

		getBillData().getHeaderValueVO(bxvo.getParentVO());

		if (bodyTableCodes != null) {

			for (int i = 0; i < bodyTableCodes.length; i++) {

				if(bodyTableCodes[i].equals(BXConstans.CONST_PAGE)){ 
					// 冲销页签不做处理
					bxvo.setTableVO(bodyTableCodes[i], getBillData().getBodyValueVOs(bodyTableCodes[i], BxcontrastVO.class.getName()));
				}else if (getBaseui().isTempletView(bodyTableCodes[i])) {
					bxvo.setTableVO(bodyTableCodes[i], getBillData().getBodyValueVOs(bodyTableCodes[i], BXBusItemVO.class.getName()));
				}else{
					BXBusItemVO[] itemVOs = getBaseui().getBodyUIController().save();
					bxvo.setTableVO(bodyTableCodes[i], itemVOs);
				}
			}
		}

	}

	/**
	 * @see nc.ui.pub.bill.BillCardPanel#getBodyPanel()
	 */
	@Override
	public BillScrollPane getBodyPanel() {
        if (hashBodyScrollPane.size() == 0) {
        	// 默认加上一个空的BillScrollPane
			addBodyPanel(BillData.DEFAULT_BODY_TABLECODE, BillUtil
					.getDefaultTableName(BODY), false);
        }
        BillScrollPane bsp = null;
        if (getBodyTabbedPane().getTabCount() > 0) {
            JScrollPane sp = getBodyTabbedPane().getSelectedScrollPane();
            if (sp instanceof BillScrollPane) {
                bsp = (BillScrollPane) sp;
            }
        }
        if (bsp == null)
            bsp = getBodyBillScrollPane(null);
        if (bsp != null && bsp.getTableCode() == null) {
            bsp.setTableCode(getBillData().getBodyTableCodes()[0]);
            bsp.setTableName(getTableName(bsp.getTableCode()));
        }
        return bsp;
    }

	private String getTableName(String tablecode) {
        String tablename = null;
        if (getBillData() != null)
            tablename = getBillData().getBodyTableName(tablecode);
        if (tablename == null) {
            if (tablecode.equals(BillData.DEFAULT_BODY_TABLECODE))
                tablename = BillUtil.getDefaultTableName(BODY);
            else
                tablename = tablecode;
        }
        return tablename;
    }

    /* (non-Javadoc)
     * @see nc.ui.pub.bill.BillCardPanel#getBodyPanel(java.lang.String)
     */
    @Override
	public BillScrollPane getBodyPanel(String tablecode) {

        boolean isTempletView = getBaseui().isTempletView(tablecode);
		if(isTempletView){

			return super.getBodyPanel(tablecode);

		}else{

			String tablename = getTableName(tablecode);
	        if (hashBodyScrollPane.size() == 0) { // 默认加上一个空的BillScrollPane
	            addBodyPanel(tablecode, tablename, false);
	        }
			BillScrollPane bsp = getBaseui().getBodyUIController().getBodyUIPanel(tablecode);
	        if (bsp != null && bsp.getTableCode() == null) {
	            bsp.setTableCode(tablecode);
	            bsp.setTableName(tablename);
	        }
	        hashBodyScrollPane.put(tablecode, bsp);

	        bsp.repaint();

	        return bsp;
		}

    }


//    public void setBillValueObjectByMetaData(Object o){
//
//    	IBusinessEntity be = getBillData().getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity();
//
//    	BillTabVO[] tabvos = getBillData().getBillBaseTabVOsByPosition(IBillItem.BODY);
//
//    	NCObject ncobject = null;
//
//		if(be.getBeanStyle().getStyle() ==  BeanStyleEnum.AGGVO_HEAD)
//			ncobject = DASFacade.newInstanceWithContainedObject(be,o);
//		else if(be.getBeanStyle().getStyle() ==  BeanStyleEnum.NCVO ||
//				be.getBeanStyle().getStyle() == BeanStyleEnum.POJO){
//			if(o instanceof AggregatedValueObject){
//				o = ((AggregatedValueObject)o).getParentVO();
//				ncobject = DASFacade.newInstanceWithContainedObject(be,o);
//			}else {
//				ncobject = DASFacade.newInstanceWithContainedObject(be,o);
//			}
//		}
//
//		if(tabvos != null){
//        	for (int i = 0; i < tabvos.length; i++) {
//    			BillTabVO tabVO = tabvos[i];
//
//    			NCObject[] ncos = (NCObject[])ncobject.getAttributeValue(tabVO.getMetadatapath());
//
//    			BillModel model = getBillModel(tabVO.getTabcode());
//
//    			model.setBodyObjectByMetaData(ncos);
//
//
//    		}
//    	}
//
//    }

	/**
	 * @see nc.ui.pub.bill.BillCardPanel#setBillValueVO(nc.vo.pub.AggregatedValueObject)
	 *
	 * 重写父类的方法，将聚合VO加载到界面上，
	 *
	 * 增加处理逻辑：
	 * 1. 处理初始化不同单位归属的字段
	 * 2. 初始化借款单冲销信息
	 * 3. 调用界面控制器的初始化方法initBodyUIPanel
	 */
	@Override
	public void setBillValueVO(AggregatedValueObject billVO) {
		JKBXVO bxvo = (JKBXVO) billVO;
		getBillData().clearViewData();

		if (billVO == null)
			return;

		CircularlyAccessibleValueObject headVO = billVO.getParentVO();
		
		//根据币种处理VO精度
		CurrencyControlBO currencyControlBO = new CurrencyControlBO();
		currencyControlBO.dealBXVOdigit(bxvo);
		
		//显示单元
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		if(parentVO.getDjlxmc()==null){
			String djlxbm = parentVO.getDjlxbm();
			String strByID= BXUiUtil.getDjlxNameMultiLang(djlxbm);
			parentVO.setDjlxmc(strByID);
		}
		getHeadItem("djlxmc").setValue(parentVO.getDjlxmc());//显示单据名称

		//初始化不同单位归属的字段
		BxCardHeadEditListener listener = new BxCardHeadEditListener();
		listener.setActionRunntimeV0(this.getBaseui());

		try{
			BusiTypeVO busTypeVO = ((BXBillMainPanel) getBaseui()).getBusTypeVO();

			List<String> list=new ArrayList<String>();
			list.addAll(busTypeVO.getCostentity_billitems());
			busTypeVO.getPayentity_billitems().add("pk_checkele");
			list.addAll(busTypeVO.getPayentity_billitems());
			list.addAll(busTypeVO.getUseentity_billitems());

			listener.initItemsBelong(busTypeVO.getCostentity_billitems(),list,JKBXHeaderVO.FYDWBM,((JKBXHeaderVO)headVO).getFydwbm(),false);
			listener.initItemsBelong(busTypeVO.getUseentity_billitems(),list,JKBXHeaderVO.DWBM,((JKBXHeaderVO)headVO).getDwbm(),false);
			listener.initItemsBelong(busTypeVO.getPayentity_billitems(),list,JKBXHeaderVO.PK_ORG,((JKBXHeaderVO)headVO).getPk_org(),false);
		}catch (Exception e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000167")/*@res "初始化单据项目归属失败！"*/);
		}

		//事由(摘要)字段赋值
		String zy = (String) headVO.getAttributeValue(JKBXHeaderVO.ZY);
		
		setDjvo((JKBXVO) billVO);

		if (this.getBillData().getBillModel() == null)
			return;
		
		String[] tables = this.getBillData().getBodyTableCodes();
		
		getBillData().setBillValueObjectByMetaData(bxvo);
		
		getHeadZyRefPane().setValueObj(zy);
		getHeadZyRefPane().getUITextField().setText(zy);

		// 修改单据类型名称加载公式
		BillItem djlxbmBillItem = getHeadItem(JKBXHeaderVO.DJLXMC);
		BXUiUtil.modifyLoadFormula(djlxbmBillItem, "billtypename");
		if (tables != null) {
			for (int i = 0; i < tables.length; i++) {
				BillModel billModel = getBillData().getBillModel(tables[i]);
				if (getBaseui().isTempletView(tables[i])) {
					CircularlyAccessibleValueObject[] bodyVOs = bxvo.getTableVO(tables[i]);
					if (bodyVOs != null && bodyVOs.length != 0) {
						// begin--modified by chendya 加载数据
						getBillData().getBillModel(tables[i]).setBodyDataVO(bodyVOs);
						getBillModel().loadLoadRelationItemValue();
						// --end
						if(isTabVisiable(billModel) && isTabExecuteFormulaNeeded(billModel)){
							this.getBillModel(tables[i]).execLoadFormula();
						}
					}
				}

				//初始化借款单冲销信息.
				if(tables[i].equals(BXConstans.CONST_PAGE)){
					if(bxvo.getParentVO().getPk_jkbx()!=null
							&& !bxvo.getParentVO().getPk_jkbx().equals("")){

						BxcontrastVO[] contrast=bxvo.getContrastVO();
						if(contrast!=null && contrast.length!=0){
							getBillData().setBodyValueVO(tables[i], contrast);
							getBillModel().loadLoadRelationItemValue();

							if(isTabVisiable(billModel) && isTabExecuteFormulaNeeded(billModel)){
								this.getBillModel(tables[i]).execLoadFormula();
							}
						}
					}
				}
			}
		}

		getBaseui().getBodyUIController().initBodyUIPanel(((JKBXVO) billVO).getBxBusItemVOS());
		
		//add by chenshuai设置精度
		try{
			if(bxvo.getParentVO().getBzbm() != null && bxvo.getParentVO().getBzbm().trim().length() != 0){
				//根据表头币种设置精度
				BXUiUtil.resetDecimal(this, bxvo.getParentVO().getPk_org(), bxvo.getParentVO().getBzbm());
			}
		}catch(Exception e){
			ExceptionHandler.consume(e);
		}
		//end
	}

	public boolean isTabVisiable(BillModel model){
		BillItem[] bodyItems = model.getBodyItems();
		for(BillItem item:bodyItems){
			if(item.isShow()){
				return true;
			}
		}
		return false;
	}

	private boolean isTabExecuteFormulaNeeded(BillModel model){
		BillItem[] bodyItems = model.getBodyItems();
		List<String> formulaKeys=new ArrayList<String>();
		for(BillItem item:bodyItems){
			if(item.getLoadFormula()!=null){
				String[] loadFormula = item.getLoadFormula();
				for(String load:loadFormula){
					if(load!=null && load.indexOf("->")!=-1){
						formulaKeys.add(load.substring(0,load.indexOf("->")));
					}
				}
			}
		}

		for(BillItem item:bodyItems){
			if(item.isShow() && formulaKeys.contains(item.getKey()))
				return true;
		}

		return false;
	}

	public String getPkcorp() {
		return pkcorp;
	}

	public void setPkcorp(String pkcorp) {
		this.pkcorp = pkcorp;
	}

	public JKBXVO getDjvo() {
		return djvo;
	}

	public void setDjvo(JKBXVO djvo) {
		this.djvo = djvo;
	}

	public List<BxcontrastVO> getContrasts() {
		return contrasts;
	}

	public void setContrasts(List<BxcontrastVO> contrasts) {
		this.contrasts = contrasts;
	}

	public boolean isContrast() {
		return isContrast;
	}

	public void setContrast(boolean isContrast) {
		this.isContrast = isContrast;
	}

	public BaseUIPanel getBaseui() {
		return baseui;
	}

	public void setBaseui(BaseUIPanel baseui) {
		this.baseui = baseui;
	}

	public void setEnable(String[] key,int pos,boolean enable){

//		if(pos==1){
//			String[] tableCodes = this.getBillData().getBodyTableCodes();
//			for(String tableCode : tableCodes){
//				BillItem[] items = panel.getBillModel(tableCode).getBodyItems();
//				for (BillItem item : items) {
//					if(key.equals(item.getKey())){//如果tableCode页签中有项目key
//						int rowCount = panel.getBillModel(tableCode).getRowCount();
//						for(int i=0;i<rowCount;i++){
//							panel.getBillModel(tableCode).setValueAt(value, i, key);
//						}
//					}
//			}
//		}
//		}else{
//			//表头
//		}
	}
}