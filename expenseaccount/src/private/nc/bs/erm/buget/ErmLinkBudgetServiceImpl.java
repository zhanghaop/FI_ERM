package nc.bs.erm.buget;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.ntb.IErmLinkBudgetService;
import nc.itf.erm.proxy.ErmProxy;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.util.erm.costshare.ErCostBudgetUtil;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.util.AuditInfoUtil;

public class ErmLinkBudgetServiceImpl implements IErmLinkBudgetService {

	@Override
	public NtbParamVO[] getBudgetLinkParams(JKBXVO vo, String actionCode, String nodeCode) throws BusinessException {
		
		if(vo == null || actionCode == null){
			return null;
		}
		
		if ((vo.getChildrenVO() == null || vo.getChildrenVO().length == 0) && !vo.isChildrenFetched()) { // ��ʼ������vo
			vo = retrieveChidren(vo);
		}
		
		if(vo.getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved){//�ݴ�ʱ�����ձ�������
			vo.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);
		}
		
		IFYControl[] items = null;

		// ���з���Ԥ������
		if (vo.getcShareDetailVo() != null && vo.getcShareDetailVo().length != 0) {
			// ��ѯ���ý�ת��vo
			AggCostShareVO[] csVos = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class)
					.queryBillByWhere(CostShareVO.SRC_ID + "='" + vo.getParentVO().getPk_jkbx() + "'");
			
			if (csVos != null) {
				for (AggCostShareVO csVo : csVos) {
					if (((CostShareVO) csVo.getParentVO()).getBillstatus() == BXStatusConst.DJZT_TempSaved) {
						((CostShareVO) csVo.getParentVO()).setBillstatus(BXStatusConst.DJZT_Saved);
					}
				}
				items = ErCostBudgetUtil.getCostControlVOByCSVO(csVos, AuditInfoUtil.getCurrentUser());
			}
		} else {
			JKBXHeaderVO[] jkbxItems = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
			// ��������,�������Ժ�,��һ�����ܷ����Ķ������������,���ܲ���ִ����,��ǰ��liangsg������,
			// �������Ժ�ȥ����,��Ҫ��busivo.getDataType()��ֵ���ü���Ϊ��˶�����busivo.getDataType()��ֵ,
			// ��������һ��,�������͵�ֵ����Ϊ��ǰ����,������ҵ�񵥾����������,���û������
			for (JKBXHeaderVO jkbxvo : jkbxItems) {
				if (jkbxvo.getShrq() == null) {
					jkbxvo.setShrq(new UFDateTime());
				}
			}
			items = jkbxItems;
		}
		
		if(items == null || items.length == 0){
			throw new BusinessException(
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
			.getStrByID("expensepub_0", "02011002-1001")/*
														 * @
														 * res
														 * "��ǰ�û�û��Ȩ�޲�ѯ�����ݵ����Ԥ�㣬����"
														 */
			);
		}

		// ����Ԥ��ӿڲ�ѯ���Ʋ��ԡ��������ֵΪ�ձ�ʾ�޿��Ʋ��ԣ������ơ����һ������Ϊfalse�������Ͳ���������β���
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(items[0].getDjlxbm(), actionCode, false);
		
		if (ruleVos == null || ruleVos.length == 0) {
			return null;
		}
		
		YsControlVO[] controlVos =  ErBudgetUtil.getCtrlVOs(items, true, ruleVos);

		if(controlVos == null || controlVos.length == 0){
			return null;
		}
		
		return ErmProxy.getILinkQuery().getLinkDatas(controlVos);
	}
	
	private JKBXVO retrieveChidren(JKBXVO zbvo) throws BusinessException {
		try {
			JKBXVO bxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(zbvo.getParentVO());
			return bxvo;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

}
