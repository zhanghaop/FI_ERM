package nc.vo.erm.matterappctrl;

import nc.vo.erm.matterapp.AggMatterAppVO;


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
	 * ��дԤ��ʹ�ã���д�������뵥ǰ�ı���
	 */
	private AggMatterAppVO[] oldMatterAppVO;
	
	/**
	 * ��дԤ��ʹ�ã���д��ķ������뵥
	 */
	private AggMatterAppVO[] matterAppVO;
	

	public String[] getControlinfos() {
		return controlinfos;
	}

	public void setControlinfos(String[] controlinfos) {
		this.controlinfos = controlinfos;
	}

	public AggMatterAppVO[] getOldMatterAppVO() {
		return oldMatterAppVO;
	}

	public void setOldMatterAppVO(AggMatterAppVO[] oldMatterAppVO) {
		this.oldMatterAppVO = oldMatterAppVO;
	}

	public AggMatterAppVO[] getMatterAppVO() {
		return matterAppVO;
	}

	public void setMatterAppVO(AggMatterAppVO[] matterAppVO) {
		this.matterAppVO = matterAppVO;
	}
	
}
