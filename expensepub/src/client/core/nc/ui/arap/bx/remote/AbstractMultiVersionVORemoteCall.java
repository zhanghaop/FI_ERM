package nc.ui.arap.bx.remote;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.er.util.BXUiUtil;
import nc.vo.pub.lang.UFDate;

/**
 * 多版本VO远程调用
 * @author chendya
 *
 */
public abstract class AbstractMultiVersionVORemoteCall extends AbstractVORemoteCall {
	
	/**
	 * 默认取截止当前业务时间
	 */
	protected String vStartDate  = BXUiUtil.getBusiDate().toStdString() + " 23:59:59";
	
	public AbstractMultiVersionVORemoteCall(BXBillMainPanel panel) {
		super(panel);
	}
	
	public AbstractMultiVersionVORemoteCall(BXBillMainPanel panel,UFDate date) {
		super(panel);
		this.vStartDate = date.toLocalString();
	}
	
	public AbstractMultiVersionVORemoteCall(BXBillMainPanel panel,String date) {
		super(panel);
		this.vStartDate = date;
	}
	
	public String getvStartDate() {
		return vStartDate;
	}
}
