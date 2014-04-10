package nc.ui.arap.bx;

import java.util.EventObject;

import nc.ui.pub.bill.BillScrollPane;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.pub.BusinessException;
/**
 *  �����൥�ݣ���Ҫʵ�ִ˽ӿڣ���Ϊ�ṩ����UI Controller.
 *  �����൥�ݱ�������Զ���չ�ַ�ʽʱ,��Ҫʵ�ֽӿ��е�ÿ������.
 *  �����൥�ݱ���ʹ��ģ�巽ʽʱ:����Ҫʵ�֡�public UIPanel getBodyUIPanel()������.
 *  
* <p> 
* <strong>�ṩ�ߣ�ARAP</strong> 
* <p> 
* <strong>ʹ���ߣ�ARAP</strong>
* <p> 
* <strong>���״̬������</strong>
* <p> 
* @version V1.0
* @author ROCKING
 */
public interface IBodyUIController {
	
	/**
	 * ע��ڵ���ڻ���.
	* <p>	
	* <strong>����ģ�飺</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-29</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void setContainer(BaseUIPanel container);
	/**
	 * 	 
	* ���ؽ����൥�ݱ�������Զ���չ�ַ�ʽ�� UI Container. 	
	* <p>	
	* <strong>����ģ�飺Arap</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	 * @param tableCode 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public BillScrollPane getBodyUIPanel(String tableCode);
	/**
	 * 	 
	* UI��ʼ������,���������ϲ�ڵ������򿪽ڵ�ʱ����.	
	* <p>	
	* <strong>����ģ�飺ARAP</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void initBodyUIPanel(BXBusItemVO[] vo);
	/**
	 * 	 
	*  ����ǰ���,���������ϲ�ڵ�����ౣ�浥��ʱ����. 	
	* <p>	
	* <strong>����ģ�飺ARAP</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void saveCheck()throws BusinessException;
	/**
	 * 	 
	* ����ӿڣ����������ϲ�ڵ�����ౣ�浥��ʱ����. 	
	* <p>	
	* <strong>����ģ�飺ARAP</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	
	* @return	������Ҫ����ı���ҵ������
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public BXBusItemVO[] save();
	/**
	 * 	 
	* ʹ�õ���ģ��ʱ��ģ���ͷ�ͱ����ֶα༭,��������ɾ�����ơ����е��¼�ִ��ǰ����ӿ�,���������ϲ�ڵ������򿪽ڵ�ʱ����.
	* �ǵ���ģ��ʱ�� ģ���ͷ�ֶα༭ǰ����ӿڡ�
	* 
	* <p>	
	* <strong>����ģ�飺ARAP</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void beferCardPanelEdit( EventObject e);
	/**
	 * 	 
	* ʹ�õ���ģ��ʱ��ģ���ͷ�ͱ����ֶα༭,��������ɾ�����ơ����е�ִ�к���ӿ�,���������ϲ�ڵ������򿪽ڵ�ʱ����.	
	* �ǵ���ģ��ʱ�� ģ���ͷ�ֶα༭ǰ����ӿڡ�
	* <p>	
	* <strong>����ģ�飺ARAP</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void afterCardPanelEdit( EventObject e);
	
 

 
}
