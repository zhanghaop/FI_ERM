package nc.pubitf.erm.erminit;

import java.util.List;

import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.erm.erminit.ErminitVO;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 费用管理期初查询
 * 
 * @author lvhj
 *
 */
public interface IErminitQueryService {
	
	/**
	 * 按组织查询费用期初
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public ErminitVO queryByOrg(String pk_org) throws BusinessException;
	
	/**
	 * 按组织查询费用期初的关闭状态
	 */
	
	public boolean queryStatusByOrg(String pk_org) throws BusinessException;
	/**
	 * 查询组织的结账信息
	 */
	public List<CloseAccBookVO> queryAccStatusByOrg(String pk_org) throws BusinessException;
	
	/**
	 * 返回组织关闭的费用期初
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryStatusByOrgs(String[] pk_org)throws BusinessException;
	
	/**
	 * 获得冲销明细
	 */
	
	public List<BxcontrastVO> getBxcontrastVO(String jkdpk)throws BusinessException;
	
}
