package nc.ui.erm.extendtab;

import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * 扩展页签前台展现-列表扩展页签
 * 
 * @author wangled
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractErmExtendList extends BillScrollPane implements AppEventListener {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 界面初始化
	 */
	public abstract void initUI();
	
	/**
	 * setmodel
	 * 
	 * @param model
	 */
	public abstract void setModel(AbstractAppModel model);
	
	/**
	 * 设置扩展页签的展现数据
	 * 
	 * @param value
	 */
	public abstract void setValue(Object value);
	
	/**
	 * 设置列表
	 * @param listView
	 */
	public abstract void setListView(BillListView listView);
	
	/**
	 * 用户自定义项前缀
	 * 
	 * @return
	 */
	public abstract String getUserDefPrefix();
	/**
	 * 扩展物料辅助属性
	 * 
	 * @return
	 */
	public abstract MarAsstPreparator getMarAsstPrepare();
	 
}
