package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * @author chenshuaia
 *
 *         ��ʽ��ӡ
 */
@SuppressWarnings( { "serial" })
public class PrintOfficalAction extends nc.ui.erm.matterapp.actions.PreViewAction {
	public PrintOfficalAction() {
		super();
		this.putValue(Action.ACCELERATOR_KEY, null);
		setBtnName(ErmActionConst.getOfficalprintName());
		setCode(ErmActionConst.OFFICALPRINT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO aggVO = (AggMatterAppVO) getModel().getSelectedData();
		// ���VO
		checkOfficalPrint(aggVO);
		// ��ӡ
		super.doAction(e);

		// ���´�ӡ��Ϣ
		aggVO.getParentVO().setPrinter(ErUiUtil.getPk_user());
		aggVO.getParentVO().setPrintdate(ErUiUtil.getBusiDate());

		MatterAppVO parent = NCLocator.getInstance().lookup(IErmMatterAppBillManage.class).updatePrintInfo(
				aggVO.getParentVO());
		aggVO.setParentVO(parent);
		((BillManageModel) getModel()).directlyUpdate(aggVO);
	}

	private boolean checkOfficalPrint(AggMatterAppVO aggVO) throws BusinessException{
		if(aggVO == null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0016")/*@res "��ѡ�񵥾�"*/);
		}

		MatterAppVO head = (MatterAppVO) aggVO.getParentVO();

		if (head.getBillstatus() == null || !(head.getBillstatus().intValue() == BXStatusConst.DJZT_Sign)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0017")/*@res "ֻ������˵ĵ��ݲ�������ʽ��ӡ"*/);
		}

		String user = head.getPrinter();
		if (!StringUtils.isNullWithTrim(user)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0018")/*@res "�����ظ���ʽ��ӡ"*/);
		}

		return true;
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
													 * @ res "{0}ʧ�ܣ�"
													 */;
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(null);
	}
}