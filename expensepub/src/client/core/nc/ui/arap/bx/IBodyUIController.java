package nc.ui.arap.bx;

import java.util.EventObject;

import nc.ui.pub.bill.BillScrollPane;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.pub.BusinessException;
/**
 *  借款报销类单据，需要实现此接口，作为提供表体UI Controller.
 *  借款报销类单据表体采用自定义展现方式时,需要实现接口中的每个方法.
 *  借款报销类单据表体使用模板方式时:不需要实现“public UIPanel getBodyUIPanel()”方法.
 *  
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
public interface IBodyUIController {
	
	/**
	 * 注入节点入口环境.
	* <p>	
	* <strong>调用模块：</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-29</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
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
	* 返回借款报销类单据表体采用自定义展现方式的 UI Container. 	
	* <p>	
	* <strong>调用模块：Arap</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
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
	* UI初始化方法,本方法由上层节点入口类打开节点时调用.	
	* <p>	
	* <strong>调用模块：ARAP</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
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
	*  保存前检查,本方法由上层节点入口类保存单据时调用. 	
	* <p>	
	* <strong>调用模块：ARAP</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
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
	* 保存接口，本方法由上层节点入口类保存单据时调用. 	
	* <p>	
	* <strong>调用模块：ARAP</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
	* <p>	 
	* @param	
	* @return	本次需要保存的表体业务数据
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public BXBusItemVO[] save();
	/**
	 * 	 
	* 使用单据模板时，模板表头和表体字段编辑,表体增、删、复制、换行等事件执行前处理接口,本方法由上层节点入口类打开节点时调用.
	* 非单据模板时， 模板表头字段编辑前处理接口。
	* 
	* <p>	
	* <strong>调用模块：ARAP</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
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
	* 使用单据模板时，模板表头和表体字段编辑,表体增、删、复制、换行等执行后处理接口,本方法由上层节点入口类打开节点时调用.	
	* 非单据模板时， 模板表头字段编辑前处理接口。
	* <p>	
	* <strong>调用模块：ARAP</strong>	 
	* 
	* <p>	 
	* <strong>最后修改人：rocking</strong>	 
	* <p>	 
	* <strong>最后修改日前：2007-5-24</strong>	 
	* <p>	 
	* <strong>用例描述：</strong>	 
	* <p>	 
	* @param	
	* @return	
	* @throws	 
	* @since  NCV5.011	
	* @see
	 */
	public void afterCardPanelEdit( EventObject e);
	
 

 
}
