package nc.ui.erm.closeaccount.action;


import java.awt.event.ActionEvent;
import nc.ui.org.closeaccbook.CloseAccBookBatchTableModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.org.CloseAccBookVO;

/**
 * 关账
 *
 * @author yaonb
 *
 */
@SuppressWarnings("serial")
public class CloseAccBookAction extends NCAction {
	public static final String ACTION_CODE = "CloseAccBook";
	private CloseAccBookBatchTableModel model = null;

	public CloseAccBookAction() {
		super();
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0055")/*@res "关账"*/);
		setCode(ACTION_CODE);
		putValue(SHORT_DESCRIPTION, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0055")/*@res "关账"*/);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getModel().save(ACTION_CODE, (CloseAccBookVO)getModel().getSelectedData());
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0056")/*@res "关账成功！"*/, getModel().getContext());
	}

	@Override
	protected boolean isActionEnable() {
		CloseAccBookVO closeAccVos = (CloseAccBookVO)getModel().getSelectedData();
		return closeAccVos!=null&&!closeAccVos.getIsclose().booleanValue()&&!closeAccVos.getIsendacc().booleanValue();
	}

	public CloseAccBookBatchTableModel getModel() {
		return model;
	}

	public void setModel(CloseAccBookBatchTableModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

}