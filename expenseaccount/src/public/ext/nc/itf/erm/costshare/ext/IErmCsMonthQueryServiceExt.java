package nc.itf.erm.costshare.ext;

import java.util.List;
import java.util.Map;

import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * ���ý�ת�����ھ�̯��¼��ѯ����
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 * 
 */
public interface IErmCsMonthQueryServiceExt {
	
	/**
	 * ���ݷ��ý�ת��PK����ѯ���ھ�̯��Ϣ
	 * 
	 * @param pk_costshare
	 * @throws BusinessException
	 */
	public CShareMonthVO[] queryMonthVOs(String pk_costshare) throws BusinessException;
	
	/**
	 * ���ݷ��ý�ת��PKS��������ѯ���ھ�̯��Ϣ
	 * 
	 * @param costshare_billPks
	 * @throws BusinessException
	 */
	public Map<String,List<CShareMonthVO>> queryMonthVOs(String[] costshare_billPks) throws BusinessException;

}
