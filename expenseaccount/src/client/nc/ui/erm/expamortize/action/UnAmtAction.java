package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableCellEditor;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortize;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uif2.LoginContext;
/**
 * 取消摊销按钮
 * @author jiawh
 *
 */
public class UnAmtAction extends NCAction{
	private static final long serialVersionUID = -4519228282257103266L;

	private BillManageModel model;
	
	private BillListView listView;

	public UnAmtAction() {
		super();
		this.setBtnName("取消摊销");
		setCode("unAmt");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		TableCellEditor cellEditor=getListView().getBillListPanel().getParentListPanel().getTable().getCellEditor();
		if(cellEditor != null){
			cellEditor.stopCellEditing();
		}
//		BillModel billModel=getListView().getBillListPanel().getHeadBillModel();
//		Object[] vos = billModel.getBodySelectedVOs(ExpamtinfoVO.class.getName());
		Object[] vos = getModel().getSelectedOperaDatas();
		
		String period = ((ExpamorizeManageModel) getModel()).getPeriod();
		
		if(vos ==null || vos .length ==0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0068")/* @res "没有选择摊销信息" */);
		}
		
		List<ExpamtinfoVO> expamtinfoList=new ArrayList<ExpamtinfoVO>();
		for (Object vo : vos) {
			ExpamtinfoVO expamtinfoVO=(ExpamtinfoVO)vo;
			
			//待摊销不需要取消摊销
			if(!expamtinfoVO.getAmt_status().booleanValue()){
				continue;
			}
			expamtinfoList.add(expamtinfoVO);
		}
		
		if (expamtinfoList.size() == 0) {
			throw new BusinessException("没有可以取消的摊销信息");
		}
		
		MessageVO[] messageVos = getExpAmortize().unAmortize(getContext().getPk_org(), period, expamtinfoList.toArray(new ExpamtinfoVO[]{}));

		List<ExpamtinfoVO> successVos = new ArrayList<ExpamtinfoVO>();

		for (int i = 0; i < messageVos.length; i++) {
			if (messageVos[i].isSuccess()) {
				successVos.add((ExpamtinfoVO) messageVos[i].getSuccessVO().getParentVO());
			}
		}

		// 更新表格数据
		getModel().directlyUpdate(successVos.toArray(new ExpamtinfoVO[] {}));
		ErUiUtil.showBatchResults(getModel().getContext(), messageVos);
		
	}

	private IExpAmortize getExpAmortize() {
		return NCLocator.getInstance().lookup(IExpAmortize.class);
	}

	public LoginContext getContext() {
		return getModel().getContext();
	}
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	@Override
	protected boolean isActionEnable() {
		Object[] selectedData = getModel().getSelectedOperaDatas();
		if (selectedData == null) {
			return false;
		}

		for (int i = 0; i < selectedData.length; i++) {
			ExpamtinfoVO expamtVo = (ExpamtinfoVO) selectedData[i];
			UFBoolean amtStatus = ((ExpamtinfoVO) expamtVo).getAmt_status();
			// 已摊销
			if (amtStatus != null && amtStatus.equals(UFBoolean.TRUE)) {
				return true;
			}
		}

		return false;
	}
	
}
