package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * ����̯����Ϣ��ѯ
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeinfoQuery {

	/**
	 * ���ݱ���PKs��ѯ̯����Ϣ
	 * 
	 * @param bxPks
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO[] queryByBxPks(String[] bxPks, String currentAccMonth) throws BusinessException;
	
	
	/**
	 * ����̯����ϢPK��ѯ̯����Ϣ
	 * 
	 * @param pks
	 * @param currentAccMonth ��ǰ�����
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO queryByPk(String pk, String currentAccMonth) throws BusinessException;
	/**
	 * ����̯����ϢPK�����ѯ̯����Ϣ
	 * 
	 * @param pks
	 * @param currentAccMonth ��ǰ�����
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO[] queryByPks(String[] pks,String currentAccMonth) throws BusinessException;
	
	/**
	 * ����̯��PK�����ѯ̯����ͷ��Ϣ��Ϣ
	 * 
	 * @param pks ̯��pk����
	 * @param currentAccMonth ��ǰ�����
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryExpamtinfoByPks(String[] pks,String currentAccMonth) throws BusinessException;
	
	/**
	 * ������֯������ڼ䣬��ѯ̯����Ϣ
	 * 
	 * @param pk_org
	 * @param currentAccMonth
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryByOrg(String pk_org,String currentAccMonth) throws BusinessException;
	
	/**
	 * ������֯������ڼ䣬��ѯ̯����Ϣ��PK
	 * 
	 * @param pk_org
	 * @param period ��ǰ�����
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryPksByCond(String pk_org,String period) throws BusinessException;
	
	/**
	 * ��ѯȫ��̯���еĴ�̯��Ϣ(������ʼ̬��̯���еĴ�̯��Ϣ����ʼ̬��̯����Ϣ�Ƿ���Խ���̯����̯��������У�鼰����)
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryAllAmtingVOs() throws BusinessException;
	
	/**
	 * ������֯�ͱ������ݺŲ�ѯ̯����Ϣ��PK
	 * 
	 */
	
	public String[] queryByOrgBillNo(String pk_org, String billno)throws BusinessException;
	
	/**
	 * ������֯�ͱ������ݺŲ�ѯ̯����Ϣ
	 * @param pk_org
	 * @param billno
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryByOrgAndBillNo(String pk_org, String billno) throws BusinessException;
	
	/**
	 * ���ݲ�ѯ������ѯ̯����Ϣ��PK
	 * @param whereSql
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryPksByWhereSql(String whereSql)throws BusinessException;
	
	/**
	 * ����̯����Ϣ�����PK��ѯ�ӱ�
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtDetailVO[] queryAllDetailVOs(String pk_expamtinfo) throws BusinessException;
	
	/**
	 * ����̯����ͷ��Ϣ������̯����Ϣ
	 * @param vo
	 * @param currentAccMonth
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO fillUpAggExpamtinfo(ExpamtinfoVO vo, String currentAccMonth) throws BusinessException;
}
