package nc.vo.erm.matterappctrl;

import nc.vo.erm.matterapp.AggMatterAppVO;


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
	 * 回写预算使用：回写费用申请单前的备份
	 */
	private AggMatterAppVO[] oldMatterAppVO;
	
	/**
	 * 回写预算使用：回写后的费用申请单
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
