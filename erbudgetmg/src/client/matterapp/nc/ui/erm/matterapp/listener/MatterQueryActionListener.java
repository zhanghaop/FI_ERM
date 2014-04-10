package nc.ui.erm.matterapp.listener;

import java.util.List;

import javax.swing.JComponent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.busi.UserDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.matterapp.common.MultiVersionUtils;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 费用申请单查询框监听
 * 
 * @author chenshuaia
 * 
 */
public class MatterQueryActionListener implements ICriteriaChangedListener {
	private AbstractUIAppModel model = null;

	private String[] pk_orgs = null;

	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		String key = event.getFieldCode();
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			if (key.equals(MatterAppVO.PK_ORG_V)) {
				// 主组织权限过滤
				String[] permissionOrgs = BXUiUtil.getPermissionOrgVs(getModel().getContext().getNodeCode());
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);

				UIRefPane orgPanel = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				try {
					// 设置默认值
					String pk_org_v = MultiVersionUtils.getHeadOrgMultiVersion(ErUiUtil.getDefaultPsnOrg(),
							ErUiUtil.getBusiDate(), orgPanel.getRefModel());
					ERMQueryActionHelper.setPk(event, pk_org_v, false);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			} else if (key.equals(MatterAppVO.PK_ORG)) {
				// 主组织权限过滤
				String[] permissionOrgs = getModel().getContext().getPkorgs();
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);
				// 设置默认值
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (key.equals(MatterAppVO.BILLDATE)) {
				UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
				UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
				leftDate.setValueObjFireValueChangeEvent(busiDate.getDateBefore(30));
				rightDate.setValueObjFireValueChangeEvent(busiDate);
			} else if (MatterAppVO.PK_TRADETYPE.equals(key)) {
				// 过滤交易类型参照,条件为当前集团下的费用结转单交易类型
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				refPane.getRefModel().setWherePart("parentbilltype = '" + ErmMatterAppConst.MatterApp_BILLTYPE + "'");
			} else if (key != null && key.contains(MtAppDetailVO.PK_RESACOSTCENTER)) {// 成本中心过滤
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				try {
					refPane.getRefModel().addWherePart(" and " + SqlUtils.getInStr("pk_financeorg", pk_orgs, false));
					setRefpaneFilter((UIRefPane) refPane);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			} else if (MatterAppVO.CREATOR.equals(key) || MatterAppVO.APPROVER.equals(key)
					|| MatterAppVO.PRINTER.equals(key) || MatterAppVO.CLOSEMAN.equals(key)
					|| MatterAppVO.MODIFIER.equals(key) || (key != null && key.contains(MatterAppVO.CLOSEMAN))) {// 用户根据集团过滤，不按组织过滤
				UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

				if (ref != null && ref.getRefModel() != null && ref.getRefModel() instanceof UserDefaultRefModel) {
					ref.getRefModel().setPk_org(getModel().getContext().getPk_group());
					((UserDefaultRefModel) ref.getRefModel()).setUserType(1);
				}
			} else {
				Object obj = ERMQueryActionHelper.getFiltComponentForInit(event);
				if (obj != null && obj instanceof UIRefPane) {

					UIRefPane refPane = (UIRefPane) obj;

					if (refPane.getRefModel() != null) {
						refPane.setMultiSelectedEnabled(true);
						setRefpaneFilter((UIRefPane) refPane);
					}
				}
			}
		}

		if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
			if (key.equals(MatterAppVO.PK_ORG)) {// 设置组织过滤
				setPkOrgFilter(event);
				fiterCostCenter(event);
			}
		}
	}

	/**
	 * 过滤业务行中查询条件
	 * 
	 * @param event
	 */
	private void fiterCostCenter(CriteriaChangedEvent event) {
		List<IFilterEditor> filterList = ERMQueryActionHelper.getIFilterEditorList(event,
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_RESACOSTCENTER);// 成本中心
		if (filterList != null) {
			JComponent[] components = ERMQueryActionHelper.getComps(filterList, false);

			if (components != null && components.length > 0) {

				for (JComponent commont : components) {
					if (commont instanceof UIRefPane) {
						UIRefPane refpane = (UIRefPane) commont;
						if (refpane.getRefModel() != null) {
							try {
								refpane.getRefModel().addWherePart(
										" and " + SqlUtils.getInStr("pk_financeorg", pk_orgs, false));
							} catch (BusinessException e) {
								ExceptionHandler.consume(e);
							}
						}
					}
				}
			}
		}
	}

	private void setPkOrgFilter(CriteriaChangedEvent orgevent) {
		UIRefPane orgRef = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(orgevent,
				MatterAppVO.PK_ORG, false);

		if (orgRef == null) {
			return;
		}

		String[] pk_orgs = orgRef.getRefPKs();

		if (pk_orgs == null || pk_orgs.length == 0) {// 如果没有选择主组织，则取用户分配组织的基本档案数据
			pk_orgs = getModel().getContext().getPkorgs();
		}

		this.pk_orgs = pk_orgs;
		setOrgFilter(orgevent);
	}

	private void setOrgFilter(CriteriaChangedEvent orgevent) {
		String[] headItems = AggMatterAppVO.getApplyOrgHeadIterms().toArray(new String[] {});
		for (int i = 0; i < headItems.length; i++) {
			JComponent[] components = ERMQueryActionHelper.getFiltComponentsForValueChanged(orgevent, headItems[i],
					false);

			if (components != null && components.length > 0) {
				for (JComponent component : components) {
					if (component instanceof UIRefPane) {
						setRefpaneFilter((UIRefPane) component);
					}
				}
			}
		}

		// 表体明细过滤pk_org
		List<String> bodyItems = AggMatterAppVO.getApplyOrgBodyIterms();
		for (int i = 0; i < bodyItems.size(); i++) {
			JComponent[] components = ERMQueryActionHelper.getFiltComponentsForValueChanged(orgevent,
					ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + bodyItems.get(i), false);
			if (components != null && components.length > 0) {
				for (JComponent component : components) {
					if (component instanceof UIRefPane) {
						setRefpaneFilter((UIRefPane) component);
					}
				}
			}
		}
	}

	/**
	 * 设置
	 * 
	 * @param ref
	 */
	private void setRefpaneFilter(UIRefPane ref) {
		if (ref == null) {
			return;
		}

		if (this.pk_orgs == null) {
			this.pk_orgs = getModel().getContext().getPkorgs();
		}

		if (pk_orgs == null || pk_orgs.length == 0) {
			return;
		}

		if (pk_orgs != null && pk_orgs.length > 1) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
		} else {
			ref.setMultiOrgSelected(false);
			ref.setMultiCorpRef(false);
		}
		ref.setPk_org(pk_orgs[0]);
		ref.setMultiRefFilterPKs(pk_orgs);
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

}
