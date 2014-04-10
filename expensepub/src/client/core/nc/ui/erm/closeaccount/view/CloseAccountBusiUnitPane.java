package nc.ui.erm.closeaccount.view;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.org.closeaccbook.OrgCloseManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.sm.funcreg.ModuleVO;

/**
 * 业务单元参照面板
 *
 *
 */
@SuppressWarnings("serial")
public class CloseAccountBusiUnitPane extends nc.ui.org.closeaccbook.CloseAccBookBusiUnitPane {

	// 刷新按钮也要调用
	public void setCenterUI() {
		try {
			String pk_org = getRefPane().getRefPK();
			((OrgCloseManageModel) getModel()).setPk_org(pk_org);
			ModuleVO[] moduleVos = new ModuleVO[0];
			if (pk_org != null)
				moduleVos = getClientServicer().queryCloseAccModuleVOs(pk_org);

			moduleVos = filter(moduleVos);

			if (moduleVos != null){
				getCenterPane().resetUI(moduleVos);
			}

		} catch (BusinessException e) {
			throw new BusinessExceptionAdapter(e);
		}
	}

	/*
	 * 只保留报销模块
	 */
	private ModuleVO[] filter(ModuleVO[] moduleVos) {
		for (int i = 0; i < moduleVos.length; i++) {
			if (BXConstans.ERM_MODULEID.equals(moduleVos[i].getModuleid())) {
				return new ModuleVO[] { moduleVos[i] };
			}
		}
		return null;
	}

}