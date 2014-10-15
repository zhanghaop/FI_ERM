package nc.itf.erm.matterapp.ext;

import java.util.List;
import java.util.Map;

import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * �������뵥���ھ�̯��¼��ѯ����
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 * 
 */
public interface IErmMtAppMonthQueryServiceExt {
	
	/**
	 * ���ݷ������뵥PK����ѯ���ھ�̯��Ϣ
	 * 
	 * @param pk_mtapp_bill
	 * @throws BusinessException
	 */
	public MtappMonthExtVO[] queryMonthVOs(String pk_mtapp_bill) throws BusinessException;
	
	/**
	 * ���ݷ������뵥PKS��������ѯ���ھ�̯��Ϣ
	 * 
	 * @param mtapp_billPks
	 * @throws BusinessException
	 */
	public Map<String,List<MtappMonthExtVO>> queryMonthVOs(String[] mtapp_billPks) throws BusinessException;

}
