package nc.ui.erm.billinput.model;

import java.util.List;

import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.model.ERMModelDataManager;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class BillInputModelDataManager extends ERMModelDataManager{

    @Override
    public Object[] queryData(ModelDataDescriptor modelDataDescriptor) {
        String sqlWhere = getSqlWhere();
        if (sqlWhere == null) {
            sqlWhere = "1=1 ";
        }else{
            sqlWhere = " " + sqlWhere;
        }
        
        //调整单不存在业务行，数据权限控制时，会查不到数据，这里对表体进行过滤掉
        DjLXVO djlxvo = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
        if(djlxvo != null && djlxvo.getBxtype() != null && djlxvo.getBxtype() == ErmDjlxConst.BXTYPE_ADJUST){
        	String[] sqls = sqlWhere.split("( and )|( AND )");
        	StringBuffer sqlBuf = new StringBuffer(sqls[0]);
        	
        	for(int i = 1; i < sqls.length; i ++){
        		if(sqls[i].indexOf("er_busitem") < 0 && sqls[i].indexOf("ER_BUSITEM") < 0){
        			sqlBuf.append(" and " + sqls[i]);
        		}
        	}
        	
        	sqlWhere = sqlBuf.toString();
        }
        
        sqlWhere += " and QCBZ='N' and DR ='0'";
        sqlWhere += " and djlxbm = '"+((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode()+"'";
        String pks[]= null;
        try {
            String pk_user = WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey();
            pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere,pk_user,((ErmBillBillManageModel)getModel()).getCurrentDjLXVO().getDjdl());
            getPaginationModel().setObjectPks(pks,modelDataDescriptor);
            if (!getListView().isComponentVisible()) {
                getListView().showMeUp();
            }
            
			int count = getPaginationModel().getCurrentDataDescriptor().getCount();
            if(count != 0){
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQuerySuccessInfo(count), getModel().getContext());
			}else{
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQueryNullInfo(), getModel().getContext());
			}
        } catch (BusinessException e) {
            ExceptionHandler.handleExceptionRuntime(e);
        }
        return null;
    }

    public void setPaginationModel(PaginationModel paginationModel) {
        super.setPaginationModel(paginationModel);
        getPaginationModel().addPaginationModelListener(this);
        getPaginationModel().setPaginationQueryService(new IPaginationQueryService() {
            @Override
            public Object[] queryObjectByPks(String[] pks)
            throws BusinessException {
                List<JKBXVO> vos;
                if(pks==null){
                    return null;
                }else{
                    vos =NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadVOsByPrimaryKeys(pks,null,false,((ErmBillBillManageModel)getModel()).getDjCondVO());
                    //数据过滤后，需要重新设置查询结果的数值
                    getPaginationModel().getCurrentDataDescriptor().setCount(vos.size());
                    return vos.toArray(new JKBXVO[pks.length]);
                }
                
            }
        });
    }
}
