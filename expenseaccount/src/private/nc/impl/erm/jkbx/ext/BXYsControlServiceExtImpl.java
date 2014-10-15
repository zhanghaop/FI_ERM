package nc.impl.erm.jkbx.ext;

import java.util.List;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.ext.common.BXFromMaHelper;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.jkbx.ext.IBXYsControlServiceExt;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.jkbx.ext.BXMaFYControlVOExt;
import nc.vo.erm.jkbx.ext.BXMaYsControlVOExt;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;

public class BXYsControlServiceExtImpl implements IBXYsControlServiceExt {
	@Override
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		// 每次都是一个报销单进行处理
		JKBXVO vo = vos[0];
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		// 逆向操作时，预警是否刚性控制
		boolean isIncludeSuper = true;
		// 是否执行上游函数
		boolean isExitParent = true;
		
		JKBXHeaderVO parentVO = vo.getParentVO();
		// 查询报销单控制策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(parentVO.getDjlxbm(), actionCode, isExitParent);
		if(ruleVos == null || ruleVos.length ==0 ) {
			return ;
		}

		// 根据报销单+申请单，包装超申请回写预算数据结构
		List<BXMaFYControlVOExt>[] bxFyControlvos = getBxYsControlVOs(
				vo, ruleVos,actionCode,isContray);
		
		BXMaYsControlVOExt[] controlVos = BXFromMaHelper.getCtrlVOs(bxFyControlvos[0].toArray(new BXMaFYControlVOExt[0]),
				bxFyControlvos[1].toArray(new BXMaFYControlVOExt[0]), isContray, ruleVos);


		// 预算控制
		if (controlVos!= null && controlVos.length > 0) {
			YsControlBO ysControlBO = new YsControlBO();
			if (isContray && !isIncludeSuper) {// 仅本身单据预算控制，则反向动作不控制
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			} else if (isContray && isIncludeSuper) {
				ysControlBO.setYsControlType(ErmConst.YsControlType_AlarmNOCHECK_CONTROL);
			}

			String warnMsg = ysControlBO.budgetCtrl(controlVos,
					vos[0].getHasNtbCheck());
			if (warnMsg != null) {
				// 返回控制信息
				vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg : warnMsg + "," + vo.getWarningMsg());
			}
		}
		
	}

	/**
	 * 查询报销单执行记录，包装fyvo
	 * 
	 * @param vo
	 * @param ruleVos
	 * @param actionCode
	 * @param isContray
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private List<BXMaFYControlVOExt>[] getBxYsControlVOs(JKBXVO vo, DataRuleVO[] ruleVos, String actionCode,boolean isContray) throws BusinessException {

		JKBXHeaderVO parentVO = vo.getParentVO();
		
		MtapppfVO[] pfVos = null;
		MtapppfVO[] oldpfVos = null;
		
		if(actionCode == BXConstans.ERM_NTB_SAVE_KEY && isContray){
			// 删除时，直接删除原执行记录
			pfVos = vo.getMaPfVos();
		}else{
			// 查询报销单在申请单上的执行记录超申请金额,报销单是整单回写所以detailbusipk也是主表pk
			pfVos = NCLocator.getInstance().lookup(IMtapppfVOQryService.class)
				.queryMtapppfVoByBusiDetailPk(new String[]{parentVO.getPrimaryKey()});
			oldpfVos = vo.getMaPfVos();
		}
		
		AggMatterAppVO mavo = BXFromMaHelper.getAggMaVOByBXPf(parentVO.getPk_item(),pfVos);
		AggMatterAppVO oldmavo = BXFromMaHelper.getAggMaVOByBXPf(parentVO.getPk_item(),oldpfVos);
		
		List<BXMaFYControlVOExt> fyvolist = BXFromMaHelper.getBxYsControlVOsBYAggMaVO(parentVO, mavo);
		List<BXMaFYControlVOExt> contray_fyvolist = BXFromMaHelper.getBxYsControlVOsBYAggMaVO(parentVO, oldmavo);
		
		return new List[]{fyvolist,contray_fyvolist};
	}

	


	@Override
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		final String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// 每次都是一个报销单进行处理
		JKBXVO vo = vos[0];
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		// 是否执行上游函数
		boolean isExitParent = true;
		
		JKBXHeaderVO parentVO = vo.getParentVO();
		// 查询报销单控制策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(parentVO.getDjlxbm(), actionCode, isExitParent);
		if(ruleVos == null || ruleVos.length ==0 ) {
			return ;
		}

		// 根据报销单+申请单，包装超申请回写预算数据结构
		List<BXMaFYControlVOExt>[] bxFyControlvos = getBxYsControlVOs(
				vo, ruleVos,actionCode,false);
		
		BXMaYsControlVOExt[] controlVos = BXFromMaHelper.getEditControlVOs(bxFyControlvos[0].toArray(new BXMaFYControlVOExt[0]),
				bxFyControlvos[1].toArray(new BXMaFYControlVOExt[0]), ruleVos);

		// 预算控制
		if (controlVos!= null && controlVos.length > 0) {
			YsControlBO ysControlBO = new YsControlBO();

			String warnMsg = ysControlBO.budgetCtrl(controlVos,
					vos[0].getHasNtbCheck());
			if (warnMsg != null) {
				// 返回控制信息
				vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg : warnMsg + "," + vo.getWarningMsg());
			}
		}
	}
	
}
