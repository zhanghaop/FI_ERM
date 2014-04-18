package nc.itf.erm.matterapp.ext;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;


/**
 * 
 * 费用申请单分期均摊记录维护服务
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 * 
 */
public interface IErmMtAppMonthManageServiceExt {

	/**
	 * 根据费用申请单，生成分期均摊信息
	 * 
	 * @param vos
	 * @param oldvos
	 * @throws BusinessException
	 */
	public void generateMonthVos(AggMatterAppVO[] vos,AggMatterAppVO[] oldvos) throws BusinessException;
}
