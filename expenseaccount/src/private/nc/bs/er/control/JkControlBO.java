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
 * 借款控制BO
 */
public class JkControlBO {

	public String checkBefSave(LoanControlVO defvo, IFYControl[] items) throws BusinessException {

		if (defvo == null || items == null || items.length == 0) {
			return null;
		}
		
		//当有人占用时，阻塞在这里，等其他锁释放后，继续进行 add by chenshuaia
		//多张单据满足一个借款控制时，会发生阻塞，如果并发量很大，会出现效率问题，大家都在排队
		//动态加锁，要整个远程请求结束后才会释放，所以要一张单据保存完后，下面的单据才可以进入下面的代码
		//这里假设并发量不是很大，动态加锁直接抛错会使本来可以保存的单据没有保存成功，并发也是有问题的
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
				if (defvo.getControlstyle() == 1) {// 控制
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
