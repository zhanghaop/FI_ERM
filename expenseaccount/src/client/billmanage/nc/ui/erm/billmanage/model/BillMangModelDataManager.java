package nc.ui.erm.billmanage.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;

public class BillMangModelDataManager extends ERMModelDataManager {
	private static final long serialVersionUID = 1L;

	@Override
	public Object[] queryData(ModelDataDescriptor modelDataDescriptor) {
		String sqlWhere = getSqlWhere();
		if (sqlWhere == null) {
			sqlWhere = "1=1 ";
		}else{
			sqlWhere = " " + sqlWhere;
		}
		sqlWhere += " and QCBZ='N' and DR ='0'";
		Map<String, DjLXVO> billTypeMapCache = ((ErmBillBillManageModel)getModel()).getBillTypeMapCache();
		Set<String> keySet = billTypeMapCache.keySet();
		List<String> djlxbm = new ArrayList<String>();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			djlxbm.add((String)iterator.next()) ;
			
		}
		String inStr1;
		try {
			inStr1 = SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbm.toArray(new String[0]),false);
			sqlWhere+=" and " +inStr1;
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
		String pks[]= null;
		try {
			WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
			pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillManageNode(sqlWhere,instance.getGroupVO().getPk_group(),instance.getLoginUser().getPrimaryKey());
			getPaginationModel().setObjectPks(pks,modelDataDescriptor);
			if (!getListView().isComponentVisible()) {
                getListView().showMeUp();
            }
			
			if(pks != null){
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQuerySuccessInfo(pks.length), getModel().getContext());
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
