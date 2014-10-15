package nc.ui.erm.extendtab;

import java.util.List;

import javax.swing.Action;

import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * ��չҳǩǰ̨չ��-��Ƭ��չҳǩ
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
	 * ���ڿ�Ƭ
	 * 
	 * @param card
	 */
	public abstract void setParentCard(BillForm card);
	
	/**
	 * �����ʼ��
	 */
	public abstract void initUI();
	
	/**
	 * ��չҳǩ�в�����ť��
	 * 
	 * @return
	 */
	public abstract List<Action> getActions();
	
	/**
	 * ������չҳǩ��չ������
	 * 
	 * @param value
	 */
	public abstract void setValue(Object value);
	
	/**
	 * �����չҳǩ�ڵ�ҵ������
	 * 
	 * @return
	 */
	public abstract Object getValue();
	
	/**
	 * �û��Զ�����ǰ׺
	 * 
	 * @return
	 */
	public abstract String getUserDefPrefix();
	/**
	 * ��չ���ϸ�������
	 * 
	 * @return
	 */
	public abstract MarAsstPreparator getMarAsstPrepare();
	
}
