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
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.link.LinkQuery;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class LinkJkdAction extends NCAction{
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;

	
	public LinkJkdAction(){
		super();
		setCode("LinkJkd"); 
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000307")/*@res "联查借款单"*/);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();
		
		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;
		if (selectedVO.getParentVO().getDjdl().equals(BXConstans.JK_DJDL))
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000354")/*
																														 * @res
																														 * "借款类单据不能联查借款单！"
																														 */);

		JKBXHeaderVO parentVO = selectedVO.getParentVO();

		Collection<BxcontrastVO> contrasts = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryContrasts(parentVO);

		if (contrasts == null || contrasts.size() == 0) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0100")/*
																																 * @res
																																 * "报销单没有冲销过借款单，没有借款信息。"
																																 */);

		}
		//冲一张借款单上的多行，还款单联查借款单，应该直接进卡片界面
		String[] oids = VOUtils.changeCollectionToArray(contrasts, BxcontrastVO.PK_JKD);
		List<String> pkset=new ArrayList<String>(); 
		for(int i=0;i<oids.length;i++){
			if(!pkset.contains(oids[i])){
				pkset.add(oids[i]);
			}
		}

		LinkQuery linkQuery = new LinkQuery(BXConstans.JK_DJDL, pkset.toArray(new String[]{}));
		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE,getEditor(), linkQuery);
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
