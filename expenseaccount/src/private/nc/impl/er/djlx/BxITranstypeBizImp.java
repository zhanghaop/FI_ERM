package nc.impl.er.djlx;

import java.util.ArrayList;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pf.ITranstypeBiz;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.sm.funcreg.FuncRegisterVO;
/**
 * 交易类型处理
 * @author 
 */
public class BxITranstypeBizImp implements ITranstypeBiz {

	public void deleteTransType(Object busitypevo) throws BusinessException {
		if(busitypevo instanceof BillTypeVO){
			NCLocator.getInstance().lookup(IArapBillTypePublic.class).deleteBillType((BillTypeVO)busitypevo);
		}

	}

	public void saveTransType(Object busitypevo) throws BusinessException {
		if(busitypevo instanceof BillTypeVO){
			NCLocator.getInstance().lookup(IArapBillTypePublic.class).insertBillType((BillTypeVO)busitypevo);
		}
	}

	public void updateTransType(Object busitypevo) throws BusinessException {
		if(busitypevo instanceof BillTypeVO){
			NCLocator.getInstance().lookup(IArapBillTypePrivate.class).updateBillType((BillTypeVO)busitypevo);
		}
	}

	public void execOnDelPublish(BilltypeVO transTypeVO, ArrayList<FuncRegisterVO> funcodes) throws BusinessException {
		try {
			String[] codes = new String[funcodes.size()];
			int length = funcodes.size();
			for(int i=0;i<length;i++){				
				codes[i] = funcodes.get(i).getFuncode();
			}
			ConfigAgent.getInstance().deleteNodes(codes);
		} catch (ConfigurationException e) { 
			throw new BusinessException(e);
		}
		
	}
	
	public void execOnPublish(String nodecode, String newNodecode, boolean isExecFunc)throws BusinessException {
		try{
			if(isExecFunc)
				ConfigAgent.getInstance().copyNode(nodecode,newNodecode);
		}catch(Exception e){
			throw ExceptionHandler.handleException(e);
		}
	} 
}

