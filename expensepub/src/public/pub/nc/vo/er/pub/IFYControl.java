package nc.vo.er.pub;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;


/**
 * @author twei
 *
 * nc.vo.itf.IitemControl
 * 
 * VO 需要实现的接口, 用于费用控制 (事项审批,预算,借款控制).
 */
public interface IFYControl {
	
	
	public boolean isYSControlAble();
	public boolean isJKControlAble();
	public boolean isSSControlAble();
	
	/**
	 * @return 事项审批单主键
	 */
	public String getPk_item();
	
	/**
	 * @return 事项审批Vo的属性值
	 */
	public Object getItemValue(String key);
	
	/**
	 * @return 控制金额[glbbb,groupbb,orgbb,yb], 带符号
	 */
	public UFDouble[] getItemJe();
	
	/**
	 * @return 事项审批项币种
	 */
	public String getBzbm();
	
	/**
	 * @return 事项审批项汇率 UFdouble[glbbbhl,groupbbhl,bb_hl]
	 */
	public UFDouble[] getItemHl();
	
	
	
	/**
	 * 
	 * 	if(head.getDjzt().intValue()==DJZBVOConsts.m_intDJStatus_Signature){
			items[k].setCloser(head.getYhqrr());
			items[k].setClosedate(head.getYhqrrq());
		}else if(head.getDjzt().intValue()==DJZBVOConsts.m_intDJStatus_Verified){
			items[k].setCloser(head.getShr());
			items[k].setClosedate(head.getShrq());
		}else if(head.getDjzt().intValue()==DJZBVOConsts.m_intDJStatus_Saved){
			items[k].setCloser(head.getLrr());
			items[k].setClosedate(head.getDjrq());
		}		
	 * @return
	 */
	public String getOperationUser();
	public UFDate getOperationDate();

	/**
	 * @return 费用承担公司pk
	 */
	public String getFydwbm();
	/**
	 * 单据录入公司 	 
	 */
	public String getPk_group();
	
	/**
	 * 报销主体 	 
	 */
	public String getDwbm();
	
	/**
	 * @return 单据日期 
	 */
	public UFDate getDjrq();
	
	/**
	 * @return 单据类型编码
	 */
	public String getDjlxbm();
	
	/**
	 * @return 主键
	 */
	public String getPk();

	/**
	 * @return 单据大类
	 */
	public String getDjdl();

	/**
	 * @return 上层来源
	 */
	public String getDdlx();

	/**
	 * @return 方向
	 */
	public Integer getFx();

	/**
	 * @return 是否保存控制
	 */
	public boolean isSaveControl();
	
	/**
	 * @return 结算方式
	 */
	public String getJsfs();

	/**
	 * 
	 * 返回对应的单据状态
	 */
	public Integer getDjzt();

	/**
	 * 
	 * 返回对应的报销人
	 */
	public String getJkbxr() ;
	/**
	 * 
	 * 返回对应的录入人
	 */
	public String getOperator() ;
	public String getPk_org();
	
}
