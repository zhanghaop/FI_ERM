package nc.pubitf.erm.billcontrast;

import nc.vo.pub.BusinessException;

/**
 * ���õ��ݶ��գ������ṩ�Ĳ�ѯ����
 * 
 * @author lvhj
 *
 */
public interface IErmBillcontrastQueryService {

	/**
	 * ������֯����Դ��������code����ѯĿ�꽻������code
	 * 
	 * @param pk_org
	 * @param src_tradetype
	 * @return
	 * @throws BusinessException
	 */
	public String queryDesTradetypeBySrc(String pk_org,String src_tradetype) throws BusinessException;
}
