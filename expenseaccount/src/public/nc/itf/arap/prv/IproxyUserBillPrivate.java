package nc.itf.arap.prv;

import java.util.List;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.pub.BusinessException;

public interface IproxyUserBillPrivate {
	/**
	 * ��������û�
	 * @param sqdlrVOs
	 * @return
	 * @throws BusinessException
	 */
	public boolean saveProxyUser(SqdlrVO[] pusers) throws BusinessException ;
	
	/**
	 * �õ������û�
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public SqdlrVO[] getProxyUser(String user) throws BusinessException ;
	
	/**
	 * ɾ�������û�
	 * @param user
	 * @param pusers
	 * @return
	 */
	public boolean delProxyuser(List<SqdlrVO> pusers) throws BusinessException ;
	
}
