package nc.ui.erm.bx.ext.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.ext.IErmCsMonthQueryServiceExt;
import nc.itf.erm.proxy.ErmProxy;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.ui.erm.billpub.action.LinkYsAction;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.erm.costshare.ext.CostShareYsControlVOExt;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

@SuppressWarnings("restriction")
public class BXLinkYsActionExt extends LinkYsAction {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		
		JKBXVO bxvo = (JKBXVO) getModel().getSelectedData();
		
		if(ErmConstExt.Distributor_BX_Tradetype.equals(bxvo.getParentVO().getDjlxbm())){
			// 经销商垫付报销单分期、分摊联查预算
			cxx1BxlinkYs();
		}else{
			super.doAction(e);
		}

	}

	private void cxx1BxlinkYs() throws BusinessShowException, BusinessException {
		JKBXVO bxvo = (JKBXVO) getModel().getSelectedData();

		if (bxvo == null || bxvo.getParentVO().getPk_jkbx() == null)
			return;

		boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused) {
			throw new BusinessShowException(getNoInstallMsg());
		}

		String actionCode = getActionCode(bxvo);
		try {
			NtbParamVO[] vos = getBudgetLinkParams(bxvo, actionCode, getEditor().getModel().getContext().getNodeCode());

			if (null == vos || vos.length == 0) {
				throw new BusinessShowException(getNoResultMsg());
			}
			
			NtbParamVOChooser chooser = new NtbParamVOChooser(getEditor(), nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"2006030102", "UPP2006030102-000430")/**
					 * @res "预算执行情况"
					 */
			);
			chooser.setParamVOs(vos);
			chooser.showModal();
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);
		}
	}
	
	/**
	 * 查询获得联查预算对象
	 * 
	 * @param vo
	 * @param actionCode
	 * @param nodeCode
	 * @return
	 * @throws BusinessException
	 */
	private NtbParamVO[] getBudgetLinkParams(JKBXVO vo, String actionCode, String nodeCode) throws BusinessException {
		
		if(vo == null || actionCode == null){
			return null;
		}
	
		// 查询费用结转主vo
		AggCostShareVO[] csVo = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class)
				.queryBillByWhere(CostShareVO.SRC_ID + "='" + vo.getParentVO().getPk_jkbx() + "'");
		if(csVo == null || csVo.length == 0){
			return null;
		}
		IFYControl[] items = getCsAppYsControlVOs(csVo[0]);
		
		// 调用预算接口查询控制策略。如果返回值为空表示无控制策略，不控制。最后一个参数为false，这样就不会查找下游策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(items[0].getDjlxbm(), actionCode, false);
		
		if (ruleVos == null || ruleVos.length == 0) {
			return null;
		}
		List<FiBillAccessableBusiVOProxy> voProxys = new ArrayList<FiBillAccessableBusiVOProxy>();
		
		YsControlVO[] controlVos =  ErBudgetUtil.getCtrlVOs(items, true, ruleVos);

		if (controlVos != null) {
			for (YsControlVO controlVo : controlVos) {
				voProxys.add(getFiBillAccessableBusiVOProxy(controlVo, controlVo.getParentBillType()));
			}
		}
		return ErmProxy.getILinkQuery().getLinkDatas(voProxys.toArray(new FiBillAccessableBusiVOProxy[] {}));
	}
	
	private FiBillAccessableBusiVOProxy getFiBillAccessableBusiVOProxy(FiBillAccessableBusiVO vo, String parentBillType) {
		FiBillAccessableBusiVOProxy voProxy;
		voProxy = new FiBillAccessableBusiVOProxy(vo);
		return voProxy;
	}
	
	/**
	 * 根据费用结转单包装预算控制vos，分期分摊占用预算
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getCsAppYsControlVOs(AggCostShareVO vo) throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		
		CostShareVO headvo = (CostShareVO) vo.getParentVO();
		// 查询monthvo
		IErmCsMonthQueryServiceExt qryservice = NCLocator.getInstance().lookup(IErmCsMonthQueryServiceExt.class);
		CShareMonthVO[] monthvos = qryservice.queryMonthVOs(vo.getParentVO().getPrimaryKey());
		// hash化明细行vo，备用
		Map<String, CShareDetailVO> detailvoMap = new HashMap<String, CShareDetailVO>();
		CircularlyAccessibleValueObject[] dtailvos = vo.getChildrenVO();
		for (int j = 0; j < dtailvos.length; j++) {
			detailvoMap.put(dtailvos[j].getPrimaryKey(), (CShareDetailVO) dtailvos[j]);
		}
		
		for (int j = 0; j < monthvos.length; j++) {
			// 遍历分期均摊记录，包装回写预算vo
			CShareMonthVO monthvo = monthvos[j];
			CostShareYsControlVOExt controlvo = new CostShareYsControlVOExt(headvo, detailvoMap.get(monthvo.getPk_cshare_detail()));
			if (controlvo.isYSControlAble()) {
				controlvo.setYsDate(monthvo.getBilldate());
				controlvo.setYsAmount(new UFDouble[]{monthvo.getOrig_amount(),monthvo.getOrg_amount(),monthvo.getGroup_amount(),monthvo.getGlobal_amount()});
				list.add(controlvo);
			}
		}
		
		return list.toArray(new IFYControl[list.size()]);
	}


	private String getActionCode(JKBXVO bxvo) {
		JKBXHeaderVO headVO = bxvo.getParentVO();
		int billStatus = headVO.getDjzt();
		switch (billStatus) {
			case BXStatusConst.DJZT_Sign:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			case BXStatusConst.DJZT_Verified:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			default:
				return BXConstans.ERM_NTB_SAVE_KEY;
		}
	}

	
}
