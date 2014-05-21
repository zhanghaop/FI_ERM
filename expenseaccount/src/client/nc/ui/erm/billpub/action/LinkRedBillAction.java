package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.link.LinkQuery;
/**
 * 联查红冲的单据
 * @author wangled
 *
 */
public class LinkRedBillAction extends NCAction{
	private static final long serialVersionUID = 1L;
	
	private BillManageModel model;
	private BillForm editor;
	
	public LinkRedBillAction(){
		setCode(ErmActionConst.LinkRed);
		setBtnName(ErmActionConst.getLinkRedName());
	}
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if(selectedData == null || selectedData.getParentVO().getRedbillpk()==null){
			return;
		}
		JKBXHeaderVO parentVO = selectedData.getParentVO();
		String djdl = parentVO.getDjdl();
		String redbillpk = parentVO.getRedbillpk();
		
		LinkQuery linkQuery = new LinkQuery(djdl, new String[]{redbillpk});
		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE,getEditor(), linkQuery);

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
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null
				|| selectedData.getParentVO().getDjzt() == BXStatusConst.DJZT_Invalid
				|| selectedData.getParentVO().getRedbillpk()==null) {
			return false;
		}
		return true;
	}

}
