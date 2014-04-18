package nc.impl.er.djlx;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ITranstypeBiz;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.sm.funcreg.FuncRegisterVO;

/**
 * 费用管理产品-交易类型业务处理
 * 
 * @author
 */
public class ErmITranstypeImp implements ITranstypeBiz {

	@Override
	public void saveTransType(Object userObj) throws BusinessException {
		DjLXVO djlx = (DjLXVO) ((BillTypeVO)userObj).getParentVO();
		NCLocator.getInstance().lookup(IArapBillTypePrivate.class).insertBillType(djlx);
	}

	@Override
	public void updateTransType(Object userObj) throws BusinessException {
		NCLocator.getInstance().lookup(IArapBillTypePrivate.class).updateBillType((BillTypeVO) userObj);
	}
	
	@Override
	public void deleteTransType(Object userObj) throws BusinessException {
		DjLXVO djlx = (DjLXVO) ((BillTypeVO)userObj).getParentVO();
		NCLocator.getInstance().lookup(IArapBillTypePrivate.class).deleteBillType(djlx);
	}
	
	@Override
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
	
	@Override
	public void execOnPublish(String nodecode, String newNodecode, boolean isExecFunc)throws BusinessException {
		try{
			if(isExecFunc)
				ConfigAgent.getInstance().copyNode(nodecode,newNodecode);
		}catch(Exception e){
			throw ExceptionHandler.handleException(e);
		}
	} 
}
