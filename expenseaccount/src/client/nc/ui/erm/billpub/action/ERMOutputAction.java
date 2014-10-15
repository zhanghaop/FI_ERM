package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.OutputAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;

public class ERMOutputAction extends OutputAction{
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doAction(ActionEvent e){
		JKBXVO vo = (JKBXVO) getModel().getSelectedData();
		try {
			List<String> funnodes = Arrays.asList(BXConstans.JKBX_COMNODES);
			if (funnodes.contains(getModel().getContext().getNodeCode())) {
				setNodeKey(vo.getParentVO().getDjlxbm());
			} else {
				String djdl = vo.getParentVO().getDjdl();
				if (BXConstans.JK_DJDL.equals(djdl)) {
					setNodeKey(BXConstans.BILLTYPECODE_CLFJK);
				} else {
					setNodeKey(BXConstans.BILLTYPECODE_CLFBX);
				}
			}
			PrintEntry entry = createPrintentry();
			if (entry.selectTemplate() == 1)
				entry.output();
		} catch (Exception e1) {
			//对于升级上来的自定义交易类型特殊处理
			setNodeKey(null);
			PrintEntry entry = createPrintentry();
			if (entry.selectTemplate() == 1)
				entry.output();

		}
	}
}
