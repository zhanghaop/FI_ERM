package nc.erm.mobile.billaction;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.erm.mobile.util.JsonData;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class BillInitDataAction {
	public static JSONObject getBillInitData(String djlxbm,JsonData jsonData) throws JSONException, Exception{
		JSONObject retJson = new JSONObject();
		AggregatedValueObject billvo = null;
		//新增单据  带出默认值
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
		if (djlxVO == null || djlxVO.getFcbz().booleanValue()) {
			throw new BusinessException("该节点单据类型已被封存，不可操作节点！");
		}
		// 初始化表头数据
		if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){ 
			//借款报销单初始化
			IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
			billvo = initservice.setBillVOtoUI(djlxVO, "", null);
			JKBXBillAddAction add = new JKBXBillAddAction();
			add.getJKBXValue(jsonData,null, billvo);
			retJson = jsonData.transBillValueObjectToJson(billvo);
			retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total"));
			retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total_name"));
		}else if(djlxbm.startsWith("262")){
			//费用预提单初始化
			IErmAccruedBillQuery initservice = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
			billvo = initservice.getAddInitAccruedBillVO(djlxVO, "", null);
			retJson = jsonData.transBillValueObjectToJson(billvo);
			retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("org_amount"));
			retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("org_amount"));
		}else if(djlxbm.startsWith("261")){
			//费用申请单初始化
			IErmMatterAppBillQuery initservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
			billvo = initservice.getAddInitAggMatterVo(djlxVO, "", null);
			setMADefaultValue((AggMatterAppVO)billvo);
			retJson = jsonData.transBillValueObjectToJson(billvo);
			retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("org_amount"));
			retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("org_amount_name"));
		}
		
		//新增时将itemlist赋值到默认值item中
		JSONArray itemsarray = (JSONArray) retJson.get("itemlist");
		if(itemsarray.length() > 0){
			retJson.put("item", itemsarray.get(0));
		}
		retJson.remove("itemlist");
		retJson.put("itemnum", 0);
		retJson.put("djlxbm", djlxbm);
		return retJson;
	}
	
	private static void setMADefaultValue(AggMatterAppVO mavo) {
		MatterAppVO parent = mavo.getParentVO();
		MtAppDetailVO detailvo = new MtAppDetailVO();
		detailvo.setAttributeValue(MtAppDetailVO.PK_GROUP, parent.getAttributeValue(MatterAppVO.PK_GROUP));
		detailvo.setAttributeValue(MtAppDetailVO.PK_ORG, parent.getAttributeValue(MatterAppVO.PK_ORG));
		detailvo.setAttributeValue(MtAppDetailVO.ASSUME_ORG, parent.getAttributeValue(MatterAppVO.PK_ORG));
		detailvo.setAttributeValue(MtAppDetailVO.ASSUME_DEPT, parent.getAttributeValue(MatterAppVO.ASSUME_DEPT));
		detailvo.setAttributeValue(MtAppDetailVO.PK_CURRTYPE, parent.getAttributeValue(MatterAppVO.PK_CURRTYPE));
		
		//汇率
		detailvo.setAttributeValue(MtAppDetailVO.ORG_CURRINFO, parent.getAttributeValue(MatterAppVO.ORG_CURRINFO));
		detailvo.setAttributeValue(MtAppDetailVO.GROUP_CURRINFO, parent.getAttributeValue(MatterAppVO.GROUP_CURRINFO));
		detailvo.setAttributeValue(MtAppDetailVO.GLOBAL_CURRINFO, parent.getAttributeValue(MatterAppVO.GLOBAL_CURRINFO));
		
		detailvo.setAttributeValue(MtAppDetailVO.ORIG_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.ORG_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.GROUP_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.GLOBAL_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.EXE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.ORG_EXE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.GROUP_EXE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.GLOBAL_EXE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.PRE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.ORG_PRE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.GROUP_PRE_AMOUNT, UFDouble.ZERO_DBL);
		detailvo.setAttributeValue(MtAppDetailVO.GLOBAL_PRE_AMOUNT, UFDouble.ZERO_DBL);
		
		setCostCenter(detailvo);
		mavo.setChildrenVO(new MtAppDetailVO[]{detailvo});
	}
	/**
	 * 根据承担部门带出成本中心
	 * 
	 * @param rowNum
	 * @param cardPanel
	 */
	public static void setCostCenter(MtAppDetailVO detailvo) {
		Object dept = detailvo.getAttributeValue(MtAppDetailVO.ASSUME_DEPT);
		
		//部门为空时，利润中心相关值清空
		if (dept == null){
			detailvo.setAttributeValue(MtAppDetailVO.PK_PCORG + "_ID", null);
			detailvo.setAttributeValue(MtAppDetailVO.PK_RESACOSTCENTER + "_ID", null);
			detailvo.setAttributeValue(MtAppDetailVO.PK_CHECKELE + "_ID", null);
			return;
		}

		String pk_dept = dept.toString();
		String pk_pcorg = null;
		String pk_costcenter = null;
		CostCenterVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
					.queryCostCenterVOByDept(new String[] { pk_dept });
		} catch (BusinessException e) {
			return;
		}
		if (vos != null) {
			for (CostCenterVO costCenterVO : vos) {
				pk_costcenter = costCenterVO.getPk_costcenter();
				pk_pcorg = costCenterVO.getPk_profitcenter();
				break;
			}
		}
		if (pk_pcorg == null) {
			detailvo.setAttributeValue(MtAppDetailVO.PK_PCORG + "_ID", null);
			detailvo.setAttributeValue(MtAppDetailVO.PK_RESACOSTCENTER + "_ID", null);
			detailvo.setAttributeValue(MtAppDetailVO.PK_CHECKELE + "_ID", null);
			return;
		}

//		BillItem pcorgBodyItem = cardPanel.getBodyItem(ErmMatterAppConst.MatterApp_MDCODE_DETAIL,
//				MtAppDetailVO.PK_PCORG);
//		
//		UIRefPane pcorgRefPane = (UIRefPane) pcorgBodyItem.getComponent();
		detailvo.setAttributeValue(MtAppDetailVO.PK_PCORG + "_ID", pk_pcorg);

//		AbstractRefModel model = (AbstractRefModel) pcorgRefPane.getRefModel();
//		model.setMatchPkWithWherePart(true);
//		@SuppressWarnings("rawtypes")
//		Vector vec = model.matchPkData(pk_pcorg);
//		if (vec == null || vec.isEmpty()) {
//			detailvo.setAttributeValue(MtAppDetailVO.PK_PCORG + "_ID", null);
//			detailvo.setAttributeValue(MtAppDetailVO.PK_RESACOSTCENTER + "_ID", null);
//			detailvo.setAttributeValue(MtAppDetailVO.PK_CHECKELE + "_ID", null);
//			return;// 利润中心为空的情况下，不再设置成本中心值
//		}

		detailvo.setAttributeValue(MtAppDetailVO.PK_RESACOSTCENTER + "_ID", pk_costcenter);
	}
}
