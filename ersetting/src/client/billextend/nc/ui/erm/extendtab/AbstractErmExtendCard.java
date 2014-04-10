package nc.ui.erm.extendtab;

import java.util.List;

import javax.swing.Action;

import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * 扩展页签前台展现-卡片扩展页签
 * 
 * @author wangled
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractErmExtendCard extends BillScrollPane implements AppEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ORG_SELECTED="org_selected";
	/**
	 * setmodel
	 * 
	 * @param model
	 */
	public abstract void setModel(AbstractAppModel model);
	
	/**
	 * 所在卡片
	 * 
	 * @param card
	 */
	public abstract void setParentCard(BillForm card);
	
	/**
	 * 界面初始化
	 */
	public abstract void initUI();
	
	/**
	 * 扩展页签行操作按钮组
	 * 
	 * @return
	 */
	public abstract List<Action> getActions();
	
	/**
	 * 设置扩展页签的展现数据
	 * 
	 * @param value
	 */
	public abstract void setValue(Object value);
	
	/**
	 * 获得扩展页签内的业务数据
	 * 
	 * @return
	 */
	public abstract Object getValue();
	
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
