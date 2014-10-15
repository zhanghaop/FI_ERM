package nc.ui.erm.expamortize.model;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.expamortize.view.ExpamortizePeriodPanel;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.bd.period2.AccperiodmonthVO;

public class ExpamorizeDataManager implements IQueryAndRefreshManager, IPaginationModelListener, IAppModelDataManager {

	private IExceptionHandler exceptionHandler;
	private AbstractAppModel model;
	private PaginationModel pageModel;
	private BillManagePaginationDelegator delegator;
	private ExpamortizePeriodPanel topperiodpane;

	public BillManagePaginationDelegator getDelegator() {
		if (delegator == null) {
			delegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPageModel());
		}
		return delegator;
	}

	public void setDelegator(BillManagePaginationDelegator delegator) {
		this.delegator = delegator;
	}

	public PaginationModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(PaginationModel pageModel) {
		this.pageModel = pageModel;
		pageModel.addPaginationModelListener(this);
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		this.initData(sqlWhere);
	}

	public void initData(String wheresql) {
		String[] Pk = null;
		try {
			if (wheresql != null) {
				Pk = getExpamtinfoQueryService().queryPksByWhereSql(wheresql);
			}
			ModelDataDescriptor mdd = new ModelDataDescriptor();
			getPageModel().setObjectPks(Pk,mdd);

		} catch (Exception e) {
			getExceptionHandler().handlerExeption(e);
		}
	}

	public void initModelByOrgBillNO(String pk_org, String billno) {
		String[] Pk = null;
		try {
			if (pk_org != null && billno != null) {
				Pk = getExpamtinfoQueryService().queryByOrgBillNo(pk_org, billno);
			}
			getPageModel().setObjectPks(Pk);

		} catch (Exception e) {
			getExceptionHandler().handlerExeption(e);
		}
	}

	@Override
    public void refresh()
    {
        //当切换业务日期时，获得新会计期间
        String pk_org = getModel().getContext().getPk_org();
        AccperiodmonthVO accperiodmonthVO;
        try
        {
            accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, BXUiUtil.getBusiDate());
            getTopperiodpane().getRefPane().setPK(accperiodmonthVO.getPk_accperiodmonth());
            ((ExpamorizeManageModel) getModel()).setPeriod(accperiodmonthVO.getYearmth());
            String condition = "pk_org='" + pk_org + "' and '" + accperiodmonthVO.getYearmth() + "' between start_period and end_period";
            initModelBySqlWhere(condition);
        }
        catch (InvalidAccperiodExcetion e)
        {
            exceptionHandler.handlerExeption(e);
        }
    }

	@Override
	public void initModel() {
		String[] Pk = null;
		String pk_org = getModel().getContext().getPk_org();
		String period = ((ExpamorizeManageModel) getModel()).getPeriod();
		try {
			if (pk_org != null && period != null) {
				Pk = getExpamtinfoQueryService().queryPksByCond(pk_org, period);
			}
			ModelDataDescriptor mdd = new ModelDataDescriptor();
			getPageModel().setObjectPks(Pk, mdd);

		} catch (Exception e) {
			getExceptionHandler().handlerExeption(e);
		}
	}

	private IExpAmortizeinfoQuery getExpamtinfoQueryService() {
		return NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
	}

	@Override
	public void onDataReady() {
		getDelegator().onDataReady();
	}

	@Override
	public void onStructChanged() {

	}

    public ExpamortizePeriodPanel getTopperiodpane()
    {
        return topperiodpane;
    }

    public void setTopperiodpane(ExpamortizePeriodPanel topperiodpane)
    {
        this.topperiodpane = topperiodpane;
    }
	
	

}
