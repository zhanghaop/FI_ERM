package nc.ui.erm.matterapp.actions.ext;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.ext.IErmMtAppMonthQueryServiceExt;
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
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterapp.ext.MatterAppYsControlVOExt;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 联查预算，
 * 
 * @author lvhj
 * 
 */
@SuppressWarnings({ "serial", "restriction" })
public class LinkBudgetActionExt extends NCAction {
	private BillManageModel model;

	public LinkBudgetActionExt() {
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
			List<FiBillAccessableBusiVOProxy> voProxys = new ArrayList<FiBillAccessableBusiVOProxy>();

			String actionCode = getActionCode(selectvo.getParentVO());

			if (actionCode == null) {
				throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0015")/* @res "没有符合条件的预算数据!" */);
			}

			// 调用预算接口查询控制策略。如果返回值为空表示无控制策略，不控制。最后一个参数为false，这样就不会查找下游策略
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(selectvo.getParentVO().getPk_tradetype(), actionCode, false);
			
			if (ruleVos != null && ruleVos.length > 0) {
				
				IFYControl[] ysvos = getMtAppYsControlVOs(selectvo);
				
				YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(ysvos, true,
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
						.getLinkDatas(voProxys.toArray(new IAccessableBusiVO[voProxys.size()]));
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
	
	/**
	 * 根据费用申请单包装预算控制vos，分期分摊占用预算
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getMtAppYsControlVOs(AggMatterAppVO vo) throws BusinessException {
		
		List<IFYControl> list = new ArrayList<IFYControl>();
		// 包装ys控制vo
		MatterAppVO headvo = vo.getParentVO();
		// 查询monthvo
		IErmMtAppMonthQueryServiceExt qryservice = NCLocator.getInstance().lookup(IErmMtAppMonthQueryServiceExt.class);
		MtappMonthExtVO[] monthvos = qryservice.queryMonthVOs(vo.getParentVO().getPrimaryKey());
		vo.setTableVO(MtappMonthExtVO.getDefaultTableName(), monthvos);
		// hash化明细行vo，备用
		Map<String, MtAppDetailVO> detailvoMap = new HashMap<String, MtAppDetailVO>();
		MtAppDetailVO[] dtailvos = vo.getChildrenVO();
		boolean isclosed = headvo.getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y;
		for (int j = 0; j < dtailvos.length; j++) {
			detailvoMap.put(dtailvos[j].getPrimaryKey(), dtailvos[j]);
			if(isclosed){
				// 关闭状态的申请单，还需要联查关闭期间的预算
				MatterAppYsControlVOExt controlvo = new MatterAppYsControlVOExt(headvo, dtailvos[j]);
				controlvo.setYsDate(headvo.getClosedate());
				list.add(controlvo);
			}
		}
		for (int j = 0; j < monthvos.length; j++) {
			// 遍历分期均摊记录，包装回写预算vo
			MtappMonthExtVO monthvo = monthvos[j];
			MatterAppYsControlVOExt controlvo = new MatterAppYsControlVOExt(headvo, detailvoMap.get(monthvo.getPk_mtapp_detail()));
			if (controlvo.isYSControlAble()) {
				controlvo.setYsDate(monthvo.getBilldate());
				list.add(controlvo);
			}
		}
		
		return list.toArray(new IFYControl[list.size()]);
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
													 * @ res "{0}失败！"
													 */;
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
	}

	private FiBillAccessableBusiVOProxy getFiBillAccessableBusiVOProxy(FiBillAccessableBusiVO vo, String parentBillType) {
		FiBillAccessableBusiVOProxy voProxy = new FiBillAccessableBusiVOProxy(vo);
		return voProxy;
	}

	private String getActionCode(MatterAppVO vo) {
		int billStatus = vo.getBillstatus();
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