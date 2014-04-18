package nc.itf.erm.prv;

import java.util.Collection;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.itf.erm.prv.IArapCommonPrivate
 */
public interface IArapCommonPrivate {

	public abstract SuperVO save(SuperVO vo) throws BusinessException;

	public abstract SuperVO update(SuperVO vo) throws BusinessException;
	
	public abstract SuperVO update(SuperVO vo,String[] fields) throws BusinessException;

	public abstract void delete(SuperVO vo) throws BusinessException;

	public abstract SuperVO update(SuperVO vo, boolean cascade) throws BusinessException;

	public abstract Collection<SuperVO> getVOs(Class clazz, String whereStr, boolean cascade) throws BusinessException;

	public abstract SuperVO getVOByPk(Class clazz, String pk, boolean cascade) throws BusinessException;

	public abstract Collection<SuperVO> getVOByPks(Class clazz, String[] pk, boolean cascade) throws BusinessException;

	public abstract AggregatedValueObject save(AggregatedValueObject vo) throws BusinessException;

	public abstract AggregatedValueObject update(AggregatedValueObject vo) throws BusinessException;

	public abstract void delete(AggregatedValueObject vo) throws BusinessException;

	public abstract AggregatedValueObject update(AggregatedValueObject vo, boolean cascade) throws BusinessException;

	public abstract void update(SuperVO[] headerVOs, String[] fields) throws BusinessException;
	
}
