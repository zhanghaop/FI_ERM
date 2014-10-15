package nc.itf.erm.multiversion;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;

public interface IMultiVersionQry {

	/**
	 * 查询多版本vo
	 * @return
	 * @throws BusinessException
	 */
	public java.util.List<SuperVO> queryVersionVO(IMultiversionVO vo) throws BusinessException;
	
	/**
	 * 根据oid查询vid
	 * @return [oid,[vid1,vid2,vid3...]]
	 * @throws BusinessException
	 */
	public Map<String,List<String>> queryVidByOid(IMultiversionVO vo,String[] oids) throws BusinessException;
	
	/**
	 * 根据oid和日期查询vid
	 * @return [oid,vid]
	 * @throws BusinessException
	 */
	public Map<String,String> queryVidByOid(IMultiversionVO vo,String[] oids,UFDate date) throws BusinessException;
	
	/**
	 * 根据vid查询oid
	 * @return [vid,oid]
	 * @throws BusinessException
	 */
	public Map<String,String> queryOidByVid(IMultiversionVO vo,String[] vids) throws BusinessException;
}
