package nc.bs.erm.costshare.eventlistener;

import java.util.List;
import java.util.Map;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.ext.IErmCsMonthQueryServiceExt;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת��-���ھ�̯��ѯ�����Ϊ���뵥VO��������ӱ���Ϣ
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 *
 */
public class ErmCsMonthQueryListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		
		AggCostShareVO[] mavos = (AggCostShareVO[]) obj.getNewObjects();
		
		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)) {
			// �޸�ǰ�����ֻ��ѯoldvos��
			mavos = (AggCostShareVO[]) obj.getOldObjects();
			
		}
		IErmCsMonthQueryServiceExt qryservice = NCLocator.getInstance().lookup(IErmCsMonthQueryServiceExt.class);
		Map<String, List<CShareMonthVO>> monthmap = qryservice.queryMonthVOs(VOUtil.getAttributeValues(mavos, null));
		for (int i = 0; i < mavos.length; i++) {
			List<CShareMonthVO> list = monthmap.get(mavos[i].getParentVO().getPrimaryKey());
			mavos[i].setTableVO(CShareMonthVO.getDefaultTableName(), list==null?null:list.toArray(new CShareMonthVO[0]));
		}
	}

}
