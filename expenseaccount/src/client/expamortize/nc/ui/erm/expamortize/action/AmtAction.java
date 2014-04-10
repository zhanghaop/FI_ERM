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
import nc.vo.uif2.LoginContext;

/**
 * 摊销按钮
 * @author wangled
 *
 */
public class AmtAction extends NCAction {
	private static final long serialVersionUID = -6255671246340124514L;

	private BillManageModel model;
	
	private BillListView listView;

	public AmtAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0067")/*@res "摊销"*/);
		setCode("Amt");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		TableCellEditor cellEditor = getListView().getBillListPanel().getParentListPanel().getTable().getCellEditor();
		if(cellEditor != null){
			cellEditor.stopCellEditing();
		}
		BillModel billModel = getListView().getBillListPanel().getHeadBillModel();
		Object[] vos = billModel.getBodySelectedVOs(ExpamtinfoVO.class.getName());
		
//		getListView().getBillListPanel().getSelectedVO(AggExpamtinfoVO.class.getName(), ExpamtinfoVO.class.getName(), ExpamtDetailVO.class.getName());
		String period = ((ExpamorizeManageModel) getModel()).getPeriod();
//		Object[] vos =  getModel().getSelectedOperaDatas();
		if (vos == null || vos.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0068")/* @res "没有选择摊销信息" */);
		}
		
		List<ExpamtinfoVO> expamtList = new ArrayList<ExpamtinfoVO>();
		for (int i = 0; i < vos.length; i++) {
			expamtList.add((ExpamtinfoVO)vos[i]);
		}

		MessageVO[] messageVo = getExpAmortize().amortize(getContext().getPk_org(), period, expamtList.toArray(new ExpamtinfoVO[]{}));

		List<ExpamtinfoVO> successVos = new ArrayList<ExpamtinfoVO>();

		for (int i = 0; i < messageVo.length; i++) {
			if (messageVo[i].isSuccess()) {
				successVos.add((ExpamtinfoVO) messageVo[i].getSuccessVO().getParentVO());
			}
		}

		// 更新表格数据
		getModel().directlyUpdate(successVos.toArray(new ExpamtinfoVO[] {}));
		ErUiUtil.showBatchResults(getModel().getContext(), messageVo);
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
}