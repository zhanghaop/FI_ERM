package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.ILinkQuery;
import nc.ui.erm.costshare.ui.CostShareModelService;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BillManageModel;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CostShareYsControlVOExt;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

/**
 * @author luolch
 * 
 *         联查预算
 * 
 */
@SuppressWarnings({ "serial", "restriction" })
public class LinkBudgetAction extends NCAction {
	private CostShareModelService modelService;
	private BillManageModel model;

	public LinkBudgetAction() {
		super();
		setCode(ErmActionConst.LINKBUDGET);
		setBtnName(ErmActionConst.getLinkBudgetName());
	}

	public void doAction(ActionEvent e) throws Exception {
		linkYs();
	}

	/**
	 * 联查预算执行情况
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public void linkYs() throws BusinessException {

		AggCostShareVO selectvo = (AggCostShareVO) getModel().getSelectedData();
		if (selectvo == null)
			return;
		boolean istbbused = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0014")/* @res "没有安装预算产品，不能联查预算执行情况！" */);
		}

		String actionCode = getActionCode((CostShareVO) selectvo.getParentVO());

		if (actionCode == null) {
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;
			((CostShareVO) selectvo.getParentVO()).setBillstatus(BXStatusConst.DJZT_Saved);
			// throw new
			// BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
			// "0201212-0015")/* @res "没有符合条件的预算数据!" */);
		}

		// 调用预算接口查询控制策略。如果返回值为空表示无控制策略，不控制。最后一个参数为false，这样就不会查找下游策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(((CostShareVO) selectvo.getParentVO()).getPk_tradetype(), actionCode, false);
		
		YsControlVO[] controlVos = null;
		if (ruleVos != null && ruleVos.length > 0) {
			List<IFYControl> item = null;
			CostShareVO headvo = (CostShareVO) selectvo.getParentVO();
			CircularlyAccessibleValueObject[] dtailvos = selectvo.getChildrenVO();
			if (dtailvos != null) {
				boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(headvo.getPk_group(), headvo.getDjlxbm(),
						ErmDjlxConst.BXTYPE_ADJUST);

				item = new ArrayList<IFYControl>();
				for (int j = 0; j < dtailvos.length; j++) {
					// 转换生成controlvo
					CShareDetailVO detailvo = (CShareDetailVO) dtailvos[j];
					CostShareYsControlVOExt cscontrolvo = new CostShareYsControlVOExt(headvo, detailvo);
					if (isAdjust) {
						// 调整单情况，需要根据分摊明细行的预算占用日期进行预算控制
						cscontrolvo.setYsDate(detailvo.getYsdate());
					}
					item.add(cscontrolvo);
				}
			}

			if (item != null) {
				controlVos = ErBudgetUtil.getCtrlVOs(item.toArray(new IFYControl[] {}), true, ruleVos);
			}
		}

		if (controlVos == null || controlVos.length == 0) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0015")/* @res "没有符合条件的预算数据!" */);
		}

		try {
			NtbParamVO[] vos = ((ILinkQuery) NCLocator.getInstance().lookup(ILinkQuery.class.getName()))
					.getLinkDatas(controlVos);
			NtbParamVOChooser chooser = new NtbParamVOChooser(getModel().getContext().getEntranceUI(),
					nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000430")/**
			 * @res
			 *      "预算执行情况"
			 */
			);
			if (null == vos || vos.length == 0) {
				throw new BusinessShowException(nc.ui.ml.NCLangRes.getInstance().getStrByID("2008", "UPP2008-000021"));
			}
			chooser.setParamVOs(vos);
			chooser.showModal();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);
		}
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && !isTempVO();
	}

	private boolean isTempVO() {
		CostShareVO pvo = (CostShareVO) ((AggCostShareVO) getModel().getSelectedData()).getParentVO();
		return pvo.getBillstatus() == BXStatusConst.DJZT_TempSaved;
	}

	public void setModelService(CostShareModelService modelService) {
		this.modelService = modelService;
	}

	public CostShareModelService getModelService() {
		return modelService;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}

	private String getActionCode(CostShareVO vo) {
		int billStatus = vo.getBillstatus();
		switch (billStatus) {
			case BXStatusConst.DJZT_Saved:// 保存
				return BXConstans.ERM_NTB_SAVE_KEY;
			case BXStatusConst.DJZT_Sign:// 确认
				return BXConstans.ERM_NTB_APPROVE_KEY;
			default:
				return null;
		}
	}
}