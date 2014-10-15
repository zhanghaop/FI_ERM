package nc.ui.erm.billinit.model;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.model.ERMModelDataManager;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class BillCommModelDataManager extends ERMModelDataManager
{

    @Override
    public Object[] queryData(ModelDataDescriptor modelDataDescriptor)
    {
        String sqlWhere = getSqlWhere();
        if (sqlWhere == null) {
            sqlWhere = "1=1 ";
        }else{
            sqlWhere = " " + sqlWhere;
        }
        sqlWhere += " and QCBZ='N' and DR ='0'";
        if(BXConstans.BXINIT_NODECODE_G.equals(getModel().getContext().getNodeCode()) ){
            sqlWhere += "and isinitgroup = 'Y'";
        }
        sqlWhere += " and pk_group = '"+getModel().getContext().getPk_group()+"'";
        sqlWhere += " order by djrq desc,djbh desc";
        String pks[]= null;
        try {
            pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereSqlForComBill(sqlWhere);
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
                List<JKBXVO> voList = null;
                if(pks==null){
                    return null;
                }else{
                    voList =NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadVOsByPrimaryKeys(pks,null,true,((ErmBillBillManageModel)getModel()).getDjCondVO());
                    if(voList != null){
                    	if(voList.get(0).getParentVO().isInit()){
                    		//常用单据赋值设置组织
                    		for(JKBXVO vo:voList){
                    			if(vo.getParentVO().getIsinitgroup().booleanValue()){
                    				vo.getParentVO().setSetorg(vo.getParentVO().getPk_group());
                    			}else{
                    				vo.getParentVO().setSetorg(vo.getParentVO().getPk_org());               
                    			}
                    		}
                    	}
                    	//数据过滤后，需要重新设置查询结果的数值
                    	getPaginationModel().getCurrentDataDescriptor().setCount(voList.size());
                    	return voList.toArray(new JKBXVO[pks.length]);
                    }
                    return null;
                }
                
            }
        });
    }

}
