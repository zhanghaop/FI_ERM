package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;

import nc.bs.erm.util.ErUtil;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.itf.fipub.report.IPubReportConstants;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefInitializeCondition;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaEditor;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.simpleeditor.SimpleEditor;
import nc.ui.querytemplate.value.IFieldValue;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditor;
import nc.ui.querytemplate.valueeditor.RefElementEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefElementEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefPanel;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
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
	@Override
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
	private String[] filterPKs;
	private boolean isLeft = true;
	
	RefModelSetDefaultOrgProcessor(String pk_org){
		this.pk_org = pk_org;
	}

	RefModelSetDefaultOrgProcessor(String pk_org,boolean isLeft){
		this.pk_org = pk_org;
		this.isLeft = isLeft;
	}

    RefModelSetDefaultOrgProcessor(String pk_org, String[] filterPKs){
        this.pk_org = pk_org;
        this.filterPKs = filterPKs;
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
		if (refPane.getRefModel() instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel ||
                refPane.getRefModel() instanceof nc.ui.org.ref.BusinessUnitDefaultRefModel ||
                refPane.getRefModel() instanceof nc.ui.org.ref.AdminOrgDefaultRefModel) {
            refPane.setMultiCorpRef(false);
        } else {
            refPane.setMultiCorpRef(true);
        }
        refPane.setMultiOrgSelected(true);
        refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
        if (filterPKs != null) {
            filterPKs = ErUtil.insertHeadOneOrg(pk_org, filterPKs);
            refPane.setMultiRefFilterPKs(filterPKs);
        }
		if (StringUtils.isNotBlank(pk_org)) {
            model.setPk_org(this.pk_org);
            if (refPane.isMultiCorpRef()) {
                RefInitializeCondition[] conditions = refPane.getRefUIConfig().getRefFilterInitconds();
                if (conditions != null) {
                    for (RefInitializeCondition condition : conditions) {
                        condition.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER); // 数据权限控制
                        if (filterPKs != null && filterPKs.length > 0) {
                            condition.setDefaultPk(filterPKs[0]);
                        }
                    }
                }
            }
		}
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
		if (!StringUtil.isEmpty(pk_org) && model instanceof CostCenterTreeRefModel) {
//		      model.setWherePart(" " + CostCenterVO.PK_PROFITCENTER + "=" + "'"
//		                + pk_org + "'");
		      CostCenterTreeRefModel costModel = (CostCenterTreeRefModel)model;
		      costModel.setCurrentOrgCreated(false);
		      costModel.setOrgType(CostCenterVO.PK_PROFITCENTER);
		}
	}
}

public class BXQryTplUtil {
    
	/**
	 * 返回查询条件值
	 * @param filter
	 * @return
	 */
	public static String[] getFilterValues(IFilter filter) {
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
            if (fieldValueElement.get(i) != null)
                fieldValues[i] = fieldValueElement.get(i).getSqlString();
		}
		return fieldValues;
	}

	private static final String BX_PK_RESACOSTCENTER = "pk_resacostcenter";
	private static final String BX_FYDWBM = "fydwbm";
	
	/**
	 * 查询条件相关组织切换后过滤 
	 * @author chendya
	 * @param evt
	 * @param orgRelKeyList
	 */
	public static void orgCriteriaChanged(CriteriaChangedEvent evt,List<String> orgRelKeyList){
		try{
		    String pk_org = null;
		    String[] val = BXQryTplUtil.getFieldValuesFromCriteriaChangedEvent(evt);
			if (null != val && val.length > 0) {
			    pk_org = val[0];
			}
			UIRefPane refPanel = BXQryTplUtil.getRefPaneByFieldCode(evt, evt.getFieldCode());
            AbstractRefModel refModel = refPanel.getRefModel();
			Object[] objs = refModel.getValues(refModel.getPkFieldCode(), refModel.getData());
            String[] objStr = null;
            if (objs != null && objs.length > 0) {
                objStr = new String[objs.length];
                for (int nPos = 0; nPos <objs.length; nPos++) {
                    objStr[nPos] = (String)objs[nPos];
                }                
            }
            for (String key : orgRelKeyList) {
                BXQryTplUtil.setOrgForField(evt.getCriteriaEditor(), key, pk_org, objStr);
                if(!BX_PK_RESACOSTCENTER.equals(key)){
                    continue;
                }
                BXQryTplUtil.setOrgAndFiOrgForCostCenterField(evt.getCriteriaEditor(),BX_PK_RESACOSTCENTER, pk_org);
            }
		} catch (BusinessException e) {
			Log.getInstance("BXQryTplUtil").error(e.getMessage());
		}
	}

    public static void orgCriteriaChanged(String pk_org, List<String> orgRelKeyList){
//        try{
            for (String key : orgRelKeyList) {
//                BXQryTplUtil.setOrgForField(evt.getCriteriaEditor(), key, pk_org);
                if(!BX_PK_RESACOSTCENTER.equals(key)){
                    continue;
                }
//                BXQryTplUtil.setOrgAndFiOrgForCostCenterField(evt.getCriteriaEditor(),BX_PK_RESACOSTCENTER, pk_org);
            }
//        } catch (BusinessException e) {
//            Log.getInstance("BXQryTplUtil").error(e.getMessage());
//        }
    }
    
	/**
	 * 借款报销单位组织相关字段编辑事件
	 * @param evt
	 * @param orgValues 
	 */
	public static void orgRelFieldCriteriaChanged(CriteriaChangedEvent evt,final String vOrgField, String[] orgValues){
		try {
            String pk_org = null;
            if (orgValues != null && orgValues.length > 0) {
                pk_org = orgValues[0];
            }
            
            UIRefPane refPanel = BXQryTplUtil.getRefPaneByFieldCode(evt, evt.getFieldCode());
            AbstractRefModel refModel = refPanel.getRefModel();
            Object[] objs = refModel.getValues(refModel.getPkFieldCode(), refModel.getData());
            String[] objStr = null;
            if (objs != null && objs.length > 0) {
                objStr = new String[objs.length];
                for (int nPos = 0; nPos <objs.length; nPos++) {
                    objStr[nPos] = (String)objs[nPos];
                }                
            }
            
            try {
                BXQryTplUtil.setOrgForField(
                        evt.getCriteriaEditor(),
                        evt.getFieldCode(), pk_org, objStr);
                // 如果编辑字段为成本中心,则设置成本中心的所属财务组织为费用承担单位
                if (BX_PK_RESACOSTCENTER.equals(evt.getFieldCode())
                        && (BX_FYDWBM.equals(vOrgField) || "zb.fydwbm".equals(vOrgField) 
                                || "assume_org".equals(vOrgField) || "zb.assume_org".equals(vOrgField))) {
                    BXQryTplUtil.setOrgAndFiOrgForCostCenterField(
                            evt.getCriteriaEditor(),
                            evt.getFieldCode(), pk_org);
                }
            } catch (BusinessException e) {
                Log.getInstance("BXQryTplUtil").error(
                        e.getMessage());
            }
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
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
                if (filterEditor == null)
                    continue;
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
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
		return list.toArray(new UIRefPane[0]);
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
                if (filterEditor == null)
                    continue;
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor.getFieldValueEditor();
						IFieldValueElementEditor fieldValueElementEditor = defaultFieldValueEditor.getFieldValueElemEditor();
						if(fieldValueElementEditor==null){
							continue;
						}
						JComponent comp = fieldValueElementEditor.getFieldValueElemEditorComponent();
                        if (comp instanceof UIRefPane)
                            return (UIRefPane) comp;
                        else
                            return null;
					}
				}
			}
		}
		return null;
	}
	
	public static List<UIRefPane>  getRefPaneListByFieldCode(CriteriaChangedEvent evt,String fieldcode){
        ICriteriaEditor criteriaEditor = evt.getCriteriaEditor();
        List<UIRefPane> list = new LinkedList<UIRefPane>();
        if(criteriaEditor instanceof SimpleEditor){
            SimpleEditor simpleEditor = (SimpleEditor) criteriaEditor;
            List<IFilterEditor> filterEditors= simpleEditor.getFilterEditorsByCode(fieldcode);
            if(filterEditors==null||filterEditors.size()==0){
                return null;
            }
            for (IFilterEditor filterEditor : filterEditors) {
                if (filterEditor == null)
                    continue;
                if (filterEditor instanceof DefaultFilterEditor) {
                    DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
                    if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
                        DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor.getFieldValueEditor();
                        IFieldValueElementEditor fieldValueElementEditor = defaultFieldValueEditor.getFieldValueElemEditor();
                        if(fieldValueElementEditor==null){
                            continue;
                        }
                        JComponent comp = fieldValueElementEditor.getFieldValueElemEditorComponent();
                        if (comp instanceof UIRefPane)
                            list.add((UIRefPane) comp);
//                        else
//                            return null;
                    }
                }
            }
        }
        return list;
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
			fieldValues[i] = fieldValueElement.get(i) != null ? ((RefValueObject)fieldValueElement
					.get(i).getValueObject()).getPk()
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
                if (filterEditor == null)
                    continue;
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
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
                if (filterEditor == null)
                    continue;
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
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

    public static void setOrgForField(ICriteriaEditor criteriaEditor,String relationField,String pk_org, String[] filterOrgs) throws BusinessException{
        FieldValueElemEditorComponentProcessor processor = new RefModelSetDefaultOrgProcessor(pk_org, filterOrgs);
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
                if (filterEditor == null)
                    continue;
				if (filterEditor instanceof DefaultFilterEditor) {
					DefaultFilterEditor defaultFilterEditor = (DefaultFilterEditor) filterEditor;
					if (defaultFilterEditor.getFieldValueEditor() instanceof DefaultFieldValueEditor) {
						DefaultFieldValueEditor defaultFieldValueEditor = (DefaultFieldValueEditor) defaultFilterEditor
								.getFieldValueEditor();
						if (defaultFieldValueEditor == null)
							continue;
                        // JComponent leftComp = defaultFieldValueEditor
                        // .getFieldValueElemEditor()
                        // .getFieldValueElemEditorComponent();
                        // JComponent rightComp = defaultFieldValueEditor
                        // .getRightFieldValueElemEditor()
                        // .getFieldValueElemEditorComponent();
					}
				}
			}
		}
	}
}
