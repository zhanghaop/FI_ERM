package nc.ui.er.reimrule.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.funcnode.ui.AbstractFunclet;
import nc.funcnode.ui.FuncletInitData;
import nc.ui.er.reimrule.dialog.BatchEditDialog;
import nc.ui.er.reimrule.model.TreeModelManager;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.er.djlx.DjLXVO;


/**
 * 按钮配置的动作
 * 
 * @author shiwla
 *
 */
public class ConfigAction extends ControlAction {

	private static final long serialVersionUID = -2886050746728516317L;
	public ConfigAction() {
		super();
		setCode(ErmActionConst.Config);//配置按钮
		setBtnName(ErmActionConst.getConfigName());
	}
	
	@SuppressWarnings("restriction")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BatchEditDialog dialog = new BatchEditDialog(this.getModel().getContext().getEntranceUI());
		String tradeType = null;
		if (getModel().getSelectedData() != null) {
			tradeType = ((DjLXVO) getModel().getSelectedData()).getDjlxbm();
		}
        String orgtypecode =
                ((AbstractFunclet) this.getModel().getContext().getEntranceUI()).getFuncletContext()
                        .getFuncRegisterVO().getOrgtypecode();
        dialog.setOrgtypecode(orgtypecode);
        // 对话框标题
        dialog.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000041")/** @* res*"报销标准配置"*/);
        FuncletInitData data = new FuncletInitData(-1, dialog);
        Dimension dimension = new Dimension(650,500);
        dialog.setLocationRelativeTo(getEditor());
        dialog.initUI(getInfo(),tradeType,getModel().getContext(), getFilepath(), data, dimension);
        dialog.setResizable(true);
        dialog.showModal();
        dialog.destroy();
        refresh();
	}
	
	protected void refresh(){
		//根据SELECTION_CHANGED事件切换模板
		((TreeModelManager)getManager()).getCtrlbilltableModel().fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED,
				getModel(),null));
		//初始化模型
		((TreeModelManager)getManager()).initData();
	}
}
