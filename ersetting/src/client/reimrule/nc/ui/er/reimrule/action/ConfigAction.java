package nc.ui.er.reimrule.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import nc.funcnode.ui.AbstractFunclet;
import nc.funcnode.ui.FuncletInitData;
import nc.ui.er.reimrule.dialog.BatchEditDialog;
import nc.ui.er.reimrule.model.TreeModelManager;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.er.djlx.DjLXVO;


/**
 * ��ť���õĶ���
 * 
 * @author shiwla
 *
 */
public class ConfigAction extends ControlAction {

	private static final long serialVersionUID = -2886050746728516317L;
	public ConfigAction() {
		super();
		setCode("Config");
		setBtnName("����");
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
        // �Ի������
        dialog.setTitle("������׼����");
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
		//����SELECTION_CHANGED�¼��л�ģ��
		((TreeModelManager)getManager()).getCtrlbilltableModel().fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED,
				getModel(),null));
		//��ʼ��ģ��
		((TreeModelManager)getManager()).initData();
	}
}
