package nc.ui.arap.bx;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.djlx.ErmBilltypeRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.remote.QcDateCall;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.ICriteriaEditor;
import nc.ui.querytemplate.IQueryTemplateTotalVOProcessor;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.ui.querytemplate.operator.IOperatorConstants;
import nc.ui.querytemplate.querytreeeditor.QueryTreeEditor;
import nc.ui.querytemplate.simpleeditor.SimpleEditor;
import nc.ui.querytemplate.value.IFieldValue;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueElementEditorFactory;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditor;
import nc.ui.querytemplate.valueeditor.RefElementEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefElementEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefPanel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXQueryUtil;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.query.IQueryConstants;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;

/**
 * @author twei
 * 
 *         报销查询对话框（新查询模板实现）
 * 
 *         nc.ui.arap.bx.BxQueryDLG
 */
@SuppressWarnings({ "serial" })
public class BxQueryDLG extends QueryConditionDLG {

//	private BXBillMainPanel parent;

	private String djlxbm;
	
	//币种最大精度(默认2位)
	private int maxCurrDigital = 2;
	
	public String getSTR_JK() {
	    return  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0041")/* @res* "借款"*/;
	}

	public String getSTR_BX() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0042")/* @res* "报销"*/;
	}
	
	/** 只有报销单才处理的字段 */
	final List<String> onlyBxFieldsList = Arrays.asList(new String[]{ "zb.zfybje", "zb.zfbbje","zb.hkybje", "zb.hkbbje" });

	/** 只有借款单才处理的字段 */
	final List<String> onlyJkFieldsList = Arrays.asList(new String[] { "zb.qzzt", "zb.zhrq","zb.zpxe" });

	/**借款报销原币金额字段*/
	final List<String> ybjeFieldsList = Arrays.asList(JKBXHeaderVO.getYbjeField());
	
	//组织相关联的字段
	private List<String> orgRelKeyList;
	private List<String> fyOrgRelKeyList;
	private List<String> userOrgRelKeyList;
	
	/**
	 * 借款报销查询对话框
	 * @author chendya
	 * @param parent
	 * @param normalQueryPanel
	 * @param tplInfo
	 * @param title
	 * @param nodecode 节点号
	 */
	public BxQueryDLG(Container parent, INormalQueryPanel normalQueryPanel,
			TemplateInfo tplInfo, String title, String nodecode,String oldnodecode ,String djlxbm) {

		super(parent, normalQueryPanel, tplInfo, title);
//		if(parent instanceof BXBillMainPanel){
//			this.parent = (BXBillMainPanel) parent;
//		}
		this.djlxbm = djlxbm;
		String djdl = djlxbm!=null && djlxbm.startsWith("263") ? BXConstans.JK_DJDL : BXConstans.BX_DJDL;
		
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(djlxbm,djdl);
		
		orgRelKeyList = busTypeVO.getPayentity_billitems();
		fyOrgRelKeyList = busTypeVO.getCostentity_billitems();
		userOrgRelKeyList = busTypeVO.getUseentity_billitems();
		
		if (tplInfo.getFunNode().equals(BXConstans.BXINIT_NODECODE)) {
			//常用单据初始化
			init4InitBillQryDlg(parent);
		} else {
			initQryDlg(parent, oldnodecode);
		}
		//重新加载查询条件
		try {
			initUIData();
		} catch (Exception e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		// added by chendya 作废查询模版自带的数据权限，拼接sql时自己写;
		// 避免查询时，查询模版默认返回的表头表体szxmid对应的数据权限出现 列名不明确(不知道是主表的还是子表的)
		getQryCondEditor().setPowerEnable(false);
	}
	
//	@Override
//	public String toString() {
//		if (parent != null && parent.getCache() != null) {
//			return parent.getNodeCode() + ","
//					+ parent.getCache().getCurrentDjdl() + ","
//					+ parent.getCache().getCurrentDjlxbm();
//		}
//		return super.toString();
//	}

	/**
	 * 初始化常用单据查询对话框
	 * 
	 */
	private void init4InitBillQryDlg(Container parent) {

		registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {

			public void processQueryTempletTotalVO(QueryTempletTotalVO totalVO) {
				QueryConditionVO[] conds = totalVO.getConditionVOs();
				for (int i = 0; i < conds.length; i++) {
					String fieldCode = conds[i].getFieldCode();
					if (("zb." + JKBXHeaderVO.PK_GROUP).equals(fieldCode)) {
						String pk_corp = BXUiUtil.getBXDefaultOrgUnit();
						if (!pk_corp.equals(BXConstans.GROUP_CODE)) {
							pk_corp += ",0001";
						}
						conds[i].setValue(pk_corp);
						conds[i].setIfImmobility(UFBoolean.TRUE);
					}
				}
			}
		});
	}

	/**
	 * 修改查询条件
	 * 
	 * @param conds
	 * @param nodecode
	 */
	private void initQryCondition(QueryConditionVO[] conds, String nodecode) {
		for (int i = 0; i < conds.length; i++) {
			String fieldCode = conds[i].getFieldCode();
			// 设置对应的默认值，锁定等
			if ((JKBXHeaderVO.PK_GROUP).equals(fieldCode)) {
				conds[i].setIfDefault(UFBoolean.TRUE);
				conds[i].setIfImmobility(UFBoolean.TRUE);
				conds[i].setValue(BXUiUtil.getPK_group());
				conds[i].setIsCondition(UFBoolean.FALSE);
			}else if ((JKBXHeaderVO.PK_ORG).equals(fieldCode)) {
				
				//如果模板中有先取模板上的 
				if(conds[i].getValue()!=null){
					continue;
				}
				
				// 获得个性化中默认主组织<1>
				final String pk_org = BXUiUtil.getBXDefaultOrgUnit();
				
				conds[i].setIfMust(UFBoolean.TRUE);
				conds[i].setIfAutoCheck(UFBoolean.TRUE);
				conds[i].setValue(pk_org);
				conds[i].setIsSysFuncRefUsed(UFBoolean.TRUE);
				
			} 
			else if ((JKBXHeaderVO.DJRQ).equals(fieldCode)) {
				if (!BXConstans.BXLR_QCCODE.equals(nodecode)) {
					UFDate currDate = BXUiUtil.getBusiDate();
					UFDate beginDate = currDate.getDateBefore(30);
					conds[i].setIfMust(UFBoolean.TRUE);
					conds[i].setValue(beginDate.toLocalString() + "," + currDate.toLocalString());
				} else if (BXConstans.BXLR_QCCODE.equals(nodecode)) {
					UFDate startDate = getQcInitDate();
					if (startDate == null) {
						Log.getInstance(this.getClass()).error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("expensepub_0" , "02011002-0001")/* @res"该组织模块启用日期为空"*/);
					}else if (BXConstans.BXLR_QCCODE.equals(nodecode)) {
						// 期初单据单据日期不做<=某个单据日期控制
						UFDateTime sysDateTime = BXUiUtil.getSysdatetime();
						UFDateTime startDateTime = new UFDateTime(startDate,sysDateTime.getUFTime());
						conds[i].setIfMust(UFBoolean.TRUE);
						conds[i].setValue(startDateTime.getDate().getDateBefore(1).toLocalString());
						conds[i].setOperaCode("<=@=@>@>=@<@between@");
					}
				}
			} else if (onlyJkFieldsList.contains(fieldCode) && !isJKNode(nodecode)) {
				conds[i].setIfUsed(UFBoolean.FALSE);
			} else if (onlyBxFieldsList.contains(fieldCode) && !isBxNode(nodecode)) {
				conds[i].setIfUsed(UFBoolean.FALSE);
			} else if (fieldCode.equals("zb.djlxbm") && isJKNode(nodecode)) {
				// 单纯的借款报销节点，设置默认的单据类型值，该字段值为默认锁定，不能修改。
				conds[i].setIfImmobility(UFBoolean.TRUE);
				conds[i].setIfUsed(UFBoolean.FALSE);
			} else if (BXQueryUtil.PZZT.equals(fieldCode)) { 
				// 凭证状态
				conds[i].setIsCondition(UFBoolean.FALSE);
			} else if (BXQueryUtil.XSPZ.equals(fieldCode)) { 
				// 是否显示凭证
				conds[i].setIsCondition(UFBoolean.FALSE);
			} else if (BXQueryUtil.APPEND.equals(fieldCode)) { 
				// 是否追加显示结果
				conds[i].setIsCondition(UFBoolean.FALSE);
			} else if(ybjeFieldsList.contains(fieldCode)){
				if(conds[i].getDataType()==IQueryConstants.DECIMAL){
					conds[i].setConsultCode(maxCurrDigital+"");
				}
			}else if ((JKBXHeaderVO.DJZT).equals(fieldCode)&&"201105CSMG".equals(nodecode)){
				conds[i].setIfDefault(UFBoolean.TRUE);
				conds[i].setIfImmobility(UFBoolean.TRUE);
				conds[i].setValue(Integer.valueOf(3).toString());
				conds[i].setIsCondition(UFBoolean.TRUE);
			}
			
		}
	}
	/**
	 * 返回期初单据初始化日期
	 * @return
	 */
	private static UFDate getQcInitDate(){
		String pk_org = null;
		UFDate startDate = null;
		// 获得个性化中默认主组织<1>
		if (WorkbenchEnvironment.getInstance().getClientCache(QcDateCall.QcDate_Org_PK_ + BXUiUtil.getPK_group()) != null) {
			pk_org = (String) WorkbenchEnvironment.getInstance().getClientCache(QcDateCall.QcDate_Org_PK_+ BXUiUtil.getPK_group());
			startDate = (UFDate) WorkbenchEnvironment.getInstance().getClientCache(QcDateCall.QcDate_Date_PK_ + pk_org);
		} else {
			final String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();

			if (defaultOrg != null && defaultOrg.length() > 0) {
				pk_org = defaultOrg;
			} else {
				// 取当前登录人所属组织
				pk_org = ErUiUtil.getPsnPk_org(ErUiUtil.getPk_psndoc());
			}
			if (pk_org != null && pk_org.length() > 0) {

				try {
					startDate = BXUiUtil.getStartDate(pk_org);
				} catch (BusinessException e) {
				}
			}
		}
		return startDate;
	}

	/**
	 * 初始化查询对话框，添加参照过滤，修改查询条件等
	 * 
	 * @author chendya
	 * @param parent
	 * @param nodecode
	 */
	private void initQryDlg(Container parent, final String nodecode) {

		// 修改查询条件
		registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
			public void processQueryTempletTotalVO(QueryTempletTotalVO totalVO) {
				QueryConditionVO[] conds = totalVO.getConditionVOs();
				if (conds == null || conds.length == 0) {
					return;
				}
				// 修改查询条件
				initQryCondition(conds, nodecode);
			}
		});
		
		//处理DefaultFieldValueElementEditor参照过滤
		DefaultFieldValueElementEditorFactory factory = new DefaultFieldValueElementEditorFactory(getQueryContext()){
			@Override
			public IFieldValueElementEditor createFieldValueElementEditor(FilterMeta meta) {
				IFieldValueElementEditor fieldValueElementEditor = super.createFieldValueElementEditor(meta);
				if(JKBXHeaderVO.PK_ORG.equals(meta.getFieldCode())){
					UIRefPane refPane = null;
					if (meta.isSysFuncRefUsed()&& fieldValueElementEditor instanceof CompositeRefElementEditor) {
						//1.带系统函数情况
						CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) fieldValueElementEditor;
						CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
						refPane = compositeRefPanel.getCurrentRefPane();
					}else{
						//2.修改查询模版，去掉系统函数的情况
						RefElementEditor refElementEditor = (RefElementEditor) fieldValueElementEditor;
						refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
					}
					
					//隐藏集团，显示多组织
					hiddenRefPaneMultiCorp(refPane);
					
					refPane.getRefModel().setFilterPks(BXUiUtil.getPermissionOrgs(nodecode),IFilterStrategy.INSECTION);
					
					// 允许参照出已停用的组织
					refPane.getRefModel().setDisabledDataShow(true);

					// 不受数据权限控制
					refPane.getRefModel().setUseDataPower(false);
				}
				else if(JKBXHeaderVO.DJLXBM.equals(meta.getFieldCode())){
					UIRefPane refPane = null;
					if (meta.isSysFuncRefUsed()&& fieldValueElementEditor instanceof CompositeRefElementEditor) {
						//1.带系统函数情况
						CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) fieldValueElementEditor;
						CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
						refPane = compositeRefPanel.getCurrentRefPane();
					}else{
						//2.修改查询模版，去掉系统函数的情况
						RefElementEditor refElementEditor = (RefElementEditor) fieldValueElementEditor;
						refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
					}
					AbstractRefModel refModel = refPane.getRefModel();
					if(refModel instanceof ErmBilltypeRefModel){
						ErmBilltypeRefModel model = (ErmBilltypeRefModel) refModel;
						model.setPk_group(BXUiUtil.getPK_group());
						if (!StringUtil.isEmpty(djlxbm)) {
							model.setWherePart(" pk_billtypecode='" + djlxbm+ "'");
						}
						//期初节点
						else if (BXConstans.BXLR_QCCODE.equals(nodecode)) {
							model.setWherePart(" pk_billtypecode like '263%' ");
						}
					}
				}
				return fieldValueElementEditor;
			}
		};
		
		//修改查询条件编辑器
		registerFieldValueEelementEditorFactory(factory);

		//编辑后过滤
		registerCriteriaEditorListener(this,orgRelKeyList,fyOrgRelKeyList,userOrgRelKeyList);
		
	}


	@Override
	public String checkCondition() {
		String result = getQryCondEditor().checkCondition(false);
		if (result == null) {
			ICriteriaEditor criteriaEditor = this.getQryCondEditor()
					.getCurrentCriteriaEditor();
			List<IFilter> filters = null;
			if (criteriaEditor instanceof SimpleEditor) {
				SimpleEditor simpleeditor = (SimpleEditor) criteriaEditor;
				List<IFilterEditor> editors = simpleeditor.getFilterEditors();
				filters = new ArrayList<IFilter>();
				for (IFilterEditor editor : editors) {
					String fieldCode = editor.getFilter().getFilterMeta()
							.getFieldCode();
					if ("zb.djrq".equals(fieldCode)) {
						filters.add(editor.getFilter());
					}
				}
			} else {
				QueryTreeEditor queryTreeEditor = (QueryTreeEditor) criteriaEditor;
				filters = queryTreeEditor.getFiltersByFieldCode("zb.djrq");
			}
			for (IFilter filter : filters) {
				boolean isBetween = filter.getOperator().getOperatorCode()
						.equals(IOperatorConstants.BETWEEN);
				if (isBetween) {
					IFieldValue iFieldValue = filter.getFieldValue();
					if (iFieldValue == null) {
						return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("expensepub_0", "02011002-0043")/*
																			 * @res
																			 * "日期必须选定一个时间范围"
																			 */;
					}
					List<IFieldValueElement> values = iFieldValue
							.getFieldValues();
					if (values == null || values.size() != 2
							|| values.get(0) == null || values.get(1) == null) {
						return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("expensepub_0", "02011002-0043")/*
																			 * @res
																			 * "日期必须选定一个时间范围"
																			 */;
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param refPane
	 * @return
	 */
	protected void hiddenRefPaneMultiCorp(UIRefPane refPane) {
		// 隐藏多公司选择框
		refPane.setMultiCorpRef(false);
	}

	/**
	 * 借款单据录入节点
	 * 
	 * @param nodecode
	 * @return
	 */
	private boolean isJKNode(String nodecode) {
		return BXConstans.BXCLFJK_CODE.equals(nodecode)
				|| BXConstans.BXMELB_CODE.equals(nodecode);
	}

	/**
	 * 报销单据录入节点
	 * 
	 * @param nodecode
	 * @return
	 */
	private boolean isBxNode(String nodecode) {
		return BXConstans.BXCLFBX_CODE.equals(nodecode)
				|| BXConstans.BXTEA_CODE.equals(nodecode)
				|| BXConstans.BXCEA_CODE.equals(nodecode)
				|| BXConstans.BXPEA_CODE.equals(nodecode)
				|| BXConstans.BXEEA_CODE.equals(nodecode)
				|| BXConstans.BXMEA_CODE.equals(nodecode)
				|| BXConstans.BXRB_CODE.equals(nodecode);
	}

	/**
	 * 返回参照 显示封存数据按钮
	 * 
	 * @author chendya
	 * @param ref
	 * @param meta
	 * @return
	 */
	protected RefElementEditor makeRefElementEditor(UIRefPane ref,
			FilterMeta meta) {
		// 显示数据封存按钮
		ref.setDisabledDataButtonShow(true);
		return new RefElementEditor(ref, meta.getReturnType());
	}

	protected String getPkOrg() {
		return BXUiUtil.getBXDefaultOrgUnit();
	}

	public static void registerCriteriaEditorListener(BxQueryDLG bxQueryDLG, final List<String> orgRelKeyList, final List<String> fyOrgRelKeyList, final List<String> userOrgRelKeyList){
		//编辑后过滤
		bxQueryDLG.registerCriteriaEditorListener(new ICriteriaChangedListener() {
			@Override
			public void criteriaChanged(CriteriaChangedEvent evt) {
				if(evt.getEventtype()==CriteriaChangedEvent.FILTEREDITOR_INITIALIZED){
					initQueryArea(evt);
				}
				if (JKBXHeaderVO.PK_ORG.equals(evt.getFieldCode())) {
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
					BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.PK_ORG,null);
				}else if (fyOrgRelKeyList.contains(evt.getFieldCode())) {
					//编辑费用承担单位相关字段时，相关字段设置组织为费用承担单位
					BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.FYDWBM,null);
				}else if (userOrgRelKeyList.contains(evt.getFieldCode())) {
					//编辑报销人单位相关字段时，相关字段设置组织为报销人单位
					BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.DWBM,null);
				}
			}

			private void initQueryArea(CriteriaChangedEvent evt) {
				ICriteriaEditor criteriaEditor = evt.getCriteriaEditor();
				if (criteriaEditor instanceof nc.ui.queryarea.quick.QuickQueryArea) {
					nc.ui.queryarea.quick.QuickQueryArea simpleEditor = (nc.ui.queryarea.quick.QuickQueryArea) criteriaEditor;
					for (IFilterEditor filterEditor : simpleEditor.getFilterEditors()) {
						if (filterEditor instanceof DefaultFilterEditor) {
							DefaultFilterEditor filter=(DefaultFilterEditor) filterEditor;
							String fieldCode = filter.getFilter().getFilterMeta().getFieldCode();
							if(fieldCode.equals(BXHeaderVO.PK_ORG)){
//								DefaultFieldValueEditor editor=(DefaultFieldValueEditor) filter.getFieldValueEditor();
//								IFieldValueElementEditor leftValueElemEditor = editor.getFieldValueElemEditor();
//								UIRefPane refPane=null;
//								if (leftValueElemEditor instanceof CompositeRefElementEditor) {
//									CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) leftValueElemEditor;
//									CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
//									refPane = compositeRefPanel.getStdRefPane();
//								} else if (leftValueElemEditor instanceof RefElementEditor) {
//									RefElementEditor refElementEditor = (RefElementEditor) leftValueElemEditor;
//									refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
//								}
//								if (refPane != null && refPane.getRefModel() != null) {
//									String bxDefaultOrgUnit = BXUiUtil.getBXDefaultOrgUnit();
//									if(bxDefaultOrgUnit!=null){
//										refPane.setPKs(new String[]{bxDefaultOrgUnit});
//										
//										DefaultFieldValue fieldValue=new DefaultFieldValue();
//										RefValueObject o = new RefValueObject();
//										o.setPk(bxDefaultOrgUnit);
//										o.setCode(refPane.getRefCode());
//										o.setName(refPane.getRefName());
//										o.setMultiLang(refPane.getRefModel().isMutilLangNameRef());
//										
//										fieldValue.add(new DefaultFieldValueElement(refPane.getRefName(),bxDefaultOrgUnit,o));
//										filter.getFilter().setFieldValue(fieldValue);
//									}
//								}
							}else if(fieldCode.equals(BXHeaderVO.DJRQ)){
//								DefaultFieldValueEditor editor=(DefaultFieldValueEditor) filter.getFieldValueEditor();
//								IFieldValueElementEditor leftValueElemEditor = editor.getFieldValueElemEditor();
//								UIRefPane refPane=null;
//								if (leftValueElemEditor instanceof CompositeRefElementEditor) {
//									CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) leftValueElemEditor;
//									CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
//									refPane = compositeRefPanel.getStdRefPane();
//								} else if (leftValueElemEditor instanceof RefElementEditor) {
//									RefElementEditor refElementEditor = (RefElementEditor) leftValueElemEditor;
//									refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
//								}
//								if (refPane != null) {
//									UFDate busiDate = BXUiUtil.getBusiDate();
//									refPane.setValueObj(busiDate);
//									DefaultFieldValue fieldValue=new DefaultFieldValue();
//									fieldValue.add(new DefaultFieldValueElement(busiDate.toLocalString(),busiDate.toLocalString(),busiDate));
//									filter.getFilter().setFieldValue(fieldValue);
//								}
							}
						}
					}
				}
			}
		});
	}
	
}