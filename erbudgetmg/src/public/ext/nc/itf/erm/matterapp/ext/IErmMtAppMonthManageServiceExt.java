package nc.itf.erm.matterapp.ext;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;


/**
 * 
 * �������뵥���ھ�̯��¼ά������
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 * 
 */
public interface IErmMtAppMonthManageServiceExt {

	/**
	 * ���ݷ������뵥�����ɷ��ھ�̯��Ϣ
	 * 
	 * @param vos
	 * @param oldvos
	 * @throws BusinessException
	 */
	public void generateMonthVos(AggMatterAppVO[] vos,AggMatterAppVO[] oldvos) throws BusinessException;
}
