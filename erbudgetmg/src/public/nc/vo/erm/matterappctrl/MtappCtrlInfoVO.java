package nc.vo.erm.matterappctrl;



/**
 * �������������ƽ��VO
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
	 * ������Ϣ
	 */
	private String[] controlinfos;
	
	/**
	 * ִ�����Ƿ񳬳�����
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
