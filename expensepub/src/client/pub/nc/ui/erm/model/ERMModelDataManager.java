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
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/** 
 * 通用ModelDataManger
 * <b>Date:</b>2012-12-6<br>
 * @author：wangled@ufida.com.cn
 * @version $Revision$
 */ 
@SuppressWarnings("restriction")
public abstract class ERMModelDataManager implements IAppModelDataManagerEx,
		IModelDataManager, IQueryAndRefreshManagerEx,IPaginationModelListener {

	private BillManageModel model;
	/**
	 * 查询使用的条件,子类可以添加过滤条件
	 */
	private String sqlWhere;
	
	IQueryScheme qryScheme;
	
	LoginContext context;
    
    
	/**
	 * 分页model
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

	// 查询数据
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
		this.qryScheme = queryScheme;
		// 方案查询,快速查询
		String schemeName = queryScheme.getName();
		ModelDataDescriptor modelDataDescriptor = new ModelDataDescriptor(
				schemeName);
		String whereCondition = queryScheme.getWhereSQLOnly();
		queryData2Model(whereCondition, modelDataDescriptor);
	}

	/**
	 * 
	 * 统一的数据查询设置入口
	 * 
	 * @param sqlWhere
	 * @param modelDataDescriptor
	 */
	public void queryData2Model(String sqlWhere,
			ModelDataDescriptor modelDataDescriptor) {
		if (sqlWhere == null) {
			sqlWhere = "1=1";
		}
		//需要查询有权限的组织
		String[] pkorgs = getModel().getContext().getPkorgs();
		if(pkorgs!=null && pkorgs.length!=0){
			String inStr1;
			try {
				if(BXConstans.BXINIT_NODECODE_G.equals(getModel().getContext().getNodeCode()) ){//常用单据集团级节点不需要 组织信息
					this.sqlWhere = sqlWhere;
				}else{
					
					inStr1 = SqlUtils.getInStr("pk_org", pkorgs, false);
					
					sqlWhere += " and " +inStr1; 
					
					this.sqlWhere = sqlWhere;
					
				}
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
    
    public IQueryScheme getQryScheme() {
		return qryScheme;
	}

	public void setQryScheme(IQueryScheme qryScheme) {
		this.qryScheme = qryScheme;
	}
}
