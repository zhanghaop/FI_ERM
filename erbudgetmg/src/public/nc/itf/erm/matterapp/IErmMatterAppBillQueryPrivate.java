package nc.itf.erm.matterapp;

import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 内部查询使用接口
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillQueryPrivate {
	/**
	 * 根据查询条件查询
	 * <li>费用申请单录入 :可查询当前用户录入的和申请人为本人的单据 
	 * <li>费用申请单管理:可查询当前用户为审批人的单据。
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryBillPksByWhere(MatterAppQueryCondition condVo) throws BusinessException;

	public MatterAppVO[] getMtappByMthPk(String pk_org, String begindate,
			String enddate) throws BusinessException;
}
