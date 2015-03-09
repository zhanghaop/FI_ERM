package nc.erm.mobile.billaction;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.erm.mobile.util.JsonData;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public class InitBillDataAction {
	public static AggregatedValueObject getBillData(String djlxbm,JsonData jsonData) throws BusinessException{
		AggregatedValueObject billvo = null;
		//新增单据  带出默认值
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
		// 初始化表头数据
		if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){ 
			//借款报销单初始化
			IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
			billvo = initservice.setBillVOtoUI(djlxVO, "", null);
			JKBXBillAddAction add = new JKBXBillAddAction();
			add.getJKBXValue(jsonData,null, billvo);
		}else if(djlxbm.startsWith("262")){
			//费用预提单初始化
			IErmAccruedBillQuery initservice = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
			billvo = initservice.getAddInitAccruedBillVO(djlxVO, "", null);
		}else if(djlxbm.startsWith("261")){
			//费用申请单初始化
			IErmMatterAppBillQuery initservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
			billvo = initservice.getAddInitAggMatterVo(djlxVO, "", null);
		}
		return billvo;
	}
}
