package nc.ui.arap.bx;

import nc.ui.pub.print.IDataSource;
import nc.vo.ep.bx.JKBXVO;
/**
 * 
* 
* �����൥�ݣ���������Զ���չ�ַ�ʽʱ����Ҫʵ�ִ˽ӿڣ��ṩ����Ĵ�ӡ����.
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
public interface IBodyPrint extends IDataSource{
	/**
	 * 	 
	* ע�����ӡ������. 	
	* <p>	
	* <strong>����ģ�飺fipub</strong>	 
	* 
	* <p>	 
	* <strong>����޸��ˣ�rocking</strong>	 
	* <p>	 
	* <strong>����޸���ǰ��2007-5-24</strong>	 
	* <p>	 
	* <strong>����������</strong>	 
	* <p>	 
	* @param	�����൥��VO
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void setVO (JKBXVO vo);
	
}
