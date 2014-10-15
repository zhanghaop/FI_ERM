package nc.impl.erm.common;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.service.IErmGroupPredataService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.pub.BusinessException;
import nc.vo.sm.createcorp.CreateOrgInfo;
import nc.vo.sm.createcorp.CreatecorpVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 费用管理产品集团初始化的预置数据处理
 * 
 * @author lvhj
 * 
 */
public class ErmGroupPredataListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		// 组织初始化前事件
		if (IArapBSEventType.TYPE_ORGIINITIALIZE_AFTER.equals(event
				.getEventType())) {
			nc.bs.businessevent.BusinessEvent evt = (nc.bs.businessevent.BusinessEvent) event;
			CreateOrgInfo info = (CreateOrgInfo) evt.getObject();
			List<String> groups = new ArrayList<String>();
			if (info != null && !ArrayUtils.isEmpty(info.getCreatecorpVOs())) {
				for (CreatecorpVO vo : info.getCreatecorpVOs()) {
					if (String.valueOf(BXConstans.ERM_MODULEID).equals(
							vo.getFunccode())) {
						groups.add(vo.getPk_org());
					}
				}
			}
			if (!groups.isEmpty()) {
				// 初始化集团预置数据
				initData(groups);
			}
		}
	}

	private void initData(List<String> groups) throws BusinessException {
		// 初始化集团预置数据
		NCLocator.getInstance().lookup(IErmGroupPredataService.class)
				.initGroupData(groups.toArray(new String[0]));

	}

}
