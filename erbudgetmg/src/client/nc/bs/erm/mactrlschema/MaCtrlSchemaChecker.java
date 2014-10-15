package nc.bs.erm.mactrlschema;

import java.util.List;

import nc.bs.erm.eventlistener.EventListenerUtil;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

import org.apache.commons.lang.ArrayUtils;

public class MaCtrlSchemaChecker {

	/***
	 * 检查动作的业务合理性 eg:新增、修改、删除时，该组织下的此类费用申请单已关联业务单据则不可进行操作
	 * 
	 * @param treemodel
	 * @throws BusinessException
	 */
	public static void checkOperation(HierachicalDataAppModel treemodel) throws BusinessException {

		String mtappTradeTypePk = ((BilltypeVO) treemodel.getSelectedData()).getPk_billtypecode();
		String pk_org = treemodel.getContext().getPk_org();
		// 根据交易类型查询费用申请单
		String[] mtAppPks = null;
		String whereSql = " pk_tradetype = '" + mtappTradeTypePk + "'  and CLOSE_STATUS = " + ErmMatterAppConst.CLOSESTATUS_N;// 未关闭的申请单
		if (BXConstans.MACTRLSCHEMA_G.equals(treemodel.getContext().getNodeCode())) {
			whereSql += " and pk_group = '" + treemodel.getContext().getPk_group() + "'";
		}else{
			whereSql += " and pk_org = '" + pk_org + "'";
		}
		
		AggMatterAppVO[] aggMatterAppVOs = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
				.queryBillByWhere(whereSql);
		if (ArrayUtils.isEmpty(aggMatterAppVOs)) {
			// 该规则没有创建任何费用申请单
			return;
		}

		mtAppPks = VOUtils.getAttributeValues(aggMatterAppVOs, MatterAppVO.PK_MTAPP_BILL);

		// 存在业务单据不可以更改
		if (EventListenerUtil.isExistBill(mtAppPks)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0123")/* @res "未关闭的费用申请单已关联业务单据，不能新增控制维度" */);
		}
	}
	
	/***
	 * 检查控制对象面板行操作动作的业务合理性
	 * eg:新增、修改、删除的控制对象交易类型在该组织或者集团下的此类费用申请单已关联业务单据(JK、BX)则不可进行操作
	 * @param treemodel
	 * @throws BusinessException
	 */
	public static void checkCtrlBillOperation(HierachicalDataAppModel treemodel,List<String> ctrlBillList)throws BusinessException {
	
		if(ctrlBillList.isEmpty() || ctrlBillList.get(0) == null){
			return;
		}
		String pk_group_org = treemodel.getContext().getPk_org();
		String wheresql = null;
		if (BXConstans.MACTRLSCHEMA_G.equals(treemodel.getContext().getNodeCode())) {
			wheresql = " where "
					+ SqlUtils_Pub.getInStr(JKBXHeaderVO.DJLXBM, ctrlBillList.toArray(new String[ctrlBillList.size()]))
					+ " and pk_group='" + pk_group_org + "'";
		} else {
			wheresql = " where "
					+ SqlUtils_Pub.getInStr(JKBXHeaderVO.DJLXBM, ctrlBillList.toArray(new String[ctrlBillList.size()]))
					+ " and pk_org='" + pk_group_org + "'";
		}
		
		List<JKBXHeaderVO> bxHeaderVos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByWhereSql(wheresql, BXConstans.BX_DJDL);
		List<JKBXHeaderVO> jkHeaderVos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByWhereSql(wheresql, BXConstans.JK_DJDL);
		if (!jkHeaderVos.isEmpty()) {
			bxHeaderVos.addAll(jkHeaderVos);
		}
		if (bxHeaderVos.isEmpty()) {
			return;
		}
		String mtappTradeTypePk = ((BilltypeVO) treemodel.getSelectedData()).getPk_billtypecode();
		StringBuffer err = new StringBuffer();
		for (JKBXHeaderVO jkbxvo : bxHeaderVos) {
			String pk_item = jkbxvo.getPk_item();
			if (pk_item != null) {
				AggMatterAppVO aggvo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPK(pk_item);
				// 包装错误数据
				if (aggvo != null && aggvo.getParentVO().getPk_tradetype().equals(mtappTradeTypePk)) {
					BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(jkbxvo.getDjlxbm());
					if (!err.toString().contains(billtypevo.getBilltypenameOfCurrLang())) {
						err.append("【" + billtypevo.getBilltypenameOfCurrLang() + "】");
					}
				}
			}
		}
		
		if (err.length()>0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0127", null, new String[]{err.toString()} )
										/*
										 * @res
										 * "控制对象{0}已关联申请单，不能删除或修改。"
										 */);
		}
	}

}
