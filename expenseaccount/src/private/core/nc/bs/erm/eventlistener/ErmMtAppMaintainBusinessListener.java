package nc.bs.erm.eventlistener;


import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;


/**
 * ����ҵ�񵥾ݣ��������뵥�����ã���������ά���������뵥��������뵥��������
 *
 * �������뵥���ƹ���    ����ά������ǰ���޸�ǰ��ɾ��ǰ��ע���������ƶ����޸�ǰ��ɾ��ǰ��
 * �������뵥ȡ������ǰ��ע����
 *
 * <b>Date:</b>2012-11-22<br>
 * @author��wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ErmMtAppMaintainBusinessListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		if (event instanceof ErmBusinessEvent) {
			ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
			Object newObjects = ((ErmCommonUserObj) erEvent.getUserObject()).getNewObjects();
			if (newObjects == null) {
				return;
			}

			String[] mtAppPks = null;
			String eventType = erEvent.getEventType();
			if (ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
				//�������뵥��ȡ����Ч��������ǰ
				if (newObjects instanceof AggMatterAppVO[]) {
					mtAppPks = VOUtils.getAttributeValues((AggMatterAppVO[]) newObjects, MatterAppVO.PK_MTAPP_BILL);
				}
			} 

			//����ҵ�񵥾ݲ����Ը���
			if (EventListenerUtil.isExistBill(mtAppPks)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0093")/*@res "�������뵥�ѹ���ҵ�񵥾ݣ�����ά���������뵥��������뵥��������"*/);
			}
		}
	}

}