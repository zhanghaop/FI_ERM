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
 * 报销管理帐表查询对话框基类
 * @author chendya
 *
 */
public abstract class ErmAbstractReportBaseDlg extends ReportQryDlg {

	private static final long serialVersionUID = 1416820562038177684L;

	protected static final String QRY_MODE_COMB = "qryModeComb"; // 查询方式
	protected static final String BILL_STATE_COMB = "billStateComb"; // 单据状态
	protected static final String BEGIN_TIME_REF = "beginTimeRef"; // 开始时间
	protected static final String END_TIME_REF = "endTimeRef"; // 结束时间
	protected static final String CURRENCY_REF = "currencyRef"; // 币种
	protected static final String FINANCIAL_ORG_REF = "financialOrgRef"; // 财务组织
	protected static final String TIME_TYPE_LABEL = "timeTypeLabel";
	
	//组织相关联的字段
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
		// 修改查询条件
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
							//锁定借款 报销单位
							vo.setIfImmobility(UFBoolean.TRUE);
						}
					}
				}
			}
		});
	}

	/**
	 * 返回查询对象中受财务组织影响的字段
	 * @return
	 */
	protected abstract String[] getOrgRelationField();

	/**
	 * 业务单元变化监听器<br>
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
		//财务组织设置pk
		UIRefPane refPaneOrg = BXQryTplUtil.getRefPaneByFieldCode(this,JKBXHeaderVO.PK_ORG);
		refPaneOrg.setPKs((String[])newPk_orgs);
		
		//组织相关字段设置过滤pk
		UIRefPane[] refPanes = BXQryTplUtil.getRefPaneByFieldCode(this, (String[])getOrgRefFields(JKBXHeaderVO.PK_ORG).toArray(new String[0]));
		for(UIRefPane refPane : refPanes){
			if(refPane != null){
				refPane.getRefModel().setPk_org(newPk_orgs[0]);
			}
		}
	}

	@Override
	protected String doBusiCheck() {
		// ①执行父类校验
		String errMsg = super.doBusiCheck();
		if (!StringUtils.isEmpty(errMsg)) {
			return errMsg;
		}

		try {
			errMsg = "";

			// ②财务组织不允许为空
			String[] pk_orgs = getPk_org();
			if (ArrayUtils.isEmpty(pk_orgs) || StringUtils.isEmpty(pk_orgs[0])) {
				errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0028")/*@res "财务组织不允许为空"*/;
			} else if (pk_orgs.length > 1) {
				// ③财务组织本位币必须一致
				Set<String> localCurrtype = new HashSet<String>();
				CurrtypeVO[] currtypeVOs = ErmReportPubUtil.getLocalCurrencyByOrgID(pk_orgs);
				for (CurrtypeVO vo : currtypeVOs) {
					localCurrtype.add(vo.getPk_currtype());
				}
				if (localCurrtype.size() > 1) {
					errMsg += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0029")/*@res "\r\n多个查询组织的本位币不一致，请分别查询！"*/;
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
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0015")/*@res "不支持别名"*/);
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
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0022")/*@res "获取界面查询条件异常。"*/);
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
				//编辑报销单位时，其它与此组织相关的查询条件根据此过滤
				BXQryTplUtil.orgCriteriaChanged(evt, orgRelKeyList);
			}else if(JKBXHeaderVO.FYDWBM.equals(evt.getFieldCode())){
				//编辑费用承担单位时，其它与此组织相关的查询条件根据此过滤
				BXQryTplUtil.orgCriteriaChanged(evt, fyOrgRelKeyList);
			}else if(JKBXHeaderVO.DWBM.equals(evt.getFieldCode())){
				//编辑借款报销人单位时，其它与此组织相关的查询条件根据此过滤
				BXQryTplUtil.orgCriteriaChanged(evt, userOrgRelKeyList);
			}else if (orgRelKeyList.contains(evt.getFieldCode())) {
				//编辑报销单位相关字段时，相关字段设置组织为借款报销单位
				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.PK_ORG,getPk_org());
			}else if (fyOrgRelKeyList.contains(evt.getFieldCode())) {
				//编辑费用承担单位相关字段时，相关字段设置组织为费用承担单位
				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.FYDWBM,null);
			}else if (userOrgRelKeyList.contains(evt.getFieldCode())) {
				//编辑报销人单位相关字段时，相关字段设置组织为报销人单位
				BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.DWBM,null);
			}
		}
	}

	/**
	 * 缓存根据组织字段过滤的字段[key=组织，value=关联字段]
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
	 * 返回报销管理查询对话框组织相关字段
	 * @return
	 */
	protected List<String> getOrgFieldList(){
		return Arrays.asList(new String[]{JKBXHeaderVO.PK_ORG,JKBXHeaderVO.FYDWBM,JKBXHeaderVO.DWBM});
	}

	/**
	 * 处理报销管理帐表查询对话框查询条件的切换拖拽事件
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
	 * 功能：设置常用查询条件VO
	 */
	protected void setQueryCond(ReportQueryCondVO qryCondVO) throws BusinessException {
		//
	}

	/**
	 * 功能：得到查询组织<br>
	 * 默认或者为空时，返回当前登陆组织 暂时注销<br>
	 *
	 * @return
	 */
	protected String[] getPk_org() {
		return ((UIRefPane) getComponent(FINANCIAL_ORG_REF)).getRefPKs();
	}

	protected String getDefaultOrg() {
		// 获得个性化中默认主组织
		String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();
		String pk_org = null;
		if (!StringUtils.isEmpty(defaultOrg)) {
			String[] values = BXUiUtil.getPermissionOrgs(getNodeCode());
			if (ArrayUtils.isEmpty(values)
					|| Arrays.asList(values).contains(defaultOrg)) {
				// 取默认组织
				pk_org = defaultOrg;
			}
		}

		return pk_org;
	}

	/**
	 * 功能：设置财务组织
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
	 * 返回需要根据组织过滤查询对象的查询对象对应的元数据ID
	 * 此处需要对应的此功能的通过业务单元过滤的情况，添加对应的元数据id
	 *
	 * @return
	 */
	@Override
	protected List<String> getMultiOrgRef() {
		List<String> list = super.getMultiOrgRef();
		list.add(IPubReportConstants.BUSINESS_UNIT); // 财务组织
		list.add(IBDMetaDataIDConst.DEPT); // 部门
		list.add(IBDMetaDataIDConst.PSNDOC); // 人员
		//v6.1新增成本中心
		list.add(IPubReportConstants.MDID_COSTCENTER);
		return list;
	}

}