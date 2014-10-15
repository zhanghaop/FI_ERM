package nc.bs.arap.bx;

import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * �����൥�ݣ���������Զ���չ�ַ�ʽʱ����Ҫʵ�ִ˽ӿڣ��ṩ�������ݵ�DAO.
 * <p>
 * <strong>�ṩ�ߣ�ARAP</strong>
 * <p>
 * <strong>ʹ���ߣ�ARAP</strong>
 * <p>
 * <strong>���״̬������</strong>
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
