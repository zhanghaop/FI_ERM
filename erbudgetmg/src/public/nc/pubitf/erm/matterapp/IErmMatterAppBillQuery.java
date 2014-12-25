package nc.pubitf.erm.matterapp;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.er.djlx.DjLXVO;
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
	 * ���������˹��ˣ���ѯ�������뵥
	 * @param condition
	 * @param djlxbm
	 * @param pk_org
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillFromMtappByPsn(String condition,String djlxbm,String pk_org,String pk_psndoc,String rolerSql) throws BusinessException;
	
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
	
	/**
	 * ��ȡ����ʱ��Ĭ��ֵ�ĳ�ʼ����VO <br>
	 * <li>��������
	 * <li>��Ա��Ϣ 
	 * <li>������
	 * <li>���֡����� 
	 * <li>���Ĭ��ֵ
	 * <li>���ݳ�ʼ״̬
	 * <li>������֯������
	 * 
	 * @param djlx
	 *            ��������
	 * @param funnode
	 *            �ڵ㹦�ܺ�
	 * @param vo
	 *            Ĭ��VO������Ϊnull
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO getAddInitAggMatterVo(DjLXVO djlx, String funnode, AggMatterAppVO vo) throws BusinessException;
	
	/**
	 * Ӫ������������������ƽ̨
	 * @param queryScheme
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryMaFor35ByQueryScheme(IQueryScheme queryScheme)throws BusinessException;
}
