package nc.ui.erm.common;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JSplitPane;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.table.NCTableModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.common.CommonSuperVO;
import nc.vo.erm.util.FormulaUtil;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.ui.arap.common.CommonList
 *
 * 实现简单的管理界面
 * 1. 定义VO		,继承CommonSuperVO    						@see LoanControlVO
 * 2. 实现卡片界面, 主要实现方法 setVo, getVo        			@see LoanControlCard
 * 3. 实现列表界面, 主要实现方法 getHeader, getHeaderColumns     @see LoanControlList
 * 4. 实现管理界面， 引用卡片列表界面      						@see LoanControlMailPanel
 *
 * @see CommonSuperVO
 * @see CommonCard
 * @see CommonList
 * @see CommonModel
 * @see CommonModelListener
 * @see CommonUI
 */
public abstract class CommonList extends UIPanel implements ValueChangedListener {

	public abstract CommonUI getParentUI();
	public abstract void setParentUI( CommonUI ui);

	private static final String PRIMARY_KEY = "PrimaryKey";

	private UIScrollPane listPanel;

	private UITable listTable;

	private NCTableModel tableModel;

	private UIPanel jPanelOrg = null;

	private UILabel jLabel6 = null;

	private UIRefPane ivjtOrg;

	private UISplitPane splitPane1;

	public abstract Vector<String> getHeader();

	public abstract Vector<String> getHeaderColumns();

	public CommonList() {

	}


	public String getSelectVoPk() {

		int selectedRow = getListTable().getSelectedRow();

		if (selectedRow < 0 || selectedRow >= getListTable().getRowCount()) {
			return null;
		}

		return tableModel.getValueAt(selectedRow, tableModel.getColumnCount() - 1).toString();

	}

	public void setData(Collection<SuperVO> vos){

		Vector<String> attr = getHeaderColumns();

		Vector<Vector<String>> data = new Vector<Vector<String>>();

		Map<String, Vector<String>> formulaValueMap  = new HashMap<String, Vector<String>>();
		Map<String, String> formulaMap = new HashMap<String, String>();

		if (vos != null) {
			for (Iterator<SuperVO> iter = vos.iterator(); iter.hasNext();) {
				SuperVO vo = iter.next();

				Vector<String> values = new Vector<String>();

				for (Iterator<String> iterator = attr.iterator(); iterator.hasNext();) {
					String key = iterator.next();

					if (key.indexOf(FormulaUtil.FORMULA_SPLIT) != -1) {

						formulaValueMap = formulaParsePrepare(formulaValueMap, formulaMap, vo, values, key);

					} else {
						values.add(vo.getAttributeValue(key).toString());
					}

				}

				values.add(vo.getPrimaryKey());

				data.add(values);
			}

			formulaParse(attr, data, formulaValueMap, formulaMap);

		}

		tableModel.setDataVector(data);
	}

	private void formulaParse(Vector<String> attr, Vector<Vector<String>> data, Map<String, Vector<String>> formulaValueMap, Map<String, String> formulaMap){
		try {
			for (Iterator<String> iter = formulaMap.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				String formula = formulaMap.get(key);
				if (formula.startsWith(FormulaUtil.GETCOLVALUE)) {
					String[] cols = formula.split(FormulaUtil.FIELD_SPLIT);
					SuperVO vo = (SuperVO) Class.forName(cols[1]).newInstance();
					String fieldName = cols[2];
					Vector<String> pks = formulaValueMap.get(key);
					IArapCommonPrivate impl = (IArapCommonPrivate) NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
					Collection<SuperVO> supVOS = impl.getVOByPks(vo.getClass(), pks.toArray(new String[] {}), false);

					Map<String, SuperVO> voMaps = VOUtils.changeCollectionToMap(supVOS, vo.getPKFieldName());

					for (Iterator<Vector<String>> iterator = data.iterator(); iterator.hasNext();) {
						Vector<String> element = iterator.next();
						int index = attr.indexOf(key+FormulaUtil.FORMULA_SPLIT+formula);
						if(StringUtils.isNullWithTrim(element.get(index)))
							continue;
						if(voMaps.get(element.get(index))==null)
							continue;
						if(StringUtils.isNullWithTrim(voMaps.get(element.get(index)).toString()))
							continue;
						element.set(index, voMaps.get(element.get(index)).getAttributeValue(fieldName)==null?"":voMaps.get(element.get(index)).getAttributeValue(fieldName).toString());
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	private Map<String, Vector<String>> formulaParsePrepare(Map<String, Vector<String>> formulaValueMap, Map<String, String> formulaMap, SuperVO vo, Vector<String> values, String key) {

		String[] keys = key.split(FormulaUtil.FORMULA_SPLIT);
		String value;
		if(vo.getAttributeValue(keys[0])==null){
			value="";
		}else{
			value = vo.getAttributeValue(keys[0]).toString();
		}
		values.add(value);

		formulaMap.put(keys[0], keys[1]);

		if (formulaValueMap.get(keys[0]) == null) {
			Vector<String> keyvalues = new Vector<String>();
			keyvalues.add(value);
			formulaValueMap.put(keys[0], keyvalues);
		} else {
			formulaValueMap.get(keys[0]).add(value);
		}
		return formulaValueMap;
	}
	
	//只有在第一次初始化的时候，会置默认值，当参照改变时，会置最新的值
	private static boolean isFirstInit=true;
	
	public void initUI() {
		setName("CommonList");
		setLayout(new java.awt.CardLayout());
		setSize(565, 337);

		//增加财务组织下拉框,如果个性化中心设置默认的财务组织，需要带出对应的财务组织
        if(!isGroup()){
    		add(getOrgPanelName(), getSplitPaneTop());
    		getSplitPaneTop().setTopComponent(getUIPanelOrg());

    		getSplitPaneTop().setBottomComponent(getListPanel());
    		
    		//默认组织
    		if(isFirstInit){
    			final String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();
        		getRefOrg().setPK(defaultOrg!=null?defaultOrg:null);
        	}
    		isFirstInit=false;
        
        }else{
    		add(getListPanel(), getListPanel().getName());
        }
	}
	
	protected String getOrgPanelName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000006")/*@res "财务组织"*/;
	}

	protected abstract boolean isGroup();

	public UIScrollPane getListPanel() {

		if (listPanel == null) {
			try {
				listPanel = new UIScrollPane();
				listPanel.setName("list");
				listPanel.setVerticalScrollBarPolicy(UIScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				listPanel.setHorizontalScrollBarPolicy(UIScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				getListPanel().setViewportView(getListTable());
			} catch (java.lang.Throwable e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			}
		}
		return listPanel;
	}

	public UITable getListTable() {
		if (listTable == null) {
			try {
				listTable = new nc.ui.pub.beans.UITable();
				listTable.setName("listTable");
				getListPanel().setColumnHeaderView(listTable.getTableHeader());
				listTable.setBounds(0, 0, 200, 200);
				listTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

				Vector<String> header = getHeader();
				header.add(PRIMARY_KEY);

				tableModel = new NCTableModel(new Vector(), header);
				tableModel.setAndEditable(false);
				getListTable().setModel(tableModel);

				listTable.getColumn(PRIMARY_KEY).setPreferredWidth(-1);
				listTable.getColumn(PRIMARY_KEY).setMaxWidth(-1);
				listTable.getColumn(PRIMARY_KEY).setMinWidth(-1);
				listTable.getColumn(PRIMARY_KEY).setWidth(-1);
			} catch (java.lang.Throwable e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			}
		}
		return listTable;
	}
	private UIPanel getUIPanelOrg() {
		if (jPanelOrg == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			jLabel6 = new UILabel();
			jLabel6.setText(getOrgPanelName());
			jPanelOrg = new UIPanel();
			jPanelOrg.setLayout(flowLayout1);
			jPanelOrg.setName("jPanelOrg");
			jPanelOrg.add(jLabel6, null);
			jPanelOrg.add(getRefOrg(), null);
			jPanelOrg.setSize(639, 363);
		}
		return jPanelOrg;
	}
	public UIRefPane getRefOrg() {
		if (ivjtOrg == null) {

			ivjtOrg = new UIRefPane();
			ivjtOrg.setRefNodeName("财务组织");/*-=notranslate=-*/
			ivjtOrg.setMultiSelectedEnabled(true);
			ivjtOrg.setPreferredSize(new Dimension(200, ivjtOrg.getHeight()));
			// 不支持多集团参照
			ivjtOrg.setMultiCorpRef(false);
			ivjtOrg.addValueChangedListener(this);
			ivjtOrg.setMultiSelectedEnabled(false);

		}
		return ivjtOrg;
	}
	public UISplitPane getSplitPaneTop(){
		if(splitPane1 == null)
			splitPane1 = new UISplitPane(JSplitPane.VERTICAL_SPLIT);
		return splitPane1;
	}

}