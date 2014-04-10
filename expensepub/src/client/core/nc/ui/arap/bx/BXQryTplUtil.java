package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.pubitf.uapbd.ICustsupPubService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.ICriteriaEditor;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.simpleeditor.SimpleEditor;
import nc.ui.querytemplate.value.DefaultFieldValue;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.IFieldValue;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditor;
import nc.ui.querytemplate.valueeditor.RefElementEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefElementEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefPanel;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 报销管理查询模版工具类
 * 
 * @author chendya
 * 
 */

/**
 * 控制编辑器
 * 
 */
interface FieldValueElemEditorComponentProcessor {

	void processComponent(JComponent component) throws BusinessException;

	boolean isLeft();
}

/**
 * 参照模型处理
 * 
 * @author taorz1
 * 
 */
abstract class RefModelProcessor implements
		FieldValueElemEditorComponentProcessor {
	public void processComponent(JComponent component) throws BusinessException {
		if (component != null && component instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) component;
			processRefModel(refPane);
		}
	}

	public abstract void processRefModel(UIRefPane refPane)
			throws BusinessException;
}

/**
 * 参照设置组织处理
 *
 */
class RefModelSetDefaultOrgProcessor extends RefModelProcessor{
	private String pk_org = null;
	private boolean isLeft = true;
	
	RefModelSetDefaultOrgProcessor(String pk_org){
		this.pk_org = pk_org;
	}

	RefModelSetDefaultOrgProcessor(String pk_org,boolean isLeft){
		this.pk_org = pk_org;
		this.isLeft = isLeft;
	}

	@Override
	public boolean isLeft() {
		return isLeft;
	}

	@Override
	public void processRefModel(UIRefPane refPane) {
		AbstractRefModel model = refPane.getRefModel();
		if(model == null)
			return;
		model.setPk_org(this.pk_org);
	}
}

/**
 * 成本中心设置财务组织参照设置处理
 * 
 */
class CostCenterSetFiOrgRefMOdelProcesser extends RefModelProcessor {
	private String pk_org = null;
	private boolean isLeft = true;

	CostCenterSetFiOrgRefMOdelProcesser(String pk_org) {
		this.pk_org = pk_org;
	}

	CostCenterSetFiOrgRefMOdelProcesser(String pk_org, boolean isLeft) {
		this.pk_org = pk_org;
		this.isLeft = isLeft;
	}

	@Override
	public boolean isLeft() {
		return isLeft;
	}

	@Override
	public void processRefModel(UIRefPane refPane) {
		AbstractRefModel model = refPane.getRefModel();
		if (model == null)
			return;
		model.setPk_org(this.pk_org);
		model.setWherePart(" " + CostCenterVO.PK_FINANCEORG + "=" + "'"
				+ pk_org + "'");
	}
}

public class BXQryTplUtil {
	
	public static void registerCriteriaEditorListener(final BxQueryDLG bxQueryDLG, final List<String> orgRelKeyList, final List<String> fyOrgRelKeyList, final List<String> userOrgRelKeyList){
		//编辑后过滤
		bxQueryDLG.registerCriteriaEditorListener(new ICriteriaChangedListener() {
			@Override
			public void criteriaChanged(CriteriaChangedEvent evt) {
				if(evt.getEventtype()==CriteriaChangedEvent.FILTEREDITOR_INITIALIZED){
					initQueryArea(evt);
				}
				if(evt.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED){
					if(JKBXHeaderVO.HBBM.equals(evt.getFieldCode())){
						//编辑供应商
						HBBMFieldCriteriaChanged(evt,JKBXHeaderVO.CUSTACCOUNT);
					}
				}
				if(evt.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED){
					if(JKBXHeaderVO.CUSTACCOUNT.equals(evt.getFieldCode())){
						//编辑供应商
						HBBMFieldCriteriaInit(evt,bxQueryDLG.getFiltersByFieldCode(JKBXHeaderVO.HBBM),JKBXHeaderVO.CUSTACCOUNT);
					}
				}
				if (JKBXHeaderVO.PK_ORG.equals(evt.getFieldCode())) {
					//编辑报销单位时，其它与此组织相关的查询条件根据此过滤
					orgCriteriaChanged(evt, orgRelKeyList);
				}else if(JKBXHeaderVO.FYDWBM.equals(evt.getFieldCode())){
					//编辑费用承担单位时，其它与此组织相关的查询条件根据此过滤
					orgCriteriaChanged(evt, fyOrgRelKeyList);
				}else if(JKBXHeaderVO.DWBM.equals(evt.getFieldCode())){
					//编辑借款报销人单位时，其它与此组织相关的查询条件根据此过滤
					orgCriteriaChanged(evt, userOrgRelKeyList);
				}else if (orgRelKeyList.contains(evt.getFieldCode())) {
					//编辑报销单位相关字段时，相关字段设置组织为借款报销单位
					orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.PK_ORG,null);
				}else if (fyOrgRelKeyList.contains(evt.getFieldCode())) {
					//编辑费用承担单位相关字段时，相关字段设置组织为费用承担单位
					orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.FYDWBM,null);
				}else if (userOrgRelKeyList.contains(evt.getFieldCode())) {
					//编辑报销人单位相关字段时，相关字段设置组织为报销人单位
					orgRelFieldCriteriaChanged(evt,JKBXHeaderVO.DWBM,null);
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
								DefaultFieldValueEditor editor=(DefaultFieldValueEditor) filter.getFieldValueEditor();
								IFieldValueElementEditor leftValueElemEditor = editor.getFieldValueElemEditor();
								UIRefPane refPane=null;
								if (leftValueElemEditor instanceof CompositeRefElementEditor) {
									CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) leftValueElemEditor;
									CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
									refPane = compositeRefPanel.getStdRefPane();
								} else if (leftValueElemEditor instanceof RefElementEditor) {
									RefElementEditor refElementEditor = (RefElementEditor) leftValueElemEditor;
									refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
								}
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
								DefaultFieldValueEditor editor=(DefaultFieldValueEditor) filter.getFieldValueEditor();
								IFieldValueElementEditor leftValueElemEditor = editor.getFieldValueElemEditor();
								UIRefPane refPane=null;
								if (leftValueElemEditor instanceof CompositeRefElementEditor) {
									CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) leftValueElemEditor;
									CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor.getFieldValueElemEditorComponent();
									refPane = compositeRefPanel.getStdRefPane();
								} else if (leftValueElemEditor instanceof RefElementEditor) {
									RefElementEditor refElementEditor = (RefElementEditor) leftValueElemEditor;
									refPane = (UIRefPane) refElementEditor.getFieldValueElemEditorComponent();
								}
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
	
	/**
	 * 返回查询条件值
	 * @param filter
	 * @return
	 */
	private static String[] getFilterValues(IFilter filter) {
		IFieldValue fieldValue = filter.getFieldValue();
		if (fieldValue == null) {
			return new String[0];
		}
		List<IFieldValueElement> fieldValueElement = fieldValue.getFieldValues();
		if (fieldValueElement == null || fieldValueElement.size() == 0) {
			return new String[0];
		}
		String[] fieldValues = new String[fieldValueElement.size()];
		for (int i = 0; i < fieldValues.length; i++) {
			fieldValues[i] = fieldValueElement.get(i).getSqlString();
		}
		return fieldValues;
	}

	
	/**
	 * 查询条件相关组织切换后过滤 
	 * @author chendya
	 * @param evt
	 * @param orgRelKeyList
	 */
	public static void orgCriteriaChanged(CriteriaChangedEvent evt,List<String> orgRelKeyList){
		try{
			if (null != BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt)
					&& BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt).length > 0) {
				final String pk_org  = BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt)[0];
				for (String key : orgRelKeyList) {
					BXQryTplUtil.setOrgForField(evt.getCriteriaEditor(), key, pk_org);
					if(!JKBXHeaderVO.PK_RESACOSTCENTER.equals(key)){
						continue;
					}
					BXQryTplUtil.setOrgAndFiOrgForCostCenterField(evt.getCriteriaEditor(),JKBXHeaderVO.PK_RESACOSTCENTER, pk_org);
				}
			}
		} catch (BusinessException e) {
			Log.getInstance("BXQryTplUtil").error(e.getMessage());
		}
	}
	public static void HBBMFieldCriteriaChanged(CriteriaChangedEvent evt,final String relationField){	
		
		ICriteriaEditor currEditor = evt.getCriteriaEditor();
		if(null == currEditor ){
			return;
		}
		if (null != BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt)
				&& BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt).length > 0) {
			String[] filterNames = getFieldValuesFromCriteriaChangedEvent(evt);
			DefaultFilterEditor iFilterEditor2 = (DefaultFilterEditor) ((SimpleEditor) currEditor).getFilterEditorsByCode(relationField).get(0);
			DefaultFieldValueEditor fieldValueEditor2 = (DefaultFieldValueEditor) iFilterEditor2.getFieldValueEditor();
			UIRefPane ref2 = (UIRefPane) fieldValueEditor2.getFieldValueEditorComponent().getComponent(0);
			((CustBankaccDefaultRefModel) ref2.getRefModel()).setPk_cust(filterNames[0]);
		}
	}
	public static void HBBMFieldCriteriaInit(CriteriaChangedEvent evt,List<IFilter> list, final String relationField){	
		ICriteriaEditor currEditor = evt.getCriteriaEditor();
		if(null == currEditor ){
			return;
		}
		if(list!=null && list.size()!=0){
			String[] pkValues = getFilterValues(list.get(0));
			if (null != pkValues && pkValues.length > 0) {
				DefaultFilterEditor iFilterEditor2 = (DefaultFilterEditor) ((SimpleEditor) currEditor).getFilterEditorsByCode(relationField).get(0);
				DefaultFieldValueEditor fieldValueEditor2 = (DefaultFieldValueEditor) iFilterEditor2.getFieldValueEditor();
				UIRefPane ref2 = (UIRefPane) fieldValueEditor2.getFieldValueEditorComponent().getComponent(0);
				((CustBankaccDefaultRefModel) ref2.getRefModel()).setPk_cust(pkValues[0]);
			}
		}
	}
	/**
	 * 借款报销单位组织相关字段编辑事件
	 * @param evt
	 * @param orgValues 
	 */
	public static void orgRelFieldCriteriaChanged(CriteriaChangedEvent evt,final String vOrgField, String[] orgValues){
		ICriteriaEditor currEditor = evt.getCriteriaEditor();
		if (currEditor instanceof SimpleEditor) {
			SimpleEditor editor = (SimpleEditor) currEditor;
			List<IFilterEditor> filterEditors = editor.getFilterEditors();
			for(IFilterEditor filterEditor  : filterEditors){
				IFilter filter = filterEditor.getFilter();
				String[] pkValues = getFilterValues(filter);
				String pkValue = null;
				if (pkValues != null && pkValues.length>0) {
					pkValue = pkValues[0];
				}
				if(StringUtil.isEmpty(pkValue)){
					if (orgValues != null && orgValues.length>0) {
						pkValue = orgValues[0];
					}
				}
				if(StringUtil.isEmpty(pkValue)){
					continue;
				}
				if (vOrgField.equals(filter.getFilterMeta().getFieldCode())) {
					try {
						final String pk_org = pkValue;
						BXQryTplUtil.setOrgForField(evt.getCriteriaEditor(),evt.getFieldCode(), pk_org);
						//如果编辑字段为成本中心,则设置成本中心的所属财务组织为费用承担单位 
						if(JKBXHeaderVO.PK_RESACOSTCENTER.equals(evt.getFieldCode()) && JKBXHeaderVO.FYDWBM.equals(vOrgField)){
							BXQryTplUtil.setOrgAndFiOrgForCostCenterField(evt.getCriteriaEditor(), evt.getFieldCode(), pk_org);
						}
					} catch (BusinessException e) {
						Log.getInstance("BXQryTplUtil").error(e.getMessage());
					}
				}
			}
		}
	}
	

	/**
	 * 设置参照模型过滤字段
	 * 
	 * @param criteriaEditor
	 * @param relationField
	 * @param filterNames
	 */
	public static void setRefModelFilterRefNodeName(
			ICriteriaEditor criteriaEditor, String relationField,
			final String[] filterNames) {
		if (criteriaEditor == null || StringUtils.isEmpty(relationField))
			return;
		List<IFilter> filters = criteriaEditor
				.getFiltersByFieldCode(relationField);
		if (CollectionUtils.isEmpty(filters))
			return;
		if (criteriaEditor instanceof SimpleEditor) {
			SimpleEditor simpleEditor = (SimpleEditor) criteriaEditor;
			List<IFilterEditor> filterEditors = simpleEditor
					.getFilterEditorsByCode(relationField);
			if (CollectionUtils.isEmpty(filterEditors))
				return;
			for (IFilterEditor filterEditor : filterEditors) {
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor == null)
						continue;
					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor
								.getFieldValueEditor();
						if (defaultFieldValueEditor == null)
							continue;
						IFieldValueElementEditor leftValueElemEditor = defaultFieldValueEditor
								.getFieldValueElemEditor();
						if (leftValueElemEditor == null)
							continue;
						UIRefPane refPane = null;
						if (leftValueElemEditor instanceof CompositeRefElementEditor) {
							CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) leftValueElemEditor;
							CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor
									.getFieldValueElemEditorComponent();
							refPane = compositeRefPanel.getCurrentRefPane();
						} else if (leftValueElemEditor instanceof RefElementEditor) {
							RefElementEditor refElementEditor = (RefElementEditor) leftValueElemEditor;
							refPane = (UIRefPane) refElementEditor
									.getFieldValueElemEditorComponent();
						}
						if (refPane != null && refPane.getRefModel() != null) {
							refPane.getRefModel().setFilterRefNodeName(
									filterNames);
							refPane.getRefModel().reset();
						}
					}
				}
			}
		}
	}
	
	public static UIRefPane[] getRefPaneByFieldCode(QueryConditionDLG dlg,String[] fieldcodes){
		List<IFilterEditor> simpleEditorFilterEditors = dlg.getSimpleEditorFilterEditors();
		List<UIRefPane> list = new ArrayList<UIRefPane>();
		for(String fieldcode : fieldcodes){
			for(IFilterEditor editor: simpleEditorFilterEditors){
				DefaultFilterEditor filterEditor = (DefaultFilterEditor) editor;
				if(fieldcode.equals(filterEditor.getFilterMeta().getFieldCode())){
					IFieldValueEditor fieldValueEditor = filterEditor.getFieldValueEditor();
					if(fieldValueEditor instanceof DefaultFieldValueEditor){
						DefaultFieldValueEditor defaultFieldValueEditor  = (DefaultFieldValueEditor) fieldValueEditor;
						IFieldValueElementEditor fieldValueElemEditor = defaultFieldValueEditor.getFieldValueElemEditor();
						JComponent component = fieldValueElemEditor.getFieldValueElemEditorComponent();
						UIRefPane refPane = null;
						if(component instanceof CompositeRefPanel){
							refPane = ((CompositeRefPanel) component).getStdRefPane();
						}else if(component instanceof UIRefPane){
							refPane =  (UIRefPane) component;
						}
						list.add(refPane);
						break;
					}
				}
			}
		}
		return (UIRefPane[])list.toArray(new UIRefPane[0]);
	}
	
	public static UIRefPane getRefPaneByFieldCode(QueryConditionDLG dlg,String fieldcode){
		List<IFilterEditor> simpleEditorFilterEditors = dlg.getSimpleEditorFilterEditors();
		for(IFilterEditor editor: simpleEditorFilterEditors){
			DefaultFilterEditor filterEditor = (DefaultFilterEditor) editor;
			if(fieldcode.equals(filterEditor.getFilterMeta().getFieldCode())){
				IFieldValueEditor fieldValueEditor = filterEditor.getFieldValueEditor();
				if(fieldValueEditor instanceof DefaultFieldValueEditor){
					DefaultFieldValueEditor defaultFieldValueEditor  = (DefaultFieldValueEditor) fieldValueEditor;
					IFieldValueElementEditor fieldValueElemEditor = defaultFieldValueEditor.getFieldValueElemEditor();
					JComponent component = fieldValueElemEditor.getFieldValueElemEditorComponent();
					if(component instanceof CompositeRefPanel){
						CompositeRefPanel compRefPanel = (CompositeRefPanel) component;
						return compRefPanel.getStdRefPane();
					}else if(component instanceof UIRefPane){
						return (UIRefPane) component;
					}
				}
			}
		}
		return null;
	}
	
	public static UIRefPane  getRefPaneByFieldCode(CriteriaChangedEvent evt,String fieldcode){
		ICriteriaEditor criteriaEditor = evt.getCriteriaEditor();
		if(criteriaEditor instanceof SimpleEditor){
			SimpleEditor simpleEditor = (SimpleEditor) criteriaEditor;
			List<IFilterEditor> filterEditors= simpleEditor.getFilterEditorsByCode(fieldcode);
			if(filterEditors==null||filterEditors.size()==0){
				return null;
			}
			for (IFilterEditor filterEditor : filterEditors) {
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor == null)
						continue;
					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor.getFieldValueEditor();
						IFieldValueElementEditor fieldValueElementEditor = defaultFieldValueEditor.getFieldValueElemEditor();
						if(fieldValueElementEditor==null){
							continue;
						}
						JComponent comp = fieldValueElementEditor.getFieldValueElemEditorComponent();
						if(comp instanceof JComponent){
							return (UIRefPane)comp;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 从事件中获取字段的值
	 * 
	 * @param event
	 * @param field
	 * @return
	 */
	public static String[] getFieldValuesFromCriteriaChangedEvent(
			CriteriaChangedEvent event) {
		String fieldCode = event.getFieldCode();
		if (StringUtils.isEmpty(fieldCode))
			return new String[0];
		IFilter filter = event.getFilter();
		if (filter == null)
			return new String[0];
		IFieldValue fieldValue = filter.getFieldValue();
		if (fieldValue == null)
			return new String[0];
		List<IFieldValueElement> fieldValueElement = fieldValue
				.getFieldValues();
		if (fieldValueElement == null || fieldValueElement.size() == 0) {
			return new String[0];
		}
		String[] fieldValues = new String[fieldValueElement.size()];
		for (int i = 0; i < fieldValues.length; i++) {
			fieldValues[i] = fieldValueElement.get(i) != null ? fieldValueElement
					.get(i).getSqlString()
					: null;
		}
		return fieldValues;
	}

	public static void setYbjeFieldValue(ICriteriaEditor criteriaEditor,String relationField,int digital){
		if (criteriaEditor == null || StringUtils.isEmpty(relationField))
			return;
		List<IFilter> filters = criteriaEditor.getFiltersByFieldCode(relationField);
		if (CollectionUtils.isEmpty(filters))
			return;
		if (criteriaEditor instanceof SimpleEditor) {
			SimpleEditor simpleEditor = (SimpleEditor) criteriaEditor;
			List<IFilterEditor> filterEditors = simpleEditor.getFilterEditorsByCode(relationField);
			if (CollectionUtils.isEmpty(filterEditors))
				return;
			for (IFilterEditor filterEditor : filterEditors) {
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor == null)
						continue;
					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor.getFieldValueEditor();
						if (defaultFieldValueEditor == null)
							continue;
						JComponent leftComp = defaultFieldValueEditor.getFieldValueElemEditor().getFieldValueElemEditorComponent();
						JComponent rightComp = defaultFieldValueEditor.getRightFieldValueElemEditor().getFieldValueElemEditorComponent();
						if(leftComp instanceof JTextField){
							JTextField field = (JTextField) leftComp;
							String value = field.getText();
							if(value == null|| value.length() == 0 || value.indexOf(".") == -1){
								continue;
							}
							int len = value.substring(value.indexOf(".")).length();
							if(len<digital){
								//补0
								StringBuffer buf = new StringBuffer();
								int i = 0 ;
								while(i<(digital-len)){
									buf.append("0");
									i++;
								}
								field.setText(value+buf.toString());
							}else{
								//截断
								field.setText(value.substring(0,value.length()-(digital-len)));
							}
						}
						if(rightComp instanceof JTextField){
							JTextField field = (JTextField) rightComp;
							String value = field.getText();
							if(value == null|| value.length() == 0 || value.indexOf(".") == -1){
								continue;
							}
							int len = value.substring(value.indexOf(".")).length();
							if(len<digital){
								//补0
								StringBuffer buf = new StringBuffer();
								int i = 0 ;
								while(i<(digital-len)){
									buf.append("0");
									i++;
								}
								field.setText(value+buf.toString());
							}else{
								//截断
								field.setText(value.substring(0,value.length()-(digital-len)));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 配置参照
	 * 
	 * @param criteriaEditor
	 * @param relationField
	 * @param processor
	 * @throws BusinessException
	 */
	public static void configFieldValueElemEditor(
			ICriteriaEditor criteriaEditor, String relationField,
			FieldValueElemEditorComponentProcessor processor)
			throws BusinessException {
		if (criteriaEditor == null || StringUtils.isEmpty(relationField))
			return;
		List<IFilter> filters = criteriaEditor
				.getFiltersByFieldCode(relationField);
		if (CollectionUtils.isEmpty(filters))
			return;

		if (criteriaEditor instanceof SimpleEditor) {
			SimpleEditor simpleEditor = (SimpleEditor) criteriaEditor;

			List<IFilterEditor> filterEditors = simpleEditor
					.getFilterEditorsByCode(relationField);
			if (CollectionUtils.isEmpty(filterEditors))
				return;

			for (IFilterEditor filterEditor : filterEditors) {
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor == null)
						continue;

					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor
								.getFieldValueEditor();
						if (defaultFieldValueEditor == null)
							continue;

						if (processor.isLeft()) {
							IFieldValueElementEditor leftValueElemEditor = defaultFieldValueEditor
									.getFieldValueElemEditor();
							if (leftValueElemEditor == null)
								continue;
							if (leftValueElemEditor instanceof RefElementEditor) {
								JComponent leftFieldValueElemEditorComponent = leftValueElemEditor
										.getFieldValueElemEditorComponent();

								if (leftFieldValueElemEditorComponent != null) {
									processor
											.processComponent(leftFieldValueElemEditorComponent);
								}
							} else if (leftValueElemEditor instanceof CompositeRefElementEditor) {
								CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) leftValueElemEditor;
								CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor
										.getFieldValueElemEditorComponent();
								JComponent leftFieldValueElemEditorComponent = compositeRefPanel
										.getCurrentRefPane();
								if (leftFieldValueElemEditorComponent != null) {
									processor
											.processComponent(leftFieldValueElemEditorComponent);
								}
							}
						} else {
							IFieldValueElementEditor rightFieldValueElemEditor = defaultFieldValueEditor
									.getRightFieldValueElemEditor();
							if (rightFieldValueElemEditor == null)
								continue;
							if (rightFieldValueElemEditor instanceof RefElementEditor) {
								JComponent rightFieldValueElemEditorComponent = rightFieldValueElemEditor
										.getFieldValueElemEditorComponent();

								if (rightFieldValueElemEditorComponent != null) {
									processor
											.processComponent(rightFieldValueElemEditorComponent);
								}
							} else if (rightFieldValueElemEditor instanceof CompositeRefElementEditor) {
								CompositeRefElementEditor compositeRefElementEditor = (CompositeRefElementEditor) rightFieldValueElemEditor;
								CompositeRefPanel compositeRefPanel = (CompositeRefPanel) compositeRefElementEditor
										.getFieldValueElemEditorComponent();
								JComponent leftFieldValueElemEditorComponent = compositeRefPanel
										.getCurrentRefPane();
								if (leftFieldValueElemEditorComponent != null) {
									processor
											.processComponent(leftFieldValueElemEditorComponent);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 为参照设置组织值
	 *
	 * @param criteriaEditor
	 * @param relationField
	 * @param newWherePart
	 * @param isAddWherePart
	 * @param isLeft
	 */
	public static void setOrgForField(ICriteriaEditor criteriaEditor,String relationField,String pk_org) throws BusinessException{
		FieldValueElemEditorComponentProcessor processor = new RefModelSetDefaultOrgProcessor(pk_org);
		configFieldValueElemEditor(criteriaEditor, relationField, processor);
	}
	
	/**
	 * 为参照设置组织值
	 * 
	 * @param criteriaEditor
	 * @param relationField
	 * @param newWherePart
	 * @param isAddWherePart
	 * @param isLeft
	 */
	public static void setOrgAndFiOrgForCostCenterField(
			ICriteriaEditor criteriaEditor, String relationField, String pk_org)
			throws BusinessException {
		FieldValueElemEditorComponentProcessor processor = new CostCenterSetFiOrgRefMOdelProcesser(
				pk_org);

		configFieldValueElemEditor(criteriaEditor, relationField, processor);
	}

	/**
	 * 设置原币金额字段精度
	 */
	public static void setYbjeFieldDigital(ICriteriaEditor criteriaEditor,
			String relationField, int digital) {
		configFieldValueElemEditor(criteriaEditor, relationField, digital);
	}

	/**
	 * 配置参照
	 * 
	 * @param criteriaEditor
	 * @param relationField
	 * @param processor
	 * @throws BusinessException
	 */
	public static void configFieldValueElemEditor(
			ICriteriaEditor criteriaEditor, String relationField, int digital) {
		if (criteriaEditor == null || StringUtils.isEmpty(relationField))
			return;
		List<IFilter> filters = criteriaEditor
				.getFiltersByFieldCode(relationField);
		if (CollectionUtils.isEmpty(filters))
			return;

		if (criteriaEditor instanceof SimpleEditor) {
			SimpleEditor simpleEditor = (SimpleEditor) criteriaEditor;

			List<IFilterEditor> filterEditors = simpleEditor
					.getFilterEditorsByCode(relationField);
			if (CollectionUtils.isEmpty(filterEditors))
				return;

			for (IFilterEditor filterEditor : filterEditors) {
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor == null)
						continue;

					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor
								.getFieldValueEditor();
						if (defaultFieldValueEditor == null)
							continue;
						JComponent leftComp = defaultFieldValueEditor
								.getFieldValueElemEditor()
								.getFieldValueElemEditorComponent();
						JComponent rightComp = defaultFieldValueEditor
								.getRightFieldValueElemEditor()
								.getFieldValueElemEditorComponent();
					}
				}
			}
		}
	}
}
