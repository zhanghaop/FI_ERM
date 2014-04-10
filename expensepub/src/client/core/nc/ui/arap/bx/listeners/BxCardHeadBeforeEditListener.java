package nc.ui.arap.bx.listeners;

import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.bd.ref.model.FreeCustRefModel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;

/**
 * @author twei
 * 
 *         nc.ui.arap.bx.listeners.BxCardHeadBeforeEditListener
 */
public class BxCardHeadBeforeEditListener extends BXDefaultAction {
	
	public void beforeEdit() throws ComponentException, BusinessException {
		EventObject event = (EventObject) getMainPanel().getAttribute(nc.ui.arap.eventagent.EventTypeConst.TEMPLATE_EDIT_EVENT);
		if (getMainPanel().getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE){
			return;
		}
		if (event instanceof BillItemEvent) {
			BillItemEvent evt = (BillItemEvent) event;
			final String key = evt.getItem().getKey();
			if (JKBXHeaderVO.FREECUST.equals(key)) {
				beforeEditFreeCust(key);
			} else if (key.equals(JKBXHeaderVO.CASHPROJ)) {
				beforeEditCashProj();
			} else if (JKBXHeaderVO.PK_ORG_V.equals(key)) {
				beforeEditPkOrg_v(evt, key);
			} else if (JKBXHeaderVO.FYDWBM_V.equals(key)) {
				beforeEditPkOrg_v(evt, key);
			} else if (JKBXHeaderVO.DWBM_V.equals(key)) {
				beforeEditPkOrg_v(evt, key);
			} else if (JKBXHeaderVO.DEPTID_V.equals(key)) {
				final String dwbm = getHeadItemStrValue(JKBXHeaderVO.DWBM);
				beforeEditDept_v(evt, dwbm, key);
			} else if (JKBXHeaderVO.FYDEPTID_V.equals(key)) {
				final String fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				beforeEditDept_v(evt, fydwbm, key);
			} else if (JKBXHeaderVO.PK_RESACOSTCENTER.equals(key)) {
				// v6.1�����ɱ����ĸ��ݷ��óе���λ����
				beforeEditResaCostCenter(evt);
			} else if (JKBXHeaderVO.PK_CHECKELE.equals(key)) {
				// ����Ҫ�ظ����������Ĺ���
				UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(key);
				String pk_pcorg = getHeadItemStrValue(JKBXHeaderVO.PK_PCORG);
				setPkOrg2RefModel(refPane, pk_pcorg);
			} else if (JKBXHeaderVO.JKBXR.equals(key)) {
				// ���ڳ����ݽ���������Ȩ�������ù�������
				beforeEditJkbxr(evt);
			} else if (JKBXHeaderVO.SKYHZH.equals(key)) {
				// �տ������˺ű༭ǰ�¼�
				beforeEditSkyhzh(key);
			} else if (JKBXHeaderVO.PROJECTTASK.equals(key)) {
				// ��Ŀ���������Ŀ����
				beforeEditProjTask(evt);
			} else if (JKBXHeaderVO.FKYHZH.equals(key)) {
				// v6.1�������˵�λ�����ʺ�
				beforeEditFkyhzh(evt);
			} else if (JKBXHeaderVO.PK_CASHACCOUNT.endsWith(key)) {
				// v6.1 added �ֽ��ʻ����ݱ��ֹ���
				beforeEditCashaccount(evt);
			} else if (JKBXHeaderVO.ZY.equals(key)) {
				// ����ժҪ�ֶ����⴦��֧���ֶ��༭
				beforeEditZy(evt);
			} else if (JKBXHeaderVO.CUSTACCOUNT.equals(key)) {
				// ���������ʺű༭ǰ�¼�
				beforeEditCustAccount(evt);
			}else if(JKBXHeaderVO.SZXMID.equals(key)){
				//��֧��Ŀ�༭��ǰ����
				UIRefPane refPane = getHeadItemUIRefPane(key);
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				refPane.setPk_org(pk_org);
			}else if(JKBXHeaderVO.JOBID.equals(key)){
				//��Ŀ���ݷ��óе���λ����
				beforeEditProj();
			}
			// ����������У�����
			this.checkRule("Y", key);
		}
	}

	/**
	 * ��Ŀ�༭ǰ�¼�
	 */
	private void beforeEditProj() {
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.JOBID);
		refPane.getRefModel().setPk_org(getHeadItemStrValue(JKBXHeaderVO.FYDWBM));
	}
	
	/**
	 * ��Ŀ����༭ǰ�¼�
	 * @param evt
	 */
	private void beforeEditProjTask(BillItemEvent evt){
		final String pk_project = getHeadItemStrValue(JKBXHeaderVO.JOBID);
		if (pk_project != null) {
			UIRefPane refPane = getHeadItemUIRefPane(evt.getItem().getKey());
			String wherePart = " pk_project=" + "'" + pk_project + "'";
			//��Ŀ����֯(�����Ǽ��ż���)
			final String pkOrg = getHeadItemUIRefPane(JKBXHeaderVO.JOBID).getRefModel().getPk_org();
			String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
			if(BXUiUtil.getPK_group().equals(pkOrg)){
				//���ż���Ŀ
				pk_org = BXUiUtil.getPK_group(); 
			}
			//������Ŀ����
			setWherePart2RefModel(refPane,pk_org, wherePart);
		}
	}

	/**
	 * �տ������ʺű༭ǰ�¼�
	 * @param key
	 */
	private void beforeEditSkyhzh(final String key) {
		// �տ������ʺŸ����տ��˹���
		String filterStr = null;
		if (isJk()) {
			// �����
			filterStr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		} else {
			// �տ���
			filterStr = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
		}
		
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);

		if (filterStr != null && filterStr.trim().length() > 0) {
			UIRefPane refPane = getHeadItemUIRefPane(key);
			String wherepart = " pk_psndoc='" + filterStr + "'";
			wherepart+=" and pk_currtype='"+pk_currtype+"'";
			setWherePart2RefModel(refPane, getHeadItemStrValue(JKBXHeaderVO.DWBM), wherepart);
		}
	}
	
	/**
	 * �ɱ����ĸ��ݷ��óе���λ����
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	private void beforeEditResaCostCenter(BillItemEvent evt) throws BusinessException {
		final String pk_fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.PK_RESACOSTCENTER);
		String wherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+pk_fydwbm+"'"; 
		addWherePart2RefModel(refPane, pk_fydwbm, wherePart);
	}
	
	/**
	 * ���������ʺű༭ǰ�¼�
	 * @param evt
	 */
	private void beforeEditFkyhzh(BillItemEvent evt) {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		filterFkyhzh(pk_currtype);
	}
	
	/**
	 * �ֽ��ʻ��༭ǰ�¼�(���ݱ��ֹ���)
	 * @author chendya
	 */
	private void beforeEditCashaccount(BillItemEvent evt) {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		filterCashAccount(pk_currtype);
	}

	
	
	/**
	 * @author chendya ����汾��֯�����ܴ����ı༭ǰ�¼�
	 */
	private void beforeEditPkOrg_v(BillItemEvent evt , final String vOrgField) {
		//����������޸Ľ�����λ
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (date == null) {
			// ��������Ϊ�գ�ȥҵ������
			date = BXUiUtil.getBusiDate();
		}
		UIRefPane refPane = getHeadItemUIRefPane(vOrgField);
		FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) refPane.getRefModel();
		
		//������Ȩ��,���Լ��ż����õ��ݽڵ�����֯���߹���Ȩ��
		if(!(getNodeCode().equals(BXConstans.BXINIT_NODECODE_G)) && JKBXHeaderVO.PK_ORG_V.equals(vOrgField)){
			//������֯�ֶθ��ݹ���Ȩ�޹���
			String refPK = refPane.getRefPK();
			String[] pk_vids = BXUiUtil.getPermissionOrgVs(getNodeCode(), date);
			refPane.getRefModel().setFilterPks(pk_vids);
			List<String> list = Arrays.asList(pk_vids);
			if(list.contains(refPK)){
				refPane.setPK(refPK);
			}else{
				refPane.setPK(null);
			}
		}
		model.setVstartdate(date);
	}

	/**
	 * ���ű༭ǰ����
	 * @param evt
	 * @param vDeptField
	 */
	private void beforeEditDept_v(BillItemEvent evt , final String pk_org, final String vDeptField){
		UIRefPane refPane = getHeadItemUIRefPane(vDeptField);
		DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) refPane.getRefModel();
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (date == null) {
			// ��������Ϊ�գ�ȥҵ������
			date = BXUiUtil.getBusiDate();
		}
		model.setVstartdate(date);
		model.setPk_org(pk_org);
	}
	
	/**
	 * �����˱༭ǰ�¼�
	 */
	private void beforeEditJkbxr(BillItemEvent evt) {
		// ���ڳ�����������Ȩ������
		if (!getBxParam().getIsQc()) {
			try {
				initSqdlr(getMainPanel(), evt.getItem(), getVoCache().getCurrentDjlxbm(), getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e);
			}
		}
	}
	/**
	 * �����ֶα༭ǰ�¼�
	 * @param evt
	 */
	private void beforeEditZy(BillItemEvent evt){
		UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(JKBXHeaderVO.ZY);
		//���ղ��Զ�ƥ��
		refPane.setAutoCheck(false);
	}

	private void beforeEditCashProj() {
		UIRefPane ref = getHeadItemUIRefPane(JKBXHeaderVO.CASHPROJ);
		final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		ref.getRefModel().setPk_org(pk_org);
		ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
	}

	/**
	 * ɢ���༭ǰ�¼�
	 * @param key
	 */
	private void beforeEditFreeCust(final String key) {
		UIRefPane refPane = getHeadItemUIRefPane(key);
		//��Ӧ��
		final String pk_supplier = getHeadItemStrValue(JKBXHeaderVO.HBBM);
		if(pk_supplier!=null&&pk_supplier.trim().length()>0){
			//ɢ�����ù�Ӧ��
			((FreeCustRefModel)refPane.getRefModel()).setCustomSupplier(pk_supplier);
		}
	}
	
	/**
	 * ���������ʺű༭ǰ�¼�
	 * @throws BusinessException
	 */
	private void beforeEditCustAccount(BillItemEvent evt) throws BusinessException{
		//��Ӧ��
		final String pk_supplier = getHeadItemStrValue(JKBXHeaderVO.HBBM);
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		UIRefPane refPane =getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT);
		String wherepart="  pk_currtype='"+pk_currtype+"'";
		setWherePart2RefModel(refPane, null, wherepart);
		
		CustBankaccDefaultRefModel refModel = (CustBankaccDefaultRefModel)refPane.getRefModel();
		if(refModel!=null){
			refModel.setPk_cust(pk_supplier);
		}
	}
}
