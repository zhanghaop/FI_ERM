package nc.itf.erm.accruedexpense;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.vo.pub.BusinessException;

/**
 * 预提单内部查询服务接口
 * @author shengqy
 *
 */
public interface IErmAccruedBillQueryPrivate {
	
	/**
	 * 根据条件查询预提pks
	 * 预提单录入节点：因为无授权代理，所以只可以查自己做的单据
	 * 预提单管理节点：可以查到有审批权限的单据（可查询当前用户为审批人的单据）
	 * 预提单查询节点：可以查集团下有所有功能权限组织下的单据
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryBillPksByWhere(AccruedBillQueryCondition condvo) throws BusinessException;
}
