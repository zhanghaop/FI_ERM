package nc.ui.er.plugin;

import nc.vo.pub.BusinessException;

/**
 * ��ť����¼�������
 * */
public interface IButtonActionListener {

	/**�������������*/
	public void setMainFrame(IMainFrame mf);
	/**��ť������������˷����ķ���ֵ��֪ͨ���س����Ƿ���Ҫˢ��ui����*/
	public boolean actionPerformed() throws BusinessException;
}
