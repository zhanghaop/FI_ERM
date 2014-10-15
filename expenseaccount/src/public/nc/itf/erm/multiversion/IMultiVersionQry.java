package nc.itf.erm.multiversion;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;

public interface IMultiVersionQry {

	/**
	 * ��ѯ��汾vo
	 * @return
	 * @throws BusinessException
	 */
	public java.util.List<SuperVO> queryVersionVO(IMultiversionVO vo) throws BusinessException;
	
	/**
	 * ����oid��ѯvid
	 * @return [oid,[vid1,vid2,vid3...]]
	 * @throws BusinessException
	 */
	public Map<String,List<String>> queryVidByOid(IMultiversionVO vo,String[] oids) throws BusinessException;
	
	/**
	 * ����oid�����ڲ�ѯvid
	 * @return [oid,vid]
	 * @throws BusinessException
	 */
	public Map<String,String> queryVidByOid(IMultiversionVO vo,String[] oids,UFDate date) throws BusinessException;
	
	/**
	 * ����vid��ѯoid
	 * @return [vid,oid]
	 * @throws BusinessException
	 */
	public Map<String,String> queryOidByVid(IMultiversionVO vo,String[] vids) throws BusinessException;
}
