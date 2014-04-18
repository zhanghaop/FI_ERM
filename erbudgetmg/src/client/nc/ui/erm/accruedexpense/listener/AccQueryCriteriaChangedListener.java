package nc.ui.erm.accruedexpense.listener;

import javax.swing.JComponent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.busi.UserDefaultRefModel;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.uap.rbac.FuncSubInfo;

public class AccQueryCriteriaChangedListener implements ICriteriaChangedListener {

	private AbstractUIAppModel model = null;
	private String key = null;
	private FuncSubInfo funcSubInfo;

	private String MDCODE_DETAIL = ErmAccruedBillConst.Accrued_MDCODE_DETAIL + ".";

	private String ORG_KEY_ASSUME_ORG = MDCODE_DETAIL + AccruedDetailVO.ASSUME_ORG;
	private String ORG_KEY_PK_PCORG = MDCODE_DETAIL + AccruedDetailVO.PK_PCORG;

	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		this.key = event.getFieldCode();
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {// ��ʼ��ʱ���������¼�
			if (AccruedVO.PK_ORG.equals(key)) {
				// ����֯Ȩ�޹���
				String[] permissionOrgs = getFuncSubInfo().getFuncPermissionPkorgs();
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (ORG_KEY_PK_PCORG.equals(key)) {// ��������
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (AccruedVO.OPERATOR_ORG.equals(key)) {// �����˵�λ
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
			} else if (AccruedVO.BILLDATE.equals(key)) {
				UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
				UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
				leftDate.setValueObjFireValueChangeEvent(busiDate.getDateBefore(30));
				rightDate.setValueObjFireValueChangeEvent(busiDate);
			} else if (AccruedVO.PK_TRADETYPEID.equals(key)) {
				// ���˽������Ͳ���,����Ϊ��ǰ�����µķ��ý�ת����������
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				refPane.getRefModel().setWherePart(
						"parentbilltype = '" + ErmAccruedBillConst.AccruedBill_Billtype + "'");
			} else if (key != null
					&& (key.contains(AccruedDetailVO.PK_RESACOSTCENTER) || key.contains(AccruedDetailVO.PK_CHECKELE))) {
				// �ɱ����Ĺ��˰���������
				setItemFilterBypcorg(event);
			} else if (AccruedVO.CREATOR.equals(key) || AccruedVO.APPROVER.equals(key) || AccruedVO.PRINTER.equals(key)
					|| AccruedVO.MODIFIER.equals(key)) {// �û����ݼ��Ź��ˣ�������֯����
				UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

				if (ref != null && ref.getRefModel() != null && ref.getRefModel() instanceof UserDefaultRefModel) {
					ref.getRefModel().setPk_org(ErUiUtil.getPK_group());
					((UserDefaultRefModel) ref.getRefModel()).setUserType(1);
				}
			} else if (ORG_KEY_ASSUME_ORG.equals(key)) {
				// ����Ĭ��ֵ
				ERMQueryActionHelper.setPk(event, ErUiUtil.getDefaultPsnOrg(), false);
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

		if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {// ��ѡ����յ�ʱ��
			if (AccruedVO.PK_ORG.equals(key)) {// ������֯����
				setPkOrgFilter(event, AccruedVO.PK_ORG, new String[] { AccruedVO.REASON }, false);
			} else if (ORG_KEY_PK_PCORG.equals(key)) {
				setPkOrgFilter(event, ORG_KEY_PK_PCORG, new String[] { AccruedDetailVO.PK_RESACOSTCENTER,
						AccruedDetailVO.PK_CHECKELE }, true);
			} else if (ORG_KEY_ASSUME_ORG.equals(key)) {
				setPkOrgFilter(event, ORG_KEY_ASSUME_ORG,
						AggAccruedBillVO.getBodyAssumeOrgBodyIterms().toArray(new String[0]), true);
			} else if (AccruedVO.OPERATOR_ORG.equals(key)) {
				setPkOrgFilter(event, AccruedVO.OPERATOR_ORG, new String[] { AccruedVO.OPERATOR_DEPT,
						AccruedVO.OPERATOR }, false);
			}
		}

		if (event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
			if (key.equals(ORG_KEY_PK_PCORG)) {
				setPkOrgFilter(event, ORG_KEY_PK_PCORG, new String[] { AccruedDetailVO.PK_RESACOSTCENTER,
						AccruedDetailVO.PK_CHECKELE }, true);
			} else if (key.equals(ORG_KEY_ASSUME_ORG)) {
				setPkOrgFilter(event, ORG_KEY_ASSUME_ORG,
						AggAccruedBillVO.getBodyAssumeOrgBodyIterms().toArray(new String[0]), true);
			} else if (key.equals(AccruedVO.OPERATOR_ORG)) {
				setPkOrgFilter(event, AccruedVO.OPERATOR_ORG, new String[] { AccruedVO.OPERATOR_DEPT,
						AccruedVO.OPERATOR }, false);
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

	/**
	 * ���˹���������pk_org
	 * 
	 * @param event
	 * @param orgRepane
	 *            ��֯pane
	 * @param fieldNames
	 * @param isBody
	 *            �ֶ��Ƿ��Ǳ����ֶ�
	 */
	private void filterChildrenByOrg(CriteriaChangedEvent event, UIRefPane orgRepane, String[] fieldNames,
			boolean isBody) {
		if (fieldNames != null) {
			for (String name : fieldNames) {
				JComponent[] refs = null;
				if (isBody) {
					refs = ERMQueryActionHelper.getFiltComponentsForValueChanged(event, MDCODE_DETAIL + name, false);
				} else {
					refs = ERMQueryActionHelper.getFiltComponentsForValueChanged(event, name, false);
				}

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
				ORG_KEY_PK_PCORG, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	/**
	 * ����pk_org�� pk_orgs
	 * 
	 * @param ref
	 * @param orgRefPane
	 */
	private void setRefpaneFilter(UIRefPane ref, UIRefPane orgRefPane) {
		if (orgRefPane == null || (orgRefPane != null && orgRefPane.getRefPKs() == null)) {
			ref.setMultiOrgSelected(true);
			ref.setMultiCorpRef(true);
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
	 * ��֯����
	 * 
	 * @param orgevent
	 */
	private void setPkOrgFilter(CriteriaChangedEvent orgevent, String org_key, String[] childrenFieldKeys,
			boolean isBody) {
		UIRefPane orgRef = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(orgevent, org_key, false);

		if (orgRef == null) {
			return;
		}
		filterChildrenByOrg(orgevent, orgRef, childrenFieldKeys, isBody);
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
		if (AggAccruedBillVO.getBodyAssumeOrgBodyIterms().contains(key) && isBody) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, ORG_KEY_ASSUME_ORG,
					false);

		} else if (key.equals(AccruedVO.REASON)) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, AccruedVO.PK_ORG,
					false);
		} else if (key.equals(AccruedVO.OPERATOR) || key.equals(AccruedVO.OPERATOR_DEPT)) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					AccruedVO.OPERATOR_ORG, false);
		} else if (key.equalsIgnoreCase(AccruedDetailVO.PK_CHECKELE)
				|| key.equalsIgnoreCase(AccruedDetailVO.PK_RESACOSTCENTER)) {
			orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, ORG_KEY_PK_PCORG,
					false);
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
