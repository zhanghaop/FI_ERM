package nc.ui.er.plugin;
/**
 * ��ť��������¼�������
 * */
public interface IButtonStatListener {
	/**�������������*/
	public void setMainFrame(IMainFrame mf);
	/**��ť�Ƿ�ɼ�*/
	public boolean isBtnVisible();
	/**��ť�Ƿ����*/
	public boolean isBtnEnable();
	/**�Ƿ����Ӱ�ť,�жϰ�ť�ڵ�ǰ��״̬���Ƿ�Ϊ�Ӱ�ť���Ƿ���true,���� false*/
	public boolean isSubBtn();
	/**����ť���Ӱ�ť������£���������ť��Ψһ��־*/
	public String getParentBtnid();
}
