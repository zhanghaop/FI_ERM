package nc.ui.erm.matterapp.actions.ext;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.matterapp.ext.MtappVOGroupHelper;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fip.service.FipRelationInfoVO;

/**
 * �������뵥������ڷ�̯���ɵ�����ƾ֤�������رյ�ƾ֤
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class LinkVoucherActionExt extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public LinkVoucherActionExt() {
		super();
		setCode("LinkVoucher");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000288")/*@res "����ƾ֤"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO selectedVO = (AggMatterAppVO) getModel().getSelectedData();

		// ��������+�������ڷ������뵥����װFipRelationInfoVO
		List<FipRelationInfoVO> querylist = new ArrayList<FipRelationInfoVO>();
		Map<String, List<AggMatterAppVO>> groupPcorgVOs = MtappVOGroupHelper.groupPcorgVOs(selectedVO);
		for (Entry<String, List<AggMatterAppVO>> groupvos : groupPcorgVOs.entrySet()) {
			List<AggMatterAppVO> vos = groupvos.getValue();
			for (AggMatterAppVO aggvo : vos) {
				FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
				MatterAppVO parentVO = aggvo.getParentVO();
				srcinfovo.setPk_group(parentVO.getPk_group());
				srcinfovo.setPk_org(parentVO.getPk_org());
				srcinfovo.setRelationID(parentVO.getPrimaryKey());
				srcinfovo.setPk_billtype(parentVO.getPk_tradetype());
				
				querylist.add(srcinfovo);
			}
		}
		// �������ķ����Ѿ��رյ����뵥����װFipRelationInfoVO
		List<AggMatterAppVO> closevos = MtappVOGroupHelper.getCloseVOs(selectedVO);
		for (AggMatterAppVO aggvo : closevos) {
			FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
			MatterAppVO parentVO = aggvo.getParentVO();
			srcinfovo.setPk_group(parentVO.getPk_group());
			srcinfovo.setPk_org(parentVO.getPk_org());
			srcinfovo.setRelationID(parentVO.getPrimaryKey());
			srcinfovo.setPk_billtype(parentVO.getPk_tradetype());
			
			querylist.add(srcinfovo);
		}
		

		try {
			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), 
					querylist.toArray(new FipRelationInfoVO[querylist.size()]));
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}

	}
	
	@Override
	protected boolean isActionEnable() {
		// ѡ���¼��Ϊ��
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
