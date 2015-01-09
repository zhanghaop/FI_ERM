package nc.itf.erm.accruedexpense;

import java.util.List;
import java.util.Map;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.jdbc.framework.exception.DbException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

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
	
	public Map<String, UFDateTime> getTsMapByPK(List<String> key, String tableName, String pk_field) throws DbException;
}
