package nc.itf.erm.ntb;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;

/**
 * 
 * 借款报销单 预算控制、回写接口
 * 
 * @author chenshuaia
 *
 */
public interface IBXYsControlService {
	
	/**
	 * 预算业务处理
	 * @param vos 借款报销vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "借款报销单预算控制接口" /*-=notranslate=-*/,type=BusinessType.DOMAIN_INT)
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException;
	
	/**
	 * 预算控制修改
	 * @param vos 借款报销VO
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "借款报销单更新预算控制接口" /*-=notranslate=-*/,type=BusinessType.DOMAIN_INT)
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException;
}
