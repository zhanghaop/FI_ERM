package nc.pubitf.erm.sharerule;

import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.pub.BusinessException;

/**
 * 分摊规则维护
 * 
 * @author lvhj
 *
 */
public interface IErShareruleManage {
	
	/**
	 * 分摊规则新增
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO insertVO(AggshareruleVO vo) throws BusinessException;
	/**
	 * 分摊规则修改
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggshareruleVO updateVO(AggshareruleVO vo) throws BusinessException;
	/**
	 * 分摊规则删除
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void deleteVO(AggshareruleVO vo) throws BusinessException;

}
