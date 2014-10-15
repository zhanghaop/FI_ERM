package nc.bs.erm.costshare.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.ext.IErmCsMonthManageServiceExt;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 费用结转单-分期均摊维护实现插件
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 *
 */
public class ErmCsMonthManageListenerExt implements IBusinessListener {
	

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		
		// 只处理 新增后、修改后、删除前的事件
		AggCostShareVO[] newvos = (AggCostShareVO[]) obj.getNewObjects();
		AggCostShareVO[] oldvos = (AggCostShareVO[]) obj.getOldObjects();
		
		if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){
			// 删除情况，删除的数据作为oldvo使用
			oldvos = newvos;
			newvos = null;
		}
		
		// 区分经销商垫付报销单的分摊，与其他报销单的分摊情况
		List<AggCostShareVO> oldmonthsharelist = new ArrayList<AggCostShareVO>();// 经销商垫付报销单分摊
		if(oldvos != null && oldvos.length >0){
			for (int i = 0; i < oldvos.length; i++) {
				CostShareVO parentvo = (CostShareVO) oldvos[i].getParentVO();
				if(ErmConstExt.Distributor_BX_Tradetype.equals(parentvo.getDjlxbm())){
					oldmonthsharelist.add(oldvos[i]);
				}
			}
		}
		List<AggCostShareVO> monthsharelist = new ArrayList<AggCostShareVO>();// 经销商垫付报销单分摊
		if(newvos != null && newvos.length >0){
			for (int i = 0; i < newvos.length; i++) {
				CostShareVO parentvo = (CostShareVO) newvos[i].getParentVO();
				if(ErmConstExt.Distributor_BX_Tradetype.equals(parentvo.getDjlxbm())){
					monthsharelist.add(newvos[i]);
				}
			}
		}
		// 生成跨月均摊记录
		if(!oldmonthsharelist.isEmpty()||!monthsharelist.isEmpty()){
			IErmCsMonthManageServiceExt service = NCLocator.getInstance().lookup(IErmCsMonthManageServiceExt.class);
			service.generateMonthVos(monthsharelist.toArray(new AggCostShareVO[0]),
					oldmonthsharelist.toArray(new AggCostShareVO[0]));
		}
	}

}
