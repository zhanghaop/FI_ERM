package nc.itf.erm.billcontrast;

import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * 费用单据对照查询（内部使用）
 * 
 * @author lvhj
 *
 */
public interface IErmBillcontrastQuery {
	
	/**
	 * 查询组织所有对照信息
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public BillcontrastVO[] queryAllByOrg(String pk_org,String pk_group,LoginContext context) throws BusinessException;
	/**
	 * 按条件查询
	 * 
	 * @param pk_org
	 * @param where
	 * @return
	 * @throws BusinessException
	 */
	public BillcontrastVO[] queryVOsByWhere(String pk_org,String where) throws BusinessException;
	
	/**
	 * 全局的预置数据查询
	 */
	public BillcontrastVO[] queryAllByGloble() throws BusinessException;

}
