package nc.ui.erm.common;

import nc.ui.pub.beans.UIPanel;
import nc.vo.erm.common.CommonSuperVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.ui.arap.common.CommonCard
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
public abstract class CommonCard extends UIPanel{

	public abstract  void initUI();
	public abstract void setParentUI( CommonUI ui);
	public abstract CommonUI getParentUI();
	public abstract void setVO(SuperVO vo);
	public abstract SuperVO getVO() throws BusinessException;
	public abstract void setEditStatus(boolean b);
	

}
