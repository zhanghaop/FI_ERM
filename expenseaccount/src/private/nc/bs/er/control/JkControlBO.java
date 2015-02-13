package nc.bs.er.control;

import java.util.List;

import nc.bs.arap.loancontrol.LoanControlMode;
import nc.cmp.bill.util.KeyLock;
import nc.vo.ep.bx.LoanControlModeVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 * nc.bs.arap.control.JkControlBO
 * 
 * ������BO
 */
public class JkControlBO {

	public String checkBefSave(LoanControlVO defvo, IFYControl[] items) throws BusinessException {

		if (defvo == null || items == null || items.length == 0) {
			return null;
		}
		
		//������ռ��ʱ��������������������ͷź󣬼������� add by chenshuaia
		//���ŵ�������һ��������ʱ���ᷢ������������������ܴ󣬻����Ч�����⣬��Ҷ����Ŷ�
		//��̬������Ҫ����Զ�����������Ż��ͷţ�����Ҫһ�ŵ��ݱ����������ĵ��ݲſ��Խ�������Ĵ���
		//������貢�������Ǻܴ󣬶�̬����ֱ���״��ʹ�������Ա���ĵ���û�б���ɹ�������Ҳ���������
		while(KeyLock.dynamicLock(defvo.getPrimaryKey()) != null){}
		
		List<LoanControlModeVO> modevos = defvo.getModevos();

		StringBuffer warnmsg = new StringBuffer();
		for (LoanControlModeVO modevo : modevos) {
			LoanControlMode mode = null;
			String controlMsg = null;
			try {
				mode = (LoanControlMode) Class.forName(modevo.getDefvo().getImpclass()).newInstance();
				controlMsg = mode.control(defvo, modevo, items);
			} catch (Exception e) {
				ExceptionHandler.handleException(this.getClass(), e);
			}

			if (controlMsg != null) {
				if (defvo.getControlstyle() == 1) {// ����
					ExceptionHandler.cteateandthrowException(controlMsg);
				} else {
					warnmsg.append(controlMsg + "\n");
				}
			}
		}

		if (warnmsg.length() != 0) {
			return warnmsg.toString();
		}
		return null;
	}
}
