package nc.ui.erm.closeaccount.view;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.org.closeaccbook.OrgCloseManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.sm.funcreg.ModuleVO;

/**
 * ҵ��Ԫ�������
 *
 *
 */
@SuppressWarnings("serial")
public class CloseAccountBusiUnitPane extends nc.ui.org.closeaccbook.CloseAccBookBusiUnitPane {

	// ˢ�°�ťҲҪ����
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
	 * ֻ��������ģ��
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