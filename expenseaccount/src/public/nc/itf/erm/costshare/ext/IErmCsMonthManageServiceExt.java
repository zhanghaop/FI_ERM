package nc.itf.erm.costshare.ext;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;


/**
 * 
 * ���ý�ת�����ھ�̯��¼ά������
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 * 
 */
public interface IErmCsMonthManageServiceExt {

	/**
	 * ���ݷ��ý�ת�������ɷ��ھ�̯��Ϣ
	 * 
	 * @param vos
	 * @param oldvos
	 * @throws BusinessException
	 */
	public void generateMonthVos(AggCostShareVO[] vos,AggCostShareVO[] oldvos) throws BusinessException;
}
