package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fip.service.FipRelationInfoVO;

/**
 * 费用申请单联查分摊生成的责任凭证、包括关闭的凭证
 * 
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class LinkVoucherAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public LinkVoucherAction() {
		super();
		setCode("LinkVoucher");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000288")/*@res "联查凭证"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO selectedVO = (AggMatterAppVO) getModel().getSelectedData();
		MatterAppVO parentVO = selectedVO.getParentVO();

		// 利润中心+分期日期分组申请单，包装FipRelationInfoVO
		List<FipRelationInfoVO> querylist = new ArrayList<FipRelationInfoVO>();
		
		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(parentVO.getPk_group());
		srcinfovo.setPk_org(parentVO.getPk_org());
		srcinfovo.setRelationID(parentVO.getPrimaryKey());
		srcinfovo.setPk_billtype(parentVO.getPk_tradetype());
		
		querylist.add(srcinfovo);
		
		// 已经关闭的申请单，包装FipRelationInfoVO
		if(selectedVO.getParentVO().getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y){
			FipRelationInfoVO close_srcinfovo = new FipRelationInfoVO();
			close_srcinfovo.setPk_group(parentVO.getPk_group());
			close_srcinfovo.setPk_org(parentVO.getPk_org());
			close_srcinfovo.setRelationID(parentVO.getPrimaryKey()+"_CLOSE");
			close_srcinfovo.setPk_billtype(parentVO.getPk_tradetype());
			
			querylist.add(close_srcinfovo);
		}
		

		try {
			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), 
					querylist.toArray(new FipRelationInfoVO[0]));
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

	}
	
	@Override
	protected boolean isActionEnable() {
		// 选择记录不为空
		return getModel().getSelectedData() != null;
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
