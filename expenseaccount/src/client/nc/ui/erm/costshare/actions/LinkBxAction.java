package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;

/**
 * @author luolch
 *
 * 联查报销单
 *
 */
@SuppressWarnings("serial")
public class LinkBxAction extends NCAction {
	private BillManageModel model;
	private BillForm editor;
	
	public LinkBxAction() {
		super();
		setCode(ErmActionConst.LINKBX);
		setBtnName(ErmActionConst.getLinkBxName());
	}
	public void doAction(ActionEvent e) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		String[] pks = new String[1];
		//在编辑态时从卡片上得到相应的值
		if(vos==null){
			pks[0] =getEditor().getBillCardPanel().getHeadItem(CostShareVO.SRC_ID).getValueObject().toString();
		}else{
			for (int i = 0; i < pks.length; i++) {
				AggCostShareVO vo=(AggCostShareVO) vos[i];
				pks[i] =  ((CostShareVO)vo.getParentVO()).getSrc_id();
			}
		}
		LinkQuery linkQuery = new LinkQuery(pks);
		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE, this.getModel().getContext().getEntranceUI(), linkQuery);
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null&&model.getUiState()== UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	public BillManageModel getModel() {
		return model;
	}
	public BillForm getEditor() {
		return editor;
	}
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	


}