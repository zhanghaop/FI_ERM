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
 * ʵ�ּ򵥵Ĺ������
 * 1. ����VO		,�̳�CommonSuperVO    						@see LoanControlVO
 * 2. ʵ�ֿ�Ƭ����, ��Ҫʵ�ַ��� setVo, getVo        			@see LoanControlCard
 * 3. ʵ���б����, ��Ҫʵ�ַ��� getHeader, getHeaderColumns     @see LoanControlList
 * 4. ʵ�ֹ�����棬 ���ÿ�Ƭ�б����      						@see LoanControlMailPanel
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
