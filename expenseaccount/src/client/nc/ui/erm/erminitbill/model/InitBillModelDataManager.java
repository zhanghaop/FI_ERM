package nc.ui.erm.erminitbill.model;

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

/**
 * 期初单据ModelDataManager <b>Date:</b>2012-12-6<br>
 * 
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class InitBillModelDataManager extends ERMModelDataManager{

	@Override
	public Object[] queryData(ModelDataDescriptor mdd) {
		String sqlWhere = getSqlWhere();
		if (sqlWhere == null) {
			sqlWhere = "1=1 ";
		}else{
			sqlWhere = " " + sqlWhere;
		}
		sqlWhere += " and QCBZ='Y' and DR <>'1'";
		sqlWhere += " order by djrq desc, djbh desc ";
		
		String pks[]= null;
		try {
			pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereSql(sqlWhere,BXConstans.JK_DJDL);
			getPaginationModel().setObjectPks(pks,mdd);
			
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
					vos =NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadVOsByPrimaryKeys(pks,BXConstans.JK_DJDL,false,((ErmBillBillManageModel)getModel()).getDjCondVO());
					//数据过滤后，需要重新设置查询结果的数值
                	getPaginationModel().getCurrentDataDescriptor().setCount(vos.size());
					return vos.toArray(new JKBXVO[pks.length]);
				}
				
			}
		});
	}
}
