package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
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
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.trade.pub.IBillStatus;

/**
 * ����Ԥ�㰴ť
 *
 */
@SuppressWarnings({ "restriction" })
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
		
		//Ԥ�㰲װ�ж�
		boolean istbbused = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0014")/* @res "û�а�װԤ���Ʒ����������Ԥ��ִ�������" */);
		}

		selectvo = (AggAccruedBillVO) selectvo.clone();// ��¡
		String actionCode = getActionCode(selectvo.getParentVO());
		if (actionCode == null) {//����״̬Ĭ������Ϊ����
//			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
//			"0201212-0015")/* @res "û�з���������Ԥ������!" */);
			selectvo.getParentVO().setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;
		}

		List<FiBillAccessableBusiVOProxy> voProxys = new ArrayList<FiBillAccessableBusiVOProxy>();
		List<AccruedBillYsControlVO> items = new ArrayList<AccruedBillYsControlVO>();

		// ����Ԥ��ӿڲ�ѯ���Ʋ��ԡ��������ֵΪ�ձ�ʾ�޿��Ʋ��ԣ������ơ����һ������Ϊfalse�������Ͳ���������β���
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(selectvo.getParentVO().getPk_tradetype(), actionCode, false);

		if (ruleVos != null && ruleVos.length > 0) {
			AccruedVO headvo = selectvo.getParentVO();
			CircularlyAccessibleValueObject[] dtailvos = selectvo.getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				// ת������controlvo
				AccruedBillYsControlVO controlVo = new AccruedBillYsControlVO(headvo, (AccruedDetailVO) dtailvos[j]);
				items.add(controlVo);
			}
			YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(items.toArray(new AccruedBillYsControlVO[] {}), true,
					ruleVos);

			if (controlVos != null) {
				for (YsControlVO vo : controlVos) {
					voProxys.add(new FiBillAccessableBusiVOProxy(vo));
				}
			}
		}

		if (voProxys.size() == 0) {
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0015")/* @res "û�з���������Ԥ������!" */);
		}
		try {
			NtbParamVO[] vos = ((ILinkQuery) NCLocator.getInstance().lookup(ILinkQuery.class.getName()))
					.getLinkDatas(voProxys.toArray(new IAccessableBusiVO[0]));
			NtbParamVOChooser chooser = new NtbParamVOChooser(getModel().getContext().getEntranceUI(),
					nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000430")/**
			 * @res
			 *      "Ԥ��ִ�����"
			 */
			);
			if (null == vos || vos.length == 0) {
				throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0015")/* @res "û�з���������Ԥ������!" */);
			}

			chooser.setParamVOs(vos);
			chooser.showModal();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);
		}
	}

	private String getActionCode(AccruedVO vo) {
		int billStatus = vo.getBillstatus();
		if (vo.getApprstatus() == IBillStatus.NOPASS) {// δ��׼�ĵ��ݷ��ر���
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
