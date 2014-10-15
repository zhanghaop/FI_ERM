package nc.vo.erm.matterappctrl;



/**
 * 事项审批单控制结果VO
 * 
 * @author lvhj
 *
 */
public class MtappCtrlInfoVO implements Cloneable, java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 控制信息
	 */
	private String[] controlinfos;
	
	/**
	 * 执行数是否超出申请
	 */
	private boolean isExceed = false;
	

	public String[] getControlinfos() {
		return controlinfos;
	}

	public void setControlinfos(String[] controlinfos) {
		this.controlinfos = controlinfos;
	}

	public boolean isExceed() {
		return isExceed;
	}

	public void setExceed(boolean isExceed) {
		this.isExceed = isExceed;
	}

}
