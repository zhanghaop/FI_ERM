package nc.pubitf.erm.costshare;

import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * ���ý�ת����ѯ�
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillQuery {
	
	/**
	 * ����PK��ѯ
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO queryBillByPK(String pk) throws BusinessException;
	/**
	 * ����PK�����ѯ
	 * 
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO[] queryBillByPKs(String[] pks) throws BusinessException;
	
	
	/**
	 * ���ݲ�ѯ������ѯ
	 * 
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO[] queryBillByWhere(String sqlWhere) throws BusinessException;
	
	/**
	 * ���ݱ�������ͷ��ѯ������ϸ
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public CShareDetailVO[] queryCShareDetailVOSByBxVoHead(BXHeaderVO header) throws BusinessException;
	
	/**
	 * ���ݱ�������ͷ��ѯ���ý�ת����ͷ
	 * @param header
	 * @param from  true��ʾ��������false��ʾ���ý�ת����null��ʾȫ��
	 * @return
	 * @throws BusinessException
	 */
	public CostShareVO queryCShareVOByBxVoHead(JKBXHeaderVO header, UFBoolean from) throws BusinessException;
	/**
	 * ������֯����ʼ�ڼ�ͽ����ڼ��ѯ���ý�ת��״̬
	 * @param pk_org
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws BusinessException
	 */
	public CostShareVO[] getShareByMthPk(String pk_org, String begindate,
			String enddate) throws BusinessException;
}
