package nc.ui.er.reimrule.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import nc.funcnode.ui.FuncletContext;
import nc.funcnode.ui.FuncletInitData;
import nc.ui.pubapp.uif2app.FuncletDialog;
import nc.ui.uif2.ToftPanelAdaptor;
import nc.ui.uif2.UIFMenuFactory;
import nc.vo.pub.SuperVO;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.uif2.LoginContext;

@SuppressWarnings({ "serial", "restriction" })
public class BatchEditDialog extends FuncletDialog {
	private List<? extends SuperVO>  returnvo;
	public BatchEditDialog(Container parent) {
        super(parent);
    }

    public void initUI(String info,String djlxbm,LoginContext contex, 
    		String configPath, FuncletInitData data, Dimension dimension) {
    	FuncRegisterVO frVO = new FuncRegisterVO();
        ToftPanelAdaptor tpAdaptor = (ToftPanelAdaptor) contex.getEntranceUI();
        String funcode =
            tpAdaptor.getFuncletContext().getFuncRegisterVO().getFuncode();

        frVO.setClass_name("nc.ui.pubapp.uif2app.ToftPanelAdaptorDialog");
        frVO.setFuncode(funcode);
        frVO.setOrgtypecode(this.getOrgtypecode());

        FuncletContext context = new FuncletContext(frVO);
        ReimToftPanelAdaptorDialog toftPanel = new ReimToftPanelAdaptorDialog();
        toftPanel.setConfigPath(configPath);
        toftPanel.init(context);
        toftPanel.getOrgPanel().getRefPane().getUITextField().setValue(djlxbm + ";" + contex.getPk_org()+";"+info);
        toftPanel.initData(data);

        setDialogContainer(toftPanel.getContainer());
        UIFMenuFactory menuFactory = new UIFMenuFactory();
        menuFactory.setContext(toftPanel.getContext());
        setMenufactory(menuFactory);
        setActionContributors(toftPanel.getActionContributors());
        setFucLet(toftPanel.getControl());
        setSize(dimension);
        setActions(toftPanel.getContainer(), toftPanel.getActionContributors());
        super.initUI();
    }

	public List<? extends SuperVO> getReturnvo() {
		return returnvo;
	}

	public void setReturnvo(List<? extends SuperVO> returnvo) {
		this.returnvo = returnvo;
	}
}
