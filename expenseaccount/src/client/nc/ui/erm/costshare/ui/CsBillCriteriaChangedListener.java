package nc.ui.erm.costshare.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.meta.IFilterMeta;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;

/**
 * <p>
 * 参加报销单对话框字段控制
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author luolch
 * @version V6.3
 * @since V6.3 创建时间：2013-1-7 下午03:31:16
 */
public class CsBillCriteriaChangedListener implements ICriteriaChangedListener {

	private AbstractUIAppModel model = null;
	private BusiTypeVO busTypeVO = null;

	public CsBillCriteriaChangedListener(AbstractUIAppModel model) {
		this.setModel(model);
	}

	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			String pk_org = ErUiUtil.getDefaultPsnOrg();
			if (event.getFieldCode().equals(JKBXHeaderVO.PK_ORG)) {
				// 主组织权限过滤
				String[] permissionOrgs = getModel().getContext().getPkorgs();
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (event.getFieldCode().equals(JKBXHeaderVO.DJRQ)) {
				UIRefPane leftDate = (UIRefPane)

				ERMQueryActionHelper.getFiltComponentForInit(event);
				UIRefPane rightDate = (UIRefPane)

				ERMQueryActionHelper.getFiltRightComponentForInit(event);
				UFDate busiDate =

				WorkbenchEnvironment.getInstance().getBusiDate();
				leftDate.setValueObjFireValueChangeEvent

				(busiDate.getDateBefore(30));
				rightDate.setValueObjFireValueChangeEvent

				(busiDate);
			} else if (event.getFieldCode().equals(JKBXHeaderVO.DJLXBM)) {
				// 过滤交易类型范围
				 UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				 refPane.setWhereString(" parentbilltype in ('264X') and pk_billtypecode <>'2647'");
			} else if (JKBXHeaderVO.FYDWBM.equals(event.getFieldCode())) {

				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (JKBXHeaderVO.FYDEPTID.equals(event.getFieldCode())
					|| JKBXHeaderVO.SZXMID.equals(event.getFieldCode())
					|| JKBXHeaderVO.JOBID.equals(event.getFieldCode())
					|| JKBXHeaderVO.HBBM.equals(event.getFieldCode())
					|| JKBXHeaderVO.CUSTOMER.equals(event.getFieldCode())) {
				setItemFilterByFydw(event);
			} else if (JKBXHeaderVO.DWBM.equals(event.getFieldCode())) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (JKBXHeaderVO.DEPTID.equals(event.getFieldCode())
					|| JKBXHeaderVO.RECEIVER.equals(event.getFieldCode())
					|| JKBXHeaderVO.SKYHZH.equals(event.getFieldCode())
					|| JKBXHeaderVO.JKBXR.equals(event.getFieldCode())) {
				setItemFilterBydwbm(event);
			} else if (JKBXHeaderVO.PK_PAYORG.equals(event.getFieldCode())) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (JKBXHeaderVO.CASHPROJ.equals(event.getFieldCode())
					|| JKBXHeaderVO.CASHITEM.equals(event.getFieldCode())
					|| JKBXHeaderVO.PK_CASHACCOUNT.equals(event.getFieldCode())) {
				setItemFilterBypayorg(event);
			} else if (JKBXHeaderVO.PK_PCORG.equals(event.getFieldCode())) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (JKBXHeaderVO.PK_CHECKELE.equals(event.getFieldCode())||
					 JKBXHeaderVO.PK_RESACOSTCENTER.equals(event.getFieldCode())) {
				setItemFilterBypcorg(event);
			}
			//处理自定义字段
			setUserdefItemFilter(event);
		} else if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
			if (event.getFieldCode().equals(JKBXHeaderVO.PK_ORG)) {
				UIRefPane pk_orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
						JKBXHeaderVO.PK_ORG,

						false);
				if (pk_orgRefPane == null) {
					return;
				}
			} else if (event.getFieldCode().equals(JKBXHeaderVO.DWBM)) {
				// 对借款人部门和借款人来进行过滤
				setdwbmFilter(event);
			} else if (JKBXHeaderVO.FYDWBM.equals(event.getFieldCode())) {
				// 根据费用承担单位过滤
				setFydwFilter(event);
			} else if (JKBXHeaderVO.PK_PAYORG.equals(event.getFieldCode())) {
				setpayorgFilter(event);
			} else if (JKBXHeaderVO.PK_PCORG.equals(event.getFieldCode())) {
				setpcorgFilter(event);
			}
			setUserdefItemChangedOrg(event);
		} else if (event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
			if (JKBXHeaderVO.FYDWBM.equals(event.getFieldCode())) {
				setFydwFilter(event);
			} else if (JKBXHeaderVO.DWBM.equals(event.getFieldCode())) {
				setdwbmFilter(event);
			} else if (JKBXHeaderVO.PK_PAYORG.equals(event.getFieldCode())) {
				setpayorgFilter(event);
			} else if (JKBXHeaderVO.PK_PCORG.equals(event.getFieldCode())) {
				setpcorgFilter(event);
			}
			setUserdefItemChangedOrg(event);
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
			if(getBusiTypeVO().getId().startsWith("264")){
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
	 *获得交易类型VO
	 * @return
	 */
	private BusiTypeVO getBusiTypeVO() {
			if(busTypeVO==null){
				String transtype=BXConstans.BX_DJLXBM;//管理节点用单据类型
				String djdl=BXConstans.BX_DJDL;
				busTypeVO=BXUtil.getBusTypeVO(transtype,djdl);
			}
		return busTypeVO;
	}
	
	private void setItemFilterBypk_org(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_orgrefane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_ORG, false);
		setRefpaneFilter(ref, pk_orgrefane);
	}

	private void setItemFilterBypcorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_PCORG, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	private void setpcorgFilter(CriteriaChangedEvent pcorgevent) {
		String[] headItems = new String[] { JKBXHeaderVO.PK_RESACOSTCENTER,JKBXHeaderVO.PK_CHECKELE };
		UIRefPane pcorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(pcorgevent,
				JKBXHeaderVO.PK_PCORG, false);
		setItemByorg(pcorgevent, headItems, pcorg);

	}

	private void setItemFilterByFydw(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.FYDWBM, false);

		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	private void setItemFilterBydwbm(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.DWBM, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);
	}

	private void setFydwFilter(CriteriaChangedEvent fydwevent) {
		String[] headItems = new String[] {JKBXHeaderVO.FYDEPTID,
				JKBXHeaderVO.FYDEPTID_V, JKBXHeaderVO.SZXMID, JKBXHeaderVO.HBBM, JKBXHeaderVO.JOBID,
				JKBXHeaderVO.CUSTOMER };
		UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent,
				JKBXHeaderVO.FYDWBM, false);
		setItemByorg(fydwevent, headItems, fydw);
	}

	private void setpayorgFilter(CriteriaChangedEvent payorgevent) {
		String[] headItems = new String[] { JKBXHeaderVO.CASHPROJ, JKBXHeaderVO.CASHITEM,JKBXHeaderVO.PK_CASHACCOUNT};
		UIRefPane payorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(payorgevent,
				JKBXHeaderVO.PK_PAYORG, false);
		setItemByorg(payorgevent, headItems, payorg);

	}

	private void setdwbmFilter(CriteriaChangedEvent dwbmevent) {
		String[] headItems = new String[] { JKBXHeaderVO.SKYHZH, JKBXHeaderVO.RECEIVER, JKBXHeaderVO.DEPTID,
				JKBXHeaderVO.DEPTID_V, JKBXHeaderVO.JKBXR };
		UIRefPane dwbm = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(dwbmevent, JKBXHeaderVO.DWBM,
				false);
		setItemByorg(dwbmevent, headItems, dwbm);

	}

	private void setItemByorg(CriteriaChangedEvent dwbmevent, String[] headItems, UIRefPane dwbm) {
		for (int i = 0; i < headItems.length; i++) {

			JComponent[] components = ERMQueryActionHelper.getFiltComponentsForValueChanged(dwbmevent, headItems[i],
					false);
			if (components != null && components.length > 0) {
				for (JComponent component : components) {
					if (component instanceof UIRefPane) {
						setRefpaneFilter((UIRefPane) component, dwbm);
					}
				}
			}
		}
	}

	private void setItemFilterBypayorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_PAYORG, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	/**
	 * 费用承担单位和借款报销单位对应所属字段的过滤处理
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
