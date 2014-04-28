package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;

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
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;
		
		List<JKBXHeaderVO> queryHeaders = NCLocator.getInstance().
		lookup(IBXBillPrivate.class).queryHeadersByPrimaryKeys(
				new String[]{selectedVO.getParentVO().getPk_jkbx()}, selectedVO.getParentVO().getDjdl());
		
		// 构造FipRelationInfoVO
		List<FipRelationInfoVO> srcinfovolist = new ArrayList<FipRelationInfoVO>();
		//只有一张凭证
		if(queryHeaders!=null && queryHeaders.size()!=0){
			if(queryHeaders.get(0).getVouchertag()==null || (queryHeaders.get(0).getVouchertag()!=null && 
					queryHeaders.get(0).getVouchertag()==BXStatusConst.ZFFlag)){
				FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
				srcinfovo.setPk_group(queryHeaders.get(0).getPk_group());
				srcinfovo.setPk_org(queryHeaders.get(0).getPk_payorg());
				if(queryHeaders.get(0).getVouchertag()==null){//凭证标志为空时，没有凭证
					srcinfovo.setRelationID(queryHeaders.get(0).getPk());
				}else{
					srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+queryHeaders.get(0).getVouchertag());
				}
				srcinfovo.setPk_billtype(queryHeaders.get(0).getDjlxbm());
				srcinfovolist.add(srcinfovo);		
			}else{//有多个凭证
				for(int i =0 ;i<=6 ;i++){
					FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
					srcinfovo.setPk_group(queryHeaders.get(0).getPk_group());
					srcinfovo.setPk_org(queryHeaders.get(0).getPk_payorg());
					if(i==0){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.MEZFFlag);
					}else if(i==1){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.MEDeal);
					}else if(i==2){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.ZGDeal);
					}else if(i==3){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.ZGZFFlag);
					}else if(i==4){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.ZGMEFlag);
					}else if(i==5){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.ZGMEZFFlag);
					}else if(i==6){
						srcinfovo.setRelationID(queryHeaders.get(0).getPk()+"_"+BXStatusConst.SXFlag);
					}
					srcinfovo.setPk_billtype(queryHeaders.get(0).getDjlxbm());
					srcinfovolist.add(srcinfovo);	
				}
			}
			try {
				CShareDetailVO[] csharedetailvos = selectedVO.getcShareDetailVo();
				if (csharedetailvos != null && csharedetailvos.length > 0) {
					// 存在分摊明细情况下，也需要联查到对应费用结转单生成的凭证
					FipRelationInfoVO costinfovo = new FipRelationInfoVO();
					costinfovo.setPk_group(csharedetailvos[0].getPk_group());
					costinfovo.setPk_org(csharedetailvos[0].getPk_org());
					costinfovo.setRelationID(csharedetailvos[0].getPk_costshare());
					costinfovo.setPk_billtype(csharedetailvos[0].getPk_tradetype());
					
					srcinfovolist.add(costinfovo);
					FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), srcinfovolist.toArray(new FipRelationInfoVO[]{}));
				} else {
					FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), srcinfovolist.toArray(new FipRelationInfoVO[]{}));
					// --end
				}
			} catch (Exception ex) {
				ExceptionHandler.consume(ex);
			}
		}else{
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000954"));
			
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
