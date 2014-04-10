package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;

/**
 * ����̯�����̼�¼��ѯ
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeprocQuery {
	
	/**
	 * ���ݷ���̯����ϢPKS��ѯ
	 * ��������ʹ��
	 * �Ὣͬһ����ڼ��ͬ
	 * @param infovo ̯����Ϣ��ͷ
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtprocVO[] linkProcByInfoVo(ExpamtinfoVO infovo) throws BusinessException  ;
	
	/**
	 * ����̯����Ϣpk���Ϻͻ���ڼ��ѯ̯����¼����
	 * @param infoPk ̯����ϸ��Ϣpk
	 * @param accperiod ����ڼ䣨����2012-12-02����
	 * @return ̯����¼
	 */
	public ExpamtprocVO[] queryByInfoPksAndAccperiod(String[] infoPks , String accperiod)throws BusinessException;
	
	/**
	 * ���ݱ�����ź�����֯
	 * @param djbh
	 * @param pk_org
	 * @return
	 */
	public ExpamtprocVO[] queryByDjbhAndPKOrg(String djbh,String pk_org)throws BusinessException;
	
	/**
	 * ����̯����¼pks��ѯ̯����¼
	 * @param djbh
	 * @param pk_org
	 * @return
	 */
	public ExpamtprocVO[] queryByProcPks(String[] pks)throws BusinessException;
}
