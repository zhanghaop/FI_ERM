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
 *         ������ѯ�Ի����²�ѯģ��ʵ�֣�
 * 
 *         nc.ui.arap.bx.BxQueryDLG
 */
@SuppressWarnings({ "serial" })
public class BxQueryDLG extends QueryConditionDLG {

//	private BXBillMainPanel parent;

	private String djlxbm;
	
	//������󾫶�(Ĭ��2λ)
	private int maxCurrDigital = 2;
	
	public String getSTR_JK() {
	    return  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0041")/* @res* "���"*/;
	}

	public String getSTR_BX() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0042")/* @res* "����"*/;
	}
	
	/** ֻ�б������Ŵ�����ֶ� */
	final List<String> onlyBxFieldsList = Arrays.asList(new String[]{ "zb.zfybje", "zb.zfbbje","zb.hkybje", "zb.hkbbje" });

	/** ֻ�н��Ŵ�����ֶ� */
	final List<String> onlyJkFieldsList = Arrays.asList(new String[] { "zb.qzzt", "zb.zhrq","zb.zpxe" });

	/**����ԭ�ҽ���ֶ�*/
	final List<String> ybjeFieldsList = Arrays.asList(JKBXHeaderVO.getYbjeField());
	
	//��֯��������ֶ�
	private List<String> orgRelKeyList;
	private List<String> fyOrgRelKeyList;
	private List<String> userOrgRelKeyList;
	
	/**
	 * ������ѯ�Ի���
	 * @author chendya
	 * @param parent
	 * @param normalQueryPanel
	 * @param tplInfo
	 * @param title
	 * @param nodecode �ڵ��
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
			//���õ��ݳ�ʼ��
			init4InitBillQryDlg(parent);
		} else {
			initQryDlg(parent, oldnodecode);
		}
		//���¼��ز�ѯ����
		try {
			initUIData();
		} catch (Exception e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		// added by chendya ���ϲ�ѯģ���Դ�������Ȩ�ޣ�ƴ��sqlʱ�Լ�д;
		// �����ѯʱ����ѯģ��Ĭ�Ϸ��صı�ͷ����szxmid��Ӧ������Ȩ�޳��� ��������ȷ(��֪��������Ļ����ӱ��)
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
	 * ��ʼ�����õ��ݲ�ѯ�Ի���
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
	 * �޸Ĳ�ѯ����
	 * 
	 * @param conds
	 * @param nodecode
	 */
	private void initQryCondition(QueryConditionVO[] conds, String nodecode) {
		for (int i = 0; i < conds.length; i++) {
			String fieldCode = conds[i].getFieldCode();
			// ���ö�Ӧ��Ĭ��ֵ��������
			if ((JKBXHeaderVO.PK_GROUP).equals(fieldCode)) {
				conds[i].setIfDefault(UFBoolean.TRUE);
				conds[i].setIfImmobility(UFBoolean.TRUE);
				conds[i].setValue(BXUiUtil.getPK_group());
				conds[i].setIsCondition(UFBoolean.FALSE);
			}else if ((JKBXHeaderVO.PK_ORG).equals(fieldCode)) {
				
				//���ģ��������ȡģ���ϵ� 
				if(conds[i].getValue()!=null){
					continue;
				}
				
				// ��ø��Ի���Ĭ������֯<1>
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
								.getStrByID("expensepub_0" , "02011002-0001")/* @res"����֯ģ����������Ϊ��"*/);
					}else if (BXConstans.BXLR_QCCODE.equals(nodecode)) {
						// �ڳ����ݵ������ڲ���<=ĳ���������ڿ���
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
				// �����Ľ����ڵ㣬����Ĭ�ϵĵ�������ֵ�����ֶ�ֵΪĬ�������������޸ġ�
				conds[i].setIfImmobility(UFBoolean.TRUE);
				conds[i].setIfUsed(UFBoolean.FALSE);
			} else if (BXQueryUtil.PZZT.equals(fieldCode)) { 
				// ƾ֤״̬
				conds[i].setIsCondition(UFBoolean.FALSE);
			} else if (BXQueryUtil.XSPZ.equals(fieldCode)) { 
				// �Ƿ���ʾƾ֤
				conds[i].setIsCondition(UFBoolean.FALSE);
			} else if (BXQueryUtil.APPEND.equals(fieldCode)) { 
				// �Ƿ�׷����ʾ���
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
	 * �����ڳ����ݳ�ʼ������
	 * @return
	 */
	private static UFDate getQcInitDate(){
		String pk_org = null;
		UFDate startDate = null;
		// ��ø��Ի���Ĭ������֯<1>
		if (WorkbenchEnvironment.getInstance().getClientCache(QcDateCall.QcDate_Org_PK_ + BXUiUtil.getPK_group()) != null) {
			pk_org = (String) WorkbenchEnvironment.getInstance().getClientCache(QcDateCall.QcDate_Org_PK_+ BXUiUtil.getPK_group());
			startDate = (UFDate) WorkbenchEnvironment.getInstance().getClientCache(QcDateCall.QcDate_Date_PK_ + pk_org);
		} else {
			final String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();

			if (defaultOrg != null && defaultOrg.length() > 0) {
				pk_org = defaultOrg;
			} else {
				// ȡ��ǰ��¼��������֯
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
	 * ��ʼ����ѯ�Ի�����Ӳ��չ��ˣ��޸Ĳ�ѯ������
	 * 
	 * @author chendya
	 * @param parent
	 * @param nodecode
	 */
	private void initQryDlg(Container parent, final String nodecode) {

		// �޸Ĳ�ѯ����
		registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
			public void processQueryTempletTotalVO(QueryTempletTotalVO totalVO) {
				QueryConditionVO[] conds = totalVO.getConditionVOs();
				if (conds == null || conds.length == 0) {
					return;
				}
				// �޸Ĳ�ѯ����
				initQryCondition(conds, nodecode);
			}
		});
		
		//����DefaultFieldValueElementEditor���չ���
		DefaultFieldValueElementEditorFactory factory = new DefaultFieldValueElementEditorFactory(getQueryContext()){
			@Override
			public IFieldValueElementEditor createFieldValueElementEditor(FilterMeta meta) {
				IFieldValueElementEditor fieldValueElementEditor = super.createFieldValueElementEditor(meta);
				if(JKBXHeaderVO.PK_ORG.equals(meta.getFieldCode())){
					UIRefPane refPane = null;
					if (meta.isSysFuncRefUsed()&& fieldValueElementEditor instanceof CompositeRefElementEditor) {
						//1.��ϵͳ�������
						CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) fieldValueElementEditor;
						CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
						refPane = compositeRefPanel.getCurrentRefPane();
					}else{
						//2.�޸Ĳ�ѯģ�棬ȥ��ϵͳ���������
						RefElementEditor refElementEditor = (RefElementEditor) fieldValueElementEditor;
						refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
					}
					
					//���ؼ��ţ���ʾ����֯
					hiddenRefPaneMultiCorp(refPane);
					
					refPane.getRefModel().setFilterPks(BXUiUtil.getPermissionOrgs(nodecode),IFilterStrategy.INSECTION);
					
					// ������ճ���ͣ�õ���֯
					refPane.getRefModel().setDisabledDataShow(true);

					// ��������Ȩ�޿���
					refPane.getRefModel().setUseDataPower(false);
				}
				else if(JKBXHeaderVO.DJLXBM.equals(meta.getFieldCode())){
					UIRefPane refPane = null;
					if (meta.isSysFuncRefUsed()&& fieldValueElementEditor instanceof CompositeRefElementEditor) {
						//1.��ϵͳ�������
						CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) fieldValueElementEditor;
						CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
						refPane = compositeRefPanel.getCurrentRefPane();
					}else{
						//2.�޸Ĳ�ѯģ�棬ȥ��ϵͳ���������
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
						//�ڳ��ڵ�
						else if (BXConstans.BXLR_QCCODE.equals(nodecode)) {
							model.setWherePart(" pk_billtypecode like '263%' ");
						}
					}
				}
				return fieldValueElementEditor;
			}
		};
		
		//�޸Ĳ�ѯ�����༭��
		registerFieldValueEelementEditorFactory(factory);

		//�༭�����
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
																			 * "���ڱ���ѡ��һ��ʱ�䷶Χ"
																			 */;
					}
					List<IFieldValueElement> values = iFieldValue
							.getFieldValues();
					if (values == null || values.size() != 2
							|| values.get(0) == null || values.get(1) == null) {
						return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("expensepub_0", "02011002-0043")/*
																			 * @res
																			 * "���ڱ���ѡ��һ��ʱ�䷶Χ"
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
		// ���ض๫˾ѡ���
		refPane.setMultiCorpRef(false);
	}

	/**
	 * ����¼��ڵ�
	 * 
	 * @param nodecode
	 * @return
	 */
	private boolean isJKNode(String nodecode) {
		return BXConstans.BXCLFJK_CODE.equals(nodecode)
				|| BXConstans.BXMELB_CODE.equals(nodecode);
	}

	/**
	 * ��������¼��ڵ�
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
	 * ���ز��� ��ʾ������ݰ�ť
	 * 
	 * @author chendya
	 * @param ref
	 * @param meta
	 * @return
	 */
	protected RefElementEditor makeRefElementEditor(UIRefPane ref,
			FilterMeta meta) {
		// ��ʾ���ݷ�水ť
		ref.setDisabledDataButtonShow(true);
		return new RefElementEditor(ref, meta.getReturnType());
	}

	protected String getPkOrg() {
		return BXUiUtil.getBXDefaultOrgUnit();
	}

	public static void registerCriteriaEditorListener(BxQueryDLG bxQueryDLG, final List<String> orgRelKeyList, final List<String> fyOrgRelKeyList, final List<String> userOrgRelKeyList){
		//�༭�����
		bxQueryDLG.registerCriteriaEditorListener(new ICriteriaChangedListener() {
			@Override
			public void criteriaChanged(CriteriaChangedEvent evt) {
				if(evt.getEventtype()==CriteriaChangedEvent.FILTEREDITOR_INITIALIZED){
					initQueryArea(evt);
				}
				if (JKBXHeaderVO.PK_ORG.equals(evt.getFieldCode())) {
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
					BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.PK_ORG,null);
				}else if (fyOrgRelKeyList.contains(evt.getFieldCode())) {
					//�༭���óе���λ����ֶ�ʱ������ֶ�������֯Ϊ���óе���λ
					BXQryTplUtil.orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.FYDWBM,null);
				}else if (userOrgRelKeyList.contains(evt.getFieldCode())) {
					//�༭�����˵�λ����ֶ�ʱ������ֶ�������֯Ϊ�����˵�λ
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