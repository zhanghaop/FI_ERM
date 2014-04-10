package nc.ui.erm.common;

/**
 * @author twei
 *
 * nc.ui.arap.common.CommonModelListener
 * 
 * 实现简单的管理界面
 * 1. 定义VO		,继承CommonSuperVO    						@see LoanControlVO
 * 2. 实现卡片界面, 主要实现方法 setVo, getVo        			@see LoanControlCard
 * 3. 实现列表界面, 主要实现方法 getHeader, getHeaderColumns     @see LoanControlList
 * 4. 实现管理界面， 引用卡片列表界面      						@see LoanControlMailPanel
 * 
 * @see CommonSuperVO
 * @see CommonCard
 * @see CommonList
 * @see CommonModel
 * @see CommonModelListener
 * @see CommonUI
 */
public interface CommonModelListener {

	void updateStatus();

	void updateVos();

}
