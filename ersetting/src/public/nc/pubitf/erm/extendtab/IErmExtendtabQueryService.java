package nc.pubitf.erm.extendtab;

import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * ��չҳǩ���ݲ�ѯ����
 * 
 * er_extendconfig��queryclass����ʵ�ֵĽӿ�
 * 
 * @author lvhj
 *
 */
public interface IErmExtendtabQueryService {
	
	/**
	 * �������뵥PK��ѯ��չ�ӱ���Ϣ
	 * 
	 * @param pk_mtapp_bill
	 * @return
	 * @throws BusinessException
	 */
	public CircularlyAccessibleValueObject[] queryByMaPK(String pk_mtapp_bill) throws BusinessException;
	/**
	 * �������뵥PKs��ѯ��չ�ӱ���Ϣ
	 * 
	 * @param mtapp_billPks
	 * @return �����뵥pk�����ӱ���Ϣ
	 * @throws BusinessException
	 */
	public Map<String,CircularlyAccessibleValueObject[]> queryByMaPKs(String[] mtapp_billPks) throws BusinessException;

}
