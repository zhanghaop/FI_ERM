package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.funcnode.ui.FuncletInitData;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.uap.sf.SFClientUtil2;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

public class LinkAccBillAction extends NCAction {

	private static final long serialVersionUID = 1L;
	
	private BillManageModel model;
	private BillForm editor;
	
	public LinkAccBillAction(){
		setCode(ErmActionConst.LinkAcc);
		setBtnName(ErmActionConst.getLinkAccName());
	}

	@Override
	public void doAction(ActionEvent arg0) throws BusinessException {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if(selectedData == null){
			return;
		}
		if(ArrayUtils.isEmpty(selectedData.getAccruedVerifyVO())){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0117")/*
					 * @res
					 * "此单据无核销明细，不能联查预提单"
					 */);
		}
		
		List<String> accpks=new ArrayList<String>(); 
		for (AccruedVerifyVO verifyVO : selectedData.getAccruedVerifyVO()) {
			if (!accpks.contains(verifyVO.getPk_accrued_bill())) {
				accpks.add(verifyVO.getPk_accrued_bill());
			}
		}
		
		LinkQuery linkQuery = new LinkQuery(ErmAccruedBillConst.DJDL, accpks.toArray(new String[]{}));
//		SFClientUtil.openLinkedQueryDialog(ErmAccruedBillConst.ACC_NODECODE_MN, getEditor(), linkQuery);
		FuncletInitData initData = new FuncletInitData();
		initData.setInitData(linkQuery);
		initData.setInitType(ILinkType.LINK_TYPE_QUERY);
		SFClientUtil2.openFuncNodeDialog(getEditor(), ErmAccruedBillConst.ACC_NODECODE_MN, initData, null, false,
				false, null, new String[] { ErmConst.BUSIACTIVE_LINKQUERY });
	}

	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null
				|| selectedData.getParentVO().getDjzt() == BXStatusConst.DJZT_Invalid
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
