package nc.impl.erm.multiversion;

import java.util.List;
import java.util.Map;

import nc.bs.erm.multiversion.MultiVersionBO;
import nc.itf.erm.multiversion.IMultiVersionQry;
import nc.itf.erm.multiversion.IMultiversionVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;

public class MultiVersionQryImpl implements IMultiVersionQry {

	private MultiVersionBO bo = new MultiVersionBO();

	@Override
	public Map<String,String> queryOidByVid(IMultiversionVO vo,String[] vids) throws BusinessException {
		return bo.queryOidByVid(vo, vids);
	}

	@Override
	public List<SuperVO> queryVersionVO(IMultiversionVO vo) throws BusinessException {
		return bo.queryVersionVO(vo);
	}

	@Override
	public Map<String,List<String>> queryVidByOid(IMultiversionVO vo,String[] oids) throws BusinessException {
		return bo.queryVidByOid(vo, oids);
	}

	@Override
	public Map<String,String> queryVidByOid(IMultiversionVO vo,String[] oids, UFDate date)
			throws BusinessException {
		return bo.queryVidByOid(vo, oids, date);
	}
}
