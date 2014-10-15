package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.TemplatePrintAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;

public class PrintOfficialAction extends TemplatePrintAction {
	private static final long serialVersionUID = 1L;

	public PrintOfficialAction() {
		super();
		setCode(ErmActionConst.OFFICALPRINT);
		setBtnName(ErmActionConst.getOfficalprintName());
		putValue(Action.ACCELERATOR_KEY, null);
		putValue(SHORT_DESCRIPTION, ErmActionConst.getOfficalprintName());
	}

	@Override
	public void doAction(ActionEvent e)  throws BusinessException {
		JKBXVO selvo = (JKBXVO) getModel().getSelectedData();
		if (!checkOfficialPrint(selvo))
			return;
		try {
			List<String> funnodes = Arrays.asList(BXConstans.JKBX_COMNODES);
			if( funnodes.contains(getModel().getContext().getNodeCode())){
				setNodeKey(selvo.getParentVO().getDjlxbm());
			}else{
				String djdl = selvo.getParentVO().getDjdl();
				if(BXConstans.JK_DJDL.equals(djdl)){
					setNodeKey(BXConstans.BILLTYPECODE_CLFJK);
				}else{
					setNodeKey(BXConstans.BILLTYPECODE_CLFBX);
				}
			}
			PrintEntry entry = createPrintentry();
			if(entry.selectTemplate() == 1)
				entry.print();
		} catch (Exception e1) {
			//对于升级上来的自定义交易类型特殊处理
			setNodeKey(null);
			PrintEntry entry = createPrintentry();
			if(entry.selectTemplate() == 1)
				entry.print();
		}
	}


	/**
	 * 正式打印检查
	 */
	private boolean checkOfficialPrint(JKBXVO vo) throws BusinessException {

		JKBXHeaderVO head = (JKBXHeaderVO) vo.getParentVO().clone();

		if (!(head.getDjzt().intValue() == BXStatusConst.DJZT_Verified || head
				.getDjzt().intValue() == BXStatusConst.DJZT_Sign)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000017")/** @res
																		 * "只有已审核的单据才能做正式打印"
																		 */);
		}
		try {
			String user = head.getOfficialprintuser();
			if (!StringUtils.isNullWithTrim(user)) {
				throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("2006030102", "UPP2006030102-000405")/** @res
																		 * "不能重复正式打印"
																		 */);
			}

			head.setOfficialprintuser(WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());
			head.setOfficialprintdate(WorkbenchEnvironment.getInstance().getBusiDate());

			head = getIBXBillPublic().updateHeader(head,
					new String[] { JKBXHeaderVO.OFFICIALPRINTDATE,
							JKBXHeaderVO.OFFICIALPRINTUSER });

			vo.setParentVO(head);

			getEditor().getBillCardPanel()
					.setHeadItem(JKBXHeaderVO.OFFICIALPRINTDATE,
							head.getOfficialprintdate());

			getEditor().getBillCardPanel()
					.setHeadItem(JKBXHeaderVO.OFFICIALPRINTUSER,
							head.getOfficialprintuser());
			getEditor().getBillCardPanel().setHeadItem(JKBXHeaderVO.TS,
					vo.getParentVO().getTs());
			
			((ErmBillBillManageModel)getModel()).directlyUpdate(vo);
			
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return true;
	}

	private ErmBillBillForm getEditor() {
		return (ErmBillBillForm) getPrintDlgParentConatiner();
	}

	protected IBXBillPublic getIBXBillPublic() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPublic.class);
	}


}