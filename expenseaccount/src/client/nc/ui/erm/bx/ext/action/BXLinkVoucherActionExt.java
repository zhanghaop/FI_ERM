package nc.ui.erm.bx.ext.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.ext.common.CostshareVOGroupHelper;
import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.ui.erm.billpub.action.LinkVoucherAction;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fip.service.FipRelationInfoVO;

@SuppressWarnings("restriction")
public class BXLinkVoucherActionExt extends LinkVoucherAction {

	private static final long serialVersionUID = 1L;


	@Override
	public void doAction(ActionEvent e) throws Exception {
	

		JKBXVO bxvo = (JKBXVO) getModel().getSelectedData();
		
		if(ErmConstExt.Distributor_BX_Tradetype.equals(bxvo.getParentVO().getDjlxbm())){
			// �����̵渶���������ڡ���̯����Ԥ��
			cxx1BxlinkVoucher(bxvo);
		}else{
			super.doAction(e);
		}

	}

	private void cxx1BxlinkVoucher(JKBXVO selectedVO) {
		// ����FipRelationInfoVO
		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		JKBXHeaderVO bxparentVO = selectedVO.getParentVO();
		srcinfovo.setPk_group(bxparentVO.getPk_group());
		srcinfovo.setPk_org(bxparentVO.getPk_payorg());
		srcinfovo.setRelationID(bxparentVO.getPk());
		srcinfovo.setPk_billtype(bxparentVO.getDjlxbm());

		try {
			CShareDetailVO[] csharedetailvos = selectedVO.getcShareDetailVo();
			if (csharedetailvos != null && csharedetailvos.length > 0) {
				List<FipRelationInfoVO> querylist = new ArrayList<FipRelationInfoVO>();
				querylist.add(srcinfovo);
				// ���ڷ�̯��ϸ����£�Ҳ��Ҫ���鵽��Ӧ���ý�ת�����ɵ�ƾ֤
				FipRelationInfoVO costinfovo = new FipRelationInfoVO();
				costinfovo.setPk_group(csharedetailvos[0].getPk_group());
				costinfovo.setPk_org(csharedetailvos[0].getPk_org());
				costinfovo.setRelationID(csharedetailvos[0].getPk_costshare());
				costinfovo.setPk_billtype(csharedetailvos[0].getPk_tradetype());
				querylist.add(costinfovo);
				if(bxparentVO.getDjlxbm().equals(ErmConstExt.Distributor_BX_Tradetype)){
					// �����̵渶�������������Ҫ������ڷ�̯���ɵ�����ƾ֤
					IErmCostShareBillQuery csservice = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class);
					AggCostShareVO csaggvo = csservice.queryBillByPK(csharedetailvos[0].getPk_costshare());
					Map<String, List<AggCostShareVO>> groupPcorgVOs = CostshareVOGroupHelper.groupPcorgVOs(csaggvo);
					for (Entry<String, List<AggCostShareVO>> groupvos : groupPcorgVOs.entrySet()) {
						List<AggCostShareVO> vos = groupvos.getValue();
						for (AggCostShareVO aggvo : vos) {
							FipRelationInfoVO infovo = new FipRelationInfoVO();
							CostShareVO parentVO = (CostShareVO) aggvo.getParentVO();
							infovo.setPk_group(parentVO.getPk_group());
							infovo.setPk_org(parentVO.getPk_org());
							infovo.setRelationID(parentVO.getPrimaryKey());
							infovo.setPk_billtype(parentVO.getPk_tradetype());
							querylist.add(infovo);
						}
					}
				}
				
				FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), querylist.toArray(new FipRelationInfoVO[0]));

			} else {
				FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), srcinfovo);
				// --end
			}
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}

}
