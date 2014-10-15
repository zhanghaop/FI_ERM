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
	 *  ��ȡ�¼����¶�Ӧ�Ŀؼ�
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
	 *  ��ȡ�¼����¶�Ӧ�Ŀؼ�(between �Ҳ�ؼ�)
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
	 * ��ȡList<IFilterEditor>
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
//			Logger.error("��֧�ֵĲ�ѯ����ʽ---TXM");
//			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0015")/*@res "��֧�ֵĲ�ѯ����ʽ"*/);
//		}
		return feList;
	}

	/**
	 * ��ȡ���¼����¶�Ӧ�Ŀؼ�
	 *
	 * @param event
	 * @param code
	 * @param isRight   �Ƿ�ȡbetween��ʽ�Ҳ��ֵ
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
	 * ��ȡ���¼����¶�Ӧ�����пؼ�
	 * ������ĳ��� ��һ��ʱ�����͵ģ�����һ�����ڣ���һ��С��һ������
	 * @param event
	 * @param code
	 * @param isRight   �Ƿ�ȡbetween��ʽ�Ҳ��ֵ
	 * @author: chenshuaia ��ѯģ���п��ܴ��ڶ����ͬ�Ĳ�ѯ
	 * @return
	 */
	public static JComponent[] getFiltComponentsForValueChanged(CriteriaChangedEvent event,String code,boolean isRight) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}

		return getComps(getIFilterEditorList(event, code),isRight);
	}
	
	/**
	 * ���ݱ���ȡ�ò�ѯ����ϵĿؼ�(��ȡ��һcode�����ֵcomponent)
	 * 
	 * @param isRight
	 *            �Ƿ�ȡbetween��ʽ�Ҳ��ֵ
	 * @param feList
	 * @author: chenshuaia ��ѯģ���п��ܴ��ڶ����ͬ�Ĳ�ѯ
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
	 * ���ݱ���ȡ�ò�ѯ����ϵĿؼ�(��ȡ��һcode�����ֵcomponent)
	 *
	 * @param isRight  �Ƿ�ȡbetween��ʽ�Ҳ��ֵ
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
	 * ��ͬcode�������ѯ�������
	 *
	 * ���ݱ���ȡ�ò�ѯ����ϵĿؼ�(ȡȫ��code�����component)
	 * valueChanged�¼�ר��FILTER_CHANGED
	 *
	 * @param event
	 * @param code
	 * @param isRight   �Ƿ�ȡbetween��ʽ�Ҳ��ֵ
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
	 * ��ͬcode�������ѯ�������
	 *
	 * ���ݱ���ȡ�ò�ѯ����ϵĿؼ�(ȡȫ��code�����component)
	 *
	 * @param isRight  �Ƿ�ȡbetween��ʽ�Ҳ��ֵ
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
	 * ��ȡList<IFilterEditor>
	 *
	 * ��event���
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
	 * ��������֯Ȩ��
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
	 * ��������֯Ȩ��
	 *
	 * @param pkOrgs
	 * @param refPane
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void filtOrgs(String[] pkOrgs, UIRefPane refPane) {
		if (pkOrgs == null) {
			// û�з�������֯Ȩ�����
			pkOrgs = new String[0];
		}
		refPane.getRefModel().setFilterPks(pkOrgs);
		refPane.getRefModel().setAddEnableStateWherePart(true);
	}

	/**
	 * ��ѯ������ֵ��������ؼ�,Ĭ����ࣩ
	 * valuechanged
	 *
	 * @param event
	 * @param itemKey
	 * @param refPk
	 * @param isRight  between��ʽ
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
	 * ��ѯ������ֵ��������ؼ�,Ĭ����ࣩ
	 *
	 * ��ʼ��ʱ���������¼��Ŀؼ�����
	 *
	 * @param event
	 * @param refPk
	 * @param isRight   between��ʽ
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