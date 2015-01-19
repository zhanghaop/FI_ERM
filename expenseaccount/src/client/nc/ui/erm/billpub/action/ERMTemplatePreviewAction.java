package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.erm.billpub.model.ErmDataSingleSelectDataSource;
import nc.ui.pub.print.IDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.TemplatePreviewAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * 
 * 
 */
public class ERMTemplatePreviewAction extends TemplatePreviewAction {

	public ERMTemplatePreviewAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.PREVIEW);
	}

	private static final long serialVersionUID = 1L;

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
				entry.preview();
			}
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);//记录日志()
			// 对于升级上来的自定义交易类型特殊处理
			setNodeKey(null);
			PrintEntry entry = createPrintentry();
			setDatasource(entry);
			if (entry.selectTemplate() == 1){
				entry.preview();
			}
		}
	}

	public void setDatasource(PrintEntry entry) throws BusinessException {
		BillManageModel model = (BillManageModel) getModel();
		JKBXVO[] vos = null;

		if (getModel() instanceof BillManageModel) {
			Object[] objs = model.getSelectedOperaDatas();
			if (objs != null) {
				vos = new JKBXVO[objs.length];
				for (int i = 0; i < vos.length; i++) {
					vos[i] = (JKBXVO) objs[i];
				}
			}
		}

		if ((((vos == null) || (vos.length == 0)))) {
			vos = new JKBXVO[] { (JKBXVO) model.getSelectedData() };
		}

		if (vos != null && vos.length > 0) {
			for (JKBXVO vo : vos) {
				if ((vo.getChildrenVO() == null || vo.getChildrenVO().length == 0) 
						&& (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0)) {
					//补齐表体信息
					vo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(vo.getParentVO());
				}
				
				ErmDataSingleSelectDataSource dataSource = new ErmDataSingleSelectDataSource(vo);
				dataSource.setModel(getModel());
				entry.setDataSource(dataSource);
			}
		}
	}

	@Override
	public IDataSource getDatasource() {
		return null;
	}
}
