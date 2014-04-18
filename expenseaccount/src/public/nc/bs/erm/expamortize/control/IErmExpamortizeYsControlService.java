package nc.bs.erm.expamortize.control;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;

/**
 * 
 * 待摊信息预算控制接口
 * 
 * @author chenshuaia
 *
 */
public interface IErmExpamortizeYsControlService {
	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            待摊信息vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "摊销信息预算控制接口" /*-=notranslate=-*/,type=BusinessType.DOMAIN_INT)
	public void ysControl(AggExpamtinfoVO[] vos, boolean isContray,
			String actionCode) throws BusinessException;
}
