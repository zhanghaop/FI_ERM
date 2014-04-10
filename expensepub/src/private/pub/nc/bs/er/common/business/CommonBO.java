package nc.bs.er.common.business;

import java.util.Collection;

import nc.bs.dao.DAOException;
import nc.bs.er.common.dao.CommonDAO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class CommonBO {
	
	private CommonDAO dao;
	
	
	public CommonDAO getDao() {
		
		if(dao==null){
			dao=new CommonDAO();
		}
		return dao;
	}

	
	public void setDao(CommonDAO dao) {
		this.dao = dao;
	}

	public SuperVO save(SuperVO vo) throws BusinessException{
		try {
			getDao().save(vo, true);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	
	public SuperVO update(SuperVO vo) throws BusinessException{
		try {
			getDao().update(vo, true);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	public void delete(SuperVO vo) throws BusinessException{
		try {
			getDao().delete(vo, true);
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	public SuperVO update(SuperVO vo,boolean cascade) throws BusinessException{
		try {
			getDao().update(vo, cascade);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	public Collection<SuperVO> getVOs(Class clazz,String whereStr,boolean cascade) throws BusinessException{
		try {
			return getDao().getVOs(clazz, whereStr, cascade);
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	
	public SuperVO getVOByPk(Class clazz,String pk,boolean cascade) throws BusinessException{
		try {
			return getDao().getVOByPk(clazz, pk, cascade);
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}

	public Collection<SuperVO> getVOByPks(Class clazz,String[] pk,boolean cascade) throws BusinessException{
		try {
			return getDao().getVOByPks(clazz, pk, cascade);
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	
	public AggregatedValueObject save(AggregatedValueObject vo) throws BusinessException{
		try {
			getDao().save(vo, true);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	
	public AggregatedValueObject update(AggregatedValueObject vo) throws BusinessException{
		try {
			getDao().update(vo, false);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	public void delete(AggregatedValueObject vo) throws BusinessException{
		try {
			getDao().delete(vo, true);
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	public AggregatedValueObject update(AggregatedValueObject vo,boolean cascade) throws BusinessException{
		try {
			getDao().update(vo, cascade);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}


	public SuperVO update(SuperVO vo, String[] fields) throws BusinessException {
		try {
			getDao().update(vo, fields);
			return vo;
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}


	public void update(SuperVO[] vos, String[] fields) throws BusinessException {
		try {
			getDao().update(vos, fields);
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}
	
	

}
