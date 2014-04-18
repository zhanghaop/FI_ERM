package nc.bs.erm.expamortize.util;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.pub.BusinessException;
/**
 * wangled
 * 
 */
public class ExpamtinfoVOChecker {
	/**
	 * ±£¥Ê–£—È
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkSave(AggExpamtinfoVO[] vos) throws BusinessException{
		for(AggExpamtinfoVO vo : vos){
			
		}
	}
	
	public void checkDelete(AggExpamtinfoVO[] vos) throws BusinessException{
		if(vos != null){
			for(AggExpamtinfoVO vo : vos){
				
			}
		}
	}
	
}
