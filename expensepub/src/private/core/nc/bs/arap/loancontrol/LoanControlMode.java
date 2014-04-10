package nc.bs.arap.loancontrol;

import nc.vo.ep.bx.LoanControlModeVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.LoanControlMode
 * 
 * �����Ʒ�ʽ����
 */
public interface LoanControlMode {
	
	/**
	 * @param defvo    ����������VO
	 * @param modevo   �����Ʒ�ʽVO
	 * @param vo	   ��Ҫ���Ƶ�VO����
	 * @return
	 * @throws BusinessException
	 * 
	 * ���п��Ʋ���, ���Ʋ�ͨ�����ش�����Ϣ
	 */
	public String control(LoanControlVO defvo,LoanControlModeVO modevo, IFYControl[] vo) throws BusinessException;
	
}
