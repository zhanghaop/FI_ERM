package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.ntb.IErmLinkBudgetService;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.tb.obj.NtbParamVO;

@SuppressWarnings("restriction")
public class LinkYsAction extends NCAction {
	private static final long serialVersionUID = 1L;
	
	private BillManageModel model;
	private BillForm editor;

	public LinkYsAction() {
		super();
		setCode("LinkYs");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPTcommon-000303")/*
																									 * @
																									 * res
																									 * "预算执行情况"
																									 */);

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO bxvo = (JKBXVO) getModel().getSelectedData();

		if (bxvo == null || bxvo.getParentVO().getPk_jkbx() == null)
			return;

		boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused) {
			throw new BusinessShowException(getNoInstallMsg());
		}
		// 拉单的借款单不走预算，所以不用联查预算
		if(BXConstans.JK_DJDL.equals(bxvo.getParentVO().getDjdl())&&!StringUtil.isEmpty(bxvo.getParentVO().getPk_item())){
			throw new BusinessShowException(getNoResultMsg());
		}
		// 处理联查预算业务
		String actionCode = getActionCode(bxvo);
		try {
			NtbParamVO[] vos = NCLocator.getInstance().lookup(IErmLinkBudgetService.class)
					.getBudgetLinkParams(bxvo, actionCode, getEditor().getModel().getContext().getNodeCode());

			if (null == vos || vos.length == 0) {
				throw new BusinessShowException(getNoResultMsg());
			}
			
			NtbParamVOChooser chooser = new NtbParamVOChooser(getEditor(), nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"2006030102", "UPP2006030102-000430")/**
					 * @res "预算执行情况"
					 */
			);
			chooser.setParamVOs(vos);
			chooser.showModal();
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);
		}

	}

	private String getActionCode(JKBXVO bxvo) {
		JKBXHeaderVO headVO = bxvo.getParentVO();
		int billStatus = headVO.getDjzt();
		switch (billStatus) {
			case BXStatusConst.DJZT_Sign:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			case BXStatusConst.DJZT_Verified:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			default:
				return BXConstans.ERM_NTB_SAVE_KEY;
		}
	}

	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null) {
			return false;
		}

		return true;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public String getNoResultMsg() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
		"02011v61013-0066")/*
		 * @res "没有符合条件的预算数据!"
		 */;
	}

	public String getNoInstallMsg() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0004")/*
																									 * @
																									 * res
																									 * "没有安装预算产品，不能联查预算执行情况！"
																									 */;
	}
}
