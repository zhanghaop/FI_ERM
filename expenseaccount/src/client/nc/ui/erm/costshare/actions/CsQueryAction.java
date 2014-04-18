package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.costshare.common.CsListView;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.meta.IFilterMeta;
import nc.ui.uif2.actions.QueryAction;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;

/**
 * 费用结转单查询action
 * 
 * @author luolch
 * 
 */
public class CsQueryAction extends QueryAction {

	private static final long serialVersionUID = 1L;

	private boolean isInitialized = false;
	private CsListView listView;
	private static String CSField = "csharedetail.";
	private final static String[] bodyDefitem;
	private static String[] headDefitem;
	
	/**
	 * 表头和表体自定义字段的属性
	 */
	static {
		bodyDefitem = new String[] { CSField + CShareDetailVO.DEFITEM1, CSField + CShareDetailVO.DEFITEM2, CSField + CShareDetailVO.DEFITEM3, CSField + CShareDetailVO.DEFITEM4, CSField + CShareDetailVO.DEFITEM5,CSField + CShareDetailVO.DEFITEM6,
									 CSField + CShareDetailVO.DEFITEM7, CSField + CShareDetailVO.DEFITEM8, CSField + CShareDetailVO.DEFITEM9, CSField + CShareDetailVO.DEFITEM10, CSField + CShareDetailVO.DEFITEM11, CSField + CShareDetailVO.DEFITEM12, 
									 CSField + CShareDetailVO.DEFITEM13, CSField + CShareDetailVO.DEFITEM14, CSField + CShareDetailVO.DEFITEM15, CSField + CShareDetailVO.DEFITEM16, CSField + CShareDetailVO.DEFITEM17, CSField + CShareDetailVO.DEFITEM18, 
									 CSField + CShareDetailVO.DEFITEM19, CSField + CShareDetailVO.DEFITEM20, CSField + CShareDetailVO.DEFITEM21, CSField + CShareDetailVO.DEFITEM22, CSField + CShareDetailVO.DEFITEM23, CSField + CShareDetailVO.DEFITEM24, 
									 CSField + CShareDetailVO.DEFITEM25, CSField + CShareDetailVO.DEFITEM26, CSField + CShareDetailVO.DEFITEM27, CSField + CShareDetailVO.DEFITEM28, CSField + CShareDetailVO.DEFITEM29, CSField + CShareDetailVO.DEFITEM30,};
	
		headDefitem = new String[] { CostShareVO.DEFITEM1, CostShareVO.DEFITEM2, CostShareVO.DEFITEM3, CostShareVO.DEFITEM4, CostShareVO.DEFITEM5,CostShareVO.DEFITEM6,
				CostShareVO.DEFITEM7, CostShareVO.DEFITEM8, CostShareVO.DEFITEM9, CostShareVO.DEFITEM10, CostShareVO.DEFITEM11, CostShareVO.DEFITEM12, 
				CostShareVO.DEFITEM13, CostShareVO.DEFITEM14, CostShareVO.DEFITEM15, CostShareVO.DEFITEM16, CostShareVO.DEFITEM17, CostShareVO.DEFITEM18, 
				CostShareVO.DEFITEM19,CShareDetailVO.DEFITEM20,CostShareVO.DEFITEM21,  CostShareVO.DEFITEM22, CostShareVO.DEFITEM23,CostShareVO.DEFITEM24, 
				 CostShareVO.DEFITEM25, CShareDetailVO.DEFITEM26, CostShareVO.DEFITEM27, CostShareVO.DEFITEM28, CostShareVO.DEFITEM29,  CostShareVO.DEFITEM30,};

	
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 先初始化dlg的监听
		super.doAction(e);
		listView.showMeUp();
	}
	
	@Override
	protected IQueryConditionDLG getQueryCoinditionDLG() {
		IQueryConditionDLG queryConditionDLG = super.getQueryCoinditionDLG();

		// 第一次调用时注册监听
		initDlgListener(queryConditionDLG);
		return queryConditionDLG;
	}
	
	private void initDlgListener(IQueryConditionDLG queryConditionDLG) {
		if (!isInitialized) {
			// 查询条件控件处理，只修改一次
			queryConditionDLG.registerCriteriaEditorListener(new ICriteriaChangedListener() {
				@Override
				public void criteriaChanged(CriteriaChangedEvent event) {
					String fieldCode = event.getFieldCode();
					if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
						if (CostShareVO.PK_ORG.equals(
								fieldCode)) {
							UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
							// 允许参照出已停用的组织
							refPane.getRefModel().setDisabledDataShow(true);
							// 不受数据权限控制
							refPane.getRefModel().setUseDataPower(false);
							// 功能权限过滤
							ERMQueryActionHelper.filtOrgsForQueryAction(event, getModel().getContext().getPkorgs());
							// 获得个性化中默认主组织
							String pk_org = ErUiUtil.getDefaultPsnOrg();
							ERMQueryActionHelper.setPk(event, pk_org, false);
							
						}else if (CostShareVO.PK_TRADETYPE.equals(
								fieldCode)) {
							// 过滤交易类型参照,条件为当前集团下的费用结转单交易类型
							UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
							refPane.getRefModel().setWherePart(
									"parentbilltype = '"
											+ ErmBillConst.CostShare_BILLTYPE
											+ "'");
						}else if (CostShareVO.BILLDATE.equals(fieldCode
							// 制单日期
						)) {
							UFDate currDate = ErUiUtil.getBusiDate();
							UFDate beginDate = currDate.getDateBefore(30);
							
							UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
							refPane.setValueObjFireValueChangeEvent(beginDate);
							UIRefPane rightrefPane = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
							rightrefPane.setValueObjFireValueChangeEvent(currDate);
						}else if (CostShareVO.FYDWBM.equals(fieldCode)) {

							ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
						}else if (CostShareVO.FYDEPTID.equals(event.getFieldCode())
								||CostShareVO.SZXMID.equals(fieldCode)
								||CostShareVO.HBBM.equals(fieldCode)
								||CostShareVO.JOBID.equals(fieldCode)
								||CostShareVO.CUSTOMER.equals(fieldCode)){
							setItemFilterByFydw(event);
						} else if((CSField+CShareDetailVO.ASSUME_DEPT).equals(fieldCode)
									|| (CSField+CShareDetailVO.PK_IOBSCLASS).equals(fieldCode)
									||(CSField+CShareDetailVO.JOBID).equals(fieldCode)||
									(CSField+CShareDetailVO.HBBM).equals(fieldCode)||
									(CSField+CShareDetailVO.CUSTOMER).equals(fieldCode)){
								setItemFilterByAssume(event);
						} else if((CSField+CShareDetailVO.ASSUME_ORG).equals(fieldCode)){
							ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
						} else if(CostShareVO.CASHPROJ.equals(fieldCode)||CostShareVO.CASHITEM.equals(fieldCode)){
							UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
							setRefpaneFilter(ref,null);
						}else if(CostShareVO.BX_PCORG.equals(event.getFieldCode())){
							ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
						}else if(CostShareVO.PK_CHECKELE.equals(event.getFieldCode())
								|| CostShareVO.PK_RESACOSTCENTER.equals(fieldCode)){
							setItemFilterBypcorg(event);
						}else if((CSField+CShareDetailVO.PK_PCORG).equals(event.getFieldCode())){
							ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
						}else if((CSField+CShareDetailVO.PK_CHECKELE).equals(event.getFieldCode())
								|| (CSField+CShareDetailVO.PK_RESACOSTCENTER).equals(event.getFieldCode())){
							setItemFilterBycspcorg(event);
						}
						setUserdefItemFilter(event);
					}else if(event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED){
						if(CostShareVO.FYDWBM.equals(fieldCode)){
							//根据费用承担单位过滤
							setFydwFilter(event);
						}else if((CSField+CShareDetailVO.ASSUME_ORG).equals(fieldCode)){
							//根据分摊明细承担单位过滤
							setassumeFilter(event);
						}else if(CostShareVO.BX_PCORG.equals(event.getFieldCode())){
							//根据利润中心过滤核算要素
							setpcorgFilter(event);
						}else if((CSField+CShareDetailVO.PK_PCORG).equals(event.getFieldCode())){
							setcspcorgFilter(event);
						}
						setUserdefItemChangedOrg(event);
					}else if(event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED){
						if (CostShareVO.FYDWBM.equals(event.getFieldCode())){
							setFydwFilter(event);
						}else if((CSField+CShareDetailVO.ASSUME_ORG).equals(fieldCode)){
							setassumeFilter(event);
						}else if(CostShareVO.BX_PCORG.equals(event.getFieldCode())){
							setpcorgFilter(event);
						}else if((CSField+CShareDetailVO.PK_PCORG).equals(event.getFieldCode())){
							setcspcorgFilter(event);
						}
						setUserdefItemChangedOrg(event);
					}
				}
			});
			isInitialized = true;
		}
	}
	
	
	/**
	 * 当对应组织变化后，自定义项的处理
	 * wangle
	 */
	private void setUserdefItemChangedOrg(CriteriaChangedEvent event){
		if((CSField+CShareDetailVO.ASSUME_ORG).equals(event.getFieldCode())){
			UIRefPane assume = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, CSField
					+ CShareDetailVO.ASSUME_ORG, false);
			setItemByPk_org(event, bodyDefitem, assume);
		}else if(CostShareVO.FYDWBM.equals(event.getFieldCode())){
			UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					CostShareVO.FYDWBM, false);
			setItemByPk_org(event, headDefitem, fydw);
		}
	}
	
	/**
	 * wangle
	 * 处理自定义项字段的过滤，
	 */
	private void setUserdefItemFilter(CriteriaChangedEvent event){
		String key = event.getFieldCode();
		IFilterMeta filterMeta = event.getFiltereditor().getFilter().getFilterMeta();
		int dataType = ((FilterMeta)filterMeta).getDataType();
		if(dataType==5 ){
			if(key.startsWith("defitem")){
				setItemFilterByFydw(event);//表头的自定义项按表头的费用承担单位过滤
			}else if(key.contains("defitem") && !key.startsWith("defitem")){
				setItemFilterByAssume(event);//表体的自定义项按表体的承担单位过滤
			}
		}
	}

	protected void setcspcorgFilter(CriteriaChangedEvent cspcorgevent) {
		String[] headItems = new String[]{CSField+CShareDetailVO.PK_CHECKELE,CSField+CShareDetailVO.PK_RESACOSTCENTER};
		UIRefPane pcorg = (UIRefPane)ERMQueryActionHelper.getFiltComponentForValueChanged(cspcorgevent, CSField+CShareDetailVO.PK_PCORG, false);
		setItemByPk_org(cspcorgevent, headItems,pcorg);
		
	}

	private void setpcorgFilter(CriteriaChangedEvent pcorgevent) {
		String[] headItems = new String[]{CostShareVO.PK_CHECKELE,CostShareVO.PK_RESACOSTCENTER};
		UIRefPane pcorg = (UIRefPane)ERMQueryActionHelper.getFiltComponentForValueChanged(pcorgevent, CostShareVO.BX_PCORG, false);
		setItemByPk_org(pcorgevent, headItems,pcorg);
		
	}
	
	private void setItemByPk_org(CriteriaChangedEvent pcorgevent, String[] headItems, UIRefPane pcorg) {
		for (int i = 0; i < headItems.length; i++) {
			JComponent[] components = ERMQueryActionHelper.getFiltComponentsForValueChanged(pcorgevent, headItems[i],
					false);
			if (components != null && components.length > 0) {
				for (JComponent component : components) {
					if (component instanceof UIRefPane) {
						setRefpaneFilter((UIRefPane) component, pcorg);
					}
				}
			}
		}
	}
	protected void setItemFilterBycspcorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				CSField+CShareDetailVO.PK_PCORG, false);
			setRefpaneFilter(ref,pk_fwdwRefPane);
		
	}
	
	private void setItemFilterBypcorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				CostShareVO.BX_PCORG, false);
			setRefpaneFilter(ref,pk_fwdwRefPane);
		
	}
	
	protected void setItemFilterByAssume(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane assumeRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				(CSField+CShareDetailVO.ASSUME_ORG), false);
		setRefpaneFilter(ref,assumeRefPane);
	}

	private void setItemFilterByFydw(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				CostShareVO.FYDWBM, false);
		
		setRefpaneFilter(ref,pk_fwdwRefPane);
		
	}
	
	private void setFydwFilter(CriteriaChangedEvent fydwevent) {
		UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent,
				CostShareVO.FYDWBM, false);
		String[] headItems = new String[] {CostShareVO.FYDEPTID,
				CostShareVO.FYDEPTID_V, CostShareVO.SZXMID, CostShareVO.HBBM, CostShareVO.JOBID, CostShareVO.CUSTOMER };
		setItemByPk_org(fydwevent, headItems, fydw);
	}
	
	private void setassumeFilter(CriteriaChangedEvent assumeevent) {
		UIRefPane assume = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(assumeevent, CSField
				+ CShareDetailVO.ASSUME_ORG, false);
		String[] headItems = new String[] { CSField + CShareDetailVO.ASSUME_DEPT,
				CSField + CShareDetailVO.PK_IOBSCLASS,
				CSField + CShareDetailVO.JOBID, CSField + CShareDetailVO.HBBM, CSField + CShareDetailVO.CUSTOMER };
		setItemByPk_org(assumeevent, headItems, assume);
	}

	private void setRefpaneFilter(UIRefPane ref, UIRefPane dwRefpane) {
		if (dwRefpane == null || (dwRefpane != null && dwRefpane.getRefPKs()==null)) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
			ref.setMultiRefFilterPKs(null);
			ref.setPk_org(null);
		}else if(dwRefpane != null && dwRefpane.getRefPKs()!=null && dwRefpane.getRefPKs().length == 1){
			ref.setMultiOrgSelected(false);
			ref.setMultiCorpRef(false);
			ref.setMultiSelectedEnabled(true);
			ref.setPk_org(dwRefpane.getRefPK());
			if(CostShareVO.PK_RESACOSTCENTER.equals(ref.getRefModel().getPkFieldCode())){
				String addWherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+dwRefpane.getRefPK()+"'"; 
				ref.getRefModel().addWherePart(" and " + addWherePart);
			}
			if((CSField+CShareDetailVO.PK_RESACOSTCENTER).equals(ref.getRefModel().getPkFieldCode())){
				String addWherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+dwRefpane.getRefPK()+"'"; 
				ref.getRefModel().addWherePart(" and " + addWherePart);
			}
		}else if(dwRefpane != null && dwRefpane.getRefPKs()!=null && dwRefpane.getRefPKs().length >1 ){
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
			ref.setMultiRefFilterPKs(dwRefpane.getRefPKs());
			ref.setPk_org(dwRefpane.getRefPKs()[0]);
			if(CostShareVO.PK_RESACOSTCENTER.equals(ref.getRefModel().getPkFieldCode())){
				String addWherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+dwRefpane.getRefPK()+"'"; 
				ref.getRefModel().addWherePart(" and " + addWherePart);
			}
			if((CSField+CShareDetailVO.PK_RESACOSTCENTER).equals(ref.getRefModel().getPkFieldCode())){
				String addWherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+dwRefpane.getRefPK()+"'"; 
				ref.getRefModel().addWherePart(" and " + addWherePart);
			}
		}
	}

	public void setListView(CsListView listView) {
		this.listView = listView;
	}

	public CsListView getListView() {
		return listView;
	}
	
	

}
