package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.OutputAction;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;

public class CSERMOutputAction extends OutputAction{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggCostShareVO vo = (AggCostShareVO) getModel().getSelectedData();
		CostShareVO parentVO = (CostShareVO) vo.getParentVO();
		setNodeKey(parentVO.getPk_tradetype());
		PrintEntry entry = createPrintentry();
		if(entry.selectTemplate() == 1)
			entry.output();
	}
}
