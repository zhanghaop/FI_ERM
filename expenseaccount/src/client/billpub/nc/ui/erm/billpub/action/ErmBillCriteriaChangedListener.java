package nc.ui.erm.billpub.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.busi.UserDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.util.TransTypeUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.queryarea.quick.QuickQueryArea;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.meta.IFilterMeta;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;

/**
 * <p>
 * TODO 接口/类功能说明，使用说明（接口是否为服务组件，服务使用者，类是否线程安全等）。
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author wangled
 * @version V6.3
 * @since V6.3 创建时间：2013-1-7 下午03:31:16
 */
public class ErmBillCriteriaChangedListener implements ICriteriaChangedListener {

	private AbstractUIAppModel model = null;
	private String BUSITEM = "jk_busitem.";
	private BusiTypeVO busTypeVO = null;

	public ErmBillCriteriaChangedListener(AbstractUIAppModel model) {
		this.setModel(model);
	}

	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		String key = event.getFieldCode();
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			String pk_org = ErUiUtil.getDefaultPsnOrg();
			if (key.equals(JKBXHeaderVO.DWBM)) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (key.equals(JKBXHeaderVO.FYDWBM)) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (key.equals(JKBXHeaderVO.PK_ORG)) {
				// 主组织权限过滤
				String[] permissionOrgs = getModel().getContext().getPkorgs();
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);
				if(permissionOrgs==null || permissionOrgs.length == 0
						||!Arrays.asList(permissionOrgs).contains(pk_org)){
					ERMQueryActionHelper.setPk(event, null, false);
				}else{
					ERMQueryActionHelper.setPk(event, pk_org, false);
				}
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG)) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG)) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			}else if (key.equals(JKBXHeaderVO.DJRQ)) {
				try {
					if (((ErmBillBillManageModel) getModel()).isInit()) {
						UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
						UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
						if (pk_org == null) {
							return;
						}
						UFDate dateStart = BXUiUtil.getStartDate(pk_org);
						if (dateStart != null) {
	                        UFDate dateBefore = BXUiUtil.getStartDate(pk_org).getDateBefore(1);
	                        leftDate.setValueObjFireValueChangeEvent(dateBefore.getDateBefore(30));
	                        rightDate.setValueObjFireValueChangeEvent(dateBefore);						    
						}
					} else {
						UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
						UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
						UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
						leftDate.setValueObjFireValueChangeEvent(busiDate.getDateBefore(30));
						rightDate.setValueObjFireValueChangeEvent(busiDate);
					}

				} catch (BusinessException e) {
					ExceptionHandler.handleExceptionRuntime(e);
				}
			} else if (key.equals(JKBXHeaderVO.DJLXBM)) {
				// 过滤交易类型范围
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				if (((ErmBillBillManageModel) getModel()).isInit()) {
					refPane.setWhereString(" parentbilltype in ('263X')");
				} else {
					refPane.setWhereString(" parentbilltype in ('263X','264X')");
				}
			} else {
				if (key.equals(JKBXHeaderVO.PK_RESACOSTCENTER) || JKBXHeaderVO.FYDEPTID.equals(key)
						|| JKBXHeaderVO.FYDEPTID_V.equals(key) || JKBXHeaderVO.SZXMID.equals(key)
						|| JKBXHeaderVO.HBBM.equals(key) || JKBXHeaderVO.CUSTOMER.equals(key)
						|| (BUSITEM + BXBusItemVO.SZXMID).equals(event.getFieldCode())) {
					setItemFilterByFydw(event);

				} 
//				else if (key.equals(JKBXHeaderVO.PK_CASHACCOUNT)) {
//					UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
//
//					UIRefPane pk_orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
//							JKBXHeaderVO.PK_ORG, false);
//					if (pk_orgRefPane == null) {
//						return;
//					}
//					if (ref != null && ref.getRefModel() != null) {
//						ref.getRefModel().setPk_org(pk_orgRefPane.getRefPK());
//					}
//				} 
				else if (key.equals(JKBXHeaderVO.PAYMAN) || key.equals(JKBXHeaderVO.OPERATOR)
						|| key.equals(JKBXHeaderVO.APPROVER) || key.equals(JKBXHeaderVO.JSR)
						|| key.equals(JKBXHeaderVO.OFFICIALPRINTUSER)) {
					UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

					if (ref != null && ref.getRefModel() != null && ref.getRefModel() instanceof UserDefaultRefModel) {
						ref.getRefModel().setPk_org(getModel().getContext().getPk_group());
						((UserDefaultRefModel) ref.getRefModel()).setUserType(1);
					}
				} else if (JKBXHeaderVO.SKYHZH.equals(key) || JKBXHeaderVO.RECEIVER.equals(key)
						|| JKBXHeaderVO.DEPTID.equals(key) || JKBXHeaderVO.DEPTID_V.equals(key)
						|| JKBXHeaderVO.JKBXR.equals(key)) {
					setItemFilterBydwbm(event);
				} else if (JKBXHeaderVO.CASHPROJ.equals(key) || JKBXHeaderVO.CASHITEM.equals(key)
						|| key.equals(JKBXHeaderVO.PK_CASHACCOUNT)) {
					setItemFilterBypayorg(event);
				} else if (JKBXHeaderVO.PK_CHECKELE.equals(key)) {
					setItemFilterBypcorg(event);
				}
			}

			//处理自定义字段
			if(!(event.getCriteriaEditor() instanceof QuickQueryArea)){
				setUserdefItemFilter(event);
			}
		} else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
			if (key.equals(JKBXHeaderVO.PK_ORG)) {
				UIRefPane pk_orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
						JKBXHeaderVO.PK_ORG, false);
				if (pk_orgRefPane == null) {
					return;
				}
				if (((ErmBillBillManageModel) getModel()).isInit()) {
					String pk_org = pk_orgRefPane.getRefPK();
					if (pk_org == null) {
						return;
					}
					UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
							JKBXHeaderVO.DJRQ, false);
					UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
							JKBXHeaderVO.DJRQ, true);
					UFDate dateBefore = null;
					try {
					    UFDate dateStart = BXUiUtil.getStartDate(pk_org);
					    if (dateStart != null) {
					        dateBefore = dateStart.getDateBefore(1);
					    }
						if (leftDate != null && dateBefore != null) {
							leftDate.setValueObjFireValueChangeEvent(dateBefore.getDateBefore(30));
						}
						if (rightDate != null && dateBefore != null) {
							rightDate.setValueObjFireValueChangeEvent(dateBefore);
						}
					} catch (BusinessException e) {
						ExceptionHandler.handleExceptionRuntime(e);
					}
				}
				// 根据主组织过滤
				//setPkOrgFilter(event);
			} else if (key.equals(JKBXHeaderVO.DWBM)) {
				// 对借款人部门和借款人来进行过滤
				setDwbmFilter(event);
			} else if (key.equals(JKBXHeaderVO.FYDWBM)) {
				// 根据费用承担单位过滤
				setFydwFilter(event);
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG)) {
				setpayorgFilter(event);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG)) {
				setpcorgFilter(event);
			}
			setUserdefItemChangedOrg(event);
		} else if (event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
			if (JKBXHeaderVO.FYDWBM.equals(event.getFieldCode())) {
				setFydwFilter(event);
			} else if (JKBXHeaderVO.DWBM.equals(event.getFieldCode())) {
				setDwbmFilter(event);
			} else if (JKBXHeaderVO.PK_PAYORG.equals(event.getFieldCode())) {
				setpayorgFilter(event);
			} else if (JKBXHeaderVO.PK_PCORG.equals(event.getFieldCode())) {
				setpcorgFilter(event);
			}
			setUserdefItemChangedOrg(event);
		}
	}
	
	/**
	 * 当对应组织变化后，自定义项的处理
	 * wangle
	 */
	private void setUserdefItemChangedOrg(CriteriaChangedEvent event){
		if (JKBXHeaderVO.FYDWBM.equals(event.getFieldCode())) {
			List<String> costentity_billitems = getBusiTypeVO().getCostentity_billitems();
			UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					JKBXHeaderVO.FYDWBM, false);
			setItemByorg(event, costentity_billitems.toArray(new String[0]), fydw);
		} else if (JKBXHeaderVO.DWBM.equals(event.getFieldCode())) {
			UIRefPane dwbm = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, JKBXHeaderVO.DWBM,
					false);
			List<String> useentity_billitems = getBusiTypeVO().getUseentity_billitems();
			setItemByorg(event, useentity_billitems.toArray(new String[0]), dwbm);
		} else if (JKBXHeaderVO.PK_PAYORG.equals(event.getFieldCode())) {
			
			UIRefPane payorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					JKBXHeaderVO.PK_PAYORG, false);
			List<String> payorgentity_billitems = getBusiTypeVO().getPayorgentity_billitems();
			setItemByorg(event, payorgentity_billitems.toArray(new String[0]), payorg);
		} else if (JKBXHeaderVO.PK_ORG.equals(event.getFieldCode())) {
			List<String> payentity_billitems = getBusiTypeVO().getPayentity_billitems();
			List<String> new_billitems = new ArrayList<String>();
			//在财务组织改变后，需要对表体的自定义项处理
			if(getBusiTypeVO().getId().startsWith("263")){
				for (String item : payentity_billitems) {
					item="jk_busitem."+item;
					new_billitems.add(item);
				}
			}else{
				for (String item : payentity_billitems) {
					item="er_busitem."+item;
					new_billitems.add(item);
				}
			}
			UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					JKBXHeaderVO.PK_ORG, false);
			setItemByorg(event, new_billitems.toArray(new String[0]), fydw);
		}
	}
	
	
	/**
	 * 处理自定义项字段的过滤，按照busitype.xml
	 * wangle
	 */
	private void setUserdefItemFilter(CriteriaChangedEvent event){
		String key = event.getFieldCode();
		if((key.startsWith("zyx")||key.contains("defitem"))){
			IFilterMeta filterMeta = event.getFiltereditor().getFilter().getFilterMeta();
			int dataType = ((FilterMeta)filterMeta).getDataType();
			if(dataType==5){
				if(key.contains("defitem")){
					key=key.split("[.]")[1];
				}
				List<String> costentity_billitems = getBusiTypeVO().getCostentity_billitems();
				List<String> payentity_billitems = getBusiTypeVO().getPayentity_billitems();
				List<String> useentity_billitems = getBusiTypeVO().getUseentity_billitems();
				List<String> payorgentity_billitems = getBusiTypeVO().getPayorgentity_billitems();
				if (costentity_billitems.contains(key)) {//根据费用承担单位过滤自定义项
					setItemFilterByFydw(event);
				} else if (payentity_billitems.contains(key)) {//根据借款报销单位过滤自定义项
					setItemFilterBypk_org(event);
				} else if (useentity_billitems.contains(key)) {//根据借款报销人单位过滤自定义项
					setItemFilterBydwbm(event);
				} else if(payorgentity_billitems.contains(key)){//根据支付单位过滤自定义项
					setItemFilterBypayorg(event);
				}
			}
		}
	}
	
	/**
	 *获得交易类型VO
	 * @return
	 */
	private BusiTypeVO getBusiTypeVO() {
			if(busTypeVO==null){
				String nodeCode = getModel().getContext().getNodeCode();
				String transtype =null;
				String djdl =null;
				if(!nodeCode.equals(BXConstans.BXLR_QCCODE)&& !nodeCode.equals(BXConstans.BXMNG_NODECODE)&& !nodeCode.equals(BXConstans.BXBILL_QUERY) &&
				        !nodeCode.equals(BXConstans.BXINIT_NODECODE_G) && !nodeCode.equals(BXConstans.BXINIT_NODECODE_U)){
				     transtype = TransTypeUtil.getTranstype((AbstractAppModel)getModel());
				     if(transtype.startsWith(BXConstans.BX_PREFIX)){
				    	 djdl=BXConstans.BX_DJDL;
				     }else{
				    	 djdl=BXConstans.JK_DJDL;
				     }
				}else{
						transtype=BXConstans.JK_DJLXBM;//管理节点用单据类型
						djdl=BXConstans.JK_DJDL;
					}
				busTypeVO=BXUtil.getBusTypeVO(transtype,djdl);
			}
		return busTypeVO;
	}
	
	/**
	 * 财务组织相关字段过滤
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	private void setPkOrgFilter(CriteriaChangedEvent event) {
		UIRefPane org = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, JKBXHeaderVO.PK_ORG,
				false);
		String[] headItems = new String[] { JKBXHeaderVO.PK_CASHACCOUNT };
		if (org == null) {
			return;
		}
		String pk_org = org.getRefPK();

		for (int i = 0; i < headItems.length; i++) {
			JComponent[] refs =  ERMQueryActionHelper.getFiltComponentsForValueChanged(event, headItems[i],
					false);
			if (refs != null && refs.length > 0) {
				for (JComponent ref : refs) {
					if (ref != null && ((UIRefPane)ref).getRefModel() != null) {
						((UIRefPane)ref).getRefModel().setPk_org(pk_org);
					}
				}
			}
		}
	}

	/**
	 * 处理利润中心相关字段
	 * 
	 * @param event
	 */
	private void setItemFilterBypcorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_PCORG, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	private void setpcorgFilter(CriteriaChangedEvent pcorgevent) {
		String[] headItems = new String[] { JKBXHeaderVO.PK_CHECKELE };
		UIRefPane pcorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(pcorgevent,
				JKBXHeaderVO.PK_PCORG, false);
		setItemByorg(pcorgevent, headItems, pcorg);

	}

	/**
	 * 处理支付单位相关字段
	 * 
	 * @param event
	 */

	private void setItemFilterBypayorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_PAYORG, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	private void setpayorgFilter(CriteriaChangedEvent payorgevent) {

		String[] headItems = new String[] { JKBXHeaderVO.CASHPROJ, JKBXHeaderVO.CASHITEM,JKBXHeaderVO.PK_CASHACCOUNT };
		UIRefPane payorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(payorgevent,
				JKBXHeaderVO.PK_PAYORG, false);
		setItemByorg(payorgevent, headItems, payorg);

	}

	/**
	 * 费用承担单位相关字段过滤
	 * 
	 * @param fydwevent
	 */
	private void setFydwFilter(CriteriaChangedEvent fydwevent) {
		UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent,
				JKBXHeaderVO.FYDWBM, false);

		String[] headItems = new String[] { JKBXHeaderVO.PK_RESACOSTCENTER, JKBXHeaderVO.FYDEPTID,
				JKBXHeaderVO.FYDEPTID_V, JKBXHeaderVO.SZXMID, JKBXHeaderVO.HBBM, JKBXHeaderVO.JOBID,
				JKBXHeaderVO.CUSTOMER, BUSITEM + BXBusItemVO.SZXMID };
		setItemByorg(fydwevent, headItems, fydw);
	}

	private void setItemFilterByFydw(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.FYDWBM, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	/**
	 * 借款报销单位相关字段过滤
	 * 
	 * @param fydwevent
	 */
	private void setDwbmFilter(CriteriaChangedEvent dwbmevent) {
		UIRefPane dwbm = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(dwbmevent, JKBXHeaderVO.DWBM,
				false);

		String[] headItems = new String[] { JKBXHeaderVO.SKYHZH, JKBXHeaderVO.RECEIVER, JKBXHeaderVO.DEPTID,
				JKBXHeaderVO.DEPTID_V, JKBXHeaderVO.JKBXR };

		setItemByorg(dwbmevent, headItems, dwbm);

	}
	
	private void setItemFilterBypk_org(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_orgrefane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_ORG, false);
		setRefpaneFilter(ref, pk_orgrefane);
	}

	private void setItemFilterBydwbm(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.DWBM, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);
	}

	private void setItemByorg(CriteriaChangedEvent orgevent, String[] headItems, UIRefPane dwbm) {
		for (int i = 0; i < headItems.length; i++) {
			JComponent[] refs = ERMQueryActionHelper.getFiltComponentsForValueChanged(orgevent, headItems[i],
					false);
			if(refs != null && refs.length > 0 ){
				for(JComponent ref : refs){
					if (ref != null && ((UIRefPane)ref).getRefModel() != null) {
						setRefpaneFilter(((UIRefPane)ref), dwbm);
					}
				}
			}
		}
	}

	/**
	 * 对应所属字段的过滤处理
	 * 
	 * @param ref
	 * @param dwRefpane
	 */
	private void setRefpaneFilter(UIRefPane ref, UIRefPane dwRefpane) {
		if (dwRefpane == null || (dwRefpane != null && dwRefpane.getRefPKs() == null)) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
			ref.setMultiRefFilterPKs(null);
			ref.setPk_org(null);
		} else if (dwRefpane != null && dwRefpane.getRefPKs() != null && dwRefpane.getRefPKs().length == 1) {
			ref.setMultiOrgSelected(false);
			ref.setMultiCorpRef(false);
			ref.setMultiSelectedEnabled(true);
			ref.setPk_org(dwRefpane.getRefPK());
			if (JKBXHeaderVO.PK_RESACOSTCENTER.equals(ref.getRefModel().getPkFieldCode())) {
				String addWherePart = CostCenterVO.PK_FINANCEORG + "=" + "'" + dwRefpane.getRefPK() + "'";
				ref.getRefModel().addWherePart(" and " + addWherePart);
			} else if ("pk_fundplan".equals(ref.getRefModel().getPkFieldCode())) {
				ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
			}
		} else if (dwRefpane != null && dwRefpane.getRefPKs() != null && dwRefpane.getRefPKs().length > 1) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
			ref.setMultiRefFilterPKs(dwRefpane.getRefPKs());
			ref.setPk_org(dwRefpane.getRefPKs()[0]);
			if (JKBXHeaderVO.PK_RESACOSTCENTER.equals(ref.getRefModel().getPkFieldCode())) {
				String addWherePart = CostCenterVO.PK_FINANCEORG + "=" + "'" + dwRefpane.getRefPKs()[0] + "'";
				ref.getRefModel().addWherePart(" and " + addWherePart);
			} else if ("pk_fundplan".equals(ref.getRefModel().getPkFieldCode())) {
				ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
			}
		}
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

}
