package nc.vo.erm.matterapp.ext;

import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用申请单预算控制vo
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 *
 */
public class MatterAppYsControlVOExt extends MatterAppYsControlVO{
	
	private static final long serialVersionUID = 1L;
	
	public MatterAppYsControlVOExt(MatterAppVO parentvo, MtAppDetailVO detailvo) {
		super(parentvo, detailvo);
	}
	
	/**
	 * 设置预算日期
	 * 
	 * @param ysDate
	 */
	public void setYsDate(UFDate ysDate){
		// 设置所有时间均为当前预算日期
		parentvo.setBilldate(ysDate);
		parentvo.setApprovetime(new UFDateTime(ysDate.toDate()));
	}
	
	/**
	 * 设置分期分摊的金额
	 * 
	 * 【原币金额、组织本币金额、集团本币金额、全局本币金额】
	 * 
	 * @param amount
	 */
	public void setYsAmount(UFDouble[] amount){
		if(amount == null || amount.length != 4){
			return ;
		}
		detailvo.setOrig_amount(amount[0]);
		detailvo.setOrg_amount(amount[1]);
		detailvo.setGroup_amount(amount[2]);
		detailvo.setGlobal_amount(amount[3]);
	}
}
