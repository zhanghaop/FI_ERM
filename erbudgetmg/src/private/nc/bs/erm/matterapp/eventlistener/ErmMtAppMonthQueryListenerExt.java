package nc.bs.erm.matterapp.eventlistener;

import java.util.List;
import java.util.Map;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.ext.IErmMtAppMonthQueryServiceExt;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.pub.BusinessException;

/**
 * �������뵥-���ھ�̯��ѯ�����Ϊ���뵥VO��������ӱ���Ϣ
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 *
 */
public class ErmMtAppMonthQueryListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		
		AggMatterAppVO[] mavos = (AggMatterAppVO[]) obj.getNewObjects();
		
		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)) {
			// �޸�ǰ�����ֻ��ѯoldvos��
			mavos = (AggMatterAppVO[]) obj.getOldObjects();
			
		}
		IErmMtAppMonthQueryServiceExt qryservice = NCLocator.getInstance().lookup(IErmMtAppMonthQueryServiceExt.class);
		Map<String, List<MtappMonthExtVO>> monthmap = qryservice.queryMonthVOs(VOUtil.getAttributeValues(mavos, null));
		for (int i = 0; i < mavos.length; i++) {
			List<MtappMonthExtVO> list = monthmap.get(mavos[i].getParentVO().getPrimaryKey());
			mavos[i].setTableVO(MtappMonthExtVO.getDefaultTableName(), list==null?null:list.toArray(new MtappMonthExtVO[0]));
		}
	}

}
