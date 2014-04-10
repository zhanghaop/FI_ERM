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
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selvo = (JKBXVO) getModel().getSelectedData();
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
		if (!checkOfficialPrint(selvo))
				return;
		super.doAction(e);
	}


	/**
	 * ��ʽ��ӡ���
	 */
	private boolean checkOfficialPrint(JKBXVO vo) throws BusinessException {

		JKBXHeaderVO head = (JKBXHeaderVO) vo.getParentVO().clone();

		if (!(head.getDjzt().intValue() == BXStatusConst.DJZT_Verified || head
				.getDjzt().intValue() == BXStatusConst.DJZT_Sign)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000017")/** @res
																		 * "ֻ������˵ĵ��ݲ�������ʽ��ӡ"
																		 */);
		}
		try {
			String user = head.getOfficialprintuser();
			if (!StringUtils.isNullWithTrim(user)) {
				throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("2006030102", "UPP2006030102-000405")/** @res
																		 * "�����ظ���ʽ��ӡ"
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