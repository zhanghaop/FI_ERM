package nc.itf.erm.costshare.ext;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;


/**
 * 
 * 费用结转单分期均摊记录维护服务
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 * 
 */
public interface IErmCsMonthManageServiceExt {

	/**
	 * 根据费用结转单，生成分期均摊信息
	 * 
	 * @param vos
	 * @param oldvos
	 * @throws BusinessException
	 */
	public void generateMonthVos(AggCostShareVO[] vos,AggCostShareVO[] oldvos) throws BusinessException;
}
