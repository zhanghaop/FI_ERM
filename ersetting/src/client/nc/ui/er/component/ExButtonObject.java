package nc.ui.er.component;

import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.plugin.IButtonStatListener;
import nc.ui.er.plugin.IMainFrame;
import nc.ui.pub.ButtonObject;
import nc.vo.pub.BusinessException;
;
/**扩展的按钮*/
public class ExButtonObject extends ButtonObject {
	
	/*按钮状态监听器*/
	private IButtonStatListener btnStatLisener=null;
	/*按钮事件监听器*/
	private IButtonActionListener btnActLisener = null;
	/*按钮唯一标志，用于按钮存在子按钮的情况下*/
	private String btnid = null;
	/*主框架的引用*/
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
	
	/**见IButtonStatLisener说明*/
	public boolean isBtnEnable(){
		return getBtnStatLisener().isBtnEnable();
	}
	/**见IButtonStatLisener说明*/
	public boolean isBtnVisible(){
		return getBtnStatLisener().isBtnVisible();
	}
	/**见IButtonStatLisener说明*/
	public boolean isSubBtn(){
		return getBtnStatLisener().isSubBtn();
	}
	/**见IButtonStatLisener说明*/
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
