package nc.ui.erm.matterapp.listener;

import java.util.List;

import javax.swing.JComponent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.busi.UserDefaultRefModel;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.matterapp.common.MultiVersionUtils;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.uap.rbac.FuncSubInfo;

/**
 * �������뵥��ѯ�����
 * 
 * @author chenshuaia
 * 
 */
public class MatterQueryActionListener implements ICriteriaChangedListener {
	private AbstractUIAppModel model = null;
	private String key = null;
	private FuncSubInfo funcSubInfo;

	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		this.key = event.getFieldCode();
		
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			if (key.equals(MatterAppVO.PK_ORG_V)) {
				// ����֯Ȩ�޹���
				String[] permissionOrgs = ErUiUtil.getPermissionOrgVs(getFuncSubInfo().getFuncode());
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);

				UIRefPane orgPanel = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				try {
					// ����Ĭ��ֵ
					String pk_org_v = MultiVersionUtils.getHeadOrgMultiVersion(ErUiUtil.getDefaultPsnOrg(), ErUiUtil
							.getBusiDate(), orgPanel.getRefModel());
					ERMQueryActionHelper.setPk(event, pk_org_v, false);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			} else if (key.equals(MatterAppVO.PK_ORG)) {
				// ����֯Ȩ�޹���
				String[] permissionOrgs = getFuncSubInfo().getFuncPermissionPkorgs();
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (key.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_PCORG)) {
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (key.equals(MatterAppVO.APPLY_ORG)) {
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (key.equals(MatterAppVO.BILLDATE)) {
				UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
				UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
				leftDate.setValueObjFireValueChangeEvent(busiDate.getDateBefore(30));
				rightDate.setValueObjFireValueChangeEvent(busiDate);
			} else if (MatterAppVO.PK_TRADETYPE.equals(key)) {
				// ���˽������Ͳ���,����Ϊ��ǰ�����µķ��ý�ת����������
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				refPane.getRefModel().setWherePart("parentbilltype = '" + ErmMatterAppConst.MatterApp_BILLTYPE + "'");
			} else if (key != null && (key.contains(MtAppDetailVO.PK_RESACOSTCENTER) || key.contains(MtAppDetailVO.PK_CHECKELE))) {
				// �ɱ����Ĺ��˰���������
				setItemFilterBypcorg(event);
			} else if (MatterAppVO.CREATOR.equals(key) || MatterAppVO.APPROVER.equals(key)
					|| MatterAppVO.PRINTER.equals(key) || MatterAppVO.CLOSEMAN.equals(key)
					|| MatterAppVO.MODIFIER.equals(key) || (key != null && key.contains(MatterAppVO.CLOSEMAN))) {// �û����ݼ��Ź��ˣ�������֯����
				UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

				if (ref != null && ref.getRefModel() != null && ref.getRefModel() instanceof UserDefaultRefModel) {
					ref.getRefModel().setPk_org(ErUiUtil.getPK_group());
					((UserDefaultRefModel) ref.getRefModel()).setUserType(1);
				}
			} else if ((ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.ASSUME_ORG).equals(key)){
			} else {
				Object obj = ERMQueryActionHelper.getFiltComponentForInit(event);
				if (obj != null && obj instanceof UIRefPane) {

					UIRefPane refPane = (UIRefPane) obj;
					if (refPane.getRefModel() != null) {
						refPane.setMultiSelectedEnabled(true);
						initRefpaneFilter(event, (UIRefPane) refPane);
					}
				}
			}
		}

		if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
			if (key.equals(MatterAppVO.PK_ORG)) {// ������֯����
				setPkOrgFilter(event);
			} else if (key.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_PCORG)) {
				setPkPcorgFilter(event);
			} else if (key.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.ASSUME_ORG)) {
				setBodyAssumeOrgFilter(event);
			} else if(key.equals(MatterAppVO.APPLY_ORG)){
				UIRefPane apply_org = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
						MatterAppVO.APPLY_ORG, false);
				filterChildrenByOrg(event, apply_org, new String[] {
						MatterAppVO.APPLY_DEPT, MatterAppVO.BILLMAKER});
			}
		}

		if (event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
			if (key.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_PCORG)) {
				setPkPcorgFilter(event);
			} else if (key.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.ASSUME_ORG)) {
				setBodyAssumeOrgFilter(event);
			} else if (key.equals(MatterAppVO.APPLY_ORG)) {
				UIRefPane apply_org = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
						MatterAppVO.APPLY_ORG, false);
				filterChildrenByOrg(event, apply_org, new String[] { MatterAppVO.APPLY_DEPT, MatterAppVO.BILLMAKER });
			}
		}
	}

	private FuncSubInfo getFuncSubInfo() {
		if (funcSubInfo != null) {
			return funcSubInfo;
		} else if (model != null) {
			return getModel().getContext().getFuncInfo();
		}
		return null;
	}
	
	public void setFuncSubInfo(FuncSubInfo funcSubInfo) {
		this.funcSubInfo = funcSubInfo;
	}

	private void setBodyAssumeOrgFilter(CriteriaChangedEvent event) {
		UIRefPane orgRef = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.ASSUME_ORG, false);

		if (orgRef == null) {
			return;
		}

		// ������ϸ����pk_org
		List<String> bodyItems = AggMatterAppVO.getBodyAssumeOrgBodyIterms();
		for (int i = 0; i < bodyItems.size(); i++) {
			JComponent[] components = ERMQueryActionHelper.getFiltComponentsForValueChanged(event,
					ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + bodyItems.get(i), false);
			if (components != null && components.length > 0) {
				for (JComponent component : components) {
					if (component instanceof UIRefPane) {
						setRefpaneFilter(((UIRefPane) component), orgRef);
					}
				}
			}
		}
	}

	/**
	 * ����ҵ�����в�ѯ����
	 * 
	 * @param event
	 */
	private void setPkPcorgFilter(CriteriaChangedEvent event) {
		UIRefPane pcorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_PCORG, false);
		filterChildrenByOrg(event, pcorg, new String[] {
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_RESACOSTCENTER,
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_CHECKELE });
	}

	/**
	 * ���˹���������pk_org
	 * 
	 * @param event
	 * @param orgRepane
	 * @param childrenNames
	 */
	private void filterChildrenByOrg(CriteriaChangedEvent event, UIRefPane orgRepane, String[] childrenNames) {
		if (childrenNames != null) {
			for (String name : childrenNames) {
				JComponent[] refs = ERMQueryActionHelper.getFiltComponentsForValueChanged(event, name, false);
				if (refs != null && refs.length > 0) {
					for (JComponent ref : refs) {
						if (ref != null && ((UIRefPane) ref).getRefModel() != null) {
							setRefpaneFilter(((UIRefPane) ref), orgRepane);
						}
					}
				}
			}
		}
	}

	/**
	 * ����������������ֶ�
	 * 
	 * @param event
	 */
	private void setItemFilterBypcorg(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_PCORG, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}
	
	/**
	 * ����pk_org�� pk_orgs
	 * @param ref
	 * @param orgRefPane
	 */
	private void setRefpaneFilter(UIRefPane ref, UIRefPane orgRefPane) {
		if (orgRefPane == null || (orgRefPane != null && orgRefPane.getRefPKs() == null)) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);

			if (key.indexOf(".") > 0) {
				key = key.split("[.]")[1];
			}

			if (AggMatterAppVO.getApplyOrgHeadIterms().contains(key)) {
				ref.setMultiRefFilterPKs(getFuncSubInfo().getFuncPermissionPkorgs());
			}
		} else if (orgRefPane != null && orgRefPane.getRefPKs() != null && orgRefPane.getRefPKs().length == 1) {
			ref.setMultiOrgSelected(false);
			ref.setMultiCorpRef(false);
			ref.setMultiSelectedEnabled(true);
			ref.setPk_org(orgRefPane.getRefPK());

			if ("pk_fundplan".equals(ref.getRefModel().getPkFieldCode())) {
				ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
			}
		} else if (orgRefPane != null && orgRefPane.getRefPKs() != null && orgRefPane.getRefPKs().length > 1) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
			ref.setMultiRefFilterPKs(orgRefPane.getRefPKs());
			ref.setPk_org(orgRefPane.getRefPK());

			if ("pk_fundplan".equals(ref.getRefModel().getPkFieldCode())) {
				ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
			}
		}
	}
	
	/**
	 * ��ͷ������֯����
	 * @param orgevent
	 */
	private void setPkOrgFilter(CriteriaChangedEvent orgevent) {
		UIRefPane orgRef = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(orgevent,
				MatterAppVO.PK_ORG, false);

		if (orgRef == null) {
			return;
		}

		String[] pk_orgs = orgRef.getRefPKs();

		if (pk_orgs == null || pk_orgs.length == 0) {// ���û��ѡ������֯����ȡ�û�������֯�Ļ�����������
			pk_orgs = getFuncSubInfo().getFuncPermissionPkorgs();
		}

		String[] headItems = AggMatterAppVO.getApplyOrgHeadIterms().toArray(new String[] {});

		filterChildrenByOrg(orgevent, orgRef, headItems);
	}
	
	/**
	 * ����
	 * 
	 * @param ref
	 */
	private void initRefpaneFilter(CriteriaChangedEvent event, UIRefPane ref) {
		if (ref == null || event == null) {
			return;
		}

		boolean isBody = false;
		String key = event.getFieldCode();

		if (key.indexOf(".") > 0) {
			key = key.split("[.]")[1];
			isBody = true;
		}

		UIRefPane orgRefPane = null;
		if (AggMatterAppVO.getBodyAssumeOrgBodyIterms().contains(key) && isBody) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.ASSUME_ORG, false);

		} else if (AggMatterAppVO.getApplyOrgHeadIterms().contains(key)) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, MatterAppVO.PK_ORG,
					false);
		} else if (key.equals(MatterAppVO.APPLY_DEPT) || key.equals(MatterAppVO.BILLMAKER)) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, MatterAppVO.APPLY_ORG,
					false);
		} else if (key.equalsIgnoreCase(MtAppDetailVO.PK_CHECKELE)
				|| key.equalsIgnoreCase(MtAppDetailVO.PK_RESACOSTCENTER)) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					ErmMatterAppConst.MatterApp_MDCODE_DETAIL + "." + MtAppDetailVO.PK_PCORG, false);
		}
		
		setRefpaneFilter(ref, orgRefPane);
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

}
