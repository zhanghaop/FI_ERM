package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class LinkBXBillAction extends NCAction{
	private BillManageModel model;
	private BillForm editor;
	public LinkBXBillAction(){
		super();
		setCode("Link");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0058")/*@res "联查报销单"*/);
	}
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO vo = (JKBXVO) getModel().getSelectedData();
		if (vo == null) {
			return;
		}
		
		if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL))
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000355")/*
																														 * @res
																														 * "报销类单据不能联查报销单！"
																														 */);
		Collection<BxcontrastVO> contrasts = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryContrasts(vo.getParentVO());
		if (contrasts == null || contrasts.size() == 0) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000011")/*
																														 * @res
																														 * "所选借款单据未进行冲借款,无法联查报销单!"
																														 */);
		}
		//借款单联查报销单，一张单据时，应联查到卡片界面
		String[] oids = VOUtils.changeCollectionToArray(contrasts, BxcontrastVO.PK_BXD);
		List<String> pkset=new ArrayList<String>(); 
		for(int i=0;i<oids.length;i++){
			if(!pkset.contains(oids[i])){
				pkset.add(oids[i]);
			}
		}

		LinkQuery linkQuery = new LinkQuery(BXConstans.BX_DJDL, pkset.toArray(new String[]{}));

		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE, getEditor(), linkQuery);

	}

	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null
				|| selectedData.getParentVO().getDjzt() == BXStatusConst.DJZT_Invalid
				|| ArrayUtils.isEmpty(selectedData.getContrastVO())) {
			return false;
		}
		return true;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

}