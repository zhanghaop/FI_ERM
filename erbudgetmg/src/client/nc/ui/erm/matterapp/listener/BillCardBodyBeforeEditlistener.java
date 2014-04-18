package nc.ui.erm.matterapp.listener;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.resa.costcenter.CostCenterVO;

/**
 * 表体编辑前
 * @author chenshuaia
 *
 */
public class BillCardBodyBeforeEditlistener implements BillEditListener2 {
	private static final long serialVersionUID = 1L;
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	protected IExceptionHandler exceptionHandler;

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		BillItem item = (BillItem) e.getSource();
		String key = e.getKey();
		
		if(ErmMatterAppConst.MatterApp_MDCODE_DETAIL.endsWith(e.getTableCode())){
			//按费用申请单位进行设置
			String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
			// 按表体费用承担单位过滤
			String assume_org = billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.ASSUME_ORG);
			
			String pk_currtype = billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.PK_CURRTYPE);
			//参照设置pk_org
			if(MtAppDetailVO.PK_RESACOSTCENTER.equals(key)){//成本中心
				UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
				String pk_pcorg = billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.PK_PCORG);
				if (pk_pcorg != null) {
					refPane.setEnabled(true);
					refPane.getRefModel().setPk_org(pk_pcorg);
				} else {
					refPane.setEnabled(false);
				}
				String wherePart = CostCenterVO.PK_PROFITCENTER+"="+"'"+pk_pcorg+"'";// 按成本中心所属利润中心过滤 
				billForm.filterRefModelWithWherePart(refPane, pk_pcorg, wherePart, null);
			}else if (MtAppDetailVO.PK_WBS.equals(key)) {
				final String pk_project = (String)billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.PK_PROJECT);
				UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
				if (pk_project != null) {
					String wherePart = " pk_project=" + "'" + pk_project + "'";
					//过滤项目任务
					billForm.filterRefModelWithWherePart(refPane, assume_org, null, wherePart);
				}else{
					billForm.filterRefModelWithWherePart(refPane, assume_org, null, "1=0");
				}
			}else if (MtAppDetailVO.PK_IOBSCLASS.equals(key)) {// 收支项目添加数据权限控制
				// 编辑前的组织
				UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
				refPane.getRefModel().setUseDataPower(true);
				refPane.setPk_org(assume_org);
			}else if(MtAppDetailVO.REASON.equals(key)){
				Object value = billForm.getBodyValue(e.getRow(), MtAppDetailVO.REASON);
				
				if(value != null){//解决多表体时输入不同的事由，然后双击变成最后输入的字段-chenshuaia
					((UIRefPane) item.getComponent()).setPK(value);
				}
				((UIRefPane) item.getComponent()).setPk_org(assume_org);
			}else if(MtAppDetailVO.PK_CHECKELE.equals(key)){//核算要素，根据利润中心过滤
				// 核算要素根据利润中心过滤
				UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
				String pk_pcorg = (String) billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.PK_PCORG);
				if (pk_pcorg == null) {
					refPane.setEnabled(false);
					refPane.setWhereString("1=0");
				} else {
					refPane.setEnabled(true);
					refPane.setWhereString(null);
					refPane.setPk_org(pk_pcorg);
				}
			}else if(MtAppDetailVO.ASSUME_DEPT.equals(key)){//费用承担部门
				UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
				refPane.setPk_org(assume_org);
			}else if (item.getComponent() instanceof UIRefPane
					&& ((UIRefPane) item.getComponent()).getRefModel() != null) {
				((UIRefPane) item.getComponent()).setPk_org(assume_org);
			} else if (MtAppDetailVO.ORG_CURRINFO.equals(key)) {
				if (pk_org != null && assume_org != null) {
					if (!pk_org.equals(assume_org)) {
						boolean isEnable = MatterAppUiUtil.getOrgRateEnableStatus(assume_org, pk_currtype);
						billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(isEnable);
					} else {
						billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(false);
					}
				} else {
					billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(false);
				}
			} else if (MtAppDetailVO.GROUP_CURRINFO.equals(key)) {
				if (pk_org != null && assume_org != null) {
					if (!pk_org.equals(assume_org)) {
						boolean isEnable = MatterAppUiUtil.getGroupRateEnableStatus(assume_org, pk_currtype);
						billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(isEnable);
					} else {
						billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(false);
					}
				} else {
					billForm.getBillCardPanel().getBodyItem(key).setEnabled(false);
				}
			} else if (MtAppDetailVO.GLOBAL_CURRINFO.equals(key)) {
				if (pk_org != null && assume_org != null) {
					if (!pk_org.equals(assume_org)) {
						boolean isEnable = MatterAppUiUtil.getGlobalRateEnableStatus(assume_org, pk_currtype);
						billForm.getBillCardPanel().getBodyItem(key).setEnabled(isEnable);
					} else {
						billForm.getBillCardPanel().getBodyItem(key).setEnabled(false);
					}
				} else {
					billForm.getBillCardPanel().getBodyItem(key).setEnabled(false);
				}
			}

			try {
				MatterAppUiUtil.crossCheck(key, billForm, "N");
			} catch (BusinessException e1) {
				ExceptionHandler.handleExceptionRuntime(e1);
				return false;
			}
		}
		
		// 事件转换，且发出事件 
		return billForm.getEventTransformer().beforeEdit(e);
		
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
