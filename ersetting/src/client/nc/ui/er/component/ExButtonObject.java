package nc.ui.er.component;

import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.plugin.IButtonStatListener;
import nc.ui.er.plugin.IMainFrame;
import nc.ui.pub.ButtonObject;
import nc.vo.pub.BusinessException;
;
/**��չ�İ�ť*/
public class ExButtonObject extends ButtonObject {
	
	/*��ť״̬������*/
	private IButtonStatListener btnStatLisener=null;
	/*��ť�¼�������*/
	private IButtonActionListener btnActLisener = null;
	/*��ťΨһ��־�����ڰ�ť�����Ӱ�ť�������*/
	private String btnid = null;
	/*����ܵ�����*/
	private IMainFrame mainFrame=null;
	
	public ExButtonObject(String name, String hint, int power, String code,String btnid) {
		super(name,hint,power,code);
		this.btnid=btnid;
	}
	public IButtonActionListener getBtnActLisener() {
		return btnActLisener;
	}
	public void setBtnActLisener(IButtonActionListener btnActLisener) {
		this.btnActLisener = btnActLisener;
		if(getMainFrame()!=null){
			getBtnActLisener().setMainFrame(getMainFrame());
		}
	}
	public IButtonStatListener getBtnStatLisener() {
		return btnStatLisener;
	}
	public void setBtnStatLisener(IButtonStatListener btnStatLisener) {
		this.btnStatLisener = btnStatLisener;
		if(getMainFrame()!=null){
			getBtnStatLisener().setMainFrame(getMainFrame());
		}
	}
	public String getBtnid() {
		return btnid;
	}
	public void setBtnid(String btnid) {
		this.btnid = btnid;
	}
	
	/**��IButtonStatLisener˵��*/
	public boolean isBtnEnable(){
		return getBtnStatLisener().isBtnEnable();
	}
	/**��IButtonStatLisener˵��*/
	public boolean isBtnVisible(){
		return getBtnStatLisener().isBtnVisible();
	}
	/**��IButtonStatLisener˵��*/
	public boolean isSubBtn(){
		return getBtnStatLisener().isSubBtn();
	}
	/**��IButtonStatLisener˵��*/
	public String getParentBtnid(){
		return getBtnStatLisener().getParentBtnid();
	}
	public IMainFrame getMainFrame() {
		return mainFrame;
	}
	public void setMainFrame(IMainFrame mainFrame) {
		this.mainFrame = mainFrame;
		if(getBtnStatLisener()!=null){
			getBtnStatLisener().setMainFrame(mainFrame);
		}
		if(getBtnActLisener()!=null){
			getBtnActLisener().setMainFrame(mainFrame);
		}
	}
	
	public boolean onClicked() throws BusinessException{
		return getBtnActLisener().actionPerformed();
	}
}
