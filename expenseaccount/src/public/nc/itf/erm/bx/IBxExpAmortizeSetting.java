package nc.itf.erm.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 报销单待摊信息设置
 * 
 * @author lvhj
 *
 */
public interface IBxExpAmortizeSetting {

	/**
	 * 报销单设置待摊
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void expAmortizeSet(JKBXVO[] vo) throws BusinessException;
	
	/**
	 * 报销单取消设置待摊
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void expAmortizeUnSet(JKBXVO[] vo) throws BusinessException;
	/**
	 * 报销单待摊设置生效
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void expAmortizeApprove(JKBXVO[] vo) throws BusinessException;
	/**
	 * 报销单待摊设置取消生效
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void expAmortizeUnApprove(JKBXVO[] vo) throws BusinessException;
	
}
