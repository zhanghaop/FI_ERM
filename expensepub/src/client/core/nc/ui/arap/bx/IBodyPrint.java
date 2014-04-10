package nc.ui.arap.bx;

import nc.ui.pub.print.IDataSource;
import nc.vo.ep.bx.JKBXVO;
/**
 * 
* 
* 借款报销类单据，表体采用自定义展现方式时，需要实现此接口，提供表体的打印数据.
* <p> 
* <strong>提供者：ARAP</strong> 
* <p> 
* <strong>使用者：ARAP</strong>
* <p> 
* <strong>设计状态：总体</strong>
* <p> 
* @version V1.0
* @author ROCKING
 */
public interface IBodyPrint extends IDataSource{
	/**
	 * 	 
	* 注入待打印的数据. 	
	* <p>	
	* <strong>调用模块：fipub</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
	* <p>	 
	* @param	借款报销类单据VO
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void setVO (JKBXVO vo);
	
}
