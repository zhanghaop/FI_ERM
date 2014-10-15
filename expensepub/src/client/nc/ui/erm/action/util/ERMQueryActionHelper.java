package nc.ui.erm.action.util;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.queryarea.quick.QuickQueryArea;
import nc.ui.queryarea.scheme.SingleQuerySchemePanel;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaEditor;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.simpleeditor.SimpleEditor;
import nc.ui.querytemplate.valueeditor.ref.CompositeRefPanel;

import org.apache.commons.lang.StringUtils;

public class ERMQueryActionHelper {
	/**
	 *  获取事件本事对应的控件
	 *
	 * @param event
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static JComponent getFiltComponentForInit(CriteriaChangedEvent event) {
		IFilterEditor filtereditor = event.getFiltereditor();
		FilterEditorWrapper wapper = new FilterEditorWrapper(filtereditor);
		JComponent component = wapper.getFieldValueElemEditorComponent();
		if (component instanceof CompositeRefPanel) {
			component = ((CompositeRefPanel) component).getStdRefPane();
		}
		return component;
	}

	/**
	 *  获取事件本事对应的控件(between 右侧控件)
	 *
	 * @param event
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static JComponent getFiltRightComponentForInit(CriteriaChangedEvent event) {
		IFilterEditor filtereditor = event.getFiltereditor();
		FilterEditorWrapper wapper = new FilterEditorWrapper(filtereditor);
		JComponent component = wapper.getFieldValueElemEditorComponentRight();
		if (component instanceof CompositeRefPanel) {
			component = ((CompositeRefPanel) component).getStdRefPane();
		}
		return component;
	}

	/**
	 * 获取List<IFilterEditor>
	 *
	 * @param event
	 * @param code
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static List<IFilterEditor> getIFilterEditorList(CriteriaChangedEvent event, String code) {
		ICriteriaEditor criteriaEditor = event.getCriteriaEditor();

		List<IFilterEditor> feList = null;
		if (criteriaEditor instanceof SimpleEditor) {
			feList = ((SimpleEditor)criteriaEditor).getFilterEditorsByCode(code);
		}else if (criteriaEditor instanceof QuickQueryArea) {
			feList = ((QuickQueryArea)criteriaEditor).getFilterEditorsByCode(code);
		}else if (criteriaEditor instanceof SingleQuerySchemePanel) {
			feList = ((SingleQuerySchemePanel)criteriaEditor).getFilterEditorsByCode(code);
		}
//		else if(criteriaEditor instanceof  QueryTreeEditor){
//			((DefaultFieldValueElement)(((QueryTreeEditor)criteriaEditor).getFiltersByFieldCode(code).get(0).getFieldValue())).getValueObject();
//		}
//		else{
//			Logger.error("不支持的查询面板格式---TXM");
//			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0015")/*@res "不支持的查询面板格式"*/);
//		}
		return feList;
	}

	/**
	 * 获取非事件本事对应的控件
	 *
	 * @param event
	 * @param code
	 * @param isRight   是否取between形式右侧的值
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static JComponent getFiltComponentForValueChanged(CriteriaChangedEvent event,String code,boolean isRight) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}

		return getComp(getIFilterEditorList(event, code),isRight);
	}
	
	/**
	 * 获取非事件本事对应的所有控件
	 * 有意义的场景 ：一个时间类型的，大于一个日期，另一个小于一个日期
	 * @param event
	 * @param code
	 * @param isRight   是否取between形式右侧的值
	 * @author: chenshuaia 查询模板中可能存在多个相同的查询
	 * @return
	 */
	public static JComponent[] getFiltComponentsForValueChanged(CriteriaChangedEvent event,String code,boolean isRight) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}

		return getComps(getIFilterEditorList(event, code),isRight);
	}
	
	/**
	 * 根据编码取得查询面板上的控件(仅取第一code相符的值component)
	 * 
	 * @param isRight
	 *            是否取between形式右侧的值
	 * @param feList
	 * @author: chenshuaia 查询模板中可能存在多个相同的查询
	 */
	public static JComponent[] getComps(List<IFilterEditor> feList, boolean isRight) {
		if (feList == null) {
			return null;
		}
		List<JComponent> result = new ArrayList<JComponent>();
		for (IFilterEditor ife : feList) {
			FilterEditorWrapper wapper = new FilterEditorWrapper(ife);
			if (!isRight) {
				JComponent component = wapper.getFieldValueElemEditorComponent();
				if (component instanceof CompositeRefPanel) {
					component = ((CompositeRefPanel) component).getStdRefPane();
				}
				result.add(component);
			} else {
				JComponent component = wapper.getFieldValueElemEditorComponentRight();
				if (component instanceof CompositeRefPanel) {
					component = ((CompositeRefPanel) component).getStdRefPane();
				}
				result.add(component);
			}
		}
		return result.toArray(new JComponent[] {});
	}

	/**
	 * 根据编码取得查询面板上的控件(仅取第一code相符的值component)
	 *
	 * @param isRight  是否取between形式右侧的值
	 * @param feList
	 * @author: wangyhh@ufida.com.cn
	 */
	public static JComponent getComp(List<IFilterEditor> feList,boolean isRight) {
		if (feList == null) {
			return null;
		}

		for (IFilterEditor ife : feList) {
			FilterEditorWrapper wapper = new FilterEditorWrapper(ife);
			if (!isRight) {
				JComponent component = wapper.getFieldValueElemEditorComponent();
				if (component instanceof CompositeRefPanel) {
					component = ((CompositeRefPanel) component).getStdRefPane();
				}
				return component;
			} else {
				JComponent component = wapper.getFieldValueElemEditorComponentRight();
				if (component instanceof CompositeRefPanel) {
					component = ((CompositeRefPanel) component).getStdRefPane();
				}
				return component;
			}
		}
		return null;
	}

	/**
	 * 相同code，多个查询条件情况
	 *
	 * 根据编码取得查询面板上的控件(取全部code相符的component)
	 * valueChanged事件专用FILTER_CHANGED
	 *
	 * @param event
	 * @param code
	 * @param isRight   是否取between形式右侧的值
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static List<JComponent> getFiltComponentForValueChangedAll(CriteriaChangedEvent event,String code,boolean isRight) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}

		return getCompAll(getIFilterEditorList(event, code),isRight);
	}

	/**
	 * 相同code，多个查询条件情况
	 *
	 * 根据编码取得查询面板上的控件(取全部code相符的component)
	 *
	 * @param isRight  是否取between形式右侧的值
	 * @param feList
	 * @author: wangyhh@ufida.com.cn
	 */
	public static List<JComponent> getCompAll(List<IFilterEditor> feList,boolean isRight) {
		if (feList == null) {
			return null;
		}

		List<JComponent> compList = new ArrayList<JComponent>();
		for (IFilterEditor ife : feList) {
			FilterEditorWrapper wapper = new FilterEditorWrapper(ife);
			if (!isRight) {
				JComponent component = wapper.getFieldValueElemEditorComponent();
				if (component instanceof CompositeRefPanel) {
					component = ((CompositeRefPanel) component).getStdRefPane();
				}
				compList.add(component);
			} else {
				JComponent component = wapper.getFieldValueElemEditorComponentRight();
				if (component instanceof CompositeRefPanel) {
					component = ((CompositeRefPanel) component).getStdRefPane();
				}
				compList.add(component);
			}
		}
		return compList;
	}

	/**
	 * 获取List<IFilterEditor>
	 *
	 * 无event情况
	 *
	 * @param editor
	 * @param itemKey
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static List<IFilterEditor> getFeList(Container editor,String itemKey) {
		List<IFilterEditor> filterEditorsByCode = null;
		if (editor instanceof SimpleEditor) {
			filterEditorsByCode = ((SimpleEditor)editor).getFilterEditorsByCode(itemKey);
		}else if (editor instanceof UIPanel) {
			Container quickEditor = editor.getParent().getParent().getParent().getParent();
			if (quickEditor instanceof QuickQueryArea) {
				filterEditorsByCode = ((QuickQueryArea)quickEditor).getFilterEditorsByCode(itemKey);
			}
		}
		return filterEditorsByCode;
	}

	/**
	 * 过滤主组织权限
	 *
	 * @param qryEditorComponent
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void filtOrgsForQueryAction(CriteriaChangedEvent event,String[] pkOrgs) {
		UIRefPane refPane = (UIRefPane) getFiltComponentForInit(event);
		refPane.setMultiCorpRef(false);
		filtOrgs(pkOrgs, refPane);
	}

	/**
	 * 过滤主组织权限
	 *
	 * @param pkOrgs
	 * @param refPane
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void filtOrgs(String[] pkOrgs, UIRefPane refPane) {
		if (pkOrgs == null) {
			// 没有分配主组织权限情况
			pkOrgs = new String[0];
		}
		refPane.getRefModel().setFilterPks(pkOrgs);
		refPane.getRefModel().setAddEnableStateWherePart(true);
	}

	/**
	 * 查询条件设值（参照类控件,默认左侧）
	 * valuechanged
	 *
	 * @param event
	 * @param itemKey
	 * @param refPk
	 * @param isRight  between形式
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void setPk(CriteriaChangedEvent event,String itemKey,String refPk,boolean isRight) {
		ICriteriaEditor criteriaEditor = event.getCriteriaEditor();
		List<IFilterEditor> editors = null;
		if (criteriaEditor instanceof SimpleEditor) {
			editors = ((SimpleEditor) criteriaEditor).getFilterEditors();
		} else if (criteriaEditor instanceof QuickQueryArea) {
			editors = ((QuickQueryArea) criteriaEditor).getFilterEditors();
		}
		if (editors == null) {
			return;
		}

		for (IFilterEditor editor : editors) {
			if (editor.getFilter().getFilterMeta().getFieldCode().equals(itemKey)) {
				FilterEditorWrapper wapper = new FilterEditorWrapper(editor);
				if (isRight) {
					wapper.setRefPKRight(refPk);
				}else{
					wapper.setRefPK(refPk);
				}
			}
		}
	}

	/**
	 * 查询条件设值（参照类控件,默认左侧）
	 *
	 * 初始化时，给发出事件的控件复制
	 *
	 * @param event
	 * @param refPk
	 * @param isRight   between形式
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void setPk(CriteriaChangedEvent event,String refPk,boolean isRight) {
		IFilterEditor filtereditor = event.getFiltereditor();
		FilterEditorWrapper wapper = new FilterEditorWrapper(filtereditor);
		if (isRight) {
			wapper.setRefPKRight(refPk);
		}else{
			wapper.setRefPK(refPk);
		}
	}
}