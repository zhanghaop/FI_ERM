package nc.ui.erm.expamortize.action;

import java.util.List;

import javax.swing.AbstractAction;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.funcnode.ui.FuncletInitData;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.expamortize.model.ExpamorizeDataManager;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.erm.expamortize.view.ExpamortizeFinOrgPanel;
import nc.ui.erm.expamortize.view.ExpamortizePeriodPanel;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.IFuncNodeInitDataListener;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.util.erm.expamortize.ExpamortizeLinkQuery;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.link.DefaultLinkData;

public class AmtFuncNodeInitData implements IFuncNodeInitDataListener {
	private IAppModelDataManager dataManager;// 注射
	private ExpamortizeFinOrgPanel toporgpane;
	private ExpamortizePeriodPanel topperiodpane;
	private AbstractAction amtAction;
	private AbstractAction amtperiodAction;
	private AbstractAction voucherAction;
	private IExceptionHandler exceptionHandler;
	private AbstractAppModel model;

	@Override
	public void initData(FuncletInitData data) {
		if (data == null) {// 表示是从功能节点打开,初始化查询
			((ExpamorizeDataManager) dataManager).initModel();

		} else if (data.getInitData() instanceof DefaultLinkData) {// 从总账制单节点和会计平台节点打开
			try {
				List<Object> vos = (List<Object>) ((DefaultLinkData) data.getInitData()).getBillVOs();
				String[] pk_expamt = new String[vos.size()];

				String currentAccMonth = null;
				for (int i = 0; i < vos.size(); i++) {
					AggExpamtinfoVO aggVo = (AggExpamtinfoVO) vos.get(i);
					pk_expamt[i] = ((ExpamtinfoVO) aggVo.getParentVO()).getPk_expamtinfo();

					if (currentAccMonth == null) {
						AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(
								((ExpamtinfoVO) aggVo.getParentVO()).getPk_org(),
								((ExpamtinfoVO) aggVo.getParentVO()).getAmortize_date());

						currentAccMonth = accperiodmonthVO.getYearmth();
					}
				}
				// 根据过程查询摊销的信息
				ExpamtinfoVO[] exp = ((IExpAmortizeinfoQuery) NCLocator.getInstance().lookup(
						IExpAmortizeinfoQuery.class.getName())).queryExpamtinfoByPks(pk_expamt, currentAccMonth);
				getModel().initModel(exp);
				setDefaultValue(exp[0].getPk_org(), exp[0].getAmortize_date());
			} catch (ComponentException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else if (data.getInitData() instanceof ExpamortizeLinkQuery) {
			// 从帐表联查的查询
			String pk_org = ((LinkQuery) data.getInitData()).getPkOrg();
			String BillID = ((LinkQuery) data.getInitData()).getBillID();
			if (pk_org != null && BillID != null) {
				try {
					((ExpamorizeDataManager) dataManager).getPageModel().setObjectPks(new String[] { BillID });
				} catch (BusinessException e) {
					exceptionHandler.handlerExeption(e);
				}
				setDefaultValue(pk_org, BXUiUtil.getBusiDate());
			}
		} else if (data.getInitData() instanceof LinkQuery) {
			// 从帐表联查的查询
			String pk_org = ((LinkQuery) data.getInitData()).getBillType();
			String billno = ((LinkQuery) data.getInitData()).getBillID();
			if (pk_org != null && billno != null) {
				setDefaultValue(pk_org, BXUiUtil.getBusiDate());
				((ExpamorizeDataManager) dataManager).initModelByOrgBillNO(pk_org, billno);
			}
		}
	}

	private void setDefaultValue(String pk_org, UFDate date) {
		((ExpamortizeFinOrgPanel) getToporgpane()).getRefPane().setPK(pk_org);
		((ExpamortizeFinOrgPanel) getToporgpane()).getRefPane().setEnabled(false);
		getModel().getContext().setPk_org(pk_org);
		AccperiodmonthVO accperiodmonthVO;
		try {
			accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, date);
			getTopperiodpane().getRefPane().setPK(accperiodmonthVO.getPk_accperiodmonth());
			((ExpamorizeManageModel) getModel()).setPeriod(accperiodmonthVO.getYearmth());
		} catch (InvalidAccperiodExcetion e) {
			exceptionHandler.handlerExeption(e);
		}
		getAmtAction().setEnabled(false);
		getAmtperiodAction().setEnabled(false);
		getVoucherAction().setEnabled(false);
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public ExpamortizePeriodPanel getTopperiodpane() {
		return topperiodpane;
	}

	public void setTopperiodpane(ExpamortizePeriodPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}

	public AbstractAction getAmtAction() {
		return amtAction;
	}

	public void setAmtAction(AbstractAction amtAction) {
		this.amtAction = amtAction;
	}

	public AbstractAction getAmtperiodAction() {
		return amtperiodAction;
	}

	public void setAmtperiodAction(AbstractAction amtperiodAction) {
		this.amtperiodAction = amtperiodAction;
	}

	public AbstractAction getVoucherAction() {
		return voucherAction;
	}

	public void setVoucherAction(AbstractAction voucherAction) {
		this.voucherAction = voucherAction;
	}

	public ExpamortizeFinOrgPanel getToporgpane() {
		return toporgpane;
	}

	public void setToporgpane(ExpamortizeFinOrgPanel toporgpane) {
		this.toporgpane = toporgpane;
	}

	public IAppModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;

	}

}
