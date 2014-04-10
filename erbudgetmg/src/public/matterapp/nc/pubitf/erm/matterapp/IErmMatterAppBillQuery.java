package nc.pubitf.erm.matterapp;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;

/**
 * ������������ѯ����
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillQuery {
	
	/**
	 * ����pk��ѯ��������VO
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO queryBillByPK(String pk) throws BusinessException;
	
	/**
	 * ����PK���ϲ�ѯ����VO����
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillByPKs(String[] pks) throws BusinessException;
	/**
	 * ����PK���ϲ�ѯ����VO����
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillByPKs(String[] pks,boolean lazyLoad) throws BusinessException;
	
	/**
	 * ��ѯVO����
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillFromMtapp(String condition,String djlxbm,String pk_org,String pk_psndoc) throws BusinessException;
	
	/**
	 * ������ѯVO����
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillByWhere(String condition) throws BusinessException;
	
	/**
	 * ���ݱ�ͷpk���ϲ�ѯ��ͷVO
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public MatterAppVO[] queryMatterAppVoByPks(String[] pks) throws BusinessException;
	
	/**
	 * ���ݱ���pk���ϲ�ѯ����VO����
	 * @param pks ����Pk����
	 * @return
	 * @throws BusinessException
	 */
	public MtAppDetailVO[] queryMtAppDetailVOVoByPks(String[] pks) throws BusinessException;
}
