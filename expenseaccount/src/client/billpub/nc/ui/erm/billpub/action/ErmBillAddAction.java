package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.uif2.actions.AddAction;
import nc.vo.pub.BusinessException;

public class ErmBillAddAction extends AddAction {

	private static final long serialVersionUID = 1L;
	
	boolean isFirstOnAdd = true;
	private ErmBillBillForm editor;
	
	public ErmBillAddAction(){
	   // setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0141")/*@res "自制"*/);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		check();
		super.doAction(e);
	}
	
	/**
	 * 检测登录用户是否关联人员
	 * 检测单据类型是否已被封存
	 * @throws BusinessException:
	 */
	private void check() throws BusinessException {
		String pkPsn = BXUiUtil.getPk_psndoc();
		if (pkPsn == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2011ermpub0316_0", "02011ermpub0316-0000")/*
																 * * @res*
																 * "当前用户未关联人员，请联系管理人员为此用户指定身份"
																 */);
		}

		String selectBillTypeCode = ((ErmBillBillManageModel) getModel())
				.getSelectBillTypeCode();
		if (((ErmBillBillManageModel) getModel()).getCurrentDjlx(
				selectBillTypeCode).getFcbz().booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000171")/**
			 * @res*
			 *      "该节点单据类型已被封存，不可操作节点！"
			 */
			);
		}
	}
	
	/**
	 * 根据key返回客户端缓存value值
	 * 
	 * @author wangled
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public Object getCacheValue(final String key) {
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	public boolean isFirstOnAdd() {
		return isFirstOnAdd;
	}

	public void setFirstOnAdd(boolean isFirstOnAdd) {
		this.isFirstOnAdd = isFirstOnAdd;
	}

	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}

}
