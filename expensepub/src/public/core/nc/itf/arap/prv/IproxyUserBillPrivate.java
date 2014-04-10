package nc.itf.arap.prv;

import java.util.List;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.pub.BusinessException;

public interface IproxyUserBillPrivate {
	/**
	 * 保存代理用户
	 * @param sqdlrVOs
	 * @return
	 * @throws BusinessException
	 */
	public boolean saveProxyUser(SqdlrVO[] pusers) throws BusinessException ;
	
	/**
	 * 得到代理用户
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public SqdlrVO[] getProxyUser(String user) throws BusinessException ;
	
	/**
	 * 删除代理用户
	 * @param user
	 * @param pusers
	 * @return
	 */
	public boolean delProxyuser(List<SqdlrVO> pusers) throws BusinessException ;
	
}
