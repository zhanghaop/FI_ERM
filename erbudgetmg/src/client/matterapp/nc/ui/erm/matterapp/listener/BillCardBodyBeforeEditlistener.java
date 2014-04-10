package nc.ui.erm.matterapp.listener;

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
 * ����༭ǰ
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
		//���������뵥λ��������
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		//��������pk_org
		if(MtAppDetailVO.PK_RESACOSTCENTER.equals(key)){//�ɱ�����
			UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
			String wherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+pk_org+"'"; 
			billForm.filterRefModelWithWherePart(refPane, pk_org, null, wherePart);
		}else if (MtAppDetailVO.PK_WBS.equals(key)) {
			final String pk_project = (String)billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.PK_PROJECT);
			UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
			if (pk_project != null) {
				String wherePart = " pk_project=" + "'" + pk_project + "'";
				//������Ŀ����
				billForm.filterRefModelWithWherePart(refPane, pk_org, null, wherePart);
			}else{
				billForm.filterRefModelWithWherePart(refPane, pk_org, null, "1=0");
			}
		}else if (MtAppDetailVO.PK_IOBSCLASS.equals(key)) {// ��֧��Ŀ�������Ȩ�޿���
			// �༭ǰ����֯
			UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
			refPane.getRefModel().setUseDataPower(true);
			refPane.setPk_org(pk_org);
		}else if(MtAppDetailVO.REASON.equals(key)){
			Object value = billForm.getBodyValue(e.getRow(), MtAppDetailVO.REASON);
			
			if(value != null){//��������ʱ���벻ͬ�����ɣ�Ȼ��˫��������������ֶ�-chenshuaia
				((UIRefPane) item.getComponent()).setPK(value);
			}
			((UIRefPane) item.getComponent()).setPk_org(pk_org);
		}else if(MtAppDetailVO.PK_CHECKELE.equals(key)){//����Ҫ�أ������������Ĺ���
			// ����Ҫ�ظ����������Ĺ���
			UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), key);
			String pk_pcorg = (String) billForm.getBodyItemStrValue(e.getRow(), MtAppDetailVO.PK_PCORG);
			if (pk_pcorg == null) {
				refPane.setWhereString("1=0");
			} else {
				refPane.setWhereString(null);
			}
			refPane.setPk_org(pk_pcorg);
		}else if (item.getComponent() instanceof UIRefPane
				&& ((UIRefPane) item.getComponent()).getRefModel() != null) {
			((UIRefPane) item.getComponent()).setPk_org(pk_org);
		}

		try {
			MatterAppUiUtil.crossCheck(key, billForm, "N");
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		
		return true;
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
