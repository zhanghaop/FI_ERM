package nc.ui.erm.billquery.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.model.ERMModelDataManager;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;

public class BillQueryModelDataManager extends ERMModelDataManager {
	@Override
	public Object[] queryData(ModelDataDescriptor modelDataDescriptor) {
		String sqlWhere = getSqlWhere();
		if (sqlWhere == null) {
			sqlWhere = "1=1 ";
		}else{
			sqlWhere = " " + sqlWhere;
		}
		sqlWhere += " and QCBZ='N' and DR ='0' and PK_GROUP= '" + getModel().getContext().getPk_group() + "'";
		Map<String, DjLXVO> billTypeMapCache = ((ErmBillBillManageModel)getModel()).getBillTypeMapCache();
		Set<String> keySet = billTypeMapCache.keySet();
		List<String> djlxbm = new ArrayList<String>();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			djlxbm.add((String)iterator.next()) ;
			
		}
		if(BXConstans.MONTHEND_DEAL.equals(getModel().getContext().getNodeCode()))
		{
			djlxbm.remove(ErmDjlxConst.BXTYPE_ADJUST_BASE_TRADETYPE);
		}
		String inStr1;
		try {
			inStr1 = SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbm.toArray(new String[0]),false);
			sqlWhere+=" and " +inStr1;
			sqlWhere += "order by djrq desc, djbh desc ";
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
		String pks[]= null;
		try {
			pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereSql(sqlWhere,null);
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

	@Override
	public void initModelByQueryScheme(IQueryScheme queryScheme) {
		// 方案查询,快速查询
		String schemeName = queryScheme.getName();
		ModelDataDescriptor modelDataDescriptor = new ModelDataDescriptor(
				schemeName);
		
		queryData2Model(getWhereSql(queryScheme), modelDataDescriptor);
	}
	
	/**
	 * 是否追加
	 * 
	 * @param scheme
	 * @author: wangyhh@ufida.com.cn
	 */
	private String getWhereSql(IQueryScheme queryScheme) {
		String whereCondition = queryScheme.getWhereSQLOnly();
		//单据查询节点单独处理，截取不包含数据权限的sql
		whereCondition=dealDataPowerSql(whereCondition);
		
		StringBuffer sqlWhere = new StringBuffer();

		if (whereCondition != null) {
			sqlWhere.append(whereCondition);
		}
		
		//单据查询，处理按表体查询处理
		IFilter[] filters = (IFilter[]) queryScheme.get(IQueryScheme.KEY_FILTERS);
		if (filters != null) {
			for (IFilter iFilter : filters) {
				String fieldCode = iFilter.getFilterMeta().getFieldCode();
				if (fieldCode.indexOf(".") > 0) {// 是否追加显示
					List<IFieldValueElement> fieldValues = iFilter.getFieldValue().getFieldValues();
					String tableName = fieldCode.substring(0, fieldCode.indexOf("."));
					String filed = fieldCode.substring(fieldCode.indexOf(".") + 1);
					
					for (IFieldValueElement value : fieldValues) {
						if(value.getValueObject() instanceof RefValueObject){
							RefValueObject refValue = (RefValueObject)value.getValueObject();
							sqlWhere.append(" and pk_jkbx in (select pk_jkbx from ");
							sqlWhere.append(tableName + " where ");
							
							try {
								sqlWhere.append(SqlUtils.getInStr(filed, refValue.getPk().split(","),true) + ")");
							} catch (BusinessException e) {
								ExceptionHandler.handleExceptionRuntime(e);
							}
						}
					}
				}
			}
		}

		return sqlWhere.toString();
	}
  
   /**
   * 单据查询节点不要数据权限
   * @param whereCondition
   * @return
   */
	private String dealDataPowerSql(String whereCondition) {
		int indexOf = whereCondition.indexOf("ZDP_");//取第一个数据权限索引的下标 
		if(indexOf!=-1){
			String tempsql = (String) whereCondition.subSequence(0, indexOf);//不包含数据权限的sql
			String lowerCase = tempsql.toLowerCase();
			int lastIndexOf = lowerCase.lastIndexOf("and");//找到最后一个and的下标 
			return whereCondition.substring(0, lastIndexOf);
		}else{
			return whereCondition;
		}
	}
}
