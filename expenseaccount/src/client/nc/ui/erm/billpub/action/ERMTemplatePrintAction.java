package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class ERMTemplatePrintAction extends ERMTemplatePreviewAction{
	private static final long serialVersionUID = 1L;
	
	public ERMTemplatePrintAction(){
		super();
		ActionInitializer.initializeAction(this, IActionCode.PRINT);
	}
	
	@Override
	public void doAction(ActionEvent e) throws BusinessException {
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
			setDatasource(entry);
			if (entry.selectTemplate() == 1){
				entry.print();
			}
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);//��¼��־()
			// ���������������Զ��彻���������⴦��
			setNodeKey(null);
			PrintEntry entry = createPrintentry();
			setDatasource(entry);
			if (entry.selectTemplate() == 1){
				entry.print();
			}
		}
	}
}
