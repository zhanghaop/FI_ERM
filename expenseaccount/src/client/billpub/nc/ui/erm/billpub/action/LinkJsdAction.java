package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.sm.funcreg.FuncRegisterVO;

public class LinkJsdAction extends NCAction {
	
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	
	
	public LinkJsdAction(){
		super();
		setCode("LinkJsd");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000355")/*@res "联查往来单"*/);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {

		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();
		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		if (!selectedVO.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Sign)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000012")/*
																														 * @res
																														 * "单据未生效,无法联查往来单据!"
																														 */);
		}

		Collection<JsConstrasVO> contrasts = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryJsContrasts(selectedVO.getParentVO());

		if (contrasts == null || contrasts.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000013")/*
																														 * @res
																														 * "联查收付往来单据失败,未生成收付往来单据!"
																														 */);
		}

		List<JsConstrasVO> contrasts0 = new ArrayList<JsConstrasVO>();
		List<JsConstrasVO> contrasts1 = new ArrayList<JsConstrasVO>();

		for (JsConstrasVO contrast : contrasts) {
			if (contrast.getBillflag() == 0) {
				contrasts0.add(contrast);
			} else {
				contrasts1.add(contrast);
			}

		}
		String[] oidsys = VOUtils.changeCollectionToArray(contrasts0, JsConstrasVO.PK_JSD);
		String[] oidsyf = VOUtils.changeCollectionToArray(contrasts1, JsConstrasVO.PK_JSD);

		if (oidsys == null || oidsys.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000013")/*
																														 * @res
																														 * "联查收付往来单据失败,未生成收付往来单据!"
																														 */);
		}
		FuncRegisterVO registerVO = WorkbenchEnvironment.getInstance().getFuncRegisterVO(BXConstans.FI_AR_MNGFUNCODE);

		if (registerVO == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000435")/*
																														 * @res
																														 * "联查收付往来单据失败,没有对应节点的权限!"
																														 */);
		}

		PfLinkData link = new PfLinkData();
		link.setUserObject(oidsys);
		if (oidsys[0] != null) {
			link.setBillID(oidsys[0]);
		}

		// 联查应收单
		FuncletInitData initData = new FuncletInitData();
		initData.setInitType(ILinkType.LINK_TYPE_QUERY);
		initData.setInitData(link);
		FuncletWindowLauncher.openFuncNodeInTabbedPane(getEditor(), registerVO, initData, null, false);
		if (oidsyf == null || oidsyf.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000013")/*
																														 * @res
																														 * "联查收付往来单据失败,未生成收付往来单据!"
																														 */);
		}

		// 联查应付单
		initData = new FuncletInitData();
		FuncRegisterVO registerVO1 = WorkbenchEnvironment.getInstance().getFuncRegisterVO(BXConstans.FI_AP_MNGFUNCODE);
		link = new PfLinkData();
		link.setUserObject(oidsyf);
		if (oidsyf[0] != null) {
			link.setBillID(oidsyf[0]);
		}
		initData.setInitType(ILinkType.LINK_TYPE_QUERY);
		initData.setInitData(link);
		FuncletWindowLauncher.openFuncNodeInTabbedPane(getEditor(), registerVO1, initData, null, false);

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
	
	
}
