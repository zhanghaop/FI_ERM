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
	 * �Ƿ�׷����ʾ
	 */
	private static final String APPEND = "append";
	/**
	 * �Ƿ���ʾƾ֤��
	 */
	private static final String XSPZ = "xspz";

	/**
	 * ƾ֤״̬
	 */
	private static final String PZZT = "pzzt";
	
	/**
	 * ֧��״̬
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
			
			//���ݲ�ѯ/����/��ĩƾ֤�Զ����ѯ��������
			String nodeCode = getModel().getContext().getNodeCode();
			if(BXConstans.BXMNG_NODECODE.equals(nodeCode) || BXConstans.BXBILL_QUERY.equals(nodeCode)
					|| BXConstans.MONTHEND_DEAL.equals(nodeCode)){
				queryConditionDLG.getQryCondEditor().setDefQCVOProcessor(new ErmBxDefQCVOProcessor(queryConditionDLG.getTempInfo()));
			}
		} 

		// ��һ�ε���ʱע�����
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
	 * �Ƿ�׷��
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
				if (fieldCode.equals(APPEND)) {// �Ƿ�׷����ʾ
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
				if (fieldCode.equals(XSPZ)) {// �Ƿ���ʾƾ֤��
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
				if(fieldCode.equals(JSFLAG)){//ֻ����ĩƾ֤����Ĳ�ѯ����ʹ��
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
