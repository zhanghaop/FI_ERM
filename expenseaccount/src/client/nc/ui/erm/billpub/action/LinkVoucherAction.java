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
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;

/**
 * ����ƾ֤
 * @author chenshuaia
 *
 */
public class LinkVoucherAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public LinkVoucherAction() {
		super();
		setCode("LinkVoucher");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000288")/*@res "����ƾ֤"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		List<JKBXHeaderVO> queryHeaders = NCLocator.getInstance().lookup(IBXBillPrivate.class)
				.queryHeadersByPrimaryKeys(new String[] { selectedVO.getParentVO().getPk_jkbx() }, selectedVO.getParentVO().getDjdl());

		// ����FipRelationInfoVO
		List<FipRelationInfoVO> srcinfovolist = new ArrayList<FipRelationInfoVO>();
		
		if (queryHeaders != null && queryHeaders.size() != 0) {
			// �ж��ƾ֤-��ƾ֤״̬���в�ѯ
			JKBXHeaderVO headVo = queryHeaders.get(0);
			for (int i = 0; i <= 7; i++) {
				FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
				srcinfovo.setPk_group(headVo.getPk_group());
				srcinfovo.setPk_org(headVo.getPk_payorg());
				srcinfovo.setPk_billtype(headVo.getDjlxbm());
				srcinfovo.setRelationID(headVo.getPk_jkbx() + "_" + i);//pk_voucherTag
				srcinfovolist.add(srcinfovo);
			}
			
			//63֮ǰ��ƾ֤�ǰ�PK��ΪrelationId��
			FipRelationInfoVO srcinfovo = (FipRelationInfoVO)srcinfovolist.get(0).clone();
			srcinfovo.setRelationID(headVo.getPk_jkbx());
			srcinfovolist.add(srcinfovo);
			
			// ���ڷ�̯��ϸ����£�Ҳ��Ҫ���鵽��Ӧ���ý�ת�����ɵ�ƾ֤
			CShareDetailVO[] csharedetailvos = selectedVO.getcShareDetailVo();
			if (csharedetailvos != null && csharedetailvos.length > 0) {
				FipRelationInfoVO costinfovo = new FipRelationInfoVO();
				costinfovo.setPk_group(csharedetailvos[0].getPk_group());
				costinfovo.setPk_org(csharedetailvos[0].getPk_org());
				costinfovo.setRelationID(csharedetailvos[0].getPk_costshare());
				costinfovo.setPk_billtype(csharedetailvos[0].getPk_tradetype());

				srcinfovolist.add(costinfovo);
			}

			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), srcinfovolist.toArray(new FipRelationInfoVO[] {}));
		} else {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000954"));
		}
	}
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null
				|| selectedData.getParentVO().getDjzt() == BXStatusConst.DJZT_Invalid) {
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
