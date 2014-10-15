package nc.ui.erm.mactrlschema.view;


import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.CacheUtil;
import nc.ui.er.util.BXUiUtil;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

public class MaCtrlTreePanel extends nc.ui.uif2.components.TreePanel {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		super.init();
		// 交易类型树展现
//		HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
//		List<BilltypeVO> list = new ArrayList<BilltypeVO>();
//		for (BilltypeVO vo : billtypes.values()) {
//			if (vo.getSystemcode() != null
//					&& vo.getSystemcode().equalsIgnoreCase(
//							BXConstans.ERM_PRODUCT_CODE)) {
//				if (vo.getPk_group() != null
//						&& !vo.getPk_group().equalsIgnoreCase(
//								ErUiUtil.getPK_group())) {
//					continue;
//				}
//				if (vo.getPk_billtypecode().startsWith(
//						ErmMatterAppConst.MatterApp_PREFIX)
//						&& !vo.getPk_billtypecode().equals(
//								ErmMatterAppConst.MatterApp_BILLTYPE)) {
//					list.add(vo);
//				}
//			}
//		}
		try {
			// 只过滤出报销费用类的申请单交易类型
			String where = "where pk_group = '"+BXUiUtil.getPK_group()+"' and pk_billtypecode in (select distinct djlxbm from er_djlx where djdl = '"+ErmBillConst.MatterApp_DJDL+"' and matype = "+ErmConst.MATTERAPP_BILLTYPE_BX+" and pk_group = '"+BXUiUtil.getPK_group()+"')";
			BilltypeVO[] vos = CacheUtil.getValueFromCacheByWherePart(BilltypeVO.class, where);
			getModel().initModel(vos);
		} catch (BusinessException e) {
			ExceptionHandler.error(e);
		}
		
	}
}
