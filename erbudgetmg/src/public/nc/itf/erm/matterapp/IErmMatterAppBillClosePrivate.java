package nc.itf.erm.matterapp;

import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 费用申请单对内关闭服务，后台任务执行使用，不对外提供
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillClosePrivate extends IErmMatterAppBillClose{
	
	/**
	 * 后台系统用户-自动关闭单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMatterAppVO[] closeVOsBackgroud(AggMatterAppVO[] vos) throws BusinessException;

}
