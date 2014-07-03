package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.ILinkQuery;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedBillYsControlVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.trade.pub.IBillStatus;

/**
 * 联查预算按钮
 *
 */
public class AccLinkBudgetAction extends NCAction {

	private static final long serialVersionUID = 1L;

	public AccLinkBudgetAction() {
		super();
		setCode(ErmActionConst.LINKBUDGET);
		setBtnName(ErmActionConst.getLinkBudgetName());
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		AggAccruedBillVO selectvo = (AggAccruedBillVO) getModel().getSelectedData();
		if (selectvo == null)
			return;
		
		//预算安装判断
		boolean istbbused = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0014")/* @res "没有安装预算产品，不能联查预算执行情况！" */);
		}
		
		YsControlVO[] controlVos = null;
		
		selectvo = (AggAccruedBillVO) selectvo.clone();// 克隆
		String actionCode = getActionCode(selectvo.getParentVO());
		if (actionCode == null) {//其他状态默认设置为保存
//			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
//			"0201212-0015")/* @res "没有符合条件的预算数据!" */);
			selectvo.getParentVO().setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;
		}

		List<AccruedBillYsControlVO> items = new ArrayList<AccruedBillYsControlVO>();

		// 调用预算接口查询控制策略。如果返回值为空表示无控制策略，不控制。最后一个参数为false，这样就不会查找下游策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(selectvo.getParentVO().getPk_tradetype(), actionCode, false);

		if (ruleVos != null && ruleVos.length > 0) {
			AccruedVO headvo = selectvo.getParentVO();
			CircularlyAccessibleValueObject[] dtailvos = selectvo.getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				// 转换生成controlvo
				AccruedBillYsControlVO controlVo = new AccruedBillYsControlVO(headvo, (AccruedDetailVO) dtailvos[j]);
				items.add(controlVo);
			}
			
			controlVos = ErBudgetUtil.getCtrlVOs(items.toArray(new AccruedBillYsControlVO[] {}), true,
					ruleVos);

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
				throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0015")/* @res "没有符合条件的预算数据!" */);
			}

			chooser.setParamVOs(vos);
			chooser.showModal();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);
		}
	}

	private String getActionCode(AccruedVO vo) {
		int billStatus = vo.getBillstatus();
		if (vo.getApprstatus() == IBillStatus.NOPASS) {// 未批准的单据返回保存
			return BXConstans.ERM_NTB_SAVE_KEY;
		}

		switch (billStatus) {
			case ErmAccruedBillConst.BILLSTATUS_SAVED:
				return BXConstans.ERM_NTB_SAVE_KEY;
			case ErmAccruedBillConst.BILLSTATUS_APPROVED:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			default:
				return null;
		}
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

	private BillManageModel model;

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}
}
