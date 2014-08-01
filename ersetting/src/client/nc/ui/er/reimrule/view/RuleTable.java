package nc.ui.er.reimrule.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.reimtype.IReimTypeService;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bill.pub.BillUtil;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;

public class RuleTable extends BatchBillTable implements IComponentWithActions {

	private static final long serialVersionUID = 1L;
	private AbstractUIAppModel treemodel;
	private String centControlItem = null;//核心控制项
	private List<Action> actions = null;
	public void handleEvent(AppEvent event) {
		if(event.getType() == AppEventConst.SELECTION_CHANGED && event.getSource() == getTreemodel())
		{
			refreshTemplate();
		}
		else{
			super.handleEvent(event);
			if(event.getType() == AppEventConst.MODEL_INITIALIZED){
				String pk_billtype = null;
				if (getTreemodel().getSelectedData() != null) {
					pk_billtype = ((DjLXVO) getTreemodel().getSelectedData()).getDjlxbm();
				}
				List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(pk_billtype);
				if (reimruledim!=null && reimruledim.size()>0) {
					for(SuperVO vo:reimruledim)
					{
						if(((ReimRuleDimVO)vo).getControlflag().booleanValue())
						{
							centControlItem=((ReimRuleDimVO)vo).getCorrespondingitem();
							break;
						}
					}
				}
			}
		}
	}
	
	private void refreshTemplate() {
		if(getTreemodel().getSelectedData() == null)
			return;
		final String djlxbm = ((DjLXVO) getTreemodel().getSelectedData()).getDjlxbm();
		if (djlxbm == null){
			return;
		}
		BillData billData = ReimRuleUtil.getTemplateInitBillData(djlxbm,getNodekey());
		List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(djlxbm);
		//如果未设置维度，则用预置数据进行初始化
		if(reimruledim == null || reimruledim.size() == 0){
			if(reimruledim==null)
				reimruledim = new ArrayList<SuperVO>();
			try {
				String pk_group = getModel().getContext().getPk_group();
				String pk_org = getModel().getContext().getPk_org();
				List<ReimRuleDimVO> vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
						.queryReimDim("2631", "GLOBLE00000000000000", "~");
				for(ReimRuleDimVO dimvo:vodims){
					dimvo.setPk_billtype(djlxbm);
					dimvo.setPk_group(pk_group);
					dimvo.setPk_org(pk_org);
					reimruledim.add(dimvo);
				}
				//保存
				List<ReimRuleDimVO> returnVos = NCLocator.getInstance().lookup(
						IReimTypeService.class).saveReimDim(djlxbm, pk_group,pk_org,
								reimruledim.toArray(new ReimRuleDimVO[0]));
				List<SuperVO> list = new ArrayList<SuperVO>();
				list.addAll(returnVos);
				ReimRuleUtil.putDim(djlxbm, list);
			}catch (BusinessException ex) {
				ExceptionHandler.consume(ex);
			}
		}
		//展示界面
		if (reimruledim!=null && reimruledim.size()>0) {
			getBillCardPanel().setVisible(true);
			BillItem[] bodyItems = billData.getBillItemsByPos(IBillItem.BODY);
			if(bodyItems==null)
				return;
			//将需要显示的列加入items中
			List<BillItem> items = new ArrayList<BillItem>();
			for (BillItem item : bodyItems) {
				if(item.getKey().equals(ReimRulerVO.SHOWITEM) || item.getKey().equals(ReimRulerVO.CONTROLITEM)
						|| item.getKey().equals(ReimRulerVO.SHOWITEM_NAME) || item.getKey().equals(ReimRulerVO.CONTROLITEM_NAME)
						|| item.getKey().equals(ReimRulerVO.CONTROLFORMULA) || item.getKey().equals(ReimRulerVO.CONTROLFLAG))
				{
					item.setShow(false);
					items.add(item);
					continue;
				}
				boolean flag=false;
				for(SuperVO vo:reimruledim)
				{
					if(item.getKey().equalsIgnoreCase(((ReimRuleDimVO)vo).getCorrespondingitem()))
					{
						item.setShow(true);
						item.setNull(false);
						item.setName(((ReimRuleDimVO)vo).getDisplayname());
						item.setShowOrder(((ReimRuleDimVO)vo).getOrders());
						String datatypename = ((ReimRuleDimVO)vo).getDatatypename();
						//修改item的reftype，修改时弹出相应对话框
						String reftype = ((ReimRuleDimVO)vo).getDatatype();
						if(datatypename != null)
							reftype += ":" + datatypename;
						initAttribByParseReftype(datatypename,item,reftype);
						items.add(item);
						flag=true;
					}
				}
				if(flag==false)
				{
					item.setShow(false);
					items.add(item);
				}
			}
			// 设置对应参照的财务组织
			BillItem[] itemArray = items.toArray(new BillItem[] {});
			for (BillItem item : itemArray) {
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane ref = (UIRefPane) item.getComponent();
					if (ref.getRefModel() != null) {
						ref.getRefModel().setPk_org(getModel().getContext().getPk_org());
					}
				}
			}

			billData.setBodyItems(itemArray);
			getBillCardPanel().setBillData(billData);
            //缓存到该单据类型对应的billdata表中
			ReimRuleUtil.getTemplateBillDataMap().put(djlxbm, billData);
		}
		else{
			getBillCardPanel().setVisible(false);
		}
	}
	
	/*
	 * 解析类型设置
	 */
	private void initAttribByParseReftype(String datatypename,BillItem item,String reftype) {

		if (reftype == null || reftype.trim().length() == 0) {
			return;
		}
		reftype = reftype.trim();

		String[] tokens = BillUtil.getStringTokensWithNullToken(reftype,
				":");

		if (tokens.length > 0) {
			try {
				// 非原数据数据类型
				if (item.getMetaDataProperty() != null) {
					IMetaDataProperty mdp = MetaDataPropertyFactory
							.creatMetaDataUserDefPropertyByType(
									item.getMetaDataProperty(), tokens[0]);
					item.setMetaDataProperty(mdp);
				} else {
					int datatype = Integer.parseInt(tokens[0]);
					if (datatype <= IBillItem.USERDEFITEM)
						item.setDataType(datatype);
				}
				if (tokens.length > 1) {
					//修改元数据的datatype,展示时使用
					if(datatypename!=null && !datatypename.equals("2")){
						tokens[1]+=",code=N";
					}
					item.setRefType(tokens[1]);
				} else {
					item.setRefType(null);
					return;
				}
			} catch (NumberFormatException e) {
				Logger.warn("数据类型设置错误：" + tokens[0]);
				item.setDataType(IBillItem.STRING);
				item.setRefType(null);
				return;
			}
		}
	}
	
	public void Save() throws BusinessException
	{
		//报销标准修改界面的保存
		getBillCardPanel().stopEditing();
		String currentBodyTableCode = getBillCardPanel()
				.getCurrentBodyTableCode();
		CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
				.getBillData().getBodyValueVOs(currentBodyTableCode,
						ReimRulerVO.class.getName());
		String pk_billtype = null;
		if (getTreemodel().getSelectedData() != null) {
			pk_billtype = ((DjLXVO) getTreemodel().getSelectedData()).getDjlxbm();
		}
		if(pk_billtype==null)
			return;
		String pk_org = getTreemodel().getContext().getPk_org();
		if(pk_org==null)
			pk_org=ReimRulerVO.PKORG;
		ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;

		// 检查报销标准的值是否正确，其中核心控制项、币种、金额不能为空
		ReimRuleUtil.checkReimRules(reimRuleVOs,pk_billtype,getTreemodel().getContext().getPk_group(),pk_org,centControlItem);
		//保存时,先从原标准中提取界面控制值，然后保存标准，然后写入控制值
		List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(pk_billtype);
		List<ReimRulerVO> returnVos;
		if(vos==null){
			//若原标准为空，则直接保存
			returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(pk_billtype, 
					getTreemodel().getContext().getPk_group(),pk_org,reimRuleVOs);
		}
		else{
			//将核心控制项所对应的控制配置提取出来，重新对所有的标准分配控制项
			List<SuperVO> vos1 = new ArrayList<SuperVO>();
			StringBuilder cents = new StringBuilder();
			for(SuperVO vo:vos){
				if(!cents.toString().contains((String) vo.getAttributeValue(centControlItem))){
					vos1.add(vo);
					cents.append((String) vo.getAttributeValue(centControlItem));
				}
			}
			NCLocator.getInstance().lookup(IReimTypeService.class).saveReimRule(pk_billtype, 
					getTreemodel().getContext().getPk_group(),pk_org,reimRuleVOs);
			returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).
					saveControlItem(centControlItem,pk_billtype, getTreemodel().getContext().getPk_group(),
					pk_org,vos1.toArray(new ReimRulerVO[0]));
		}
		List<SuperVO> list = new ArrayList<SuperVO>();
		list.addAll(returnVos);
		ReimRuleUtil.putRule(pk_billtype, list);
		getModel().initModel(list.toArray(new SuperVO[0]));
	} 
	
	/**
	 * 实现编辑后的逻辑
	 * @param e
	 */
	protected void doAfterEdit(BillEditEvent e) {
		Object value = getBillCardPanel().getBodyValueAt(e.getRow(), e.getKey());
		if(value==null){
			getBillCardPanel().setBodyValueAt(null,e.getRow(), e.getKey()+"_name");
			return;
		}
		getBillCardPanel().setBodyValueAt(value.toString(),e.getRow(), e.getKey()+"_name");

		//精度处理
//		if (e.getKey().equals(ReimRulerVO.PK_CURRTYPE)) {
//			String currentBodyTableCode = getBillCardPanel()
//					.getCurrentBodyTableCode();
//			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel()
//					.getBillData().getBodyValueVOs(
//							currentBodyTableCode,
//							ReimRulerVO.class.getName());
//			ReimRulerVO[] reimRuleVOs = (ReimRulerVO[]) bodyValueVOs;
//	
//			int currencyPrecision = 0;// 币种精度
//	
//			for (ReimRulerVO vo : reimRuleVOs) {
//				try {
//					currencyPrecision = Currency
//							.getCurrDigit((String)vo.getPk_currtype());
//				} catch (Exception e1) {
//					ExceptionHandler.consume(e1);
//				}
//				getBillCardPanel().getBodyItem(ReimRulerVO.AMOUNT).setDecimalDigits(currencyPrecision);
//				
//				getBillCardPanel().setBodyValueAt(vo.getAmount() == null ? UFDouble.ZERO_DBL: 
//					vo.getAmount().setScale(currencyPrecision,UFDouble.ROUND_HALF_UP),
//						e.getRow(), ReimRulerVO.AMOUNT);
//				
//			}
//		}
	}

	public AbstractUIAppModel getTreemodel() {
		return treemodel;
	}

	public void setTreemodel(AbstractUIAppModel treemodel) {
		this.treemodel = treemodel;
		treemodel.addAppEventListener(this);
	}
	
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
}
