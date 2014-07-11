package nc.ui.erm.budgetlink.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.erm.budgetlink.listener.ErmBillListView;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.er.link.LinkQuery;
import nc.vo.pub.BusinessException;

/**
 * Ԥ��������鵥��
 * @author chenshuaia
 *
 */
public class LinkErmBillAction extends NCAction {
	private static final long serialVersionUID = 1L;
	BillManageModel model = null;
	
	ErmBillListView editor = null;
	
	public LinkErmBillAction(){
		super();
		this.setCode("BillLinkQuery");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"pubapp_0", "0pubapp-0130")/* @res "���鵥��" */);
	}
	
	@Override
	public void doAction(ActionEvent event) throws Exception {
		BxDetailLinkQueryVO selectedData = (BxDetailLinkQueryVO) getModel().getSelectedData();
        if(selectedData!=null){
        	String nodeCode = null;
        	if(selectedData.getPk_billtype().equals(BXConstans.BX_DJLXBM)){//������
        		nodeCode = BXConstans.BXMNG_NODECODE;
        	}else if(selectedData.getPk_billtype().equals(BXConstans.JK_DJLXBM)){//��
        		nodeCode = BXConstans.BXMNG_NODECODE;
        	}else if(selectedData.getPk_billtype().equals(ErmBillConst.MatterApp_BILLTYPE)){//���뵥
        		nodeCode = ErmBillConst.MatterApp_FUNCODE;
        	}else if(selectedData.getPk_billtype().equals(ErmBillConst.AccruedBill_Billtype)){//Ԥ�ᵥ
        		nodeCode = ErmBillConst.ACC_NODECODE_MN;
        	}else if(selectedData.getPk_billtype().equals(ErmBillConst.CostShare_BILLTYPE)){//��ת��
        		nodeCode = ErmBillConst.CostShare_FUNCODE;
        	}else if(selectedData.getPk_billtype().equals(ErmBillConst.Expamoritize_BILLTYPE)){//̯����Ϣ
        		nodeCode = BXConstans.EXPAMORTIZE_NODE;
        	}else{
        		throw new BusinessException("��֧�ָõ������͵�������[" + selectedData.getPk_billtype() + "]");
        	}
        	
        	LinkQuery linkQuery = new LinkQuery(selectedData.getPk_billtype(), new String[]{selectedData.getPk_jkbx()});
    		SFClientUtil.openLinkedQueryDialog(nodeCode, getEditor(), linkQuery);
        }
	}
	
	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData() == null){
			return false;
		}
		return super.isActionEnable();
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public ErmBillListView getEditor() {
		return editor;
	}

	public void setEditor(ErmBillListView editor) {
		this.editor = editor;
	}
}
