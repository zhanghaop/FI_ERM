package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.ILinkQuery;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.trade.pub.IBillStatus;

/**
 * @author chenshuaia
 * 
 *         联查预算
 * 
 */
@SuppressWarnings({ "serial", "restriction" })
public class LinkBudgetAction extends NCAction {
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

		AggMatterAppVO selectvo = (AggMatterAppVO) getModel().getSelectedData();
		if (selectvo == null)
			return;

		boolean istbbused = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0014")/* @res "没有安装预算产品，不能联查预算执行情况！" */);
		} else {
			AggMatterAppVO[] datas = new AggMatterAppVO[] { selectvo };
			List<FiBillAccessableBusiVOProxy> voProxys = new ArrayList<FiBillAccessableBusiVOProxy>();
			List<MatterAppYsControlVO> items = new ArrayList<MatterAppYsControlVO>();

			String actionCode = getActionCode(selectvo.getParentVO());

			if (actionCode == null) {
				throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0015")/* @res "没有符合条件的预算数据!" */);
			}

			// 调用预算接口查询控制策略。如果返回值为空表示无控制策略，不控制。最后一个参数为false，这样就不会查找下游策略
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(selectvo.getParentVO().getPk_tradetype(), actionCode, false);
			
			if (ruleVos != null && ruleVos.length > 0) {
				for (int i = 0; i < datas.length; i++) {
					AggMatterAppVO aggvo = datas[i];
					MatterAppVO headvo = aggvo.getParentVO();

					CircularlyAccessibleValueObject[] dtailvos = aggvo.getChildrenVO();
					for (int j = 0; j < dtailvos.length; j++) {
						// 转换生成controlvo
						MatterAppYsControlVO controlVo = new MatterAppYsControlVO(headvo, (MtAppDetailVO) dtailvos[j]);
						items.add(controlVo);
					}
				}
				
				YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(items.toArray(new MatterAppYsControlVO[] {}), true,
						ruleVos);

				if (controlVos != null) {
					for (YsControlVO vo : controlVos) {
						voProxys.add(getFiBillAccessableBusiVOProxy(vo, vo.getParentBillType()));
					}
				}
			}

			try {
				// list voProxys
				NtbParamVO[] vos = ((ILinkQuery) NCLocator.getInstance().lookup(ILinkQuery.class.getName()))
						.getLinkDatas(voProxys.toArray(new IAccessableBusiVO[0]));
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

	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = ErmActionConst.getLinkBudgetName() + ErmActionConst.FAIL_MSG;
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(null);
	}

	private FiBillAccessableBusiVOProxy getFiBillAccessableBusiVOProxy(FiBillAccessableBusiVO vo, String parentBillType) {
		FiBillAccessableBusiVOProxy voProxy = new FiBillAccessableBusiVOProxy(vo);
		return voProxy;
	}

	private String getActionCode(MatterAppVO vo) {
		int billStatus = vo.getBillstatus();
		if(vo.getApprstatus() == IBillStatus.NOPASS){//未批准的单据返回保存
			return BXConstans.ERM_NTB_SAVE_KEY;
		}
		
		switch (billStatus) {
			case ErmMatterAppConst.BILLSTATUS_SAVED:
				return BXConstans.ERM_NTB_SAVE_KEY;
			case ErmMatterAppConst.BILLSTATUS_COMMITED:
				return BXConstans.ERM_NTB_SAVE_KEY;
			case ErmMatterAppConst.BILLSTATUS_APPROVED:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			default:
				return null;
		}
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}
}