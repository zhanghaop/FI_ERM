package nc.pubitf.erm.expamortize;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * ���ô�̯̯������
 * 
 * @author lvhj
 *
 */
public interface IExpAmortize {
	
	/**
	 * ����̯��
	 * 
	 * @param pk_org ��֯pk
	 * @param currYearMonth ��ǰ�������
	 * @param vos ̯����Ϣ
	 * @throws BusinessException
	 */
	public MessageVO[] amortize(String pk_org,String currYearMonth, ExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * ����̯������������
	 * @param pk_org ��֯pk
	 * @param currYearMonth ��ǰ�������
	 * @param vo ̯����Ϣ
	 * @throws BusinessException
	 */
	public MessageVO amortize_RequiresNew(String pk_org,String currYearMonth,ExpamtinfoVO vo) throws BusinessException;

}
