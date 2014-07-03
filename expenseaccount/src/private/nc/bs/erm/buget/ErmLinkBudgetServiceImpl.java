package nc.bs.erm.buget;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.ntb.IErmLinkBudgetService;
import nc.itf.erm.proxy.ErmProxy;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.util.erm.costshare.ErCostBudgetUtil;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.util.AuditInfoUtil;

public class ErmLinkBudgetServiceImpl implements IErmLinkBudgetService {

	@Override
	public NtbParamVO[] getBudgetLinkParams(JKBXVO vo, String actionCode, String nodeCode) throws BusinessException {
		
		if(vo == null || actionCode == null){
			return null;
		}
		
		if ((vo.getChildrenVO() == null || vo.getChildrenVO().length == 0) && !vo.isChildrenFetched()) { // 初始化表体vo
			vo = retrieveChidren(vo);
		}
		
		if(vo.getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved){//暂存时，按照保存联查
			vo.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);
		}
		
		IFYControl[] items = null;

		// 进行费用预算联查
		if (vo.getcShareDetailVo() != null && vo.getcShareDetailVo().length != 0) {
			// 查询费用结转主vo
			AggCostShareVO[] csVos = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class)
					.queryBillByWhere(CostShareVO.SRC_ID + "='" + vo.getParentVO().getPk_jkbx() + "'");
			
			if (csVos != null) {
				for (AggCostShareVO csVo : csVos) {
					if (((CostShareVO) csVo.getParentVO()).getBillstatus() == BXStatusConst.DJZT_TempSaved) {
						((CostShareVO) csVo.getParentVO()).setBillstatus(BXStatusConst.DJZT_Saved);
					}
				}
				items = ErCostBudgetUtil.getCostControlVOByCSVO(csVos, AuditInfoUtil.getCurrentUser());
			}
		} else {
			JKBXHeaderVO[] jkbxItems = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
			// 给财务报销,保存完以后,下一个可能发生的动作可能是审核,可能产生执行数,以前跟liangsg商量的,
			// 保存完以后去联查,需要把busivo.getDataType()的值设置假设为审核动作的busivo.getDataType()的值,
			// 日期类型一样,日期类型的值设置为当前日期,就联查业务单据有这个问题,审核没有问题
			for (JKBXHeaderVO jkbxvo : jkbxItems) {
				if (jkbxvo.getShrq() == null) {
					jkbxvo.setShrq(new UFDateTime());
				}
			}
			items = jkbxItems;
		}
		
		if(items == null || items.length == 0){
			throw new BusinessException(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
			.getStrByID("expensepub_0", "02011002-1001")/*
														 * @
														 * res
														 * "当前用户没有权限查询本单据的相关预算，请检查"
														 */
			);
		}

		// 调用预算接口查询控制策略。如果返回值为空表示无控制策略，不控制。最后一个参数为false，这样就不会查找下游策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(items[0].getDjlxbm(), actionCode, false);
		
		if (ruleVos == null || ruleVos.length == 0) {
			return null;
		}
		
		YsControlVO[] controlVos =  ErBudgetUtil.getCtrlVOs(items, true, ruleVos);

		if(controlVos == null || controlVos.length == 0){
			return null;
		}
		
		return ErmProxy.getILinkQuery().getLinkDatas(controlVos);
	}
	
	private JKBXVO retrieveChidren(JKBXVO zbvo) throws BusinessException {
		try {
			JKBXVO bxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(zbvo.getParentVO());
			return bxvo;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

}
