package nc.ui.erm.pub;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.SwingConstants;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.comp.ReportQryDlg;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.IQueryTemplateTotalVOProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.pub.ErmReportPubUtil;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;

/**
 * ���������ʱ��ѯ�Ի������
 * @author chendya
 *
 */
public abstract class ErmAbstractReportBaseDlg extends ReportQryDlg {

	private static final long serialVersionUID = 1416820562038177684L;

	protected static final String QRY_MODE_COMB = "qryModeComb"; // ��ѯ��ʽ
	protected static final String BILL_STATE_COMB = "billStateComb"; // ����״̬
	protected static final String BEGIN_TIME_REF = "beginTimeRef"; // ��ʼʱ��
	protected static final String END_TIME_REF = "endTimeRef"; // ����ʱ��
	protected static final String CURRENCY_REF = "currencyRef"; // ����
	protected static final String FINANCIAL_ORG_REF = "financialOrgRef"; // ������֯
	protected static final String TIME_TYPE_LABEL = "timeTypeLabel";
	
	//��֯��������ֶ�
	private List<String> orgRelKeyList;
	private List<String> fyOrgRelKeyList;
	private List<String> userOrgRelKeyList;

	public ErmAbstractReportBaseDlg(Container parent, IContext context,
			String strNodeCode, int iSysCode, TemplateInfo ti, String title) {
		super(parent, context, strNodeCode, iSysCode, ti, title);
		
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(BXConstans.BILLTYPECODE_CLFBX,BXConstans.BX_DJDL);
		orgRelKeyList = busTypeVO.getPayentity_billitems();
		fyOrgRelKeyList = busTypeVO.getCostentity_billitems();
		userOrgRelKeyList = busTypeVO.getUseentity_billitems();
		
		initialize();
	}

	private void initialize(){
		// �޸Ĳ�ѯ����
		registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
			public void processQueryTempletTotalVO(QueryTempletTotalVO totalVO) {
				QueryConditionVO[] vos = totalVO.getConditionVOs();
				if (vos == null || vos.length == 0) {
					return;
				}
				for(QueryConditionVO vo: vos){
					if(JKBXHeaderVO.PK_ORG.equals(vo.getFieldCode())){
						String[] pkOrg = getPk_org();
						if(pkOrg!=null && pkOrg.length>0){
							vo.setValue(pkOrg[0]);
							//������� ������λ
							vo.setIfImmobility(UFBoolean.TRUE);
						}
					}
				}
			}
		});
	}

	/**
	 * ���ز�ѯ�������ܲ�����֯Ӱ����ֶ�
	 * @return
	 */
	protected abstract String[] getOrgRelationField();

	/**
	 * ҵ��Ԫ�仯������<br>
	 *
	 * @since V60<br>
	 */
	public class OrgChangedListener implements ValueChangedListener {
		public void valueChanged(ValueChangedEvent event) {
			try {
				handleOrgChangeEvent(event);
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e.getMessage());
			}
		}
	}

	protected void handleOrgChangeEvent(ValueChangedEvent event)
			throws BusinessException {
		String[] newPk_orgs = (String[])event.getNewValue();
		if (newPk_orgs == null) {
			setPk_org(null);
			return;
		} else {
			setPk_org((String[]) newPk_orgs);
		}
		//������֯����pk
		UIRefPane refPaneOrg = BXQryTplUtil.getRefPaneByFieldCode(this,JKBXHeaderVO.PK_ORG);
		refPaneOrg.setPKs((String[])newPk_orgs);
		
		//��֯����ֶ����ù���pk
		UIRefPane[] refPanes = BXQryTplUtil.getRefPaneByFieldCode(this, (String[])getOrgRefFields(JKBXHeaderVO.PK_ORG).toArray(new String[0]));
		for(UIRefPane refPane : refPanes){
			if(refPane != null){
				refPane.getRefModel().setPk_org(newPk_orgs[0]);
			}
		}
	}

	@Override
	protected String doBusiCheck() {
		// ��ִ�и���У��
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}

		try {
			errMsg = "";

			// �ڲ�����֯������Ϊ��
			String[] pk_orgs = getPk_org();
			if (ArrayUtils.isEmpty(pk_orgs) || StringUtils.isEmpty(pk_orgs[0])) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0028")/*@res "������֯������Ϊ��"*/;
			} else if (pk_orgs.length > 1) {
				// �۲�����֯��λ�ұ���һ��
				Set<String> localCurrtype = new HashSet<String>();
				CurrtypeVO[] currtypeVOs = ErmReportPubUtil.getLocalCurrencyByOrgID(pk_orgs);
				for (CurrtypeVO vo : currtypeVOs) {
					localCurrtype.add(vo.getPk_currtype());
				}
				if (localCurrtype.size() > 1) {
					errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0029")/*@res "\r\n�����ѯ��֯�ı�λ�Ҳ�һ�£���ֱ��ѯ��"*/;
				}
			}
		} catch (BusinessException e) {
			errMsg = e.getMessage();
		}

		return StringUtils.isEmpty(errMsg) ? null : errMsg;
	}

	protected UILabel getShowLabel(String labelName) {
		UILabel tempLabel = new UILabel(labelName);
		tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
		return tempLabel;
	}

	protected UILabel getShowLabel0(String labelName) {
		UILabel tempLabel = new UILabel(labelName);
		tempLabel.setHorizontalAlignment(SwingConstants.LEFT);
		return tempLabel;
	}

	public static String convertQueryTemplateSql(String oriSql) {
		if (StringUtils.isEmpty(oriSql)) {
			return "";
		}

		Map<String, String> filters = new HashMap<String, String>();
		filters.put("er_bxzb.", "zb.");
		filters.put("er_jkzb.", "zb.");

		Map<String, String> tableAlas = new HashMap<String, String>();
		Set<Entry<String, String>> entrySet = filters.entrySet();
		for (Entry<String, String> entry : entrySet) {
			if(oriSql.contains(entry.getKey())){
				tableAlas.put(entry.getKey(), entry.getValue());
			}
		}
		if(tableAlas.isEmpty()){
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0015")/*@res "��֧�ֱ���"*/);
		}
		for (Entry<String, String> entry : tableAlas.entrySet()) {
			oriSql = StringUtils.replace(oriSql, entry.getKey(), entry.getValue());
		}
		return oriSql;
	}

	@Override
	public IReportQueryCond getReportQueryCondVO() throws BusinessException {
		ReportQueryCondVO queryCondVO = (ReportQueryCondVO) super.getReportQueryCondVO();
		try {
			getQryCondEditor().setPowerEnable(false);
			queryCondVO.setWhereSql(convertQueryTemplateSql(getQryCondEditor().getQueryScheme()
							.getTableListFromWhereSQL().getWhere()));

			setQueryCond(queryCondVO);
		} catch (InvalidAccperiodExcetion e) {
			Logger.error(e.getMessage(), e, this.getClass(), "getReportQueryCondVO");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0022")/*@res "��ȡ�����ѯ�����쳣��"*/);
		} finally{
			getQryCondEditor().setPowerEnable(true);
		}

		return queryCondVO;
	}

	@Override
	protected void resetTemplateField(CriteriaChangedEvent evt) {
		if(evt.getCriteriaEditor()!=null){
			UIRefPane refPane = BXQryTplUtil.getRefPaneByFieldCode(evt, JKBXHeaderVO.DJLXBM);
			if (JKBXHeaderVO.DJLXBM.equalsIgnoreCase(evt.getFieldCode())) {
				String newWherePart = " systemcode in ('erm') and pk_billtypecode like '26%' and istransaction='Y' ";
				refPane.getRefModel().setWherePart(newWherePart);
			}else if (JKBXHeaderVO.PK_ORG.equals(evt.getFieldCode())) {
				//�༭������λʱ�����������֯��صĲ�ѯ�������ݴ˹���
				BXQryTplUtil.orgCriteriaChanged(evt, orgRelKeyList);
			}else if(JKBXHeaderVO.FYDWBM.equals(evt.getFieldCode())){
				//�༭���óе���λʱ�����������֯��صĲ�ѯ�������ݴ˹���
				BXQryTplUtil.orgCriteriaChanged(evt, fyOrgRelKeyList);
			}else if(JKBXHeaderVO.DWBM.equals(evt.getFieldCode())){
				//�༭�����˵�λʱ�����������֯��صĲ�ѯ�������ݴ˹���
				BXQryTplUtil.orgCriteriaChanged(evt, userOrgRelKeyList);
			}else if (orgRelKeyList.contains(evt.getFieldCode())) {
				//�༭������λ����ֶ�ʱ������ֶ�������֯Ϊ������λ
				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.PK_ORG,getPk_org());
			}else if (fyOrgRelKeyList.contains(evt.getFieldCode())) {
				//�༭���óе���λ����ֶ�ʱ������ֶ�������֯Ϊ���óе���λ
				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.FYDWBM,null);
			}else if (userOrgRelKeyList.contains(evt.getFieldCode())) {
				//�༭�����˵�λ����ֶ�ʱ������ֶ�������֯Ϊ�����˵�λ
				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.DWBM,null);
			}
		}
	}

	/**
	 * ���������֯�ֶι��˵��ֶ�[key=��֯��value=�����ֶ�]
	 */
	protected Map<String, List<String>> orgRefFieldsMap = new HashMap<String, List<String>>();

	protected List<String> getOrgRefFields(String orgField) {
		if(!orgRefFieldsMap.containsKey(orgField)){
			List<String> refFields = new ArrayList<String>();
			if (JKBXHeaderVO.PK_ORG.equals(orgField)) {
				refFields = BXUtil.getBusTypeVO(null, BXConstans.JK_DJDL).getPayentity_billitems();
			} else if (JKBXHeaderVO.FYDWBM.equals(orgField)) {
				refFields = BXUtil.getBusTypeVO(null, BXConstans.JK_DJDL).getCostentity_billitems();
			} else if (JKBXHeaderVO.DWBM.equals(orgField)) {
				refFields = BXUtil.getBusTypeVO(null, BXConstans.JK_DJDL).getUseentity_billitems();
			}
			orgRefFieldsMap.put(orgField, refFields);
		}
		return orgRefFieldsMap.get(orgField);

	}

	/**
	 * ���ر��������ѯ�Ի�����֯����ֶ�
	 * @return
	 */
	protected List<String> getOrgFieldList(){
		return Arrays.asList(new String[]{JKBXHeaderVO.PK_ORG,JKBXHeaderVO.FYDWBM,JKBXHeaderVO.DWBM});
	}

	/**
	 * �����������ʱ��ѯ�Ի����ѯ�������л���ק�¼�
	 * @param event
	 */
	protected void orgCriteriaChangedEvent(CriteriaChangedEvent evt,List<String> orgRelKeyList) throws BusinessException{
		if(BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt) != null
				&&BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt).length>0){
			String pk_org = BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt)[0];
			for (String key : orgRelKeyList) {
				BXQryTplUtil.setOrgForField(evt.getCriteriaEditor(), key, pk_org);
			}
		}
	}

	/**
	 * ���ܣ����ó��ò�ѯ����VO
	 */
	protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		//
	}

	/**
	 * ���ܣ��õ���ѯ��֯<br>
	 * Ĭ�ϻ���Ϊ��ʱ�����ص�ǰ��½��֯ ��ʱע��<br>
	 *
	 * @return
	 */
	protected String[] getPk_org() {
		return ((UIRefPane) getComponent(FINANCIAL_ORG_REF)).getRefPKs();
	}

	protected String getDefaultOrg() {
		// ��ø��Ի���Ĭ������֯
		String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();
		String pk_org = null;
		if (!StringUtils.isEmpty(defaultOrg)) {
			String[] values = BXUiUtil.getPermissionOrgs(getNodeCode());
			if (ArrayUtils.isEmpty(values)
					|| Arrays.asList(values).contains(defaultOrg)) {
				// ȡĬ����֯
				pk_org = defaultOrg;
			}
		}

		return pk_org;
	}

	/**
	 * ���ܣ����ò�����֯
	 *
	 * @return String[]
	 */
	public void setPk_org(String[] pkOrgs) {
		super.setPk_org(pkOrgs);
		String pk_org = null;
		if (pkOrgs != null && pkOrgs.length > 0) {
			pk_org = pkOrgs[0];
		}
		String[] refKey = getOrgRelationField();;
		UIRefPane ref = null;
		for (String key : refKey) {
			ref = (UIRefPane) (getComponent(key));
			if (ref.getRefModel() != null) {
				ref.setPk_org(pk_org);
			}
		}
	}

	/**
	 * ������Ҫ������֯���˲�ѯ����Ĳ�ѯ�����Ӧ��Ԫ����ID
	 * �˴���Ҫ��Ӧ�Ĵ˹��ܵ�ͨ��ҵ��Ԫ���˵��������Ӷ�Ӧ��Ԫ����id
	 *
	 * @return
	 */
	@Override
	protected List<String> getMultiOrgRef() {
		List<String> list = super.getMultiOrgRef();
		list.add(IPubReportConstants.BUSINESS_UNIT); // ������֯
		list.add(IBDMetaDataIDConst.DEPT); // ����
		list.add(IBDMetaDataIDConst.PSNDOC); // ��Ա
		//v6.1�����ɱ�����
		list.add(IPubReportConstants.MDID_COSTCENTER);
		return list;
	}

}