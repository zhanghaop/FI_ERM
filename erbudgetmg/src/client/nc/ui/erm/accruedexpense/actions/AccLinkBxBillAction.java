package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class AccLinkBxBillAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public AccLinkBxBillAction(){
		setCode(ErmActionConst.LINKBX);
		setBtnName(ErmActionConst.getLinkBxName());
	}

	@Override
	public void doAction(ActionEvent arg0) throws BusinessException {
		AggAccruedBillVO selectedData = (AggAccruedBillVO) getModel().getSelectedData();
		if(selectedData == null){
			return;
		}
		if(ArrayUtils.isEmpty(selectedData.getAccruedVerifyVO())){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0000")/*@res "此单据无核销明细，不能联查报销单"*/);
		}
		List<String> bxpks=new ArrayList<String>();
		for (AccruedVerifyVO verifyVO : selectedData.getAccruedVerifyVO()) {
			if (!bxpks.contains(verifyVO.getPk_bxd())) {
				bxpks.add(verifyVO.getPk_bxd());
			}
		}

		LinkQuery linkQuery = new LinkQuery(BXConstans.BX_DJDL, bxpks.toArray(new String[]{}));

		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE, getEditor(), linkQuery);


	}

	@Override
	protected boolean isActionEnable() {
		AggAccruedBillVO selectedData = (AggAccruedBillVO) getModel().getSelectedData();
		if (selectedData == null
				|| ArrayUtils.isEmpty(selectedData.getAccruedVerifyVO())) {
			return false;
		}

		return true;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}


}