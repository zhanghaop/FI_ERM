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
 * 存在业务单据（费用申请单被引用），不可以维护费用申请单或费用申请单规则设置
 *
 * 费用申请单控制规则    控制维度新增前，修改前，删除前，注册插件；控制对象修改前，删除前，
 * 费用申请单取消审批前，注册插件
 *
 * <b>Date:</b>2012-11-22<br>
 * @author：wangyhh@ufida.com.cn
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
				//费用申请单，取消生效（审批）前
				if (newObjects instanceof AggMatterAppVO[]) {
					mtAppPks = VOUtils.getAttributeValues((AggMatterAppVO[]) newObjects, MatterAppVO.PK_MTAPP_BILL);
				}
			} 

			//存在业务单据不可以更改
			if (EventListenerUtil.isExistBill(mtAppPks)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0093")/*@res "费用申请单已关联业务单据，不能维护费用申请单或费用申请单规则设置"*/);
			}
		}
	}

}