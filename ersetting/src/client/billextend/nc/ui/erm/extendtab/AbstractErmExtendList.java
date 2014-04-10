package nc.ui.erm.extendtab;

import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * ��չҳǩǰ̨չ��-�б���չҳǩ
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
	 * �����ʼ��
	 */
	public abstract void initUI();
	
	/**
	 * setmodel
	 * 
	 * @param model
	 */
	public abstract void setModel(AbstractAppModel model);
	
	/**
	 * ������չҳǩ��չ������
	 * 
	 * @param value
	 */
	public abstract void setValue(Object value);
	
	/**
	 * �����б�
	 * @param listView
	 */
	public abstract void setListView(BillListView listView);
	
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
