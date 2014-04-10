package nc.ui.erm.billmanage.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.common.ErmConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.model.ERMModelDataManager;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.value.IFieldValueElement;
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
		} else {
			sqlWhere = " " + sqlWhere;
		}

		try {
			sqlWhere += " and QCBZ='N' and DR ='0'";

			// 交易类型
			Map<String, DjLXVO> billTypeMapCache = ((ErmBillBillManageModel) getModel()).getBillTypeMapCache();
			Set<String> keySet = billTypeMapCache.keySet();
			List<String> djlxbm = new ArrayList<String>();
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				djlxbm.add((String) iterator.next());
			}
			sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbm.toArray(new String[0]), false);

			if(getQryScheme() != null){
				//已审批、待审批
				boolean user_approving = false;
				boolean user_approved = false;
				IFilter[] filters = (IFilter[]) getQryScheme().get(IQueryScheme.KEY_FILTERS);
				if (filters != null) {
					for (IFilter iFilter : filters) {
						String fieldCode = iFilter.getFilterMeta().getFieldCode();
						if (fieldCode.equals(ErmConst.QUERY_CONDITION_APPROVING)) {// 待审批
							List<IFieldValueElement> fieldValues = iFilter.getFieldValue().getFieldValues();
							List<String> valueList = new ArrayList<String>();
							for (IFieldValueElement value : fieldValues) {
								valueList.add(value.getSqlString());
								if ("Y".equals(value.getSqlString())) {
									user_approving = true;
								}
							}
						}
						
						if (fieldCode.equals(ErmConst.QUERY_CONDITION_APPROVED)) {// 我已审批
							List<IFieldValueElement> fieldValues = iFilter.getFieldValue().getFieldValues();
							List<String> valueList = new ArrayList<String>();
							for (IFieldValueElement value : fieldValues) {
								valueList.add(value.getSqlString());
								if ("Y".equals(value.getSqlString())) {
									user_approved = true;
								}
							}
						}
					}
				}
				
				if (user_approving) { // 我待审批
					String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
							.queryApprovedWFBillPksByCondition(null, djlxbm.toArray(new String[0]), false);
					if (billPks != null && billPks.length > 0) {
						sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, false);
					} else {
						sqlWhere += " and 1=0 ";
					}
				}
				
				if (user_approved) { // 我已审批
					String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
					.queryApprovedWFBillPksByCondition(null, djlxbm.toArray(new String[0]), true);
					
					if (billPks != null && billPks.length > 0) {
						sqlWhere += " and (" + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, false);
						sqlWhere += " or approver = '" + ErUiUtil.getPk_user() + "')";
					} else {
						sqlWhere += " and approver = '" + ErUiUtil.getPk_user() + "' ";
					}
				}
			}

			String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class)
					.queryPKsByWhereForBillManageNode(sqlWhere, ErUiUtil.getPK_group(), ErUiUtil.getPk_user());
			getPaginationModel().setObjectPks(pks, modelDataDescriptor);
			if (!getListView().isComponentVisible()) {
				getListView().showMeUp();
			}
			
			int count = getPaginationModel().getCurrentDataDescriptor().getCount();
			if (count != 0) {
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQuerySuccessInfo(count), getModel()
						.getContext());
			} else {
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
			public Object[] queryObjectByPks(String[] pks) throws BusinessException {
				List<JKBXVO> vos;
				if (pks == null) {
					return null;
				} else {
					vos = NCLocator
							.getInstance()
							.lookup(IBXBillPrivate.class)
							.queryHeadVOsByPrimaryKeys(pks, null, false,
									((ErmBillBillManageModel) getModel()).getDjCondVO());
					// 数据过滤后，需要重新设置查询结果的数值
					getPaginationModel().getCurrentDataDescriptor().setCount(vos.size());
					return vos.toArray(new JKBXVO[pks.length]);
				}
			}
		});
	}

}
