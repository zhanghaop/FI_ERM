package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.costshare.IErmCostShareBillManagePrivate;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.ui.erm.costshare.ui.CostShareEditor;
import nc.ui.erm.costshare.ui.CostShareLinkBxDlg;
import nc.ui.erm.costshare.ui.CsBillCriteriaChangedListener;
import nc.ui.erm.costshare.ui.CsBillManageModel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;

/**
 * @author luolch
 *　
 *　费用结转单单据增加 Action　
 *
 */
@SuppressWarnings("serial")
public class CsAddAction extends nc.ui.uif2.actions.AddAction {

	private QueryConditionDLG queryDialog;
	private IEditor editor;
	private CostShareLinkBxDlg fydialog;
	public void doAction(ActionEvent e) throws Exception {
		try {
			add(e);
		} catch (Exception e1) {
			//先返回列表界面再throws Exception
			((CostShareEditor)editor).getReturnaction().doAction(e);
			throw e1;
		}
	}
	
	/**
	 * 报销查询对话框
	 * @return
	 */
	public QueryConditionDLG getQryDlg() {
		if (queryDialog == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			tempinfo.setPk_Org(getModel().getContext().getPk_group());
			tempinfo.setFunNode(getModel().getContext().getNodeCode());
			tempinfo.setUserid(getModel().getContext().getPk_loginUser());
			tempinfo.setNodekey("20110501");
			/*
			* @res "查询条件"
			*/
			queryDialog = new QueryConditionDLG(getBillCardPanel(), null,
					tempinfo,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0002782"));
			queryDialog.registerCriteriaEditorListener(new CsBillCriteriaChangedListener(getModel()));


		}
		return queryDialog;
	}

	public BillCardPanel getBillCardPanel() {
		return ((BillForm)getEditor()).getBillCardPanel();
	}
	
	/**
	 * 报销单据
	 * @return
	 */
	private CostShareLinkBxDlg getBxDlg() {
		if (fydialog == null ) {
			fydialog = new CostShareLinkBxDlg(getModel().getContext());
		}
		return fydialog;
	}
	@Business(business = ErmBusinessDef.COSTSHARE, subBusiness="拉单操作"/*-=notranslate=-*/,
			description = "费用结转单拉报销单"/*-=notranslate=-*/,type = BusinessType.CORE )
	public void add(ActionEvent e) throws Exception {
		//查出对应的报销单
		int result = getQryDlg().showModal();
		if (UIDialog.ID_OK==result) {
			
			//过滤掉查询条件，新增查询时，不将需要待摊的单据查出
			String whereSQL = getQryDlg().getWhereSQL();
			List<JKBXVO> svo = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryJKBXByWhereSql(whereSQL, true);
			//初始化报销界面并弹出
			getBxDlg().initData(svo);
			result = getBxDlg().showModal();
			
			if (UIDialog.ID_OK==result) {
				//取得选中数据
				int selectedRow = getBxDlg().getBillListPanel().getHeadTable().getSelectedRow();
				if (selectedRow == -1||svo.isEmpty()) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0004")/*@res "未选中报销单据，不允许新增"*/);
				}
				AggCostShareVO csAggvo = ErmForCShareUtil.convertFromBxHead(svo.get(selectedRow).getParentVO());
				String tradetype = csAggvo.getParentVO().getAttributeValue("Pk_tradetype").toString();
				((CsBillManageModel)getModel()).setTrTypeCode(tradetype);
				
				//交易类型封存
				String group = csAggvo.getParentVO().getAttributeValue("pk_group").toString();				
				boolean queryFcbz = NCLocator.getInstance().lookup(IErmCostShareBillManagePrivate.class).queryFcbz(group,tradetype);
				if(queryFcbz){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("2011", "UPP2011-000171"));
				}
				super.doAction(e);
				getEditor().setValue(csAggvo);
				//将被清空的默认值再set上去
				((CostShareEditor)getEditor()).setDefaultValue();
				
				//事后结转处理组织多版本
				try {
					String pk_org = ((AggCostShareVO)getEditor().getValue()).getParentVO().getAttributeValue(CostShareVO.PK_ORG).toString();
					HashMap<String, String> newVID = NCLocator.getInstance().lookup(
							IOrgUnitPubService_C.class).getNewVIDSByOrgIDSAndDate(
							new String[] { pk_org },
							(UFDate)getBillCardPanel().getHeadItem(CostShareVO.BILLDATE).getValueObject());
					if(newVID.size()!=0){
						String values = newVID.get(pk_org);
						getBillCardPanel().getHeadItem(CostShareVO.PK_ORG_V).setValue(values);
					}
				} catch (BusinessException ex) {
					ExceptionHandler.handleRuntimeException(ex);
				}
				//执行公式
				((CostShareEditor)getEditor()).loadFormula();
			}

		}

	}
	
	/**
	 * @return　返回默认的DjCondVO
	 * @throws BusinessException 
	 */
//	protected DjCondVO getCondVO() throws BusinessException {
//
//		QryCondArrayVO[] vos = BXQueryUtil.getValueCondVO(false);
//
//		List<ConditionVO> condVOList = new ArrayList<ConditionVO>();
//		condVOList.addAll(Arrays.asList(getQryDlg().getQryCondEditor().getLogicalConditionVOs()));
//		condVOList.addAll(Arrays.asList(getQryDlg().getQryCondEditor().getGeneralCondtionVOs()));
//		DjCondVO cur_Djcondvo = new nc.vo.ep.dj.DjCondVO();
//		cur_Djcondvo.m_NorCondVos = vos;
//		getQryDlg().getQueryScheme().getWhereSQLOnly();
//		String whereSQL = getQryDlg().getWhereSQL()+" and zb.djlxbm <>'2647' "+" and zb.pk_jkbx not in (select src_id from er_costshare) " +
//				" zb.sxbz = "+BXStatusConst.SXBZ_VALID;
//
//		// 处理对应的zb形式，以后看看有没有其他的处理方式
//		if (whereSQL != null) {
//			if (whereSQL.indexOf(JKBXHeaderVO.PK_GROUP) > -1) {
//				whereSQL = whereSQL.substring(0, whereSQL.indexOf(JKBXHeaderVO.PK_GROUP))
//						+ " zb."+JKBXHeaderVO.PK_GROUP + " "
//						+ whereSQL.substring(whereSQL.indexOf(JKBXHeaderVO.PK_GROUP) - 2
//								+ ("zb."+JKBXHeaderVO.PK_GROUP).length()) + " ";
//			}
//			//借款报销单位
//			if ( whereSQL.indexOf(JKBXHeaderVO.PK_ORG) > -1) {
//				whereSQL = whereSQL.substring(0, whereSQL.indexOf(JKBXHeaderVO.PK_ORG))
//				+ " zb."+JKBXHeaderVO.PK_ORG + " "
//				+ whereSQL.substring(whereSQL.indexOf(JKBXHeaderVO.PK_ORG) - 2 + ("zb."+JKBXHeaderVO.PK_ORG).length()) + " ";
//			}
//			if (whereSQL.indexOf(JKBXHeaderVO.PK_JKBX) > -1) {
//				whereSQL = whereSQL.substring(0, whereSQL.indexOf("pk_jkbx")) + " zb."+JKBXHeaderVO.PK_JKBX + " "
//						+ whereSQL.substring(whereSQL.indexOf("pk_jkbx") - 2 + ("zb."+JKBXHeaderVO.PK_JKBX).length()) + " ";
//			}
//		}
//
//		String djlxbms = "";
//		ConditionVO[] logicalConditionVOs = getQryDlg().getLogicalConditionVOs();
//		if (logicalConditionVOs != null && logicalConditionVOs.length > 0) {
//			for (int i = 0; i < logicalConditionVOs.length; i++) {
//				final String value = logicalConditionVOs[i].getValue();
//				final String fieldCode = logicalConditionVOs[i].getFieldCode();
//				if (fieldCode.equals(BXQueryUtil.PZZT)) {
//					// aded by chendya 避免选择后取消再点击查询时出现空指针异常
//					if (!StringUtil.isEmpty(value)) {
//						Integer[] Ivalues = BXQueryUtil.splitQueryConditons(value);
//						cur_Djcondvo.VoucherFlags = Ivalues;
//					}
//					// --end
//				}
//				else if (fieldCode.equals(BXQueryUtil.XSPZ)) {
//					cur_Djcondvo.isLinkPz = new UFBoolean(value).booleanValue();
//				}
//				else if (fieldCode.equals(BXQueryUtil.APPEND)) {
//					cur_Djcondvo.isAppend = new UFBoolean(value).booleanValue();
//				}
//				else if (fieldCode.equals(BXQueryUtil.DJLXBM)) {
//					djlxbms = value;
//				}
//			}
//		}
//
//		String djlxbmStr = "";
//		if (!StringUtils.isNullWithTrim(djlxbms)) {
//			if (djlxbms.indexOf("(") == -1) {
//				djlxbmStr = " zb.djlxbm='" + djlxbms + "'";
//			} else {
//				djlxbmStr = " zb.djlxbm in " + djlxbms + "";
//			}
//		} 
//		if (djlxbmStr.length() != 0) {
//			{
//				whereSQL = whereSQL + " and " + djlxbmStr;
//			}
//		}
//
//		String user = getModel().getContext().getPk_loginUser();
//
//		cur_Djcondvo.defWhereSQL = whereSQL;
//		cur_Djcondvo.isCHz = false;
//		cur_Djcondvo.operator = user;
//		cur_Djcondvo.isInit = false;
//		cur_Djcondvo.nodecode = BXConstans.BXBILL_QUERY;
//		cur_Djcondvo.djdl = BXConstans.BX_DJDL;
//
////		 added by chendya 追加数据权限查询
//		 String dataPowerSql  = getDataPowerSql(getQryDlg());
//		 cur_Djcondvo.setDataPowerSql(dataPowerSql);
////		 --end
//		return cur_Djcondvo;
//	}

	/**
	 * 返回查询数据权限SQL added by chendya
	 *
	 * @throws BusinessException
	 */
//	protected String getDataPowerSql(QueryConditionDLG dlg)
//			throws BusinessException {
//		QueryConditionEditor editor = dlg.getQryCondEditor();
//		if (editor == null) {
//			return null;
//		}
//		List<FilterMeta> allFilterMeta = editor.getAllFilterMeta();
//		// K,V=编码，{元数据路径，使用场景}
//		Map<String, List<String>> map = new HashMap<String, List<String>>();
//		for (Iterator<FilterMeta> iterator = allFilterMeta.iterator(); iterator
//				.hasNext();) {
//			FilterMeta filterMeta = iterator.next();
//			int dataType = filterMeta.getDataType();
//			if (IQueryConstants.UFREF == dataType) {
//				if (filterMeta instanceof MDFilterMeta) {
//					MDFilterMeta meta = ((MDFilterMeta) filterMeta);
//					List<String> value = new ArrayList<String>();
//					value
//							.add(((Attribute) meta.getAttribute())
//									.getDataTypeID());
//					value.add(meta.getDataPowerOperation());
//					map.put(meta.getFieldCode(), value);
//				}
//			}
//		}
//		Map<String, String> tableMap = getDataPowerTable(map);
//		StringBuffer sql = new StringBuffer();
//		Set<Entry<String, String>> entrySet = tableMap.entrySet();
//		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator
//				.hasNext();) {
//			Entry<String, String> entry = iterator.next();
//			final String column = entry.getKey();
//			final String table = entry.getValue();
//			if (sql.length() > 0) {
//				sql.append(" and ");
//			}
//			String field = column.indexOf(".") > 0 ? "fb."
//					+ column.substring(column.indexOf(".") + 1) : "zb."
//					+ column;
//			sql.append(field).append(" in ").append("(").append(
//					"select pk_doc from " + table).append(")");
//		}
//		return sql.toString();
//	}
	
	/**
	 * 返回字段对应的数据权限表
	 *
	 * @param map
	 * @return
	 * @throws BusinessException
	 */
//	private Map<String, String> getDataPowerTable(Map<String, List<String>> map)
//			throws BusinessException {
//		Collection<List<String>> values = map.values();
//		Map<String, String> retMap = new HashMap<String, String>();
//		List<String> beanIDList = new ArrayList<String>();
//		List<String> operationCodeList = new ArrayList<String>();
//		for (Iterator<List<String>> iterator = values.iterator(); iterator
//				.hasNext();) {
//			List<String> list = iterator.next();
//			beanIDList.add(list.get(0));
//			operationCodeList.add(list.get(1));
//		}
//		
//		String[] dataPowerTables = NCLocator.getInstance().lookup(
//				IDataPermissionPubService.class)
//				.getDataPermProfileTableNameByBeanID(BXUiUtil.getPk_user(),
//						beanIDList.toArray(new String[0]),
//						operationCodeList.toArray(new String[0]),
//						BXUiUtil.getPK_group());
//		String[] columns = map.keySet().toArray(new String[0]);
//		for (int i = 0; i < columns.length; i++) {
//			if (dataPowerTables[i] != null) {
//				retMap.put(columns[i], dataPowerTables[i]);
//			}
//		}
//		return retMap;
//	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public IEditor getEditor() {
		return editor;
	}



}