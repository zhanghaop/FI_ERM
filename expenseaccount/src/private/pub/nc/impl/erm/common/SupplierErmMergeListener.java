package nc.impl.erm.common;

import nc.bs.bd.businessevent.MergeBusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.service.IErmMergeService;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;

/**
 * <p>
 * ��������ͻ��ϲ������� ��Ҫ�����¼�ע����ƣ�ע�᷽ʽͨ��ϵͳ ҵ����ע��-��̬��ҵ��ģƽ̨-��Ӧ�̻�����Ϣ-�ϲ�ǰ
 * �˴���Ҫע�⣺(1)ע���Ӧ�ļ����࣬����ע����ִ��˳��ţ��˴��ǰ�����Ҫ�������ִ�еģ�
 * Ҳ����˵������������Ʒ��װ��ʱ��ע�������Ҫ����ͨuapʵ�֡�
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-9-2 ����04:54:44
 */
public class SupplierErmMergeListener implements IBusinessListener {

	public void doAction(IBusinessEvent event) throws BusinessException {

		MergeBusinessEvent mergeEvent = (MergeBusinessEvent) event;
		SuperVO source = mergeEvent.getSourceVO();
		SuperVO target = mergeEvent.getTargetVO();
		String sourcesup = source.getPrimaryKey();
		String targetsup = target.getPrimaryKey();

		try {
			NCLocator.getInstance().lookup(IErmMergeService.class).mergeSupplier(targetsup, sourcesup);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}
}
