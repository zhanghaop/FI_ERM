package nc.ui.er.reimrule.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.funcnode.ui.AbstractFunclet;
import nc.funcnode.ui.FuncletInitData;
import nc.ui.er.reimrule.dialog.BatchEditDialog;
import nc.ui.er.reimrule.model.TreeModelManager;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.er.djlx.DjLXVO;


/**
 * 按钮配置的动作
 * 
 * @author shiwla
 *
 */
public class ControlAction extends NCAction {

	private static final long serialVersionUID = -2886050746728516317L;
	private BatchBillTable editor = null;
	private AbstractAppModel model;

	//是否配置按钮
	private IAppModelDataManager manager;
	//是否配置按钮
	private String filepath;
	//是否配置按钮
	private String info;
	
	public ControlAction() {
		super();
		setCode(ErmActionConst.Control);//控制设置
		setBtnName(ErmActionConst.getControlName());
	}
	
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
        dialog.setTitle("报销标准配置");
        FuncletInitData data = new FuncletInitData(-1, dialog);
        Dimension dimension = new Dimension(700,500);
        dialog.setLocationRelativeTo(getEditor());
        dialog.initUI(getInfo(),tradeType,getModel().getContext(), getFilepath(), data, dimension);
        dialog.setResizable(true);
        dialog.showModal();
        dialog.destroy();
        refresh();
	}
	
	protected void refresh(){
		//初始化模型
		((TreeModelManager)getManager()).initData();
	}
	protected String getFilePath(boolean isConfig){
		if(isConfig)
			return "nc/ui/er/reimrule/config/reimconfig_config.xml";
		else
			return "nc/ui/er/reimrule/config/reimcontrol_config.xml";
		
	}
	
	@Override 
	protected boolean isActionEnable() {
		return getModel().getUiState() == UIState.NOT_EDIT && getModel().getSelectedData()!=null;
	}
	
	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}
	
	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public IAppModelDataManager getManager() {
		return manager;
	}

	public void setManager(IAppModelDataManager manager) {
		this.manager = manager;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
