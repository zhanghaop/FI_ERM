package nc.ui.erm.common;

/**
 * @author twei
 *
 * nc.ui.arap.common.CommonModelListener
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
public interface CommonModelListener {

	void updateStatus();

	void updateVos();

}
