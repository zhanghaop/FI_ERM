package nc.vo.erm.costshare.ext;

import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用结转单回写预算VO
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class CostShareYsControlVOExt extends CostShareYsControlVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CostShareYsControlVOExt(CostShareVO parentvo, CShareDetailVO detailvo) {
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
		parentvo.setApprovedate(ysDate);
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
		detailvo.setAssume_amount(amount[0]);
		detailvo.setBbje(amount[1]);
		detailvo.setGroupbbje(amount[2]);
		detailvo.setGlobalbbje(amount[3]);
	}

}
