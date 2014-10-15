package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.model.ERMBillManageModel;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.actions.DefaultQueryDelegator;
import nc.ui.uif2.actions.QueryAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.dj.ERMDjCondVO;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;

public class ErmBillQueryAction extends QueryAction {
	/**
	 * 是否追加显示
	 */
	private static final String APPEND = "append";
	/**
	 * 是否显示凭证号
	 */
	private static final String XSPZ = "xspz";

	/**
	 * 凭证状态
	 */
	private static final String PZZT = "pzzt";
	
	/**
	 * 支付状态
	 */
	public static final String JSFLAG = "jsflag"; 
		
	private static final long serialVersionUID = 1L;

	private boolean isFirstCall = true;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getQueryCoinditionDLG();
		super.doAction(e);
	}

	@Override
	protected IQueryConditionDLG getQueryCoinditionDLG() {
		IQueryConditionDLG queryConditionDLG = null;
		
		if(getQueryDelegator() instanceof DefaultQueryDelegator) {
			queryConditionDLG = ((DefaultQueryDelegator) getQueryDelegator()).getIQueryDlg();
			queryConditionDLG.getQryCondEditor().getQueryContext().setReloadQuickAreaValue(isReloadQuickAreaValue());
			
			//单据查询/管理/月末凭证自定义查询条件处理
			String nodeCode = getModel().getContext().getNodeCode();
			if(BXConstans.BXMNG_NODECODE.equals(nodeCode) || BXConstans.BXBILL_QUERY.equals(nodeCode)
					|| BXConstans.MONTHEND_DEAL.equals(nodeCode)){
				queryConditionDLG.getQryCondEditor().setDefQCVOProcessor(new ErmBxDefQCVOProcessor(queryConditionDLG.getTempInfo()));
			}
		} 

		// 第一次调用时注册监听
		if (isFirstCall) {
			queryConditionDLG.registerCriteriaEditorListener(new ErmBillCriteriaChangedListener(getModel()));
			
			
			isFirstCall = false;
		}
		
		return queryConditionDLG;
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		if (ERMBillManageModel.QueryScheme_CHANGED.equals(event.getType())) {
			doSimpleSchemeQuery((SimpleQuerySchemeVO) event.getContextObject());
		}
		super.handleEvent(event);
	}

	@Override
	protected void executeQuery(IQueryScheme scheme) {
		resetQueryScheme(scheme);

		super.executeQuery(scheme);
	}

	/**
	 * 是否追加
	 * 
	 * @param scheme
	 * @author: wangyhh@ufida.com.cn
	 */
	private void resetQueryScheme(IQueryScheme scheme) {
		((ErmBillBillManageModel) getModel()).setAppend(false);
		ERMDjCondVO currCondVO=new ERMDjCondVO();
		IFilter[] filters = (IFilter[]) scheme.get(IQueryScheme.KEY_FILTERS);
		if (filters != null) {
			for (IFilter iFilter : filters) {
				String fieldCode = iFilter.getFilterMeta().getFieldCode();
				if (fieldCode.equals(APPEND)) {// 是否追加显示
					List<IFieldValueElement> fieldValues = iFilter
							.getFieldValue().getFieldValues();
					List<String> valueList = new ArrayList<String>();
					for (IFieldValueElement value : fieldValues) {
						valueList.add(value.getSqlString());
						if ("Y".equals(value.getSqlString())) {
							((ErmBillBillManageModel) getModel())
									.setAppend(true);
						}
					}
				}
				if (fieldCode.equals(XSPZ)) {// 是否显示凭证号
					List<IFieldValueElement> fieldValues = iFilter
							.getFieldValue().getFieldValues();
					List<String> valueList = new ArrayList<String>();
					for (IFieldValueElement value : fieldValues) {
						valueList.add(value.getSqlString());
						if ("Y".equals(value.getSqlString())) {
							currCondVO.setLinkPz(true);
							((ErmBillBillManageModel) getModel()).setDjCondVO(currCondVO);
							
						}
					}
				}
				if (fieldCode.equals(PZZT)) {
					List<IFieldValueElement> fieldValues = iFilter
							.getFieldValue().getFieldValues();
					List<String> valueList = new ArrayList<String>();
					Integer[] voucherStatus = new Integer[fieldValues.size()];
					for (int i = 0; i < fieldValues.size(); i++) {
						valueList.add(fieldValues.get(i).getSqlString());
						voucherStatus[i] = Integer.valueOf(fieldValues.get(i)
								.getSqlString());
					}
					if (voucherStatus.length != 0) {
						currCondVO.setVoucherFlags(voucherStatus);
						((ErmBillBillManageModel) getModel()).setDjCondVO(currCondVO);
					}
				}
				if(fieldCode.equals(JSFLAG)){//只是月末凭证处理的查询方案使用
					List<IFieldValueElement> fieldValues = iFilter
					.getFieldValue().getFieldValues();
					List<String> valueList = new ArrayList<String>();
					for (IFieldValueElement value : fieldValues) {
						valueList.add(value.getSqlString());
						if ("N".equals(value.getSqlString())) {
							currCondVO.setIsjs(false);
							((ErmBillBillManageModel) getModel()).setDjCondVO(currCondVO);
							
						}
					}
				}
			}
		}
	}

}
