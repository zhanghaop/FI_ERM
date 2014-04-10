package nc.bs.arap.bx;

import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 借款报销类单据，表体采用自定义展现方式时，需要实现此接口，提供表体数据的DAO.
 * <p>
 * <strong>提供者：ARAP</strong>
 * <p>
 * <strong>使用者：ARAP</strong>
 * <p>
 * <strong>设计状态：总体</strong>
 * <p>
 * 
 * @version V1.0
 * @author ROCKING
 */
public interface IBXBusItemBO {
	
	public BXBusItemVO[] save(BXBusItemVO[] items)throws BusinessException;
		
	public BXBusItemVO[] update(BXBusItemVO[] items)throws BusinessException;

	public void deleteVOs(BXBusItemVO[] items)throws BusinessException;
	
	public BXBusItemVO[] queryByHeaders(JKBXHeaderVO[] headerVOs)  throws BusinessException ;
	
	public void deleteByBXVOs(JKBXVO[] headers) throws BusinessException;

}
