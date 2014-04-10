package nc.ui.erm.mactrlschema.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.pf.pub.PfDataCache;
import nc.ui.erm.util.ErUiUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;

public class MaCtrlTreePanel extends nc.ui.uif2.components.TreePanel {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		super.init();
		// 交易类型树展现
		HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
		List<BilltypeVO> list = new ArrayList<BilltypeVO>();
		for (BilltypeVO vo : billtypes.values()) {
			if (vo.getSystemcode() != null
					&& vo.getSystemcode().equalsIgnoreCase(
							BXConstans.ERM_PRODUCT_CODE)) {
				if (vo.getPk_group() != null
						&& !vo.getPk_group().equalsIgnoreCase(
								ErUiUtil.getPK_group())) {
					continue;
				}
				if (vo.getPk_billtypecode().startsWith(
						ErmMatterAppConst.MatterApp_PREFIX)
						&& !vo.getPk_billtypecode().equals(
								ErmMatterAppConst.MatterApp_BILLTYPE)) {
					list.add(vo);
				}
			}
		}
		getModel().initModel(list.toArray(new SuperVO[0]));
	}
}
