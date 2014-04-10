package nc.ui.erm.sharerule.actions;

import java.awt.event.ActionEvent;

import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.editor.IBillCardPanelEditor;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;

public class AddShareruleAction extends AddAction {

	private static final long serialVersionUID = 1L;

	private IBillCardPanelEditor billForm;

	public void doAction(ActionEvent e) throws Exception {
		String pk_org = getModel().getContext().getPk_org();
		if (null == pk_org) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0019")/*@res "财务组织不能为空"*/);
		}
		String pk_group = getModel().getContext().getPk_group();
		getBillForm().getBillCardPanel().getHeadItem(ShareruleVO.PK_GROUP)
				.setDefaultValue(pk_group);
		getBillForm().getBillCardPanel().getHeadItem(ShareruleVO.PK_ORG)
				.setDefaultValue(pk_org);

		super.doAction(e);
	}

	public IBillCardPanelEditor getBillForm() {
		return billForm;
	}

	public void setBillForm(IBillCardPanelEditor billForm) {
		this.billForm = billForm;
	}

}