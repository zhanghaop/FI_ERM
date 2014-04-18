package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import nc.bs.logging.Log;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class LinkLimitAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	
	public LinkLimitAction() {
		super();
		setCode("LinkLimit");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000309")/*@res "联查报销标准"*/);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		JKBXHeaderVO parentVO = selectedVO.getParentVO();
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(parentVO.getDjlxbm(), parentVO.getDjdl());

		final String strUrl = busTypeVO.getLimit();
		if (strUrl == null || strUrl.trim().length() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0008")/*
																															 * @res
																															 * "联查报销标准出错，没有配置URL超链接"
																															 */);
		}
		URL url = null;
		try {
			url = new URL(strUrl);
			nc.sfbase.client.ClientToolKit.showDocument(url, "_blank");
		} catch (MalformedURLException ex) {
			Log.getInstance(this.getClass()).error(ex.getMessage(), ex);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0007")/*
																															 * @res
																															 * "非法的URL超链接"
																															 */);
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
	
	

}
