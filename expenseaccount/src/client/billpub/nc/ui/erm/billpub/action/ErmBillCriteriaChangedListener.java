package nc.ui.erm.billpub.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.busi.UserDefaultRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.bd.ref.model.FreeCustRefModel;
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
import nc.vo.bd.bankaccount.IBankAccConstant;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * <p>
 * TODO �ӿ�/�๦��˵����ʹ��˵�����ӿ��Ƿ�Ϊ�������������ʹ���ߣ����Ƿ��̰߳�ȫ�ȣ���
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @author wangled
 * @version V6.3
 * @since V6.3 ����ʱ�䣺2013-1-7 ����03:31:16
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
			if (key.equals(JKBXHeaderVO.DWBM) || key.equals(JKBXHeaderVO.FYDWBM)
					|| key.equals(JKBXHeaderVO.PK_PAYORG) || key.equals(JKBXHeaderVO.PK_PCORG)) {
				ERMQueryActionHelper.setPk(event, pk_org, false);
			}else if (key.equals(JKBXHeaderVO.PK_ORG)) {
				// ����֯Ȩ�޹���
				String[] permissionOrgs = getModel().getContext().getPkorgs();
				ERMQueryActionHelper.filtOrgsForQueryAction(event, permissionOrgs);
				if(permissionOrgs==null || permissionOrgs.length == 0
						||!Arrays.asList(permissionOrgs).contains(pk_org)){
					ERMQueryActionHelper.setPk(event, null, false);
				}else{
					ERMQueryActionHelper.setPk(event, pk_org, false);
				}
			}else if (key.equals(JKBXHeaderVO.DJRQ)) {
				UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
				try {
					if (((ErmBillBillManageModel) getModel()).isInit()) {
						if (pk_org == null) {
							return;
						}
						UFDate dateStart = BXUiUtil.getStartDate(pk_org);
						if (dateStart != null) {
	                        UFDate dateBefore = dateStart.getDateBefore(1);
	                        leftDate.setValueObjFireValueChangeEvent(dateBefore.getDateBefore(30));
	                        rightDate.setValueObjFireValueChangeEvent(dateBefore);						    
						}
					} else {
						UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
						leftDate.setValueObjFireValueChangeEvent(busiDate.getDateBefore(30));
						rightDate.setValueObjFireValueChangeEvent(busiDate);
					}
				} catch (BusinessException e) {
					ExceptionHandler.handleExceptionRuntime(e);
				}
			} else if (key.equals(JKBXHeaderVO.DJLXBM)) {
				// ���˽������ͷ�Χ
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				if (((ErmBillBillManageModel) getModel()).isInit()) {
					refPane.setWhereString(" parentbilltype in ('263X')");
				} else {
					refPane.setWhereString(" parentbilltype in ('263X','264X')");
				}
			} else {
				if (JKBXHeaderVO.FYDEPTID.equals(key)
						|| JKBXHeaderVO.FYDEPTID_V.equals(key) || JKBXHeaderVO.SZXMID.equals(key)
						|| JKBXHeaderVO.HBBM.equals(key) || JKBXHeaderVO.CUSTOMER.equals(key)
						|| (BUSITEM + BXBusItemVO.SZXMID).equals(event.getFieldCode())
						|| ("er_busitem."+BXBusItemVO.SZXMID).equals(event.getFieldCode())) {
					setItemFilterByFydw(event);
				} else if (key.equals(JKBXHeaderVO.CENTER_DEPT)) {//��ڹ����Ų��շ�Χ
					UIRefPane center_dept = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
					center_dept.setMultiCorpRef(true);
					center_dept.setMultiOrgSelected(true);
					center_dept.setMultiRefFilterPKs(null);
					center_dept.setPk_org(null);
				} else if (key.equals(JKBXHeaderVO.PAYMAN) || key.equals(JKBXHeaderVO.OPERATOR)
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
				} else if (key.equals(JKBXHeaderVO.PK_RESACOSTCENTER) ||  JKBXHeaderVO.PK_CHECKELE.equals(key)) {
					setItemFilterBypcorg(event);
				}
			}
			// ehp2���ӵı����ֶεĴ���
			if(key.equals("er_busitem."+BXBusItemVO.DWBM)){//����ı����˲���
				ERMQueryActionHelper.setPk(event, pk_org, false);
			}else if(key.equals("er_busitem." + BXBusItemVO.DEPTID) || key.equals("er_busitem." + BXBusItemVO.JKBXR)
					|| key.equals("er_busitem." + BXBusItemVO.RECEIVER)){
				setItemFilterBydwbmbody(event);
			}else if(key.equals("er_busitem."+BXBusItemVO.CUSTOMER) || key.equals("er_busitem."+BXBusItemVO.HBBM)){
				setItemFilterByFydw(event);
			}else if(key.equals("er_busitem."+BXBusItemVO.SKYHZH)){
				setSkyhzhItemFilter(event);
			}else if(key.equals("er_busitem."+BXBusItemVO.CUSTACCOUNT)){
				setCustAccItemFilter(event);
			}else if(key.equals("er_busitem."+BXBusItemVO.FREECUST)){
				setFreeCustItemFilter(event);
			}
			//�����Զ����ֶ�
			if(!(event.getCriteriaEditor() instanceof QuickQueryArea)){
				setUserdefItemFilter(event);
			}
			
			// EHP2���ӷ�̯��ϸ���ֶεĴ���
			if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.ASSUME_ORG)){
				// ��̯��ϸ-���óе���λ
				ERMQueryActionHelper.setPk(event, pk_org, false);
			}else if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_PCORG)){
				// ��̯��ϸ-��������
				ERMQueryActionHelper.setPk(event, pk_org, false);
			}else if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_CHECKELE)||key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_RESACOSTCENTER)){
				// ��̯��ϸ-����Ҫ�ء��ɱ�����,�����������Ĺ���
				setItemFilterByCsOrg(event,BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_PCORG);
			}else if(key.startsWith(BXConstans.CS_Metadatapath+".")&&!key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.YSDATE)){
				// ��̯��ϸ-���������ֶξ����շ��óе���λ����
				setItemFilterByCsOrg(event,BXConstans.CS_Metadatapath+"."+CShareDetailVO.ASSUME_ORG);
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
				// ��������֯����
			} else if (key.equals(JKBXHeaderVO.DWBM)) {
				// �Խ���˲��źͽ���������й���
				setDwbmFilter(event);
			} else if (key.equals(JKBXHeaderVO.FYDWBM)) {
				// ���ݷ��óе���λ����
				setFydwFilter(event);
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG)) {
				setpayorgFilter(event);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG)) {
				setpcorgFilter(event);
			} else if(key.equals("er_busitem."+BXBusItemVO.DWBM)){
				setDwbmFilterbody(event);
			} else if(key.equals("er_busitem."+BXBusItemVO.RECEIVER) || key.equals(JKBXHeaderVO.BZBM)){
				setChangeSkyhzhItemFilter(event);
			}  
			if(key.equals("er_busitem."+BXBusItemVO.HBBM) || key.equals("er_busitem."+BXBusItemVO.CUSTOMER) ||key.equals(JKBXHeaderVO.BZBM)){
				setChangeCustAccItemFilter(event);
				if(!key.equals(JKBXHeaderVO.BZBM)){
					setChangeFreeCustItemFilter(event);
				}
			} 
			
			setUserdefItemChangedOrg(event);
			// EHP2���ӷ�̯��ϸ���ֶεĴ���
			if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.ASSUME_ORG)){
				// ��̯��ϸ-���óе���λ
				setOrgCsFilter(event);
			}else if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_PCORG)){
				// ��̯��ϸ-��������
				setpcorgCsFilter(event);
			}
		} else if (event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
			if (JKBXHeaderVO.FYDWBM.equals(event.getFieldCode())) {
				setFydwFilter(event);
			} else if (JKBXHeaderVO.DWBM.equals(event.getFieldCode())) {
				setDwbmFilter(event);
			} else if (JKBXHeaderVO.PK_PAYORG.equals(event.getFieldCode())) {
				setpayorgFilter(event);
			} else if (JKBXHeaderVO.PK_PCORG.equals(event.getFieldCode())) {
				setpcorgFilter(event);
			} else if(key.equals("er_busitem."+BXBusItemVO.DWBM)){
				setDwbmFilterbody(event);
			} else if(key.equals("er_busitem."+BXBusItemVO.RECEIVER) || key.equals(JKBXHeaderVO.BZBM)){
				setChangeSkyhzhItemFilter(event);
			} 
			if(key.equals("er_busitem."+BXBusItemVO.HBBM) || key.equals("er_busitem."+BXBusItemVO.CUSTOMER) ||key.equals(JKBXHeaderVO.BZBM)){
				setChangeCustAccItemFilter(event);
				if(!key.equals(JKBXHeaderVO.BZBM)){
					setChangeFreeCustItemFilter(event);
				}
			} 
			setUserdefItemChangedOrg(event);
			// EHP2���ӷ�̯��ϸ���ֶεĴ���
			if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.ASSUME_ORG)){
				// ��̯��ϸ-���óе���λ
				setOrgCsFilter(event);
			}else if(key.equals(BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_PCORG)){
				// ��̯��ϸ-��������
				setpcorgCsFilter(event);
			}
		}
	}
	
	private void setChangeFreeCustItemFilter(CriteriaChangedEvent event) {
		UIRefPane hbbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.HBBM, false);
		
		UIRefPane custpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.CUSTOMER, false);
		
		UIRefPane freecustpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.FREECUST, false);
		
		String pk_custsup = null;
		if((hbbmpane!=null && hbbmpane.getRefPKs()!=null && hbbmpane.getRefPKs().length==1)
				&&(custpane==null || (custpane!=null && custpane.getRefPKs()==null))){
			pk_custsup = hbbmpane.getRefPK();
		}else if((custpane!=null && custpane.getRefPKs()!=null && custpane.getRefPKs().length==1)
				&&(hbbmpane==null || (hbbmpane!=null && hbbmpane.getRefPKs()==null))){
			pk_custsup = custpane.getRefPK();
		}else{
			((FreeCustRefModel) freecustpane.getRefModel()).setCustomSupplier(null);
			return ;
		}
		if(!StringUtil.isEmptyWithTrim(pk_custsup)){
			if (freecustpane.getRefModel() != null && freecustpane.getRefModel() instanceof FreeCustRefModel) {
				((FreeCustRefModel) freecustpane.getRefModel()).setCustomSupplier(pk_custsup);
				
			}
		}
	}
	
	private void setFreeCustItemFilter(CriteriaChangedEvent event) {
		UIRefPane hbbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.HBBM, false);
		
		UIRefPane custpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.CUSTOMER, false);

		UIRefPane ref =(UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		String pk_custsup = null;
		if((hbbmpane!=null && hbbmpane.getRefPKs()!=null && hbbmpane.getRefPKs().length==1)
				&&(custpane==null || (custpane!=null && custpane.getRefPKs()==null))){
			pk_custsup = hbbmpane.getRefPK();
		}else if((custpane!=null && custpane.getRefPKs()!=null && custpane.getRefPKs().length==1)
				&&(hbbmpane==null || (hbbmpane!=null && hbbmpane.getRefPKs()==null))){
			pk_custsup = custpane.getRefPK();
		}else{
			((FreeCustRefModel) ref.getRefModel()).setCustomSupplier(null);
			return ;
		}
		if(!StringUtil.isEmptyWithTrim(pk_custsup)){
			if (ref.getRefModel() != null && ref.getRefModel() instanceof FreeCustRefModel) {
				((FreeCustRefModel) ref.getRefModel()).setCustomSupplier(pk_custsup);
				
			}
		}
	}

	/**
	 * ������Ӧ�̺Ϳͻ�������
	 * @param event
	 */
	private void setChangeCustAccItemFilter(CriteriaChangedEvent event) {
		UIRefPane hbbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.HBBM, false);
		
		UIRefPane custpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.CUSTOMER, false);
		
		UIRefPane bzbmpane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,JKBXHeaderVO.BZBM, false);
		
		UIRefPane custaccpane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.CUSTACCOUNT, false);
		
		if(custaccpane!=null){
			CustBankaccDefaultRefModel refModel = (CustBankaccDefaultRefModel) custaccpane.getRefModel();
			String pk_custsup = null;
			int accclass =10000;
			if((hbbmpane!=null && hbbmpane.getRefPKs()!=null && hbbmpane.getRefPKs().length==1)
					&&(custpane==null || (custpane!=null && custpane.getRefPKs()==null))){
				pk_custsup = hbbmpane.getRefPK();
				accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
				
			}else if((custpane!=null && custpane.getRefPKs()!=null && custpane.getRefPKs().length==1)
					&&(hbbmpane==null || (hbbmpane!=null && hbbmpane.getRefPKs()==null))){
				pk_custsup = custpane.getRefPK();
				accclass = IBankAccConstant.ACCCLASS_CUST;
			}else{
				refModel.setPk_org(null);
				refModel.setWherePart(null);
				refModel.setPk_cust(null);
				return ;
			}
			if(!StringUtil.isEmptyWithTrim(pk_custsup)){
				StringBuffer wherepart = new StringBuffer();
				wherepart.append(" enablestate='" + IPubEnumConst.ENABLESTATE_ENABLE+"'");
				wherepart.append(" and accclass='" + accclass + "'");
				if(bzbmpane.getRefPK()!= null){
					try {
						String insql = SqlUtils.getInStr("pk_currtype", bzbmpane.getRefPKs(), false);
						wherepart.append(" and "+ insql);
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				}
				if (refModel != null) {
					refModel.setPk_org(null);
					refModel.setWherePart(wherepart.toString());
					refModel.setPk_cust(pk_custsup);
				}
			}
		}
	}

	/**
	 *���������ʻ����ݹ�Ӧ�̻�ͻ�����
	 * @param event
	 */
	private void setCustAccItemFilter(CriteriaChangedEvent event) {
		UIRefPane hbbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.HBBM, false);
		
		UIRefPane custpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.CUSTOMER, false);
		
		UIRefPane bzbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,JKBXHeaderVO.BZBM, false);
		
		
		String pk_custsup = null;
		int accclass =10000;
		if((hbbmpane!=null && hbbmpane.getRefPKs()!=null && hbbmpane.getRefPKs().length==1)
				&&(custpane==null || (custpane!=null && custpane.getRefPKs()==null))){
			pk_custsup = hbbmpane.getRefPK();
			accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
		}else if((custpane!=null && custpane.getRefPKs()!=null && custpane.getRefPKs().length==1)
				&&(hbbmpane==null || (hbbmpane!=null && hbbmpane.getRefPKs()==null))){
			pk_custsup = custpane.getRefPK();
			accclass = IBankAccConstant.ACCCLASS_CUST;
		}else{
			return ;
		}
		if(!StringUtil.isEmptyWithTrim(pk_custsup)){
			StringBuffer wherepart = new StringBuffer();
			wherepart.append(" enablestate='" + IPubEnumConst.ENABLESTATE_ENABLE+"'");
			wherepart.append(" and accclass='" + accclass + "'");
			if(bzbmpane.getRefPK()!= null){
				try {
					String insql = SqlUtils.getInStr("pk_currtype", bzbmpane.getRefPKs(), false);
					wherepart.append(" and "+ insql);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			}
			UIRefPane ref =(UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
			if (ref.getRefModel() != null && ref.getRefModel() instanceof CustBankaccDefaultRefModel) {
				CustBankaccDefaultRefModel refModel = (CustBankaccDefaultRefModel) ref.getRefModel();
				if (refModel != null) {
					refModel.setPk_org(null);
					refModel.setWherePart(wherepart.toString());
					refModel.setPk_cust(pk_custsup);
				}
			}
		}
	}

	/**
	 * �ı��տ��˻����
	 * @param event
	 */
	private void setChangeSkyhzhItemFilter(CriteriaChangedEvent event){
		UIRefPane receiver = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, "er_busitem."+BXBusItemVO.RECEIVER,
				false);
		UIRefPane skyhzh = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event, "er_busitem."+BXBusItemVO.SKYHZH,
				false);
		
		UIRefPane bzbmpane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,JKBXHeaderVO.BZBM, false);
		
		UIRefPane dwbmpane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.DWBM, false);
		if(skyhzh!=null){
			if(event.getFieldCode().equals("er_busitem."+BXBusItemVO.RECEIVER)){
				if(receiver==null || (receiver!=null && receiver.getRefPKs()==null) || (receiver!=null && receiver.getRefPKs()!=null && receiver.getRefPKs().length>1)){
					skyhzh.getRefModel().setPk_org(null);
					skyhzh.getRefModel().setWherePart(null);
					return;
				}
			}
			StringBuffer wherepart = new StringBuffer();
			wherepart.append(" pk_psndoc='" +receiver.getRefPK()+ "'");
			if (bzbmpane.getRefPK() != null) {
				try {
					String insql = SqlUtils.getInStr("pk_currtype", bzbmpane
							.getRefPKs(), false);
					wherepart.append(" and " + insql);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			}
			skyhzh.getRefModel().setPk_org(dwbmpane.getRefPK());
			skyhzh.getRefModel().setWherePart(wherepart.toString());

		}
	}

	/**
	 * �����տ������ʻ�����
	 */
	private void setSkyhzhItemFilter(CriteriaChangedEvent event){
		UIRefPane receiverpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.RECEIVER, false);
		
		UIRefPane bzbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,JKBXHeaderVO.BZBM, false);
		
		UIRefPane dwbmpane = (UIRefPane) ERMQueryActionHelper.
		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.DWBM, false);
		
		UIRefPane ref =(UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		if(receiverpane==null || dwbmpane==null){
			return ;
		} else if((receiverpane.getRefPKs()==null || receiverpane.getRefPKs()!=null && receiverpane.getRefPKs().length > 1) 
				|| (dwbmpane.getRefPKs()==null || dwbmpane.getRefPKs()!=null && dwbmpane.getRefPKs().length > 1)){
			return ;
		}
		else if(receiverpane.getRefPKs().length == 1 && dwbmpane.getRefPKs().length==1){
				StringBuffer wherepart = new StringBuffer();
				wherepart.append(" pk_psndoc='" +receiverpane.getRefPK()+ "'");
				if(bzbmpane.getRefPK()!= null){
					try {
						String insql = SqlUtils.getInStr("pk_currtype", bzbmpane.getRefPKs(), false);
						wherepart.append(" and "+ insql);
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				}
				ref.getRefModel().setPk_org(dwbmpane.getRefPK());
				ref.getRefModel().setWherePart(wherepart.toString());
		}
	}

	/**
	 * ���ÿ��������ʻ���
	 */
//	private void setCustaccountItemFilter(CriteriaChangedEvent event){
//		UIRefPane hbbmpane = (UIRefPane) ERMQueryActionHelper.
//		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.HBBM, false);
//
//		UIRefPane custpane = (UIRefPane) ERMQueryActionHelper.
//		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.CUSTOMER, false);
//		
//		UIRefPane bzbmpane = (UIRefPane) ERMQueryActionHelper.
//		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.BZBM, false);
//		
//		UIRefPane dwbmpane = (UIRefPane) ERMQueryActionHelper.
//		getFiltComponentForValueChanged(event,"er_busitem." + BXBusItemVO.DWBM, false);
//		
//		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
//		if((hbbmpane==null && custpane==null) || bzbmpane==null || dwbmpane==null){
//			return ;
//		}
//		else{
//			if(custpane==null || (custpane!=null && custpane.getRefPK()!=null)){
//				//����Ӧ��
//			}
//			if(hbbmpane.getRefPK()!=null && bzbmpane.getRefPK()!=null && dwbmpane.getRefPK()!=null){
//				StringBuffer wherepart = new StringBuffer();
//				wherepart.append(" pk_psndoc='" +hbbmpane.getRefPK()+ "'");
//				wherepart.append(" and pk_currtype='" +bzbmpane.getRefPK()+ "'");
//				ref.getRefModel().setPk_org(dwbmpane.getRefPK());
//				ref.getRefModel().setWherePart(wherepart.toString());
//			}
//		}
//	}
	
	
	/**
	 * ����Ӧ��֯�仯���Զ�����Ĵ���
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
			//�ڲ�����֯�ı����Ҫ�Ա�����Զ������
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
	 * �����Զ������ֶεĹ��ˣ�����busitype.xml
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
				if (costentity_billitems.contains(key)) {//���ݷ��óе���λ�����Զ�����
					setItemFilterByFydw(event);
				} else if (payentity_billitems.contains(key)) {//���ݽ�����λ�����Զ�����
					setItemFilterBypk_org(event);
				} else if (useentity_billitems.contains(key)) {//���ݽ����˵�λ�����Զ�����
					setItemFilterBydwbm(event);
				} else if(payorgentity_billitems.contains(key)){//����֧����λ�����Զ�����
					setItemFilterBypayorg(event);
				}
			}
		}
	}
	
	/**
	 *��ý�������VO
	 * @return
	 */
	private BusiTypeVO getBusiTypeVO() {
			if(busTypeVO==null){
				String nodeCode = getModel().getContext().getNodeCode();
				String transtype =null;
				String djdl =null;
				if(!nodeCode.equals(BXConstans.BXLR_QCCODE)&& !nodeCode.equals(BXConstans.BXMNG_NODECODE)&& !nodeCode.equals(BXConstans.BXBILL_QUERY) &&
				        !nodeCode.equals(BXConstans.BXINIT_NODECODE_G) && !nodeCode.equals(BXConstans.BXINIT_NODECODE_U)&& !nodeCode.equals(BXConstans.MONTHEND_DEAL)){
				     transtype = TransTypeUtil.getTranstype((AbstractAppModel)getModel());
				     if(transtype.startsWith(BXConstans.BX_PREFIX)){
				    	 djdl=BXConstans.BX_DJDL;
				     }else{
				    	 djdl=BXConstans.JK_DJDL;
				     }
				}else{
						transtype=BXConstans.JK_DJLXBM;//����ڵ��õ�������
						djdl=BXConstans.JK_DJDL;
					}
				busTypeVO=BXUtil.getBusTypeVO(transtype,djdl);
			}
		return busTypeVO;
	}
	
	/**
	 * ������֯����ֶι���
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
	 * ����������������ֶ�
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
		String[] headItems = new String[] { JKBXHeaderVO.PK_CHECKELE,JKBXHeaderVO.PK_RESACOSTCENTER };
		UIRefPane pcorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(pcorgevent,
				JKBXHeaderVO.PK_PCORG, false);
		setItemByorg(pcorgevent, headItems, pcorg);

	}

	/**
	 * ����֧����λ����ֶ�
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
	 * ���óе���λ����ֶι���
	 * 
	 * @param fydwevent
	 */
	private void setFydwFilter(CriteriaChangedEvent fydwevent) {
		UIRefPane fydw = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(fydwevent,
				JKBXHeaderVO.FYDWBM, false);

		String[] headItems = new String[] { JKBXHeaderVO.FYDEPTID,
				JKBXHeaderVO.FYDEPTID_V, JKBXHeaderVO.SZXMID, JKBXHeaderVO.HBBM, JKBXHeaderVO.JOBID,
				JKBXHeaderVO.CUSTOMER, BUSITEM + BXBusItemVO.SZXMID ,"er_busitem."+BXBusItemVO.SZXMID,
				"er_busitem."+BXBusItemVO.HBBM,"er_busitem."+BXBusItemVO.CUSTOMER};
		setItemByorg(fydwevent, headItems, fydw);
	}

	private void setItemFilterByFydw(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.FYDWBM, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);

	}

	/**
	 * ������λ����ֶι���
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
	
	private void setDwbmFilterbody(CriteriaChangedEvent dwbmevent) {
		UIRefPane dwbm = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(dwbmevent, "er_busitem."+BXBusItemVO.DWBM,
				false);

		String[] bodyItems = new String[] {"er_busitem."+BXBusItemVO.DEPTID,
				 "er_busitem."+BXBusItemVO.JKBXR,"er_busitem."+BXBusItemVO.RECEIVER};

		setItemByorg(dwbmevent, bodyItems, dwbm);

	}
	
	private void setItemFilterBypk_org(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
		UIRefPane pk_orgrefane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				JKBXHeaderVO.PK_ORG, false);
		setRefpaneFilter(ref, pk_orgrefane);
	}
	
	private void setItemFilterBydwbmbody(CriteriaChangedEvent event) {
		UIRefPane ref = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);

		UIRefPane pk_fwdwRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
				"er_busitem."+BXBusItemVO.DWBM, false);
		setRefpaneFilter(ref, pk_fwdwRefPane);
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
	 * ��Ӧ�����ֶεĹ��˴���
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
			if ("pk_fundplan".equals(ref.getRefModel().getPkFieldCode())) {
				ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
			}
		} else if (dwRefpane != null && dwRefpane.getRefPKs() != null && dwRefpane.getRefPKs().length > 1) {
			ref.setMultiCorpRef(true);
			ref.setMultiOrgSelected(true);
			ref.setMultiRefFilterPKs(dwRefpane.getRefPKs());
			ref.setPk_org(dwRefpane.getRefPKs()[0]);
			if ("pk_fundplan".equals(ref.getRefModel().getPkFieldCode())) {
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

	/**
	 * ���ݷ�̯��ϸ��λ���ˣ���̯��ϸ�����ֶ�
	 * 
	 * @param event
	 */
	private void setItemFilterByCsOrg(CriteriaChangedEvent event,String orgfield) {
		JComponent filtComponentForInit = ERMQueryActionHelper.getFiltComponentForInit(event);
		if(filtComponentForInit != null && (filtComponentForInit instanceof UIRefPane)){
			
			UIRefPane ref = (UIRefPane) filtComponentForInit;
			UIRefPane pk_orgRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					orgfield, false);
			setRefpaneFilter(ref, pk_orgRefPane);
		}
	}
	/**
	 * ��̯��ϸ�У����óе���λ�������
	 * 
	 * @param pcorgevent
	 */
	private void setOrgCsFilter(CriteriaChangedEvent event) {
		CShareDetailVO vo = new CShareDetailVO();
		String[] attributeNames = vo.getAttributeNames();
		List<String> items = new ArrayList<String>();
		for (int i = 0; i < attributeNames.length; i++) {
			String attr = attributeNames[i];
			if(CShareDetailVO.YSDATE.equals(attr)||CShareDetailVO.PK_PCORG.equals(attr)
					||CShareDetailVO.PK_RESACOSTCENTER.equals(attr)||CShareDetailVO.PK_CHECKELE.equals(attr)){
				continue;
			}
			JComponent filtComponentForInit = ERMQueryActionHelper.getFiltComponentForValueChanged(event,BXConstans.CS_Metadatapath+"."+attr,false);
			if(filtComponentForInit != null && (filtComponentForInit instanceof UIRefPane)){
				items.add(BXConstans.CS_Metadatapath+"."+attr);
			}
		}
		if(!items.isEmpty()){
			UIRefPane org = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					BXConstans.CS_Metadatapath+"."+CShareDetailVO.ASSUME_ORG, false);
			setItemByorg(event, items.toArray(new String[items.size()]), org);
		}
		
	}
	/**
	 * ��̯��ϸ�У��������ı������
	 * 
	 * @param pcorgevent
	 */
	private void setpcorgCsFilter(CriteriaChangedEvent pcorgevent) {
		String[] items = new String[] { BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_CHECKELE,BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_RESACOSTCENTER };
		UIRefPane pcorg = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(pcorgevent,
				BXConstans.CS_Metadatapath+"."+CShareDetailVO.PK_PCORG, false);
		setItemByorg(pcorgevent, items, pcorg);

	}
}
