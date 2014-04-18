package nc.impl.erm.common;

import java.util.Collection;

import nc.bs.er.common.business.CommonBO;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.impl.erm.common.ArapCommonPriImp
 */
public class ArapCommonPriImp implements IArapCommonPrivate{
	
	private CommonBO bo;

	public CommonBO getBo() {
		if(bo==null){
			bo=new CommonBO();
		}
		return bo;
	}

	public void setBo(CommonBO bo) {
		this.bo = bo;
	}

	public void delete(SuperVO vo) throws BusinessException {
		getBo().delete(vo);
	}

	public void delete(AggregatedValueObject vo) throws BusinessException {
		getBo().delete(vo);
	}

	public SuperVO getVOByPk(Class clazz, String pk, boolean cascade) throws BusinessException {
		return getBo().getVOByPk(clazz,pk,cascade);
	}

	public Collection<SuperVO> getVOByPks(Class clazz, String[] pk, boolean cascade) throws BusinessException {
		return getBo().getVOByPks(clazz,pk,cascade);
	}

	public Collection<SuperVO> getVOs(Class clazz, String whereStr, boolean cascade) throws BusinessException {
		return getBo().getVOs(clazz,whereStr,cascade);
	}

	public SuperVO save(SuperVO vo) throws BusinessException {
		return getBo().save(vo);
	}

	public AggregatedValueObject save(AggregatedValueObject vo) throws BusinessException {
		return getBo().save(vo);
	}

	public SuperVO update(SuperVO vo) throws BusinessException {
		return getBo().update(vo);
	}

	public SuperVO update(SuperVO vo, boolean cascade) throws BusinessException {
		return getBo().update(vo,cascade);
	}

	public AggregatedValueObject update(AggregatedValueObject vo) throws BusinessException {
		return getBo().update(vo);
	}

	public AggregatedValueObject update(AggregatedValueObject vo, boolean cascade) throws BusinessException {
		return getBo().update(vo,cascade);
	}

	public SuperVO update(SuperVO vo, String[] fields) throws BusinessException {
		return getBo().update(vo,fields);
	}

	public void update(SuperVO[] vos, String[] fields) throws BusinessException {
		getBo().update(vos,fields);
	}

}
