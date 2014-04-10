package nc.ui.erm.model;

import nc.funcnode.ui.FuncletInitData;
import nc.ui.erm.view.ERMBillListView;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.uif2.model.IQueryAndRefreshManagerEx;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/** 
 * ͨ��ModelDataManger
 * <b>Date:</b>2012-12-6<br>
 * @author��wangled@ufida.com.cn
 * @version $Revision$
 */ 
@SuppressWarnings("restriction")
public abstract class ERMModelDataManager implements IAppModelDataManagerEx,
		IModelDataManager, IQueryAndRefreshManagerEx,IPaginationModelListener {

	private BillManageModel model;
	/**
	 * ��ѯʹ�õ�����,���������ӹ�������
	 */
	private String sqlWhere;

    LoginContext context;
    
    
	/**
	 * ��ҳmodel
	 */
	private PaginationModel paginationModel = null;

	private BillManagePaginationDelegator delegator = null;
	
	private ERMBillListView listView;


    @Override
    public void initModel() {
        Object initdata = null;
        if (getContext() != null)
            initdata = getContext().getInitData();
        if (initdata instanceof FuncletInitData) {
            FuncletInitData fcinitdata = (FuncletInitData) initdata;
            Object initData2 = fcinitdata.getInitData();
            if (initData2 == null)
                return;
        } else {
            getModel().initModel(null);
        }
    }

	@Override
	public void refresh() {
		if (!StringUtil.isEmpty(sqlWhere)) {
			initModelBySqlWhere(sqlWhere);
		}else{
			initModelBySqlWhere(" 1=1 ");
		}
	}

	// ��ѯ����
	public abstract Object[] queryData(ModelDataDescriptor modelDataDescriptor);

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		ModelDataDescriptor modelDataDescriptor = new ModelDataDescriptor();
		queryData2Model(sqlWhere, modelDataDescriptor);
	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {

	}

	@Override
	public void initModelBySqlWhere(IQueryScheme qryScheme) {
		initModelByQueryScheme(qryScheme);
	}

	@Override
	public void initModelByQueryScheme(IQueryScheme queryScheme) {
		// ������ѯ,���ٲ�ѯ
		String schemeName = queryScheme.getName();
		ModelDataDescriptor modelDataDescriptor = new ModelDataDescriptor(
				schemeName);
		String whereCondition = queryScheme.getWhereSQLOnly();
		queryData2Model(whereCondition, modelDataDescriptor);
	}

	/**
	 * 
	 * ͳһ�����ݲ�ѯ�������
	 * 
	 * @param sqlWhere
	 * @param modelDataDescriptor
	 */
	public void queryData2Model(String sqlWhere,
			ModelDataDescriptor modelDataDescriptor) {
		if (sqlWhere == null) {
			sqlWhere = "1=1";
		}
		//��Ҫ��ѯ��Ȩ�޵���֯
		String[] pkorgs = getModel().getContext().getPkorgs();
		if(pkorgs!=null && pkorgs.length!=0){
			String inStr1;
			try {
				inStr1 = SqlUtils.getInStr("pk_org", pkorgs, false);
				
				sqlWhere += " and " +inStr1; 
				
				this.sqlWhere = sqlWhere;
				
				queryData(modelDataDescriptor);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
	}
	@Override
	public void onDataReady() {
		getDelegator().onDataReady();
	}
	
	public void setPaginationModel(PaginationModel paginationModel) {
		this.paginationModel = paginationModel;
	}
	 
	public PaginationModel getPaginationModel() {
		return paginationModel;
	}

	@Override
	public void onStructChanged() {
		// TODO Auto-generated method stub
	}
	
	public BillManagePaginationDelegator getDelegator() {
		return delegator;
	}

	public void setDelegator(BillManagePaginationDelegator delegator) {
		this.delegator = delegator;
	}
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	public String getSqlWhere() {
		return sqlWhere;
	}

	public void setSqlWhere(String sqlWhere) {
		this.sqlWhere = sqlWhere;
	}
	
    
    public LoginContext getContext() {
        return context;
    }

    public void setContext(LoginContext context) {
        this.context = context;
    }
    
    public ERMBillListView getListView() {
        return listView;
    }

    public void setListView(ERMBillListView listView) {
        this.listView = listView;
    }
}
