package nc.bs.er.control;

import java.util.List;

import nc.bs.arap.loancontrol.LoanControlMode;
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
 * ½è¿î¿ØÖÆBO
 */
public class JkControlBO {

	public String checkBefSave(LoanControlVO defvo, IFYControl[] items) throws BusinessException {

		if (defvo == null || items == null || items.length == 0) {
			return null;
		}

		List<LoanControlModeVO> modevos = defvo.getModevos();

		StringBuffer warnmsg=new StringBuffer();
		for (LoanControlModeVO modevo : modevos) {
			LoanControlMode mode = null;
			try {
				mode = (LoanControlMode) Class.forName(modevo.getDefvo().getImpclass()).newInstance();
			} catch (Exception e) {
				ExceptionHandler.handleException(this.getClass(), e);
			}
			String controlMsg = mode.control(defvo, modevo, items);
			
			if(controlMsg!=null){
				if(defvo.getControlstyle()==1){//¿ØÖÆ
					ExceptionHandler.cteateandthrowException(controlMsg);
				}else{
					warnmsg.append(controlMsg+"\n");
				}
			}
		}

		if(warnmsg.length()!=0)
			return warnmsg.toString();
		
		return null;
	}

}
